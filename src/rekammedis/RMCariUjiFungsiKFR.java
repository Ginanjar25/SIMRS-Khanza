/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * DlgPenyakit.java
 *
 * Created on May 23, 2010, 12:57:16 AM
 */
package rekammedis;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import fungsi.sekuel;

/**
 *
 * @author dosen
 */
public final class RMCariUjiFungsiKFR extends javax.swing.JDialog {

    private final DefaultTableModel tabMode = null, tabModePemeriksaan;
    private validasi Valid = new validasi();
    private Connection koneksi = koneksiDB.condb();
    private PreparedStatement ps4;
    private ResultSet rs;
    private String norawat = "", Kodedokter = "", norawatrujuk = "", norm="";
    private int z = 0, i = 0;
    private sekuel Sequel = new sekuel();

    /**
     * Creates new form DlgPenyakit
     *
     * @param parent
     * @param modal
     */
    public RMCariUjiFungsiKFR(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(10, 2);
        setSize(656, 250);

        tabModePemeriksaan = new DefaultTableModel(null, new Object[]{
            "NO RAWAT","NO RM", "NAMA PASIEN","TANGGAL","DOKTER","DX FUNGSIONAL", "DX MEDIS","|","ANAMNESA", "PEM FISIK & FUNGSI","PEM PENUNJANG", "TATA LAKSANA",
            "GOAL TREATMENT", "EDUKASI", "ANJURAN", "EVALUASI", "SUSP PENYAKIT", "KET SUSP PENYAKIT",
            "|","HASIL", "REKOMENDASI", "KESIMPULAN", "STATUS"}) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                boolean a = false;
//                if (colIndex == 0) {
//                    a = true;
//                }
                return a;
            }
            Class[] types = new Class[]{
                java.lang.Object.class,java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class,
                java.lang.Object.class,java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class,
                java.lang.Object.class,java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class,java.lang.Object.class,
                java.lang.Object.class,java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        tbPemeriksaan.setModel(tabModePemeriksaan);
        tbPemeriksaan.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbPemeriksaan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 23; i++) {
            TableColumn column = tbPemeriksaan.getColumnModel().getColumn(i);
            if(i == 0){
                column.setPreferredWidth(150);
            }else if(i == 1){
                column.setPreferredWidth(100);
            }else if(i == 2){
                column.setPreferredWidth(200);
            }else if(i == 3){
                column.setPreferredWidth(200);
            }else if(i == 4){
                column.setPreferredWidth(150);
            }else if (i == 5) {
                column.setPreferredWidth(250);
            }else if (i == 6) {
                column.setPreferredWidth(250);
            }else if (i == 7) {
                column.setPreferredWidth(10);
            }else if (i == 8) {
                column.setPreferredWidth(250);
            }else if (i == 9) {
                column.setPreferredWidth(250);
            }else if (i == 10) {
                column.setPreferredWidth(250);
            }else if (i == 11) {
                column.setPreferredWidth(250);
            }else if (i == 12) {
                column.setPreferredWidth(250);
            }else if (i == 13) {
                column.setPreferredWidth(250);
            }else if (i == 14) {
                column.setPreferredWidth(250);
            }else if (i == 15) {
                column.setPreferredWidth(250);
            }else if (i == 16) {
                column.setPreferredWidth(250);
            }else if (i == 17) {
                column.setPreferredWidth(250);
            }else if (i == 18) {
                column.setPreferredWidth(10);
            } else if (i == 19) {
                column.setPreferredWidth(250);
            }else if (i == 20) {
                column.setPreferredWidth(250);
            }else if (i == 21) {
                column.setPreferredWidth(250);
            }else if (i == 22) {
                column.setPreferredWidth(250);
            }
        }
        tbPemeriksaan.setDefaultRenderer(Object.class, new WarnaTable());

        TCari.setDocument(new batasInput((byte) 100).getKata(TCari));
        if (koneksiDB.CARICEPAT().equals("aktif")) {
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampilPemeriksaan();
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampilPemeriksaan();
                    }
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampilPemeriksaan();
                    }
                }
            });
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

        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbPemeriksaan = new widget.Table();
        panelisi3 = new widget.panelisi();
        label9 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        label10 = new widget.Label();
        LCount = new widget.Label();
        BtnKeluar = new widget.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ CARI RIWAYAT LAYANAN KFR & UJI FUNGSI ]::", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbPemeriksaan.setName("tbPemeriksaan"); // NOI18N
        tbPemeriksaan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbPemeriksaanMouseClicked(evt);
            }
        });
        tbPemeriksaan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbPemeriksaanKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbPemeriksaan);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        panelisi3.setName("panelisi3"); // NOI18N
        panelisi3.setPreferredSize(new java.awt.Dimension(100, 43));
        panelisi3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 9));

        label9.setText("Key Word :");
        label9.setName("label9"); // NOI18N
        label9.setPreferredSize(new java.awt.Dimension(68, 23));
        panelisi3.add(label9);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(300, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelisi3.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('1');
        BtnCari.setToolTipText("Alt+1");
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
        panelisi3.add(BtnCari);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('2');
        BtnAll.setToolTipText("2Alt+2");
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
        panelisi3.add(BtnAll);

        label10.setText("Record :");
        label10.setName("label10"); // NOI18N
        label10.setPreferredSize(new java.awt.Dimension(60, 23));
        panelisi3.add(label10);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(50, 23));
        panelisi3.add(LCount);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setMnemonic('4');
        BtnKeluar.setToolTipText("Alt+4");
        BtnKeluar.setName("BtnKeluar"); // NOI18N
        BtnKeluar.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluarActionPerformed(evt);
            }
        });
        panelisi3.add(BtnKeluar);

        internalFrame1.add(panelisi3, java.awt.BorderLayout.PAGE_END);

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
        } else if (evt.getKeyCode() == KeyEvent.VK_UP) {
            tbPemeriksaan.requestFocus();
        }
}//GEN-LAST:event_TCariKeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        tampilPemeriksaan();
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
        tampilPemeriksaan();
}//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnAllActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnCari, TCari);
        }
}//GEN-LAST:event_BtnAllKeyPressed

    private void tbPemeriksaanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPemeriksaanMouseClicked
        if (tabModePemeriksaan.getRowCount() != 0) {
            if (evt.getClickCount() == 2) {
                dispose();
            }
        }
}//GEN-LAST:event_tbPemeriksaanMouseClicked

    private void tbPemeriksaanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbPemeriksaanKeyPressed
        if (tabModePemeriksaan.getRowCount() != 0) {
            if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
                dispose();
            } else if (evt.getKeyCode() == KeyEvent.VK_SHIFT) {
                TCari.setText("");
                TCari.requestFocus();
            }
        }
}//GEN-LAST:event_tbPemeriksaanKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        emptTeks();
    }//GEN-LAST:event_formWindowActivated

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        tampilPemeriksaan();
    }//GEN-LAST:event_formWindowOpened

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMCariUjiFungsiKFR dialog = new RMCariUjiFungsiKFR(new javax.swing.JFrame(), true);
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
    private widget.Button BtnCari;
    private widget.Button BtnKeluar;
    private widget.Label LCount;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.InternalFrame internalFrame1;
    private widget.Label label10;
    private widget.Label label9;
    private widget.panelisi panelisi3;
    private widget.Table tbPemeriksaan;
    // End of variables declaration//GEN-END:variables

    private void tampilPemeriksaan() {
        Valid.tabelKosong(tabModePemeriksaan);
        try {
            ps4 = koneksi.prepareStatement("select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,reg_periksa.umurdaftar,reg_periksa.sttsumur,\n" +
                    "pasien.jk,uji_fungsi_kfr.tanggal,uji_fungsi_kfr.diagnosis_fungsional,uji_fungsi_kfr.diagnosis_medis,uji_fungsi_kfr.hasil_didapat,\n" +
                    "uji_fungsi_kfr.kesimpulan,uji_fungsi_kfr.rekomedasi,uji_fungsi_kfr.kd_dokter,dokter.nm_dokter,date_format(pasien.tgl_lahir,'%d-%m-%Y') as lahir,\n" +
                    "layanan_kfr.anamnesa, layanan_kfr.pemeriksaan_fisik_fungsi, layanan_kfr.diagnosa_medis, layanan_kfr.diagnosa_fungsi,layanan_kfr.pemeriksaan_penunjang,\n" +
                    "layanan_kfr.tata_laksana_kfr, layanan_kfr.goal_treatment, layanan_kfr.edukasi, layanan_kfr.anjuran, layanan_kfr.evaluasi,layanan_kfr.suspek_penyakit,\n" +
                    "layanan_kfr.ket_suspek_penyakit, if(layanan_kfr.status = '1', 'Aktif', 'Tidak Aktif') as status \n" +
                    "from uji_fungsi_kfr inner join reg_periksa on uji_fungsi_kfr.no_rawat=reg_periksa.no_rawat\n" +
                    "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis\n" +
                    "inner join dokter on uji_fungsi_kfr.kd_dokter=dokter.kd_dokter\n" +
                    "INNER JOIN layanan_kfr ON layanan_kfr.no_rawat = uji_fungsi_kfr.no_rawat\n" +
                    "where pasien.no_rkm_medis = ? and (uji_fungsi_kfr.kd_dokter like ? OR reg_periksa.no_rawat = ? or uji_fungsi_kfr.diagnosis_fungsional LIKE ? OR uji_fungsi_kfr.diagnosis_medis LIKE ?  OR uji_fungsi_kfr.hasil_didapat LIKE ?  OR uji_fungsi_kfr.kesimpulan LIKE ?  OR uji_fungsi_kfr.rekomedasi LIKE ?  )\n" +
                    "order by uji_fungsi_kfr.tanggal DESC LIMIT 200");
            try {
                ps4.setString(1, norm);
                ps4.setString(2, "%" + Kodedokter + "%");
                ps4.setString(3, TCari.getText().trim());
                ps4.setString(4, "%" + TCari.getText().trim() + "%");
                ps4.setString(5, "%" + TCari.getText().trim() + "%");
                ps4.setString(6, "%" + TCari.getText().trim() + "%");
                ps4.setString(7, "%" + TCari.getText().trim() + "%");
                ps4.setString(8, "%" + TCari.getText().trim() + "%");
                rs = ps4.executeQuery();
                while (rs.next()) {

                    tabModePemeriksaan.addRow(new Object[]{
                        rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                        rs.getString("tanggal"),rs.getString("nm_dokter"),rs.getString("diagnosis_fungsional"), rs.getString("diagnosis_medis"), 
                        " |",rs.getString("anamnesa"), rs.getString("pemeriksaan_fisik_fungsi"), rs.getString("pemeriksaan_penunjang"),
                        rs.getString("tata_laksana_kfr"),rs.getString("goal_treatment"), rs.getString("edukasi"), 
                        rs.getString("anjuran"),rs.getString("evaluasi"), rs.getString("suspek_penyakit"), rs.getString("ket_suspek_penyakit"),
                        " |",rs.getString("hasil_didapat"),rs.getString("rekomedasi"), rs.getString("kesimpulan"), rs.getString("status")
                    });
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
        LCount.setText("" + tabModePemeriksaan.getRowCount());
    }

    public void emptTeks() {
        TCari.requestFocus();
    }

    public void isCek(String kodedokter) {
        Kodedokter = kodedokter;
        TCari.requestFocus();
//        TCari.setText("");
    }

    public void setNoRawat(String norawat) {
        this.norawat = norawat;
        this.norm = Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat = ?", norawat);
        TCari.setText(norm);
    }

    public void iscekNoRawat(String nomerrawat) {
        norawatrujuk = nomerrawat;
        TCari.requestFocus();
    }

    public JTable getTable() {
        return tbPemeriksaan;
    }

}
