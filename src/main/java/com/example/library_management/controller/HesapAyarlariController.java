package com.example.library_management.controller;

import com.example.library_management.model.Kullanici;
import com.example.library_management.repository.KullaniciRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user/hesap-ayarlari")
public class HesapAyarlariController {

    private final KullaniciRepository kullaniciRepository;

    public HesapAyarlariController(KullaniciRepository kullaniciRepository) {
        this.kullaniciRepository = kullaniciRepository;
    }

    @GetMapping
    public String showHesapAyarlari(HttpSession session, Model model) {
        Kullanici aktifKullanici = (Kullanici) session.getAttribute("aktifKullanici");

        if (aktifKullanici == null) {
            return "redirect:/login";
        }

        Kullanici kullanici = kullaniciRepository.findById(aktifKullanici.getId()).orElse(null);

        if (kullanici == null) {
            session.invalidate();
            return "redirect:/login";
        }

        model.addAttribute("kullanici", kullanici);

        return "user/hesap-ayarlari";
    }

    @PostMapping("/save")
    public String saveHesapAyarlari(@ModelAttribute Kullanici form,
                                    HttpSession session,
                                    RedirectAttributes redirectAttrs) {

        Kullanici aktifKullanici = (Kullanici) session.getAttribute("aktifKullanici");

        if (aktifKullanici == null) {
            return "redirect:/login";
        }

        Kullanici k = kullaniciRepository.findById(aktifKullanici.getId()).orElse(null);

        if (k == null) {
            session.invalidate();
            return "redirect:/login";
        }

        k.setAd(form.getAd());
        k.setSoyad(form.getSoyad());
        k.setEmail(form.getEmail());
        k.setTelefon(form.getTelefon());
        k.setAdres(form.getAdres());

        if (form.getSifre() != null && !form.getSifre().isBlank()) {
            k.setSifre(form.getSifre());
        }

        kullaniciRepository.save(k);

        session.setAttribute("aktifKullanici", k);

        redirectAttrs.addFlashAttribute("basari", "Hesap bilgileriniz başarıyla güncellendi.");

        return "redirect:/user/hesap-ayarlari";
    }
}