package com.example.library_management.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "odunc_islemleri")
public class OduncIslemi {


    public static final String DURUM_DEVAM_EDIYOR = "DEVAM_EDIYOR";
    public static final String DURUM_TESLIM_EDILDI = "TESLIM_EDILDI";
    public static final double GUNLUK_CEZA_UCRETI = 10.00;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "kopya_id")
    private int kopyaId;

    @Column(name = "kullanici_id")
    private int kullaniciId;

    @Column(name = "verilis_tarihi")
    private LocalDate verilisTarihi;

    @Column(name = "teslim_tarihi")
    private LocalDate teslimTarihi;

    @Column(name = "gercek_teslim_tarihi")
    private LocalDate gercekTeslimTarihi;

    @Column(name = "durum")
    private String durum;

    @Column(name = "aciklama")
    private String aciklama;

    @Column(name = "kitap_durumu")
    private String kitapDurumu;

    @Column(name = "uzatildi_mi")
    private Boolean uzatildiMi = false;


    @Transient
    private String kitapBaslik;

    @Transient
    private String kitapYazari;

    @Transient
    private String kullaniciAdi;

    @Transient
    private String barkod;

    @Transient
    private Integer kitapStok;


    public OduncIslemi() {
    }


    public OduncIslemi(int id, int kopyaId, int kullaniciId, LocalDate verilisTarihi,
                       LocalDate teslimTarihi, LocalDate gercekTeslimTarihi, String durum) {
        this.id = id;
        this.kopyaId = kopyaId;
        this.kullaniciId = kullaniciId;
        this.verilisTarihi = verilisTarihi;
        this.teslimTarihi = teslimTarihi;
        this.gercekTeslimTarihi = gercekTeslimTarihi;
        this.durum = durum;
    }


    public OduncIslemi(int kopyaId, int kullaniciId, LocalDate verilisTarihi,
                       LocalDate teslimTarihi, String durum) {
        this.kopyaId = kopyaId;
        this.kullaniciId = kullaniciId;
        this.verilisTarihi = verilisTarihi;
        this.teslimTarihi = teslimTarihi;
        this.durum = durum;
    }


    public long gecikmeGunSayisi() {
        LocalDate teslim = (gercekTeslimTarihi != null) ? gercekTeslimTarihi : LocalDate.now();
        if (teslimTarihi != null && teslim.isAfter(teslimTarihi)) {
            return ChronoUnit.DAYS.between(teslimTarihi, teslim);
        }
        return 0;
    }

    public long kalanGunSayisi() {
        if (teslimTarihi == null) return 0;
        return ChronoUnit.DAYS.between(LocalDate.now(), teslimTarihi);
    }

    public double cezaTutari() {
        return gecikmeGunSayisi() * GUNLUK_CEZA_UCRETI;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getKopyaId() { return kopyaId; }
    public void setKopyaId(int kopyaId) { this.kopyaId = kopyaId; }

    public int getKullaniciId() { return kullaniciId; }
    public void setKullaniciId(int kullaniciId) { this.kullaniciId = kullaniciId; }

    public LocalDate getVerilisTarihi() { return verilisTarihi; }
    public void setVerilisTarihi(LocalDate verilisTarihi) { this.verilisTarihi = verilisTarihi; }

    public LocalDate getTeslimTarihi() { return teslimTarihi; }
    public void setTeslimTarihi(LocalDate teslimTarihi) { this.teslimTarihi = teslimTarihi; }

    public LocalDate getGercekTeslimTarihi() { return gercekTeslimTarihi; }
    public void setGercekTeslimTarihi(LocalDate gercekTeslimTarihi) { this.gercekTeslimTarihi = gercekTeslimTarihi; }

    public String getDurum() { return durum; }
    public void setDurum(String durum) { this.durum = durum; }

    public String getKitapBaslik() { return kitapBaslik; }
    public void setKitapBaslik(String kitapBaslik) { this.kitapBaslik = kitapBaslik; }

    public String getKitapYazari() { return kitapYazari; }
    public void setKitapYazari(String kitapYazari) { this.kitapYazari = kitapYazari; }

    public String getKullaniciAdi() { return kullaniciAdi; }
    public void setKullaniciAdi(String kullaniciAdi) { this.kullaniciAdi = kullaniciAdi; }

    public String getBarkod() { return barkod; }
    public void setBarkod(String barkod) { this.barkod = barkod; }

    public String getAciklama() { return aciklama; }
    public void setAciklama(String aciklama) { this.aciklama = aciklama; }

    public String getKitapDurumu() { return kitapDurumu; }
    public void setKitapDurumu(String kitapDurumu) { this.kitapDurumu = kitapDurumu; }

    public boolean isUzatildiMi() {
        return uzatildiMi != null && uzatildiMi;
    }

    public void setUzatildiMi(Boolean uzatildiMi) {
        this.uzatildiMi = uzatildiMi;
    }

    public Integer getKitapStok() { return kitapStok; }
    public void setKitapStok(Integer kitapStok) { this.kitapStok = kitapStok; }

    @Override
    public String toString() {
        return "OduncIslemi{id=" + id + ", kopyaId=" + kopyaId +
                ", kullaniciId=" + kullaniciId + ", durum='" + durum + "'}";
    }
}