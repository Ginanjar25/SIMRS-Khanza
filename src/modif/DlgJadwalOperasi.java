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
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariDokter;
import keuangan.DlgKamar;
import laporan.DlgCariPenyakit;
import modif.DlgPendaftaranBayi;

/**
 *
 * @author dosen
 */
public class DlgJadwalOperasi extends javax.swing.JDialog {
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
    private String status="", gabungkan="",norawatgabung="",kamaryangdigabung="",dokterranap="",bangsal="",diagnosa_akhir="",namakamar="",umur="0",sttsumur="Th",pilihan="";
    private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd");
    private DlgKamar kamar=new DlgKamar(null,false);
    
    


    /** Creates new form DlgPemberianInfus
     * @param parent
     * @param modal */
    public DlgJadwalOperasi(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        Object[] row={"No RM", "Nama Pasien","Tanggal","Mulai","Selesai","Diagnosa", "Nama Operasi","Kode Operator","Operator",};
        tabMode=new DefaultTableModel(null,row){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        tbObat.setModel(tabMode);

        //tbObat.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbObat.getBackground()));
        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 9; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(110);
            }else if(i==1){
                column.setPreferredWidth(170);
            }else if(i==2){
                column.setPreferredWidth(100);
            }else if(i==3){
                column.setPreferredWidth(100);
            }else if(i==4){
                column.setPreferredWidth(100);
            }else if(i==5){
                column.setPreferredWidth(250);
            }else if(i==6){
                column.setPreferredWidth(200);
            }else if(i==7){
                column.setPreferredWidth(100);
            }else if(i==8){
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
        
        ChkInput.setSelected(true);
        isForm();
        
        pasien.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(akses.getform().equals("DlgKamarInap")){
                    if(pasien.getTable().getSelectedRow()!= -1){                   
                        TNoRM.setText(pasien.getTable().getValueAt(pasien.getTable().getSelectedRow(),0).toString());                    
                        TPasien.setText(pasien.getTable().getValueAt(pasien.getTable().getSelectedRow(),1).toString());                    
                    }
                    TNoRM.requestFocus();
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
        
         dokter.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {;}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(dokter.getTable().getSelectedRow()!= -1){                    
                    KdDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),0).toString());
                    NmDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),1).toString());
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
        PanelInput = new javax.swing.JPanel();
        ChkInput = new widget.CekBox();
        FormInput = new widget.PanelBiasa();
        jLabel4 = new widget.Label();
        TPasien = new widget.TextBox();
        BtnSeekPasien = new widget.Button();
        TNoRM = new widget.TextBox();
        jLabel15 = new widget.Label();
        TDiagnosa = new widget.TextBox();
        jLabel10 = new widget.Label();
        DTPTgl = new widget.Tanggal();
        jLabel35 = new widget.Label();
        JamMulai = new widget.ComboBox();
        MenitMulai = new widget.ComboBox();
        DetikMulai = new widget.ComboBox();
        jLabel36 = new widget.Label();
        JamSelesai = new widget.ComboBox();
        MenitSelesai = new widget.ComboBox();
        DetikSelesai = new widget.ComboBox();
        jLabel9 = new widget.Label();
        KdDokter = new widget.TextBox();
        NmDokter = new widget.TextBox();
        BtnOperator = new widget.Button();
        jLabel16 = new widget.Label();
        TOperasi = new widget.TextBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Booking Jadwal Operasi ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbObat.setAutoCreateRowSorter(true);
        tbObat.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
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
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "01-10-2025" }));
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
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "01-10-2025" }));
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
        PanelInput.setPreferredSize(new java.awt.Dimension(192, 160));
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

        jLabel4.setText("No.RM Pasien :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(10, 10, 75, 23);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N
        FormInput.add(TPasien);
        TPasien.setBounds(200, 10, 390, 23);

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
        BtnSeekPasien.setBounds(590, 10, 28, 23);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM"); // NOI18N
        FormInput.add(TNoRM);
        TNoRM.setBounds(90, 10, 100, 23);

        jLabel15.setText("Diagnosa :");
        jLabel15.setName("jLabel15"); // NOI18N
        FormInput.add(jLabel15);
        jLabel15.setBounds(10, 100, 60, 23);

        TDiagnosa.setBackground(new java.awt.Color(202, 202, 202));
        TDiagnosa.setHighlighter(null);
        TDiagnosa.setName("TDiagnosa"); // NOI18N
        FormInput.add(TDiagnosa);
        TDiagnosa.setBounds(80, 100, 630, 23);

        jLabel10.setText("Tanggal Rencana:");
        jLabel10.setName("jLabel10"); // NOI18N
        FormInput.add(jLabel10);
        jLabel10.setBounds(0, 40, 100, 23);

        DTPTgl.setForeground(new java.awt.Color(50, 70, 50));
        DTPTgl.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "01-10-2025" }));
        DTPTgl.setDisplayFormat("dd-MM-yyyy");
        DTPTgl.setName("DTPTgl"); // NOI18N
        DTPTgl.setOpaque(false);
        DTPTgl.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DTPTglKeyPressed(evt);
            }
        });
        FormInput.add(DTPTgl);
        DTPTgl.setBounds(110, 40, 90, 23);

        jLabel35.setText("Mulai :");
        jLabel35.setName("jLabel35"); // NOI18N
        FormInput.add(jLabel35);
        jLabel35.setBounds(200, 40, 40, 23);

        JamMulai.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        JamMulai.setName("JamMulai"); // NOI18N
        JamMulai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JamMulaiKeyPressed(evt);
            }
        });
        FormInput.add(JamMulai);
        JamMulai.setBounds(240, 40, 62, 23);

        MenitMulai.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        MenitMulai.setName("MenitMulai"); // NOI18N
        MenitMulai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MenitMulaiKeyPressed(evt);
            }
        });
        FormInput.add(MenitMulai);
        MenitMulai.setBounds(310, 40, 62, 23);

        DetikMulai.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        DetikMulai.setName("DetikMulai"); // NOI18N
        DetikMulai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DetikMulaiKeyPressed(evt);
            }
        });
        FormInput.add(DetikMulai);
        DetikMulai.setBounds(380, 40, 62, 23);

        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setText("s.d.");
        jLabel36.setName("jLabel36"); // NOI18N
        FormInput.add(jLabel36);
        jLabel36.setBounds(450, 40, 25, 23);

        JamSelesai.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        JamSelesai.setName("JamSelesai"); // NOI18N
        JamSelesai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JamSelesaiKeyPressed(evt);
            }
        });
        FormInput.add(JamSelesai);
        JamSelesai.setBounds(480, 40, 62, 23);

        MenitSelesai.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        MenitSelesai.setName("MenitSelesai"); // NOI18N
        MenitSelesai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MenitSelesaiKeyPressed(evt);
            }
        });
        FormInput.add(MenitSelesai);
        MenitSelesai.setBounds(550, 40, 62, 23);

        DetikSelesai.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        DetikSelesai.setName("DetikSelesai"); // NOI18N
        DetikSelesai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DetikSelesaiKeyPressed(evt);
            }
        });
        FormInput.add(DetikSelesai);
        DetikSelesai.setBounds(620, 40, 62, 23);

        jLabel9.setText("Operator :");
        jLabel9.setName("jLabel9"); // NOI18N
        FormInput.add(jLabel9);
        jLabel9.setBounds(0, 70, 70, 23);

        KdDokter.setEditable(false);
        KdDokter.setHighlighter(null);
        KdDokter.setName("KdDokter"); // NOI18N
        FormInput.add(KdDokter);
        KdDokter.setBounds(80, 70, 80, 23);

        NmDokter.setEditable(false);
        NmDokter.setHighlighter(null);
        NmDokter.setName("NmDokter"); // NOI18N
        FormInput.add(NmDokter);
        NmDokter.setBounds(160, 70, 200, 23);

        BtnOperator.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnOperator.setMnemonic('X');
        BtnOperator.setToolTipText("Alt+X");
        BtnOperator.setName("BtnOperator"); // NOI18N
        BtnOperator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnOperatorActionPerformed(evt);
            }
        });
        BtnOperator.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnOperatorKeyPressed(evt);
            }
        });
        FormInput.add(BtnOperator);
        BtnOperator.setBounds(360, 70, 28, 23);

        jLabel16.setText("Operasi :");
        jLabel16.setName("jLabel16"); // NOI18N
        FormInput.add(jLabel16);
        jLabel16.setBounds(380, 70, 60, 23);

        TOperasi.setBackground(new java.awt.Color(202, 202, 202));
        TOperasi.setHighlighter(null);
        TOperasi.setName("TOperasi"); // NOI18N
        FormInput.add(TOperasi);
        TOperasi.setBounds(440, 70, 270, 23);

        PanelInput.add(FormInput, java.awt.BorderLayout.CENTER);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (TNoRM.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRM, "pasien");
        } else if (NmDokter.getText().trim().equals("")) {
            Valid.textKosong(KdDokter, "Operator");
        } else if (TOperasi.getText().trim().equals("")) {
            Valid.textKosong(TOperasi, "Nama Operasi");
        } else if (TDiagnosa.getText().trim().equals("")) {
            Valid.textKosong(TDiagnosa, "Diagnosa Operasi");
        } else if ((!(JamMulai.getSelectedItem().toString() + MenitMulai.getSelectedItem().toString() + DetikMulai.getSelectedItem().toString()).equals("000000"))
                && (Sequel.cariInteger("select count(jadwal_operasi.no_rkm_medis) from jadwal_operasi where jadwal_operasi.tanggal='" + Valid.SetTgl(DTPTgl.getSelectedItem() + "") + "' "
                        + "and jadwal_operasi.no_rkm_medis<>'" + TNoRM.getText() + "' and "
                        + "jadwal_operasi.jam_mulai between '" + JamMulai.getSelectedItem() + ":" + MenitMulai.getSelectedItem() + ":" + DetikMulai.getSelectedItem() + "' and '" + JamSelesai.getSelectedItem() + ":" + MenitSelesai.getSelectedItem() + ":" + DetikSelesai.getSelectedItem() + "'") > 0)) {
            JOptionPane.showMessageDialog(rootPane, "Jadwal bentrok dengan jam mulai operasi yang lain..!!");
            JamMulai.requestFocus();
        } else {
            if (Sequel.menyimpantf("jadwal_operasi", "?,?,?,?,?,?,?", "data", 7, new String[]{
                TNoRM.getText(), Valid.SetTgl(DTPTgl.getSelectedItem() + ""),
                JamMulai.getSelectedItem() + ":" + MenitMulai.getSelectedItem() + ":" + DetikMulai.getSelectedItem(),
                JamSelesai.getSelectedItem() + ":" + MenitSelesai.getSelectedItem() + ":" + DetikSelesai.getSelectedItem(),
                KdDokter.getText(), TDiagnosa.getText(), TOperasi.getText()
            }) == true) {
                tampil();
                emptTeks();
            }
        }
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnSimpanActionPerformed(null);
        }else{
            Valid.pindah(evt,BtnEdit,BtnBatal);
        }
}//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        emptTeks();
}//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBatalKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnBatalActionPerformed(null);
        }else{Valid.pindah(evt, BtnSimpan, BtnHapus);}
}//GEN-LAST:event_BtnBatalKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
         if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis...!!!!");
            DTPTgl.requestFocus();
        }else if(TPasien.getText().trim().equals("")){
            JOptionPane.showMessageDialog(null,"Maaf, Gagal menghapus. Pilih dulu data yang mau dihapus.\nKlik data pada table untuk memilih...!!!!");
        }else if(!(TPasien.getText().trim().equals(""))){
            if(tbObat.getSelectedRow()!= -1){
                try{
                    Sequel.queryu("delete from jadwal_operasi " +
                            "where no_rkm_medis='"+TNoRM.getText()+"' " +
                            "and tanggal='"+Valid.SetTgl(DTPTgl.getSelectedItem()+"")+"' " +
                            "and jam_mulai='"+JamMulai.getSelectedItem()+":"+MenitMulai.getSelectedItem()+":"+DetikMulai.getSelectedItem()+"' " +
                            "and jam_selesai='"+JamSelesai.getSelectedItem()+":"+MenitSelesai.getSelectedItem()+":"+DetikSelesai.getSelectedItem()+"' "+
                            "and kd_dokter='"+KdDokter.getText()+"' ");
                    tampil();
                    emptTeks();
                }catch(Exception e){
                    JOptionPane.showMessageDialog(null,"Maaf, Silahkan anda pilih terlebih dulu data yang mau anda hapus...\n Klik data pada table untuk memilih data...!!!!");
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
//            BtnBatalActionPerformed(null);
        } catch (Exception e) {
            
        }
        
    }//GEN-LAST:event_formWindowOpened

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed
        if(TNoRM.getText().trim().equals("")||TPasien.getText().trim().equals("")){
            Valid.textKosong(TNoRM,"pasien");
        }else if(NmDokter.getText().trim().equals("")){
            Valid.textKosong(KdDokter,"Operator");
        }else if(TOperasi.getText().trim().equals("")){
            Valid.textKosong(TOperasi,"Operasi");
        }else if(TDiagnosa.getText().trim().equals("")){
            Valid.textKosong(TDiagnosa,"Ruang Operasi");
        }else if((!(JamMulai.getSelectedItem().toString()+MenitMulai.getSelectedItem().toString()+DetikMulai.getSelectedItem().toString()).equals("000000"))&&
            (Sequel.cariInteger("select count(jadwal_operasi.no_rkm_medis) from jadwal_operasi where jadwal_operasi.tanggal='"+Valid.SetTgl(DTPTgl.getSelectedItem()+"")+"' "+
            "and jadwal_operasi.no_rkm_medis<>'"+TNoRM.getText()+"' and "+
            "jadwal_operasi.jam_mulai between '"+JamMulai.getSelectedItem()+":"+MenitMulai.getSelectedItem()+":"+DetikMulai.getSelectedItem()+"' and '"+JamSelesai.getSelectedItem()+":"+MenitSelesai.getSelectedItem()+":"+DetikSelesai.getSelectedItem()+"'")>0)){
            JOptionPane.showMessageDialog(rootPane,"Jadwal bentrok dengan jam mulai operasi yang lain..!!");
            JamMulai.requestFocus();
        }else{
            if(tbObat.getSelectedRow()!= -1){
                if(Sequel.mengedittf("jadwal_operasi","no_rkm_medis=? and tanggal=? and jam_mulai=? and jam_selesai=? and kd_dokter=? and diagnosa=? and nm_operasi=?",
                        "tanggal=?,jam_mulai=?,jam_selesai=?,kd_dokter=?,diagnosa=?, nm_operasi=?",13,new String[]{
                         Valid.SetTgl(DTPTgl.getSelectedItem()+""),
                        JamMulai.getSelectedItem()+":"+MenitMulai.getSelectedItem()+":"+DetikMulai.getSelectedItem(),
                        JamSelesai.getSelectedItem()+":"+MenitSelesai.getSelectedItem()+":"+DetikSelesai.getSelectedItem(),
                        KdDokter.getText(), TDiagnosa.getText(), TOperasi.getText(),    
                        tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(), tbObat.getValueAt(tbObat.getSelectedRow(),2).toString(),
                        tbObat.getValueAt(tbObat.getSelectedRow(),3).toString(), tbObat.getValueAt(tbObat.getSelectedRow(),4).toString(),
                        tbObat.getValueAt(tbObat.getSelectedRow(),7).toString(), tbObat.getValueAt(tbObat.getSelectedRow(),5).toString(),
                        tbObat.getValueAt(tbObat.getSelectedRow(),6).toString()
                     })==true){
                    tampil();
                    emptTeks();
                }
            }
        }
    }//GEN-LAST:event_BtnEditActionPerformed

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEditKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnEditActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnHapus, BtnKeluar);
        }
    }//GEN-LAST:event_BtnEditKeyPressed

    private void DTPTglKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DTPTglKeyPressed
        Valid.pindah(evt,TCari,JamMulai);
    }//GEN-LAST:event_DTPTglKeyPressed

    private void JamMulaiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_JamMulaiKeyPressed
        Valid.pindah(evt,DTPTgl,MenitMulai);
    }//GEN-LAST:event_JamMulaiKeyPressed

    private void MenitMulaiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MenitMulaiKeyPressed
        Valid.pindah(evt,JamMulai,DetikMulai);
    }//GEN-LAST:event_MenitMulaiKeyPressed

    private void DetikMulaiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DetikMulaiKeyPressed
        Valid.pindah(evt,MenitMulai,JamSelesai);
    }//GEN-LAST:event_DetikMulaiKeyPressed

    private void JamSelesaiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_JamSelesaiKeyPressed
        Valid.pindah(evt,DetikMulai,MenitSelesai);
    }//GEN-LAST:event_JamSelesaiKeyPressed

    private void MenitSelesaiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MenitSelesaiKeyPressed
        Valid.pindah(evt,JamSelesai,DetikSelesai);
    }//GEN-LAST:event_MenitSelesaiKeyPressed

    private void DetikSelesaiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DetikSelesaiKeyPressed
//        Valid.pindah(evt,MenitSelesai,Status);
    }//GEN-LAST:event_DetikSelesaiKeyPressed

    private void BtnOperatorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnOperatorActionPerformed
        dokter.isCek();
        dokter.TCari.requestFocus();
        dokter.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setVisible(true);
    }//GEN-LAST:event_BtnOperatorActionPerformed

    private void BtnOperatorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnOperatorKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnOperatorActionPerformed(null);
        }else{
//            Valid.pindah(evt,Status,BtnOperasi);
        }
    }//GEN-LAST:event_BtnOperatorKeyPressed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgJadwalOperasi dialog = new DlgJadwalOperasi(new javax.swing.JFrame(), true);
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
    private widget.Button BtnOperator;
    private widget.Button BtnSeekPasien;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkInput;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.Tanggal DTPTgl;
    private widget.ComboBox DetikMulai;
    private widget.ComboBox DetikSelesai;
    private widget.PanelBiasa FormInput;
    private widget.ComboBox JamMulai;
    private widget.ComboBox JamSelesai;
    private widget.TextBox KdDokter;
    private widget.Label LCount;
    private widget.ComboBox MenitMulai;
    private widget.ComboBox MenitSelesai;
    private widget.TextBox NmDokter;
    private javax.swing.JPanel PanelInput;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TDiagnosa;
    private widget.TextBox TNoRM;
    private widget.TextBox TOperasi;
    private widget.TextBox TPasien;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel10;
    private widget.Label jLabel15;
    private widget.Label jLabel16;
    private widget.Label jLabel19;
    private widget.Label jLabel21;
    private widget.Label jLabel35;
    private widget.Label jLabel36;
    private widget.Label jLabel4;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel9;
    private javax.swing.JPanel jPanel3;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

    public void tampil() {   
        
        status=" jadwal_operasi.tanggal between '"+Valid.SetTgl(DTPCari1.getSelectedItem()+"")+"' and '"+Valid.SetTgl(DTPCari2.getSelectedItem()+"")+"' ";
         
        Valid.tabelKosong(tabMode);
        try{        
            ps=koneksi.prepareStatement(
                "SELECT jadwal_operasi.no_rkm_medis, pasien.nm_pasien, jadwal_operasi.tanggal, jadwal_operasi.jam_mulai, jadwal_operasi.jam_selesai,\n" +
                "jadwal_operasi.diagnosa, jadwal_operasi.nm_operasi, jadwal_operasi.kd_dokter, dokter.nm_dokter\n" +
                "FROM jadwal_operasi \n" +
                "JOIN pasien ON pasien.no_rkm_medis = jadwal_operasi.no_rkm_medis\n" +
                "JOIN dokter ON dokter.kd_dokter = jadwal_operasi.kd_dokter\n" +
                "where "+status+(TCari.getText().trim().equals("")?"":"AND (jadwal_operasi.no_rkm_medis LIKE ? " +
                "OR pasien.nm_pasien LIKE ? OR jadwal_operasi.kd_dokter LIKE ? OR dokter.nm_dokter LIKE ? )")+"order by jadwal_operasi.tanggal,jadwal_operasi.jam_mulai");
            try {
               if(!TCari.getText().trim().equals("")){
                    ps.setString(1,"%"+TCari.getText().trim()+"%");
                    ps.setString(2,"%"+TCari.getText().trim()+"%");
                    ps.setString(3,"%"+TCari.getText().trim()+"%");
                    ps.setString(4,"%"+TCari.getText().trim()+"%");
                }             
                rs=ps.executeQuery();  
                while(rs.next()){                    
                    tabMode.addRow(new String[]{
                        rs.getString("no_rkm_medis"),rs.getString("nm_pasien"),rs.getString("tanggal"),
                        rs.getString("jam_mulai"),rs.getString("jam_selesai"),rs.getString("diagnosa"),
                        rs.getString("nm_operasi"), rs.getString("kd_dokter"), rs.getString("nm_dokter")
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
//         Object[] row={"No RM", "Nama Pasien","Tanggal","Mulai","Selesai","Diagnosa", "Nama Operasi","Kode Operator","Operator",};
        if(tbObat.getSelectedRow()!= -1){            
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(),0).toString()); 
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(),1).toString());
            JamMulai.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),3).toString().substring(0,2));
            MenitMulai.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),3).toString().substring(3,5));
            DetikMulai.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),3).toString().substring(6,8));
            JamSelesai.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),4).toString().substring(0,2));
            DetikSelesai.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),4).toString().substring(3,5));
            DetikSelesai.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),4).toString().substring(6,8));
            TDiagnosa.setText(tbObat.getValueAt(tbObat.getSelectedRow(),5).toString());
            TOperasi.setText(tbObat.getValueAt(tbObat.getSelectedRow(),6).toString());
            KdDokter.setText(tbObat.getValueAt(tbObat.getSelectedRow(),7).toString());
            NmDokter.setText(tbObat.getValueAt(tbObat.getSelectedRow(),8).toString());
            Valid.SetTgl(DTPTgl,tbObat.getValueAt(tbObat.getSelectedRow(),2).toString());
        }
    }

    private void isRawat() {        
        Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis=? ",TPasien,TNoRM.getText());
    }
    
    public void setNoRm(String norm,String nama) {
        TNoRM.setText(norm);
        TPasien.setText(nama);
        TCari.setText(norm);
        ChkInput.setSelected(true);
        isForm();
    }
    
    private void isForm(){
        if(ChkInput.isSelected()==true){
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH,165));
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
        BtnSimpan.setEnabled(akses.getbooking_operasi());
        BtnEdit.setEnabled(akses.getbooking_operasi());
        BtnHapus.setEnabled(akses.getbooking_operasi());
    }
    
    
     public void emptTeks() {
        TNoRM.setText("");
        TPasien.setText("");
        KdDokter.setText("");
        NmDokter.setText("");
        JamMulai.setSelectedItem("00");
        MenitMulai.setSelectedItem("00");
        DetikMulai.setSelectedItem("00");
        JamSelesai.setSelectedItem("00");
        MenitSelesai.setSelectedItem("00");
        DetikSelesai.setSelectedItem("00");
        DTPTgl.setDate(new Date());
        DTPTgl.requestFocus();
        TDiagnosa.setText("");
        TOperasi.setText("");
    }
}
