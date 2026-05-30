package com.example.library_management.repository;

import com.example.library_management.model.Ceza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CezaRepository extends JpaRepository<Ceza, Integer> {

    // JpaRepository'den kalıtım aldığı için aşağıdaki metodlar otomatik olarak hazırdır:
    // - findAll(): Tüm cezaları getirir.
    // - save(Ceza): Yeni ceza kaydeder veya günceller.
    // - deleteById(Integer): ID'ye göre ceza siler.
    // - findById(Integer): ID'ye göre tek bir ceza getirir.

    long countByKullaniciIdAndOdendiMiFalse(int kullaniciId);
}