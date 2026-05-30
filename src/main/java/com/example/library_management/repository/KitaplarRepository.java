package com.example.library_management.repository;

import com.example.library_management.model.Kitaplar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KitaplarRepository extends JpaRepository<Kitaplar, Integer> {

    List<Kitaplar> findByBaslikContainingIgnoreCase(String baslik);

    List<Kitaplar> findByKategoriId(int kategoriId);

    boolean existsByIsbn(String isbn);

    List<Kitaplar> findByBaslikContainingIgnoreCaseOrYazar_AdContainingIgnoreCaseOrYazar_SoyadContainingIgnoreCaseOrKategori_AdContainingIgnoreCase(
            String baslik,
            String yazarAd,
            String yazarSoyad,
            String kategoriAd
    );
}