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

        long totalBooks = kitaplarRepository.count();

        model.addAttribute("totalBooks", totalBooks);
        model.addAttribute("activeBorrowings", activeBorrowings);
        model.addAttribute("overdueCount", overdueCount);

        model.addAttribute("oduncler", oduncler);

        model.addAttribute("kitaplar", kitaplarRepository.findAll());
        model.addAttribute("kullanicilar", kullaniciRepository.findAll());

        model.addAttribute("yeniOdunc", new OduncIslemi());

        return "admin/odunc";
    }


    @PostMapping("/save")
    public String saveOduncIslemi(@ModelAttribute("yeniOdunc") OduncIslemi oduncIslemi, RedirectAttributes redirectAttributes, jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpSession session) {
        String referer = request.getHeader("Referer");
        String redirectUrl = "redirect:" + (referer != null ? referer : "/odunc-islemleri");


        long unpaidPenaltyCount = cezaRepository.countByKullaniciIdAndOdendiMiFalse(oduncIslemi.getKullaniciId());
        
        if (unpaidPenaltyCount > 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bu kullanıcının ödenmemiş cezası bulunduğu için yeni kitap ödünç verilemez.");
            return redirectUrl;
        }


        java.util.List<OduncIslemi> userActiveBorrows = repository.findAll().stream()
                .filter(o -> o.getKullaniciId() == oduncIslemi.getKullaniciId() && 
                             o.getDurum() != null && 
                             !o.getDurum().equalsIgnoreCase("TAMAMLANDI") && 
                             !o.getDurum().equalsIgnoreCase("TESLIM_EDILDI"))
                .toList();


        if (userActiveBorrows.size() >= 3) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hata: Bir kullanıcı aynı anda en fazla 3 kitap ödünç alabilir.");
            return redirectUrl;
        }


        boolean hasOverdue = userActiveBorrows.stream().anyMatch(o -> 
                o.getDurum().equalsIgnoreCase("GECIKMIS") || 
                o.getDurum().equalsIgnoreCase("GECİKMİŞ") ||
                (o.getTeslimTarihi() != null && java.time.LocalDate.now().isAfter(o.getTeslimTarihi())));

        if (hasOverdue) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hata: Kullanıcının iadesi gecikmiş kitabı bulunmaktadır. Önce onu iade etmelidir.");
            return redirectUrl;
        }


        boolean hasSameBook = userActiveBorrows.stream().anyMatch(o -> o.getKopyaId() == oduncIslemi.getKopyaId());
        
        if (hasSameBook) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hata: Kullanıcı bu kitabı zaten ödünç almış ve henüz iade etmemiş.");
            return redirectUrl;
        }


        if (oduncIslemi.getTeslimTarihi() != null && oduncIslemi.getTeslimTarihi().isBefore(java.time.LocalDate.now())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hata: Geri teslim tarihi bugünden daha eski bir tarih olamaz!");
            return redirectUrl;
        }


        com.example.library_management.model.Kitaplar kitap = kitaplarRepository.findById(oduncIslemi.getKopyaId()).orElse(null);
        
        if (kitap == null || kitap.getStok() <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hata: Seçilen kitaptan stokta kalmamıştır!");
            return redirectUrl;
        }
        

        kitap.setStok(kitap.getStok() - 1);
        kitaplarRepository.save(kitap);


        oduncIslemi.setDurum(OduncIslemi.DURUM_DEVAM_EDIYOR);
        if(oduncIslemi.getVerilisTarihi() == null) {
            oduncIslemi.setVerilisTarihi(java.time.LocalDate.now());
        }
        repository.save(oduncIslemi);
        redirectAttributes.addFlashAttribute("successMessage", "Ödünç işlemi başarıyla kaydedildi.");


        return redirectUrl;
    }


    @PostMapping("/delete/{id}")
    public String deleteOduncIslemi(@PathVariable int id, jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpSession session, RedirectAttributes redirectAttributes) {
        String referer = request.getHeader("Referer");
        String redirectUrl = "redirect:" + (referer != null ? referer : "/odunc-islemleri");

        com.example.library_management.model.Kullanici aktifKullanici = (com.example.library_management.model.Kullanici) session.getAttribute("aktifKullanici");
        if (aktifKullanici == null || !"ADMIN".equalsIgnoreCase(aktifKullanici.getRol())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hata: Silme işlemi için ADMIN yetkisi gereklidir.");
            return redirectUrl;
        }

        OduncIslemi odunc = repository.findById(id).orElse(null);
        if (odunc != null) {

            if (odunc.getDurum() != null && !odunc.getDurum().equalsIgnoreCase("TAMAMLANDI") && !odunc.getDurum().equalsIgnoreCase("TESLIM_EDILDI")) {
                com.example.library_management.model.Kitaplar kitap = kitaplarRepository.findById(odunc.getKopyaId()).orElse(null);
                if (kitap != null) {
                    kitap.setStok(kitap.getStok() + 1);
                    kitaplarRepository.save(kitap);
                }
            }
            

            java.util.List<com.example.library_management.model.Ceza> bagliCezalar = cezaRepository.findAll().stream()
                    .filter(c -> c.getOduncId() == id)
                    .toList();
            if (!bagliCezalar.isEmpty()) {
                cezaRepository.deleteAll(bagliCezalar);
            }

            repository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Ödünç kaydı ve bağlı cezalar silindi, gerekliyse stok güncellendi.");
        }

        return redirectUrl;
    }


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
                           RedirectAttributes redirectAttributes, jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpSession session) {
        String referer = request.getHeader("Referer");
        String redirectUrl = "redirect:" + (referer != null ? referer : "/odunc-islemleri");

        OduncIslemi odunc = repository.findById(id).orElse(null);
        if (odunc == null) return redirectUrl;
        
        if (odunc.getDurum() != null && (odunc.getDurum().equalsIgnoreCase("TAMAMLANDI") || odunc.getDurum().equalsIgnoreCase(OduncIslemi.DURUM_TESLIM_EDILDI))) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hata: Bu işlem zaten tamamlanmış (kitap teslim alınmış).");
            return redirectUrl;
        }

        odunc.setDurum(OduncIslemi.DURUM_TESLIM_EDILDI);
        odunc.setGercekTeslimTarihi(java.time.LocalDate.now());
        odunc.setAciklama(aciklama);
        odunc.setKitapDurumu(kitapDurumu);

        com.example.library_management.model.Kitaplar kitap = kitaplarRepository.findById(odunc.getKopyaId()).orElse(null);
        
        double kesilecekCeza = odunc.cezaTutari(); 
        double hasarCezasi = 0.0;
        String hasarSebebi = "";

        if ("Kayip".equalsIgnoreCase(kitapDurumu)) {
            hasarCezasi = 300.0;
            hasarSebebi = "Kitap kaybedildi.";

        } else {
            if (kitap != null) {
                kitap.setStok(kitap.getStok() + 1);
                kitaplarRepository.save(kitap);
            }
            if ("Hasarli".equalsIgnoreCase(kitapDurumu)) {
                hasarCezasi = 50.0;
                hasarSebebi = "Kitap hasarlı teslim edildi.";
            } else if ("Eksik_Sayfa".equalsIgnoreCase(kitapDurumu)) {
                hasarCezasi = 100.0;
                hasarSebebi = "Kitap eksik sayfalı teslim edildi.";
            }
        }

        if (kesilecekCeza > 0) {
            com.example.library_management.model.Ceza gecikmeCezasi = new com.example.library_management.model.Ceza(
                    odunc.getKullaniciId(),
                    odunc.getId(),
                    java.math.BigDecimal.valueOf(kesilecekCeza),
                    "Kitap gecikmeli teslim edildi.",
                    false
            );
            cezaRepository.save(gecikmeCezasi);
        }

        if (hasarCezasi > 0) {
            com.example.library_management.model.Ceza ekCeza = new com.example.library_management.model.Ceza(
                    odunc.getKullaniciId(),
                    odunc.getId(),
                    java.math.BigDecimal.valueOf(hasarCezasi),
                    hasarSebebi,
                    false
            );
            cezaRepository.save(ekCeza);
        }

        double toplamCeza = kesilecekCeza + hasarCezasi;
        if (toplamCeza > 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Uyarı: Toplam " + toplamCeza + " TL ceza kesildi! (Ayrı kalemler halinde kaydedildi)");
        } else {
            redirectAttributes.addFlashAttribute("successMessage", "Kitap zamanında ve sorunsuz teslim alındı.");
        }

        repository.save(odunc);
        return redirectUrl;
    }

    @PostMapping("/uzat/{id}")
    public org.springframework.http.ResponseEntity<String> sureyiUzat(@PathVariable int id, jakarta.servlet.http.HttpSession session) {
        com.example.library_management.model.Kullanici aktifKullanici = (com.example.library_management.model.Kullanici) session.getAttribute("aktifKullanici");
        if (aktifKullanici == null) {
             return org.springframework.http.ResponseEntity.status(401).body("Oturum açmanız gerekiyor.");
        }

        OduncIslemi odunc = repository.findById(id).orElse(null);
        if (odunc == null) {
            return org.springframework.http.ResponseEntity.status(404).body("Kayıt bulunamadı.");
        }
        

        if ("USER".equalsIgnoreCase(aktifKullanici.getRol()) && odunc.getKullaniciId() != aktifKullanici.getId()) {
            return org.springframework.http.ResponseEntity.status(403).body("Yetkisiz işlem.");
        }

        if (odunc.isUzatildiMi()) {
            return org.springframework.http.ResponseEntity.status(400).body("Bu kitabın süresi daha önce uzatılmış. Sadece 1 kez uzatma yapılabilir.");
        }
        
        if (odunc.getDurum() != null && (odunc.getDurum().equalsIgnoreCase("TAMAMLANDI") || odunc.getDurum().equalsIgnoreCase(OduncIslemi.DURUM_TESLIM_EDILDI))) {
            return org.springframework.http.ResponseEntity.status(400).body("Teslim edilmiş kitabın süresi uzatılamaz.");
        }
        
        if (odunc.getTeslimTarihi() != null && java.time.LocalDate.now().isAfter(odunc.getTeslimTarihi())) {
            return org.springframework.http.ResponseEntity.status(400).body("Gecikmiş kitapların süresi uzatılamaz.");
        }
        
        if (odunc.getTeslimTarihi() != null) {
            odunc.setTeslimTarihi(odunc.getTeslimTarihi().plusDays(15));
            odunc.setUzatildiMi(true);
            repository.save(odunc);
            return org.springframework.http.ResponseEntity.ok("Süre başarıyla 15 gün uzatıldı.");
        }
        
        return org.springframework.http.ResponseEntity.status(400).body("Teslim tarihi bulunamadı.");
    }
}
