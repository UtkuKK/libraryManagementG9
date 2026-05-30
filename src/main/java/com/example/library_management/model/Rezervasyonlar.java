package com.example.library_management.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "rezervasyonlar")
public class Rezervasyonlar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "kullanici_id", nullable = false)
    private Kullanici kullanici;

    @ManyToOne
    @JoinColumn(name = "kitap_id", nullable = false)
    private Kitaplar kitap;

    @Column(name = "rezervasyon_tarihi", nullable = false)
    private LocalDate rezervasyonTarihi;

    @Column(name = "durum", nullable = false)
    private String durum = "BEKLEMEDE";

    @Column(name = "aciklama")
    private String aciklama;

    public Rezervasyonlar() {
    }

    public Rezervasyonlar(Kullanici kullanici, Kitaplar kitap, LocalDate rezervasyonTarihi, String durum, String aciklama) {
        this.kullanici = kullanici;
        this.kitap = kitap;
        this.rezervasyonTarihi = rezervasyonTarihi;
        this.durum = durum;
        this.aciklama = aciklama;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public Kullanici getKullanici() {
        return kullanici;
    }

    public void setKullanici(Kullanici kullanici) {
        this.kullanici = kullanici;
    }

    public Kitaplar getKitap() {
        return kitap;
    }

    public void setKitap(Kitaplar kitap) {
        this.kitap = kitap;
    }

    public LocalDate getRezervasyonTarihi() {
        return rezervasyonTarihi;
    }

    public void setRezervasyonTarihi(LocalDate rezervasyonTarihi) {
        this.rezervasyonTarihi = rezervasyonTarihi;
    }

    public String getDurum() {
        return durum;
    }

    public void setDurum(String durum) {
        this.durum = durum;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }
}