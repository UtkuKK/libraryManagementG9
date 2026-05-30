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

    @Column(name = "odendi_mi")
    private boolean odendiMi;


    @Transient
    private String kullaniciAdi;

    @Transient
    private String kitapAdi;

    @Transient
    private long gecikmeGunu;


    public Ceza() {
    }


    public Ceza(int kullaniciId, int oduncId, BigDecimal cezaMiktari, String aciklama, boolean odendiMi) {
        this.kullaniciId = kullaniciId;
        this.oduncId = oduncId;
        this.cezaMiktari = cezaMiktari;
        this.aciklama = aciklama;
        this.odendiMi = odendiMi;
    }


    public Ceza(int id, int kullaniciId, int oduncId, BigDecimal cezaMiktari, String aciklama, boolean odendiMi) {
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

    public boolean isOdendiMi() { return odendiMi; }
    public void setOdendiMi(boolean odendiMi) { this.odendiMi = odendiMi; }

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