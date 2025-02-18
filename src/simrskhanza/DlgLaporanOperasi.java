package simrskhanza;
import kepegawaian.DlgCariDokter;
import kepegawaian.DlgCariPetugas;
import keuangan.DlgJnsPerawatanOperasi;
import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import keuangan.Jurnal;
import rekammedis.MasterCariTemplateLaporanOperasi;

public class DlgLaporanOperasi extends javax.swing.JDialog {
    private final DefaultTableModel tabMode2;
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private Jurnal jur=new Jurnal();
    private Connection koneksi=koneksiDB.condb();
    private PreparedStatement pstindakan,pstindakan2,pstindakan3,pstindakan4,psobat,psset_tarif,psrekening;
    private ResultSet rs,rsset_tarif,rsrekening;
    private DlgCariPetugas petugas=new DlgCariPetugas( null,false);
    private DlgCariDokter dokter=new DlgCariDokter(null,false);
    private MasterCariTemplateLaporanOperasi template=new MasterCariTemplateLaporanOperasi(null,false);
    private String kelas_operasi="Yes",kelas="",cara_bayar_operasi="Yes",kd_pj="",status="";
    private double ttljmdokter=0,ttljmpetugas=0,ttlpendapatan=0,ttlbhp=0;
    private String Suspen_Piutang_Operasi_Ranap="",Operasi_Ranap="",Beban_Jasa_Medik_Dokter_Operasi_Ranap="",Utang_Jasa_Medik_Dokter_Operasi_Ranap="",
            Beban_Jasa_Medik_Paramedis_Operasi_Ranap="",Utang_Jasa_Medik_Paramedis_Operasi_Ranap="",HPP_Obat_Operasi_Ranap="",Persediaan_Obat_Kamar_Operasi_Ranap="",
            Suspen_Piutang_Operasi_Ralan="",Operasi_Ralan="",Beban_Jasa_Medik_Dokter_Operasi_Ralan="",Utang_Jasa_Medik_Dokter_Operasi_Ralan="",
            Beban_Jasa_Medik_Paramedis_Operasi_Ralan="",Utang_Jasa_Medik_Paramedis_Operasi_Ralan="",HPP_Obat_Operasi_Ralan="",Persediaan_Obat_Kamar_Operasi_Ralan="",
            norawatibu="",finger="",kodeoperator="";;
    private double y=0,biayatindakan=0,biayaobat=0;
    private int jml=0,pilihan=0,i=0,index=0;
    private boolean[] pilih; 
    private boolean sukses=true;
    private String[] kode_paket, nm_perawatan,kategori,kd_obat,nm_obat, satuan;
    private double[] operator1, operator2, operator3, asisten_operator1, asisten_operator2,asisten_operator3,dokter_pjanak,dokter_umum,
                  instrumen, dokter_anak, perawaat_resusitas, dokter_anestesi, asisten_anestesi,asisten_anestesi2, bidan,bidan2,bidan3, 
                  perawat_luar, sewa_ok, alat,akomodasi,bagian_rs,omloop,omloop2,omloop3,omloop4,omloop5,sarpras,ttltindakan,jmlobat,hargasatuan,ttlobat;


    /** Creates new form DlgProgramStudi
     * @param parent
     * @param modal*/
    public DlgLaporanOperasi(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        ChkInput.setSelected(true);

        Object[] row2={
        "No. Rawat",
        "No. RM",
        "Nama",
        "Tanggal Operasi",
        "Selesai",
        "Diagnosa Pre-Op",
        "Diagnosa Post-Op",
        "Jaringan Insisi",
        "PA",
        "Laporan","kd Dok","Dokter"};
        
        tabMode2=new DefaultTableModel(null,row2){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = false;
                if ((colIndex==0)||(colIndex==4)) {
                    a=true;
                }
                return a;
             }
             Class[] types = new Class[] {
                java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, 
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class,
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };

        tbObat.setModel(tabMode2);
        //tampil();

        //tbBangsal.setDefaultRenderer(Object.class, new WarnaTable(jPanel2.getBackground(),tbBangsal.getBackground()));
        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 12; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(200);
            }else if(i==1){
                column.setPreferredWidth(80);
            }else if(i==2){
                column.setPreferredWidth(150);
            }else if(i==3){
                column.setPreferredWidth(150);
            }else if(i==4){
                column.setPreferredWidth(150);
            }else if(i==10){
                column.setMinWidth(0);
                column.setMaxWidth(0);
            }else {
                column.setPreferredWidth(150);
            }
        }

        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRw.setDocument(new batasInput((byte)17).getKata(TNoRw));      
        PreOp.setDocument(new batasInput((int)100).getKata(PreOp));      
        PostOp.setDocument(new batasInput((int)100).getKata(PostOp));      
        Jaringan.setDocument(new batasInput((int)100).getKata(Jaringan));
        Laporan.setDocument(new batasInput((int)8000).getKata(Laporan));
        
        
        if(koneksiDB.CARICEPAT().equals("aktif")){
     
        }  
        
        dokter.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(dokter.getTable().getSelectedRow()!= -1){                    
                    if(pilihan==1){
                        kdoperator1.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),0).toString());
                        nmoperator1.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),1).toString());
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
        
        petugas.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(petugas.getTable().getSelectedRow()!= -1){    
                    if(pilihan==1){
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
        
        template.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(template.getTable().getSelectedRow()!= -1){  
                    PreOp.setText(template.getTable().getValueAt(template.getTable().getSelectedRow(),2).toString());
                    PostOp.setText(template.getTable().getValueAt(template.getTable().getSelectedRow(),3).toString());
                    Jaringan.setText(template.getTable().getValueAt(template.getTable().getSelectedRow(),4).toString());
                    DikirimPA.setSelectedItem(template.getTable().getValueAt(template.getTable().getSelectedRow(),5).toString());
                    Laporan.setText(template.getTable().getValueAt(template.getTable().getSelectedRow(),6).toString());
                    Laporan.requestFocus();
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
        
        isForm();
        
        try {
            psrekening=koneksi.prepareStatement(
                "select set_akun_ralan.Suspen_Piutang_Operasi_Ralan,set_akun_ralan.Operasi_Ralan,set_akun_ralan.Beban_Jasa_Medik_Dokter_Operasi_Ralan,"+
                "set_akun_ralan.Utang_Jasa_Medik_Dokter_Operasi_Ralan,set_akun_ralan.Beban_Jasa_Medik_Paramedis_Operasi_Ralan,"+
                "set_akun_ralan.Utang_Jasa_Medik_Paramedis_Operasi_Ralan,set_akun_ralan.HPP_Obat_Operasi_Ralan,"+
                "set_akun_ralan.Persediaan_Obat_Kamar_Operasi_Ralan from set_akun_ralan");
            try {
                rsrekening=psrekening.executeQuery();
                while(rsrekening.next()){
                    Suspen_Piutang_Operasi_Ralan=rsrekening.getString("Suspen_Piutang_Operasi_Ralan");
                    Operasi_Ralan=rsrekening.getString("Operasi_Ralan");
                    Beban_Jasa_Medik_Dokter_Operasi_Ralan=rsrekening.getString("Beban_Jasa_Medik_Dokter_Operasi_Ralan");
                    Utang_Jasa_Medik_Dokter_Operasi_Ralan=rsrekening.getString("Utang_Jasa_Medik_Dokter_Operasi_Ralan");
                    Beban_Jasa_Medik_Paramedis_Operasi_Ralan=rsrekening.getString("Beban_Jasa_Medik_Paramedis_Operasi_Ralan");
                    Utang_Jasa_Medik_Paramedis_Operasi_Ralan=rsrekening.getString("Utang_Jasa_Medik_Paramedis_Operasi_Ralan");
                    HPP_Obat_Operasi_Ralan=rsrekening.getString("HPP_Obat_Operasi_Ralan");
                    Persediaan_Obat_Kamar_Operasi_Ralan=rsrekening.getString("Persediaan_Obat_Kamar_Operasi_Ralan");
                }
            } catch (Exception e) {
                System.out.println("Notif Rekening : "+e);
            } finally{
                if(rsrekening!=null){
                    rsrekening.close();
                }
                if(psrekening!=null){
                    psrekening.close();
                }
            }  
            
            psrekening=koneksi.prepareStatement(
               "select set_akun_ranap.Suspen_Piutang_Operasi_Ranap,set_akun_ranap.Operasi_Ranap,set_akun_ranap.Beban_Jasa_Medik_Dokter_Operasi_Ranap,"+
               "set_akun_ranap.Utang_Jasa_Medik_Dokter_Operasi_Ranap,set_akun_ranap.Beban_Jasa_Medik_Paramedis_Operasi_Ranap,"+
               "set_akun_ranap.Utang_Jasa_Medik_Paramedis_Operasi_Ranap,set_akun_ranap.HPP_Obat_Operasi_Ranap from set_akun_ranap");
            try {
                rsrekening=psrekening.executeQuery();
                while(rsrekening.next()){
                    Suspen_Piutang_Operasi_Ranap=rsrekening.getString("Suspen_Piutang_Operasi_Ranap");
                    Operasi_Ranap=rsrekening.getString("Operasi_Ranap");
                    Beban_Jasa_Medik_Dokter_Operasi_Ranap=rsrekening.getString("Beban_Jasa_Medik_Dokter_Operasi_Ranap");
                    Utang_Jasa_Medik_Dokter_Operasi_Ranap=rsrekening.getString("Utang_Jasa_Medik_Dokter_Operasi_Ranap");
                    Beban_Jasa_Medik_Paramedis_Operasi_Ranap=rsrekening.getString("Beban_Jasa_Medik_Paramedis_Operasi_Ranap");
                    Utang_Jasa_Medik_Paramedis_Operasi_Ranap=rsrekening.getString("Utang_Jasa_Medik_Paramedis_Operasi_Ranap");
                    HPP_Obat_Operasi_Ranap=rsrekening.getString("HPP_Obat_Operasi_Ranap");
                }
            } catch (Exception e) {
                System.out.println("Notif Rekening : "+e);
            } finally{
                if(rsrekening!=null){
                    rsrekening.close();
                }
                if(psrekening!=null){
                    psrekening.close();
                }
            }   
            
            psrekening=koneksi.prepareStatement("select set_akun_ranap2.Persediaan_Obat_Kamar_Operasi_Ranap from set_akun_ranap2");
            try {
                rsrekening=psrekening.executeQuery();
                while(rsrekening.next()){
                    Persediaan_Obat_Kamar_Operasi_Ranap=rsrekening.getString("Persediaan_Obat_Kamar_Operasi_Ranap");
                }
            } catch (Exception e) {
                System.out.println("Notif Rekening : "+e);
            } finally{
                if(rsrekening!=null){
                    rsrekening.close();
                }
                if(psrekening!=null){
                    psrekening.close();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        } 
        
        try {
            psset_tarif=koneksi.prepareStatement("select set_tarif.cara_bayar_operasi,set_tarif.kelas_operasi from set_tarif");
            try {
                rsset_tarif=psset_tarif.executeQuery();
                if(rsset_tarif.next()){
                    cara_bayar_operasi=rsset_tarif.getString("cara_bayar_operasi");
                    kelas_operasi=rsset_tarif.getString("kelas_operasi");
                }else{
                    cara_bayar_operasi="Yes";
                    kelas_operasi="Yes";
                }  
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            }finally{
                if(rsset_tarif != null){
                    rsset_tarif.close();
                }
                if(psset_tarif != null){
                    psset_tarif.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
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
        MnLaporanOperasi = new javax.swing.JMenuItem();
        internalFrame1 = new widget.InternalFrame();
        panelisi1 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnEdit = new widget.Button();
        BtnHapus = new widget.Button();
        label10 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari2 = new widget.Button();
        BtnCari = new widget.Button();
        BtnKeluar = new widget.Button();
        PanelInput = new javax.swing.JPanel();
        ChkInput = new widget.CekBox();
        scrollPane1 = new widget.ScrollPane();
        FormInput = new widget.panelisi();
        label11 = new widget.Label();
        tgl = new widget.Tanggal();
        jLabel3 = new widget.Label();
        TNoRw = new widget.TextBox();
        TPasienTgl = new widget.TextBox();
        label12 = new widget.Label();
        tgl2 = new widget.Tanggal();
        PreOp = new widget.TextBox();
        jLabel6 = new widget.Label();
        jLabel7 = new widget.Label();
        PostOp = new widget.TextBox();
        jLabel8 = new widget.Label();
        Jaringan = new widget.TextBox();
        jLabel9 = new widget.Label();
        DikirimPA = new widget.ComboBox();
        scrollPane2 = new widget.ScrollPane();
        Laporan = new widget.TextArea();
        jLabel10 = new widget.Label();
        btnTemplate = new widget.Button();
        TPasien = new widget.TextBox();
        jLabel4 = new widget.Label();
        TPasienRM = new widget.TextBox();
        label14 = new widget.Label();
        kdoperator1 = new widget.TextBox();
        nmoperator1 = new widget.TextBox();
        BtnOperator1 = new widget.Button();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();

        Kd2.setName("Kd2"); // NOI18N
        Kd2.setPreferredSize(new java.awt.Dimension(207, 23));

        Popup.setName("Popup"); // NOI18N

        MnLaporanOperasi.setBackground(new java.awt.Color(255, 255, 254));
        MnLaporanOperasi.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnLaporanOperasi.setForeground(new java.awt.Color(50, 50, 50));
        MnLaporanOperasi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnLaporanOperasi.setText("Laporan Operasi Pasien");
        MnLaporanOperasi.setName("MnLaporanOperasi"); // NOI18N
        MnLaporanOperasi.setPreferredSize(new java.awt.Dimension(220, 26));
        MnLaporanOperasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnLaporanOperasiActionPerformed(evt);
            }
        });
        Popup.add(MnLaporanOperasi);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Laporan Operasi ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        panelisi1.setName("panelisi1"); // NOI18N
        panelisi1.setPreferredSize(new java.awt.Dimension(100, 56));
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
        panelisi1.add(BtnEdit);

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

        label10.setText("Key Word :");
        label10.setName("label10"); // NOI18N
        label10.setPreferredSize(new java.awt.Dimension(68, 23));
        panelisi1.add(label10);

        TCari.setToolTipText("Alt+C");
        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(215, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelisi1.add(TCari);

        BtnCari2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari2.setMnemonic('1');
        BtnCari2.setToolTipText("Alt+1");
        BtnCari2.setName("BtnCari2"); // NOI18N
        BtnCari2.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCari2ActionPerformed(evt);
            }
        });
        BtnCari2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCari2KeyPressed(evt);
            }
        });
        panelisi1.add(BtnCari2);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnCari.setMnemonic('E');
        BtnCari.setText("Cari");
        BtnCari.setToolTipText("Alt+E");
        BtnCari.setName("BtnCari"); // NOI18N
        BtnCari.setPreferredSize(new java.awt.Dimension(100, 30));
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
        panelisi1.add(BtnCari);

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

        internalFrame1.add(panelisi1, java.awt.BorderLayout.PAGE_END);

        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(560, 532));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

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

        scrollPane1.setName("scrollPane1"); // NOI18N
        scrollPane1.setPreferredSize(new java.awt.Dimension(91, 183));

        FormInput.setBorder(null);
        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(89, 553));
        FormInput.setLayout(null);

        label11.setText("Tangga Jaml Mulai :");
        label11.setName("label11"); // NOI18N
        label11.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label11);
        label11.setBounds(10, 70, 120, 23);

        tgl.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        tgl.setName("tgl"); // NOI18N
        tgl.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tglKeyPressed(evt);
            }
        });
        FormInput.add(tgl);
        tgl.setBounds(150, 70, 150, 23);

        jLabel3.setText("Tgl. Lahir");
        jLabel3.setName("jLabel3"); // NOI18N
        FormInput.add(jLabel3);
        jLabel3.setBounds(640, 10, 50, 23);

        TNoRw.setEditable(false);
        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw"); // NOI18N
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });
        FormInput.add(TNoRw);
        TNoRw.setBounds(84, 10, 140, 23);

        TPasienTgl.setEditable(false);
        TPasienTgl.setHighlighter(null);
        TPasienTgl.setName("TPasienTgl"); // NOI18N
        FormInput.add(TPasienTgl);
        TPasienTgl.setBounds(700, 10, 110, 23);

        label12.setText("Tanggal Jam Selesai :");
        label12.setName("label12"); // NOI18N
        label12.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label12);
        label12.setBounds(10, 110, 120, 23);

        tgl2.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        tgl2.setName("tgl2"); // NOI18N
        tgl2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tgl2KeyPressed(evt);
            }
        });
        FormInput.add(tgl2);
        tgl2.setBounds(150, 110, 150, 23);

        PreOp.setHighlighter(null);
        PreOp.setName("PreOp"); // NOI18N
        PreOp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PreOpKeyPressed(evt);
            }
        });
        FormInput.add(PreOp);
        PreOp.setBounds(460, 110, 340, 23);

        jLabel6.setText("Diagnosis Pre-operatif :");
        jLabel6.setName("jLabel6"); // NOI18N
        FormInput.add(jLabel6);
        jLabel6.setBounds(320, 110, 130, 23);

        jLabel7.setText("Diagnosis Post-operatif :");
        jLabel7.setName("jLabel7"); // NOI18N
        FormInput.add(jLabel7);
        jLabel7.setBounds(320, 70, 130, 23);

        PostOp.setHighlighter(null);
        PostOp.setName("PostOp"); // NOI18N
        PostOp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PostOpKeyPressed(evt);
            }
        });
        FormInput.add(PostOp);
        PostOp.setBounds(460, 70, 340, 23);

        jLabel8.setText("Jaringan di-Eksisi / -Insisi :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(310, 150, 140, 23);

        Jaringan.setHighlighter(null);
        Jaringan.setName("Jaringan"); // NOI18N
        Jaringan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JaringanKeyPressed(evt);
            }
        });
        FormInput.add(Jaringan);
        Jaringan.setBounds(460, 150, 340, 23);

        jLabel9.setText("Dikirim Pemeriksaan PA :");
        jLabel9.setName("jLabel9"); // NOI18N
        FormInput.add(jLabel9);
        jLabel9.setBounds(10, 150, 120, 23);

        DikirimPA.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ya", "Tidak" }));
        DikirimPA.setName("DikirimPA"); // NOI18N
        DikirimPA.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DikirimPAKeyPressed(evt);
            }
        });
        FormInput.add(DikirimPA);
        DikirimPA.setBounds(150, 150, 150, 23);

        scrollPane2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane2.setName("scrollPane2"); // NOI18N

        Laporan.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        Laporan.setColumns(20);
        Laporan.setRows(5);
        Laporan.setName("Laporan"); // NOI18N
        scrollPane2.setViewportView(Laporan);

        FormInput.add(scrollPane2);
        scrollPane2.setBounds(130, 200, 680, 290);

        jLabel10.setText("Laporan Operasi :");
        jLabel10.setName("jLabel10"); // NOI18N
        FormInput.add(jLabel10);
        jLabel10.setBounds(20, 190, 101, 23);

        btnTemplate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnTemplate.setMnemonic('2');
        btnTemplate.setText("Template");
        btnTemplate.setToolTipText("Alt+2");
        btnTemplate.setName("btnTemplate"); // NOI18N
        btnTemplate.setPreferredSize(new java.awt.Dimension(28, 23));
        btnTemplate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTemplateActionPerformed(evt);
            }
        });
        FormInput.add(btnTemplate);
        btnTemplate.setBounds(20, 220, 100, 23);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N
        FormInput.add(TPasien);
        TPasien.setBounds(350, 10, 290, 23);

        jLabel4.setText("No.Rawat :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(0, 10, 81, 23);

        TPasienRM.setEditable(false);
        TPasienRM.setHighlighter(null);
        TPasienRM.setName("TPasienRM"); // NOI18N
        TPasienRM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TPasienRMActionPerformed(evt);
            }
        });
        FormInput.add(TPasienRM);
        TPasienRM.setBounds(230, 10, 110, 23);

        label14.setText("Dokter Operator :");
        label14.setName("label14"); // NOI18N
        label14.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label14);
        label14.setBounds(0, 40, 130, 23);

        kdoperator1.setEditable(false);
        kdoperator1.setName("kdoperator1"); // NOI18N
        kdoperator1.setPreferredSize(new java.awt.Dimension(80, 23));
        kdoperator1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdoperator1KeyPressed(evt);
            }
        });
        FormInput.add(kdoperator1);
        kdoperator1.setBounds(150, 40, 100, 23);

        nmoperator1.setEditable(false);
        nmoperator1.setName("nmoperator1"); // NOI18N
        nmoperator1.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(nmoperator1);
        nmoperator1.setBounds(260, 40, 190, 23);

        BtnOperator1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnOperator1.setMnemonic('2');
        BtnOperator1.setToolTipText("Alt+2");
        BtnOperator1.setName("BtnOperator1"); // NOI18N
        BtnOperator1.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnOperator1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnOperator1ActionPerformed(evt);
            }
        });
        BtnOperator1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnOperator1KeyPressed(evt);
            }
        });
        FormInput.add(BtnOperator1);
        BtnOperator1.setBounds(460, 40, 28, 23);

        scrollPane1.setViewportView(FormInput);

        PanelInput.add(scrollPane1, java.awt.BorderLayout.CENTER);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setOpaque(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(816, 102));
        jPanel1.setLayout(new java.awt.GridLayout(1, 2));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)), "::[ Data Laporan Operasi ] ::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setOpaque(false);
        jPanel2.setPreferredSize(new java.awt.Dimension(350, 102));
        jPanel2.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 253)));
        Scroll.setComponentPopupMenu(Popup);
        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbObat.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbObat.setComponentPopupMenu(Popup);
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

        jPanel2.add(Scroll, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel2);

        internalFrame1.add(jPanel1, java.awt.BorderLayout.CENTER);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        DlgCariTagihanOperasi form=new DlgCariTagihanOperasi(null,false);
        //form.emptTeks();      
        form.setPasien(TNoRw.getText());
        form.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        form.setLocationRelativeTo(internalFrame1);
        form.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
}//GEN-LAST:event_BtnCariActionPerformed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
            dispose();  
}//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){            
            dispose();              
        }else{}
}//GEN-LAST:event_BtnKeluarKeyPressed
/*
private void KdKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TKdKeyPressed
    Valid.pindah(evt,BtnCari,Nm);
}//GEN-LAST:event_TKdKeyPressed
*/

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnCariActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnSimpan, BtnKeluar);
        }
    }//GEN-LAST:event_BtnCariKeyPressed

private void tglKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tglKeyPressed
        
}//GEN-LAST:event_tglKeyPressed

private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRwKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            Sequel.cariIsi("select concat(pasien.no_rkm_medis,', ',pasien.nm_pasien) from reg_periksa inner join pasien "+
                        " on pasien.no_rkm_medis=reg_periksa.no_rkm_medis where reg_periksa.no_rawat=? ",TPasienTgl,TNoRw.getText());
        }else{     
            
        }
}//GEN-LAST:event_TNoRwKeyPressed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnSimpanActionPerformed(null);
        }else{
            //Valid.pindah(evt,kddrumum,BtnKeluar);
        }
    }//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if(TNoRw.getText().trim().equals("")||TPasienTgl.getText().trim().equals("")){
            Valid.textKosong(TNoRw,"Pasien");
        }else if(PostOp.getText().equals("")){
            Valid.textKosong(PostOp,"Post Op");
        }else if(nmoperator1.getText().equals("")){
            Valid.textKosong(nmoperator1,"Dokter Operator");
        }else if(PreOp.getText().equals("")){
            Valid.textKosong(PreOp,"Pre Op");
        }else if(Jaringan.getText().equals("")){
            Valid.textKosong(Jaringan,"Jaringan di Insisi");
        }else if(Laporan.getText().equals("")){
            Valid.textKosong(Laporan,"Laporan");
        }
        else{            
            if(Sequel.cariRegistrasi(TNoRw.getText())>0){
                JOptionPane.showMessageDialog(rootPane,"Data billing sudah terverifikasi, data tidak boleh dihapus.\nSilahkan hubungi bagian kasir/keuangan ..!!");                
            }else{
                
                Map<String, String> reportMap = new HashMap<>();                
                try{    
                    psobat=koneksi.prepareStatement("SELECT op.no_rawat, op.tgl_operasi, op.kode_paket, po.nm_perawatan "
                            + "FROM operasi op JOIN paket_operasi po ON po.kode_paket =op.kode_paket WHERE op.no_rawat =?");
                    try{
                        psobat.setString(1,TNoRw.getText());
                        rs=psobat.executeQuery();
                        while(rs.next()){
                            //reportList.add(rs.getString("nm_perawatan"));
                            String kodePaket = rs.getString("kode_paket");
                            String namaPerawatan = rs.getString("nm_perawatan");
                            reportMap.put(namaPerawatan, kodePaket);
                        }
                    }catch(SQLException e){
                        System.out.println(e);
                    }finally{
                        if(rs!=null){
                            rs.close();
                        }
                        if(psobat!=null){
                            psobat.close();
                        }
                    }
                }catch(SQLException e){
                    System.out.println("Notifikasi : "+e);
                }
                
                
                List<String> reportLabels = new ArrayList<>(reportMap.keySet());
                String[] reportArray = reportLabels.toArray(new String[0]);
                
                if(reportArray.length>0){
                    String selectedLabel = (String) JOptionPane.showInputDialog(
                            null,
                            "Silahkan pilih Tindakan Operasi..!",
                            "Pilihan Tindakan",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            reportArray,
                            reportArray.length > 0 ? reportArray[0] : null // Default to the first report if available
                    );

                    if (selectedLabel != null) {
                        String selectedValue = reportMap.get(selectedLabel);
                        System.out.println("Selected report: " + selectedLabel + ", Value: " + selectedValue);
                        try {
                            Sequel.mengedit("operasi", "no_rawat='" + TNoRw.getText() + "' and kode_paket='" + selectedValue + "'",
                                    "tgl_operasi='" +Valid.SetTgl(tgl.getSelectedItem()+"")+" "+tgl.getSelectedItem().toString().substring(11,19)+ "'");
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                        
                    } else {
                        System.out.println("No report selected.");
                    }
                }
                
                Sequel.AutoComitFalse();
                sukses=true; 
                if(sukses==true){
                    if(!Laporan.getText().equals("")){
                        if(Sequel.menyimpantf2("laporan_operasi","?,?,?,?,?,?,?,?,?","laporan operasi",9,new String[]{
                                TNoRw.getText(),
                                Valid.SetTgl(tgl.getSelectedItem()+"")+" "+tgl.getSelectedItem().toString().substring(11,19),
                                PreOp.getText(),
                                PostOp.getText(),
                                Jaringan.getText(),
                                Valid.SetTgl(tgl2.getSelectedItem()+"")+" "+tgl2.getSelectedItem().toString().substring(11,19),
                                DikirimPA.getSelectedItem().toString(),
                                Laporan.getText(),
                                kdoperator1.getText()
                            })==false){
                            sukses=false;
                        }
                    }
                }
                                    
                if(sukses==true){
                    Sequel.Commit();
                    tampil();
                    tampil2();
                    PreOp.setText("");
                    PostOp.setText("");
                    Jaringan.setText("");
                    Laporan.setText("");
                    emptTeks();
                    //JOptionPane.showMessageDialog(rootPane,"Proses simpan selesai...!");
                }else{
                    JOptionPane.showMessageDialog(null,"Terjadi kesalahan saat pemrosesan data, transaksi dibatalkan.\nPeriksa kembali data sebelum melanjutkan menyimpan..!!");
                    Sequel.RollBack();
                }
                Sequel.AutoComitTrue();
            }
        }
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void tgl2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tgl2KeyPressed
        //Valid.pindah(evt,kdonloop5,PreOp);
    }//GEN-LAST:event_tgl2KeyPressed

    private void PreOpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PreOpKeyPressed
        Valid.pindah(evt,tgl2,PostOp);
    }//GEN-LAST:event_PreOpKeyPressed

    private void PostOpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PostOpKeyPressed
        Valid.pindah(evt,PreOp,Jaringan);
    }//GEN-LAST:event_PostOpKeyPressed

    private void JaringanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_JaringanKeyPressed
        Valid.pindah(evt,PostOp,DikirimPA);
    }//GEN-LAST:event_JaringanKeyPressed

    private void DikirimPAKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DikirimPAKeyPressed
        Valid.pindah(evt,Jaringan,Laporan);
    }//GEN-LAST:event_DikirimPAKeyPressed

    private void btnTemplateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTemplateActionPerformed
        template.emptTeks();
        template.isCek();
        template.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        template.setLocationRelativeTo(internalFrame1);
        template.setVisible(true);
    }//GEN-LAST:event_btnTemplateActionPerformed

    private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInputActionPerformed
        isForm();
    }//GEN-LAST:event_ChkInputActionPerformed

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbObatMouseClicked
        if(tbObat.getRowCount()!=0){
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }//GEN-LAST:event_tbObatMouseClicked

    private void tbObatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbObatKeyPressed
        if(tbObat.getRowCount()!=0){
            if(evt.getKeyCode()==KeyEvent.VK_ENTER){
                try {
                    getData();
                    int row=tbObat.getSelectedColumn();
                    if(row==1){
                        
                    }
                } catch (java.lang.NullPointerException e) {
                }
            }else if((evt.getKeyCode()==KeyEvent.VK_UP)||(evt.getKeyCode()==KeyEvent.VK_DOWN)){
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
        
    }//GEN-LAST:event_tbObatKeyPressed

    private void BtnCari2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCari2ActionPerformed
        tampil2();
    }//GEN-LAST:event_BtnCari2ActionPerformed

    private void BtnCari2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCari2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnCari2KeyPressed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TCariKeyPressed

    private void TPasienRMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TPasienRMActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TPasienRMActionPerformed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if (tbObat.getSelectedRow() != -1) {
            try {
                if(Sequel.queryutf("delete from laporan_operasi where no_rawat='"+tbObat.getValueAt(tbObat.getSelectedRow(), 0)+"' and tanggal='"+tbObat.getValueAt(tbObat.getSelectedRow(), 3)+"'")==true){
                    tampil2();
                    emptTeks();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }else{
            JOptionPane.showMessageDialog(rootPane,"Maaf, Belum ada data yang di pilih !!!");  
        }
    }//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        
    }//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed
        if (tbObat.getSelectedRow() != -1) {
            try {
                Sequel.mengedit("laporan_operasi", "no_rawat='" + tbObat.getValueAt(tbObat.getSelectedRow(), 0) + "' and tanggal='"+tbObat.getValueAt(tbObat.getSelectedRow(), 3) +"'" ,
                    "diagnosa_postop='" + PostOp.getText() + "',diagnosa_preop='" + PreOp.getText() + "',jaringan_dieksekusi='" + Jaringan.getText() + "', laporan_operasi='"+Laporan.getText()+"', kd_dokter='"+kdoperator1.getText()+"' ");
                    tampil2();
                    emptTeks();                
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }//GEN-LAST:event_BtnEditActionPerformed

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEditKeyPressed
       
    }//GEN-LAST:event_BtnEditKeyPressed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed

        emptTeks();
    }//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBatalKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            emptTeks();
        }else{Valid.pindah(evt, BtnSimpan, BtnHapus);}
    }//GEN-LAST:event_BtnBatalKeyPressed

    private void MnLaporanOperasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnLaporanOperasiActionPerformed
        if(tbObat.getSelectedRow()>-1){
            if(!tbObat.getValueAt(tbObat.getSelectedRow(),0).toString().equals("")){
                Map<String, Object> param = new HashMap<>();
                param.put("namars",akses.getnamars());
                param.put("alamatrs",akses.getalamatrs());
                param.put("kotars",akses.getkabupatenrs());
                param.put("propinsirs",akses.getpropinsirs());
                param.put("kontakrs",akses.getkontakrs());
                param.put("emailrs",akses.getemailrs());
                param.put("logo",Sequel.cariGambar("select setting.logo from setting"));
                param.put("norawat",tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());
                param.put("tanggaloperasi",tbObat.getValueAt(tbObat.getSelectedRow(),3).toString());
                kodeoperator=Sequel.cariIsi("select operasi.operator1 from operasi where operasi.no_rawat='"+tbObat.getValueAt(tbObat.getSelectedRow(),0).toString()+"' and tgl_operasi='"+tbObat.getValueAt(tbObat.getSelectedRow(),3).toString()+"'");
                finger=Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?",kodeoperator);
                String nm_dokter = Sequel.cariIsi("SELECT dr.nm_dokter FROM dokter dr WHERE dr.kd_dokter =?",kodeoperator);
                param.put("finger","Dikeluarkan di "+akses.getnamars()+", Kabupaten/Kota "+akses.getkabupatenrs()+"\nDitandatangani secara elektronik oleh "+nm_dokter+"\nID "+(finger.equals("")?kodeoperator:finger)+"\n"+Valid.SetTgl3(tbObat.getValueAt(tbObat.getSelectedRow(),3).toString()));
                if(Sequel.cariIsi("select reg_periksa.status_lanjut from reg_periksa where reg_periksa.no_rawat=?",tbObat.getValueAt(tbObat.getSelectedRow(),0).toString()).equals("Ralan")){
                    try {
                        try {
                            rs=koneksi.prepareStatement(
                                "select pemeriksaan_ralan.no_rawat,pemeriksaan_ralan.tgl_perawatan,pemeriksaan_ralan.jam_rawat,pemeriksaan_ralan.suhu_tubuh,"+
                                "pemeriksaan_ralan.tensi,pemeriksaan_ralan.nadi,pemeriksaan_ralan.respirasi,pemeriksaan_ralan.tinggi,pemeriksaan_ralan.berat,"+
                                "pemeriksaan_ralan.gcs,pemeriksaan_ralan.keluhan,pemeriksaan_ralan.pemeriksaan,pemeriksaan_ralan.alergi,pemeriksaan_ralan.rtl,"+
                                "pemeriksaan_ralan.penilaian from pemeriksaan_ralan where pemeriksaan_ralan.no_rawat='"+tbObat.getValueAt(tbObat.getSelectedRow(),0).toString()+"' "+
                                "and concat(pemeriksaan_ralan.tgl_perawatan,' ',pemeriksaan_ralan.jam_rawat) <= '"+tbObat.getValueAt(tbObat.getSelectedRow(),3).toString()+"' "+
                                "order by pemeriksaan_ralan.tgl_perawatan desc,pemeriksaan_ralan.jam_rawat desc limit 1").executeQuery();
                            if(rs.next()){
                                param.put("tgl_perawatan",rs.getDate("tgl_perawatan"));
                                param.put("jam_rawat",rs.getString("jam_rawat"));
                                param.put("alergi",rs.getString("alergi"));
                                param.put("keluhan",rs.getString("keluhan"));
                                param.put("pemeriksaan",rs.getString("pemeriksaan"));
                                param.put("penilaian",rs.getString("penilaian"));
                                param.put("rtl",rs.getString("rtl"));
                                param.put("ruang",Sequel.cariIsi("select poliklinik.nm_poli from poliklinik inner join reg_periksa on reg_periksa.kd_poli=poliklinik.kd_poli where reg_periksa.no_rawat=?",rs.getString("no_rawat")));
                                param.put("suhu_tubuh",rs.getString("suhu_tubuh"));
                                param.put("tensi",rs.getString("tensi"));
                                param.put("tinggi",rs.getString("tinggi"));
                                param.put("berat",rs.getString("berat"));
                                param.put("nadi",rs.getString("nadi"));
                                param.put("respirasi",rs.getString("respirasi"));
                                param.put("gcs",rs.getString("gcs"));
                            }
                        } catch (Exception e) {
                            System.out.println("Notif : "+e);
                        } finally{
                            if(rs!=null){
                                rs.close();
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Notif : "+e);
                    }
                }else{
                    try {
                        try {
                            rs=koneksi.prepareStatement(
                                "select pemeriksaan_ranap.no_rawat,pemeriksaan_ranap.tgl_perawatan,pemeriksaan_ranap.jam_rawat,pemeriksaan_ranap.suhu_tubuh,"+
                                "pemeriksaan_ranap.tensi,pemeriksaan_ranap.nadi,pemeriksaan_ranap.respirasi,pemeriksaan_ranap.tinggi,pemeriksaan_ranap.berat,"+
                                "pemeriksaan_ranap.gcs,pemeriksaan_ranap.keluhan,pemeriksaan_ranap.pemeriksaan,pemeriksaan_ranap.alergi,pemeriksaan_ranap.rtl,"+
                                "pemeriksaan_ranap.penilaian from pemeriksaan_ranap where pemeriksaan_ranap.no_rawat='"+tbObat.getValueAt(tbObat.getSelectedRow(),0).toString()+"' "+
                                "and concat(pemeriksaan_ranap.tgl_perawatan,' ',pemeriksaan_ranap.jam_rawat) <= '"+tbObat.getValueAt(tbObat.getSelectedRow(),3).toString()+"' "+
                                "order by pemeriksaan_ranap.tgl_perawatan desc,pemeriksaan_ranap.jam_rawat desc limit 1").executeQuery();
                            if(rs.next()){
                                param.put("tgl_perawatan",rs.getDate("tgl_perawatan"));
                                param.put("jam_rawat",rs.getString("jam_rawat"));
                                param.put("alergi",rs.getString("alergi"));
                                param.put("keluhan",rs.getString("keluhan"));
                                param.put("pemeriksaan",rs.getString("pemeriksaan"));
                                param.put("penilaian",rs.getString("penilaian"));
                                param.put("rtl",rs.getString("rtl"));
                                param.put("ruang",Sequel.cariIsi("select nm_bangsal from bangsal inner join kamar inner join kamar_inap on bangsal.kd_bangsal=kamar.kd_bangsal and kamar_inap.kd_kamar=kamar.kd_kamar where no_rawat=? order by tgl_masuk desc limit 1 ",rs.getString("no_rawat")));
                                param.put("suhu_tubuh",rs.getString("suhu_tubuh"));
                                param.put("tensi",rs.getString("tensi"));
                                param.put("tinggi",rs.getString("tinggi"));
                                param.put("berat",rs.getString("berat"));
                                param.put("nadi",rs.getString("nadi"));
                                param.put("respirasi",rs.getString("respirasi"));
                                param.put("gcs",rs.getString("gcs"));
                            }
                        } catch (Exception e) {
                            System.out.println("Notif : "+e);
                        } finally{
                            if(rs!=null){
                                rs.close();
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Notif : "+e);
                    }
                }
                Valid.MyReport("rptLaporanOperasi.jasper","report","::[ Laporan Operasi ]::",param);
            }else{
                JOptionPane.showMessageDialog(rootPane,"Silahkan pilih data, klik pada No.Rawat ..!!");
            }
        }
    }//GEN-LAST:event_MnLaporanOperasiActionPerformed

    private void kdoperator1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kdoperator1KeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            nmoperator1.setText(dokter.tampil3(kdoperator1.getText()));
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            BtnOperator1ActionPerformed(null);
        }
    }//GEN-LAST:event_kdoperator1KeyPressed

    private void BtnOperator1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnOperator1ActionPerformed
        pilihan=1;
        dokter.emptTeks();
        dokter.isCek();
        dokter.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setVisible(true);
    }//GEN-LAST:event_BtnOperator1ActionPerformed

    private void BtnOperator1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnOperator1KeyPressed
        
    }//GEN-LAST:event_BtnOperator1KeyPressed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgLaporanOperasi dialog = new DlgLaporanOperasi(new javax.swing.JFrame(), true);
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
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnCari2;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnOperator1;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkInput;
    private widget.ComboBox DikirimPA;
    private widget.panelisi FormInput;
    private widget.TextBox Jaringan;
    private widget.TextBox Kd2;
    private widget.TextArea Laporan;
    private javax.swing.JMenuItem MnLaporanOperasi;
    private javax.swing.JPanel PanelInput;
    private javax.swing.JPopupMenu Popup;
    private widget.TextBox PostOp;
    private widget.TextBox PreOp;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.TextBox TPasienRM;
    private widget.TextBox TPasienTgl;
    private widget.Button btnTemplate;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel10;
    private widget.Label jLabel3;
    private widget.Label jLabel4;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.Label jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private widget.TextBox kdoperator1;
    private widget.Label label10;
    private widget.Label label11;
    private widget.Label label12;
    private widget.Label label14;
    private widget.TextBox nmoperator1;
    private widget.panelisi panelisi1;
    private widget.ScrollPane scrollPane1;
    private widget.ScrollPane scrollPane2;
    private widget.Table tbObat;
    private widget.Tanggal tgl;
    private widget.Tanggal tgl2;
    // End of variables declaration//GEN-END:variables

    private void tampil() {  
        
    }
    
    //obat
    private void tampil2() {  
        Valid.tabelKosong(tabMode2);
        try{    
            psobat=koneksi.prepareStatement("SELECT lo.no_rawat, ps.no_rkm_medis, ps.nm_pasien, lo.tanggal, lo.diagnosa_preop, lo.diagnosa_postop, lo.jaringan_dieksekusi, lo.selesaioperasi, lo.permintaan_pa, lo.laporan_operasi, lo.kd_dokter, dr.nm_dokter FROM laporan_operasi lo \n" +
            "JOIN reg_periksa rp ON rp.no_rawat = lo.no_rawat\n" +
            "JOIN pasien ps ON ps.no_rkm_medis = rp.no_rkm_medis\n" +
            "JOIN dokter dr ON dr.kd_dokter = lo.kd_dokter "+
            "WHERE lo.no_rawat = ? ");
            try{
                psobat.setString(1,TNoRw.getText());
                rs=psobat.executeQuery();
                while(rs.next()){
                    tabMode2.addRow(new Object[]{                                   
                                   rs.getString("no_rawat"),
                                   rs.getString("no_rkm_medis"),
                                   rs.getString("nm_pasien"),
                                   rs.getString("tanggal").substring(0, 19),
                                   rs.getString("selesaioperasi").substring(0, 19),
                                   rs.getString("diagnosa_preop"),
                                   rs.getString("diagnosa_postop"),
                                   rs.getString("jaringan_dieksekusi"),
                                   rs.getString("permintaan_pa"),
                                   rs.getString("laporan_operasi"),
                                   rs.getString("kd_dokter"),
                                   rs.getString("nm_dokter")
                    });
                }
            }catch(SQLException e){
                System.out.println(e);
            }finally{
                if(rs!=null){
                    rs.close();
                }
                if(psobat!=null){
                    psobat.close();
                }
            }
        }catch(SQLException e){
            System.out.println("Notifikasi : "+e);
        }
    }

    
       
    private void getData(){
        if (tbObat.getSelectedRow() != -1) {
            Valid.SetTgl2(tgl,tbObat.getValueAt(tbObat.getSelectedRow(),3).toString());
            Valid.SetTgl2(tgl2,tbObat.getValueAt(tbObat.getSelectedRow(),4).toString());
            DikirimPA.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 8).toString());
            PostOp.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString());
            PreOp.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString());
            Jaringan.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString());
            Laporan.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString());
            kdoperator1.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 10).toString());
            nmoperator1.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 11).toString());
        }
    }
    
    private void getData2(){
        
    }
    

    
    public void isCek(){
       
    }
    
    public void setNoRm(String norw,String nama,String posisi){
        String[] parts = nama.split(", ");
        TNoRw.setText(norw);
        TPasienRM.setText(parts[0]);
        TPasien.setText(parts[1]);
        TPasienTgl.setText(Sequel.cariIsi("SELECT ps.tgl_lahir FROM pasien ps WHERE ps.no_rkm_medis=?",parts[0]));
        this.status=posisi;
        this.kd_pj=Sequel.cariIsi("select reg_periksa.kd_pj from reg_periksa where reg_periksa.no_rawat=?",norw);        
        if(status.equals("Ranap")){
            norawatibu=Sequel.cariIsi("select ranap_gabung.no_rawat from ranap_gabung where ranap_gabung.no_rawat2=?",TNoRw.getText());
        
            if(!norawatibu.equals("")){
                kelas=Sequel.cariIsi(
                    "select kamar.kelas from kamar inner join kamar_inap "+
                    "on kamar.kd_kamar=kamar_inap.kd_kamar where no_rawat=? "+
                    "and kamar_inap.stts_pulang='-' order by STR_TO_DATE(concat(kamar_inap.tgl_masuk,' ',kamar_inap.jam_masuk),'%Y-%m-%d %H:%i:%s') desc limit 1",norawatibu);
            }else{
                kelas=Sequel.cariIsi(
                    "select kamar.kelas from kamar inner join kamar_inap "+
                    "on kamar.kd_kamar=kamar_inap.kd_kamar where no_rawat=? "+
                    "and kamar_inap.stts_pulang='-' order by STR_TO_DATE(concat(kamar_inap.tgl_masuk,' ',kamar_inap.jam_masuk),'%Y-%m-%d %H:%i:%s') desc limit 1",TNoRw.getText());
            } 
        }else if(status.equals("Ralan")){
            kelas="Rawat Jalan";
        }
        tampil();
        tampil2();
    }
    
    public void setNoRm(String norm,String nama,String posisi,String KodeOperator,String NamaOperator){
        TNoRw.setText(norm);
        TPasienTgl.setText(nama);
        this.status=posisi;
        this.kd_pj=Sequel.cariIsi("select reg_periksa.kd_pj from reg_periksa where reg_periksa.no_rawat=?",norm);        
        if(status.equals("Ranap")){
            norawatibu=Sequel.cariIsi("select ranap_gabung.no_rawat from ranap_gabung where ranap_gabung.no_rawat2=?",TNoRw.getText());
        
            if(!norawatibu.equals("")){
                kelas=Sequel.cariIsi(
                    "select kamar.kelas from kamar inner join kamar_inap "+
                    "on kamar.kd_kamar=kamar_inap.kd_kamar where no_rawat=? "+
                    "and kamar_inap.stts_pulang='-' order by STR_TO_DATE(concat(kamar_inap.tgl_masuk,' ',kamar_inap.jam_masuk),'%Y-%m-%d %H:%i:%s') desc limit 1",norawatibu);
            }else{
                kelas=Sequel.cariIsi(
                    "select kamar.kelas from kamar inner join kamar_inap "+
                    "on kamar.kd_kamar=kamar_inap.kd_kamar where no_rawat=? "+
                    "and kamar_inap.stts_pulang='-' order by STR_TO_DATE(concat(kamar_inap.tgl_masuk,' ',kamar_inap.jam_masuk),'%Y-%m-%d %H:%i:%s') desc limit 1",TNoRw.getText());
            } 
        }else if(status.equals("Ralan")){
            kelas="Rawat Jalan";
        }
        tampil();
        tampil2();
    }
    
    
    private void isForm(){
        if(ChkInput.isSelected()==true){
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH,532));
            FormInput.setVisible(true);      
            ChkInput.setVisible(true);
        }else if(ChkInput.isSelected()==false){           
            ChkInput.setVisible(false);            
            PanelInput.setPreferredSize(new Dimension(WIDTH,20));
            FormInput.setVisible(false);      
            ChkInput.setVisible(true);
        }
    }
    
    public void SetCariOperasi(String Operasi,String kodedokter,String namadokter){
    }
    
    public void emptTeks() {        
        DikirimPA.setSelectedItem("");
        PostOp.setText("");
        PreOp.setText("");
        Jaringan.setText("");
        Laporan.setText("");
        nmoperator1.setText("");
        kdoperator1.setText("");
    }
 
}
