package com.example.library_management.model;

import jakarta.persistence.*;

@Entity
@Table(name = "yayinevleri")
public class Yayinevi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "ad", nullable = false, unique = true)
    private String ad;

    @Column(name = "adres")
    private String adres;

    @Column(name = "telefon")
    private String telefon;

    public Yayinevi() {}

    public Yayinevi(String ad, String adres) {
        this.ad = ad;
        this.adres = adres;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }

    public String getAdres() { return adres; }
    public void setAdres(String adres) { this.adres = adres; }

    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }
}
