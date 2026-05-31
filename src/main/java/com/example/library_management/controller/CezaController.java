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
                           jakarta.servlet.http.HttpServletRequest request) {
        cezaRepository.save(ceza);

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/cezalar");
    }

    @GetMapping("/delete/{id}")
    public String deleteCeza(@PathVariable int id,
                             jakarta.servlet.http.HttpServletRequest request) {
        cezaRepository.deleteById(id);

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/cezalar");
    }

    @GetMapping("/odeme/{id}")
    public String cezaTahsilEt(@PathVariable int id,
                               jakarta.servlet.http.HttpServletRequest request) {
        Ceza bulunanCeza = cezaRepository.findById(id).orElse(null);

        if (bulunanCeza != null) {
            bulunanCeza.setOdendiMi(true);
            cezaRepository.save(bulunanCeza);
        }

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/cezalar");
    }
}