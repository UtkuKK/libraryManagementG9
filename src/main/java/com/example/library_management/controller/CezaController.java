package com.example.library_management.controller;

import com.example.library_management.model.Ceza;
import com.example.library_management.model.Kullanici;
import com.example.library_management.model.OduncIslemi;
import com.example.library_management.model.Kitaplar;
import com.example.library_management.repository.CezaRepository;
import com.example.library_management.repository.KullaniciRepository;
import com.example.library_management.repository.OduncIslemiRepository;
import com.example.library_management.repository.KitaplarRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/cezalar")
public class CezaController {


    private final CezaRepository cezaRepository;
    private final KullaniciRepository kullaniciRepository;
    private final OduncIslemiRepository oduncIslemiRepository;
    private final KitaplarRepository kitaplarRepository;

    public CezaController(CezaRepository cezaRepository, KullaniciRepository kullaniciRepository, 
                          OduncIslemiRepository oduncIslemiRepository, KitaplarRepository kitaplarRepository) {
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
            Kullanici kullanici = kullaniciRepository.findById(ceza.getKullaniciId()).orElse(null);
            if (kullanici != null) {
                ceza.setKullaniciAdi(kullanici.getAd() + " " + kullanici.getSoyad());
            }

            OduncIslemi odunc = oduncIslemiRepository.findById(ceza.getOduncId()).orElse(null);
            if (odunc != null) {
                Kitaplar kitap = kitaplarRepository.findById(odunc.getKopyaId()).orElse(null);
                ceza.setGecikmeGunu(odunc.gecikmeGunSayisi());
                if (kitap != null) {
                    ceza.setKitapAdi(kitap.getBaslik());
                    if (ceza.getAciklama() != null && ceza.getAciklama().contains("geç teslim")) {
                        ceza.setAciklama("'" + kitap.getBaslik() + "' isimli kitap geç teslim edildi.");
                    }
                }
            }
        }
        return cezalar;
    }


    @PostMapping("/save")
    public String saveCeza(@ModelAttribute("yeniCeza") Ceza ceza, jakarta.servlet.http.HttpServletRequest request) {

        cezaRepository.save(ceza);

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/cezalar");
    }


    @GetMapping("/delete/{id}")
    public String deleteCeza(@PathVariable int id, jakarta.servlet.http.HttpServletRequest request) {
        // ID'si verilen cezayı siler
        cezaRepository.deleteById(id);

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/cezalar");
    }

    // CezaController.java içine eklenecek metod
    @GetMapping("/odeme/{id}")
    public String cezaTahsilEt(@PathVariable int id, jakarta.servlet.http.HttpServletRequest request) {
        // 1. Veritabanından o ID'ye sahip cezayı bul
        Ceza bulunanCeza = cezaRepository.findById(id).orElse(null);

        if (bulunanCeza != null) {
            // 2. Cezayı "Ödendi" olarak işaretle
            bulunanCeza.setOdendiMi(true);

            // 3. Güncellenmiş halini veritabanına geri kaydet
            cezaRepository.save(bulunanCeza);
        }

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/cezalar");
    }


    @GetMapping("/tahsil-et/{id}")
    public String tahsilEt(@PathVariable int id, jakarta.servlet.http.HttpServletRequest request) {
        // 1. Cezayı ID ile bul
        Ceza ceza = cezaRepository.findById(id).get();

        // 2. Ödeme durumunu güncelle
        ceza.setOdendiMi(true);

        // 3. Güncellenmiş cezayı kaydet
        cezaRepository.save(ceza);

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/cezalar");
    }
}