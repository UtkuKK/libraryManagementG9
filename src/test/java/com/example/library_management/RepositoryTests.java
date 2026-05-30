package com.example.library_management;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.library_management.repository.CezaRepository;
import com.example.library_management.repository.KategoriRepository;
import com.example.library_management.repository.KitaplarRepository;
import com.example.library_management.repository.KullaniciRepository;
import com.example.library_management.repository.OduncIslemiRepository;
import com.example.library_management.repository.RezervasyonlarRepository;
import com.example.library_management.repository.YayineviRepository;
import com.example.library_management.repository.YazarRepository;

@SpringBootTest
public class RepositoryTests {

    @Autowired
    private KitaplarRepository kitaplarRepository;

    @Autowired
    private KullaniciRepository kullaniciRepository;

    @Autowired
    private KategoriRepository kategoriRepository;

    @Autowired
    private YazarRepository yazarRepository;

    @Autowired
    private YayineviRepository yayineviRepository;

    @Autowired
    private OduncIslemiRepository oduncIslemiRepository;

    @Autowired
    private RezervasyonlarRepository rezervasyonlarRepository;

    @Autowired
    private CezaRepository cezaRepository;

    @Test
    void kitaplarRepositoryCalisiyor() {
        assertNotNull(kitaplarRepository.findAll());
    }

    @Test
    void kullaniciRepositoryCalisiyor() {
        assertNotNull(kullaniciRepository.findAll());
    }

    @Test
    void kategoriRepositoryCalisiyor() {
        assertNotNull(kategoriRepository.findAll());
    }

    @Test
    void yazarRepositoryCalisiyor() {
        assertNotNull(yazarRepository.findAll());
    }

    @Test
    void yayineviRepositoryCalisiyor() {
        assertNotNull(yayineviRepository.findAll());
    }

    @Test
    void oduncRepositoryCalisiyor() {
        assertNotNull(oduncIslemiRepository.findAll());
    }

    @Test
    void rezervasyonRepositoryCalisiyor() {
        assertNotNull(rezervasyonlarRepository.findAll());
    }

    @Test
    void cezaRepositoryCalisiyor() {
        assertNotNull(cezaRepository.findAll());
    }
}