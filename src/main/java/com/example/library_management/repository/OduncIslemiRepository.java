package com.example.library_management.repository;

import com.example.library_management.model.OduncIslemi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OduncIslemiRepository extends JpaRepository<OduncIslemi, Integer> {

}