package modif;

import AESsecurity.EnkripsiAES;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Setting antrean anjungan dari setting/database.xml, dipisah dari fungsi.koneksiDB supaya file asli Khanza tidak berubah.
 * AKTIFKANANJUNGAN (yes/no, teks biasa), SECRETKODEBOOKING (terenkripsi EnkripsiAES).
 */
public class SettingAnjungan {
    private static final String BERKAS="setting/database.xml";

    private static Properties baca() throws Exception {
        Properties prop=new Properties();
        try (FileInputStream berkas=new FileInputStream(BERKAS)) {
            prop.loadFromXML(berkas);
        }
        return prop;
    }

    /** true bila fitur Antrean Anjungan diaktifkan (AKTIFKANANJUNGAN = yes). Bila entry tidak ada, fitur nonaktif. */
    public static boolean aktif(){
        try {
            return baca().getProperty("AKTIFKANANJUNGAN","no").trim().equalsIgnoreCase("yes");
        } catch (Exception e) {
            return false;
        }
    }

    /** Secret kode booking anjungan, sama dengan booking.secret aplikasi anjungan. Kosong bila belum diisi. */
    public static String secretKodeBooking(){
        try {
            return EnkripsiAES.decrypt(baca().getProperty("SECRETKODEBOOKING"));
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Koneksi tersendiri (bukan koneksi bersama koneksiDB.condb()), wajib ditutup pemanggil.
     * Untuk transaksi yang tidak boleh tercampur query lain. Alamat dan user sama dengan koneksiDB.condb().
     */
    public static Connection koneksiBaru() throws SQLException {
        try {
            Properties prop=baca();
            MysqlDataSource sumber=new MysqlDataSource();
            sumber.setURL("jdbc:mysql://"+EnkripsiAES.decrypt(prop.getProperty("HOST"))+":"+EnkripsiAES.decrypt(prop.getProperty("PORT"))+"/"+
                EnkripsiAES.decrypt(prop.getProperty("DATABASE"))+"?zeroDateTimeBehavior=convertToNull&autoReconnect=true&useCompression=true");
            sumber.setUser(EnkripsiAES.decrypt(prop.getProperty("USER")));
            sumber.setPassword(EnkripsiAES.decrypt(prop.getProperty("PAS")));
            return sumber.getConnection();
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("Setting database tidak bisa dibaca : "+e.getMessage(),e);
        }
    }
}
