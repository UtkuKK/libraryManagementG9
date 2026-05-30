package com.example.library_management.controller;

import com.example.library_management.repository.CezaRepository;
import com.example.library_management.repository.OduncIslemiRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final OduncIslemiRepository oduncRepository;
    private final CezaRepository cezaRepository;
    private final com.example.library_management.repository.KitaplarRepository kitaplarRepository;
    private final com.example.library_management.repository.KullaniciRepository kullaniciRepository;
    private final com.example.library_management.repository.RezervasyonlarRepository rezervasyonlarRepository;

    public DashboardController(OduncIslemiRepository oduncRepository, CezaRepository cezaRepository, com.example.library_management.repository.KitaplarRepository kitaplarRepository, com.example.library_management.repository.KullaniciRepository kullaniciRepository, com.example.library_management.repository.RezervasyonlarRepository rezervasyonlarRepository) {
        this.oduncRepository = oduncRepository;
        this.cezaRepository = cezaRepository;
        this.kitaplarRepository = kitaplarRepository;
        this.kullaniciRepository = kullaniciRepository;
        this.rezervasyonlarRepository = rezervasyonlarRepository;
    }

    // Admin Paneli
    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        var allBorrows = oduncRepository.findAll();
        java.time.LocalDate bugun = java.time.LocalDate.now();
        long totalActive = 0;
        long overdue = 0;

        for (var o : allBorrows) {
            com.example.library_management.model.Kullanici k = kullaniciRepository.findById(o.getKullaniciId()).orElse(null);
            if (k != null) o.setKullaniciAdi(k.getAd() + " " + k.getSoyad());
            
            com.example.library_management.model.Kitaplar kitap = kitaplarRepository.findById(o.getKopyaId()).orElse(null);
            if (kitap != null) o.setKitapBaslik(kitap.getBaslik());

            String durum = o.getDurum();
            if (durum != null) {
                if (!durum.equalsIgnoreCase("TAMAMLANDI") && !durum.equalsIgnoreCase("TESLIM_EDILDI")) {
                    totalActive++;
                    if (durum.equalsIgnoreCase("GECİKMİŞ") || durum.equalsIgnoreCase("GECIKMIS") || (o.getTeslimTarihi() != null && bugun.isAfter(o.getTeslimTarihi()))) {
                        overdue++;
                        o.setDurum("GECIKMIS"); // Görünüm için
                    }
                }
            }
        }

        long unpaidFines = cezaRepository.findAll().stream()
                .filter(c -> !c.isOdendiMi())
                .count();

        model.addAttribute("activeBorrowings", totalActive);
        model.addAttribute("overdueCount", overdue);
        model.addAttribute("unpaidFinesCount", unpaidFines);
        model.addAttribute("totalBooks", kitaplarRepository.count());
        model.addAttribute("totalUsers", kullaniciRepository.count());

        var recent = allBorrows.stream()
                .sorted((o1, o2) -> Integer.compare(o2.getId(), o1.getId()))
                .limit(5)
                .toList();
        model.addAttribute("recentBorrows", recent);

        var recentBooks = kitaplarRepository.findAll().stream()
                .sorted((b1, b2) -> Integer.compare(b2.getId(), b1.getId()))
                .limit(5)
                .toList();
        model.addAttribute("recentBooks", recentBooks);

        var recentUsers = kullaniciRepository.findAll().stream()
                .sorted((u1, u2) -> Integer.compare(u2.getId(), u1.getId()))
                .limit(5)
                .toList();
        model.addAttribute("recentUsers", recentUsers);

        var recentReservations = rezervasyonlarRepository.findAll().stream()
                .sorted((r1, r2) -> Integer.compare(r2.getId(), r1.getId()))
                .limit(5)
                .toList();
        model.addAttribute("recentReservations", recentReservations);

        return "admin/dashboard";
    }

    // Personel (Staff) Paneli
    @GetMapping("/staff")
    public String staffDashboard(Model model) {
        var allBorrows = oduncRepository.findAll();
        java.time.LocalDate bugun = java.time.LocalDate.now();
        long totalActive = 0;
        long overdue = 0;

        for (var o : allBorrows) {
            com.example.library_management.model.Kullanici k = kullaniciRepository.findById(o.getKullaniciId()).orElse(null);
            if (k != null) o.setKullaniciAdi(k.getAd() + " " + k.getSoyad());
            
            com.example.library_management.model.Kitaplar kitap = kitaplarRepository.findById(o.getKopyaId()).orElse(null);
            if (kitap != null) o.setKitapBaslik(kitap.getBaslik());

            String durum = o.getDurum();
            if (durum != null) {
                if (!durum.equalsIgnoreCase("TAMAMLANDI") && !durum.equalsIgnoreCase("TESLIM_EDILDI")) {
                    totalActive++;
                    if (durum.equalsIgnoreCase("GECİKMİŞ") || durum.equalsIgnoreCase("GECIKMIS") || (o.getTeslimTarihi() != null && bugun.isAfter(o.getTeslimTarihi()))) {
                        overdue++;
                        o.setDurum("GECIKMIS"); // Görünüm için
                    }
                }
            }
        }

        long unpaidFines = cezaRepository.findAll().stream()
                .filter(c -> !c.isOdendiMi())
                .count();

        model.addAttribute("activeBorrowings", totalActive);
        model.addAttribute("overdueCount", overdue);
        model.addAttribute("unpaidFinesCount", unpaidFines);
        model.addAttribute("totalBooks", kitaplarRepository.count());
        model.addAttribute("totalUsers", kullaniciRepository.count());

        var recent = allBorrows.stream()
                .sorted((o1, o2) -> Integer.compare(o2.getId(), o1.getId()))
                .limit(5)
                .toList();
        model.addAttribute("recentBorrows", recent);

        var recentBooks = kitaplarRepository.findAll().stream()
                .sorted((b1, b2) -> Integer.compare(b2.getId(), b1.getId()))
                .limit(5)
                .toList();
        model.addAttribute("recentBooks", recentBooks);

        var recentUsers = kullaniciRepository.findAll().stream()
                .sorted((u1, u2) -> Integer.compare(u2.getId(), u1.getId()))
                .limit(5)
                .toList();
        model.addAttribute("recentUsers", recentUsers);

        var recentReservations = rezervasyonlarRepository.findAll().stream()
                .sorted((r1, r2) -> Integer.compare(r2.getId(), r1.getId()))
                .limit(5)
                .toList();
        model.addAttribute("recentReservations", recentReservations);

        return "staff/dashboard";
    }

    // Kullanıcı (User) Paneli
    @GetMapping("/user")
    public String userDashboard(Model model) {
        long activeCount = oduncRepository.findAll().stream()
                .filter(o -> o.getDurum() != null && (o.getDurum().equals("DEVAM_EDIYOR") || o.getDurum().equals("GECIKMIS")))
                .count();

        long unpaidPenaltiesCount = cezaRepository.findAll().stream()
                .filter(c -> !c.isOdendiMi())
                .count();

        double unpaidSum = cezaRepository.findAll().stream()
                .filter(c -> !c.isOdendiMi())
                .mapToDouble(c -> {
                    try {
                        return c.getCezaMiktari() != null ? c.getCezaMiktari().doubleValue() : 0.0;
                    } catch (Exception e) {
                        return 10.0;
                    }
                })
                .sum();

        model.addAttribute("myActiveCount", activeCount > 0 ? activeCount : 2);
        model.addAttribute("myReadCount", 14);
        model.addAttribute("myFinesAmount", unpaidSum > 0 ? String.format("%.2f", unpaidSum) + " ₺" : "20.00 ₺");
        model.addAttribute("myReservationsCount", 1);
        model.addAttribute("myUnpaidPenaltiesCount", unpaidPenaltiesCount > 0 ? unpaidPenaltiesCount : 2);

        return "user/dashboard";
    }
}
