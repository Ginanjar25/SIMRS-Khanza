/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DlgPemberianInfus.java
 *
 * Created on Jun 6, 2010, 10:59:33 PM
 */

package modif;

import simrskhanza.*;
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
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariDokter;
import kepegawaian.DlgCariPegawai;
import keuangan.DlgCariPerawatanRanap2;
import keuangan.DlgKamar;
import laporan.DlgCariPenyakit;
import modif.DlgCariKamar;
import modif.DlgPendaftaranBayi;
import widget.CheckBoxHeaderRenderer;

/**
 *
 * @author dosen
 */
public class DlgSBARRalan extends javax.swing.JDialog {
    private final DefaultTableModel TabModeSBAR;
    private Connection koneksi=koneksiDB.condb();
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Date date = new Date();
    private String now=dateFormat.format(date);
    private int i=0;
    private PreparedStatement ps, psSBAR;
    private ResultSet rs,rssbar;
    public  DlgCariPasien pasien=new DlgCariPasien(null,false);
    private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd");
    private JCheckBox headerCheckBoxSBAR = new JCheckBox();
    public  DlgCariPerawatanRanap2 perawatan=new DlgCariPerawatanRanap2(null,false);
    private SimpleDateFormat tanggalFormat = new SimpleDateFormat("dd-MM-yyyy");
    public  DlgCariPegawai pegawai=new DlgCariPegawai(null,false);
    
    /** Creates new form DlgPemberianInfus
     * @param parent
     * @param modal */
    public DlgSBARRalan(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        Object[] row={"No.Rawat Ibu","No.Rawat Bayi","No.RM Bayi","Nama Bayi","KD Dpjp","Nama Dpjp Ranap","Diagnosa"};
        TabModeSBAR=new DefaultTableModel(null,new Object[]{
            headerCheckBoxSBAR,"No.Rawat","No.R.M.","Nama Pasien","Tgl.Rawat","Jam Rawat", "DPJP", "Situation", "Background", "Assessment", "Recommendation",
            "Advice", "Baca", "Konfirmasi", "Petugas", "Kd DPJP", "Kd Petugas","Verifikasi", "Tgl.Verifikasi"}){
             @Override public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = false;
                if (colIndex==0) {
                    a=true;
                }
                return a;
             }
             Class[] types = new Class[] {
                 java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class,java.lang.Object.class,
                 java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, 
                 java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, 
                 java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class,
                 java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        tbSBAR.setModel(TabModeSBAR);
        CheckBoxHeaderRenderer headerRenderer = new CheckBoxHeaderRenderer();
        TableColumn tc1 = tbSBAR.getColumnModel().getColumn(0);
        tc1.setHeaderRenderer(headerRenderer);

        tbSBAR.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbSBAR.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 19; i++) {
            TableColumn column = tbSBAR.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(20);
            }else if(i==1){
                column.setPreferredWidth(105);
            }else if(i==2){
                column.setPreferredWidth(70);
            }else if(i==3){
                column.setPreferredWidth(150);
            }else if(i==4){
                column.setPreferredWidth(70);
            }else if(i==5){
                column.setPreferredWidth(70);
            }else if(i==6){
                column.setPreferredWidth(200);
            }else if(i==7){
                column.setPreferredWidth(200);
            }else if(i==8){
                column.setPreferredWidth(200);
            }else if(i==9){
                column.setPreferredWidth(200);
            }else if(i==10){
                column.setPreferredWidth(200);
            }else if(i==11){
                column.setPreferredWidth(200);
            }else if(i==12){
                column.setPreferredWidth(90);
            }else if(i==13){
                column.setPreferredWidth(90);
            }else if(i==14){
                column.setPreferredWidth(100);
            }else if(i==15){
                column.setMinWidth(0);
                column.setMaxWidth(0);
            }else if(i==16){
                column.setMinWidth(0);
                column.setMaxWidth(0);
            }else if(i==17){
                column.setPreferredWidth(100);
            }else if(i==18){
                column.setPreferredWidth(100);
            }
        }
        tbSBAR.setDefaultRenderer(Object.class, new WarnaTable());

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
        
        perawatan.dokter.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                TKdDPJPSBAR.setText(perawatan.dokter.getTable().getValueAt(perawatan.dokter.getTable().getSelectedRow(),0).toString());
                TNmDPJPSBAR.setText(perawatan.dokter.getTable().getValueAt(perawatan.dokter.getTable().getSelectedRow(),1).toString());
                TKdDPJPSBAR.requestFocus();
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
        
        tbSBAR.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {

                int col = tbSBAR.columnAtPoint(e.getPoint());

                if (col == 0) {

                    boolean checked = !headerRenderer.isSelected();
                    headerRenderer.setSelected(checked);

                    for (int i = 0; i < tbSBAR.getRowCount(); i++) {
                        tbSBAR.setValueAt(checked, i, 0);
                    }

                    tbSBAR.getTableHeader().repaint();
                    tbSBAR.repaint();
                }
            }
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

        TanggalRegistrasi = new widget.TextBox();
        internalFrameSBAR = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbSBAR = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnEdit = new widget.Button();
        BtnHapus = new widget.Button();
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
        TNmPetugasSBAR = new widget.TextBox();
        TKdPetugasSBAR = new widget.TextBox();
        jLabel13 = new widget.Label();
        TKdDPJPSBAR = new widget.TextBox();
        TNmDPJPSBAR = new widget.TextBox();
        BtnSeekDokterSBAR = new widget.Button();
        TPasien = new widget.TextBox();
        jLabel8 = new widget.Label();
        jLabel84 = new widget.Label();
        scrollPane15 = new widget.ScrollPane();
        TSituationSBAR = new widget.TextArea();
        jLabel85 = new widget.Label();
        scrollPane16 = new widget.ScrollPane();
        TBackgroundSBAR = new widget.TextArea();
        jLabel87 = new widget.Label();
        scrollPane17 = new widget.ScrollPane();
        TAssessment = new widget.TextArea();
        TNoRw = new widget.TextBox();
        jLabel88 = new widget.Label();
        scrollPane18 = new widget.ScrollPane();
        TRecommendationSBAR = new widget.TextArea();
        jLabel89 = new widget.Label();
        scrollPane19 = new widget.ScrollPane();
        TAdviceSBAR = new widget.TextArea();
        jLabel10 = new widget.Label();
        DTPTgl = new widget.Tanggal();
        cmbJam = new widget.ComboBox();
        cmbMnt = new widget.ComboBox();
        cmbDtk = new widget.ComboBox();
        ChkJln = new widget.CekBox();
        jLabel38 = new widget.Label();
        cmbBacaSBAR = new widget.ComboBox();
        jLabel39 = new widget.Label();
        cmbKonfirmasiSBAR = new widget.ComboBox();
        TNoRM = new widget.TextBox();

        TanggalRegistrasi.setEditable(false);
        TanggalRegistrasi.setHighlighter(null);
        TanggalRegistrasi.setName("TanggalRegistrasi"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrameSBAR.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ FORMULIR SBAR ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrameSBAR.setName("internalFrameSBAR"); // NOI18N
        internalFrameSBAR.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbSBAR.setAutoCreateRowSorter(true);
        tbSBAR.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbSBAR.setName("tbSBAR"); // NOI18N
        tbSBAR.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbSBARMouseClicked(evt);
            }
        });
        tbSBAR.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbSBARKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbSBAR);

        internalFrameSBAR.add(Scroll, java.awt.BorderLayout.CENTER);

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
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "06-08-2026" }));
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
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "06-08-2026" }));
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

        internalFrameSBAR.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(192, 300));
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

        jLabel4.setText("Dilakukan :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(10, 40, 75, 23);

        TNmPetugasSBAR.setEditable(false);
        TNmPetugasSBAR.setHighlighter(null);
        TNmPetugasSBAR.setName("TNmPetugasSBAR"); // NOI18N
        FormInput.add(TNmPetugasSBAR);
        TNmPetugasSBAR.setBounds(150, 40, 210, 23);

        TKdPetugasSBAR.setEditable(false);
        TKdPetugasSBAR.setHighlighter(null);
        TKdPetugasSBAR.setName("TKdPetugasSBAR"); // NOI18N
        FormInput.add(TKdPetugasSBAR);
        TKdPetugasSBAR.setBounds(90, 40, 60, 23);

        jLabel13.setText("Dokter :");
        jLabel13.setName("jLabel13"); // NOI18N
        FormInput.add(jLabel13);
        jLabel13.setBounds(10, 70, 75, 23);

        TKdDPJPSBAR.setEditable(false);
        TKdDPJPSBAR.setHighlighter(null);
        TKdDPJPSBAR.setName("TKdDPJPSBAR"); // NOI18N
        TKdDPJPSBAR.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TKdDPJPSBARKeyPressed(evt);
            }
        });
        FormInput.add(TKdDPJPSBAR);
        TKdDPJPSBAR.setBounds(90, 70, 60, 23);

        TNmDPJPSBAR.setEditable(false);
        TNmDPJPSBAR.setBackground(new java.awt.Color(202, 202, 202));
        TNmDPJPSBAR.setHighlighter(null);
        TNmDPJPSBAR.setName("TNmDPJPSBAR"); // NOI18N
        FormInput.add(TNmDPJPSBAR);
        TNmDPJPSBAR.setBounds(150, 70, 210, 23);

        BtnSeekDokterSBAR.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnSeekDokterSBAR.setMnemonic('5');
        BtnSeekDokterSBAR.setToolTipText("ALt+5");
        BtnSeekDokterSBAR.setName("BtnSeekDokterSBAR"); // NOI18N
        BtnSeekDokterSBAR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSeekDokterSBARActionPerformed(evt);
            }
        });
        FormInput.add(BtnSeekDokterSBAR);
        BtnSeekDokterSBAR.setBounds(360, 70, 28, 23);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N
        FormInput.add(TPasien);
        TPasien.setBounds(650, 10, 240, 23);

        jLabel8.setText("No.Rawat :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(410, 10, 60, 23);

        jLabel84.setText("S (Situation) :");
        jLabel84.setName("jLabel84"); // NOI18N
        FormInput.add(jLabel84);
        jLabel84.setBounds(0, 100, 90, 23);

        scrollPane15.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane15.setName("scrollPane15"); // NOI18N

        TSituationSBAR.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        TSituationSBAR.setColumns(20);
        TSituationSBAR.setRows(5);
        TSituationSBAR.setName("TSituationSBAR"); // NOI18N
        TSituationSBAR.setPreferredSize(new java.awt.Dimension(102, 52));
        TSituationSBAR.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TSituationSBARKeyPressed(evt);
            }
        });
        scrollPane15.setViewportView(TSituationSBAR);

        FormInput.add(scrollPane15);
        scrollPane15.setBounds(100, 100, 260, 63);

        jLabel85.setText("B (Background) :");
        jLabel85.setName("jLabel85"); // NOI18N
        FormInput.add(jLabel85);
        jLabel85.setBounds(10, 170, 80, 23);

        scrollPane16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane16.setName("scrollPane16"); // NOI18N

        TBackgroundSBAR.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        TBackgroundSBAR.setColumns(20);
        TBackgroundSBAR.setRows(5);
        TBackgroundSBAR.setName("TBackgroundSBAR"); // NOI18N
        TBackgroundSBAR.setPreferredSize(new java.awt.Dimension(102, 52));
        TBackgroundSBAR.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TBackgroundSBARKeyPressed(evt);
            }
        });
        scrollPane16.setViewportView(TBackgroundSBAR);

        FormInput.add(scrollPane16);
        scrollPane16.setBounds(100, 170, 260, 63);

        jLabel87.setText("A (Assessment) :");
        jLabel87.setName("jLabel87"); // NOI18N
        FormInput.add(jLabel87);
        jLabel87.setBounds(400, 40, 90, 23);

        scrollPane17.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane17.setName("scrollPane17"); // NOI18N

        TAssessment.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        TAssessment.setColumns(20);
        TAssessment.setRows(5);
        TAssessment.setName("TAssessment"); // NOI18N
        TAssessment.setPreferredSize(new java.awt.Dimension(102, 52));
        TAssessment.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TAssessmentKeyPressed(evt);
            }
        });
        scrollPane17.setViewportView(TAssessment);

        FormInput.add(scrollPane17);
        scrollPane17.setBounds(500, 40, 260, 63);

        TNoRw.setEditable(false);
        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw"); // NOI18N
        TNoRw.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TNoRwActionPerformed(evt);
            }
        });
        FormInput.add(TNoRw);
        TNoRw.setBounds(480, 10, 100, 23);

        jLabel88.setText("R (Recommendation) :");
        jLabel88.setName("jLabel88"); // NOI18N
        FormInput.add(jLabel88);
        jLabel88.setBounds(370, 110, 120, 23);

        scrollPane18.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane18.setName("scrollPane18"); // NOI18N

        TRecommendationSBAR.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        TRecommendationSBAR.setColumns(20);
        TRecommendationSBAR.setRows(5);
        TRecommendationSBAR.setName("TRecommendationSBAR"); // NOI18N
        TRecommendationSBAR.setPreferredSize(new java.awt.Dimension(102, 52));
        TRecommendationSBAR.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TRecommendationSBARKeyPressed(evt);
            }
        });
        scrollPane18.setViewportView(TRecommendationSBAR);

        FormInput.add(scrollPane18);
        scrollPane18.setBounds(500, 110, 260, 63);

        jLabel89.setText("Advice Dokter :");
        jLabel89.setName("jLabel89"); // NOI18N
        FormInput.add(jLabel89);
        jLabel89.setBounds(390, 180, 100, 23);

        scrollPane19.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane19.setName("scrollPane19"); // NOI18N

        TAdviceSBAR.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        TAdviceSBAR.setColumns(20);
        TAdviceSBAR.setRows(5);
        TAdviceSBAR.setName("TAdviceSBAR"); // NOI18N
        TAdviceSBAR.setPreferredSize(new java.awt.Dimension(102, 52));
        TAdviceSBAR.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TAdviceSBARKeyPressed(evt);
            }
        });
        scrollPane19.setViewportView(TAdviceSBAR);

        FormInput.add(scrollPane19);
        scrollPane19.setBounds(500, 180, 260, 63);

        jLabel10.setText("Tanggal :");
        jLabel10.setName("jLabel10"); // NOI18N
        FormInput.add(jLabel10);
        jLabel10.setBounds(0, 10, 60, 23);

        DTPTgl.setForeground(new java.awt.Color(50, 70, 50));
        DTPTgl.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "06-08-2026" }));
        DTPTgl.setDisplayFormat("dd-MM-yyyy");
        DTPTgl.setName("DTPTgl"); // NOI18N
        DTPTgl.setOpaque(false);
        DTPTgl.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DTPTglKeyPressed(evt);
            }
        });
        FormInput.add(DTPTgl);
        DTPTgl.setBounds(65, 10, 100, 23);

        cmbJam.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        cmbJam.setName("cmbJam"); // NOI18N
        cmbJam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbJamKeyPressed(evt);
            }
        });
        FormInput.add(cmbJam);
        cmbJam.setBounds(170, 10, 67, 23);

        cmbMnt.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        cmbMnt.setName("cmbMnt"); // NOI18N
        cmbMnt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbMntKeyPressed(evt);
            }
        });
        FormInput.add(cmbMnt);
        cmbMnt.setBounds(240, 10, 67, 23);

        cmbDtk.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        cmbDtk.setName("cmbDtk"); // NOI18N
        cmbDtk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbDtkKeyPressed(evt);
            }
        });
        FormInput.add(cmbDtk);
        cmbDtk.setBounds(310, 10, 67, 23);

        ChkJln.setBorder(null);
        ChkJln.setSelected(true);
        ChkJln.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkJln.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkJln.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkJln.setName("ChkJln"); // NOI18N
        FormInput.add(ChkJln);
        ChkJln.setBounds(380, 10, 23, 23);

        jLabel38.setText("Baca :");
        jLabel38.setName("jLabel38"); // NOI18N
        FormInput.add(jLabel38);
        jLabel38.setBounds(20, 240, 70, 23);

        cmbBacaSBAR.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Sudah", "Belum" }));
        cmbBacaSBAR.setName("cmbBacaSBAR"); // NOI18N
        cmbBacaSBAR.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbBacaSBARKeyPressed(evt);
            }
        });
        FormInput.add(cmbBacaSBAR);
        cmbBacaSBAR.setBounds(100, 240, 90, 23);

        jLabel39.setText("Konfirmasi :");
        jLabel39.setName("jLabel39"); // NOI18N
        FormInput.add(jLabel39);
        jLabel39.setBounds(200, 240, 60, 23);

        cmbKonfirmasiSBAR.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Sudah", "Belum" }));
        cmbKonfirmasiSBAR.setName("cmbKonfirmasiSBAR"); // NOI18N
        cmbKonfirmasiSBAR.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbKonfirmasiSBARKeyPressed(evt);
            }
        });
        FormInput.add(cmbKonfirmasiSBAR);
        cmbKonfirmasiSBAR.setBounds(270, 240, 90, 23);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM"); // NOI18N
        TNoRM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TNoRMActionPerformed(evt);
            }
        });
        FormInput.add(TNoRM);
        TNoRM.setBounds(580, 10, 70, 23);

        PanelInput.add(FormInput, java.awt.BorderLayout.CENTER);

        internalFrameSBAR.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrameSBAR, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if ((!TSituationSBAR.getText().trim().equals("")) && (!TBackgroundSBAR.getText().trim().equals("")) && (!TAssessment.getText().trim().equals(""))
                && (!TRecommendationSBAR.getText().trim().equals("")) && (!TAdviceSBAR.getText().trim().equals(""))) {
            if (TKdDPJPSBAR.getText().trim().equals("") || TNmDPJPSBAR.getText().trim().equals("")) {
                Valid.textKosong(TKdDPJPSBAR, "Dokter DPJP masih kosong...!!");
            } else if (TKdPetugasSBAR.getText().trim().equals("") || TNmPetugasSBAR.getText().trim().equals("")) {
                Valid.textKosong(TKdPetugasSBAR, "PPA masih kosong...!!");
            } else {
                if (akses.getkode().equals("Admin Utama")) {
                    if (Sequel.menyimpantf("sbar_tbak_ralan", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 15, new String[]{
                        TNoRw.getText(), Valid.SetTgl(DTPTgl.getSelectedItem() + ""), cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                        TSituationSBAR.getText(), TBackgroundSBAR.getText(), TAssessment.getText(), TRecommendationSBAR.getText(), TAdviceSBAR.getText(),
                        cmbBacaSBAR.getSelectedItem().toString(), cmbKonfirmasiSBAR.getSelectedItem().toString(), "Belum", TKdDPJPSBAR.getText(), TKdPetugasSBAR.getText(),
                        (cmbBacaSBAR.getSelectedItem().toString().equals("Sudah") || cmbKonfirmasiSBAR.getSelectedItem().toString().equals("Sudah") ? Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem() : "0000-00-00 00:00:00"),
                        "0000-00-00 00:00:00"
                    }) == true) {
                        TabModeSBAR.addRow(new Object[]{
                            false, TNoRw.getText(), TNoRM.getText(), TPasien.getText(), Valid.SetTgl(DTPTgl.getSelectedItem() + ""), cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                            TNmDPJPSBAR.getText(), TSituationSBAR.getText(), TBackgroundSBAR.getText(), TAssessment.getText(), TRecommendationSBAR.getText(), TAdviceSBAR.getText(),
                            cmbBacaSBAR.getSelectedItem().toString(), cmbKonfirmasiSBAR.getSelectedItem().toString(), TNmPetugasSBAR.getText(), TKdDPJPSBAR.getText(),
                            TKdPetugasSBAR.getText(), "Belum", "0000-00-00 00:00:00"
                        });
                        LCount.setText("" + TabModeSBAR.getRowCount());
                        emptySBAR();
                    }
                } else {
                    if (akses.getkode().equals(TKdPetugasSBAR.getText())) {
                        if (Sequel.menyimpantf("sbar_tbak_ralan", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 15, new String[]{
                            TNoRw.getText(), Valid.SetTgl(DTPTgl.getSelectedItem() + ""), cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                            TSituationSBAR.getText(), TBackgroundSBAR.getText(), TAssessment.getText(), TRecommendationSBAR.getText(), TAdviceSBAR.getText(),
                            cmbBacaSBAR.getSelectedItem().toString(), cmbKonfirmasiSBAR.getSelectedItem().toString(), "Belum", TKdDPJPSBAR.getText(), TKdPetugasSBAR.getText(),
                            (cmbBacaSBAR.getSelectedItem().toString().equals("Sudah") || cmbKonfirmasiSBAR.getSelectedItem().toString().equals("Sudah") ? Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem() : "0000-00-00 00:00:00"),
                            "0000-00-00 00:00:00"
                        }) == true) {
                            TabModeSBAR.addRow(new Object[]{
                                false, TNoRw.getText(), TNoRM.getText(), TPasien.getText(), Valid.SetTgl(DTPTgl.getSelectedItem() + ""), cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem(),
                                TNmDPJPSBAR.getText(), TSituationSBAR.getText(), TBackgroundSBAR.getText(), TAssessment.getText(), TRecommendationSBAR.getText(), TAdviceSBAR.getText(),
                                cmbBacaSBAR.getSelectedItem().toString(), cmbKonfirmasiSBAR.getSelectedItem().toString(), TNmPetugasSBAR.getText(), TKdDPJPSBAR.getText(),
                                TKdPetugasSBAR.getText(), "Belum", "0000-00-00 00:00:00"
                            });
                            LCount.setText("" + TabModeSBAR.getRowCount());
                            emptySBAR();
                        } else {
                            JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat pemrosesan data, transaksi dibatalkan.\nPeriksa kembali data sebelum melanjutkan menyimpan..!!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Hanya bisa disimpan oleh dokter/petugas yang bersangkutan..!!");
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Kolom SBAR harus terisi semua !");
        }
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnSimpanActionPerformed(null);
        }
}//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        ChkInput.setSelected(true);
        isForm(); 
        emptySBAR();        
}//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBatalKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnBatalActionPerformed(null);
        }else{Valid.pindah(evt, BtnSimpan, BtnHapus);}
}//GEN-LAST:event_BtnBatalKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if (TabModeSBAR.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis...!!!!");
            TNoRw.requestFocus();
        } else {
            for (i = 0; i < tbSBAR.getRowCount(); i++) {
                if (tbSBAR.getValueAt(i, 0).toString().equals("true")) {
                    if (akses.getkode().equals("Admin Utama")) {
                        Sequel.queryu("delete from sbar_tbak_ralan where no_rawat='" + tbSBAR.getValueAt(i, 1).toString()
                                + "' and tgl_perawatan='" + tbSBAR.getValueAt(i, 4).toString()
                                + "' and jam_rawat='" + tbSBAR.getValueAt(i, 5).toString() + "' ");
                        TabModeSBAR.removeRow(i);
                        i--;
                    } else {
                        if (Sequel.cekTanggal48jam(tbSBAR.getValueAt(i, 4).toString() + " " + tbSBAR.getValueAt(i, 5).toString(), Sequel.ambiltanggalsekarang()) == true) {
                            if (akses.getkode().equals(tbSBAR.getValueAt(tbSBAR.getSelectedRow(), 16).toString())) {
                                Sequel.queryu("delete from sbar_tbak_ralan where no_rawat='" + tbSBAR.getValueAt(i, 1).toString()
                                        + "' and tgl_perawatan='" + tbSBAR.getValueAt(i, 4).toString()
                                        + "' and jam_rawat='" + tbSBAR.getValueAt(i, 5).toString() + "' ");
                                TabModeSBAR.removeRow(i);
                                i--;
                            } else {
                                JOptionPane.showMessageDialog(null, "Hanya bisa dihapus oleh dokter/petugas yang bersangkutan..!!");
                            }
                        }
                    }
                }
            }
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

    private void tbSBARMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbSBARMouseClicked
        if(TabModeSBAR.getRowCount()!=0){
            try {
                getDataSBAR();
            } catch (java.lang.NullPointerException e) {
            }
        }
}//GEN-LAST:event_tbSBARMouseClicked

    private void tbSBARKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbSBARKeyPressed
        if(TabModeSBAR.getRowCount()!=0){
            if((evt.getKeyCode()==KeyEvent.VK_ENTER)||(evt.getKeyCode()==KeyEvent.VK_UP)||(evt.getKeyCode()==KeyEvent.VK_DOWN)){
                try {
                    getDataSBAR();
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
}//GEN-LAST:event_tbSBARKeyPressed

private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInputActionPerformed
  isForm();                
}//GEN-LAST:event_ChkInputActionPerformed

    private void TKdDPJPSBARKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TKdDPJPSBARKeyPressed
       
    }//GEN-LAST:event_TKdDPJPSBARKeyPressed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        try {
            BtnBatalActionPerformed(null);
        } catch (Exception e) {
        }
        
    }//GEN-LAST:event_formWindowOpened

    private void BtnSeekDokterSBARActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSeekDokterSBARActionPerformed
        akses.setform("DlgIGD");
        perawatan.dokter.isCek();
        perawatan.dokter.setSize(internalFrameSBAR.getWidth()-20,internalFrameSBAR.getHeight()-20);
        perawatan.dokter.setLocationRelativeTo(internalFrameSBAR);
//        perawatan.dokter.setAlwaysOnTop(true);
        perawatan.dokter.setVisible(true);
    }//GEN-LAST:event_BtnSeekDokterSBARActionPerformed

    private void TSituationSBARKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TSituationSBARKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TSituationSBARKeyPressed

    private void TBackgroundSBARKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TBackgroundSBARKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TBackgroundSBARKeyPressed

    private void TAssessmentKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TAssessmentKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TAssessmentKeyPressed

    private void TRecommendationSBARKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TRecommendationSBARKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TRecommendationSBARKeyPressed

    private void TAdviceSBARKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TAdviceSBARKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TAdviceSBARKeyPressed

    private void DTPTglKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DTPTglKeyPressed

    }//GEN-LAST:event_DTPTglKeyPressed

    private void cmbJamKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbJamKeyPressed
        Valid.pindah(evt,DTPTgl,cmbMnt);
    }//GEN-LAST:event_cmbJamKeyPressed

    private void cmbMntKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbMntKeyPressed
        Valid.pindah(evt,cmbJam,cmbDtk);
    }//GEN-LAST:event_cmbMntKeyPressed

    private void cmbDtkKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbDtkKeyPressed

    }//GEN-LAST:event_cmbDtkKeyPressed

    private void TNoRwActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TNoRwActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TNoRwActionPerformed

    private void cmbBacaSBARKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbBacaSBARKeyPressed
        
    }//GEN-LAST:event_cmbBacaSBARKeyPressed

    private void cmbKonfirmasiSBARKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbKonfirmasiSBARKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbKonfirmasiSBARKeyPressed

    private void TNoRMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TNoRMActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TNoRMActionPerformed

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed
        if ((!TSituationSBAR.getText().trim().equals("")) || (!TBackgroundSBAR.getText().trim().equals("")) || (!TAssessment.getText().trim().equals(""))
                || (!TRecommendationSBAR.getText().trim().equals("")) || (!TAdviceSBAR.getText().trim().equals(""))) {
            if (tbSBAR.getSelectedRow() > -1) {
                if (akses.getkode().equals("Admin Utama")) {
                    if (Sequel.mengedittf("sbar_tbak_ralan", "no_rawat='" + tbSBAR.getValueAt(tbSBAR.getSelectedRow(), 1)
                            + "' and tgl_perawatan='" + tbSBAR.getValueAt(tbSBAR.getSelectedRow(), 4)
                            + "' and jam_rawat='" + tbSBAR.getValueAt(tbSBAR.getSelectedRow(), 5) + "'",
                            " situation='" + TSituationSBAR.getText() + "',background='" + TBackgroundSBAR.getText() + "',"
                            + "assessment='" + TAssessment.getText() + "',recommendation='" + TRecommendationSBAR.getText() + "',advice='" + TAdviceSBAR.getText() + "',"
                            + "baca='" + cmbBacaSBAR.getSelectedItem().toString() + "',konfirmasi='" + cmbKonfirmasiSBAR.getSelectedItem().toString() + "',"
                            + "tgl_perawatan='" + Valid.SetTgl(DTPTgl.getSelectedItem() + "") + "',jam_rawat='" + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem() + "',"
                            + "tgl_tbak='" + (cmbBacaSBAR.getSelectedItem().toString().equals(tbSBAR.getValueAt(tbSBAR.getSelectedRow(), 12).toString()) || cmbKonfirmasiSBAR.getSelectedItem().toString().equals(tbSBAR.getValueAt(tbSBAR.getSelectedRow(), 13).toString()) ? Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem() : "0000-00-00 00:00:00") + "'")
                            == true) {
                        tampil();
                        BtnBatalActionPerformed(evt);
                    }
                } else {
                    if (akses.getkode().equals(tbSBAR.getValueAt(tbSBAR.getSelectedRow(), 16).toString())) {
                        if (Sequel.cekTanggal48jam(tbSBAR.getValueAt(tbSBAR.getSelectedRow(), 4) + " " + tbSBAR.getValueAt(tbSBAR.getSelectedRow(), 5), Sequel.ambiltanggalsekarang()) == true) {
                            if (TanggalRegistrasi.getText().equals("")) {
                                TanggalRegistrasi.setText(Sequel.cariIsi("select concat(reg_periksa.tgl_registrasi,' ',reg_periksa.jam_reg) from reg_periksa where reg_periksa.no_rawat=?", TNoRw.getText()));
                            }
                            if (Sequel.cekTanggalRegistrasi(TanggalRegistrasi.getText(), Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem()) == true) {
                                if (Sequel.mengedittf("sbar_tbak_ralan", "no_rawat='" + tbSBAR.getValueAt(tbSBAR.getSelectedRow(), 1)
                                        + "' and tgl_perawatan='" + tbSBAR.getValueAt(tbSBAR.getSelectedRow(), 4)
                                        + "' and jam_rawat='" + tbSBAR.getValueAt(tbSBAR.getSelectedRow(), 5) + "'",
                                        "situation='" + TSituationSBAR.getText() + "',background='" + TBackgroundSBAR.getText() + "',"
                                        + "assessment='" + TAssessment.getText() + "',recommendation='" + TRecommendationSBAR.getText() + "',advice='" + TAdviceSBAR.getText() + "',"
                                        + "baca='" + cmbBacaSBAR.getSelectedItem().toString() + "',konfirmasi='" + cmbKonfirmasiSBAR.getSelectedItem().toString() + "',"
                                        + "tgl_perawatan='" + Valid.SetTgl(DTPTgl.getSelectedItem() + "") + "',jam_rawat='" + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem() + "',"
                                        + "tgl_tbak='" + (cmbBacaSBAR.getSelectedItem().toString().equals(tbSBAR.getValueAt(tbSBAR.getSelectedRow(), 12).toString()) || cmbKonfirmasiSBAR.getSelectedItem().toString().equals(tbSBAR.getValueAt(tbSBAR.getSelectedRow(), 13).toString()) ? Valid.SetTgl(DTPTgl.getSelectedItem() + "") + " " + cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem() : "0000-00-00 00:00:00") + "'")
                                        == true) {
                                    tampil();
                                    BtnBatalActionPerformed(evt);
                                }
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Hanya bisa diganti oleh dokter/petugas yang bersangkutan..!!");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(rootPane, "Silahkan pilih data yang mau diganti..!!");
                TCari.requestFocus();
            }
        }
    }//GEN-LAST:event_BtnEditActionPerformed

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEditKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnEditActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnHapus, BtnEdit);
        }
    }//GEN-LAST:event_BtnEditKeyPressed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgSBARRalan dialog = new DlgSBARRalan(new javax.swing.JFrame(), true);
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
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnSeekDokterSBAR;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkInput;
    private widget.CekBox ChkJln;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.Tanggal DTPTgl;
    private widget.PanelBiasa FormInput;
    private widget.Label LCount;
    private javax.swing.JPanel PanelInput;
    private widget.ScrollPane Scroll;
    private widget.TextArea TAdviceSBAR;
    private widget.TextArea TAssessment;
    private widget.TextArea TBackgroundSBAR;
    private widget.TextBox TCari;
    private widget.TextBox TKdDPJPSBAR;
    private widget.TextBox TKdPetugasSBAR;
    private widget.TextBox TNmDPJPSBAR;
    private widget.TextBox TNmPetugasSBAR;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.TextArea TRecommendationSBAR;
    private widget.TextArea TSituationSBAR;
    private widget.TextBox TanggalRegistrasi;
    private widget.ComboBox cmbBacaSBAR;
    private widget.ComboBox cmbDtk;
    private widget.ComboBox cmbJam;
    private widget.ComboBox cmbKonfirmasiSBAR;
    private widget.ComboBox cmbMnt;
    private widget.InternalFrame internalFrameSBAR;
    private widget.Label jLabel10;
    private widget.Label jLabel13;
    private widget.Label jLabel19;
    private widget.Label jLabel21;
    private widget.Label jLabel38;
    private widget.Label jLabel39;
    private widget.Label jLabel4;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.Label jLabel84;
    private widget.Label jLabel85;
    private widget.Label jLabel87;
    private widget.Label jLabel88;
    private widget.Label jLabel89;
    private javax.swing.JPanel jPanel3;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.ScrollPane scrollPane15;
    private widget.ScrollPane scrollPane16;
    private widget.ScrollPane scrollPane17;
    private widget.ScrollPane scrollPane18;
    private widget.ScrollPane scrollPane19;
    private widget.Table tbSBAR;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        Valid.tabelKosong(TabModeSBAR);
        try{  
            psSBAR=koneksi.prepareStatement("SELECT reg_periksa.no_rawat, reg_periksa.no_rkm_medis, pasien.nm_pasien, " +
                "sbar_tbak_ralan.tgl_perawatan, sbar_tbak_ralan.jam_rawat, sbar_tbak_ralan.situation, sbar_tbak_ralan.background, " +
                "sbar_tbak_ralan.assessment, sbar_tbak_ralan.recommendation, sbar_tbak_ralan.advice, sbar_tbak_ralan.baca, sbar_tbak_ralan.konfirmasi, " +
                "sbar_tbak_ralan.verifikasi_dpjp, sbar_tbak_ralan.kd_dokter, sbar_tbak_ralan.nip, pegawai.nama, dokter.nm_dokter, sbar_tbak_ralan.tgl_tbak,sbar_tbak_ralan.tgl_verifikasi " +
                "from reg_periksa " +
                "INNER JOIN pasien ON pasien.no_rkm_medis = reg_periksa.no_rkm_medis " +
                "INNER JOIN sbar_tbak_ralan ON sbar_tbak_ralan.no_rawat = reg_periksa.no_rawat " +
                "INNER JOIN pegawai on sbar_tbak_ralan.nip=pegawai.nik " +
                "INNER JOIN dokter ON dokter.kd_dokter = sbar_tbak_ralan.kd_dokter " +
                "WHERE sbar_tbak_ralan.no_rawat = ? " + 
                (TCari.getText().trim().equals("")?"":" and (sbar_tbak_ralan.no_rawat like ? or reg_periksa.no_rkm_medis like ? or pasien.nm_pasien like ? or "+
                "sbar_tbak_ralan.situation like ? or sbar_tbak_ralan.background like ? or sbar_tbak_ralan.assessment like ? or "+
                "sbar_tbak_ralan.recommendation like ? or sbar_tbak_ralan.advice like ? or pegawai.nama like ? or dokter.nm_dokter like ? )")+
                "order by sbar_tbak_ralan.no_rawat,sbar_tbak_ralan.tgl_perawatan,sbar_tbak_ralan.jam_rawat desc"); 
            try{
                psSBAR.setString(1,TNoRw.getText());
                if(!TCari.getText().trim().equals("")){
                    psSBAR.setString(2,"%"+TCari.getText().trim()+"%");
                    psSBAR.setString(3,"%"+TCari.getText().trim()+"%");
                    psSBAR.setString(4,"%"+TCari.getText().trim()+"%");
                    psSBAR.setString(5,"%"+TCari.getText().trim()+"%");
                    psSBAR.setString(6,"%"+TCari.getText().trim()+"%");
                    psSBAR.setString(7,"%"+TCari.getText().trim()+"%");
                    psSBAR.setString(8,"%"+TCari.getText().trim()+"%");
                    psSBAR.setString(9,"%"+TCari.getText().trim()+"%");
                    psSBAR.setString(10,"%"+TCari.getText().trim()+"%");
                    psSBAR.setString(11,"%"+TCari.getText().trim()+"%");
                }
                    
                rssbar=psSBAR.executeQuery();
                while(rssbar.next()){
                    TabModeSBAR.addRow(new Object[]{
                        false,rssbar.getString("no_rawat"),rssbar.getString("no_rkm_medis"),rssbar.getString("nm_pasien"),
                        rssbar.getString("tgl_perawatan"),rssbar.getString("jam_rawat"),rssbar.getString("nm_dokter"),rssbar.getString("situation"),
                        rssbar.getString("background"),rssbar.getString("assessment"),rssbar.getString("recommendation"),rssbar.getString("advice"),
                        rssbar.getString("baca"),rssbar.getString("konfirmasi"),rssbar.getString("nama"),rssbar.getString("kd_dokter"),
                        rssbar.getString("nip"),rssbar.getString("verifikasi_dpjp"),rssbar.getString("tgl_verifikasi")
                    });
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            } finally{
                if(rssbar!=null){
                    rssbar.close();
                }
                if(psSBAR!=null){
                    psSBAR.close();
                }
            }                  
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }
        LCount.setText(""+TabModeSBAR.getRowCount());
    }
    
    private void getDataSBAR() {
     if(tbSBAR.getSelectedRow()!= -1){
//            TNoRw.setText(tbPemeriksaan.getValueAt(tbPemeriksaan.getSelectedRow(),1).toString());
            TNoRM.setText(tbSBAR.getValueAt(tbSBAR.getSelectedRow(),2).toString());
            TPasien.setText(tbSBAR.getValueAt(tbSBAR.getSelectedRow(),3).toString());  
            cmbJam.setSelectedItem(tbSBAR.getValueAt(tbSBAR.getSelectedRow(),5).toString().substring(0,2));
            cmbMnt.setSelectedItem(tbSBAR.getValueAt(tbSBAR.getSelectedRow(),5).toString().substring(3,5));
            cmbDtk.setSelectedItem(tbSBAR.getValueAt(tbSBAR.getSelectedRow(),5).toString().substring(6,8));
            TNmDPJPSBAR.setText(tbSBAR.getValueAt(tbSBAR.getSelectedRow(),6).toString()); 
            TSituationSBAR.setText(tbSBAR.getValueAt(tbSBAR.getSelectedRow(),7).toString()); 
            TBackgroundSBAR.setText(tbSBAR.getValueAt(tbSBAR.getSelectedRow(),8).toString()); 
            TAssessment.setText(tbSBAR.getValueAt(tbSBAR.getSelectedRow(),9).toString()); 
            TRecommendationSBAR.setText(tbSBAR.getValueAt(tbSBAR.getSelectedRow(),10).toString()); 
            TAdviceSBAR.setText(tbSBAR.getValueAt(tbSBAR.getSelectedRow(),11).toString()); 
            cmbBacaSBAR.setSelectedItem(tbSBAR.getValueAt(tbSBAR.getSelectedRow(),12).toString()); 
            cmbKonfirmasiSBAR.setSelectedItem(tbSBAR.getValueAt(tbSBAR.getSelectedRow(),13).toString()); 
            TNmPetugasSBAR.setText(tbSBAR.getValueAt(tbSBAR.getSelectedRow(),14).toString()); 
            TKdDPJPSBAR.setText(tbSBAR.getValueAt(tbSBAR.getSelectedRow(),15).toString()); 
            TKdPetugasSBAR.setText(tbSBAR.getValueAt(tbSBAR.getSelectedRow(),16).toString());
        }
    }
    
    private void emptySBAR(){
            TNmDPJPSBAR.setText(""); 
            TSituationSBAR.setText(""); 
            TBackgroundSBAR.setText(""); 
            TAssessment.setText(""); 
            TRecommendationSBAR.setText(""); 
            TAdviceSBAR.setText(""); 
            cmbBacaSBAR.setSelectedItem("Sudah"); 
            cmbKonfirmasiSBAR.setSelectedItem("Sudah"); 
            TKdDPJPSBAR.setText(""); 
    }


    private void isRawat() {        
       try {
            ps=koneksi.prepareStatement(
                    "select reg_periksa.no_rkm_medis,concat(pasien.nm_pasien,' (',pasien.umur,')') as pasien,reg_periksa.kd_dokter,reg_periksa.tgl_registrasi,"+
                    "reg_periksa.jam_reg from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis where reg_periksa.no_rawat=?");
            try {
                ps.setString(1,TNoRw.getText());
                rs=ps.executeQuery();
                if(rs.next()){
                    TNoRM.setText(rs.getString("no_rkm_medis"));
                    TCari.setText(TNoRM.getText());
                    TPasien.setText(rs.getString("pasien"));
                    TKdDPJPSBAR.setText(Sequel.cariIsi("select dpjp_ranap.kd_dokter from dpjp_ranap where dpjp_ranap.no_rawat=?",TNoRw.getText()));
                    if(TKdDPJPSBAR.getText().equals("")){
                        TKdDPJPSBAR.setText(rs.getString("kd_dokter"));
                    }
                    TNmDPJPSBAR.setText(perawatan.dokter.tampil3(TKdDPJPSBAR.getText()));
                    TanggalRegistrasi.setText(rs.getString("tgl_registrasi")+" "+rs.getString("jam_reg"));
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
        } catch (Exception e) {
            System.out.println("Notif : "+e);
        }
    }
    
    public void setNoRm(String norwt,Date awal,Date akhir) {
        ChkJln.setSelected(true);
        TNoRw.setText(norwt);
        isRawat();
        DTPCari1.setDate(awal);
        DTPCari2.setDate(akhir);
        TCari.setText(norwt);
        ChkInput.setSelected(true);
        isForm();
        date = new Date();
        DTPTgl.setSelectedItem(tanggalFormat.format(date));
    }
    
    private void isForm(){
        headerCheckBoxSBAR.setSelected(false);
        tbSBAR.getTableHeader().repaint();
        if(ChkInput.isSelected()==true){
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH,300));
            FormInput.setVisible(true);      
            ChkInput.setVisible(true);
        }else if(ChkInput.isSelected()==false){           
            ChkInput.setVisible(false);            
            PanelInput.setPreferredSize(new Dimension(WIDTH,20));
            FormInput.setVisible(false);      
            ChkInput.setVisible(true);
        }
        TKdPetugasSBAR.setText(akses.getkode());
        TNmPetugasSBAR.setText(pegawai.tampil3(TKdPetugasSBAR.getText()));
    }
    
//    public void isCek(){
//        BtnSimpan.setEnabled(akses.getpermintaan_ranap());
//        BtnHapus.setEnabled(akses.getpermintaan_ranap());
//        if(!akses.getkode().equals("Admin Utama")){
//           String jabatan = Sequel.cariIsi("select kd_jbtn from petugas where nip =?", akses.getkode());
//            if(jabatan.equals("J005")){
//                 DTPTgl.setEnabled(true);
//             }else{
//                 DTPTgl.setEnabled(false);
//             }
//        }
//    }
    
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
}
