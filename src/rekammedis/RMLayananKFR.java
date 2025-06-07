/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * DlgDataSkriningGiziLanjut.java
 * Kontribusi Haris Rochmatullah RS Bhayangkara Nganjuk
 * Created on 11 November 2020, 20:19:56
 */
package rekammedis;

import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import simrskhanza.DlgCariDiagnosaICD10;
import simrskhanza.DlgCariDiagnosaICD9;

/**
 *
 * @author perpustakaan
 */
public final class RMLayananKFR extends javax.swing.JDialog {

    private final DefaultTableModel tabMode;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps, ps4;
    private ResultSet rs;
    private int i = 0;
    public DlgCariDiagnosaICD10 diagnosa = new DlgCariDiagnosaICD10(null, false);
    public DlgCariDiagnosaICD10 diagnosa2 = new DlgCariDiagnosaICD10(null, false);
    public DlgCariDiagnosaICD9 diagnosa3 = new DlgCariDiagnosaICD9(null, false);
    private DlgCariDokter dokter = new DlgCariDokter(null, false);
    private String finger = "";

    /**
     * Creates new form DlgRujuk
     *
     * @param parent
     * @param modal
     */
    public RMLayananKFR(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8, 1);
        setSize(628, 674);

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.R.M.", "Nama Pasien", "Umur", "JK", "Tgl.Lahir", "Tanggal Uji", "Anamesa", "Pem Fisik dan Uji Fungsi", "Dx Medis", "Dx Fungsi", "Pem Penunjang", "Tata Laksana", "Anjuran", "Evaluasi", "Susp Penyakit", "Nama Dokter"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbObat.setModel(tabMode);

        //tbObat.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbObat.getBackground()));
        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 17; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(100);
            } else if (i == 1) {
                column.setPreferredWidth(65);
            } else if (i == 2) {
                column.setPreferredWidth(160);
            } else if (i == 3) {
                column.setPreferredWidth(35);
            } else if (i == 4) {
                column.setPreferredWidth(20);
            } else if (i == 5) {
                column.setPreferredWidth(65);
            } else if (i == 6) {
                column.setPreferredWidth(120);
            } else if (i == 7) {
                column.setPreferredWidth(150);
            } else if (i == 8) {
                column.setPreferredWidth(150);
            } else if (i == 9) {
                column.setPreferredWidth(150);
            } else if (i == 10) {
                column.setPreferredWidth(150);
            } else if (i == 11) {
                column.setPreferredWidth(150);
            } else if (i == 12) {
                column.setPreferredWidth(80);
            } else if (i == 13) {
                column.setPreferredWidth(140);
            } else if (i == 14) {
                column.setPreferredWidth(140);
            } else if (i == 15) {
                column.setPreferredWidth(140);
            } else if (i == 16) {
                column.setPreferredWidth(140);
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRw.setDocument(new batasInput((byte) 17).getKata(TNoRw));
        KdDokter.setDocument(new batasInput((byte) 20).getKata(KdDokter));
        Anamesa.setDocument(new batasInput((int) 100).getKata(Anamesa));
        PemFisikUji.setDocument(new batasInput((int) 100).getKata(PemFisikUji));
        TataLaksana.setDocument(new batasInput((int) 200).getKata(TataLaksana));
        Anjuran.setDocument(new batasInput((int) 200).getKata(Anjuran));
        Evaluasi.setDocument(new batasInput((int) 100).getKata(Evaluasi));

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

        dokter.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (dokter.getTable().getSelectedRow() != -1) {
                    KdDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 0).toString());
                    NmDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString());
                }
                KdDokter.requestFocus();
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

        diagnosa.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {

                if (diagnosa.getTable().getSelectedRow() != -1) {
                    DxMedis.setText(diagnosa.getTable().getValueAt(diagnosa.getTable().getSelectedRow(), 0).toString() + " " + diagnosa.getTable().getValueAt(diagnosa.getTable().getSelectedRow(), 1).toString());
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

        diagnosa2.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {

                if (diagnosa2.getTable().getSelectedRow() != -1) {
                    DxFungsi.setText(diagnosa2.getTable().getValueAt(diagnosa2.getTable().getSelectedRow(), 0).toString() + " " + diagnosa2.getTable().getValueAt(diagnosa2.getTable().getSelectedRow(), 1).toString());
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

        diagnosa3.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {

                if (diagnosa3.getTable().getSelectedRow() != -1) {
                    TataLaksana.setText(diagnosa3.getTable().getValueAt(diagnosa3.getTable().getSelectedRow(), 0).toString() + " " + diagnosa3.getTable().getValueAt(diagnosa3.getTable().getSelectedRow(), 1).toString());
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

        ChkInput.setSelected(false);
        isForm();

        jam();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        MnUjiFungsi = new javax.swing.JMenuItem();
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
        jLabel4 = new widget.Label();
        TNoRw = new widget.TextBox();
        TPasien = new widget.TextBox();
        Tanggal = new widget.Tanggal();
        TNoRM = new widget.TextBox();
        jLabel16 = new widget.Label();
        Jam = new widget.ComboBox();
        Menit = new widget.ComboBox();
        Detik = new widget.ComboBox();
        ChkKejadian = new widget.CekBox();
        jLabel18 = new widget.Label();
        KdDokter = new widget.TextBox();
        NmDokter = new widget.TextBox();
        btnPetugas = new widget.Button();
        jLabel8 = new widget.Label();
        TglLahir = new widget.TextBox();
        jLabel12 = new widget.Label();
        Anamesa = new widget.TextBox();
        JK = new widget.TextBox();
        jLabel13 = new widget.Label();
        PemFisikUji = new widget.TextBox();
        jLabel15 = new widget.Label();
        jLabel17 = new widget.Label();
        jLabel20 = new widget.Label();
        scrollPane1 = new widget.ScrollPane();
        TataLaksana = new widget.TextArea();
        scrollPane2 = new widget.ScrollPane();
        Anjuran = new widget.TextArea();
        scrollPane3 = new widget.ScrollPane();
        Evaluasi = new widget.TextArea();
        DxMedis = new widget.TextBox();
        jLabel22 = new widget.Label();
        jLabel23 = new widget.Label();
        SuspKet = new widget.TextBox();
        jLabel14 = new widget.Label();
        PemPenunjang = new widget.TextBox();
        SuspPeny = new widget.ComboBox();
        jLabel24 = new widget.Label();
        DxFungsi = new widget.TextBox();
        btnDiagnosa = new widget.Button();
        btnDiagnosa1 = new widget.Button();
        btnDiagnosa2 = new widget.Button();
        BtnUjiKFRGet3 = new widget.Button();
        ChkInput = new widget.CekBox();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnUjiFungsi.setBackground(new java.awt.Color(255, 255, 254));
        MnUjiFungsi.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnUjiFungsi.setForeground(new java.awt.Color(50, 50, 50));
        MnUjiFungsi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnUjiFungsi.setText("Formulir/Lembar Uji Fungsi/Prosedur KFR");
        MnUjiFungsi.setName("MnUjiFungsi"); // NOI18N
        MnUjiFungsi.setPreferredSize(new java.awt.Dimension(270, 26));
        MnUjiFungsi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnUjiFungsiActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnUjiFungsi);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Layanan KFR ]::", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);
        Scroll.setPreferredSize(new java.awt.Dimension(452, 200));

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

        jLabel19.setText("Tanggal :");
        jLabel19.setName("jLabel19"); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(55, 23));
        panelGlass9.add(jLabel19);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "31-05-2025" }));
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
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "31-05-2025" }));
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
        TCari.setPreferredSize(new java.awt.Dimension(320, 23));
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
        PanelInput.setPreferredSize(new java.awt.Dimension(250, 300));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput.setBackground(new java.awt.Color(250, 255, 245));
        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(100, 225));
        FormInput.setLayout(null);

        jLabel4.setText("No.Rawat :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(0, 10, 75, 23);

        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw"); // NOI18N
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });
        FormInput.add(TNoRw);
        TNoRw.setBounds(79, 10, 141, 23);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N
        TPasien.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TPasienKeyPressed(evt);
            }
        });
        FormInput.add(TPasien);
        TPasien.setBounds(336, 10, 240, 23);

        Tanggal.setForeground(new java.awt.Color(50, 70, 50));
        Tanggal.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "31-05-2025" }));
        Tanggal.setDisplayFormat("dd-MM-yyyy");
        Tanggal.setName("Tanggal"); // NOI18N
        Tanggal.setOpaque(false);
        Tanggal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalKeyPressed(evt);
            }
        });
        FormInput.add(Tanggal);
        Tanggal.setBounds(79, 40, 90, 23);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM"); // NOI18N
        TNoRM.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRMKeyPressed(evt);
            }
        });
        FormInput.add(TNoRM);
        TNoRM.setBounds(222, 10, 112, 23);

        jLabel16.setText("Tanggal :");
        jLabel16.setName("jLabel16"); // NOI18N
        jLabel16.setVerifyInputWhenFocusTarget(false);
        FormInput.add(jLabel16);
        jLabel16.setBounds(0, 40, 75, 23);

        Jam.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        Jam.setName("Jam"); // NOI18N
        Jam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JamKeyPressed(evt);
            }
        });
        FormInput.add(Jam);
        Jam.setBounds(173, 40, 62, 23);

        Menit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        Menit.setName("Menit"); // NOI18N
        Menit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MenitKeyPressed(evt);
            }
        });
        FormInput.add(Menit);
        Menit.setBounds(238, 40, 62, 23);

        Detik.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        Detik.setName("Detik"); // NOI18N
        Detik.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DetikKeyPressed(evt);
            }
        });
        FormInput.add(Detik);
        Detik.setBounds(303, 40, 62, 23);

        ChkKejadian.setBorder(null);
        ChkKejadian.setSelected(true);
        ChkKejadian.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkKejadian.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkKejadian.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkKejadian.setName("ChkKejadian"); // NOI18N
        FormInput.add(ChkKejadian);
        ChkKejadian.setBounds(368, 40, 23, 23);

        jLabel18.setText("Dokter KFR :");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(400, 40, 80, 23);

        KdDokter.setEditable(false);
        KdDokter.setHighlighter(null);
        KdDokter.setName("KdDokter"); // NOI18N
        KdDokter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdDokterKeyPressed(evt);
            }
        });
        FormInput.add(KdDokter);
        KdDokter.setBounds(484, 40, 94, 23);

        NmDokter.setEditable(false);
        NmDokter.setName("NmDokter"); // NOI18N
        FormInput.add(NmDokter);
        NmDokter.setBounds(587, 40, 220, 23);

        btnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnPetugas.setMnemonic('2');
        btnPetugas.setToolTipText("ALt+2");
        btnPetugas.setName("btnPetugas"); // NOI18N
        btnPetugas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPetugasActionPerformed(evt);
            }
        });
        btnPetugas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnPetugasKeyPressed(evt);
            }
        });
        FormInput.add(btnPetugas);
        btnPetugas.setBounds(810, 40, 28, 23);

        jLabel8.setText("Tgl.Lahir :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(625, 10, 60, 23);

        TglLahir.setHighlighter(null);
        TglLahir.setName("TglLahir"); // NOI18N
        FormInput.add(TglLahir);
        TglLahir.setBounds(689, 10, 150, 23);

        jLabel12.setText("Anamesa : ");
        jLabel12.setName("jLabel12"); // NOI18N
        FormInput.add(jLabel12);
        jLabel12.setBounds(10, 70, 125, 23);

        Anamesa.setFocusTraversalPolicyProvider(true);
        Anamesa.setName("Anamesa"); // NOI18N
        Anamesa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                AnamesaKeyPressed(evt);
            }
        });
        FormInput.add(Anamesa);
        Anamesa.setBounds(139, 70, 330, 23);

        JK.setEditable(false);
        JK.setHighlighter(null);
        JK.setName("JK"); // NOI18N
        JK.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JKKeyPressed(evt);
            }
        });
        FormInput.add(JK);
        JK.setBounds(578, 10, 35, 23);

        jLabel13.setText("Pem Fisik & Uji Fungsi : ");
        jLabel13.setName("jLabel13"); // NOI18N
        FormInput.add(jLabel13);
        jLabel13.setBounds(490, 70, 112, 23);

        PemFisikUji.setFocusTraversalPolicyProvider(true);
        PemFisikUji.setName("PemFisikUji"); // NOI18N
        PemFisikUji.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PemFisikUjiKeyPressed(evt);
            }
        });
        FormInput.add(PemFisikUji);
        PemFisikUji.setBounds(620, 70, 230, 23);

        jLabel15.setText("TataLaksana KFR");
        jLabel15.setName("jLabel15"); // NOI18N
        FormInput.add(jLabel15);
        jLabel15.setBounds(0, 160, 114, 23);

        jLabel17.setText("Anjuran");
        jLabel17.setName("jLabel17"); // NOI18N
        FormInput.add(jLabel17);
        jLabel17.setBounds(30, 220, 78, 23);

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        jLabel20.setText("Susp Penyakit Akibat Kerja");
        jLabel20.setHorizontalTextPosition(javax.swing.SwingConstants.TRAILING);
        jLabel20.setName("jLabel20"); // NOI18N
        FormInput.add(jLabel20);
        jLabel20.setBounds(460, 220, 160, 20);

        scrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane1.setName("scrollPane1"); // NOI18N

        TataLaksana.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        TataLaksana.setColumns(20);
        TataLaksana.setRows(5);
        TataLaksana.setName("TataLaksana"); // NOI18N
        TataLaksana.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TataLaksanaKeyPressed(evt);
            }
        });
        scrollPane1.setViewportView(TataLaksana);

        FormInput.add(scrollPane1);
        scrollPane1.setBounds(120, 160, 330, 50);

        scrollPane2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane2.setName("scrollPane2"); // NOI18N

        Anjuran.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        Anjuran.setColumns(20);
        Anjuran.setRows(5);
        Anjuran.setName("Anjuran"); // NOI18N
        Anjuran.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                AnjuranKeyPressed(evt);
            }
        });
        scrollPane2.setViewportView(Anjuran);

        FormInput.add(scrollPane2);
        scrollPane2.setBounds(120, 220, 330, 40);

        scrollPane3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane3.setName("scrollPane3"); // NOI18N

        Evaluasi.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        Evaluasi.setColumns(20);
        Evaluasi.setRows(5);
        Evaluasi.setName("Evaluasi"); // NOI18N
        Evaluasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                EvaluasiKeyPressed(evt);
            }
        });
        scrollPane3.setViewportView(Evaluasi);

        FormInput.add(scrollPane3);
        scrollPane3.setBounds(520, 160, 290, 50);

        DxMedis.setFocusTraversalPolicyProvider(true);
        DxMedis.setName("DxMedis"); // NOI18N
        DxMedis.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DxMedisKeyPressed(evt);
            }
        });
        FormInput.add(DxMedis);
        DxMedis.setBounds(140, 100, 260, 23);

        jLabel22.setText("Diagnosa Medis");
        jLabel22.setName("jLabel22"); // NOI18N
        FormInput.add(jLabel22);
        jLabel22.setBounds(40, 100, 90, 23);

        jLabel23.setText("Diagnosa Fungsi : ");
        jLabel23.setName("jLabel23"); // NOI18N
        FormInput.add(jLabel23);
        jLabel23.setBounds(480, 100, 100, 23);

        SuspKet.setEditable(false);
        SuspKet.setFocusTraversalPolicyProvider(true);
        SuspKet.setName("SuspKet"); // NOI18N
        SuspKet.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SuspKetKeyPressed(evt);
            }
        });
        FormInput.add(SuspKet);
        SuspKet.setBounds(710, 220, 100, 23);

        jLabel14.setText("Pemeriksaan Penunjang");
        jLabel14.setName("jLabel14"); // NOI18N
        FormInput.add(jLabel14);
        jLabel14.setBounds(0, 130, 140, 23);

        PemPenunjang.setFocusTraversalPolicyProvider(true);
        PemPenunjang.setName("PemPenunjang"); // NOI18N
        PemPenunjang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PemPenunjangKeyPressed(evt);
            }
        });
        FormInput.add(PemPenunjang);
        PemPenunjang.setBounds(140, 130, 660, 23);

        SuspPeny.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ya", "Tidak" }));
        SuspPeny.setSelectedIndex(1);
        SuspPeny.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        SuspPeny.setName("SuspPeny"); // NOI18N
        SuspPeny.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                SuspPenyItemStateChanged(evt);
            }
        });
        SuspPeny.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SuspPenyKeyPressed(evt);
            }
        });
        FormInput.add(SuspPeny);
        SuspPeny.setBounds(630, 220, 70, 20);

        jLabel24.setText("Evaluasi");
        jLabel24.setName("jLabel24"); // NOI18N
        FormInput.add(jLabel24);
        jLabel24.setBounds(460, 160, 60, 23);

        DxFungsi.setFocusTraversalPolicyProvider(true);
        DxFungsi.setName("DxFungsi"); // NOI18N
        DxFungsi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DxFungsiKeyPressed(evt);
            }
        });
        FormInput.add(DxFungsi);
        DxFungsi.setBounds(580, 100, 220, 23);

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
        btnDiagnosa.setBounds(410, 100, 28, 23);

        btnDiagnosa1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnDiagnosa1.setMnemonic('3');
        btnDiagnosa1.setToolTipText("Alt+3");
        btnDiagnosa1.setName("btnDiagnosa1"); // NOI18N
        btnDiagnosa1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDiagnosa1ActionPerformed(evt);
            }
        });
        btnDiagnosa1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnDiagnosa1KeyPressed(evt);
            }
        });
        FormInput.add(btnDiagnosa1);
        btnDiagnosa1.setBounds(810, 100, 28, 23);

        btnDiagnosa2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnDiagnosa2.setMnemonic('3');
        btnDiagnosa2.setToolTipText("Alt+3");
        btnDiagnosa2.setName("btnDiagnosa2"); // NOI18N
        btnDiagnosa2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDiagnosa2ActionPerformed(evt);
            }
        });
        btnDiagnosa2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnDiagnosa2KeyPressed(evt);
            }
        });
        FormInput.add(btnDiagnosa2);
        btnDiagnosa2.setBounds(90, 180, 28, 23);

        BtnUjiKFRGet3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/download.png"))); // NOI18N
        BtnUjiKFRGet3.setMnemonic('K');
        BtnUjiKFRGet3.setText("Pemeriksaan Terakhir");
        BtnUjiKFRGet3.setToolTipText("Alt+K");
        BtnUjiKFRGet3.setGlassColor(new java.awt.Color(255, 51, 51));
        BtnUjiKFRGet3.setName("BtnUjiKFRGet3"); // NOI18N
        BtnUjiKFRGet3.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnUjiKFRGet3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnUjiKFRGet3ActionPerformed(evt);
            }
        });
        BtnUjiKFRGet3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnUjiKFRGet3KeyPressed(evt);
            }
        });
        FormInput.add(BtnUjiKFRGet3);
        BtnUjiKFRGet3.setBounds(850, 10, 240, 20);

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

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRwKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            isRawat();
            isPsien();
        } else {
            Valid.pindah(evt, TCari, Tanggal);
        }
}//GEN-LAST:event_TNoRwKeyPressed

    private void TPasienKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TPasienKeyPressed
        Valid.pindah(evt, TCari, BtnSimpan);
}//GEN-LAST:event_TPasienKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (TNoRw.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "Pasien");
        } else if (KdDokter.getText().trim().equals("") || NmDokter.getText().trim().equals("")) {
            Valid.textKosong(KdDokter, "Dokter");
        } else if (Anamesa.getText().trim().equals("")) {
            Valid.textKosong(Anamesa, "Anamesa");
        } else if (PemFisikUji.getText().trim().equals("")) {
            Valid.textKosong(PemFisikUji, "Pemeriksaan Fisik dan Uji Fungsi");
        } else if (TataLaksana.getText().trim().equals("")) {
            Valid.textKosong(TataLaksana, "Tata Laksana");
        } else if (Anjuran.getText().trim().equals("")) {
            Valid.textKosong(Anjuran, "Anjuran");
        } else if (Evaluasi.getText().trim().equals("")) {
            Valid.textKosong(Evaluasi, "Evaluasi");
        } else {
            if (Sequel.menyimpantf("layanankfr", "?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 13, new String[]{
                TNoRw.getText(), Valid.SetTgl(Tanggal.getSelectedItem() + "") + " " + Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
                Anamesa.getText(), PemFisikUji.getText(), DxMedis.getText(), DxFungsi.getText(), PemPenunjang.getText(), TataLaksana.getText(), Anjuran.getText(), Evaluasi.getText(), SuspPeny.getSelectedItem().toString(), SuspKet.getText(), KdDokter.getText()
            }) == true) {
                tampil();
                emptTeks();
            }
        }
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnSimpanActionPerformed(null);
        } else {
            //Valid.pindah(evt,cmbSkor3,BtnBatal);
        }
}//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        emptTeks();
        ChkInput.setSelected(true);
        isForm();
}//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBatalKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            emptTeks();
        } else {
            Valid.pindah(evt, BtnSimpan, BtnHapus);
        }
}//GEN-LAST:event_BtnBatalKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if (tbObat.getSelectedRow() != -1) {
            if (akses.getkode().equals("Admin Utama")) {
                hapus();
            } else {
                if (KdDokter.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(), 12).toString())) {
                    hapus();
                } else {
                    JOptionPane.showMessageDialog(rootPane, "Hanya bisa dihapus oleh dokter yang bersangkutan..!!");
                }
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan anda pilih data terlebih dahulu..!!");
        }

}//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnHapusActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnBatal, BtnEdit);
        }
}//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed
        if (TNoRw.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "Pasien");
        } else if (KdDokter.getText().trim().equals("") || NmDokter.getText().trim().equals("")) {
            Valid.textKosong(KdDokter, "Dokter");
        } else if (Anamesa.getText().trim().equals("")) {
            Valid.textKosong(Anamesa, "Diagnosis Fungsional");
        } else if (PemFisikUji.getText().trim().equals("")) {
            Valid.textKosong(PemFisikUji, "Diagnosis Medis");
        } else if (TataLaksana.getText().trim().equals("")) {
            Valid.textKosong(TataLaksana, "Hasil Yang Didapat");
        } else if (Anjuran.getText().trim().equals("")) {
            Valid.textKosong(Anjuran, "Kesimpulan");
        } else if (Evaluasi.getText().trim().equals("")) {
            Valid.textKosong(Evaluasi, "Rekomendasi");
        } else {
            if (tbObat.getSelectedRow() > -1) {
                if (akses.getkode().equals("Admin Utama")) {
                    ganti();
                } else {
                    if (KdDokter.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(), 12).toString())) {
                        ganti();
                    } else {
                        JOptionPane.showMessageDialog(rootPane, "Hanya bisa diganti oleh dokter yang bersangkutan..!!");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(rootPane, "Silahkan anda pilih data terlebih dahulu..!!");
            }
        }
}//GEN-LAST:event_BtnEditActionPerformed

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEditKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnEditActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnHapus, BtnPrint);
        }
}//GEN-LAST:event_BtnEditKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dokter.dispose();
        dispose();
}//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnKeluarActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnEdit, TCari);
        }
}//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        JOptionPane.showMessageDialog(rootPane, "Belum dibuat gaes, maap");
//        if (tabMode.getRowCount() == 0) {
//            JOptionPane.showMessageDialog(rootPane, "Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
//            BtnBatal.requestFocus();
//        } else if (tabMode.getRowCount() != 0) {
//            Map<String, Object> param = new HashMap<>();
//            param.put("namars", akses.getnamars());
//            param.put("alamatrs", akses.getalamatrs());
//            param.put("kotars", akses.getkabupatenrs());
//            param.put("propinsirs", akses.getpropinsirs());
//            param.put("kontakrs", akses.getkontakrs());
//            param.put("emailrs", akses.getemailrs());
//            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
//
//            if (TCari.getText().trim().equals("")) {
//                Valid.MyReportqry("rptUjiFungsiKFR.jasper", "report", "::[ Data Uji Fugsi/Prosedur KFR ]::",
//                        "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,reg_periksa.umurdaftar,reg_periksa.sttsumur,"
//                        + "pasien.jk,uji_fungsi_kfr.tanggal,uji_fungsi_kfr.diagnosis_fungsional,uji_fungsi_kfr.diagnosis_medis,uji_fungsi_kfr.hasil_didapat,"
//                        + "uji_fungsi_kfr.kesimpulan,uji_fungsi_kfr.rekomedasi,uji_fungsi_kfr.kd_dokter,dokter.nm_dokter,date_format(pasien.tgl_lahir,'%d-%m-%Y') as lahir "
//                        + "from uji_fungsi_kfr inner join reg_periksa on uji_fungsi_kfr.no_rawat=reg_periksa.no_rawat "
//                        + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
//                        + "inner join dokter on uji_fungsi_kfr.kd_dokter=dokter.kd_dokter where "
//                        + "uji_fungsi_kfr.tanggal between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59' order by uji_fungsi_kfr.tanggal ", param);
//            } else {
//                Valid.MyReportqry("rptUjiFungsiKFR.jasper", "report", "::[ Data Uji Fugsi/Prosedur KFR ]::",
//                        "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,reg_periksa.umurdaftar,reg_periksa.sttsumur,"
//                        + "pasien.jk,uji_fungsi_kfr.tanggal,uji_fungsi_kfr.diagnosis_fungsional,uji_fungsi_kfr.diagnosis_medis,uji_fungsi_kfr.hasil_didapat,"
//                        + "uji_fungsi_kfr.kesimpulan,uji_fungsi_kfr.rekomedasi,uji_fungsi_kfr.kd_dokter,dokter.nm_dokter,date_format(pasien.tgl_lahir,'%d-%m-%Y') as lahir "
//                        + "from uji_fungsi_kfr inner join reg_periksa on uji_fungsi_kfr.no_rawat=reg_periksa.no_rawat "
//                        + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
//                        + "inner join dokter on uji_fungsi_kfr.kd_dokter=dokter.kd_dokter "
//                        + "where uji_fungsi_kfr.tanggal between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59' and "
//                        + "(reg_periksa.no_rawat like '%" + TCari.getText().trim() + "%' or pasien.no_rkm_medis like '%" + TCari.getText().trim() + "%' or pasien.nm_pasien like '%" + TCari.getText().trim() + "%' or "
//                        + "uji_fungsi_kfr.kd_dokter like '%" + TCari.getText().trim() + "%' or dokter.nm_dokter like '%" + TCari.getText().trim() + "%') order by uji_fungsi_kfr.tanggal ", param);
//            }
//        }
        this.setCursor(Cursor.getDefaultCursor());
}//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnPrintActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnEdit, BtnKeluar);
        }
}//GEN-LAST:event_BtnPrintKeyPressed

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

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        tampil();
}//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            tampil();
            TCari.setText("");
        } else {
            Valid.pindah(evt, BtnCari, TPasien);
        }
}//GEN-LAST:event_BtnAllKeyPressed

    private void TanggalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TanggalKeyPressed
        Valid.pindah(evt, TCari, Jam);
}//GEN-LAST:event_TanggalKeyPressed

    private void TNoRMKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRMKeyPressed
        // Valid.pindah(evt, TNm, BtnSimpan);
}//GEN-LAST:event_TNoRMKeyPressed

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbObatMouseClicked
        if (tabMode.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
}//GEN-LAST:event_tbObatMouseClicked

    private void tbObatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbObatKeyPressed
        if (tabMode.getRowCount() != 0) {
            if ((evt.getKeyCode() == KeyEvent.VK_ENTER) || (evt.getKeyCode() == KeyEvent.VK_UP) || (evt.getKeyCode() == KeyEvent.VK_DOWN)) {
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

    private void JamKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_JamKeyPressed
        Valid.pindah(evt, Tanggal, Menit);
    }//GEN-LAST:event_JamKeyPressed

    private void MenitKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MenitKeyPressed
        Valid.pindah(evt, Jam, Detik);
    }//GEN-LAST:event_MenitKeyPressed

    private void DetikKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DetikKeyPressed
        Valid.pindah(evt, Menit, btnPetugas);
    }//GEN-LAST:event_DetikKeyPressed

    private void KdDokterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdDokterKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            Sequel.cariIsi("select dokter.nm_dokter from dokter where dokter.kd_dokter=?", NmDokter, KdDokter.getText());
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            Detik.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            //Alergi.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_UP) {
            btnPetugasActionPerformed(null);
        }
    }//GEN-LAST:event_KdDokterKeyPressed

    private void btnPetugasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPetugasActionPerformed
        dokter.emptTeks();
        dokter.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setVisible(true);
    }//GEN-LAST:event_btnPetugasActionPerformed

    private void btnPetugasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnPetugasKeyPressed
        Valid.pindah(evt, Detik, Anamesa);
    }//GEN-LAST:event_btnPetugasKeyPressed

    private void MnUjiFungsiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnUjiFungsiActionPerformed
        if (tbObat.getSelectedRow() > -1) {
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", tbObat.getValueAt(tbObat.getSelectedRow(), 12).toString());
            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + tbObat.getValueAt(tbObat.getSelectedRow(), 16).toString() + "\nID " + (finger.equals("") ? tbObat.getValueAt(tbObat.getSelectedRow(), 12).toString() : finger) + "\n" + Tanggal.getSelectedItem());
            Valid.MyReportqry("rptCetakLayananKFR.jasper", "report", "::[ Form Layanan KFR ]::",
                    "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,reg_periksa.umurdaftar,reg_periksa.sttsumur,"
                    + "pasien.jk,layanankfr.tanggal,layanankfr.anamesa,layanankfr.pemfisik_ujifungsi,layanankfr.dx_medis,"
                    + "layanankfr.dx_fungsi,layanankfr.pem_penunjang,layanankfr.tatalaksana_kfr,layanankfr.anjuran,layanankfr.evaluasi,layanankfr.suspek_kerja,layanankfr.ketsuspek,layanankfr.kd_dokter,dokter.nm_dokter,date_format(pasien.tgl_lahir,'%d-%m-%Y') as lahir,date_format(layanankfr.tanggal,'%d-%m-%Y') as periksa "
                    + "from layanankfr inner join reg_periksa on layanankfr.no_rawat=reg_periksa.no_rawat "
                    + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                    + "inner join dokter on layanankfr.kd_dokter=dokter.kd_dokter where reg_periksa.no_rawat='" + tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString() + "'", param);
        }
    }//GEN-LAST:event_MnUjiFungsiActionPerformed

    public void LayananKFRPdf(String norawat, String norm) {

        Map<String, Object> param = new HashMap<>();
        param.put("namars", akses.getnamars());
        param.put("alamatrs", akses.getalamatrs());
        param.put("kotars", akses.getkabupatenrs());
        param.put("propinsirs", akses.getpropinsirs());
        param.put("kontakrs", akses.getkontakrs());
        param.put("emailrs", akses.getemailrs());
        param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
//            finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", tbObat.getValueAt(tbObat.getSelectedRow(), 12).toString());
//            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + tbObat.getValueAt(tbObat.getSelectedRow(), 16).toString() + "\nID " + (finger.equals("") ? tbObat.getValueAt(tbObat.getSelectedRow(), 12).toString() : finger) + "\n" + Tanggal.getSelectedItem());
//        Valid.ReportKompilasiBerkas("rptCetakLayananKFR.jasper", "report", "::[ Form Layanan KFR ]::",
//                "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,reg_periksa.umurdaftar,reg_periksa.sttsumur,"
//                + "pasien.jk,layanankfr.tanggal,layanankfr.anamesa,layanankfr.pemfisik_ujifungsi,layanankfr.dx_medis,"
//                + "layanankfr.dx_fungsi,layanankfr.pem_penunjang,layanankfr.tatalaksana_kfr,layanankfr.anjuran,layanankfr.evaluasi,layanankfr.suspek_kerja,layanankfr.ketsuspek,layanankfr.kd_dokter,dokter.nm_dokter,date_format(pasien.tgl_lahir,'%d-%m-%Y') as lahir,date_format(layanankfr.tanggal,'%d-%m-%Y') as periksa "
//                + "from layanankfr inner join reg_periksa on layanankfr.no_rawat=reg_periksa.no_rawat "
//                + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
//                + "inner join dokter on layanankfr.kd_dokter=dokter.kd_dokter where reg_periksa.no_rawat='" + norawat + "'", param, norawat, norm, "PROGRAMKFR");

    }

    public void LayananKFRPdfKlaim(String norawat, String norm) {
        String kodedokter = Sequel.cariIsi("SELECT\n"
                + "	layanankfr.kd_dokter\n"
                + "FROM\n"
                + "	layanankfr\n"
                + "	INNER JOIN\n"
                + "	dokter\n"
                + "	ON \n"
                + "		layanankfr.kd_dokter = dokter.kd_dokter where layanankfr.no_rawat='" + norawat + "'");
        String namadokter = Sequel.cariIsi("SELECT\n"
                + "	dokter.nm_dokter\n"
                + "FROM\n"
                + "	layanankfr\n"
                + "	INNER JOIN\n"
                + "	dokter\n"
                + "	ON \n"
                + "		layanankfr.kd_dokter = dokter.kd_dokter where layanankfr.no_rawat='" + norawat + "'");
        String tanggal = Sequel.cariIsi("SELECT\n"
                + "	layanankfr.tanggal\n"
                + "FROM\n"
                + "	layanankfr\n"
                + "	where layanankfr.no_rawat='" + norawat + "'");
        Map<String, Object> param = new HashMap<>();
        param.put("namars", akses.getnamars());
        param.put("alamatrs", akses.getalamatrs());
        param.put("kotars", akses.getkabupatenrs());
        param.put("propinsirs", akses.getpropinsirs());
        param.put("kontakrs", akses.getkontakrs());
        param.put("emailrs", akses.getemailrs());
        param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
        finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", kodedokter);
        param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + namadokter + "\nID " + (finger.equals("") ? kodedokter : finger) + "\n" + tanggal);
        Valid.MyReportqry("rptCetakLayananKFR.jasper", "report", "::[ Form Layanan KFR ]::",
                "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,reg_periksa.umurdaftar,reg_periksa.sttsumur,"
                + "pasien.jk,layanankfr.tanggal,layanankfr.anamesa,layanankfr.pemfisik_ujifungsi,layanankfr.dx_medis,"
                + "layanankfr.dx_fungsi,layanankfr.pem_penunjang,layanankfr.tatalaksana_kfr,layanankfr.anjuran,layanankfr.evaluasi,layanankfr.suspek_kerja,layanankfr.ketsuspek,layanankfr.kd_dokter,dokter.nm_dokter,date_format(pasien.tgl_lahir,'%d-%m-%Y') as lahir,date_format(layanankfr.tanggal,'%d-%m-%Y') as periksa "
                + "from layanankfr inner join reg_periksa on layanankfr.no_rawat=reg_periksa.no_rawat "
                + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                + "inner join dokter on layanankfr.kd_dokter=dokter.kd_dokter where reg_periksa.no_rawat='" + norawat + "'", param);

    }

    public void LayananKFRPdfKlaimGabung(String norawat, String norm, String norawatuntuknamafile) {
        String kodedokter = Sequel.cariIsi("SELECT\n"
                + "	layanankfr.kd_dokter\n"
                + "FROM\n"
                + "	layanankfr\n"
                + "	INNER JOIN\n"
                + "	dokter\n"
                + "	ON \n"
                + "		layanankfr.kd_dokter = dokter.kd_dokter where layanankfr.no_rawat='" + norawat + "'");
        String namadokter = Sequel.cariIsi("SELECT\n"
                + "	dokter.nm_dokter\n"
                + "FROM\n"
                + "	layanankfr\n"
                + "	INNER JOIN\n"
                + "	dokter\n"
                + "	ON \n"
                + "		layanankfr.kd_dokter = dokter.kd_dokter where layanankfr.no_rawat='" + norawat + "'");
        String tanggal = Sequel.cariIsi("SELECT\n"
                + "	layanankfr.tanggal\n"
                + "FROM\n"
                + "	layanankfr\n"
                + "	where layanankfr.no_rawat='" + norawat + "'");
        Map<String, Object> param = new HashMap<>();
        param.put("namars", akses.getnamars());
        param.put("alamatrs", akses.getalamatrs());
        param.put("kotars", akses.getkabupatenrs());
        param.put("propinsirs", akses.getpropinsirs());
        param.put("kontakrs", akses.getkontakrs());
        param.put("emailrs", akses.getemailrs());
        param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
        finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", kodedokter);
        param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + namadokter + "\nID " + (finger.equals("") ? kodedokter : finger) + "\n" + tanggal);
//        Valid.MyReportqrypdfKlaim("rptCetakLayananKFR.jasper", "report", "5LAYKFR",
//                "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,reg_periksa.umurdaftar,reg_periksa.sttsumur,"
//                + "pasien.jk,layanankfr.tanggal,layanankfr.anamesa,layanankfr.pemfisik_ujifungsi,layanankfr.dx_medis,"
//                + "layanankfr.dx_fungsi,layanankfr.pem_penunjang,layanankfr.tatalaksana_kfr,layanankfr.anjuran,layanankfr.evaluasi,layanankfr.suspek_kerja,layanankfr.ketsuspek,layanankfr.kd_dokter,dokter.nm_dokter,date_format(pasien.tgl_lahir,'%d-%m-%Y') as lahir,date_format(layanankfr.tanggal,'%d-%m-%Y') as periksa "
//                + "from layanankfr inner join reg_periksa on layanankfr.no_rawat=reg_periksa.no_rawat "
//                + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
//                + "inner join dokter on layanankfr.kd_dokter=dokter.kd_dokter where reg_periksa.no_rawat='" + norawat + "'", param, "hasilkompilasiklaim", norawatuntuknamafile);

    }

    private void AnamesaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_AnamesaKeyPressed
        Valid.pindah(evt, btnPetugas, PemFisikUji);
    }//GEN-LAST:event_AnamesaKeyPressed

    private void JKKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_JKKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_JKKeyPressed

    private void PemFisikUjiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PemFisikUjiKeyPressed
        Valid.pindah(evt, Anamesa, TataLaksana);
    }//GEN-LAST:event_PemFisikUjiKeyPressed

    private void TataLaksanaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TataLaksanaKeyPressed
        Valid.pindah2(evt, PemFisikUji, Anjuran);
    }//GEN-LAST:event_TataLaksanaKeyPressed

    private void AnjuranKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_AnjuranKeyPressed
        Valid.pindah2(evt, TataLaksana, Evaluasi);
    }//GEN-LAST:event_AnjuranKeyPressed

    private void EvaluasiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_EvaluasiKeyPressed
        Valid.pindah2(evt, Anjuran, BtnSimpan);
    }//GEN-LAST:event_EvaluasiKeyPressed

    private void DxMedisKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DxMedisKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_DxMedisKeyPressed

    private void SuspKetKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SuspKetKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_SuspKetKeyPressed

    private void PemPenunjangKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PemPenunjangKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_PemPenunjangKeyPressed

    private void SuspPenyItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_SuspPenyItemStateChanged
        if (SuspPeny.getSelectedItem().toString().equals("Ya")) {
            SuspKet.setEditable(true);
        }
    }//GEN-LAST:event_SuspPenyItemStateChanged

    private void SuspPenyKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SuspPenyKeyPressed

    }//GEN-LAST:event_SuspPenyKeyPressed

    private void DxFungsiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DxFungsiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_DxFungsiKeyPressed

    private void btnDiagnosaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiagnosaActionPerformed
        if (TNoRw.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, Silahkan anda pilih dulu pasien...!!!");
        } else {
            diagnosa.setSize(internalFrame1.getWidth() - 40, internalFrame1.getHeight() - 40);
            diagnosa.setLocationRelativeTo(internalFrame1);
            diagnosa.isCek();
            diagnosa.setVisible(true);
        }
    }//GEN-LAST:event_btnDiagnosaActionPerformed

    private void btnDiagnosaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnDiagnosaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDiagnosaKeyPressed

    private void btnDiagnosa1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiagnosa1ActionPerformed
        if (TNoRw.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, Silahkan anda pilih dulu pasien...!!!");
        } else {
            diagnosa2.setSize(internalFrame1.getWidth() - 40, internalFrame1.getHeight() - 40);
            diagnosa2.setLocationRelativeTo(internalFrame1);
            diagnosa2.isCek();
            diagnosa2.setVisible(true);
        }
    }//GEN-LAST:event_btnDiagnosa1ActionPerformed

    private void btnDiagnosa1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnDiagnosa1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDiagnosa1KeyPressed

    private void btnDiagnosa2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiagnosa2ActionPerformed
        if (TNoRw.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, Silahkan anda pilih dulu pasien...!!!");
        } else {
            diagnosa3.setSize(internalFrame1.getWidth() - 40, internalFrame1.getHeight() - 40);
            diagnosa3.setLocationRelativeTo(internalFrame1);
            diagnosa3.isCek();
            diagnosa3.setVisible(true);
        }
    }//GEN-LAST:event_btnDiagnosa2ActionPerformed

    private void btnDiagnosa2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnDiagnosa2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDiagnosa2KeyPressed

    private void BtnUjiKFRGet3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnUjiKFRGet3ActionPerformed
        if (TNoRw.getText().equals("") && TNoRM.getText().equals("")) {
            JOptionPane.showMessageDialog(rootPane, "Pasien masih kosong...!!!");
        } else {
            try {
                ps4 = koneksi.prepareStatement("SELECT\n"
                        + "	layanankfr.no_rawat,\n"
                        + "	reg_periksa.no_rkm_medis,\n"
                        + "	pasien.nm_pasien,\n"
                        + "	pegawai.nama,\n"
                        + "	pegawai.jbtn,\n"
                        + "	layanankfr.no_rawat,\n"
                        + "	layanankfr.tanggal,\n"
                        + "	layanankfr.anamesa,\n"
                        + "	layanankfr.pemfisik_ujifungsi,\n"
                        + "	layanankfr.dx_medis,\n"
                        + "	layanankfr.dx_fungsi,\n"
                        + "	layanankfr.pem_penunjang,\n"
                        + "	layanankfr.tatalaksana_kfr,\n"
                        + "	layanankfr.anjuran,\n"
                        + "	layanankfr.evaluasi,\n"
                        + "	layanankfr.suspek_kerja,\n"
                        + "	layanankfr.ketsuspek,\n"
                        + "	layanankfr.kd_dokter \n"
                        + "FROM\n"
                        + "	pasien\n"
                        + "	INNER JOIN reg_periksa ON reg_periksa.no_rkm_medis = pasien.no_rkm_medis\n"
                        + "	INNER JOIN pegawai\n"
                        + "	INNER JOIN layanankfr ON reg_periksa.no_rawat = layanankfr.no_rawat \n"
                        + "	AND reg_periksa.no_rawat = layanankfr.no_rawat \n"
                        + "	AND pegawai.nik = layanankfr.kd_dokter  where layanankfr.kd_dokter='" + akses.getkode() + "' and reg_periksa.no_rkm_medis='" + TNoRM.getText() + "' and reg_periksa.no_rawat!='" + TNoRw.getText() + "'"
                        + "order by layanankfr.tanggal desc LIMIT 1");
                try {
                    rs = ps4.executeQuery();
                    while (rs.next()) {
                        Anamesa.setText(rs.getString("anamesa"));
                        DxMedis.setText(rs.getString("dx_medis"));
                        PemFisikUji.setText(rs.getString("pemfisik_ujifungsi"));
                        DxFungsi.setText(rs.getString("dx_fungsi"));
                        PemPenunjang.setText(rs.getString("pem_penunjang"));
                        TataLaksana.setText(rs.getString("tatalaksana_kfr"));
                        Anjuran.setText(rs.getString("anjuran"));
                        Evaluasi.setText(rs.getString("evaluasi"));

                        String Susp = Sequel.cariIsi("select layanankfr.suspek_kerja from layanankfr where layanankfr.no_rawat='" + tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString() + "'");
                        if (Susp.trim().contains("Tidak")) {
                            SuspPeny.setSelectedIndex(0);
                            SuspKet.setText(Susp.replaceFirst(SuspPeny.getSelectedItem().toString(), "").replaceAll(":", ""));
                        } else if (Susp.trim().contains("Ya")) {
                            SuspPeny.setSelectedIndex(1);
                            SuspKet.setText(Susp.replaceFirst(SuspPeny.getSelectedItem().toString(), "").replaceAll(":", ""));
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Notifikasi gagal tampil: " + e);
                } finally {
                    if (rs != null) {
                        rs.close();
                    }
                    if (ps4 != null) {
                        ps4.close();
                    }
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : " + e);
            }
        }
    }//GEN-LAST:event_BtnUjiKFRGet3ActionPerformed

    private void BtnUjiKFRGet3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnUjiKFRGet3KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnUjiKFRGet3KeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMLayananKFR dialog = new RMLayananKFR(new javax.swing.JFrame(), true);
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
    private widget.TextBox Anamesa;
    private widget.TextArea Anjuran;
    private widget.Button BtnAll;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.Button BtnUjiKFRGet3;
    private widget.CekBox ChkInput;
    private widget.CekBox ChkKejadian;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.ComboBox Detik;
    private widget.TextBox DxFungsi;
    private widget.TextBox DxMedis;
    private widget.TextArea Evaluasi;
    private widget.PanelBiasa FormInput;
    private widget.TextBox JK;
    private widget.ComboBox Jam;
    private widget.TextBox KdDokter;
    private widget.Label LCount;
    private widget.ComboBox Menit;
    private javax.swing.JMenuItem MnUjiFungsi;
    private widget.TextBox NmDokter;
    private javax.swing.JPanel PanelInput;
    private widget.TextBox PemFisikUji;
    private widget.TextBox PemPenunjang;
    private widget.ScrollPane Scroll;
    private widget.TextBox SuspKet;
    private widget.ComboBox SuspPeny;
    private widget.TextBox TCari;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.Tanggal Tanggal;
    private widget.TextArea TataLaksana;
    private widget.TextBox TglLahir;
    private widget.Button btnDiagnosa;
    private widget.Button btnDiagnosa1;
    private widget.Button btnDiagnosa2;
    private widget.Button btnPetugas;
    private widget.InternalFrame internalFrame1;
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
    private widget.Label jLabel22;
    private widget.Label jLabel23;
    private widget.Label jLabel24;
    private widget.Label jLabel4;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.ScrollPane scrollPane1;
    private widget.ScrollPane scrollPane2;
    private widget.ScrollPane scrollPane3;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            if (TCari.getText().toString().trim().equals("")) {
                ps = koneksi.prepareStatement(
                        "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,reg_periksa.umurdaftar,reg_periksa.sttsumur,"
                        + "pasien.jk,layanankfr.tanggal,layanankfr.anamesa,layanankfr.pemfisik_ujifungsi,layanankfr.dx_medis,"
                        + "layanankfr.dx_fungsi,layanankfr.pem_penunjang,layanankfr.tatalaksana_kfr,layanankfr.anjuran,layanankfr.evaluasi,layanankfr.suspek_kerja,layanankfr.ketsuspek,layanankfr.kd_dokter,dokter.nm_dokter,date_format(pasien.tgl_lahir,'%d-%m-%Y') as lahir "
                        + "from layanankfr inner join reg_periksa on layanankfr.no_rawat=reg_periksa.no_rawat "
                        + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                        + "inner join dokter on layanankfr.kd_dokter=dokter.kd_dokter where "
                        + "layanankfr.tanggal between ? and ? order by layanankfr.tanggal ");
            } else {
                ps = koneksi.prepareStatement(
                        "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,reg_periksa.umurdaftar,reg_periksa.sttsumur,"
                        + "pasien.jk,layanankfr.tanggal,layanankfr.anamesa,layanankfr.pemfisik_ujifungsi,layanankfr.dx_medis,"
                        + "layanankfr.dx_fungsi,layanankfr.pem_penunjang,layanankfr.tatalaksana_kfr,layanankfr.anjuran,layanankfr.evaluasi,layanankfr.suspek_kerja,layanankfr.ketsuspek,layanankfr.kd_dokter,dokter.nm_dokter,date_format(pasien.tgl_lahir,'%d-%m-%Y') as lahir "
                        + "from layanankfr inner join reg_periksa on layanankfr.no_rawat=reg_periksa.no_rawat "
                        + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                        + "inner join dokter on layanankfr.kd_dokter=dokter.kd_dokter "
                        + "where layanankfr.tanggal between ? and ? and "
                        + "(reg_periksa.no_rawat like ? or pasien.no_rkm_medis like ? or pasien.nm_pasien like ? or layanankfr.kd_dokter like ? or dokter.nm_dokter like ?) "
                        + "order by layanankfr.tanggal ");
            }

            try {
                if (TCari.getText().toString().trim().equals("")) {
                    ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00");
                    ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");
                } else {
                    ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00");
                    ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");
                    ps.setString(3, "%" + TCari.getText() + "%");
                    ps.setString(4, "%" + TCari.getText() + "%");
                    ps.setString(5, "%" + TCari.getText() + "%");
                    ps.setString(6, "%" + TCari.getText() + "%");
                    ps.setString(7, "%" + TCari.getText() + "%");
                }

                rs = ps.executeQuery();
                while (rs.next()) {
                    tabMode.addRow(new String[]{
                        rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                        rs.getString("umurdaftar") + " " + rs.getString("sttsumur"), rs.getString("jk"),
                        rs.getString("lahir"), rs.getString("tanggal"), rs.getString("anamesa"),
                        rs.getString("pemfisik_ujifungsi"), rs.getString("dx_medis"), rs.getString("dx_fungsi"),
                        rs.getString("pem_penunjang"), rs.getString("tatalaksana_kfr"), rs.getString("anjuran"), rs.getString("evaluasi"), rs.getString("suspek_kerja"), rs.getString("nm_dokter")
                    });
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
        } catch (SQLException e) {
            System.out.println("Notifikasi : " + e);
        }
        LCount.setText("" + tabMode.getRowCount());
    }

    public void emptTeks() {
        Anamesa.setText("");
        PemFisikUji.setText("");
        TataLaksana.setText("");
        Anjuran.setText("");
        Evaluasi.setText("");
        Anamesa.requestFocus();
        PemPenunjang.setText("");
        DxMedis.setText("");
        DxFungsi.setText("");
        SuspPeny.setSelectedIndex(1);
        SuspKet.setText("");

    }

    private void getData() {
        if (tbObat.getSelectedRow() != -1) {
            // "No.Rawat","No.R.M.","Nama Pasien","Umur","JK","Tgl.Lahir","Tanggal Uji","Diagnosis Fugsional","Diagnosis Medis","Hasil Yang Didapat","Kesimpulan","Rekomendasi","Kode Dokter","Nama Dokter"
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
            JK.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString());
            TglLahir.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString());
            Anamesa.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString());
            PemFisikUji.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 8).toString());
            DxMedis.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString());
            DxFungsi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 10).toString());
            PemPenunjang.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 11).toString());
            TataLaksana.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 12).toString());
            Anjuran.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 13).toString());
            Evaluasi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 14).toString());
            SuspPeny.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 15).toString());
            Valid.SetTgl(Tanggal, tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString());
            Jam.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString().substring(11, 13));
            Menit.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString().substring(14, 15));
            Detik.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString().substring(17, 19));
        }
    }

    private void isRawat() {
        Sequel.cariIsi("select reg_periksa.no_rkm_medis from reg_periksa where reg_periksa.no_rawat='" + TNoRw.getText() + "' ", TNoRM);
    }

    private void isPsien() {
        Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis='" + TNoRM.getText() + "' ", TPasien);
        Sequel.cariIsi("select pasien.jk from pasien where pasien.no_rkm_medis='" + TNoRM.getText() + "' ", JK);
        Sequel.cariIsi("select date_format(pasien.tgl_lahir,'%d-%m-%Y') from pasien where pasien.no_rkm_medis=? ", TglLahir, TNoRM.getText());
    }

    public void setNoRm(String norwt, Date tgl2) {
        TNoRw.setText(norwt);
        TCari.setText(norwt);
        Sequel.cariIsi("select reg_periksa.tgl_registrasi from reg_periksa where reg_periksa.no_rawat='" + norwt + "'", DTPCari1);
        DTPCari2.setDate(tgl2);
        isRawat();
        isPsien();
        ChkInput.setSelected(true);
        isForm();
    }

    private void isForm() {
        if (ChkInput.isSelected() == true) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 300));
            FormInput.setVisible(true);
            ChkInput.setVisible(true);
        } else if (ChkInput.isSelected() == false) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 20));
            FormInput.setVisible(false);
            ChkInput.setVisible(true);
        }
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getuji_fungsi_kfr());
        BtnHapus.setEnabled(akses.getuji_fungsi_kfr());
        BtnEdit.setEnabled(akses.getuji_fungsi_kfr());
        BtnPrint.setEnabled(akses.getuji_fungsi_kfr());
        if (akses.getjml2() >= 1) {
            KdDokter.setEditable(false);
            btnPetugas.setEnabled(false);
            KdDokter.setText(akses.getkode());
            Sequel.cariIsi("select dokter.nm_dokter from dokter where dokter.kd_dokter=?", NmDokter, KdDokter.getText());
            if (NmDokter.getText().equals("")) {
                KdDokter.setText("");
                JOptionPane.showMessageDialog(rootPane, "User login bukan dokter...!!");
            }
        }
    }

    private void jam() {
        ActionListener taskPerformer = new ActionListener() {
            private int nilai_jam;
            private int nilai_menit;
            private int nilai_detik;

            public void actionPerformed(ActionEvent e) {
                String nol_jam = "";
                String nol_menit = "";
                String nol_detik = "";

                Date now = Calendar.getInstance().getTime();

                // Mengambil nilaj JAM, MENIT, dan DETIK Sekarang
                if (ChkKejadian.isSelected() == true) {
                    nilai_jam = now.getHours();
                    nilai_menit = now.getMinutes();
                    nilai_detik = now.getSeconds();
                } else if (ChkKejadian.isSelected() == false) {
                    nilai_jam = Jam.getSelectedIndex();
                    nilai_menit = Menit.getSelectedIndex();
                    nilai_detik = Detik.getSelectedIndex();
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
                Jam.setSelectedItem(jam);
                Menit.setSelectedItem(menit);
                Detik.setSelectedItem(detik);
            }
        };
        // Timer
        new Timer(1000, taskPerformer).start();
    }

    private void ganti() {
        Sequel.mengedit("layanankfr", "no_rawat=?", "no_rawat=?,tanggal=?,anamesa=?,pemfisik_ujifungsi=?,dx_medis=?,dx_fungsi=?,rekomedasi=?,pem_penunjang=?,tatalaksana_kfr=?,anjuran=?,evaluasi=?,suspek_kerja=?,ketsuspek=?,kd_dokter=?", 14, new String[]{
            TNoRw.getText(),
            Valid.SetTgl(Tanggal.getSelectedItem() + "") + " " + Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
            Anamesa.getText(),
            PemFisikUji.getText(),
            DxMedis.getText(),
            DxFungsi.getText(),
            PemPenunjang.getText(),
            TataLaksana.getText(),
            Anjuran.getText(),
            Evaluasi.getText(),
            SuspPeny.getSelectedItem().toString(),
            SuspKet.getText(),
            KdDokter.getText(),
            tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString()
        });
        if (tabMode.getRowCount() != 0) {
            tampil();
        }
        emptTeks();
    }

    private void hapus() {
        if (Sequel.queryu2tf("delete from layanankfr where no_rawat=?", 1, new String[]{
            tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString()
        }) == true) {
            tampil();
            emptTeks();
        } else {
            JOptionPane.showMessageDialog(rootPane, "Gagal menghapus..!!");
        }
    }
}
