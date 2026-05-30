package com.example.library_management;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.library_management.repository.KullaniciRepository;

@SpringBootTest
public class KullaniciTests {

    @Autowired
    private KullaniciRepository kullaniciRepository;

    @Test
    void TC_17_KullaniciListesiBosOlmamali() {
        assertFalse(kullaniciRepository.findAll().isEmpty());
    }

    @Test
    void TC_18_KullaniciSayisiSifirdanBuyukOlmali() {
        assertTrue(kullaniciRepository.count() > 0);
    }
}