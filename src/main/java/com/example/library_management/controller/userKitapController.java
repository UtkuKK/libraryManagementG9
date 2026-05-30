package com.example.library_management.controller;

import com.example.library_management.model.Kitaplar;
import com.example.library_management.model.Kullanici;
import com.example.library_management.model.Rezervasyonlar;
import com.example.library_management.repository.KitaplarRepository;
import com.example.library_management.repository.KullaniciRepository;
import com.example.library_management.repository.RezervasyonlarRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/user")
public class userKitapController {

    private final KitaplarRepository kitaplarRepository;
    private final KullaniciRepository kullaniciRepository;
    private final RezervasyonlarRepository rezervasyonlarRepository;

    public userKitapController(KitaplarRepository kitaplarRepository,
                               KullaniciRepository kullaniciRepository,
                               RezervasyonlarRepository rezervasyonlarRepository) {
        this.kitaplarRepository = kitaplarRepository;
        this.kullaniciRepository = kullaniciRepository;
        this.rezervasyonlarRepository = rezervasyonlarRepository;
    }

    @GetMapping("/kitap-ara")
    public String kitapAra(@RequestParam(required = false) String keyword,
                           HttpSession session,
                           Model model) {

        Kullanici aktifKullanici = (Kullanici) session.getAttribute("aktifKullanici");

        if (aktifKullanici == null) {
            return "redirect:/login";
        }

        List<Kitaplar> kitaplar;

        if (keyword != null && !keyword.trim().isEmpty()) {
            String arama = keyword.trim();

            kitaplar = kitaplarRepository
                    .findByBaslikContainingIgnoreCaseOrYazar_AdContainingIgnoreCaseOrYazar_SoyadContainingIgnoreCaseOrKategori_AdContainingIgnoreCase(
                            arama,
                            arama,
                            arama,
                            arama
                    );
        } else {
            kitaplar = kitaplarRepository.findAll();
        }

        model.addAttribute("kitaplar", kitaplar);
        model.addAttribute("keyword", keyword);

        return "user/kitapAra";
    }

    @PostMapping("/rezervasyon-yap/{kitapId}")
    public String rezervasyonYap(@PathVariable int kitapId,
                                 HttpSession session) {

        Kullanici aktifKullanici = (Kullanici) session.getAttribute("aktifKullanici");

        if (aktifKullanici == null) {
            return "redirect:/login";
        }

        Kullanici kullanici = kullaniciRepository.findById(aktifKullanici.getId()).orElse(null);
        Kitaplar kitap = kitaplarRepository.findById(kitapId).orElse(null);

        if (kullanici == null || kitap == null) {
            return "redirect:/user/kitap-ara?error=notfound";
        }

        if (kitap.getStok() <= 0) {
            return "redirect:/user/kitap-ara?error=stok";
        }

        List<String> aktifDurumlar = Arrays.asList("BEKLEMEDE", "ONAYLANDI", "HAZIR");

        boolean zatenVar = rezervasyonlarRepository
                .existsByKullaniciIdAndKitapIdAndDurumIn(
                        kullanici.getId(),
                        kitap.getId(),
                        aktifDurumlar
                );

        if (zatenVar) {
            return "redirect:/user/kitap-ara?error=duplicate";
        }

        Rezervasyonlar rezervasyon = new Rezervasyonlar();
        rezervasyon.setKullanici(kullanici);
        rezervasyon.setKitap(kitap);
        rezervasyon.setRezervasyonTarihi(LocalDate.now());
        rezervasyon.setDurum("BEKLEMEDE");
        rezervasyon.setAciklama("Kullanıcı kitap rezervasyon isteği oluşturdu.");

        rezervasyonlarRepository.save(rezervasyon);

        return "redirect:/user/rezervasyonlar?success=true";
    }

    @GetMapping("/rezervasyonlar")
    public String userRezervasyonlar(HttpSession session,
                                     Model model) {

        Kullanici aktifKullanici = (Kullanici) session.getAttribute("aktifKullanici");

        if (aktifKullanici == null) {
            return "redirect:/login";
        }

        List<Rezervasyonlar> rezervasyonlar =
                rezervasyonlarRepository.findByKullaniciId(aktifKullanici.getId());

        model.addAttribute("rezervasyonlar", rezervasyonlar);

        return "user/rezervasyonlar";
    }

    @GetMapping("/rezervasyon-iptal/{id}")
    public String userRezervasyonIptal(@PathVariable int id,
                                       HttpSession session) {

        Kullanici aktifKullanici = (Kullanici) session.getAttribute("aktifKullanici");

        if (aktifKullanici == null) {
            return "redirect:/login";
        }

        Rezervasyonlar rezervasyon = rezervasyonlarRepository.findById(id).orElse(null);

        if (rezervasyon != null
                && rezervasyon.getKullanici() != null
                && rezervasyon.getKullanici().getId() == aktifKullanici.getId()
                && "BEKLEMEDE".equals(rezervasyon.getDurum())) {

            rezervasyon.setDurum("IPTAL");
            rezervasyon.setAciklama("Kullanıcı rezervasyonunu iptal etti.");
            rezervasyonlarRepository.save(rezervasyon);
        }

        return "redirect:/user/rezervasyonlar";
    }
}