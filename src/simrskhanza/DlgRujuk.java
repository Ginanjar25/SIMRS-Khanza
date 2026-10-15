/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DlgRujuk.java
 *
 * Created on 31 Mei 10, 20:19:56
 */

package simrskhanza;

import bridging.BPJSCekReferensiFaskes;
import kepegawaian.DlgCariDokter;
import kepegawaian.DlgCariPegawai;
import kepegawaian.DlgCariPetugas;
import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import javax.swing.Timer;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import laporan.DlgCariPenyakit;


/**
 *
 * @author perpustakaan
 */
public final class DlgRujuk extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private Connection koneksi=koneksiDB.condb();
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private String diagnosa="",diagnosa2="",keluar="",status="",tgl="",sql="";
    private PreparedStatement psobat,ps;
    private ResultSet rs;
    private int i=0;
    private DlgCariPenyakit penyakit=new DlgCariPenyakit(null,false);
    private BPJSCekReferensiFaskes faskes=new BPJSCekReferensiFaskes(null,false);
    /** Creates new form DlgRujuk
     * @param parent
     * @param modal */
    public DlgRujuk(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8,1);
        setSize(628,674);


        Object[] row={
            "No.Rujuk","No.Rawat","No.R.M.","Nama Pasien","Tempat Rujuk","Tgl.Rujuk","Jam Rujuk",
            "Keterangan Diagnosa","Kode Dokter","Dokter Perujuk","Kategori Rujuk","Ambulance","Keterangan",
            "Kd.Dokter Pendamping","Dokter Pendamping","NIP Perawat Pendamping","Perawat Pendamping",
            "NIP Driver","Driver Ambulance","Jarak"
        };
        tabMode=new DefaultTableModel(null,row){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        tbObat.setModel(tabMode);

        //tbObat.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbObat.getBackground()));
        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 20; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(75);
            }else if(i==1){
                column.setPreferredWidth(105);
            }else if(i==2){
                column.setPreferredWidth(70);
            }else if(i==3){
                column.setPreferredWidth(150);
            }else if(i==4){
                column.setPreferredWidth(150);
            }else if(i==5){
                column.setPreferredWidth(75);
            }else if(i==6){
                column.setPreferredWidth(65);
            }else if(i==7){
                column.setPreferredWidth(120);
            }else if(i==8){
                column.setMinWidth(0);
                column.setMaxWidth(0);
            }else if(i==9){
                column.setPreferredWidth(150);
            }else if(i==10){
                column.setPreferredWidth(90);
            }else if(i==11){
                column.setPreferredWidth(90);
            }else if(i==12){
                column.setPreferredWidth(150);
            }else if(i==13||i==15||i==17){
                column.setMinWidth(0);
                column.setMaxWidth(0);
            }else if(i==14||i==16||i==18){
                column.setPreferredWidth(150);
            }else if(i==19){
                column.setPreferredWidth(70);
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRj.setDocument(new batasInput((byte)10).getKata(TNoRj));
        TTmpRujuk.setDocument(new batasInput((byte)45).getKata(TTmpRujuk));
        TNoRw.setDocument(new batasInput((byte)17).getKata(TNoRw));
        TDiagnosa.setDocument(new batasInput((int)5000).getKata(TDiagnosa));
        ket.setDocument(new batasInput((int)5000).getKata(ket));
        TCari.setDocument(new batasInput((byte)100).getKata(TCari));
        KdDok.setDocument(new batasInput((byte)20).getKata(KdDok));
        KdDokPendamping.setDocument(new batasInput((byte)20).getKata(KdDokPendamping));
        KdPerawatPendamping.setDocument(new batasInput((byte)20).getKata(KdPerawatPendamping));
        KdDriver.setDocument(new batasInput((byte)20).getKata(KdDriver));
        TJarak.setDocument(new batasInput((byte)20).getKata(TJarak));
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
        
        jam();
        
        dokter.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(dokter.getTable().getSelectedRow()!= -1){
                    KdDok.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),0).toString());
                    TDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),1).toString());
                }   
                KdDok.requestFocus();
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
        
        penyakit.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if( penyakit.getTable().getSelectedRow()!= -1){                   
                    TDiagnosa.setText(penyakit.getTable().getValueAt(penyakit.getTable().getSelectedRow(),0).toString()+" - "+penyakit.getTable().getValueAt(penyakit.getTable().getSelectedRow(),1).toString());
                }  
                TDiagnosa.requestFocus();
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
        
        faskes.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(faskes.getTable().getSelectedRow()!= -1){  
                    TTmpRujuk.setText(faskes.getTable().getValueAt(faskes.getTable().getSelectedRow(),1).toString());
                    TTmpRujuk.setText(faskes.getTable().getValueAt(faskes.getTable().getSelectedRow(),2).toString());
                    TTmpRujuk.requestFocus();
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
        
        faskes.getTable().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_SPACE){
                    faskes.dispose();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        
        dokterpendamping.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(dokterpendamping.getTable().getSelectedRow()!= -1){
                    KdDokPendamping.setText(dokterpendamping.getTable().getValueAt(dokterpendamping.getTable().getSelectedRow(),0).toString());
                    TDokterPendamping.setText(dokterpendamping.getTable().getValueAt(dokterpendamping.getTable().getSelectedRow(),1).toString());
                }
                KdDokPendamping.requestFocus();
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

        perawat.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(perawat.getTable().getSelectedRow()!= -1){
                    KdPerawatPendamping.setText(perawat.getTable().getValueAt(perawat.getTable().getSelectedRow(),0).toString());
                    TPerawatPendamping.setText(perawat.getTable().getValueAt(perawat.getTable().getSelectedRow(),1).toString());
                }
                KdPerawatPendamping.requestFocus();
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

        driver.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(driver.getTable().getSelectedRow()!= -1){
                    KdDriver.setText(driver.getTable().getValueAt(driver.getTable().getSelectedRow(),0).toString());
                    TDriver.setText(driver.getTable().getValueAt(driver.getTable().getSelectedRow(),1).toString());
                }
                KdDriver.requestFocus();
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

        ChkInput.setSelected(false);
        isForm();
    }

    private DlgCariDokter dokter=new DlgCariDokter(null,false);
    private DlgCariDokter dokterpendamping=new DlgCariDokter(null,false);
    private DlgCariPetugas perawat=new DlgCariPetugas(null,false);
    private DlgCariPegawai driver=new DlgCariPegawai(null,false);

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        MnSuratRujukan = new javax.swing.JMenuItem();
        MnSuratRujukanAmbulance = new javax.swing.JMenuItem();
        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        BtnPrint = new widget.Button();
        BtnAll = new widget.Button();
        BtnKeluar = new widget.Button();
        panelGlass9 = new widget.panelisi();
        jLabel19 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel21 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        PanelInput = new javax.swing.JPanel();
        FormInput = new widget.PanelBiasa();
        jLabel3 = new widget.Label();
        TNoRj = new widget.TextBox();
        jLabel8 = new widget.Label();
        TTmpRujuk = new widget.TextBox();
        jLabel4 = new widget.Label();
        TNoRw = new widget.TextBox();
        TDiagnosa = new widget.TextBox();
        jLabel9 = new widget.Label();
        TPasien = new widget.TextBox();
        DTPRujuk = new widget.Tanggal();
        jLabel12 = new widget.Label();
        TNoRM = new widget.TextBox();
        jLabel5 = new widget.Label();
        KdDok = new widget.TextBox();
        btnDokter = new widget.Button();
        TDokter = new widget.TextBox();
        jLabel13 = new widget.Label();
        jLabel14 = new widget.Label();
        jLabel15 = new widget.Label();
        ket = new widget.TextBox();
        jLabel11 = new widget.Label();
        CmbJam = new widget.ComboBox();
        CmbMenit = new widget.ComboBox();
        ambulance = new widget.ComboBox();
        ChkJln = new widget.CekBox();
        CmbDetik = new widget.ComboBox();
        ktrujuk = new widget.ComboBox();
        btnDiagnosa = new widget.Button();
        btnPPKRujukan1 = new widget.Button();
        TJarak = new widget.TextBox();
        KdDokPendamping = new widget.TextBox();
        TDokterPendamping = new widget.TextBox();
        btnDokterPendamping = new widget.Button();
        jLabel10 = new widget.Label();
        jLabel16 = new widget.Label();
        KdPerawatPendamping = new widget.TextBox();
        TPerawatPendamping = new widget.TextBox();
        btnPerawatPendamping = new widget.Button();
        jLabel17 = new widget.Label();
        jLabel18 = new widget.Label();
        KdDriver = new widget.TextBox();
        TDriver = new widget.TextBox();
        btnDriver = new widget.Button();
        jLabel20 = new widget.Label();
        ChkInput = new widget.CekBox();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnSuratRujukan.setBackground(new java.awt.Color(255, 255, 254));
        MnSuratRujukan.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnSuratRujukan.setForeground(new java.awt.Color(50, 50, 50));
        MnSuratRujukan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnSuratRujukan.setText("Surat Rujukan");
        MnSuratRujukan.setName("MnSuratRujukan"); // NOI18N
        MnSuratRujukan.setPreferredSize(new java.awt.Dimension(250, 26));
        MnSuratRujukan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnSuratRujukanActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnSuratRujukan);

        MnSuratRujukanAmbulance.setBackground(new java.awt.Color(255, 255, 254));
        MnSuratRujukanAmbulance.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnSuratRujukanAmbulance.setForeground(new java.awt.Color(50, 50, 50));
        MnSuratRujukanAmbulance.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnSuratRujukanAmbulance.setText("Surat Rujukan & Layanan Ambulance");
        MnSuratRujukanAmbulance.setName("MnSuratRujukanAmbulance"); // NOI18N
        MnSuratRujukanAmbulance.setPreferredSize(new java.awt.Dimension(250, 26));
        MnSuratRujukanAmbulance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnSuratRujukanAmbulanceActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnSuratRujukanAmbulance);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Data Rujukan Keluar ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);
        Scroll.setPreferredSize(new java.awt.Dimension(452, 200));

        tbObat.setAutoCreateRowSorter(true);
        tbObat.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbObat.setComponentPopupMenu(jPopupMenu1);
        tbObat.setName("tbObat"); // NOI18N
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbObatMouseClicked(evt);
            }
        });
        tbObat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbObatKeyReleased(evt);
            }
        });
        Scroll.setViewportView(tbObat);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

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

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('M');
        BtnAll.setText("Semua");
        BtnAll.setToolTipText("Alt+M");
        BtnAll.setName("BtnAll"); // NOI18N
        BtnAll.setPreferredSize(new java.awt.Dimension(100, 30));
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
        panelGlass8.add(BtnAll);

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

        jLabel19.setText("Tgl.Rujuk :");
        jLabel19.setName("jLabel19"); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(67, 23));
        panelGlass9.add(jLabel19);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "14-10-2026" }));
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
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "14-10-2026" }));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2"); // NOI18N
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari2);

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(200, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass9.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('3');
        BtnCari.setToolTipText("Alt+3");
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

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(65, 23));
        panelGlass9.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(50, 23));
        panelGlass9.add(LCount);

        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setOpaque(false);
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(100, 165));
        FormInput.setLayout(null);

        jLabel3.setText("No.Rujuk :");
        jLabel3.setName("jLabel3"); // NOI18N
        FormInput.add(jLabel3);
        jLabel3.setBounds(0, 40, 72, 23);

        TNoRj.setHighlighter(null);
        TNoRj.setName("TNoRj"); // NOI18N
        TNoRj.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRjKeyPressed(evt);
            }
        });
        FormInput.add(TNoRj);
        TNoRj.setBounds(75, 40, 140, 23);

        jLabel8.setText("Tanggal :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(237, 40, 60, 23);

        TTmpRujuk.setEditable(false);
        TTmpRujuk.setHighlighter(null);
        TTmpRujuk.setName("TTmpRujuk"); // NOI18N
        TTmpRujuk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TTmpRujukKeyPressed(evt);
            }
        });
        FormInput.add(TTmpRujuk);
        TTmpRujuk.setBounds(75, 70, 280, 23);

        jLabel4.setText("No.Rawat :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(0, 10, 72, 23);

        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw"); // NOI18N
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });
        FormInput.add(TNoRw);
        TNoRw.setBounds(75, 10, 141, 23);

        TDiagnosa.setHighlighter(null);
        TDiagnosa.setName("TDiagnosa"); // NOI18N
        TDiagnosa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TDiagnosaKeyPressed(evt);
            }
        });
        FormInput.add(TDiagnosa);
        TDiagnosa.setBounds(75, 100, 282, 23);

        jLabel9.setText("Diagnosa :");
        jLabel9.setName("jLabel9"); // NOI18N
        FormInput.add(jLabel9);
        jLabel9.setBounds(0, 100, 72, 23);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N
        TPasien.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TPasienKeyPressed(evt);
            }
        });
        FormInput.add(TPasien);
        TPasien.setBounds(340, 10, 340, 23);

        DTPRujuk.setForeground(new java.awt.Color(50, 70, 50));
        DTPRujuk.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "14-10-2026" }));
        DTPRujuk.setDisplayFormat("dd-MM-yyyy");
        DTPRujuk.setName("DTPRujuk"); // NOI18N
        DTPRujuk.setOpaque(false);
        DTPRujuk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DTPRujukKeyPressed(evt);
            }
        });
        FormInput.add(DTPRujuk);
        DTPRujuk.setBounds(300, 40, 100, 23);

        jLabel12.setText("Rujuk Ke :");
        jLabel12.setName("jLabel12"); // NOI18N
        FormInput.add(jLabel12);
        jLabel12.setBounds(0, 70, 72, 23);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM"); // NOI18N
        TNoRM.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRMKeyPressed(evt);
            }
        });
        FormInput.add(TNoRM);
        TNoRM.setBounds(218, 10, 120, 23);

        jLabel5.setText("Km");
        jLabel5.setName("jLabel5"); // NOI18N
        FormInput.add(jLabel5);
        jLabel5.setBounds(890, 130, 20, 23);

        KdDok.setHighlighter(null);
        KdDok.setName("KdDok"); // NOI18N
        KdDok.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdDokKeyPressed(evt);
            }
        });
        FormInput.add(KdDok);
        KdDok.setBounds(75, 130, 100, 23);

        btnDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnDokter.setMnemonic('2');
        btnDokter.setToolTipText("Alt+2");
        btnDokter.setName("btnDokter"); // NOI18N
        btnDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDokterActionPerformed(evt);
            }
        });
        FormInput.add(btnDokter);
        btnDokter.setBounds(359, 130, 28, 23);

        TDokter.setEditable(false);
        TDokter.setHighlighter(null);
        TDokter.setName("TDokter"); // NOI18N
        TDokter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TDokterKeyPressed(evt);
            }
        });
        FormInput.add(TDokter);
        TDokter.setBounds(177, 130, 180, 23);

        jLabel13.setText(" Kategori Rujuk :");
        jLabel13.setName("jLabel13"); // NOI18N
        FormInput.add(jLabel13);
        jLabel13.setBounds(416, 100, 90, 23);

        jLabel14.setText(" Ambulance :");
        jLabel14.setName("jLabel14"); // NOI18N
        FormInput.add(jLabel14);
        jLabel14.setBounds(416, 130, 90, 23);

        jLabel15.setText(" Keterangan :");
        jLabel15.setName("jLabel15"); // NOI18N
        FormInput.add(jLabel15);
        jLabel15.setBounds(416, 70, 90, 23);

        ket.setHighlighter(null);
        ket.setName("ket"); // NOI18N
        ket.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ketKeyPressed(evt);
            }
        });
        FormInput.add(ket);
        ket.setBounds(510, 70, 170, 23);

        jLabel11.setText("Jam :");
        jLabel11.setName("jLabel11"); // NOI18N
        FormInput.add(jLabel11);
        jLabel11.setBounds(417, 40, 40, 23);

        CmbJam.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        CmbJam.setName("CmbJam"); // NOI18N
        CmbJam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CmbJamKeyPressed(evt);
            }
        });
        FormInput.add(CmbJam);
        CmbJam.setBounds(460, 40, 62, 23);

        CmbMenit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        CmbMenit.setName("CmbMenit"); // NOI18N
        CmbMenit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CmbMenitKeyPressed(evt);
            }
        });
        FormInput.add(CmbMenit);
        CmbMenit.setBounds(526, 40, 62, 23);

        ambulance.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "AG 8997 RQ" }));
        ambulance.setName("ambulance"); // NOI18N
        ambulance.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ambulanceKeyPressed(evt);
            }
        });
        FormInput.add(ambulance);
        ambulance.setBounds(510, 130, 170, 23);

        ChkJln.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(195, 215, 195)));
        ChkJln.setSelected(true);
        ChkJln.setBorderPainted(true);
        ChkJln.setBorderPaintedFlat(true);
        ChkJln.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkJln.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkJln.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkJln.setName("ChkJln"); // NOI18N
        FormInput.add(ChkJln);
        ChkJln.setBounds(657, 40, 23, 23);

        CmbDetik.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        CmbDetik.setName("CmbDetik"); // NOI18N
        CmbDetik.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CmbDetikKeyPressed(evt);
            }
        });
        FormInput.add(CmbDetik);
        CmbDetik.setBounds(592, 40, 62, 23);

        ktrujuk.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-", "Bedah", "Non Bedah", "Kebidanan", "Anak" }));
        ktrujuk.setName("ktrujuk"); // NOI18N
        ktrujuk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ktrujukKeyPressed(evt);
            }
        });
        FormInput.add(ktrujuk);
        ktrujuk.setBounds(510, 100, 170, 23);

        btnDiagnosa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnDiagnosa.setMnemonic('3');
        btnDiagnosa.setToolTipText("Alt+3");
        btnDiagnosa.setName("btnDiagnosa"); // NOI18N
        btnDiagnosa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDiagnosaActionPerformed(evt);
            }
        });
        btnDiagnosa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnDiagnosaKeyPressed(evt);
            }
        });
        FormInput.add(btnDiagnosa);
        btnDiagnosa.setBounds(359, 100, 28, 23);

        btnPPKRujukan1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnPPKRujukan1.setMnemonic('X');
        btnPPKRujukan1.setToolTipText("Alt+X");
        btnPPKRujukan1.setName("btnPPKRujukan1"); // NOI18N
        btnPPKRujukan1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPPKRujukan1ActionPerformed(evt);
            }
        });
        btnPPKRujukan1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnPPKRujukan1KeyPressed(evt);
            }
        });
        FormInput.add(btnPPKRujukan1);
        btnPPKRujukan1.setBounds(360, 70, 28, 23);

        TJarak.setHighlighter(null);
        TJarak.setName("TJarak"); // NOI18N
        TJarak.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TJarakKeyPressed(evt);
            }
        });
        FormInput.add(TJarak);
        TJarak.setBounds(810, 130, 80, 23);

        KdDokPendamping.setHighlighter(null);
        KdDokPendamping.setName("KdDokPendamping"); // NOI18N
        KdDokPendamping.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdDokPendampingKeyPressed(evt);
            }
        });
        FormInput.add(KdDokPendamping);
        KdDokPendamping.setBounds(810, 40, 60, 23);

        TDokterPendamping.setEditable(false);
        TDokterPendamping.setHighlighter(null);
        TDokterPendamping.setName("TDokterPendamping"); // NOI18N
        TDokterPendamping.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TDokterPendampingKeyPressed(evt);
            }
        });
        FormInput.add(TDokterPendamping);
        TDokterPendamping.setBounds(880, 40, 150, 23);

        btnDokterPendamping.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnDokterPendamping.setMnemonic('2');
        btnDokterPendamping.setToolTipText("Alt+2");
        btnDokterPendamping.setName("btnDokterPendamping"); // NOI18N
        btnDokterPendamping.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDokterPendampingActionPerformed(evt);
            }
        });
        FormInput.add(btnDokterPendamping);
        btnDokterPendamping.setBounds(1030, 40, 28, 23);

        jLabel10.setText("Dokter :");
        jLabel10.setName("jLabel10"); // NOI18N
        FormInput.add(jLabel10);
        jLabel10.setBounds(0, 130, 72, 23);

        jLabel16.setText("Perawat Pendamping :");
        jLabel16.setName("jLabel16"); // NOI18N
        FormInput.add(jLabel16);
        jLabel16.setBounds(690, 70, 110, 23);

        KdPerawatPendamping.setHighlighter(null);
        KdPerawatPendamping.setName("KdPerawatPendamping"); // NOI18N
        KdPerawatPendamping.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdPerawatPendampingKeyPressed(evt);
            }
        });
        FormInput.add(KdPerawatPendamping);
        KdPerawatPendamping.setBounds(810, 70, 60, 23);

        TPerawatPendamping.setEditable(false);
        TPerawatPendamping.setHighlighter(null);
        TPerawatPendamping.setName("TPerawatPendamping"); // NOI18N
        TPerawatPendamping.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TPerawatPendampingKeyPressed(evt);
            }
        });
        FormInput.add(TPerawatPendamping);
        TPerawatPendamping.setBounds(880, 70, 150, 23);

        btnPerawatPendamping.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnPerawatPendamping.setMnemonic('2');
        btnPerawatPendamping.setToolTipText("Alt+2");
        btnPerawatPendamping.setName("btnPerawatPendamping"); // NOI18N
        btnPerawatPendamping.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPerawatPendampingActionPerformed(evt);
            }
        });
        FormInput.add(btnPerawatPendamping);
        btnPerawatPendamping.setBounds(1030, 70, 28, 23);

        jLabel17.setText("Dokter Pendamping :");
        jLabel17.setName("jLabel17"); // NOI18N
        FormInput.add(jLabel17);
        jLabel17.setBounds(690, 40, 110, 23);

        jLabel18.setText("Driver :");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(690, 100, 110, 23);

        KdDriver.setHighlighter(null);
        KdDriver.setName("KdDriver"); // NOI18N
        FormInput.add(KdDriver);
        KdDriver.setBounds(810, 100, 60, 23);

        TDriver.setEditable(false);
        TDriver.setHighlighter(null);
        TDriver.setName("TDriver"); // NOI18N
        TDriver.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TDriverKeyPressed(evt);
            }
        });
        FormInput.add(TDriver);
        TDriver.setBounds(880, 100, 150, 23);

        btnDriver.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnDriver.setMnemonic('2');
        btnDriver.setToolTipText("Alt+2");
        btnDriver.setName("btnDriver"); // NOI18N
        btnDriver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDriverActionPerformed(evt);
            }
        });
        FormInput.add(btnDriver);
        btnDriver.setBounds(1030, 100, 28, 23);

        jLabel20.setText("Jarak :");
        jLabel20.setName("jLabel20"); // NOI18N
        FormInput.add(jLabel20);
        jLabel20.setBounds(760, 130, 40, 23);

        PanelInput.add(FormInput, java.awt.BorderLayout.CENTER);

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
        PanelInput.add(ChkInput, java.awt.BorderLayout.PAGE_END);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TNoRjKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRjKeyPressed
        Valid.pindah(evt,TCari,DTPRujuk);
}//GEN-LAST:event_TNoRjKeyPressed

    private void TTmpRujukKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TTmpRujukKeyPressed
        Valid.pindah(evt,CmbDetik,ket);
}//GEN-LAST:event_TTmpRujukKeyPressed

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRwKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            isRawat();
            isPsien();
        }else{            
            Valid.pindah(evt,TTmpRujuk,TDiagnosa);
        }
}//GEN-LAST:event_TNoRwKeyPressed

    private void TDiagnosaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TDiagnosaKeyPressed
        Valid.pindah(evt,ket,ktrujuk);
}//GEN-LAST:event_TDiagnosaKeyPressed

    private void TPasienKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TPasienKeyPressed
        Valid.pindah(evt,TDokter,BtnSimpan);
}//GEN-LAST:event_TPasienKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if(TNoRj.getText().trim().equals("")){
            Valid.textKosong(TNoRj,"No.Rujuk");
        }else if(TTmpRujuk.getText().trim().equals("")){
            Valid.textKosong(TTmpRujuk,"tempat rujuk");
        }else if(TNoRw.getText().trim().equals("")||TPasien.getText().trim().equals("")){
            Valid.textKosong(TNoRw,"pasien");
        }else if(TDokter.getText().trim().equals("")){
            Valid.textKosong(KdDok,"dokter yang bertugas");
        }else{
            // field pendamping, driver & jarak yang tidak diisi disimpan sebagai "-" agar kolom di database tidak null
            isiStrip(KdDokPendamping);
            isiStrip(KdPerawatPendamping);
            isiStrip(KdDriver);
            isiStrip(TJarak);
            if(Sequel.menyimpantf("rujuk","?,?,?,?,?,?,?,?,?,?,?,?,?,?","No.Rujuk",14,new String[]{
                    TNoRj.getText(),TNoRw.getText(),TTmpRujuk.getText(),Valid.SetTgl(DTPRujuk.getSelectedItem()+""),
                    TDiagnosa.getText(),KdDok.getText(),ktrujuk.getSelectedItem().toString(),ambulance.getSelectedItem().toString(),ket.getText(),
                    CmbJam.getSelectedItem()+":"+CmbMenit.getSelectedItem()+":"+CmbDetik.getSelectedItem(),
                    KdDokPendamping.getText().trim(),KdPerawatPendamping.getText().trim(),KdDriver.getText().trim(),TJarak.getText().trim()
                })==true){
                tabMode.addRow(new String[]{
                    TNoRj.getText(),TNoRw.getText(),TNoRM.getText(),TPasien.getText(),TTmpRujuk.getText(),Valid.SetTgl(DTPRujuk.getSelectedItem()+""),
                    CmbJam.getSelectedItem()+":"+CmbMenit.getSelectedItem()+":"+CmbDetik.getSelectedItem(),TDiagnosa.getText(),KdDok.getText(),
                    TDokter.getText(),ktrujuk.getSelectedItem().toString(),ambulance.getSelectedItem().toString(),ket.getText(),
                    KdDokPendamping.getText().trim(),TDokterPendamping.getText(),KdPerawatPendamping.getText().trim(),TPerawatPendamping.getText(),
                    KdDriver.getText().trim(),TDriver.getText(),TJarak.getText().trim()
                });
                LCount.setText(""+tabMode.getRowCount());
                emptTeks();
            }
        }
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnSimpanActionPerformed(null);
        }else{
            Valid.pindah(evt,TJarak,BtnBatal);
        }
}//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        emptTeks();
        ChkInput.setSelected(true);
        isForm(); 
}//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBatalKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            emptTeks();
        }else{Valid.pindah(evt, BtnSimpan, BtnHapus);}
}//GEN-LAST:event_BtnBatalKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if(Valid.hapusTabletf(tabMode,TNoRj,"rujuk","no_rujuk")==true){
            if(tbObat.getSelectedRow()!= -1){
                tabMode.removeRow(tbObat.getSelectedRow());
                LCount.setText(""+tabMode.getRowCount());
                emptTeks();
            }
        }
}//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnHapusActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnBatal, BtnEdit);
        }
}//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed
        if(TNoRj.getText().trim().equals("")){
            Valid.textKosong(TNoRj,"No.Rujuk");
        }else if(TTmpRujuk.getText().trim().equals("")){
            Valid.textKosong(TTmpRujuk,"tempat rujuk");
        }else if(TNoRw.getText().trim().equals("")||TPasien.getText().trim().equals("")){
            Valid.textKosong(TNoRw,"pasien");
        }else if(TDokter.getText().trim().equals("")){
            Valid.textKosong(KdDok,"dokter yang bertugas");
        }else{         
            if(tbObat.getSelectedRow()== -1){
                JOptionPane.showMessageDialog(null,"Maaf, silahkan pilih data rujukan yang mau diganti...!!!");
            }else if(Sequel.mengedittf("rujuk","no_rujuk=?","no_rujuk=?,no_rawat=?,rujuk_ke=?,tgl_rujuk=?,jam=?,keterangan_diagnosa=?,kd_dokter=?,"+
                    "kat_rujuk=?,ambulance=?,keterangan=?,kd_dokter_pendamping=?,kd_perawat_pendamping=?,kd_driver=?,jarak=?",15,new String[]{
                    TNoRj.getText(),TNoRw.getText(),TTmpRujuk.getText(),Valid.SetTgl(DTPRujuk.getSelectedItem()+""),
                    CmbJam.getSelectedItem()+":"+CmbMenit.getSelectedItem()+":"+CmbDetik.getSelectedItem(),TDiagnosa.getText(),KdDok.getText(),
                    ktrujuk.getSelectedItem().toString(),ambulance.getSelectedItem().toString(),ket.getText(),
                    kosongNull(KdDokPendamping),kosongNull(KdPerawatPendamping),kosongNull(KdDriver),TJarak.getText().trim(),
                    tbObat.getValueAt(tbObat.getSelectedRow(),0).toString()
                })==true){
                tbObat.setValueAt(TNoRj.getText(),tbObat.getSelectedRow(),0);
                tbObat.setValueAt(TNoRw.getText(),tbObat.getSelectedRow(),1);
                tbObat.setValueAt(TNoRM.getText(),tbObat.getSelectedRow(),2);
                tbObat.setValueAt(TPasien.getText(),tbObat.getSelectedRow(),3);
                tbObat.setValueAt(TTmpRujuk.getText(),tbObat.getSelectedRow(),4);
                tbObat.setValueAt(Valid.SetTgl(DTPRujuk.getSelectedItem()+""),tbObat.getSelectedRow(),5);
                tbObat.setValueAt(CmbJam.getSelectedItem()+":"+CmbMenit.getSelectedItem()+":"+CmbDetik.getSelectedItem(),tbObat.getSelectedRow(),6);
                tbObat.setValueAt(TDiagnosa.getText(),tbObat.getSelectedRow(),7);
                tbObat.setValueAt(KdDok.getText(),tbObat.getSelectedRow(),8);
                tbObat.setValueAt(TDokter.getText(),tbObat.getSelectedRow(),9);
                tbObat.setValueAt(ktrujuk.getSelectedItem().toString(),tbObat.getSelectedRow(),10);
                tbObat.setValueAt(ambulance.getSelectedItem().toString(),tbObat.getSelectedRow(),11);
                tbObat.setValueAt(ket.getText(),tbObat.getSelectedRow(),12);
                tbObat.setValueAt(KdDokPendamping.getText().trim(),tbObat.getSelectedRow(),13);
                tbObat.setValueAt(TDokterPendamping.getText(),tbObat.getSelectedRow(),14);
                tbObat.setValueAt(KdPerawatPendamping.getText().trim(),tbObat.getSelectedRow(),15);
                tbObat.setValueAt(TPerawatPendamping.getText(),tbObat.getSelectedRow(),16);
                tbObat.setValueAt(KdDriver.getText().trim(),tbObat.getSelectedRow(),17);
                tbObat.setValueAt(TDriver.getText(),tbObat.getSelectedRow(),18);
                tbObat.setValueAt(TJarak.getText().trim(),tbObat.getSelectedRow(),19);
                emptTeks();
            }
        }
}//GEN-LAST:event_BtnEditActionPerformed

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEditKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnEditActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnHapus, BtnPrint);
        }
}//GEN-LAST:event_BtnEditKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
}//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            dispose();
        }else{Valid.pindah(evt,BtnEdit,TCari);}
}//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            BtnBatal.requestFocus();
        }else if(tabMode.getRowCount()!=0){
            Map<String, Object> param = new HashMap<>(); 
                param.put("namars",akses.getnamars());
                param.put("alamatrs",akses.getalamatrs());
                param.put("kotars",akses.getkabupatenrs());
                param.put("propinsirs",akses.getpropinsirs());
                param.put("kontakrs",akses.getkontakrs());
                param.put("emailrs",akses.getemailrs());   
                param.put("logo",Sequel.cariGambar("select setting.logo from setting")); 
            String tgl=" rujuk.tgl_rujuk between '"+Valid.SetTgl(DTPCari1.getSelectedItem()+"")+"' and '"+Valid.SetTgl(DTPCari2.getSelectedItem()+"")+"' ";
            Valid.MyReportqry("rptRujuk.jasper","report","::[ Data Rujuk Pasien ]::","select rujuk.no_rujuk,rujuk.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,"+
                "rujuk.rujuk_ke,rujuk.tgl_rujuk,rujuk.jam,rujuk.keterangan_diagnosa,rujuk.kd_dokter,dokter.nm_dokter,rujuk.kat_rujuk,rujuk.ambulance,rujuk.keterangan "+
                "from rujuk inner join reg_periksa inner join pasien inner join dokter "+
                "on rujuk.no_rawat=reg_periksa.no_rawat "+
                "and reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                "and rujuk.kd_dokter=dokter.kd_dokter "+
                "where "+tgl+"and no_rujuk like '%"+TCari.getText().trim()+"%' or "+
                tgl+"and rujuk.no_rawat like '%"+TCari.getText().trim()+"%' or "+
                tgl+"and reg_periksa.no_rkm_medis like '%"+TCari.getText().trim()+"%' or "+
                tgl+"and pasien.nm_pasien like '%"+TCari.getText().trim()+"%' or "+
                tgl+"and rujuk.rujuk_ke like '%"+TCari.getText().trim()+"%' or "+
                tgl+"and rujuk.tgl_rujuk like '%"+TCari.getText().trim()+"%' or "+
                tgl+"and rujuk.keterangan_diagnosa like '%"+TCari.getText().trim()+"%' or "+
                tgl+"and rujuk.kd_dokter like '%"+TCari.getText().trim()+"%' or "+
                tgl+"and dokter.nm_dokter like '%"+TCari.getText().trim()+"%' "+
                " order by rujuk.no_rujuk",param);
        }
        this.setCursor(Cursor.getDefaultCursor());
}//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnPrintActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnEdit, BtnKeluar);
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
            Valid.pindah(evt, BtnCari, TPasien);
        }
}//GEN-LAST:event_BtnAllKeyPressed

    private void DTPRujukKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DTPRujukKeyPressed
        Valid.pindah(evt,TNoRj,CmbJam);
}//GEN-LAST:event_DTPRujukKeyPressed

    private void TNoRMKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRMKeyPressed
        // Valid.pindah(evt, TNm, BtnSimpan);
}//GEN-LAST:event_TNoRMKeyPressed

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbObatMouseClicked
        if(tabMode.getRowCount()!=0){
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
}//GEN-LAST:event_tbObatMouseClicked

private void KdDokKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdDokKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            TDokter.setText(dokter.tampil3(KdDok.getText()));
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            btnDokterActionPerformed(null);
        }else{            
            Valid.pindah(evt,ktrujuk,ambulance);
        }
}//GEN-LAST:event_KdDokKeyPressed

private void btnDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDokterActionPerformed
        dokter.isCek();
        dokter.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setVisible(true);
}//GEN-LAST:event_btnDokterActionPerformed

private void TDokterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TDokterKeyPressed
        //Valid.pindah(evt,TKd,TSpek);
}//GEN-LAST:event_TDokterKeyPressed

    private void MnSuratRujukanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnSuratRujukanActionPerformed
        cetakSuratRujukan(false);
    }//GEN-LAST:event_MnSuratRujukanActionPerformed

    private void MnSuratRujukanAmbulanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnSuratRujukanAmbulanceActionPerformed
        cetakSuratRujukan(true);
    }//GEN-LAST:event_MnSuratRujukanAmbulanceActionPerformed

    /**
     * Cetak surat rujukan & bukti layanan ambulance.
     * @param gabung true = satu lembar A4 landscape berisi keduanya (masing-masing A5),
     *               false = dua report A4 terpisah
     */
    private void cetakSuratRujukan(boolean gabung) {
        if(TPasien.getText().trim().equals("")||tbObat.getSelectedRow()== -1){
            JOptionPane.showMessageDialog(null,"Maaf, Silahkan anda pilih dulu data rujukan pasien di tabel...!!!");
        }else{
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> param = new HashMap<>();
            keluar="";

            diagnosa2="";
            i=Sequel.cariInteger("select count(no_rawat) from rawat_inap_dr where no_rawat=?",TNoRw.getText());
            if(i>0){
                if(diagnosa2.equals("")){
                    diagnosa2="rawat inap";
                }else {
                    diagnosa2=diagnosa2+", rawat inap";
                }
            }

            i=Sequel.cariInteger("select count(no_rawat) from periksa_lab where no_rawat=?",TNoRw.getText());
            if(i>0){
                if(diagnosa2.equals("")){
                    diagnosa2="pemeriksaan laboratorium";
                }else {
                    diagnosa2=diagnosa2+", pemeriksaan laboratorium";
                }
            }

            i=Sequel.cariInteger("select count(no_rawat) from periksa_radiologi where no_rawat=?",TNoRw.getText());
            if(i>0){
                if(diagnosa2.equals("")){
                    diagnosa2="pemeriksaan radiologi";
                }else {
                    diagnosa2=diagnosa2+", pemeriksaan radiologi";
                }
            }

            i=Sequel.cariInteger("select count(no_rawat) from operasi where no_rawat=?",TNoRw.getText());
            if(i>0){
                if(diagnosa2.equals("")){
                    diagnosa2="operasi";
                }else {
                    diagnosa2=diagnosa2+", operasi";
                }
            }

            keluar=Sequel.cariIsi("select stts_pulang from kamar_inap where no_rawat=? and stts_pulang='-' order by STR_TO_DATE(concat(tgl_masuk,' ',jam_masuk),'%Y-%m-%d %H:%i:%s') desc limit 1",TNoRw.getText());

            param.put("html","Demikianlah riwayat perawatan selama di "+akses.getnamars()+" dengan diagnosa akhir "+tbObat.getValueAt(tbObat.getSelectedRow(),7).toString()+". "+
                "Atas kerjasamanya kami ucapkan terima kasih");
            String kodedokter=Sequel.cariIsi("SELECT kd_dokter FROM dpjp_ranap WHERE no_rawat = ?",TNoRw.getText());
            if(kodedokter.isBlank()){
               kodedokter=Sequel.cariIsi("SELECT kd_dokter FROM reg_periksa WHERE no_rawat = ?",TNoRw.getText());
            }
            String dpjp=Sequel.cariIsi("select dokter.nm_dokter from dokter where dokter.kd_dokter=?",kodedokter);
            String finger=Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?",kodedokter);
            param.put("finger","Dikeluarkan di "+akses.getnamars()+", Kabupaten/Kota "+akses.getkabupatenrs()+"\nDitandatangani secara elektronik oleh "+dpjp+"\nID "+(finger.equals("")?kodedokter:finger)+"\n"+Sequel.cariIsi("select DATE_FORMAT(reg_periksa.tgl_registrasi,'%d-%m-%Y') from reg_periksa where reg_periksa.no_rawat=?",TNoRw.getText()));  
            param.put("namars",akses.getnamars());
            param.put("alamatrs",akses.getalamatrs());
            param.put("diagnosa",tbObat.getValueAt(tbObat.getSelectedRow(),7).toString());
            param.put("tindakan",diagnosa2);
            param.put("terapi",Sequel.cariIsi(
                "select concat_ws(char(10),"+
                    "if(obat_di_rs!='',concat('Obat di RS: ',obat_di_rs),null),"+
                    "if(tindakan_dan_operasi!='',concat('Tindakan & Operasi: ',tindakan_dan_operasi),null),"+
                    "if(obat_pulang!='',concat('Obat Pulang: ',obat_pulang),null),"+
                    "if(diet!='',concat('Diet: ',diet),null)) "+
                "from resume_pasien_ranap where no_rawat=?",TNoRw.getText()));
            param.put("kotars",akses.getkabupatenrs());
            param.put("propinsirs",akses.getpropinsirs());
            param.put("kontakrs",akses.getkontakrs());
            param.put("emailrs",akses.getemailrs());
            param.put("logo",Sequel.cariGambar("select setting.logo from setting"));

            // tanda tangan elektronik dokter & perawat pendamping rujukan yang dipilih
            String norujuk=tbObat.getValueAt(tbObat.getSelectedRow(),0).toString();
            String tglrujuk=Sequel.cariIsi("select DATE_FORMAT(rujuk.tgl_rujuk,'%d-%m-%Y') from rujuk where rujuk.no_rujuk=?",norujuk);
            String kodedokterpendamping=Sequel.cariIsi("select rujuk.kd_dokter_pendamping from rujuk where rujuk.no_rujuk=?",norujuk);
            String kodeperawatpendamping=Sequel.cariIsi("select rujuk.kd_perawat_pendamping from rujuk where rujuk.no_rujuk=?",norujuk);
            param.put("fingerdokterpendamping",fingerTTE(kodedokterpendamping,
                    Sequel.cariIsi("select dokter.nm_dokter from dokter where dokter.kd_dokter=?",kodedokterpendamping),tglrujuk));
            param.put("fingerperawatpendamping",fingerTTE(kodeperawatpendamping,
                    Sequel.cariIsi("select petugas.nama from petugas where petugas.nip=?",kodeperawatpendamping),tglrujuk));

            sql="select rujuk.rujuk_ke,rujuk.no_rujuk,reg_periksa.no_rawat,pasien.alamat,dokter.nm_dokter, "+
                "reg_periksa.no_rkm_medis,pasien.jk,pasien.keluarga,pasien.namakeluarga,pasien.tgl_lahir,pasien.nm_pasien,"+
                "reg_periksa.almt_pj,pasien.umur,reg_periksa.tgl_registrasi,rujuk.tgl_rujuk, pasien.pekerjaan, pasien.no_peserta, rujuk.keterangan, "+
                "rujuk.kd_dokter_pendamping,ifnull(dokterpendamping.nm_dokter,'') as nm_dokter_pendamping,"+
                "rujuk.kd_perawat_pendamping,ifnull(petugas.nama,'') as nm_perawat_pendamping,"+
                "rujuk.kd_driver,ifnull(pegawai.nama,'') as nm_driver,ifnull(rujuk.jarak,'') as jarak, rujuk.ambulance from reg_periksa "+
                "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                "inner join rujuk on reg_periksa.no_rawat=rujuk.no_rawat "+
                "inner join dokter on rujuk.kd_dokter=dokter.kd_dokter "+
                "left join dokter dokterpendamping on rujuk.kd_dokter_pendamping=dokterpendamping.kd_dokter "+
                "left join petugas on rujuk.kd_perawat_pendamping=petugas.nip "+
                "left join pegawai on rujuk.kd_driver=pegawai.nik "+
                "where rujuk.no_rujuk='"+norujuk+"'";

            if(gabung){
                // logo berupa stream yang hanya bisa dibaca sekali, sisi kanan pakai stream sendiri
                param.put("logo2",Sequel.cariGambar("select setting.logo from setting"));
                Valid.MyReportqry("rptSuratRujukanDanLayananAmbulan.jasper","report","::[ Surat Rujukan & Layanan Ambulance ]::",sql,param);
            }else{
                Valid.MyReportqry("rptSuratRujukan.jasper","report","::[ Surat Rujukan ]::",sql,param);

                // logo berupa stream yang sudah terpakai oleh report pertama, ambil ulang untuk report kedua
                param.put("logo",Sequel.cariGambar("select setting.logo from setting"));
                Valid.MyReportqry("rptSuratLayananAmbulance.jasper","report","::[ Bukti Pelayanan Ambulance ]::",sql,param);
            }
            this.setCursor(Cursor.getDefaultCursor());
        }
    }

    private void ketKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ketKeyPressed
        Valid.pindah(evt,TTmpRujuk,TDiagnosa);
    }//GEN-LAST:event_ketKeyPressed

    private void CmbJamKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CmbJamKeyPressed
        Valid.pindah(evt,DTPRujuk,CmbMenit);
    }//GEN-LAST:event_CmbJamKeyPressed

    private void CmbMenitKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CmbMenitKeyPressed
        Valid.pindah(evt,CmbJam,CmbDetik);
    }//GEN-LAST:event_CmbMenitKeyPressed

    private void ambulanceKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ambulanceKeyPressed
        Valid.pindah(evt,KdDok,KdDokPendamping);
    }//GEN-LAST:event_ambulanceKeyPressed

    private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInputActionPerformed
        isForm();
    }//GEN-LAST:event_ChkInputActionPerformed

    private void CmbDetikKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CmbDetikKeyPressed
        Valid.pindah(evt,CmbMenit,TTmpRujuk);
    }//GEN-LAST:event_CmbDetikKeyPressed

    private void ktrujukKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ktrujukKeyPressed
        Valid.pindah(evt,TDiagnosa,KdDok);
    }//GEN-LAST:event_ktrujukKeyPressed

    private void tbObatKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbObatKeyReleased
        if(tabMode.getRowCount()!=0){
            if((evt.getKeyCode()==KeyEvent.VK_ENTER)||(evt.getKeyCode()==KeyEvent.VK_UP)||(evt.getKeyCode()==KeyEvent.VK_DOWN)){
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
    }//GEN-LAST:event_tbObatKeyReleased

    private void btnDiagnosaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiagnosaActionPerformed
        penyakit.isCek();
        penyakit.emptTeks();
        penyakit.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        penyakit.setLocationRelativeTo(internalFrame1);
        penyakit.setVisible(true);
    }//GEN-LAST:event_btnDiagnosaActionPerformed

    private void btnDiagnosaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnDiagnosaKeyPressed
        Valid.pindah(evt,ket,ktrujuk);
    }//GEN-LAST:event_btnDiagnosaKeyPressed

    private void btnPPKRujukan1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPPKRujukan1ActionPerformed
        faskes.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        faskes.setLocationRelativeTo(internalFrame1);
        faskes.setVisible(true);
    }//GEN-LAST:event_btnPPKRujukan1ActionPerformed

    private void btnPPKRujukan1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnPPKRujukan1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPPKRujukan1KeyPressed

    private void TJarakKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TJarakKeyPressed
        Valid.pindah(evt,KdDriver,BtnSimpan);
    }//GEN-LAST:event_TJarakKeyPressed

    private void KdDokPendampingKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdDokPendampingKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            TDokterPendamping.setText(dokterpendamping.tampil3(KdDokPendamping.getText()));
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            btnDokterPendampingActionPerformed(null);
        }else{
            Valid.pindah(evt,ambulance,KdPerawatPendamping);
        }
    }//GEN-LAST:event_KdDokPendampingKeyPressed

    private void TDokterPendampingKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TDokterPendampingKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TDokterPendampingKeyPressed

    private void btnDokterPendampingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDokterPendampingActionPerformed
        dokterpendamping.isCek();
        dokterpendamping.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        dokterpendamping.setLocationRelativeTo(internalFrame1);
        dokterpendamping.setVisible(true);
    }//GEN-LAST:event_btnDokterPendampingActionPerformed

    private void KdPerawatPendampingKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdPerawatPendampingKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            TPerawatPendamping.setText(perawat.tampil3(KdPerawatPendamping.getText()));
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            btnPerawatPendampingActionPerformed(null);
        }else{
            Valid.pindah(evt,KdDokPendamping,KdDriver);
        }
    }//GEN-LAST:event_KdPerawatPendampingKeyPressed

    private void TPerawatPendampingKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TPerawatPendampingKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TPerawatPendampingKeyPressed

    private void btnPerawatPendampingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPerawatPendampingActionPerformed
        perawat.isCek();
        perawat.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        perawat.setLocationRelativeTo(internalFrame1);
        perawat.setVisible(true);
    }//GEN-LAST:event_btnPerawatPendampingActionPerformed

    private void TDriverKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TDriverKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TDriverKeyPressed

    private void btnDriverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDriverActionPerformed
        driver.emptTeks();
        driver.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        driver.setLocationRelativeTo(internalFrame1);
        driver.setVisible(true);
    }//GEN-LAST:event_btnDriverActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgRujuk dialog = new DlgRujuk(new javax.swing.JFrame(), true);
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
    private widget.CekBox ChkJln;
    private widget.ComboBox CmbDetik;
    private widget.ComboBox CmbJam;
    private widget.ComboBox CmbMenit;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.Tanggal DTPRujuk;
    private widget.PanelBiasa FormInput;
    private widget.TextBox KdDok;
    private widget.TextBox KdDokPendamping;
    private widget.TextBox KdDriver;
    private widget.TextBox KdPerawatPendamping;
    private widget.Label LCount;
    private javax.swing.JMenuItem MnSuratRujukan;
    private javax.swing.JMenuItem MnSuratRujukanAmbulance;
    private javax.swing.JPanel PanelInput;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TDiagnosa;
    private widget.TextBox TDokter;
    private widget.TextBox TDokterPendamping;
    private widget.TextBox TDriver;
    private widget.TextBox TJarak;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRj;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.TextBox TPerawatPendamping;
    private widget.TextBox TTmpRujuk;
    private widget.ComboBox ambulance;
    private widget.Button btnDiagnosa;
    private widget.Button btnDokter;
    private widget.Button btnDokterPendamping;
    private widget.Button btnDriver;
    private widget.Button btnPPKRujukan1;
    private widget.Button btnPerawatPendamping;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel12;
    private widget.Label jLabel13;
    private widget.Label jLabel14;
    private widget.Label jLabel15;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private widget.Label jLabel18;
    private widget.Label jLabel19;
    private widget.Label jLabel20;
    private widget.Label jLabel21;
    private widget.Label jLabel3;
    private widget.Label jLabel4;
    private widget.Label jLabel5;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.Label jLabel9;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.TextBox ket;
    private widget.ComboBox ktrujuk;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try{
            tgl=" rujuk.tgl_rujuk between '"+Valid.SetTgl(DTPCari1.getSelectedItem()+"")+"' and '"+Valid.SetTgl(DTPCari2.getSelectedItem()+"")+"' ";
            sql="select rujuk.no_rujuk,rujuk.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,"+
                "rujuk.rujuk_ke,rujuk.tgl_rujuk,rujuk.jam,rujuk.keterangan_diagnosa,rujuk.kd_dokter,dokter.nm_dokter,rujuk.kat_rujuk,rujuk.ambulance,rujuk.keterangan,"+
                "rujuk.kd_dokter_pendamping,dokterpendamping.nm_dokter,rujuk.kd_perawat_pendamping,petugas.nama,"+
                "rujuk.kd_driver,pegawai.nama,rujuk.jarak "+
                "from rujuk inner join reg_periksa on rujuk.no_rawat=reg_periksa.no_rawat "+
                "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                "inner join dokter on rujuk.kd_dokter=dokter.kd_dokter "+
                "left join dokter dokterpendamping on rujuk.kd_dokter_pendamping=dokterpendamping.kd_dokter "+
                "left join petugas on rujuk.kd_perawat_pendamping=petugas.nip "+
                "left join pegawai on rujuk.kd_driver=pegawai.nik "+
                "where "+tgl+" and (no_rujuk like '%"+TCari.getText().trim()+"%' or rujuk.no_rawat like '%"+TCari.getText().trim()+"%' or "+
                "reg_periksa.no_rkm_medis like '%"+TCari.getText().trim()+"%' or pasien.nm_pasien like '%"+TCari.getText().trim()+"%' or "+
                "rujuk.rujuk_ke like '%"+TCari.getText().trim()+"%' or rujuk.keterangan_diagnosa like '%"+TCari.getText().trim()+"%' or "+
                "rujuk.kd_dokter like '%"+TCari.getText().trim()+"%' or dokter.nm_dokter like '%"+TCari.getText().trim()+"%') "+
                " order by rujuk.no_rujuk";
            ps=koneksi.prepareStatement(sql);
            try {
                rs=ps.executeQuery();
                while(rs.next()){
                    tabMode.addRow(new String[]{
                        rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),
                        rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9),rs.getString(10),
                        rs.getString(11),rs.getString(12),rs.getString(13),
                        kosong(rs.getString(14)),kosong(rs.getString(15)),kosong(rs.getString(16)),kosong(rs.getString(17)),
                        kosong(rs.getString(18)),kosong(rs.getString(19)),kosong(rs.getString(20))
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
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }
        LCount.setText(""+tabMode.getRowCount());
    }

    public void emptTeks() {
        TNoRj.setText("");
        TTmpRujuk.setText("");
        TNoRw.setText("");
        TNoRM.setText("");
        TPasien.setText("");
        KdDok.setText("");
        TDokter.setText("");
        TDiagnosa.setText("");
        DTPRujuk.setDate(new Date());
        Valid.autoNomer("rujuk","R",9,TNoRj);
        TNoRj.requestFocus();
        ktrujuk.setSelectedIndex(0);
        ambulance.setSelectedIndex(0);
        ket.setText("");
        KdDokPendamping.setText("");
        TDokterPendamping.setText("");
        KdPerawatPendamping.setText("");
        TPerawatPendamping.setText("");
        KdDriver.setText("");
        TDriver.setText("");
        TJarak.setText("");
    }

    // teks tanda tangan elektronik, format sama dengan finger dokter di surat rujukan
    private String fingerTTE(String kode, String nama, String tanggal) {
        if(kode.trim().equals("")||kode.trim().equals("-")){
            return "";
        }
        String finger=Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?",kode);
        return "Dikeluarkan di "+akses.getnamars()+", Kabupaten/Kota "+akses.getkabupatenrs()+"\nDitandatangani secara elektronik oleh "+nama+
               "\nID "+(finger.equals("")?kode:finger)+"\n"+tanggal;
    }

    private String kosong(String nilai) {
        return nilai==null?"":nilai;
    }

    // kolom pendamping/driver bersifat opsional, simpan NULL bila tidak diisi
    private void isiStrip(widget.TextBox field) {
        if(field.getText().trim().equals("")){
            field.setText("-");
        }
    }

    private String kosongNull(widget.TextBox kode) {
        return kode.getText().trim().equals("")?null:kode.getText().trim();
    }

    public void load(String param) {
        if(! param.equals("")){
            KdDok.setText(param);   
            KdDok.setEditable(false);
            btnDokter.setEnabled(false);
            TDokter.setText(dokter.tampil3(param));
        }else if(param.equals("")){
            KdDok.setText("");   
            KdDok.setEditable(true);
            btnDokter.setEnabled(true);
        }
    }

    private void getData() {
        if(tbObat.getSelectedRow()!= -1){
            TNoRj.setText(tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(),1).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(),2).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(),3).toString());
            TTmpRujuk.setText(tbObat.getValueAt(tbObat.getSelectedRow(),4).toString());
            Valid.SetTgl(DTPRujuk,tbObat.getValueAt(tbObat.getSelectedRow(),5).toString());
            CmbJam.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),6).toString().substring(0,2));
            CmbMenit.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),6).toString().substring(3,5));
            CmbDetik.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),6).toString().substring(6,8));
            TDiagnosa.setText(tbObat.getValueAt(tbObat.getSelectedRow(),7).toString());
            KdDok.setText(tbObat.getValueAt(tbObat.getSelectedRow(),8).toString());
            TDokter.setText(tbObat.getValueAt(tbObat.getSelectedRow(),9).toString());
            ktrujuk.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),10).toString());
            ambulance.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),11).toString());
            ket.setText(tbObat.getValueAt(tbObat.getSelectedRow(),12).toString());
            KdDokPendamping.setText(tbObat.getValueAt(tbObat.getSelectedRow(),13).toString());
            TDokterPendamping.setText(tbObat.getValueAt(tbObat.getSelectedRow(),14).toString());
            KdPerawatPendamping.setText(tbObat.getValueAt(tbObat.getSelectedRow(),15).toString());
            TPerawatPendamping.setText(tbObat.getValueAt(tbObat.getSelectedRow(),16).toString());
            KdDriver.setText(tbObat.getValueAt(tbObat.getSelectedRow(),17).toString());
            TDriver.setText(tbObat.getValueAt(tbObat.getSelectedRow(),18).toString());
            TJarak.setText(tbObat.getValueAt(tbObat.getSelectedRow(),19).toString());
        }
    }

    private void isRawat() {
         Sequel.cariIsi("select reg_periksa.no_rkm_medis from reg_periksa where reg_periksa.no_rawat='"+TNoRw.getText()+"' ",TNoRM);
    }

    private void isPsien() {
        Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis='"+TNoRM.getText()+"' ",TPasien);
    }
    
    public void setNoRm(String norwt, Date tgl1, Date tgl2) {
        TNoRw.setText(norwt);
        TCari.setText(norwt);
        DTPCari1.setDate(tgl1);
        DTPCari2.setDate(tgl2);
        isRawat();
        isPsien();              
        KdDok.setText(Sequel.cariIsi("select reg_periksa.kd_dokter from reg_periksa where reg_periksa.no_rawat='"+norwt+"'"));
        TDokter.setText(dokter.tampil3(KdDok.getText()));
        Sequel.cariIsi("select penyakit.nm_penyakit from penyakit inner join diagnosa_pasien on diagnosa_pasien.kd_penyakit=penyakit.kd_penyakit where diagnosa_pasien.no_rawat=?",norwt);
        ChkInput.setSelected(true);
        isForm();
    }
    
    private void isForm(){
        if(ChkInput.isSelected()==true){
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH,186));
            FormInput.setVisible(true);      
            ChkInput.setVisible(true);
        }else if(ChkInput.isSelected()==false){           
            ChkInput.setVisible(false);            
            PanelInput.setPreferredSize(new Dimension(WIDTH,20));
            FormInput.setVisible(false);      
            ChkInput.setVisible(true);
        }
    }
    
    public void isCek(){
        BtnSimpan.setEnabled(akses.getrujukan_keluar());
        BtnHapus.setEnabled(akses.getrujukan_keluar());
        BtnPrint.setEnabled(akses.getrujukan_keluar());
    }

   private void jam(){
        ActionListener taskPerformer = new ActionListener(){
            private int nilai_jam;
            private int nilai_menit;
            private int nilai_detik;
            public void actionPerformed(ActionEvent e) {
                String nol_jam = "";
                String nol_menit = "";
                String nol_detik = "";
                
                Date now = Calendar.getInstance().getTime();

                // Mengambil nilaj JAM, MENIT, dan DETIK Sekarang
                if(ChkJln.isSelected()==true){
                    nilai_jam = now.getHours();
                    nilai_menit = now.getMinutes();
                    nilai_detik = now.getSeconds();
                }else if(ChkJln.isSelected()==false){
                    nilai_jam =CmbJam.getSelectedIndex();
                    nilai_menit =CmbMenit.getSelectedIndex();
                    nilai_detik =CmbDetik.getSelectedIndex();
                }

                // Jika nilai JAM lebih kecil dari 10 (hanya 1 digit)
                if (nilai_jam <= 9) {
                    // Tambahkan "0" didepannya
                    nol_jam = "0";
                }
                // Jika nilai MENIT lebih kecil dari 10 (hanya 1 digit)
                if (nilai_menit <= 9) {
                    // Tambahkan "0" didepannya
                    nol_menit = "0";
                }
                // Jika nilai DETIK lebih kecil dari 10 (hanya 1 digit)
                if (nilai_detik <= 9) {
                    // Tambahkan "0" didepannya
                    nol_detik = "0";
                }
                // Membuat String JAM, MENIT, DETIK
                String jam = nol_jam + Integer.toString(nilai_jam);
                String menit = nol_menit + Integer.toString(nilai_menit);
                String detik = nol_detik + Integer.toString(nilai_detik);
                // Menampilkan pada Layar
                //tampil_jam.setText("  " + jam + " : " + menit + " : " + detik + "  ");
                CmbJam.setSelectedItem(jam);
                CmbMenit.setSelectedItem(menit);
                CmbDetik.setSelectedItem(detik);
            }
        };
        // Timer
        new Timer(1000, taskPerformer).start();
    }
}
