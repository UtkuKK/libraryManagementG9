package com.example.library_management.model;

import jakarta.persistence.*;

@Entity
@Table(name = "kategoriler")
public class Kategori {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "ad", nullable = false, unique = true)
    private String ad;

    @Column(name = "aciklama")
    private String aciklama;

    public Kategori() {}

    public Kategori(String ad, String aciklama) {
        this.ad = ad;
        this.aciklama = aciklama;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }

    public String getAciklama() { return aciklama; }
    public void setAciklama(String aciklama) { this.aciklama = aciklama; }
}
