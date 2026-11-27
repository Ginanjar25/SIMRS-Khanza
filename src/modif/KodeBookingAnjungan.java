package modif;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Kode booking 6 karakter untuk check-in booking registrasi di anjungan, dibuat sendiri oleh SIMRS (tanpa aplikasi anjungan).
 * Rumus sama persis dengan aplikasi anjungan (internal/kodebooking/booking.go, KODE-BOOKING.md):
 * v = 9497 x 65536 + sidik x 1.000.000 + No.RM, sidik = (hari sejak 2024-01-01 + FNV-1a kode dokter) mod 265,
 * lalu disamarkan dengan Feistel HMAC-SHA256 dan ditulis basis 31.
 * Secret = SECRETKODEBOOKING di database.xml (terenkripsi), nilainya sama dengan booking.secret aplikasi anjungan.
 */
public class KodeBookingAnjungan {
    private static final String ALFABET="23456789ABCDEFGHJKMNPQRSTUVWXYZ";
    private static final long N=887503681L; // 31^6
    private static final int MASK=0x7FFF,BATAS=9497*65536,MAKS_RM=1000000,DAUR=(int)((N-BATAS)/MAKS_RM);
    private static final LocalDate AWAL=LocalDate.of(2024,1,1);
    private final Mac hmac;

    public KodeBookingAnjungan() throws Exception {
        this(SettingAnjungan.secretKodeBooking());
    }

    public KodeBookingAnjungan(String secret) throws Exception {
        if((secret==null)||secret.equals("")){
            throw new Exception("SECRETKODEBOOKING belum diisi di setting/database.xml..!!");
        }
        hmac=Mac.getInstance("HmacSHA256");
        hmac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),"HmacSHA256"));
    }

    /**
     * @param norm No.RM 6 digit angka
     * @param tanggal tanggal periksa yyyy-MM-dd
     * @param kddokter kode dokter booking
     */
    public String buat(String norm,String tanggal,String kddokter) throws Exception {
        if((norm==null)||!norm.matches("[0-9]{6}")){
            throw new Exception("Kode booking anjungan hanya untuk No.RM 6 digit angka..!!");
        }
        if((kddokter==null)||kddokter.trim().equals("")){
            throw new Exception("Kode dokter booking kosong..!!");
        }
        long hari=ChronoUnit.DAYS.between(AWAL,LocalDate.parse(tanggal.trim()));
        if(hari<0){
            throw new Exception("Tanggal periksa di luar rentang kode booking..!!");
        }
        int sidik=(int)((hari+fnv(kddokter.trim().toUpperCase())%DAUR)%DAUR);
        return tulis(samarkan(BATAS+sidik*MAKS_RM+Integer.parseInt(norm)));
    }

    // FNV-1a 32 bit, tanpa tanda
    private static long fnv(String teks){
        long h=2166136261L;
        for(byte b:teks.getBytes(StandardCharsets.UTF_8)){
            h=((h^(b&0xFF))*16777619L)&0xFFFFFFFFL;
        }
        return h;
    }

    private int f(int i,int x){
        byte[] h=hmac.doFinal(new byte[]{(byte)i,(byte)(x>>8),(byte)x});
        return (((h[0]&0xFF)<<8)|(h[1]&0xFF))&MASK;
    }

    private long samarkan(long v){
        do{
            int l=(int)((v>>15)&MASK),r=(int)(v&MASK);
            for(int i=0;i<4;i++){
                int t=r;
                r=l^f(i,r);
                l=t;
            }
            v=((long)l<<15)|r;
        }while(v>=N);
        return v;
    }

    private static String tulis(long v){
        char[] kode=new char[6];
        for(int i=5;i>=0;i--){
            kode[i]=ALFABET.charAt((int)(v%31));
            v/=31;
        }
        return new String(kode);
    }
}
