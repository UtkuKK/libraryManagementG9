package com.example.library_management.repository;

import com.example.library_management.model.Kategori;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KategoriRepository extends JpaRepository<Kategori, Integer> {

    boolean existsByAdIgnoreCase(String ad);

    Optional<Kategori> findByAdIgnoreCase(String ad);
}