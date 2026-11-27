package modif;

/** Satu tiket antrean anjungan (side_db.anjungan_antrean_poli). */
public class TiketAnjungan {
    public long id;
    public String tanggal,kddokter,nmdokter,kdpoli,nmpoli,kategori,jalur,carabayar,notiketfo,noreg,norawat,status,jamcetak,norm,meja,sttsregistrasi;

    /** Tiket BPJS wajib memakai cara bayar BPJ. Pasien langsung (kategori -) dilihat dari cara bayarnya. */
    public boolean isBpjs(){
        return "B".equals(kategori)||"BPJS".equals(carabayar);
    }

    /** Tiket sudah punya registrasi (WEBSITE/MJKN dari booking, atau Onsite yang sudah disimpan). */
    public boolean isTerdaftar(){
        return !norawat.equals("");
    }

    /** Tiket Onsite yang masih bisa diregistrasikan. */
    public boolean isBisaDidaftarkan(){
        return jalur.equals("ONSITE")&&!isTerdaftar()&&isMenunggu();
    }

    /** Registrasi tiket masih ada dan tidak batal. */
    public boolean isRegistrasiAktif(){
        return isTerdaftar()&&!norm.equals("")&&!sttsregistrasi.equals("Batal");
    }

    /** Tiket belum dilayani FO. */
    public boolean isMenunggu(){
        return status.equals("MENUNGGU")||status.equals("DIPANGGIL");
    }
}
