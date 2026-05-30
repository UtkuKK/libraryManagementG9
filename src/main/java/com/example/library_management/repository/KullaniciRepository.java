package com.example.library_management.repository;

import com.example.library_management.model.Kullanici;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KullaniciRepository extends JpaRepository<Kullanici, Integer> {

    Optional<Kullanici> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Kullanici> findByEmailAndTelefon(String email, String telefon);
}