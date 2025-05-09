/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package permintaan;
import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariDokter;
import kepegawaian.DlgCariDokter2;
import simrskhanza.DlgCariPoli;
import simrskhanza.DlgCariPoli2;
import simrskhanza.DlgPasien;

/**
 *
 * @author GINANJAR
 */
public class DlgBookingKuota extends javax.swing.JFrame {
    private final DefaultTableModel tabMode;
    private Connection koneksi=koneksiDB.condb();
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i=0,kuota=0;
    private DlgCariDokter dokter=new DlgCariDokter(null,false);
    private DlgCariDokter dokter2=new DlgCariDokter(null,false);
    private DlgCariPoli poli=new DlgCariPoli(null,false);
    private DlgCariPoli2 poli2=new DlgCariPoli2(null,false);
    private DlgPasien pasien=new DlgPasien(null,false);
    private String aktifjadwal="",URUTNOREG="",BASENOREG="",status="",no_rawat="",umur="",sttsumur="",nohp="";
    private StringBuilder htmlContent;

    /**
     * Creates new form bookingKuota
     */
    public DlgBookingKuota() {
        initComponents();
        tabMode=new DefaultTableModel(null,new Object[]{
                "P","Tgl Periksa","Nama Dokter","Nama Pasien","Alamat","No. Telp","Cara Bayar","Catatan","Kd Dok"
            }){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = false;
                if (colIndex==0) {
                    a=true;
                }
                return a;
             }
             Class[] types = new Class[] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, 
                 java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        tbObat.setModel(tabMode);

        //tbObat.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbObat.getBackground()));
        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 9; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(20);
            }else if(i==1){
                column.setPreferredWidth(80);
            }else if(i==2){
                column.setPreferredWidth(190);
            }else if(i==3){
                column.setPreferredWidth(190);
            }else if(i==4){
                column.setPreferredWidth(250);
            }else if(i==5){
                column.setPreferredWidth(100);
            }else if(i==6){
                column.setPreferredWidth(150);
            }else if(i==8){
                column.setMinWidth(0);
                column.setMaxWidth(0);
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());
        
        TCari.setDocument(new batasInput((byte)100).getKata(TCari));
        KdDokter.setDocument(new batasInput((byte)3).getKata(KdDokter));
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
        
        dokter2.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {;}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(dokter2.getTable().getSelectedRow()!= -1){                    
                    jLabelKdDok.setText(dokter2.getTable().getValueAt(dokter2.getTable().getSelectedRow(),0).toString());
                    NmDokter1.setText(dokter2.getTable().getValueAt(dokter2.getTable().getSelectedRow(),1).toString());
                    tampil();
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
        
        poli.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(poli.getTable().getSelectedRow()!= -1){                    
                    
                    TAlamat.setText(poli.getTable().getValueAt(poli.getTable().getSelectedRow(),1).toString());
                    
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
        
        poli2.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(poli2.getTable().getSelectedRow()!= -1){                    
                    
                    TAlamat.setText(poli2.getTable().getValueAt(poli2.getTable().getSelectedRow(),1).toString());
                    
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
        
        pasien.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(pasien.getTable().getSelectedRow()!= -1){  
                    TPasien.setText(pasien.getTable().getValueAt(pasien.getTable().getSelectedRow(),2).toString());                                                
                }  
                if(pasien.getTable2().getSelectedRow()!= -1){  
                    TPasien.setText(pasien.getTable2().getValueAt(pasien.getTable2().getSelectedRow(),2).toString());   
                }  
                if(pasien.getTable3().getSelectedRow()!= -1){  
                    TPasien.setText(pasien.getTable3().getValueAt(pasien.getTable3().getSelectedRow(),2).toString());   
                }  
                if(pasien.getTable4().getSelectedRow()!= -1){  
                    TPasien.setText(pasien.getTable4().getValueAt(pasien.getTable4().getSelectedRow(),2).toString());   
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
                if(e.getKeyCode()==KeyEvent.VK_SPACE){
                    pasien.dispose();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });    
        
        pasien.getTable2().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_SPACE){
                    pasien.dispose();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        }); 
        
        pasien.getTable3().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_SPACE){
                    pasien.dispose();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        
        pasien.getTable4().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    pasien.dispose();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        
        pasien.penjab.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(akses.getform().equals("DlgBooking")){
                    if(pasien.penjab.getTable().getSelectedRow()!= -1){
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
        
        pasien.penjab.getTable().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if(akses.getform().equals("DlgBooking")){
                    if(e.getKeyCode()==KeyEvent.VK_SPACE){
                        pasien.penjab.dispose();
                    }
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        
        try {
            aktifjadwal=koneksiDB.JADWALDOKTERDIREGISTRASI();
            URUTNOREG=koneksiDB.URUTNOREG();
        } catch (Exception ex) {
            aktifjadwal="";
            URUTNOREG="";
        }
        
        try {
            BASENOREG=koneksiDB.BASENOREG();
        } catch (Exception e) {
            BASENOREG="registrasi";
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelKdDok = new widget.Label();
        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnEdit1 = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnAll = new widget.Button();
        BtnCekData = new widget.Button();
        BtnKeluar = new widget.Button();
        panelGlass10 = new widget.panelisi();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        panelCari = new widget.panelisi();
        jLabel13 = new widget.Label();
        DTPCari3 = new widget.Tanggal();
        jLabel8 = new widget.Label();
        NmDokter1 = new widget.TextBox();
        BtnDokter1 = new widget.Button();
        PanelInput = new javax.swing.JPanel();
        ChkInput = new widget.CekBox();
        FormInput = new widget.PanelBiasa();
        jLabel4 = new widget.Label();
        jLabel9 = new widget.Label();
        NmDokter = new widget.TextBox();
        TPasien = new widget.TextBox();
        TanggalPeriksa = new widget.Tanggal();
        jLabel10 = new widget.Label();
        KdDokter = new widget.TextBox();
        BtnDokter = new widget.Button();
        jLabel11 = new widget.Label();
        TAlamat = new widget.TextBox();
        jLabel12 = new widget.Label();
        TNotelp = new widget.TextBox();
        TCatatan = new widget.TextBox();
        jLabel14 = new widget.Label();
        jLabel5 = new widget.Label();
        labelBpjs = new widget.Label();
        jLabel16 = new widget.Label();
        labelUmum = new widget.Label();
        cmbStts = new widget.ComboBox();
        jLabel15 = new widget.Label();

        jLabelKdDok.setText("Filter Dokter : ");
        jLabelKdDok.setPreferredSize(new java.awt.Dimension(90, 23));

        setTitle("Booking Kuota Dokter");

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout());

        Scroll.setOpaque(true);

        tbObat.setAutoCreateRowSorter(true);
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbObatMouseClicked(evt);
            }
        });
        tbObat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbObatKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbObatKeyReleased(evt);
            }
        });
        Scroll.setViewportView(tbObat);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(44, 144));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setPreferredSize(new java.awt.Dimension(55, 55));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Simpan");
        BtnSimpan.setToolTipText("Alt+S");
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

        BtnEdit1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png"))); // NOI18N
        BtnEdit1.setMnemonic('S');
        BtnEdit1.setText("Ganti");
        BtnEdit1.setToolTipText("Alt+S");
        BtnEdit1.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnEdit1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnEdit1ActionPerformed(evt);
            }
        });
        BtnEdit1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnEdit1KeyPressed(evt);
            }
        });
        panelGlass8.add(BtnEdit1);

        BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png"))); // NOI18N
        BtnBatal.setMnemonic('B');
        BtnBatal.setText("Baru");
        BtnBatal.setToolTipText("Alt+B");
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

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('M');
        BtnAll.setText("Semua");
        BtnAll.setToolTipText("Alt+M");
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

        BtnCekData.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/add-file-16x16.png"))); // NOI18N
        BtnCekData.setMnemonic('G');
        BtnCekData.setText("Cek Data");
        BtnCekData.setToolTipText("Alt+G");
        BtnCekData.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnCekData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCekDataActionPerformed(evt);
            }
        });
        BtnCekData.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCekDataKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnCekData);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
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

        jPanel3.add(panelGlass8, java.awt.BorderLayout.PAGE_END);

        panelGlass10.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass10.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel6.setText("Key Word :");
        jLabel6.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass10.add(jLabel6);

        TCari.setPreferredSize(new java.awt.Dimension(450, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass10.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('2');
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
        panelGlass10.add(BtnCari);

        jLabel7.setText("Record :");
        jLabel7.setPreferredSize(new java.awt.Dimension(65, 23));
        panelGlass10.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setPreferredSize(new java.awt.Dimension(50, 23));
        panelGlass10.add(LCount);

        jPanel3.add(panelGlass10, java.awt.BorderLayout.CENTER);

        panelCari.setPreferredSize(new java.awt.Dimension(44, 43));
        panelCari.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 9));

        jLabel13.setText("Tanggal Periksa : ");
        jLabel13.setPreferredSize(new java.awt.Dimension(90, 23));
        panelCari.add(jLabel13);

        DTPCari3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "09-05-2025" }));
        DTPCari3.setDisplayFormat("dd-MM-yyyy");
        DTPCari3.setOpaque(false);
        DTPCari3.setPreferredSize(new java.awt.Dimension(100, 23));
        DTPCari3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                DTPCari3ItemStateChanged(evt);
            }
        });
        DTPCari3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DTPCari3KeyPressed(evt);
            }
        });
        panelCari.add(DTPCari3);

        jLabel8.setText("Filter Dokter : ");
        jLabel8.setPreferredSize(new java.awt.Dimension(90, 23));
        panelCari.add(jLabel8);

        NmDokter1.setEditable(false);
        NmDokter1.setHighlighter(null);
        NmDokter1.setPreferredSize(new java.awt.Dimension(200, 24));
        panelCari.add(NmDokter1);

        BtnDokter1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnDokter1.setMnemonic('X');
        BtnDokter1.setToolTipText("Alt+X");
        BtnDokter1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnDokter1ActionPerformed(evt);
            }
        });
        BtnDokter1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnDokter1KeyPressed(evt);
            }
        });
        panelCari.add(BtnDokter1);

        jPanel3.add(panelCari, java.awt.BorderLayout.PAGE_START);

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(192, 156));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setMnemonic('M');
        ChkInput.setText(".: Input Data");
        ChkInput.setBorderPainted(true);
        ChkInput.setBorderPaintedFlat(true);
        ChkInput.setFocusable(false);
        ChkInput.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInput.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
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

        FormInput.setPreferredSize(new java.awt.Dimension(190, 107));
        FormInput.setLayout(null);

        jLabel4.setText("BPJS :");
        FormInput.add(jLabel4);
        jLabel4.setBounds(700, 70, 70, 23);

        jLabel9.setText("Dokter :");
        FormInput.add(jLabel9);
        jLabel9.setBounds(170, 10, 60, 23);

        NmDokter.setEditable(false);
        NmDokter.setHighlighter(null);
        FormInput.add(NmDokter);
        NmDokter.setBounds(320, 10, 210, 23);

        TPasien.setHighlighter(null);
        FormInput.add(TPasien);
        TPasien.setBounds(70, 40, 490, 23);

        TanggalPeriksa.setForeground(new java.awt.Color(50, 70, 50));
        TanggalPeriksa.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2025-05-09" }));
        TanggalPeriksa.setDisplayFormat("yyyy-MM-dd");
        TanggalPeriksa.setOpaque(false);
        TanggalPeriksa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalPeriksaKeyPressed(evt);
            }
        });
        FormInput.add(TanggalPeriksa);
        TanggalPeriksa.setBounds(80, 10, 90, 23);

        jLabel10.setText("Tgl.Periksa :");
        FormInput.add(jLabel10);
        jLabel10.setBounds(0, 10, 70, 23);

        KdDokter.setEditable(false);
        KdDokter.setHighlighter(null);
        FormInput.add(KdDokter);
        KdDokter.setBounds(240, 10, 70, 23);

        BtnDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnDokter.setMnemonic('X');
        BtnDokter.setToolTipText("Alt+X");
        BtnDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnDokterActionPerformed(evt);
            }
        });
        BtnDokter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnDokterKeyPressed(evt);
            }
        });
        FormInput.add(BtnDokter);
        BtnDokter.setBounds(540, 10, 28, 23);

        jLabel11.setText("Cara Bayar :");
        FormInput.add(jLabel11);
        jLabel11.setBounds(570, 40, 70, 23);

        TAlamat.setHighlighter(null);
        TAlamat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TAlamatActionPerformed(evt);
            }
        });
        FormInput.add(TAlamat);
        TAlamat.setBounds(70, 70, 490, 23);

        jLabel12.setText("No. Telp :");
        FormInput.add(jLabel12);
        jLabel12.setBounds(0, 100, 60, 23);

        TNotelp.setHighlighter(null);
        FormInput.add(TNotelp);
        TNotelp.setBounds(70, 100, 490, 23);

        TCatatan.setHighlighter(null);
        FormInput.add(TCatatan);
        TCatatan.setBounds(660, 10, 270, 23);

        jLabel14.setText("Alamat :");
        FormInput.add(jLabel14);
        jLabel14.setBounds(0, 70, 60, 23);

        jLabel5.setText("Nama :");
        FormInput.add(jLabel5);
        jLabel5.setBounds(10, 40, 50, 23);

        labelBpjs.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelBpjs.setText("0");
        labelBpjs.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        FormInput.add(labelBpjs);
        labelBpjs.setBounds(720, 90, 70, 30);

        jLabel16.setText("UMUM/ASURANSI :");
        FormInput.add(jLabel16);
        jLabel16.setBounds(590, 70, 100, 23);

        labelUmum.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelUmum.setText("0");
        labelUmum.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        FormInput.add(labelUmum);
        labelUmum.setBounds(610, 90, 70, 30);

        cmbStts.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-- Cara Bayar --", "BPJS", "UMUM/ASURANSI" }));
        FormInput.add(cmbStts);
        cmbStts.setBounds(660, 40, 150, 23);

        jLabel15.setText("Catatan :");
        FormInput.add(jLabel15);
        jLabel15.setBounds(580, 10, 60, 23);

        PanelInput.add(FormInput, java.awt.BorderLayout.CENTER);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
                    BtnCekDataActionPerformed(null);
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
    }//GEN-LAST:event_tbObatKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if(TPasien.getText().trim().equals("")){
            Valid.textKosong(TPasien,"Nama");
        }else if(NmDokter.getText().trim().equals("")||KdDokter.getText().trim().equals("")){
            Valid.textKosong(KdDokter,"Dokter");
        }else if(TAlamat.getText().trim().equals("")){
            Valid.textKosong(KdDokter,"Alamat");
        }else if(TNotelp.getText().trim().equals("")){
            Valid.textKosong(KdDokter,"No. Telp");
        }else{
            if(akses.getkode().equals("Admin Utama")){
                isBooking();
            }else{
                isBooking();
            }
        }
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnSimpanActionPerformed(null);
        }else{
        }
    }//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnEdit1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEdit1ActionPerformed
        if (!TPasien.getText().equals("")) {
            try {
                Sequel.mengedit("booking_kuota", "nama=? and tgl_periksa=? and alamat=? and no_telp=?", "tgl_periksa=?,nama=?,alamat=?,no_telp=?,kd_dok=?, catatan=?,updated_at=CURRENT_TIMESTAMP()", 10, new String[]{
                    TanggalPeriksa.getSelectedItem() + "", TPasien.getText(), TAlamat.getText(), TNotelp.getText(), KdDokter.getText(), TCatatan.getText(),
                    tbObat.getValueAt(tbObat.getSelectedRow(),3).toString(), tbObat.getValueAt(tbObat.getSelectedRow(),1).toString(),
                    tbObat.getValueAt(tbObat.getSelectedRow(),4).toString(), tbObat.getValueAt(tbObat.getSelectedRow(),5).toString()
                });
                tampil();
                emptTeks();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Maaf, gagal menyimpan data. Silahkan periksa kembali data !");
            }
        }else{
            JOptionPane.showMessageDialog(null, "Maaf, Pilih data yang akan di edit !");
        }

    }//GEN-LAST:event_BtnEdit1ActionPerformed

    private void BtnEdit1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEdit1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnEdit1KeyPressed

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
        for(i=0;i<tbObat.getRowCount();i++){
            if(tbObat.getValueAt(i,0).toString().equals("true")){
                Sequel.queryu2("delete from booking_kuota where tgl_periksa=? and nama=? and alamat=?",3,new String[]{
                    tbObat.getValueAt(i,1).toString(),tbObat.getValueAt(i,3).toString(),tbObat.getValueAt(i,4).toString()
                });
            }
        }
        tampil();
    }//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnHapusActionPerformed(null);
        }
    }//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        NmDokter1.setText("");
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

    private void BtnCekDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCekDataActionPerformed
        pasien.emptTeks();
        pasien.isCek();
        pasien.isPasienBooking(TPasien.getText(), TAlamat.getText());
        pasien.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        pasien.setLocationRelativeTo(internalFrame1);
        pasien.setVisible(true);
    }//GEN-LAST:event_BtnCekDataActionPerformed

    private void BtnCekDataKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCekDataKeyPressed

    }//GEN-LAST:event_BtnCekDataKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
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
            Valid.pindah(evt, TCari, BtnAll);
        }
    }//GEN-LAST:event_BtnCariKeyPressed

    private void DTPCari3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_DTPCari3ItemStateChanged

    }//GEN-LAST:event_DTPCari3ItemStateChanged

    private void DTPCari3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DTPCari3KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_DTPCari3KeyPressed

    private void BtnDokter1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDokter1ActionPerformed
        dokter2.isCek();
        dokter2.TCari.requestFocus();
        dokter2.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        dokter2.setLocationRelativeTo(internalFrame1);
        dokter2.setVisible(true);
    }//GEN-LAST:event_BtnDokter1ActionPerformed

    private void BtnDokter1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnDokter1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnDokter1KeyPressed

    private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInputActionPerformed
        isForm();
    }//GEN-LAST:event_ChkInputActionPerformed

    private void TanggalPeriksaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TanggalPeriksaKeyPressed

    }//GEN-LAST:event_TanggalPeriksaKeyPressed

    private void BtnDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDokterActionPerformed
        if(aktifjadwal.equals("aktif")){
            if(akses.getkode().equals("Admin Utama")){
                dokter.isCek();
                dokter.TCari.requestFocus();
                dokter.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
                dokter.setLocationRelativeTo(internalFrame1);
                dokter.setVisible(true);
            }else{

            }
        }else{
            dokter.isCek();
            dokter.TCari.requestFocus();
            dokter.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
            dokter.setLocationRelativeTo(internalFrame1);
            dokter.setVisible(true);
        }
    }//GEN-LAST:event_BtnDokterActionPerformed

    private void BtnDokterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnDokterKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnDokterActionPerformed(null);
        }else{

        }
    }//GEN-LAST:event_BtnDokterKeyPressed

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

    private void TAlamatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TAlamatActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TAlamatActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DlgBookingKuota.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DlgBookingKuota.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DlgBookingKuota.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DlgBookingKuota.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DlgBookingKuota().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.Button BtnAll;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnCekData;
    private widget.Button BtnDokter;
    private widget.Button BtnDokter1;
    private widget.Button BtnEdit1;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkInput;
    private widget.Tanggal DTPCari3;
    private widget.PanelBiasa FormInput;
    private widget.TextBox KdDokter;
    private widget.Label LCount;
    private widget.TextBox NmDokter;
    private widget.TextBox NmDokter1;
    private javax.swing.JPanel PanelInput;
    private widget.ScrollPane Scroll;
    private widget.TextBox TAlamat;
    public widget.TextBox TCari;
    private widget.TextBox TCatatan;
    private widget.TextBox TNotelp;
    private widget.TextBox TPasien;
    private widget.Tanggal TanggalPeriksa;
    private widget.ComboBox cmbStts;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel12;
    private widget.Label jLabel13;
    private widget.Label jLabel14;
    private widget.Label jLabel15;
    private widget.Label jLabel16;
    private widget.Label jLabel4;
    private widget.Label jLabel5;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.Label jLabel9;
    private widget.Label jLabelKdDok;
    private javax.swing.JPanel jPanel3;
    private widget.Label labelBpjs;
    private widget.Label labelUmum;
    private widget.panelisi panelCari;
    private widget.panelisi panelGlass10;
    private widget.panelisi panelGlass8;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables
private void tampil() { 
        if(!NmDokter1.getText().equals("")){
           status=" kd_dok = '"+jLabelKdDok.getText()+"' and bk.tgl_periksa between '"+Valid.SetTgl(DTPCari3.getSelectedItem()+"")+"' and '"+Valid.SetTgl(DTPCari3.getSelectedItem()+"")+"' "; 
        }else{
            status=" bk.tgl_periksa between '"+Valid.SetTgl(DTPCari3.getSelectedItem()+"")+"' and '"+Valid.SetTgl(DTPCari3.getSelectedItem()+"")+"' "; 
        }
        Valid.tabelKosong(tabMode);
        try {
            ps=koneksi.prepareStatement(
                    "SELECT bk.tgl_periksa, dr.nm_dokter, bk.nama, bk.alamat, bk.no_telp, SUBSTRING(bk.cretaed_at,1,10) AS tgl_booking, dr.kd_dokter, bk.catatan, bk.penjab " +
                    "FROM booking_kuota bk " +
                    "JOIN dokter dr ON dr.kd_dokter = bk.kd_dok " +
                    "WHERE "+status+" AND bk.nama LIKE ? or "+status+" and bk.alamat LIKE  ? or "+status+" and dr.nm_dokter LIKE ? order by dr.kd_dokter asc");
            try {
                ps.setString(1,"%"+TCari.getText().trim()+"%");
                ps.setString(2,"%"+TCari.getText().trim()+"%");
                ps.setString(3,"%"+TCari.getText().trim()+"%");
                rs=ps.executeQuery();
                while(rs.next()){                    
                    tabMode.addRow(new Object[]{
                        false,rs.getString("tgl_periksa"),rs.getString("nm_dokter"),rs.getString("nama"),rs.getString("alamat"),
                        rs.getString("no_telp"),rs.getString("penjab"),rs.getString("catatan"),rs.getString("kd_dokter"),
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
        } catch (Exception e) {
            System.out.println("Notif : "+e);
        } 
        LCount.setText(""+tabMode.getRowCount());
    }


    public void emptTeks() {
        TPasien.setText("");
        TAlamat.setText("");
        TanggalPeriksa.setDate(new Date());
        TNotelp.setText("");
        KdDokter.setText("");
        NmDokter.setText("");
        TCatatan.setText("");
    }
    


    private void getData() {
        if(tbObat.getSelectedRow()!= -1){
            Valid.SetTgl(TanggalPeriksa,tbObat.getValueAt(tbObat.getSelectedRow(),1).toString());
            KdDokter.setText(tbObat.getValueAt(tbObat.getSelectedRow(),7).toString());
            NmDokter.setText(tbObat.getValueAt(tbObat.getSelectedRow(),2).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(),3).toString());
            TAlamat.setText(tbObat.getValueAt(tbObat.getSelectedRow(),4).toString());
            TNotelp.setText(tbObat.getValueAt(tbObat.getSelectedRow(),5).toString());  
            TCatatan.setText(tbObat.getValueAt(tbObat.getSelectedRow(),6).toString());  
            String jmlBpjs = Sequel.cariIsi("SELECT COUNT(bk.kd_dok) as jml FROM booking_kuota bk WHERE bk.tgl_periksa = '"+tbObat.getValueAt(tbObat.getSelectedRow(),1).toString()+"' AND bk.kd_dok = '"+tbObat.getValueAt(tbObat.getSelectedRow(),7).toString()+"' AND bk.penjab = 'BPJS'");
            String jmlUmum = Sequel.cariIsi("SELECT COUNT(bk.kd_dok) as jml FROM booking_kuota bk WHERE bk.tgl_periksa = '"+tbObat.getValueAt(tbObat.getSelectedRow(),1).toString()+"' AND bk.kd_dok = '"+tbObat.getValueAt(tbObat.getSelectedRow(),7).toString()+"' AND bk.penjab = 'UMUM'");
            System.out.println(jmlBpjs);
            System.out.println(jmlUmum);
            labelBpjs.setText(jmlBpjs);
            labelUmum.setText(jmlUmum);
        }
        
    }
    
    public void setNoRm(String norm,String nama) {
        TPasien.setText(nama);
        TCari.setText(norm);
        ChkInput.setSelected(true);
        isForm();
        tampil();
    }
    
    public void setNoRm(String norm,String nama,String kodepoli,String namapoli,String kodedokter,String namadokter) {
        TPasien.setText(nama);
        TAlamat.setText(namapoli);
        KdDokter.setText(kodedokter);
        NmDokter.setText(namadokter);
        TCari.setText(norm);
        ChkInput.setSelected(true);
        isForm();
        tampil();
    }
    
    private void isForm(){
        if(ChkInput.isSelected()==true){
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH,156));
            FormInput.setVisible(true);      
            ChkInput.setVisible(true);
        }else if(ChkInput.isSelected()==false){           
            ChkInput.setVisible(true);            
            PanelInput.setPreferredSize(new Dimension(WIDTH,156));
            FormInput.setVisible(true);      
            ChkInput.setVisible(true);
        }
    }
    
    public void isCek(){
        if(akses.getkode().equals("Admin Utama") || akses.getkode().equals("087")){
            BtnSimpan.setEnabled(true);
            BtnHapus.setEnabled(true);
            BtnCekData.setEnabled(true);
            BtnEdit1.setEnabled(true);
        }else{
            BtnSimpan.setEnabled(akses.getbooking_registrasi());
            BtnHapus.setEnabled(false);
            BtnCekData.setEnabled(akses.getbooking_registrasi());
            BtnEdit1.setEnabled(akses.getbooking_registrasi());
        }
        
    }


    private void isBooking() {
        if(Sequel.menyimpantf("booking_kuota","?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP(),?","Pasien dan Tanggal",9,new String[]{
            TanggalPeriksa.getSelectedItem()+"",
            KdDokter.getText(),
            TPasien.getText(),
            TAlamat.getText(),
            TNotelp.getText(),            
            akses.getkode(),
            TCatatan.getText(),
            cmbStts.getSelectedItem().toString(),
            "0000-00-00 00:00:00"
           })==true){
            emptTeks();
            tampil();
        } 
    }
}
