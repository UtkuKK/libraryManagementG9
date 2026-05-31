package com.example.library_management.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cezalar")
public class Ceza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "kullanici_id")
    private int kullaniciId;

    @Column(name = "odunc_id")
    private int oduncId;

    @Column(name = "ceza_miktari")
    private BigDecimal cezaMiktari;

    @Column(name = "aciklama")
    private String aciklama;

    @Column(name = "odendi_mi", columnDefinition = "boolean default false")
    private Boolean odendiMi = false;

    @Column(name = "odenen_miktar", columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    private BigDecimal odenenMiktar = BigDecimal.ZERO;

    @Column(name = "odeme_tarihi")
    private java.time.LocalDate odemeTarihi;

    @Column(name = "tahsil_eden_kullanici_id")
    private Integer tahsilEdenKullaniciId;


    @Transient
    private String kullaniciAdi;

    @Transient
    private String kitapAdi;

    @Transient
    private long gecikmeGunu;


    public Ceza() {
    }


    public Ceza(int kullaniciId, int oduncId, BigDecimal cezaMiktari, String aciklama, Boolean odendiMi) {
        this.kullaniciId = kullaniciId;
        this.oduncId = oduncId;
        this.cezaMiktari = cezaMiktari;
        this.aciklama = aciklama;
        this.odendiMi = odendiMi;
    }


    public Ceza(int id, int kullaniciId, int oduncId, BigDecimal cezaMiktari, String aciklama, Boolean odendiMi) {
        this.id = id;
        this.kullaniciId = kullaniciId;
        this.oduncId = oduncId;
        this.cezaMiktari = cezaMiktari;
        this.aciklama = aciklama;
        this.odendiMi = odendiMi;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getKullaniciId() { return kullaniciId; }
    public void setKullaniciId(int kullaniciId) { this.kullaniciId = kullaniciId; }

    public int getOduncId() { return oduncId; }
    public void setOduncId(int oduncId) { this.oduncId = oduncId; }

    public BigDecimal getCezaMiktari() { return cezaMiktari; }
    public void setCezaMiktari(BigDecimal cezaMiktari) { this.cezaMiktari = cezaMiktari; }

    public String getAciklama() { return aciklama; }
    public void setAciklama(String aciklama) { this.aciklama = aciklama; }

    public boolean isOdendiMi() {
        return odendiMi != null && odendiMi;
    }

    public void setOdendiMi(Boolean odendiMi) {
        this.odendiMi = odendiMi;
    }

    public BigDecimal getOdenenMiktar() { return odenenMiktar; }
    public void setOdenenMiktar(BigDecimal odenenMiktar) { this.odenenMiktar = odenenMiktar; }

    public java.time.LocalDate getOdemeTarihi() { return odemeTarihi; }
    public void setOdemeTarihi(java.time.LocalDate odemeTarihi) { this.odemeTarihi = odemeTarihi; }

    public Integer getTahsilEdenKullaniciId() { return tahsilEdenKullaniciId; }
    public void setTahsilEdenKullaniciId(Integer tahsilEdenKullaniciId) { this.tahsilEdenKullaniciId = tahsilEdenKullaniciId; }

    public String getKullaniciAdi() { return kullaniciAdi; }
    public void setKullaniciAdi(String kullaniciAdi) { this.kullaniciAdi = kullaniciAdi; }

    public String getKitapAdi() { return kitapAdi; }
    public void setKitapAdi(String kitapAdi) { this.kitapAdi = kitapAdi; }

    public long getGecikmeGunu() { return gecikmeGunu; }
    public void setGecikmeGunu(long gecikmeGunu) { this.gecikmeGunu = gecikmeGunu; }

    @Override
    public String toString() {
        return "Ceza{id=" + id + ", kullaniciId=" + kullaniciId +
                ", cezaMiktari=" + cezaMiktari + ", odendiMi=" + odendiMi + '}';
    }
}