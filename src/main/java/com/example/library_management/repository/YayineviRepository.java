package com.example.library_management.repository;

import com.example.library_management.model.Yayinevi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface YayineviRepository extends JpaRepository<Yayinevi, Integer> {

    boolean existsByAdIgnoreCase(String ad);

    Optional<Yayinevi> findByAdIgnoreCase(String ad);
}