package com.example.library_management.controller;

import com.example.library_management.model.Kitaplar;
import com.example.library_management.model.Rezervasyonlar;
import com.example.library_management.repository.KitaplarRepository;
import com.example.library_management.repository.RezervasyonlarRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/staff/rezervasyonlar")
public class StaffRezervasyonlarController {

    private final RezervasyonlarRepository rezervasyonlarRepository;
    private final KitaplarRepository kitaplarRepository;

    public StaffRezervasyonlarController(RezervasyonlarRepository rezervasyonlarRepository,
                                    KitaplarRepository kitaplarRepository) {
        this.rezervasyonlarRepository = rezervasyonlarRepository;
        this.kitaplarRepository = kitaplarRepository;
    }

    @GetMapping
    public String listRezervasyonlar(Model model) {
        model.addAttribute("rezervasyonlar", rezervasyonlarRepository.findAll());
        return "staff/rezervasyonlar";
    }

    @GetMapping("/onayla/{id}")
    public String onaylaRezervasyon(@PathVariable int id) {
        Rezervasyonlar rezervasyon = rezervasyonlarRepository.findById(id).orElse(null);

        if (rezervasyon != null && "BEKLEMEDE".equals(rezervasyon.getDurum())) {
            Kitaplar kitap = rezervasyon.getKitap();

            if (kitap != null && kitap.getStok() > 0) {


                rezervasyon.setDurum("HAZIR");
                rezervasyon.setAciklama("Rezervasyon onaylandı. Kitap teslim için hazır.");
                rezervasyonlarRepository.save(rezervasyon);
            } else {
                rezervasyon.setAciklama("Stok bulunmadığı için rezervasyon onaylanamadı.");
                rezervasyonlarRepository.save(rezervasyon);
            }
        }

        return "redirect:/staff/rezervasyonlar";
    }

    @GetMapping("/hazir/{id}")
    public String hazirYap(@PathVariable int id) {
        Rezervasyonlar rezervasyon = rezervasyonlarRepository.findById(id).orElse(null);

        if (rezervasyon != null && "ONAYLANDI".equals(rezervasyon.getDurum())) {
            String rafYeri = "-";

            if (rezervasyon.getKitap() != null && rezervasyon.getKitap().getRafYeri() != null) {
                rafYeri = rezervasyon.getKitap().getRafYeri();
            }

            rezervasyon.setDurum("HAZIR");
            rezervasyon.setAciklama("Kitap teslim için hazırlandı. Raf yeri: " + rafYeri);
            rezervasyonlarRepository.save(rezervasyon);
        }

        return "redirect:/staff/rezervasyonlar";
    }

    @GetMapping("/iptal/{id}")
    public String iptalRezervasyon(@PathVariable int id) {
        Rezervasyonlar rezervasyon = rezervasyonlarRepository.findById(id).orElse(null);

        if (rezervasyon != null && !"IPTAL".equals(rezervasyon.getDurum())) {



            rezervasyon.setDurum("IPTAL");
            rezervasyon.setAciklama("Rezervasyon personel tarafından iptal edildi.");
            rezervasyonlarRepository.save(rezervasyon);
        }

        return "redirect:/staff/rezervasyonlar";
    }
}
