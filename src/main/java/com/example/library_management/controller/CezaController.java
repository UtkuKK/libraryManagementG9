package com.example.library_management.controller;

import com.example.library_management.model.Ceza;
import com.example.library_management.model.Kitaplar;
import com.example.library_management.model.Kullanici;
import com.example.library_management.model.OduncIslemi;
import com.example.library_management.repository.CezaRepository;
import com.example.library_management.repository.KitaplarRepository;
import com.example.library_management.repository.KullaniciRepository;
import com.example.library_management.repository.OduncIslemiRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cezalar")
public class CezaController {

    private final CezaRepository cezaRepository;
    private final KullaniciRepository kullaniciRepository;
    private final OduncIslemiRepository oduncIslemiRepository;
    private final KitaplarRepository kitaplarRepository;

    public CezaController(CezaRepository cezaRepository,
                          KullaniciRepository kullaniciRepository,
                          OduncIslemiRepository oduncIslemiRepository,
                          KitaplarRepository kitaplarRepository) {
        this.cezaRepository = cezaRepository;
        this.kullaniciRepository = kullaniciRepository;
        this.oduncIslemiRepository = oduncIslemiRepository;
        this.kitaplarRepository = kitaplarRepository;
    }

    @GetMapping
    public String listCezalar(Model model) {
        List<Ceza> cezalar = getEnrichedCezalar();
        model.addAttribute("cezalar", cezalar);
        model.addAttribute("yeniCeza", new Ceza());
        return "admin/ceza";
    }

    @GetMapping("/staff")
    public String listStaffCezalar(Model model) {
        List<Ceza> cezalar = getEnrichedCezalar();
        model.addAttribute("cezalar", cezalar);
        model.addAttribute("yeniCeza", new Ceza());
        return "staff/ceza";
    }

    private List<Ceza> getEnrichedCezalar() {
        List<Ceza> cezalar = cezaRepository.findAll();

        for (Ceza ceza : cezalar) {
            Kullanici kullanici =
                    kullaniciRepository.findById(ceza.getKullaniciId()).orElse(null);

            if (kullanici != null) {
                ceza.setKullaniciAdi(kullanici.getAd() + " " + kullanici.getSoyad());
            } else {
                ceza.setKullaniciAdi("-");
            }

            OduncIslemi odunc =
                    oduncIslemiRepository.findById(ceza.getOduncId()).orElse(null);

            if (odunc != null) {
                ceza.setGecikmeGunu(odunc.gecikmeGunSayisi());

                Kitaplar kitap =
                        kitaplarRepository.findById(odunc.getKopyaId()).orElse(null);

                if (kitap != null) {
                    ceza.setKitapAdi(kitap.getBaslik());

                    if (ceza.getAciklama() != null &&
                            ceza.getAciklama().toLowerCase().contains("geç teslim")) {
                        ceza.setAciklama("'" + kitap.getBaslik() + "' isimli kitap geç teslim edildi.");
                    }
                } else {
                    ceza.setKitapAdi("-");
                }
            } else {
                ceza.setKitapAdi("-");
                ceza.setGecikmeGunu(0);
            }
        }

        return cezalar;
    }

    @PostMapping("/save")
    public String saveCeza(@ModelAttribute("yeniCeza") Ceza ceza,
                           jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpSession session, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        String referer = request.getHeader("Referer");
        String redirectUrl = "redirect:" + (referer != null ? referer : "/cezalar");

        cezaRepository.save(ceza);

        return redirectUrl;
    }

    @PostMapping("/delete/{id}")
    public String deleteCeza(@PathVariable int id,
                             jakarta.servlet.http.HttpServletRequest request,
                             jakarta.servlet.http.HttpSession session,
                             org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        String referer = request.getHeader("Referer");
        String redirectUrl = "redirect:" + (referer != null ? referer : "/cezalar");

        Kullanici aktifKullanici = (Kullanici) session.getAttribute("aktifKullanici");
        if (aktifKullanici == null || !"ADMIN".equalsIgnoreCase(aktifKullanici.getRol())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hata: Silme işlemi için ADMIN yetkisi gereklidir.");
            return redirectUrl;
        }

        cezaRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Ceza kaydı başarıyla silindi.");
        return redirectUrl;
    }

    @PostMapping("/tahsil-et/{id}")
    public org.springframework.http.ResponseEntity<String> cezaTahsilEt(@PathVariable int id,
                               @RequestParam(required = false) java.math.BigDecimal miktar,
                               jakarta.servlet.http.HttpSession session) {
        
        Kullanici aktifKullanici = (Kullanici) session.getAttribute("aktifKullanici");


        Ceza bulunanCeza = cezaRepository.findById(id).orElse(null);

        if (bulunanCeza != null) {
            java.math.BigDecimal currentPaid = bulunanCeza.getOdenenMiktar() != null ? bulunanCeza.getOdenenMiktar() : java.math.BigDecimal.ZERO;
            java.math.BigDecimal eklenecekMiktar = miktar;
            
            if (eklenecekMiktar != null && eklenecekMiktar.compareTo(java.math.BigDecimal.ZERO) < 0) {
                return org.springframework.http.ResponseEntity.status(400).body("Ödenecek miktar 0'dan küçük olamaz.");
            }
            
            if (eklenecekMiktar == null || eklenecekMiktar.compareTo(java.math.BigDecimal.ZERO) == 0) {
                // Kısmi miktar girilmemişse, kalan borcun tamamını tahsil et
                eklenecekMiktar = bulunanCeza.getCezaMiktari().subtract(currentPaid);
            }

            java.math.BigDecimal yeniOdenen = currentPaid.add(eklenecekMiktar);
            
            if (yeniOdenen.compareTo(bulunanCeza.getCezaMiktari()) >= 0) {
                bulunanCeza.setOdendiMi(true);
                bulunanCeza.setOdenenMiktar(bulunanCeza.getCezaMiktari());
            } else {
                bulunanCeza.setOdenenMiktar(yeniOdenen);
            }
            
            bulunanCeza.setOdemeTarihi(java.time.LocalDate.now());
            if (aktifKullanici != null) {
                bulunanCeza.setTahsilEdenKullaniciId(aktifKullanici.getId());
            }
            
            cezaRepository.save(bulunanCeza);
            return org.springframework.http.ResponseEntity.ok("Başarılı");
        }

        return org.springframework.http.ResponseEntity.status(404).body("Ceza bulunamadı");
    }

    @GetMapping("/test-tahsil/{id}")
    @ResponseBody
    public String testTahsil(@PathVariable int id) {
        try {
            Ceza bulunanCeza = cezaRepository.findById(id).orElse(null);
            if (bulunanCeza != null) {
                java.math.BigDecimal currentPaid = bulunanCeza.getOdenenMiktar() != null ? bulunanCeza.getOdenenMiktar() : java.math.BigDecimal.ZERO;
                java.math.BigDecimal eklenecekMiktar = bulunanCeza.getCezaMiktari().subtract(currentPaid);
                java.math.BigDecimal yeniOdenen = currentPaid.add(eklenecekMiktar);
                
                if (yeniOdenen.compareTo(bulunanCeza.getCezaMiktari()) >= 0) {
                    bulunanCeza.setOdendiMi(true);
                    bulunanCeza.setOdenenMiktar(bulunanCeza.getCezaMiktari());
                } else {
                    bulunanCeza.setOdenenMiktar(yeniOdenen);
                }
                
                bulunanCeza.setOdemeTarihi(java.time.LocalDate.now());
                bulunanCeza.setTahsilEdenKullaniciId(1);
                
                cezaRepository.save(bulunanCeza);
                return "SUCCESS";
            }
            return "NOT FOUND";
        } catch (Exception e) {
            java.io.StringWriter sw = new java.io.StringWriter();
            e.printStackTrace(new java.io.PrintWriter(sw));
            return "ERROR: " + e.getMessage() + "\n" + sw.toString();
        }
    }
}