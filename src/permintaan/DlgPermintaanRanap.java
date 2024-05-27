package permintaan;

import bridging.BPJSDataSEP;
import bridging.BPJSSPRI;
import fungsi.BackgroundMusic;
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
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import static java.awt.image.ImageObserver.HEIGHT;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import keuangan.DlgKamar;
import laporan.DlgCariPenyakit;
import simrskhanza.DlgKamarInap;
import rekammedis.RMRiwayatPerawatan;
import simrskhanza.DlgCariPasien;
import surat.SuratPersetujuanRawatInap;

/**
 *
 * @author dosen
 */
public class DlgPermintaanRanap extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private Connection koneksi=koneksiDB.condb();
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private PreparedStatement ps, psanak, pstarif, psibu, pscariumur;
    private ResultSet rs, rs2;
    private int i=0,nilai_detik,bookingbaru=0;
    private String alarm="",nol_detik,detik,sql="",finger="";
    private DlgKamar kamar=new DlgKamar(null,false);
    private boolean aktif=false;
    private BackgroundMusic music;
    private DlgCariPenyakit penyakit=new DlgCariPenyakit(null,false);
    private DlgCariDokter dokter = new DlgCariDokter(null, false);
    private DlgCariDokter dokter2 = new DlgCariDokter(null, false);
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
    private Date date = new Date();
    private String now = dateFormat.format(date), kd_pj;
    private String jml_hari = "1", hargakmr2, diagakir, umur = "0", sttsumur = "Th";
    private ResultSet rssetjam;
    private double hargakamar = 0;
    private DlgCariPasien pasien = new DlgCariPasien(null, false);
    private SimpleDateFormat dateformat3 = new SimpleDateFormat("yyyy/MM/dd");

    /** Creates new form DlgPemberianInfus
     * @param parent
     * @param modal */
    public DlgPermintaanRanap(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        jLabel16.setVisible(false);
        KdDokterBayi.setVisible(false);
        TDokterBayi.setVisible(false);
        BtnDokterBayi.setVisible(false);

        jLabel48.setVisible(false);
        TRMBayi.setVisible(false);
        TNamaBayi.setVisible(false);
        BtnSeek4.setVisible(false);
        NoRawatGabung.setVisible(false);
        
        tabMode=new DefaultTableModel(null,new Object[]{
                "No.Rawat","No.RM","Nama Pasien","J.K.","Umur","No.Telp","Cara Bayar","Asal Poli/Unit","Dokter Yang Memeriksa",
                "Tanggal","No.Bad/Kamar","Kode Bangsal","Kamar Diminta","Tarif Kamar","Diagnosa Awal","Catatan","KodeDokter",
                "Kd_Dok","Dokter DPJP","Gabung"
            }){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        tbObat.setModel(tabMode);

        //tbObat.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbObat.getBackground()));
        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 18; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(105);
            }else if(i==1){
                column.setPreferredWidth(70);
            }else if(i==2){
                column.setPreferredWidth(150);
            }else if(i==3){
                column.setPreferredWidth(25);
            }else if(i==4){
                column.setPreferredWidth(40);
            }else if(i==5){
                column.setPreferredWidth(90);
            }else if(i==6){
                column.setPreferredWidth(120);
            }else if(i==7){
                column.setPreferredWidth(130);
            }else if(i==8){
                column.setPreferredWidth(160);
            }else if(i==9){
                column.setPreferredWidth(65);
            }else if(i==10){
                column.setMinWidth(0);
                column.setMaxWidth(0);
            }else if(i==11){
                column.setMinWidth(0);
                column.setMaxWidth(0);
            }else if(i==12){
                column.setPreferredWidth(150);
            }else if(i==13){
                column.setPreferredWidth(75);
            }else if(i==14){
                column.setPreferredWidth(150);
            }else if(i==15){
                column.setPreferredWidth(150);
            } else if (i == 16 || i == 17 || i == 18) {
                column.setMinWidth(0);
                column.setMaxWidth(0);
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        NoRw.setDocument(new batasInput((byte)17).getKata(NoRw));
        TCari.setDocument(new batasInput((byte)100).getKata(TCari));
        Catatan.setDocument(new batasInput((byte)50).getKata(Catatan));
        Diagnosa.setDocument(new batasInput((byte)50).getKata(Diagnosa));
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
        
        kamar.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(kamar.getTable().getSelectedRow()!= -1){   
                    KdKamar.setText(kamar.getTable().getValueAt(kamar.getTable().getSelectedRow(),1).toString());
                    KdBangsal.setText(kamar.getTable().getValueAt(kamar.getTable().getSelectedRow(),2).toString());
                    NmBangsal.setText(kamar.getTable().getValueAt(kamar.getTable().getSelectedRow(),3).toString()+" ( "+kamar.getTable().getValueAt(kamar.getTable().getSelectedRow(),4).toString()+")");
                    HargaKamar.setText(Valid.SetAngka(Valid.SetAngka(kamar.getTable().getValueAt(kamar.getTable().getSelectedRow(),5).toString())));
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
        
                dokter.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {;
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (akses.getform().equals("DlgIGD")) {
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
                if (akses.getform().equals("DlgIGD")) {
                    if (dokter2.getTable().getSelectedRow() != -1) {
                        KdDokterBayi.setText(dokter2.getTable().getValueAt(dokter2.getTable().getSelectedRow(), 0).toString());
                        TDokterBayi.setText(dokter2.getTable().getValueAt(dokter2.getTable().getSelectedRow(), 1).toString());
                        //isNumber();
                        KdDokterBayi.requestFocus();
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
        
        kamar.getTable().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_SPACE){
                    kamar.dispose();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        }); 
        
        penyakit.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if( penyakit.getTable().getSelectedRow()!= -1){ 
                    if((penyakit.getTable().getValueAt(penyakit.getTable().getSelectedRow(),0).toString()+" - "+penyakit.getTable().getValueAt(penyakit.getTable().getSelectedRow(),1).toString()).length()<50){
                        Diagnosa.setText(penyakit.getTable().getValueAt(penyakit.getTable().getSelectedRow(),0).toString()+" - "+penyakit.getTable().getValueAt(penyakit.getTable().getSelectedRow(),1).toString());
                    }else{
                        Diagnosa.setText((penyakit.getTable().getValueAt(penyakit.getTable().getSelectedRow(),0).toString()+" - "+penyakit.getTable().getValueAt(penyakit.getTable().getSelectedRow(),1).toString()).substring(0,50));
                    }   
                }  
                Diagnosa.requestFocus();
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
                    TRMBayi.setText(pasien.getTable().getValueAt(pasien.getTable().getSelectedRow(), 0).toString());
                    TNamaBayi.setText(pasien.getTable().getValueAt(pasien.getTable().getSelectedRow(), 1).toString());
                }
                TRMBayi.requestFocus();
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
        
        ChkInput.setSelected(false);
        isForm();
        
        if(alarm.equals("yes")){
            //jam();
        }
        
        ChkAccor.setSelected(false);
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
        BtnKeluar = new widget.Button();
        panelGlass10 = new widget.panelisi();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        panelCari = new widget.panelisi();
        R1 = new widget.RadioButton();
        jLabel15 = new widget.Label();
        R2 = new widget.RadioButton();
        DTPCari1 = new widget.Tanggal();
        jLabel25 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        PanelInput = new javax.swing.JPanel();
        ChkInput = new widget.CekBox();
        FormInput = new widget.PanelBiasa();
        NoRw = new widget.TextBox();
        NmPasien = new widget.TextBox();
        DTPTgl = new widget.Tanggal();
        jLabel10 = new widget.Label();
        NoRM = new widget.TextBox();
        jLabel5 = new widget.Label();
        NoTelp = new widget.TextBox();
        jLabel8 = new widget.Label();
        Poli = new widget.TextBox();
        jLabel9 = new widget.Label();
        jLabel11 = new widget.Label();
        Dokter = new widget.TextBox();
        jLabel12 = new widget.Label();
        Diagnosa = new widget.TextBox();
        CaraBayar = new widget.TextBox();
        jLabel13 = new widget.Label();
        btnKamar = new widget.Button();
        NmBangsal = new widget.TextBox();
        KdBangsal = new widget.TextBox();
        KdKamar = new widget.TextBox();
        jLabel20 = new widget.Label();
        HargaKamar = new widget.TextBox();
        Catatan = new widget.TextBox();
        jLabel14 = new widget.Label();
        btnDiagnosa = new widget.Button();
        jLabel17 = new widget.Label();
        KdDokter1 = new widget.TextBox();
        TDokter = new widget.TextBox();
        BtnDokter = new widget.Button();
        ChkAccor1 = new widget.CekBox();
        NoRawatGabung = new widget.TextBox();
        TNamaBayi = new widget.TextBox();
        BtnSeek4 = new widget.Button();
        TRMBayi = new widget.TextBox();
        jLabel48 = new widget.Label();
        jLabel16 = new widget.Label();
        KdDokterBayi = new widget.TextBox();
        TDokterBayi = new widget.TextBox();
        BtnDokterBayi = new widget.Button();
        ChkAccor2 = new widget.CekBox();
        PanelAccor = new widget.PanelBiasa();
        ChkAccor = new widget.CekBox();
        ScrollMenu = new widget.ScrollPane();
        FormMenu = new widget.PanelBiasa();
        BtnKamarInap = new widget.Button();
        BtnRiwayatPasien = new widget.Button();
        BtnSuratPermintaan = new widget.Button();
        BtnSuratPRI = new widget.Button();
        BtnPersetujuanRanap = new widget.Button();
        BtnSuratPRI1 = new widget.Button();
        BtnSuratPRI2 = new widget.Button();
        BtnSuratPRI3 = new widget.Button();

        KdDokter.setEditable(false);
        KdDokter.setHighlighter(null);
        KdDokter.setName("KdDokter"); // NOI18N

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

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Permintaan Rawat Inap ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbObat.setAutoCreateRowSorter(true);
        tbObat.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
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
        jPanel3.setPreferredSize(new java.awt.Dimension(44, 144));
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

        panelCari.setName("panelCari"); // NOI18N
        panelCari.setPreferredSize(new java.awt.Dimension(44, 43));
        panelCari.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 9));

        buttonGroup1.add(R1);
        R1.setSelected(true);
        R1.setText("Menunggu Masuk Rawat Inap");
        R1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        R1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        R1.setName("R1"); // NOI18N
        R1.setPreferredSize(new java.awt.Dimension(195, 23));
        panelCari.add(R1);

        jLabel15.setName("jLabel15"); // NOI18N
        jLabel15.setPreferredSize(new java.awt.Dimension(40, 23));
        panelCari.add(jLabel15);

        buttonGroup1.add(R2);
        R2.setText("Sudah Masuk Rawat Inap :");
        R2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        R2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        R2.setName("R2"); // NOI18N
        R2.setPreferredSize(new java.awt.Dimension(165, 23));
        panelCari.add(R2);

        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "27-05-2024" }));
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
        panelCari.add(DTPCari1);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setText("s.d");
        jLabel25.setName("jLabel25"); // NOI18N
        jLabel25.setPreferredSize(new java.awt.Dimension(30, 23));
        panelCari.add(jLabel25);

        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "27-05-2024" }));
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
        panelCari.add(DTPCari2);

        jPanel3.add(panelCari, java.awt.BorderLayout.PAGE_START);

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(192, 186));
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
        FormInput.setPreferredSize(new java.awt.Dimension(190, 77));
        FormInput.setLayout(null);

        NoRw.setEditable(false);
        NoRw.setHighlighter(null);
        NoRw.setName("NoRw"); // NOI18N
        NoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoRwKeyPressed(evt);
            }
        });
        FormInput.add(NoRw);
        NoRw.setBounds(73, 10, 125, 23);

        NmPasien.setEditable(false);
        NmPasien.setHighlighter(null);
        NmPasien.setName("NmPasien"); // NOI18N
        FormInput.add(NmPasien);
        NmPasien.setBounds(288, 10, 330, 23);

        DTPTgl.setForeground(new java.awt.Color(50, 70, 50));
        DTPTgl.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "27-05-2024" }));
        DTPTgl.setDisplayFormat("dd-MM-yyyy");
        DTPTgl.setName("DTPTgl"); // NOI18N
        DTPTgl.setOpaque(false);
        DTPTgl.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DTPTglKeyPressed(evt);
            }
        });
        FormInput.add(DTPTgl);
        DTPTgl.setBounds(528, 70, 90, 23);

        jLabel10.setText("Tanggal :");
        jLabel10.setName("jLabel10"); // NOI18N
        FormInput.add(jLabel10);
        jLabel10.setBounds(454, 70, 70, 23);

        NoRM.setEditable(false);
        NoRM.setHighlighter(null);
        NoRM.setName("NoRM"); // NOI18N
        FormInput.add(NoRM);
        NoRM.setBounds(200, 10, 86, 23);

        jLabel5.setText("No.Rawat :");
        jLabel5.setName("jLabel5"); // NOI18N
        FormInput.add(jLabel5);
        jLabel5.setBounds(0, 10, 69, 23);

        NoTelp.setEditable(false);
        NoTelp.setHighlighter(null);
        NoTelp.setName("NoTelp"); // NOI18N
        FormInput.add(NoTelp);
        NoTelp.setBounds(73, 40, 120, 23);

        jLabel8.setText("No.Telp :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(0, 40, 69, 23);

        Poli.setEditable(false);
        Poli.setHighlighter(null);
        Poli.setName("Poli"); // NOI18N
        FormInput.add(Poli);
        Poli.setBounds(459, 40, 159, 23);

        jLabel9.setText("Unit/Poli :");
        jLabel9.setName("jLabel9"); // NOI18N
        FormInput.add(jLabel9);
        jLabel9.setBounds(395, 40, 60, 23);

        jLabel11.setText("Dokter :");
        jLabel11.setName("jLabel11"); // NOI18N
        FormInput.add(jLabel11);
        jLabel11.setBounds(0, 70, 69, 23);

        Dokter.setEditable(false);
        Dokter.setHighlighter(null);
        Dokter.setName("Dokter"); // NOI18N
        FormInput.add(Dokter);
        Dokter.setBounds(73, 70, 318, 23);

        jLabel12.setText("Diagnosa :");
        jLabel12.setName("jLabel12"); // NOI18N
        FormInput.add(jLabel12);
        jLabel12.setBounds(0, 130, 69, 23);

        Diagnosa.setHighlighter(null);
        Diagnosa.setName("Diagnosa"); // NOI18N
        Diagnosa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosaKeyPressed(evt);
            }
        });
        FormInput.add(Diagnosa);
        Diagnosa.setBounds(73, 130, 213, 23);

        CaraBayar.setEditable(false);
        CaraBayar.setHighlighter(null);
        CaraBayar.setName("CaraBayar"); // NOI18N
        FormInput.add(CaraBayar);
        CaraBayar.setBounds(271, 40, 120, 23);

        jLabel13.setText("Cara Bayar :");
        jLabel13.setName("jLabel13"); // NOI18N
        FormInput.add(jLabel13);
        jLabel13.setBounds(192, 40, 75, 23);

        btnKamar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnKamar.setMnemonic('2');
        btnKamar.setToolTipText("Alt+2");
        btnKamar.setName("btnKamar"); // NOI18N
        btnKamar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKamarActionPerformed(evt);
            }
        });
        FormInput.add(btnKamar);
        btnKamar.setBounds(590, 100, 28, 23);

        NmBangsal.setEditable(false);
        NmBangsal.setHighlighter(null);
        NmBangsal.setName("NmBangsal"); // NOI18N
        FormInput.add(NmBangsal);
        NmBangsal.setBounds(222, 100, 260, 23);

        KdBangsal.setEditable(false);
        KdBangsal.setName("KdBangsal"); // NOI18N
        FormInput.add(KdBangsal);
        KdBangsal.setBounds(155, 100, 65, 23);

        KdKamar.setEditable(false);
        KdKamar.setHighlighter(null);
        KdKamar.setName("KdKamar"); // NOI18N
        FormInput.add(KdKamar);
        KdKamar.setBounds(73, 100, 80, 23);

        jLabel20.setText("Kamar :");
        jLabel20.setName("jLabel20"); // NOI18N
        FormInput.add(jLabel20);
        jLabel20.setBounds(0, 100, 69, 23);

        HargaKamar.setEditable(false);
        HargaKamar.setHighlighter(null);
        HargaKamar.setName("HargaKamar"); // NOI18N
        FormInput.add(HargaKamar);
        HargaKamar.setBounds(484, 100, 103, 23);

        Catatan.setEditable(false);
        Catatan.setHighlighter(null);
        Catatan.setName("Catatan"); // NOI18N
        Catatan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CatatanKeyPressed(evt);
            }
        });
        FormInput.add(Catatan);
        Catatan.setBounds(390, 130, 120, 23);

        jLabel14.setText("Catatan :");
        jLabel14.setName("jLabel14"); // NOI18N
        FormInput.add(jLabel14);
        jLabel14.setBounds(326, 130, 60, 23);

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
        btnDiagnosa.setBounds(289, 130, 28, 23);

        jLabel17.setText("DPJP Ranap :");
        jLabel17.setName("jLabel17"); // NOI18N
        FormInput.add(jLabel17);
        jLabel17.setBounds(630, 10, 80, 23);

        KdDokter1.setHighlighter(null);
        KdDokter1.setName("KdDokter1"); // NOI18N
        KdDokter1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdDokter1KeyPressed(evt);
            }
        });
        FormInput.add(KdDokter1);
        KdDokter1.setBounds(720, 10, 90, 23);

        TDokter.setEditable(false);
        TDokter.setName("TDokter"); // NOI18N
        FormInput.add(TDokter);
        TDokter.setBounds(820, 10, 200, 23);

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
        BtnDokter.setBounds(1020, 10, 28, 23);

        ChkAccor1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(250, 255, 248)));
        ChkAccor1.setText("Rawat Gabung");
        ChkAccor1.setFocusable(false);
        ChkAccor1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkAccor1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkAccor1.setName("ChkAccor1"); // NOI18N
        ChkAccor1.setPreferredSize(new java.awt.Dimension(15, 20));
        ChkAccor1.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/kanan.png"))); // NOI18N
        ChkAccor1.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/kiri.png"))); // NOI18N
        ChkAccor1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/kiri.png"))); // NOI18N
        ChkAccor1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkAccor1ActionPerformed(evt);
            }
        });
        FormInput.add(ChkAccor1);
        ChkAccor1.setBounds(640, 40, 100, 20);

        NoRawatGabung.setName("NoRawatGabung"); // NOI18N
        NoRawatGabung.setPreferredSize(new java.awt.Dimension(130, 23));
        NoRawatGabung.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NoRawatGabungActionPerformed(evt);
            }
        });
        NoRawatGabung.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoRawatGabungKeyPressed(evt);
            }
        });
        FormInput.add(NoRawatGabung);
        NoRawatGabung.setBounds(750, 40, 270, 23);

        TNamaBayi.setName("TNamaBayi"); // NOI18N
        TNamaBayi.setPreferredSize(new java.awt.Dimension(130, 23));
        TNamaBayi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNamaBayiKeyPressed(evt);
            }
        });
        FormInput.add(TNamaBayi);
        TNamaBayi.setBounds(820, 70, 200, 23);

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
        BtnSeek4.setBounds(1020, 70, 28, 23);

        TRMBayi.setName("TRMBayi"); // NOI18N
        TRMBayi.setPreferredSize(new java.awt.Dimension(130, 23));
        TRMBayi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TRMBayiKeyPressed(evt);
            }
        });
        FormInput.add(TRMBayi);
        TRMBayi.setBounds(720, 70, 90, 23);

        jLabel48.setText("No.RM Bayi :");
        jLabel48.setName("jLabel48"); // NOI18N
        jLabel48.setPreferredSize(new java.awt.Dimension(50, 23));
        FormInput.add(jLabel48);
        jLabel48.setBounds(640, 70, 70, 23);

        jLabel16.setText("DPJP Bayi :");
        jLabel16.setName("jLabel16"); // NOI18N
        FormInput.add(jLabel16);
        jLabel16.setBounds(630, 100, 80, 23);

        KdDokterBayi.setHighlighter(null);
        KdDokterBayi.setName("KdDokterBayi"); // NOI18N
        KdDokterBayi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdDokterBayiKeyPressed(evt);
            }
        });
        FormInput.add(KdDokterBayi);
        KdDokterBayi.setBounds(720, 100, 90, 23);

        TDokterBayi.setEditable(false);
        TDokterBayi.setName("TDokterBayi"); // NOI18N
        FormInput.add(TDokterBayi);
        TDokterBayi.setBounds(820, 100, 200, 23);

        BtnDokterBayi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnDokterBayi.setMnemonic('3');
        BtnDokterBayi.setToolTipText("ALt+3");
        BtnDokterBayi.setName("BtnDokterBayi"); // NOI18N
        BtnDokterBayi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnDokterBayiActionPerformed(evt);
            }
        });
        FormInput.add(BtnDokterBayi);
        BtnDokterBayi.setBounds(1020, 100, 28, 23);

        ChkAccor2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(250, 255, 248)));
        ChkAccor2.setText("Titip Kamar");
        ChkAccor2.setFocusable(false);
        ChkAccor2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkAccor2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkAccor2.setName("ChkAccor2"); // NOI18N
        ChkAccor2.setPreferredSize(new java.awt.Dimension(15, 20));
        ChkAccor2.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/kanan.png"))); // NOI18N
        ChkAccor2.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/kiri.png"))); // NOI18N
        ChkAccor2.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/kiri.png"))); // NOI18N
        ChkAccor2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkAccor2ActionPerformed(evt);
            }
        });
        FormInput.add(ChkAccor2);
        ChkAccor2.setBounds(510, 130, 80, 20);

        PanelInput.add(FormInput, java.awt.BorderLayout.CENTER);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        PanelAccor.setBackground(new java.awt.Color(255, 255, 255));
        PanelAccor.setName("PanelAccor"); // NOI18N
        PanelAccor.setPreferredSize(new java.awt.Dimension(175, 43));
        PanelAccor.setLayout(new java.awt.BorderLayout());

        ChkAccor.setBackground(new java.awt.Color(255, 250, 250));
        ChkAccor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(250, 255, 248)));
        ChkAccor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/kanan.png"))); // NOI18N
        ChkAccor.setFocusable(false);
        ChkAccor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkAccor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkAccor.setName("ChkAccor"); // NOI18N
        ChkAccor.setPreferredSize(new java.awt.Dimension(15, 20));
        ChkAccor.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/kanan.png"))); // NOI18N
        ChkAccor.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/kiri.png"))); // NOI18N
        ChkAccor.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/kiri.png"))); // NOI18N
        ChkAccor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkAccorActionPerformed(evt);
            }
        });
        PanelAccor.add(ChkAccor, java.awt.BorderLayout.EAST);

        ScrollMenu.setBorder(null);
        ScrollMenu.setName("ScrollMenu"); // NOI18N
        ScrollMenu.setOpaque(true);

        FormMenu.setBackground(new java.awt.Color(255, 255, 255));
        FormMenu.setBorder(null);
        FormMenu.setName("FormMenu"); // NOI18N
        FormMenu.setPreferredSize(new java.awt.Dimension(115, 43));
        FormMenu.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 1, 1));

        BtnKamarInap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnKamarInap.setText("Kamar Inap");
        BtnKamarInap.setFocusPainted(false);
        BtnKamarInap.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnKamarInap.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnKamarInap.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnKamarInap.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnKamarInap.setName("BtnKamarInap"); // NOI18N
        BtnKamarInap.setPreferredSize(new java.awt.Dimension(160, 23));
        BtnKamarInap.setRoundRect(false);
        BtnKamarInap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKamarInapActionPerformed(evt);
            }
        });
        FormMenu.add(BtnKamarInap);

        BtnRiwayatPasien.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnRiwayatPasien.setText("Riwayat Perawatan");
        BtnRiwayatPasien.setFocusPainted(false);
        BtnRiwayatPasien.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnRiwayatPasien.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnRiwayatPasien.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnRiwayatPasien.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnRiwayatPasien.setName("BtnRiwayatPasien"); // NOI18N
        BtnRiwayatPasien.setPreferredSize(new java.awt.Dimension(160, 23));
        BtnRiwayatPasien.setRoundRect(false);
        BtnRiwayatPasien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnRiwayatPasienActionPerformed(evt);
            }
        });
        FormMenu.add(BtnRiwayatPasien);

        BtnSuratPermintaan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnSuratPermintaan.setText("Surat Permintaan Ranap");
        BtnSuratPermintaan.setFocusPainted(false);
        BtnSuratPermintaan.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnSuratPermintaan.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnSuratPermintaan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnSuratPermintaan.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnSuratPermintaan.setName("BtnSuratPermintaan"); // NOI18N
        BtnSuratPermintaan.setPreferredSize(new java.awt.Dimension(160, 23));
        BtnSuratPermintaan.setRoundRect(false);
        BtnSuratPermintaan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSuratPermintaanActionPerformed(evt);
            }
        });
        FormMenu.add(BtnSuratPermintaan);

        BtnSuratPRI.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnSuratPRI.setText("Perintah Rawat Inap BPJS");
        BtnSuratPRI.setFocusPainted(false);
        BtnSuratPRI.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnSuratPRI.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnSuratPRI.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnSuratPRI.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnSuratPRI.setName("BtnSuratPRI"); // NOI18N
        BtnSuratPRI.setPreferredSize(new java.awt.Dimension(160, 23));
        BtnSuratPRI.setRoundRect(false);
        BtnSuratPRI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSuratPRIActionPerformed(evt);
            }
        });
        FormMenu.add(BtnSuratPRI);

        BtnPersetujuanRanap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnPersetujuanRanap.setText("Persetujuan Rawat Inap");
        BtnPersetujuanRanap.setFocusPainted(false);
        BtnPersetujuanRanap.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnPersetujuanRanap.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnPersetujuanRanap.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnPersetujuanRanap.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnPersetujuanRanap.setName("BtnPersetujuanRanap"); // NOI18N
        BtnPersetujuanRanap.setPreferredSize(new java.awt.Dimension(160, 23));
        BtnPersetujuanRanap.setRoundRect(false);
        BtnPersetujuanRanap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPersetujuanRanapActionPerformed(evt);
            }
        });
        FormMenu.add(BtnPersetujuanRanap);

        BtnSuratPRI1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnSuratPRI1.setText("Cetak Label Pasien");
        BtnSuratPRI1.setFocusPainted(false);
        BtnSuratPRI1.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnSuratPRI1.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnSuratPRI1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnSuratPRI1.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnSuratPRI1.setName("BtnSuratPRI1"); // NOI18N
        BtnSuratPRI1.setPreferredSize(new java.awt.Dimension(160, 23));
        BtnSuratPRI1.setRoundRect(false);
        BtnSuratPRI1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSuratPRI1ActionPerformed(evt);
            }
        });
        FormMenu.add(BtnSuratPRI1);

        BtnSuratPRI2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnSuratPRI2.setText("Cetak Gelang Pasien");
        BtnSuratPRI2.setFocusPainted(false);
        BtnSuratPRI2.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnSuratPRI2.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnSuratPRI2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnSuratPRI2.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnSuratPRI2.setName("BtnSuratPRI2"); // NOI18N
        BtnSuratPRI2.setPreferredSize(new java.awt.Dimension(160, 23));
        BtnSuratPRI2.setRoundRect(false);
        BtnSuratPRI2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSuratPRI2ActionPerformed(evt);
            }
        });
        FormMenu.add(BtnSuratPRI2);

        BtnSuratPRI3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnSuratPRI3.setText("Cetak SEP Ranap");
        BtnSuratPRI3.setFocusPainted(false);
        BtnSuratPRI3.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnSuratPRI3.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnSuratPRI3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnSuratPRI3.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnSuratPRI3.setName("BtnSuratPRI3"); // NOI18N
        BtnSuratPRI3.setPreferredSize(new java.awt.Dimension(160, 23));
        BtnSuratPRI3.setRoundRect(false);
        BtnSuratPRI3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSuratPRI3ActionPerformed(evt);
            }
        });
        FormMenu.add(BtnSuratPRI3);

        ScrollMenu.setViewportView(FormMenu);

        PanelAccor.add(ScrollMenu, java.awt.BorderLayout.CENTER);

        internalFrame1.add(PanelAccor, java.awt.BorderLayout.WEST);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void NoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoRwKeyPressed
        //Valid.pindah(evt,Status,KdDokter);
        
}//GEN-LAST:event_NoRwKeyPressed

    private void DTPTglKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DTPTglKeyPressed
        Valid.pindah(evt,TCari,Diagnosa);
}//GEN-LAST:event_DTPTglKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
                if (ChkAccor1.isSelected() == false) {
            System.out.println("non gabung");
            if (NoRw.getText().trim().equals("") || NoRM.getText().trim().equals("") || NmPasien.getText().trim().equals("") || TDokter.getText().trim().equals("")) {
                Valid.textKosong(TCari, "Pasien");
            } else if (KdBangsal.getText().trim().equals("") || KdKamar.getText().trim().equals("") || NmBangsal.getText().trim().equals("")) {
                Valid.textKosong(btnKamar, "Kamar/Bangsal");
            } else if (Diagnosa.getText().trim().equals("")) {
                Valid.textKosong(Diagnosa, "Diagnosa");
            } else {
                String titip_kamar = "-";
                if (ChkAccor2.isSelected()) {
                    titip_kamar = "Titip";
                } else {
                    titip_kamar = "-";
                }
                if (Sequel.menyimpantf("permintaan_ranap", "?,?,?,?,?", "Pasien", 5, new String[]{
                    NoRw.getText(),
                    Valid.SetTgl(DTPTgl.getSelectedItem() + ""),
                    KdKamar.getText(),
                    Diagnosa.getText(),
                    Catatan.getText() + "#" + titip_kamar
                }) == true) {
                    Sequel.mengedit("kamar", "kd_kamar=?", "status='DIBOOKING'", 1, new String[]{KdKamar.getText()});
                    if (Sequel.menyimpantf("dpjp_ranap", "?,?", "Dokter", 2, new String[]{
                        NoRw.getText(),
                        KdDokter1.getText()
                    }) == true) {
                        System.out.println("simpan dpjp Berhasil" + NoRw.getText() + KdDokter1.getText());
                        emptTeks();
                    }
                    tampil();
                }
            }
        } else {
            if (NoRw.getText().trim().equals("") || NoRM.getText().trim().equals("") || NmPasien.getText().trim().equals("") || TDokter.getText().trim().equals("")) {
                Valid.textKosong(TCari, "Pasien");
            } else if (KdBangsal.getText().trim().equals("") || KdKamar.getText().trim().equals("") || NmBangsal.getText().trim().equals("")) {
                Valid.textKosong(btnKamar, "Kamar/Bangsal");
            } else if (Diagnosa.getText().trim().equals("")) {
                Valid.textKosong(Diagnosa, "Diagnosa");
            } else if (TRMBayi.getText().trim().equals("")) {
                Valid.textKosong(TRMBayi, "RM Bayi");
            } else if (KdDokterBayi.getText().trim().equals("")) {
                Valid.textKosong(KdDokterBayi, "DPJP Bayi");
            } else {
                if (Sequel.menyimpantf("permintaan_ranap", "?,?,?,?,?", "Pasien", 5, new String[]{
                    NoRw.getText(),
                    Valid.SetTgl(DTPTgl.getSelectedItem() + ""),
                    KdKamar.getText(),
                    Diagnosa.getText(),
                    Catatan.getText()
                }) == true) {

                    Sequel.mengedit("kamar", "kd_kamar=?", "status='DIBOOKING'", 1, new String[]{KdKamar.getText()});

                    if (Sequel.menyimpantf("dpjp_ranap", "?,?", "Dokter", 2, new String[]{
                        NoRw.getText(),
                        KdDokter1.getText()
                    }) == true) {
                        System.out.println("simpan dpjp Berhasil" + NoRw.getText() + KdDokter1.getText());

                        if (NoRw.getText().trim().equals("")) {
                            Valid.textKosong(TRMBayi, "Pasien");
                        } else {
                            try {
                                psibu = koneksi.prepareStatement("select no_reg,tgl_registrasi,jam_reg,kd_dokter,no_rkm_medis,kd_poli,p_jawab,"
                                        + "almt_pj,hubunganpj,biaya_reg,stts,stts_daftar,status_lanjut,kd_pj from reg_periksa where no_rawat=?");
                                try {
                                    psibu.setString(1, NoRw.getText());
                                    rs = psibu.executeQuery();
                                    if (rs.next()) {
                                        pscariumur = koneksi.prepareStatement(
                                                "select TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()) as tahun, "
                                                + "(TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12)) as bulan, "
                                                + "TIMESTAMPDIFF(DAY, DATE_ADD(DATE_ADD(tgl_lahir,INTERVAL TIMESTAMPDIFF(YEAR, tgl_lahir, CURDATE()) YEAR), INTERVAL TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) - ((TIMESTAMPDIFF(MONTH, tgl_lahir, CURDATE()) div 12) * 12) MONTH), CURDATE()) as hari "
                                                + "from pasien where no_rkm_medis=?");
                                        try {
                                            pscariumur.setString(1, TRMBayi.getText());
                                            rs2 = pscariumur.executeQuery();
                                            if (rs2.next()) {
                                                umur = "0";
                                                sttsumur = "Th";
                                                if (rs2.getInt("tahun") > 0) {
                                                    umur = rs2.getString("tahun");
                                                    sttsumur = "Th";
                                                } else if (rs2.getInt("tahun") == 0) {
                                                    if (rs2.getInt("bulan") > 0) {
                                                        umur = rs2.getString("bulan");
                                                        sttsumur = "Bl";
                                                    } else if (rs2.getInt("bulan") == 0) {
                                                        umur = rs2.getString("hari");
                                                        sttsumur = "Hr";
                                                    }
                                                }
                                            }
                                        } catch (Exception e) {
                                            System.out.println("Notifikasi Umur : " + e);
                                        } finally {
                                            if (rs2 != null) {
                                                rs2.close();
                                            }
                                            if (pscariumur != null) {
                                                pscariumur.close();
                                            }
                                        }
                                        Valid.autoNomer3("select (ifnull(MAX(CONVERT(RIGHT(no_rawat,6),signed)),0)+1) from reg_periksa where tgl_registrasi='" + rs.getString("tgl_registrasi") + "' ", dateformat3.format(rs.getDate("tgl_registrasi")) + "/", 6, NoRawatGabung);
                                        if (Sequel.menyimpantf2("reg_periksa", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Reg Periksa", 19,
                                                new String[]{rs.getString("no_reg"), NoRawatGabung.getText(), rs.getString("tgl_registrasi"), rs.getString("jam_reg"),
                                                    rs.getString("kd_dokter"), TRMBayi.getText(), rs.getString("kd_poli"), rs.getString("p_jawab"),
                                                    rs.getString("almt_pj"), rs.getString("hubunganpj"), rs.getString("biaya_reg"), "Belum", "Baru", "Ranap", rs.getString("kd_pj"), umur, sttsumur, "Sudah Bayar", "Baru"}) == true) {
                                            Sequel.menyimpan("ranap_gabung", "?,?", "Data Ranap Gabung", 2, new String[]{
                                                NoRw.getText(), NoRawatGabung.getText()
                                            });
                                            if (Sequel.menyimpantf("dpjp_ranap", "?,?", "Dokter", 2, new String[]{
                                                NoRawatGabung.getText(),
                                                KdDokterBayi.getText()
                                            }) == true) {
                                                System.out.println("Sukses DPJP Bayi");
                                            }
                                        }
                                    }
                                } catch (Exception ex) {
                                    System.out.println("Notifikasi : " + ex);
                                } finally {
                                    if (rs != null) {
                                        rs.close();
                                    }
                                    if (psibu != null) {
                                        psibu.close();
                                    }
                                }

                                tampil();
                            } catch (Exception e) {
                                System.out.println(e);
                            }
                        }
                        emptTeks();
                    }
                    tampil();
                }
            }
        }
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnSimpanActionPerformed(null);
        }else{
           Valid.pindah(evt,Catatan,BtnBatal);
        }
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
        if(Valid.hapusTabletf(tabMode,NoRw,"permintaan_ranap","no_rawat")==true){
            tabMode.removeRow(tbObat.getSelectedRow());
            Valid.hapusTable(tabMode, NoRw, "dpjp_ranap", "no_rawat");
            Valid.hapusTable(tabMode, NoRawatGabung, "dpjp_ranap", "no_rawat");
            Valid.hapusTable(tabMode, NoRw, "ranap_gabung", "no_rawat");
            Valid.hapusTable(tabMode, NoRawatGabung, "reg_periksa", "no_rawat");
            emptTeks();
            LCount.setText(""+tabMode.getRowCount());
        }
}//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnHapusActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnBatal, BtnPrint);
        }
}//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
}//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            dispose();
        }else{
            Valid.pindah(evt,BtnPrint,TCari);
        }
}//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            BtnBatal.requestFocus();
        }else if(tabMode.getRowCount()!=0){
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> param = new HashMap<>();    
            param.put("namars",akses.getnamars());
            param.put("alamatrs",akses.getalamatrs());
            param.put("kotars",akses.getkabupatenrs());
            param.put("propinsirs",akses.getpropinsirs());
            param.put("kontakrs",akses.getkontakrs());
            param.put("emailrs",akses.getemailrs());   
            param.put("logo",Sequel.cariGambar("select setting.logo from setting")); 
            sql="";
            if(R1.isSelected()==true){
                sql="select permintaan_ranap.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.jk,reg_periksa.umurdaftar,reg_periksa.sttsumur,"+
                    "pasien.no_tlp,penjab.png_jawab,poliklinik.nm_poli,dokter.nm_dokter,permintaan_ranap.tanggal,permintaan_ranap.kd_kamar,kamar.kd_bangsal,"+
                    "bangsal.nm_bangsal,kamar.trf_kamar,permintaan_ranap.diagnosa,permintaan_ranap.catatan from permintaan_ranap "+
                    "inner join reg_periksa on permintaan_ranap.no_rawat=reg_periksa.no_rawat "+
                    "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                    "inner join penjab on reg_periksa.kd_pj=penjab.kd_pj "+
                    "inner join dokter on reg_periksa.kd_dokter=dokter.kd_dokter "+
                    "inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli "+
                    "inner join kamar on permintaan_ranap.kd_kamar=kamar.kd_kamar "+
                    "inner join bangsal on kamar.kd_bangsal=bangsal.kd_bangsal "+
                    "where permintaan_ranap.no_rawat not in (select DISTINCT no_rawat from kamar_inap) "+
                    (TCari.getText().equals("")?"":"and (permintaan_ranap.no_rawat like '%"+TCari.getText().trim()+"%' or reg_periksa.no_rkm_medis like '%"+TCari.getText().trim()+"%' or pasien.nm_pasien like '%"+TCari.getText().trim()+"%' "+
                    "or penjab.png_jawab like '%"+TCari.getText().trim()+"%' or poliklinik.nm_poli like '%"+TCari.getText().trim()+"%' or dokter.nm_dokter like '%"+TCari.getText().trim()+"%' or bangsal.nm_bangsal like '%"+TCari.getText().trim()+"%' "+
                    "or permintaan_ranap.diagnosa like '%"+TCari.getText().trim()+"%')")+" order by permintaan_ranap.tanggal";
            }else if(R2.isSelected()==true){
                sql="select permintaan_ranap.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.jk,reg_periksa.umurdaftar,reg_periksa.sttsumur,"+
                    "pasien.no_tlp,penjab.png_jawab,poliklinik.nm_poli,dokter.nm_dokter,permintaan_ranap.tanggal,permintaan_ranap.kd_kamar,kamar.kd_bangsal,"+
                    "bangsal.nm_bangsal,kamar.trf_kamar,permintaan_ranap.diagnosa,permintaan_ranap.catatan from permintaan_ranap "+
                    "inner join reg_periksa on permintaan_ranap.no_rawat=reg_periksa.no_rawat "+
                    "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                    "inner join penjab on reg_periksa.kd_pj=penjab.kd_pj "+
                    "inner join dokter on reg_periksa.kd_dokter=dokter.kd_dokter "+
                    "inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli "+
                    "inner join kamar on permintaan_ranap.kd_kamar=kamar.kd_kamar "+
                    "inner join bangsal on kamar.kd_bangsal=bangsal.kd_bangsal "+
                    "where permintaan_ranap.no_rawat in (select DISTINCT no_rawat from kamar_inap) and permintaan_ranap.tanggal between '"+Valid.SetTgl(DTPCari1.getSelectedItem()+"")+"' and '"+Valid.SetTgl(DTPCari2.getSelectedItem()+"")+"' "+
                    (TCari.getText().equals("")?"":"and (permintaan_ranap.no_rawat like '%"+TCari.getText().trim()+"%' or reg_periksa.no_rkm_medis like '%"+TCari.getText().trim()+"%' or pasien.nm_pasien like '%"+TCari.getText().trim()+"%' "+
                    "or penjab.png_jawab like '%"+TCari.getText().trim()+"%' or poliklinik.nm_poli like '%"+TCari.getText().trim()+"%' or dokter.nm_dokter like '%"+TCari.getText().trim()+"%' or bangsal.nm_bangsal like '%"+TCari.getText().trim()+"%' "+
                    "or permintaan_ranap.diagnosa like '%"+TCari.getText().trim()+"%')")+" order by permintaan_ranap.tanggal";
            }
            
            Valid.MyReportqry("rptPermintaanRawatInap.jasper","report","::[ Data Pemesanan Rawat Inap ]::",sql,param);
            this.setCursor(Cursor.getDefaultCursor());
        }
}//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnPrintActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnHapus, BtnKeluar);
        }
}//GEN-LAST:event_BtnPrintKeyPressed

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
                if (NoRw.getText().trim().equals("") || NoRM.getText().trim().equals("") || NmPasien.getText().trim().equals("")) {
            Valid.textKosong(TCari, "Pasien");
        } else if (KdBangsal.getText().trim().equals("") || KdKamar.getText().trim().equals("") || NmBangsal.getText().trim().equals("")) {
            Valid.textKosong(btnKamar, "Kamar/Bangsal");
        } else if (Diagnosa.getText().trim().equals("")) {
            Valid.textKosong(Diagnosa, "Diagnosa");
        } else {
            if (tbObat.getSelectedRow() > -1) {
                String titip_kamar = "-";
                if (ChkAccor2.isSelected()) {
                    titip_kamar = "Titip";
                } else {
                    titip_kamar = "-";
                }
                if (Sequel.mengedittf("permintaan_ranap", "no_rawat=?", "no_rawat=?,tanggal=?,kd_kamar=?,diagnosa=?,catatan=?", 6, new String[]{
                    NoRw.getText(), Valid.SetTgl(DTPTgl.getSelectedItem() + ""), KdKamar.getText(), Diagnosa.getText(), Catatan.getText() + "#" + titip_kamar, tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString()
                }) == true) {
                    tampil();
                    Sequel.mengedit("kamar", "kd_kamar=?", "status='DIBOOKING'", 1, new String[]{KdKamar.getText()});
                    emptTeks();
                }
            }
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

    private void btnKamarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKamarActionPerformed
        kamar.load();
        kamar.isCek();
        kamar.emptTeks();
        kamar.tampil();
        kamar.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        kamar.setLocationRelativeTo(internalFrame1);
        kamar.setVisible(true);        
    }//GEN-LAST:event_btnKamarActionPerformed

    private void DiagnosaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DiagnosaKeyPressed
        Valid.pindah(evt,DTPTgl,Catatan);
    }//GEN-LAST:event_DiagnosaKeyPressed

    private void CatatanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CatatanKeyPressed
        Valid.pindah(evt,Diagnosa,BtnSimpan);
    }//GEN-LAST:event_CatatanKeyPressed

    private void DTPCari2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DTPCari2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_DTPCari2KeyPressed

    private void DTPCari2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_DTPCari2ItemStateChanged
        R2.setSelected(true);
    }//GEN-LAST:event_DTPCari2ItemStateChanged

    private void DTPCari1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DTPCari1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_DTPCari1KeyPressed

    private void DTPCari1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_DTPCari1ItemStateChanged
        R2.setSelected(true);
    }//GEN-LAST:event_DTPCari1ItemStateChanged

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        aktif=false;
        kamar.dispose();
    }//GEN-LAST:event_formWindowClosed

    private void btnDiagnosaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiagnosaActionPerformed
        penyakit.isCek();
        penyakit.emptTeks();
        penyakit.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        penyakit.setLocationRelativeTo(internalFrame1);
        penyakit.setVisible(true);
    }//GEN-LAST:event_btnDiagnosaActionPerformed

    private void btnDiagnosaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnDiagnosaKeyPressed
        Valid.pindah(evt,Diagnosa,Catatan);
    }//GEN-LAST:event_btnDiagnosaKeyPressed

    private void ChkAccorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkAccorActionPerformed
        isMenu();
    }//GEN-LAST:event_ChkAccorActionPerformed

    private void BtnKamarInapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKamarInapActionPerformed
                if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                if (Sequel.cariRegistrasi(NoRw.getText()) > 0) {
                    JOptionPane.showMessageDialog(rootPane, "Data billing sudah terverifikasi..!!");
                } else {
                    //System.out.println(Valid.SetTgl(DTPTgl.getSelectedItem() + ""));
                    kd_pj = CaraBayar.getText();
                    try {
                        hargakamar = 0;
                        pstarif = koneksi.prepareStatement("select set_harga_kamar.tarif from set_harga_kamar JOIN penjab pj ON pj.kd_pj = set_harga_kamar.kd_pj where set_harga_kamar.kd_kamar=? AND pj.png_jawab=?");
                        try {
                            pstarif.setString(1, KdKamar.getText());
                            pstarif.setString(2, kd_pj);
                            rs = pstarif.executeQuery();
                            if (rs.next()) {
                                //TTarif.setText(rs.getString(1));
                                hargakmr2 = rs.getString(1);
                            } else {
                                //TTarif.setText(hargakamar + "");
                                hargakmr2 = HargaKamar.getText().replace(",", "");
                            }
                        } catch (Exception e) {
                            System.out.println("Notifikasi : " + e);
                        } finally {
                            if (rs != null) {
                                rs.close();
                            }
                            if (pstarif != null) {
                                pstarif.close();
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Notifikasi : " + e);
                    }
                    diagakir = "";
                    //System.out.println(hargakmr2);
                    //System.out.println(kd_pj);
                    //System.out.println(hargakamar);

                    if (Sequel.menyimpantf("kamar_inap", "'" + NoRw.getText() + "','"
                            + KdKamar.getText() + "','" + hargakmr2 + "','"
                            + Diagnosa.getText() + "','"
                            + diagakir + "','"
                            + Valid.SetTgl(DTPTgl.getSelectedItem() + "") + "','"
                            + Catatan.getText() + "','0000-00-00','00:00:00','" + jml_hari + "','"
                            + hargakmr2 + "','-'", "No.Rawat") == true) {
                        Sequel.mengedit("reg_periksa", "no_rawat='" + NoRw.getText() + "'", "status_lanjut='Ranap'");
                        Sequel.mengedit("kamar", "kd_kamar='" + KdKamar.getText() + "'", "status='ISI'");

                        String titip_kamar = "-";
                        if (ChkAccor2.isSelected()) {
                            titip_kamar = "Titip";
                        } else {
                            titip_kamar = "-";
                        }
                        Sequel.menyimpan("side_db.pindah_kamar",
                                "'" + NoRw.getText() + "','" + KdKamar.getText() + "','" + hargakmr2 + "','"
                                + Valid.SetTgl(DTPTgl.getSelectedItem() + "") + "','"
                                + Catatan.getText() + "','0000-00-00','00:00:00','0','1','"+titip_kamar+"','-'", "No.Rawat");

                        Sequel.menyimpan("side_db.detail_pindah_kamar",
                                "'" + NoRw.getText() + "','" + KdKamar.getText() + "','" + Valid.SetTgl(DTPTgl.getSelectedItem() + "") + "','" + Catatan.getText() + "','" + Valid.SetTgl(DTPTgl.getSelectedItem() + "") + "','" + hargakmr2 + "','"+titip_kamar+"','-'", "No.Rawat");
                        JOptionPane.showMessageDialog(rootPane, "Berhasil menginapkan pasien " + NmPasien.getText());
                        tampil();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnKamarInapActionPerformed

    private void BtnSuratPermintaanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSuratPermintaanActionPerformed
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        }else{
            if(tbObat.getSelectedRow()!= -1){
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                Map<String, Object> param = new HashMap<>();
                param.put("namars",akses.getnamars());
                param.put("alamatrs",akses.getalamatrs());
                param.put("kotars",akses.getkabupatenrs());
                param.put("propinsirs",akses.getpropinsirs());
                param.put("kontakrs",akses.getkontakrs());
                param.put("emailrs",akses.getemailrs());  
                finger=Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?",KdDokter.getText());
                param.put("finger","Dikeluarkan di "+akses.getnamars()+", Kabupaten/Kota "+akses.getkabupatenrs()+"\nDitandatangani secara elektronik oleh "+Dokter.getText()+"\nID "+(finger.equals("")?KdDokter.getText():finger)+"\n"+DTPTgl.getSelectedItem()); 
                param.put("logo",Sequel.cariGambar("select setting.logo from setting")); 
                Valid.MyReportqry("rptSuratPermintaanRawatInap.jasper","report","::[ Surat Permintaan Rawat Inap ]::",
                        " select permintaan_ranap.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.jk,reg_periksa.umurdaftar,reg_periksa.sttsumur,"+
                        "pasien.no_tlp,penjab.png_jawab,poliklinik.nm_poli,dokter.nm_dokter,permintaan_ranap.tanggal,permintaan_ranap.kd_kamar,kamar.kd_bangsal,"+
                        "bangsal.nm_bangsal,kamar.trf_kamar,permintaan_ranap.diagnosa,permintaan_ranap.catatan,reg_periksa.kd_dokter from permintaan_ranap "+
                        "inner join reg_periksa on permintaan_ranap.no_rawat=reg_periksa.no_rawat "+
                        "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                        "inner join penjab on reg_periksa.kd_pj=penjab.kd_pj "+
                        "inner join dokter on reg_periksa.kd_dokter=dokter.kd_dokter "+
                        "inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli "+
                        "inner join kamar on permintaan_ranap.kd_kamar=kamar.kd_kamar "+
                        "inner join bangsal on kamar.kd_bangsal=bangsal.kd_bangsal "+
                        "where reg_periksa.no_rawat='"+NoRw.getText()+"' ",param);
                this.setCursor(Cursor.getDefaultCursor());
            }else{
                JOptionPane.showMessageDialog(null,"Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnSuratPermintaanActionPerformed

    private void BtnRiwayatPasienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnRiwayatPasienActionPerformed
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        }else{
            if(tbObat.getSelectedRow()!= -1){
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                RMRiwayatPerawatan resume=new RMRiwayatPerawatan(null,true);
                resume.setNoRm(tbObat.getValueAt(tbObat.getSelectedRow(),1).toString(),tbObat.getValueAt(tbObat.getSelectedRow(),2).toString());
                resume.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
                resume.setLocationRelativeTo(internalFrame1);
                resume.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            }else{
                JOptionPane.showMessageDialog(null,"Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnRiwayatPasienActionPerformed

    private void BtnSuratPRIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSuratPRIActionPerformed
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        }else{
            if(tbObat.getSelectedRow()!= -1){
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    ps=koneksi.prepareStatement("select pasien.no_peserta,pasien.tgl_lahir,pasien.jk from pasien where pasien.no_rkm_medis=?");
                    try {
                        ps.setString(1,NoRM.getText());
                        rs=ps.executeQuery();
                        if(rs.next()){
                            if(rs.getString("no_peserta").length()<13){
                                JOptionPane.showMessageDialog(null,"Kartu BPJS Pasien tidak valid, silahkan hubungi bagian terkait..!!");
                            }else{
                                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                                BPJSSPRI form=new BPJSSPRI(null,false);
                                form.setNoRm(NoRw.getText(),rs.getString("no_peserta"),NoRM.getText(),NmPasien.getText(),rs.getString("tgl_lahir"),rs.getString("jk"),"-");
                                form.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
                                form.setLocationRelativeTo(internalFrame1);
                                form.setVisible(true);
                                this.setCursor(Cursor.getDefaultCursor());
                            }
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
                } catch (Exception e) {
                    System.out.println("Notif : "+e);
                }
                this.setCursor(Cursor.getDefaultCursor());
            }else{
                JOptionPane.showMessageDialog(null,"Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnSuratPRIActionPerformed

    
    private void BtnPersetujuanRanapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPersetujuanRanapActionPerformed
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        }else{
            if(tbObat.getSelectedRow()!= -1){
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                SuratPersetujuanRawatInap resume=new SuratPersetujuanRawatInap(null,false);
                resume.isCek();
                resume.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
                resume.setLocationRelativeTo(internalFrame1);
                resume.setVisible(true);
                resume.emptTeks();
                resume.setNoRm(NoRw.getText(),DTPCari2.getDate());
                resume.tampil();
                this.setCursor(Cursor.getDefaultCursor());
            }else{
                JOptionPane.showMessageDialog(null,"Maaf, silahkan pilih data...!!!!");
            }      
        }
    }//GEN-LAST:event_BtnPersetujuanRanapActionPerformed

    private void BtnSuratPRI1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSuratPRI1ActionPerformed
        String isGabung = "";
        if (NoRw.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu pasien...!!!");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("no_rawat", NoRw.getText());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            Valid.MyReport("rptBarcodeRawat4_2.jasper", param, "::[ Label Ibu ]::");
            this.setCursor(Cursor.getDefaultCursor());

            isGabung = Sequel.cariIsi("SELECT rg.no_rawat2 AS gabung FROM reg_periksa rp LEFT JOIN ranap_gabung rg ON rg.no_rawat = rp.no_rawat WHERE rp.no_rawat =?", NoRw.getText());
            if (isGabung != null) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                Map<String, Object> param2 = new HashMap<>();
                param2.put("namars", akses.getnamars());
                param2.put("alamatrs", akses.getalamatrs());
                param2.put("kotars", akses.getkabupatenrs());
                param2.put("propinsirs", akses.getpropinsirs());
                param2.put("kontakrs", akses.getkontakrs());
                param2.put("emailrs", akses.getemailrs());
                param2.put("no_rawat", isGabung);
                param2.put("logo", Sequel.cariGambar("select setting.logo from setting"));
                Valid.MyReport("rptBarcodeRawat4_3.jasper", param2, "::[ Label Bayi ]::");
                this.setCursor(Cursor.getDefaultCursor());
                System.out.println(isGabung);
            }

        }
    }//GEN-LAST:event_BtnSuratPRI1ActionPerformed

    private void BtnSuratPRI2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSuratPRI2ActionPerformed
        String isGabung = "", RmBayi = "";
        if (tabMode.getRowCount() == 0 || NoRw.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, data kamar inap pasien sudah habis...!!!!");
            //BtnIn.requestFocus();
        } else {
            if (1 == 1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                Map<String, Object> param = new HashMap<>();
                param.put("namars", akses.getnamars());
                param.put("alamatrs", akses.getalamatrs());
                param.put("kotars", akses.getkabupatenrs());
                param.put("propinsirs", akses.getpropinsirs());
                param.put("kontakrs", akses.getkontakrs());
                param.put("emailrs", akses.getemailrs());
                param.put("tanggal", DTPTgl.getDate().toString());
                param.put("kamar", KdKamar.getText() + " " + NmBangsal.getText());
                param.put("kamar2", NmBangsal.getText());
                param.put("kls_bpjs", Sequel.cariIsi("select bsep.klsrawat FROM bridging_sep bsep WHERE bsep.no_rawat =? ", NoRw.getText()));
                param.put("dpjp", Sequel.cariIsi("select dokter.nm_dokter from dpjp_ranap inner join dokter on dpjp_ranap.kd_dokter=dokter.kd_dokter where dpjp_ranap.no_rawat=? ", NoRw.getText()));
                param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
                Valid.MyReportqry("rptGelangPasienDewasa.jasper", "report", "::[ Gelang Pasien Ibu ]::", "select pasien.no_rkm_medis, pasien.nm_pasien, pasien.no_ktp, pasien.jk, "
                    + "pasien.tmp_lahir, pasien.tgl_lahir,pasien.nm_ibu, concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) as alamat, pasien.gol_darah, pasien.pekerjaan,"
                    + "pasien.stts_nikah,pasien.agama,pasien.tgl_daftar,pasien.no_tlp,pasien.umur,"
                    + "pasien.pnd, pasien.keluarga, pasien.namakeluarga,penjab.png_jawab,pasien.pekerjaanpj,"
                    + "concat(pasien.alamatpj,', ',pasien.kelurahanpj,', ',pasien.kecamatanpj,', ',pasien.kabupatenpj) as alamatpj from pasien "
                    + "inner join kelurahan inner join kecamatan inner join kabupaten "
                    + "inner join penjab on pasien.kd_pj=penjab.kd_pj and pasien.kd_kel=kelurahan.kd_kel "
                    + "and pasien.kd_kec=kecamatan.kd_kec and pasien.kd_kab=kabupaten.kd_kab  where pasien.no_rkm_medis='" + NoRM.getText() + "' ", param);
                this.setCursor(Cursor.getDefaultCursor());

                isGabung = Sequel.cariIsi("SELECT rg.no_rawat2 AS gabung FROM reg_periksa rp LEFT JOIN ranap_gabung rg ON rg.no_rawat = rp.no_rawat WHERE rp.no_rawat =?", NoRw.getText());
                if (isGabung != null) {
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    Map<String, Object> param2 = new HashMap<>();
                    param2.put("namars", akses.getnamars());
                    param2.put("alamatrs", akses.getalamatrs());
                    param2.put("kotars", akses.getkabupatenrs());
                    param2.put("propinsirs", akses.getpropinsirs());
                    param2.put("kontakrs", akses.getkontakrs());
                    param2.put("emailrs", akses.getemailrs());
                    param2.put("tanggal", DTPTgl.getDate().toString());
                    param2.put("kamar", KdKamar.getText() + " " + NmBangsal.getText());
                    param2.put("kamar2", NmBangsal.getText());
                    param2.put("kls_bpjs", Sequel.cariIsi("select bsep.klsrawat FROM bridging_sep bsep WHERE bsep.no_rawat =? ", isGabung));
                    param2.put("dpjp", Sequel.cariIsi("select dokter.nm_dokter from dpjp_ranap inner join dokter on dpjp_ranap.kd_dokter=dokter.kd_dokter where dpjp_ranap.no_rawat=? ", isGabung));
                    param2.put("logo", Sequel.cariGambar("select setting.logo from setting"));
                    RmBayi = Sequel.cariIsi("SELECT (SELECT rp.no_rkm_medis FROM reg_periksa rp WHERE rp.no_rawat = gr.no_rawat2) AS rm_bayi FROM ranap_gabung gr WHERE gr.no_rawat =?", NoRw.getText());
                    Valid.MyReportqry("rptGelangPasienDewasa2.jasper", "report", "::[ Gelang Pasien Bayi ]::", "select pasien.no_rkm_medis, pasien.nm_pasien, pasien.no_ktp, pasien.jk, "
                        + "pasien.tmp_lahir, pasien.tgl_lahir,pasien.nm_ibu, concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) as alamat, pasien.gol_darah, pasien.pekerjaan,"
                        + "pasien.stts_nikah,pasien.agama,pasien.tgl_daftar,pasien.no_tlp,pasien.umur,"
                        + "pasien.pnd, pasien.keluarga, pasien.namakeluarga,penjab.png_jawab,pasien.pekerjaanpj,"
                        + "concat(pasien.alamatpj,', ',pasien.kelurahanpj,', ',pasien.kecamatanpj,', ',pasien.kabupatenpj) as alamatpj from pasien "
                        + "inner join kelurahan inner join kecamatan inner join kabupaten "
                        + "inner join penjab on pasien.kd_pj=penjab.kd_pj and pasien.kd_kel=kelurahan.kd_kel "
                        + "and pasien.kd_kec=kecamatan.kd_kec and pasien.kd_kab=kabupaten.kd_kab  where pasien.no_rkm_medis='" + RmBayi + "' ", param2);
                    this.setCursor(Cursor.getDefaultCursor());
                }
            }
        }
    }//GEN-LAST:event_BtnSuratPRI2ActionPerformed

    private void BtnSuratPRI3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSuratPRI3ActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() > -1) {
                if (tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString().equals("")) {
                    try {
                        psanak = koneksi.prepareStatement(
                            "select pasien.no_rkm_medis,pasien.nm_pasien,ranap_gabung.no_rawat2,concat(reg_periksa.umurdaftar,' ',reg_periksa.sttsumur)as umur,pasien.no_peserta, "
                            + "concat(pasien.alamatpj,', ',pasien.kelurahanpj,', ',pasien.kecamatanpj,', ',pasien.kabupatenpj) as alamat "
                            + "from reg_periksa inner join pasien inner join ranap_gabung on "
                            + "pasien.no_rkm_medis=reg_periksa.no_rkm_medis and ranap_gabung.no_rawat2=reg_periksa.no_rawat where ranap_gabung.no_rawat=?");
                        try {
                            psanak.setString(1, tbObat.getValueAt(tbObat.getSelectedRow() - 1, 0).toString());
                            rs2 = psanak.executeQuery();
                            if (rs2.next()) {
                                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                                akses.setform("DlgKamarInap");
                                BPJSDataSEP dlgki = new BPJSDataSEP(null, false);
                                dlgki.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                                dlgki.setLocationRelativeTo(internalFrame1);
                                dlgki.isCek();
                                dlgki.setNoRm(rs2.getString("no_rawat2"), Valid.SetTgl2(tbObat.getValueAt(tbObat.getSelectedRow(), 11).toString()), "1. Ranap", "", "");
                                dlgki.setVisible(true);
                                this.setCursor(Cursor.getDefaultCursor());
                            } else {
                                JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu pasien...!!!");
                                tbObat.requestFocus();
                            }
                        } catch (Exception ex) {
                            System.out.println("Notifikasi : " + ex);
                        } finally {
                            if (rs2 != null) {
                                rs2.close();
                            }
                            if (psanak != null) {
                                psanak.close();
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                } else {
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    akses.setform("DlgKamarInap");
                    BPJSDataSEP dlgki = new BPJSDataSEP(null, false);
                    dlgki.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                    dlgki.setLocationRelativeTo(internalFrame1);
                    dlgki.isCek();
                    dlgki.setNoRm(NoRw.getText(), Valid.SetTgl2(tbObat.getValueAt(tbObat.getSelectedRow(), 11).toString()), "1. Ranap", "", "");
                    dlgki.setVisible(true);
                    this.setCursor(Cursor.getDefaultCursor());
                }
            }
        }
    }//GEN-LAST:event_BtnSuratPRI3ActionPerformed

    private void KdDokter1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdDokter1KeyPressed

    }//GEN-LAST:event_KdDokter1KeyPressed

    private void BtnDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDokterActionPerformed
        akses.setform("DlgIGD");
        dokter.isCek();
        dokter.TCari.requestFocus();
        dokter.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setVisible(true);
    }//GEN-LAST:event_BtnDokterActionPerformed

    private void ChkAccor1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkAccor1ActionPerformed
        isMenu2();
    }//GEN-LAST:event_ChkAccor1ActionPerformed

    private void NoRawatGabungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NoRawatGabungActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NoRawatGabungActionPerformed

    private void NoRawatGabungKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoRawatGabungKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_NoRawatGabungKeyPressed

    private void TNamaBayiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNamaBayiKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampil();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            BtnSeek4.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            DTPCari2.requestFocus();
        }
    }//GEN-LAST:event_TNamaBayiKeyPressed

    private void BtnSeek4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSeek4ActionPerformed
        pasien.emptTeks();
        pasien.isCek();
        pasien.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        pasien.setLocationRelativeTo(internalFrame1);
        pasien.setVisible(true);
    }//GEN-LAST:event_BtnSeek4ActionPerformed

    private void BtnSeek4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSeek4KeyPressed
        Valid.pindah(evt, TNamaBayi, DTPCari1);
    }//GEN-LAST:event_BtnSeek4KeyPressed

    private void TRMBayiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TRMBayiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TRMBayiKeyPressed

    private void KdDokterBayiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdDokterBayiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_KdDokterBayiKeyPressed

    private void BtnDokterBayiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDokterBayiActionPerformed
        akses.setform("DlgIGD");
        dokter2.isCek();
        dokter2.TCari.requestFocus();
        dokter2.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dokter2.setLocationRelativeTo(internalFrame1);
        dokter2.setVisible(true);
    }//GEN-LAST:event_BtnDokterBayiActionPerformed

    private void ChkAccor2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkAccor2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ChkAccor2ActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgPermintaanRanap dialog = new DlgPermintaanRanap(new javax.swing.JFrame(), true);
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
    private widget.Button BtnDokter;
    private widget.Button BtnDokterBayi;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKamarInap;
    private widget.Button BtnKeluar;
    private widget.Button BtnPersetujuanRanap;
    private widget.Button BtnPrint;
    private widget.Button BtnRiwayatPasien;
    private widget.Button BtnSeek4;
    private widget.Button BtnSimpan;
    private widget.Button BtnSuratPRI;
    private widget.Button BtnSuratPRI1;
    private widget.Button BtnSuratPRI2;
    private widget.Button BtnSuratPRI3;
    private widget.Button BtnSuratPermintaan;
    private widget.TextBox CaraBayar;
    private widget.TextBox Catatan;
    private widget.CekBox ChkAccor;
    private widget.CekBox ChkAccor1;
    private widget.CekBox ChkAccor2;
    private widget.CekBox ChkInput;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.Tanggal DTPTgl;
    private widget.TextBox Diagnosa;
    private widget.TextBox Dokter;
    private widget.PanelBiasa FormInput;
    private widget.PanelBiasa FormMenu;
    private widget.TextBox HargaKamar;
    private widget.TextBox KdBangsal;
    private widget.TextBox KdDokter;
    private widget.TextBox KdDokter1;
    private widget.TextBox KdDokterBayi;
    private widget.TextBox KdKamar;
    private widget.Label LCount;
    private widget.TextBox NmBangsal;
    private widget.TextBox NmPasien;
    private widget.TextBox NoRM;
    private widget.TextBox NoRawatGabung;
    private widget.TextBox NoRw;
    private widget.TextBox NoTelp;
    private widget.PanelBiasa PanelAccor;
    private javax.swing.JPanel PanelInput;
    private widget.TextBox Poli;
    private widget.RadioButton R1;
    private widget.RadioButton R2;
    private widget.ScrollPane Scroll;
    private widget.ScrollPane ScrollMenu;
    private widget.TextBox TCari;
    private widget.TextBox TDokter;
    private widget.TextBox TDokterBayi;
    private widget.TextBox TNamaBayi;
    private widget.TextBox TRMBayi;
    private widget.Button btnDiagnosa;
    private widget.Button btnKamar;
    private javax.swing.ButtonGroup buttonGroup1;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel12;
    private widget.Label jLabel13;
    private widget.Label jLabel14;
    private widget.Label jLabel15;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private widget.Label jLabel20;
    private widget.Label jLabel25;
    private widget.Label jLabel48;
    private widget.Label jLabel5;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.Label jLabel9;
    private javax.swing.JPanel jPanel3;
    private widget.panelisi panelCari;
    private widget.panelisi panelGlass10;
    private widget.panelisi panelGlass8;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

    public void tampil() {     
        Valid.tabelKosong(tabMode);
        try{ 
            if(R1.isSelected()==true){
                ps=koneksi.prepareStatement("select permintaan_ranap.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.jk,reg_periksa.umurdaftar,reg_periksa.sttsumur,"+
                    "pasien.no_tlp,penjab.png_jawab,poliklinik.nm_poli,dokter.nm_dokter,permintaan_ranap.tanggal,permintaan_ranap.kd_kamar,kamar.kd_bangsal,"+
                    "bangsal.nm_bangsal,kamar.trf_kamar,permintaan_ranap.diagnosa,permintaan_ranap.catatan,reg_periksa.kd_dokter, drpj.kd_dokter AS dp1, drpj.nm_dokter AS dp2, "+
                    " COALESCE((SELECT ranap_gabung.no_rawat2 FROM ranap_gabung WHERE ranap_gabung.no_rawat=reg_periksa.no_rawat),\"-\") AS gabung from permintaan_ranap "+
                    "inner join reg_periksa on permintaan_ranap.no_rawat=reg_periksa.no_rawat "+
                    "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                    "inner join penjab on reg_periksa.kd_pj=penjab.kd_pj "+
                    "inner join dokter on reg_periksa.kd_dokter=dokter.kd_dokter "+
                    "inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli "+
                    "inner join kamar on permintaan_ranap.kd_kamar=kamar.kd_kamar "+
                    "inner join bangsal on kamar.kd_bangsal=bangsal.kd_bangsal INNER JOIN dpjp_ranap ON dpjp_ranap.no_rawat = permintaan_ranap.no_rawat INNER JOIN dokter drpj  ON drpj.kd_dokter = dpjp_ranap.kd_dokter  "+
                    "where permintaan_ranap.no_rawat not in (select DISTINCT no_rawat from kamar_inap) "+
                    (TCari.getText().equals("")?"":"and (permintaan_ranap.no_rawat like ? or reg_periksa.no_rkm_medis like ? or pasien.nm_pasien like ? "+
                    "or penjab.png_jawab like ? or poliklinik.nm_poli like ? or dokter.nm_dokter like ? or bangsal.nm_bangsal like ? "+
                    "or permintaan_ranap.diagnosa like ?)")+" order by permintaan_ranap.tanggal");
                try {
                    if(!TCari.getText().equals("")){
                        ps.setString(1,"%"+TCari.getText().trim()+"%");
                        ps.setString(2,"%"+TCari.getText().trim()+"%");
                        ps.setString(3,"%"+TCari.getText().trim()+"%");
                        ps.setString(4,"%"+TCari.getText().trim()+"%");
                        ps.setString(5,"%"+TCari.getText().trim()+"%");
                        ps.setString(6,"%"+TCari.getText().trim()+"%");
                        ps.setString(7,"%"+TCari.getText().trim()+"%");
                        ps.setString(8,"%"+TCari.getText().trim()+"%");
                    }
                    rs=ps.executeQuery();
                    while(rs.next()){
                        tabMode.addRow(new String[]{
                            rs.getString("no_rawat"),rs.getString("no_rkm_medis"),rs.getString("nm_pasien"),rs.getString("jk"),rs.getString("umurdaftar")+" "+rs.getString("sttsumur"),
                            rs.getString("no_tlp"),rs.getString("png_jawab"),rs.getString("nm_poli"),rs.getString("nm_dokter"),rs.getString("tanggal"),rs.getString("kd_kamar"),
                            rs.getString("kd_bangsal"),rs.getString("kd_kamar")+" "+rs.getString("nm_bangsal"),Valid.SetAngka(rs.getDouble("trf_kamar")),rs.getString("diagnosa"),rs.getString("catatan"),
                            rs.getString("kd_dokter"), rs.getString("dp1"), rs.getString("dp2"), rs.getString("gabung")
                        });
                        psanak = koneksi.prepareStatement(
                                "select pasien.no_rkm_medis,pasien.nm_pasien,ranap_gabung.no_rawat2,concat(reg_periksa.umurdaftar,' ',reg_periksa.sttsumur)as umur,pasien.no_peserta, "
                                + "concat(pasien.alamatpj,', ',pasien.kelurahanpj,', ',pasien.kecamatanpj,', ',pasien.kabupatenpj) as alamat, dpjp_ranap.kd_dokter, dokter.nm_dokter "
                                + "from reg_periksa inner join pasien on pasien.no_rkm_medis=reg_periksa.no_rkm_medis "
                                + "inner join ranap_gabung on ranap_gabung.no_rawat2=reg_periksa.no_rawat "
                                + "INNER JOIN dpjp_ranap ON dpjp_ranap.no_rawat = ranap_gabung.no_rawat2 "
                                + "INNER JOIN dokter ON dokter.kd_dokter = dpjp_ranap.kd_dokter "
                                + "where ranap_gabung.no_rawat=?");
                        try {
                            psanak.setString(1, rs.getString("no_rawat"));
                            rs2 = psanak.executeQuery();
                            if (rs2.next()) {
                                tabMode.addRow(new String[]{
                                    "", rs2.getString("no_rkm_medis"), rs2.getString("nm_pasien"), rs.getString("jk"), rs2.getString("umur"),
                                    "", rs.getString("png_jawab"), "", "", rs.getString("tanggal"), rs.getString("kd_kamar"),
                                    rs.getString("kd_bangsal"), rs.getString("kd_kamar") + " " + rs.getString("nm_bangsal"), Valid.SetAngka(rs.getDouble("trf_kamar")), "", "",
                                    "", rs2.getString("kd_dokter"), rs2.getString("nm_dokter"), "Bayi"
                                });
                            }
                        } catch (Exception ex) {
                            System.out.println("Notifikasi : " + ex);
                        } finally {
                            if (rs2 != null) {
                                rs2.close();
                            }
                            if (psanak != null) {
                                psanak.close();
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Notif Kamar : "+e);
                } finally{
                    if(rs!=null){
                        rs.close();
                    }
                    if(ps!=null){
                        ps.close();
                    }
                }
            }else if(R2.isSelected()==true){
                ps=koneksi.prepareStatement("select permintaan_ranap.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.jk,reg_periksa.umurdaftar,reg_periksa.sttsumur,"+
                    "pasien.no_tlp,penjab.png_jawab,poliklinik.nm_poli,dokter.nm_dokter,permintaan_ranap.tanggal,permintaan_ranap.kd_kamar,kamar.kd_bangsal,"+
                    "bangsal.nm_bangsal,kamar.trf_kamar,permintaan_ranap.diagnosa,permintaan_ranap.catatan,reg_periksa.kd_dokter, drpj.kd_dokter AS dp1, drpj.nm_dokter AS dp2, "+ 
                    "COALESCE((SELECT ranap_gabung.no_rawat2 FROM ranap_gabung WHERE ranap_gabung.no_rawat=reg_periksa.no_rawat),\"-\") AS gabung from permintaan_ranap "+
                    "inner join reg_periksa on permintaan_ranap.no_rawat=reg_periksa.no_rawat "+
                    "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                    "inner join penjab on reg_periksa.kd_pj=penjab.kd_pj "+
                    "inner join dokter on reg_periksa.kd_dokter=dokter.kd_dokter "+
                    "inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli "+
                    "inner join kamar on permintaan_ranap.kd_kamar=kamar.kd_kamar "+
                    "inner join bangsal on kamar.kd_bangsal=bangsal.kd_bangsal INNER JOIN dpjp_ranap ON dpjp_ranap.no_rawat = permintaan_ranap.no_rawat INNER JOIN dokter drpj  ON drpj.kd_dokter = dpjp_ranap.kd_dokter  "+
                    "where permintaan_ranap.no_rawat in (select DISTINCT no_rawat from kamar_inap) and permintaan_ranap.tanggal between ? and ? "+
                    (TCari.getText().equals("")?"":"and (permintaan_ranap.no_rawat like ? or reg_periksa.no_rkm_medis like ? or pasien.nm_pasien like ? "+
                    "or penjab.png_jawab like ? or poliklinik.nm_poli like ? or dokter.nm_dokter like ? or bangsal.nm_bangsal like ? "+
                    "or permintaan_ranap.diagnosa like ?)")+" order by permintaan_ranap.tanggal");
                try {
                    ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+""));
                    ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+""));
                    if(!TCari.getText().equals("")){
                        ps.setString(3,"%"+TCari.getText().trim()+"%");
                        ps.setString(4,"%"+TCari.getText().trim()+"%");
                        ps.setString(5,"%"+TCari.getText().trim()+"%");
                        ps.setString(6,"%"+TCari.getText().trim()+"%");
                        ps.setString(7,"%"+TCari.getText().trim()+"%");
                        ps.setString(8,"%"+TCari.getText().trim()+"%");
                        ps.setString(9,"%"+TCari.getText().trim()+"%");
                        ps.setString(10,"%"+TCari.getText().trim()+"%");
                    }
                    rs=ps.executeQuery();
                    while(rs.next()){
                        tabMode.addRow(new String[]{
                            rs.getString("no_rawat"),rs.getString("no_rkm_medis"),rs.getString("nm_pasien"),rs.getString("jk"),rs.getString("umurdaftar")+" "+rs.getString("sttsumur"),
                            rs.getString("no_tlp"),rs.getString("png_jawab"),rs.getString("nm_poli"),rs.getString("nm_dokter"),rs.getString("tanggal"),rs.getString("kd_kamar"),
                            rs.getString("kd_bangsal"),rs.getString("kd_kamar")+" "+rs.getString("nm_bangsal"),Valid.SetAngka(rs.getDouble("trf_kamar")),rs.getString("diagnosa"),
                            rs.getString("catatan"),rs.getString("kd_dokter"), rs.getString("dp1"), rs.getString("dp2"), rs.getString("gabung")
                        });
                    }
                } catch (Exception e) {
                    System.out.println("Notif Kamar : "+e);
                } finally{
                    if(rs!=null){
                        rs.close();
                    }
                    if(ps!=null){
                        ps.close();
                    }
                }
            }
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }
        LCount.setText(""+tabMode.getRowCount());
    }

    public void emptTeks() {
        NoRw.setText("");
        NoRM.setText("");
        NmPasien.setText("");
        NoTelp.setText("");
        CaraBayar.setText("");
        Poli.setText("");
        Dokter.setText("");
        KdKamar.setText("");
        KdDokter.setText("");
        KdBangsal.setText("");
        NmBangsal.setText("");
        HargaKamar.setText("");
        Diagnosa.setText("");
        Catatan.setText("");
        KdDokter1.setText("");
        TDokter.setText("");
        DTPTgl.setDate(new Date());
        Diagnosa.requestFocus();
        date = new Date();
        now = dateFormat.format(date);
        Catatan.setText(now.substring(11, 13) + now.substring(14, 16) + now.substring(17, 19));

        KdDokterBayi.setText("");
        TDokterBayi.setText("");
        BtnDokterBayi.setText("");
        TRMBayi.setText("");
        TNamaBayi.setText("");
        BtnSeek4.setText("");
        NoRawatGabung.setText("");
    }

    private void getData() {
        if(tbObat.getSelectedRow()!= -1){
            String[] splitcatatan = tbObat.getValueAt(tbObat.getSelectedRow(), 15).toString().split("#");            
            if (splitcatatan.length == 2) {
                Catatan.setText(splitcatatan[0]);
                if (splitcatatan[1].equals("Titip")) {
                    ChkAccor2.setSelected(true);
                } else if (splitcatatan[1].equals("-")) {
                    ChkAccor2.setSelected(false);
                }
            } else if (splitcatatan.length == 1) {
                Catatan.setText(splitcatatan[0]);
                ChkAccor2.setSelected(false);
            }
            NoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(),0).toString()); 
            NoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(),1).toString()); 
            NmPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(),2).toString());
            NoTelp.setText(tbObat.getValueAt(tbObat.getSelectedRow(),5).toString());
            CaraBayar.setText(tbObat.getValueAt(tbObat.getSelectedRow(),6).toString());
            Poli.setText(tbObat.getValueAt(tbObat.getSelectedRow(),7).toString());
            Dokter.setText(tbObat.getValueAt(tbObat.getSelectedRow(),8).toString());
            KdKamar.setText(tbObat.getValueAt(tbObat.getSelectedRow(),10).toString());
            KdBangsal.setText(tbObat.getValueAt(tbObat.getSelectedRow(),11).toString());
            NmBangsal.setText(tbObat.getValueAt(tbObat.getSelectedRow(),12).toString());
            HargaKamar.setText(tbObat.getValueAt(tbObat.getSelectedRow(),13).toString());
            Diagnosa.setText(tbObat.getValueAt(tbObat.getSelectedRow(),14).toString());
            KdDokter1.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 17).toString());
            TDokter.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 18).toString());
            KdDokter.setText(tbObat.getValueAt(tbObat.getSelectedRow(),16).toString());
            Valid.SetTgl(DTPTgl,tbObat.getValueAt(tbObat.getSelectedRow(),9).toString());
            
            if (!tbObat.getValueAt(tbObat.getSelectedRow(), 19).toString().equals("-")) {
                String norwbayi = tbObat.getValueAt(tbObat.getSelectedRow(), 19).toString();
                NoRawatGabung.setText(norwbayi);
                TRMBayi.setText(Sequel.cariIsi("SELECT rp.no_rkm_medis FROM reg_periksa rp WHERE rp.no_rawat=?", norwbayi));
                TNamaBayi.setText(Sequel.cariIsi("SELECT (SELECT ps.nm_pasien FROM pasien ps WHERE ps.no_rkm_medis = rp.no_rkm_medis) FROM reg_periksa rp WHERE rp.no_rawat=?", norwbayi));
                KdDokterBayi.setText(Sequel.cariIsi("SELECT dpjp.kd_dokter FROM dpjp_ranap dpjp WHERE dpjp.no_rawat=?", norwbayi));
                TDokterBayi.setText(Sequel.cariIsi("SELECT (SELECT dr.nm_dokter FROM dokter dr WHERE dr.kd_dokter = dpjp.kd_dokter) FROM dpjp_ranap dpjp WHERE dpjp.no_rawat =?", norwbayi));
                ChkAccor1.setSelected(true);
                jLabel16.setVisible(true);
                KdDokterBayi.setVisible(true);
                TDokterBayi.setVisible(true);
                BtnDokterBayi.setVisible(true);

                jLabel48.setVisible(true);
                TRMBayi.setVisible(true);
                TNamaBayi.setVisible(true);
                BtnSeek4.setVisible(true);
                NoRawatGabung.setVisible(true);
            } else {
                ChkAccor1.setSelected(false);
                jLabel16.setVisible(false);
                KdDokterBayi.setVisible(false);
                TDokterBayi.setVisible(false);
                BtnDokterBayi.setVisible(false);

                jLabel48.setVisible(false);
                TRMBayi.setVisible(false);
                TNamaBayi.setVisible(false);
                BtnSeek4.setVisible(false);
                NoRawatGabung.setVisible(false);

                NoRawatGabung.setText("");
                TRMBayi.setText("");
                TNamaBayi.setText("");
                KdDokterBayi.setText("");
                TDokterBayi.setText("");
            }
        }
    }
    
    public void setNoRm(String norwt,String norm,String nama,String namadokter,String carabayar,String poli,String notelp) {
        NoRw.setText(norwt);
        NoRM.setText(norm);
        NmPasien.setText(nama);
        Dokter.setText(namadokter);
        CaraBayar.setText(carabayar);
        Poli.setText(poli);
        NoTelp.setText(notelp);
        TCari.setText(norwt);
        ChkInput.setSelected(true);
        Catatan.setText(now.substring(11, 13) + ":" + now.substring(14, 16) + ":" + now.substring(17, 19));
        aktif=false;
        isForm();
    }
    
    private void isForm(){
        if(ChkInput.isSelected()==true){
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH,186));
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
        BtnSimpan.setEnabled(akses.getpermintaan_ranap());
        BtnHapus.setEnabled(akses.getpermintaan_ranap());
        BtnPrint.setEnabled(akses.getpermintaan_ranap());
        BtnKamarInap.setEnabled(akses.getkamar_inap());
        BtnRiwayatPasien.setEnabled(akses.getresume_pasien());
        BtnSuratPRI.setEnabled(akses.getbpjs_surat_pri());
        BtnEdit.setEnabled(akses.getpermintaan_ranap());   
        BtnPersetujuanRanap.setEnabled(akses.getsurat_persetujuan_rawat_inap());
    }

    private void jam(){
        ActionListener taskPerformer = (ActionEvent e) -> {
            if(aktif==true){
                nol_detik = "";
                Date now = Calendar.getInstance().getTime();
                nilai_detik = now.getSeconds();
                if (nilai_detik <= 9) {
                    nol_detik = "0";
                }

                detik = nol_detik + Integer.toString(nilai_detik);
                if(detik.equals("05")){
                    bookingbaru=Sequel.cariInteger("select count(*) from permintaan_ranap where no_rawat not in (select DISTINCT no_rawat from kamar_inap) ");
                    if(bookingbaru>0){
                        try {
                            music = new BackgroundMusic("./suara/alarm.mp3");
                            music.start();
                        } catch (Exception ex) {
                            System.out.println(ex);
                        }
                        
                        i=JOptionPane.showConfirmDialog(null, "Ada permintaan rawat inap baru, apa mau ditampilkan????","Konfirmasi",JOptionPane.YES_NO_OPTION);
                        if(i==JOptionPane.YES_OPTION){
                            R1.setSelected(true);
                            TCari.setText("");
                            tampil();
                        }
                    }
                }
            }                
        };
        new Timer(1000, taskPerformer).start();
    }
    
    private void isMenu(){
        if(ChkAccor.isSelected()==true){
            ChkAccor.setVisible(false);
            PanelAccor.setPreferredSize(new Dimension(175,HEIGHT));
            FormMenu.setVisible(true); 
            ChkAccor.setVisible(true);
        }else if(ChkAccor.isSelected()==false){  
            ChkAccor.setVisible(false);
            PanelAccor.setPreferredSize(new Dimension(15,HEIGHT));
            FormMenu.setVisible(false);    
            ChkAccor.setVisible(true);
        }
    }
    
        private void isMenu2() {
        if (ChkAccor1.isSelected() == true) {
            jLabel16.setVisible(true);
            KdDokterBayi.setVisible(true);
            TDokterBayi.setVisible(true);
            BtnDokterBayi.setVisible(true);

            jLabel48.setVisible(true);
            TRMBayi.setVisible(true);
            TNamaBayi.setVisible(true);
            BtnSeek4.setVisible(true);
            NoRawatGabung.setVisible(true);
        } else if (ChkAccor1.isSelected() == false) {
            jLabel16.setVisible(false);
            KdDokterBayi.setVisible(false);
            TDokterBayi.setVisible(false);
            BtnDokterBayi.setVisible(false);
            KdDokterBayi.setText("");
            TDokterBayi.setText("");
            BtnDokterBayi.setText("");

            jLabel48.setVisible(false);
            TRMBayi.setVisible(false);
            TNamaBayi.setVisible(false);
            BtnSeek4.setVisible(false);
            NoRawatGabung.setVisible(false);
            TRMBayi.setText("");
            TNamaBayi.setText("");
            BtnSeek4.setText("");
            NoRawatGabung.setText("");
        }
    }
    
}
