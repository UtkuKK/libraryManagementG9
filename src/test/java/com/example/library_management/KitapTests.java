package com.example.library_management;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.library_management.repository.KitaplarRepository;

@SpringBootTest
public class KitapTests {

    @Autowired
    private KitaplarRepository kitaplarRepository;

    @Test
    void TC_15_KitapListesiBosOlmamali() {
        assertFalse(kitaplarRepository.findAll().isEmpty());
    }

    @Test
    void TC_16_KitapSayisiSifirdanBuyukOlmali() {
        assertTrue(kitaplarRepository.count() > 0);
    }
}