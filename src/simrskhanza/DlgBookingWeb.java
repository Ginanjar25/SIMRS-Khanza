/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * DlgPemberianObat.java
 *
 * Created on 27 Mei 10, 14:52:31
 */
package simrskhanza;

import AESsecurity.EnkripsiAES;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.BackgroundMusic;
import kepegawaian.DlgDokter;
import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;

/**
 *
 * @author perpustakaan
 */
public class DlgBookingWeb extends javax.swing.JDialog {

    private final DefaultTableModel TabModePasien, tabModeDiagnosa, tabModeHint;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private DlgCariPasien pasien = new DlgCariPasien(null, false);
    private PreparedStatement ps, ps2, ps3;
    private ResultSet rs;
    private String pilihtampil = "";
    private int jml = 0, i = 0, z = 0, index = 0;
    private String[] kode, nama;
    private boolean[] pilih;
    private String alarm = "", nol_detik, detik, pengurutan = "", tahun = "", bulan = "", posisitahun = "", awalantahun = "", awalanbulan = "";
    private String kelurahan = "", kecamatan = "", kabupaten = "", propinsi = "", kdkel = "", kdkec = "", kdkab = "", kdprop = "",
            aktifjadwal = "", URUTNOREG = "";
    private int nilai_detik, bookingbaru = 0, p_kelurahan = 0, p_kecamatan = 0, p_kabupaten = 0, p_propinsi = 0, kuota = 0;
    private boolean sukses = true;
    private BackgroundMusic music;
    private boolean aktif = false;
    private String[] arrSplit;
    private String status = "", no_rawat = "", umur = "", sttsumur = "", nohp = "";

    /**
     * Creates new form DlgPemberianObat
     *
     * @param parent
     * @param modal
     */
    public DlgBookingWeb(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        Object[] row = {"P", "No. Booking", "Tgl Periksa", "Nama", "No. KTP", "Telp", "Alamat", "Tempat Lahir", "Tgl. Lahir",
            "Ibu Kandung", "Hint No.RM", "Pernah Ke RSPW", "JK", "Kd Poli", "Poli", "Kd Dok", "Dokter", "Kd Pj", "Penjab", "E-Mail", "Status", "Tgl Book", "Umur", "Nama Penjab", "Hubungan Penjab", "Agama", "pendidiakn", "Kawin"};

        TabModePasien = new DefaultTableModel(null, row) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                boolean a = false;
                if (colIndex == 0) {
                    a = true;
                }
                return a;
            }
            Class[] types = new Class[]{
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class,
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class,
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class,
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class,
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class,
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        tbPasien.setModel(TabModePasien);
        tampil();

        tbPasien.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbPasien.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 28; i++) {
            TableColumn column = tbPasien.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(20);
            } else if (i == 1) {
                column.setPreferredWidth(90);
            } else if (i == 2) {
                column.setPreferredWidth(90);
            } else if (i == 3) {
                column.setPreferredWidth(90);
            } else if (i == 4) {
                column.setPreferredWidth(90);
            } else if (i == 5) {
                column.setPreferredWidth(90);
            } else if (i == 6) {
                column.setPreferredWidth(90);
            } else if (i == 7) {
                column.setPreferredWidth(90);
            } else if (i == 8) {
                column.setPreferredWidth(90);
            } else if (i == 9) {
                column.setPreferredWidth(90);
            } else if (i == 10) {
                column.setPreferredWidth(90);
            } else if (i == 11) {
                column.setPreferredWidth(90);
            } else if (i == 12) {
                column.setPreferredWidth(30);
            } else {
                column.setPreferredWidth(90);
            }
        }
        tbPasien.setDefaultRenderer(Object.class, new WarnaTable());

        Object[] row2 = {"P", "Kode Dokter", "Nama Dokter"};
        tabModeDiagnosa = new DefaultTableModel(null, row2) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                boolean a = false;
                if (colIndex == 0) {
                    a = true;
                }
                return a;
            }
            Class[] types = new Class[]{
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
//        tbDiagnosa.setModel(tabModeDiagnosa);
//        //tbDokter.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbDokter.getBackground()));
//        tbDiagnosa.setPreferredScrollableViewportSize(new Dimension(500, 500));
//        tbDiagnosa.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//        for (i = 0; i < 3; i++) {
//            TableColumn column = tbDiagnosa.getColumnModel().getColumn(i);
//            if (i == 0) {
//                column.setPreferredWidth(20);
//            } else if (i == 1) {
//                column.setPreferredWidth(80);
//            } else if (i == 2) {
//                column.setPreferredWidth(300);
//            }
//        }
//        tbDiagnosa.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeHint = new DefaultTableModel(null, new Object[]{
            "P", "No.R.M", "Nama Pasien", "No.SIM/KTP", "J.K.", "Tmp.Lahir", "Tgl.Lahir", "Nama Ibu", "Alamat"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                boolean a = false;
                if (colIndex == 0) {
                    a = true;
                }
                return a;
            }
            Class[] types = new Class[]{
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class,
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class,
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        tbPasien1.setModel(tabModeHint);

        //tbPetugas.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbPetugas.getBackground()));
        tbPasien1.setPreferredScrollableViewportSize(new Dimension(800, 800));
        tbPasien1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (z = 0; z < 9; z++) {
            TableColumn column = tbPasien1.getColumnModel().getColumn(z);
            if (z == 0) {
                column.setPreferredWidth(20);
            } else if (z == 1) {
                column.setPreferredWidth(85);
            } else if (z == 2) {
                column.setPreferredWidth(190);
            } else if (z == 3) {
                column.setPreferredWidth(100);
            } else if (z == 4) {
                column.setPreferredWidth(25);
            } else if (z == 5) {
                column.setPreferredWidth(100);
            } else if (z == 6) {
                column.setPreferredWidth(70);
            } else if (z == 7) {
                column.setPreferredWidth(150);
            } else if (z == 8) {
                column.setPreferredWidth(190);
            }
        }
        tbPasien1.setDefaultRenderer(Object.class, new WarnaTable());

        this.setLocation(8, 1);
        setSize(885, 674);

        TNoBooking.setDocument(new batasInput((byte) 17).getKata(TNoBooking));
        TCari.setDocument(new batasInput((byte) 100).getKata(TCari));
        //TCariPasien.setDocument(new batasInput((byte) 20).getKata(TCariPasien));
        if (koneksiDB.CARICEPAT().equals("aktif")) {
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampil();
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampil();
                    }
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampil();
                    }
                }
            });
        }
//        if (koneksiDB.CARICEPAT().equals("aktif")) {
//            Dokter.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
//                @Override
//                public void insertUpdate(DocumentEvent e) {
//                    if (Dokter.getText().length() > 2) {
//                        tampildiagnosa();
//                    }
//                }
//
//                @Override
//                public void removeUpdate(DocumentEvent e) {
//                    if (Dokter.getText().length() > 2) {
//                        tampildiagnosa();
//                    }
//                }
//
//                @Override
//                public void changedUpdate(DocumentEvent e) {
//                    if (Dokter.getText().length() > 2) {
//                        tampildiagnosa();
//                    }
//                }
//            });
//        }
        ChkInput.setSelected(false);
        isForm();

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
                    //TCariPasien.setText(pasien.getTable().getValueAt(pasien.getTable().getSelectedRow(), 0).toString());
                }
                //TCariPasien.requestFocus();
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
            aktifjadwal = koneksiDB.JADWALDOKTERDIREGISTRASI();
            URUTNOREG = koneksiDB.URUTNOREG();
        } catch (Exception ex) {
            aktifjadwal = "";
            URUTNOREG = "";
        }

        try {
            alarm = koneksiDB.ALARMBOOKINGPERIKSA();
        } catch (Exception e) {
            alarm = "no";
        }

        try {
            ps = koneksi.prepareStatement("select * from set_alamat_pasien");
            try {
                rs = ps.executeQuery();
                while (rs.next()) {
                    TRKel.setEditable(rs.getBoolean("kelurahan"));
                    TRKec.setEditable(rs.getBoolean("kecamatan"));
                    TRKab.setEditable(rs.getBoolean("kabupaten"));
                    TRProv.setEditable(rs.getBoolean("propinsi"));
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

            ps = koneksi.prepareStatement("select * from set_kelengkapan_data_pasien");
            try {
                rs = ps.executeQuery();
                while (rs.next()) {
                    kelurahan = rs.getString("kelurahan");
                    p_kelurahan = rs.getInt("p_kelurahan");
                    kecamatan = rs.getString("kecamatan");
                    p_kecamatan = rs.getInt("p_kecamatan");
                    kabupaten = rs.getString("kabupaten");
                    p_kabupaten = rs.getInt("p_kabupaten");
                    propinsi = rs.getString("propinsi");
                    p_propinsi = rs.getInt("p_propinsi");
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

            ps = koneksi.prepareStatement("select * from set_urut_no_rkm_medis");
            try {
                rs = ps.executeQuery();
                while (rs.next()) {
                    pengurutan = rs.getString("urutan");
                    tahun = rs.getString("tahun");
                    bulan = rs.getString("bulan");
                    posisitahun = rs.getString("posisi_tahun_bulan");
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

        if (alarm.equals("yes")) {
            jam();
        }

        autoNomor();
    }

    //private DlgCariObatDokter dlgobtpny=new DlgCariObatDokter(null,false);
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        NoRm = new widget.TextBox();
        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbPasien = new widget.Table();
        Scroll2 = new widget.ScrollPane();
        tbPasien1 = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnPrint = new widget.Button();
        BtnAll = new widget.Button();
        jLabel10 = new widget.Label();
        LCount = new widget.Label();
        BtnKeluar = new widget.Button();
        panelGlass9 = new widget.panelisi();
        R1 = new widget.RadioButton();
        R2 = new widget.RadioButton();
        DTPCari1 = new widget.Tanggal();
        jLabel19 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        R3 = new widget.RadioButton();
        DTPCari3 = new widget.Tanggal();
        jLabel25 = new widget.Label();
        DTPCari4 = new widget.Tanggal();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        jSeparator5 = new javax.swing.JSeparator();
        BtnNewRM = new widget.Button();
        BtnUpdateRM = new widget.Button();
        PanelInput = new javax.swing.JPanel();
        FormInput = new widget.PanelBiasa();
        jLabel3 = new widget.Label();
        TNoBooking = new widget.TextBox();
        TNoKtp = new widget.TextBox();
        TPasien = new widget.TextBox();
        jLabel13 = new widget.Label();
        TAlamat = new widget.TextBox();
        jLabel17 = new widget.Label();
        Scroll1 = new widget.ScrollPane();
        tbDiagnosa = new widget.Table();
        Dokter = new widget.TextBox();
        BtnCari1 = new widget.Button();
        btnTarif = new widget.Button();
        jLabel4 = new widget.Label();
        jLabel5 = new widget.Label();
        jLabel18 = new widget.Label();
        TNoTelp = new widget.TextBox();
        jLabel20 = new widget.Label();
        TTempatLahir = new widget.TextBox();
        jLabel21 = new widget.Label();
        TTl = new widget.TextBox();
        jLabel22 = new widget.Label();
        TIbuKandung = new widget.TextBox();
        jLabel23 = new widget.Label();
        THintRm = new widget.TextBox();
        jLabel24 = new widget.Label();
        TPernahKeRspw = new widget.TextBox();
        TCariPasien = new widget.TextBox();
        BtnSeek4 = new widget.Button();
        jLabel16 = new widget.Label();
        TRKec = new widget.TextBox();
        TRNoRm = new widget.TextBox();
        TRProv = new widget.TextBox();
        TRKab = new widget.TextBox();
        TRKel = new widget.TextBox();
        TRTglPoli = new widget.TextBox();
        TRKdPoli = new widget.TextBox();
        TRNmPoli = new widget.TextBox();
        jLabel41 = new widget.Label();
        jLabel42 = new widget.Label();
        jLabel43 = new widget.Label();
        jLabel44 = new widget.Label();
        jLabel45 = new widget.Label();
        jLabel46 = new widget.Label();
        jLabel47 = new widget.Label();
        jLabel48 = new widget.Label();
        TRKdDok = new widget.TextBox();
        TRNmDok = new widget.TextBox();
        jLabel49 = new widget.Label();
        TRKdPj = new widget.TextBox();
        TRNmPj = new widget.TextBox();
        jLabel50 = new widget.Label();
        TRKuota = new widget.TextBox();
        TRSisaKuota = new widget.TextBox();
        jLabel51 = new widget.Label();
        jLabel52 = new widget.Label();
        TRNoReg = new widget.TextBox();
        jLabel57 = new widget.Label();
        StatusBalas1 = new widget.ComboBox();
        ChkRM1 = new widget.CekBox();
        TRJamBook = new widget.TextBox();
        TRTglBook = new widget.TextBox();
        jLabel58 = new widget.Label();
        jLabel59 = new widget.Label();
        TRJk = new widget.TextBox();
        TUmur = new widget.TextBox();
        TRHbSaudara = new widget.TextBox();
        TRNmPenjab = new widget.TextBox();
        jLabel60 = new widget.Label();
        jLabel61 = new widget.Label();
        ChkInput = new widget.CekBox();

        NoRm.setHighlighter(null);
        NoRm.setName("NoRm"); // NOI18N
        NoRm.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoRmKeyPressed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[Booking Registrasi Website Pasien Baru ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);
        Scroll.setPreferredSize(new java.awt.Dimension(900, 402));

        tbPasien.setAutoCreateRowSorter(true);
        tbPasien.setName("tbPasien"); // NOI18N
        tbPasien.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbPasienMouseClicked(evt);
            }
        });
        tbPasien.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbPasienKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbPasien);

        internalFrame1.add(Scroll, java.awt.BorderLayout.WEST);

        Scroll2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "::[Hint Data Pasien Berdasar Data Booking]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11))); // NOI18N
        Scroll2.setName("Scroll2"); // NOI18N
        Scroll2.setOpaque(true);

        tbPasien1.setAutoCreateRowSorter(true);
        tbPasien1.setName("tbPasien1"); // NOI18N
        tbPasien1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbPasien1MouseClicked(evt);
            }
        });
        tbPasien1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbPasien1KeyPressed(evt);
            }
        });
        Scroll2.setViewportView(tbPasien1);

        internalFrame1.add(Scroll2, java.awt.BorderLayout.CENTER);

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

        jLabel10.setText("Record :");
        jLabel10.setName("jLabel10"); // NOI18N
        jLabel10.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass8.add(jLabel10);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(110, 23));
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
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 10));

        R1.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.pink));
        buttonGroup1.add(R1);
        R1.setSelected(true);
        R1.setText("Belum Dibalas");
        R1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        R1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        R1.setName("R1"); // NOI18N
        R1.setPreferredSize(new java.awt.Dimension(100, 23));
        R1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                R1ActionPerformed(evt);
            }
        });
        panelGlass9.add(R1);

        R2.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.pink));
        buttonGroup1.add(R2);
        R2.setText("Booking :");
        R2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        R2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        R2.setName("R2"); // NOI18N
        R2.setPreferredSize(new java.awt.Dimension(75, 23));
        R2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                R2ActionPerformed(evt);
            }
        });
        panelGlass9.add(R2);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "12-04-2024" }));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1"); // NOI18N
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(100, 23));
        panelGlass9.add(DTPCari1);

        jLabel19.setText("s.d");
        jLabel19.setName("jLabel19"); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(18, 23));
        panelGlass9.add(jLabel19);

        DTPCari2.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "12-04-2024" }));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2"); // NOI18N
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(100, 23));
        panelGlass9.add(DTPCari2);

        R3.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.pink));
        buttonGroup1.add(R3);
        R3.setText("Periksa :");
        R3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        R3.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        R3.setName("R3"); // NOI18N
        R3.setPreferredSize(new java.awt.Dimension(75, 23));
        R3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                R3ActionPerformed(evt);
            }
        });
        panelGlass9.add(R3);

        DTPCari3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "12-04-2024" }));
        DTPCari3.setDisplayFormat("dd-MM-yyyy");
        DTPCari3.setName("DTPCari3"); // NOI18N
        DTPCari3.setOpaque(false);
        DTPCari3.setPreferredSize(new java.awt.Dimension(90, 23));
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
        panelGlass9.add(DTPCari3);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setText("s.d");
        jLabel25.setName("jLabel25"); // NOI18N
        jLabel25.setPreferredSize(new java.awt.Dimension(25, 23));
        panelGlass9.add(jLabel25);

        DTPCari4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "12-04-2024" }));
        DTPCari4.setDisplayFormat("dd-MM-yyyy");
        DTPCari4.setName("DTPCari4"); // NOI18N
        DTPCari4.setOpaque(false);
        DTPCari4.setPreferredSize(new java.awt.Dimension(90, 23));
        DTPCari4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                DTPCari4ItemStateChanged(evt);
            }
        });
        DTPCari4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DTPCari4KeyPressed(evt);
            }
        });
        panelGlass9.add(DTPCari4);

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(65, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(190, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass9.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('6');
        BtnCari.setToolTipText("Alt+6");
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

        jSeparator5.setBackground(new java.awt.Color(220, 225, 215));
        jSeparator5.setForeground(new java.awt.Color(220, 225, 215));
        jSeparator5.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator5.setName("jSeparator5"); // NOI18N
        jSeparator5.setOpaque(true);
        jSeparator5.setPreferredSize(new java.awt.Dimension(1, 23));
        panelGlass9.add(jSeparator5);

        BtnNewRM.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/add-file-16x16.png"))); // NOI18N
        BtnNewRM.setMnemonic('K');
        BtnNewRM.setText("Buat RM & Register");
        BtnNewRM.setToolTipText("Alt+K");
        BtnNewRM.setMinimumSize(new java.awt.Dimension(200, 22));
        BtnNewRM.setName("BtnNewRM"); // NOI18N
        BtnNewRM.setPreferredSize(new java.awt.Dimension(170, 30));
        BtnNewRM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnNewRMActionPerformed(evt);
            }
        });
        BtnNewRM.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnNewRMKeyPressed(evt);
            }
        });
        panelGlass9.add(BtnNewRM);

        BtnUpdateRM.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept_page.png"))); // NOI18N
        BtnUpdateRM.setMnemonic('K');
        BtnUpdateRM.setText("Update RM & Register");
        BtnUpdateRM.setToolTipText("Alt+K");
        BtnUpdateRM.setMinimumSize(new java.awt.Dimension(200, 22));
        BtnUpdateRM.setName("BtnUpdateRM"); // NOI18N
        BtnUpdateRM.setPreferredSize(new java.awt.Dimension(170, 30));
        BtnUpdateRM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnUpdateRMActionPerformed(evt);
            }
        });
        BtnUpdateRM.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnUpdateRMKeyPressed(evt);
            }
        });
        panelGlass9.add(BtnUpdateRM);

        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setOpaque(false);
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(865, 200));
        FormInput.setLayout(null);

        jLabel3.setText("No.Booking :");
        jLabel3.setName("jLabel3"); // NOI18N
        FormInput.add(jLabel3);
        jLabel3.setBounds(-2, 12, 80, 23);

        TNoBooking.setHighlighter(null);
        TNoBooking.setName("TNoBooking"); // NOI18N
        TNoBooking.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoBookingKeyPressed(evt);
            }
        });
        FormInput.add(TNoBooking);
        TNoBooking.setBounds(80, 10, 190, 23);

        TNoKtp.setEditable(false);
        TNoKtp.setHighlighter(null);
        TNoKtp.setName("TNoKtp"); // NOI18N
        FormInput.add(TNoKtp);
        TNoKtp.setBounds(81, 42, 190, 23);

        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N
        TPasien.setPreferredSize(new java.awt.Dimension(25, 28));
        FormInput.add(TPasien);
        TPasien.setBounds(81, 72, 250, 23);

        jLabel13.setText("Dokter :");
        jLabel13.setName("jLabel13"); // NOI18N
        FormInput.add(jLabel13);
        jLabel13.setBounds(1090, 10, 70, 23);

        TAlamat.setHighlighter(null);
        TAlamat.setName("TAlamat"); // NOI18N
        FormInput.add(TAlamat);
        TAlamat.setBounds(80, 100, 250, 23);

        jLabel17.setText("Kab :");
        jLabel17.setName("jLabel17"); // NOI18N
        FormInput.add(jLabel17);
        jLabel17.setBounds(20, 160, 50, 23);

        Scroll1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll1.setName("Scroll1"); // NOI18N

        tbDiagnosa.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbDiagnosa.setName("tbDiagnosa"); // NOI18N
        tbDiagnosa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbDiagnosaKeyPressed(evt);
            }
        });
        Scroll1.setViewportView(tbDiagnosa);

        FormInput.add(Scroll1);
        Scroll1.setBounds(1120, 40, 380, 80);

        Dokter.setHighlighter(null);
        Dokter.setName("Dokter"); // NOI18N
        Dokter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DokterKeyPressed(evt);
            }
        });
        FormInput.add(Dokter);
        Dokter.setBounds(1170, 10, 274, 23);

        BtnCari1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari1.setMnemonic('1');
        BtnCari1.setToolTipText("Alt+1");
        BtnCari1.setName("BtnCari1"); // NOI18N
        BtnCari1.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCari1ActionPerformed(evt);
            }
        });
        FormInput.add(BtnCari1);
        BtnCari1.setBounds(1440, 10, 28, 23);

        btnTarif.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/plus_16.png"))); // NOI18N
        btnTarif.setMnemonic('2');
        btnTarif.setToolTipText("Alt+2");
        btnTarif.setName("btnTarif"); // NOI18N
        btnTarif.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTarifActionPerformed(evt);
            }
        });
        FormInput.add(btnTarif);
        btnTarif.setBounds(1470, 10, 28, 23);

        jLabel4.setText("No. KTP :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(-2, 42, 80, 23);

        jLabel5.setText("Nama Pasien :");
        jLabel5.setName("jLabel5"); // NOI18N
        FormInput.add(jLabel5);
        jLabel5.setBounds(-2, 72, 80, 23);

        jLabel18.setText("Jam. Book :");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(330, 100, 60, 23);

        TNoTelp.setHighlighter(null);
        TNoTelp.setName("TNoTelp"); // NOI18N
        FormInput.add(TNoTelp);
        TNoTelp.setBounds(370, 10, 130, 23);

        jLabel20.setText("Tempat Lahir :");
        jLabel20.setName("jLabel20"); // NOI18N
        FormInput.add(jLabel20);
        jLabel20.setBounds(280, 40, 80, 23);

        TTempatLahir.setHighlighter(null);
        TTempatLahir.setName("TTempatLahir"); // NOI18N
        FormInput.add(TTempatLahir);
        TTempatLahir.setBounds(370, 40, 130, 23);

        jLabel21.setText("Tgl Lahir :");
        jLabel21.setName("jLabel21"); // NOI18N
        FormInput.add(jLabel21);
        jLabel21.setBounds(510, 40, 60, 23);

        TTl.setHighlighter(null);
        TTl.setName("TTl"); // NOI18N
        FormInput.add(TTl);
        TTl.setBounds(580, 40, 90, 23);

        jLabel22.setText("Ibu Kandung :");
        jLabel22.setName("jLabel22"); // NOI18N
        FormInput.add(jLabel22);
        jLabel22.setBounds(510, 10, 80, 23);

        TIbuKandung.setHighlighter(null);
        TIbuKandung.setName("TIbuKandung"); // NOI18N
        FormInput.add(TIbuKandung);
        TIbuKandung.setBounds(600, 10, 170, 23);

        jLabel23.setText("Hint No. RM :");
        jLabel23.setName("jLabel23"); // NOI18N
        FormInput.add(jLabel23);
        jLabel23.setBounds(520, 70, 80, 23);

        THintRm.setHighlighter(null);
        THintRm.setName("THintRm"); // NOI18N
        FormInput.add(THintRm);
        THintRm.setBounds(610, 70, 160, 23);

        jLabel24.setText("Pernah Ke RSPW :");
        jLabel24.setName("jLabel24"); // NOI18N
        FormInput.add(jLabel24);
        jLabel24.setBounds(500, 100, 100, 23);

        TPernahKeRspw.setHighlighter(null);
        TPernahKeRspw.setName("TPernahKeRspw"); // NOI18N
        FormInput.add(TPernahKeRspw);
        TPernahKeRspw.setBounds(610, 100, 60, 23);

        TCariPasien.setName("TCariPasien"); // NOI18N
        TCariPasien.setPreferredSize(new java.awt.Dimension(130, 23));
        TCariPasien.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariPasienKeyPressed(evt);
            }
        });
        FormInput.add(TCariPasien);
        TCariPasien.setBounds(1180, 130, 130, 23);

        BtnSeek4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnSeek4.setMnemonic('5');
        BtnSeek4.setToolTipText("Alt+5");
        BtnSeek4.setName("BtnSeek4"); // NOI18N
        BtnSeek4.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnSeek4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSeek4ActionPerformed(evt);
            }
        });
        BtnSeek4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnSeek4KeyPressed(evt);
            }
        });
        FormInput.add(BtnSeek4);
        BtnSeek4.setBounds(1320, 130, 28, 23);

        jLabel16.setText("Dokter :");
        jLabel16.setName("jLabel16"); // NOI18N
        jLabel16.setPreferredSize(new java.awt.Dimension(50, 23));
        FormInput.add(jLabel16);
        jLabel16.setBounds(610, 130, 60, 23);

        TRKec.setHighlighter(null);
        TRKec.setName("TRKec"); // NOI18N
        FormInput.add(TRKec);
        TRKec.setBounds(260, 130, 120, 23);

        TRNoRm.setHighlighter(null);
        TRNoRm.setName("TRNoRm"); // NOI18N
        TRNoRm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TRNoRmActionPerformed(evt);
            }
        });
        FormInput.add(TRNoRm);
        TRNoRm.setBounds(860, 10, 110, 24);

        TRProv.setHighlighter(null);
        TRProv.setName("TRProv"); // NOI18N
        FormInput.add(TRProv);
        TRProv.setBounds(260, 160, 120, 23);

        TRKab.setHighlighter(null);
        TRKab.setName("TRKab"); // NOI18N
        FormInput.add(TRKab);
        TRKab.setBounds(80, 160, 130, 23);

        TRKel.setHighlighter(null);
        TRKel.setName("TRKel"); // NOI18N
        FormInput.add(TRKel);
        TRKel.setBounds(80, 130, 130, 23);

        TRTglPoli.setHighlighter(null);
        TRTglPoli.setName("TRTglPoli"); // NOI18N
        FormInput.add(TRTglPoli);
        TRTglPoli.setBounds(460, 130, 110, 23);

        TRKdPoli.setHighlighter(null);
        TRKdPoli.setName("TRKdPoli"); // NOI18N
        FormInput.add(TRKdPoli);
        TRKdPoli.setBounds(460, 160, 70, 23);

        TRNmPoli.setHighlighter(null);
        TRNmPoli.setName("TRNmPoli"); // NOI18N
        FormInput.add(TRNmPoli);
        TRNmPoli.setBounds(540, 160, 130, 23);

        jLabel41.setText("Alamat :");
        jLabel41.setName("jLabel41"); // NOI18N
        FormInput.add(jLabel41);
        jLabel41.setBounds(-2, 102, 80, 23);

        jLabel42.setText("Kel :");
        jLabel42.setName("jLabel42"); // NOI18N
        FormInput.add(jLabel42);
        jLabel42.setBounds(20, 130, 50, 23);

        jLabel43.setText("Kec :");
        jLabel43.setName("jLabel43"); // NOI18N
        FormInput.add(jLabel43);
        jLabel43.setBounds(210, 130, 40, 23);

        jLabel44.setText("Poli Tujuan :");
        jLabel44.setName("jLabel44"); // NOI18N
        FormInput.add(jLabel44);
        jLabel44.setBounds(390, 160, 60, 23);

        jLabel45.setText("Prov :");
        jLabel45.setName("jLabel45"); // NOI18N
        FormInput.add(jLabel45);
        jLabel45.setBounds(210, 160, 40, 23);

        jLabel46.setText("Tgl. periksa :");
        jLabel46.setName("jLabel46"); // NOI18N
        FormInput.add(jLabel46);
        jLabel46.setBounds(380, 130, 70, 23);

        jLabel47.setText("No.RM :");
        jLabel47.setName("jLabel47"); // NOI18N
        jLabel47.setPreferredSize(new java.awt.Dimension(50, 23));
        FormInput.add(jLabel47);
        jLabel47.setBounds(1120, 130, 50, 23);

        jLabel48.setText("Hub. PJ:");
        jLabel48.setName("jLabel48"); // NOI18N
        jLabel48.setPreferredSize(new java.awt.Dimension(50, 23));
        FormInput.add(jLabel48);
        jLabel48.setBounds(770, 70, 50, 20);

        TRKdDok.setHighlighter(null);
        TRKdDok.setName("TRKdDok"); // NOI18N
        FormInput.add(TRKdDok);
        TRKdDok.setBounds(680, 130, 70, 23);

        TRNmDok.setHighlighter(null);
        TRNmDok.setName("TRNmDok"); // NOI18N
        FormInput.add(TRNmDok);
        TRNmDok.setBounds(760, 130, 200, 23);

        jLabel49.setText("Cara Bayar :");
        jLabel49.setName("jLabel49"); // NOI18N
        jLabel49.setPreferredSize(new java.awt.Dimension(50, 23));
        FormInput.add(jLabel49);
        jLabel49.setBounds(670, 160, 70, 23);

        TRKdPj.setHighlighter(null);
        TRKdPj.setName("TRKdPj"); // NOI18N
        FormInput.add(TRKdPj);
        TRKdPj.setBounds(750, 160, 60, 23);

        TRNmPj.setHighlighter(null);
        TRNmPj.setName("TRNmPj"); // NOI18N
        FormInput.add(TRNmPj);
        TRNmPj.setBounds(820, 160, 140, 23);

        jLabel50.setText("Kuota :");
        jLabel50.setName("jLabel50"); // NOI18N
        jLabel50.setPreferredSize(new java.awt.Dimension(50, 23));
        FormInput.add(jLabel50);
        jLabel50.setBounds(970, 130, 60, 23);

        TRKuota.setHighlighter(null);
        TRKuota.setName("TRKuota"); // NOI18N
        FormInput.add(TRKuota);
        TRKuota.setBounds(1040, 130, 60, 23);

        TRSisaKuota.setHighlighter(null);
        TRSisaKuota.setName("TRSisaKuota"); // NOI18N
        FormInput.add(TRSisaKuota);
        TRSisaKuota.setBounds(1040, 160, 60, 23);

        jLabel51.setText("Sisa Kuota :");
        jLabel51.setName("jLabel51"); // NOI18N
        jLabel51.setPreferredSize(new java.awt.Dimension(50, 23));
        FormInput.add(jLabel51);
        jLabel51.setBounds(970, 160, 60, 23);

        jLabel52.setText("No. Reg :");
        jLabel52.setName("jLabel52"); // NOI18N
        jLabel52.setPreferredSize(new java.awt.Dimension(50, 23));
        FormInput.add(jLabel52);
        jLabel52.setBounds(670, 100, 60, 23);

        TRNoReg.setHighlighter(null);
        TRNoReg.setName("TRNoReg"); // NOI18N
        FormInput.add(TRNoReg);
        TRNoReg.setBounds(740, 100, 60, 23);

        jLabel57.setText("Status :");
        jLabel57.setName("jLabel57"); // NOI18N
        FormInput.add(jLabel57);
        jLabel57.setBounds(810, 100, 40, 23);

        StatusBalas1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Diterima", "Ditolak" }));
        StatusBalas1.setName("StatusBalas1"); // NOI18N
        StatusBalas1.setPreferredSize(new java.awt.Dimension(150, 23));
        StatusBalas1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                StatusBalas1KeyPressed(evt);
            }
        });
        FormInput.add(StatusBalas1);
        StatusBalas1.setBounds(850, 100, 95, 23);

        ChkRM1.setBorder(null);
        ChkRM1.setSelected(true);
        ChkRM1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkRM1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkRM1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkRM1.setName("ChkRM1"); // NOI18N
        ChkRM1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ChkRM1ItemStateChanged(evt);
            }
        });
        FormInput.add(ChkRM1);
        ChkRM1.setBounds(980, 10, 23, 23);

        TRJamBook.setHighlighter(null);
        TRJamBook.setName("TRJamBook"); // NOI18N
        TRJamBook.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TRJamBookKeyPressed(evt);
            }
        });
        FormInput.add(TRJamBook);
        TRJamBook.setBounds(400, 100, 100, 23);

        TRTglBook.setHighlighter(null);
        TRTglBook.setName("TRTglBook"); // NOI18N
        TRTglBook.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TRTglBookKeyPressed(evt);
            }
        });
        FormInput.add(TRTglBook);
        TRTglBook.setBounds(400, 70, 100, 23);

        jLabel58.setText("No. Telp :");
        jLabel58.setName("jLabel58"); // NOI18N
        FormInput.add(jLabel58);
        jLabel58.setBounds(280, 10, 80, 23);

        jLabel59.setText("Tgl. Book :");
        jLabel59.setName("jLabel59"); // NOI18N
        FormInput.add(jLabel59);
        jLabel59.setBounds(330, 70, 60, 23);

        TRJk.setHighlighter(null);
        TRJk.setName("TRJk"); // NOI18N
        FormInput.add(TRJk);
        TRJk.setBounds(580, 130, 40, 23);

        TUmur.setHighlighter(null);
        TUmur.setName("TUmur"); // NOI18N
        FormInput.add(TUmur);
        TUmur.setBounds(680, 40, 90, 23);

        TRHbSaudara.setHighlighter(null);
        TRHbSaudara.setName("TRHbSaudara"); // NOI18N
        TRHbSaudara.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TRHbSaudaraActionPerformed(evt);
            }
        });
        FormInput.add(TRHbSaudara);
        TRHbSaudara.setBounds(830, 70, 140, 24);

        TRNmPenjab.setHighlighter(null);
        TRNmPenjab.setName("TRNmPenjab"); // NOI18N
        TRNmPenjab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TRNmPenjabActionPerformed(evt);
            }
        });
        FormInput.add(TRNmPenjab);
        TRNmPenjab.setBounds(830, 40, 140, 24);

        jLabel60.setText("No.RM BARU :");
        jLabel60.setName("jLabel60"); // NOI18N
        jLabel60.setPreferredSize(new java.awt.Dimension(50, 23));
        FormInput.add(jLabel60);
        jLabel60.setBounds(780, 10, 70, 20);

        jLabel61.setText("Penjab :");
        jLabel61.setName("jLabel61"); // NOI18N
        jLabel61.setPreferredSize(new java.awt.Dimension(50, 23));
        FormInput.add(jLabel61);
        jLabel61.setBounds(770, 40, 50, 20);

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

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnCariActionPerformed(null);
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            BtnCari.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            BtnKeluar.requestFocus();
        }
}//GEN-LAST:event_TCariKeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        tampil();
}//GEN-LAST:event_BtnCariActionPerformed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnCariActionPerformed(null);
        } else {
            Valid.pindah(evt, TCari, BtnAll);
        }
}//GEN-LAST:event_BtnCariKeyPressed

    private void TNoBookingKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoBookingKeyPressed
        Valid.pindah(evt, TCari, Dokter);
}//GEN-LAST:event_TNoBookingKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        jml = 0;
        for (i = 0; i < tbDiagnosa.getRowCount(); i++) {
            if (tbDiagnosa.getValueAt(i, 0).toString().equals("true")) {
                jml++;
            }
        }
        if (TNoBooking.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoBooking, "No.Rawat");
        } else if (jml == 0) {
            Valid.textKosong(Dokter, "Data Dokter");
        } else {
            for (i = 0; i < tbDiagnosa.getRowCount(); i++) {
                if (tbDiagnosa.getValueAt(i, 0).toString().equals("true")) {
                    if (Sequel.menyimpantf("dpjp_ranap", "?,?", "Dokter", 2, new String[]{
                        TNoBooking.getText(), tbDiagnosa.getValueAt(i, 1).toString()
                    }) == true) {
                        TabModePasien.addRow(new Object[]{
                            false, TAlamat.getText(), TNoBooking.getText(), TNoKtp.getText(), TPasien.getText(), tbDiagnosa.getValueAt(i, 1).toString(), tbDiagnosa.getValueAt(i, 2).toString()
                        });
                        tbDiagnosa.setValueAt(false, i, 0);
                    }
                }
            }
            LCount.setText("" + TabModePasien.getRowCount());
        }
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnSimpanActionPerformed(null);
        } else {
            Valid.pindah(evt, Dokter, BtnBatal);
        }
}//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        Dokter.setText("");
        TNoBooking.requestFocus();
        for (i = 0; i < tbDiagnosa.getRowCount(); i++) {
            tbDiagnosa.setValueAt(false, i, 0);
        }
        ChkInput.setSelected(true);
        isForm();
}//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBatalKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnBatalActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnSimpan, BtnHapus);
        }
}//GEN-LAST:event_BtnBatalKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if (tbPasien.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis...!!!!");
            TNoBooking.requestFocus();
        } else if (TPasien.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Gagal menghapus. Pilih dulu data yang mau dihapus.\nKlik data pada table untuk memilih...!!!!");
        } else if (!(TPasien.getText().trim().equals(""))) {
            for (i = 0; i < tbPasien.getRowCount(); i++) {
                if (tbPasien.getValueAt(i, 0).toString().equals("true")) {
                    if (Sequel.queryu2tf("delete from dpjp_ranap where no_rawat=? and kd_dokter=?", 2, new String[]{
                        tbPasien.getValueAt(i, 2).toString(), tbPasien.getValueAt(i, 5).toString()
                    }) == true) {
                        TabModePasien.removeRow(i);
                        i--;
                    }
                }
            }
            LCount.setText("" + TabModePasien.getRowCount());
        }
}//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnHapusActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnBatal, BtnPrint);
        }
}//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (TabModePasien.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            BtnBatal.requestFocus();
        } else if (TabModePasien.getRowCount() != 0) {
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            Valid.MyReportqry("rptDpjp.jasper", "report", "::[ Data Diagnosa Pasien ]::",
                    "select reg_periksa.tgl_registrasi,dpjp_ranap.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,"
                    + "dpjp_ranap.kd_dokter,dokter.nm_dokter from dpjp_ranap inner join reg_periksa inner join pasien inner join dokter "
                    + "on dpjp_ranap.no_rawat=reg_periksa.no_rawat and reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                    + "and dpjp_ranap.kd_dokter=dokter.kd_dokter "
                    + "where reg_periksa.tgl_registrasi between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + "' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + "' and reg_periksa.no_rkm_medis like '%" + TCariPasien.getText() + "%' and reg_periksa.tgl_registrasi like '%" + TCari.getText().trim() + "%' or "
                    + "reg_periksa.tgl_registrasi between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + "' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + "' and reg_periksa.no_rkm_medis like '%" + TCariPasien.getText() + "%' and dpjp_ranap.no_rawat like '%" + TCari.getText().trim() + "%' or "
                    + "reg_periksa.tgl_registrasi between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + "' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + "' and reg_periksa.no_rkm_medis like '%" + TCariPasien.getText() + "%' and reg_periksa.no_rkm_medis like '%" + TCari.getText().trim() + "%' or "
                    + "reg_periksa.tgl_registrasi between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + "' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + "' and reg_periksa.no_rkm_medis like '%" + TCariPasien.getText() + "%' and pasien.nm_pasien like '%" + TCari.getText().trim() + "%' or "
                    + "reg_periksa.tgl_registrasi between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + "' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + "' and reg_periksa.no_rkm_medis like '%" + TCariPasien.getText() + "%' and dokter.kd_dokter like '%" + TCari.getText().trim() + "%' or "
                    + "reg_periksa.tgl_registrasi between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + "' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + "' and reg_periksa.no_rkm_medis like '%" + TCariPasien.getText() + "%' and dokter.nm_dokter like '%" + TCari.getText().trim() + "%' "
                    + "order by reg_periksa.tgl_registrasi", param);
        }
        this.setCursor(Cursor.getDefaultCursor());
}//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnPrintActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnHapus, BtnAll);
        }
}//GEN-LAST:event_BtnPrintKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
}//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            dispose();
        } else {
            Valid.pindah(evt, BtnPrint, TCari);
        }
}//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        TCariPasien.setText("");
        tampil();
}//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnAllActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnPrint, BtnKeluar);
        }
}//GEN-LAST:event_BtnAllKeyPressed

    private void tbPasienMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPasienMouseClicked
        if (TabModePasien.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }

        }
}//GEN-LAST:event_tbPasienMouseClicked

    private void tbPasienKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbPasienKeyPressed
        if (TabModePasien.getRowCount() != 0) {
            if ((evt.getKeyCode() == KeyEvent.VK_ENTER) || (evt.getKeyCode() == KeyEvent.VK_UP) || (evt.getKeyCode() == KeyEvent.VK_DOWN)) {
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
}//GEN-LAST:event_tbPasienKeyPressed

private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInputActionPerformed
    isForm();
}//GEN-LAST:event_ChkInputActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        tampildiagnosa();
    }//GEN-LAST:event_formWindowOpened

    private void tbPasien1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPasien1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tbPasien1MouseClicked

    private void tbPasien1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbPasien1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_tbPasien1KeyPressed

    private void R1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_R1ActionPerformed

    }//GEN-LAST:event_R1ActionPerformed

    private void R3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_R3ActionPerformed
//        PanelAccor.setVisible(true);
//        ChkAccor.setSelected(false);
//        isMenu();
    }//GEN-LAST:event_R3ActionPerformed

    private void DTPCari3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_DTPCari3ItemStateChanged
        R3.setSelected(true);
    }//GEN-LAST:event_DTPCari3ItemStateChanged

    private void DTPCari3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DTPCari3KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_DTPCari3KeyPressed

    private void DTPCari4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_DTPCari4ItemStateChanged
        R3.setSelected(true);
    }//GEN-LAST:event_DTPCari4ItemStateChanged

    private void DTPCari4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DTPCari4KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_DTPCari4KeyPressed

    private void R2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_R2ActionPerformed
        //PanelAccor.setVisible(true);
        //ChkAccor.setSelected(false);
        //isMenu();
    }//GEN-LAST:event_R2ActionPerformed

    private void BtnUpdateRMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnUpdateRMActionPerformed
        String rmlama = "";
        if (tbPasien1.getSelectedRow() != -1) {
            rmlama = tbPasien1.getValueAt(tbPasien1.getSelectedRow(), 1).toString();
            int choice = JOptionPane.showConfirmDialog(null, "Yakin, update dan regist No. RM : " + rmlama + " dari data booking ", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                if (Sequel.mengedittf("pasien", "no_rkm_medis=?", "nm_pasien=?,no_ktp=?",
                        3,
                        new String[]{
                            TPasien.getText(),
                            TNoKtp.getText(),
                            rmlama.trim()

                        }) == true) {
                    JOptionPane.showMessageDialog(rootPane,
                            "No. Booking :" + TNoBooking.getText() + "Update"
                            + "\nNo.RM : " + rmlama + tbPasien1.getValueAt(tbPasien1.getSelectedRow(), 1).toString()
                            + "\nNo. Urut : " + TRNoReg.getText() + " Poli : " + TRNmPoli.getText()
                            + "\nDokter : " + TRNmDok.getText() + " Tgl : " + TRTglPoli.getText()
                    );

                    isBooking2();
                    isRegister2();
                }
            } else {
                // User clicked "No" or closed the dialog
                tampil();
                System.out.println("User clicked No or closed the dialog.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Belum ada pasien yang di pilih !!!");
        }

    }//GEN-LAST:event_BtnUpdateRMActionPerformed

    private void BtnUpdateRMKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnUpdateRMKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnUpdateRMKeyPressed

    private void BtnNewRMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnNewRMActionPerformed
        if (R1.isSelected() == true) {
            if (tbPasien.getSelectedRow() > -1) {
                emptTeks();
                i = Sequel.cariInteger("select (TO_DAYS('" + tbPasien.getValueAt(tbPasien.getSelectedRow(), 2).toString() + "')-TO_DAYS(current_date()))");
                if (i >= 0) {
                    //this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//                    if (aktifjadwal.equals("aktif")) {
//                        if (akses.getkode().equals("Admin Utama")) {
//                            LabelStatus.setText("Pengaturan kuota dan jadwal tidak diaktifkan, silahkan cek kuota dan jadwal secara manual...!!");
//                        } else {
//                            LabelStatus.setText("Pengaturan kuota dan jadwal diaktifkan...!!");
//                        }
//                    } else {
//                        LabelStatus.setText("Pengaturan kuota dan jadwal tidak diaktifkan, silahkan cek kuota dan jadwal secara manual...!!");
//                    }
                    int choice = JOptionPane.showConfirmDialog(null, "Yakin, daftar dan regis sebagai pasien baru?", "Confirmation", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        getData(); //untuk memindah dari row tabel ke fild
                        autoNomor(); // untuk mengambil no rm terbaru
                        isBooking();
                        isRegister();
                        JOptionPane.showMessageDialog(rootPane,
                                "No. Booking : " + TNoBooking.getText() + " " + TPasien.getText()
                                + "\nNo.RM Baru : " + TRNoRm.getText()
                                + "\nNo. Urut : " + TRNoReg.getText() + " Poli : " + TRNmPoli.getText()
                                + "\nDokter : " + TRNmDok.getText() + " Tgl : " + TRTglPoli.getText()
                        );
                        autoNomor();
                        getData();
                    } else {
                        tampil();
                        autoNomor();
                        getData();
                    }

                } else {
                    JOptionPane.showMessageDialog(rootPane, "Tanggal periksa sudah kadaluarsa..!!");
                    tbPasien.requestFocus();
                }
            } else {
                JOptionPane.showMessageDialog(rootPane, "Silahkan anda pilih data terlebih dahulu..!!");
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan validasi yang belum dibalas..!!");
        }
    }//GEN-LAST:event_BtnNewRMActionPerformed

    private void BtnNewRMKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnNewRMKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnNewRMKeyPressed

    private void NoRmKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoRmKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_NoRmKeyPressed

    private void TRNoRmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TRNoRmActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TRNoRmActionPerformed

    private void StatusBalas1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_StatusBalas1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_StatusBalas1KeyPressed

    private void ChkRM1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ChkRM1ItemStateChanged
//        if (ChkRM.isSelected() == true) {
//            TNo.setEditable(false);
//            TNo.setBackground(new Color(245, 250, 240));
//            autoNomor();
//        } else if (ChkRM.isSelected() == false) {
//            TNo.setEditable(true);
//            TNo.setBackground(new Color(250, 255, 245));
//        }
    }//GEN-LAST:event_ChkRM1ItemStateChanged

    private void TRJamBookKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TRJamBookKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TRJamBookKeyPressed

    private void TRTglBookKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TRTglBookKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TRTglBookKeyPressed

    private void TRHbSaudaraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TRHbSaudaraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TRHbSaudaraActionPerformed

    private void TRNmPenjabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TRNmPenjabActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TRNmPenjabActionPerformed

    private void BtnSeek4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSeek4KeyPressed
        Valid.pindah(evt, TCariPasien, DTPCari1);
    }//GEN-LAST:event_BtnSeek4KeyPressed

    private void BtnSeek4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSeek4ActionPerformed
        pasien.emptTeks();
        pasien.isCek();
        pasien.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        pasien.setLocationRelativeTo(internalFrame1);
        pasien.setVisible(true);
    }//GEN-LAST:event_BtnSeek4ActionPerformed

    private void TCariPasienKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariPasienKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampil();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            BtnSeek4.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            DTPCari2.requestFocus();
        }
    }//GEN-LAST:event_TCariPasienKeyPressed

    private void tbDiagnosaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbDiagnosaKeyPressed
        if (tbDiagnosa.getRowCount() != 0) {
            if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                try {
                    i = tbDiagnosa.getSelectedColumn();
                    if (i == 1) {
                        if (tbDiagnosa.getSelectedRow() > -1) {
                            tbDiagnosa.setValueAt(true, tbDiagnosa.getSelectedRow(), 0);
                        }
                        Dokter.setText("");
                        Dokter.requestFocus();
                    }
                } catch (java.lang.NullPointerException e) {
                }
            } else if (evt.getKeyCode() == KeyEvent.VK_SHIFT) {
                Dokter.setText("");
                Dokter.requestFocus();
            }
        }
    }//GEN-LAST:event_tbDiagnosaKeyPressed

    private void btnTarifActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTarifActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        DlgDokter tariflab = new DlgDokter(null, false);
        tariflab.emptTeks();
        tariflab.isCek();
        tariflab.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        tariflab.setLocationRelativeTo(internalFrame1);
        tariflab.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_btnTarifActionPerformed

    private void BtnCari1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCari1ActionPerformed
        if (!TNoBooking.getText().equals("")) {
            tampildiagnosa();
        }
    }//GEN-LAST:event_BtnCari1ActionPerformed

    private void DokterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DokterKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!TNoBooking.getText().equals("")) {
                tampildiagnosa();
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_UP) {
            if (akses.getpenyakit() == true) {
                btnTarifActionPerformed(null);
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_UP) {
            tbDiagnosa.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            BtnSimpan.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            TNoBooking.requestFocus();
        }
    }//GEN-LAST:event_DokterKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgBookingWeb dialog = new DlgBookingWeb(new javax.swing.JFrame(), true);
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
    private widget.Button BtnCari1;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnNewRM;
    private widget.Button BtnPrint;
    private widget.Button BtnSeek4;
    private widget.Button BtnSimpan;
    private widget.Button BtnUpdateRM;
    private widget.CekBox ChkInput;
    private widget.CekBox ChkRM1;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.Tanggal DTPCari3;
    private widget.Tanggal DTPCari4;
    private widget.TextBox Dokter;
    private widget.PanelBiasa FormInput;
    private widget.Label LCount;
    private widget.TextBox NoRm;
    private javax.swing.JPanel PanelInput;
    private widget.RadioButton R1;
    private widget.RadioButton R2;
    private widget.RadioButton R3;
    private widget.ScrollPane Scroll;
    private widget.ScrollPane Scroll1;
    private widget.ScrollPane Scroll2;
    private widget.ComboBox StatusBalas1;
    private widget.TextBox TAlamat;
    public widget.TextBox TCari;
    private widget.TextBox TCariPasien;
    private widget.TextBox THintRm;
    private widget.TextBox TIbuKandung;
    private widget.TextBox TNoBooking;
    private widget.TextBox TNoKtp;
    private widget.TextBox TNoTelp;
    private widget.TextBox TPasien;
    private widget.TextBox TPernahKeRspw;
    private widget.TextBox TRHbSaudara;
    private widget.TextBox TRJamBook;
    private widget.TextBox TRJk;
    private widget.TextBox TRKab;
    private widget.TextBox TRKdDok;
    private widget.TextBox TRKdPj;
    private widget.TextBox TRKdPoli;
    private widget.TextBox TRKec;
    private widget.TextBox TRKel;
    private widget.TextBox TRKuota;
    private widget.TextBox TRNmDok;
    private widget.TextBox TRNmPenjab;
    private widget.TextBox TRNmPj;
    private widget.TextBox TRNmPoli;
    private widget.TextBox TRNoReg;
    private widget.TextBox TRNoRm;
    private widget.TextBox TRProv;
    private widget.TextBox TRSisaKuota;
    private widget.TextBox TRTglBook;
    private widget.TextBox TRTglPoli;
    private widget.TextBox TTempatLahir;
    private widget.TextBox TTl;
    private widget.TextBox TUmur;
    private widget.Button btnTarif;
    private javax.swing.ButtonGroup buttonGroup1;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel10;
    private widget.Label jLabel13;
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
    private widget.Label jLabel4;
    private widget.Label jLabel41;
    private widget.Label jLabel42;
    private widget.Label jLabel43;
    private widget.Label jLabel44;
    private widget.Label jLabel45;
    private widget.Label jLabel46;
    private widget.Label jLabel47;
    private widget.Label jLabel48;
    private widget.Label jLabel49;
    private widget.Label jLabel5;
    private widget.Label jLabel50;
    private widget.Label jLabel51;
    private widget.Label jLabel52;
    private widget.Label jLabel57;
    private widget.Label jLabel58;
    private widget.Label jLabel59;
    private widget.Label jLabel6;
    private widget.Label jLabel60;
    private widget.Label jLabel61;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator5;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Table tbDiagnosa;
    private widget.Table tbPasien;
    private widget.Table tbPasien1;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        Valid.tabelKosong(TabModePasien);
        try {
            pilihtampil = "";
            if (R1.isSelected() == true) {
                pilihtampil = "booking_periksa.status='Belum Dibalas' and booking_periksa.tanggal >= CURDATE()";
            } else if (R2.isSelected() == true) {
                pilihtampil = "booking_periksa.tanggal_booking between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + "' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + "' ";
            } else if (R3.isSelected() == true) {
                pilihtampil = "booking_periksa.tanggal between '" + Valid.SetTgl(DTPCari3.getSelectedItem() + "") + "' and '" + Valid.SetTgl(DTPCari4.getSelectedItem() + "") + "' ";
            }
            ps = koneksi.prepareStatement(
                    "select booking_periksa.no_booking,booking_periksa.tanggal,booking_periksa.nama,booking_periksa.alamat,booking_periksa.no_telp,booking_periksa.email,booking_periksa.kd_poli,poliklinik.nm_poli,"
                    + "booking_periksa.tambahan_pesan,booking_periksa.status,booking_periksa.tanggal_booking, booking_periksa.no_ktp, booking_periksa.tempat_lahir, booking_periksa.tgl_lahir, booking_periksa.ibukandung, booking_periksa.namakel, booking_periksa.namakec, booking_periksa.namakab, booking_periksa.namapro,"
                    + "booking_periksa.hintnorm, if(booking_periksa.pernahkerspw=\"1\",\"Ya\",\"Tidak\") AS pernahkerspw,booking_periksa.jk,booking_periksa.kd_poli,dokter.nm_dokter, penjab.png_jawab, booking_periksa.jam_praktek, booking_periksa.kd_pj, booking_periksa.kd_dok, booking_periksa.umur,booking_periksa.namapj,booking_periksa.hubunganpj,booking_periksa.agama,booking_periksa.pendidikan,booking_periksa.perkawinan  "
                    + "from booking_periksa inner join poliklinik on booking_periksa.kd_poli=poliklinik.kd_poli INNER JOIN dokter ON dokter.kd_dokter = booking_periksa.kd_dok INNER JOIN penjab ON penjab.kd_pj = booking_periksa.kd_pj "
                    + "where " + pilihtampil + (TCari.getText().trim().equals("") ? "" : " and (booking_periksa.no_booking like ? or booking_periksa.nama like ? or booking_periksa.alamat like ? or booking_periksa.no_telp like ? or "
                    + "booking_periksa.email like ? or poliklinik.nm_poli like ? or booking_periksa.tambahan_pesan like ? or booking_periksa.status like ?)") + " order by booking_periksa.no_booking");
            try {
                if (!TCari.getText().trim().equals("")) {
                    ps.setString(1, "%" + TCari.getText().trim() + "%");
                    ps.setString(2, "%" + TCari.getText().trim() + "%");
                    ps.setString(3, "%" + TCari.getText().trim() + "%");
                    ps.setString(4, "%" + TCari.getText().trim() + "%");
                    ps.setString(5, "%" + TCari.getText().trim() + "%");
                    ps.setString(6, "%" + TCari.getText().trim() + "%");
                    ps.setString(7, "%" + TCari.getText().trim() + "%");
                    ps.setString(8, "%" + TCari.getText().trim() + "%");
                }

                rs = ps.executeQuery();
                while (rs.next()) {
                    TabModePasien.addRow(new Object[]{
                        false, rs.getString("no_booking"), rs.getString("tanggal"), rs.getString("nama"), rs.getString("no_ktp"), rs.getString("no_telp"), rs.getString("alamat") + ", " + rs.getString("namakel") + ", " + rs.getString("namakec") + ", " + rs.getString("namakab") + ", " + rs.getString("namapro"),
                        rs.getString("tempat_lahir"), rs.getString("tgl_lahir"), rs.getString("ibukandung"), rs.getString("hintnorm"), rs.getString("pernahkerspw"), rs.getString("jk"),
                        rs.getString("kd_poli"), rs.getString("nm_poli"), rs.getString("kd_dok"), rs.getString("nm_dokter"), rs.getString("kd_pj"), rs.getString("png_jawab"),
                        rs.getString("email"), rs.getString("status"), rs.getString("tanggal_booking"), rs.getString("umur"), rs.getString("namapj"), rs.getString("hubunganpj"),
                        rs.getString("agama"), rs.getString("pendidikan"), rs.getString("perkawinan")
                    });
                }
            } catch (Exception e) {
                System.out.println(e);
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
    }

    private void tampilHint() {
        Valid.tabelKosong(tabModeHint);
        try {
            if (!TPasien.getText().trim().equals("")) {
                ps3 = koneksi.prepareStatement("select pasien.no_rkm_medis, pasien.nm_pasien, pasien.no_ktp, pasien.jk, "
                        + "pasien.tmp_lahir, pasien.tgl_lahir,pasien.nm_ibu, pasien.alamat,kelurahan.nm_kel,kecamatan.nm_kec,kabupaten.nm_kab,propinsi.nm_prop,"
                        + "pasien.gol_darah, pasien.pekerjaan,pasien.stts_nikah,pasien.agama,pasien.tgl_daftar,pasien.no_tlp,pasien.umur,"
                        + "pasien.pnd, pasien.keluarga, pasien.namakeluarga,penjab.png_jawab,pasien.no_peserta,pasien.pekerjaanpj,"
                        + "pasien.alamatpj,pasien.kelurahanpj,pasien.kecamatanpj,pasien.kabupatenpj,pasien.propinsipj,"
                        + "perusahaan_pasien.kode_perusahaan,perusahaan_pasien.nama_perusahaan,pasien.bahasa_pasien,"
                        + "bahasa_pasien.nama_bahasa,pasien.suku_bangsa,suku_bangsa.nama_suku_bangsa,pasien.nip,pasien.email,cacat_fisik.nama_cacat,pasien.cacat_fisik,pasien.kd_pj from pasien "
                        + "inner join kelurahan on pasien.kd_kel=kelurahan.kd_kel inner join kecamatan on pasien.kd_kec=kecamatan.kd_kec "
                        + "inner join kabupaten on pasien.kd_kab=kabupaten.kd_kab inner join perusahaan_pasien on perusahaan_pasien.kode_perusahaan=pasien.perusahaan_pasien "
                        + "inner join cacat_fisik on pasien.cacat_fisik=cacat_fisik.id inner join propinsi on pasien.kd_prop=propinsi.kd_prop "
                        + "inner join bahasa_pasien on bahasa_pasien.id=pasien.bahasa_pasien inner join suku_bangsa on suku_bangsa.id=pasien.suku_bangsa "
                        + "inner join penjab on pasien.kd_pj=penjab.kd_pj "
                        + "where  pasien.nm_pasien like ? UNION select pasien.no_rkm_medis, pasien.nm_pasien, pasien.no_ktp, pasien.jk,"
                        + "pasien.tmp_lahir, pasien.tgl_lahir,pasien.nm_ibu, pasien.alamat,kelurahan.nm_kel,kecamatan.nm_kec,kabupaten.nm_kab,propinsi.nm_prop,"
                        + "pasien.gol_darah, pasien.pekerjaan,pasien.stts_nikah,pasien.agama,pasien.tgl_daftar,pasien.no_tlp,pasien.umur,"
                        + "pasien.pnd, pasien.keluarga, pasien.namakeluarga,penjab.png_jawab,pasien.no_peserta,pasien.pekerjaanpj,"
                        + "pasien.alamatpj,pasien.kelurahanpj,pasien.kecamatanpj,pasien.kabupatenpj,pasien.propinsipj,"
                        + "perusahaan_pasien.kode_perusahaan,perusahaan_pasien.nama_perusahaan,pasien.bahasa_pasien,"
                        + "bahasa_pasien.nama_bahasa,pasien.suku_bangsa,suku_bangsa.nama_suku_bangsa,pasien.nip,pasien.email,cacat_fisik.nama_cacat,pasien.cacat_fisik,pasien.kd_pj from pasien "
                        + "inner join kelurahan on pasien.kd_kel=kelurahan.kd_kel inner join kecamatan on pasien.kd_kec=kecamatan.kd_kec "
                        + "inner join kabupaten on pasien.kd_kab=kabupaten.kd_kab inner join perusahaan_pasien on perusahaan_pasien.kode_perusahaan=pasien.perusahaan_pasien "
                        + "inner join cacat_fisik on pasien.cacat_fisik=cacat_fisik.id inner join propinsi on pasien.kd_prop=propinsi.kd_prop "
                        + "inner join bahasa_pasien on bahasa_pasien.id=pasien.bahasa_pasien inner join suku_bangsa on suku_bangsa.id=pasien.suku_bangsa "
                        + "inner join penjab on pasien.kd_pj=penjab.kd_pj "
                        + "where  pasien.no_ktp like ?  UNION select pasien.no_rkm_medis, pasien.nm_pasien, pasien.no_ktp, pasien.jk,"
                        + "pasien.tmp_lahir, pasien.tgl_lahir,pasien.nm_ibu, pasien.alamat,kelurahan.nm_kel,kecamatan.nm_kec,kabupaten.nm_kab,propinsi.nm_prop,"
                        + "pasien.gol_darah, pasien.pekerjaan,pasien.stts_nikah,pasien.agama,pasien.tgl_daftar,pasien.no_tlp,pasien.umur,"
                        + "pasien.pnd, pasien.keluarga, pasien.namakeluarga,penjab.png_jawab,pasien.no_peserta,pasien.pekerjaanpj,"
                        + "pasien.alamatpj,pasien.kelurahanpj,pasien.kecamatanpj,pasien.kabupatenpj,pasien.propinsipj,"
                        + "perusahaan_pasien.kode_perusahaan,perusahaan_pasien.nama_perusahaan,pasien.bahasa_pasien,"
                        + "bahasa_pasien.nama_bahasa,pasien.suku_bangsa,suku_bangsa.nama_suku_bangsa,pasien.nip,pasien.email,cacat_fisik.nama_cacat,pasien.cacat_fisik,pasien.kd_pj from pasien "
                        + "inner join kelurahan on pasien.kd_kel=kelurahan.kd_kel inner join kecamatan on pasien.kd_kec=kecamatan.kd_kec "
                        + "inner join kabupaten on pasien.kd_kab=kabupaten.kd_kab inner join perusahaan_pasien on perusahaan_pasien.kode_perusahaan=pasien.perusahaan_pasien "
                        + "inner join cacat_fisik on pasien.cacat_fisik=cacat_fisik.id inner join propinsi on pasien.kd_prop=propinsi.kd_prop "
                        + "inner join bahasa_pasien on bahasa_pasien.id=pasien.bahasa_pasien inner join suku_bangsa on suku_bangsa.id=pasien.suku_bangsa "
                        + "inner join penjab on pasien.kd_pj=penjab.kd_pj "
                        + "where  pasien.tgl_lahir like ? UNION select pasien.no_rkm_medis, pasien.nm_pasien, pasien.no_ktp, pasien.jk,"
                        + "pasien.tmp_lahir, pasien.tgl_lahir,pasien.nm_ibu, pasien.alamat,kelurahan.nm_kel,kecamatan.nm_kec,kabupaten.nm_kab,propinsi.nm_prop,"
                        + "pasien.gol_darah, pasien.pekerjaan,pasien.stts_nikah,pasien.agama,pasien.tgl_daftar,pasien.no_tlp,pasien.umur,"
                        + "pasien.pnd, pasien.keluarga, pasien.namakeluarga,penjab.png_jawab,pasien.no_peserta,pasien.pekerjaanpj,"
                        + "pasien.alamatpj,pasien.kelurahanpj,pasien.kecamatanpj,pasien.kabupatenpj,pasien.propinsipj,"
                        + "perusahaan_pasien.kode_perusahaan,perusahaan_pasien.nama_perusahaan,pasien.bahasa_pasien,"
                        + "bahasa_pasien.nama_bahasa,pasien.suku_bangsa,suku_bangsa.nama_suku_bangsa,pasien.nip,pasien.email,cacat_fisik.nama_cacat,pasien.cacat_fisik,pasien.kd_pj from pasien "
                        + "inner join kelurahan on pasien.kd_kel=kelurahan.kd_kel inner join kecamatan on pasien.kd_kec=kecamatan.kd_kec "
                        + "inner join kabupaten on pasien.kd_kab=kabupaten.kd_kab inner join perusahaan_pasien on perusahaan_pasien.kode_perusahaan=pasien.perusahaan_pasien "
                        + "inner join cacat_fisik on pasien.cacat_fisik=cacat_fisik.id inner join propinsi on pasien.kd_prop=propinsi.kd_prop "
                        + "inner join bahasa_pasien on bahasa_pasien.id=pasien.bahasa_pasien inner join suku_bangsa on suku_bangsa.id=pasien.suku_bangsa "
                        + "inner join penjab on pasien.kd_pj=penjab.kd_pj "
                        + "where  pasien.nm_ibu like ?  UNION select pasien.no_rkm_medis, pasien.nm_pasien, pasien.no_ktp, pasien.jk,"
                        + "pasien.tmp_lahir, pasien.tgl_lahir,pasien.nm_ibu, pasien.alamat,kelurahan.nm_kel,kecamatan.nm_kec,kabupaten.nm_kab,propinsi.nm_prop,"
                        + "pasien.gol_darah, pasien.pekerjaan,pasien.stts_nikah,pasien.agama,pasien.tgl_daftar,pasien.no_tlp,pasien.umur,"
                        + "pasien.pnd, pasien.keluarga, pasien.namakeluarga,penjab.png_jawab,pasien.no_peserta,pasien.pekerjaanpj,"
                        + "pasien.alamatpj,pasien.kelurahanpj,pasien.kecamatanpj,pasien.kabupatenpj,pasien.propinsipj,"
                        + "perusahaan_pasien.kode_perusahaan,perusahaan_pasien.nama_perusahaan,pasien.bahasa_pasien,"
                        + "bahasa_pasien.nama_bahasa,pasien.suku_bangsa,suku_bangsa.nama_suku_bangsa,pasien.nip,pasien.email,cacat_fisik.nama_cacat,pasien.cacat_fisik,pasien.kd_pj from pasien "
                        + "inner join kelurahan on pasien.kd_kel=kelurahan.kd_kel inner join kecamatan on pasien.kd_kec=kecamatan.kd_kec "
                        + "inner join kabupaten on pasien.kd_kab=kabupaten.kd_kab inner join perusahaan_pasien on perusahaan_pasien.kode_perusahaan=pasien.perusahaan_pasien "
                        + "inner join cacat_fisik on pasien.cacat_fisik=cacat_fisik.id inner join propinsi on pasien.kd_prop=propinsi.kd_prop "
                        + "inner join bahasa_pasien on bahasa_pasien.id=pasien.bahasa_pasien inner join suku_bangsa on suku_bangsa.id=pasien.suku_bangsa "
                        + "inner join penjab on pasien.kd_pj=penjab.kd_pj "
                        + "where  pasien.no_rkm_medis like ? ");
            }

            try {

                if (!TAlamat.getText().trim().equals("")) {
                    ps3.setString(1, "%" + TPasien.getText().trim() + "%");
                    ps3.setString(2, "%" + TNoKtp.getText().trim() + "%");
                    ps3.setString(3, "%" + TTl.getText().trim() + "%");
                    ps3.setString(4, "%" + TIbuKandung.getText().trim() + "%");
                    if (TPernahKeRspw.getText().trim().equals("Ya")) {
                        ps3.setString(5, "%" + THintRm.getText().trim() + "%");
                    } else {
                        ps3.setString(5, "%------%");
                    }
                }

                rs = ps3.executeQuery();
                while (rs.next()) {
                    tabModeHint.addRow(new Object[]{
                        false,
                        rs.getString("no_rkm_medis"),
                        rs.getString("nm_pasien"),
                        rs.getString("no_ktp"),
                        rs.getString("jk"),
                        rs.getString("tmp_lahir"),
                        rs.getString("tgl_lahir"),
                        rs.getString("nm_ibu"),
                        rs.getString("alamat") + ", " + rs.getString("nm_kel") + ", " + rs.getString("nm_kec") + ", " + rs.getString("nm_kab") + ", " + rs.getString("nm_prop"),});
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }

                if (ps3 != null) {
                    ps3.close();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        //LCountHint.setText(tabMode4.getRowCount()+" dari nama pasien ");
    }

    private void isRawat() {
        Sequel.cariIsi("select reg_periksa.no_rkm_medis from reg_periksa where reg_periksa.no_rawat=? ", TNoKtp, TNoBooking.getText());
    }

    private void isPsien() {
        Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis=? ", TPasien, TNoKtp.getText());
    }

    private void getData() {
        if (tbPasien.getSelectedRow() != -1) {
            String[] arrtglbok;
            arrtglbok = tbPasien.getValueAt(tbPasien.getSelectedRow(), 21).toString().toUpperCase().split(" ");
            TNoBooking.setText(tbPasien.getValueAt(tbPasien.getSelectedRow(), 1).toString());
            TRTglBook.setText(arrtglbok[0]);
            TRJamBook.setText(arrtglbok[1].replaceAll("\\.0$", ""));
            TNoKtp.setText(tbPasien.getValueAt(tbPasien.getSelectedRow(), 4).toString());
            TPasien.setText(tbPasien.getValueAt(tbPasien.getSelectedRow(), 3).toString().toUpperCase());
            TAlamat.setText(tbPasien.getValueAt(tbPasien.getSelectedRow(), 6).toString().toUpperCase());
            TNoTelp.setText(tbPasien.getValueAt(tbPasien.getSelectedRow(), 5).toString());
            TTempatLahir.setText(tbPasien.getValueAt(tbPasien.getSelectedRow(), 7).toString().toUpperCase());
            TTl.setText(tbPasien.getValueAt(tbPasien.getSelectedRow(), 8).toString());
            TIbuKandung.setText(tbPasien.getValueAt(tbPasien.getSelectedRow(), 9).toString().toUpperCase());
            THintRm.setText(tbPasien.getValueAt(tbPasien.getSelectedRow(), 10).toString());
            TPernahKeRspw.setText(tbPasien.getValueAt(tbPasien.getSelectedRow(), 11).toString());
            TRJk.setText(tbPasien.getValueAt(tbPasien.getSelectedRow(), 12).toString());

            TRTglPoli.setText(tbPasien.getValueAt(tbPasien.getSelectedRow(), 2).toString());
            TRKdPoli.setText(tbPasien.getValueAt(tbPasien.getSelectedRow(), 13).toString());
            TRNmPoli.setText(tbPasien.getValueAt(tbPasien.getSelectedRow(), 14).toString());
            TRKdDok.setText(tbPasien.getValueAt(tbPasien.getSelectedRow(), 15).toString());
            TRNmDok.setText(tbPasien.getValueAt(tbPasien.getSelectedRow(), 16).toString());
            TRKdPj.setText(tbPasien.getValueAt(tbPasien.getSelectedRow(), 17).toString());
            TRNmPj.setText(tbPasien.getValueAt(tbPasien.getSelectedRow(), 18).toString());

//            TanggalBooking.setText(tbPasien.getValueAt(tbPasien.getSelectedRow(), 2).toString());
//            TanggalPeriksa.setText(tbPasien.getValueAt(tbPasien.getSelectedRow(), 2).toString());
//            NamaPasien.setText(tbPasien.getValueAt(tbPasien.getSelectedRow(), 3).toString().toUpperCase());
            arrSplit = tbPasien.getValueAt(tbPasien.getSelectedRow(), 6).toString().toUpperCase().split(",");

            try {
                if (!arrSplit[0].equals("")) {
//                    Alamat.setText(arrSplit[0]);
                }
            } catch (Exception e) {
//                Alamat.setText("-");
            }

            try {
                if (!arrSplit[1].equals("")) {
//                    Kelurahan.setText(arrSplit[1].replaceFirst(" ", ""));
                    TRKel.setText(arrSplit[1].replaceFirst(" ", ""));
                }
            } catch (Exception e) {
//                Kelurahan.setText("-");
                TRKel.setText("-");
            }

            try {
                if (!arrSplit[2].equals("")) {
//                    Kecamatan.setText(arrSplit[2].replaceFirst(" ", ""));
                    TRKec.setText(arrSplit[2].replaceFirst(" ", ""));
                }
            } catch (Exception e) {
//                Kecamatan.setText("-");
                TRKec.setText("-");
            }

            try {
                if (!arrSplit[3].equals("")) {
//                    Kabupaten.setText(arrSplit[3].replaceFirst(" ", ""));
                    TRKab.setText(arrSplit[3].replaceFirst(" ", ""));
                }
            } catch (Exception e) {
//                Kabupaten.setText("-");
                TRKab.setText("-");
            }

            try {
                if (!arrSplit[4].equals("")) {
//                    Propinsi.setText(arrSplit[4].replaceFirst(" ", ""));
                    TRProv.setText(arrSplit[4].replaceFirst(" ", ""));
                }
            } catch (Exception e) {
//                Propinsi.setText("-");
                TRProv.setText("-");
            }

//            NoTelp.setText(tbPasien.getValueAt(tbPasien.getSelectedRow(), 5).toString());
            //Email.setText(tbPasien.getValueAt(tbPasien.getSelectedRow(),6).toString());
//            KdPoli.setText(tbPasien.getValueAt(tbPasien.getSelectedRow(), 7).toString());
//            NmPoli.setText(tbPasien.getValueAt(tbPasien.getSelectedRow(), 8).toString());
//            TambahanPesan.setText(tbPasien.getValueAt(tbPasien.getSelectedRow(), 9).toString());
            TUmur.setText(tbPasien.getValueAt(tbPasien.getSelectedRow(), 22).toString());
            TRNmPenjab.setText(tbPasien.getValueAt(tbPasien.getSelectedRow(), 23).toString().toUpperCase());
            TRHbSaudara.setText(tbPasien.getValueAt(tbPasien.getSelectedRow(), 24).toString());

            tampilHint();
            isNomer();
            System.out.println(URUTNOREG);
//            isRawat();
//            isPsien();

        } else {
            Valid.tabelKosong(tabModeHint);
        }
    }

    public void setNoRm(String norwt, Date tgl1, Date tgl2, String status) {
        TNoBooking.setText(norwt);
        TCari.setText(norwt);
        TAlamat.setText(status);
        isRawat();
        isPsien();
        DTPCari1.setDate(tgl1);
        DTPCari2.setDate(tgl2);
        ChkInput.setSelected(true);
        isForm();
    }

    private void isForm() {
        if (ChkInput.isSelected() == true) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 210));
            FormInput.setVisible(true);
            ChkInput.setVisible(true);
        } else if (ChkInput.isSelected() == false) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 210));
            FormInput.setVisible(true);
            ChkInput.setVisible(true);
        }
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getdpjp_ranap());
        BtnHapus.setEnabled(akses.getdpjp_ranap());
        btnTarif.setEnabled(akses.getdokter());
        BtnPrint.setEnabled(akses.getdpjp_ranap());

    }

    private void tampildiagnosa() {
        try {
            jml = 0;
            for (i = 0; i < tbDiagnosa.getRowCount(); i++) {
                if (tbDiagnosa.getValueAt(i, 0).toString().equals("true")) {
                    jml++;
                }
            }

            pilih = null;
            pilih = new boolean[jml];
            kode = null;
            kode = new String[jml];
            nama = null;
            nama = new String[jml];

            index = 0;
            for (i = 0; i < tbDiagnosa.getRowCount(); i++) {
                if (tbDiagnosa.getValueAt(i, 0).toString().equals("true")) {
                    pilih[index] = true;
                    kode[index] = tbDiagnosa.getValueAt(i, 1).toString();
                    nama[index] = tbDiagnosa.getValueAt(i, 2).toString();
                    index++;
                }
            }

            Valid.tabelKosong(tabModeDiagnosa);
            for (i = 0; i < jml; i++) {
                tabModeDiagnosa.addRow(new Object[]{pilih[i], kode[i], nama[i]});
            }

            ps = koneksi.prepareStatement("select dokter.kd_dokter,dokter.nm_dokter from dokter "
                    + "where  status='1' and dokter.kd_dokter like ? or status='1' and dokter.nm_dokter like ? order by dokter.nm_dokter");
            try {
                ps.setString(1, "%" + Dokter.getText().trim() + "%");
                ps.setString(2, "%" + Dokter.getText().trim() + "%");
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabModeDiagnosa.addRow(new Object[]{false, rs.getString(1), rs.getString(2)});
                }
            } catch (Exception e) {
                System.out.println(e);
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
    }

    private void autoNomor() {
        if (ChkRM1.isSelected() == true) {
            if (tahun.equals("Yes")) {
                awalantahun = TRTglPoli.getText().substring(6, 10);
            } else {
                awalantahun = "";
            }

            if (bulan.equals("Yes")) {
                awalanbulan = TRTglPoli.getText().substring(3, 5);
            } else {
                awalanbulan = "";
            }

            if (posisitahun.equals("Depan")) {
                switch (pengurutan) {
                    case "Straight":
                        Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(set_no_rkm_medis.no_rkm_medis,6),signed)),0) from set_no_rkm_medis", "", 6, NoRm);
                        break;
                    case "Terminal":
                        Valid.autoNomer4("select ifnull(MAX(CONVERT(CONCAT(SUBSTRING(RIGHT(set_no_rkm_medis.no_rkm_medis,6),5,2),SUBSTRING(RIGHT(set_no_rkm_medis.no_rkm_medis,6),3,2),SUBSTRING(RIGHT(set_no_rkm_medis.no_rkm_medis,6),1,2)),signed)),0) from set_no_rkm_medis", "", 6, NoRm);
                        break;
                    case "Middle":
                        Valid.autoNomer5("select ifnull(MAX(CONVERT(CONCAT(SUBSTRING(RIGHT(set_no_rkm_medis.no_rkm_medis,6),3,2),SUBSTRING(RIGHT(set_no_rkm_medis.no_rkm_medis,6),1,2),SUBSTRING(RIGHT(set_no_rkm_medis.no_rkm_medis,6),5,2)),signed)),0) from set_no_rkm_medis", "", 6, NoRm);
                        break;
                }
            } else if (posisitahun.equals("Belakang")) {
                switch (pengurutan) {
                    case "Straight":
                        Valid.autoNomer3("select ifnull(MAX(CONVERT(LEFT(set_no_rkm_medis.no_rkm_medis,6),signed)),0) from set_no_rkm_medis", "", 6, NoRm);
                        break;
                    case "Terminal":
                        Valid.autoNomer4("select ifnull(MAX(CONVERT(CONCAT(SUBSTRING(LEFT(set_no_rkm_medis.no_rkm_medis,6),5,2),SUBSTRING(LEFT(set_no_rkm_medis.no_rkm_medis,6),3,2),SUBSTRING(LEFT(set_no_rkm_medis.no_rkm_medis,6),1,2)),signed)),0) from set_no_rkm_medis", "", 6, NoRm);
                        break;
                    case "Middle":
                        Valid.autoNomer5("select ifnull(MAX(CONVERT(CONCAT(SUBSTRING(LEFT(set_no_rkm_medis.no_rkm_medis,6),3,2),SUBSTRING(LEFT(set_no_rkm_medis.no_rkm_medis,6),1,2),SUBSTRING(LEFT(set_no_rkm_medis.no_rkm_medis,6),5,2)),signed)),0) from set_no_rkm_medis", "", 6, NoRm);
                        break;
                }
            }

            if (posisitahun.equals("Depan")) {
                TRNoRm.setText(awalantahun + awalanbulan + NoRm.getText());
            } else if (posisitahun.equals("Belakang")) {
                if (!(awalanbulan + awalantahun).equals("")) {
                    TRNoRm.setText(NoRm.getText() + "-" + awalanbulan + awalantahun);
                } else {
                    TRNoRm.setText(NoRm.getText());
                }
            }
        }
    }

    private void isBooking2() {
        Sequel.AutoComitFalse();
        sukses = true;
        if (Sequel.menyimpantf("booking_registrasi", "?,?,?,?,?,?,?,?,?,?,?", "Pasien dan Tanggal", 11, new String[]{
            TRTglBook.getText(), TRJamBook.getText(), tbPasien1.getValueAt(tbPasien1.getSelectedRow(), 1).toString(),
            TRTglPoli.getText(), TRKdDok.getText(),
            TRKdPoli.getText(), TRNoReg.getText(), TRKdPj.getText(), "0",
            TRTglPoli.getText() + " " + TRJamBook.getText(),
            "Belum"
        }) == true) {
            sukses = true;
        } else {
            sukses = false;
        }

        if (sukses == true) {
            sukses = Sequel.menyimpantf2("booking_periksa_balasan", "?,?", "Balasan Pesan", 2, new String[]{TNoBooking.getText(), "Diterima oleh admin, dan update RM : " + tbPasien1.getValueAt(tbPasien1.getSelectedRow(), 1).toString()});
        }

        if (sukses == true) {
            Sequel.mengedit("booking_periksa", "no_booking=?", "status=?", 2, new String[]{StatusBalas1.getSelectedItem().toString(), TNoBooking.getText()});
//            if (ChkRM1.isSelected() == true) {
//                Sequel.queryu2("delete from set_no_rkm_medis");
//                Sequel.queryu2("insert into set_no_rkm_medis values(?)", 1, new String[]{TRNoRm.getText()});
//            }
            Sequel.Commit();
            tampil();
        } else {
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat pemrosesan data, transaksi dibatalkan.\nPeriksa kembali data sebelum melanjutkan menyimpan..!!");
            Sequel.RollBack();
        }
        Sequel.AutoComitTrue();
    }

    private void isBooking() {
        String agama = tbPasien.getValueAt(tbPasien.getSelectedRow(), 25).toString();
        String pendidikan = tbPasien.getValueAt(tbPasien.getSelectedRow(), 26).toString();
        String kawin = tbPasien.getValueAt(tbPasien.getSelectedRow(), 27).toString();

        Sequel.AutoComitFalse();
        sukses = true;
        if (StatusBalas1.getSelectedItem().toString().equals("Diterima")) {
            System.out.println("diterima");
            if (TRKel.isEditable() == true) {
                Sequel.queryu4("insert ignore into kelurahan values(?,?)", 2, new String[]{"0", TRKel.getText().replaceAll("KELURAHAN", "-")});
                kdkel = Sequel.cariIsi("select kelurahan.kd_kel from kelurahan where kelurahan.nm_kel=?", TRKel.getText().replaceAll("KELURAHAN", "-"));
            } else if (TRKel.isEditable() == false) {
                if (kdkel.equals("")) {
                    Sequel.queryu4("insert ignore into kelurahan values(?,?)", 2, new String[]{"0", TRKel.getText().replaceAll("KELURAHAN", "-")});
                    kdkel = Sequel.cariIsi("select kelurahan.kd_kel from kelurahan where kelurahan.nm_kel=?", TRKel.getText().replaceAll("KELURAHAN", "-"));
                }
            }

            if (TRKec.isEditable() == true) {
                Sequel.queryu4("insert ignore into kecamatan values(?,?)", 2, new String[]{"0", TRKec.getText().replaceAll("KECAMATAN", "-")});
                kdkec = Sequel.cariIsi("select kecamatan.kd_kec from kecamatan where kecamatan.nm_kec=?", TRKec.getText().replaceAll("KECAMATAN", "-"));
            } else if (TRKec.isEditable() == false) {
                if (kdkec.equals("")) {
                    Sequel.queryu4("insert ignore into kecamatan values(?,?)", 2, new String[]{"0", TRKec.getText().replaceAll("KECAMATAN", "-")});
                    kdkec = Sequel.cariIsi("select kecamatan.kd_kec from kecamatan where kecamatan.nm_kec=?", TRKec.getText().replaceAll("KECAMATAN", "-"));
                }
            }

            if (TRKab.isEditable() == true) {
                Sequel.queryu4("insert ignore into kabupaten values(?,?)", 2, new String[]{"0", TRKab.getText().replaceAll("KABUPATEN", "-")});
                kdkab = Sequel.cariIsi("select kabupaten.kd_kab from kabupaten where kabupaten.nm_kab=?", TRKab.getText().replaceAll("KABUPATEN", "-"));
            } else if (TRKab.isEditable() == false) {
                if (kdkab.equals("")) {
                    Sequel.queryu4("insert ignore into kabupaten values(?,?)", 2, new String[]{"0", TRKab.getText().replaceAll("KABUPATEN", "-")});
                    kdkab = Sequel.cariIsi("select kabupaten.kd_kab from kabupaten where kabupaten.nm_kab=?", TRKab.getText().replaceAll("KABUPATEN", "-"));
                }
            }

            if (TRProv.isEditable() == true) {
                Sequel.queryu4("insert ignore into propinsi values(?,?)", 2, new String[]{"0", TRProv.getText().replaceAll("PROPINSI", "-")});
                kdprop = Sequel.cariIsi("select propinsi.kd_prop from propinsi where propinsi.nm_prop=?", TRProv.getText().replaceAll("PROPINSI", "-"));
            } else if (TRProv.isEditable() == false) {
                if (kdprop.equals("")) {
                    Sequel.queryu4("insert ignore into propinsi values(?,?)", 2, new String[]{"0", TRProv.getText().replaceAll("PROPINSI", "-")});
                    kdprop = Sequel.cariIsi("select propinsi.kd_prop from propinsi where propinsi.nm_prop=?", TRProv.getText().replaceAll("PROPINSI", "-"));
                }
            }

            Sequel.queryu4("insert into cacat_fisik values(?,?)", 2, new String[]{"0", "-"});
            Sequel.queryu4("insert into penjab values(?,?)", 2, new String[]{"-", "-"});
            Sequel.queryu4("insert into bahasa_pasien values(?,?)", 2, new String[]{"0", "-"});
            Sequel.queryu4("insert into suku_bangsa values(?,?)", 2, new String[]{"0", "-"});
            Sequel.queryu4("insert into perusahaan_pasien values(?,?,?,?,?)", 2, new String[]{"-", "-", "-", "-", "-"});

            autoNomor();
            if (Sequel.menyimpantf2("pasien", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "No.Rekam Medis Pasien", 36, new String[]{
                TRNoRm.getText(), TPasien.getText(), TNoKtp.getText(), TRJk.getText(), TTempatLahir.getText(), TTl.getText(), TIbuKandung.getText(), TAlamat.getText().split(",")[0].replaceAll("ALAMAT", ""),
                "-", "-", kawin, agama, TRTglPoli.getText(), TNoTelp.getText(), "0 Th", pendidikan, TRHbSaudara.getText(), TRNmPenjab.getText(), TRKdPj.getText(), "", kdkel, kdkec, kdkab, "-", TAlamat.getText().split(",")[0].replaceAll("ALAMAT", ""),
                TRKel.getText(), TRKec.getText(), TRKab.getText(), "-",
                Sequel.cariIsi("select suku_bangsa.id from suku_bangsa where suku_bangsa.nama_suku_bangsa=?", "-"),
                Sequel.cariIsi("select bahasa_pasien.id from bahasa_pasien where bahasa_pasien.nama_bahasa=?", "-"),
                Sequel.cariIsi("select cacat_fisik.id from cacat_fisik where cacat_fisik.nama_cacat=?", "-"), "pasien@mail.com", "-", kdprop, TRProv.getText()
            }) == true) {
                if (Sequel.menyimpantf2("booking_periksa_diterima", "?,?", "Booking Diterima", 2, new String[]{TNoBooking.getText(), TRNoRm.getText()}) == true) {
                    if (Sequel.menyimpantf2("booking_registrasi", "?,?,?,?,?,?,?,?,?,?,?", "Pasien dan Tanggal", 11, new String[]{
                        TRTglBook.getText(), TRJamBook.getText(), TRNoRm.getText(),
                        TRTglPoli.getText(), TRKdDok.getText(), TRKdPoli.getText(), TRNoReg.getText(), TRKdPj.getText(), "1",
                        TRTglPoli.getText() + " " + TRJamBook.getText(), "belum"
                    }) == true) {
                        if (Sequel.menyimpantf2("personal_pasien", "?,'-',aes_encrypt(?,'windi')", "Password Pasien", 2, new String[]{TRNoRm.getText(), EnkripsiAES.encrypt(TRNoRm.getText())}) == false) {
                            sukses = false;
                        }
                    } else {
                        sukses = false;
                    }
                } else {
                    sukses = false;
                }
            } else {
                autoNomor();
                if (Sequel.menyimpantf2("pasien", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "No.Rekam Medis Pasien", 36, new String[]{
                    TRNoRm.getText(), TPasien.getText(), TNoKtp.getText(), TRJk.getText(), TTempatLahir.getText(), TTl.getText(), TIbuKandung.getText(), TAlamat.getText().split(",")[0].replaceAll("ALAMAT", ""),
                "-", "-", kawin, agama, TRTglPoli.getText(), TNoTelp.getText(), "0 Th", pendidikan, TRHbSaudara.getText(), TRNmPenjab.getText(), TRKdPj.getText(), "", kdkel, kdkec, kdkab, "-", TAlamat.getText().split(",")[0].replaceAll("ALAMAT", ""),
                TRKel.getText(), TRKec.getText(), TRKab.getText(), "-",
                Sequel.cariIsi("select suku_bangsa.id from suku_bangsa where suku_bangsa.nama_suku_bangsa=?", "-"),
                Sequel.cariIsi("select bahasa_pasien.id from bahasa_pasien where bahasa_pasien.nama_bahasa=?", "-"),
                Sequel.cariIsi("select cacat_fisik.id from cacat_fisik where cacat_fisik.nama_cacat=?", "-"), "pasien@mail.com", "-", kdprop, TRProv.getText()
                }) == true) {
                    if (Sequel.menyimpantf2("booking_periksa_diterima", "?,?", "Booking Diterima", 2, new String[]{TNoBooking.getText(), TRNoRm.getText()}) == true) {
                        if (Sequel.menyimpantf2("booking_registrasi", "?,?,?,?,?,?,?,?,?,?,?", "Pasien dan Tanggal", 11, new String[]{
                            TRTglBook.getText(), TRJamBook.getText(), TRNoRm.getText(),
                            TRTglPoli.getText(), TRKdDok.getText(), TRKdPoli.getText(), TRNoReg.getText(), TRKdPj.getText(), "1",
                            TRTglPoli.getText() + " " + TRJamBook.getText(), "belum"
                        }) == true) {
                            if (Sequel.menyimpantf2("personal_pasien", "?,'-',aes_encrypt(?,'windi')", "Password Pasien", 2, new String[]{TRNoRm.getText(), EnkripsiAES.encrypt(TRNoRm.getText())}) == false) {
                                sukses = false;
                            }
                        } else {
                            sukses = false;
                        }
                    } else {
                        sukses = false;
                    }
                } else {
                    sukses = false;
                }
            }
        }

        if (sukses == true) {
            sukses = Sequel.menyimpantf2("booking_periksa_balasan", "?,?", "Balasan Pesan", 2, new String[]{TNoBooking.getText(), "Diterima oleh admin"});
        }

        if (sukses == true) {
            Sequel.mengedit("booking_periksa", "no_booking=?", "status=?", 2, new String[]{StatusBalas1.getSelectedItem().toString(), TNoBooking.getText()});
            if (ChkRM1.isSelected() == true) {
                Sequel.queryu2("delete from set_no_rkm_medis");
                Sequel.queryu2("insert into set_no_rkm_medis values(?)", 1, new String[]{TRNoRm.getText()});
            }
            Sequel.Commit();
            tampil();
//            BtnCloseBalasanActionPerformed(null);
        } else {
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat pemrosesan data, transaksi dibatalkan.\nPeriksa kembali data sebelum melanjutkan menyimpan..!!");
            Sequel.RollBack();
        }
        Sequel.AutoComitTrue();
        //autoNomor();
    }

    private void isRegister() {
        Sequel.mengedit("pasien", "no_rkm_medis=?", "umur=CONCAT(CONCAT(CONCAT(TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()), ' Th '),CONCAT(TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12), ' Bl ')),CONCAT(TIMESTAMPDIFF(DAY, DATE_ADD(DATE_ADD(tgl_lahir,INTERVAL TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()) YEAR), INTERVAL TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12) MONTH), CURDATE()), ' Hr'))", 1, new String[]{TRNoRm.getText()});
        status = Sequel.cariIsi("select if((select count(no_rkm_medis) from reg_periksa where no_rkm_medis='" + TRNoRm.getText() + "' and kd_poli='" + TRKdPoli.getText() + "')>0,'Lama','Baru' )");
        no_rawat = Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(no_rawat,6),signed)),0) from reg_periksa where tgl_registrasi='" + TRTglPoli.getText() + "' ", TRTglPoli.getText().replace("-", "/") + "/", 6);
        umur = "0";
        sttsumur = "Th";
        String[] arrumur;
        arrumur = TUmur.getText().toString().split(" ");
        if (Double.parseDouble(arrumur[0].replaceAll("Th", "")) > 0) {
            umur = "" + Double.parseDouble(arrumur[0].replaceAll("Th", ""));
            sttsumur = "Th";
        } else if (Double.parseDouble(arrumur[0].replaceAll("Th", "")) == 0) {
            if (Double.parseDouble(arrumur[1].replaceAll("Bl", "")) > 0) {
                umur = "" + Double.parseDouble(arrumur[1].replaceAll("Bl", ""));
                sttsumur = "Bl";
            } else if (Double.parseDouble(arrumur[1].replaceAll("Bl", "")) == 0) {
                umur = "" + Double.parseDouble(arrumur[2].replaceAll("Hr", ""));
                sttsumur = "Hr";
            }
        }
        if (Sequel.menyimpantf2("reg_periksa", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "No.Rawat", 19, new String[]{
            TRNoReg.getText(),
            no_rawat,
            TRTglPoli.getText(),
            jamN(),
            TRKdDok.getText(),
            TRNoRm.getText(),
            TRKdPoli.getText(),
            TRNmPenjab.getText(),
            TAlamat.getText() + ", " + TRKel.getText() + ", " + TRKec.getText() + ", " + TRKab.getText() + ", " + TRProv.getText(),
            TRHbSaudara.getText(),
            "" + Sequel.cariIsiAngka("select poliklinik.registrasilama from poliklinik where poliklinik.kd_poli=?", TRKdPoli.getText()),
            "Belum",
            "Lama",
            "Ralan",
            TRKdPj.getText(),
            umur,
            sttsumur,
            "Belum Bayar",
            status
        }) == true) {
//            Sequel.mengedit3("skdp_bpjs", "no_rkm_medis=? and tanggal_datang=?", "status='Sudah Periksa'", 2, new String[]{
//                tbObat.getValueAt(i, 3).toString(), tbObat.getValueAt(i, 5).toString()
//            });
            Sequel.queryu2("update booking_registrasi set status='Terdaftar' where no_rkm_medis=? and tanggal_periksa=?", 2, new String[]{
                TRNoRm.getText(),
                TRTglPoli.getText()
            });
            autoNomor();
        }
    }

    private void isRegister2() {
        String NoRMLama = tbPasien1.getValueAt(tbPasien1.getSelectedRow(), 1).toString();
        Sequel.mengedit("pasien", "no_rkm_medis=?", "umur=CONCAT(CONCAT(CONCAT(TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()), ' Th '),CONCAT(TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12), ' Bl ')),CONCAT(TIMESTAMPDIFF(DAY, DATE_ADD(DATE_ADD(tgl_lahir,INTERVAL TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()) YEAR), INTERVAL TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12) MONTH), CURDATE()), ' Hr'))", 1, new String[]{NoRMLama});
        status = Sequel.cariIsi("select if((select count(no_rkm_medis) from reg_periksa where no_rkm_medis='" + NoRMLama + "' and kd_poli='" + TRKdPoli.getText() + "')>0,'Lama','Baru' )");
        no_rawat = Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(no_rawat,6),signed)),0) from reg_periksa where tgl_registrasi='" + TRTglPoli.getText() + "' ", TRTglPoli.getText().replace("-", "/") + "/", 6);
        umur = "0";
        sttsumur = "Th";
        String[] arrumur;
        arrumur = TUmur.getText().toString().split(" ");
        if (Double.parseDouble(arrumur[0].replaceAll("Th", "")) > 0) {
            umur = "" + Double.parseDouble(arrumur[0].replaceAll("Th", ""));
            sttsumur = "Th";
        } else if (Double.parseDouble(arrumur[0].replaceAll("Th", "")) == 0) {
            if (Double.parseDouble(arrumur[1].replaceAll("Bl", "")) > 0) {
                umur = "" + Double.parseDouble(arrumur[1].replaceAll("Bl", ""));
                sttsumur = "Bl";
            } else if (Double.parseDouble(arrumur[1].replaceAll("Bl", "")) == 0) {
                umur = "" + Double.parseDouble(arrumur[2].replaceAll("Hr", ""));
                sttsumur = "Hr";
            }
        }
        if (Sequel.menyimpantf2("reg_periksa", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "No.Rawat", 19, new String[]{
            TRNoReg.getText(),
            no_rawat,
            TRTglPoli.getText(),
            jamN(),
            TRKdDok.getText(),
            NoRMLama,
            TRKdPoli.getText(),
            TRNmPenjab.getText(),
            TAlamat.getText() + ", " + TRKel.getText() + ", " + TRKec.getText() + ", " + TRKab.getText() + ", " + TRProv.getText(),
            TRHbSaudara.getText(),
            "" + Sequel.cariIsiAngka("select poliklinik.registrasilama from poliklinik where poliklinik.kd_poli=?", TRKdPoli.getText()),
            "Belum",
            "Lama",
            "Ralan",
            TRKdPj.getText(),
            umur,
            sttsumur,
            "Belum Bayar",
            status
        }) == true) {
//            Sequel.mengedit3("skdp_bpjs", "no_rkm_medis=? and tanggal_datang=?", "status='Sudah Periksa'", 2, new String[]{
//                tbObat.getValueAt(i, 3).toString(), tbObat.getValueAt(i, 5).toString()
//            });
            Sequel.queryu2("update booking_registrasi set status='Terdaftar' where no_rkm_medis=? and tanggal_periksa=?", 2, new String[]{
                NoRMLama,
                TRTglPoli.getText()
            });
            autoNomor();
        }
    }

    private void emptTeks() {
//        KdDokter.setText("");
//        NmDokter.setText("");
//        Kuota.setText("");
//        Pendaftar.setText("");
//        SisaKuota.setText("");
//        NoReg.setText("0");
//        kdpnj.setText("");
//        nmpnj.setText("");
//        BalasanPesan.setText("");
    }

    private void isNomer() {
        switch (URUTNOREG) {
            case "poli":
                Valid.autoNomer3("select ifnull(MAX(CONVERT(no_reg,signed)),0) from booking_registrasi where kd_poli='" + TRKdPoli.getText() + "' and tanggal_periksa='" + TRTglPoli.getText() + "'", "", 3, TRNoReg);
                break;
            case "dokter":
                Valid.autoNomer3("select ifnull(MAX(CONVERT(no_reg,signed)),0) from booking_registrasi where kd_dokter='" + TRKdDok.getText() + "' and tanggal_periksa='" + TRTglPoli.getText() + "'", "", 3, TRNoReg);
                break;
            case "dokter + poli":
                if (Sequel.cariInteger("select ifnull(MAX(CONVERT(booking_registrasi.no_reg,signed)),0) from booking_registrasi where booking_registrasi.kd_dokter='" + TRKdDok.getText() + "' and booking_registrasi.kd_poli='" + TRKdPoli.getText() + "' and booking_registrasi.tanggal_periksa='" + TRTglPoli.getText() + "'")
                        >= Sequel.cariInteger("select ifnull(MAX(CONVERT(reg_periksa.no_reg,signed)),0) from reg_periksa where reg_periksa.kd_dokter='" + TRKdDok.getText() + "' and reg_periksa.kd_poli='" + TRKdPoli.getText() + "' and reg_periksa.tgl_registrasi='" + TRTglPoli.getText() + "'")) {
                    Valid.autoNomer3("select ifnull(MAX(CONVERT(booking_registrasi.no_reg,signed)),0) from booking_registrasi where booking_registrasi.kd_dokter='" + TRKdDok.getText() + "' and booking_registrasi.kd_poli='" + TRKdPoli.getText() + "' and booking_registrasi.tanggal_periksa='" + TRTglPoli.getText() + "'", "", 3, TRNoReg);
                } else {
                    Valid.autoNomer3("select ifnull(MAX(CONVERT(reg_periksa.no_reg,signed)),0) from reg_periksa where kd_dokter='" + TRKdDok.getText() + "' and reg_periksa.kd_poli='" + TRKdPoli.getText() + "' and reg_periksa.tgl_registrasi='" + TRTglPoli.getText() + "'", "", 3, TRNoReg);
                }
                break;
            default:
                Valid.autoNomer3("select ifnull(MAX(CONVERT(no_reg,signed)),0) from booking_registrasi where kd_dokter='" + TRKdDok.getText() + "' and tanggal_periksa='" + TRTglPoli.getText() + "'", "", 3, TRNoReg);
                break;
        }
    }

    private void jam() {
        ActionListener taskPerformer = (ActionEvent e) -> {
            if (aktif == true) {
                nol_detik = "";
                Date now = Calendar.getInstance().getTime();
                nilai_detik = now.getSeconds();
                if (nilai_detik <= 9) {
                    nol_detik = "0";
                }

                detik = nol_detik + Integer.toString(nilai_detik);
                if (detik.equals("05")) {
                    bookingbaru = Sequel.cariInteger("select count(*) from booking_periksa where status='Belum Dibalas'");
                    if (bookingbaru > 0) {
                        try {
                            music = new BackgroundMusic("./suara/alarm.mp3");
                            music.start();
                        } catch (Exception ex) {
                            System.out.println(ex);
                        }

//                        if (WindowBalas.isVisible() == false) {
//                            i = JOptionPane.showConfirmDialog(null, "Ada booking periksa baru yang belum dibalas, apa mau ditampilkan????", "Konfirmasi", JOptionPane.YES_NO_OPTION);
//                            if (i == JOptionPane.YES_OPTION) {
//                                R1.setSelected(true);
//                                TCari.setText("");
//                                tampil();
//                            }
//                        }
                    }
                }
            }
        };
        new Timer(1000, taskPerformer).start();
    }

    private String jamN() {
        int nilai_jam;
        int nilai_menit;
        int nilai_detik;
        String nol_jam = "";
        String nol_menit = "";
        String nol_detik = "";
        Date now = Calendar.getInstance().getTime();
        nilai_jam = now.getHours();
        nilai_menit = now.getMinutes();
        nilai_detik = now.getSeconds();

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
        String jam = nol_jam + Integer.toString(nilai_jam);
        String menit = nol_menit + Integer.toString(nilai_menit);
        String detik = nol_detik + Integer.toString(nilai_detik);
        return jam + ":" + menit + ":" + detik;
    }
}
