/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DlgRujuk.java
 *
 * Created on 31 Mei 10, 20:19:56
 */

package rekammedis;

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
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariDokter;
import laporan.DlgBerkasRawat;
import laporan.DlgDiagnosaPenyakit;


/**
 *
 * @author perpustakaan
 */
public final class RMKonsultasiDokter extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private Connection koneksi=koneksiDB.condb();
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i=0;    
    private DlgCariDokter dokter=new DlgCariDokter(null,false);
    private DlgCariDokter dokter2=new DlgCariDokter(null,false);
    private RMCariKeluhan carikeluhan=new RMCariKeluhan(null,false);
    private RMCariHasilRadiologi cariradiologi=new RMCariHasilRadiologi(null,false);
    private RMCariHasilLaborat carilaborat=new RMCariHasilLaborat(null,false);
    private RMCariJumlahObat cariobat=new RMCariJumlahObat(null,false);
    private DlgDiagnosaPenyakit penyakit=new DlgDiagnosaPenyakit(null,false);
    private String tanggal="",finger1="",finger2="",variabel="";
    
    /** Creates new form DlgRujuk
     * @param parent
     * @param modal */
    public RMKonsultasiDokter(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        tabMode=new DefaultTableModel(null,new Object[]{
            "No.Rawat","No.RM","Nama Pasien", "Tgl Lahir","Umur","Ruang","Tgl Konsultasi","Kode Dokter Konsulen","Dokter Konsulen","Kode Dokter DPJP","Dokter DPJP", "Konsul Dokter", "Jawaban Konsulen"
        }){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        tbObat.setModel(tabMode);

        //tbObat.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbObat.getBackground()));
        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 13; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(120);
            }else if(i==1){
                column.setPreferredWidth(90);
            }else if(i==2){
                column.setPreferredWidth(150);
            }else if(i==3){
                column.setPreferredWidth(90);
            }else if(i==4){
                column.setPreferredWidth(90);
            }else if(i==5){
                column.setPreferredWidth(150);
            }else if(i==6){
                column.setPreferredWidth(100);
            }else if(i==7){
                 column.setMinWidth(0);
                column.setMaxWidth(0);
            }else if(i==8){
                column.setPreferredWidth(150);
            }else if(i==9){
                 column.setMinWidth(0);
                column.setMaxWidth(0);
            }else if(i==10){
                column.setPreferredWidth(150);
            }else if(i==11){
                column.setPreferredWidth(300);
            }else if(i==12){
                column.setPreferredWidth(300);
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRw.setDocument(new batasInput((byte)17).getKata(TNoRw));
        KonsulDPJP.setDocument(new batasInput((int)2000).getKata(KonsulDPJP));
        JawabanDPJP.setDocument(new batasInput((int)2000).getKata(JawabanDPJP));
        TCari.setDocument(new batasInput((int)100).getKata(TCari));
        
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
        
        dokter.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(dokter.getTable().getSelectedRow()!= -1){
                    KodeDokterKonsulen.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),0).toString());
                    NamaDokterKonsulen.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),1).toString());
                    KodeDokterKonsulen.requestFocus();
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
        
        dokter2.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(dokter2.getTable().getSelectedRow()!= -1){
                    KodeDPJP.setText(dokter2.getTable().getValueAt(dokter2.getTable().getSelectedRow(),0).toString());
                    NamaDPJP.setText(dokter2.getTable().getValueAt(dokter2.getTable().getSelectedRow(),1).toString());
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
        
        
        
        ChkInput.setSelected(false);
        isForm();
      
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
        MnLembarKonsultasi = new javax.swing.JMenuItem();
        Tanggal = new widget.TextBox();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
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
        ChkInput = new widget.CekBox();
        scrollInput = new widget.ScrollPane();
        FormInput = new widget.PanelBiasa();
        jLabel4 = new widget.Label();
        TNoRw = new widget.TextBox();
        TPasien = new widget.TextBox();
        TNoRM = new widget.TextBox();
        scrollPane2 = new widget.ScrollPane();
        KonsulDPJP = new widget.TextArea();
        jLabel5 = new widget.Label();
        jLabel8 = new widget.Label();
        scrollPane3 = new widget.ScrollPane();
        JawabanDPJP = new widget.TextArea();
        labelDokterKonsulen = new widget.Label();
        KodeDokterKonsulen = new widget.TextBox();
        NamaDokterKonsulen = new widget.TextBox();
        BtnDokterKonsulen = new widget.Button();
        jLabelDPJP = new widget.Label();
        KodeDPJP = new widget.TextBox();
        NamaDPJP = new widget.TextBox();
        BtnDPJP = new widget.Button();
        jLabelTL = new widget.Label();
        TTglLahir = new widget.TextBox();
        jLabelRuang = new widget.Label();
        TRuang = new widget.TextBox();
        jLabelTglMasuk = new widget.Label();
        jLabelUmur = new widget.Label();
        TUmur = new widget.TextBox();
        tgl = new widget.Tanggal();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnLembarKonsultasi.setBackground(new java.awt.Color(255, 255, 254));
        MnLembarKonsultasi.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnLembarKonsultasi.setForeground(new java.awt.Color(50, 50, 50));
        MnLembarKonsultasi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnLembarKonsultasi.setText("Lembar Konsultasi");
        MnLembarKonsultasi.setName("MnLembarKonsultasi"); // NOI18N
        MnLembarKonsultasi.setPreferredSize(new java.awt.Dimension(250, 26));
        MnLembarKonsultasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnLembarKonsultasiActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnLembarKonsultasi);

        Tanggal.setHighlighter(null);
        Tanggal.setName("Tanggal"); // NOI18N

        jLayeredPane1.setName("jLayeredPane1"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Data Lembar Konsultasi ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
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

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(80, 23));
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
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "28-04-2025" }));
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
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "28-04-2025" }));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2"); // NOI18N
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari2);

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(310, 23));
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
        PanelInput.setPreferredSize(new java.awt.Dimension(192, 448));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

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

        scrollInput.setName("scrollInput"); // NOI18N

        FormInput.setBackground(new java.awt.Color(250, 255, 245));
        FormInput.setBorder(null);
        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(100, 651));
        FormInput.setLayout(null);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel4.setText("Konsul Dokter DPJP :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(30, 100, 230, 23);

        TNoRw.setEditable(false);
        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw"); // NOI18N
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });
        FormInput.add(TNoRw);
        TNoRw.setBounds(104, 10, 100, 23);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N
        TPasien.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TPasienKeyPressed(evt);
            }
        });
        FormInput.add(TPasien);
        TPasien.setBounds(310, 10, 230, 23);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM"); // NOI18N
        TNoRM.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRMKeyPressed(evt);
            }
        });
        FormInput.add(TNoRM);
        TNoRM.setBounds(210, 10, 90, 23);

        scrollPane2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane2.setName("scrollPane2"); // NOI18N

        KonsulDPJP.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        KonsulDPJP.setColumns(20);
        KonsulDPJP.setRows(5);
        KonsulDPJP.setName("KonsulDPJP"); // NOI18N
        KonsulDPJP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KonsulDPJPKeyPressed(evt);
            }
        });
        scrollPane2.setViewportView(KonsulDPJP);

        FormInput.add(scrollPane2);
        scrollPane2.setBounds(30, 120, 390, 510);

        jLabel5.setText("No.Rawat :");
        jLabel5.setName("jLabel5"); // NOI18N
        FormInput.add(jLabel5);
        jLabel5.setBounds(0, 10, 100, 23);

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText("Jawaban Konsultan :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(460, 100, 190, 23);

        scrollPane3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane3.setName("scrollPane3"); // NOI18N

        JawabanDPJP.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        JawabanDPJP.setColumns(20);
        JawabanDPJP.setRows(5);
        JawabanDPJP.setName("JawabanDPJP"); // NOI18N
        JawabanDPJP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JawabanDPJPKeyPressed(evt);
            }
        });
        scrollPane3.setViewportView(JawabanDPJP);

        FormInput.add(scrollPane3);
        scrollPane3.setBounds(460, 120, 410, 510);

        labelDokterKonsulen.setText("Dokter Konsulen :");
        labelDokterKonsulen.setName("labelDokterKonsulen"); // NOI18N
        labelDokterKonsulen.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(labelDokterKonsulen);
        labelDokterKonsulen.setBounds(10, 70, 90, 23);

        KodeDokterKonsulen.setEditable(false);
        KodeDokterKonsulen.setName("KodeDokterKonsulen"); // NOI18N
        KodeDokterKonsulen.setPreferredSize(new java.awt.Dimension(80, 23));
        KodeDokterKonsulen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KodeDokterKonsulenKeyPressed(evt);
            }
        });
        FormInput.add(KodeDokterKonsulen);
        KodeDokterKonsulen.setBounds(100, 70, 70, 23);

        NamaDokterKonsulen.setEditable(false);
        NamaDokterKonsulen.setName("NamaDokterKonsulen"); // NOI18N
        NamaDokterKonsulen.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(NamaDokterKonsulen);
        NamaDokterKonsulen.setBounds(180, 70, 220, 23);

        BtnDokterKonsulen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnDokterKonsulen.setMnemonic('2');
        BtnDokterKonsulen.setToolTipText("Alt+2");
        BtnDokterKonsulen.setName("BtnDokterKonsulen"); // NOI18N
        BtnDokterKonsulen.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnDokterKonsulen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnDokterKonsulenActionPerformed(evt);
            }
        });
        BtnDokterKonsulen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnDokterKonsulenKeyPressed(evt);
            }
        });
        FormInput.add(BtnDokterKonsulen);
        BtnDokterKonsulen.setBounds(400, 70, 28, 23);

        jLabelDPJP.setText("Dokter DPJP :");
        jLabelDPJP.setName("jLabelDPJP"); // NOI18N
        jLabelDPJP.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(jLabelDPJP);
        jLabelDPJP.setBounds(460, 70, 70, 23);

        KodeDPJP.setEditable(false);
        KodeDPJP.setName("KodeDPJP"); // NOI18N
        KodeDPJP.setPreferredSize(new java.awt.Dimension(80, 23));
        KodeDPJP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KodeDPJPKeyPressed(evt);
            }
        });
        FormInput.add(KodeDPJP);
        KodeDPJP.setBounds(530, 70, 70, 23);

        NamaDPJP.setEditable(false);
        NamaDPJP.setName("NamaDPJP"); // NOI18N
        NamaDPJP.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(NamaDPJP);
        NamaDPJP.setBounds(610, 70, 220, 23);

        BtnDPJP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnDPJP.setMnemonic('2');
        BtnDPJP.setToolTipText("Alt+2");
        BtnDPJP.setName("BtnDPJP"); // NOI18N
        BtnDPJP.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnDPJP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnDPJPActionPerformed(evt);
            }
        });
        BtnDPJP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnDPJPKeyPressed(evt);
            }
        });
        FormInput.add(BtnDPJP);
        BtnDPJP.setBounds(830, 70, 28, 23);

        jLabelTL.setText("Tgl.Lahir :");
        jLabelTL.setName("jLabelTL"); // NOI18N
        FormInput.add(jLabelTL);
        jLabelTL.setBounds(550, 10, 50, 23);

        TTglLahir.setEditable(false);
        TTglLahir.setHighlighter(null);
        TTglLahir.setName("TTglLahir"); // NOI18N
        TTglLahir.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TTglLahirKeyPressed(evt);
            }
        });
        FormInput.add(TTglLahir);
        TTglLahir.setBounds(600, 10, 90, 23);

        jLabelRuang.setText("Ruang/Poli :");
        jLabelRuang.setName("jLabelRuang"); // NOI18N
        FormInput.add(jLabelRuang);
        jLabelRuang.setBounds(30, 40, 70, 23);

        TRuang.setEditable(false);
        TRuang.setHighlighter(null);
        TRuang.setName("TRuang"); // NOI18N
        TRuang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TRuangKeyPressed(evt);
            }
        });
        FormInput.add(TRuang);
        TRuang.setBounds(100, 40, 310, 23);

        jLabelTglMasuk.setText("Tgl. Konsultasi :");
        jLabelTglMasuk.setName("jLabelTglMasuk"); // NOI18N
        FormInput.add(jLabelTglMasuk);
        jLabelTglMasuk.setBounds(440, 40, 90, 23);

        jLabelUmur.setText("Umur :");
        jLabelUmur.setName("jLabelUmur"); // NOI18N
        FormInput.add(jLabelUmur);
        jLabelUmur.setBounds(690, 10, 40, 23);

        TUmur.setEditable(false);
        TUmur.setHighlighter(null);
        TUmur.setName("TUmur"); // NOI18N
        TUmur.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TUmurKeyPressed(evt);
            }
        });
        FormInput.add(TUmur);
        TUmur.setBounds(730, 10, 60, 23);

        tgl.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        tgl.setName("tgl"); // NOI18N
        tgl.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tglKeyPressed(evt);
            }
        });
        FormInput.add(tgl);
        tgl.setBounds(540, 40, 150, 23);

        scrollInput.setViewportView(FormInput);

        PanelInput.add(scrollInput, java.awt.BorderLayout.CENTER);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRwKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            isRawat();
            isPsien();
        }else{            
            Valid.pindah(evt,TCari,BtnDokterKonsulen);
        }
}//GEN-LAST:event_TNoRwKeyPressed

    private void TPasienKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TPasienKeyPressed
        Valid.pindah(evt,TCari,BtnSimpan);
}//GEN-LAST:event_TPasienKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if(TNoRw.getText().equals("")||TNoRM.getText().equals("")||TPasien.getText().equals("")){
            Valid.textKosong(TNoRw,"Pasien");
        }else if(KodeDokterKonsulen.getText().equals("")||NamaDokterKonsulen.getText().equals("")){
            Valid.textKosong(BtnDokterKonsulen,"Dokter Konsulen");
        }else if(KodeDPJP.getText().equals("")||NamaDPJP.getText().equals("")){
            Valid.textKosong(BtnDPJP,"Dokter DPJP");
        }else if(KonsulDPJP.getText().equals("")){
            Valid.textKosong(KonsulDPJP,"Konsul Dokter DPJP");
        }else if(JawabanDPJP.getText().equals("")){
            Valid.textKosong(KonsulDPJP,"Konsul Dokter DPJP");
        }else{
            if(Sequel.menyimpantf("lembar_konsultasi","?,?,?,?,?,?,?,?","No.Rawat",7,new String[]{
                    TNoRw.getText(),TRuang.getText(), KodeDokterKonsulen.getText(),
                    KodeDPJP.getText(), KonsulDPJP.getText(), JawabanDPJP.getText(), Valid.SetTgl(tgl.getSelectedItem()+"")+" "+tgl.getSelectedItem().toString().substring(11,19)
                })==true){
                tabMode.addRow(new String[]{
                    TNoRw.getText(),TNoRM.getText(), TPasien.getText(), TTglLahir.getText(), TUmur.getText(), TRuang.getText(),
                    Valid.SetTgl(tgl.getSelectedItem()+"")+" "+tgl.getSelectedItem().toString().substring(11,19),
                    KodeDokterKonsulen.getText(), NamaDokterKonsulen.getText(), KodeDPJP.getText(), NamaDPJP.getText(),
                    KonsulDPJP.getText(), JawabanDPJP.getText()
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
        if(tbObat.getSelectedRow()>-1){
            if(akses.getkode().equals("Admin Utama")){
                hapus();
            }else{
                if(KodeDokterKonsulen.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(),4).toString())){
                    hapus();
                }else{
                    JOptionPane.showMessageDialog(null,"Hanya bisa dihapus oleh dokter yang bersangkutan..!!");
                }
            }
        }else{
            JOptionPane.showMessageDialog(rootPane,"Silahkan anda pilih data terlebih dahulu..!!");
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
        if(TNoRw.getText().equals("")||TNoRM.getText().equals("")||TPasien.getText().equals("")){
            Valid.textKosong(TNoRw,"Pasien");
        }else if(KodeDokterKonsulen.getText().equals("")||NamaDokterKonsulen.getText().equals("")){
            Valid.textKosong(BtnDokterKonsulen,"Dokter Konsulen");
        }else if(KodeDPJP.getText().equals("")||NamaDPJP.getText().equals("")){
            Valid.textKosong(BtnDPJP,"Dokter DPJP");
        }else if(KonsulDPJP.getText().equals("")){
            Valid.textKosong(KonsulDPJP,"Konsul Dokter DPJP");
        }else if(JawabanDPJP.getText().equals("")){
            Valid.textKosong(KonsulDPJP,"Konsul Dokter DPJP");
        }else{
            if(tbObat.getSelectedRow()>-1){
                if(akses.getkode().equals("Admin Utama")){
                    ganti();
                }else{
                    if(KodeDokterKonsulen.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(), 10).toString())){
                        ganti();
                    }else{
                        JOptionPane.showMessageDialog(null,"Hanya bisa diganti oleh dokter yang bersangkutan..!!");
                    }
                }
            }else{
                JOptionPane.showMessageDialog(rootPane,"Silahkan anda pilih data terlebih dahulu..!!");
            }
        }
}//GEN-LAST:event_BtnEditActionPerformed

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEditKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnEditActionPerformed(null);
        }else{
            
        }
}//GEN-LAST:event_BtnEditKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dokter.dispose();
        dokter2.dispose();
        penyakit.dispose();
        dispose();
}//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnKeluarActionPerformed(null);
        }else{Valid.pindah(evt,BtnEdit,TCari);}
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

    private void tbObatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbObatKeyPressed
        if(tabMode.getRowCount()!=0){
            if((evt.getKeyCode()==KeyEvent.VK_ENTER)||(evt.getKeyCode()==KeyEvent.VK_UP)||(evt.getKeyCode()==KeyEvent.VK_DOWN)){
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }else if(evt.getKeyCode()==KeyEvent.VK_SPACE){
                try {
                    ChkInput.setSelected(true);
                    isForm(); 
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
}//GEN-LAST:event_tbObatKeyPressed

    private void KodeDokterKonsulenKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KodeDokterKonsulenKeyPressed
       
    }//GEN-LAST:event_KodeDokterKonsulenKeyPressed

    private void BtnDokterKonsulenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDokterKonsulenActionPerformed
        dokter.emptTeks();
        dokter.isCek();
        dokter.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setVisible(true);
    }//GEN-LAST:event_BtnDokterKonsulenActionPerformed

    private void BtnDokterKonsulenKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnDokterKonsulenKeyPressed
       
    }//GEN-LAST:event_BtnDokterKonsulenKeyPressed

    private void KonsulDPJPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KonsulDPJPKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            if(evt.isShiftDown()){
                JawabanDPJP.requestFocus();
            }
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
           
        }
    }//GEN-LAST:event_KonsulDPJPKeyPressed

    private void JawabanDPJPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_JawabanDPJPKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            if(evt.isShiftDown()){
            }
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            KonsulDPJP.requestFocus();
        }
    }//GEN-LAST:event_JawabanDPJPKeyPressed

    private void MnLembarKonsultasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnLembarKonsultasiActionPerformed
        if(tbObat.getSelectedRow()>-1){
            Map<String, Object> param = new HashMap<>();    
            param.put("namars",akses.getnamars());
            param.put("alamatrs",akses.getalamatrs());
            param.put("kotars",akses.getkabupatenrs());
            param.put("propinsirs",akses.getpropinsirs());
            param.put("kontakrs",akses.getkontakrs());
            param.put("emailrs",akses.getemailrs());   
            param.put("logo",Sequel.cariGambar("select setting.logo from setting")); 
            param.put("norawat",tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());
            param.put("tgl_konsultasi",tbObat.getValueAt(tbObat.getSelectedRow(),7).toString());
            finger1=Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?",tbObat.getValueAt(tbObat.getSelectedRow(),8).toString());
            param.put("finger1","Dikeluarkan di "+akses.getnamars()+", Kabupaten/Kota "+akses.getkabupatenrs()+"\nDitandatangani secara elektronik oleh "+tbObat.getValueAt(tbObat.getSelectedRow(),9).toString()+"\nID "+(finger1.equals("")?tbObat.getValueAt(tbObat.getSelectedRow(),8).toString():finger1)+"\n"+tanggal); 
            finger2=Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?",tbObat.getValueAt(tbObat.getSelectedRow(),10).toString());
            param.put("finger2","Dikeluarkan di "+akses.getnamars()+", Kabupaten/Kota "+akses.getkabupatenrs()+"\nDitandatangani secara elektronik oleh "+tbObat.getValueAt(tbObat.getSelectedRow(),11).toString()+"\nID "+(finger2.equals("")?tbObat.getValueAt(tbObat.getSelectedRow(),10).toString():finger2)+"\n"+tanggal); 
            
            Valid.MyReport("rptLembarKonsultasi.jasper","report","::[ Lembar Konsultasi ]::",param);
        }
    }//GEN-LAST:event_MnLembarKonsultasiActionPerformed

    private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInputActionPerformed
        isForm();
    }//GEN-LAST:event_ChkInputActionPerformed

    private void KodeDPJPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KodeDPJPKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_KodeDPJPKeyPressed

    private void BtnDPJPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDPJPActionPerformed
        dokter2.emptTeks();
        dokter2.isCek();
        dokter2.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        dokter2.setLocationRelativeTo(internalFrame1);
        dokter2.setVisible(true);
    }//GEN-LAST:event_BtnDPJPActionPerformed

    private void BtnDPJPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnDPJPKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnDPJPKeyPressed

    private void TTglLahirKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TTglLahirKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TTglLahirKeyPressed

    private void TRuangKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TRuangKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TRuangKeyPressed

    private void TUmurKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TUmurKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TUmurKeyPressed

    private void tglKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tglKeyPressed

    }//GEN-LAST:event_tglKeyPressed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMKonsultasiDokter dialog = new RMKonsultasiDokter(new javax.swing.JFrame(), true);
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
    private widget.Button BtnDPJP;
    private widget.Button BtnDokterKonsulen;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkInput;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.PanelBiasa FormInput;
    private widget.TextArea JawabanDPJP;
    private widget.TextBox KodeDPJP;
    private widget.TextBox KodeDokterKonsulen;
    private widget.TextArea KonsulDPJP;
    private widget.Label LCount;
    private javax.swing.JMenuItem MnLembarKonsultasi;
    private widget.TextBox NamaDPJP;
    private widget.TextBox NamaDokterKonsulen;
    private javax.swing.JPanel PanelInput;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.TextBox TRuang;
    private widget.TextBox TTglLahir;
    private widget.TextBox TUmur;
    private widget.TextBox Tanggal;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel19;
    private widget.Label jLabel21;
    private widget.Label jLabel4;
    private widget.Label jLabel5;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.Label jLabelDPJP;
    private widget.Label jLabelRuang;
    private widget.Label jLabelTL;
    private widget.Label jLabelTglMasuk;
    private widget.Label jLabelUmur;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.Label labelDokterKonsulen;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.ScrollPane scrollInput;
    private widget.ScrollPane scrollPane2;
    private widget.ScrollPane scrollPane3;
    private widget.Table tbObat;
    private widget.Tanggal tgl;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try{
            if(TCari.getText().equals("")){
                ps=koneksi.prepareStatement(
                    "SELECT lembar_konsultasi.no_rawat, reg_periksa.no_rkm_medis, pasien.nm_pasien, pasien.jk, pasien.tgl_lahir, CONCAT(reg_periksa.umurdaftar, ' ', reg_periksa.sttsumur) as umur,\n" +
                    "lembar_konsultasi.ruang, lembar_konsultasi.tgl_konsultasi,\n" +
                    "lembar_konsultasi.kd_dokter_konsul, dokter1.nm_dokter as dokter_konsul, lembar_konsultasi.kd_dokter_dpjp, \n" +
                    "dokter2.nm_dokter as dokter_dpjp, lembar_konsultasi.konsul_dpjp, lembar_konsultasi.jawaban_konsul\n" +
                    "FROM lembar_konsultasi\n" +
                    "INNER JOIN reg_periksa ON reg_periksa.no_rawat = lembar_konsultasi.no_rawat\n" +
                    "INNER JOIN pasien ON pasien.no_rkm_medis = reg_periksa.no_rkm_medis\n" +
                    "INNER JOIN dokter as dokter1 ON dokter1.kd_dokter = lembar_konsultasi.kd_dokter_konsul\n" +
                    "INNER JOIN dokter as dokter2 ON dokter2.kd_dokter = lembar_konsultasi.kd_dokter_dpjp\n" +
                    "where lembar_konsultasi.tgl_konsultasi between ? and ? order by lembar_konsultasi.tgl_konsultasi");
            }else{
                ps=koneksi.prepareStatement(
                    "SELECT lembar_konsultasi.no_rawat, reg_periksa.no_rkm_medis, pasien.nm_pasien, pasien.jk, pasien.tgl_lahir, CONCAT(reg_periksa.umurdaftar, ' ', reg_periksa.sttsumur) as umur,\n" +
                    "lembar_konsultasi.ruang, lembar_konsultasi.tgl_konsultasi,\n" +
                    "lembar_konsultasi.kd_dokter_konsul, dokter1.nm_dokter as dokter_konsul, lembar_konsultasi.kd_dokter_dpjp, \n" +
                    "dokter2.nm_dokter as dokter_dpjp, lembar_konsultasi.konsul_dpjp, lembar_konsultasi.jawaban_konsul\n" +
                    "FROM lembar_konsultasi\n" +
                    "INNER JOIN reg_periksa ON reg_periksa.no_rawat = lembar_konsultasi.no_rawat\n" +
                    "INNER JOIN pasien ON pasien.no_rkm_medis = reg_periksa.no_rkm_medis\n" +
                    "INNER JOIN dokter as dokter1 ON dokter1.kd_dokter = lembar_konsultasi.kd_dokter_konsul\n" +
                    "INNER JOIN dokter as dokter2 ON dokter2.kd_dokter = lembar_konsultasi.kd_dokter_dpjp\n" +
                    "where lembar_konsultasi.tgl_konsultasi between ? and ? and \n" +
                    "(lembar_konsultasi.no_rawat LIKE ? or reg_periksa.no_rkm_medis like ? or pasien.nm_pasien like ? or lembar_konsultasi.ruang like ? or\n" +
                    "dokter1.nm_dokter like ? or dokter2.nm_dokter like ?) order by lembar_konsultasi.tgl_konsultasi");
            }
            try {
                if(TCari.getText().equals("")){
                    ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
                    ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:00");
                }else{
                   ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
                    ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:00");
                    ps.setString(3,"%"+TCari.getText()+"%");
                    ps.setString(4,"%"+TCari.getText()+"%");
                    ps.setString(5,"%"+TCari.getText()+"%");
                    ps.setString(6,"%"+TCari.getText()+"%");
                    ps.setString(7,"%"+TCari.getText()+"%");
                    ps.setString(8,"%"+TCari.getText()+"%");
                }   
                rs=ps.executeQuery();
                while(rs.next()){
                    tabMode.addRow(new String[]{
                        rs.getString("no_rawat"),rs.getString("no_rkm_medis"),rs.getString("nm_pasien"), rs.getString("tgl_lahir"),
                        rs.getString("umur"), rs.getString("ruang"), rs.getString("tgl_konsultasi"),
                        rs.getString("kd_dokter_konsul"), rs.getString("dokter_konsul"), rs.getString("kd_dokter_dpjp"), rs.getString("dokter_dpjp"), 
                        rs.getString("konsul_dpjp"), rs.getString("jawaban_konsul")
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
        }catch(SQLException e){
            System.out.println("Notifikasi : "+e);
        }
        LCount.setText(""+tabMode.getRowCount());
    }

    public void emptTeks() {
        KonsulDPJP.setText("");
        JawabanDPJP.setText("");
    } 

    private void getData() {
        if(tbObat.getSelectedRow()!= -1){
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());  
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(),1).toString());  
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(),2).toString());  
            TTglLahir.setText(tbObat.getValueAt(tbObat.getSelectedRow(),3).toString());      
            TUmur.setText(tbObat.getValueAt(tbObat.getSelectedRow(),4).toString());  
            TRuang.setText(tbObat.getValueAt(tbObat.getSelectedRow(),5).toString()); 
            Valid.SetTgl2(tgl,tbObat.getValueAt(tbObat.getSelectedRow(),6).toString());
            KodeDokterKonsulen.setText(tbObat.getValueAt(tbObat.getSelectedRow(),7).toString());  
            NamaDokterKonsulen.setText(tbObat.getValueAt(tbObat.getSelectedRow(),8).toString());  
            KodeDPJP.setText(tbObat.getValueAt(tbObat.getSelectedRow(),9).toString());
            NamaDPJP.setText(tbObat.getValueAt(tbObat.getSelectedRow(),10).toString());  
            KonsulDPJP.setText(tbObat.getValueAt(tbObat.getSelectedRow(),11).toString());  
            JawabanDPJP.setText(tbObat.getValueAt(tbObat.getSelectedRow(),12).toString());
            
        }
    }

    private void isRawat() {
         Sequel.cariIsi("select reg_periksa.no_rkm_medis from reg_periksa where reg_periksa.no_rawat='"+TNoRw.getText()+"' ",TNoRM);
    }

    private void isPsien() {
        Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis='"+TNoRM.getText()+"' ",TPasien);
    }
    
    public void setNoRm(String norwt, String ruang, String kd_kamar) {
        TNoRw.setText(norwt);
        TRuang.setText(ruang);
        ChkInput.setSelected(true);
        isForm();
        try {
            ps=koneksi.prepareStatement(
                    "SELECT rp.no_rawat, rp.no_rkm_medis, p.nm_pasien, p.tgl_lahir, CONCAT(rp.umurdaftar, ' ', rp.sttsumur) as umur \n" +
                    "FROM reg_periksa rp \n" +
                    "INNER JOIN pasien p ON p.no_rkm_medis = rp.no_rkm_medis\n" +
                    "WHERE rp.no_rawat = ?");
            try {
                ps.setString(1,norwt);
                rs=ps.executeQuery();
                if(rs.next()){
                    TNoRM.setText(rs.getString("no_rkm_medis"));
                    TPasien.setText(rs.getString("nm_pasien"));
                    TTglLahir.setText(rs.getString("tgl_lahir"));
                    TUmur.setText(rs.getString("umur"));
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
    
    private void isForm(){
        if(ChkInput.isSelected()==true){
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH,this.getHeight()-122));
            scrollInput.setVisible(true);      
            ChkInput.setVisible(true);
        }else if(ChkInput.isSelected()==false){           
            ChkInput.setVisible(false);            
            PanelInput.setPreferredSize(new Dimension(WIDTH,20));
            scrollInput.setVisible(false);      
            ChkInput.setVisible(true);
        }
    }
    
    public void isCek(){
        BtnSimpan.setEnabled(akses.getdata_resume_pasien());
        BtnHapus.setEnabled(akses.getdata_resume_pasien());
        BtnEdit.setEnabled(akses.getdata_resume_pasien());
        if(akses.getjml2()>=1){
            KodeDokterKonsulen.setEditable(false);
            BtnDokterKonsulen.setEnabled(false);
            KodeDokterKonsulen.setText(akses.getkode());
            NamaDokterKonsulen.setText(dokter.tampil3(KodeDokterKonsulen.getText()));
            if(NamaDokterKonsulen.getText().equals("")){
                KodeDokterKonsulen.setText("");
                JOptionPane.showMessageDialog(null,"User login bukan dokter...!!");
            }
        }            
    }

    private void ganti() {
        try {
            Sequel.mengedit("lembar_konsultasi", "no_rawat='" + tbObat.getValueAt(tbObat.getSelectedRow(), 0) + "' and tgl_konsultasi='" + tbObat.getValueAt(tbObat.getSelectedRow(), 7) + "'",
                    "kd_dokter_dpjp='" + KodeDPJP.getText() + "',konsul_dpjp='" + KonsulDPJP.getText() + "',jawaban_konsul='" + JawabanDPJP.getText() + "'"
            );
            tampil();
            emptTeks();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void hapus() {
        if(Sequel.queryu2tf("delete from lembar_konsultasi where no_rawat=?",1,new String[]{
            tbObat.getValueAt(tbObat.getSelectedRow(),1).toString()
        })==true){
            tabMode.removeRow(tbObat.getSelectedRow());
            LCount.setText(""+tabMode.getRowCount());
            emptTeks();
        }else{
            JOptionPane.showMessageDialog(null,"Gagal menghapus..!!");
        }
    }
}
