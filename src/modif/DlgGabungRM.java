/*
 * DlgGabungRM.java
 *
 * Menggabungkan 2 No.R.M pasien. Data pasien yang dicentang pada No.R.M asal
 * disalin ke No.R.M tujuan, seluruh riwayat No.R.M asal dipindah ke
 * No.R.M tujuan, kemudian No.R.M asal dihapus.
 */

package modif;

import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author perpustakaan
 */
public class DlgGabungRM extends javax.swing.JDialog {
    private validasi Valid=new validasi();
    private final sekuel Sequel=new sekuel();
    private final DefaultTableModel tabModeNoRMAsal,tabModeNoRMTujuan;
    private Connection koneksi=koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i=0;
    private final widget.CekBox ChkSemua=new widget.CekBox();
    // {label, kolom tabel pasien yang diupdate, kolom hasil query yang ditampilkan}
    private final String[][] kolompasien={
        {"Nama Pasien","nm_pasien","nm_pasien"},
        {"No.KTP/SIM","no_ktp","no_ktp"},
        {"Jenis Kelamin","jk","jk"},
        {"Tempat Lahir","tmp_lahir","tmp_lahir"},
        {"Tgl.Lahir","tgl_lahir","tgl_lahir"},
        {"Nama Ibu","nm_ibu","nm_ibu"},
        {"Alamat","alamat","alamat"},
        {"Kelurahan","kd_kel","nm_kel"},
        {"Kecamatan","kd_kec","nm_kec"},
        {"Kabupaten","kd_kab","nm_kab"},
        {"Propinsi","kd_prop","nm_prop"},
        {"Gol.Darah","gol_darah","gol_darah"},
        {"Pekerjaan","pekerjaan","pekerjaan"},
        {"Stts.Nikah","stts_nikah","stts_nikah"},
        {"Agama","agama","agama"},
        {"Tgl.Daftar","tgl_daftar","tgl_daftar"},
        {"No.Telp","no_tlp","no_tlp"},
        {"Umur","umur","umur"},
        {"Pendidikan","pnd","pnd"},
        {"Keluarga","keluarga","keluarga"},
        {"Nama Keluarga","namakeluarga","namakeluarga"},
        {"Cara Bayar","kd_pj","png_jawab"},
        {"No.Peserta","no_peserta","no_peserta"},
        {"Pekerjaan P.J.","pekerjaanpj","pekerjaanpj"},
        {"Alamat P.J.","alamatpj","alamatpj"},
        {"Kelurahan P.J.","kelurahanpj","kelurahanpj"},
        {"Kecamatan P.J.","kecamatanpj","kecamatanpj"},
        {"Kabupaten P.J.","kabupatenpj","kabupatenpj"},
        {"Propinsi P.J.","propinsipj","propinsipj"},
        {"Instansi/Perusahaan","perusahaan_pasien","nama_perusahaan"},
        {"Bahasa","bahasa_pasien","nama_bahasa"},
        {"Suku Bangsa","suku_bangsa","nama_suku_bangsa"},
        {"NIP/NRP","nip","nip"},
        {"Email","email","email"},
        {"Cacat Fisik","cacat_fisik","nama_cacat"}
    };
    // {tabel, kolom no.rm} yang riwayatnya dipindah dari No.R.M asal ke No.R.M tujuan
    private final String[][] tabelriwayat={
        {"bayar_piutang","no_rkm_medis"},{"booking_periksa_diterima","no_rkm_medis"},
        {"booking_registrasi","no_rkm_medis"},{"bridging_dukcapil","no_rkm_medis"},
        {"catatan_pasien","no_rkm_medis"},{"diagnosa_corona","no_rkm_medis"},
        {"pasien_bayi","no_rkm_medis"},{"pasien_corona","no_rkm_medis"},
        {"pasien_mati","no_rkm_medis"},{"pasien_polri","no_rkm_medis"},
        {"pasien_tni","no_rkm_medis"},{"pcare_peserta_kegiatan_kelompok","no_rkm_medis"},
        {"peminjaman_berkas","no_rkm_medis"},{"pengaduan","no_rkm_medis"},
        {"penjualan","no_rkm_medis"},{"personal_pasien","no_rkm_medis"},
        {"piutang","no_rkm_medis"},{"piutang_pasien","no_rkm_medis"},
        {"referensi_mobilejkn_bpjs_batal","no_rkm_medis"},{"reg_periksa","no_rkm_medis"},
        {"retensi_pasien","no_rkm_medis"},{"returjual","no_rkm_medis"},
        {"riwayat_imunisasi","no_rkm_medis"},{"riwayat_persalinan_pasien","no_rkm_medis"},
        {"returpiutang","no_rkm_medis"},{"rujukanranap_dokter_rs","no_rkm_medis"},
        {"sidikjaripasien","no_rkm_medis"},{"skdp_bpjs","no_rkm_medis"},
        {"skrining_rawat_jalan","no_rkm_medis"},{"tagihan_bpd_jateng","no_rkm_medis"},
        {"referensi_mobilejkn_bpjs","norm"},{"side_db.reg_periksa_website","norm"}
    };
    private final String[] nilaiasal=new String[kolompasien.length],
            tampilasal=new String[kolompasien.length],
            tampiltujuan=new String[kolompasien.length];
    private String normasal="",normtujuan="";

    /** Creates new form DlgGabungRM
     * @param parent
     * @param modal */
    public DlgGabungRM(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        // ukuran awal mengikuti layar, bisa ditimpa pemanggil (mis. mengikuti ukuran form induk)
        Dimension layar=java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(Math.max(800,layar.width-40),Math.max(500,layar.height-80));
        setLocationRelativeTo(null);

        tabModeNoRMAsal=new DefaultTableModel(null,new Object[]{
            "P","Data Pasien","Isi No.R.M Asal"}){
            @Override public boolean isCellEditable(int rowIndex, int colIndex){
                return colIndex==0;
             }
             Class[] types = new Class[] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        tbPasienAsal.setModel(tabModeNoRMAsal);
        tbPasienAsal.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbPasienAsal.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        for (i= 0; i < 3; i++) {
            TableColumn column = tbPasienAsal.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(20);
                column.setMinWidth(20);
                column.setMaxWidth(25);
            }else if(i==1){
                column.setPreferredWidth(120);
            }else if(i==2){
                column.setPreferredWidth(300);
            }
        }
        tbPasienAsal.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeNoRMTujuan=new DefaultTableModel(null,new Object[]{
            "Data Pasien","Isi No.R.M Tujuan","Hasil Setelah Digabung"}){
            @Override public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
             }
        };
        tbPasienTujuan.setModel(tabModeNoRMTujuan);
        tbPasienTujuan.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbPasienTujuan.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        for (i= 0; i < 3; i++) {
            TableColumn column = tbPasienTujuan.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(120);
            }else if(i==1){
                column.setPreferredWidth(200);
            }else if(i==2){
                column.setPreferredWidth(200);
            }
        }
        tbPasienTujuan.setDefaultRenderer(Object.class, new WarnaTable());

        // setiap centang di tabel asal berubah, kolom hasil di tabel tujuan ikut diperbarui
        tabModeNoRMAsal.addTableModelListener(e -> {
            if(e.getColumn()==0){
                tampilHasil();
            }
        });

        ChkSemua.setText("Pilih Semua Data");
        ChkSemua.setName("ChkSemua");
        ChkSemua.setPreferredSize(new Dimension(130,23));
        ChkSemua.addActionListener(e -> {
            for(int r=0;r<tabModeNoRMAsal.getRowCount();r++){
                tabModeNoRMAsal.setValueAt(ChkSemua.isSelected(),r,0);
            }
        });
        panelGlass8.add(ChkSemua);

        NoRMAsal.setDocument(new batasInput((byte)15).getKata(NoRMAsal));
        NoRMTujuan.setDocument(new batasInput((byte)15).getKata(NoRMTujuan));
        if(koneksiDB.CARICEPAT().equals("aktif")){
            NoRMAsal.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if(NoRMAsal.getText().length()>2){
                        tampilPasienAsal();
                    }
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    if(NoRMAsal.getText().length()>2){
                        tampilPasienAsal();
                    }
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    if(NoRMAsal.getText().length()>2){
                        tampilPasienAsal();
                    }
                }
            });

            NoRMTujuan.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if(NoRMTujuan.getText().length()>2){
                        tampilPasienTujuan();
                    }
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    if(NoRMTujuan.getText().length()>2){
                        tampilPasienTujuan();
                    }
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    if(NoRMTujuan.getText().length()>2){
                        tampilPasienTujuan();
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        internalFrame1 = new widget.InternalFrame();
        panelisi1 = new widget.panelisi();
        TabRawat = new javax.swing.JTabbedPane();
        ScrollInput = new widget.ScrollPane();
        FormData = new widget.PanelBiasa();
        PanelAsal = new widget.PanelBiasa();
        PanelCariAsal = new widget.PanelBiasa();
        jLabel13 = new widget.Label();
        NoRMAsal = new widget.TextBox();
        BtnCariNoRMAasl = new widget.Button();
        Scroll1 = new widget.ScrollPane();
        tbPasienAsal = new widget.Table();
        PanelTujuan = new widget.PanelBiasa();
        PanelCariTujuan = new widget.PanelBiasa();
        jLabel14 = new widget.Label();
        NoRMTujuan = new widget.TextBox();
        BtnCariNoRMTujuan = new widget.Button();
        Scroll2 = new widget.ScrollPane();
        tbPasienTujuan = new widget.Table();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
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

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Gabung Data Rekam Medis Pasien ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
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

        FormData.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        FormData.setName("FormData"); // NOI18N
        FormData.setPreferredSize(new java.awt.Dimension(700, 400));
        FormData.setLayout(new java.awt.GridLayout(1, 2, 10, 0));

        PanelAsal.setName("PanelAsal"); // NOI18N
        PanelAsal.setLayout(new java.awt.BorderLayout(0, 5));

        PanelCariAsal.setName("PanelCariAsal"); // NOI18N
        PanelCariAsal.setPreferredSize(new java.awt.Dimension(100, 27));
        PanelCariAsal.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 2));

        jLabel13.setText("No Rekam Medis Asal :");
        jLabel13.setName("jLabel13"); // NOI18N
        jLabel13.setPreferredSize(new java.awt.Dimension(125, 23));
        PanelCariAsal.add(jLabel13);

        NoRMAsal.setHighlighter(null);
        NoRMAsal.setName("NoRMAsal"); // NOI18N
        NoRMAsal.setPreferredSize(new java.awt.Dimension(180, 23));
        NoRMAsal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoRMAsalKeyPressed(evt);
            }
        });
        PanelCariAsal.add(NoRMAsal);

        BtnCariNoRMAasl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCariNoRMAasl.setMnemonic('1');
        BtnCariNoRMAasl.setToolTipText("Alt+1");
        BtnCariNoRMAasl.setName("BtnCariNoRMAasl"); // NOI18N
        BtnCariNoRMAasl.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCariNoRMAasl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariNoRMAaslActionPerformed(evt);
            }
        });
        PanelCariAsal.add(BtnCariNoRMAasl);

        PanelAsal.add(PanelCariAsal, java.awt.BorderLayout.PAGE_START);

        Scroll1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll1.setName("Scroll1"); // NOI18N
        Scroll1.setOpaque(true);

        tbPasienAsal.setName("tbPasienAsal"); // NOI18N
        tbPasienAsal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbPasienAsalMouseClicked(evt);
            }
        });
        Scroll1.setViewportView(tbPasienAsal);

        PanelAsal.add(Scroll1, java.awt.BorderLayout.CENTER);

        FormData.add(PanelAsal);

        PanelTujuan.setName("PanelTujuan"); // NOI18N
        PanelTujuan.setLayout(new java.awt.BorderLayout(0, 5));

        PanelCariTujuan.setName("PanelCariTujuan"); // NOI18N
        PanelCariTujuan.setPreferredSize(new java.awt.Dimension(100, 27));
        PanelCariTujuan.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 2));

        jLabel14.setText("No Rekam Medis Tujuan :");
        jLabel14.setName("jLabel14"); // NOI18N
        jLabel14.setPreferredSize(new java.awt.Dimension(125, 23));
        PanelCariTujuan.add(jLabel14);

        NoRMTujuan.setHighlighter(null);
        NoRMTujuan.setName("NoRMTujuan"); // NOI18N
        NoRMTujuan.setPreferredSize(new java.awt.Dimension(180, 23));
        NoRMTujuan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoRMTujuanKeyPressed(evt);
            }
        });
        PanelCariTujuan.add(NoRMTujuan);

        BtnCariNoRMTujuan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCariNoRMTujuan.setMnemonic('2');
        BtnCariNoRMTujuan.setToolTipText("Alt+2");
        BtnCariNoRMTujuan.setName("BtnCariNoRMTujuan"); // NOI18N
        BtnCariNoRMTujuan.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCariNoRMTujuan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariNoRMTujuanActionPerformed(evt);
            }
        });
        PanelCariTujuan.add(BtnCariNoRMTujuan);

        PanelTujuan.add(PanelCariTujuan, java.awt.BorderLayout.PAGE_START);

        Scroll2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)));
        Scroll2.setName("Scroll2"); // NOI18N
        Scroll2.setOpaque(true);

        tbPasienTujuan.setName("tbPasienTujuan"); // NOI18N
        Scroll2.setViewportView(tbPasienTujuan);

        PanelTujuan.add(Scroll2, java.awt.BorderLayout.CENTER);

        FormData.add(PanelTujuan);

        ScrollInput.setViewportView(FormData);

        TabRawat.addTab("Input Data", ScrollInput);

        panelisi1.add(TabRawat, java.awt.BorderLayout.CENTER);

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 54));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Gabung");
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
        tampilPasienAsal();
        tampilPasienTujuan();
    }//GEN-LAST:event_formWindowOpened

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated

    }//GEN-LAST:event_formWindowActivated

    private void NoRMAsalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoRMAsalKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            tampilPasienAsal();
            cariTujuanOtomatis();
            NoRMTujuan.requestFocus();
        }
    }//GEN-LAST:event_NoRMAsalKeyPressed

    private void BtnCariNoRMAaslActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariNoRMAaslActionPerformed
        tampilPasienAsal();
        cariTujuanOtomatis();
    }//GEN-LAST:event_BtnCariNoRMAaslActionPerformed

    private void NoRMTujuanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoRMTujuanKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            tampilPasienTujuan();
            BtnSimpan.requestFocus();
        }
    }//GEN-LAST:event_NoRMTujuanKeyPressed

    private void BtnCariNoRMTujuanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariNoRMTujuanActionPerformed
        tampilPasienTujuan();
    }//GEN-LAST:event_BtnCariNoRMTujuanActionPerformed

    private void TabRawatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TabRawatMouseClicked

    }//GEN-LAST:event_TabRawatMouseClicked

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            dispose();
        }
    }//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if(normasal.equals("")||tabModeNoRMAsal.getRowCount()==0){
            Valid.textKosong(NoRMAsal,"No.R.M Asal");
        }else if(normtujuan.equals("")||tabModeNoRMTujuan.getRowCount()==0){
            Valid.textKosong(NoRMTujuan,"No.R.M Tujuan");
        }else if(!normasal.equals(NoRMAsal.getText().trim())){
            JOptionPane.showMessageDialog(rootPane,"No.R.M Asal berubah, silahkan tampilkan ulang data pasien asal...!!");
            NoRMAsal.requestFocus();
        }else if(!normtujuan.equals(NoRMTujuan.getText().trim())){
            JOptionPane.showMessageDialog(rootPane,"No.R.M Tujuan berubah, silahkan tampilkan ulang data pasien tujuan...!!");
            NoRMTujuan.requestFocus();
        }else if(normasal.equals(normtujuan)){
            JOptionPane.showMessageDialog(rootPane,"No.R.M Asal dan No.R.M Tujuan tidak boleh sama...!!");
            NoRMTujuan.requestFocus();
        }else{
            StringBuilder kolomupdate=new StringBuilder();
            StringBuilder daftarkolom=new StringBuilder();
            int jmlpilih=0;
            for(int r=0;r<tabModeNoRMAsal.getRowCount();r++){
                if(Boolean.TRUE.equals(tabModeNoRMAsal.getValueAt(r,0))){
                    jmlpilih++;
                    daftarkolom.append("\n - ").append(kolompasien[r][0]).append(" : ")
                            .append(tampiltujuan[r]==null?"":tampiltujuan[r]).append(" -> ")
                            .append(tampilasal[r]==null?"":tampilasal[r]);
                }
            }

            if(JOptionPane.showConfirmDialog(rootPane,
                    "Pasien Asal    : "+normasal+" - "+tampilasal[0]+"\n"+
                    "Pasien Tujuan : "+normtujuan+" - "+tampiltujuan[0]+"\n\n"+
                    "Seluruh riwayat No.R.M "+normasal+" akan dipindah ke No.R.M "+normtujuan+
                    " dan No.R.M "+normasal+" akan dihapus.\n"+
                    (jmlpilih==0?"Tidak ada data pasien yang disalin.":"Data pasien yang disalin ke No.R.M "+normtujuan+" :"+daftarkolom)+
                    "\n\nProses ini tidak bisa dibatalkan. Lanjutkan..?",
                    "Konfirmasi Gabung No.R.M",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION){
                return;
            }

            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                koneksi.setAutoCommit(false);

                // 1. pindahkan seluruh riwayat No.R.M asal ke No.R.M tujuan
                for(String[] tabel:tabelriwayat){
                    try {
                        ps=koneksi.prepareStatement("update "+tabel[0]+" set "+tabel[1]+"=? where "+tabel[1]+"=?");
                        try {
                            ps.setString(1,normtujuan);
                            ps.setString(2,normasal);
                            ps.executeUpdate();
                        } finally {
                            ps.close();
                        }
                    } catch (SQLException e) {
                        // tabel tidak ada di database ini, lewati
                        if(e.getErrorCode()!=1146){
                            throw new SQLException("Gagal memindah data "+tabel[0]+" : "+e.getMessage(),e);
                        }
                    }
                }

                // 2. hapus No.R.M asal, gagal bila masih dipakai tabel lain yang belum dipindah
                ps=koneksi.prepareStatement("delete from pasien where no_rkm_medis=?");
                try {
                    ps.setString(1,normasal);
                    ps.executeUpdate();
                } finally {
                    ps.close();
                }

                // 3. salin data pasien yang dipilih ke No.R.M tujuan
                if(jmlpilih>0){
                    String[] nilai=new String[jmlpilih+1];
                    int n=0;
                    for(int r=0;r<tabModeNoRMAsal.getRowCount();r++){
                        if(Boolean.TRUE.equals(tabModeNoRMAsal.getValueAt(r,0))){
                            if(kolomupdate.length()>0){
                                kolomupdate.append(",");
                            }
                            kolomupdate.append(kolompasien[r][1]).append("=?");
                            nilai[n++]=nilaiasal[r];
                        }
                    }
                    nilai[n]=normtujuan;
                    ps=koneksi.prepareStatement("update pasien set "+kolomupdate+" where no_rkm_medis=?");
                    try {
                        for(int p=0;p<nilai.length;p++){
                            ps.setString(p+1,nilai[p]);
                        }
                        ps.executeUpdate();
                    } finally {
                        ps.close();
                    }
                }

                koneksi.commit();
                Sequel.menyimpan2("trackersql","now(),?,?",2,new String[]{
                    akses.getalamatip()+" gabung no.rm "+normasal+" ke "+normtujuan+(jmlpilih>0?", salin data pasien : "+kolomupdate:""),akses.getkode()
                });
                this.setCursor(Cursor.getDefaultCursor());
                JOptionPane.showMessageDialog(rootPane,"No.R.M "+normasal+" berhasil digabung ke No.R.M "+normtujuan+"...!!");
                NoRMAsal.setText("");
                tampilPasienAsal();
                tampilPasienTujuan();
            } catch (Exception e) {
                try {
                    koneksi.rollback();
                } catch (Exception ex) {
                    System.out.println("Notifikasi : "+ex);
                }
                this.setCursor(Cursor.getDefaultCursor());
                System.out.println("Notifikasi : "+e);
                JOptionPane.showMessageDialog(rootPane,"Gabung No.R.M gagal, tidak ada data yang berubah.\n"+e.getMessage());
            } finally {
                try {
                    koneksi.setAutoCommit(true);
                } catch (Exception e) {
                    System.out.println("Notifikasi : "+e);
                }
                this.setCursor(Cursor.getDefaultCursor());
            }
        }
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnSimpanActionPerformed(null);
        }
    }//GEN-LAST:event_BtnSimpanKeyPressed

    private void tbPasienAsalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPasienAsalMouseClicked

    }//GEN-LAST:event_tbPasienAsalMouseClicked

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgGabungRM dialog = new DlgGabungRM(new javax.swing.JFrame(), true);
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
    private widget.Button BtnCariNoRMAasl;
    private widget.Button BtnCariNoRMTujuan;
    private widget.Button BtnKeluar;
    private widget.Button BtnSimpan;
    public widget.PanelBiasa FormData;
    public widget.TextBox NoRMAsal;
    public widget.TextBox NoRMTujuan;
    private widget.PanelBiasa PanelAsal;
    private widget.PanelBiasa PanelCariAsal;
    private widget.PanelBiasa PanelCariTujuan;
    private widget.PanelBiasa PanelTujuan;
    private widget.ScrollPane Scroll1;
    private widget.ScrollPane Scroll2;
    public widget.ScrollPane ScrollInput;
    public javax.swing.JTabbedPane TabRawat;
    private javax.swing.ButtonGroup buttonGroup1;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel13;
    private widget.Label jLabel14;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelisi1;
    private widget.Table tbPasienAsal;
    private widget.Table tbPasienTujuan;
    // End of variables declaration//GEN-END:variables

    public void setNoRM(String norm){
        NoRMAsal.setText(norm);
        tampilPasienAsal();
        cariTujuanOtomatis();
    }

    private PreparedStatement ambilPasien(String norm) throws SQLException {
        PreparedStatement pst=koneksi.prepareStatement("select pasien.no_rkm_medis, pasien.nm_pasien, pasien.no_ktp, pasien.jk, "
                + "pasien.tmp_lahir, pasien.tgl_lahir,pasien.nm_ibu, pasien.alamat,kelurahan.nm_kel,kecamatan.nm_kec,kabupaten.nm_kab,propinsi.nm_prop,"
                + "pasien.gol_darah, pasien.pekerjaan,pasien.stts_nikah,pasien.agama,pasien.tgl_daftar,pasien.no_tlp,pasien.umur,"
                + "pasien.pnd, pasien.keluarga, pasien.namakeluarga,penjab.png_jawab,pasien.no_peserta,pasien.pekerjaanpj,"
                + "pasien.alamatpj,pasien.kelurahanpj,pasien.kecamatanpj,pasien.kabupatenpj,pasien.propinsipj,"
                + "perusahaan_pasien.kode_perusahaan,perusahaan_pasien.nama_perusahaan,pasien.bahasa_pasien,"
                + "bahasa_pasien.nama_bahasa,pasien.suku_bangsa,suku_bangsa.nama_suku_bangsa,pasien.nip,pasien.email,cacat_fisik.nama_cacat,pasien.cacat_fisik,pasien.kd_pj,"
                + "pasien.kd_kel,pasien.kd_kec,pasien.kd_kab,pasien.kd_prop,pasien.perusahaan_pasien from pasien "
                + "inner join kelurahan on pasien.kd_kel=kelurahan.kd_kel inner join kecamatan on pasien.kd_kec=kecamatan.kd_kec "
                + "inner join kabupaten on pasien.kd_kab=kabupaten.kd_kab inner join perusahaan_pasien on perusahaan_pasien.kode_perusahaan=pasien.perusahaan_pasien "
                + "inner join cacat_fisik on pasien.cacat_fisik=cacat_fisik.id inner join propinsi on pasien.kd_prop=propinsi.kd_prop "
                + "inner join bahasa_pasien on bahasa_pasien.id=pasien.bahasa_pasien inner join suku_bangsa on suku_bangsa.id=pasien.suku_bangsa "
                + "inner join penjab on pasien.kd_pj=penjab.kd_pj "
                + "where pasien.no_rkm_medis=?");
        pst.setString(1,norm);
        return pst;
    }

    private void tampilPasienAsal() {
        Valid.tabelKosong(tabModeNoRMAsal);
        normasal="";
        ChkSemua.setSelected(false);
        if(!NoRMAsal.getText().trim().equals("")){
            try{
                ps=ambilPasien(NoRMAsal.getText().trim());
                try {
                    rs=ps.executeQuery();
                    if(rs.next()){
                        normasal=rs.getString("no_rkm_medis");
                        for(int r=0;r<kolompasien.length;r++){
                            nilaiasal[r]=rs.getString(kolompasien[r][1]);
                            tampilasal[r]=rs.getString(kolompasien[r][2]);
                            tabModeNoRMAsal.addRow(new Object[]{false,kolompasien[r][0],tampilasal[r]});
                        }
                    }
                } finally {
                    if(rs!=null){
                        rs.close();
                    }
                    ps.close();
                }
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);
            }
        }
        tampilHasil();
    }

    private String nilaiAsal(String kolom){
        for(int r=0;r<kolompasien.length;r++){
            if(kolompasien[r][1].equals(kolom)){
                return nilaiasal[r]==null?"":nilaiasal[r].trim();
            }
        }
        return "";
    }

    // No.KTP/No.Peserta kosong, "-" atau isinya nol semua dianggap tidak valid untuk pencarian
    private boolean nomorValid(String nomor){
        return !nomor.replaceAll("[0\\-\\s]","").equals("");
    }

    // cari pasien lain dengan No.KTP atau No.Peserta yang sama dengan No.R.M asal
    private void cariTujuanOtomatis(){
        if(normasal.equals("")){
            return;
        }
        String noktp=nilaiAsal("no_ktp"),nopeserta=nilaiAsal("no_peserta");
        boolean ktpvalid=nomorValid(noktp),pesertavalid=nomorValid(nopeserta);
        if(!ktpvalid&&!pesertavalid){
            return;
        }
        java.util.List<String> daftarnorm=new java.util.ArrayList<>();
        java.util.List<String> daftartampil=new java.util.ArrayList<>();
        try {
            ps=koneksi.prepareStatement(
                "select no_rkm_medis,nm_pasien,tgl_lahir,no_ktp,no_peserta from pasien where no_rkm_medis<>? and ("+
                (ktpvalid?"no_ktp=?":"")+(ktpvalid&&pesertavalid?" or ":"")+(pesertavalid?"no_peserta=?":"")+
                ") order by tgl_daftar,no_rkm_medis");
            try {
                int p=1;
                ps.setString(p++,normasal);
                if(ktpvalid){
                    ps.setString(p++,noktp);
                }
                if(pesertavalid){
                    ps.setString(p++,nopeserta);
                }
                rs=ps.executeQuery();
                while(rs.next()){
                    daftarnorm.add(rs.getString("no_rkm_medis"));
                    daftartampil.add(rs.getString("no_rkm_medis")+" - "+rs.getString("nm_pasien")+
                            " - Lahir "+rs.getString("tgl_lahir")+" - KTP "+rs.getString("no_ktp")+
                            " - Peserta "+rs.getString("no_peserta"));
                }
            } finally {
                if(rs!=null){
                    rs.close();
                }
                ps.close();
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }

        if(daftarnorm.isEmpty()){
            JOptionPane.showMessageDialog(rootPane,"Tidak ditemukan pasien lain dengan No.KTP/No.Peserta yang sama.\nSilahkan isi No.R.M Tujuan secara manual.");
            NoRMTujuan.requestFocus();
        }else if(daftarnorm.size()==1){
            NoRMTujuan.setText(daftarnorm.get(0));
            tampilPasienTujuan();
        }else{
            Object pilihan=JOptionPane.showInputDialog(rootPane,
                    "Ditemukan "+daftarnorm.size()+" pasien dengan No.KTP/No.Peserta yang sama.\nPilih No.R.M Tujuan :",
                    "Pilih No.R.M Tujuan",JOptionPane.QUESTION_MESSAGE,null,daftartampil.toArray(),daftartampil.get(0));
            if(pilihan!=null){
                NoRMTujuan.setText(daftarnorm.get(daftartampil.indexOf(pilihan)));
                tampilPasienTujuan();
            }
        }
    }

    private void tampilPasienTujuan(){
        Valid.tabelKosong(tabModeNoRMTujuan);
        normtujuan="";
        if(!NoRMTujuan.getText().trim().equals("")){
            try{
                ps=ambilPasien(NoRMTujuan.getText().trim());
                try {
                    rs=ps.executeQuery();
                    if(rs.next()){
                        normtujuan=rs.getString("no_rkm_medis");
                        for(int r=0;r<kolompasien.length;r++){
                            tampiltujuan[r]=rs.getString(kolompasien[r][2]);
                            tabModeNoRMTujuan.addRow(new Object[]{kolompasien[r][0],tampiltujuan[r],tampiltujuan[r]});
                        }
                    }
                } finally {
                    if(rs!=null){
                        rs.close();
                    }
                    ps.close();
                }
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);
            }
        }
        tampilHasil();
    }

    // kolom "Hasil Setelah Digabung" = data asal bila dicentang, selain itu data tujuan tetap
    private void tampilHasil(){
        if(tabModeNoRMTujuan.getRowCount()!=kolompasien.length){
            return;
        }
        boolean adaasal=tabModeNoRMAsal.getRowCount()==kolompasien.length;
        for(int r=0;r<kolompasien.length;r++){
            if(adaasal&&Boolean.TRUE.equals(tabModeNoRMAsal.getValueAt(r,0))){
                tabModeNoRMTujuan.setValueAt(tampilasal[r],r,2);
            }else{
                tabModeNoRMTujuan.setValueAt(tampiltujuan[r],r,2);
            }
        }
    }

}
