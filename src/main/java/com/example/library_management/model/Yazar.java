package com.example.library_management.model;

import jakarta.persistence.*;

@Entity
@Table(name = "yazarlar")
public class Yazar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "ad", nullable = false)
    private String ad;

    @Column(name = "soyad", nullable = false)
    private String soyad;

    @Column(name = "biyografi")
    private String biyografi;

    public Yazar() {}

    public Yazar(String ad, String soyad) {
        this.ad = ad;
        this.soyad = soyad;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }

    public String getSoyad() { return soyad; }
    public void setSoyad(String soyad) { this.soyad = soyad; }

    public String getBiyografi() { return biyografi; }
    public void setBiyografi(String biyografi) { this.biyografi = biyografi; }

    public String getAdSoyad() { return ad + " " + soyad; }
}
