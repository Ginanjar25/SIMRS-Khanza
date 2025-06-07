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
import kepegawaian.DlgCariPegawai;
import simrskhanza.DlgCariDiagnosaICD10;

/**
 *
 * @author perpustakaan
 */
public final class RMLaporanProgramKFR extends javax.swing.JDialog {

    private final DefaultTableModel tabMode;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i = 0, pilihan = 0;
    private DlgCariDokter dokter = new DlgCariDokter(null, false);
    private DlgCariPegawai pegawai = new DlgCariPegawai(null, false);
    private DlgCariDiagnosaICD10 icd10 = new DlgCariDiagnosaICD10(null, false);
    private String finger = "";

    /**
     * Creates new form DlgRujuk
     *
     * @param parent
     * @param modal
     */
    public RMLaporanProgramKFR(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8, 1);
        setSize(628, 674);

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No. RM", "Nama Pasien", "Umur", "JK", "Tgl.Lahir", "Tanggal Uji", "Diagnosa", "Telah Kami Berikan", "Saran", "NIP", "Nama DPJP", "NIP", "Dokter KFR"
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

        for (i = 0; i < 13; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(100);
            } else if (i == 1) {
                column.setPreferredWidth(100);
            } else if (i == 2) {
                column.setPreferredWidth(160);
            } else if (i == 3) {
                column.setPreferredWidth(100);
            } else if (i == 4) {
                column.setPreferredWidth(100);
            } else if (i == 5) {
                column.setPreferredWidth(120);
            } else if (i == 6) {
                column.setPreferredWidth(150);
            } else if (i == 7) {
                column.setPreferredWidth(150);
            } else if (i == 8) {
                column.setPreferredWidth(150);
            } else if (i == 9) {
                column.setPreferredWidth(150);
            } else if (i == 10) {
                column.setPreferredWidth(200);
            } else if (i == 11) {
                column.setPreferredWidth(150);
            } else if (i == 12) {
                column.setPreferredWidth(200);
            } else if (i == 13) {
                column.setPreferredWidth(200);
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRw.setDocument(new batasInput((byte) 17).getKata(TNoRw));
        KdDok.setDocument(new batasInput((byte) 20).getKata(KdDok));
        Diagnosa.setDocument(new batasInput((int) 50).getKata(Diagnosa));
        tkm.setDocument(new batasInput((int) 200).getKata(tkm));

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
        icd10.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (akses.getform().equals("AwalMedisRalan")) {
                    if (icd10.getTable().getSelectedRow() != -1) {
//                        TPenilaian.append(icd10.getTable().getValueAt(icd10.getTable().getSelectedRow(), 0).toString() + " " + icd10.getTable().getValueAt(icd10.getTable().getSelectedRow(), 1).toString() + "\n");
                        KodeICD10.setText(icd10.getTable().getValueAt(icd10.getTable().getSelectedRow(), 0).toString());
                        Diagnosa.setText(icd10.getTable().getValueAt(icd10.getTable().getSelectedRow(), 1).toString());

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
        pegawai.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (pegawai.getTable().getSelectedRow() != -1) {
                    KdDok.setText(pegawai.getTable().getValueAt(pegawai.getTable().getSelectedRow(), 0).toString());
                    TDokter.setText(pegawai.getTable().getValueAt(pegawai.getTable().getSelectedRow(), 1).toString());
                }
                KdDok.requestFocus();
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
                    nip.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 0).toString());
                    namaDPJP.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString());
                }
                nip.requestFocus();
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
        jLabel8 = new widget.Label();
        TglLahir = new widget.TextBox();
        jLabel12 = new widget.Label();
        Diagnosa = new widget.TextBox();
        JK = new widget.TextBox();
        jLabel13 = new widget.Label();
        jLabel15 = new widget.Label();
        scrollPane1 = new widget.ScrollPane();
        tkm = new widget.TextArea();
        jLabel20 = new widget.Label();
        KdDok = new widget.TextBox();
        btnDokter = new widget.Button();
        TDokter = new widget.TextBox();
        nip = new widget.TextBox();
        namaDPJP = new widget.TextBox();
        btnDokter1 = new widget.Button();
        jLabel9 = new widget.Label();
        jLabel10 = new widget.Label();
        umur = new widget.TextBox();
        BtnICD10 = new widget.Button();
        KodeICD10 = new widget.TextBox();
        scrollPane2 = new widget.ScrollPane();
        saran = new widget.TextArea();
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

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Program KFR ]::", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);
        Scroll.setPreferredSize(new java.awt.Dimension(452, 100));

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
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "27-06-2024" }));
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
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "27-06-2024" }));
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
        PanelInput.setPreferredSize(new java.awt.Dimension(275, 300));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput.setBackground(new java.awt.Color(250, 255, 245));
        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(275, 300));
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
        TNoRw.setBounds(79, 10, 140, 23);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N
        TPasien.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TPasienKeyPressed(evt);
            }
        });
        FormInput.add(TPasien);
        TPasien.setBounds(340, 10, 280, 23);

        Tanggal.setForeground(new java.awt.Color(50, 70, 50));
        Tanggal.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "27-06-2024" }));
        Tanggal.setDisplayFormat("dd-MM-yyyy");
        Tanggal.setName("Tanggal"); // NOI18N
        Tanggal.setOpaque(false);
        Tanggal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalKeyPressed(evt);
            }
        });
        FormInput.add(Tanggal);
        Tanggal.setBounds(480, 40, 90, 23);

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
        jLabel16.setBounds(415, 40, 60, 23);

        Jam.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        Jam.setName("Jam"); // NOI18N
        Jam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JamKeyPressed(evt);
            }
        });
        FormInput.add(Jam);
        Jam.setBounds(570, 40, 62, 23);

        Menit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        Menit.setName("Menit"); // NOI18N
        Menit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MenitKeyPressed(evt);
            }
        });
        FormInput.add(Menit);
        Menit.setBounds(640, 40, 62, 23);

        Detik.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        Detik.setName("Detik"); // NOI18N
        Detik.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DetikKeyPressed(evt);
            }
        });
        FormInput.add(Detik);
        Detik.setBounds(700, 40, 62, 23);

        ChkKejadian.setBorder(null);
        ChkKejadian.setSelected(true);
        ChkKejadian.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkKejadian.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkKejadian.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkKejadian.setName("ChkKejadian"); // NOI18N
        FormInput.add(ChkKejadian);
        ChkKejadian.setBounds(770, 40, 23, 23);

        jLabel18.setText("DPJP :");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(410, 70, 50, 23);

        jLabel8.setText("Jenis Kelamin :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(620, 10, 80, 23);

        TglLahir.setHighlighter(null);
        TglLahir.setName("TglLahir"); // NOI18N
        FormInput.add(TglLahir);
        TglLahir.setBounds(80, 40, 120, 23);

        jLabel12.setText("Diagnosa :");
        jLabel12.setName("jLabel12"); // NOI18N
        FormInput.add(jLabel12);
        jLabel12.setBounds(0, 100, 125, 23);

        Diagnosa.setFocusTraversalPolicyProvider(true);
        Diagnosa.setName("Diagnosa"); // NOI18N
        Diagnosa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosaKeyPressed(evt);
            }
        });
        FormInput.add(Diagnosa);
        Diagnosa.setBounds(200, 100, 500, 23);

        JK.setEditable(false);
        JK.setHighlighter(null);
        JK.setName("JK"); // NOI18N
        JK.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JKKeyPressed(evt);
            }
        });
        FormInput.add(JK);
        JK.setBounds(710, 10, 80, 23);

        jLabel13.setText("Saran :");
        jLabel13.setName("jLabel13"); // NOI18N
        FormInput.add(jLabel13);
        jLabel13.setBounds(0, 200, 125, 23);

        jLabel15.setText("Telah kami berikan :");
        jLabel15.setName("jLabel15"); // NOI18N
        FormInput.add(jLabel15);
        jLabel15.setBounds(10, 130, 114, 23);

        scrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane1.setName("scrollPane1"); // NOI18N

        tkm.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        tkm.setColumns(20);
        tkm.setRows(5);
        tkm.setName("tkm"); // NOI18N
        tkm.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tkmKeyPressed(evt);
            }
        });
        scrollPane1.setViewportView(tkm);

        FormInput.add(scrollPane1);
        scrollPane1.setBounds(130, 130, 650, 60);

        jLabel20.setText("Dokter KFR :");
        jLabel20.setName("jLabel20"); // NOI18N
        FormInput.add(jLabel20);
        jLabel20.setBounds(0, 70, 80, 23);

        KdDok.setHighlighter(null);
        KdDok.setName("KdDok"); // NOI18N
        KdDok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                KdDokActionPerformed(evt);
            }
        });
        KdDok.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdDokKeyPressed(evt);
            }
        });
        FormInput.add(KdDok);
        KdDok.setBounds(80, 70, 90, 23);

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
        btnDokter.setBounds(760, 70, 28, 23);

        TDokter.setEditable(false);
        TDokter.setHighlighter(null);
        TDokter.setName("TDokter"); // NOI18N
        TDokter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TDokterKeyPressed(evt);
            }
        });
        FormInput.add(TDokter);
        TDokter.setBounds(170, 70, 220, 23);

        nip.setEditable(false);
        nip.setName("nip"); // NOI18N
        nip.setPreferredSize(new java.awt.Dimension(80, 23));
        FormInput.add(nip);
        nip.setBounds(460, 70, 90, 23);

        namaDPJP.setEditable(false);
        namaDPJP.setName("namaDPJP"); // NOI18N
        namaDPJP.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(namaDPJP);
        namaDPJP.setBounds(550, 70, 210, 23);

        btnDokter1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnDokter1.setMnemonic('2');
        btnDokter1.setToolTipText("Alt+2");
        btnDokter1.setName("btnDokter1"); // NOI18N
        btnDokter1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDokter1ActionPerformed(evt);
            }
        });
        FormInput.add(btnDokter1);
        btnDokter1.setBounds(390, 70, 28, 23);

        jLabel9.setText("Tgl.Lahir :");
        jLabel9.setName("jLabel9"); // NOI18N
        FormInput.add(jLabel9);
        jLabel9.setBounds(20, 40, 60, 23);

        jLabel10.setText("Umur :");
        jLabel10.setName("jLabel10"); // NOI18N
        FormInput.add(jLabel10);
        jLabel10.setBounds(190, 40, 80, 23);

        umur.setEditable(false);
        umur.setHighlighter(null);
        umur.setName("umur"); // NOI18N
        umur.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                umurKeyPressed(evt);
            }
        });
        FormInput.add(umur);
        umur.setBounds(270, 40, 120, 23);

        BtnICD10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/download.png"))); // NOI18N
        BtnICD10.setMnemonic('K');
        BtnICD10.setText("ICD10");
        BtnICD10.setToolTipText("Alt+K");
        BtnICD10.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnICD10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnICD10.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        BtnICD10.setIconTextGap(0);
        BtnICD10.setName("BtnICD10"); // NOI18N
        BtnICD10.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnICD10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnICD10ActionPerformed(evt);
            }
        });
        BtnICD10.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnICD10KeyPressed(evt);
            }
        });
        FormInput.add(BtnICD10);
        BtnICD10.setBounds(710, 100, 80, 20);

        KodeICD10.setEditable(false);
        KodeICD10.setName("KodeICD10"); // NOI18N
        KodeICD10.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KodeICD10KeyPressed(evt);
            }
        });
        FormInput.add(KodeICD10);
        KodeICD10.setBounds(130, 100, 70, 23);

        scrollPane2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane2.setName("scrollPane2"); // NOI18N

        saran.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        saran.setColumns(20);
        saran.setRows(5);
        saran.setName("saran"); // NOI18N
        saran.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                saranKeyPressed(evt);
            }
        });
        scrollPane2.setViewportView(saran);

        FormInput.add(scrollPane2);
        scrollPane2.setBounds(130, 200, 650, 60);

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
        } else if (KdDok.getText().trim().equals("") || TDokter.getText().trim().equals("")) {
            Valid.textKosong(KdDok, "Dokter");
        } else if (nip.getText().trim().equals("") || namaDPJP.getText().trim().equals("")) {
            Valid.textKosong(nip, "Nama Dokter Pengirim");
        } else if (Diagnosa.getText().trim().equals("")) {
            Valid.textKosong(Diagnosa, "Diagnosa");
        } else if (saran.getText().trim().equals("")) {
            Valid.textKosong(saran, "Terapi");
        } else if (tkm.getText().trim().equals("")) {
            Valid.textKosong(tkm, "Program");
        } else {
            if (Sequel.menyimpantf("laporan_program_kfr", "?,?,?,?,?,?,?,?", "Data", 8, new String[]{
                TNoRw.getText(), TNoRM.getText(), Valid.SetTgl(Tanggal.getSelectedItem() + "") + " " + Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
                Diagnosa.getText(), saran.getText(), tkm.getText(), nip.getText(), KdDok.getText()
            }) == true) {
                JOptionPane.showMessageDialog(rootPane, "Berhasil Simpan !!");
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
                if (KdDok.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(), 12).toString())) {
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
        } else if (KdDok.getText().trim().equals("") || TDokter.getText().trim().equals("")) {
            Valid.textKosong(KdDok, "Dokter");
        } else if (nip.getText().trim().equals("") || namaDPJP.getText().trim().equals("")) {
            Valid.textKosong(nip, "Dokter Pengirim");
        } else if (Diagnosa.getText().trim().equals("")) {
            Valid.textKosong(Diagnosa, "Diagnosis");
        } else if (saran.getText().trim().equals("")) {
            Valid.textKosong(saran, "Saran");
        } else if (tkm.getText().trim().equals("")) {
            Valid.textKosong(tkm, "Hasil");
        } else {
            if (tbObat.getSelectedRow() > -1) {
                if (akses.getkode().equals("Admin Utama")) {
                    ganti();
                    JOptionPane.showMessageDialog(rootPane, "Berhasil Edit..!!");
                } else {
                    if (KdDok.getText().equals(akses.getkode())) {
                        ganti();
                        JOptionPane.showMessageDialog(rootPane, "Berhasil Edit..!!");
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
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            BtnBatal.requestFocus();
        } else if (tabMode.getRowCount() != 0) {
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            Valid.MyReportqry("rptProgramKRF.jasper", "report", "::[ Program KFR ]::",
                    "SELECT\n"
                    + "	pasien.no_rkm_medis, \n"
                    + "	pasien.nm_pasien, \n"
                    + "	pasien.umur, \n"
                    + "	pasien.jk, \n"
                    + "	pasien.tgl_lahir, \n"
                    + "	program_kfr.tanggal, \n"
                    + "	program_kfr.diagnosa, \n"
                    + "	program_kfr.perm_terapi, \n"
                    + "	program_kfr.program, \n"
                    + "	petugas.nip, \n"
                    + "	petugas.nama, \n"
                    + "	dokter.kd_dokter, \n"
                    + "	dokter.nm_dokter\n"
                    + "FROM\n"
                    + "	pasien\n"
                    + "	INNER JOIN\n"
                    + "	program_kfr\n"
                    + "	ON \n"
                    + "		pasien.no_rkm_medis = program_kfr.no_rkm_medis\n"
                    + "	INNER JOIN\n"
                    + "	petugas\n"
                    + "	ON \n"
                    + "		program_kfr.nik = petugas.nip\n"
                    + "	INNER JOIN\n"
                    + "	dokter\n"
                    + "	ON \n"
                    + "		program_kfr.kd_dokter = dokter.kd_dokter "
                    + " where pasien.no_rkm_medis = '" + tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString() + "'"
                    + " order by program_kfr.tanggal", param);
        }
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
        //Valid.pindah(evt, Menit, btnPetugas);
    }//GEN-LAST:event_DetikKeyPressed

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
            finger = Sequel.cariIsi("SELECT sha1( sidikjari.sidikjari ) FROM sidikjari INNER JOIN dokter INNER JOIN pegawai ON sidikjari.id = pegawai.id  AND dokter.kd_dokter = pegawai.nik  WHERE dokter.kd_dokter = ?", tbObat.getValueAt(tbObat.getSelectedRow(), 11).toString());
            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + tbObat.getValueAt(tbObat.getSelectedRow(), 12).toString() + "\nID " + (finger.equals("") ? tbObat.getValueAt(tbObat.getSelectedRow(), 11).toString() : finger) + "\n" + Tanggal.getSelectedItem());
            Valid.MyReportqry("rptLaporanProgramKFR.jasper", "report", "::[ Laporan Pelaksanaan Program Rehabilitasi ]::",
                    "SELECT\n"
                    + "	laporan_program_kfr.no_rawat,\n"
                    + "	laporan_program_kfr.no_rkm_medis,\n"
                    + "	laporan_program_kfr.tanggal,\n"
                    + "	laporan_program_kfr.diagnosa,\n"
                    + "	laporan_program_kfr.tkm,\n"
                    + "	laporan_program_kfr.saran,\n"
                    + "	laporan_program_kfr.nik,\n"
                    + "	laporan_program_kfr.kd_dokter,\n"
                    + "	pasien.jk,\n"
                    + "	pasien.nm_pasien,\n"
                    + "	pasien.tmp_lahir,\n"
                    + "	pasien.tgl_lahir,\n"
                    + "	pasien.umur,\n"
                    + "	pasien.alamat,\n"
                    + "	pasien.no_ktp,\n"
                    + "	pegawai.nama,\n"
                    + "	dokter.nm_dokter \n"
                    + "FROM\n"
                    + "	laporan_program_kfr\n"
                    + "	INNER JOIN pasien ON laporan_program_kfr.no_rkm_medis = pasien.no_rkm_medis\n"
                    + "	INNER JOIN pegawai ON laporan_program_kfr.nik = pegawai.nik\n"
                    + "	INNER JOIN dokter ON laporan_program_kfr.kd_dokter = dokter.kd_dokter \n"
                    + "WHERE\n"
                    + "	laporan_program_kfr.no_rawat ='" + tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString() + "' and laporan_program_kfr.tanggal ='" + tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString() + "' ", param);
        }
    }//GEN-LAST:event_MnUjiFungsiActionPerformed

    public void cetakPDFLaporanKFR(String norawat, String norm) {
        String kodedokter = Sequel.cariIsi("SELECT\n"
                + "	laporan_program_kfr.kd_dokter\n"
                + "FROM\n"
                + "	laporan_program_kfr\n"
                + "	INNER JOIN\n"
                + "	dokter\n"
                + "	ON \n"
                + "		laporan_program_kfr.kd_dokter = dokter.kd_dokter where laporan_program_kfr.no_rawat='" + norawat + "'");
        String namadokter = Sequel.cariIsi("SELECT\n"
                + "	dokter.nm_dokter\n"
                + "FROM\n"
                + "	laporan_program_kfr\n"
                + "	INNER JOIN\n"
                + "	dokter\n"
                + "	ON \n"
                + "		laporan_program_kfr.kd_dokter = dokter.kd_dokter where laporan_program_kfr.no_rawat='" + norawat + "'");
        String tanggal = Sequel.cariIsi("SELECT\n"
                + "	laporan_program_kfr.tanggal\n"
                + "FROM\n"
                + "	laporan_program_kfr\n"
                + "	where laporan_program_kfr.no_rawat='" + norawat + "'");
        Map<String, Object> param = new HashMap<>();
        param.put("namars", akses.getnamars());
        param.put("alamatrs", akses.getalamatrs());
        param.put("kotars", akses.getkabupatenrs());
        param.put("propinsirs", akses.getpropinsirs());
        param.put("kontakrs", akses.getkontakrs());
        param.put("emailrs", akses.getemailrs());
        param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
        finger = Sequel.cariIsi("SELECT sha1( sidikjari.sidikjari ) FROM sidikjari INNER JOIN dokter INNER JOIN pegawai ON sidikjari.id = pegawai.id  AND dokter.kd_dokter = pegawai.nik  WHERE dokter.kd_dokter = ?", kodedokter);
        param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + namadokter + "\nID " + (finger.equals("") ? kodedokter : finger) + "\n" + tanggal);
        Valid.MyReportqry("rptLaporanProgramKFR.jasper", "report", "::[ Laporan Pelaksanaan Program Rehabilitasi ]::",
                "SELECT\n"
                + "	laporan_program_kfr.no_rawat,\n"
                + "	laporan_program_kfr.no_rkm_medis,\n"
                + "	laporan_program_kfr.tanggal,\n"
                + "	laporan_program_kfr.diagnosa,\n"
                + "	laporan_program_kfr.tkm,\n"
                + "	laporan_program_kfr.saran,\n"
                + "	laporan_program_kfr.nik,\n"
                + "	laporan_program_kfr.kd_dokter,\n"
                + "	pasien.jk,\n"
                + "	pasien.nm_pasien,\n"
                + "	pasien.tmp_lahir,\n"
                + "	pasien.tgl_lahir,\n"
                + "	pasien.umur,\n"
                + "	pasien.alamat,\n"
                + "	pasien.no_ktp,\n"
                + "	pegawai.nama,\n"
                + "	dokter.nm_dokter \n"
                + "FROM\n"
                + "	laporan_program_kfr\n"
                + "	INNER JOIN pasien ON laporan_program_kfr.no_rkm_medis = pasien.no_rkm_medis\n"
                + "	INNER JOIN pegawai ON laporan_program_kfr.nik = pegawai.nik\n"
                + "	INNER JOIN dokter ON laporan_program_kfr.kd_dokter = dokter.kd_dokter \n"
                + "WHERE\n"
                + "	laporan_program_kfr.no_rawat ='" + norawat + "' and laporan_program_kfr.tanggal ='" + tanggal + "'", param);

    }

    public void cetakPDFLaporanKFRGabung(String norawat, String norm, String norawatuntuknamafile) {
        String kodedokter = Sequel.cariIsi("SELECT\n"
                + "	laporan_program_kfr.kd_dokter\n"
                + "FROM\n"
                + "	laporan_program_kfr\n"
                + "	INNER JOIN\n"
                + "	dokter\n"
                + "	ON \n"
                + "		laporan_program_kfr.kd_dokter = dokter.kd_dokter where laporan_program_kfr.no_rawat='" + norawat + "'");
        String namadokter = Sequel.cariIsi("SELECT\n"
                + "	dokter.nm_dokter\n"
                + "FROM\n"
                + "	laporan_program_kfr\n"
                + "	INNER JOIN\n"
                + "	dokter\n"
                + "	ON \n"
                + "		laporan_program_kfr.kd_dokter = dokter.kd_dokter where laporan_program_kfr.no_rawat='" + norawat + "'");
        String tanggal = Sequel.cariIsi("SELECT\n"
                + "	laporan_program_kfr.tanggal\n"
                + "FROM\n"
                + "	laporan_program_kfr\n"
                + "	where laporan_program_kfr.no_rawat='" + norawat + "'");
        Map<String, Object> param = new HashMap<>();
        param.put("namars", akses.getnamars());
        param.put("alamatrs", akses.getalamatrs());
        param.put("kotars", akses.getkabupatenrs());
        param.put("propinsirs", akses.getpropinsirs());
        param.put("kontakrs", akses.getkontakrs());
        param.put("emailrs", akses.getemailrs());
        param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
        finger = Sequel.cariIsi("SELECT sha1( sidikjari.sidikjari ) FROM sidikjari INNER JOIN dokter INNER JOIN pegawai ON sidikjari.id = pegawai.id  AND dokter.kd_dokter = pegawai.nik  WHERE dokter.kd_dokter = ?", kodedokter);
        param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + namadokter + "\nID " + (finger.equals("") ? kodedokter : finger) + "\n" + tanggal);
//        Valid.MyReportqrypdfKlaim("rptLaporanProgramKFR.jasper", "report", "5LAPPROGKFR",
//                "SELECT\n"
//                + "	laporan_program_kfr.no_rawat,\n"
//                + "	laporan_program_kfr.no_rkm_medis,\n"
//                + "	laporan_program_kfr.tanggal,\n"
//                + "	laporan_program_kfr.diagnosa,\n"
//                + "	laporan_program_kfr.tkm,\n"
//                + "	laporan_program_kfr.saran,\n"
//                + "	laporan_program_kfr.nik,\n"
//                + "	laporan_program_kfr.kd_dokter,\n"
//                + "	pasien.jk,\n"
//                + "	pasien.nm_pasien,\n"
//                + "	pasien.tmp_lahir,\n"
//                + "	pasien.tgl_lahir,\n"
//                + "	pasien.umur,\n"
//                + "	pasien.alamat,\n"
//                + "	pasien.no_ktp,\n"
//                + "	pegawai.nama,\n"
//                + "	dokter.nm_dokter \n"
//                + "FROM\n"
//                + "	laporan_program_kfr\n"
//                + "	INNER JOIN pasien ON laporan_program_kfr.no_rkm_medis = pasien.no_rkm_medis\n"
//                + "	INNER JOIN pegawai ON laporan_program_kfr.nik = pegawai.nik\n"
//                + "	INNER JOIN dokter ON laporan_program_kfr.kd_dokter = dokter.kd_dokter \n"
//                + "WHERE\n"
//                + "	laporan_program_kfr.no_rawat ='" + norawat + "' and laporan_program_kfr.tanggal ='" + tanggal + "'", param, "hasilkompilasiklaim", norawatuntuknamafile);

    }

    private void DiagnosaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DiagnosaKeyPressed
        Valid.pindah(evt, Diagnosa, saran);
    }//GEN-LAST:event_DiagnosaKeyPressed

    private void JKKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_JKKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_JKKeyPressed

    private void tkmKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tkmKeyPressed
        //Valid.pindah2(evt, DiagnosisMedis, kesimpulan);
    }//GEN-LAST:event_tkmKeyPressed

    private void KdDokActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_KdDokActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_KdDokActionPerformed

    private void KdDokKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdDokKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            Sequel.cariIsi("select nm_dokter from dokter where kd_dokter=?", TDokter, KdDok.getText());
        } else if (evt.getKeyCode() == KeyEvent.VK_UP) {
            btnDokterActionPerformed(null);
        } else {
            //Valid.pindah(evt, rujukke, PKartu);
        }
    }//GEN-LAST:event_KdDokKeyPressed

    private void btnDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDokterActionPerformed
        dokter.isCek();
        dokter.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setVisible(true);
    }//GEN-LAST:event_btnDokterActionPerformed

    private void TDokterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TDokterKeyPressed
        //Valid.pindah(evt,TKd,TSpek);
    }//GEN-LAST:event_TDokterKeyPressed

    private void btnDokter1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDokter1ActionPerformed
        pilihan = 1;
        pegawai.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        pegawai.setLocationRelativeTo(internalFrame1);
        pegawai.setAlwaysOnTop(false);
        pegawai.setVisible(true);
    }//GEN-LAST:event_btnDokter1ActionPerformed

    private void umurKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_umurKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_umurKeyPressed

    private void BtnICD10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnICD10ActionPerformed
        akses.setform("AwalMedisRalan");
        icd10.emptTeks();
        icd10.isCek();
        icd10.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        icd10.setLocationRelativeTo(internalFrame1);
        icd10.setVisible(true);
    }//GEN-LAST:event_BtnICD10ActionPerformed

    private void BtnICD10KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnICD10KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnICD10KeyPressed

    private void KodeICD10KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KodeICD10KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_KodeICD10KeyPressed

    private void saranKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_saranKeyPressed
        //Valid.pindah2(evt, DiagnosisMedis, kesimpulan);
    }//GEN-LAST:event_saranKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMLaporanProgramKFR dialog = new RMLaporanProgramKFR(new javax.swing.JFrame(), true);
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
    private widget.Button BtnICD10;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkInput;
    private widget.CekBox ChkKejadian;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.ComboBox Detik;
    private widget.TextBox Diagnosa;
    private widget.PanelBiasa FormInput;
    private widget.TextBox JK;
    private widget.ComboBox Jam;
    private widget.TextBox KdDok;
    private widget.TextBox KodeICD10;
    private widget.Label LCount;
    private widget.ComboBox Menit;
    private javax.swing.JMenuItem MnUjiFungsi;
    private javax.swing.JPanel PanelInput;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TDokter;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.Tanggal Tanggal;
    private widget.TextBox TglLahir;
    private widget.Button btnDokter;
    private widget.Button btnDokter1;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel10;
    private widget.Label jLabel12;
    private widget.Label jLabel13;
    private widget.Label jLabel15;
    private widget.Label jLabel16;
    private widget.Label jLabel18;
    private widget.Label jLabel19;
    private widget.Label jLabel20;
    private widget.Label jLabel21;
    private widget.Label jLabel4;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.Label jLabel9;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.TextBox namaDPJP;
    private widget.TextBox nip;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.TextArea saran;
    private widget.ScrollPane scrollPane1;
    private widget.ScrollPane scrollPane2;
    private widget.Table tbObat;
    private widget.TextArea tkm;
    private widget.TextBox umur;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            if (TCari.getText().toString().trim().equals("")) {
                ps = koneksi.prepareStatement(
                        "SELECT\n"
                        + "	pasien.nm_pasien,\n"
                        + "	laporan_program_kfr.no_rkm_medis,\n"
                        + "	reg_periksa.no_rawat,\n"
                        + "	pasien.jk,\n"
                        + "	pasien.tmp_lahir,\n"
                        + "	pasien.tgl_lahir,\n"
                        + "	pasien.umur,\n"
                        + "	laporan_program_kfr.tanggal,\n"
                        + "	laporan_program_kfr.diagnosa,\n"
                        + "	laporan_program_kfr.tkm,\n"
                        + "	laporan_program_kfr.saran,\n"
                        + "	laporan_program_kfr.nik,\n"
                        + "	laporan_program_kfr.kd_dokter,\n"
                        + "	dokter.nm_dokter,\n"
                        + "	pegawai.nama,\n"
                        + "	pasien.alamat,\n"
                        + "	pasien.no_ktp \n"
                        + "FROM\n"
                        + "	reg_periksa\n"
                        + "	INNER JOIN laporan_program_kfr ON reg_periksa.no_rkm_medis = laporan_program_kfr.no_rkm_medis\n"
                        + "	INNER JOIN dokter ON laporan_program_kfr.kd_dokter = dokter.kd_dokter\n"
                        + "	INNER JOIN pegawai ON laporan_program_kfr.nik = pegawai.nik\n"
                        + "	INNER JOIN pasien ON laporan_program_kfr.no_rkm_medis = pasien.no_rkm_medis \n"
                        + "WHERE\n"
                        + "	reg_periksa.no_rawat ='" + TNoRw.getText() + "' AND laporan_program_kfr.tanggal between ? and ? order by laporan_program_kfr.tanggal ");
            } else {
                ps = koneksi.prepareStatement(
                        "SELECT\n"
                        + "	pasien.nm_pasien,\n"
                        + "	laporan_program_kfr.no_rkm_medis,\n"
                        + "	reg_periksa.no_rawat,\n"
                        + "	pasien.jk,\n"
                        + "	pasien.tmp_lahir,\n"
                        + "	pasien.tgl_lahir,\n"
                        + "	pasien.umur,\n"
                        + "	laporan_program_kfr.tanggal,\n"
                        + "	laporan_program_kfr.diagnosa,\n"
                        + "	laporan_program_kfr.tkm,\n"
                        + "	laporan_program_kfr.saran,\n"
                        + "	laporan_program_kfr.nik,\n"
                        + "	laporan_program_kfr.kd_dokter,\n"
                        + "	dokter.nm_dokter,\n"
                        + "	pegawai.nama,\n"
                        + "	pasien.alamat,\n"
                        + "	pasien.no_ktp \n"
                        + "FROM\n"
                        + "	reg_periksa\n"
                        + "	INNER JOIN laporan_program_kfr ON reg_periksa.no_rkm_medis = laporan_program_kfr.no_rkm_medis\n"
                        + "	INNER JOIN dokter ON laporan_program_kfr.kd_dokter = dokter.kd_dokter\n"
                        + "	INNER JOIN pegawai ON laporan_program_kfr.nik = pegawai.nik\n"
                        + "	INNER JOIN pasien ON laporan_program_kfr.no_rkm_medis = pasien.no_rkm_medis \n"
                        + "WHERE\n"
                        + "	reg_periksa.no_rawat ='" + TNoRw.getText() + "' AND laporan_program_kfr.tanggal between ? and ? and "
                        + "(pasien.no_rkm_medis like ? or pasien.nm_pasien like ? or pegawai.nik like ? or pegawai.nama like ?) "
                        + " order by laporan_program_kfr.tanggal ");
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
                }

                rs = ps.executeQuery();
                while (rs.next()) {
                    tabMode.addRow(new String[]{
                        rs.getString("no_rawat"),
                        rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                        rs.getString("umur") + " ", rs.getString("jk"),
                        rs.getString("tgl_lahir"), rs.getString("tanggal"), rs.getString("diagnosa"),
                        rs.getString("tkm"), rs.getString("saran"), rs.getString("nik"),
                        rs.getString("nama"), rs.getString("kd_dokter"), rs.getString("nm_dokter")
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
        Diagnosa.setText("");
        saran.setText("");
        tkm.setText("");
        KdDok.setText("");
        TDokter.setText("");
        TNoRw.requestFocus();
    }

    private void getData() {
        if (tbObat.getSelectedRow() != -1) {
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
            JK.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString());
            TglLahir.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString());
            Diagnosa.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString());
            saran.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 8).toString());
            tkm.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString());
            nip.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 10).toString());
            namaDPJP.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 11).toString());
            KdDok.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 12).toString());
            TDokter.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 13).toString());
            Valid.SetTgl(Tanggal, tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString());
            Jam.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString());
            Menit.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString());
            Detik.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString());
        }
    }

    private void isRawat() {
        Sequel.cariIsi("select reg_periksa.no_rkm_medis from reg_periksa where reg_periksa.no_rawat='" + TNoRw.getText() + "' ", TNoRM);
    }

    private void isPsien() {
        Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis='" + TNoRM.getText() + "' ", TPasien);
        Sequel.cariIsi("select pasien.jk from pasien where pasien.no_rkm_medis='" + TNoRM.getText() + "' ", JK);
        Sequel.cariIsi("select pasien.umur from pasien where pasien.no_rkm_medis='" + TNoRM.getText() + "' ", umur);
        Sequel.cariIsi("select date_format(pasien.tgl_lahir,'%d-%m-%Y') from pasien where pasien.no_rkm_medis=? ", TglLahir, TNoRM.getText());
    }

    public void setNoRm(String norwt, Date tgl2) {
        TNoRw.setText(norwt);
        //TCari.setText(norwt);
        Sequel.cariIsi("select reg_periksa.tgl_registrasi from reg_periksa where reg_periksa.no_rkm_medis='" + norwt + "'", DTPCari1);
        DTPCari2.setDate(tgl2);
        isRawat();
        isPsien();
        ChkInput.setSelected(true);
        isForm();
        TCari.setText(TNoRM.getText());
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
            KdDok.setEditable(false);
            btnDokter.setEnabled(true);
            KdDok.setText(akses.getkode());
            Sequel.cariIsi("select dokter.nm_dokter from dokter where dokter.kd_dokter=?", TDokter, KdDok.getText());
//            nip.setText("-");
            if (TDokter.getText().equals("")) {
                KdDok.setText("");
                btnDokter.setEnabled(true);
                namaDPJP.setText(Sequel.cariIsi("select dokter.nm_dokter from dokter where dokter.kd_dokter='" + nip.getText() + "'"));
            }
        }

        tampil();
        BtnCariActionPerformed(null);
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
        Sequel.mengedit("laporan_program_kfr", " no_rawat=? and no_rkm_medis=? and   tanggal=?", "no_rawat=?,no_rkm_medis=?,tanggal=?,diagnosa=?,tkm=?,saran=?,nip=?,kd_dokter", 10, new String[]{
            TNoRw.getText(), TNoRM.getText(), Valid.SetTgl(Tanggal.getSelectedItem() + "") + " " + Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
            Diagnosa.getText(), tkm.getText(), saran.getText(), nip.getText(), KdDok.getText(),
            tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(), tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString(), tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString()
        });
        if (tabMode.getRowCount() != 0) {
            tampil();
        }
        emptTeks();
    }

    private void hapus() {
        if (Sequel.queryu2tf("delete from laporan_program_kfr where  no_rkm_medis=? and tanggal=? and no_rawat=?", 3, new String[]{
            tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(), tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString(), tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString()
        }) == true) {
            JOptionPane.showMessageDialog(rootPane, "Berhasi Menghapus..!!");
            tampil();
            emptTeks();
        } else {
            JOptionPane.showMessageDialog(rootPane, "Gagal menghapus..!!");
        }
    }

    public void ProgramKFRPdf(String norawat, String norm) {

        Map<String, Object> param = new HashMap<>();
        param.put("namars", akses.getnamars());
        param.put("alamatrs", akses.getalamatrs());
        param.put("kotars", akses.getkabupatenrs());
        param.put("propinsirs", akses.getpropinsirs());
        param.put("kontakrs", akses.getkontakrs());
        param.put("emailrs", akses.getemailrs());
        param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
//        Valid.ReportKompilasiBerkas("rptProgramKRF.jasper", "report", "::[ Form Program KFR ]::",
//                "SELECT\n"
//                + "	pasien.no_rkm_medis, \n"
//                + "	pasien.nm_pasien, \n"
//                + "	pasien.umur, \n"
//                + "	pasien.jk, \n"
//                + "	pasien.tgl_lahir, \n"
//                + "	program_kfr.tanggal, \n"
//                + "	program_kfr.diagnosa, \n"
//                + "	program_kfr.perm_terapi, \n"
//                + "	program_kfr.program, \n"
//                + "	petugas.nip, \n"
//                + "	petugas.nama, \n"
//                + "	dokter.kd_dokter, \n"
//                + "	dokter.nm_dokter\n"
//                + "FROM\n"
//                + "	pasien\n"
//                + "	INNER JOIN\n"
//                + "	program_kfr\n"
//                + "	ON \n"
//                + "		pasien.no_rkm_medis = program_kfr.no_rkm_medis\n"
//                + "	INNER JOIN\n"
//                + "	petugas\n"
//                + "	ON \n"
//                + "		program_kfr.nik = petugas.nip\n"
//                + "	INNER JOIN\n"
//                + "	dokter\n"
//                + "	ON \n"
//                + "		program_kfr.kd_dokter = dokter.kd_dokter "
//                + " where pasien.no_rkm_medis = '" + norm + "' "
//                + " order by program_kfr.tanggal", param, norawat, norm, "PROGRAMKFR");

    }
}
