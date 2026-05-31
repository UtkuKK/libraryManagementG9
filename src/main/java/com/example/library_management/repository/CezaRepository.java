package com.example.library_management.repository;

import com.example.library_management.model.Ceza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CezaRepository extends JpaRepository<Ceza, Integer> {



    long countByKullaniciIdAndOdendiMiFalse(int kullaniciId);
}