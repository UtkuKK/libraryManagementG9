package com.example.library_management.controller;

import com.example.library_management.model.Kullanici;
import com.example.library_management.repository.KullaniciRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class GirisKayitController {

    private final KullaniciRepository kullaniciRepository;

    public GirisKayitController(KullaniciRepository kullaniciRepository) {
        this.kullaniciRepository = kullaniciRepository;
    }

    @GetMapping("/")
    public String anaSayfa() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String girisSayfasi() {
        return "girisKayit/login";
    }

    @PostMapping("/login")
    public String girisYap(@RequestParam String email,
                           @RequestParam String sifre,
                           HttpSession session,
                           Model model) {

        Optional<Kullanici> kullaniciOptional = kullaniciRepository.findByEmail(email);

        if (kullaniciOptional.isEmpty()) {
            model.addAttribute("error", "Bu e-posta ile kayıtlı kullanıcı bulunamadı.");
            return "girisKayit/login";
        }

        Kullanici kullanici = kullaniciOptional.get();

        if (!kullanici.getSifre().equals(sifre)) {
            model.addAttribute("error", "Şifre hatalı.");
            return "girisKayit/login";
        }

        if (!kullanici.isAktif()) {
            model.addAttribute("error", "Hesabınız aktif değil.");
            return "girisKayit/login";
        }

        session.setAttribute("aktifKullanici", kullanici);

        if ("ADMIN".equalsIgnoreCase(kullanici.getRol())) {
            return "redirect:/dashboard/admin";
        }
        
        if ("STAFF".equalsIgnoreCase(kullanici.getRol())) {
            return "redirect:/dashboard/staff";
        }

        return "redirect:/user/dashboard";
    }

    @GetMapping("/register")
    public String kayitSayfasi(Model model) {
        model.addAttribute("kullanici", new Kullanici());
        return "girisKayit/register";
    }

    @PostMapping("/register")
    public String kayitOl(@ModelAttribute Kullanici kullanici,
                          Model model) {

        if (kullaniciRepository.existsByEmail(kullanici.getEmail())) {
            model.addAttribute("error", "Bu e-posta adresi zaten kayıtlı.");
            model.addAttribute("kullanici", kullanici);
            return "girisKayit/register";
        }

        kullanici.setRol("USER");
        kullanici.setAktif(true);

        kullaniciRepository.save(kullanici);

        return "redirect:/login?registered=true";
    }

    @GetMapping("/sifremi-unuttum")
    public String sifremiUnuttumSayfasi() {
        return "girisKayit/sifremiUnuttum";
    }

    @PostMapping("/sifremi-unuttum")
    public String sifreSifirla(@RequestParam String email,
                               @RequestParam String telefon,
                               @RequestParam String yeniSifre,
                               @RequestParam String yeniSifreTekrar,
                               Model model) {

        String temizTelefon = telefon.replaceAll("\\D", "");

        if (!yeniSifre.equals(yeniSifreTekrar)) {
            model.addAttribute("error", "Yeni şifreler eşleşmiyor.");
            model.addAttribute("email", email);
            model.addAttribute("telefon", telefon);
            return "girisKayit/sifremiUnuttum";
        }

        if (temizTelefon.length() != 11 || !temizTelefon.startsWith("05")) {
            model.addAttribute("error", "Telefon numarası 05555556600 formatında olmalıdır.");
            model.addAttribute("email", email);
            model.addAttribute("telefon", telefon);
            return "girisKayit/sifremiUnuttum";
        }

        Optional<Kullanici> kullaniciOptional =
                kullaniciRepository.findByEmailAndTelefon(email, temizTelefon);

        if (kullaniciOptional.isEmpty()) {
            model.addAttribute("error", "E-posta ve telefon bilgileri eşleşmedi.");
            model.addAttribute("email", email);
            model.addAttribute("telefon", telefon);
            return "girisKayit/sifremiUnuttum";
        }

        Kullanici kullanici = kullaniciOptional.get();
        kullanici.setSifre(yeniSifre);
        kullaniciRepository.save(kullanici);

        return "redirect:/login?passwordChanged=true";
    }

    @GetMapping("/logout")
    public String cikisYap(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout=true";
    }
}