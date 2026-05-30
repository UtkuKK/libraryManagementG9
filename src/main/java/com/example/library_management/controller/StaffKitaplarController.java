package com.example.library_management.controller;

import com.example.library_management.model.Kitaplar;
import com.example.library_management.repository.KitaplarRepository;
import com.example.library_management.repository.YazarRepository;
import com.example.library_management.repository.KategoriRepository;
import com.example.library_management.repository.YayineviRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.Random;

@Controller
@RequestMapping("/staff/kitaplar")
public class StaffKitaplarController {

    private final KitaplarRepository kitaplarRepository;
    private final YazarRepository yazarRepository;
    private final KategoriRepository kategoriRepository;
    private final YayineviRepository yayineviRepository;

    public StaffKitaplarController(KitaplarRepository kitaplarRepository,
                              YazarRepository yazarRepository,
                              KategoriRepository kategoriRepository,
                              YayineviRepository yayineviRepository) {
        this.kitaplarRepository = kitaplarRepository;
        this.yazarRepository = yazarRepository;
        this.kategoriRepository = kategoriRepository;
        this.yayineviRepository = yayineviRepository;
    }

    @GetMapping
    public String listKitaplar(Model model) {
        model.addAttribute("kitaplar", kitaplarRepository.findAll());
        model.addAttribute("yeniKitap", new Kitaplar());
        model.addAttribute("yazarlar", yazarRepository.findAll());
        model.addAttribute("kategoriler", kategoriRepository.findAll());
        model.addAttribute("yayinevleri", yayineviRepository.findAll());
        model.addAttribute("duzenlemeModu", false);
        model.addAttribute("currentYear", Year.now().getValue());

        return "staff/kitaplar";
    }

    @PostMapping("/save")
    public String saveKitap(
            @ModelAttribute("yeniKitap") Kitaplar kitap,
            @RequestParam(required = false) Integer yazarId,
            @RequestParam(required = false) Integer kategoriId,
            @RequestParam(required = false) Integer yayineviId) {

        int currentYear = Year.now().getValue();

        if (kitap.getYil() > currentYear) {
            return "redirect:/staff/kitaplar";
        }

        if (kitap.getStok() < 0) {
            kitap.setStok(0);
        }

        kitap.setIsbn(generateUniqueIsbn());

        if (yazarId != null) {
            yazarRepository.findById(yazarId).ifPresent(kitap::setYazar);
        }

        if (kategoriId != null) {
            kategoriRepository.findById(kategoriId).ifPresent(kitap::setKategori);
        }

        if (yayineviId != null) {
            yayineviRepository.findById(yayineviId).ifPresent(kitap::setYayinevi);
        }

        kitaplarRepository.save(kitap);
        return "redirect:/staff/kitaplar";
    }

    @GetMapping("/duzenle/{id}")
    public String duzenleForm(@PathVariable int id, Model model) {
        Kitaplar kitap = kitaplarRepository.findById(id).orElse(null);

        if (kitap == null) {
            return "redirect:/staff/kitaplar";
        }

        model.addAttribute("duzenlenecekKitap", kitap);
        model.addAttribute("kitaplar", kitaplarRepository.findAll());
        model.addAttribute("yeniKitap", new Kitaplar());
        model.addAttribute("yazarlar", yazarRepository.findAll());
        model.addAttribute("kategoriler", kategoriRepository.findAll());
        model.addAttribute("yayinevleri", yayineviRepository.findAll());
        model.addAttribute("duzenlemeModu", true);
        model.addAttribute("currentYear", Year.now().getValue());

        return "staff/kitaplar";
    }

    @PostMapping("/update/{id}")
    public String updateKitap(
            @PathVariable int id,
            @ModelAttribute Kitaplar form,
            @RequestParam(required = false) Integer yazarId,
            @RequestParam(required = false) Integer kategoriId,
            @RequestParam(required = false) Integer yayineviId) {

        Kitaplar mevcut = kitaplarRepository.findById(id).orElse(null);

        if (mevcut != null) {
            int currentYear = Year.now().getValue();

            if (form.getYil() > currentYear) {
                return "redirect:/staff/kitaplar/duzenle/" + id;
            }

            mevcut.setBaslik(form.getBaslik());
            mevcut.setYil(form.getYil());
            mevcut.setStok(Math.max(form.getStok(), 0));
            mevcut.setAciklama(form.getAciklama());
            mevcut.setRafYeri(form.getRafYeri());

            if (yazarId != null) {
                yazarRepository.findById(yazarId).ifPresent(mevcut::setYazar);
            } else {
                mevcut.setYazar(null);
            }

            if (kategoriId != null) {
                kategoriRepository.findById(kategoriId).ifPresent(mevcut::setKategori);
            } else {
                mevcut.setKategori(null);
            }

            if (yayineviId != null) {
                yayineviRepository.findById(yayineviId).ifPresent(mevcut::setYayinevi);
            } else {
                mevcut.setYayinevi(null);
            }

            kitaplarRepository.save(mevcut);
        }

        return "redirect:/staff/kitaplar";
    }

    private String generateUniqueIsbn() {
        Random random = new Random();
        String isbn;

        do {
            isbn = "978" + String.format("%010d", Math.abs(random.nextLong()) % 10_000_000_000L);
        } while (kitaplarRepository.existsByIsbn(isbn));

        return isbn;
    }
}
