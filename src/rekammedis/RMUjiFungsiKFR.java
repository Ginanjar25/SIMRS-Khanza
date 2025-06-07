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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import static java.awt.image.ImageObserver.WIDTH;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
public final class RMUjiFungsiKFR extends javax.swing.JDialog {

    private final DefaultTableModel tabMode;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps, ps4;
    private RMCariUjiFungsiKFR carikfr = new RMCariUjiFungsiKFR(null, false);
    private ResultSet rs;
    private int i = 0;
    private DlgCariDokter dokter = new DlgCariDokter(null, false);
    public DlgCariDiagnosaICD10 diagnosa = new DlgCariDiagnosaICD10(null, false);
    public DlgCariDiagnosaICD10 diagnosa2 = new DlgCariDiagnosaICD10(null, false);
    public DlgCariDiagnosaICD9 diagnosa3 = new DlgCariDiagnosaICD9(null, false);
    private String finger = "";

    /**
     * Creates new form DlgRujuk
     *
     * @param parent
     * @param modal
     */
    public RMUjiFungsiKFR(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
//        this.setLocation(8, 1);
//        setSize(628, 674);

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.R.M.", "Nama Pasien", "Umur", "JK", "Tgl.Lahir", "Tanggal Uji", "Diagnosis Fugsional", "Diagnosis Medis", "Hasil Yang Didapat", "Kesimpulan", "Rekomendasi", 
            "Anamesa", "Pem Fisik dan Uji Fungsi", "Pem Penunjang", "Tata Laksana","Goal of Treatment", "Edukasi","Anjuran", "Evaluasi", "Susp Penyakit","Ket Susp Penyakit", "Kode Dokter", "Nama Dokter"
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

        for (i = 0; i < 24; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(105);
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
            }  else if (i == 12) {
                column.setPreferredWidth(150);
            } else if (i == 13) {
                column.setPreferredWidth(150);
            }  else if (i == 14) {
                column.setPreferredWidth(150);
            } else if (i == 15) {
                column.setPreferredWidth(150);
            }  else if (i == 16) {
                column.setPreferredWidth(150);
            } else if (i == 17) {
                column.setPreferredWidth(80);
            } else if (i == 18) {
                column.setPreferredWidth(140);
            } else if (i == 19) {
                column.setPreferredWidth(140);
            } else if (i == 20) {
                column.setPreferredWidth(100);
            } else if (i == 21) {
                column.setPreferredWidth(140);
            } else if (i == 22) {
                column.setPreferredWidth(65);
            } else if (i == 23) {
                column.setPreferredWidth(140);
            }
            
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRw.setDocument(new batasInput((byte) 17).getKata(TNoRw));
        KdDokter.setDocument(new batasInput((byte) 20).getKata(KdDokter));
        DiagnosisFungsional.setDocument(new batasInput((int) 50).getKata(DiagnosisFungsional));
        DiagnosisMedis.setDocument(new batasInput((int) 50).getKata(DiagnosisMedis));
        hasilDidapat.setDocument(new batasInput((int) 200).getKata(hasilDidapat));
        kesimpulan.setDocument(new batasInput((int) 200).getKata(kesimpulan));
        rekomendasi.setDocument(new batasInput((int) 100).getKata(rekomendasi));

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

        carikfr.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (carikfr.getTable().getSelectedRow() != -1) {
                    DiagnosisFungsional.setText(carikfr.getTable().getValueAt(carikfr.getTable().getSelectedRow(), 5).toString());
                    DiagnosisMedis.setText(carikfr.getTable().getValueAt(carikfr.getTable().getSelectedRow(), 6).toString());
                    Anamesa.setText(carikfr.getTable().getValueAt(carikfr.getTable().getSelectedRow(), 8).toString());
                    PemFisikUji.setText(carikfr.getTable().getValueAt(carikfr.getTable().getSelectedRow(), 9).toString());
                    PemPenunjang.setText(carikfr.getTable().getValueAt(carikfr.getTable().getSelectedRow(), 10).toString());
                    TataLaksana.setText(carikfr.getTable().getValueAt(carikfr.getTable().getSelectedRow(), 11).toString());
                    goalTreatment.setText(carikfr.getTable().getValueAt(carikfr.getTable().getSelectedRow(), 12).toString());
                    Edukasi.setText(carikfr.getTable().getValueAt(carikfr.getTable().getSelectedRow(), 13).toString());
                    Evaluasi.setText(carikfr.getTable().getValueAt(carikfr.getTable().getSelectedRow(), 14).toString());
                    Anjuran.setText(carikfr.getTable().getValueAt(carikfr.getTable().getSelectedRow(), 15).toString());
                    SuspPeny.setSelectedItem(carikfr.getTable().getValueAt(carikfr.getTable().getSelectedRow(), 16).toString());
                    SuspKet.setText(carikfr.getTable().getValueAt(carikfr.getTable().getSelectedRow(), 17).toString());
                    
                    hasilDidapat.setText(carikfr.getTable().getValueAt(carikfr.getTable().getSelectedRow(), 19).toString());
                    rekomendasi.setText(carikfr.getTable().getValueAt(carikfr.getTable().getSelectedRow(), 20).toString());
                    kesimpulan.setText(carikfr.getTable().getValueAt(carikfr.getTable().getSelectedRow(), 21).toString());
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
                    DiagnosisMedis.setText(diagnosa.getTable().getValueAt(diagnosa.getTable().getSelectedRow(), 0).toString() + " " + diagnosa.getTable().getValueAt(diagnosa.getTable().getSelectedRow(), 1).toString());
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
                    DiagnosisFungsional.setText(diagnosa2.getTable().getValueAt(diagnosa2.getTable().getSelectedRow(), 0).toString() + " " + diagnosa2.getTable().getValueAt(diagnosa2.getTable().getSelectedRow(), 1).toString());
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
        MnProgramKFR = new javax.swing.JMenuItem();
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
        ChkInput = new widget.CekBox();
        scrollInput = new widget.ScrollPane();
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
        DiagnosisFungsional = new widget.TextBox();
        JK = new widget.TextBox();
        jLabel13 = new widget.Label();
        DiagnosisMedis = new widget.TextBox();
        jLabel15 = new widget.Label();
        jLabel17 = new widget.Label();
        jLabel20 = new widget.Label();
        scrollPane1 = new widget.ScrollPane();
        hasilDidapat = new widget.TextArea();
        scrollPane2 = new widget.ScrollPane();
        kesimpulan = new widget.TextArea();
        scrollPane3 = new widget.ScrollPane();
        rekomendasi = new widget.TextArea();
        BtnUjiKFRGet1 = new widget.Button();
        BtnUjiKFRGet3 = new widget.Button();
        jLabel14 = new widget.Label();
        Anamesa = new widget.TextBox();
        jLabel22 = new widget.Label();
        PemFisikUji = new widget.TextBox();
        jLabel23 = new widget.Label();
        jLabel24 = new widget.Label();
        jLabel25 = new widget.Label();
        scrollPane4 = new widget.ScrollPane();
        TataLaksana = new widget.TextArea();
        scrollPane5 = new widget.ScrollPane();
        Anjuran = new widget.TextArea();
        scrollPane6 = new widget.ScrollPane();
        Evaluasi = new widget.TextArea();
        SuspKet = new widget.TextBox();
        jLabel28 = new widget.Label();
        PemPenunjang = new widget.TextBox();
        SuspPeny = new widget.ComboBox();
        jLabel29 = new widget.Label();
        btnDiagnosa = new widget.Button();
        btnDiagnosa1 = new widget.Button();
        btnDiagnosa2 = new widget.Button();
        jLabel30 = new widget.Label();
        scrollPane7 = new widget.ScrollPane();
        Edukasi = new widget.TextArea();
        jLabel31 = new widget.Label();
        jLabel2 = new javax.swing.JLabel();
        scrollPane8 = new widget.ScrollPane();
        goalTreatment = new widget.TextArea();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel26 = new widget.Label();

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

        MnProgramKFR.setBackground(new java.awt.Color(255, 255, 254));
        MnProgramKFR.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnProgramKFR.setForeground(new java.awt.Color(50, 50, 50));
        MnProgramKFR.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnProgramKFR.setText("Program KFR");
        MnProgramKFR.setName("MnProgramKFR"); // NOI18N
        MnProgramKFR.setPreferredSize(new java.awt.Dimension(270, 26));
        MnProgramKFR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnProgramKFRActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnProgramKFR);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Uji Fungsi/Prosedur KFR ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
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
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "03-06-2025" }));
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
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "03-06-2025" }));
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
        PanelInput.setPreferredSize(new java.awt.Dimension(250, 400));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setMnemonic('I');
        ChkInput.setSelected(true);
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
        scrollInput.setPreferredSize(new java.awt.Dimension(252, 450));

        FormInput.setBackground(new java.awt.Color(250, 255, 245));
        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(250, 550));
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
        Tanggal.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "03-06-2025" }));
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

        jLabel18.setText("Dokter Sp.RM :");
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
        NmDokter.setBounds(580, 40, 177, 23);

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
        btnPetugas.setBounds(761, 40, 28, 23);

        jLabel8.setText("Tgl.Lahir :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(625, 10, 60, 23);

        TglLahir.setHighlighter(null);
        TglLahir.setName("TglLahir"); // NOI18N
        FormInput.add(TglLahir);
        TglLahir.setBounds(689, 10, 100, 23);

        jLabel12.setText("Diagnosis Fungsional :");
        jLabel12.setName("jLabel12"); // NOI18N
        FormInput.add(jLabel12);
        jLabel12.setBounds(5, 360, 110, 23);

        DiagnosisFungsional.setFocusTraversalPolicyProvider(true);
        DiagnosisFungsional.setName("DiagnosisFungsional"); // NOI18N
        DiagnosisFungsional.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosisFungsionalKeyPressed(evt);
            }
        });
        FormInput.add(DiagnosisFungsional);
        DiagnosisFungsional.setBounds(120, 360, 660, 23);

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

        jLabel13.setText("Diagnosis Medis :");
        jLabel13.setName("jLabel13"); // NOI18N
        FormInput.add(jLabel13);
        jLabel13.setBounds(5, 390, 110, 23);

        DiagnosisMedis.setFocusTraversalPolicyProvider(true);
        DiagnosisMedis.setName("DiagnosisMedis"); // NOI18N
        DiagnosisMedis.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosisMedisKeyPressed(evt);
            }
        });
        FormInput.add(DiagnosisMedis);
        DiagnosisMedis.setBounds(120, 390, 660, 23);

        jLabel15.setText("Hasil Yang Didapat :");
        jLabel15.setName("jLabel15"); // NOI18N
        FormInput.add(jLabel15);
        jLabel15.setBounds(0, 450, 114, 23);

        jLabel17.setText("Kesimpulan :");
        jLabel17.setName("jLabel17"); // NOI18N
        FormInput.add(jLabel17);
        jLabel17.setBounds(35, 500, 78, 20);

        jLabel20.setText("Rekomendasi :");
        jLabel20.setName("jLabel20"); // NOI18N
        FormInput.add(jLabel20);
        jLabel20.setBounds(480, 450, 87, 20);

        scrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane1.setName("scrollPane1"); // NOI18N

        hasilDidapat.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        hasilDidapat.setColumns(20);
        hasilDidapat.setRows(5);
        hasilDidapat.setName("hasilDidapat"); // NOI18N
        hasilDidapat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                hasilDidapatKeyPressed(evt);
            }
        });
        scrollPane1.setViewportView(hasilDidapat);

        FormInput.add(scrollPane1);
        scrollPane1.setBounds(120, 450, 330, 40);

        scrollPane2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane2.setName("scrollPane2"); // NOI18N

        kesimpulan.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        kesimpulan.setColumns(20);
        kesimpulan.setRows(5);
        kesimpulan.setName("kesimpulan"); // NOI18N
        kesimpulan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kesimpulanKeyPressed(evt);
            }
        });
        scrollPane2.setViewportView(kesimpulan);

        FormInput.add(scrollPane2);
        scrollPane2.setBounds(120, 500, 690, 40);

        scrollPane3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane3.setName("scrollPane3"); // NOI18N

        rekomendasi.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        rekomendasi.setColumns(20);
        rekomendasi.setRows(5);
        rekomendasi.setName("rekomendasi"); // NOI18N
        rekomendasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                rekomendasiKeyPressed(evt);
            }
        });
        scrollPane3.setViewportView(rekomendasi);

        FormInput.add(scrollPane3);
        scrollPane3.setBounds(570, 450, 240, 40);

        BtnUjiKFRGet1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Edit.png"))); // NOI18N
        BtnUjiKFRGet1.setMnemonic('K');
        BtnUjiKFRGet1.setText("AMBIL PEMERIKSAAN TERDAHULU");
        BtnUjiKFRGet1.setToolTipText("Alt+K");
        BtnUjiKFRGet1.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnUjiKFRGet1.setName("BtnUjiKFRGet1"); // NOI18N
        BtnUjiKFRGet1.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnUjiKFRGet1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnUjiKFRGet1ActionPerformed(evt);
            }
        });
        BtnUjiKFRGet1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnUjiKFRGet1KeyPressed(evt);
            }
        });
        FormInput.add(BtnUjiKFRGet1);
        BtnUjiKFRGet1.setBounds(190, 70, 220, 20);

        BtnUjiKFRGet3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/History.png"))); // NOI18N
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
        BtnUjiKFRGet3.setBounds(480, 70, 240, 20);

        jLabel14.setText("Anamesa : ");
        jLabel14.setName("jLabel14"); // NOI18N
        FormInput.add(jLabel14);
        jLabel14.setBounds(60, 120, 70, 23);

        Anamesa.setFocusTraversalPolicyProvider(true);
        Anamesa.setName("Anamesa"); // NOI18N
        Anamesa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                AnamesaKeyPressed(evt);
            }
        });
        FormInput.add(Anamesa);
        Anamesa.setBounds(130, 120, 330, 23);

        jLabel22.setText("Pem Fisik & Uji Fungsi : ");
        jLabel22.setName("jLabel22"); // NOI18N
        FormInput.add(jLabel22);
        jLabel22.setBounds(480, 120, 112, 23);

        PemFisikUji.setFocusTraversalPolicyProvider(true);
        PemFisikUji.setName("PemFisikUji"); // NOI18N
        PemFisikUji.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PemFisikUjiKeyPressed(evt);
            }
        });
        FormInput.add(PemFisikUji);
        PemFisikUji.setBounds(590, 120, 210, 23);

        jLabel23.setText("TataLaksana KFR :");
        jLabel23.setName("jLabel23"); // NOI18N
        FormInput.add(jLabel23);
        jLabel23.setBounds(0, 180, 114, 23);

        jLabel24.setText("Anjuran :");
        jLabel24.setName("jLabel24"); // NOI18N
        FormInput.add(jLabel24);
        jLabel24.setBounds(35, 300, 78, 23);

        jLabel25.setText("Ket Susp Penyakit :");
        jLabel25.setHorizontalTextPosition(javax.swing.SwingConstants.TRAILING);
        jLabel25.setName("jLabel25"); // NOI18N
        FormInput.add(jLabel25);
        jLabel25.setBounds(475, 330, 120, 20);

        scrollPane4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane4.setName("scrollPane4"); // NOI18N

        TataLaksana.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        TataLaksana.setColumns(20);
        TataLaksana.setRows(5);
        TataLaksana.setName("TataLaksana"); // NOI18N
        TataLaksana.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TataLaksanaKeyPressed(evt);
            }
        });
        scrollPane4.setViewportView(TataLaksana);

        FormInput.add(scrollPane4);
        scrollPane4.setBounds(120, 180, 330, 50);

        scrollPane5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane5.setName("scrollPane5"); // NOI18N

        Anjuran.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        Anjuran.setColumns(20);
        Anjuran.setRows(5);
        Anjuran.setName("Anjuran"); // NOI18N
        Anjuran.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                AnjuranKeyPressed(evt);
            }
        });
        scrollPane5.setViewportView(Anjuran);

        FormInput.add(scrollPane5);
        scrollPane5.setBounds(120, 300, 330, 50);

        scrollPane6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane6.setName("scrollPane6"); // NOI18N

        Evaluasi.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        Evaluasi.setColumns(20);
        Evaluasi.setRows(5);
        Evaluasi.setName("Evaluasi"); // NOI18N
        Evaluasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                EvaluasiKeyPressed(evt);
            }
        });
        scrollPane6.setViewportView(Evaluasi);

        FormInput.add(scrollPane6);
        scrollPane6.setBounds(510, 240, 290, 50);

        SuspKet.setEditable(false);
        SuspKet.setFocusTraversalPolicyProvider(true);
        SuspKet.setName("SuspKet"); // NOI18N
        SuspKet.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SuspKetKeyPressed(evt);
            }
        });
        FormInput.add(SuspKet);
        SuspKet.setBounds(600, 330, 200, 23);

        jLabel28.setText("Pemeriksaan Penunjang :");
        jLabel28.setName("jLabel28"); // NOI18N
        FormInput.add(jLabel28);
        jLabel28.setBounds(0, 150, 130, 23);

        PemPenunjang.setFocusTraversalPolicyProvider(true);
        PemPenunjang.setName("PemPenunjang"); // NOI18N
        PemPenunjang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PemPenunjangKeyPressed(evt);
            }
        });
        FormInput.add(PemPenunjang);
        PemPenunjang.setBounds(130, 150, 670, 23);

        SuspPeny.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ya", "Tidak" }));
        SuspPeny.setSelectedIndex(1);
        SuspPeny.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
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
        SuspPeny.setBounds(600, 300, 70, 20);

        jLabel29.setText("Evaluasi :");
        jLabel29.setName("jLabel29"); // NOI18N
        FormInput.add(jLabel29);
        jLabel29.setBounds(460, 240, 50, 23);

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
        btnDiagnosa.setBounds(780, 390, 28, 23);

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
        btnDiagnosa1.setBounds(780, 360, 28, 23);

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
        btnDiagnosa2.setBounds(90, 200, 28, 23);

        jLabel30.setText("Edukasi :");
        jLabel30.setName("jLabel30"); // NOI18N
        FormInput.add(jLabel30);
        jLabel30.setBounds(430, 180, 78, 23);

        scrollPane7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane7.setName("scrollPane7"); // NOI18N

        Edukasi.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        Edukasi.setColumns(20);
        Edukasi.setRows(5);
        Edukasi.setName("Edukasi"); // NOI18N
        Edukasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                EdukasiKeyPressed(evt);
            }
        });
        scrollPane7.setViewportView(Edukasi);

        FormInput.add(scrollPane7);
        scrollPane7.setBounds(510, 180, 290, 50);

        jLabel31.setText("Goal Of Treatment :");
        jLabel31.setName("jLabel31"); // NOI18N
        FormInput.add(jLabel31);
        jLabel31.setBounds(15, 240, 100, 23);

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setLabelFor(jLabel1);
        jLabel2.setText("Layanan KFR");
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel2.setName("jLabel2"); // NOI18N
        jLabel2.setOpaque(true);
        FormInput.add(jLabel2);
        jLabel2.setBounds(400, 90, 110, 25);

        scrollPane8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane8.setName("scrollPane8"); // NOI18N

        goalTreatment.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        goalTreatment.setColumns(20);
        goalTreatment.setRows(5);
        goalTreatment.setName("goalTreatment"); // NOI18N
        goalTreatment.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                goalTreatmentKeyPressed(evt);
            }
        });
        scrollPane8.setViewportView(goalTreatment);

        FormInput.add(scrollPane8);
        scrollPane8.setBounds(120, 240, 330, 50);

        jLabel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));
        jLabel1.setName("jLabel1"); // NOI18N
        FormInput.add(jLabel1);
        jLabel1.setBounds(20, 105, 800, 3);

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setLabelFor(jLabel1);
        jLabel5.setText("Hasil Uji Fungsi");
        jLabel5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel5.setName("jLabel5"); // NOI18N
        jLabel5.setOpaque(true);
        FormInput.add(jLabel5);
        jLabel5.setBounds(380, 420, 130, 25);

        jLabel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));
        jLabel3.setName("jLabel3"); // NOI18N
        FormInput.add(jLabel3);
        jLabel3.setBounds(20, 435, 800, 3);

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        jLabel26.setText("Susp Penyakit Akibat Kerja :");
        jLabel26.setHorizontalTextPosition(javax.swing.SwingConstants.TRAILING);
        jLabel26.setName("jLabel26"); // NOI18N
        FormInput.add(jLabel26);
        jLabel26.setBounds(460, 300, 150, 20);

        scrollInput.setViewportView(FormInput);

        PanelInput.add(scrollInput, java.awt.BorderLayout.CENTER);

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
        } else if (DiagnosisFungsional.getText().trim().equals("")) {
            Valid.textKosong(DiagnosisFungsional, "Diagnosis Fungsional");
        } else if (DiagnosisMedis.getText().trim().equals("")) {
            Valid.textKosong(DiagnosisMedis, "Diagnosis Medis");
        } else if (hasilDidapat.getText().trim().equals("")) {
            Valid.textKosong(hasilDidapat, "Hasil Yang Didapat");
        } else if (kesimpulan.getText().trim().equals("")) {
            Valid.textKosong(kesimpulan, "Kesimpulan");
        } else if (rekomendasi.getText().trim().equals("")) {
            Valid.textKosong(rekomendasi, "Rekomendasi");
        } else if (Anamesa.getText().trim().equals("")) {
            Valid.textKosong(Anamesa, "Anamesa");
        } else if (TataLaksana.getText().trim().equals("")) {
            Valid.textKosong(TataLaksana, "TataLaksana");
        } else if (PemFisikUji.getText().trim().equals("")) {
            Valid.textKosong(PemFisikUji, "PemFisikUji");
        } else {
            if (Sequel.menyimpantf("uji_fungsi_kfr", "?,?,?,?,?,?,?,?", "Data", 8, new String[]{
                TNoRw.getText(), Valid.SetTgl(Tanggal.getSelectedItem() + "") + " " + Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
                DiagnosisFungsional.getText(), DiagnosisMedis.getText(), hasilDidapat.getText(), kesimpulan.getText(), rekomendasi.getText(), KdDokter.getText()
            }) == true) {
                if(Sequel.menyimpantf("layanan_kfr", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 15, new String[]{
                    TNoRw.getText(), Valid.SetTgl(Tanggal.getSelectedItem() + "") + " " + Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
                    Anamesa.getText(), PemFisikUji.getText(), DiagnosisMedis.getText(), DiagnosisFungsional.getText(), PemPenunjang.getText(), TataLaksana.getText(),
                    goalTreatment.getText(), Anjuran.getText(), Edukasi.getText(), Evaluasi.getText(), SuspPeny.getSelectedItem().toString(), SuspKet.getText(), KdDokter.getText()
                }) == true){
                     tampil();
                emptTeks();
                }
               
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
                if (KdDokter.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(), 22).toString())) {
                    hapus();
                } else {
                    JOptionPane.showMessageDialog(null, "Hanya bisa dihapus oleh dokter yang bersangkutan..!!");
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
        } else if (DiagnosisFungsional.getText().trim().equals("")) {
            Valid.textKosong(DiagnosisFungsional, "Diagnosis Fungsional");
        } else if (DiagnosisMedis.getText().trim().equals("")) {
            Valid.textKosong(DiagnosisMedis, "Diagnosis Medis");
        } else if (hasilDidapat.getText().trim().equals("")) {
            Valid.textKosong(hasilDidapat, "Hasil Yang Didapat");
        } else if (kesimpulan.getText().trim().equals("")) {
            Valid.textKosong(kesimpulan, "Kesimpulan");
        } else if (rekomendasi.getText().trim().equals("")) {
            Valid.textKosong(rekomendasi, "Rekomendasi");
        }else if (Anamesa.getText().trim().equals("")) {
            Valid.textKosong(Anamesa, "Anamesa");
        } else if (TataLaksana.getText().trim().equals("")) {
            Valid.textKosong(TataLaksana, "TataLaksana");
        } else if (PemFisikUji.getText().trim().equals("")) {
            Valid.textKosong(PemFisikUji, "PemFisikUji");
        } else {
            if (tbObat.getSelectedRow() > -1) {
                if (akses.getkode().equals("Admin Utama")) {
                    ganti();
                } else {
                    if (KdDokter.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(), 22).toString())) {
                        ganti();
                    } else {
                        JOptionPane.showMessageDialog(null, "Hanya bisa diganti oleh dokter yang bersangkutan..!!");
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
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
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

            if (TCari.getText().trim().equals("")) {
                Valid.MyReportqry("rptUjiFungsiKFR.jasper", "report", "::[ Data Uji Fugsi/Prosedur KFR ]::",
                        "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,reg_periksa.umurdaftar,reg_periksa.sttsumur,"
                        + "pasien.jk,uji_fungsi_kfr.tanggal,uji_fungsi_kfr.diagnosis_fungsional,uji_fungsi_kfr.diagnosis_medis,uji_fungsi_kfr.hasil_didapat,"
                        + "uji_fungsi_kfr.kesimpulan,uji_fungsi_kfr.rekomedasi,uji_fungsi_kfr.kd_dokter,dokter.nm_dokter,date_format(pasien.tgl_lahir,'%d-%m-%Y') as lahir "
                        + "from uji_fungsi_kfr inner join reg_periksa on uji_fungsi_kfr.no_rawat=reg_periksa.no_rawat "
                        + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                        + "inner join dokter on uji_fungsi_kfr.kd_dokter=dokter.kd_dokter where "
                        + "uji_fungsi_kfr.tanggal between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59' order by uji_fungsi_kfr.tanggal ", param);
            } else {
                Valid.MyReportqry("rptUjiFungsiKFR.jasper", "report", "::[ Data Uji Fugsi/Prosedur KFR ]::",
                        "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,reg_periksa.umurdaftar,reg_periksa.sttsumur,"
                        + "pasien.jk,uji_fungsi_kfr.tanggal,uji_fungsi_kfr.diagnosis_fungsional,uji_fungsi_kfr.diagnosis_medis,uji_fungsi_kfr.hasil_didapat,"
                        + "uji_fungsi_kfr.kesimpulan,uji_fungsi_kfr.rekomedasi,uji_fungsi_kfr.kd_dokter,dokter.nm_dokter,date_format(pasien.tgl_lahir,'%d-%m-%Y') as lahir "
                        + "from uji_fungsi_kfr inner join reg_periksa on uji_fungsi_kfr.no_rawat=reg_periksa.no_rawat "
                        + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                        + "inner join dokter on uji_fungsi_kfr.kd_dokter=dokter.kd_dokter "
                        + "where uji_fungsi_kfr.tanggal between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59' and "
                        + "(reg_periksa.no_rawat like '%" + TCari.getText().trim() + "%' or pasien.no_rkm_medis like '%" + TCari.getText().trim() + "%' or pasien.nm_pasien like '%" + TCari.getText().trim() + "%' or "
                        + "uji_fungsi_kfr.kd_dokter like '%" + TCari.getText().trim() + "%' or dokter.nm_dokter like '%" + TCari.getText().trim() + "%') order by uji_fungsi_kfr.tanggal ", param);
            }
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
        Valid.pindah(evt, Detik, DiagnosisFungsional);
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
            param.put("no_rawat", tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            param.put("alamatpasien", Sequel.cariIsi("SELECT CONCAT(ps.alamat, ', DS. ',kl.nm_kel, ', KEC. ',kc.nm_kec, ',',kb.nm_kab) FROM pasien ps " +
                        "INNER JOIN kelurahan kl ON kl.kd_kel = ps.kd_kel " +
                        "INNER JOIN kecamatan kc ON kc.kd_kec = ps.kd_kec " +
                        "INNER JOIN kabupaten kb ON kb.kd_kab = ps.kd_kab " +
                        "WHERE ps.no_rkm_medis = ?", tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString()));
            finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", tbObat.getValueAt(tbObat.getSelectedRow(), 22).toString());
            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + tbObat.getValueAt(tbObat.getSelectedRow(), 23).toString() + "\nID " + (finger.equals("") ? tbObat.getValueAt(tbObat.getSelectedRow(), 22).toString() : finger) + "\n" + Tanggal.getSelectedItem());
            List<String> reports = Arrays.asList( "rptCetakLayananKFR.jasper", "rptCetakUjiFungsiKFR.jasper");
            
            Valid.CombinedReports(reports, "report", "::[ Formulir/Lembar Uji Fungsi/Prosedur KFR ]::", param);
        }
    }//GEN-LAST:event_MnUjiFungsiActionPerformed

    public void CetakPDFUjiFungsi(String norawat, String noRM) {
        String kodedokter = Sequel.cariIsi("SELECT\n"
                + "	uji_fungsi_kfr.kd_dokter\n"
                + "FROM\n"
                + "	uji_fungsi_kfr\n"
                + "	INNER JOIN\n"
                + "	dokter\n"
                + "	ON \n"
                + "		uji_fungsi_kfr.kd_dokter = dokter.kd_dokter where uji_fungsi_kfr.no_rawat='" + norawat + "'");
        String namadokter = Sequel.cariIsi("SELECT\n"
                + "	dokter.nm_dokter\n"
                + "FROM\n"
                + "	uji_fungsi_kfr\n"
                + "	INNER JOIN\n"
                + "	dokter\n"
                + "	ON \n"
                + "		uji_fungsi_kfr.kd_dokter = dokter.kd_dokter where uji_fungsi_kfr.no_rawat='" + norawat + "'");
        String tanggal = Sequel.cariIsi("SELECT\n"
                + "	uji_fungsi_kfr.tanggal\n"
                + "FROM\n"
                + "	uji_fungsi_kfr\n"
                + "	where uji_fungsi_kfr.no_rawat='" + norawat + "'");

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
        Valid.MyReportqry("rptCetakUjiFungsiKFR.jasper", "report", "::[ Formulir/Lembar Uji Fungsi/Prosedur KFR ]::",
                "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,reg_periksa.umurdaftar,reg_periksa.sttsumur,"
                + "pasien.jk,uji_fungsi_kfr.tanggal,uji_fungsi_kfr.diagnosis_fungsional,uji_fungsi_kfr.diagnosis_medis,uji_fungsi_kfr.hasil_didapat,"
                + "uji_fungsi_kfr.kesimpulan,uji_fungsi_kfr.rekomedasi,uji_fungsi_kfr.kd_dokter,dokter.nm_dokter,date_format(pasien.tgl_lahir,'%d-%m-%Y') as lahir, "
                + "date_format(uji_fungsi_kfr.tanggal,'%d-%m-%Y') as periksa from uji_fungsi_kfr inner join reg_periksa on uji_fungsi_kfr.no_rawat=reg_periksa.no_rawat "
                + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                + "inner join dokter on uji_fungsi_kfr.kd_dokter=dokter.kd_dokter where reg_periksa.no_rawat='" + norawat + "'", param);
    }

    public void CetakPDFUjiFungsiGabung(String norawat, String noRM, String norawatuntuknamafile) {
        String kodedokter = Sequel.cariIsi("SELECT\n"
                + "	uji_fungsi_kfr.kd_dokter\n"
                + "FROM\n"
                + "	uji_fungsi_kfr\n"
                + "	INNER JOIN\n"
                + "	dokter\n"
                + "	ON \n"
                + "		uji_fungsi_kfr.kd_dokter = dokter.kd_dokter where uji_fungsi_kfr.no_rawat='" + norawat + "'");
        String namadokter = Sequel.cariIsi("SELECT\n"
                + "	dokter.nm_dokter\n"
                + "FROM\n"
                + "	uji_fungsi_kfr\n"
                + "	INNER JOIN\n"
                + "	dokter\n"
                + "	ON \n"
                + "		uji_fungsi_kfr.kd_dokter = dokter.kd_dokter where uji_fungsi_kfr.no_rawat='" + norawat + "'");
        String tanggal = Sequel.cariIsi("SELECT\n"
                + "	uji_fungsi_kfr.tanggal\n"
                + "FROM\n"
                + "	uji_fungsi_kfr\n"
                + "	where uji_fungsi_kfr.no_rawat='" + norawat + "'");

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
//        Valid.MyReportqrypdfKlaim("rptCetakUjiFungsiKFR.jasper", "report", "4UJIFUNGSIKFR",
//                "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,reg_periksa.umurdaftar,reg_periksa.sttsumur,"
//                + "pasien.jk,uji_fungsi_kfr.tanggal,uji_fungsi_kfr.diagnosis_fungsional,uji_fungsi_kfr.diagnosis_medis,uji_fungsi_kfr.hasil_didapat,"
//                + "uji_fungsi_kfr.kesimpulan,uji_fungsi_kfr.rekomedasi,uji_fungsi_kfr.kd_dokter,dokter.nm_dokter,date_format(pasien.tgl_lahir,'%d-%m-%Y') as lahir, "
//                + "date_format(uji_fungsi_kfr.tanggal,'%d-%m-%Y') as periksa from uji_fungsi_kfr inner join reg_periksa on uji_fungsi_kfr.no_rawat=reg_periksa.no_rawat "
//                + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
//                + "inner join dokter on uji_fungsi_kfr.kd_dokter=dokter.kd_dokter where reg_periksa.no_rawat='" + norawat + "'", param, "hasilkompilasiklaim", norawatuntuknamafile);
    }

    private void DiagnosisFungsionalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DiagnosisFungsionalKeyPressed
        Valid.pindah(evt, btnPetugas, DiagnosisMedis);
    }//GEN-LAST:event_DiagnosisFungsionalKeyPressed

    private void JKKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_JKKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_JKKeyPressed

    private void DiagnosisMedisKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DiagnosisMedisKeyPressed
        Valid.pindah(evt, DiagnosisFungsional, hasilDidapat);
    }//GEN-LAST:event_DiagnosisMedisKeyPressed

    private void hasilDidapatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_hasilDidapatKeyPressed
        Valid.pindah2(evt, DiagnosisMedis, kesimpulan);
    }//GEN-LAST:event_hasilDidapatKeyPressed

    private void kesimpulanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kesimpulanKeyPressed
        Valid.pindah2(evt, hasilDidapat, rekomendasi);
    }//GEN-LAST:event_kesimpulanKeyPressed

    private void rekomendasiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rekomendasiKeyPressed
        Valid.pindah2(evt, kesimpulan, BtnSimpan);
    }//GEN-LAST:event_rekomendasiKeyPressed

    private void BtnUjiKFRGet1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnUjiKFRGet1ActionPerformed
        if (TNoRw.getText().equals("") && TNoRM.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Pasien masih kosong...!!!");
        } else {
            carikfr.setNoRawat(TNoRw.getText());
            carikfr.isCek(KdDokter.getText());
            carikfr.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            carikfr.setLocationRelativeTo(internalFrame1);
            carikfr.setVisible(true);
        }
    }//GEN-LAST:event_BtnUjiKFRGet1ActionPerformed

    private void BtnUjiKFRGet1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnUjiKFRGet1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnUjiKFRGet1KeyPressed

    private void BtnUjiKFRGet3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnUjiKFRGet3ActionPerformed
        if (TNoRw.getText().equals("") && TNoRM.getText().equals("")) {
            JOptionPane.showMessageDialog(rootPane, "Pasien masih kosong...!!!");
        } else {
            try {
                ps4 = koneksi.prepareStatement("SELECT uji_fungsi_kfr.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,pegawai.nama, " +
                        "pegawai.jbtn,uji_fungsi_kfr.tanggal,uji_fungsi_kfr.diagnosis_fungsional,uji_fungsi_kfr.diagnosis_medis, " +
                        "uji_fungsi_kfr.hasil_didapat,uji_fungsi_kfr.kesimpulan,uji_fungsi_kfr.rekomedasi,uji_fungsi_kfr.kd_dokter, " +
                        "layanan_kfr.anamnesa, layanan_kfr.pemeriksaan_fisik_fungsi, layanan_kfr.diagnosa_medis, layanan_kfr.diagnosa_fungsi,layanan_kfr.pemeriksaan_penunjang, " +
                        "layanan_kfr.tata_laksana_kfr, layanan_kfr.goal_treatment, layanan_kfr.edukasi, layanan_kfr.anjuran, layanan_kfr.evaluasi,layanan_kfr.suspek_penyakit, " +
                        "layanan_kfr.ket_suspek_penyakit " +
                        "FROM pasien INNER JOIN reg_periksa ON reg_periksa.no_rkm_medis = pasien.no_rkm_medis " +
                        "INNER JOIN pegawai " +
                        "INNER JOIN uji_fungsi_kfr ON reg_periksa.no_rawat = uji_fungsi_kfr.no_rawat " +
                        "AND reg_periksa.no_rawat = uji_fungsi_kfr.no_rawat " +
                        "AND pegawai.nik = uji_fungsi_kfr.kd_dokter " +
                        "INNER JOIN layanan_kfr ON layanan_kfr.no_rawat = uji_fungsi_kfr.no_rawat " +
                        "where uji_fungsi_kfr.kd_dokter='" + akses.getkode() + "' and reg_periksa.no_rkm_medis='" + TNoRM.getText() + "' and reg_periksa.no_rawat!='" + TNoRw.getText() + "'"+
                        "order by uji_fungsi_kfr.tanggal desc LIMIT 1");
                try {
                    rs = ps4.executeQuery();
                    while (rs.next()) {
                        DiagnosisFungsional.setText(rs.getString("diagnosis_fungsional"));
                        DiagnosisMedis.setText(rs.getString("diagnosis_medis"));
                        hasilDidapat.setText(rs.getString("hasil_didapat"));
                        kesimpulan.setText(rs.getString("kesimpulan"));
                        rekomendasi.setText(rs.getString("rekomedasi"));

                        Anamesa.setText(rs.getString("anamnesa"));
                        PemFisikUji.setText(rs.getString("pemeriksaan_fisik_fungsi"));
                        PemPenunjang.setText(rs.getString("pemeriksaan_penunjang"));
                        TataLaksana.setText(rs.getString("tata_laksana_kfr"));
                        goalTreatment.setText(rs.getString("goal_treatment"));
                        Anjuran.setText(rs.getString("anjuran"));
                        Edukasi.setText(rs.getString("edukasi"));
                        Evaluasi.setText(rs.getString("evaluasi"));
                        SuspPeny.setSelectedItem(rs.getString("suspek_penyakit"));
                        SuspKet.setText(rs.getString("ket_suspek_penyakit"));
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

    private void AnamesaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_AnamesaKeyPressed
        Valid.pindah(evt, btnPetugas, PemFisikUji);
    }//GEN-LAST:event_AnamesaKeyPressed

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

    private void EdukasiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_EdukasiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_EdukasiKeyPressed

    private void goalTreatmentKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_goalTreatmentKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_goalTreatmentKeyPressed

    private void btnDiagnosa1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnDiagnosa1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDiagnosa1KeyPressed

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

    private void btnDiagnosaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnDiagnosaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDiagnosaKeyPressed

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

    private void MnProgramKFRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnProgramKFRActionPerformed
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, table masih kosong...!!!!");
            //TNoReg.requestFocus();
        }else if(TNoRw.getText().trim().equals("")){
            JOptionPane.showMessageDialog(null,"Maaf, Silahkan anda pilih dulu dengan menklik data pada table...!!!");
            tbObat.requestFocus();
        }else{
            if(tbObat.getSelectedRow()!= -1){
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                RMProgramKFR form=new RMProgramKFR(null,false);
                form.isCek();
                form.emptTeks();
                form.setNoRm(TNoRw.getText(),DTPCari2.getDate());
                form.setRefKFR(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
                form.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            }
        }
    }//GEN-LAST:event_MnProgramKFRActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMUjiFungsiKFR dialog = new RMUjiFungsiKFR(new javax.swing.JFrame(), true);
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
    private widget.Button BtnUjiKFRGet1;
    private widget.Button BtnUjiKFRGet3;
    private widget.CekBox ChkInput;
    private widget.CekBox ChkKejadian;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.ComboBox Detik;
    private widget.TextBox DiagnosisFungsional;
    private widget.TextBox DiagnosisMedis;
    private widget.TextArea Edukasi;
    private widget.TextArea Evaluasi;
    private widget.PanelBiasa FormInput;
    private widget.TextBox JK;
    private widget.ComboBox Jam;
    private widget.TextBox KdDokter;
    private widget.Label LCount;
    private widget.ComboBox Menit;
    private javax.swing.JMenuItem MnProgramKFR;
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
    private widget.TextArea goalTreatment;
    private widget.TextArea hasilDidapat;
    private widget.InternalFrame internalFrame1;
    private javax.swing.JLabel jLabel1;
    private widget.Label jLabel12;
    private widget.Label jLabel13;
    private widget.Label jLabel14;
    private widget.Label jLabel15;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private widget.Label jLabel18;
    private widget.Label jLabel19;
    private javax.swing.JLabel jLabel2;
    private widget.Label jLabel20;
    private widget.Label jLabel21;
    private widget.Label jLabel22;
    private widget.Label jLabel23;
    private widget.Label jLabel24;
    private widget.Label jLabel25;
    private widget.Label jLabel26;
    private widget.Label jLabel28;
    private widget.Label jLabel29;
    private javax.swing.JLabel jLabel3;
    private widget.Label jLabel30;
    private widget.Label jLabel31;
    private widget.Label jLabel4;
    private javax.swing.JLabel jLabel5;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.TextArea kesimpulan;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.TextArea rekomendasi;
    private widget.ScrollPane scrollInput;
    private widget.ScrollPane scrollPane1;
    private widget.ScrollPane scrollPane2;
    private widget.ScrollPane scrollPane3;
    private widget.ScrollPane scrollPane4;
    private widget.ScrollPane scrollPane5;
    private widget.ScrollPane scrollPane6;
    private widget.ScrollPane scrollPane7;
    private widget.ScrollPane scrollPane8;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            if (TCari.getText().toString().trim().equals("")) {
                ps = koneksi.prepareStatement(
                        "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,reg_periksa.umurdaftar,reg_periksa.sttsumur, " +
                        "pasien.jk,uji_fungsi_kfr.tanggal,uji_fungsi_kfr.diagnosis_fungsional,uji_fungsi_kfr.diagnosis_medis,uji_fungsi_kfr.hasil_didapat, " +
                        "uji_fungsi_kfr.kesimpulan,uji_fungsi_kfr.rekomedasi,uji_fungsi_kfr.kd_dokter,dokter.nm_dokter,date_format(pasien.tgl_lahir,'%d-%m-%Y') as lahir, " +
                        "layanan_kfr.anamnesa, layanan_kfr.pemeriksaan_fisik_fungsi, layanan_kfr.diagnosa_medis, layanan_kfr.diagnosa_fungsi,layanan_kfr.pemeriksaan_penunjang, " +
                        "layanan_kfr.tata_laksana_kfr, layanan_kfr.goal_treatment, layanan_kfr.edukasi, layanan_kfr.anjuran, layanan_kfr.evaluasi,layanan_kfr.suspek_penyakit, " +
                        "layanan_kfr.ket_suspek_penyakit " +
                        "from uji_fungsi_kfr inner join reg_periksa on uji_fungsi_kfr.no_rawat=reg_periksa.no_rawat " +
                        "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis " +
                        "inner join dokter on uji_fungsi_kfr.kd_dokter=dokter.kd_dokter " +
                        "INNER JOIN layanan_kfr ON layanan_kfr.no_rawat = uji_fungsi_kfr.no_rawat " +
                        "where uji_fungsi_kfr.tanggal between ? and ? order by uji_fungsi_kfr.tanggal ");
            } else {
                ps = koneksi.prepareStatement(
                        "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,reg_periksa.umurdaftar,reg_periksa.sttsumur,"
                        + "pasien.jk,uji_fungsi_kfr.tanggal,uji_fungsi_kfr.diagnosis_fungsional,uji_fungsi_kfr.diagnosis_medis,uji_fungsi_kfr.hasil_didapat,"
                        + "uji_fungsi_kfr.kesimpulan,uji_fungsi_kfr.rekomedasi,uji_fungsi_kfr.kd_dokter,dokter.nm_dokter,date_format(pasien.tgl_lahir,'%d-%m-%Y') as lahir, "
                        + "layanan_kfr.anamnesa, layanan_kfr.pemeriksaan_fisik_fungsi, layanan_kfr.diagnosa_medis, layanan_kfr.diagnosa_fungsi,layanan_kfr.pemeriksaan_penunjang, " 
                        + "layanan_kfr.tata_laksana_kfr, layanan_kfr.goal_treatment, layanan_kfr.edukasi, layanan_kfr.anjuran, layanan_kfr.evaluasi,layanan_kfr.suspek_penyakit, "
                        + "layanan_kfr.ket_suspek_penyakit "
                        + "from uji_fungsi_kfr inner join reg_periksa on uji_fungsi_kfr.no_rawat=reg_periksa.no_rawat "
                        + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                        + "inner join dokter on uji_fungsi_kfr.kd_dokter=dokter.kd_dokter "
                        + "INNER JOIN layanan_kfr ON layanan_kfr.no_rawat = uji_fungsi_kfr.no_rawat "
                        + "where uji_fungsi_kfr.tanggal between ? and ? and "
                        + "(reg_periksa.no_rawat like ? or pasien.no_rkm_medis like ? or pasien.nm_pasien like ? or uji_fungsi_kfr.kd_dokter like ? or dokter.nm_dokter like ?) "
                        + "order by uji_fungsi_kfr.tanggal ");
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
                        rs.getString("lahir"), rs.getString("tanggal"), rs.getString("diagnosis_fungsional"),
                        rs.getString("diagnosis_medis"), rs.getString("hasil_didapat"), rs.getString("kesimpulan"),
                        rs.getString("rekomedasi"), rs.getString("anamnesa"),rs.getString("pemeriksaan_fisik_fungsi"), 
                        rs.getString("pemeriksaan_penunjang"),rs.getString("tata_laksana_kfr"), rs.getString("goal_treatment"),rs.getString("edukasi"),
                        rs.getString("anjuran"), rs.getString("evaluasi"),rs.getString("suspek_penyakit"),rs.getString("ket_suspek_penyakit"),
                        rs.getString("kd_dokter"), rs.getString("nm_dokter")
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
        DiagnosisFungsional.setText("");
        DiagnosisMedis.setText("");
        hasilDidapat.setText("");
        kesimpulan.setText("");
        rekomendasi.setText("");
        
        Anamesa.setText("");
        PemFisikUji.setText("");
        PemPenunjang.setText("");
        TataLaksana.setText("");
        goalTreatment.setText("");
        Edukasi.setText("");
        Anjuran.setText("");
        Evaluasi.setText("");
        SuspPeny.setSelectedItem("Tidak");
        SuspKet.setText("");
            
            
        DiagnosisFungsional.requestFocus();
    }

    private void getData() {
        if (tbObat.getSelectedRow() != -1) {
            // "No.Rawat","No.R.M.","Nama Pasien","Umur","JK","Tgl.Lahir","Tanggal Uji","Diagnosis Fugsional","Diagnosis Medis","Hasil Yang Didapat","Kesimpulan","Rekomendasi","Kode Dokter","Nama Dokter"
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
            JK.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString());
            TglLahir.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString());
            DiagnosisFungsional.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString());
            DiagnosisMedis.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 8).toString());
            hasilDidapat.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString());
            kesimpulan.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 10).toString());
            rekomendasi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 11).toString());
            
            Anamesa.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 12).toString());
            PemFisikUji.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 13).toString());
            PemPenunjang.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 14).toString());
            TataLaksana.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 15).toString());
            goalTreatment.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 16).toString());
            Edukasi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 17).toString());
            Anjuran.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 18).toString());
            Evaluasi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 19).toString());
            SuspPeny.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 20).toString());
            SuspKet.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 21).toString());
            KdDokter.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 22).toString());
            NmDokter.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 23).toString());

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
        ChkInput.setSelected(false);
        isForm();
    }

    private void isForm() {
        if (ChkInput.isSelected() == true) {
            ChkInput.setVisible(false);
//            PanelInput.setPreferredSize(new Dimension(WIDTH,this.getHeight()-230));
            PanelInput.setPreferredSize(new Dimension(WIDTH,this.getHeight()-122));
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
                JOptionPane.showMessageDialog(null, "User login bukan dokter...!!");
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
        Sequel.mengedit("uji_fungsi_kfr", "no_rawat=?", "tanggal=?,diagnosis_fungsional=?,diagnosis_medis=?,hasil_didapat=?,kesimpulan=?,rekomedasi=?,kd_dokter=?", 8, new String[]{
            TNoRw.getText(), Valid.SetTgl(Tanggal.getSelectedItem() + "") + " " + Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
            DiagnosisFungsional.getText(), DiagnosisMedis.getText(), hasilDidapat.getText(), kesimpulan.getText(), rekomendasi.getText(), KdDokter.getText(),
            tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString()
        });
        
        Sequel.mengedit("layanan_kfr", "no_rawat=?", "tanggal=?,anamnesa=?,pemeriksaan_fisik_fungsi=?,diagnosa_medis=?,"
                + "diagnosa_fungsi=?,pemeriksaan_penunjang=?,tata_laksana_kfr=?,goal_treatment=?,anjuran=?,edukasi=?,evaluasi=?,"
                + "suspek_penyakit=?,ket_suspek_penyakit=?, kd_dokter=?", 15, new String[]{
            Valid.SetTgl(Tanggal.getSelectedItem() + "") + " " + Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
            Anamesa.getText(),
            PemFisikUji.getText(),
            DiagnosisMedis.getText(),
            DiagnosisFungsional.getText(),
            PemPenunjang.getText(),
            TataLaksana.getText(),
            goalTreatment.getText(),
            Anjuran.getText(),
            Edukasi.getText(),
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
        if (Sequel.queryu2tf("delete from uji_fungsi_kfr where no_rawat=?", 1, new String[]{
            tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString()
        }) == true) {
            if (Sequel.queryu2tf("delete from layanan_kfr where no_rawat=?", 1, new String[]{
                tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString()
            }) == true) {
                tampil();
                emptTeks();
            } else {
                JOptionPane.showMessageDialog(rootPane, "Gagal menghapus..!!");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
        }
    }

    public void UjiKFRPdf(String norawat, String norm) {
        Map<String, Object> param = new HashMap<>();
        param.put("namars", akses.getnamars());
        param.put("alamatrs", akses.getalamatrs());
        param.put("kotars", akses.getkabupatenrs());
        param.put("propinsirs", akses.getpropinsirs());
        param.put("kontakrs", akses.getkontakrs());
        param.put("emailrs", akses.getemailrs());
        param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
        finger=Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?",tbObat.getValueAt(tbObat.getSelectedRow(),22).toString());
        param.put("finger","Dikeluarkan di "+akses.getnamars()+", Kabupaten/Kota "+akses.getkabupatenrs()+"\nDitandatangani secara elektronik oleh "+tbObat.getValueAt(tbObat.getSelectedRow(),23).toString()+"\nID "+(finger.equals("")?tbObat.getValueAt(tbObat.getSelectedRow(),22).toString():finger)+"\n"+Valid.SetTgl3(Tanggal.getSelectedItem().toString())); 
        Valid.MyReport("rptCetakUjiFungsiKFR.jasper", "report", "::[ Formulir/Lembar Uji Fungsi/Prosedur KFR ]::", param);

    }
}
