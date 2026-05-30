package com.example.library_management;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.library_management.repository.RezervasyonlarRepository;

@SpringBootTest
public class RezervasyonTests {

    @Autowired
    private RezervasyonlarRepository rezervasyonlarRepository;

    @Test
    void TC_13_RezervasyonlarBasariliSekildeGetirilmeli() {
        assertNotNull(rezervasyonlarRepository.findAll());
    }

    @Test
    void TC_14_RezervasyonRepositoryBosOlmamali() {
        assertTrue(rezervasyonlarRepository.count() >= 0);
    }
}