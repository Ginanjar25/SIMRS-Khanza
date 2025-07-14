/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fungsi;

import static com.sun.org.glassfish.external.amx.AMXUtil.prop;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import fungsi.sekuel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import fungsi.koneksiDB;
import java.sql.Connection;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.sql.SQLException;

/**
 *
 * @author khanzasoft
 */
public class CheckPenjabMissmatch {
    public String link="";
    private static String var="";
    private sekuel Sequel=new sekuel();
    private Connection koneksi=koneksiDB.condb();
    private static final Properties prop = new Properties();
    private PreparedStatement ps;
    private ResultSet rs;
    String kd_petugas = "placeholder", kd_petugas2 ="placeholder", kd_petugas3="placeholder", nm_petugas="", nm_petugas2="", nm_petugas3="";
    
    // SQL queries untuk mengecek mismatch penjab
    String sqldr = "SELECT reg_periksa.kd_pj, rawat_jl_dr.kd_jenis_prw, jns_perawatan.nm_perawatan, "
            + "rawat_jl_dr.no_rawat, rawat_jl_dr.tarif_tindakandr "
            + "FROM rawat_jl_dr "
            + "INNER JOIN jns_perawatan ON rawat_jl_dr.kd_jenis_prw = jns_perawatan.kd_jenis_prw "
            + "INNER JOIN reg_periksa ON reg_periksa.no_rawat = rawat_jl_dr.no_rawat "
            + "WHERE rawat_jl_dr.no_rawat = ? "
            + "AND jns_perawatan.kd_pj != reg_periksa.kd_pj\n"
            + "AND jns_perawatan.kd_pj NOT IN ('-', '')";
    
    String sqldrinap = "SELECT reg_periksa.kd_pj, rawat_inap_dr.kd_jenis_prw, jns_perawatan_inap.nm_perawatan, "
            + "rawat_inap_dr.no_rawat, rawat_inap_dr.tarif_tindakandr "
            + "FROM rawat_inap_dr "
            + "INNER JOIN jns_perawatan_inap ON rawat_inap_dr.kd_jenis_prw = jns_perawatan_inap.kd_jenis_prw "
            + "INNER JOIN reg_periksa ON reg_periksa.no_rawat = rawat_inap_dr.no_rawat "
            + "WHERE rawat_inap_dr.no_rawat = ? "
            + "AND jns_perawatan_inap.kd_pj != reg_periksa.kd_pj\n"
            + "AND jns_perawatan_inap.kd_pj NOT IN ('-', '')";
            
    String sqldrpr = "SELECT reg_periksa.kd_pj, rawat_jl_drpr.kd_jenis_prw, jns_perawatan.nm_perawatan, "
            + "rawat_jl_drpr.no_rawat, rawat_jl_drpr.tarif_tindakandr "
            + "FROM rawat_jl_drpr "
            + "INNER JOIN reg_periksa ON reg_periksa.no_rawat = rawat_jl_drpr.no_rawat "
            + "INNER JOIN jns_perawatan ON rawat_jl_drpr.kd_jenis_prw = jns_perawatan.kd_jenis_prw "
            + "WHERE rawat_jl_drpr.no_rawat = ? "
            + "AND jns_perawatan.kd_pj != reg_periksa.kd_pj\n"
            + "AND jns_perawatan.kd_pj NOT IN ('-', '')";
    
     String sqldrprinap = "SELECT reg_periksa.kd_pj, rawat_inap_drpr.kd_jenis_prw, jns_perawatan_inap.nm_perawatan, "
            + "rawat_inap_drpr.no_rawat, rawat_inap_drpr.tarif_tindakandr "
            + "FROM rawat_inap_drpr "
            + "INNER JOIN reg_periksa ON reg_periksa.no_rawat = rawat_inap_drpr.no_rawat "
            + "INNER JOIN jns_perawatan_inap ON rawat_inap_drpr.kd_jenis_prw = jns_perawatan_inap.kd_jenis_prw "
            + "WHERE rawat_inap_drpr.no_rawat = ? "
            + "AND jns_perawatan_inap.kd_pj != reg_periksa.kd_pj\n"
            + "AND jns_perawatan_inap.kd_pj NOT IN ('-', '')";
            
    String sqlpr = "SELECT reg_periksa.kd_pj, rawat_jl_pr.kd_jenis_prw, jns_perawatan.nm_perawatan, "
            + "rawat_jl_pr.no_rawat, rawat_jl_pr.tarif_tindakanpr "
            + "FROM rawat_jl_pr "
            + "INNER JOIN reg_periksa ON reg_periksa.no_rawat = rawat_jl_pr.no_rawat "
            + "INNER JOIN jns_perawatan ON rawat_jl_pr.kd_jenis_prw = jns_perawatan.kd_jenis_prw "
            + "WHERE rawat_jl_pr.no_rawat = ? "
            + "AND jns_perawatan.kd_pj != reg_periksa.kd_pj\n"
            + "AND jns_perawatan.kd_pj NOT IN ('-', '')";
    
     String sqlprinap = "SELECT reg_periksa.kd_pj, rawat_inap_pr.kd_jenis_prw, jns_perawatan_inap.nm_perawatan, "
            + "rawat_inap_pr.no_rawat, rawat_inap_pr.tarif_tindakanpr "
            + "FROM rawat_inap_pr "
            + "INNER JOIN reg_periksa ON reg_periksa.no_rawat = rawat_inap_pr.no_rawat "
            + "INNER JOIN jns_perawatan_inap ON rawat_inap_pr.kd_jenis_prw = jns_perawatan_inap.kd_jenis_prw "
            + "WHERE rawat_inap_pr.no_rawat = ? "
            + "AND jns_perawatan_inap.kd_pj != reg_periksa.kd_pj\n"
            + "AND jns_perawatan_inap.kd_pj NOT IN ('-', '')";
    
    String sqllab = "SELECT reg_periksa.kd_pj,periksa_lab.kd_jenis_prw, jns_perawatan_lab.nm_perawatan,periksa_lab.biaya as total_byrlab, periksa_lab.no_rawat \n"
            + "from periksa_lab\n"
            + "INNER JOIN reg_periksa ON reg_periksa.no_rawat = periksa_lab.no_rawat\n"
            + "INNER join jns_perawatan_lab on periksa_lab.kd_jenis_prw=jns_perawatan_lab.kd_jenis_prw  \n"
            + "where periksa_lab.no_rawat = ? "
            + "AND jns_perawatan_lab.kd_pj != reg_periksa.kd_pj\n"
            + "AND jns_perawatan_lab.kd_pj NOT IN ('-', '')";
    
    String sqlrad = "SELECT reg_periksa.kd_pj,periksa_radiologi.kd_jenis_prw,jns_perawatan_radiologi.nm_perawatan,periksa_radiologi.biaya as total_byrradiologi, periksa_radiologi.no_rawat\n"
            + "from periksa_radiologi \n"
            + "INNER JOIN reg_periksa ON reg_periksa.no_rawat = periksa_radiologi.no_rawat\n"
            + "inner join jns_perawatan_radiologi on periksa_radiologi.kd_jenis_prw=jns_perawatan_radiologi.kd_jenis_prw  \n"
            + "where periksa_radiologi.no_rawat = ? "
            + "AND jns_perawatan_radiologi.kd_pj != reg_periksa.kd_pj\n"
            + "AND jns_perawatan_radiologi.kd_pj NOT IN ('-', '')";
    
    String sqlop = "SELECT reg_periksa.kd_pj, operasi.kode_paket, paket_operasi.nm_perawatan,\n"
            + "(operasi.biayaoperator1+operasi.biayaoperator2+\n"
            + "operasi.biayaoperator3+operasi.biayaasisten_operator1+\n"
            + "operasi.biayaasisten_operator2+operasi.biayaasisten_operator3+\n"
            + "operasi.biayainstrumen+operasi.biayadokter_anak+\n"
            + "operasi.biayaperawaat_resusitas+operasi.biayadokter_anestesi+\n"
            + "operasi.biayaasisten_anestesi+operasi.biayaasisten_anestesi2+\n"
            + "operasi.biayabidan+operasi.biayabidan2+operasi.biayabidan3+\n"
            + "operasi.biayaperawat_luar+operasi.biayaalat+operasi.biayasewaok+\n"
            + "operasi.akomodasi+operasi.bagian_rs+operasi.biaya_omloop+\n"
            + "operasi.biaya_omloop2+operasi.biaya_omloop3+operasi.biaya_omloop4+\n"
            + "operasi.biaya_omloop5+operasi.biayasarpras+operasi.biaya_dokter_pjanak+\n"
            + "operasi.biaya_dokter_umum) as biaya, operasi.no_rawat\n"
            + "FROM operasi\n"
            + "INNER JOIN reg_periksa ON reg_periksa.no_rawat = operasi.no_rawat\n"
            + "INNER JOIN paket_operasi ON operasi.kode_paket = paket_operasi.kode_paket\n"
            + "WHERE operasi.no_rawat = ? "
            + "AND paket_operasi.kd_pj != reg_periksa.kd_pj\n"
            + "AND paket_operasi.kd_pj NOT IN ('-', '')";
        
    public static class PenjabMismatch {
        private String kdPj;
        private String kdJenisPrw;
        private String nmPerawatan;
        private String noRawat;
        private double tarif;
        private String jenisTindakan; // dr, drpr, atau pr
        
        public PenjabMismatch(String kdPj, String kdJenisPrw, String nmPerawatan, String noRawat, double tarif, String jenisTindakan) {
            this.kdPj = kdPj;
            this.kdJenisPrw = kdJenisPrw;
            this.nmPerawatan = nmPerawatan;
            this.noRawat = noRawat;
            this.tarif = tarif;
            this.jenisTindakan = jenisTindakan;
        }
        
        // Getters
        public String getKdPj() { return kdPj; }
        public String getKdJenisPrw() { return kdJenisPrw; }
        public String getNmPerawatan() { return nmPerawatan; }
        public String getNoRawat() { return noRawat; }
        public double getTarif() { return tarif; }
        public String getJenisTindakan() { return jenisTindakan; }
        
        @Override
        public String toString() {
            return String.format("PenjabMismatch{noRawat='%s', kdPj='%s', kdJenisPrw='%s', nmPerawatan='%s', tarif=%.2f, jenis='%s'}", 
                               noRawat, kdPj, kdJenisPrw, nmPerawatan, tarif, jenisTindakan);
        }
    }
    
    public CheckPenjabMissmatch(){
        try {
//            link = URLAPIANTRIAN();
        } catch (Exception e) {
            System.out.println("E : " + e);
        }
    }
    
    public List<PenjabMismatch> checkMismatchByNoRawatAll(String noRawat) {
        List<PenjabMismatch> mismatches = new ArrayList<>();
        
        mismatches.addAll(checkMismatchDr(noRawat));
        
        mismatches.addAll(checkMismatchDrpr(noRawat));
        
        mismatches.addAll(checkMismatchPr(noRawat));
        
        mismatches.addAll(checkMismatchDrInap(noRawat));
        
        mismatches.addAll(checkMismatchDrprInap(noRawat));
        
        mismatches.addAll(checkMismatchPrInap(noRawat));
        
        mismatches.addAll(checkMismatchLab(noRawat));
        
        mismatches.addAll(checkMismatchRad(noRawat));
        
        mismatches.addAll(checkMismatchOp(noRawat));
        
        return mismatches;
    }
     

    public List<PenjabMismatch> checkMismatchByNoRawatRalan(String noRawat) {
        List<PenjabMismatch> mismatches = new ArrayList<>();
        
        mismatches.addAll(checkMismatchDr(noRawat));
        
        mismatches.addAll(checkMismatchDrpr(noRawat));
        
        mismatches.addAll(checkMismatchPr(noRawat));
        
        mismatches.addAll(checkMismatchLab(noRawat));
        
        mismatches.addAll(checkMismatchRad(noRawat));
        
        mismatches.addAll(checkMismatchOp(noRawat));
        
        return mismatches;
    }
    
    
     public List<PenjabMismatch> checkMismatchByNoRawatRanap(String noRawat) {
        List<PenjabMismatch> mismatches = new ArrayList<>();
        
        mismatches.addAll(checkMismatchDrInap(noRawat));
        
        mismatches.addAll(checkMismatchDrprInap(noRawat));
        
        mismatches.addAll(checkMismatchPrInap(noRawat));
        
        mismatches.addAll(checkMismatchLab(noRawat));
        
        mismatches.addAll(checkMismatchRad(noRawat));
        
        mismatches.addAll(checkMismatchOp(noRawat));
        
        return mismatches;
    }
    /**
     * Mengecek mismatch untuk tindakan dokter (rawat_jl_dr)
     */
    private List<PenjabMismatch> checkMismatchDr(String noRawat) {
        List<PenjabMismatch> mismatches = new ArrayList<>();
        
        try {
            ps = koneksi.prepareStatement(sqldr);
            ps.setString(1, noRawat);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                PenjabMismatch mismatch = new PenjabMismatch(
                    rs.getString("kd_pj"),
                    rs.getString("kd_jenis_prw"),
                    rs.getString("nm_perawatan"),
                    rs.getString("no_rawat"),
                    rs.getDouble("tarif_tindakandr"),
                    "Tindakan Dokter Ralan"
                );
                mismatches.add(mismatch);
            }
            
        } catch (SQLException e) {
            System.err.println("Error checkMismatchDr: " + e.getMessage());
        } finally {
            closeResources();
        }
        
        return mismatches;
    }
    
    private List<PenjabMismatch> checkMismatchDrInap(String noRawat) {
        List<PenjabMismatch> mismatches = new ArrayList<>();
        
        try {
            ps = koneksi.prepareStatement(sqldrinap);
            ps.setString(1, noRawat);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                PenjabMismatch mismatch = new PenjabMismatch(
                    rs.getString("kd_pj"),
                    rs.getString("kd_jenis_prw"),
                    rs.getString("nm_perawatan"),
                    rs.getString("no_rawat"),
                    rs.getDouble("tarif_tindakandr"),
                    "Tindakan Dokter Ranap"
                );
                mismatches.add(mismatch);
            }
            
        } catch (SQLException e) {
            System.err.println("Error checkMismatchDrInap: " + e.getMessage());
        } finally {
            closeResources();
        }
        
        return mismatches;
    }
    
    /**
     * Mengecek mismatch untuk tindakan dokter + paramedis (rawat_jl_drpr)
     */
    private List<PenjabMismatch> checkMismatchDrpr(String noRawat) {
        List<PenjabMismatch> mismatches = new ArrayList<>();
        
        try {
            ps = koneksi.prepareStatement(sqldrpr);
            ps.setString(1, noRawat);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                PenjabMismatch mismatch = new PenjabMismatch(
                    rs.getString("kd_pj"),
                    rs.getString("kd_jenis_prw"),
                    rs.getString("nm_perawatan"),
                    rs.getString("no_rawat"),
                    rs.getDouble("tarif_tindakandr"),
                    "Tindakan Dokter & Perawat Ralan"
                );
                mismatches.add(mismatch);
            }
            
        } catch (SQLException e) {
            System.err.println("Error checkMismatchDrpr: " + e.getMessage());
        } finally {
            closeResources();
        }
        
        return mismatches;
    }
    
    private List<PenjabMismatch> checkMismatchDrprInap(String noRawat) {
        List<PenjabMismatch> mismatches = new ArrayList<>();
        
        try {
            ps = koneksi.prepareStatement(sqldrprinap);
            ps.setString(1, noRawat);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                PenjabMismatch mismatch = new PenjabMismatch(
                    rs.getString("kd_pj"),
                    rs.getString("kd_jenis_prw"),
                    rs.getString("nm_perawatan"),
                    rs.getString("no_rawat"),
                    rs.getDouble("tarif_tindakandr"),
                    "Tindakan Dokter & Perawat Ranap"
                );
                mismatches.add(mismatch);
            }
            
        } catch (SQLException e) {
            System.err.println("Error checkMismatchDrprInap: " + e.getMessage());
        } finally {
            closeResources();
        }
        
        return mismatches;
    }
    
    /**
     * Mengecek mismatch untuk tindakan paramedis (rawat_jl_pr)
     */
    private List<PenjabMismatch> checkMismatchPr(String noRawat) {
        List<PenjabMismatch> mismatches = new ArrayList<>();
        
        try {
            ps = koneksi.prepareStatement(sqlpr);
            ps.setString(1, noRawat);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                PenjabMismatch mismatch = new PenjabMismatch(
                    rs.getString("kd_pj"),
                    rs.getString("kd_jenis_prw"),
                    rs.getString("nm_perawatan"),
                    rs.getString("no_rawat"),
                    rs.getDouble("tarif_tindakanpr"),
                    "Tindakan Perawat Ralan"
                );
                mismatches.add(mismatch);
            }
            
        } catch (SQLException e) {
            System.err.println("Error checkMismatchPr: " + e.getMessage());
        } finally {
            closeResources();
        }
        
        return mismatches;
    }
    
    private List<PenjabMismatch> checkMismatchPrInap(String noRawat) {
        List<PenjabMismatch> mismatches = new ArrayList<>();
        
        try {
            ps = koneksi.prepareStatement(sqlprinap);
            ps.setString(1, noRawat);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                PenjabMismatch mismatch = new PenjabMismatch(
                    rs.getString("kd_pj"),
                    rs.getString("kd_jenis_prw"),
                    rs.getString("nm_perawatan"),
                    rs.getString("no_rawat"),
                    rs.getDouble("tarif_tindakanpr"),
                    "Tindakan Perawat Ranap"
                );
                mismatches.add(mismatch);
            }
            
        } catch (SQLException e) {
            System.err.println("Error checkMismatchPrInap: " + e.getMessage());
        } finally {
            closeResources();
        }
        
        return mismatches;
    }
    
    private List<PenjabMismatch> checkMismatchLab(String noRawat) {
        List<PenjabMismatch> mismatches = new ArrayList<>();
        
        try {
            ps = koneksi.prepareStatement(sqllab);
            ps.setString(1, noRawat);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                PenjabMismatch mismatch = new PenjabMismatch(
                    rs.getString("kd_pj"),
                    rs.getString("kd_jenis_prw"),
                    rs.getString("nm_perawatan"),
                    rs.getString("no_rawat"),
                    rs.getDouble("total_byrlab"),
                    "Tindakan Lab"
                );
                mismatches.add(mismatch);
            }
            
        } catch (SQLException e) {
            System.err.println("Error checkMismatchPr: " + e.getMessage());
        } finally {
            closeResources();
        }
        
        return mismatches;
    }
    
    
    private List<PenjabMismatch> checkMismatchRad(String noRawat) {
        List<PenjabMismatch> mismatches = new ArrayList<>();
        
        try {
            ps = koneksi.prepareStatement(sqlrad);
            ps.setString(1, noRawat);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                PenjabMismatch mismatch = new PenjabMismatch(
                    rs.getString("kd_pj"),
                    rs.getString("kd_jenis_prw"),
                    rs.getString("nm_perawatan"),
                    rs.getString("no_rawat"),
                    rs.getDouble("total_byrradiologi"),
                    "Tindakan Radiologi"
                );
                mismatches.add(mismatch);
            }
            
        } catch (SQLException e) {
            System.err.println("Error checkMismatchPr: " + e.getMessage());
        } finally {
            closeResources();
        }
        
        return mismatches;
    }
    
    private List<PenjabMismatch> checkMismatchOp(String noRawat) {
        List<PenjabMismatch> mismatches = new ArrayList<>();
        
        try {
            ps = koneksi.prepareStatement(sqlop);
            ps.setString(1, noRawat);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                PenjabMismatch mismatch = new PenjabMismatch(
                    rs.getString("kd_pj"),
                    rs.getString("kode_paket"),
                    rs.getString("nm_perawatan"),
                    rs.getString("no_rawat"),
                    rs.getDouble("biaya"),
                    "Tindakan Operasi"
                );
                mismatches.add(mismatch);
            }
            
        } catch (SQLException e) {
            System.err.println("Error checkMismatchPr: " + e.getMessage());
        } finally {
            closeResources();
        }
        
        return mismatches;
    }
    
     private void closeResources() {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        } catch (SQLException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }
     
    public void closeConnection() {
        try {
            if (koneksi != null && !koneksi.isClosed()) {
                koneksi.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
    
}
