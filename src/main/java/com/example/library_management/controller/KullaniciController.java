package com.example.library_management.controller;

import com.example.library_management.model.Kullanici;
import com.example.library_management.repository.KullaniciRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/kullanicilar")
public class KullaniciController {

    private final KullaniciRepository kullaniciRepository;

    public KullaniciController(KullaniciRepository kullaniciRepository) {
        this.kullaniciRepository = kullaniciRepository;
    }

    @GetMapping
    public String listKullanicilar(Model model) {
        List<Kullanici> kullanicilar = kullaniciRepository.findAll();
        model.addAttribute("kullanicilar", kullanicilar);
        return "admin/kullanicilar";
    }

    @GetMapping("/delete/{id}")
    public String deleteKullanici(@PathVariable int id) {
        kullaniciRepository.deleteById(id);
        return "redirect:/admin/kullanicilar";
    }

    @GetMapping("/durum/{id}")
    public String durumDegistir(@PathVariable int id) {
        Kullanici kullanici = kullaniciRepository.findById(id).orElse(null);

        if (kullanici != null) {
            kullanici.setAktif(!kullanici.isAktif());
            kullaniciRepository.save(kullanici);
        }

        return "redirect:/admin/kullanicilar";
    }

    @GetMapping("/duzenle/{id}")
    public String duzenleForm(@PathVariable int id, Model model) {
        Kullanici kullanici = kullaniciRepository.findById(id).orElse(null);

        if (kullanici == null) {
            return "redirect:/admin/kullanicilar";
        }

        model.addAttribute("kullanici", kullanici);
        model.addAttribute("kullanicilar", kullaniciRepository.findAll());
        model.addAttribute("duzenlemeModu", true);

        return "admin/kullanicilar";
    }

    @PostMapping("/update/{id}")
    public String updateKullanici(@PathVariable int id,
                                  @ModelAttribute Kullanici form) {

        Kullanici mevcut = kullaniciRepository.findById(id).orElse(null);

        if (mevcut != null) {
            mevcut.setAd(form.getAd());
            mevcut.setSoyad(form.getSoyad());
            mevcut.setEmail(form.getEmail());
            mevcut.setTelefon(form.getTelefon());
            mevcut.setAdres(form.getAdres());
            mevcut.setRol(form.getRol());

            kullaniciRepository.save(mevcut);
        }

        return "redirect:/admin/kullanicilar";
    }
}