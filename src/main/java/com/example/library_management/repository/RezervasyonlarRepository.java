package com.example.library_management.repository;

import com.example.library_management.model.Rezervasyonlar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RezervasyonlarRepository extends JpaRepository<Rezervasyonlar, Integer> {

    List<Rezervasyonlar> findByDurum(String durum);

    List<Rezervasyonlar> findByKullaniciId(int kullaniciId);

    boolean existsByKullaniciIdAndKitapIdAndDurumIn(
            int kullaniciId,
            int kitapId,
            List<String> durumlar
    );
}