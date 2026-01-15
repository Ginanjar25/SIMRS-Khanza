/*
  Dilarang keras menggandakan/mengcopy/menyebarkan/membajak/mendecompile 
  Software ini dalam bentuk apapun tanpa seijin pembuat software
  (Khanza.Soft Media). Bagi yang sengaja membajak softaware ini ta
  npa ijin, kami sumpahi sial 1000 turunan, miskin sampai 500 turu
  nan. Selalu mendapat kecelakaan sampai 400 turunan. Anak pertama
  nya cacat tidak punya kaki sampai 300 turunan. Susah cari jodoh
  sampai umur 50 tahun sampai 200 turunan. Ya Alloh maafkan kami 
  karena telah berdoa buruk, semua ini kami lakukan karena kami ti
  dak pernah rela karya kami dibajak tanpa ijin.
 */
package modif;

import fungsi.akses;
import simrskhanza.*;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import inventory.DlgCariKonversi;
import inventory.DlgCariObat;
import inventory.DlgCariObat2;
import inventory.DlgCariObat3;
import inventory.riwayatobat;
import java.io.FileInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import keuangan.Jurnal;

/**
 *
 * @author perpustakaan
 */
public class DlgBatalPeriksa extends javax.swing.JDialog {

    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps,psrekening, psdetailberiobat;
    private ResultSet rs,rsrekening, rsdetailberiobat;
    private boolean sukses=false;
    private double ttljmdokter=0,ttljmperawat=0,ttlkso=0,ttljasasarana=0,ttlbhp=0,ttlmenejemen=0,ttlpendapatan=0,ttljmpetugas=0,ttljmperujuk=0;
    private String aktifkanparsial="no",kode_poli="",kd_pj="",poli_ralan="No",cara_bayar_ralan="No",TANGGALMUNDUR="yes",
            Suspen_Piutang_Tindakan_Ralan="",Tindakan_Ralan="",Beban_Jasa_Medik_Dokter_Tindakan_Ralan="",Utang_Jasa_Medik_Dokter_Tindakan_Ralan="",
            Beban_Jasa_Medik_Paramedis_Tindakan_Ralan="",Utang_Jasa_Medik_Paramedis_Tindakan_Ralan="",Beban_KSO_Tindakan_Ralan="",Utang_KSO_Tindakan_Ralan="",
            Beban_Jasa_Sarana_Tindakan_Ralan="",Utang_Jasa_Sarana_Tindakan_Ralan="",HPP_BHP_Tindakan_Ralan="",Persediaan_BHP_Tindakan_Ralan="",
            Beban_Jasa_Menejemen_Tindakan_Ralan="",Utang_Jasa_Menejemen_Tindakan_Ralan="";
    private String jabatan = Sequel.cariIsi("select kd_jbtn from petugas where nip =?", akses.getkode());
    private Jurnal jur=new Jurnal();
    private riwayatobat Trackobat = new riwayatobat();
    private String diagnosa="",saran="",kesan="",Suspen_Piutang_Laborat_Ranap="",Laborat_Ranap="",Beban_Jasa_Medik_Dokter_Laborat_Ranap="",Utang_Jasa_Medik_Dokter_Laborat_Ranap="",
        Beban_Jasa_Medik_Petugas_Laborat_Ranap="",Utang_Jasa_Medik_Petugas_Laborat_Ranap="",Beban_Kso_Laborat_Ranap="",Utang_Kso_Laborat_Ranap="",
        HPP_Persediaan_Laborat_Rawat_inap="",Persediaan_BHP_Laborat_Rawat_Inap="",Beban_Jasa_Sarana_Laborat_Ranap="",Utang_Jasa_Sarana_Laborat_Ranap="",
        Beban_Jasa_Perujuk_Laborat_Ranap="",Utang_Jasa_Perujuk_Laborat_Ranap="",Beban_Jasa_Menejemen_Laborat_Ranap="",Utang_Jasa_Menejemen_Laborat_Ranap="",
        Suspen_Piutang_Laborat_Ralan="",Laborat_Ralan="",Beban_Jasa_Medik_Dokter_Laborat_Ralan="",Utang_Jasa_Medik_Dokter_Laborat_Ralan="",
        Beban_Jasa_Medik_Petugas_Laborat_Ralan="",Utang_Jasa_Medik_Petugas_Laborat_Ralan="",Beban_Kso_Laborat_Ralan="",Utang_Kso_Laborat_Ralan="",
        HPP_Persediaan_Laborat_Rawat_Jalan="",Persediaan_BHP_Laborat_Rawat_Jalan="",Beban_Jasa_Sarana_Laborat_Ralan="",Utang_Jasa_Sarana_Laborat_Ralan="",
        Beban_Jasa_Perujuk_Laborat_Ralan="",Utang_Jasa_Perujuk_Laborat_Ralan="",Beban_Jasa_Menejemen_Laborat_Ralan="",Utang_Jasa_Menejemen_Laborat_Ralan="",status="";
    
    private String kdpetugas="",kdpenjab="",Suspen_Piutang_Radiologi_Ranap="",Radiologi_Ranap="",Beban_Jasa_Medik_Dokter_Radiologi_Ranap="",Utang_Jasa_Medik_Dokter_Radiologi_Ranap="",
            Beban_Jasa_Medik_Petugas_Radiologi_Ranap="",Utang_Jasa_Medik_Petugas_Radiologi_Ranap="",Beban_Kso_Radiologi_Ranap="",Utang_Kso_Radiologi_Ranap="",
            HPP_Persediaan_Radiologi_Rawat_Inap="",Persediaan_BHP_Radiologi_Rawat_Inap="",Beban_Jasa_Sarana_Radiologi_Ranap="",Utang_Jasa_Sarana_Radiologi_Ranap="",
            Beban_Jasa_Perujuk_Radiologi_Ranap="",Utang_Jasa_Perujuk_Radiologi_Ranap="",Beban_Jasa_Menejemen_Radiologi_Ranap="",Utang_Jasa_Menejemen_Radiologi_Ranap="",
            Suspen_Piutang_Radiologi_Ralan="",Radiologi_Ralan="",Beban_Jasa_Medik_Dokter_Radiologi_Ralan="",Utang_Jasa_Medik_Dokter_Radiologi_Ralan="",
            Beban_Jasa_Medik_Petugas_Radiologi_Ralan="",Utang_Jasa_Medik_Petugas_Radiologi_Ralan="",Beban_Kso_Radiologi_Ralan="",Utang_Kso_Radiologi_Ralan="",
            HPP_Persediaan_Radiologi_Rawat_Jalan="",Persediaan_BHP_Radiologi_Rawat_Jalan="",Beban_Jasa_Sarana_Radiologi_Ralan="",Utang_Jasa_Sarana_Radiologi_Ralan="",
            Beban_Jasa_Perujuk_Radiologi_Ralan="",Utang_Jasa_Perujuk_Radiologi_Ralan="",Beban_Jasa_Menejemen_Radiologi_Ralan="",Utang_Jasa_Menejemen_Radiologi_Ralan="";
    
    private String sql="", bangsal = "", lokasi = "", aktifkanbatch = "no", kodedokter = "", namadokter = "", statusberi = "",
            Suspen_Piutang_Obat_Ranap = "", Obat_Ranap = "", HPP_Obat_Rawat_Inap = "", Persediaan_Obat_Rawat_Inap = "",
            Suspen_Piutang_Obat_Ralan = "", Obat_Ralan = "", HPP_Obat_Rawat_Jalan = "", Persediaan_Obat_Rawat_Jalan = "";
    
    private double ttljual, ttlhpp, jumlahtotaldetail = 0;
    private Properties prop = new Properties();
    

    /**
     * Creates new form DlgPemberianObat
     *
     * @param parent
     * @param modal
     */
    public DlgBatalPeriksa(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        this.setLocation(8, 1);
        setSize(885, 674);

        //TCatatan.setText(TCatatan); 
        TCatatan.setLineWrap(true);
        TCatatan.setWrapStyleWord(true);
        //set rekening tindakan dr pr ralan
        try {
            psrekening = koneksi.prepareStatement(
                    "select set_akun_ralan.Suspen_Piutang_Tindakan_Ralan,set_akun_ralan.Tindakan_Ralan,set_akun_ralan.Beban_Jasa_Medik_Dokter_Tindakan_Ralan,"
                    + "set_akun_ralan.Utang_Jasa_Medik_Dokter_Tindakan_Ralan,set_akun_ralan.Beban_Jasa_Medik_Paramedis_Tindakan_Ralan,"
                    + "set_akun_ralan.Utang_Jasa_Medik_Paramedis_Tindakan_Ralan,set_akun_ralan.Beban_KSO_Tindakan_Ralan,"
                    + "set_akun_ralan.Utang_KSO_Tindakan_Ralan,set_akun_ralan.Beban_Jasa_Sarana_Tindakan_Ralan,"
                    + "set_akun_ralan.Utang_Jasa_Sarana_Tindakan_Ralan,set_akun_ralan.Beban_Jasa_Menejemen_Tindakan_Ralan,"
                    + "set_akun_ralan.Utang_Jasa_Menejemen_Tindakan_Ralan,set_akun_ralan.HPP_BHP_Tindakan_Ralan,set_akun_ralan.Persediaan_BHP_Tindakan_Ralan from set_akun_ralan");
            try {
                rsrekening = psrekening.executeQuery();
                while (rsrekening.next()) {
                    Suspen_Piutang_Tindakan_Ralan = rsrekening.getString("Suspen_Piutang_Tindakan_Ralan");
                    Tindakan_Ralan = rsrekening.getString("Tindakan_Ralan");
                    Beban_Jasa_Medik_Dokter_Tindakan_Ralan = rsrekening.getString("Beban_Jasa_Medik_Dokter_Tindakan_Ralan");
                    Utang_Jasa_Medik_Dokter_Tindakan_Ralan = rsrekening.getString("Utang_Jasa_Medik_Dokter_Tindakan_Ralan");
                    Beban_Jasa_Medik_Paramedis_Tindakan_Ralan = rsrekening.getString("Beban_Jasa_Medik_Paramedis_Tindakan_Ralan");
                    Utang_Jasa_Medik_Paramedis_Tindakan_Ralan = rsrekening.getString("Utang_Jasa_Medik_Paramedis_Tindakan_Ralan");
                    Beban_KSO_Tindakan_Ralan = rsrekening.getString("Beban_KSO_Tindakan_Ralan");
                    Utang_KSO_Tindakan_Ralan = rsrekening.getString("Utang_KSO_Tindakan_Ralan");
                    Beban_Jasa_Sarana_Tindakan_Ralan = rsrekening.getString("Beban_Jasa_Sarana_Tindakan_Ralan");
                    Utang_Jasa_Sarana_Tindakan_Ralan = rsrekening.getString("Utang_Jasa_Sarana_Tindakan_Ralan");
                    Beban_Jasa_Menejemen_Tindakan_Ralan = rsrekening.getString("Beban_Jasa_Menejemen_Tindakan_Ralan");
                    Utang_Jasa_Menejemen_Tindakan_Ralan = rsrekening.getString("Utang_Jasa_Menejemen_Tindakan_Ralan");
                    HPP_BHP_Tindakan_Ralan = rsrekening.getString("HPP_BHP_Tindakan_Ralan");
                    Persediaan_BHP_Tindakan_Ralan = rsrekening.getString("Persediaan_BHP_Tindakan_Ralan");
                }
            } catch (Exception e) {
                System.out.println("Notif Rekening : " + e);
            } finally {
                if (rsrekening != null) {
                    rsrekening.close();
                }
                if (psrekening != null) {
                    psrekening.close();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        //set rekening lab
        try {            
            psrekening = koneksi.prepareStatement("select * from set_akun_ralan");
            try {
                rsrekening = psrekening.executeQuery();
                while (rsrekening.next()) {
                    Suspen_Piutang_Laborat_Ralan = rsrekening.getString("Suspen_Piutang_Laborat_Ralan");
                    Laborat_Ralan = rsrekening.getString("Laborat_Ralan");
                    Beban_Jasa_Medik_Dokter_Laborat_Ralan = rsrekening.getString("Beban_Jasa_Medik_Dokter_Laborat_Ralan");
                    Utang_Jasa_Medik_Dokter_Laborat_Ralan = rsrekening.getString("Utang_Jasa_Medik_Dokter_Laborat_Ralan");
                    Beban_Jasa_Medik_Petugas_Laborat_Ralan = rsrekening.getString("Beban_Jasa_Medik_Petugas_Laborat_Ralan");
                    Utang_Jasa_Medik_Petugas_Laborat_Ralan = rsrekening.getString("Utang_Jasa_Medik_Petugas_Laborat_Ralan");
                    Beban_Kso_Laborat_Ralan = rsrekening.getString("Beban_Kso_Laborat_Ralan");
                    Utang_Kso_Laborat_Ralan = rsrekening.getString("Utang_Kso_Laborat_Ralan");
                    HPP_Persediaan_Laborat_Rawat_Jalan = rsrekening.getString("HPP_Persediaan_Laborat_Rawat_Jalan");
                    Persediaan_BHP_Laborat_Rawat_Jalan = rsrekening.getString("Persediaan_BHP_Laborat_Rawat_Jalan");
                    Beban_Jasa_Sarana_Laborat_Ralan = rsrekening.getString("Beban_Jasa_Sarana_Laborat_Ralan");
                    Utang_Jasa_Sarana_Laborat_Ralan = rsrekening.getString("Utang_Jasa_Sarana_Laborat_Ralan");
                    Beban_Jasa_Perujuk_Laborat_Ralan = rsrekening.getString("Beban_Jasa_Perujuk_Laborat_Ralan");
                    Utang_Jasa_Perujuk_Laborat_Ralan = rsrekening.getString("Utang_Jasa_Perujuk_Laborat_Ralan");
                    Beban_Jasa_Menejemen_Laborat_Ralan = rsrekening.getString("Beban_Jasa_Menejemen_Laborat_Ralan");
                    Utang_Jasa_Menejemen_Laborat_Ralan = rsrekening.getString("Utang_Jasa_Menejemen_Laborat_Ralan");
                }
            } catch (Exception e) {
                System.out.println("Notif Rekening : " + e);
            } finally {
                if (rsrekening != null) {
                    rsrekening.close();
                }
                if (psrekening != null) {
                    psrekening.close();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        //set rekening rad
        try {
            psrekening = koneksi.prepareStatement(
                    "select set_akun_ralan.Suspen_Piutang_Radiologi_Ralan,set_akun_ralan.Radiologi_Ralan,set_akun_ralan.Beban_Jasa_Medik_Dokter_Radiologi_Ralan,"
                    + "set_akun_ralan.Utang_Jasa_Medik_Dokter_Radiologi_Ralan,set_akun_ralan.Beban_Jasa_Medik_Petugas_Radiologi_Ralan,set_akun_ralan.Utang_Jasa_Medik_Petugas_Radiologi_Ralan,"
                    + "set_akun_ralan.Beban_Kso_Radiologi_Ralan,set_akun_ralan.Utang_Kso_Radiologi_Ralan,set_akun_ralan.HPP_Persediaan_Radiologi_Rawat_Jalan,"
                    + "set_akun_ralan.Persediaan_BHP_Radiologi_Rawat_Jalan,set_akun_ralan.Beban_Jasa_Sarana_Radiologi_Ralan,set_akun_ralan.Utang_Jasa_Sarana_Radiologi_Ralan,"
                    + "set_akun_ralan.Beban_Jasa_Perujuk_Radiologi_Ralan,set_akun_ralan.Utang_Jasa_Perujuk_Radiologi_Ralan,set_akun_ralan.Beban_Jasa_Menejemen_Radiologi_Ralan,"
                    + "set_akun_ralan.Utang_Jasa_Menejemen_Radiologi_Ralan from set_akun_ralan");
            try {
                rsrekening = psrekening.executeQuery();
                while (rsrekening.next()) {
                    Suspen_Piutang_Radiologi_Ralan = rsrekening.getString("Suspen_Piutang_Radiologi_Ralan");
                    Radiologi_Ralan = rsrekening.getString("Radiologi_Ralan");
                    Beban_Jasa_Medik_Dokter_Radiologi_Ralan = rsrekening.getString("Beban_Jasa_Medik_Dokter_Radiologi_Ralan");
                    Utang_Jasa_Medik_Dokter_Radiologi_Ralan = rsrekening.getString("Utang_Jasa_Medik_Dokter_Radiologi_Ralan");
                    Beban_Jasa_Medik_Petugas_Radiologi_Ralan = rsrekening.getString("Beban_Jasa_Medik_Petugas_Radiologi_Ralan");
                    Utang_Jasa_Medik_Petugas_Radiologi_Ralan = rsrekening.getString("Utang_Jasa_Medik_Petugas_Radiologi_Ralan");
                    Beban_Kso_Radiologi_Ralan = rsrekening.getString("Beban_Kso_Radiologi_Ralan");
                    Utang_Kso_Radiologi_Ralan = rsrekening.getString("Utang_Kso_Radiologi_Ralan");
                    HPP_Persediaan_Radiologi_Rawat_Jalan = rsrekening.getString("HPP_Persediaan_Radiologi_Rawat_Jalan");
                    Persediaan_BHP_Radiologi_Rawat_Jalan = rsrekening.getString("Persediaan_BHP_Radiologi_Rawat_Jalan");
                    Beban_Jasa_Sarana_Radiologi_Ralan = rsrekening.getString("Beban_Jasa_Sarana_Radiologi_Ralan");
                    Utang_Jasa_Sarana_Radiologi_Ralan = rsrekening.getString("Utang_Jasa_Sarana_Radiologi_Ralan");
                    Beban_Jasa_Perujuk_Radiologi_Ralan = rsrekening.getString("Beban_Jasa_Perujuk_Radiologi_Ralan");
                    Utang_Jasa_Perujuk_Radiologi_Ralan = rsrekening.getString("Utang_Jasa_Perujuk_Radiologi_Ralan");
                    Beban_Jasa_Menejemen_Radiologi_Ralan = rsrekening.getString("Beban_Jasa_Menejemen_Radiologi_Ralan");
                    Utang_Jasa_Menejemen_Radiologi_Ralan = rsrekening.getString("Utang_Jasa_Menejemen_Radiologi_Ralan");
                }
            } catch (Exception e) {
                System.out.println("Notif Rekening : " + e);
            } finally {
                if (rsrekening != null) {
                    rsrekening.close();
                }
                if (psrekening != null) {
                    psrekening.close();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        
        //set rek Obat
                try {
            psrekening = koneksi.prepareStatement(
                    "select set_akun_ralan.Suspen_Piutang_Obat_Ralan,set_akun_ralan.Obat_Ralan,"
                    + "set_akun_ralan.HPP_Obat_Rawat_Jalan,set_akun_ralan.Persediaan_Obat_Rawat_Jalan from set_akun_ralan");
            try {
                rsrekening = psrekening.executeQuery();
                while (rsrekening.next()) {
                    Suspen_Piutang_Obat_Ralan = rsrekening.getString("Suspen_Piutang_Obat_Ralan");
                    Obat_Ralan = rsrekening.getString("Obat_Ralan");
                    HPP_Obat_Rawat_Jalan = rsrekening.getString("HPP_Obat_Rawat_Jalan");
                    Persediaan_Obat_Rawat_Jalan = rsrekening.getString("Persediaan_Obat_Rawat_Jalan");
                }
            } catch (Exception e) {
                System.out.println("Notif Rekening : " + e);
            } finally {
                if (rsrekening != null) {
                    rsrekening.close();
                }
                if (psrekening != null) {
                    psrekening.close();
                }
            }

            psrekening = koneksi.prepareStatement(
                    "select set_akun_ranap.Suspen_Piutang_Obat_Ranap,set_akun_ranap.Obat_Ranap,"
                    + "set_akun_ranap.HPP_Obat_Rawat_Inap,set_akun_ranap.Persediaan_Obat_Rawat_Inap from set_akun_ranap");
            try {
                rsrekening = psrekening.executeQuery();
                while (rsrekening.next()) {
                    Suspen_Piutang_Obat_Ranap = rsrekening.getString("Suspen_Piutang_Obat_Ranap");
                    Obat_Ranap = rsrekening.getString("Obat_Ranap");
                    HPP_Obat_Rawat_Inap = rsrekening.getString("HPP_Obat_Rawat_Inap");
                    Persediaan_Obat_Rawat_Inap = rsrekening.getString("Persediaan_Obat_Rawat_Inap");
                }
            } catch (Exception e) {
                System.out.println("Notif Rekening : " + e);
            } finally {
                if (rsrekening != null) {
                    rsrekening.close();
                }
                if (psrekening != null) {
                    psrekening.close();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            aktifkanparsial = prop.getProperty("AKTIFKANBILLINGPARSIAL");
            aktifkanbatch = prop.getProperty("AKTIFKANBATCHOBAT");
        } catch (Exception ex) {
            aktifkanparsial = "no";
            aktifkanbatch = "no";
        }
    }

    //private DlgCariObatPenyakit dlgobtpny=new DlgCariObatPenyakit(null,false);
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        internalFrame1 = new widget.InternalFrame();
        FormInput = new widget.PanelBiasa();
        jLabel3 = new widget.Label();
        TNoRM = new widget.TextBox();
        TPasien = new widget.TextBox();
        jLabel9 = new widget.Label();
        Scroll3 = new widget.ScrollPane();
        TCatatan = new widget.TextArea();
        TNoRw = new widget.TextBox();
        jLabel4 = new widget.Label();
        TKdPetugas = new widget.TextBox();
        TNmPetugas = new widget.TextBox();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnEdit = new widget.Button();
        BtnKeluar = new widget.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Batal Periksa ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(865, 105));
        FormInput.setLayout(null);

        jLabel3.setText("Pasien :");
        jLabel3.setName("jLabel3"); // NOI18N
        FormInput.add(jLabel3);
        jLabel3.setBounds(0, 10, 65, 23);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM"); // NOI18N
        FormInput.add(TNoRM);
        TNoRM.setBounds(70, 10, 90, 23);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N
        TPasien.setPreferredSize(new java.awt.Dimension(25, 28));
        FormInput.add(TPasien);
        TPasien.setBounds(320, 10, 360, 23);

        jLabel9.setText("Alasan :");
        jLabel9.setName("jLabel9"); // NOI18N
        FormInput.add(jLabel9);
        jLabel9.setBounds(0, 80, 60, 23);

        Scroll3.setName("Scroll3"); // NOI18N
        Scroll3.setOpaque(true);

        TCatatan.setColumns(20);
        TCatatan.setRows(5);
        TCatatan.setName("TCatatan"); // NOI18N
        Scroll3.setViewportView(TCatatan);

        FormInput.add(Scroll3);
        Scroll3.setBounds(70, 80, 620, 50);

        TNoRw.setEditable(false);
        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw"); // NOI18N
        FormInput.add(TNoRw);
        TNoRw.setBounds(160, 10, 160, 23);

        jLabel4.setText("Petugas :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(0, 40, 65, 23);

        TKdPetugas.setEditable(false);
        TKdPetugas.setHighlighter(null);
        TKdPetugas.setName("TKdPetugas"); // NOI18N
        FormInput.add(TKdPetugas);
        TKdPetugas.setBounds(70, 40, 90, 23);

        TNmPetugas.setEditable(false);
        TNmPetugas.setHighlighter(null);
        TNmPetugas.setName("TNmPetugas"); // NOI18N
        FormInput.add(TNmPetugas);
        TNmPetugas.setBounds(160, 40, 520, 23);

        internalFrame1.add(FormInput, java.awt.BorderLayout.CENTER);
        FormInput.getAccessibleContext().setAccessibleName("");
        FormInput.getAccessibleContext().setAccessibleDescription("");

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(100, 56));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Simpan");
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setName("BtnSimpan"); // NOI18N
        BtnSimpan.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanActionPerformed(evt);
            }
        });
        BtnSimpan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnSimpanKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnSimpan);

        BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png"))); // NOI18N
        BtnEdit.setMnemonic('G');
        BtnEdit.setText("Ganti");
        BtnEdit.setToolTipText("Alt+G");
        BtnEdit.setName("BtnEdit"); // NOI18N
        BtnEdit.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnEditActionPerformed(evt);
            }
        });
        BtnEdit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnEditKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnEdit);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/cross.png"))); // NOI18N
        BtnKeluar.setMnemonic('T');
        BtnKeluar.setText("Tutup");
        BtnKeluar.setToolTipText("Alt+T");
        BtnKeluar.setName("BtnKeluar"); // NOI18N
        BtnKeluar.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluarActionPerformed(evt);
            }
        });
        BtnKeluar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnKeluarKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnKeluar);

        internalFrame1.add(panelGlass8, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (TNoRM.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRM, "No.Rekam Medis");
        } else if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No Rawat");
        } else if (TCatatan.getText().trim().equals("")) {
            Valid.textKosong(TCatatan, "Alasan");
        } else if (TCatatan.getText().trim().length() < 5) {
            JOptionPane.showMessageDialog(null, "Maaf, Alasan harus diisi minimal 5 karakter");
            TCatatan.requestFocus();
        } else {
            if (hapusTindakanRajalDr() && hapusTindakanRajalPr() && hapusPemeriksaanLab()&& hapusPemeriksaanRad() && hapusPemberianObat()) {
                if (Sequel.menyimpantf("reg_batal", "?,?,?,NOW() ", "reg_batal", 3, new String[]{
                    TNoRw.getText(), TCatatan.getText(), TKdPetugas.getText()
                }) == true) {
                    Sequel.mengedit("reg_periksa", "no_rawat=?", "stts='Batal',biaya_reg='0'", 1, new String[]{TNoRw.getText()});
                    if (!Sequel.cariIsi("select kd_poli from reg_periksa where no_rawat = ?", TNoRw.getText()).equals("IGDK")) {
                        if (!Sequel.cariIsi("SELECT rmb.nobooking FROM referensi_mobilejkn_bpjs rmb WHERE rmb.no_rawat =?", TNoRw.getText()).isBlank()) {
                            if (Sequel.mengedittf("referensi_mobilejkn_bpjs", "nobooking=?", "status='Batal',validasi=now()", 1, new String[]{
                                Sequel.cariIsi("SELECT rmb.nobooking FROM referensi_mobilejkn_bpjs rmb WHERE rmb.no_rawat =?", TNoRw.getText())
                            }) == true) {
                                Sequel.menyimpan2("referensi_mobilejkn_bpjs_batal", "?,?,?,now(),?,?,?", 6, new String[]{
                                    TNoRM.getText(),
                                    TNoRw.getText(),
                                    Sequel.cariIsi("SELECT rmb.nomorreferensi FROM referensi_mobilejkn_bpjs rmb WHERE rmb.no_rawat =?", TNoRw.getText()),
                                    "Dibatalkan Oleh " + akses.getkode() + "",
                                    "Belum",
                                    Sequel.cariIsi("SELECT rmb.nobooking FROM referensi_mobilejkn_bpjs rmb WHERE rmb.no_rawat =?", TNoRw.getText())
                                });
                            }
                        }
                    }

                }
            }else{
                JOptionPane.showMessageDialog(null, "Maaf, Gagal Hapus item pemeriksaan");
            }
            BtnKeluarActionPerformed(evt);
        }
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnSimpanActionPerformed(null);
        } else {
            Valid.pindah(evt, TCatatan, BtnKeluar);
        }
}//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
}//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            dispose();
        }
}//GEN-LAST:event_BtnKeluarKeyPressed

private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed
    if (TNoRM.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
        Valid.textKosong(TNoRM, "No.Rekam Medis");
    } else if (TCatatan.getText().trim().equals("")) {
        Valid.textKosong(TCatatan, "Catatan");
    } else if (TCatatan.getText().trim().length() < 5) {
        JOptionPane.showMessageDialog(null, "Maaf, Alasan harus diisi minimal 5 karakter");
        TCatatan.requestFocus();
    } else {
        Sequel.mengedit2("reg_batal", "no_rawat=?", "alasan=?", 2, new String[]{
            TCatatan.getText(), TNoRw.getText()
        });
        BtnKeluarActionPerformed(evt);
    }
}//GEN-LAST:event_BtnEditActionPerformed

private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEditKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
        BtnEditActionPerformed(null);
    }
}//GEN-LAST:event_BtnEditKeyPressed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        TCatatan.requestFocus();
    }//GEN-LAST:event_formWindowActivated

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgBatalPeriksa dialog = new DlgBatalPeriksa(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.Button BtnEdit;
    private widget.Button BtnKeluar;
    private widget.Button BtnSimpan;
    private widget.PanelBiasa FormInput;
    private widget.ScrollPane Scroll3;
    private widget.TextArea TCatatan;
    private widget.TextBox TKdPetugas;
    private widget.TextBox TNmPetugas;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel3;
    private widget.Label jLabel4;
    private widget.Label jLabel9;
    private widget.panelisi panelGlass8;
    // End of variables declaration//GEN-END:variables

    private void isPsien() {
        Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis=? ", TPasien, TNoRM.getText());
    }

    public void setNoRm(String norm, String no_rawat) {
        TNoRM.setText(norm);
        TNoRw.setText(no_rawat);
        TKdPetugas.setText(akses.getkode());
        TNmPetugas.setText(Sequel.cariIsi("select pegawai.nama from pegawai where pegawai.nik='" + TKdPetugas.getText() + "'"));
        isPsien();
        Sequel.cariIsi("select reg_batal.alasan from reg_batal where reg_batal.no_rawat=?", TCatatan, TNoRw.getText());
    }

    public void isCek() {
        BtnSimpan.setEnabled(true);
        BtnEdit.setEnabled(true);
    }

    private boolean hapusTindakanRajalDr() {
        Sequel.AutoComitFalse();
        sukses = true;
        ttljmdokter = 0;
        ttljmperawat = 0;
        ttlkso = 0;
        ttlpendapatan = 0;
        ttljasasarana = 0;
        ttlbhp = 0;
        ttlmenejemen = 0;

        try {
            ps = koneksi.prepareStatement("SELECT * FROM rawat_jl_dr rjdr WHERE rjdr.no_rawat = ?");
            try {
                ps.setString(1, TNoRw.getText());
                rs = ps.executeQuery();
                while (rs.next()) {
                    System.out.println("coba hapus "+rs.getString("kd_jenis_prw"));
                    
                    if (akses.getkode().equals("Admin Utama") || jabatan.equals("J005")) {
                        if (Sequel.cariRegistrasi(rs.getString("no_rawat")) > 0) {
                            JOptionPane.showMessageDialog(rootPane, "Data billing sudah terverifikasi, data tidak boleh dihapus.\nSilahkan hubungi bagian kasir/keuangan ..!!");
                        } else {
                            if (Sequel.queryutf("delete from rawat_jl_dr where no_rawat='" + rs.getString("no_rawat")
                                    + "' and kd_jenis_prw='" + rs.getString("kd_jenis_prw")
                                    + "' and kd_dokter='" + rs.getString("kd_dokter")
                                    + "' and tgl_perawatan='" + rs.getString("tgl_perawatan")
                                    + "' and jam_rawat='" + rs.getString("jam_rawat") + "'") == true) {
                                ttljmdokter = ttljmdokter + rs.getDouble("tarif_tindakandr");
                                ttlkso = ttlkso + rs.getDouble("kso");
                                ttlpendapatan = ttlpendapatan + rs.getDouble("biaya_rawat");
                                ttljasasarana = ttljasasarana + rs.getDouble("material");
                                ttlbhp = ttlbhp + rs.getDouble("bhp");
                                ttlmenejemen = ttlmenejemen + rs.getDouble("menejemen");
                            } else {
                                sukses = false;
                            }
                        }
                    } else {
                        if (Sequel.cekTanggal48jam(rs.getString("tgl_perawatan") + " " + rs.getString("jam_rawat"), Sequel.ambiltanggalsekarang()) == true) {
                            if (Sequel.cariRegistrasi(rs.getString("no_rawat")) > 0) {
                                JOptionPane.showMessageDialog(rootPane, "Data billing sudah terverifikasi, data tidak boleh dihapus.\nSilahkan hubungi bagian kasir/keuangan ..!!");
                            } else {
                                if (Sequel.queryutf("delete from rawat_jl_dr where no_rawat='" + rs.getString("no_rawat")
                                        + "' and kd_jenis_prw='" + rs.getString("kd_jenis_prw")
                                        + "' and kd_dokter='" + rs.getString("kd_dokter")
                                        + "' and tgl_perawatan='" + rs.getString("tgl_perawatan")
                                        + "' and jam_rawat='" + rs.getString("jam_rawat") + "'") == true) {
                                    ttljmdokter = ttljmdokter + rs.getDouble("tarif_tindakandr");
                                    ttlkso = ttlkso + rs.getDouble("kso");
                                    ttlpendapatan = ttlpendapatan + rs.getDouble("biaya_rawat");
                                    ttljasasarana = ttljasasarana + rs.getDouble("material");
                                    ttlbhp = ttlbhp + rs.getDouble("bhp");
                                    ttlmenejemen = ttlmenejemen + rs.getDouble("menejemen");
                                } else {
                                    sukses = false;
                                }
                            }
                        }
                    }
                }
                
                if (sukses == true) {
                    Sequel.queryu("delete from tampjurnal");
                    if (ttlpendapatan > 0) {
                        Sequel.menyimpan("tampjurnal", "'" + Suspen_Piutang_Tindakan_Ralan + "','Suspen Piutang Tindakan Ralan','0','" + ttlpendapatan + "'", "kredit=kredit+'" + (ttlpendapatan) + "'", "kd_rek='" + Suspen_Piutang_Tindakan_Ralan + "'");
                        Sequel.menyimpan("tampjurnal", "'" + Tindakan_Ralan + "','Pendapatan Tindakan Rawat Jalan','" + ttlpendapatan + "','0'", "debet=debet+'" + (ttlpendapatan) + "'", "kd_rek='" + Tindakan_Ralan + "'");
                    }
                    if (ttljmdokter > 0) {
                        Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Medik_Dokter_Tindakan_Ralan + "','Beban Jasa Medik Dokter Tindakan Ralan','0','" + ttljmdokter + "'", "kredit=kredit+'" + (ttljmdokter) + "'", "kd_rek='" + Beban_Jasa_Medik_Dokter_Tindakan_Ralan + "'");
                        Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Medik_Dokter_Tindakan_Ralan + "','Utang Jasa Medik Dokter Tindakan Ralan','" + ttljmdokter + "','0'", "debet=debet+'" + (ttljmdokter) + "'", "kd_rek='" + Utang_Jasa_Medik_Dokter_Tindakan_Ralan + "'");
                    }
                    if (ttlkso > 0) {
                        Sequel.menyimpan("tampjurnal", "'" + Beban_KSO_Tindakan_Ralan + "','Beban KSO Tindakan Ralan','0','" + ttlkso + "'", "kredit=kredit+'" + (ttlkso) + "'", "kd_rek='" + Beban_KSO_Tindakan_Ralan + "'");
                        Sequel.menyimpan("tampjurnal", "'" + Utang_KSO_Tindakan_Ralan + "','Utang KSO Tindakan Ralan','" + ttlkso + "','0'", "debet=debet+'" + (ttlkso) + "'", "kd_rek='" + Utang_KSO_Tindakan_Ralan + "'");
                    }
                    if (ttlmenejemen > 0) {
                        Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Menejemen_Tindakan_Ralan + "','Beban Jasa Menejemen Tindakan Ralan','0','" + ttlmenejemen + "'", "kredit=kredit+'" + (ttlmenejemen) + "'", "kd_rek='" + Beban_Jasa_Menejemen_Tindakan_Ralan + "'");
                        Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Menejemen_Tindakan_Ralan + "','Utang Jasa Menejemen Tindakan Ralan','" + ttlmenejemen + "','0'", "debet=debet+'" + (ttlmenejemen) + "'", "kd_rek='" + Utang_Jasa_Menejemen_Tindakan_Ralan + "'");
                    }
                    if (ttljasasarana > 0) {
                        Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Sarana_Tindakan_Ralan + "','Beban Jasa Sarana Tindakan Ralan','0','" + ttljasasarana + "'", "kredit=kredit+'" + (ttljasasarana) + "'", "kd_rek='" + Beban_Jasa_Sarana_Tindakan_Ralan + "'");
                        Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Sarana_Tindakan_Ralan + "','Utang Jasa Sarana Tindakan Ralan','" + ttljasasarana + "','0'", "debet=debet+'" + (ttljasasarana) + "'", "kd_rek='" + Utang_Jasa_Sarana_Tindakan_Ralan + "'");
                    }
                    if (ttlbhp > 0) {
                        Sequel.menyimpan("tampjurnal", "'" + HPP_BHP_Tindakan_Ralan + "','HPP BHP Tindakan Ralan','0','" + ttlbhp + "'", "kredit=kredit+'" + (ttlbhp) + "'", "kd_rek='" + HPP_BHP_Tindakan_Ralan + "'");
                        Sequel.menyimpan("tampjurnal", "'" + Persediaan_BHP_Tindakan_Ralan + "','Persediaan BHP Tindakan Ralan','" + ttlbhp + "','0'", "debet=debet+'" + (ttlbhp) + "'", "kd_rek='" + Persediaan_BHP_Tindakan_Ralan + "'");
                    }
                    sukses = jur.simpanJurnal(TNoRw.getText(), "U", "PEMBATALAN TINDAKAN RAWAT JALAN PASIEN " + TNoRM.getText() + " " + TPasien.getText() + " OLEH " + akses.getkode());
                }

                if (sukses == true) {
                    Sequel.Commit();
                } else {
                    sukses = false;
                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat pemrosesan data, transaksi dibatalkan.\nPeriksa kembali data sebelum melanjutkan menyimpan..!!");
                    Sequel.RollBack();
                    return sukses;
                }
                Sequel.AutoComitTrue();
                
            } catch (Exception e) {
                System.out.println("Notifikasi : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }       
        return sukses;
    }
    
    private boolean hapusTindakanRajalPr() {
        Sequel.AutoComitFalse();
        sukses = true;
        ttljmdokter = 0;
        ttljmperawat = 0;
        ttlkso = 0;
        ttlpendapatan = 0;
        ttljasasarana = 0;
        ttlbhp = 0;
        ttlmenejemen = 0;

        try {
            ps = koneksi.prepareStatement("SELECT * FROM rawat_jl_pr rjdr WHERE rjdr.no_rawat = ?");
            try {
                ps.setString(1, TNoRw.getText());
                rs = ps.executeQuery();
                while (rs.next()) {
                    System.out.println("coba hapus "+rs.getString("kd_jenis_prw"));
                    
                    if (akses.getkode().equals("Admin Utama") || jabatan.equals("J005")) {
                        if (Sequel.cariRegistrasi(rs.getString("no_rawat")) > 0) {
                            JOptionPane.showMessageDialog(rootPane, "Data billing sudah terverifikasi, data tidak boleh dihapus.\nSilahkan hubungi bagian kasir/keuangan ..!!");
                        } else {
                            if (Sequel.queryutf("delete from rawat_jl_pr where no_rawat='" + rs.getString("no_rawat")
                                    + "' and kd_jenis_prw='" + rs.getString("kd_jenis_prw")
                                    + "' and nip='" + rs.getString("nip")
                                    + "' and tgl_perawatan='" + rs.getString("tgl_perawatan")
                                    + "' and jam_rawat='" + rs.getString("jam_rawat") + "'") == true) {
                                ttljmdokter = ttljmdokter + rs.getDouble("tarif_tindakanpr");
                                ttlkso = ttlkso + rs.getDouble("kso");
                                ttlpendapatan = ttlpendapatan + rs.getDouble("biaya_rawat");
                                ttljasasarana = ttljasasarana + rs.getDouble("material");
                                ttlbhp = ttlbhp + rs.getDouble("bhp");
                                ttlmenejemen = ttlmenejemen + rs.getDouble("menejemen");
                            } else {
                                sukses = false;
                            }
                        }
                    } else {
                        if (Sequel.cekTanggal48jam(rs.getString("tgl_perawatan") + " " + rs.getString("jam_rawat"), Sequel.ambiltanggalsekarang()) == true) {
                            if (Sequel.cariRegistrasi(rs.getString("no_rawat")) > 0) {
                                JOptionPane.showMessageDialog(rootPane, "Data billing sudah terverifikasi, data tidak boleh dihapus.\nSilahkan hubungi bagian kasir/keuangan ..!!");
                            } else {
                                if (Sequel.queryutf("delete from rawat_jl_pr where no_rawat='" + rs.getString("no_rawat")
                                        + "' and kd_jenis_prw='" + rs.getString("kd_jenis_prw")
                                        + "' and nip='" + rs.getString("nip")
                                        + "' and tgl_perawatan='" + rs.getString("tgl_perawatan")
                                        + "' and jam_rawat='" + rs.getString("jam_rawat") + "'") == true) {
                                    ttljmdokter = ttljmdokter + rs.getDouble("tarif_tindakanpr");
                                    ttlkso = ttlkso + rs.getDouble("kso");
                                    ttlpendapatan = ttlpendapatan + rs.getDouble("biaya_rawat");
                                    ttljasasarana = ttljasasarana + rs.getDouble("material");
                                    ttlbhp = ttlbhp + rs.getDouble("bhp");
                                    ttlmenejemen = ttlmenejemen + rs.getDouble("menejemen");
                                } else {
                                    sukses = false;
                                }
                            }
                        }
                    }
                }
                
                if (sukses == true) {
                    Sequel.queryu("delete from tampjurnal");
                    if (ttlpendapatan > 0) {
                        Sequel.menyimpan("tampjurnal", "'" + Suspen_Piutang_Tindakan_Ralan + "','Suspen Piutang Tindakan Ralan','0','" + ttlpendapatan + "'", "kredit=kredit+'" + (ttlpendapatan) + "'", "kd_rek='" + Suspen_Piutang_Tindakan_Ralan + "'");
                        Sequel.menyimpan("tampjurnal", "'" + Tindakan_Ralan + "','Pendapatan Tindakan Rawat Jalan','" + ttlpendapatan + "','0'", "debet=debet+'" + (ttlpendapatan) + "'", "kd_rek='" + Tindakan_Ralan + "'");
                    }
                    if (ttljmperawat > 0) {
                        Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Medik_Paramedis_Tindakan_Ralan + "','Beban Jasa Medik Paramedis Tindakan Ralan','0','" + ttljmperawat + "'", "kredit=kredit+'" + (ttljmperawat) + "'", "kd_rek='" + Beban_Jasa_Medik_Paramedis_Tindakan_Ralan + "'");
                        Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Medik_Paramedis_Tindakan_Ralan + "','Utang Jasa Medik Paramedis Tindakan Ralan','" + ttljmperawat + "','0'", "debet=debet+'" + (ttljmperawat) + "'", "kd_rek='" + Utang_Jasa_Medik_Paramedis_Tindakan_Ralan + "'");
                    }
                    if (ttlkso > 0) {
                        Sequel.menyimpan("tampjurnal", "'" + Beban_KSO_Tindakan_Ralan + "','Beban KSO Tindakan Ralan','0','" + ttlkso + "'", "kredit=kredit+'" + (ttlkso) + "'", "kd_rek='" + Beban_KSO_Tindakan_Ralan + "'");
                        Sequel.menyimpan("tampjurnal", "'" + Utang_KSO_Tindakan_Ralan + "','Utang KSO Tindakan Ralan','" + ttlkso + "','0'", "debet=debet+'" + (ttlkso) + "'", "kd_rek='" + Utang_KSO_Tindakan_Ralan + "'");
                    }
                    if (ttlmenejemen > 0) {
                        Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Menejemen_Tindakan_Ralan + "','Beban Jasa Menejemen Tindakan Ralan','0','" + ttlmenejemen + "'", "kredit=kredit+'" + (ttlmenejemen) + "'", "kd_rek='" + Beban_Jasa_Menejemen_Tindakan_Ralan + "'");
                        Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Menejemen_Tindakan_Ralan + "','Utang Jasa Menejemen Tindakan Ralan','" + ttlmenejemen + "','0'", "debet=debet+'" + (ttlmenejemen) + "'", "kd_rek='" + Utang_Jasa_Menejemen_Tindakan_Ralan + "'");
                    }
                    if (ttljasasarana > 0) {
                        Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Sarana_Tindakan_Ralan + "','Beban Jasa Sarana Tindakan Ralan','0','" + ttljasasarana + "'", "kredit=kredit+'" + (ttljasasarana) + "'", "kd_rek='" + Beban_Jasa_Sarana_Tindakan_Ralan + "'");
                        Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Sarana_Tindakan_Ralan + "','Utang Jasa Sarana Tindakan Ralan','" + ttljasasarana + "','0'", "debet=debet+'" + (ttljasasarana) + "'", "kd_rek='" + Utang_Jasa_Sarana_Tindakan_Ralan + "'");
                    }
                    if (ttlbhp > 0) {
                        Sequel.menyimpan("tampjurnal", "'" + HPP_BHP_Tindakan_Ralan + "','HPP BHP Tindakan Ralan','0','" + ttlbhp + "'", "kredit=kredit+'" + (ttlbhp) + "'", "kd_rek='" + HPP_BHP_Tindakan_Ralan + "'");
                        Sequel.menyimpan("tampjurnal", "'" + Persediaan_BHP_Tindakan_Ralan + "','Persediaan BHP Tindakan Ralan','" + ttlbhp + "','0'", "debet=debet+'" + (ttlbhp) + "'", "kd_rek='" + Persediaan_BHP_Tindakan_Ralan + "'");
                    }
                    sukses = jur.simpanJurnal(TNoRw.getText(), "U", "PEMBATALAN TINDAKAN RAWAT JALAN PASIEN " + TNoRM.getText() + " " + TPasien.getText() + " OLEH " + akses.getkode());
                }

                if (sukses == true) {
                    Sequel.Commit();
                } else {
                    sukses = false;
                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat pemrosesan data, transaksi dibatalkan.\nPeriksa kembali data sebelum melanjutkan menyimpan..!!");
                    Sequel.RollBack();
                    return sukses;
                }
                Sequel.AutoComitTrue();
                
            } catch (Exception e) {
                System.out.println("Notifikasi : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }       
        return sukses;
    }
    
    private boolean hapusPemeriksaanLab() {
        try {
            ps = koneksi.prepareStatement("SELECT * FROM periksa_lab pl WHERE pl.no_rawat = ? AND pl.status = 'Ralan' ");
            try {
                ps.setString(1, TNoRw.getText());
                rs = ps.executeQuery();
                while (rs.next()) {
                    if (Sequel.cariRegistrasi(rs.getString("no_rawat")) > 0) {
                        JOptionPane.showMessageDialog(rootPane, "Data billing sudah terverifikasi, data tidak boleh dihapus.\nSilahkan hubungi bagian kasir/keuangan ..!!");
                    } else {
                        try {
                            Sequel.AutoComitFalse();
                            sukses = true;
                            String status = "";
                            ttljmdokter = 0;
                            ttljmpetugas = 0;
                            ttlkso = 0;
                            ttlpendapatan = 0;
                            ttlbhp = 0;
                            ttljasasarana = 0;
                            ttljmperujuk = 0;
                            ttlmenejemen = 0;
                            ttljmdokter = Sequel.cariIsiAngka("select sum(tarif_tindakan_dokter) from periksa_lab where periksa_lab.kategori='PK' and no_rawat='" + rs.getString("no_rawat")
                                    + "' and tgl_periksa='" + rs.getString("tgl_periksa")
                                    + "' and jam='" + rs.getString("jam") + "'");
                            ttljmpetugas = Sequel.cariIsiAngka("select sum(tarif_tindakan_petugas) from periksa_lab where periksa_lab.kategori='PK' and no_rawat='" + rs.getString("no_rawat")
                                    + "' and tgl_periksa='" + rs.getString("tgl_periksa")
                                    + "' and jam='" + rs.getString("jam") + "'");
                            ttlkso = Sequel.cariIsiAngka("select sum(kso) from periksa_lab where periksa_lab.kategori='PK' and no_rawat='" + rs.getString("no_rawat")
                                    + "' and tgl_periksa='" + rs.getString("tgl_periksa")
                                    + "' and jam='" + rs.getString("jam") + "'");
                            ttlbhp = Sequel.cariIsiAngka("select sum(bhp) from periksa_lab where periksa_lab.kategori='PK' and no_rawat='" + rs.getString("no_rawat")
                                    + "' and tgl_periksa='" + rs.getString("tgl_periksa")
                                    + "' and jam='" + rs.getString("jam") + "'");
                            ttlpendapatan = Sequel.cariIsiAngka("select sum(biaya) from periksa_lab where periksa_lab.kategori='PK' and no_rawat='" + rs.getString("no_rawat")
                                    + "' and tgl_periksa='" + rs.getString("tgl_periksa")
                                    + "' and jam='" + rs.getString("jam") + "'");
                            ttljasasarana = Sequel.cariIsiAngka("select sum(bagian_rs) from periksa_lab where periksa_lab.kategori='PK' and no_rawat='" + rs.getString("no_rawat")
                                    + "' and tgl_periksa='" + rs.getString("tgl_periksa")
                                    + "' and jam='" + rs.getString("jam") + "'");
                            ttljmperujuk = Sequel.cariIsiAngka("select sum(tarif_perujuk) from periksa_lab where periksa_lab.kategori='PK' and no_rawat='" + rs.getString("no_rawat")
                                    + "' and tgl_periksa='" + rs.getString("tgl_periksa")
                                    + "' and jam='" + rs.getString("jam") + "'");
                            ttlmenejemen = Sequel.cariIsiAngka("select sum(menejemen) from periksa_lab where periksa_lab.kategori='PK' and no_rawat='" + rs.getString("no_rawat")
                                    + "' and tgl_periksa='" + rs.getString("tgl_periksa")
                                    + "' and jam='" + rs.getString("jam") + "'");

                            ttljmdokter = ttljmdokter + Sequel.cariIsiAngka("select sum(bagian_dokter) from detail_periksa_lab where no_rawat='" + rs.getString("no_rawat")
                                    + "' and tgl_periksa='" + rs.getString("tgl_periksa")
                                    + "' and jam='" + rs.getString("jam") + "'");
                            ttljmpetugas = ttljmpetugas + Sequel.cariIsiAngka("select sum(bagian_laborat) from detail_periksa_lab where no_rawat='" + rs.getString("no_rawat")
                                    + "' and tgl_periksa='" + rs.getString("tgl_periksa")
                                    + "' and jam='" + rs.getString("jam") + "'");
                            ttlkso = ttlkso + Sequel.cariIsiAngka("select sum(kso) from detail_periksa_lab where no_rawat='" + rs.getString("no_rawat")
                                    + "' and tgl_periksa='" + rs.getString("tgl_periksa")
                                    + "' and jam='" + rs.getString("jam") + "'");
                            ttlbhp = ttlbhp + Sequel.cariIsiAngka("select sum(bhp) from detail_periksa_lab where no_rawat='" + rs.getString("no_rawat")
                                    + "' and tgl_periksa='" + rs.getString("tgl_periksa")
                                    + "' and jam='" + rs.getString("jam") + "'");
                            ttlpendapatan = ttlpendapatan + Sequel.cariIsiAngka("select sum(biaya_item) from detail_periksa_lab where no_rawat='" + rs.getString("no_rawat")
                                    + "' and tgl_periksa='" + rs.getString("tgl_periksa")
                                    + "' and jam='" + rs.getString("jam") + "'");
                            ttljasasarana = ttljasasarana + Sequel.cariIsiAngka("select sum(bagian_rs) from detail_periksa_lab where no_rawat='" + rs.getString("no_rawat")
                                    + "' and tgl_periksa='" + rs.getString("tgl_periksa")
                                    + "' and jam='" + rs.getString("jam") + "'");
                            ttljmperujuk = ttljmperujuk + Sequel.cariIsiAngka("select sum(bagian_perujuk) from detail_periksa_lab where no_rawat='" + rs.getString("no_rawat")
                                    + "' and tgl_periksa='" + rs.getString("tgl_periksa")
                                    + "' and jam='" + rs.getString("jam") + "'");
                            ttlmenejemen = ttlmenejemen + Sequel.cariIsiAngka("select sum(menejemen) from detail_periksa_lab where no_rawat='" + rs.getString("no_rawat")
                                    + "' and tgl_periksa='" + rs.getString("tgl_periksa")
                                    + "' and jam='" + rs.getString("jam") + "'");

                            status = Sequel.cariIsi("select status from periksa_lab where periksa_lab.kategori='PK' and no_rawat='" + rs.getString("no_rawat")
                                    + "' and tgl_periksa='" + rs.getString("tgl_periksa")
                                    + "' and jam='" + rs.getString("jam") + "'");

                            if (Sequel.queryutf("delete from periksa_lab where periksa_lab.kategori='PK' and no_rawat='" + rs.getString("no_rawat")
                                    + "' and tgl_periksa='" + rs.getString("tgl_periksa")
                                    + "' and jam='" + rs.getString("jam") + "'") == true) {
                                if (Sequel.queryutf("delete from detail_periksa_lab where no_rawat='" + rs.getString("no_rawat")
                                        + "' and tgl_periksa='" + rs.getString("tgl_periksa")
                                        + "' and jam='" + rs.getString("jam") + "'") == false) {
                                    sukses = false;
                                }
                            } else {
                                sukses = false;
                            }

                            if (sukses == true) {
                                if (status.equals("Ralan")) {
                                    Sequel.queryu("delete from tampjurnal");
                                    if (ttlpendapatan > 0) {
                                        Sequel.menyimpan("tampjurnal", "'" + Suspen_Piutang_Laborat_Ralan + "','Suspen Piutang Laborat Ralan','0','" + ttlpendapatan + "'", "kredit=kredit+'" + (ttlpendapatan) + "'", "kd_rek='" + Suspen_Piutang_Laborat_Ralan + "'");
                                        Sequel.menyimpan("tampjurnal", "'" + Laborat_Ralan + "','Pendapatan Laborat Rawat Jalan','" + ttlpendapatan + "','0'", "debet=debet+'" + (ttlpendapatan) + "'", "kd_rek='" + Laborat_Ralan + "'");
                                    }
                                    if (ttljmdokter > 0) {
                                        Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Medik_Dokter_Laborat_Ralan + "','Beban Jasa Medik Dokter Laborat Ralan','0','" + ttljmdokter + "'", "kredit=kredit+'" + (ttljmdokter) + "'", "kd_rek='" + Beban_Jasa_Medik_Dokter_Laborat_Ralan + "'");
                                        Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Medik_Dokter_Laborat_Ralan + "','Utang Jasa Medik Dokter Laborat Ralan','" + ttljmdokter + "','0'", "debet=debet+'" + (ttljmdokter) + "'", "kd_rek='" + Utang_Jasa_Medik_Dokter_Laborat_Ralan + "'");
                                    }
                                    if (ttljmpetugas > 0) {
                                        Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Medik_Petugas_Laborat_Ralan + "','Beban Jasa Medik Petugas Laborat Ralan','0','" + ttljmpetugas + "'", "kredit=kredit+'" + (ttljmpetugas) + "'", "kd_rek='" + Beban_Jasa_Medik_Petugas_Laborat_Ralan + "'");
                                        Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Medik_Petugas_Laborat_Ralan + "','Utang Jasa Medik Petugas Laborat Ralan','" + ttljmpetugas + "','0'", "debet=debet+'" + (ttljmpetugas) + "'", "kd_rek='" + Utang_Jasa_Medik_Petugas_Laborat_Ralan + "'");
                                    }
                                    if (ttlbhp > 0) {
                                        Sequel.menyimpan("tampjurnal", "'" + HPP_Persediaan_Laborat_Rawat_Jalan + "','HPP Persediaan Laborat Rawat Jalan','0','" + ttlbhp + "'", "kredit=kredit+'" + (ttlbhp) + "'", "kd_rek='" + HPP_Persediaan_Laborat_Rawat_inap + "'");
                                        Sequel.menyimpan("tampjurnal", "'" + Persediaan_BHP_Laborat_Rawat_Jalan + "','Persediaan BHP Laborat Rawat Jalan','" + ttlbhp + "','0'", "debet=debet+'" + (ttlbhp) + "'", "kd_rek='" + Persediaan_BHP_Laborat_Rawat_Inap + "'");
                                    }
                                    if (ttlkso > 0) {
                                        Sequel.menyimpan("tampjurnal", "'" + Beban_Kso_Laborat_Ralan + "','HPP Persediaan Laborat Rawat Inap','0','" + ttlkso + "'", "kredit=kredit+'" + (ttlkso) + "'", "kd_rek='" + Beban_Kso_Laborat_Ralan + "'");
                                        Sequel.menyimpan("tampjurnal", "'" + Utang_Kso_Laborat_Ralan + "','Persediaan BHP Laborat Rawat Inap','" + ttlkso + "','0'", "debet=debet+'" + (ttlkso) + "'", "kd_rek='" + Utang_Kso_Laborat_Ralan + "'");
                                    }
                                    if (ttljasasarana > 0) {
                                        Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Sarana_Laborat_Ralan + "','Beban Jasa Sarana Laborat Ralan','0','" + ttljasasarana + "'", "kredit=kredit+'" + (ttljasasarana) + "'", "kd_rek='" + Beban_Jasa_Sarana_Laborat_Ralan + "'");
                                        Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Sarana_Laborat_Ralan + "','Utang Jasa Sarana Laborat Ralan','" + ttljasasarana + "','0'", "debet=debet+'" + (ttljasasarana) + "'", "kd_rek='" + Utang_Jasa_Sarana_Laborat_Ralan + "'");
                                    }
                                    if (ttljmperujuk > 0) {
                                        Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Perujuk_Laborat_Ralan + "','Beban Jasa Perujuk Laborat Ralan','0','" + ttljmperujuk + "'", "kredit=kredit+'" + (ttljmperujuk) + "'", "kd_rek='" + Beban_Jasa_Perujuk_Laborat_Ralan + "'");
                                        Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Perujuk_Laborat_Ralan + "','Utang Jasa Perujuk Laborat Ralan','" + ttljmperujuk + "','0'", "debet=debet+'" + (ttljmperujuk) + "'", "kd_rek='" + Utang_Jasa_Perujuk_Laborat_Ralan + "'");
                                    }
                                    if (ttlmenejemen > 0) {
                                        Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Menejemen_Laborat_Ralan + "','Beban Jasa Menejemen Laborat Ralan','0','" + ttlmenejemen + "'", "kredit=kredit+'" + (ttlmenejemen) + "'", "kd_rek='" + Beban_Jasa_Menejemen_Laborat_Ralan + "'");
                                        Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Menejemen_Laborat_Ralan + "','Utang Jasa Menejemen Laborat Ralan','" + ttlmenejemen + "','0'", "debet=debet+'" + (ttlmenejemen) + "'", "kd_rek='" + Utang_Jasa_Menejemen_Laborat_Ralan + "'");
                                    }
                                    String nm_pasien = Sequel.cariIsi("SELECT  CONCAT(ps.no_rkm_medis,' ',ps.nm_pasien,' (Poli : ',pl.nm_poli,')') FROM reg_periksa rp \n" +
                                    "JOIN pasien ps ON ps.no_rkm_medis = rp.no_rkm_medis\n" +
                                    "JOIN poliklinik pl ON pl.kd_poli = rp.kd_poli\n" +
                                    "WHERE rp.no_rawat =? ",rs.getString("no_rawat"));
                                    sukses = jur.simpanJurnal(rs.getString("no_rawat").toString(), "U", "PEMBATALAN PEMERIKSAAN LABORAT RAWAT JALAN PASIEN " + nm_pasien + " OLEH " + akses.getkode());
                                }
                            }

                            if (sukses == true) {
                                Sequel.Commit();
                            } else {
                                JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat pemrosesan data, transaksi dibatalkan.\nPeriksa kembali data sebelum melanjutkan menyimpan..!!");
                                Sequel.RollBack();
                            }
                            Sequel.AutoComitTrue();
                        } catch (Exception e) {
                            System.out.println("Notifikasi : " + e);
                            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih terlebih dulu data yang mau anda hapus...\n Klik data pada table untuk memilih data...!!!!");
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
        return sukses;
    }
    
    private boolean hapusPemeriksaanRad() {        
        try {
            ps = koneksi.prepareStatement("SELECT * FROM periksa_radiologi pr WHERE pr.no_rawat = ? AND pr.status = 'Ralan' ");
            try {
                ps.setString(1, TNoRw.getText());
                rs = ps.executeQuery();
                while (rs.next()) {
                    if (Sequel.cariRegistrasi(rs.getString("no_rawat")) > 0) {
                        JOptionPane.showMessageDialog(rootPane, "Data billing sudah terverifikasi, data tidak boleh dihapus.\nSilahkan hubungi bagian kasir/keuangan ..!!");
                    } else {
                        try {
                            Sequel.AutoComitFalse();
                            sukses = true;
                            status = "";
                            ttljmdokter = 0;
                            ttljmpetugas = 0;
                            ttlkso = 0;
                            ttlpendapatan = 0;
                            ttlbhp = 0;
                            ttljasasarana = 0;
                            ttljmperujuk = 0;
                            ttlmenejemen = 0;
                            ttljmdokter = Sequel.cariIsiAngka("select sum(periksa_radiologi.tarif_tindakan_dokter) from periksa_radiologi where periksa_radiologi.no_rawat='" + rs.getString("no_rawat")
                                    + "' and tgl_periksa='" + rs.getString("tgl_periksa")
                                    + "' and jam='" + rs.getString("jam") + "'");
                            ttljmpetugas = Sequel.cariIsiAngka("select sum(periksa_radiologi.tarif_tindakan_petugas) from periksa_radiologi where periksa_radiologi.no_rawat='" + rs.getString("no_rawat")
                                    + "' and tgl_periksa='" + rs.getString("tgl_periksa")
                                    + "' and jam='" + rs.getString("jam") + "'");
                            ttlkso = Sequel.cariIsiAngka("select sum(periksa_radiologi.kso) from periksa_radiologi where periksa_radiologi.no_rawat='" + rs.getString("no_rawat")
                                    + "' and tgl_periksa='" + rs.getString("tgl_periksa")
                                    + "' and jam='" + rs.getString("jam") + "'");
                            ttlbhp = Sequel.cariIsiAngka("select sum(periksa_radiologi.bhp) from periksa_radiologi where periksa_radiologi.no_rawat='" + rs.getString("no_rawat")
                                    + "' and tgl_periksa='" + rs.getString("tgl_periksa")
                                    + "' and jam='" + rs.getString("jam") + "'");
                            ttlpendapatan = Sequel.cariIsiAngka("select sum(periksa_radiologi.biaya) from periksa_radiologi where periksa_radiologi.no_rawat='" + rs.getString("no_rawat")
                                    + "' and tgl_periksa='" + rs.getString("tgl_periksa")
                                    + "' and jam='" + rs.getString("jam") + "'");
                            ttljasasarana = Sequel.cariIsiAngka("select sum(periksa_radiologi.bagian_rs) from periksa_radiologi where periksa_radiologi.no_rawat='" + rs.getString("no_rawat")
                                    + "' and tgl_periksa='" + rs.getString("tgl_periksa")
                                    + "' and jam='" + rs.getString("jam") + "'");
                            ttljmperujuk = Sequel.cariIsiAngka("select sum(periksa_radiologi.tarif_perujuk) from periksa_radiologi where periksa_radiologi.no_rawat='" + rs.getString("no_rawat")
                                    + "' and tgl_periksa='" + rs.getString("tgl_periksa")
                                    + "' and jam='" + rs.getString("jam") + "'");
                            ttlmenejemen = Sequel.cariIsiAngka("select sum(periksa_radiologi.menejemen) from periksa_radiologi where periksa_radiologi.no_rawat='" + rs.getString("no_rawat")
                                    + "' and tgl_periksa='" + rs.getString("tgl_periksa")
                                    + "' and jam='" + rs.getString("jam") + "'");

                            status = Sequel.cariIsi("select periksa_radiologi.status from periksa_radiologi where periksa_radiologi.no_rawat='" + rs.getString("no_rawat")
                                    + "' and tgl_periksa='" + rs.getString("tgl_periksa")
                                    + "' and jam='" + rs.getString("jam") + "'");

                            if (Sequel.queryu2tf("delete from periksa_radiologi where periksa_radiologi.no_rawat=? and tgl_periksa=? and jam=?", 3, new String[]{
                                rs.getString("no_rawat").toString(), rs.getString("tgl_periksa").toString(), rs.getString("jam").toString()
                            }) == true) {
                                Sequel.queryu2("delete from hasil_radiologi where no_rawat=? and tgl_periksa=? and jam=?", 3, new String[]{
                                    rs.getString("no_rawat").toString(), rs.getString("tgl_periksa").toString(), rs.getString("jam").toString()
                                });
                                Sequel.queryu2("delete from gambar_radiologi where no_rawat=? and tgl_periksa=? and jam=?", 3, new String[]{
                                    rs.getString("no_rawat").toString(), rs.getString("tgl_periksa").toString(), rs.getString("jam").toString()
                                });

                                ttlbhp = ttlbhp + Sequel.cariIsiAngka("select sum(beri_bhp_radiologi.total) from beri_bhp_radiologi where no_rawat='" + rs.getString("no_rawat")
                                        + "' and tgl_periksa='" + rs.getString("tgl_periksa")
                                        + "' and jam='" + rs.getString("jam") + "'");
                                if (Sequel.queryu2tf("delete from beri_bhp_radiologi where no_rawat=? and tgl_periksa=? and jam=?", 3, new String[]{
                                    rs.getString("no_rawat").toString(), rs.getString("tgl_periksa").toString(), rs.getString("jam").toString()
                                }) == false) {
                                    sukses = false;
                                }
                            } else {
                                sukses = false;
                            }

                            if (sukses == true) {
                                if (status.equals("Ralan")) {
                                    Sequel.queryu("delete from tampjurnal");
                                    if (ttlpendapatan > 0) {
                                        Sequel.menyimpan("tampjurnal", "'" + Suspen_Piutang_Radiologi_Ralan + "','Suspen Piutang Radiologi Ralan','0','" + ttlpendapatan + "'", "kredit=kredit+'" + (ttlpendapatan) + "'", "kd_rek='" + Suspen_Piutang_Radiologi_Ralan + "'");
                                        Sequel.menyimpan("tampjurnal", "'" + Radiologi_Ralan + "','Pendapatan Radiologi Rawat Inap','" + ttlpendapatan + "','0'", "debet=debet+'" + (ttlpendapatan) + "'", "kd_rek='" + Radiologi_Ralan + "'");
                                    }
                                    if (ttljmdokter > 0) {
                                        Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Medik_Dokter_Radiologi_Ralan + "','Beban Jasa Medik Dokter Radiologi Ralan','0','" + ttljmdokter + "'", "kredit=kredit+'" + (ttljmdokter) + "'", "kd_rek='" + Beban_Jasa_Medik_Dokter_Radiologi_Ralan + "'");
                                        Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Medik_Dokter_Radiologi_Ralan + "','Utang Jasa Medik Dokter Radiologi Ralan','" + ttljmdokter + "','0'", "debet=debet+'" + (ttljmdokter) + "'", "kd_rek='" + Utang_Jasa_Medik_Dokter_Radiologi_Ralan + "'");
                                    }
                                    if (ttljmpetugas > 0) {
                                        Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Medik_Petugas_Radiologi_Ralan + "','Beban Jasa Medik Petugas Radiologi Ralan','0','" + ttljmpetugas + "'", "kredit=kredit+'" + (ttljmpetugas) + "'", "kd_rek='" + Beban_Jasa_Medik_Petugas_Radiologi_Ralan + "'");
                                        Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Medik_Petugas_Radiologi_Ralan + "','Utang Jasa Medik Petugas Radiologi Ralan','" + ttljmpetugas + "','0'", "debet=debet+'" + (ttljmpetugas) + "'", "kd_rek='" + Utang_Jasa_Medik_Petugas_Radiologi_Ralan + "'");
                                    }
                                    if (ttlbhp > 0) {
                                        Sequel.menyimpan("tampjurnal", "'" + HPP_Persediaan_Radiologi_Rawat_Jalan + "','HPP Persediaan Radiologi Rawat Jalan','0','" + ttlbhp + "'", "kredit=kredit+'" + (ttlbhp) + "'", "kd_rek='" + HPP_Persediaan_Radiologi_Rawat_Jalan + "'");
                                        Sequel.menyimpan("tampjurnal", "'" + Persediaan_BHP_Radiologi_Rawat_Jalan + "','Persediaan BHP Radiologi Rawat Jalan','" + ttlbhp + "','0'", "debet=debet+'" + (ttlbhp) + "'", "kd_rek='" + Persediaan_BHP_Radiologi_Rawat_Jalan + "'");
                                    }
                                    if (ttlkso > 0) {
                                        Sequel.menyimpan("tampjurnal", "'" + Beban_Kso_Radiologi_Ralan + "','HPP Persediaan Radiologi Rawat Inap','0','" + ttlkso + "'", "kredit=kredit+'" + (ttlkso) + "'", "kd_rek='" + Beban_Kso_Radiologi_Ralan + "'");
                                        Sequel.menyimpan("tampjurnal", "'" + Utang_Kso_Radiologi_Ralan + "','Persediaan BHP Radiologi Rawat Inap','" + ttlkso + "','0'", "debet=debet+'" + (ttlkso) + "'", "kd_rek='" + Utang_Kso_Radiologi_Ralan + "'");
                                    }
                                    if (ttljasasarana > 0) {
                                        Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Sarana_Radiologi_Ralan + "','Beban Jasa Sarana Radiologi Ralan','0','" + ttljasasarana + "'", "kredit=kredit+'" + (ttljasasarana) + "'", "kd_rek='" + Beban_Jasa_Sarana_Radiologi_Ralan + "'");
                                        Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Sarana_Radiologi_Ralan + "','Utang Jasa Sarana Radiologi Ralan','" + ttljasasarana + "','0'", "debet=debet+'" + (ttljasasarana) + "'", "kd_rek='" + Utang_Jasa_Sarana_Radiologi_Ralan + "'");
                                    }
                                    if (ttljmperujuk > 0) {
                                        Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Perujuk_Radiologi_Ralan + "','Beban Jasa Perujuk Radiologi Ralan','0','" + ttljmperujuk + "'", "kredit=kredit+'" + (ttljmperujuk) + "'", "kd_rek='" + Beban_Jasa_Perujuk_Radiologi_Ralan + "'");
                                        Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Perujuk_Radiologi_Ralan + "','Utang Jasa Perujuk Radiologi Ralan','" + ttljmperujuk + "','0'", "debet=debet+'" + (ttljmperujuk) + "'", "kd_rek='" + Utang_Jasa_Perujuk_Radiologi_Ralan + "'");
                                    }
                                    if (ttlmenejemen > 0) {
                                        Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Menejemen_Radiologi_Ralan + "','Beban Jasa Menejemen Radiologi Ralan','0','" + ttlmenejemen + "'", "kredit=kredit+'" + (ttlmenejemen) + "'", "kd_rek='" + Beban_Jasa_Menejemen_Radiologi_Ralan + "'");
                                        Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Menejemen_Radiologi_Ralan + "','Utang Jasa Menejemen Radiologi Ralan','" + ttlmenejemen + "','0'", "debet=debet+'" + (ttlmenejemen) + "'", "kd_rek='" + Utang_Jasa_Menejemen_Radiologi_Ralan + "'");
                                    }
                                    String nm_pasien = Sequel.cariIsi("SELECT  CONCAT(ps.no_rkm_medis,' ',ps.nm_pasien,' (Poli : ',pl.nm_poli,')') FROM reg_periksa rp \n" +
                                    "JOIN pasien ps ON ps.no_rkm_medis = rp.no_rkm_medis\n" +
                                    "JOIN poliklinik pl ON pl.kd_poli = rp.kd_poli\n" +
                                    "WHERE rp.no_rawat =? ",rs.getString("no_rawat"));
                                    sukses = jur.simpanJurnal(rs.getString("no_rawat").toString(), "U", "PEMBATALAN PEMERIKSAAN RADIOLOGI RAWAT JALAN PASIEN " + nm_pasien + " OLEH " + akses.getkode());
                                }
                            }

                            if (sukses == true) {
                                Sequel.Commit();
                            } else {
                                JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat pemrosesan data, transaksi dibatalkan.\nPeriksa kembali data sebelum melanjutkan menyimpan..!!");
                                Sequel.RollBack();
                            }
                            Sequel.AutoComitTrue();
                        } catch (Exception e) {
                            System.out.println("Notifikasi : " + e);
                            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih terlebih dulu data yang mau anda hapus...\n Klik data pada table untuk memilih data...!!!!");
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
        return sukses;
    }
    
    private boolean hapusPemberianObat() {        
        try {
            ps = koneksi.prepareStatement("SELECT * FROM resep_obat ro WHERE ro.no_rawat = ? AND CONCAT(ro.tgl_perawatan, ' ', ro.jam) <> '0000-00-00 00:00:00'");
            try {
                ps.setString(1, TNoRw.getText());
                rs = ps.executeQuery();
                while (rs.next()) {
                    if (Sequel.cariRegistrasi(rs.getString("no_rawat")) > 0) {
                        JOptionPane.showMessageDialog(rootPane, "Data billing sudah terverifikasi, data tidak boleh dihapus.\nSilahkan hubungi bagian kasir/keuangan ..!!");
                    } else {
                        String no_rm = Sequel.cariIsi("select rp.no_rkm_medis from reg_periksa rp where rp.no_rawat = ?", rs.getString("no_rawat"));
                        String nm_pasien = Sequel.cariIsi("select ps.nm_pasien from pasien ps where ps.no_rkm_medis = ?", no_rm);
                        String tgl_perawatan = Sequel.cariIsi("select tgl_perawatan from resep_obat where no_resep = ?", rs.getString("no_resep"));
                        String jam = Sequel.cariIsi("select jam from resep_obat where no_resep = ?", rs.getString("no_resep"));
                        sql = "select detail_pemberian_obat.tgl_perawatan,detail_pemberian_obat.jam,"
                                + "detail_pemberian_obat.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,"
                                + "detail_pemberian_obat.kode_brng,databarang.nama_brng,detail_pemberian_obat.embalase,detail_pemberian_obat.tuslah,"
                                + "detail_pemberian_obat.jml,detail_pemberian_obat.biaya_obat,detail_pemberian_obat.total,detail_pemberian_obat.h_beli,"
                                + "detail_pemberian_obat.kd_bangsal,detail_pemberian_obat.no_batch,detail_pemberian_obat.no_faktur "
                                + "from detail_pemberian_obat inner join reg_periksa on detail_pemberian_obat.no_rawat=reg_periksa.no_rawat "
                                + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                                + "inner join databarang on detail_pemberian_obat.kode_brng=databarang.kode_brng "
                                + "where detail_pemberian_obat.tgl_perawatan = ? and detail_pemberian_obat.jam = ? and detail_pemberian_obat.no_rawat = ?";
                        try {
                            psdetailberiobat = koneksi.prepareStatement(sql);
                            try {
                                psdetailberiobat.setString(1, tgl_perawatan);
                                psdetailberiobat.setString(2, jam);
                                psdetailberiobat.setString(3, rs.getString("no_rawat"));

                                rsdetailberiobat = psdetailberiobat.executeQuery();
                                while (rsdetailberiobat.next()) {
                                    bangsal = rsdetailberiobat.getString("kd_bangsal");
                                    statusberi = Sequel.cariIsi("select detail_pemberian_obat.status from detail_pemberian_obat where detail_pemberian_obat.no_rawat='" + rsdetailberiobat.getString("no_rawat") + "' "
                                            + "and detail_pemberian_obat.kode_brng='" + rsdetailberiobat.getString("kode_brng") + "' "
                                            + "and detail_pemberian_obat.tgl_perawatan='" + rsdetailberiobat.getString("tgl_perawatan") + "' "
                                            + "and detail_pemberian_obat.jam='" + rsdetailberiobat.getString("jam") + "' "
                                            + "and detail_pemberian_obat.no_batch='" + rsdetailberiobat.getString("no_batch") + "' "
                                            + "and detail_pemberian_obat.no_faktur='" + rsdetailberiobat.getString("no_faktur") + "' ");
                                    ttlhpp = Sequel.cariIsiAngka("select sum(detail_pemberian_obat.h_beli*detail_pemberian_obat.jml) from detail_pemberian_obat where detail_pemberian_obat.no_rawat='" + rsdetailberiobat.getString("no_rawat") + "' "
                                            + "and detail_pemberian_obat.kode_brng='" + rsdetailberiobat.getString("kode_brng") + "' "
                                            + "and detail_pemberian_obat.tgl_perawatan='" + rsdetailberiobat.getString("tgl_perawatan") + "' "
                                            + "and detail_pemberian_obat.jam='" + rsdetailberiobat.getString("jam") + "' "
                                            + "and detail_pemberian_obat.no_batch='" + rsdetailberiobat.getString("no_batch") + "' "
                                            + "and detail_pemberian_obat.no_faktur='" + rsdetailberiobat.getString("no_faktur") + "' ");
                                    ttljual = Sequel.cariIsiAngka("select sum(detail_pemberian_obat.total) from detail_pemberian_obat where detail_pemberian_obat.no_rawat='" + rsdetailberiobat.getString("no_rawat") + "' "
                                            + "and detail_pemberian_obat.kode_brng='" + rsdetailberiobat.getString("kode_brng") + "' "
                                            + "and detail_pemberian_obat.tgl_perawatan='" + rsdetailberiobat.getString("tgl_perawatan") + "' "
                                            + "and detail_pemberian_obat.jam='" + rsdetailberiobat.getString("jam") + "' "
                                            + "and detail_pemberian_obat.no_batch='" + rsdetailberiobat.getString("no_batch") + "' "
                                            + "and detail_pemberian_obat.no_faktur='" + rsdetailberiobat.getString("no_faktur") + "' ");
                                    if (Sequel.queryutf("delete from detail_pemberian_obat where no_rawat='" + rsdetailberiobat.getString("no_rawat") + "' "
                                            + "and kode_brng='" + rsdetailberiobat.getString("kode_brng") + "' "
                                            + "and tgl_perawatan='" + rsdetailberiobat.getString("tgl_perawatan") + "' "
                                            + "and jam='" + rsdetailberiobat.getString("jam") + "' "
                                            + "and no_batch='" + rsdetailberiobat.getString("no_batch") + "' "
                                            + "and no_faktur='" + rsdetailberiobat.getString("no_faktur") + "' ") == true) {

                                        if (statusberi.equals("Ralan")) {
                                            Sequel.queryu("delete from tampjurnal");
                                            if (ttljual > 0) {
                                                Sequel.menyimpan("tampjurnal", "'" + Suspen_Piutang_Obat_Ralan + "','Suspen Piutang Obat Ralan','0','" + ttljual + "'", "Rekening");
                                                Sequel.menyimpan("tampjurnal", "'" + Obat_Ralan + "','Pendapatan Obat Rawat Jalan','" + ttljual + "','0'", "Rekening");
                                            }
                                            if (ttlhpp > 0) {
                                                Sequel.menyimpan("tampjurnal", "'" + HPP_Obat_Rawat_Jalan + "','HPP Persediaan Obat Rawat Jalan','0','" + ttlhpp + "'", "Rekening");
                                                Sequel.menyimpan("tampjurnal", "'" + Persediaan_Obat_Rawat_Jalan + "','Persediaan Obat Rawat Jalan','" + ttlhpp + "','0'", "Rekening");
                                            }
                                            sukses = jur.simpanJurnal(rsdetailberiobat.getString("no_rawat"), "U", "PEMBATALAN PEMBERIAN OBAT RAWAT JALAN PASIEN " + no_rm + " " + nm_pasien + " OLEH " + akses.getkode());
                                        }

                                        Sequel.queryu("delete from aturan_pakai where no_rawat='" + rsdetailberiobat.getString("no_rawat") + "' "
                                                + "and kode_brng='" + rsdetailberiobat.getString("kode_brng") + "' "
                                                + "and tgl_perawatan='" + rsdetailberiobat.getString("tgl_perawatan") + "' "
                                                + "and jam='" + rsdetailberiobat.getString("jam") + "'");
                                        if (Sequel.cariInteger("select count(stok_obat_pasien.no_rawat) from stok_obat_pasien where stok_obat_pasien.no_rawat=? ", rsdetailberiobat.getString("no_rawat")) == 0) {
                                            if (aktifkanbatch.equals("yes")) {
                                                Sequel.mengedit("data_batch", "no_batch=? and kode_brng=? and no_faktur=?", "sisa=sisa+?", 4, new String[]{
                                                    rsdetailberiobat.getString("jml"),
                                                    rsdetailberiobat.getString("no_batch"),
                                                    rsdetailberiobat.getString("kode_brng"),
                                                    rsdetailberiobat.getString("no_faktur")
                                                });
                                                Trackobat.catatRiwayat(rsdetailberiobat.getString("kode_brng"), Valid.SetAngka(rsdetailberiobat.getString("jml")),
                                                        0, "Pemberian Obat", akses.getkode(), bangsal, "Hapus", rsdetailberiobat.getString("no_batch"), rsdetailberiobat.getString("no_faktur"),
                                                        rsdetailberiobat.getString("no_rawat") + " " + rsdetailberiobat.getString("no_rkm_medis") + " " + rsdetailberiobat.getString("nm_pasien")
                                                );
                                                Sequel.menyimpan("gudangbarang", "'" + rsdetailberiobat.getString("kode_brng") + "','" + bangsal + "',"
                                                        + "'" + rsdetailberiobat.getString("jml") + "',"
                                                        + "'" + rsdetailberiobat.getString("no_batch") + "',"
                                                        + "'" + rsdetailberiobat.getString("no_faktur") + "'",
                                                        "stok=stok+'" + rsdetailberiobat.getString("jml") + "'",
                                                        "kode_brng='" + rsdetailberiobat.getString("kode_brng") + "' and kd_bangsal='" + bangsal + "'  "
                                                        + "and no_batch='" + rsdetailberiobat.getString("no_batch") + "' "
                                                        + "and no_faktur='" + rsdetailberiobat.getString("no_faktur") + "'");
                                            } else {
                                                Trackobat.catatRiwayat(rsdetailberiobat.getString("kode_brng"), Valid.SetAngka(rsdetailberiobat.getString("jml")), 0, "Pemberian Obat", akses.getkode(), bangsal, "Hapus", "", "",
                                                        rsdetailberiobat.getString("no_rawat") + " " + rsdetailberiobat.getString("no_rkm_medis") + " " + rsdetailberiobat.getString("nm_pasien")
                                                );
                                                Sequel.menyimpan("gudangbarang", "'" + rsdetailberiobat.getString("kode_brng") + "','" + bangsal + "',"
                                                        + "'" + rsdetailberiobat.getString("jml") + "','',''",
                                                        "stok=stok+'" + rsdetailberiobat.getString("jml") + "'",
                                                        "kode_brng='" + rsdetailberiobat.getString("kode_brng") + "' and kd_bangsal='" + bangsal + "'  "
                                                        + "and no_batch='' and no_faktur=''");
                                            }
                                        }
                                        Sequel.mengedit("resep_obat", "no_resep=? ", "tgl_perawatan='0000-00-00', jam='00:00:00'", 1, new String[]{
                                            rs.getString("no_resep")
                                        });
                                    }
                                }
                            } catch (Exception e) {
                                System.out.println("Notif : " + e);
                                sukses=false;
                            }
                        } catch (Exception e) {
                            System.out.println("Notif : " + e);
                            sukses=false;
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
        return sukses;
    }
    
    
    

}
