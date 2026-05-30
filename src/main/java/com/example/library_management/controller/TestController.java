package com.example.library_management.controller;

import com.example.library_management.model.OduncIslemi;
import com.example.library_management.repository.OduncIslemiRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class TestController {

    private final OduncIslemiRepository repo;

    public TestController(OduncIslemiRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/api/test-odunc")
    public List<OduncIslemi> testOdunc() {
        return repo.findAll();
    }
}
