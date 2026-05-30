package com.example.library_management.controller;

import com.example.library_management.model.Kategori;
import com.example.library_management.model.Yayinevi;
import com.example.library_management.model.Yazar;
import com.example.library_management.repository.KategoriRepository;
import com.example.library_management.repository.YayineviRepository;
import com.example.library_management.repository.YazarRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/sistem-ayarlari")
public class SistemAyarlariController {

    private final KategoriRepository kategoriRepository;
    private final YazarRepository yazarRepository;
    private final YayineviRepository yayineviRepository;

    public SistemAyarlariController(KategoriRepository kategoriRepository,
                                    YazarRepository yazarRepository,
                                    YayineviRepository yayineviRepository) {
        this.kategoriRepository = kategoriRepository;
        this.yazarRepository = yazarRepository;
        this.yayineviRepository = yayineviRepository;
    }

    @GetMapping
    public String showSettings(Model model) {
        model.addAttribute("kategoriler", kategoriRepository.findAll());
        model.addAttribute("yazarlar", yazarRepository.findAll());
        model.addAttribute("yayinevleri", yayineviRepository.findAll());
        model.addAttribute("yeniKategori", new Kategori());
        model.addAttribute("yeniYazar", new Yazar());
        model.addAttribute("yeniYayinevi", new Yayinevi());
        return "admin/sistem-ayarlari";
    }

    @PostMapping("/kategori/save")
    public String saveKategori(@ModelAttribute Kategori kategori,
                               RedirectAttributes redirectAttributes) {
        String ad = kategori.getAd() != null ? kategori.getAd().trim() : "";
        kategori.setAd(ad);

        if (ad.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Kategori adı boş olamaz.");
            redirectAttributes.addFlashAttribute("activeTab", "kategoriler");
            return "redirect:/admin/sistem-ayarlari#kategoriler";
        }

        if (kategoriRepository.existsByAdIgnoreCase(ad)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bu kategori zaten kayıtlı: " + ad);
            redirectAttributes.addFlashAttribute("activeTab", "kategoriler");
            return "redirect:/admin/sistem-ayarlari#kategoriler";
        }

        kategoriRepository.save(kategori);
        redirectAttributes.addFlashAttribute("successMessage", "Kategori başarıyla eklendi.");
        redirectAttributes.addFlashAttribute("activeTab", "kategoriler");
        return "redirect:/admin/sistem-ayarlari#kategoriler";
    }

    @GetMapping("/kategori/delete/{id}")
    public String deleteKategori(@PathVariable int id,
                                 RedirectAttributes redirectAttributes) {
        try {
            kategoriRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Kategori başarıyla silindi.");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bu kategori kitaplarda kullanıldığı için silinemez.");
        }

        redirectAttributes.addFlashAttribute("activeTab", "kategoriler");
        return "redirect:/admin/sistem-ayarlari#kategoriler";
    }

    @PostMapping("/kategori/update/{id}")
    public String updateKategori(@PathVariable int id,
                                 @ModelAttribute Kategori form,
                                 RedirectAttributes redirectAttributes) {
        Kategori mevcut = kategoriRepository.findById(id).orElse(null);
        String ad = form.getAd() != null ? form.getAd().trim() : "";

        if (mevcut == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Kategori bulunamadı.");
            redirectAttributes.addFlashAttribute("activeTab", "kategoriler");
            return "redirect:/admin/sistem-ayarlari#kategoriler";
        }

        if (ad.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Kategori adı boş olamaz.");
            redirectAttributes.addFlashAttribute("activeTab", "kategoriler");
            return "redirect:/admin/sistem-ayarlari#kategoriler";
        }

        boolean duplicate = kategoriRepository.findByAdIgnoreCase(ad)
                .map(k -> k.getId() != id)
                .orElse(false);

        if (duplicate) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bu kategori adı zaten kullanılıyor: " + ad);
            redirectAttributes.addFlashAttribute("activeTab", "kategoriler");
            return "redirect:/admin/sistem-ayarlari#kategoriler";
        }

        mevcut.setAd(ad);
        mevcut.setAciklama(form.getAciklama());
        kategoriRepository.save(mevcut);

        redirectAttributes.addFlashAttribute("successMessage", "Kategori başarıyla güncellendi.");
        redirectAttributes.addFlashAttribute("activeTab", "kategoriler");
        return "redirect:/admin/sistem-ayarlari#kategoriler";
    }

    @PostMapping("/yazar/save")
    public String saveYazar(@ModelAttribute Yazar yazar,
                            RedirectAttributes redirectAttributes) {
        String ad = yazar.getAd() != null ? yazar.getAd().trim() : "";
        String soyad = yazar.getSoyad() != null ? yazar.getSoyad().trim() : "";

        yazar.setAd(ad);
        yazar.setSoyad(soyad);

        if (ad.isEmpty() || soyad.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Yazar adı ve soyadı boş olamaz.");
            redirectAttributes.addFlashAttribute("activeTab", "yazarlar");
            return "redirect:/admin/sistem-ayarlari#yazarlar";
        }

        if (yazarRepository.existsByAdIgnoreCaseAndSoyadIgnoreCase(ad, soyad)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bu yazar zaten kayıtlı: " + ad + " " + soyad);
            redirectAttributes.addFlashAttribute("activeTab", "yazarlar");
            return "redirect:/admin/sistem-ayarlari#yazarlar";
        }

        yazarRepository.save(yazar);
        redirectAttributes.addFlashAttribute("successMessage", "Yazar başarıyla eklendi.");
        redirectAttributes.addFlashAttribute("activeTab", "yazarlar");
        return "redirect:/admin/sistem-ayarlari#yazarlar";
    }

    @GetMapping("/yazar/delete/{id}")
    public String deleteYazar(@PathVariable int id,
                              RedirectAttributes redirectAttributes) {
        try {
            yazarRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Yazar başarıyla silindi.");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bu yazar kitaplarda kullanıldığı için silinemez.");
        }

        redirectAttributes.addFlashAttribute("activeTab", "yazarlar");
        return "redirect:/admin/sistem-ayarlari#yazarlar";
    }

    @PostMapping("/yazar/update/{id}")
    public String updateYazar(@PathVariable int id,
                              @ModelAttribute Yazar form,
                              RedirectAttributes redirectAttributes) {
        Yazar mevcut = yazarRepository.findById(id).orElse(null);
        String ad = form.getAd() != null ? form.getAd().trim() : "";
        String soyad = form.getSoyad() != null ? form.getSoyad().trim() : "";

        if (mevcut == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Yazar bulunamadı.");
            redirectAttributes.addFlashAttribute("activeTab", "yazarlar");
            return "redirect:/admin/sistem-ayarlari#yazarlar";
        }

        if (ad.isEmpty() || soyad.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Yazar adı ve soyadı boş olamaz.");
            redirectAttributes.addFlashAttribute("activeTab", "yazarlar");
            return "redirect:/admin/sistem-ayarlari#yazarlar";
        }

        boolean duplicate = yazarRepository.findByAdIgnoreCaseAndSoyadIgnoreCase(ad, soyad)
                .map(y -> y.getId() != id)
                .orElse(false);

        if (duplicate) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bu yazar zaten kayıtlı: " + ad + " " + soyad);
            redirectAttributes.addFlashAttribute("activeTab", "yazarlar");
            return "redirect:/admin/sistem-ayarlari#yazarlar";
        }

        mevcut.setAd(ad);
        mevcut.setSoyad(soyad);
        mevcut.setBiyografi(form.getBiyografi());
        yazarRepository.save(mevcut);

        redirectAttributes.addFlashAttribute("successMessage", "Yazar başarıyla güncellendi.");
        redirectAttributes.addFlashAttribute("activeTab", "yazarlar");
        return "redirect:/admin/sistem-ayarlari#yazarlar";
    }

    @PostMapping("/yayinevi/save")
    public String saveYayinevi(@ModelAttribute Yayinevi yayinevi,
                               RedirectAttributes redirectAttributes) {
        String ad = yayinevi.getAd() != null ? yayinevi.getAd().trim() : "";
        yayinevi.setAd(ad);

        if (ad.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Yayınevi adı boş olamaz.");
            redirectAttributes.addFlashAttribute("activeTab", "yayinevleri");
            return "redirect:/admin/sistem-ayarlari#yayinevleri";
        }

        if (yayineviRepository.existsByAdIgnoreCase(ad)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bu yayınevi zaten kayıtlı: " + ad);
            redirectAttributes.addFlashAttribute("activeTab", "yayinevleri");
            return "redirect:/admin/sistem-ayarlari#yayinevleri";
        }

        yayineviRepository.save(yayinevi);
        redirectAttributes.addFlashAttribute("successMessage", "Yayınevi başarıyla eklendi.");
        redirectAttributes.addFlashAttribute("activeTab", "yayinevleri");
        return "redirect:/admin/sistem-ayarlari#yayinevleri";
    }

    @GetMapping("/yayinevi/delete/{id}")
    public String deleteYayinevi(@PathVariable int id,
                                 RedirectAttributes redirectAttributes) {
        try {
            yayineviRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Yayınevi başarıyla silindi.");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bu yayınevi kitaplarda kullanıldığı için silinemez.");
        }

        redirectAttributes.addFlashAttribute("activeTab", "yayinevleri");
        return "redirect:/admin/sistem-ayarlari#yayinevleri";
    }

    @PostMapping("/yayinevi/update/{id}")
    public String updateYayinevi(@PathVariable int id,
                                 @ModelAttribute Yayinevi form,
                                 RedirectAttributes redirectAttributes) {
        Yayinevi mevcut = yayineviRepository.findById(id).orElse(null);
        String ad = form.getAd() != null ? form.getAd().trim() : "";

        if (mevcut == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Yayınevi bulunamadı.");
            redirectAttributes.addFlashAttribute("activeTab", "yayinevleri");
            return "redirect:/admin/sistem-ayarlari#yayinevleri";
        }

        if (ad.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Yayınevi adı boş olamaz.");
            redirectAttributes.addFlashAttribute("activeTab", "yayinevleri");
            return "redirect:/admin/sistem-ayarlari#yayinevleri";
        }

        boolean duplicate = yayineviRepository.findByAdIgnoreCase(ad)
                .map(y -> y.getId() != id)
                .orElse(false);

        if (duplicate) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bu yayınevi zaten kayıtlı: " + ad);
            redirectAttributes.addFlashAttribute("activeTab", "yayinevleri");
            return "redirect:/admin/sistem-ayarlari#yayinevleri";
        }

        mevcut.setAd(ad);
        mevcut.setAdres(form.getAdres());
        mevcut.setTelefon(form.getTelefon());
        yayineviRepository.save(mevcut);

        redirectAttributes.addFlashAttribute("successMessage", "Yayınevi başarıyla güncellendi.");
        redirectAttributes.addFlashAttribute("activeTab", "yayinevleri");
        return "redirect:/admin/sistem-ayarlari#yayinevleri";
    }
}