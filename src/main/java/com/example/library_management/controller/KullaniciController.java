package com.example.library_management.controller;

import com.example.library_management.model.Kullanici;
import com.example.library_management.repository.KullaniciRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String deleteKullanici(@PathVariable int id,
                                   jakarta.servlet.http.HttpSession session,
                                   RedirectAttributes redirectAttrs) {
        // Yöneticinin kendi hesabını silmesini engelle
        Kullanici aktif = (Kullanici) session.getAttribute("aktifKullanici");
        if (aktif != null && aktif.getId() == id) {
            redirectAttrs.addFlashAttribute("errorMessage", "Kendi hesabınızı silemezsiniz.");
            return "redirect:/admin/kullanicilar";
        }
        try {
            kullaniciRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            redirectAttrs.addFlashAttribute("errorMessage",
                "Bu kullanıcı silinemiyor: Aktif ödünç, rezervasyon veya ceza kaydı bulunuyor.");
        }
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
                                  @ModelAttribute Kullanici form,
                                  RedirectAttributes redirectAttrs) {

        Kullanici mevcut = kullaniciRepository.findById(id).orElse(null);

        if (mevcut != null) {
            // E-posta değiştiyse benzersizlik kontrolü
            if (!mevcut.getEmail().equalsIgnoreCase(form.getEmail())) {
                if (kullaniciRepository.existsByEmail(form.getEmail())) {
                    redirectAttrs.addFlashAttribute("errorMessage",
                        "Bu e-posta adresi zaten başka bir kullanıcı tarafından kullanılıyor.");
                    return "redirect:/admin/kullanicilar/duzenle/" + id;
                }
            }
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