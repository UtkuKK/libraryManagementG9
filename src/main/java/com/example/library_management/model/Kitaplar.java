package com.example.library_management.model;

import jakarta.persistence.*;

@Entity
@Table(name = "kitaplar")
public class Kitaplar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "baslik", nullable = false)
    private String baslik;

    @Column(name = "isbn", unique = true)
    private String isbn;

    @Column(name = "yil")
    private int yil;

    @Column(name = "stok")
    private int stok = 0;

    @Column(name = "aciklama", length = 1000)
    private String aciklama;

    @ManyToOne
    @JoinColumn(name = "yazar_id")
    private Yazar yazar;

    @Column(name = "raf_yeri")
    private String rafYeri;
    public String getRafYeri() {
        return rafYeri;
    }
    public void setRafYeri(String rafYeri) {
        this.rafYeri = rafYeri;
    }

    @ManyToOne
    @JoinColumn(name = "kategori_id")
    private Kategori kategori;

    @ManyToOne
    @JoinColumn(name = "yayinevi_id")
    private Yayinevi yayinevi;

    public Kitaplar() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBaslik() { return baslik; }
    public void setBaslik(String baslik) { this.baslik = baslik; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public int getYil() { return yil; }
    public void setYil(int yil) { this.yil = yil; }

    public int getStok() { return stok; }
    public void setStok(int stok) { this.stok = stok; }

    public String getAciklama() { return aciklama; }
    public void setAciklama(String aciklama) { this.aciklama = aciklama; }

    public Yazar getYazar() { return yazar; }
    public void setYazar(Yazar yazar) { this.yazar = yazar; }

    public Kategori getKategori() { return kategori; }
    public void setKategori(Kategori kategori) { this.kategori = kategori; }

    public Yayinevi getYayinevi() { return yayinevi; }
    public void setYayinevi(Yayinevi yayinevi) { this.yayinevi = yayinevi; }


}