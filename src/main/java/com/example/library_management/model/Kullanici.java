package com.example.library_management.model;

import jakarta.persistence.*;

@Entity
@Table(name = "kullanicilar")
public class Kullanici {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "ad", nullable = false)
    private String ad;

    @Column(name = "soyad", nullable = false)
    private String soyad;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "sifre", nullable = false)
    private String sifre;

    @Column(name = "telefon")
    private String telefon;

    @Column(name = "adres")
    private String adres;

    @Column(name = "rol", nullable = false)
    private String rol = "USER"; // USER veya ADMIN

    @Column(name = "aktif", nullable = false)
    private boolean aktif = true;

    public Kullanici() {}

    public Kullanici(String ad, String soyad, String email, String sifre, String rol) {
        this.ad = ad;
        this.soyad = soyad;
        this.email = email;
        this.sifre = sifre;
        this.rol = rol;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }

    public String getSoyad() { return soyad; }
    public void setSoyad(String soyad) { this.soyad = soyad; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSifre() { return sifre; }
    public void setSifre(String sifre) { this.sifre = sifre; }

    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }

    public String getAdres() { return adres; }
    public void setAdres(String adres) { this.adres = adres; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public boolean isAktif() { return aktif; }
    public void setAktif(boolean aktif) { this.aktif = aktif; }

    public String getAdSoyad() { return ad + " " + soyad; }

    @Override
    public String toString() {
        return "Kullanici{id=" + id + ", email='" + email + "', rol='" + rol + "'}";
    }
}
