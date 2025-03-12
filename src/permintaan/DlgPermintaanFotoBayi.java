package permintaan;

import fungsi.WarnaTable;
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
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariDokter;
import keuangan.DlgKamar;
import simrskhanza.DlgCariPasien;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import widget.DlgPreviewImage;
import static widget.DlgPreviewImage.URLPATHIMAGES;

/**
 *
 * @author dosen
 */
public class DlgPermintaanFotoBayi extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private Connection koneksi=koneksiDB.condb();
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i=0;
    private String alarm="";
    private DlgKamar kamar=new DlgKamar(null,false);
    private DlgCariDokter dokter = new DlgCariDokter(null, false);
    private DlgCariDokter dokter2 = new DlgCariDokter(null, false);
    private DlgCariPasien pasien=new DlgCariPasien(null,false);
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Date date = new Date();
    private String now = dateFormat.format(date);
    private DlgPreviewImage PrevImage = new DlgPreviewImage(null, false);
    private String tglcari="", link="";
    private boolean aktif=false;
    private File fotoRaw1, fotoRaw2, fotoJadi1, fotoJadi2, fotoSelected;
    private static String var="";
    private static final Properties prop = new Properties();

    /** Creates new form DlgPemberianInfus
     * @param parent
     * @param modal */
    public DlgPermintaanFotoBayi(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        
        tabMode=new DefaultTableModel(null,new Object[]{
            "No.RM","Nama Bayi","Nama Ibu","Nama Ayah","Jenis Kelamin", "Golongan Darah",
            "Panjang Badan(cm)", "Berat Badan (gram)", "Tanggal Lahir", "Jam Lahir", "Kode Dokter Bayi", "Dokter Bayi", 
            "Kode Dokter Ibu", "Dokter Ibu", "Foto RAW 1", "Foto Raw 2", "Foto Jadi 1", "Foto Jadi 2", "Tanggal Permintaan"
            }){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        tbObat.setModel(tabMode);

        //tbObat.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbObat.getBackground()));
        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 19; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(70);
            }else if(i==1){
                column.setPreferredWidth(150);
            }else if(i==2){
                column.setPreferredWidth(150);
            }else if(i==3){
                column.setPreferredWidth(150);
            }else if(i==4){
                column.setPreferredWidth(100);
            }else if(i==5){
                column.setPreferredWidth(100);
            }else if(i==6){
                column.setPreferredWidth(100);
            }else if(i==7){
                column.setPreferredWidth(100);
            }else if(i==8){
                column.setPreferredWidth(100);
            }else if(i==9){
                column.setPreferredWidth(100);
            }else if(i==11){
                column.setPreferredWidth(150);
            }else if(i==13){
                column.setPreferredWidth(150);
            }else if(i==14){
                column.setPreferredWidth(150);
            }else if(i==15){
                column.setPreferredWidth(150);
            }else if(i==16){
                column.setPreferredWidth(150);
            }else if(i==17){
                column.setPreferredWidth(150);
            }else if(i==18){
                column.setPreferredWidth(100);
            } else if (i == 10 || i == 12) {
                column.setMinWidth(0);
                column.setMaxWidth(0);
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
        
        dokter.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {;
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (akses.getform().equals("DlgPermintaanFotoBayi")) {
                    if (dokter.getTable().getSelectedRow() != -1) {
                        KdDokter1.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 0).toString());
                        TDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString());
                        //isNumber();
                        KdDokter.requestFocus();
                    }
                }
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });

        dokter2.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {;
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (akses.getform().equals("DlgPermintaanFotoBayi")) {
                    if (dokter2.getTable().getSelectedRow() != -1) {
                        KdDokter2.setText(dokter2.getTable().getValueAt(dokter2.getTable().getSelectedRow(), 0).toString());
                        TDokter1.setText(dokter2.getTable().getValueAt(dokter2.getTable().getSelectedRow(), 1).toString());
                        //isNumber();
                        KdDokter2.requestFocus();
                    }
                }
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });
        
        pasien.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (pasien.getTable().getSelectedRow() != -1) {
                    NoRM.setText(pasien.getTable().getValueAt(pasien.getTable().getSelectedRow(), 0).toString());
                    NmPasien.setText(pasien.getTable().getValueAt(pasien.getTable().getSelectedRow(), 1).toString());
                }
                NoRM.requestFocus();
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });

        pasien.getTable().addKeyListener(new KeyListener() {
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

        try {
            alarm=koneksiDB.ALARMPERMINTAANRANAP();
        } catch (Exception e) {
            alarm="no";
        }
        
        try {
            link = URLEDITIMAGES();
        } catch (Exception e) {
            System.out.println("E : " + e);
        }
        
        ChkInput.setSelected(true);
        isForm();
        
        if(alarm.equals("yes")){
            //jam();
        }
        isMenu();        
    }
 
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        KdDokter = new widget.TextBox();
        NoRawatIbu = new widget.TextBox();
        NoRawatBayi = new widget.TextBox();
        jFileChooser1 = new javax.swing.JFileChooser();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        MnEditFoto = new javax.swing.JMenuItem();
        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        BtnKeluar = new widget.Button();
        panelGlass10 = new widget.panelisi();
        jLabel16 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel26 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        PanelInput = new javax.swing.JPanel();
        ChkInput = new widget.CekBox();
        FormInput = new widget.PanelBiasa();
        NmPasien = new widget.TextBox();
        NoRM = new widget.TextBox();
        jLabel5 = new widget.Label();
        jLabel8 = new widget.Label();
        jLabel17 = new widget.Label();
        KdDokter1 = new widget.TextBox();
        TDokter = new widget.TextBox();
        BtnDokter = new widget.Button();
        jLabel18 = new widget.Label();
        NmIbu = new widget.TextBox();
        jLabel19 = new widget.Label();
        NmAyah = new widget.TextBox();
        jLabel21 = new widget.Label();
        KdDokter2 = new widget.TextBox();
        TDokter1 = new widget.TextBox();
        BtnFotoJadi = new widget.Button();
        JKel = new widget.ComboBox();
        Golda = new widget.TextBox();
        label39 = new widget.Label();
        label24 = new widget.Label();
        Panjang = new widget.TextBox();
        label23 = new widget.Label();
        Berat = new widget.TextBox();
        label30 = new widget.Label();
        Lahir = new widget.Tanggal();
        label32 = new widget.Label();
        jam = new widget.ComboBox();
        menit = new widget.ComboBox();
        detik = new widget.ComboBox();
        BtnSeek5 = new widget.Button();
        FotoJadi = new widget.TextBox();
        jLabel20 = new widget.Label();
        jLabel22 = new widget.Label();
        FotoRaw = new widget.TextBox();
        BtnDokter2 = new widget.Button();
        BtnFotoBayi1 = new widget.Button();
        jLabel23 = new widget.Label();
        FotoRaw1 = new widget.TextBox();
        BtnFotoBayi2 = new widget.Button();
        BtnFotoJadi1 = new widget.Button();
        FotoJadi2 = new widget.TextBox();
        jLabel24 = new widget.Label();
        BtnPreviewFotoRaw = new widget.Button();
        BtnPreviewFotoRaw1 = new widget.Button();
        BtnPreviewFotoJadi = new widget.Button();
        BtnPreviewFotoJadi1 = new widget.Button();

        KdDokter.setEditable(false);
        KdDokter.setHighlighter(null);
        KdDokter.setName("KdDokter"); // NOI18N

        NoRawatIbu.setEditable(false);
        NoRawatIbu.setHighlighter(null);
        NoRawatIbu.setName("NoRawatIbu"); // NOI18N

        NoRawatBayi.setEditable(false);
        NoRawatBayi.setHighlighter(null);
        NoRawatBayi.setName("NoRawatBayi"); // NOI18N

        jFileChooser1.setName("jFileChooser1"); // NOI18N

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnEditFoto.setBackground(new java.awt.Color(255, 255, 254));
        MnEditFoto.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnEditFoto.setForeground(new java.awt.Color(50, 50, 50));
        MnEditFoto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnEditFoto.setText("Edit Foto");
        MnEditFoto.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnEditFoto.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnEditFoto.setName("MnEditFoto"); // NOI18N
        MnEditFoto.setPreferredSize(new java.awt.Dimension(160, 26));
        MnEditFoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnEditFotoActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnEditFoto);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Permintaan Foto Bayi ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
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
        panelGlass8.setPreferredSize(new java.awt.Dimension(55, 55));
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

        jPanel3.add(panelGlass8, java.awt.BorderLayout.PAGE_END);

        panelGlass10.setName("panelGlass10"); // NOI18N
        panelGlass10.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass10.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel16.setText("Tanggal Permintaan :");
        jLabel16.setName("jLabel16"); // NOI18N
        jLabel16.setPreferredSize(new java.awt.Dimension(120, 23));
        panelGlass10.add(jLabel16);

        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "12-03-2025" }));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1"); // NOI18N
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(90, 23));
        DTPCari1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                DTPCari1ItemStateChanged(evt);
            }
        });
        DTPCari1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DTPCari1KeyPressed(evt);
            }
        });
        panelGlass10.add(DTPCari1);

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("s.d");
        jLabel26.setName("jLabel26"); // NOI18N
        jLabel26.setPreferredSize(new java.awt.Dimension(30, 23));
        panelGlass10.add(jLabel26);

        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "12-03-2025" }));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2"); // NOI18N
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(90, 23));
        DTPCari2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                DTPCari2ItemStateChanged(evt);
            }
        });
        DTPCari2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DTPCari2KeyPressed(evt);
            }
        });
        panelGlass10.add(DTPCari2);

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(68, 23));
        panelGlass10.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(370, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass10.add(TCari);

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
        panelGlass10.add(BtnCari);

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
        panelGlass10.add(BtnAll);

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass10.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(50, 23));
        panelGlass10.add(LCount);

        jPanel3.add(panelGlass10, java.awt.BorderLayout.CENTER);

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(192, 220));
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
        FormInput.setPreferredSize(new java.awt.Dimension(190, 220));
        FormInput.setLayout(null);

        NmPasien.setHighlighter(null);
        NmPasien.setName("NmPasien"); // NOI18N
        FormInput.add(NmPasien);
        NmPasien.setBounds(160, 10, 330, 23);

        NoRM.setEditable(false);
        NoRM.setHighlighter(null);
        NoRM.setName("NoRM"); // NOI18N
        FormInput.add(NoRM);
        NoRM.setBounds(70, 10, 86, 23);

        jLabel5.setText("Nama Bayi :");
        jLabel5.setName("jLabel5"); // NOI18N
        FormInput.add(jLabel5);
        jLabel5.setBounds(0, 10, 69, 23);

        jLabel8.setText("Jenis Kelamin :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(10, 100, 80, 23);

        jLabel17.setText("DPJP Bayi :");
        jLabel17.setName("jLabel17"); // NOI18N
        FormInput.add(jLabel17);
        jLabel17.setBounds(530, 10, 80, 23);

        KdDokter1.setHighlighter(null);
        KdDokter1.setName("KdDokter1"); // NOI18N
        KdDokter1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdDokter1KeyPressed(evt);
            }
        });
        FormInput.add(KdDokter1);
        KdDokter1.setBounds(610, 10, 90, 23);

        TDokter.setEditable(false);
        TDokter.setName("TDokter"); // NOI18N
        FormInput.add(TDokter);
        TDokter.setBounds(710, 10, 200, 23);

        BtnDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnDokter.setMnemonic('3');
        BtnDokter.setToolTipText("ALt+3");
        BtnDokter.setName("BtnDokter"); // NOI18N
        BtnDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnDokterActionPerformed(evt);
            }
        });
        FormInput.add(BtnDokter);
        BtnDokter.setBounds(910, 10, 28, 23);

        jLabel18.setText("Nama Ibu :");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(0, 40, 69, 23);

        NmIbu.setHighlighter(null);
        NmIbu.setName("NmIbu"); // NOI18N
        FormInput.add(NmIbu);
        NmIbu.setBounds(70, 40, 420, 23);

        jLabel19.setText("Nama Ayah :");
        jLabel19.setName("jLabel19"); // NOI18N
        FormInput.add(jLabel19);
        jLabel19.setBounds(0, 70, 69, 23);

        NmAyah.setHighlighter(null);
        NmAyah.setName("NmAyah"); // NOI18N
        FormInput.add(NmAyah);
        NmAyah.setBounds(70, 70, 420, 23);

        jLabel21.setText("DPJP Ibu :");
        jLabel21.setName("jLabel21"); // NOI18N
        FormInput.add(jLabel21);
        jLabel21.setBounds(530, 40, 80, 23);

        KdDokter2.setHighlighter(null);
        KdDokter2.setName("KdDokter2"); // NOI18N
        KdDokter2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdDokter2KeyPressed(evt);
            }
        });
        FormInput.add(KdDokter2);
        KdDokter2.setBounds(610, 40, 90, 23);

        TDokter1.setEditable(false);
        TDokter1.setName("TDokter1"); // NOI18N
        FormInput.add(TDokter1);
        TDokter1.setBounds(710, 40, 200, 23);

        BtnFotoJadi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnFotoJadi.setMnemonic('3');
        BtnFotoJadi.setToolTipText("ALt+3");
        BtnFotoJadi.setName("BtnFotoJadi"); // NOI18N
        BtnFotoJadi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnFotoJadiActionPerformed(evt);
            }
        });
        FormInput.add(BtnFotoJadi);
        BtnFotoJadi.setBounds(910, 130, 28, 23);

        JKel.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "LAKI-LAKI", "PEREMPUAN" }));
        JKel.setSelectedIndex(1);
        JKel.setName("JKel"); // NOI18N
        JKel.setPreferredSize(new java.awt.Dimension(100, 23));
        JKel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JKelKeyPressed(evt);
            }
        });
        FormInput.add(JKel);
        JKel.setBounds(90, 100, 110, 23);

        Golda.setName("Golda"); // NOI18N
        Golda.setPreferredSize(new java.awt.Dimension(207, 23));
        Golda.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                GoldaKeyPressed(evt);
            }
        });
        FormInput.add(Golda);
        Golda.setBounds(90, 130, 50, 23);

        label39.setText("Golongan Darah :");
        label39.setName("label39"); // NOI18N
        label39.setPreferredSize(new java.awt.Dimension(65, 23));
        FormInput.add(label39);
        label39.setBounds(-10, 130, 100, 23);

        label24.setText("P.B. :");
        label24.setName("label24"); // NOI18N
        label24.setPreferredSize(new java.awt.Dimension(65, 23));
        FormInput.add(label24);
        label24.setBounds(190, 100, 50, 23);

        Panjang.setName("Panjang"); // NOI18N
        Panjang.setPreferredSize(new java.awt.Dimension(207, 23));
        Panjang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PanjangKeyPressed(evt);
            }
        });
        FormInput.add(Panjang);
        Panjang.setBounds(240, 100, 50, 23);

        label23.setText("B.B.(gram) :");
        label23.setName("label23"); // NOI18N
        label23.setPreferredSize(new java.awt.Dimension(65, 23));
        FormInput.add(label23);
        label23.setBounds(290, 100, 70, 23);

        Berat.setName("Berat"); // NOI18N
        Berat.setPreferredSize(new java.awt.Dimension(207, 23));
        Berat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BeratKeyPressed(evt);
            }
        });
        FormInput.add(Berat);
        Berat.setBounds(360, 100, 50, 23);

        label30.setText("Tgl. Lahir :");
        label30.setName("label30"); // NOI18N
        label30.setPreferredSize(new java.awt.Dimension(65, 23));
        FormInput.add(label30);
        label30.setBounds(140, 130, 60, 23);

        Lahir.setDisplayFormat("dd-MM-yyyy");
        Lahir.setName("Lahir"); // NOI18N
        Lahir.setVerifyInputWhenFocusTarget(false);
        Lahir.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                LahirItemStateChanged(evt);
            }
        });
        Lahir.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                LahirKeyPressed(evt);
            }
        });
        FormInput.add(Lahir);
        Lahir.setBounds(200, 130, 90, 23);

        label32.setText("Jam Lahir :");
        label32.setName("label32"); // NOI18N
        label32.setPreferredSize(new java.awt.Dimension(65, 23));
        FormInput.add(label32);
        label32.setBounds(290, 130, 60, 23);

        jam.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        jam.setName("jam"); // NOI18N
        jam.setPreferredSize(new java.awt.Dimension(100, 23));
        jam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jamKeyPressed(evt);
            }
        });
        FormInput.add(jam);
        jam.setBounds(350, 130, 50, 23);

        menit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        menit.setName("menit"); // NOI18N
        menit.setPreferredSize(new java.awt.Dimension(100, 23));
        menit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                menitKeyPressed(evt);
            }
        });
        FormInput.add(menit);
        menit.setBounds(400, 130, 50, 23);

        detik.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        detik.setName("detik"); // NOI18N
        detik.setPreferredSize(new java.awt.Dimension(100, 23));
        detik.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                detikKeyPressed(evt);
            }
        });
        FormInput.add(detik);
        detik.setBounds(450, 130, 50, 23);

        BtnSeek5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnSeek5.setMnemonic('5');
        BtnSeek5.setToolTipText("Alt+5");
        BtnSeek5.setName("BtnSeek5"); // NOI18N
        BtnSeek5.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnSeek5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSeek5ActionPerformed(evt);
            }
        });
        BtnSeek5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnSeek5KeyPressed(evt);
            }
        });
        FormInput.add(BtnSeek5);
        BtnSeek5.setBounds(490, 10, 28, 23);

        FotoJadi.setEditable(false);
        FotoJadi.setHighlighter(null);
        FotoJadi.setName("FotoJadi"); // NOI18N
        FormInput.add(FotoJadi);
        FotoJadi.setBounds(610, 130, 300, 23);

        jLabel20.setText("Foto Bayi Jadi 1 :");
        jLabel20.setName("jLabel20"); // NOI18N
        FormInput.add(jLabel20);
        jLabel20.setBounds(520, 130, 90, 23);

        jLabel22.setText("Foto Bayi Raw 1 :");
        jLabel22.setName("jLabel22"); // NOI18N
        FormInput.add(jLabel22);
        jLabel22.setBounds(520, 70, 90, 23);

        FotoRaw.setEditable(false);
        FotoRaw.setHighlighter(null);
        FotoRaw.setName("FotoRaw"); // NOI18N
        FormInput.add(FotoRaw);
        FotoRaw.setBounds(610, 70, 300, 23);

        BtnDokter2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnDokter2.setMnemonic('3');
        BtnDokter2.setToolTipText("ALt+3");
        BtnDokter2.setName("BtnDokter2"); // NOI18N
        BtnDokter2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnDokter2ActionPerformed(evt);
            }
        });
        FormInput.add(BtnDokter2);
        BtnDokter2.setBounds(910, 40, 28, 23);

        BtnFotoBayi1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnFotoBayi1.setMnemonic('3');
        BtnFotoBayi1.setToolTipText("ALt+3");
        BtnFotoBayi1.setName("BtnFotoBayi1"); // NOI18N
        BtnFotoBayi1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnFotoBayi1ActionPerformed(evt);
            }
        });
        FormInput.add(BtnFotoBayi1);
        BtnFotoBayi1.setBounds(910, 70, 28, 23);

        jLabel23.setText("Foto Bayi Raw 2 :");
        jLabel23.setName("jLabel23"); // NOI18N
        FormInput.add(jLabel23);
        jLabel23.setBounds(520, 100, 90, 23);

        FotoRaw1.setEditable(false);
        FotoRaw1.setHighlighter(null);
        FotoRaw1.setName("FotoRaw1"); // NOI18N
        FormInput.add(FotoRaw1);
        FotoRaw1.setBounds(610, 100, 300, 23);

        BtnFotoBayi2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnFotoBayi2.setMnemonic('3');
        BtnFotoBayi2.setToolTipText("ALt+3");
        BtnFotoBayi2.setName("BtnFotoBayi2"); // NOI18N
        BtnFotoBayi2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnFotoBayi2ActionPerformed(evt);
            }
        });
        FormInput.add(BtnFotoBayi2);
        BtnFotoBayi2.setBounds(910, 100, 28, 23);

        BtnFotoJadi1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnFotoJadi1.setMnemonic('3');
        BtnFotoJadi1.setToolTipText("ALt+3");
        BtnFotoJadi1.setName("BtnFotoJadi1"); // NOI18N
        BtnFotoJadi1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnFotoJadi1ActionPerformed(evt);
            }
        });
        FormInput.add(BtnFotoJadi1);
        BtnFotoJadi1.setBounds(910, 160, 28, 23);

        FotoJadi2.setEditable(false);
        FotoJadi2.setHighlighter(null);
        FotoJadi2.setName("FotoJadi2"); // NOI18N
        FormInput.add(FotoJadi2);
        FotoJadi2.setBounds(610, 160, 300, 23);

        jLabel24.setText("Foto Bayi Jadi 2 :");
        jLabel24.setName("jLabel24"); // NOI18N
        FormInput.add(jLabel24);
        jLabel24.setBounds(520, 160, 90, 23);

        BtnPreviewFotoRaw.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/editcopy.png"))); // NOI18N
        BtnPreviewFotoRaw.setMnemonic('3');
        BtnPreviewFotoRaw.setToolTipText("ALt+3");
        BtnPreviewFotoRaw.setName("BtnPreviewFotoRaw"); // NOI18N
        BtnPreviewFotoRaw.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPreviewFotoRawActionPerformed(evt);
            }
        });
        FormInput.add(BtnPreviewFotoRaw);
        BtnPreviewFotoRaw.setBounds(930, 70, 28, 23);

        BtnPreviewFotoRaw1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/editcopy.png"))); // NOI18N
        BtnPreviewFotoRaw1.setMnemonic('3');
        BtnPreviewFotoRaw1.setToolTipText("ALt+3");
        BtnPreviewFotoRaw1.setName("BtnPreviewFotoRaw1"); // NOI18N
        BtnPreviewFotoRaw1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPreviewFotoRaw1ActionPerformed(evt);
            }
        });
        FormInput.add(BtnPreviewFotoRaw1);
        BtnPreviewFotoRaw1.setBounds(930, 100, 28, 23);

        BtnPreviewFotoJadi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/editcopy.png"))); // NOI18N
        BtnPreviewFotoJadi.setMnemonic('3');
        BtnPreviewFotoJadi.setToolTipText("ALt+3");
        BtnPreviewFotoJadi.setName("BtnPreviewFotoJadi"); // NOI18N
        BtnPreviewFotoJadi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPreviewFotoJadiActionPerformed(evt);
            }
        });
        FormInput.add(BtnPreviewFotoJadi);
        BtnPreviewFotoJadi.setBounds(930, 130, 28, 23);

        BtnPreviewFotoJadi1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/editcopy.png"))); // NOI18N
        BtnPreviewFotoJadi1.setMnemonic('3');
        BtnPreviewFotoJadi1.setToolTipText("ALt+3");
        BtnPreviewFotoJadi1.setName("BtnPreviewFotoJadi1"); // NOI18N
        BtnPreviewFotoJadi1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPreviewFotoJadi1ActionPerformed(evt);
            }
        });
        FormInput.add(BtnPreviewFotoJadi1);
        BtnPreviewFotoJadi1.setBounds(930, 160, 28, 23);

        PanelInput.add(FormInput, java.awt.BorderLayout.CENTER);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-DD HH:MM:SS");
        String formattedDate = now.format(formatter);
        
        if (NoRM.getText().trim().equals("")) {
            Valid.textKosong(NoRM, "No.Rekam Medis");
        } else if (NmIbu.getText().trim().equals("")) {
            Valid.textKosong(NmIbu, "Nama Ibu");
        } else if (NmAyah.getText().trim().equals("")) {
            Valid.textKosong(NmAyah, "Nama Ayah");
        } else {
            if (Sequel.menyimpantf("permintaan_foto_bayi", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NOW()", "No.Rekam Medis Pasien", 16, new String[]{
                NoRM.getText(), NmPasien.getText(),NmIbu.getText(),NmAyah.getText(),JKel.getSelectedItem().toString().substring(0,1),
                Golda.getText(),Berat.getText(), Panjang.getText(), Valid.SetTgl(Lahir.getSelectedItem()+""), jam.getSelectedItem()+":"+menit.getSelectedItem()+":"+detik.getSelectedItem(),
                KdDokter1.getText(), KdDokter2.getText(), "","","",""
            }) == true) {
                try {
                    
                    if(!FotoRaw.getText().trim().equals("")){
                        String path = PrevImage.saveToServer(FotoRaw.getText(), NoRM.getText(), NmPasien.getText(), "RAW 1", "");
                        Sequel.mengedit("permintaan_foto_bayi","no_rm = ?", "foto_raw1=?", 2, new String[]{path, NoRM.getText()});
                    }if(!FotoRaw1.getText().trim().equals("")){
                        String path = PrevImage.saveToServer(FotoRaw1.getText(), NoRM.getText(), NmPasien.getText(), "RAW 2", "");
                        Sequel.mengedit("permintaan_foto_bayi","no_rm = ?", "foto_raw2=?", 2, new String[]{path, NoRM.getText()});
                    }if(!FotoJadi.getText().trim().equals("")){
                        String path = PrevImage.saveToServer(FotoJadi.getText(), NoRM.getText(), NmPasien.getText(), "JADI 1", "");
                        Sequel.mengedit("permintaan_foto_bayi","no_rm = ?", "foto_jadi2=1", 2, new String[]{path, NoRM.getText()});
                    }if(!FotoJadi2.getText().trim().equals("")){
                        String path = PrevImage.saveToServer(FotoJadi2.getText(), NoRM.getText(), NmPasien.getText(), "JADI 2", "");
                        Sequel.mengedit("permintaan_foto_bayi","no_rm = ?", "foto_jadi2=?", 2, new String[]{path, NoRM.getText()});
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Gagal menyimpan kelahiran bayi, silahkan periksa data yang mau disimpan..!!");
                }
                emptTeks();
            }
        }
        tampil();
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed

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
        int reply = JOptionPane.showConfirmDialog(rootPane, 
            "Apakah yakin menghapus data permintaan foto bayi " + NmPasien.getText() + " ?", 
            "Konfirmasi", JOptionPane.YES_NO_OPTION);

        if (reply == JOptionPane.YES_OPTION) {
            String[] fotoPaths = {
                FotoRaw.getText().trim(),
                FotoRaw1.getText().trim(),
                FotoJadi.getText().trim(),
                FotoJadi2.getText().trim()
            };

            int[] columnIndexes = {14, 15, 16, 17};

            for (int i = 0; i < fotoPaths.length; i++) {
                if (!fotoPaths[i].isEmpty()) {
                    PrevImage.deleteFromServer(tbObat.getValueAt(tbObat.getSelectedRow(), columnIndexes[i]).toString());
                }
            }

            Valid.hapusTable(tabMode, NoRM, "permintaan_foto_bayi", "no_rm");
            tampil();
        }
}//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnHapusActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnBatal, BtnKeluar);
        }
}//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
}//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            dispose();
        }else{
            Valid.pindah(evt,BtnBatal,TCari);
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

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        tampil();
}//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            tampil();
            TCari.setText("");
        }else{
            Valid.pindah(evt, BtnCari, NmPasien);
        }
}//GEN-LAST:event_BtnAllKeyPressed

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

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed
        if (NoRM.getText().trim().equals("")) {
            Valid.textKosong(NoRM, "No.Rekam Medis");
        } else if (NmPasien.getText().trim().equals("")) {
            Valid.textKosong(NmPasien, "Nama Bayi");
        } else if (NmIbu.getText().trim().equals("")) {
            Valid.textKosong(NmIbu, "Nama Ibu");
        } else if (NmAyah.getText().trim().equals("")) {
            Valid.textKosong(NmAyah, "Nama Ayah");
        } else {
            Valid.editTable(tabMode, "permintaan_foto_bayi", "no_rm", NoRM, "no_rm='" + NoRM.getText()
                    + "',nama_bayi='" + NmPasien.getText()
                    + "',nama_ibu='" + NmIbu.getText()
                    + "',nama_ayah='" + NmAyah.getText()
                    + "',jk='" + JKel.getSelectedItem().toString().substring(0, 1)
                    + "',golda='" + Golda.getText()
                    + "',bb='" + Berat.getText()
                    + "',pb='" + Panjang.getText()
                    + "',tgl_lahir='" + Valid.SetTgl(Lahir.getSelectedItem() + "")
                    + "',jam_lahir='" + jam.getSelectedItem() + ":" + menit.getSelectedItem() + ":" + detik.getSelectedItem()
                    + "',dpjp_bayi='" + KdDokter1.getText()
                    + "',dpjp_ibu='" + KdDokter2.getText() + "'");
            if (!FotoRaw.getText().trim().equals(tbObat.getValueAt(tbObat.getSelectedRow(),14).toString())) {
                String path = PrevImage.saveToServer(FotoRaw.getText(), NoRM.getText(), NmPasien.getText(), "RAW 1", tbObat.getValueAt(tbObat.getSelectedRow(),18).toString());
                Sequel.mengedit("permintaan_foto_bayi", "no_rm = ?", "foto_raw1=?", 2, new String[]{path, NoRM.getText()});
            }
            if (!FotoRaw1.getText().trim().equals(tbObat.getValueAt(tbObat.getSelectedRow(),15).toString())) {
                String path = PrevImage.saveToServer(FotoRaw1.getText(), NoRM.getText(), NmPasien.getText(), "RAW 2", tbObat.getValueAt(tbObat.getSelectedRow(),18).toString());
                Sequel.mengedit("permintaan_foto_bayi", "no_rm = ?", "foto_raw2=?", 2, new String[]{path, NoRM.getText()});
            }
            if (!FotoJadi.getText().trim().equals(tbObat.getValueAt(tbObat.getSelectedRow(),16).toString())) {
                String path = PrevImage.saveToServer(FotoJadi.getText(), NoRM.getText(), NmPasien.getText(), "JADI 1", tbObat.getValueAt(tbObat.getSelectedRow(),18).toString());
                Sequel.mengedit("permintaan_foto_bayi", "no_rm = ?", "foto_jadi2=?", 2, new String[]{path, NoRM.getText()});
            }
            if (!FotoJadi2.getText().trim().equals(tbObat.getValueAt(tbObat.getSelectedRow(),17).toString())) {
                String path = PrevImage.saveToServer(FotoJadi2.getText(), NoRM.getText(), NmPasien.getText(), "JADI 2", tbObat.getValueAt(tbObat.getSelectedRow(),18).toString());
                Sequel.mengedit("permintaan_foto_bayi", "no_rm = ?", "foto_jadi2=?", 2, new String[]{path, NoRM.getText()});
            }
            tampil();
            emptTeks();
        }
    }//GEN-LAST:event_BtnEditActionPerformed

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEditKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnEditActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnHapus, BtnKeluar);
        }
    }//GEN-LAST:event_BtnEditKeyPressed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        aktif=true;
        tampil();
    }//GEN-LAST:event_formWindowOpened

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        aktif=false;
        kamar.dispose();
    }//GEN-LAST:event_formWindowClosed

    
    private void KdDokter1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdDokter1KeyPressed

    }//GEN-LAST:event_KdDokter1KeyPressed

    private void BtnDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDokterActionPerformed
        akses.setform("DlgPermintaanFotoBayi");
        dokter.isCek();
        dokter.TCari.requestFocus();
        dokter.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setVisible(true);
    }//GEN-LAST:event_BtnDokterActionPerformed

    private void KdDokter2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdDokter2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_KdDokter2KeyPressed

    private void BtnFotoJadiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnFotoJadiActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            jFileChooser1.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "jpeg"));
            int result = jFileChooser1.showOpenDialog(null);

            // Dapatkan daftar file yang ada di direktori saat ini
            File currentDirectory = jFileChooser1.getCurrentDirectory();
            File[] files = currentDirectory.listFiles((dir, name) -> name.toLowerCase().matches(".*\\.(jpg|jpeg|png)"));

            // Sort file berdasarkan tanggal terakhir dimodifikasi (terbaru di atas)
            if (files != null) {
                Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
            }
            
            if (result == JFileChooser.APPROVE_OPTION) {
                fotoJadi1 = jFileChooser1.getSelectedFile();
                FotoJadi.setText(fotoJadi1.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_BtnFotoJadiActionPerformed

    private void JKelKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_JKelKeyPressed

    }//GEN-LAST:event_JKelKeyPressed

    private void GoldaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_GoldaKeyPressed

    }//GEN-LAST:event_GoldaKeyPressed

    private void PanjangKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PanjangKeyPressed
        Valid.pindah(evt,JKel,Berat);
    }//GEN-LAST:event_PanjangKeyPressed

    private void BeratKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BeratKeyPressed
        Valid.pindah(evt,Panjang,Golda);
    }//GEN-LAST:event_BeratKeyPressed

    private void LahirItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_LahirItemStateChanged

    }//GEN-LAST:event_LahirItemStateChanged

    private void LahirKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_LahirKeyPressed

    }//GEN-LAST:event_LahirKeyPressed

    private void jamKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jamKeyPressed
        Valid.pindah(evt,Lahir, menit);
    }//GEN-LAST:event_jamKeyPressed

    private void menitKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_menitKeyPressed
        Valid.pindah(evt,jam,detik);
    }//GEN-LAST:event_menitKeyPressed

    private void detikKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_detikKeyPressed

    }//GEN-LAST:event_detikKeyPressed

    private void BtnSeek5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSeek5ActionPerformed
        pasien.emptTeks();
        pasien.isCek();
        pasien.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        pasien.setLocationRelativeTo(internalFrame1);
        pasien.setAlwaysOnTop(false);
        pasien.setVisible(true);
    }//GEN-LAST:event_BtnSeek5ActionPerformed

    private void BtnSeek5KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSeek5KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnSeek5KeyPressed

    private void BtnDokter2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDokter2ActionPerformed
        akses.setform("DlgPermintaanFotoBayi");
        dokter2.isCek();
        dokter2.TCari.requestFocus();
        dokter2.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dokter2.setLocationRelativeTo(internalFrame1);
        dokter2.setVisible(true);
    }//GEN-LAST:event_BtnDokter2ActionPerformed

    private void BtnFotoBayi1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnFotoBayi1ActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            JFileChooser jFileChooser1 = new JFileChooser();
            jFileChooser1.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "jpeg"));

            // Dapatkan daftar file yang ada di direktori saat ini
            File currentDirectory = jFileChooser1.getCurrentDirectory();
            File[] files = currentDirectory.listFiles((dir, name) -> name.toLowerCase().matches(".*\\.(jpg|jpeg|png)"));

            // Sort file berdasarkan tanggal terakhir dimodifikasi (terbaru di atas)
            if (files != null) {
                Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
            }

            int result = jFileChooser1.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                fotoRaw1 = jFileChooser1.getSelectedFile();
                FotoRaw.setText(fotoRaw1.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_BtnFotoBayi1ActionPerformed

    private void BtnFotoBayi2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnFotoBayi2ActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            jFileChooser1.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "jpeg"));
            int result = jFileChooser1.showOpenDialog(null);
            // Dapatkan daftar file yang ada di direktori saat ini
            File currentDirectory = jFileChooser1.getCurrentDirectory();
            File[] files = currentDirectory.listFiles((dir, name) -> name.toLowerCase().matches(".*\\.(jpg|jpeg|png)"));

            // Sort file berdasarkan tanggal terakhir dimodifikasi (terbaru di atas)
            if (files != null) {
                Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
            }
            if (result == JFileChooser.APPROVE_OPTION) {
                fotoRaw2 = jFileChooser1.getSelectedFile();
                FotoRaw1.setText(fotoRaw2.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_BtnFotoBayi2ActionPerformed

    private void BtnFotoJadi1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnFotoJadi1ActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            jFileChooser1.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "jpeg"));
            int result = jFileChooser1.showOpenDialog(null);

            // Dapatkan daftar file yang ada di direktori saat ini
            File currentDirectory = jFileChooser1.getCurrentDirectory();
            File[] files = currentDirectory.listFiles((dir, name) -> name.toLowerCase().matches(".*\\.(jpg|jpeg|png)"));

            // Sort file berdasarkan tanggal terakhir dimodifikasi (terbaru di atas)
            if (files != null) {
                Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
            }
            
            if (result == JFileChooser.APPROVE_OPTION) {
                fotoJadi2 = jFileChooser1.getSelectedFile();
                FotoJadi2.setText(fotoJadi2.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_BtnFotoJadi1ActionPerformed

    private void DTPCari1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_DTPCari1ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_DTPCari1ItemStateChanged

    private void DTPCari1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DTPCari1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_DTPCari1KeyPressed

    private void DTPCari2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_DTPCari2ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_DTPCari2ItemStateChanged

    private void DTPCari2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DTPCari2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_DTPCari2KeyPressed

    private void BtnPreviewFotoRawActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPreviewFotoRawActionPerformed
        if (!FotoRaw.getText().trim().equals("")) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                DlgPreviewImage form = new DlgPreviewImage(null, false);
                form.setSize(internalFrame1.getWidth()-100,internalFrame1.getHeight()-20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                form.tampil(FotoRaw.getText());
                this.setCursor(Cursor.getDefaultCursor());
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            }
            this.setCursor(Cursor.getDefaultCursor());
        } else {
            JOptionPane.showMessageDialog(null, "Maaf, Foto masih kosong, Harap pilih foto terlebih dahulu...!!!!");
        }
    }//GEN-LAST:event_BtnPreviewFotoRawActionPerformed

    private void BtnPreviewFotoRaw1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPreviewFotoRaw1ActionPerformed
        if (!FotoRaw1.getText().trim().equals("")) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                DlgPreviewImage form = new DlgPreviewImage(null, false);
                form.setSize(internalFrame1.getWidth()-100,internalFrame1.getHeight()-20);
                form.setLocationRelativeTo(internalFrame1);
                form.tampil(FotoRaw1.getText());
                form.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            }
            this.setCursor(Cursor.getDefaultCursor());
        } else {
            JOptionPane.showMessageDialog(null, "Maaf, Foto masih kosong, Harap pilih foto terlebih dahulu...!!!!");
        }
    }//GEN-LAST:event_BtnPreviewFotoRaw1ActionPerformed

    private void BtnPreviewFotoJadiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPreviewFotoJadiActionPerformed
         if (!FotoJadi.getText().trim().equals("")) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                DlgPreviewImage form = new DlgPreviewImage(null, false);
                form.setSize(internalFrame1.getWidth()-100,internalFrame1.getHeight()-20);
                form.setLocationRelativeTo(internalFrame1);
                form.tampil(FotoJadi.getText());
                form.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            }
            this.setCursor(Cursor.getDefaultCursor());
        } else {
            JOptionPane.showMessageDialog(null, "Maaf, Foto masih kosong, Harap pilih foto terlebih dahulu...!!!!");
        }
    }//GEN-LAST:event_BtnPreviewFotoJadiActionPerformed

    private void BtnPreviewFotoJadi1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPreviewFotoJadi1ActionPerformed
         if (!FotoJadi2.getText().trim().equals("")) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                DlgPreviewImage form = new DlgPreviewImage(null, false);
                form.setSize(internalFrame1.getWidth()-100,internalFrame1.getHeight()-20);
                form.setLocationRelativeTo(internalFrame1);
                form.tampil(FotoJadi2.getText());
                form.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            }
            this.setCursor(Cursor.getDefaultCursor());
        } else {
            JOptionPane.showMessageDialog(null, "Maaf, Foto masih kosong, Harap pilih foto terlebih dahulu...!!!!");
        }
    }//GEN-LAST:event_BtnPreviewFotoJadi1ActionPerformed

    private void MnEditFotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnEditFotoActionPerformed
        if (tbObat.getSelectedRow() != -1) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            if (!FotoRaw.getText().equals("")) {
                Valid.panggilUrl2(link + "editfotobayi.php?datafoto=" + NoRM.getText() + "-1");

                if (!FotoRaw1.getText().equals("")) {
                    // Jalankan delay di thread terpisah
                    new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            Thread.sleep(2000); // Delay 2 detik
                            return null;
                        }

                        @Override
                        protected void done() {
                            Valid.panggilUrl2(link + "editfotobayi.php?datafoto=" + NoRM.getText() + "-2");
                        }
                    }.execute();
                }
            }

            this.setCursor(Cursor.getDefaultCursor());
        } else {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data Foto Bayi yang mau dicetak...!!!!");
            BtnBatal.requestFocus();
        }
    }//GEN-LAST:event_MnEditFotoActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgPermintaanFotoBayi dialog = new DlgPermintaanFotoBayi(new javax.swing.JFrame(), true);
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
    private widget.TextBox Berat;
    private widget.Button BtnAll;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnDokter;
    private widget.Button BtnDokter2;
    private widget.Button BtnEdit;
    private widget.Button BtnFotoBayi1;
    private widget.Button BtnFotoBayi2;
    private widget.Button BtnFotoJadi;
    private widget.Button BtnFotoJadi1;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPreviewFotoJadi;
    private widget.Button BtnPreviewFotoJadi1;
    private widget.Button BtnPreviewFotoRaw;
    private widget.Button BtnPreviewFotoRaw1;
    private widget.Button BtnSeek5;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkInput;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.PanelBiasa FormInput;
    private widget.TextBox FotoJadi;
    private widget.TextBox FotoJadi2;
    private widget.TextBox FotoRaw;
    private widget.TextBox FotoRaw1;
    private widget.TextBox Golda;
    private widget.ComboBox JKel;
    private widget.TextBox KdDokter;
    private widget.TextBox KdDokter1;
    private widget.TextBox KdDokter2;
    private widget.Label LCount;
    private widget.Tanggal Lahir;
    private javax.swing.JMenuItem MnEditFoto;
    private widget.TextBox NmAyah;
    private widget.TextBox NmIbu;
    private widget.TextBox NmPasien;
    private widget.TextBox NoRM;
    private widget.TextBox NoRawatBayi;
    private widget.TextBox NoRawatIbu;
    private javax.swing.JPanel PanelInput;
    private widget.TextBox Panjang;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TDokter;
    private widget.TextBox TDokter1;
    private javax.swing.ButtonGroup buttonGroup1;
    private widget.ComboBox detik;
    private widget.InternalFrame internalFrame1;
    private javax.swing.JFileChooser jFileChooser1;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private widget.Label jLabel18;
    private widget.Label jLabel19;
    private widget.Label jLabel20;
    private widget.Label jLabel21;
    private widget.Label jLabel22;
    private widget.Label jLabel23;
    private widget.Label jLabel24;
    private widget.Label jLabel26;
    private widget.Label jLabel5;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.ComboBox jam;
    private widget.Label label23;
    private widget.Label label24;
    private widget.Label label30;
    private widget.Label label32;
    private widget.Label label39;
    private widget.ComboBox menit;
    private widget.panelisi panelGlass10;
    private widget.panelisi panelGlass8;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

    public void tampil() {     
        tglcari=" pfb.tgl_permintaan between '"+Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00' and '"+Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:00' and ";      
        
        Valid.tabelKosong(tabMode);
        try{
            ps = koneksi.prepareStatement("SELECT pfb.no_rm,pfb.nama_bayi, pfb.nama_ibu, pfb.nama_ayah, pfb.jk, pfb.golda, pfb.pb, pfb.bb,\n"
                    + "pfb.tgl_lahir, pfb.jam_lahir, pfb.dpjp_bayi, dokter1.nm_dokter as dokter_bayi, pfb.dpjp_ibu, dokter2.nm_dokter as dokter_ibu,\n"
                    + "pfb.foto_raw1, pfb.foto_raw2, pfb.foto_jadi1, pfb.foto_jadi2, pfb.tgl_permintaan\n"
                    + "from permintaan_foto_bayi pfb\n"
                    + "INNER JOIN pasien ON pasien.no_rkm_medis = pfb.no_rm\n"
                    + "INNER JOIN dokter AS dokter1 ON dokter1.kd_dokter = pfb.dpjp_bayi\n"
                    + "INNER JOIN dokter AS dokter2 ON dokter2.kd_dokter = pfb.dpjp_ibu \n"
                    + "where " + tglcari + " pfb.no_rm like '%" + TCari.getText().trim() + "%' "
                    + "or " + tglcari + " pfb.nama_bayi like '%" + TCari.getText().trim() + "%' "
                    + "or " + tglcari + " pfb.nama_ibu like '%" + TCari.getText().trim() + "%' "
                    + "or " + tglcari + " pfb.nama_ayah like '%" + TCari.getText().trim() + "%' "
                    + " order by pfb.no_rm desc");
            try {
                rs=ps.executeQuery();
                while(rs.next()){
                    tabMode.addRow(new Object[]{
                        rs.getString("no_rm"),rs.getString("nama_bayi"),rs.getString("nama_ibu"),rs.getString("nama_ayah"),
                        rs.getString("jk"),rs.getString("golda"),rs.getString("pb"),rs.getString("bb"),rs.getString("tgl_lahir"),
                        rs.getString("jam_lahir"),  rs.getString("dpjp_bayi"), rs.getString("dokter_bayi"), rs.getString("dpjp_ibu"), 
                        rs.getString("dokter_ibu"), rs.getString("foto_raw1"), rs.getString("foto_raw2"), rs.getString("foto_jadi1"), 
                        rs.getString("foto_jadi2"), rs.getString("tgl_permintaan").replace(".0", "")
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
        int b=tabMode.getRowCount();
        LCount.setText(""+b);
    }

    public void emptTeks() {
        NoRM.setText("");
        NoRawatBayi.setText("");
        NmPasien.setText("");
        NmIbu.setText("");
        NmAyah.setText("");
        JKel.setSelectedIndex(0);
        Golda.setText("");
        Berat.setText("");
        Panjang.setText("");
        Lahir.setDate(new Date());
        jam.setSelectedIndex(0);
        menit.setSelectedIndex(0);
        detik.setSelectedIndex(0);
        KdDokter1.setText("");
        TDokter.setText(""); 
        KdDokter2.setText(""); 
        TDokter1.setText(""); 
        FotoRaw.setText(""); 
        FotoRaw1.setText(""); 
        FotoJadi.setText(""); 
        FotoJadi2.setText(""); 
        NoRM.requestFocus();
    }

    private void getData() {
         if(tbObat.getSelectedRow()!= -1){
            NoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());
            NmPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(),1).toString());
            NmIbu.setText(tbObat.getValueAt(tbObat.getSelectedRow(),2).toString());
            NmAyah.setText(tbObat.getValueAt(tbObat.getSelectedRow(),3).toString());
            if(tbObat.getValueAt(tbObat.getSelectedRow(),4).toString().equals("L")){
                JKel.setSelectedItem("LAKI-LAKI");
            }else if(tbObat.getValueAt(tbObat.getSelectedRow(),4).toString().equals("P")){
                JKel.setSelectedItem("PEREMPUAN");
            }
            Golda.setText(tbObat.getValueAt(tbObat.getSelectedRow(),5).toString());
            Panjang.setText(tbObat.getValueAt(tbObat.getSelectedRow(),6).toString());
            Berat.setText(tbObat.getValueAt(tbObat.getSelectedRow(),7).toString());
            Valid.SetTgl(Lahir,tbObat.getValueAt(tbObat.getSelectedRow(),8).toString());   
            jam.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),9).toString().substring(0,2));
            menit.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),9).toString().substring(3,5));
            detik.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),9).toString().substring(6,8));
            KdDokter1.setText(tbObat.getValueAt(tbObat.getSelectedRow(),10).toString());
            TDokter.setText(tbObat.getValueAt(tbObat.getSelectedRow(),11).toString());
            KdDokter2.setText(tbObat.getValueAt(tbObat.getSelectedRow(),12).toString());
            TDokter1.setText(tbObat.getValueAt(tbObat.getSelectedRow(),13).toString());
            FotoRaw.setText(tbObat.getValueAt(tbObat.getSelectedRow(),14).toString());
            FotoRaw1.setText(tbObat.getValueAt(tbObat.getSelectedRow(),15).toString());
            FotoJadi.setText(tbObat.getValueAt(tbObat.getSelectedRow(),16).toString());
            FotoJadi2.setText(tbObat.getValueAt(tbObat.getSelectedRow(),17).toString());
        }
    }
    
    public void setNoRm(String norm, String no_rawat) {
        NoRawatBayi.setText(no_rawat);
        TCari.setText(norm);
        NoRM.setText(norm);
        Sequel.cariIsi("select pasien.nm_pasien from pasien where no_rkm_medis = ?", NmPasien,norm);
        try {
            ps = koneksi.prepareStatement("SELECT pasien.nm_pasien, pasien.nm_ibu, if(pasien.jk = 'L', 'Laki-Laki', 'Perempuan') AS jk, pasien.tgl_lahir,\n"
                    + "concat(reg_periksa.umurdaftar,' ',reg_periksa.sttsumur)as umur,COALESCE(pasien_bayi.nama_ayah,'') AS nama_ayah, COALESCE(pasien_bayi.berat_badan,'0') AS berat_badan,\n"
                    + "COALESCE(pasien_bayi.panjang_badan, '0') AS panjang_badan, COALESCE(pasien_bayi.lingkar_dada,'') as lingkar_dada, "
                    + "pasien.tgl_lahir, COALESCE(pasien_bayi.jam_lahir, '00:00:00') as jam_lahir, dpjp_ranap.kd_dokter, dokter.nm_dokter\n"
                    + "FROM reg_periksa INNER JOIN pasien ON pasien.no_rkm_medis = reg_periksa.no_rkm_medis\n"
                    + "INNER JOIN ranap_gabung ON ranap_gabung.no_rawat2 = reg_periksa.no_rawat\n"
                    + "LEFT JOIN pasien_bayi ON pasien_bayi.no_rkm_medis = pasien.no_rkm_medis\n"
                    + "LEFT JOIN dpjp_ranap ON dpjp_ranap.no_rawat = reg_periksa.no_rawat\n"
                    + "INNER JOIN dokter ON dokter.kd_dokter = dpjp_ranap.kd_dokter "
                    + "WHERE reg_periksa.no_rawat =  ?");
            try {                  
                ps.setString(1, no_rawat);
                rs=ps.executeQuery();
                if(rs.next()){
                    NmIbu.setText(rs.getString("nm_ibu"));
                    JKel.setSelectedItem(rs.getString("jk"));
                    Valid.SetTgl(Lahir,rs.getString("tgl_lahir"));
                    jam.setSelectedItem(rs.getString("jam_lahir").substring(0,2));
                    menit.setSelectedItem(rs.getString("jam_lahir").substring(3,5));
                    detik.setSelectedItem(rs.getString("jam_lahir").substring(6,8));
                    Panjang.setText(rs.getString("panjang_badan"));
                    Berat.setText(rs.getString("panjang_badan"));
                    Golda.setText(rs.getString("lingkar_dada"));
                    KdDokter1.setText(rs.getString("kd_dokter"));
                    TDokter.setText(rs.getString("nm_dokter"));
                }
            } catch (Exception e) {
                System.out.println(e);
            } finally{
                if(rs!=null){
                    rs.close();
                }
                if(rs!=null){
                    rs.close();
                }
            }              
        } catch (Exception e) {
            System.out.println(e);
        }
        
        String no_rawat_ibu = Sequel.cariIsi("select ranap_gabung.no_rawat from ranap_gabung where ranap_gabung.no_rawat2=?", no_rawat);
        if(!no_rawat_ibu.isBlank()){
            Sequel.cariIsi("Select dpjp_ranap.kd_dokter from dpjp_ranap where dpjp_ranap.no_rawat = ?", KdDokter2,no_rawat_ibu );
            Sequel.cariIsi("Select dokter.nm_dokter from dpjp_ranap INNER JOIN dokter ON dokter.kd_dokter = dpjp_ranap.kd_dokter where dpjp_ranap.no_rawat = ?", TDokter1,no_rawat_ibu );
        }
        
        if(Sequel.cariIsi("select pasien.keluarga from pasien where pasien.no_rkm_medis=?",norm).equals("AYAH")){
            Sequel.cariIsi("select pasien.namakeluarga from pasien where pasien.no_rkm_medis=?",NmAyah,norm);
        }
//        autoSKL();
    }
    
    private void isForm(){
        if(ChkInput.isSelected()==true){
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH,210));
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
        BtnSimpan.setEnabled(akses.getkelahiran_bayi());
        BtnHapus.setEnabled(akses.getkelahiran_bayi());
        BtnEdit.setEnabled(akses.getkelahiran_bayi());   
    }

    private void isMenu(){

    }
    
    private void isMenu2() {

    }
    
        public static String URLEDITIMAGES(){
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var = prop.getProperty("URLEDITFOTOBAYI");
        } catch (Exception e) {
            var = "";
        }
        return var;
    }
}
