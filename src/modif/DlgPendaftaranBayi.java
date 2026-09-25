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
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author dosen
 */
public class DlgPendaftaranBayi extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private Connection koneksi=koneksiDB.condb();
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Date date = new Date();
    private int i=0;
    private PreparedStatement ps;
    private ResultSet rs;
    public  DlgCariPasien bayi=new DlgCariPasien(null,false);
    public  DlgCariPasien ibu=new DlgCariPasien(null,false);
    
    


    /** Creates new form DlgPemberianInfus
     * @param parent
     * @param modal */
    public DlgPendaftaranBayi(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        Object[] row={"No.RM Bayi","Nama Bayi", "No.RM Ibu", "Nama Ibu", "Nama Ayah", "Pekerjaan Ayah", "Umur Ayah", "No KK", "Kode Pos" };
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
                column.setPreferredWidth(100);
            }else if(i==1){
                column.setPreferredWidth(200);
            }else if(i==2){
                column.setPreferredWidth(100);
            }else if(i==3){
                column.setPreferredWidth(200);
            }else if(i==4){
                column.setPreferredWidth(200);
            }else if(i==5){
                column.setPreferredWidth(200);
            }else if(i==6){
                column.setPreferredWidth(50);
            }else if(i==7){
                column.setPreferredWidth(150);
            }else if(i==8){
                column.setPreferredWidth(100);
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
        
        bayi.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(akses.getform().equals("DlgPendaftaranBayi") || akses.getform().equals("DlgPasien")){
                    if(bayi.getTable().getSelectedRow()!= -1){                   
                        TNoRMBayi.setText(bayi.getTable().getValueAt(bayi.getTable().getSelectedRow(),0).toString());                    
                        TPasienBayi.setText(bayi.getTable().getValueAt(bayi.getTable().getSelectedRow(),1).toString());                    
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
        
        bayi.getTable().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if(akses.getform().equals("DlgPendaftaranBayi") || akses.getform().equals("DlgPasien")){
                    if(e.getKeyCode()==KeyEvent.VK_SPACE){
                        bayi.dispose();
                    }
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });       
        
        
        ibu.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(akses.getform().equals("DlgPendaftaranBayi") ||akses.getform().equals("DlgPasien")){
                    if(ibu.getTable().getSelectedRow()!= -1){                   
                        TNoRMIbu.setText(ibu.getTable().getValueAt(ibu.getTable().getSelectedRow(),0).toString());                    
                        TIbu.setText(ibu.getTable().getValueAt(ibu.getTable().getSelectedRow(),1).toString());         
                        if(ibu.getTable().getValueAt(ibu.getTable().getSelectedRow(),16).toString().equals("SUAMI")){
                            TAyah.setText(ibu.getTable().getValueAt(ibu.getTable().getSelectedRow(),17).toString());
                            PekerjaanAyah.setText(ibu.getTable().getValueAt(ibu.getTable().getSelectedRow(),21).toString());
                        }
                    }
                    TNoRMIbu.requestFocus();
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
        
        ibu.getTable().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if(akses.getform().equals("DlgPendaftaranBayi") || akses.getform().equals("DlgPasien")){
                    if(e.getKeyCode()==KeyEvent.VK_SPACE){
                        ibu.dispose();
                    }
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
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
        BtnEdit = new widget.Button();
        BtnBatal = new widget.Button();
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
        TPasienBayi = new widget.TextBox();
        jLabel13 = new widget.Label();
        NoKK = new widget.TextBox();
        BtnSeekPasien = new widget.Button();
        TNoRMBayi = new widget.TextBox();
        jLabel9 = new widget.Label();
        TNoRMIbu = new widget.TextBox();
        TIbu = new widget.TextBox();
        BtnSeekIbu = new widget.Button();
        KdPos = new widget.TextBox();
        jLabel14 = new widget.Label();
        jLabel10 = new widget.Label();
        PekerjaanAyah = new widget.TextBox();
        jLabel11 = new widget.Label();
        TAyah = new widget.TextBox();
        UmurAyah = new widget.TextBox();
        jLabel16 = new widget.Label();
        jLabel17 = new widget.Label();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Pendaftaran Bayi Baru Lahir ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
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

        BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png"))); // NOI18N
        BtnEdit.setMnemonic('G');
        BtnEdit.setText("Ganti");
        BtnEdit.setToolTipText("Alt+G");
        BtnEdit.setName("BtnEdit"); // NOI18N
        BtnEdit.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnEditActionPerformed1(evt);
            }
        });
        BtnEdit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnEditKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnEdit);

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

        jLabel19.setText("Tgl. Daftar :");
        jLabel19.setName("jLabel19"); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(67, 23));
        panelGlass9.add(jLabel19);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "02-07-2025" }));
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
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "02-07-2025" }));
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
        jLabel4.setBounds(10, 10, 80, 23);

        TPasienBayi.setEditable(false);
        TPasienBayi.setHighlighter(null);
        TPasienBayi.setName("TPasienBayi"); // NOI18N
        FormInput.add(TPasienBayi);
        TPasienBayi.setBounds(190, 10, 390, 23);

        jLabel13.setText("No KK :");
        jLabel13.setName("jLabel13"); // NOI18N
        FormInput.add(jLabel13);
        jLabel13.setBounds(10, 130, 80, 23);

        NoKK.setBackground(new java.awt.Color(202, 202, 202));
        NoKK.setHighlighter(null);
        NoKK.setName("NoKK"); // NOI18N
        FormInput.add(NoKK);
        NoKK.setBounds(90, 130, 270, 23);

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
        BtnSeekPasien.setBounds(580, 10, 28, 23);

        TNoRMBayi.setEditable(false);
        TNoRMBayi.setHighlighter(null);
        TNoRMBayi.setName("TNoRMBayi"); // NOI18N
        FormInput.add(TNoRMBayi);
        TNoRMBayi.setBounds(90, 10, 100, 23);

        jLabel9.setText("No.RM. Ibu :");
        jLabel9.setName("jLabel9"); // NOI18N
        FormInput.add(jLabel9);
        jLabel9.setBounds(10, 40, 80, 23);

        TNoRMIbu.setEditable(false);
        TNoRMIbu.setHighlighter(null);
        TNoRMIbu.setName("TNoRMIbu"); // NOI18N
        FormInput.add(TNoRMIbu);
        TNoRMIbu.setBounds(90, 40, 100, 23);

        TIbu.setEditable(false);
        TIbu.setHighlighter(null);
        TIbu.setName("TIbu"); // NOI18N
        FormInput.add(TIbu);
        TIbu.setBounds(190, 40, 390, 23);

        BtnSeekIbu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnSeekIbu.setMnemonic('5');
        BtnSeekIbu.setToolTipText("ALt+5");
        BtnSeekIbu.setName("BtnSeekIbu"); // NOI18N
        BtnSeekIbu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSeekIbuActionPerformed(evt);
            }
        });
        FormInput.add(BtnSeekIbu);
        BtnSeekIbu.setBounds(580, 40, 28, 23);

        KdPos.setBackground(new java.awt.Color(202, 202, 202));
        KdPos.setHighlighter(null);
        KdPos.setName("KdPos"); // NOI18N
        FormInput.add(KdPos);
        KdPos.setBounds(430, 130, 160, 23);

        jLabel14.setText("Kode Pos :");
        jLabel14.setName("jLabel14"); // NOI18N
        FormInput.add(jLabel14);
        jLabel14.setBounds(350, 130, 75, 23);

        jLabel10.setText("Pekerjaan Ayah :");
        jLabel10.setName("jLabel10"); // NOI18N
        FormInput.add(jLabel10);
        jLabel10.setBounds(0, 100, 90, 23);

        PekerjaanAyah.setHighlighter(null);
        PekerjaanAyah.setName("PekerjaanAyah"); // NOI18N
        FormInput.add(PekerjaanAyah);
        PekerjaanAyah.setBounds(90, 100, 270, 23);

        jLabel11.setText("Nama Ayah :");
        jLabel11.setName("jLabel11"); // NOI18N
        FormInput.add(jLabel11);
        jLabel11.setBounds(0, 70, 90, 23);

        TAyah.setHighlighter(null);
        TAyah.setName("TAyah"); // NOI18N
        FormInput.add(TAyah);
        TAyah.setBounds(90, 70, 500, 23);

        UmurAyah.setBackground(new java.awt.Color(202, 202, 202));
        UmurAyah.setHighlighter(null);
        UmurAyah.setName("UmurAyah"); // NOI18N
        FormInput.add(UmurAyah);
        UmurAyah.setBounds(430, 100, 50, 23);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("Tahun");
        jLabel16.setName("jLabel16"); // NOI18N
        FormInput.add(jLabel16);
        jLabel16.setBounds(485, 100, 60, 23);

        jLabel17.setText("Umur Ayah :");
        jLabel17.setName("jLabel17"); // NOI18N
        FormInput.add(jLabel17);
        jLabel17.setBounds(350, 100, 75, 23);

        PanelInput.add(FormInput, java.awt.BorderLayout.CENTER);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if(TNoRMBayi.getText().trim().equals("")||TPasienBayi.getText().trim().equals("")){
            Valid.textKosong(TNoRMBayi,"No.Rekam Medis Bayi");
        }else if(TNoRMIbu.getText().trim().equals("")||TIbu.getText().trim().equals("")){
            Valid.textKosong(TNoRMIbu,"No.Rekam Medis Ibu");
        }else if(TAyah.getText().trim().equals("") || PekerjaanAyah.getText().trim().equals("")|| UmurAyah.getText().trim().equals("")){
            Valid.textKosong(TAyah,"Data Ayah");
        }else if(NoKK.getText().trim().equals("") || KdPos.getText().trim().equals("")){
            Valid.textKosong(TNoRMIbu,"No KK atau Kode Pos");
        }else if(TNoRMIbu.getText().trim().equals(TNoRMBayi.getText().trim())){
            Valid.textKosong(TNoRMIbu,"No.Rekam Medis Ibu dan No.Rekam Medis Bayi Tidak Boleh Sama");
        }else {
            if(Sequel.menyimpantf("pendaftaran_bayi", "?,?,?,?,?,?,?,?", "Pendaftaran Bayi", 8, new String[]{
                TNoRMBayi.getText(),
                TNoRMIbu.getText(),
                TAyah.getText(),
                PekerjaanAyah.getText(),
                UmurAyah.getText(),
                NoKK.getText(),
                KdPos.getText(),
                "0"
             }) == true){
                    tampil();
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
        emptTeks();
        TNoRMBayi.requestFocus();
}//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBatalKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnBatalActionPerformed(null);
        }else{Valid.pindah(evt, BtnSimpan, BtnHapus);}
}//GEN-LAST:event_BtnBatalKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if (tbObat.getSelectedRow() != -1) {
           Sequel.meghapus("pendaftaran_bayi", "no_rkm_medis", tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());
           tampil();
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

    private void BtnSeekPasienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSeekPasienActionPerformed
        akses.setform("DlgPendaftaranBayi");
        bayi.emptTeks();
        bayi.isCek();
        bayi.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        bayi.setLocationRelativeTo(internalFrame1);
        bayi.setVisible(true);
    }//GEN-LAST:event_BtnSeekPasienActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        try {
//            BtnBatalActionPerformed(null);
        } catch (Exception e) {
            
        }
        
    }//GEN-LAST:event_formWindowOpened

    private void BtnSeekIbuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSeekIbuActionPerformed
        akses.setform("DlgPendaftaranBayi");
        ibu.emptTeks();
        ibu.isCek();
        ibu.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        ibu.setLocationRelativeTo(internalFrame1);
        ibu.setVisible(true);
    }//GEN-LAST:event_BtnSeekIbuActionPerformed

    private void BtnEditActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed1
        if(TNoRMBayi.getText().trim().equals("")||TPasienBayi.getText().trim().equals("")){
            Valid.textKosong(TNoRMBayi,"No.Rekam Medis Bayi");
        }else if(TNoRMIbu.getText().trim().equals("")||TIbu.getText().trim().equals("")){
            Valid.textKosong(TNoRMIbu,"No.Rekam Medis Ibu");
        }else if(TAyah.getText().trim().equals("") || PekerjaanAyah.getText().trim().equals("")|| UmurAyah.getText().trim().equals("")){
            Valid.textKosong(TAyah,"Data Ayah");
        }else if(NoKK.getText().trim().equals("") || KdPos.getText().trim().equals("")){
            Valid.textKosong(TNoRMIbu,"No KK atau Kode Pos");
        }else if(TNoRMIbu.getText().trim().equals(TNoRMBayi.getText().trim())){
            Valid.textKosong(TNoRMIbu,"No.Rekam Medis Ibu dan No.Rekam Medis Bayi Tidak Boleh Sama");
        }else {
            Valid.editTable(tabMode,"pendaftaran_bayi","no_rkm_medis",TNoRMBayi,
            "no_rkm_medis_ibu='"+TNoRMIbu.getText()+"',nama_ayah='"+TAyah.getText()+"', pekerjaan_ayah='"+PekerjaanAyah.getText()+"', umur_ayah='"+UmurAyah.getText()+"', no_kk='"+NoKK.getText()+"', kode_pos='"+KdPos.getText()+"'");
             Sequel.mengedit("pasien_bayi", "no_rkm_medis='"+TNoRMBayi+"'", "nama_ayah='"+TAyah.getText()+"', pekerjaan_ayah='"+PekerjaanAyah.getText()+"', umur_ayah='"+UmurAyah.getText()+"'");
            emptTeks();
            tampil();
        }
    }//GEN-LAST:event_BtnEditActionPerformed1

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEditKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnEditActionPerformed1(null);
        }else{
            Valid.pindah(evt, BtnHapus, BtnBatal);
        }
    }//GEN-LAST:event_BtnEditKeyPressed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgPendaftaranBayi dialog = new DlgPendaftaranBayi(new javax.swing.JFrame(), true);
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
    private widget.Button BtnSeekIbu;
    private widget.Button BtnSeekPasien;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkInput;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.PanelBiasa FormInput;
    private widget.TextBox KdPos;
    private widget.Label LCount;
    private widget.TextBox NoKK;
    private javax.swing.JPanel PanelInput;
    private widget.TextBox PekerjaanAyah;
    private widget.ScrollPane Scroll;
    private widget.TextBox TAyah;
    private widget.TextBox TCari;
    private widget.TextBox TIbu;
    private widget.TextBox TNoRMBayi;
    private widget.TextBox TNoRMIbu;
    private widget.TextBox TPasienBayi;
    private widget.TextBox UmurAyah;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel13;
    private widget.Label jLabel14;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private widget.Label jLabel19;
    private widget.Label jLabel21;
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
        Valid.tabelKosong(tabMode);
        try{        
            ps=koneksi.prepareStatement(
                "SELECT pendaftaran_bayi.no_rkm_medis, bayi.nm_pasien as nama_bayi, pendaftaran_bayi.no_rkm_medis_ibu, ibu.nm_pasien as nama_ibu,\n" +
                "pendaftaran_bayi.nama_ayah, pendaftaran_bayi.pekerjaan_ayah, pendaftaran_bayi.umur_ayah,\n" +
                "pendaftaran_bayi.no_kk, pendaftaran_bayi.kode_pos \n" +
                "FROM pendaftaran_bayi\n" +
                "INNER JOIN pasien AS bayi ON bayi.no_rkm_medis = pendaftaran_bayi.no_rkm_medis\n" +
                "INNER JOIN pasien AS ibu ON ibu.no_rkm_medis = pendaftaran_bayi.no_rkm_medis_ibu\n" +
                "WHERE pendaftaran_bayi.no_rkm_medis = ? or pendaftaran_bayi.no_rkm_medis_ibu = ? or bayi.nm_pasien like ? or ibu.nm_pasien like ?");
            try {
                ps.setString(1, TCari.getText().trim() );    
                ps.setString(2, TCari.getText().trim() );   
                ps.setString(3, "%"+TCari.getText()+"%");   
                ps.setString(4, "%"+TCari.getText()+"%" );  
                rs=ps.executeQuery();  
                while(rs.next()){                    
                    tabMode.addRow(new String[]{
                        rs.getString("no_rkm_medis"),rs.getString("nama_bayi"),rs.getString("no_rkm_medis_ibu"),rs.getString("nama_ibu"),
                        rs.getString("nama_ayah"),rs.getString("pekerjaan_ayah"),rs.getString("umur_ayah"), rs.getString("no_kk"),rs.getString("kode_pos")
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
            TNoRMBayi.setText(tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());
            TPasienBayi.setText(tbObat.getValueAt(tbObat.getSelectedRow(),1).toString());
            TNoRMIbu.setText(tbObat.getValueAt(tbObat.getSelectedRow(),2).toString());
            TIbu.setText(tbObat.getValueAt(tbObat.getSelectedRow(),3).toString());
            TAyah.setText(tbObat.getValueAt(tbObat.getSelectedRow(),4).toString());
            PekerjaanAyah.setText(tbObat.getValueAt(tbObat.getSelectedRow(),5).toString());
            UmurAyah.setText(tbObat.getValueAt(tbObat.getSelectedRow(),6).toString());
            NoKK.setText(tbObat.getValueAt(tbObat.getSelectedRow(),7).toString());
            KdPos.setText(tbObat.getValueAt(tbObat.getSelectedRow(),8).toString());
        }
    }
    
    public void setNoRm(String no_rm_bayi,String nama_bayi,Date tgl1,Date tgl2) {
        TCari.setText(no_rm_bayi);
        TNoRMBayi.setText(no_rm_bayi);
        TPasienBayi.setText(nama_bayi);
        DTPCari1.setDate(tgl1);
        DTPCari2.setDate(tgl2);
        ChkInput.setSelected(true);
        Sequel.cariIsi("SELECT pasien.no_rkm_medis FROM reg_periksa\n"
                + "INNER JOIN ranap_gabung ON ranap_gabung.no_rawat = reg_periksa.no_rawat\n"
                + "INNER JOIN pasien on pasien.no_rkm_medis = reg_periksa.no_rkm_medis \n"
                + "WHERE ranap_gabung.no_rm_bayi = ?", TNoRMIbu,no_rm_bayi);
        
        Sequel.cariIsi("SELECT pasien.nm_pasien FROM reg_periksa\n"
                + "INNER JOIN ranap_gabung ON ranap_gabung.no_rawat = reg_periksa.no_rawat\n"
                + "INNER JOIN pasien on pasien.no_rkm_medis = reg_periksa.no_rkm_medis \n"
                + "WHERE ranap_gabung.no_rm_bayi = ?", TIbu,no_rm_bayi);
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
        BtnSimpan.setEnabled(akses.getpasien());
        BtnHapus.setEnabled(akses.getpasien());
        if(akses.getjml2()>=1){
            BtnSeekPasien.setEnabled(false);
        } 
    }
    
    public void emptTeks() {
        TNoRMBayi.setText("");
        TPasienBayi.setText("");
        TNoRMIbu.setText("");
        TIbu.setText("");
        TAyah.setText("");
        PekerjaanAyah.setText("");
        UmurAyah.setText("");
        NoKK.setText("");
        KdPos.setText("");
    }
    
}
