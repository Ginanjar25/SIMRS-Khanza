/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DlgPemberianInfus.java
 *
 * Created on Jun 6, 2010, 10:59:33 PM
 */

package simrskhanza;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariDokter;
import keuangan.DlgKamar;
import laporan.DlgCariPenyakit;

/**
 *
 * @author dosen
 */
public class DlgRanapGabung extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private Connection koneksi=koneksiDB.condb();
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private DlgCariPetugas petugas=new DlgCariPetugas(null,false);
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Date date = new Date();
    private String now=dateFormat.format(date);
    private int i=0;
    private PreparedStatement ps;
    private ResultSet rs,rs2,rssetjam, rsrekapkamar;
    private DlgCariPenyakit penyakit=new DlgCariPenyakit(null,false);
    private DlgCariDokter dokter = new DlgCariDokter(null, false);
    public  DlgCariPasien pasien=new DlgCariPasien(null,false);
    private PreparedStatement pssetjam,pscaripiutang,psdiagnosa,psibu,psanak,pstarif,psdpjp,pscariumur, psinsertkamar, psupdatekamar, psrekapkamar, pstitip;
    private String gabungkan="",norawatgabung="",kamaryangdigabung="",dokterranap="",bangsal="",diagnosa_akhir="",namakamar="",umur="0",sttsumur="Th",pilihan="";
    private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd");
    private DlgKamar kamar=new DlgKamar(null,false);
    
    


    /** Creates new form DlgPemberianInfus
     * @param parent
     * @param modal */
    public DlgRanapGabung(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        Object[] row={"No.Rawat Ibu","No.Rawat Bayi","No.RM Bayi","Nama Bayi","KD Dpjp","Nama Dpjp Ranap","Diagnosa"};
        tabMode=new DefaultTableModel(null,row){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        tbObat.setModel(tabMode);

        //tbObat.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbObat.getBackground()));
        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 7; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(110);
            }else if(i==1){
                column.setPreferredWidth(100);
            }else if(i==2){
                column.setPreferredWidth(70);
            }else if(i==3){
                column.setPreferredWidth(100);
            }else if(i==4){
                column.setPreferredWidth(100);
            }else if(i==5){
                column.setPreferredWidth(150);
            }else if(i==6){
                column.setPreferredWidth(200);
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TCari.setDocument(new batasInput((byte)100).getKata(TCari));
        
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
           
        
        jam();
        
        pasien.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(akses.getform().equals("DlgKamarInap")){
                    if(pasien.getTable().getSelectedRow()!= -1){                   
                        TNoRMBayi.setText(pasien.getTable().getValueAt(pasien.getTable().getSelectedRow(),0).toString());                    
                        TPasienBayi.setText(pasien.getTable().getValueAt(pasien.getTable().getSelectedRow(),1).toString());                    
                    }
                    TNoRMBayi.requestFocus();
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
        
        dokter.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(akses.getform().equals("DlgIGD")){
                    if(pilihan=="1"){
                        if (dokter.getTable().getSelectedRow() != -1) {
                            TKdDrPindahKamar.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 0).toString());
                            TNmDokterPindahKamar.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString());
                            dokter.dispose();
                            WindowPindahranapGabung.setVisible(true);
                        }
                    }else if(pilihan=="2"){
                        if (dokter.getTable().getSelectedRow() != -1) {
                            TKdDPJPPindahKamar.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 0).toString());
                            TNmDPJPPindahKamar.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString());
                            dokter.dispose();
                            WindowPindahranapGabung.setVisible(true);
                        }
                    }else{
                        if (dokter.getTable().getSelectedRow() != -1) {
                            KodeDPJP.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 0).toString());
                            NamaDPJP.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString());
                        }
                    }                    
                    KodeDPJP.requestFocus();
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
                
        penyakit.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if (pilihan=="1") {
                    if (penyakit.getTable().getSelectedRow() != -1) {
                        if ((penyakit.getTable().getValueAt(penyakit.getTable().getSelectedRow(), 0).toString() + " - " + penyakit.getTable().getValueAt(penyakit.getTable().getSelectedRow(), 1).toString()).length() < 50) {
                            TDiagnosaPindahKamar.setText(penyakit.getTable().getValueAt(penyakit.getTable().getSelectedRow(), 0).toString() + " - " + penyakit.getTable().getValueAt(penyakit.getTable().getSelectedRow(), 1).toString());
                            penyakit.dispose();
                            WindowPindahranapGabung.setVisible(true);
                        } else {
                            TDiagnosaPindahKamar.setText((penyakit.getTable().getValueAt(penyakit.getTable().getSelectedRow(), 0).toString() + " - " + penyakit.getTable().getValueAt(penyakit.getTable().getSelectedRow(), 1).toString()).substring(0, 50));
                            penyakit.dispose();
                            WindowPindahranapGabung.setVisible(true);
                        }
                    }
                }else{
                    if (penyakit.getTable().getSelectedRow() != -1) {
                        if ((penyakit.getTable().getValueAt(penyakit.getTable().getSelectedRow(), 0).toString() + " - " + penyakit.getTable().getValueAt(penyakit.getTable().getSelectedRow(), 1).toString()).length() < 50) {
                            TDiagnosa.setText(penyakit.getTable().getValueAt(penyakit.getTable().getSelectedRow(), 0).toString() + " - " + penyakit.getTable().getValueAt(penyakit.getTable().getSelectedRow(), 1).toString());
                        } else {
                            TDiagnosa.setText((penyakit.getTable().getValueAt(penyakit.getTable().getSelectedRow(), 0).toString() + " - " + penyakit.getTable().getValueAt(penyakit.getTable().getSelectedRow(), 1).toString()).substring(0, 50));
                        }
                    }
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
        
        pasien.getTable().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if(akses.getform().equals("DlgKamarInap")){
                    if(e.getKeyCode()==KeyEvent.VK_SPACE){
                        pasien.dispose();
                    }
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });        
 
        kamar.getTable().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if(akses.getform().equals("DlgKamarInap")){
                    if(e.getKeyCode()==KeyEvent.VK_SPACE){
                        kamar.dispose();
                        WindowPindahranapGabung.setVisible(true);
                    }
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        }); 
        
        kamar.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if (akses.getform().equals("DlgKamarInap")) {
                    if (kamar.getTable().getSelectedRow() != -1) {
                        KdKamar.setText(kamar.getTable().getValueAt(kamar.getTable().getSelectedRow(), 1).toString());
                        KdBangsal.setText(kamar.getTable().getValueAt(kamar.getTable().getSelectedRow(), 2).toString());
                        NmBangsal.setText(kamar.getTable().getValueAt(kamar.getTable().getSelectedRow(), 3).toString() + " ( " + kamar.getTable().getValueAt(kamar.getTable().getSelectedRow(), 4).toString() + ")");
                        HargaKamar.setText(Valid.SetAngka(Valid.SetAngka(kamar.getTable().getValueAt(kamar.getTable().getSelectedRow(), 5).toString())));
                    }
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
        MnKwitansiDeposit = new javax.swing.JMenuItem();
        NoRawatGabung = new widget.TextBox();
        WindowPindahranapGabung = new javax.swing.JDialog();
        internalFrame2 = new widget.InternalFrame();
        PanelInput1 = new javax.swing.JPanel();
        FormInput1 = new widget.PanelBiasa();
        jLabel12 = new widget.Label();
        TNorawatPindah = new widget.TextBox();
        TNoRMBayi1 = new widget.TextBox();
        BtnSimpanPindahKamar = new widget.Button();
        BtnKeluar1 = new widget.Button();
        jLabel20 = new widget.Label();
        KdKamar = new widget.TextBox();
        KdBangsal = new widget.TextBox();
        NmBangsal = new widget.TextBox();
        HargaKamar = new widget.TextBox();
        btnKamar = new widget.Button();
        TPasienBayi2 = new widget.TextBox();
        TNmDokterPindahKamar = new widget.TextBox();
        btnDokterPindahKamar = new widget.Button();
        jLabel22 = new widget.Label();
        jLabel23 = new widget.Label();
        TDiagnosaPindahKamar = new widget.TextBox();
        btnDiagnosaPindahKamar = new widget.Button();
        TKdDrPindahKamar = new widget.TextBox();
        jLabel24 = new widget.Label();
        TKdDPJPPindahKamar = new widget.TextBox();
        TNmDPJPPindahKamar = new widget.TextBox();
        btnDPJPPindahKamar1 = new widget.Button();
        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnPindahKamar = new widget.Button();
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
        PanelInput = new javax.swing.JPanel();
        ChkInput = new widget.CekBox();
        FormInput = new widget.PanelBiasa();
        jLabel4 = new widget.Label();
        TPasienBayi = new widget.TextBox();
        DTPTgl = new widget.Tanggal();
        jLabel10 = new widget.Label();
        cmbJam = new widget.ComboBox();
        cmbMnt = new widget.ComboBox();
        cmbDtk = new widget.ComboBox();
        jLabel13 = new widget.Label();
        KodeDPJP = new widget.TextBox();
        NamaDPJP = new widget.TextBox();
        BtnSeekPasien = new widget.Button();
        ChkJln = new widget.CekBox();
        TNoRMBayi = new widget.TextBox();
        BtnSeekDPJP = new widget.Button();
        jLabel15 = new widget.Label();
        TDiagnosa = new widget.TextBox();
        BtnSeekPetugas2 = new widget.Button();
        jLabel5 = new widget.Label();
        TNoRWIbu = new widget.TextBox();
        TPasienIbu = new widget.TextBox();
        TKdKamar = new widget.TextBox();
        jLabel8 = new widget.Label();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnKwitansiDeposit.setBackground(new java.awt.Color(255, 255, 254));
        MnKwitansiDeposit.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnKwitansiDeposit.setForeground(java.awt.Color.darkGray);
        MnKwitansiDeposit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnKwitansiDeposit.setText("Pindah Kamar");
        MnKwitansiDeposit.setName("MnKwitansiDeposit"); // NOI18N
        MnKwitansiDeposit.setPreferredSize(new java.awt.Dimension(250, 28));
        MnKwitansiDeposit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnKwitansiDepositActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnKwitansiDeposit);

        NoRawatGabung.setEditable(false);
        NoRawatGabung.setHighlighter(null);
        NoRawatGabung.setName("NoRawatGabung"); // NOI18N

        WindowPindahranapGabung.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        WindowPindahranapGabung.setModal(true);
        WindowPindahranapGabung.setName("WindowPindahranapGabung"); // NOI18N
        WindowPindahranapGabung.setUndecorated(true);
        WindowPindahranapGabung.setResizable(false);

        internalFrame2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Pindah Bayi Ranap Gabung ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame2.setName("internalFrame2"); // NOI18N
        internalFrame2.setLayout(new java.awt.BorderLayout(1, 1));

        PanelInput1.setName("PanelInput1"); // NOI18N
        PanelInput1.setOpaque(false);
        PanelInput1.setPreferredSize(new java.awt.Dimension(192, 184));
        PanelInput1.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput1.setName("FormInput1"); // NOI18N
        FormInput1.setPreferredSize(new java.awt.Dimension(160, 107));
        FormInput1.setLayout(null);

        jLabel12.setText("No.RM. Bayi :");
        jLabel12.setName("jLabel12"); // NOI18N
        FormInput1.add(jLabel12);
        jLabel12.setBounds(10, 10, 75, 23);

        TNorawatPindah.setEditable(false);
        TNorawatPindah.setHighlighter(null);
        TNorawatPindah.setName("TNorawatPindah"); // NOI18N
        FormInput1.add(TNorawatPindah);
        TNorawatPindah.setBounds(420, 10, 180, 23);

        TNoRMBayi1.setEditable(false);
        TNoRMBayi1.setHighlighter(null);
        TNoRMBayi1.setName("TNoRMBayi1"); // NOI18N
        FormInput1.add(TNoRMBayi1);
        TNoRMBayi1.setBounds(90, 10, 90, 23);

        BtnSimpanPindahKamar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpanPindahKamar.setMnemonic('S');
        BtnSimpanPindahKamar.setText("Simpan");
        BtnSimpanPindahKamar.setToolTipText("Alt+S");
        BtnSimpanPindahKamar.setName("BtnSimpanPindahKamar"); // NOI18N
        BtnSimpanPindahKamar.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnSimpanPindahKamar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanPindahKamarActionPerformed(evt);
            }
        });
        BtnSimpanPindahKamar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnSimpanPindahKamarKeyPressed(evt);
            }
        });
        FormInput1.add(BtnSimpanPindahKamar);
        BtnSimpanPindahKamar.setBounds(320, 180, 100, 30);

        BtnKeluar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar1.setMnemonic('K');
        BtnKeluar1.setText("Keluar");
        BtnKeluar1.setToolTipText("Alt+K");
        BtnKeluar1.setName("BtnKeluar1"); // NOI18N
        BtnKeluar1.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluar1ActionPerformed(evt);
            }
        });
        BtnKeluar1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnKeluar1KeyPressed(evt);
            }
        });
        FormInput1.add(BtnKeluar1);
        BtnKeluar1.setBounds(210, 180, 100, 30);

        jLabel20.setText("Dokter IGD :");
        jLabel20.setName("jLabel20"); // NOI18N
        FormInput1.add(jLabel20);
        jLabel20.setBounds(0, 70, 69, 23);

        KdKamar.setEditable(false);
        KdKamar.setHighlighter(null);
        KdKamar.setName("KdKamar"); // NOI18N
        FormInput1.add(KdKamar);
        KdKamar.setBounds(80, 40, 70, 23);

        KdBangsal.setEditable(false);
        KdBangsal.setName("KdBangsal"); // NOI18N
        FormInput1.add(KdBangsal);
        KdBangsal.setBounds(160, 40, 70, 23);

        NmBangsal.setEditable(false);
        NmBangsal.setHighlighter(null);
        NmBangsal.setName("NmBangsal"); // NOI18N
        FormInput1.add(NmBangsal);
        NmBangsal.setBounds(240, 40, 250, 23);

        HargaKamar.setEditable(false);
        HargaKamar.setHighlighter(null);
        HargaKamar.setName("HargaKamar"); // NOI18N
        FormInput1.add(HargaKamar);
        HargaKamar.setBounds(500, 40, 100, 23);

        btnKamar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnKamar.setMnemonic('2');
        btnKamar.setToolTipText("Alt+2");
        btnKamar.setName("btnKamar"); // NOI18N
        btnKamar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKamarActionPerformed(evt);
            }
        });
        FormInput1.add(btnKamar);
        btnKamar.setBounds(600, 40, 28, 23);

        TPasienBayi2.setEditable(false);
        TPasienBayi2.setHighlighter(null);
        TPasienBayi2.setName("TPasienBayi2"); // NOI18N
        FormInput1.add(TPasienBayi2);
        TPasienBayi2.setBounds(190, 10, 220, 23);

        TNmDokterPindahKamar.setEditable(false);
        TNmDokterPindahKamar.setHighlighter(null);
        TNmDokterPindahKamar.setName("TNmDokterPindahKamar"); // NOI18N
        TNmDokterPindahKamar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TNmDokterPindahKamarActionPerformed(evt);
            }
        });
        FormInput1.add(TNmDokterPindahKamar);
        TNmDokterPindahKamar.setBounds(240, 70, 360, 23);

        btnDokterPindahKamar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnDokterPindahKamar.setMnemonic('2');
        btnDokterPindahKamar.setToolTipText("Alt+2");
        btnDokterPindahKamar.setName("btnDokterPindahKamar"); // NOI18N
        btnDokterPindahKamar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDokterPindahKamarActionPerformed(evt);
            }
        });
        FormInput1.add(btnDokterPindahKamar);
        btnDokterPindahKamar.setBounds(600, 70, 28, 23);

        jLabel22.setText("Kamar :");
        jLabel22.setName("jLabel22"); // NOI18N
        FormInput1.add(jLabel22);
        jLabel22.setBounds(0, 40, 69, 23);

        jLabel23.setText("Diagnosa :");
        jLabel23.setName("jLabel23"); // NOI18N
        FormInput1.add(jLabel23);
        jLabel23.setBounds(0, 130, 69, 23);

        TDiagnosaPindahKamar.setEditable(false);
        TDiagnosaPindahKamar.setHighlighter(null);
        TDiagnosaPindahKamar.setName("TDiagnosaPindahKamar"); // NOI18N
        FormInput1.add(TDiagnosaPindahKamar);
        TDiagnosaPindahKamar.setBounds(80, 130, 520, 23);

        btnDiagnosaPindahKamar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnDiagnosaPindahKamar.setMnemonic('2');
        btnDiagnosaPindahKamar.setToolTipText("Alt+2");
        btnDiagnosaPindahKamar.setName("btnDiagnosaPindahKamar"); // NOI18N
        btnDiagnosaPindahKamar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDiagnosaPindahKamarActionPerformed(evt);
            }
        });
        FormInput1.add(btnDiagnosaPindahKamar);
        btnDiagnosaPindahKamar.setBounds(600, 130, 28, 23);

        TKdDrPindahKamar.setEditable(false);
        TKdDrPindahKamar.setHighlighter(null);
        TKdDrPindahKamar.setName("TKdDrPindahKamar"); // NOI18N
        FormInput1.add(TKdDrPindahKamar);
        TKdDrPindahKamar.setBounds(80, 70, 150, 23);

        jLabel24.setText("DPJP Ranap :");
        jLabel24.setName("jLabel24"); // NOI18N
        FormInput1.add(jLabel24);
        jLabel24.setBounds(0, 100, 69, 23);

        TKdDPJPPindahKamar.setEditable(false);
        TKdDPJPPindahKamar.setHighlighter(null);
        TKdDPJPPindahKamar.setName("TKdDPJPPindahKamar"); // NOI18N
        FormInput1.add(TKdDPJPPindahKamar);
        TKdDPJPPindahKamar.setBounds(80, 100, 150, 23);

        TNmDPJPPindahKamar.setEditable(false);
        TNmDPJPPindahKamar.setHighlighter(null);
        TNmDPJPPindahKamar.setName("TNmDPJPPindahKamar"); // NOI18N
        FormInput1.add(TNmDPJPPindahKamar);
        TNmDPJPPindahKamar.setBounds(240, 100, 360, 23);

        btnDPJPPindahKamar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnDPJPPindahKamar1.setMnemonic('2');
        btnDPJPPindahKamar1.setToolTipText("Alt+2");
        btnDPJPPindahKamar1.setName("btnDPJPPindahKamar1"); // NOI18N
        btnDPJPPindahKamar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDPJPPindahKamar1ActionPerformed(evt);
            }
        });
        FormInput1.add(btnDPJPPindahKamar1);
        btnDPJPPindahKamar1.setBounds(600, 100, 28, 23);

        PanelInput1.add(FormInput1, java.awt.BorderLayout.CENTER);

        internalFrame2.add(PanelInput1, java.awt.BorderLayout.CENTER);

        WindowPindahranapGabung.getContentPane().add(internalFrame2, java.awt.BorderLayout.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Ranap Gabung Ibu & Bayi ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

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
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbObatKeyPressed(evt);
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

        BtnPindahKamar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/barranap.png"))); // NOI18N
        BtnPindahKamar.setMnemonic('H');
        BtnPindahKamar.setText("Pindah Kamar");
        BtnPindahKamar.setToolTipText("Alt+H");
        BtnPindahKamar.setName("BtnPindahKamar"); // NOI18N
        BtnPindahKamar.setPreferredSize(new java.awt.Dimension(160, 30));
        BtnPindahKamar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPindahKamarActionPerformed(evt);
            }
        });
        BtnPindahKamar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnPindahKamarKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnPindahKamar);

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(65, 23));
        panelGlass8.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(70, 23));
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

        jLabel19.setText("Tgl.Rawat :");
        jLabel19.setName("jLabel19"); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(67, 23));
        panelGlass9.add(jLabel19);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "17-06-2025" }));
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
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "17-06-2025" }));
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
        TCari.setPreferredSize(new java.awt.Dimension(335, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass9.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('2');
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

        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(192, 184));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setMnemonic('M');
        ChkInput.setText(".: Input Data");
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

        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(160, 107));
        FormInput.setLayout(null);

        jLabel4.setText("No.RM. Bayi :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(10, 70, 75, 23);

        TPasienBayi.setEditable(false);
        TPasienBayi.setHighlighter(null);
        TPasienBayi.setName("TPasienBayi"); // NOI18N
        FormInput.add(TPasienBayi);
        TPasienBayi.setBounds(200, 70, 390, 23);

        DTPTgl.setEditable(false);
        DTPTgl.setForeground(new java.awt.Color(50, 70, 50));
        DTPTgl.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "17-06-2025" }));
        DTPTgl.setDisplayFormat("dd-MM-yyyy");
        DTPTgl.setName("DTPTgl"); // NOI18N
        DTPTgl.setOpaque(false);
        DTPTgl.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DTPTglKeyPressed(evt);
            }
        });
        FormInput.add(DTPTgl);
        DTPTgl.setBounds(70, 10, 105, 23);

        jLabel10.setText("Tanggal :");
        jLabel10.setName("jLabel10"); // NOI18N
        FormInput.add(jLabel10);
        jLabel10.setBounds(0, 10, 75, 23);

        cmbJam.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        cmbJam.setName("cmbJam"); // NOI18N
        cmbJam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbJamKeyPressed(evt);
            }
        });
        FormInput.add(cmbJam);
        cmbJam.setBounds(190, 10, 67, 23);

        cmbMnt.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        cmbMnt.setName("cmbMnt"); // NOI18N
        cmbMnt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbMntKeyPressed(evt);
            }
        });
        FormInput.add(cmbMnt);
        cmbMnt.setBounds(260, 10, 67, 23);

        cmbDtk.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        cmbDtk.setName("cmbDtk"); // NOI18N
        cmbDtk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbDtkKeyPressed(evt);
            }
        });
        FormInput.add(cmbDtk);
        cmbDtk.setBounds(340, 10, 67, 23);

        jLabel13.setText("DPJP Ranap :");
        jLabel13.setName("jLabel13"); // NOI18N
        FormInput.add(jLabel13);
        jLabel13.setBounds(10, 100, 75, 23);

        KodeDPJP.setEditable(false);
        KodeDPJP.setHighlighter(null);
        KodeDPJP.setName("KodeDPJP"); // NOI18N
        KodeDPJP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KodeDPJPKeyPressed(evt);
            }
        });
        FormInput.add(KodeDPJP);
        KodeDPJP.setBounds(90, 100, 100, 23);

        NamaDPJP.setEditable(false);
        NamaDPJP.setBackground(new java.awt.Color(202, 202, 202));
        NamaDPJP.setHighlighter(null);
        NamaDPJP.setName("NamaDPJP"); // NOI18N
        FormInput.add(NamaDPJP);
        NamaDPJP.setBounds(200, 100, 390, 23);

        BtnSeekPasien.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnSeekPasien.setMnemonic('5');
        BtnSeekPasien.setToolTipText("ALt+5");
        BtnSeekPasien.setName("BtnSeekPasien"); // NOI18N
        BtnSeekPasien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSeekPasienActionPerformed(evt);
            }
        });
        FormInput.add(BtnSeekPasien);
        BtnSeekPasien.setBounds(590, 70, 28, 23);

        ChkJln.setBorder(null);
        ChkJln.setSelected(true);
        ChkJln.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkJln.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkJln.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkJln.setName("ChkJln"); // NOI18N
        FormInput.add(ChkJln);
        ChkJln.setBounds(410, 10, 23, 23);

        TNoRMBayi.setEditable(false);
        TNoRMBayi.setHighlighter(null);
        TNoRMBayi.setName("TNoRMBayi"); // NOI18N
        FormInput.add(TNoRMBayi);
        TNoRMBayi.setBounds(90, 70, 100, 23);

        BtnSeekDPJP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnSeekDPJP.setMnemonic('5');
        BtnSeekDPJP.setToolTipText("ALt+5");
        BtnSeekDPJP.setName("BtnSeekDPJP"); // NOI18N
        BtnSeekDPJP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSeekDPJPActionPerformed(evt);
            }
        });
        FormInput.add(BtnSeekDPJP);
        BtnSeekDPJP.setBounds(590, 100, 28, 23);

        jLabel15.setText("Diagnosa :");
        jLabel15.setName("jLabel15"); // NOI18N
        FormInput.add(jLabel15);
        jLabel15.setBounds(10, 130, 75, 23);

        TDiagnosa.setEditable(false);
        TDiagnosa.setBackground(new java.awt.Color(202, 202, 202));
        TDiagnosa.setHighlighter(null);
        TDiagnosa.setName("TDiagnosa"); // NOI18N
        FormInput.add(TDiagnosa);
        TDiagnosa.setBounds(90, 130, 500, 23);

        BtnSeekPetugas2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnSeekPetugas2.setMnemonic('5');
        BtnSeekPetugas2.setToolTipText("ALt+5");
        BtnSeekPetugas2.setName("BtnSeekPetugas2"); // NOI18N
        BtnSeekPetugas2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSeekPetugas2ActionPerformed(evt);
            }
        });
        FormInput.add(BtnSeekPetugas2);
        BtnSeekPetugas2.setBounds(590, 130, 28, 23);

        jLabel5.setText("Kd. Kamar :");
        jLabel5.setName("jLabel5"); // NOI18N
        FormInput.add(jLabel5);
        jLabel5.setBounds(460, 40, 60, 23);

        TNoRWIbu.setEditable(false);
        TNoRWIbu.setHighlighter(null);
        TNoRWIbu.setName("TNoRWIbu"); // NOI18N
        FormInput.add(TNoRWIbu);
        TNoRWIbu.setBounds(90, 40, 130, 23);

        TPasienIbu.setEditable(false);
        TPasienIbu.setHighlighter(null);
        TPasienIbu.setName("TPasienIbu"); // NOI18N
        FormInput.add(TPasienIbu);
        TPasienIbu.setBounds(230, 40, 220, 23);

        TKdKamar.setEditable(false);
        TKdKamar.setHighlighter(null);
        TKdKamar.setName("TKdKamar"); // NOI18N
        FormInput.add(TKdKamar);
        TKdKamar.setBounds(530, 40, 80, 23);

        jLabel8.setText("No.Rawat Ibu :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(10, 40, 75, 23);

        PanelInput.add(FormInput, java.awt.BorderLayout.CENTER);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void DTPTglKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DTPTglKeyPressed
       
}//GEN-LAST:event_DTPTglKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (Sequel.cariInteger("select count(no_rm_bayi) from ranap_gabung where ranap_gabung.no_rm_bayi=? ", TNoRMBayi.getText()) > 0) {
            JOptionPane.showMessageDialog(null,"Maaf, No. RM Bayi sudah di Ranap Gabung...!!!");
        }else{
            try {
                psibu = koneksi.prepareStatement("select no_reg,tgl_registrasi,jam_reg,kd_dokter,no_rkm_medis,kd_poli,p_jawab,"
                        + "almt_pj,hubunganpj,biaya_reg,stts,stts_daftar,status_lanjut,kd_pj from reg_periksa where no_rawat=?");
                try {
                    psibu.setString(1, TNoRWIbu.getText());
                    rs = psibu.executeQuery();
                    if (rs.next()) {
                        pscariumur = koneksi.prepareStatement(
                                "select TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()) as tahun, "
                                + "(TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12)) as bulan, "
                                + "TIMESTAMPDIFF(DAY, DATE_ADD(DATE_ADD(tgl_lahir,INTERVAL TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()) YEAR), INTERVAL TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12) MONTH), CURDATE()) as hari "
                                + "from pasien where no_rkm_medis=?");
                        try {
                            pscariumur.setString(1, TNoRMBayi.getText());
                            rs2 = pscariumur.executeQuery();
                            if (rs2.next()) {
                                umur = "0";
                                sttsumur = "Th";
                                if (rs2.getInt("tahun") > 0) {
                                    umur = rs2.getString("tahun");
                                    sttsumur = "Th";
                                } else if (rs2.getInt("tahun") == 0) {
                                    if (rs2.getInt("bulan") > 0) {
                                        umur = rs2.getString("bulan");
                                        sttsumur = "Bl";
                                    } else if (rs2.getInt("bulan") == 0) {
                                        umur = rs2.getString("hari");
                                        sttsumur = "Hr";
                                    }
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Notifikasi Umur : " + e);
                        } finally {
                            if (rs2 != null) {
                                rs2.close();
                            }
                            if (pscariumur != null) {
                                pscariumur.close();
                            }
                        }
                        String titip_kamar = "-";
                        Valid.autoNomer3("select (ifnull(MAX(CONVERT(RIGHT(no_rawat,6),signed)),0)+1) from reg_periksa where tgl_registrasi='" + Valid.SetTgl(DTPTgl.getSelectedItem() + "") + "' ", dateformat.format(DTPTgl.getDate()) + "/", 6, NoRawatGabung);
                        if (Sequel.menyimpantf2("reg_periksa", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Reg Periksa", 19,
                                new String[]{rs.getString("no_reg"), NoRawatGabung.getText(), Valid.SetTgl(DTPTgl.getSelectedItem() + ""), cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                                    "-", TNoRMBayi.getText(), rs.getString("kd_poli"), rs.getString("p_jawab"),
                                    rs.getString("almt_pj"), rs.getString("hubunganpj"), rs.getString("biaya_reg"), "Belum", "Baru", "Ranap", rs.getString("kd_pj"), umur, sttsumur, "Sudah Bayar", "Baru"}) == true) {

                            Sequel.menyimpan("ranap_gabung", "?,?,?", "Data Ranap Gabung", 3, new String[]{
                                TNoRWIbu.getText(), NoRawatGabung.getText(), TNoRMBayi.getText()
                            });

                            String penjab2 = Sequel.cariIsi("SELECT CONCAT(penjab_reg.kd_pj, '-', penjab.png_jawab,'-', penjab_reg.no_kartu) AS penjab2 FROM reg_periksa\n"
                                    + "LEFT JOIN penjab_reg on penjab_reg.no_rawat = reg_periksa.no_rawat\n"
                                    + "LEFT JOIN penjab on penjab.kd_pj = penjab_reg.kd_pj\n"
                                    + "WHERE reg_periksa.no_rawat=? "
                                    + "AND penjab_reg.`order` != '1' ", TNoRWIbu.getText());

                            if (penjab2 != null && !penjab2.trim().isEmpty()) {
                                String[] parts = penjab2.split("-", 3);
                                String kode = parts.length > 0 ? parts[0] : "";
//                          String nama_penjab = parts.length > 1 ? parts[1] : "";
                                String no_kartu = parts.length > 2 ? parts[2] : "";
                                Sequel.menyimpan("penjab_reg", "?,?,?,?", "Data Penjab Registrasi", 4, new String[]{
                                    NoRawatGabung.getText(), kode, no_kartu, "2"
                                });
                            }

                            Sequel.menyimpantf("permintaan_ranap", "?,?,?,?,?", "Pasien", 5, new String[]{
                                NoRawatGabung.getText(),
                                Valid.SetTgl(DTPTgl.getSelectedItem() + ""),
                                TKdKamar.getText(),
                                TDiagnosa.getText(),
                                cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem() + "#" + titip_kamar
                            });

                            if (Sequel.menyimpantf("dpjp_ranap", "?,?", "Dokter", 2, new String[]{
                                NoRawatGabung.getText(),
                                KodeDPJP.getText()
                            }) == true) {
                                tampil();
                                BtnBatalActionPerformed(null);
                            }
                        }
                    }
                } catch (Exception ex) {
                    System.out.println("Notifikasi : " + ex);
                } finally {
                    if (rs != null) {
                        rs.close();
                    }
                    if (psibu != null) {
                        psibu.close();
                    }
                }

                tampil();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnSimpanActionPerformed(null);
        }else{
            
        }
}//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        ChkInput.setSelected(true);
        isForm(); 
        TNoRMBayi.setText("");
        TPasienBayi.setText("");
        KodeDPJP.setText("");
        NamaDPJP.setText("");
        TDiagnosa.setText("");
        TNoRMBayi.requestFocus();
}//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBatalKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnBatalActionPerformed(null);
        }else{Valid.pindah(evt, BtnSimpan, BtnHapus);}
}//GEN-LAST:event_BtnBatalKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if (tbObat.getSelectedRow() != -1) {
            int reply = JOptionPane.showConfirmDialog(rootPane, "Apakah Anda Ingin Menghapus Ranap Gabung : \n"
                    + "No Rawat: " + tbObat.getValueAt(tbObat.getSelectedRow(),1).toString() + "/ Nama: " + tbObat.getValueAt(tbObat.getSelectedRow(),3).toString(), "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                //TNoRMBayi.setText(tbObat.getValueAt(tbObat.getSelectedRow(),2).toString());
                Sequel.mengedit("reg_periksa", "no_rawat='" + Sequel.cariIsi("select ranap_gabung.no_rawat2 from ranap_gabung where ranap_gabung.no_rawat=?", tbObat.getValueAt(tbObat.getSelectedRow(),1).toString()) + "'", "status_lanjut='Ralan', status_bayar='Belum Bayar'");
                Sequel.meghapus("permintaan_ranap", "no_rawat", tbObat.getValueAt(tbObat.getSelectedRow(),1).toString());
                Sequel.meghapus("ranap_gabung", "no_rawat2", tbObat.getValueAt(tbObat.getSelectedRow(),1).toString());
                Sequel.meghapus("dpjp_ranap", "no_rawat", tbObat.getValueAt(tbObat.getSelectedRow(),1).toString());
                tampil();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Belum ada data yang dipilih!!!");
        }
}//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnHapusActionPerformed(null);
        }
}//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
        BtnBatalActionPerformed(null);
}//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            dispose();
        }
}//GEN-LAST:event_BtnKeluarKeyPressed

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
            
        }
}//GEN-LAST:event_BtnCariKeyPressed

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbObatMouseClicked
        if(tabMode.getRowCount()!=0){
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
}//GEN-LAST:event_tbObatMouseClicked

    private void tbObatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbObatKeyPressed
        if(tabMode.getRowCount()!=0){
            if((evt.getKeyCode()==KeyEvent.VK_ENTER)||(evt.getKeyCode()==KeyEvent.VK_UP)||(evt.getKeyCode()==KeyEvent.VK_DOWN)){
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
}//GEN-LAST:event_tbObatKeyPressed

private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInputActionPerformed
  isForm();                
}//GEN-LAST:event_ChkInputActionPerformed

    private void cmbJamKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbJamKeyPressed
        Valid.pindah(evt,DTPTgl,cmbMnt);
    }//GEN-LAST:event_cmbJamKeyPressed

    private void cmbMntKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbMntKeyPressed
        Valid.pindah(evt,cmbJam,cmbDtk);
    }//GEN-LAST:event_cmbMntKeyPressed

    private void cmbDtkKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbDtkKeyPressed
        
    }//GEN-LAST:event_cmbDtkKeyPressed

    private void KodeDPJPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KodeDPJPKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_UP){
            BtnSeekDPJPActionPerformed(null);
        }else{
            
        }
    }//GEN-LAST:event_KodeDPJPKeyPressed

    private void BtnSeekPasienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSeekPasienActionPerformed
        akses.setform("DlgKamarInap");
        pasien.emptTeks();
        pasien.isCek();
        pasien.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        pasien.setLocationRelativeTo(internalFrame1);
        pasien.setVisible(true);
    }//GEN-LAST:event_BtnSeekPasienActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        try {
            BtnBatalActionPerformed(null);
        } catch (Exception e) {
        }
        
    }//GEN-LAST:event_formWindowOpened

    private void MnKwitansiDepositActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnKwitansiDepositActionPerformed
        
    }//GEN-LAST:event_MnKwitansiDepositActionPerformed

    private void BtnSeekDPJPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSeekDPJPActionPerformed
        akses.setform("DlgIGD");
        pilihan ="";
        dokter.isCek();
        dokter.TCari.requestFocus();
        dokter.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setVisible(true);
    }//GEN-LAST:event_BtnSeekDPJPActionPerformed

    private void BtnSeekPetugas2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSeekPetugas2ActionPerformed
        pilihan="";
        penyakit.isCek();
        penyakit.emptTeks();
        penyakit.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        penyakit.setLocationRelativeTo(internalFrame1);
        penyakit.setVisible(true);
    }//GEN-LAST:event_BtnSeekPetugas2ActionPerformed

    private void BtnPindahKamarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPindahKamarActionPerformed
        if (tbObat.getSelectedRow() != -1) {
            TNoRMBayi1.setText(tbObat.getValueAt(tbObat.getSelectedRow(),2).toString());
            TPasienBayi2.setText(tbObat.getValueAt(tbObat.getSelectedRow(),3).toString());
            TNorawatPindah.setText(tbObat.getValueAt(tbObat.getSelectedRow(),1).toString());
            pilihan = "2";
            WindowPindahranapGabung.setSize(662, 250);
            WindowPindahranapGabung.setLocationRelativeTo(internalFrame1);
            WindowPindahranapGabung.setAlwaysOnTop(false);
            WindowPindahranapGabung.setVisible(true);
        }
    }//GEN-LAST:event_BtnPindahKamarActionPerformed

    private void BtnPindahKamarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPindahKamarKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnPindahKamarKeyPressed

    private void BtnSimpanPindahKamarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanPindahKamarActionPerformed
        if (!TNorawatPindah.getText().equals("") && !KdKamar.getText().equals("")) {
            int reply = JOptionPane.showConfirmDialog(rootPane, "Apakah Anda Ingin Menghapus Ranap Gabung dan Memindahkan ke Kamar : \n"
                    + "No Rawat: " + TNorawatPindah.getText() + "/ Nama: " + TPasienBayi2.getText() + "\nKamar :" + KdKamar.getText() + " " + NmBangsal.getText(), "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                Sequel.meghapus("ranap_gabung", "no_rawat2", TNorawatPindah.getText());
                Sequel.mengedit("reg_periksa", "no_rawat='" + TNorawatPindah.getText() + "'", "status_lanjut='Ralan', status_bayar='Belum Bayar'");
                Sequel.mengedit("permintaan_ranap", "no_rawat='" + TNorawatPindah.getText() + "'", "kd_kamar='" + KdKamar.getText() + "'");
                Sequel.mengedit("reg_periksa", "no_rawat='" + TNorawatPindah.getText() + "'", "kd_dokter='" + TKdDrPindahKamar.getText() + "'");
                Sequel.mengedit("dpjp_ranap", "no_rawat='" + TNorawatPindah.getText() + "'", "kd_dokter='" + TKdDPJPPindahKamar.getText() + "'");
                Sequel.mengedit("permintaan_ranap", "no_rawat='" + TNorawatPindah.getText() + "'", "diagnosa='" + TDiagnosaPindahKamar.getText() + "'");
                WindowPindahranapGabung.dispose();
                tampil();
                emptTeks();
            }
        } else {
            JOptionPane.showMessageDialog(null, "No. Rawat/Kamar Masih Kosong");
        }        
    }//GEN-LAST:event_BtnSimpanPindahKamarActionPerformed

    private void BtnSimpanPindahKamarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanPindahKamarKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnSimpanPindahKamarKeyPressed

    private void BtnKeluar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluar1ActionPerformed
        emptTeks();
    }//GEN-LAST:event_BtnKeluar1ActionPerformed

    private void BtnKeluar1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluar1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnKeluar1KeyPressed

    private void btnKamarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKamarActionPerformed
        WindowPindahranapGabung.setVisible(false);        
        akses.setform("DlgKamarInap");
        kamar.load();
        kamar.isCek();
        kamar.emptTeks();
        kamar.tampil();
        kamar.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        kamar.setLocationRelativeTo(internalFrame1);
        kamar.setVisible(true);
    }//GEN-LAST:event_btnKamarActionPerformed

    private void btnDokterPindahKamarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDokterPindahKamarActionPerformed
        WindowPindahranapGabung.setVisible(false);
        pilihan="1";
        akses.setform("DlgIGD");
        dokter.isCek();
        dokter.TCari.requestFocus();
        dokter.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setVisible(true);
    }//GEN-LAST:event_btnDokterPindahKamarActionPerformed

    private void btnDiagnosaPindahKamarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiagnosaPindahKamarActionPerformed
        WindowPindahranapGabung.setVisible(false);
        pilihan="1";
        penyakit.isCek();
        penyakit.emptTeks();
        penyakit.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        penyakit.setLocationRelativeTo(internalFrame1);
        penyakit.setVisible(true);
    }//GEN-LAST:event_btnDiagnosaPindahKamarActionPerformed

    private void btnDPJPPindahKamar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDPJPPindahKamar1ActionPerformed
        WindowPindahranapGabung.setVisible(false);
        pilihan="2";
        akses.setform("DlgIGD");
        dokter.isCek();
        dokter.TCari.requestFocus();
        dokter.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setVisible(true);
    }//GEN-LAST:event_btnDPJPPindahKamar1ActionPerformed

    private void TNmDokterPindahKamarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TNmDokterPindahKamarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TNmDokterPindahKamarActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgRanapGabung dialog = new DlgRanapGabung(new javax.swing.JFrame(), true);
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
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnKeluar1;
    private widget.Button BtnPindahKamar;
    private widget.Button BtnSeekDPJP;
    private widget.Button BtnSeekPasien;
    private widget.Button BtnSeekPetugas2;
    private widget.Button BtnSimpan;
    private widget.Button BtnSimpanPindahKamar;
    private widget.CekBox ChkInput;
    private widget.CekBox ChkJln;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.Tanggal DTPTgl;
    private widget.PanelBiasa FormInput;
    private widget.PanelBiasa FormInput1;
    private widget.TextBox HargaKamar;
    private widget.TextBox KdBangsal;
    private widget.TextBox KdKamar;
    private widget.TextBox KodeDPJP;
    private widget.Label LCount;
    private javax.swing.JMenuItem MnKwitansiDeposit;
    private widget.TextBox NamaDPJP;
    private widget.TextBox NmBangsal;
    private widget.TextBox NoRawatGabung;
    private javax.swing.JPanel PanelInput;
    private javax.swing.JPanel PanelInput1;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TDiagnosa;
    private widget.TextBox TDiagnosaPindahKamar;
    private widget.TextBox TKdDPJPPindahKamar;
    private widget.TextBox TKdDrPindahKamar;
    private widget.TextBox TKdKamar;
    private widget.TextBox TNmDPJPPindahKamar;
    private widget.TextBox TNmDokterPindahKamar;
    private widget.TextBox TNoRMBayi;
    private widget.TextBox TNoRMBayi1;
    private widget.TextBox TNoRWIbu;
    private widget.TextBox TNorawatPindah;
    private widget.TextBox TPasienBayi;
    private widget.TextBox TPasienBayi2;
    private widget.TextBox TPasienIbu;
    private javax.swing.JDialog WindowPindahranapGabung;
    private widget.Button btnDPJPPindahKamar1;
    private widget.Button btnDiagnosaPindahKamar;
    private widget.Button btnDokterPindahKamar;
    private widget.Button btnKamar;
    private widget.ComboBox cmbDtk;
    private widget.ComboBox cmbJam;
    private widget.ComboBox cmbMnt;
    private widget.InternalFrame internalFrame1;
    private widget.InternalFrame internalFrame2;
    private widget.Label jLabel10;
    private widget.Label jLabel12;
    private widget.Label jLabel13;
    private widget.Label jLabel15;
    private widget.Label jLabel19;
    private widget.Label jLabel20;
    private widget.Label jLabel21;
    private widget.Label jLabel22;
    private widget.Label jLabel23;
    private widget.Label jLabel24;
    private widget.Label jLabel4;
    private widget.Label jLabel5;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

    public void tampil() {   
        
        Valid.tabelKosong(tabMode);
        try{        
            ps=koneksi.prepareStatement(
                "SELECT rg.no_rawat, rg.no_rawat2, rp.no_rkm_medis, ps.nm_pasien, dpjp.kd_dokter, dr.nm_dokter, pr.diagnosa FROM ranap_gabung rg \n" +
                "JOIN reg_periksa rp ON rp.no_rawat = rg.no_rawat2\n" +
                "JOIN dpjp_ranap dpjp ON dpjp.no_rawat = rg.no_rawat2\n" +
                "JOIN permintaan_ranap pr ON pr.no_rawat = rg.no_rawat2\n" +
                "JOIN pasien ps ON ps.no_rkm_medis= rp.no_rkm_medis\n" +
                "JOIN dokter dr ON dr.kd_dokter = dpjp.kd_dokter\n" +
                "WHERE rg.no_rawat =? ");
            try {

                ps.setString(1, TCari.getText().trim() );                  
                rs=ps.executeQuery();  
                while(rs.next()){                    
                    tabMode.addRow(new String[]{
                        rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7)
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

    private void getData() {
        if(tbObat.getSelectedRow()!= -1){            
            TNoRMBayi.setText(tbObat.getValueAt(tbObat.getSelectedRow(),2).toString());
            TPasienBayi.setText(tbObat.getValueAt(tbObat.getSelectedRow(),3).toString());
            KodeDPJP.setText(tbObat.getValueAt(tbObat.getSelectedRow(),4).toString());
            NamaDPJP.setText(tbObat.getValueAt(tbObat.getSelectedRow(),5).toString());
            TDiagnosa.setText(tbObat.getValueAt(tbObat.getSelectedRow(),6).toString());
        }
    }

    private void isRawat() {        
        Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis=? ",TPasienBayi,TNoRMBayi.getText());
    }
    
    public void setNoRm(String norwt, String nmibu,Date tgl1,Date tgl2,String kdkamar) {
        TKdKamar.setText(kdkamar);
        TNoRWIbu.setText(norwt);
        TPasienIbu.setText(nmibu);
        TCari.setText(norwt);
        isRawat();
        DTPCari1.setDate(tgl1);
        DTPCari2.setDate(tgl2);
        ChkInput.setSelected(true);
        isForm();
    }
    
    private void isForm(){
        if(ChkInput.isSelected()==true){
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH,184));
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
        BtnSimpan.setEnabled(akses.getdeposit_pasien());
        BtnHapus.setEnabled(akses.getdeposit_pasien());
        
        if(akses.getjml2()>=1){
            BtnSeekPasien.setEnabled(false);
            KodeDPJP.setText(akses.getkode());
            NamaDPJP.setText(petugas.tampil3(KodeDPJP.getText()));
        } 
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
                    nilai_jam =cmbJam.getSelectedIndex();
                    nilai_menit =cmbMnt.getSelectedIndex();
                    nilai_detik =cmbDtk.getSelectedIndex();
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
                cmbJam.setSelectedItem(jam);
                cmbMnt.setSelectedItem(menit);
                cmbDtk.setSelectedItem(detik);
            }
        };
        // Timer
        new Timer(1000, taskPerformer).start();
    }



    private void emptTeks() {
        TNoRMBayi1.setText("");
        TPasienBayi2.setText("");
        TNorawatPindah.setText("");
        KdKamar.setText("");
        KdBangsal.setText("");
        NmBangsal.setText("");
        HargaKamar.setText("");
        TKdDPJPPindahKamar.setText("");
        TKdDrPindahKamar.setText("");
        TNmDPJPPindahKamar.setText("");
        TNmDokterPindahKamar.setText("");
        TDiagnosaPindahKamar.setText("");
        WindowPindahranapGabung.dispose();
    }
    
 
    
   

 
}
