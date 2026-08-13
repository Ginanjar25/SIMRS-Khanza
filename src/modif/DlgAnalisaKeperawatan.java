/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DlgPemberianObat.java
 *
 * Created on 27 Mei 10, 14:52:31
 */

package modif;

import com.fasterxml.jackson.databind.JsonNode;
import fungsi.WarnaTable;
import laporan.*;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.Date;
import javax.swing.JOptionPane;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author perpustakaan
 */
public class DlgAnalisaKeperawatan extends javax.swing.JDialog {
    private validasi Valid=new validasi();
    private final sekuel Sequel=new sekuel();
     private final DefaultTableModel tabModeAnalisa,tabModeEtiologi, tabModeRencana, tabModeImplementasi;
    private Connection koneksi=koneksiDB.condb();
    private PreparedStatement psanalisa,psrencana, psetiologi, psimplementasi;
    private ResultSet rs;
    private int jml=0,i=0,index=0;
    private String[] kodeanalisa,namaanalisa, kodeetiologi, namaetiologi,koderencana, namarencana, kodeimplementasi, namaimplementasi;
    private boolean[] pilihanalisa, pilihetiologi, pilihrencana, pilihimplementasi;
    public String norawat="",status="",norm="",tanggal1="",tanggal2="",keyword="";
    
    

    /** Creates new form DlgPemberianObat
     * @param parent
     * @param modal */
    public DlgAnalisaKeperawatan(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8,1);
        setSize(885,674);
        
        
        tabModeAnalisa=new DefaultTableModel(null,new Object[]{
            "P","Kode","Analisa Keperawatan"}){
            @Override public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = false;
                if (colIndex==0) {
                    a=true;
                }
                return a;
             }
             Class[] types = new Class[] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        tbAnalisa.setModel(tabModeAnalisa);
        //tbPenyakit.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbPenyakit.getBackground()));
        tbAnalisa.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbAnalisa.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (i= 0; i < 3; i++) {
            TableColumn column = tbAnalisa.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(20);
            }else if(i==1){
                column.setPreferredWidth(80);
            }else if(i==2){
                column.setPreferredWidth(400);
            }
        }
        tbAnalisa.setDefaultRenderer(Object.class, new WarnaTable());
        
        tabModeEtiologi=new DefaultTableModel(null,new Object[]{
            "P","Kode","Etiologi"}){
            @Override public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = false;
                if (colIndex==0) {
                    a=true;
                }
                return a;
             }
             Class[] types = new Class[] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        tbEtiologi.setModel(tabModeEtiologi);
        //tbPenyakit.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbPenyakit.getBackground()));
        tbEtiologi.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbEtiologi.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (i= 0; i < 3; i++) {
            TableColumn column = tbEtiologi.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(20);
            }else if(i==1){
                column.setPreferredWidth(80);
            }else if(i==2){
                column.setPreferredWidth(400);
            }
        }
        tbEtiologi.setDefaultRenderer(Object.class, new WarnaTable());
        
        tabModeRencana=new DefaultTableModel(null,new Object[]{
            "P","Kode","Perencanaan (Intervensi)"}){
            @Override public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = false;
                if (colIndex==0) {
                    a=true;
                }
                return a;
             }
             Class[] types = new Class[] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        tbRencana.setModel(tabModeRencana);
        //tbPenyakit.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbPenyakit.getBackground()));
        tbRencana.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbRencana.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (i= 0; i < 3; i++) {
            TableColumn column = tbRencana.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(20);
            }else if(i==1){
                column.setPreferredWidth(80);
            }else if(i==2){
                column.setPreferredWidth(400);
            }
        }
        tbRencana.setDefaultRenderer(Object.class, new WarnaTable());
        
        tabModeImplementasi=new DefaultTableModel(null,new Object[]{
            "P","Kode","Implementasi"}){
            @Override public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = false;
                if (colIndex==0) {
                    a=true;
                }
                return a;
             }
             Class[] types = new Class[] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        tbImplementasi.setModel(tabModeImplementasi);
        //tbPenyakit.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbPenyakit.getBackground()));
        tbImplementasi.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbImplementasi.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (i= 0; i < 3; i++) {
            TableColumn column = tbImplementasi.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(20);
            }else if(i==1){
                column.setPreferredWidth(80);
            }else if(i==2){
                column.setPreferredWidth(400);
            }
        }
        tbImplementasi.setDefaultRenderer(Object.class, new WarnaTable());
           
        Analisa.setDocument(new batasInput((byte)100).getKata(Analisa));
        Etiologi.setDocument(new batasInput((byte)100).getKata(Etiologi));
        Rencana.setDocument(new batasInput((byte)100).getKata(Rencana));
        Implementasi.setDocument(new batasInput((byte)100).getKata(Implementasi));
        
        if(koneksiDB.CARICEPAT().equals("aktif")){
            Analisa.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if(Analisa.getText().length()>2){
                        tampilanalisa();
                    }
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    if(Analisa.getText().length()>2){
                        tampilanalisa();
                    }
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    if(Analisa.getText().length()>2){
                        tampilanalisa();
                    }
                }
            });
        } 
    }

    //private DlgCariObatPenyakit dlgobtpny=new DlgCariObatPenyakit(null,false);
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        internalFrame1 = new widget.InternalFrame();
        panelisi1 = new widget.panelisi();
        TabRawat = new javax.swing.JTabbedPane();
        ScrollInput = new widget.ScrollPane();
        FormData = new widget.PanelBiasa();
        jLabel13 = new widget.Label();
        Analisa = new widget.TextBox();
        BtnCariAnalisa = new widget.Button();
        btnTambahAnalisa = new widget.Button();
        Scroll1 = new widget.ScrollPane();
        tbAnalisa = new widget.Table();
        jLabel14 = new widget.Label();
        Etiologi = new widget.TextBox();
        BtnCariEtiologi = new widget.Button();
        btnTambahEtiologi = new widget.Button();
        ScrollEtiologi = new widget.ScrollPane();
        tbEtiologi = new widget.Table();
        jLabel15 = new widget.Label();
        Rencana = new widget.TextBox();
        BtnCariRencana = new widget.Button();
        btnTambahRencana = new widget.Button();
        ScrollRencana = new widget.ScrollPane();
        tbRencana = new widget.Table();
        jLabel16 = new widget.Label();
        Implementasi = new widget.TextBox();
        BtnCariImplementasi = new widget.Button();
        btnTambahImplementasi = new widget.Button();
        ScrollImplementasi = new widget.ScrollPane();
        tbImplementasi = new widget.Table();
        panelGlass8 = new widget.panelisi();
        BtnAll = new widget.Button();
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

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Master Analisa Keperawatan ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        panelisi1.setName("panelisi1"); // NOI18N
        panelisi1.setLayout(new java.awt.BorderLayout(1, 1));

        TabRawat.setBackground(new java.awt.Color(255, 255, 253));
        TabRawat.setForeground(new java.awt.Color(50, 50, 50));
        TabRawat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        TabRawat.setName("TabRawat"); // NOI18N
        TabRawat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TabRawatMouseClicked(evt);
            }
        });

        ScrollInput.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        ScrollInput.setName("ScrollInput"); // NOI18N
        ScrollInput.setOpaque(true);

        FormData.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        FormData.setName("FormData"); // NOI18N
        FormData.setPreferredSize(new java.awt.Dimension(865, 417));
        FormData.setLayout(null);

        jLabel13.setText("Analisa Keperawatan :");
        jLabel13.setName("jLabel13"); // NOI18N
        FormData.add(jLabel13);
        jLabel13.setBounds(10, 10, 120, 23);

        Analisa.setHighlighter(null);
        Analisa.setName("Analisa"); // NOI18N
        Analisa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                AnalisaKeyPressed(evt);
            }
        });
        FormData.add(Analisa);
        Analisa.setBounds(131, 10, 290, 23);

        BtnCariAnalisa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCariAnalisa.setMnemonic('1');
        BtnCariAnalisa.setToolTipText("Alt+1");
        BtnCariAnalisa.setName("BtnCariAnalisa"); // NOI18N
        BtnCariAnalisa.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCariAnalisa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariAnalisaActionPerformed(evt);
            }
        });
        FormData.add(BtnCariAnalisa);
        BtnCariAnalisa.setBounds(420, 10, 28, 23);

        btnTambahAnalisa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/plus_16.png"))); // NOI18N
        btnTambahAnalisa.setMnemonic('2');
        btnTambahAnalisa.setToolTipText("Alt+2");
        btnTambahAnalisa.setName("btnTambahAnalisa"); // NOI18N
        btnTambahAnalisa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahAnalisaActionPerformed(evt);
            }
        });
        FormData.add(btnTambahAnalisa);
        btnTambahAnalisa.setBounds(450, 10, 28, 23);

        Scroll1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll1.setName("Scroll1"); // NOI18N
        Scroll1.setOpaque(true);

        tbAnalisa.setName("tbAnalisa"); // NOI18N
        tbAnalisa.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbAnalisaMouseClicked(evt);
            }
        });
        Scroll1.setViewportView(tbAnalisa);

        FormData.add(Scroll1);
        Scroll1.setBounds(10, 40, 470, 240);

        jLabel14.setText("Etiologi :");
        jLabel14.setName("jLabel14"); // NOI18N
        FormData.add(jLabel14);
        jLabel14.setBounds(500, 10, 120, 23);

        Etiologi.setHighlighter(null);
        Etiologi.setName("Etiologi"); // NOI18N
        Etiologi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                EtiologiKeyPressed(evt);
            }
        });
        FormData.add(Etiologi);
        Etiologi.setBounds(630, 10, 250, 23);

        BtnCariEtiologi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCariEtiologi.setMnemonic('1');
        BtnCariEtiologi.setToolTipText("Alt+1");
        BtnCariEtiologi.setName("BtnCariEtiologi"); // NOI18N
        BtnCariEtiologi.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCariEtiologi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariEtiologiActionPerformed(evt);
            }
        });
        FormData.add(BtnCariEtiologi);
        BtnCariEtiologi.setBounds(880, 10, 28, 23);

        btnTambahEtiologi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/plus_16.png"))); // NOI18N
        btnTambahEtiologi.setMnemonic('2');
        btnTambahEtiologi.setToolTipText("Alt+2");
        btnTambahEtiologi.setName("btnTambahEtiologi"); // NOI18N
        btnTambahEtiologi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahEtiologiActionPerformed(evt);
            }
        });
        FormData.add(btnTambahEtiologi);
        btnTambahEtiologi.setBounds(910, 10, 28, 23);

        ScrollEtiologi.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        ScrollEtiologi.setName("ScrollEtiologi"); // NOI18N
        ScrollEtiologi.setOpaque(true);

        tbEtiologi.setName("tbEtiologi"); // NOI18N
        ScrollEtiologi.setViewportView(tbEtiologi);

        FormData.add(ScrollEtiologi);
        ScrollEtiologi.setBounds(510, 40, 420, 240);

        jLabel15.setText(" Rencana (Intervensi) :");
        jLabel15.setName("jLabel15"); // NOI18N
        FormData.add(jLabel15);
        jLabel15.setBounds(20, 300, 120, 20);

        Rencana.setHighlighter(null);
        Rencana.setName("Rencana"); // NOI18N
        Rencana.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RencanaKeyPressed(evt);
            }
        });
        FormData.add(Rencana);
        Rencana.setBounds(140, 300, 290, 23);

        BtnCariRencana.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCariRencana.setMnemonic('1');
        BtnCariRencana.setToolTipText("Alt+1");
        BtnCariRencana.setName("BtnCariRencana"); // NOI18N
        BtnCariRencana.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCariRencana.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariRencanaActionPerformed(evt);
            }
        });
        FormData.add(BtnCariRencana);
        BtnCariRencana.setBounds(430, 300, 28, 23);

        btnTambahRencana.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/plus_16.png"))); // NOI18N
        btnTambahRencana.setMnemonic('2');
        btnTambahRencana.setToolTipText("Alt+2");
        btnTambahRencana.setName("btnTambahRencana"); // NOI18N
        btnTambahRencana.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahRencanaActionPerformed(evt);
            }
        });
        FormData.add(btnTambahRencana);
        btnTambahRencana.setBounds(460, 300, 28, 23);

        ScrollRencana.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        ScrollRencana.setName("ScrollRencana"); // NOI18N
        ScrollRencana.setOpaque(true);

        tbRencana.setName("tbRencana"); // NOI18N
        tbRencana.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbRencanaMouseClicked(evt);
            }
        });
        ScrollRencana.setViewportView(tbRencana);

        FormData.add(ScrollRencana);
        ScrollRencana.setBounds(10, 330, 470, 250);

        jLabel16.setText("Implementasi  :");
        jLabel16.setName("jLabel16"); // NOI18N
        FormData.add(jLabel16);
        jLabel16.setBounds(500, 300, 120, 23);

        Implementasi.setHighlighter(null);
        Implementasi.setName("Implementasi"); // NOI18N
        Implementasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ImplementasiKeyPressed(evt);
            }
        });
        FormData.add(Implementasi);
        Implementasi.setBounds(630, 300, 250, 23);

        BtnCariImplementasi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCariImplementasi.setMnemonic('1');
        BtnCariImplementasi.setToolTipText("Alt+1");
        BtnCariImplementasi.setName("BtnCariImplementasi"); // NOI18N
        BtnCariImplementasi.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCariImplementasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariImplementasiActionPerformed(evt);
            }
        });
        FormData.add(BtnCariImplementasi);
        BtnCariImplementasi.setBounds(880, 300, 28, 23);

        btnTambahImplementasi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/plus_16.png"))); // NOI18N
        btnTambahImplementasi.setMnemonic('2');
        btnTambahImplementasi.setToolTipText("Alt+2");
        btnTambahImplementasi.setName("btnTambahImplementasi"); // NOI18N
        btnTambahImplementasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahImplementasiActionPerformed(evt);
            }
        });
        FormData.add(btnTambahImplementasi);
        btnTambahImplementasi.setBounds(910, 300, 28, 23);

        ScrollImplementasi.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        ScrollImplementasi.setName("ScrollImplementasi"); // NOI18N
        ScrollImplementasi.setOpaque(true);

        tbImplementasi.setName("tbImplementasi"); // NOI18N
        tbImplementasi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbImplementasiMouseClicked(evt);
            }
        });
        ScrollImplementasi.setViewportView(tbImplementasi);

        FormData.add(ScrollImplementasi);
        ScrollImplementasi.setBounds(510, 330, 420, 250);

        ScrollInput.setViewportView(FormData);

        TabRawat.addTab("Input Data", ScrollInput);

        panelisi1.add(TabRawat, java.awt.BorderLayout.CENTER);

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 54));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

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

        panelisi1.add(panelGlass8, java.awt.BorderLayout.PAGE_END);

        internalFrame1.add(panelisi1, java.awt.BorderLayout.CENTER);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        tampilanalisa();
    }//GEN-LAST:event_formWindowOpened

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated

    }//GEN-LAST:event_formWindowActivated

    private void AnalisaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_AnalisaKeyPressed

    }//GEN-LAST:event_AnalisaKeyPressed

    private void BtnCariAnalisaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariAnalisaActionPerformed
        tampilanalisa();
    }//GEN-LAST:event_BtnCariAnalisaActionPerformed

    private void btnTambahAnalisaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahAnalisaActionPerformed
       
    }//GEN-LAST:event_btnTambahAnalisaActionPerformed

    private void EtiologiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_EtiologiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_EtiologiKeyPressed

    private void BtnCariEtiologiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariEtiologiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnCariEtiologiActionPerformed

    private void btnTambahEtiologiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahEtiologiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambahEtiologiActionPerformed

    private void RencanaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_RencanaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_RencanaKeyPressed

    private void BtnCariRencanaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariRencanaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnCariRencanaActionPerformed

    private void btnTambahRencanaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahRencanaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambahRencanaActionPerformed

    private void ImplementasiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ImplementasiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_ImplementasiKeyPressed

    private void BtnCariImplementasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariImplementasiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnCariImplementasiActionPerformed

    private void btnTambahImplementasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahImplementasiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTambahImplementasiActionPerformed

    private void TabRawatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TabRawatMouseClicked

    }//GEN-LAST:event_TabRawatMouseClicked

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
   
    }//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
      
    }//GEN-LAST:event_BtnAllKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
     
    }//GEN-LAST:event_BtnKeluarKeyPressed

    private void tbAnalisaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbAnalisaMouseClicked
       tampilEtiologi();
       tampilRencana();
    }//GEN-LAST:event_tbAnalisaMouseClicked

    private void tbImplementasiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbImplementasiMouseClicked
        
    }//GEN-LAST:event_tbImplementasiMouseClicked

    private void tbRencanaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbRencanaMouseClicked
       tampilImplementasi();
    }//GEN-LAST:event_tbRencanaMouseClicked

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgAnalisaKeperawatan dialog = new DlgAnalisaKeperawatan(new javax.swing.JFrame(), true);
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
    public widget.TextBox Analisa;
    private widget.Button BtnAll;
    private widget.Button BtnCariAnalisa;
    private widget.Button BtnCariEtiologi;
    private widget.Button BtnCariImplementasi;
    private widget.Button BtnCariRencana;
    private widget.Button BtnKeluar;
    public widget.TextBox Etiologi;
    public widget.PanelBiasa FormData;
    public widget.TextBox Implementasi;
    public widget.TextBox Rencana;
    private widget.ScrollPane Scroll1;
    private widget.ScrollPane ScrollEtiologi;
    private widget.ScrollPane ScrollImplementasi;
    public widget.ScrollPane ScrollInput;
    private widget.ScrollPane ScrollRencana;
    public javax.swing.JTabbedPane TabRawat;
    public widget.Button btnTambahAnalisa;
    public widget.Button btnTambahEtiologi;
    public widget.Button btnTambahImplementasi;
    public widget.Button btnTambahRencana;
    private javax.swing.ButtonGroup buttonGroup1;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel13;
    private widget.Label jLabel14;
    private widget.Label jLabel15;
    private widget.Label jLabel16;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelisi1;
    public widget.Table tbAnalisa;
    public widget.Table tbEtiologi;
    public widget.Table tbImplementasi;
    public widget.Table tbRencana;
    // End of variables declaration//GEN-END:variables


    public int getRecord(){
        if(TabRawat.getSelectedIndex()==0){
            i=0;
        }
        return i;
    }
    
    private void tampilanalisa() {
        try{
            jml=0;
            for(i=0;i<tbAnalisa.getRowCount();i++){
                if(tbAnalisa.getValueAt(i,0).toString().equals("true")){
                    jml++;
                }
            }

            pilihanalisa=null;
            pilihanalisa=new boolean[jml];
            kodeanalisa=null;
            kodeanalisa=new String[jml];
            namaanalisa=null;
            namaanalisa=new String[jml];

            index=0; 
            for(i=0;i<tbAnalisa.getRowCount();i++){
                if(tbAnalisa.getValueAt(i,0).toString().equals("true")){
                    pilihanalisa[index]=true;
                    kodeanalisa[index]=tbAnalisa.getValueAt(i,1).toString();
                    namaanalisa[index]=tbAnalisa.getValueAt(i,2).toString();
                    index++;
                }
            }

            Valid.tabelKosong(tabModeAnalisa);
            for(i=0;i<jml;i++){
                tabModeAnalisa.addRow(new Object[] {pilihanalisa[i],kodeanalisa[i],namaanalisa[i]});
            }       

            psanalisa=koneksi.prepareStatement("select kode_masalah, nama_masalah from master_masalah_keperawatan where nama_masalah like ? ");
            try {
                psanalisa.setString(1,"%"+Analisa.getText().trim()+"%");
                rs=psanalisa.executeQuery();
                while(rs.next()){
                    tabModeAnalisa.addRow(new Object[]{false,rs.getString(1),rs.getString(2)});
                } 
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            } finally{
                if(rs!=null){
                    rs.close();
                }
                if(psanalisa!=null){
                    psanalisa.close();
                }
            }           
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }
    }
    
    
    private void tampilEtiologi(){
        try{
            // 1. Simpan dulu baris etiologi yang SUDAH tercentang di tbEtiologi (supaya tidak hilang saat refresh)
            jml=0;
            for(i=0;i<tbEtiologi.getRowCount();i++){
                if(tbEtiologi.getValueAt(i,0).toString().equals("true")){
                    jml++;
                }
            }
            pilihetiologi=null;
            pilihetiologi=new boolean[jml];
            kodeetiologi=null;
            kodeetiologi=new String[jml];
            namaetiologi=null;
            namaetiologi=new String[jml];
            index=0;
            for(i=0;i<tbEtiologi.getRowCount();i++){
                if(tbEtiologi.getValueAt(i,0).toString().equals("true")){
                    pilihetiologi[index]=true;
                    kodeetiologi[index]=tbEtiologi.getValueAt(i,1).toString();
                    namaetiologi[index]=tbEtiologi.getValueAt(i,2).toString();
                    index++;
                }
            }
            Valid.tabelKosong(tabModeEtiologi);
            for(i=0;i<jml;i++){
                tabModeEtiologi.addRow(new Object[] {pilihetiologi[i],kodeetiologi[i],namaetiologi[i]});
            }

            // 2. Kumpulkan kode_masalah dari baris tbAnalisa yang SEDANG tercentang
            java.util.List<String> kodeMasalahTercentang = new java.util.ArrayList<>();
            for(i=0;i<tbAnalisa.getRowCount();i++){
                if(tbAnalisa.getValueAt(i,0).toString().equals("true")){
                    kodeMasalahTercentang.add(tbAnalisa.getValueAt(i,1).toString());
                }
            }

            // 3. Kalau tidak ada analisa yang tercentang, tidak perlu tampilkan etiologi apapun
            if(kodeMasalahTercentang.isEmpty()){
                return;
            }

            // 4. Bangun placeholder IN (?,?,?...) sejumlah kode_masalah tercentang
            StringBuilder placeholder = new StringBuilder();
            for(int k=0;k<kodeMasalahTercentang.size();k++){
                placeholder.append(k==0 ? "?" : ",?");
            }

            psetiologi = koneksi.prepareStatement(
                "select kode_etiologi, etiologi_keperawatan from master_etiologi_keperawatan " +
                "where kode_masalah in ("+placeholder+") and etiologi_keperawatan like ? " +
                "order by kode_etiologi"
            );
            try {
                int p=1;
                for(String kd : kodeMasalahTercentang){
                    psetiologi.setString(p++, kd);
                }
                psetiologi.setString(p, "%"+Etiologi.getText().trim()+"%");

                rs=psetiologi.executeQuery();
                while(rs.next()){
                    // hindari duplikat dengan baris yang sudah tercentang di atas
                    boolean sudahAda=false;
                    for(String kd : kodeetiologi){
                        if(kd!=null && kd.equals(rs.getString(1))){
                            sudahAda=true;
                            break;
                        }
                    }
                    if(!sudahAda){
                        tabModeEtiologi.addRow(new Object[]{false, rs.getString(1), rs.getString(2)});
                    }
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            } finally{
                if(rs!=null){
                    rs.close();
                }
                if(psetiologi!=null){
                    psetiologi.close();
                }
            }
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }
    }
    
    private void tampilRencana(){
        try{
            // 1. Simpan dulu baris etiologi yang SUDAH tercentang di tbEtiologi (supaya tidak hilang saat refresh)
            jml=0;
            for(i=0;i<tbRencana.getRowCount();i++){
                if(tbRencana.getValueAt(i,0).toString().equals("true")){
                    jml++;
                }
            }
            pilihrencana=null;
            pilihrencana=new boolean[jml];
            koderencana=null;
            koderencana=new String[jml];
            namarencana=null;
            namarencana=new String[jml];
            index=0;
            for(i=0;i<tbRencana.getRowCount();i++){
                if(tbRencana.getValueAt(i,0).toString().equals("true")){
                    pilihrencana[index]=true;
                    koderencana[index]=tbRencana.getValueAt(i,1).toString();
                    namarencana[index]=tbRencana.getValueAt(i,2).toString();
                    index++;
                }
            }
            Valid.tabelKosong(tabModeRencana);
            for(i=0;i<jml;i++){
                tabModeRencana.addRow(new Object[] {pilihrencana[i],koderencana[i],namarencana[i]});
            }

            // 2. Kumpulkan kode_masalah dari baris tbAnalisa yang SEDANG tercentang
            java.util.List<String> kodeAnalisaTercentang = new java.util.ArrayList<>();
            for(i=0;i<tbAnalisa.getRowCount();i++){
                if(tbAnalisa.getValueAt(i,0).toString().equals("true")){
                    kodeAnalisaTercentang.add(tbAnalisa.getValueAt(i,1).toString());
                }
            }

            // 3. Kalau tidak ada analisa yang tercentang, tidak perlu tampilkan etiologi apapun
            if(kodeAnalisaTercentang.isEmpty()){
                return;
            }

            // 4. Bangun placeholder IN (?,?,?...) sejumlah kode_masalah tercentang
            StringBuilder placeholder = new StringBuilder();
            for(int k=0;k<kodeAnalisaTercentang.size();k++){
                placeholder.append(k==0 ? "?" : ",?");
            }

            psrencana = koneksi.prepareStatement(
                "select kode_rencana, rencana_keperawatan from master_rencana_keperawatan " +
                "where kode_masalah in ("+placeholder+") and rencana_keperawatan like ? " +
                "order by kode_rencana"
            );
            try {
                int p=1;
                for(String kd : kodeAnalisaTercentang){
                    psrencana.setString(p++, kd);
                }
                psrencana.setString(p, "%"+Rencana.getText().trim()+"%");

                rs=psrencana.executeQuery();
                 while(rs.next()){
                    String rencanaLengkap = rs.getString("rencana_keperawatan");
                    // Hilangkan 3 karakter awal, mis. "[O]" atau "[E]", jika panjangnya cukup
                    String rencanaBersih = rencanaLengkap.length() > 3
                            ? rencanaLengkap.substring(3)
                            : rencanaLengkap;
 
                    boolean sudahAda=false;
                    for(String kd : koderencana){
                        if(kd!=null && kd.equals(rs.getString("kode_rencana"))){
                            sudahAda=true;
                            break;
                        }
                    }
                    if(!sudahAda){
                        tabModeRencana.addRow(new Object[]{false, rs.getString("kode_rencana"), rencanaBersih});
                    }
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            } finally{
                if(rs!=null){
                    rs.close();
                }
                if(psrencana!=null){
                    psrencana.close();
                }
            }
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }
    }
    
    private void tampilImplementasi(){
        try{
            // 1. Kumpulkan kode_masalah dari baris tbAnalisa yang SEDANG tercentang
            java.util.List<String> kodeAnalisaTercentang = new java.util.ArrayList<>();
            for(i=0;i<tbAnalisa.getRowCount();i++){
                if(tbAnalisa.getValueAt(i,0).toString().equals("true")){
                    kodeAnalisaTercentang.add(tbAnalisa.getValueAt(i,1).toString());
                }
            }

            // 2. Kumpulkan kode_rencana dari baris tbRencana yang SEDANG tercentang
            java.util.List<String> kodeRencanaTercentang = new java.util.ArrayList<>();
            for(i=0;i<tbRencana.getRowCount();i++){
                if(tbRencana.getValueAt(i,0).toString().equals("true")){
                    kodeRencanaTercentang.add(tbRencana.getValueAt(i,1).toString());
                }
            }

            // 3. Kalau salah satu (analisa ATAU rencana) tidak ada yang tercentang,
            //    implementasi harus hilang TOTAL - termasuk yang sebelumnya sudah tercentang.
            if(kodeAnalisaTercentang.isEmpty() || kodeRencanaTercentang.isEmpty()){
                Valid.tabelKosong(tabModeImplementasi);
                return;
            }

            // 4. Baru sekarang aman untuk preservasi baris implementasi yang SUDAH tercentang
            jml=0;
            for(i=0;i<tbImplementasi.getRowCount();i++){
                if(tbImplementasi.getValueAt(i,0).toString().equals("true")){
                    jml++;
                }
            }
            pilihimplementasi=null;
            pilihimplementasi=new boolean[jml];
            kodeimplementasi=null;
            kodeimplementasi=new String[jml];
            namaimplementasi=null;
            namaimplementasi=new String[jml];
            index=0;
            for(i=0;i<tbImplementasi.getRowCount();i++){
                if(tbImplementasi.getValueAt(i,0).toString().equals("true")){
                    pilihimplementasi[index]=true;
                    kodeimplementasi[index]=tbImplementasi.getValueAt(i,1).toString();
                    namaimplementasi[index]=tbImplementasi.getValueAt(i,2).toString();
                    index++;
                }
            }
            Valid.tabelKosong(tabModeImplementasi);
            for(i=0;i<jml;i++){
                tabModeImplementasi.addRow(new Object[] {pilihimplementasi[i],kodeimplementasi[i],namaimplementasi[i]});
            }

            // Placeholder IN (?,?,?...) untuk kode_masalah
            StringBuilder placeholder = new StringBuilder();
            for(int k=0;k<kodeAnalisaTercentang.size();k++){
                placeholder.append(k==0 ? "?" : ",?");
            }

            // Placeholder IN (?,?,?...) untuk kode_rencana
            StringBuilder placeholderRencana = new StringBuilder();
            for(int k=0;k<kodeRencanaTercentang.size();k++){
                placeholderRencana.append(k==0 ? "?" : ",?");
            }

            psimplementasi = koneksi.prepareStatement(
                "select kode_implementasi, implementasi_keperawatan from master_implementasi_keperawatan " +
                "where kode_masalah in ("+placeholder+") and kode_rencana in ("+placeholderRencana+") and implementasi_keperawatan like ? " +
                "order by kode_implementasi"
            );
            try {
                int p=1;
                // bind kode_masalah (dari analisa yang tercentang)
                for(String kd : kodeAnalisaTercentang){
                    psimplementasi.setString(p++, kd);
                }
                // bind kode_rencana (dari rencana yang tercentang)
                for(String kd : kodeRencanaTercentang){
                    psimplementasi.setString(p++, kd);
                }
                // bind kata kunci pencarian implementasi
                psimplementasi.setString(p, "%"+Implementasi.getText().trim()+"%");

                rs=psimplementasi.executeQuery();
                while(rs.next()){
                    // hindari duplikat dengan baris yang sudah tercentang di atas
                    boolean sudahAda=false;
                    for(String kd : kodeimplementasi){
                        if(kd!=null && kd.equals(rs.getString(1))){
                            sudahAda=true;
                            break;
                        }
                    }
                    if(!sudahAda){
                        tabModeImplementasi.addRow(new Object[]{true, rs.getString(1), rs.getString(2)});
                    }
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            } finally{
                if(rs!=null){
                    rs.close();
                }
                if(psimplementasi!=null){
                    psimplementasi.close();
                }
            }
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }
    }
}
