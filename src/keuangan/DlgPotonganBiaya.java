/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DlgPenyakit.java
 *
 * Created on May 23, 2010, 12:57:16 AM
 */

package keuangan;

import simrskhanza.*;
import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import restore.DlgRestoreCaraBayar;

/**
 *
 * @author dosen
 */
public final class DlgPotonganBiaya extends javax.swing.JDialog {
    private final DefaultTableModel tabModeRajal,tabModeRanap,tabModePenunjang;
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private Connection koneksi=koneksiDB.condb();
    private PreparedStatement ps,psrajal,psranap,pspenunjang;
    private ResultSet rs,rsrajal,rsranap,rspenunjang;
    private int i=0;
    
    /** Creates new form DlgPenyakit
     * @param parent
     * @param modal */
    public DlgPotonganBiaya(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(10,2);
        setSize(628,674);

        Object[] row={"P","Tanggal","Nama Tindakan","Petugas", "Total Potongan"};
        tabModeRajal=new DefaultTableModel(null,row){
             @Override public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = false;
                if ((colIndex==0)||(colIndex==4)) {
                    a=true;
                }
                return a;
             }
             Class[] types = new Class[] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        tbRajal.setModel(tabModeRajal);
        //tbPenyakit.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbPenyakit.getBackground()));
        tbRajal.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbRajal.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 5; i++) {
            TableColumn column = tbRajal.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(20);
            }else if(i==1){
                column.setPreferredWidth(160);
            }else if(i==2){
                column.setPreferredWidth(160);
            }else if(i==3){
                column.setPreferredWidth(160);
            }else if(i==4){
                column.setPreferredWidth(100);
            }
        }
        tbRajal.setDefaultRenderer(Object.class, new WarnaTable());
        
        Object[] rowRanap={"P","Tanggal","Nama Tindakan","Petugas","Total Potongan"};
        tabModeRanap=new DefaultTableModel(null,rowRanap){
             @Override public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = false;
                if ((colIndex==0)||(colIndex==4)) {
                    a=true;
                }
                return a;
             }
             Class[] types = new Class[] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        tbRanap.setModel(tabModeRanap);
        //tbPenyakit.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbPenyakit.getBackground()));
        tbRanap.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbRanap.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 5; i++) {
            TableColumn column = tbRanap.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(20);
            }else if(i==1){
                column.setPreferredWidth(160);
            }else if(i==2){
                column.setPreferredWidth(160);
            }else if(i==3){
                column.setPreferredWidth(160);
            }else if(i==4){
                column.setPreferredWidth(100);
            }
        }
        tbRanap.setDefaultRenderer(Object.class, new WarnaTable());
        
        Object[] rowPenunjang={"P","Tanggal","Nama Tindakan","Petugas","Total Potongan"};
        tabModePenunjang=new DefaultTableModel(null,rowPenunjang){
             @Override public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = false;
                if ((colIndex==0)||(colIndex==4)) {
                    a=true;
                }
                return a;
             }
             Class[] types = new Class[] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        tbPenunjang.setModel(tabModePenunjang);
        //tbPenyakit.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbPenyakit.getBackground()));
        tbPenunjang.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbPenunjang.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 5; i++) {
            TableColumn column = tbPenunjang.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(20);
            }else if(i==1){
                column.setPreferredWidth(160);
            }else if(i==2){
                column.setPreferredWidth(160);
            }else if(i==3){
                column.setPreferredWidth(160);
            }else if(i==4){
                column.setPreferredWidth(100);
            }
        }
        tbPenunjang.setDefaultRenderer(Object.class, new WarnaTable());
        
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
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Kd2 = new widget.TextBox();
        Popup = new javax.swing.JPopupMenu();
        MnRestore = new javax.swing.JMenuItem();
        internalFrame1 = new widget.InternalFrame();
        jPanel1 = new javax.swing.JPanel();
        panelisi3 = new widget.panelisi();
        label9 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        label10 = new widget.Label();
        LCount = new widget.Label();
        NoRw = new widget.Label();
        TNoRw = new widget.TextBox();
        panelisi1 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnKeluar = new widget.Button();
        TabRawat = new javax.swing.JTabbedPane();
        rajal = new widget.ScrollPane();
        tbRajal = new widget.Table();
        ranap = new widget.ScrollPane();
        tbRanap = new widget.Table();
        penunjang = new widget.ScrollPane();
        tbPenunjang = new widget.Table();

        Kd2.setHighlighter(null);
        Kd2.setName("Kd2"); // NOI18N
        Kd2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Kd2KeyPressed(evt);
            }
        });

        Popup.setName("Popup"); // NOI18N

        MnRestore.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnRestore.setForeground(new java.awt.Color(50, 50, 50));
        MnRestore.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnRestore.setText("Data Sampah");
        MnRestore.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnRestore.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnRestore.setName("MnRestore"); // NOI18N
        MnRestore.setPreferredSize(new java.awt.Dimension(200, 28));
        MnRestore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnRestoreActionPerformed(evt);
            }
        });
        Popup.add(MnRestore);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Potongan Biaya ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setOpaque(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(816, 100));
        jPanel1.setLayout(new java.awt.BorderLayout(1, 1));

        panelisi3.setName("panelisi3"); // NOI18N
        panelisi3.setPreferredSize(new java.awt.Dimension(100, 44));
        panelisi3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 9));

        label9.setText("Key Word :");
        label9.setName("label9"); // NOI18N
        label9.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi3.add(label9);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(200, 23));
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
        BtnAll.setToolTipText("Alt+2");
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
        label10.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi3.add(label10);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(60, 23));
        panelisi3.add(LCount);

        NoRw.setText("No Rawat :");
        NoRw.setName("NoRw"); // NOI18N
        NoRw.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi3.add(NoRw);

        TNoRw.setName("TNoRw"); // NOI18N
        TNoRw.setPreferredSize(new java.awt.Dimension(200, 23));
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });
        panelisi3.add(TNoRw);

        jPanel1.add(panelisi3, java.awt.BorderLayout.PAGE_START);

        panelisi1.setName("panelisi1"); // NOI18N
        panelisi1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

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
        panelisi1.add(BtnSimpan);

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
        panelisi1.add(BtnBatal);

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
        panelisi1.add(BtnHapus);

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
        panelisi1.add(BtnKeluar);

        jPanel1.add(panelisi1, java.awt.BorderLayout.CENTER);

        internalFrame1.add(jPanel1, java.awt.BorderLayout.PAGE_END);

        TabRawat.setBackground(new java.awt.Color(255, 255, 253));
        TabRawat.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(241, 246, 236)));
        TabRawat.setForeground(new java.awt.Color(50, 50, 50));
        TabRawat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        TabRawat.setName("TabRawat"); // NOI18N
        TabRawat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TabRawatMouseClicked(evt);
            }
        });

        rajal.setComponentPopupMenu(Popup);
        rajal.setName("rajal"); // NOI18N
        rajal.setOpaque(true);

        tbRajal.setAutoCreateRowSorter(true);
        tbRajal.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbRajal.setComponentPopupMenu(Popup);
        tbRajal.setName("tbRajal"); // NOI18N
        tbRajal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbRajalMouseClicked(evt);
            }
        });
        tbRajal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbRajalKeyPressed(evt);
            }
        });
        rajal.setViewportView(tbRajal);

        TabRawat.addTab("IGD & Operasi/VK", rajal);

        ranap.setComponentPopupMenu(Popup);
        ranap.setName("ranap"); // NOI18N
        ranap.setOpaque(true);

        tbRanap.setAutoCreateRowSorter(true);
        tbRanap.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbRanap.setComponentPopupMenu(Popup);
        tbRanap.setName("tbRanap"); // NOI18N
        tbRanap.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbRanapMouseClicked(evt);
            }
        });
        tbRanap.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbRanapKeyPressed(evt);
            }
        });
        ranap.setViewportView(tbRanap);

        TabRawat.addTab("Rawat Inap", ranap);

        penunjang.setComponentPopupMenu(Popup);
        penunjang.setName("penunjang"); // NOI18N
        penunjang.setOpaque(true);

        tbPenunjang.setAutoCreateRowSorter(true);
        tbPenunjang.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbPenunjang.setComponentPopupMenu(Popup);
        tbPenunjang.setName("tbPenunjang"); // NOI18N
        tbPenunjang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbPenunjangMouseClicked(evt);
            }
        });
        tbPenunjang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbPenunjangKeyPressed(evt);
            }
        });
        penunjang.setViewportView(tbPenunjang);

        TabRawat.addTab("Penunjang", penunjang);

        internalFrame1.add(TabRawat, java.awt.BorderLayout.CENTER);
        TabRawat.getAccessibleContext().setAccessibleName("Rawat Jalan");

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (TabRawat.getSelectedIndex() == 1) {
            if (TNoRw.getText().trim().equals("") || (tbRanap.getRowCount() <= 0)) {
                Valid.textKosong(TNoRw, "Data");
            } else {
                for (i = 0; i < tbRanap.getRowCount(); i++) {
                    if (tbRanap.getValueAt(i, 0).toString().equals("true") && Valid.SetAngka(tbRanap.getValueAt(i, 4).toString()) > 0) {
                        Sequel.menyimpan("pengurangan_biaya", "'" + TNoRw.getText() + "','" 
                                + tbRanap.getValueAt(i, 1).toString()+"#"+tbRanap.getValueAt(i, 2).toString() +"#"+ tbRanap.getValueAt(i, 3).toString()
                                + "','" + tbRanap.getValueAt(i, 4).toString() + "'", "Potongan Biaya");
                    }
                }
                this.dispose();
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
       
        isForm();
        emptTeks();
}//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBatalKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            emptTeks();
        }else{Valid.pindah(evt, BtnSimpan, BtnHapus);}
}//GEN-LAST:event_BtnBatalKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        for(i=0;i<tbRajal.getRowCount();i++){ 
            if(tbRajal.getValueAt(i,0).toString().equals("true")){
                Sequel.mengedit("penjab","kd_pj='"+tbRajal.getValueAt(i,1).toString()+"'","status='0'");
            }
        } 
        BtnCariActionPerformed(evt);
        emptTeks();
}//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnHapusActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnBatal, BtnCari);
        }
}//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
}//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            dispose();
        }else{Valid.pindah(evt,BtnCari,TCari);}
}//GEN-LAST:event_BtnKeluarKeyPressed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            BtnCariActionPerformed(null);
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            BtnCari.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            BtnKeluar.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            tbRajal.requestFocus();
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
            BtnAllActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnCari, TCari);
        }
}//GEN-LAST:event_BtnAllKeyPressed

    private void tbRajalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbRajalMouseClicked
        if(tabModeRajal.getRowCount()!=0){
            try {
            } catch (java.lang.NullPointerException e) {
            }
        }
}//GEN-LAST:event_tbRajalMouseClicked

    private void tbRajalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbRajalKeyPressed
        if(tabModeRajal.getRowCount()!=0){
            if((evt.getKeyCode()==KeyEvent.VK_ENTER)||(evt.getKeyCode()==KeyEvent.VK_UP)||(evt.getKeyCode()==KeyEvent.VK_DOWN)){
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }else if(evt.getKeyCode()==KeyEvent.VK_SHIFT){
                TCari.setText("");
                TCari.requestFocus();
            }
        }
}//GEN-LAST:event_tbRajalKeyPressed

    private void Kd2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Kd2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_Kd2KeyPressed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
       if(TabRawat.getSelectedIndex()==1){
           tampilTindakanRanap();
       }
    }//GEN-LAST:event_formWindowOpened

    private void MnRestoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnRestoreActionPerformed
        DlgRestoreCaraBayar restore=new DlgRestoreCaraBayar(null,true);
        restore.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        restore.setLocationRelativeTo(internalFrame1);
        restore.setVisible(true);
    }//GEN-LAST:event_MnRestoreActionPerformed

    private void TabRawatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TabRawatMouseClicked
       if(TabRawat.getSelectedIndex()==1){
           tampilTindakanRanap();
       }
    }//GEN-LAST:event_TabRawatMouseClicked

    private void tbRanapMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbRanapMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tbRanapMouseClicked

    private void tbRanapKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbRanapKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_tbRanapKeyPressed

    private void tbPenunjangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPenunjangMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tbPenunjangMouseClicked

    private void tbPenunjangKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbPenunjangKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_tbPenunjangKeyPressed

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRwKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TNoRwKeyPressed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgPotonganBiaya dialog = new DlgPotonganBiaya(new javax.swing.JFrame(), true);
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
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnSimpan;
    private widget.TextBox Kd2;
    private widget.Label LCount;
    private javax.swing.JMenuItem MnRestore;
    private widget.Label NoRw;
    private javax.swing.JPopupMenu Popup;
    private widget.TextBox TCari;
    private widget.TextBox TNoRw;
    private javax.swing.JTabbedPane TabRawat;
    private widget.InternalFrame internalFrame1;
    private javax.swing.JPanel jPanel1;
    private widget.Label label10;
    private widget.Label label9;
    private widget.panelisi panelisi1;
    private widget.panelisi panelisi3;
    private widget.ScrollPane penunjang;
    private widget.ScrollPane rajal;
    private widget.ScrollPane ranap;
    private widget.Table tbPenunjang;
    private widget.Table tbRajal;
    private widget.Table tbRanap;
    // End of variables declaration//GEN-END:variables

    private void tampil() {
        if(TabRawat.getSelectedIndex()==1){
            
            tampilTindakanRanap();
        }
        
      
    }

    public void emptTeks() {
       
    }

    private void getData() {
       
    }

    public JTable getTable(){
        return tbRajal;
    }
    
    public void onCari(){        
        TCari.requestFocus();
    }
    
    public void isCek(){
        BtnSimpan.setEnabled(akses.getpotongan_biaya());
        BtnHapus.setEnabled(akses.getpotongan_biaya());
        if(akses.getkode().equals("Admin Utama")){
            MnRestore.setEnabled(true);
        }else{
            MnRestore.setEnabled(false);
        }
    }
    
    private void isForm(){
       
    } 
    
    public void setNoRm(String no_rawat){
        TNoRw.setText(no_rawat);
    }
    
    private void tampilTindakanRanap(){
         Valid.tabelKosong(tabModeRanap);
         try{
            ps=koneksi.prepareStatement(
                    "SELECT t.tgl_perawatan\n" +
                    "FROM ( SELECT no_rawat,kd_jenis_prw, kd_dokter AS id_petugas,tgl_perawatan, biaya_rawat FROM rawat_inap_dr\n" +
                    "UNION ALL SELECT no_rawat, kd_jenis_prw, nip AS id_petugas, tgl_perawatan,  biaya_rawat FROM rawat_inap_pr) t\n" +
                    "WHERE t.no_rawat = ? GROUP BY t.tgl_perawatan" );
            try{
                ps.setString(1,TNoRw.getText().trim());
                rs=ps.executeQuery();
                while(rs.next()){
//                  tabModeRanap.addRow(new Object[]{false,rs.getString("tgl_perawatan"),"","",""});
                    try {
                        psranap=koneksi.prepareStatement(
                            "SELECT CONCAT(t.tgl_perawatan, ' ', t.jam_rawat) AS tgl, CONCAT(p.nik,'#', p.nama) AS petugas, CONCAT(t.kd_jenis_prw,'#', j.nm_perawatan) AS tindakan, t.biaya_rawat\n" +
                            "FROM ( SELECT no_rawat,kd_jenis_prw, kd_dokter AS id_petugas,tgl_perawatan, jam_rawat, biaya_rawat FROM rawat_inap_dr\n" +
                            "UNION ALL SELECT no_rawat, kd_jenis_prw, nip AS id_petugas, tgl_perawatan,jam_rawat,  biaya_rawat FROM rawat_inap_pr) t\n" +
                            "INNER JOIN jns_perawatan_inap j ON j.kd_jenis_prw = t.kd_jenis_prw\n" +
                            "LEFT JOIN pegawai p ON p.nik = t.id_petugas\n" +
                            "WHERE t.no_rawat = ? AND t.tgl_perawatan = ?" );
                        try {
                              psranap.setString(1,TNoRw.getText().trim());
                              psranap.setString(2,rs.getString("tgl_perawatan"));
                              rsranap=psranap.executeQuery();
                              while(rsranap.next()){
                                tabModeRanap.addRow(new Object[]{false,rsranap.getString("tgl"),rsranap.getString("tindakan"),rsranap.getString("petugas"),rsranap.getString("biaya_rawat")});
                              }
                        } catch (Exception e) {
                             System.out.println(e);
                        }finally{
                            if(rsranap!=null){
                                rsranap.close();
                            }
                            if(psranap!=null){
                                psranap.close();
                            }
                        }
                    } catch (Exception e) {
                         System.out.println(e);
                    }
                }
            }catch(Exception ex){
                System.out.println(ex);
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
    }
    
}
