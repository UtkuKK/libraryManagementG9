package com.example.library_management.controller;

import com.example.library_management.model.OduncIslemi;
import com.example.library_management.repository.OduncIslemiRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.library_management.repository.CezaRepository;

@Controller
@RequestMapping("/odunc-islemleri")
public class OduncIslemiController {

    private final OduncIslemiRepository repository;
    private final CezaRepository cezaRepository;
    private final com.example.library_management.repository.KitaplarRepository kitaplarRepository;
    private final com.example.library_management.repository.KullaniciRepository kullaniciRepository;

    public OduncIslemiController(OduncIslemiRepository repository, CezaRepository cezaRepository, com.example.library_management.repository.KitaplarRepository kitaplarRepository, com.example.library_management.repository.KullaniciRepository kullaniciRepository) {
        this.repository = repository;
        this.cezaRepository = cezaRepository;
        this.kitaplarRepository = kitaplarRepository;
        this.kullaniciRepository = kullaniciRepository;
    }

    // Tüm ödünç işlemlerini listeleme metodu
    @GetMapping
    public String listOduncIslemleri(Model model) {
        java.util.List<OduncIslemi> oduncler = repository.findAll();
        long activeBorrowings = 0;
        long overdueCount = 0;
        java.time.LocalDate bugun = java.time.LocalDate.now();

        for (OduncIslemi odunc : oduncler) {
            com.example.library_management.model.Kullanici kullanici = kullaniciRepository.findById(odunc.getKullaniciId()).orElse(null);
            if (kullanici != null) {
                odunc.setKullaniciAdi(kullanici.getAd() + " " + kullanici.getSoyad());
            }
            com.example.library_management.model.Kitaplar kitap = kitaplarRepository.findById(odunc.getKopyaId()).orElse(null);
            if (kitap != null) {
                odunc.setKitapBaslik(kitap.getBaslik());
                odunc.setKitapStok(kitap.getStok());
            }

            // Gecikme ve aktiflik durumu hesaplaması
            String durum = odunc.getDurum();
            if (durum != null) {
                // Eğer durum bitti (TAMAMLANDI / TESLIM_EDILDI) DEĞİLSE, demek ki kitap hala dışarıdadır (Aktiftir)
                if (!durum.equalsIgnoreCase("TAMAMLANDI") && !durum.equalsIgnoreCase("TESLIM_EDILDI")) {
                    activeBorrowings++;

                    // Peki gecikmiş mi? (Veritabanında bizzat GECİKMİŞ yazıyor olabilir veya tarihi geçmiş olabilir)
                    if (durum.equalsIgnoreCase("GECİKMİŞ") || durum.equalsIgnoreCase("GECIKMIS")) {
                        overdueCount++;
                    } else if (odunc.getTeslimTarihi() != null && bugun.isAfter(odunc.getTeslimTarihi())) {
                        overdueCount++;
                        // Sadece tarihi geçmiş ama veritabanında henüz güncellenmemişse tabloda GECIKMIS görünmesi için
                        odunc.setDurum("GECIKMIS"); 
                    }
                }
            }
        }

        long totalBooks = kitaplarRepository.count();

        model.addAttribute("totalBooks", totalBooks);
        model.addAttribute("activeBorrowings", activeBorrowings);
        model.addAttribute("overdueCount", overdueCount);

        // Veritabanındaki tüm kayıtları çekip "oduncler" ismiyle sayfaya gönderir
        model.addAttribute("oduncler", oduncler);

        // Formlarda görünmesi için kitaplar ve kullanıcılar listeleri
        model.addAttribute("kitaplar", kitaplarRepository.findAll());
        model.addAttribute("kullanicilar", kullaniciRepository.findAll());

        // Yeni bir boş nesne gönderir (Formda doldurulmak üzere)
        model.addAttribute("yeniOdunc", new OduncIslemi());

        return "admin/odunc"; // "odunc-list" yerine yeni yol
    }

    // Yeni ödünç işlemi kaydetme metodu
    @PostMapping("/save")
    public String saveOduncIslemi(@ModelAttribute("yeniOdunc") OduncIslemi oduncIslemi, RedirectAttributes redirectAttributes, jakarta.servlet.http.HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        String redirectUrl = "redirect:" + (referer != null ? referer : "/odunc-islemleri");

        // 2'den fazla ödenmemiş cezası olan kullanıcı yeni kitap alamaz
        long unpaidPenaltyCount = cezaRepository.countByKullaniciIdAndOdendiMiFalse(oduncIslemi.getKullaniciId());
        
        if (unpaidPenaltyCount > 2) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bu kullanıcının ödenmemiş 2'den fazla cezası bulunduğu için yeni kitap ödünç verilemez.");
            return redirectUrl;
        }

        // Geçmiş tarih kontrolü
        if (oduncIslemi.getTeslimTarihi() != null && oduncIslemi.getTeslimTarihi().isBefore(java.time.LocalDate.now())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hata: Geri teslim tarihi bugünden daha eski bir tarih olamaz!");
            return redirectUrl;
        }

        // Stok kontrolü ve düşürme
        com.example.library_management.model.Kitaplar kitap = kitaplarRepository.findById(oduncIslemi.getKopyaId()).orElse(null);
        
        if (kitap == null || kitap.getStok() <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hata: Seçilen kitaptan stokta kalmamıştır!");
            return redirectUrl;
        }
        
        // Stoktan 1 düş ve kitabı güncelle
        kitap.setStok(kitap.getStok() - 1);
        kitaplarRepository.save(kitap);

        // Formdan gelen verileri veritabanına kaydeder
        oduncIslemi.setDurum(OduncIslemi.DURUM_DEVAM_EDIYOR);
        if(oduncIslemi.getVerilisTarihi() == null) {
            oduncIslemi.setVerilisTarihi(java.time.LocalDate.now());
        }
        repository.save(oduncIslemi);
        redirectAttributes.addFlashAttribute("successMessage", "Ödünç işlemi başarıyla kaydedildi.");

        // İşlem bitince tekrar listeleme sayfasına yönlendirir
        return redirectUrl;
    }

    // Bir ödünç işlemini silme metodu
    @GetMapping("/delete/{id}")
    public String deleteOduncIslemi(@PathVariable int id, jakarta.servlet.http.HttpServletRequest request) {
        // Belirtilen ID'ye sahip kaydı siler
        repository.deleteById(id);

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/odunc-islemleri");
    }

    // Personel (Staff) özel ödünç listeleme sayfası
    @GetMapping("/staff")
    public String listStaffOduncIslemleri(Model model) {
        java.util.List<OduncIslemi> oduncler = repository.findAll();
        long activeBorrowings = 0;
        long overdueCount = 0;
        java.time.LocalDate bugun = java.time.LocalDate.now();

        for (OduncIslemi odunc : oduncler) {
            com.example.library_management.model.Kullanici kullanici = kullaniciRepository.findById(odunc.getKullaniciId()).orElse(null);
            if (kullanici != null) {
                odunc.setKullaniciAdi(kullanici.getAd() + " " + kullanici.getSoyad());
            }
            com.example.library_management.model.Kitaplar kitap = kitaplarRepository.findById(odunc.getKopyaId()).orElse(null);
            if (kitap != null) {
                odunc.setKitapBaslik(kitap.getBaslik());
                odunc.setKitapStok(kitap.getStok());
            }

            String durum = odunc.getDurum();
            if (durum != null) {
                if (!durum.equalsIgnoreCase("TAMAMLANDI") && !durum.equalsIgnoreCase("TESLIM_EDILDI")) {
                    activeBorrowings++;
                    if (durum.equalsIgnoreCase("GECİKMİŞ") || durum.equalsIgnoreCase("GECIKMIS")) {
                        overdueCount++;
                    } else if (odunc.getTeslimTarihi() != null && bugun.isAfter(odunc.getTeslimTarihi())) {
                        overdueCount++;
                        odunc.setDurum("GECIKMIS"); 
                    }
                }
            }
        }

        model.addAttribute("activeBorrowings", activeBorrowings);
        model.addAttribute("overdueCount", overdueCount);
        model.addAttribute("oduncler", oduncler);
        model.addAttribute("yeniOdunc", new OduncIslemi());
        model.addAttribute("kitaplar", kitaplarRepository.findAll());
        model.addAttribute("kullanicilar", kullaniciRepository.findAll());

        return "staff/odunc";
    }

    // Kullanıcıya özel ödünç listeleme sayfası
    @GetMapping("/user")
    public String listUserOduncIslemleri(jakarta.servlet.http.HttpSession session, Model model) {
        com.example.library_management.model.Kullanici aktifKullanici = (com.example.library_management.model.Kullanici) session.getAttribute("aktifKullanici");
        if (aktifKullanici == null) {
            return "redirect:/login";
        }
        
        java.util.List<OduncIslemi> userOduncler = repository.findAll().stream()
                .filter(o -> o.getKullaniciId() == aktifKullanici.getId())
                .toList();
                
        for(OduncIslemi odunc : userOduncler) {
            com.example.library_management.model.Kitaplar kitap = kitaplarRepository.findById(odunc.getKopyaId()).orElse(null);
            if(kitap != null) {
                odunc.setKitapBaslik(kitap.getBaslik());
                if(kitap.getYazar() != null) {
                    odunc.setKitapYazari(kitap.getYazar().getAd() + " " + kitap.getYazar().getSoyad());
                } else {
                    odunc.setKitapYazari("Bilinmeyen Yazar");
                }
            }
            // Veritabanında alış tarihi boş kalmışsa, teslim tarihinden 14 gün öncesini varsay (Görsellik için)
            if(odunc.getVerilisTarihi() == null) {
                if(odunc.getTeslimTarihi() != null) {
                    odunc.setVerilisTarihi(odunc.getTeslimTarihi().minusDays(14));
                } else {
                    odunc.setVerilisTarihi(java.time.LocalDate.now());
                }
            }
            if(odunc.getDurum() == null || (!odunc.getDurum().equals("TAMAMLANDI") && !odunc.getDurum().equals("TESLIM_EDILDI"))) {
                if(odunc.getTeslimTarihi() != null && java.time.LocalDate.now().isAfter(odunc.getTeslimTarihi())) {
                    odunc.setDurum("GECIKMIS");
                } else {
                    odunc.setDurum("DEVAM_EDIYOR");
                }
            }
        }
        
        java.util.List<com.example.library_management.model.Ceza> userCezalar = cezaRepository.findAll().stream()
                .filter(c -> c.getKullaniciId() == aktifKullanici.getId())
                .toList();
                
        for(com.example.library_management.model.Ceza ceza : userCezalar) {
            OduncIslemi odunc = repository.findById(ceza.getOduncId()).orElse(null);
            if (odunc != null) {
                ceza.setGecikmeGunu(odunc.gecikmeGunSayisi());
                com.example.library_management.model.Kitaplar kitap = kitaplarRepository.findById(odunc.getKopyaId()).orElse(null);
                if (kitap != null) {
                    ceza.setKitapAdi(kitap.getBaslik());
                }
            }
        }
        
        model.addAttribute("oduncler", userOduncler);
        model.addAttribute("cezalar", userCezalar);
        return "user/odunc";
    }

    @PostMapping("/teslim-al")
    public String teslimAl(@RequestParam("oduncId") int id, 
                           @RequestParam(value = "aciklama", required = false) String aciklama,
                           @RequestParam(value = "kitapDurumu", defaultValue = "Iyi") String kitapDurumu,
                           RedirectAttributes redirectAttributes, jakarta.servlet.http.HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        String redirectUrl = "redirect:" + (referer != null ? referer : "/odunc-islemleri");

        OduncIslemi odunc = repository.findById(id).orElse(null);
        if (odunc == null) return redirectUrl;

        odunc.setDurum(OduncIslemi.DURUM_TESLIM_EDILDI);
        odunc.setGercekTeslimTarihi(java.time.LocalDate.now());
        odunc.setAciklama(aciklama);
        odunc.setKitapDurumu(kitapDurumu);

        com.example.library_management.model.Kitaplar kitap = kitaplarRepository.findById(odunc.getKopyaId()).orElse(null);
        if (kitap != null) {
            kitap.setStok(kitap.getStok() + 1);
            kitaplarRepository.save(kitap);
        }

        double kesilecekCeza = odunc.cezaTutari(); 
        double hasarCezasi = 0.0;
        String cezaSebebi = "Kitap gecikmeli teslim edildi.";

        if ("Hasarli".equalsIgnoreCase(kitapDurumu)) {
            hasarCezasi = 50.0; // Hasarlı kitap cezası
            cezaSebebi = (kesilecekCeza > 0) ? "Kitap gecikmeli ve hasarlı teslim edildi." : "Kitap hasarlı teslim edildi.";
        } else if ("Eksik_Sayfa".equalsIgnoreCase(kitapDurumu)) {
            hasarCezasi = 100.0; // Eksik sayfa cezası
            cezaSebebi = (kesilecekCeza > 0) ? "Kitap gecikmeli ve eksik sayfalı teslim edildi." : "Kitap eksik sayfalı teslim edildi.";
        }

        double toplamCeza = kesilecekCeza + hasarCezasi;

        if (toplamCeza > 0) {
            com.example.library_management.model.Ceza yeniCeza = new com.example.library_management.model.Ceza(
                    odunc.getKullaniciId(),
                    odunc.getId(),
                    java.math.BigDecimal.valueOf(toplamCeza),
                    cezaSebebi,
                    false
            );
            cezaRepository.save(yeniCeza);
            redirectAttributes.addFlashAttribute("errorMessage", "Uyarı: Toplam " + toplamCeza + " TL ceza kesildi! (" + cezaSebebi + ")");
        } else {
            redirectAttributes.addFlashAttribute("successMessage", "Kitap zamanında ve sorunsuz teslim alındı.");
        }

        repository.save(odunc);
        return redirectUrl;
    }
}
