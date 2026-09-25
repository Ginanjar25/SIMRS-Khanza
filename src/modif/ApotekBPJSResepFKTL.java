/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DlgResepObat.java
 *
 * Created on 31 Mei 10, 11:27:40
 */

package modif;
import bridging.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.WarnaTable;
import fungsi.WarnaTable2;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URI;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


/**
 *
 * @author perpustakaan
 */
public final class ApotekBPJSResepFKTL extends javax.swing.JDialog {
    private final DefaultTableModel tabMode,tabMode2;
    private Connection koneksi=koneksiDB.condb();
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private PreparedStatement ps,ps2,psresep;
    private ResultSet rs,rs2,rsresep;
    private BPJSCekReferensiDokterDPJP dokter=new BPJSCekReferensiDokterDPJP(null,false);
    private BPJSCekReferensiDiagnosaPRB diagnosa=new BPJSCekReferensiDiagnosaPRB(null,false);
    private ApotekBPJSCekReferensiPoli poli=new ApotekBPJSCekReferensiPoli(null,false);
    private int i=0,z=0;
    private WarnaTable2 warna=new WarnaTable2();
    private double[] jumlah;
    private String[] kodeobat,namaobat,signa1,signa2;
    private HttpHeaders headers;
    private HttpEntity requestEntity;
    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode root;
    private JsonNode nameNode;
    private JsonNode response;
    private ApiBPJS api=new ApiBPJS();
    private String URL="",link="",utc="",requestJson="",obat="", user="";

    /** Creates new form DlgResepObat 
     *@param parent
     *@param modal*/
    public ApotekBPJSResepFKTL(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8,1);
        setSize(628,674);        
        tabMode=new DefaultTableModel(null,new Object[]{
            "No.Rawat","No.RM","Nama Pasien", "No.SEP","No.SEP Apotek",
            "Tgl.SEP","Kode DPJP","Dokter DPJP","Kode Poli","Nama Poli","No Resep",
            "Tgl.Resep","Tgl.Pel Resep","Jenis Resep","Iterasi", "Status", "PPK Rujukan",
            "Nama PPK Rujukan", "byTagRsp", "ID User", "byVerRsp", "Tgl.Entry"
            }){
             @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        tbProgramPRB.setModel(tabMode);

        tbProgramPRB.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbProgramPRB.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 22; i++) {
            TableColumn column = tbProgramPRB.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(105);
            }else if(i==1){
                column.setPreferredWidth(70);
            }else if(i==2){
                column.setPreferredWidth(150);
            }else if(i==3){
                column.setPreferredWidth(180);
            }else if(i==4){
                column.setPreferredWidth(150);
            }else if(i==5){
                column.setPreferredWidth(85);
            }else if(i==6){
                column.setPreferredWidth(122);
            }else if(i==7){
                column.setPreferredWidth(80);
            }else if(i==8){
                column.setPreferredWidth(65);
            }else if(i==9){
                column.setPreferredWidth(75);
            }else if(i==10){
                column.setPreferredWidth(150);
            }else if(i==11){
                column.setPreferredWidth(60);
            }else if(i==12){
                column.setPreferredWidth(150);
            }else if(i==13){
                column.setPreferredWidth(150);
            }else if(i==14){
                column.setPreferredWidth(150);
            }else if(i==15){
                column.setPreferredWidth(105);
            }else if(i==16){
                column.setPreferredWidth(70);
            }else if(i==17){
                column.setPreferredWidth(150);
            }else if(i==18){
                column.setPreferredWidth(180);
            }else if(i==19){
                column.setPreferredWidth(150);
            }else if(i==20){
                column.setPreferredWidth(85);
            }else if(i==21){
                column.setPreferredWidth(122);
            }
        }
        tbProgramPRB.setDefaultRenderer(Object.class, new WarnaTable());
        
       
        tabMode2=new DefaultTableModel(null,new Object[]{
            "No Resep","Tgl Peresepan","Tgl Pelayanan","Status","Status Kirim"}){
             @Override public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = false;
                if ((colIndex==0)||(colIndex==3)||(colIndex==4)) {
                    a=true;
                }
                return a;
             }
             Class[] types = new Class[] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        tbObat.setModel(tabMode2);

        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 5; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(100);
            }else if(i==1){
                column.setPreferredWidth(120);
            }else if(i==2){
                column.setPreferredWidth(120);
            }else if(i==3){
                column.setPreferredWidth(90);
            }else if(i==4){
                column.setPreferredWidth(90);
            }
        }
        warna.kolom=0;
        tbObat.setDefaultRenderer(Object.class,warna);

        NoRawat.setDocument(new batasInput((byte)17).getKata(NoRawat));
        NoSEP.setDocument(new batasInput((int)200).getKata(NoSEP));
        kd_user.setDocument(new batasInput((byte)100).getKata(kd_user));
        TCari.setDocument(new batasInput((byte)100).getKata(TCari));
        NotaPiutang.setDocument(new batasInput((byte)100).getKata(NotaPiutang));
        
        if(koneksiDB.CARICEPAT().equals("aktif")){
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
            });
        }  
        ChkInput.setSelected(false);
        isForm();
        
        dokter.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(dokter.getTable().getSelectedRow()!= -1){  
                    KdDPJP.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),1).toString());
                    NmDPJP.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),2).toString());
                    KdDPJP.requestFocus();             
                }  
            }
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
        
        dokter.getTable().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_SPACE){
                    dokter.dispose();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        
        diagnosa.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(diagnosa.getTable().getSelectedRow()!= -1){  
                    KdPoli.setText(diagnosa.getTable().getValueAt(diagnosa.getTable().getSelectedRow(),1).toString());
                    NmPoli.setText(diagnosa.getTable().getValueAt(diagnosa.getTable().getSelectedRow(),2).toString());
                    KdPoli.requestFocus();             
                }  
            }
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
        
        diagnosa.getTable().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_SPACE){
                    diagnosa.dispose();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        
        poli.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(poli.getTable().getSelectedRow()!= -1){  
                     KdPoli.setText(poli.getTable().getValueAt(poli.getTable().getSelectedRow(),1).toString());
                     NmPoli.setText(poli.getTable().getValueAt(poli.getTable().getSelectedRow(),2).toString());
                     KdPoli.requestFocus();         
                }  
            }
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
        
        poli.getTable().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_SPACE){
                    poli.dispose();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        }); 
        
        try {
             link=koneksiDB.URLAPIAPOTEKBPJS();
        } catch (Exception e) {
            System.out.println("E : "+e);
        }
        
        try {
            user=akses.getkode().replace(" ","").substring(0,9);
        } catch (Exception e) {
            user=akses.getkode();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        MnSurat = new javax.swing.JMenuItem();
        MnTampilkanObatPRB = new javax.swing.JMenuItem();
        internalFrame1 = new widget.InternalFrame();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        BtnPrint = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        BtnKeluar = new widget.Button();
        panelGlass9 = new widget.panelisi();
        jLabel19 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel21 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        PanelInput = new javax.swing.JPanel();
        FormInput = new widget.PanelBiasa();
        NoRawat = new widget.TextBox();
        NoRM = new widget.TextBox();
        KdDPJP = new widget.TextBox();
        NmDPJP = new widget.TextBox();
        jLabel3 = new widget.Label();
        jLabel13 = new widget.Label();
        btnDokterDPJP = new widget.Button();
        NmPasien = new widget.TextBox();
        jLabel5 = new widget.Label();
        NoSEP = new widget.TextBox();
        jLabel10 = new widget.Label();
        NoSEPApotek = new widget.TextBox();
        jLabel14 = new widget.Label();
        KdPoli = new widget.TextBox();
        NmPoli = new widget.TextBox();
        btnPoli = new widget.Button();
        jLabel11 = new widget.Label();
        kd_user = new widget.TextBox();
        jLabel15 = new widget.Label();
        NotaPiutang = new widget.TextBox();
        jLabel20 = new widget.Label();
        Scroll2 = new widget.ScrollPane();
        tbObat = new widget.Table();
        cmbJenisResep = new widget.ComboBox();
        jLabel16 = new widget.Label();
        jLabel17 = new widget.Label();
        cmbIterasi = new widget.ComboBox();
        jLabel18 = new widget.Label();
        cmbStatus = new widget.ComboBox();
        tglSEP = new widget.TextBox();
        tglResep = new widget.TextBox();
        jLabel22 = new widget.Label();
        jLabel23 = new widget.Label();
        tglPelResep = new widget.TextBox();
        kdPpkRujukan = new widget.TextBox();
        jLabel24 = new widget.Label();
        jLabel25 = new widget.Label();
        nmPpkPelayanan = new widget.TextBox();
        panelCari = new widget.panelisi();
        ChkInput = new widget.CekBox();
        Scroll = new widget.ScrollPane();
        tbProgramPRB = new widget.Table();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnSurat.setBackground(new java.awt.Color(255, 255, 254));
        MnSurat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnSurat.setForeground(new java.awt.Color(50, 50, 50));
        MnSurat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnSurat.setText("Surat Rujuk Balik");
        MnSurat.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnSurat.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnSurat.setName("MnSurat"); // NOI18N
        MnSurat.setPreferredSize(new java.awt.Dimension(160, 26));
        MnSurat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnSuratActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnSurat);

        MnTampilkanObatPRB.setBackground(new java.awt.Color(255, 255, 254));
        MnTampilkanObatPRB.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnTampilkanObatPRB.setForeground(new java.awt.Color(50, 50, 50));
        MnTampilkanObatPRB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnTampilkanObatPRB.setText("Tampilkan Obat PRB");
        MnTampilkanObatPRB.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnTampilkanObatPRB.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnTampilkanObatPRB.setName("MnTampilkanObatPRB"); // NOI18N
        MnTampilkanObatPRB.setPreferredSize(new java.awt.Dimension(160, 26));
        MnTampilkanObatPRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnTampilkanObatPRBActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnTampilkanObatPRB);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Apotek BPJS Resep ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout());

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(44, 100));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 44));
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

        BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png"))); // NOI18N
        BtnBatal.setMnemonic('B');
        BtnBatal.setText("Baru");
        BtnBatal.setToolTipText("Alt+B");
        BtnBatal.setName("BtnBatal"); // NOI18N
        BtnBatal.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBatalActionPerformed(evt);
            }
        });
        BtnBatal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnBatalKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnBatal);

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        BtnHapus.setMnemonic('H');
        BtnHapus.setText("Hapus");
        BtnHapus.setToolTipText("Alt+H");
        BtnHapus.setName("BtnHapus"); // NOI18N
        BtnHapus.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHapusActionPerformed(evt);
            }
        });
        BtnHapus.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnHapusKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnHapus);

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

        BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png"))); // NOI18N
        BtnPrint.setMnemonic('T');
        BtnPrint.setText("Cetak");
        BtnPrint.setToolTipText("Alt+T");
        BtnPrint.setName("BtnPrint"); // NOI18N
        BtnPrint.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPrintActionPerformed(evt);
            }
        });
        BtnPrint.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnPrintKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnPrint);

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(55, 30));
        panelGlass8.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(52, 30));
        panelGlass8.add(LCount);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
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

        jPanel3.add(panelGlass8, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9"); // NOI18N
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel19.setText("Tanggal :");
        jLabel19.setName("jLabel19"); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass9.add(jLabel19);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "20-11-2025" }));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1"); // NOI18N
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari1);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("s.d.");
        jLabel21.setName("jLabel21"); // NOI18N
        jLabel21.setPreferredSize(new java.awt.Dimension(23, 23));
        panelGlass9.add(jLabel21);

        DTPCari2.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "20-11-2025" }));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2"); // NOI18N
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari2);

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(290, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass9.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('4');
        BtnCari.setToolTipText("Alt+4");
        BtnCari.setName("BtnCari"); // NOI18N
        BtnCari.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariActionPerformed(evt);
            }
        });
        BtnCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCariKeyPressed(evt);
            }
        });
        panelGlass9.add(BtnCari);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('M');
        BtnAll.setToolTipText("Alt+M");
        BtnAll.setName("BtnAll"); // NOI18N
        BtnAll.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAllActionPerformed(evt);
            }
        });
        BtnAll.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnAllKeyPressed(evt);
            }
        });
        panelGlass9.add(BtnAll);

        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(400, 200));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(440, 200));
        FormInput.setLayout(null);

        NoRawat.setEditable(false);
        NoRawat.setHighlighter(null);
        NoRawat.setName("NoRawat"); // NOI18N
        FormInput.add(NoRawat);
        NoRawat.setBounds(83, 10, 125, 23);

        NoRM.setEditable(false);
        NoRM.setHighlighter(null);
        NoRM.setName("NoRM"); // NOI18N
        FormInput.add(NoRM);
        NoRM.setBounds(210, 10, 95, 23);

        KdDPJP.setEditable(false);
        KdDPJP.setHighlighter(null);
        KdDPJP.setName("KdDPJP"); // NOI18N
        FormInput.add(KdDPJP);
        KdDPJP.setBounds(83, 70, 75, 23);

        NmDPJP.setEditable(false);
        NmDPJP.setHighlighter(null);
        NmDPJP.setName("NmDPJP"); // NOI18N
        FormInput.add(NmDPJP);
        NmDPJP.setBounds(160, 70, 170, 23);

        jLabel3.setText("No.Rawat :");
        jLabel3.setName("jLabel3"); // NOI18N
        FormInput.add(jLabel3);
        jLabel3.setBounds(0, 10, 80, 23);

        jLabel13.setText("No Resep :");
        jLabel13.setName("jLabel13"); // NOI18N
        FormInput.add(jLabel13);
        jLabel13.setBounds(10, 130, 70, 23);

        btnDokterDPJP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnDokterDPJP.setMnemonic('3');
        btnDokterDPJP.setToolTipText("Alt+3");
        btnDokterDPJP.setName("btnDokterDPJP"); // NOI18N
        btnDokterDPJP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDokterDPJPActionPerformed(evt);
            }
        });
        btnDokterDPJP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnDokterDPJPKeyPressed(evt);
            }
        });
        FormInput.add(btnDokterDPJP);
        btnDokterDPJP.setBounds(332, 70, 28, 23);

        NmPasien.setEditable(false);
        NmPasien.setHighlighter(null);
        NmPasien.setName("NmPasien"); // NOI18N
        FormInput.add(NmPasien);
        NmPasien.setBounds(307, 10, 240, 23);

        jLabel5.setText("No SEP :");
        jLabel5.setName("jLabel5"); // NOI18N
        FormInput.add(jLabel5);
        jLabel5.setBounds(0, 40, 80, 23);

        NoSEP.setEditable(false);
        NoSEP.setHighlighter(null);
        NoSEP.setName("NoSEP"); // NOI18N
        NoSEP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoSEPKeyPressed(evt);
            }
        });
        FormInput.add(NoSEP);
        NoSEP.setBounds(83, 40, 270, 23);

        jLabel10.setText("No.SEP Apotek :");
        jLabel10.setName("jLabel10"); // NOI18N
        FormInput.add(jLabel10);
        jLabel10.setBounds(360, 40, 90, 23);

        NoSEPApotek.setEditable(false);
        NoSEPApotek.setHighlighter(null);
        NoSEPApotek.setName("NoSEPApotek"); // NOI18N
        FormInput.add(NoSEPApotek);
        NoSEPApotek.setBounds(458, 40, 280, 23);

        jLabel14.setText("Poliklinik :");
        jLabel14.setName("jLabel14"); // NOI18N
        FormInput.add(jLabel14);
        jLabel14.setBounds(360, 70, 90, 23);

        KdPoli.setEditable(false);
        KdPoli.setHighlighter(null);
        KdPoli.setName("KdPoli"); // NOI18N
        FormInput.add(KdPoli);
        KdPoli.setBounds(461, 70, 75, 23);

        NmPoli.setEditable(false);
        NmPoli.setHighlighter(null);
        NmPoli.setName("NmPoli"); // NOI18N
        FormInput.add(NmPoli);
        NmPoli.setBounds(538, 70, 170, 23);

        btnPoli.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnPoli.setMnemonic('3');
        btnPoli.setToolTipText("Alt+3");
        btnPoli.setName("btnPoli"); // NOI18N
        btnPoli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPoliActionPerformed(evt);
            }
        });
        btnPoli.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnPoliKeyPressed(evt);
            }
        });
        FormInput.add(btnPoli);
        btnPoli.setBounds(710, 70, 28, 23);

        jLabel11.setText("ID User :");
        jLabel11.setName("jLabel11"); // NOI18N
        FormInput.add(jLabel11);
        jLabel11.setBounds(580, 10, 60, 23);

        kd_user.setEditable(false);
        kd_user.setHighlighter(null);
        kd_user.setName("kd_user"); // NOI18N
        kd_user.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kd_userKeyPressed(evt);
            }
        });
        FormInput.add(kd_user);
        kd_user.setBounds(645, 10, 90, 23);

        jLabel15.setText("Dokter DPJP :");
        jLabel15.setName("jLabel15"); // NOI18N
        FormInput.add(jLabel15);
        jLabel15.setBounds(0, 70, 80, 23);

        NotaPiutang.setEditable(false);
        NotaPiutang.setName("NotaPiutang"); // NOI18N
        NotaPiutang.setPreferredSize(new java.awt.Dimension(578, 23));
        NotaPiutang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NotaPiutangKeyPressed(evt);
            }
        });
        FormInput.add(NotaPiutang);
        NotaPiutang.setBounds(85, 130, 160, 23);

        jLabel20.setText("Tanggal SEP :");
        jLabel20.setName("jLabel20"); // NOI18N
        jLabel20.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel20);
        jLabel20.setBounds(0, 100, 80, 23);

        Scroll2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Data Resep Dokter", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11))); // NOI18N
        Scroll2.setName("Scroll2"); // NOI18N

        tbObat.setName("tbObat"); // NOI18N
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbObatMouseClicked(evt);
            }
        });
        Scroll2.setViewportView(tbObat);

        FormInput.add(Scroll2);
        Scroll2.setBounds(750, 10, 800, 180);

        cmbJenisResep.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1. Obat PRB", "2. Obat Kronis Blm Stabil", "3. Obat Kemoterapi" }));
        cmbJenisResep.setLightWeightPopupEnabled(false);
        cmbJenisResep.setName("cmbJenisResep"); // NOI18N
        cmbJenisResep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbJenisResepActionPerformed(evt);
            }
        });
        cmbJenisResep.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbJenisResepKeyPressed(evt);
            }
        });
        FormInput.add(cmbJenisResep);
        cmbJenisResep.setBounds(85, 160, 180, 23);

        jLabel16.setText("Jenis Resep :");
        jLabel16.setName("jLabel16"); // NOI18N
        FormInput.add(jLabel16);
        jLabel16.setBounds(0, 160, 80, 20);

        jLabel17.setText("Iterasi :");
        jLabel17.setName("jLabel17"); // NOI18N
        FormInput.add(jLabel17);
        jLabel17.setBounds(270, 160, 50, 20);

        cmbIterasi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0. Non Iterasi", "1. Iterasi" }));
        cmbIterasi.setLightWeightPopupEnabled(false);
        cmbIterasi.setName("cmbIterasi"); // NOI18N
        cmbIterasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbIterasiActionPerformed(evt);
            }
        });
        cmbIterasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbIterasiKeyPressed(evt);
            }
        });
        FormInput.add(cmbIterasi);
        cmbIterasi.setBounds(320, 160, 180, 23);

        jLabel18.setText("Status :");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(500, 160, 50, 20);

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Non Piutang", "Piutang" }));
        cmbStatus.setLightWeightPopupEnabled(false);
        cmbStatus.setName("cmbStatus"); // NOI18N
        cmbStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbStatusActionPerformed(evt);
            }
        });
        cmbStatus.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbStatusKeyPressed(evt);
            }
        });
        FormInput.add(cmbStatus);
        cmbStatus.setBounds(550, 160, 190, 23);

        tglSEP.setEditable(false);
        tglSEP.setName("tglSEP"); // NOI18N
        tglSEP.setPreferredSize(new java.awt.Dimension(578, 23));
        tglSEP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tglSEPKeyPressed(evt);
            }
        });
        FormInput.add(tglSEP);
        tglSEP.setBounds(85, 100, 160, 23);

        tglResep.setEditable(false);
        tglResep.setName("tglResep"); // NOI18N
        tglResep.setPreferredSize(new java.awt.Dimension(578, 23));
        tglResep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tglResepActionPerformed(evt);
            }
        });
        tglResep.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tglResepKeyPressed(evt);
            }
        });
        FormInput.add(tglResep);
        tglResep.setBounds(325, 130, 160, 23);

        jLabel22.setText("Tgl Resep :");
        jLabel22.setName("jLabel22"); // NOI18N
        jLabel22.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel22);
        jLabel22.setBounds(240, 130, 80, 23);

        jLabel23.setText("Tgl Pelayanan :");
        jLabel23.setName("jLabel23"); // NOI18N
        jLabel23.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel23);
        jLabel23.setBounds(490, 130, 80, 23);

        tglPelResep.setEditable(false);
        tglPelResep.setName("tglPelResep"); // NOI18N
        tglPelResep.setPreferredSize(new java.awt.Dimension(578, 23));
        tglPelResep.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tglPelResepKeyPressed(evt);
            }
        });
        FormInput.add(tglPelResep);
        tglPelResep.setBounds(580, 130, 160, 23);

        kdPpkRujukan.setEditable(false);
        kdPpkRujukan.setName("kdPpkRujukan"); // NOI18N
        kdPpkRujukan.setPreferredSize(new java.awt.Dimension(578, 23));
        kdPpkRujukan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdPpkRujukanKeyPressed(evt);
            }
        });
        FormInput.add(kdPpkRujukan);
        kdPpkRujukan.setBounds(325, 100, 160, 23);

        jLabel24.setText("PPK Rujukan :");
        jLabel24.setName("jLabel24"); // NOI18N
        FormInput.add(jLabel24);
        jLabel24.setBounds(250, 100, 70, 23);

        jLabel25.setText("PPK Pelayanan :");
        jLabel25.setName("jLabel25"); // NOI18N
        FormInput.add(jLabel25);
        jLabel25.setBounds(490, 100, 80, 20);

        nmPpkPelayanan.setEditable(false);
        nmPpkPelayanan.setName("nmPpkPelayanan"); // NOI18N
        nmPpkPelayanan.setPreferredSize(new java.awt.Dimension(578, 23));
        nmPpkPelayanan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nmPpkPelayananActionPerformed(evt);
            }
        });
        nmPpkPelayanan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                nmPpkPelayananKeyPressed(evt);
            }
        });
        FormInput.add(nmPpkPelayanan);
        nmPpkPelayanan.setBounds(580, 100, 160, 23);

        PanelInput.add(FormInput, java.awt.BorderLayout.CENTER);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        panelCari.setBorder(null);
        panelCari.setName("panelCari"); // NOI18N
        panelCari.setPreferredSize(new java.awt.Dimension(44, 44));
        panelCari.setLayout(new java.awt.BorderLayout());

        ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setMnemonic('I');
        ChkInput.setText(".: Input Data");
        ChkInput.setToolTipText("Alt+I");
        ChkInput.setBorderPainted(true);
        ChkInput.setBorderPaintedFlat(true);
        ChkInput.setFocusable(false);
        ChkInput.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInput.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInput.setName("ChkInput"); // NOI18N
        ChkInput.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInput.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInputActionPerformed(evt);
            }
        });
        panelCari.add(ChkInput, java.awt.BorderLayout.PAGE_START);

        Scroll.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)), "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11))); // NOI18N
        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);
        Scroll.setPreferredSize(new java.awt.Dimension(452, 300));

        tbProgramPRB.setAutoCreateRowSorter(true);
        tbProgramPRB.setComponentPopupMenu(jPopupMenu1);
        tbProgramPRB.setName("tbProgramPRB"); // NOI18N
        tbProgramPRB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbProgramPRBMouseClicked(evt);
            }
        });
        tbProgramPRB.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbProgramPRBKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbProgramPRB);

        panelCari.add(Scroll, java.awt.BorderLayout.CENTER);

        internalFrame1.add(panelCari, java.awt.BorderLayout.CENTER);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if(tbProgramPRB.getSelectedRow()!= -1){
            try {
                bodyWithDeleteRequest();
            }catch (Exception ex) {
                System.out.println("Notifikasi Bridging : "+ex);
            }
        }else{
            JOptionPane.showMessageDialog(null,"Silahkan pilih dulu data yang mau dihapus..!!");
        }

}//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnHapusActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnCari,BtnPrint);
        }
}//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
}//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            dispose();
        }else{Valid.pindah(evt,BtnPrint,TCari);}
}//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        BtnCariActionPerformed(evt);
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            TCari.requestFocus();
        }else if(tabMode.getRowCount()!=0){
            Map<String, Object> param = new HashMap<>();  
                param.put("namars",akses.getnamars());
                param.put("alamatrs",akses.getalamatrs());
                param.put("kotars",akses.getkabupatenrs());
                param.put("propinsirs",akses.getpropinsirs());
                param.put("kontakrs",akses.getkontakrs());
                param.put("emailrs",akses.getemailrs());   
                param.put("logo",Sequel.cariGambar("select setting.logo from setting")); 
            Valid.MyReportqry("rptDataSRBBPJS.jasper","report","::[ Data Resep Pulang ]::",
                    "select bridging_sep.no_rawat,bridging_sep.nomr,bridging_sep.nama_pasien,bridging_srb_bpjs.alamat,"+
                    "bridging_srb_bpjs.email,bridging_sep.no_kartu,bridging_sep.no_sep,bridging_srb_bpjs.no_srb,bridging_srb_bpjs.tgl_srb,"+
                    "bridging_srb_bpjs.kodedpjp,bridging_srb_bpjs.nmdpjp,bridging_srb_bpjs.kodeprogram,bridging_srb_bpjs.namaprogram,"+
                    "bridging_srb_bpjs.keterangan,bridging_srb_bpjs.saran from bridging_sep inner join bridging_srb_bpjs "+
                    "on bridging_srb_bpjs.no_sep=bridging_sep.no_sep where bridging_srb_bpjs.tgl_srb between '"+Valid.SetTgl(DTPCari1.getSelectedItem()+"")+"' and '"+Valid.SetTgl(DTPCari2.getSelectedItem()+"")+"' "+
                    (TCari.getText().trim().equals("")?"":" and (bridging_sep.no_rawat like '%"+TCari.getText().trim()+"%' or bridging_sep.nomr like '%"+TCari.getText().trim()+"%' or "+
                    "bridging_sep.nama_pasien like '%"+TCari.getText().trim()+"%' or bridging_sep.no_kartu like '%"+TCari.getText().trim()+"%' or bridging_sep.no_sep like '%"+TCari.getText().trim()+"%' or "+
                    "bridging_srb_bpjs.no_srb like '%"+TCari.getText().trim()+"%' or bridging_srb_bpjs.kodedpjp like '%"+TCari.getText().trim()+"%' or bridging_srb_bpjs.nmdpjp like '%"+TCari.getText().trim()+"%' or "+
                    "bridging_srb_bpjs.kodeprogram like '%"+TCari.getText().trim()+"%' or bridging_srb_bpjs.namaprogram like '%"+TCari.getText().trim()+"%')")+"order by bridging_srb_bpjs.tgl_srb",param);
        }
        this.setCursor(Cursor.getDefaultCursor());
}//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnPrintActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnHapus, BtnKeluar);
        }
}//GEN-LAST:event_BtnPrintKeyPressed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
         if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            BtnCariActionPerformed(null);
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            BtnCari.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            BtnKeluar.requestFocus();
        }
}//GEN-LAST:event_TCariKeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        tampil();
}//GEN-LAST:event_BtnCariActionPerformed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnCariActionPerformed(null);
        }else{
            Valid.pindah(evt, TCari, BtnAll);
        }
}//GEN-LAST:event_BtnCariKeyPressed

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        tampil();
}//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            tampil();
            TCari.setText("");
        }else{
            Valid.pindah(evt, BtnCari, NmDPJP);
        }
}//GEN-LAST:event_BtnAllKeyPressed

private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInputActionPerformed
  isForm();                
}//GEN-LAST:event_ChkInputActionPerformed

    private void btnDokterDPJPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnDokterDPJPKeyPressed
        Valid.pindah(evt,NoSEPApotek,btnPoli);
    }//GEN-LAST:event_btnDokterDPJPKeyPressed

    private void btnDokterDPJPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDokterDPJPActionPerformed
        dokter.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        dokter.setLocationRelativeTo(internalFrame1);        
        dokter.setVisible(true);
    }//GEN-LAST:event_btnDokterDPJPActionPerformed

    private void NoSEPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoSEPKeyPressed
        Valid.pindah(evt, TCari,NoSEPApotek);
    }//GEN-LAST:event_NoSEPKeyPressed

    private void btnPoliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPoliActionPerformed
        poli.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        poli.setLocationRelativeTo(internalFrame1);        
        poli.setVisible(true);
    }//GEN-LAST:event_btnPoliActionPerformed

    private void btnPoliKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnPoliKeyPressed
        Valid.pindah(evt,btnDokterDPJP,btnPoli);
    }//GEN-LAST:event_btnPoliKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if(NoRawat.getText().trim().equals("")){
            Valid.textKosong(NoRawat,"Pasien");
        }else if(NotaPiutang.getText().trim().equals("")){
            Valid.textKosong(NotaPiutang,"No Resep");
        }else if(KdDPJP.getText().trim().equals("")||NmDPJP.getText().trim().equals("")){
            Valid.textKosong(btnDokterDPJP,"Dokter DPJP");
        }else if(KdPoli.getText().trim().equals("")||NmPoli.getText().trim().equals("")){
            Valid.textKosong(btnDokterDPJP,"Poliklinik");
        }else if(NoSEP.getText().trim().equals("")){
            Valid.textKosong(NoSEP,"NO SEP");
        }else{
            try{
                headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.add("x-cons-id",koneksiDB.CONSIDAPIAPOTEKBPJS());
                utc=String.valueOf(api.GetUTCdatetimeAsString());
                headers.add("x-timestamp",utc);
                headers.add("x-signature",api.getHmac(utc));
                headers.add("user_key",koneksiDB.USERKEYAPIAPOTEKBPJS());
                requestEntity = new HttpEntity(headers);
                URL = link+"/sjpresep/v3/insert";
                //System.out.println("URL : "+URL);
                requestJson = "{"
                        + "\"TGLSJP\":\"" + tglSEP.getText() + "\","
                        + "\"REFASALSJP\":\"" + NoSEP.getText() + "\","
                        + "\"POLIRSP\":\"" + KdPoli.getText() + "\","
                        + "\"KDJNSOBAT\":\"" + cmbJenisResep.getSelectedIndex()+ "\","
                        + "\"NORESEP\":\"" + NotaPiutang.getText().trim() + "\","
                        + "\"IDUSERSJP\":\"" + kd_user.getText() + "\","
                        + "\"TGLRSP\":\"" + tglResep.getText() + "\","
                        + "\"TGLPELRSP\":\"" + tglPelResep.getText() + "\","
                        + "\"KdDokter\":\"" + KdDPJP.getText() + "\","
                        + "\"iterasi\":\"" + cmbIterasi.getSelectedIndex() + "\""
                        + "}";
                System.out.println("JSON : "+requestJson);
                requestEntity = new HttpEntity(requestJson,headers);
                root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                nameNode = root.path("metaData");
                JOptionPane.showMessageDialog(null,nameNode.path("message").asText()); 
                System.out.println("RESPONSE APOTEK BPJS : " + nameNode.path("code").asText() + " " + nameNode.path("message").asText());
                if(nameNode.path("code").asText().equals("200")){
                    response = mapper.readTree(api.Decrypt(root.path("response").asText(),utc));
                        if(Sequel.menyimpantf("bridging_resep_apotek_bpjs","?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?","No.SEP Apotek",21,new String[]{
                         NoSEP.getText(),response.path("noApotik").asText(),tglSEP.getText(),KdPoli.getText(),NmPoli.getText(),cmbJenisResep.getSelectedItem().toString(),NotaPiutang.getText(),
                         kd_user.getText(),tglResep.getText(),tglPelResep.getText(),KdDPJP.getText(),NmDPJP.getText(),cmbIterasi.getSelectedItem().toString(),response.path("noKartu").asText(),NmPasien.getText(),
                         response.path("faskesAsal").asText(),nmPpkPelayanan.getText(),response.path("byTagRsp").asText(),response.path("byVerRsp").asText(),cmbStatus.getSelectedItem().toString(),
                         response.path("tglEntry").asText()
                        })==true){
                        emptTeks();
                        tampil();
                    }
                }  
            } catch (Exception ex) {
                System.out.println("Notifikasi : "+ex);
                if(ex.toString().contains("UnknownHostException")){
                    JOptionPane.showMessageDialog(rootPane,"Koneksi ke server BPJS terputus...!");
                }
            }
        }
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnSimpanActionPerformed(null);
        }else{
            Valid.pindah(evt,NotaPiutang,BtnBatal);
        }
    }//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        emptTeks();
        ChkInput.setSelected(true);
        isForm();
    }//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBatalKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnBatalActionPerformed(null);
        }else{Valid.pindah(evt, BtnSimpan, BtnHapus);}
    }//GEN-LAST:event_BtnBatalKeyPressed

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed

    }//GEN-LAST:event_BtnEditActionPerformed

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEditKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnEditActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnHapus, BtnPrint);
        }
    }//GEN-LAST:event_BtnEditKeyPressed

    private void kd_userKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kd_userKeyPressed
//        Valid.pindah(evt, Keterangan,Tanggal);
    }//GEN-LAST:event_kd_userKeyPressed

    private void NotaPiutangKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NotaPiutangKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kd_user.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_TAB){
            BtnSimpan.requestFocus();
        }
    }//GEN-LAST:event_NotaPiutangKeyPressed

    private void MnSuratActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnSuratActionPerformed

    }//GEN-LAST:event_MnSuratActionPerformed

    private void MnTampilkanObatPRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnTampilkanObatPRBActionPerformed
        Valid.tabelKosong(tabMode);
        try{     
            ps=koneksi.prepareStatement("select bridging_sep.no_rawat,bridging_sep.nomr,bridging_sep.nama_pasien,bridging_srb_bpjs.alamat,"+
                    "bridging_srb_bpjs.email,bridging_sep.no_kartu,bridging_sep.no_sep,bridging_srb_bpjs.no_srb,bridging_srb_bpjs.tgl_srb,"+
                    "bridging_srb_bpjs.kodedpjp,bridging_srb_bpjs.nmdpjp,bridging_srb_bpjs.kodeprogram,bridging_srb_bpjs.namaprogram,"+
                    "bridging_srb_bpjs.keterangan,bridging_srb_bpjs.saran from bridging_sep inner join bridging_srb_bpjs "+
                    "on bridging_srb_bpjs.no_sep=bridging_sep.no_sep where bridging_srb_bpjs.tgl_srb between ? and ? "+
                    (TCari.getText().trim().equals("")?"":" and (bridging_sep.no_rawat like ? or bridging_sep.nomr like ? or "+
                    "bridging_sep.nama_pasien like ? or bridging_sep.no_kartu like ? or bridging_sep.no_sep like ? or "+
                    "bridging_srb_bpjs.no_srb like ? or bridging_srb_bpjs.kodedpjp like ? or bridging_srb_bpjs.nmdpjp like ? or "+
                    "bridging_srb_bpjs.kodeprogram like ? or bridging_srb_bpjs.namaprogram like ?)")+"order by bridging_srb_bpjs.tgl_srb");
            try {
                ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+""));
                ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+""));
                if(!TCari.getText().equals("")){
                    ps.setString(3,"%"+TCari.getText().trim()+"%");
                    ps.setString(4,"%"+TCari.getText().trim()+"%");
                    ps.setString(5,"%"+TCari.getText().trim()+"%");
                    ps.setString(6,"%"+TCari.getText().trim()+"%");
                    ps.setString(7,"%"+TCari.getText().trim()+"%");
                    ps.setString(8,"%"+TCari.getText().trim()+"%");
                    ps.setString(9,"%"+TCari.getText().trim()+"%");
                    ps.setString(10,"%"+TCari.getText().trim()+"%");
                    ps.setString(11,"%"+TCari.getText().trim()+"%");
                    ps.setString(12,"%"+TCari.getText().trim()+"%");
                }
                rs=ps.executeQuery();
                while(rs.next()){
                    tabMode.addRow(new Object[]{
                        rs.getString("no_rawat"),rs.getString("nomr"),rs.getString("nama_pasien"),rs.getString("alamat"),
                        rs.getString("email"),rs.getString("no_kartu"),rs.getString("no_sep"),rs.getString("no_srb"),rs.getString("tgl_srb"),
                        rs.getString("kodedpjp"),rs.getString("nmdpjp"),rs.getString("kodeprogram"),rs.getString("namaprogram"),
                        rs.getString("keterangan"),rs.getString("saran")
                    });
                    try{     
                        ps2=koneksi.prepareStatement("select * from bridging_srb_bpjs_obat where no_sep=? and no_srb=?");
                        try {
                            ps2.setString(1,rs.getString("no_sep"));
                            ps2.setString(2,rs.getString("no_srb"));
                            rs2=ps2.executeQuery();
                            if(rs2.next()){
                                tabMode.addRow(new Object[]{
                                    "","Jumlah","Kode Barang","Nama Barag","Signa 1","Signa 2","","","","","","","","",""
                                });
                                rs2.beforeFirst();
                                while(rs2.next()){
                                    tabMode.addRow(new Object[]{
                                        "",rs2.getDouble("jumlah"),rs2.getString("kd_obat"),rs2.getString("nm_obat"),rs2.getString("signa1"),rs2.getString("signa2"),"","","","","","","","",""
                                    });
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Notif : "+e);
                        } finally{
                            if(rs2!=null){
                                rs2.close();
                            }
                            if(ps2!=null){
                                ps2.close();
                            }
                        }
                    }catch(Exception e){
                        System.out.println("Notifikasi : "+e);
                    } 
                }
                rs.last();
                LCount.setText(""+rs.getRow());
            } catch (Exception e) {
                System.out.println("Notif : "+e);
            } finally{
                if(rs!=null){
                    rs.close();
                }
                if(ps!=null){
                    ps.close();
                }
            }
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }     
    }//GEN-LAST:event_MnTampilkanObatPRBActionPerformed

    private void tbProgramPRBKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbProgramPRBKeyPressed
        if(tabMode.getRowCount()!=0){
            if((evt.getKeyCode()==KeyEvent.VK_ENTER)||(evt.getKeyCode()==KeyEvent.VK_UP)||(evt.getKeyCode()==KeyEvent.VK_DOWN)){
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
    }//GEN-LAST:event_tbProgramPRBKeyPressed

    private void tbProgramPRBMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbProgramPRBMouseClicked
        if(tabMode.getRowCount()!=0){
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }//GEN-LAST:event_tbProgramPRBMouseClicked

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbObatMouseClicked
          if(tabMode2.getRowCount()!=0){
            try {
                getDataResep();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }//GEN-LAST:event_tbObatMouseClicked

    private void cmbJenisResepKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbJenisResepKeyPressed
//        Valid.pindah(evt,BtnCacat,CmbStts);
    }//GEN-LAST:event_cmbJenisResepKeyPressed

    private void cmbJenisResepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbJenisResepActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbJenisResepActionPerformed

    private void cmbIterasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbIterasiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbIterasiActionPerformed

    private void cmbIterasiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbIterasiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbIterasiKeyPressed

    private void cmbStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbStatusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbStatusActionPerformed

    private void cmbStatusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbStatusKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbStatusKeyPressed

    private void tglSEPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tglSEPKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_tglSEPKeyPressed

    private void tglResepKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tglResepKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_tglResepKeyPressed

    private void tglPelResepKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tglPelResepKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_tglPelResepKeyPressed

    private void tglResepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tglResepActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tglResepActionPerformed

    private void kdPpkRujukanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kdPpkRujukanKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_kdPpkRujukanKeyPressed

    private void nmPpkPelayananKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nmPpkPelayananKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_nmPpkPelayananKeyPressed

    private void nmPpkPelayananActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nmPpkPelayananActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nmPpkPelayananActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            ApotekBPJSResepFKTL dialog = new ApotekBPJSResepFKTL(new javax.swing.JFrame(), true);
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
    private widget.Button BtnAll;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkInput;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.PanelBiasa FormInput;
    private widget.TextBox KdDPJP;
    private widget.TextBox KdPoli;
    private widget.Label LCount;
    private javax.swing.JMenuItem MnSurat;
    private javax.swing.JMenuItem MnTampilkanObatPRB;
    private widget.TextBox NmDPJP;
    private widget.TextBox NmPasien;
    private widget.TextBox NmPoli;
    private widget.TextBox NoRM;
    private widget.TextBox NoRawat;
    private widget.TextBox NoSEP;
    private widget.TextBox NoSEPApotek;
    private widget.TextBox NotaPiutang;
    private javax.swing.JPanel PanelInput;
    private widget.ScrollPane Scroll;
    private widget.ScrollPane Scroll2;
    private widget.TextBox TCari;
    private widget.Button btnDokterDPJP;
    private widget.Button btnPoli;
    private widget.ComboBox cmbIterasi;
    private widget.ComboBox cmbJenisResep;
    private widget.ComboBox cmbStatus;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel13;
    private widget.Label jLabel14;
    private widget.Label jLabel15;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private widget.Label jLabel18;
    private widget.Label jLabel19;
    private widget.Label jLabel20;
    private widget.Label jLabel21;
    private widget.Label jLabel22;
    private widget.Label jLabel23;
    private widget.Label jLabel24;
    private widget.Label jLabel25;
    private widget.Label jLabel3;
    private widget.Label jLabel5;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.TextBox kdPpkRujukan;
    private widget.TextBox kd_user;
    private widget.TextBox nmPpkPelayanan;
    private widget.panelisi panelCari;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Table tbObat;
    private widget.Table tbProgramPRB;
    private widget.TextBox tglPelResep;
    private widget.TextBox tglResep;
    private widget.TextBox tglSEP;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try{     
            ps=koneksi.prepareStatement("SELECT bridging_sep.no_rawat, bridging_sep.nomr, bridging_sep.nama_pasien, bridging_resep_apotek_bpjs.no_sep, \n" +
                    "bridging_resep_apotek_bpjs.no_sep_apotek, bridging_resep_apotek_bpjs.tgl_sep, bridging_resep_apotek_bpjs.kodedpjp, \n" +
                    "bridging_resep_apotek_bpjs.nmdpjp, bridging_resep_apotek_bpjs.kdpoli, bridging_resep_apotek_bpjs.nmpoli, bridging_resep_apotek_bpjs.nota_piutang, \n" +
                    "bridging_resep_apotek_bpjs.tgl_resep, bridging_resep_apotek_bpjs.tgl_pelayanan, bridging_resep_apotek_bpjs.kdjenis, bridging_resep_apotek_bpjs.iterasi, \n" +
                    "bridging_resep_apotek_bpjs.status, bridging_resep_apotek_bpjs.kdppkrujukan, bridging_resep_apotek_bpjs.nmppkpelayanan, bridging_resep_apotek_bpjs.byTagRsp, \n" +
                    "bridging_resep_apotek_bpjs.byVerRsp, bridging_resep_apotek_bpjs.id_user_sep, bridging_resep_apotek_bpjs.tgl_entry\n" +
                    "FROM bridging_resep_apotek_bpjs \n" +
                    "INNER JOIN bridging_sep ON bridging_sep.no_sep = bridging_resep_apotek_bpjs.no_sep\n" +
                    "WHERE bridging_resep_apotek_bpjs.tgl_entry between ? and ? "+
                    (TCari.getText().trim().equals("")?"":" and (bridging_sep.no_rawat like ? or bridging_sep.nomr like ? or "+
                    "bridging_sep.nama_pasien like ? or bridging_sep.no_kartu like ? or bridging_sep.no_sep like ? or "+
                    "bridging_resep_apotek_bpjs.no_sep_apotek like ? or bridging_resep_apotek_bpjs.nota_piutang like ? )")+"order by bridging_resep_apotek_bpjs.tgl_entry");
            try {
                ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+""));
                ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+""));
                if(!TCari.getText().equals("")){
                    ps.setString(3,"%"+TCari.getText().trim()+"%");
                    ps.setString(4,"%"+TCari.getText().trim()+"%");
                    ps.setString(5,"%"+TCari.getText().trim()+"%");
                    ps.setString(6,"%"+TCari.getText().trim()+"%");
                    ps.setString(7,"%"+TCari.getText().trim()+"%");
                    ps.setString(8,"%"+TCari.getText().trim()+"%");
                    ps.setString(9,"%"+TCari.getText().trim()+"%");
                }
                rs=ps.executeQuery();
                while(rs.next()){
                    tabMode.addRow(new Object[]{
                        rs.getString("no_rawat"),rs.getString("nomr"),rs.getString("nama_pasien"),rs.getString("no_sep"),
                        rs.getString("no_sep_apotek"),rs.getString("tgl_sep"),rs.getString("kodedpjp"),rs.getString("nmdpjp"),
                        rs.getString("kdpoli"),rs.getString("nmpoli"),rs.getString("nota_piutang"),rs.getString("tgl_resep"),
                        rs.getString("tgl_pelayanan"),rs.getString("kdjenis"),rs.getString("iterasi"),rs.getString("status"),
                        rs.getString("kdppkrujukan"),rs.getString("nmppkpelayanan"),rs.getString("byTagRsp"),rs.getString("byVerRsp"),
                        rs.getString("id_user_sep"),rs.getString("tgl_entry")
                    });
                }
            } catch (Exception e) {
                System.out.println("Notif : "+e);
            } finally{
                if(rs!=null){
                    rs.close();
                }
                if(ps!=null){
                    ps.close();
                }
            }
            LCount.setText(""+tabMode.getRowCount());
        }catch(SQLException e){
            System.out.println("Notifikasi : "+e);
        }        
    }
    
    
    private void getData() {
        if(tbProgramPRB.getSelectedRow()!= -1){
            NoRawat.setText(tbProgramPRB.getValueAt(tbProgramPRB.getSelectedRow(),0).toString()); 
            NoRM.setText(tbProgramPRB.getValueAt(tbProgramPRB.getSelectedRow(),1).toString()); 
            NmPasien.setText(tbProgramPRB.getValueAt(tbProgramPRB.getSelectedRow(),2).toString()); 
            NoSEP.setText(tbProgramPRB.getValueAt(tbProgramPRB.getSelectedRow(),3).toString());  
            NoSEPApotek.setText(tbProgramPRB.getValueAt(tbProgramPRB.getSelectedRow(),4).toString());   
            tglSEP.setText(tbProgramPRB.getValueAt(tbProgramPRB.getSelectedRow(),5).toString());   
            KdDPJP.setText(tbProgramPRB.getValueAt(tbProgramPRB.getSelectedRow(),6).toString());   
            NmDPJP.setText(tbProgramPRB.getValueAt(tbProgramPRB.getSelectedRow(),7).toString());  
            KdPoli.setText(tbProgramPRB.getValueAt(tbProgramPRB.getSelectedRow(),8).toString());  
            NmPoli.setText(tbProgramPRB.getValueAt(tbProgramPRB.getSelectedRow(),9).toString());  
            NotaPiutang.setText(tbProgramPRB.getValueAt(tbProgramPRB.getSelectedRow(),10).toString()); 
            tglResep.setText(tbProgramPRB.getValueAt(tbProgramPRB.getSelectedRow(),11).toString()); 
            tglPelResep.setText(tbProgramPRB.getValueAt(tbProgramPRB.getSelectedRow(),12).toString());
            cmbJenisResep.setSelectedItem(tbProgramPRB.getValueAt(tbProgramPRB.getSelectedRow(),13).toString());
            cmbIterasi.setSelectedItem(tbProgramPRB.getValueAt(tbProgramPRB.getSelectedRow(),14).toString());
            cmbStatus.setSelectedItem(tbProgramPRB.getValueAt(tbProgramPRB.getSelectedRow(),15).toString());
            kdPpkRujukan.setText(tbProgramPRB.getValueAt(tbProgramPRB.getSelectedRow(),16).toString()); 
            nmPpkPelayanan.setText(tbProgramPRB.getValueAt(tbProgramPRB.getSelectedRow(),17).toString());
            kd_user.setText(tbProgramPRB.getValueAt(tbProgramPRB.getSelectedRow(),20).toString());  
            Valid.tabelKosong(tabMode2);
        }
    } 
    
    
    private void getDataResep() {
        if(tbObat.getSelectedRow()!= -1){
//            NotaPiutang.setText(tbObat.getValueAt(tbObat.getSelectedRow(),0).toString()); 
            NotaPiutang.setText("12346");
            tglResep.setText(tbObat.getValueAt(tbObat.getSelectedRow(),1).toString()); 
            tglPelResep.setText(tbObat.getValueAt(tbObat.getSelectedRow(),2).toString()); 
        }
    }
    
    
   
    public void setNoRm(String norawat) {
        NoRawat.setText(norawat);
        ChkInput.setSelected(true);
//        kd_user.setText("RSPW"+akses.getkode());
        Valid.tabelKosong(tabMode2);
        try {
            ps = koneksi.prepareStatement("SELECT bridging_sep.no_sep, bridging_sep.no_rawat, bridging_sep.nomr, bridging_sep.nama_pasien,\n" +
                    "bridging_sep.kdppkrujukan, bridging_sep.nmppkpelayanan, bridging_sep.kddpjp, bridging_sep.nmdpdjp,\n" +
                    "bridging_sep.kdpolitujuan, bridging_sep.nmpolitujuan,bridging_sep.tglsep,bridging_sep.user, concat(bridging_sep.tglsep,' ', reg_periksa.jam_reg) as tgl_daftar \n" +
                    "FROM reg_periksa \n" +
                    "INNER JOIN bridging_sep ON bridging_sep.no_rawat = reg_periksa.no_rawat\n" +
                    "WHERE reg_periksa.no_rawat = ? ");
            try {
                ps.setString(1, NoRawat.getText());
                rs = ps.executeQuery();
                if(rs.next()) {
                   NoRM.setText(rs.getString("nomr"));
                   NmPasien.setText(rs.getString("nama_pasien"));
                   NoSEP.setText(rs.getString("no_sep"));
                   KdDPJP.setText(rs.getString("kddpjp"));
                   NmDPJP.setText(rs.getString("nmdpdjp"));
                   KdPoli.setText(rs.getString("kdpolitujuan"));
                   NmPoli.setText(rs.getString("nmpolitujuan"));
                   tglSEP.setText(rs.getString("tglsep"));
//                   tglSEP.setText(rs.getString("tgl_daftar"));
                   kdPpkRujukan.setText(rs.getString("kdppkrujukan"));
                   nmPpkPelayanan.setText(rs.getString("nmppkpelayanan"));
                   kd_user.setText(rs.getString("user"));
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
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
        
        
         try {
            psresep = koneksi.prepareStatement("SELECT resep_obat.no_resep, CONCAT(resep_obat.tgl_perawatan, ' ', resep_obat.jam) AS tgl_pelayanan,\n" +
                    "CONCAT(resep_obat.tgl_peresepan, ' ', resep_obat.jam_peresepan) AS tgl_resep, resep_obat.status,\n" +
                    "CASE WHEN bridging_resep_apotek_bpjs.nota_piutang IS NULL THEN 'Belum' ELSE 'Sudah'\n" +
                    "END AS status_kirim\n" +
                    "FROM resep_obat\n" +
                    "INNER JOIN detail_pemberian_obat ON detail_pemberian_obat.no_rawat = resep_obat.no_rawat AND \n" +
                    "CONCAT(detail_pemberian_obat.tgl_perawatan, ' ', detail_pemberian_obat.jam) = CONCAT(resep_obat.tgl_perawatan, ' ', resep_obat.jam)\n" +
                    "INNER JOIN maping_obat_apotek_bpjs ON maping_obat_apotek_bpjs.kode_brng = detail_pemberian_obat.kode_brng\n" +
                    "LEFT JOIN bridging_resep_apotek_bpjs ON bridging_resep_apotek_bpjs.nota_piutang = resep_obat.no_resep\n" +
                    "WHERE resep_obat.no_rawat = ? ");
            try {
                psresep.setString(1, NoRawat.getText());
                rsresep = psresep.executeQuery();
                while(rsresep.next()) {
                    tabMode2.addRow(new Object[]{
                        rsresep.getString("no_resep"), rsresep.getString("tgl_resep"), rsresep.getString("tgl_pelayanan"), rsresep.getString("status"), rsresep.getString("status_kirim")
                    });
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            } finally {
                if (rsresep != null) {
                    rsresep.close();
                }
                if (psresep != null) {
                    psresep.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
        
        isForm();
        tampil();
}
    
    
    private void isForm(){
        if(ChkInput.isSelected()==true){
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH,266));
            FormInput.setVisible(true);      
            ChkInput.setVisible(true);
        }else if(ChkInput.isSelected()==false){           
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH,0));
            FormInput.setVisible(true);      
            ChkInput.setVisible(true);
        }
    }
    
    public void isCek(){
        btnDokterDPJP.setEnabled(akses.getbpjs_program_prb());
        BtnHapus.setEnabled(akses.getbpjs_program_prb());
        BtnPrint.setEnabled(akses.getbpjs_program_prb());
    }

   

    private void emptTeks() {
        KdPoli.setText("");
        NmPoli.setText("");
        kd_user.setText("");
        NotaPiutang.setText("");
        Valid.tabelKosong(tabMode2);
        NoSEP.requestFocus();
    }
    
    @Test
    public void bodyWithDeleteRequest() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        SSLContext sslContext = SSLContext.getInstance("SSL");
        javax.net.ssl.TrustManager[] trustManagers= {
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {return null;}
                public void checkServerTrusted(X509Certificate[] arg0, String arg1)throws CertificateException {}
                public void checkClientTrusted(X509Certificate[] arg0, String arg1)throws CertificateException {}
            }
        };
        sslContext.init(null,trustManagers , new SecureRandom());
        SSLSocketFactory sslFactory=new SSLSocketFactory(sslContext,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        Scheme scheme=new Scheme("https",443,sslFactory);
    
        HttpComponentsClientHttpRequestFactory factory=new HttpComponentsClientHttpRequestFactory(){
            @Override
            protected HttpUriRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
                if (HttpMethod.DELETE == httpMethod) {
                    return new BPJSSuratKontrol.HttpEntityEnclosingDeleteRequest(uri);
                }
                return super.createHttpUriRequest(httpMethod, uri);
            }
        };
        factory.getHttpClient().getConnectionManager().getSchemeRegistry().register(scheme);
        restTemplate.setRequestFactory(factory);
        
        try {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("x-cons-id", koneksiDB.CONSIDAPIAPOTEKBPJS());
            utc = String.valueOf(api.GetUTCdatetimeAsString());
            headers.add("x-timestamp", utc);
            headers.add("x-signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIAPOTEKBPJS());
            requestEntity = new HttpEntity(headers);
            URL = link+"/hapusresep";
            requestJson = "{"
                        + "\"nosjp\":\"" + tbProgramPRB.getValueAt(tbProgramPRB.getSelectedRow(),3).toString() + "\","
                        + "\"refasalsjp\":\"" + tbProgramPRB.getValueAt(tbProgramPRB.getSelectedRow(),4).toString() + "\","
                        + "\"noresep\":\"" + tbProgramPRB.getValueAt(tbProgramPRB.getSelectedRow(),10).toString() + "\""
                        + "}";
            requestEntity = new HttpEntity(requestJson,headers);
            root = mapper.readTree(restTemplate.exchange(URL, HttpMethod.DELETE,requestEntity, String.class).getBody());
            nameNode = root.path("metaData");
            System.out.println("code : "+nameNode.path("code").asText());
            System.out.println("message : "+nameNode.path("message").asText());
            JOptionPane.showMessageDialog(null,nameNode.path("message").asText());
            if(nameNode.path("code").asText().equals("200")){
                Sequel.meghapus("bridging_resep_apotek_bpjs","no_sep","no_sep_apotek",tbProgramPRB.getValueAt(tbProgramPRB.getSelectedRow(),4).toString(),tbProgramPRB.getValueAt(tbProgramPRB.getSelectedRow(),3).toString());
                tampil();
            }
        } catch (Exception e) {   
            System.out.println("Notif : "+e);
            if(e.toString().contains("UnknownHostException")){
                JOptionPane.showMessageDialog(null,"Koneksi ke server BPJS terputus...!");
            }
        }
    }
    
}
