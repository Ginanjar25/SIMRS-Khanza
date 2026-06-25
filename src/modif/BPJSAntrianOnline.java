/*
  Dilarang keras menggandakan/mengcopy/menyebarkan/membajak/mendecompile 
  Software ini dalam bentuk apapun tanpa seijin pembuat software
  (Khanza.Soft Media). Bagi yang sengaja membajak softaware ini ta
  npa ijin, kami sumpahi sial 1000 turunan, miskin sampai 500 turu
  nan. Selalu mendapat kecelakaan sampai 400 turunan. Anak pertama
  nya cacat tidak punya kaki sampai 300 turunan. Susah cari jodoh
  sampai umur 50 tahun sampai 200 turunan. Ya Alloh maafkan kami 
  karena telah berdoa buruk, semua ini kami lakukan karena kami ti
  dak pernah rela karya kami dibajak tanpa ijin.
 */

package modif;

import bridging.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileInputStream;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;


/**
 *
 * @author perpustakaan
 */
public final class BPJSAntrianOnline extends javax.swing.JDialog {
    private Connection koneksi=koneksiDB.condb();
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i=0,pilihan=1,reply=0,tab=0,kuota=0, interval = 0;
    private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd");
    
    private BPJSSuratKontrol skdp=new BPJSSuratKontrol(null,false);    
    private BPJSCekRiwayatRujukanTerakhir rujukanterakhir=new BPJSCekRiwayatRujukanTerakhir(null,false);
    
    private String prb="",no_peserta="",link="",ADDANTRIANAPIMOBILEJKN="no",requestJson,URL="",query="",utc="",user="",kddokter="",tglkkl="0000-00-00",penunjang="",kodedokterreg="",kodepolireg="",
            jammulai="",jamselesai="",datajam="",jeniskunjungan="",hari="",nomorreg="",respon="200", status="";
    private HttpHeaders headers;
    private HttpEntity requestEntity;
    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode root;
    private JsonNode nameNode;
    private JsonNode response;
    private boolean statusantrean=true;
    private BPJSCekHistoriPelayanan historiPelayanan=new BPJSCekHistoriPelayanan(null,false);
    private Calendar cal = Calendar.getInstance();
    private int day = cal.get(Calendar.DAY_OF_WEEK);
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Date parsedDate;    
    private static final Properties prop = new Properties(); 
    
    /** Creates new form DlgRujuk
     * @param parent
     * @param modal */
    public BPJSAntrianOnline(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8,1);

        NoRujukan.setDocument(new batasInput((byte)40).getKata(NoRujukan));
        NoSKDP.setDocument(new batasInput((byte)40).getKata(NoSKDP));
        btnSKDP.setEnabled(false);
        NoSKDP.setEnabled(false);
        btnSKDP.setVisible(false);
        NoSKDP.setVisible(false);
        jLabel39.setVisible(false);
        
        rujukanterakhir.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(rujukanterakhir.getTable().getSelectedRow()!= -1){  
                    NoRujukan.setText(rujukanterakhir.getTable().getValueAt(rujukanterakhir.getTable().getSelectedRow(),2).toString());
                    KdPoli.setText(rujukanterakhir.getTable().getValueAt(rujukanterakhir.getTable().getSelectedRow(),3).toString());
                    NmPoli.setText(rujukanterakhir.getTable().getValueAt(rujukanterakhir.getTable().getSelectedRow(),4).toString());
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
        
        rujukanterakhir.getTable().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_SPACE){
                    rujukanterakhir.dispose();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        }); 
               
        
        skdp.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(skdp.getTable().getSelectedRow()!= -1){                   
                    NoSKDP.setText(skdp.getTable().getValueAt(skdp.getTable().getSelectedRow(),9).toString());
                    String kdDpjpLayanan = Sequel.cariIsi("SELECT if(b.kddpjplayanan='',b.kddpjp,b.kddpjplayanan) AS kddr FROM bridging_surat_kontrol_bpjs a JOIN bridging_sep b ON b.no_sep = a.no_sep WHERE a.no_surat =?",skdp.getTable().getValueAt(skdp.getTable().getSelectedRow(),9).toString());
                    String nmDpjpLayanan = Sequel.cariIsi("SELECT if(b.nmdpjplayanan='',b.nmdpdjp,b.nmdpjplayanan) AS namadr FROM bridging_surat_kontrol_bpjs a JOIN bridging_sep b ON b.no_sep = a.no_sep WHERE a.no_surat =?",skdp.getTable().getValueAt(skdp.getTable().getSelectedRow(),9).toString());                                            
                    if(Sequel.cariInteger("SELECT COUNT(dp.kd_dokter) FROM maping_dokter_dpjpvclaim dp WHERE dp.kd_dokter_bpjs = ?",kdDpjpLayanan)>0){
                        KdDPJP.setText(kdDpjpLayanan);
                        NmDPJP.setText(nmDpjpLayanan);
                    }                    
                    NoSKDP.requestFocus();
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
        
        skdp.getTable().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_SPACE){
                    skdp.dispose();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });
               
        historiPelayanan.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(historiPelayanan.getTable().getSelectedRow()!= -1){         
                    if((historiPelayanan.getTable().getSelectedColumn()==6)||(historiPelayanan.getTable().getSelectedColumn()==7)){
                        NoRujukan.setText(historiPelayanan.getTable().getValueAt(historiPelayanan.getTable().getSelectedRow(),historiPelayanan.getTable().getSelectedColumn()).toString());
                    }
                }  
                NoRujukan.requestFocus();
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
        
        historiPelayanan.getTable().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_SPACE){
                    historiPelayanan.dispose();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        
        
        try {
            user=akses.getkode().replace(" ","").substring(0,9);
        } catch (Exception e) {
            user=akses.getkode();
        }
        
        try {
            link=koneksiDB.URLAPIBPJS();
        } catch (Exception e) {
            System.out.println("E : "+e);
        }
        
        try {
            ADDANTRIANAPIMOBILEJKN=koneksiDB.ADDANTRIANAPIMOBILEJKN();
        } catch (Exception e) {
            ADDANTRIANAPIMOBILEJKN="";
            System.out.println("Notif : "+e);
        }
        
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            interval = Integer.parseInt(prop.getProperty("INTERVALESTIMASIDEFAULT"));
        } catch (NumberFormatException e) {
            // Jika terjadi kesalahan konversi, gunakan nilai default
            interval = 360;
        } catch (Exception e) {
            // Jika terjadi kesalahan saat membaca file atau properti
            interval = 360;
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

        internalFrame1 = new widget.InternalFrame();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnKeluar = new widget.Button();
        internalFrame2 = new widget.InternalFrame();
        Scroll1 = new widget.ScrollPane();
        FormInput = new widget.PanelBiasa();
        NIK = new widget.TextBox();
        jLabel4 = new widget.Label();
        TNoRw = new widget.TextBox();
        TPasien = new widget.TextBox();
        TNoRM = new widget.TextBox();
        jLabel5 = new widget.Label();
        NoKartu = new widget.TextBox();
        jLabel20 = new widget.Label();
        TanggalSEP = new widget.Tanggal();
        jLabel23 = new widget.Label();
        NoRujukan = new widget.TextBox();
        NmPoli = new widget.TextBox();
        KdPoli = new widget.TextBox();
        LabelPoli = new widget.Label();
        jLabel39 = new widget.Label();
        NoSKDP = new widget.TextBox();
        LabelPoli2 = new widget.Label();
        KdDPJP = new widget.TextBox();
        NmDPJP = new widget.TextBox();
        btnRiwayat = new widget.Button();
        btnSKDP = new widget.Button();
        jLabel42 = new widget.Label();
        TujuanKunjungan = new widget.ComboBox();
        btnRiwayatRujukan = new widget.Button();
        NoTelp = new widget.TextBox();
        jLabel29 = new widget.Label();
        jLabel30 = new widget.Label();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Kirim Antrean Onlinel BPJS ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 54));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 9));

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

        internalFrame1.add(panelGlass8, java.awt.BorderLayout.PAGE_END);

        internalFrame2.setBorder(null);
        internalFrame2.setName("internalFrame2"); // NOI18N
        internalFrame2.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        Scroll1.setName("Scroll1"); // NOI18N
        Scroll1.setOpaque(true);

        FormInput.setBorder(null);
        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(745, 467));
        FormInput.setLayout(null);

        NIK.setEditable(false);
        NIK.setHighlighter(null);
        NIK.setName("NIK"); // NOI18N
        NIK.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NIKKeyPressed(evt);
            }
        });
        FormInput.add(NIK);
        NIK.setBounds(254, 70, 140, 24);

        jLabel4.setText("No.Rawat :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(0, 12, 90, 23);

        TNoRw.setEditable(false);
        TNoRw.setBackground(new java.awt.Color(245, 250, 240));
        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw"); // NOI18N
        FormInput.add(TNoRw);
        TNoRw.setBounds(100, 10, 150, 23);

        TPasien.setEditable(false);
        TPasien.setBackground(new java.awt.Color(245, 250, 240));
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N
        FormInput.add(TPasien);
        TPasien.setBounds(420, 10, 370, 23);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM"); // NOI18N
        FormInput.add(TNoRM);
        TNoRM.setBounds(260, 10, 140, 23);

        jLabel5.setText("No.Kartu :");
        jLabel5.setName("jLabel5"); // NOI18N
        FormInput.add(jLabel5);
        jLabel5.setBounds(0, 40, 90, 23);

        NoKartu.setEditable(false);
        NoKartu.setBackground(new java.awt.Color(245, 250, 240));
        NoKartu.setHighlighter(null);
        NoKartu.setName("NoKartu"); // NOI18N
        FormInput.add(NoKartu);
        NoKartu.setBounds(100, 40, 120, 23);

        jLabel20.setText("Tgl.SEP :");
        jLabel20.setName("jLabel20"); // NOI18N
        jLabel20.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel20);
        jLabel20.setBounds(240, 40, 55, 23);

        TanggalSEP.setForeground(new java.awt.Color(50, 70, 50));
        TanggalSEP.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10-06-2026" }));
        TanggalSEP.setDisplayFormat("dd-MM-yyyy");
        TanggalSEP.setName("TanggalSEP"); // NOI18N
        TanggalSEP.setOpaque(false);
        TanggalSEP.setPreferredSize(new java.awt.Dimension(95, 23));
        FormInput.add(TanggalSEP);
        TanggalSEP.setBounds(305, 40, 90, 23);

        jLabel23.setText("No.Rujukan :");
        jLabel23.setName("jLabel23"); // NOI18N
        jLabel23.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel23);
        jLabel23.setBounds(420, 70, 100, 23);

        NoRujukan.setHighlighter(null);
        NoRujukan.setName("NoRujukan"); // NOI18N
        NoRujukan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoRujukanKeyPressed(evt);
            }
        });
        FormInput.add(NoRujukan);
        NoRujukan.setBounds(530, 70, 190, 23);

        NmPoli.setEditable(false);
        NmPoli.setBackground(new java.awt.Color(245, 250, 240));
        NmPoli.setHighlighter(null);
        NmPoli.setName("NmPoli"); // NOI18N
        FormInput.add(NmPoli);
        NmPoli.setBounds(200, 100, 200, 23);

        KdPoli.setEditable(false);
        KdPoli.setBackground(new java.awt.Color(245, 250, 240));
        KdPoli.setHighlighter(null);
        KdPoli.setName("KdPoli"); // NOI18N
        FormInput.add(KdPoli);
        KdPoli.setBounds(110, 100, 80, 23);

        LabelPoli.setText("Poli Tujuan :");
        LabelPoli.setName("LabelPoli"); // NOI18N
        FormInput.add(LabelPoli);
        LabelPoli.setBounds(10, 100, 90, 23);

        jLabel39.setText("No.SKDP :");
        jLabel39.setName("jLabel39"); // NOI18N
        jLabel39.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel39);
        jLabel39.setBounds(420, 100, 100, 23);

        NoSKDP.setEditable(false);
        NoSKDP.setHighlighter(null);
        NoSKDP.setName("NoSKDP"); // NOI18N
        FormInput.add(NoSKDP);
        NoSKDP.setBounds(530, 100, 220, 23);

        LabelPoli2.setText("Dokter DPJP :");
        LabelPoli2.setName("LabelPoli2"); // NOI18N
        FormInput.add(LabelPoli2);
        LabelPoli2.setBounds(10, 130, 90, 23);

        KdDPJP.setEditable(false);
        KdDPJP.setBackground(new java.awt.Color(245, 250, 240));
        KdDPJP.setHighlighter(null);
        KdDPJP.setName("KdDPJP"); // NOI18N
        FormInput.add(KdDPJP);
        KdDPJP.setBounds(110, 130, 80, 23);

        NmDPJP.setEditable(false);
        NmDPJP.setBackground(new java.awt.Color(245, 250, 240));
        NmDPJP.setHighlighter(null);
        NmDPJP.setName("NmDPJP"); // NOI18N
        FormInput.add(NmDPJP);
        NmDPJP.setBounds(200, 130, 200, 23);

        btnRiwayat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnRiwayat.setMnemonic('X');
        btnRiwayat.setToolTipText("Alt+X");
        btnRiwayat.setName("btnRiwayat"); // NOI18N
        btnRiwayat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRiwayatActionPerformed(evt);
            }
        });
        FormInput.add(btnRiwayat);
        btnRiwayat.setBounds(730, 70, 28, 23);

        btnSKDP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnSKDP.setMnemonic('X');
        btnSKDP.setToolTipText("Alt+X");
        btnSKDP.setName("btnSKDP"); // NOI18N
        btnSKDP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSKDPActionPerformed(evt);
            }
        });
        FormInput.add(btnSKDP);
        btnSKDP.setBounds(760, 100, 28, 23);

        jLabel42.setText("Tujuan Kunjungan :");
        jLabel42.setName("jLabel42"); // NOI18N
        FormInput.add(jLabel42);
        jLabel42.setBounds(420, 40, 100, 23);

        TujuanKunjungan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Rujukan FKTP ", "Internal ", "Kontrol", "Post Ranap", "Rujukan Antar RS" }));
        TujuanKunjungan.setName("TujuanKunjungan"); // NOI18N
        TujuanKunjungan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                TujuanKunjunganItemStateChanged(evt);
            }
        });
        TujuanKunjungan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TujuanKunjunganKeyPressed(evt);
            }
        });
        FormInput.add(TujuanKunjungan);
        TujuanKunjungan.setBounds(530, 40, 250, 23);

        btnRiwayatRujukan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnRiwayatRujukan.setMnemonic('X');
        btnRiwayatRujukan.setToolTipText("Alt+X");
        btnRiwayatRujukan.setName("btnRiwayatRujukan"); // NOI18N
        btnRiwayatRujukan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRiwayatRujukanActionPerformed(evt);
            }
        });
        FormInput.add(btnRiwayatRujukan);
        btnRiwayatRujukan.setBounds(760, 70, 28, 23);

        NoTelp.setHighlighter(null);
        NoTelp.setMaxLenth(13);
        NoTelp.setName("NoTelp"); // NOI18N
        NoTelp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoTelpKeyPressed(evt);
            }
        });
        FormInput.add(NoTelp);
        NoTelp.setBounds(100, 70, 110, 23);

        jLabel29.setText("NIK :");
        jLabel29.setName("jLabel29"); // NOI18N
        jLabel29.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel29);
        jLabel29.setBounds(190, 70, 58, 23);

        jLabel30.setText("No.Telp :");
        jLabel30.setName("jLabel30"); // NOI18N
        jLabel30.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(jLabel30);
        jLabel30.setBounds(30, 70, 58, 23);

        Scroll1.setViewportView(FormInput);

        internalFrame2.add(Scroll1, java.awt.BorderLayout.CENTER);

        internalFrame1.add(internalFrame2, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (TNoRw.getText().trim().equals("")||TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "Pasien");
        }else if (NoKartu.getText().trim().equals("")) {
            Valid.textKosong(NoKartu, "Nomor Kartu");
        }else if (KdDPJP.getText().trim().equals("")||NmDPJP.getText().trim().equals("")) {
            Valid.textKosong(KdDPJP, "DPJP");
        }else if(Sequel.cariInteger("select count(no_sep) from bridging_sep where no_rawat = ?", TNoRw.getText()) > 0){
             JOptionPane.showMessageDialog(null, "No Rawat tersebut sudah diterbitkan SEP, silahkan cek kembali data !");
        }else{
            //Mulai pengecakan tgl SKDP sama dengan tgl SEP
            if (!NoSKDP.getText().equals("")) {
                if (Valid.SetTgl(TanggalSEP.getSelectedItem() + "").equals(Sequel.cariIsi("SELECT SUBSTRING(surkon.created_at, 1,10) AS tgl_surat FROM bridging_surat_kontrol_bpjs surkon WHERE surkon.no_surat =?", NoSKDP.getText()))) {
                    JOptionPane.showMessageDialog(null, "Tanggal terbit SKDP sama dengan Tanggal Rencana terbit SEP, Silahkan hubungi PIC BPJS");
                } else {
                    if (!NmPoli.getText().toLowerCase().contains("darurat")) {
                        if (Sequel.cariInteger("select count(bridging_sep.no_kartu) from bridging_sep where bridging_sep.no_kartu='" + no_peserta + "' and bridging_sep.jnspelayanan='2' and bridging_sep.tglsep like '%" + Valid.SetTgl(TanggalSEP.getSelectedItem() + "") + "%' and bridging_sep.nmpolitujuan='" + NmPoli.getText() + "'") >= 1) {
                            JOptionPane.showMessageDialog(null, "Maaf, sebelumnya sudah dilakukan pembuatan SEP di jenis pelayanan yang sama..!!");
                        } else {
                            if (ADDANTRIANAPIMOBILEJKN.equals("yes")) {
                                if (SimpanAntrianOnSite() == true) {
                                    JOptionPane.showMessageDialog(null, "Sukses, Antrian online BPJS berhasil dibuat.");
                                    dispose();
                                } else {
                                    JOptionPane.showMessageDialog(null, "Maaf, antrian mobile JKN gagal dibuat. Silahkan cek jadwal dokter / Nomor Referensi..!!");
                                }
                            }
                        }
                    }
                }
            } else {
                if (!NmPoli.getText().toLowerCase().contains("darurat")) {
                    if (Sequel.cariInteger("select count(bridging_sep.no_kartu) from bridging_sep where bridging_sep.no_kartu='" + no_peserta + "' and bridging_sep.jnspelayanan='2' and bridging_sep.tglsep like '%" + Valid.SetTgl(TanggalSEP.getSelectedItem() + "") + "%' and bridging_sep.nmpolitujuan='" + NmPoli.getText() + "'") >= 1) {
                        JOptionPane.showMessageDialog(null, "Maaf, sebelumnya sudah dilakukan pembuatan SEP di jenis pelayanan yang sama..!!");
                    } else {
                        if (ADDANTRIANAPIMOBILEJKN.equals("yes")) {
                            if (SimpanAntrianOnSite() == true) {
                                JOptionPane.showMessageDialog(null, "Sukses, Antrian online BPJS berhasil dibuat.");
                                dispose();
                            } else {
                                JOptionPane.showMessageDialog(null, "Maaf, antrian mobile JKN gagal dibuat. Silahkan cek jadwal dokter / Nomor Referensi..!!");
                            }
                        }
                    }
                }
            }
        }   
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnSimpanActionPerformed(null);
        }else{
            
        }
}//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        skdp.dispose();
        rujukanterakhir.dispose();
        emptTeks();
        dispose();
}//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnKeluarActionPerformed(null);
        }
}//GEN-LAST:event_BtnKeluarKeyPressed

    private void btnRiwayatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRiwayatActionPerformed
        historiPelayanan.setModal(true);
        historiPelayanan.setSize(internalFrame1.getWidth()+100,internalFrame1.getHeight()+100);
        historiPelayanan.setLocationRelativeTo(internalFrame1);
        historiPelayanan.setKartu(NoKartu.getText());
        historiPelayanan.setVisible(true);
    }//GEN-LAST:event_btnRiwayatActionPerformed

    private void btnSKDPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSKDPActionPerformed
        skdp.setModal(true);
        skdp.setNoRm(NoKartu.getText());
        skdp.isCek();
        skdp.setSize(internalFrame1.getWidth()+100,internalFrame1.getHeight()+100);
        skdp.setLocationRelativeTo(internalFrame1);        
        skdp.setVisible(true);
    }//GEN-LAST:event_btnSKDPActionPerformed

    private void TujuanKunjunganKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TujuanKunjunganKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TujuanKunjunganKeyPressed

    private void TujuanKunjunganItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_TujuanKunjunganItemStateChanged
        if(TujuanKunjungan.getSelectedIndex()==0){
            //fktp
            btnSKDP.setEnabled(false);
            NoSKDP.setEnabled(false);
            btnSKDP.setVisible(false);
            NoSKDP.setVisible(false);
            jLabel39.setVisible(false);
            
            NoRujukan.setEnabled(true);
            NoRujukan.setVisible(true);
            btnRiwayat.setEnabled(true);
            btnRiwayat.setVisible(true);
            btnRiwayatRujukan.setEnabled(true);
            btnRiwayatRujukan.setVisible(true);
            jLabel23.setVisible(true);
        }else if(TujuanKunjungan.getSelectedIndex()==1){
            //internal
            btnSKDP.setEnabled(false);
            NoSKDP.setEnabled(false);
            btnSKDP.setVisible(false);
            NoSKDP.setVisible(false);
            jLabel39.setVisible(false);
            
            NoRujukan.setEnabled(true);
            NoRujukan.setVisible(true);
            btnRiwayat.setEnabled(true);
            btnRiwayat.setVisible(true);
            btnRiwayatRujukan.setEnabled(true);
            btnRiwayatRujukan.setVisible(true);
            jLabel23.setVisible(true);
        }else if (TujuanKunjungan.getSelectedIndex()==2){
            //kontrol
            NoSKDP.setEnabled(true);
            btnSKDP.setEnabled(true);
            btnSKDP.setVisible(true);
            NoSKDP.setVisible(true );
            jLabel39.setVisible(true);
            
            NoRujukan.setEnabled(true);
            NoRujukan.setVisible(true);
            btnRiwayat.setEnabled(true);
            btnRiwayat.setVisible(true);
            btnRiwayatRujukan.setEnabled(true);
            btnRiwayatRujukan.setVisible(true);
            jLabel23.setVisible(true);
        }else if (TujuanKunjungan.getSelectedIndex()==3){
            //kontrol post ranap
            NoSKDP.setEnabled(true);
            btnSKDP.setEnabled(true);
            btnSKDP.setVisible(true);
            NoSKDP.setVisible(true );
            jLabel39.setVisible(true);
            
            NoRujukan.setEnabled(false);
            NoRujukan.setVisible(false);
            btnRiwayat.setEnabled(false);
            btnRiwayat.setVisible(false);
            btnRiwayatRujukan.setEnabled(false);
            btnRiwayatRujukan.setVisible(false);
            jLabel23.setVisible(false);
        }else if (TujuanKunjungan.getSelectedIndex()==4){
            // rujukan antar rs
            btnSKDP.setEnabled(false);
            NoSKDP.setEnabled(false);
            btnSKDP.setVisible(false);
            NoSKDP.setVisible(false);
            jLabel39.setVisible(false);
            
            NoRujukan.setEnabled(true);
            NoRujukan.setVisible(true);
            btnRiwayat.setEnabled(true);
            btnRiwayat.setVisible(true);
            btnRiwayatRujukan.setEnabled(true);
            btnRiwayatRujukan.setVisible(true);
            jLabel23.setVisible(true);
        }
    }//GEN-LAST:event_TujuanKunjunganItemStateChanged

    private void btnRiwayatRujukanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRiwayatRujukanActionPerformed
        if(NoKartu.getText().trim().equals("")){
            JOptionPane.showMessageDialog(null,"No.Kartu masih kosong...!!");
        }else{
            rujukanterakhir.setModal(true);
            rujukanterakhir.setSize(internalFrame1.getWidth()+100,internalFrame1.getHeight()+100);
            rujukanterakhir.setLocationRelativeTo(internalFrame1);
            rujukanterakhir.tampil(NoKartu.getText(),TPasien.getText());
            rujukanterakhir.setVisible(true);
            rujukanterakhir.toFront();
        }
    }//GEN-LAST:event_btnRiwayatRujukanActionPerformed

    private void NIKKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NIKKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_NIKKeyPressed

    private void NoTelpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoTelpKeyPressed
        
    }//GEN-LAST:event_NoTelpKeyPressed

    private void NoRujukanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoRujukanKeyPressed

    }//GEN-LAST:event_NoRujukanKeyPressed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        ApiBPJS api=new ApiBPJS();
        try {
            URL = link + "/Rujukan/Peserta/" + NoKartu.getText();
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIBPJS());
            utc = String.valueOf(api.GetUTCdatetimeAsString());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIBPJS());
            requestEntity = new HttpEntity(headers);
            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.GET, requestEntity, String.class).getBody());
            nameNode = root.path("metaData");

            String skdp_rajal ="", skdp_ranap="", rujukan ="";            
            if (nameNode.path("code").asText().equals("200")) {
                response = mapper.readTree(api.Decrypt(root.path("response").asText(), utc)).path("rujukan");
                rujukan = response.path("noKunjungan").asText();
            }            
            skdp_rajal = Sequel.cariIsi("select bridging_surat_kontrol_bpjs.no_surat from bridging_sep " +
                "inner join bridging_surat_kontrol_bpjs on bridging_surat_kontrol_bpjs.no_sep=bridging_sep.no_sep " +
                "where bridging_surat_kontrol_bpjs.tgl_rencana = CURDATE() and bridging_sep.jnspelayanan = '2'" +
                "and bridging_sep.no_kartu = ? " +
                "order by bridging_surat_kontrol_bpjs.tgl_rencana",NoKartu.getText());            
            skdp_ranap = Sequel.cariIsi("select bridging_surat_kontrol_bpjs.no_surat from bridging_sep " +
                "inner join bridging_surat_kontrol_bpjs on bridging_surat_kontrol_bpjs.no_sep=bridging_sep.no_sep " +
                "where bridging_surat_kontrol_bpjs.tgl_rencana = CURDATE() and bridging_sep.jnspelayanan = '1' "+
                "and bridging_sep.no_kartu = ? " +
                "order by bridging_surat_kontrol_bpjs.tgl_rencana",NoKartu.getText());
            
            if(!skdp_rajal.equals("") && skdp_ranap.equals("") &&!rujukan.equals("")){
                // 3. Kontrol rajal
                NoRujukan.setText(rujukan);
                NoSKDP.setText(skdp_rajal);
                TujuanKunjungan.setSelectedIndex(2);
                NoSKDP.setEnabled(true);
                btnSKDP.setEnabled(true);
                btnSKDP.setVisible(true);
                NoSKDP.setVisible(true );
                jLabel39.setVisible(true);
                
                NoRujukan.setEnabled(true);
                NoRujukan.setVisible(true);
                btnRiwayat.setEnabled(true);
                btnRiwayat.setVisible(true);
                btnRiwayatRujukan.setEnabled(true);
                btnRiwayatRujukan.setVisible(true);
                jLabel23.setVisible(true);
            }else if(skdp_rajal.equals("") && !skdp_ranap.equals("")){
                // 3. Kontrol Post Ranap
                NoRujukan.setText(rujukan);
                NoSKDP.setText(skdp_ranap);
                TujuanKunjungan.setSelectedIndex(3);
                NoSKDP.setEnabled(true);
                btnSKDP.setEnabled(true);
                btnSKDP.setVisible(true);
                NoSKDP.setVisible(true );
                jLabel39.setVisible(true);
                
                
                NoRujukan.setEnabled(false);
                NoRujukan.setVisible(false);
                btnRiwayat.setEnabled(false);
                btnRiwayat.setVisible(false);
                btnRiwayatRujukan.setEnabled(false);
                btnRiwayatRujukan.setVisible(false);
                jLabel23.setVisible(false);
            }else if(skdp_rajal.equals("") && skdp_ranap.equals("") && !rujukan.equals("")){
                int jml_sep = Sequel.cariInteger("SELECT COUNT(bs.no_rujukan) AS jml_sep FROM bridging_sep bs WHERE bs.no_rujukan =?", rujukan);                
                if(jml_sep>0){
                    //2. Internal
                    TujuanKunjungan.setSelectedIndex(1);
                    NoRujukan.setText(rujukan);
                    NoSKDP.setEnabled(false);
                    btnSKDP.setEnabled(false);
                    btnSKDP.setVisible(false);
                    NoSKDP.setVisible(false );
                    jLabel39.setVisible(false);
                    
                    NoRujukan.setEnabled(true);
                    NoRujukan.setVisible(true);
                    btnRiwayat.setEnabled(true);
                    btnRiwayat.setVisible(true);
                    btnRiwayatRujukan.setEnabled(true);
                    btnRiwayatRujukan.setVisible(true);
                    jLabel23.setVisible(true);
                }else{
                    //1. FKTP
                    TujuanKunjungan.setSelectedIndex(0);
                    NoRujukan.setText(rujukan);
                    NoSKDP.setEnabled(false);
                    btnSKDP.setEnabled(false);
                    btnSKDP.setVisible(false);
                    NoSKDP.setVisible(false );
                    jLabel39.setVisible(false);
                    
                    NoRujukan.setEnabled(true);
                    NoRujukan.setVisible(true);
                    btnRiwayat.setEnabled(true);
                    btnRiwayat.setVisible(true);
                    btnRiwayatRujukan.setEnabled(true);
                    btnRiwayatRujukan.setVisible(true);
                    jLabel23.setVisible(true);
                }
            }
            
        } catch (Exception e) {
            System.out.println("Gagal mencari rujukan" + e);
        }
    }//GEN-LAST:event_formWindowOpened

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            BPJSAntrianOnline dialog = new BPJSAntrianOnline(new javax.swing.JFrame(), true);
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
    private widget.Button BtnKeluar;
    private widget.Button BtnSimpan;
    private widget.PanelBiasa FormInput;
    private widget.TextBox KdDPJP;
    private widget.TextBox KdPoli;
    private widget.Label LabelPoli;
    private widget.Label LabelPoli2;
    private widget.TextBox NIK;
    private widget.TextBox NmDPJP;
    private widget.TextBox NmPoli;
    private widget.TextBox NoKartu;
    private widget.TextBox NoRujukan;
    private widget.TextBox NoSKDP;
    private widget.TextBox NoTelp;
    private widget.ScrollPane Scroll1;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.Tanggal TanggalSEP;
    private widget.ComboBox TujuanKunjungan;
    private widget.Button btnRiwayat;
    private widget.Button btnRiwayatRujukan;
    private widget.Button btnSKDP;
    private widget.InternalFrame internalFrame1;
    private widget.InternalFrame internalFrame2;
    private widget.Label jLabel20;
    private widget.Label jLabel23;
    private widget.Label jLabel29;
    private widget.Label jLabel30;
    private widget.Label jLabel39;
    private widget.Label jLabel4;
    private widget.Label jLabel42;
    private widget.Label jLabel5;
    private widget.panelisi panelGlass8;
    // End of variables declaration//GEN-END:variables

    
    
    

    private void isRawat() {
        Sequel.cariIsi("select reg_periksa.no_rkm_medis from reg_periksa where reg_periksa.no_rawat=? ",TNoRM,TNoRw.getText());
        
        Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis=? ",TPasien,TNoRM.getText());
        Sequel.cariIsi("select pasien.no_peserta from pasien where pasien.no_rkm_medis=? ",NoKartu,TNoRM.getText());
        Sequel.cariIsi("select left(pasien.no_tlp, 13) as no_tlp from pasien where pasien.no_rkm_medis=? ",NoTelp,TNoRM.getText());
        Sequel.cariIsi("select pasien.no_ktp from pasien where pasien.no_rkm_medis=? ",NIK,TNoRM.getText());
        
    }
    
    private void emptTeks(){
        TNoRw.setText("");
        TPasien.setText("");
        TanggalSEP.setDate(new Date());
        NoKartu.setText("");
        NoRujukan.setText("");
        KdPoli.setText("");
        NmPoli.setText("");
        TNoRM.setText("");
        NoSKDP.setText("");
        KdDPJP.setText("");
        NmDPJP.setText("");
        TujuanKunjungan.setSelectedIndex(0);       
        NoRujukan.requestFocus();
    }
    
    public void setNoRm2(String norwt, Date tgl1,String kdpoli,String namapoli,String kddokter,String noreg) {
        TNoRw.setText(norwt);
        kodedokterreg=kddokter;
        kodepolireg=kdpoli;
        nomorreg=noreg;
        KdPoli.setText(Sequel.cariIsi("select maping_poli_bpjs.kd_poli_bpjs from maping_poli_bpjs where maping_poli_bpjs.kd_poli_rs=?",kdpoli));
        NmPoli.setText(Sequel.cariIsi("select maping_poli_bpjs.nm_poli_bpjs from maping_poli_bpjs where maping_poli_bpjs.kd_poli_bpjs=?",KdPoli.getText()));
        KdDPJP.setText(Sequel.cariIsi("select maping_dokter_dpjpvclaim.kd_dokter_bpjs from maping_dokter_dpjpvclaim where maping_dokter_dpjpvclaim.kd_dokter=?",kddokter));
        NmDPJP.setText(Sequel.cariIsi("select maping_dokter_dpjpvclaim.nm_dokter_bpjs from maping_dokter_dpjpvclaim where maping_dokter_dpjpvclaim.kd_dokter=?",kddokter));


        isRawat();            
    }
    
    public void setNoRm(String norwt, Date tgl1,String status,String kdpoli,String namapoli) {
        TNoRw.setText(norwt);
        KdPoli.setText(Sequel.cariIsi("select maping_poli_bpjs.kd_poli_bpjs from maping_poli_bpjs where maping_poli_bpjs.kd_poli_rs=?",kdpoli));
        NmPoli.setText(Sequel.cariIsi("select maping_poli_bpjs.nm_poli_bpjs from maping_poli_bpjs where maping_poli_bpjs.kd_poli_bpjs=?",KdPoli.getText()));
        isRawat();
    }
      
    
    public boolean SimpanAntrianOnSite(){
        ApiMobileJKN apiMobileJKN=new ApiMobileJKN();
        statusantrean=true;
        if(Sequel.cariInteger("select count(referensi_mobilejkn_bpjs.no_rawat) from referensi_mobilejkn_bpjs where referensi_mobilejkn_bpjs.no_rawat=?", TNoRw.getText())==0 
                || Sequel.cariInteger("select count(referensi_mobilejkn_bpjs.no_rawat) from referensi_mobilejkn_bpjs where referensi_mobilejkn_bpjs.status ='Batal' and referensi_mobilejkn_bpjs.no_rawat=?", TNoRw.getText())>0 ){
            if((!NoRujukan.getText().equals(""))||(!NoSKDP.getText().equals(""))){
                if(TujuanKunjungan.getSelectedItem().toString().trim().equals("Rujukan FKTP")){
                    jeniskunjungan="1";
                }else if (TujuanKunjungan.getSelectedItem().toString().trim().equals("Internal")){
                    jeniskunjungan="2";
                }else if (TujuanKunjungan.getSelectedItem().toString().trim().equals("Kontrol")){
                    jeniskunjungan="3";
                }else if (TujuanKunjungan.getSelectedItem().toString().trim().equals("Post Ranap")){
                    jeniskunjungan="3";
                }else if (TujuanKunjungan.getSelectedItem().toString().trim().equals("Rujukan Antar RS")){
                    jeniskunjungan="4";
                }

                try {
                    day=cal.get(Calendar.DAY_OF_WEEK);
                    switch (day) {
                        case 1:
                            hari="AKHAD";
                            break;
                        case 2:
                            hari="SENIN";
                            break;
                        case 3:
                            hari="SELASA";
                            break;
                        case 4:
                            hari="RABU";
                            break;
                        case 5:
                            hari="KAMIS";
                            break;
                        case 6:
                            hari="JUMAT";
                            break;
                        case 7:
                            hari="SABTU";
                            break;
                        default:
                            break;
                    }
                    String tanggal = LocalDate.now().toString();
                    if(Sequel.cariInteger("SELECT COUNT(ln.tanggal) FROM libur_nasional ln WHERE ln.tanggal =?", tanggal)>0){
                        hari="LIBNAS";
                    }

                    ps=koneksi.prepareStatement("select jadwal.jam_mulai,jadwal.jam_selesai,jadwal.kuota from jadwal where jadwal.hari_kerja=? and jadwal.kd_poli=? and jadwal.kd_dokter=?");
                    try {
                        ps.setString(1,hari);
                        ps.setString(2,kodepolireg);
                        ps.setString(3,kodedokterreg);
                        rs=ps.executeQuery();
                        if(rs.next()){
                            jammulai=rs.getString("jam_mulai");
                            jamselesai=rs.getString("jam_selesai");
                            kuota=rs.getInt("kuota");
                            
                            datajam=Sequel.cariIsi("select DATE_ADD(concat('"+Valid.SetTgl(TanggalSEP.getSelectedItem()+"")+"',' ','"+jammulai+"'),INTERVAL "+(Integer.parseInt(nomorreg)*interval)+" SECOND) ");
                            parsedDate = dateFormat.parse(datajam);
                        }else{
                            statusantrean=false;
                            System.out.println("Jadwal tidak ditemukan...!");
                        }
                    } catch (Exception e) {
                        statusantrean=false;
                        System.out.println("Notif : "+e);
                    } finally{
                        if(rs!=null){
                            rs.close();
                        }
                        if(ps!=null){
                            ps.close();
                        }
                    }   

                    respon="200";
                    if(!NoRujukan.getText().equals("")){
                        try {
                            headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_JSON);
                            headers.add("x-cons-id",koneksiDB.CONSIDAPIMOBILEJKN());
                            utc=String.valueOf(apiMobileJKN.GetUTCdatetimeAsString());
                            headers.add("x-timestamp",utc);
                            headers.add("x-signature",apiMobileJKN.getHmac(utc));
                            headers.add("user_key",koneksiDB.USERKEYAPIMOBILEJKN());

                            requestJson ="{" +
                                            "\"kodebooking\": \""+TNoRw.getText()+"\"," +
                                            "\"jenispasien\": \"JKN\"," +
                                            "\"nomorkartu\": \""+NoKartu.getText()+"\"," +
                                            "\"nik\": \""+NIK.getText()+"\"," +
                                            "\"nohp\": \""+NoTelp.getText()+"\"," +
                                            "\"kodepoli\": \""+KdPoli.getText()+"\"," +
                                            "\"namapoli\": \""+NmPoli.getText()+"\"," +
                                            "\"pasienbaru\": 0," +
                                            "\"norm\": \""+TNoRM.getText()+"\"," +
                                            "\"tanggalperiksa\": \""+Valid.SetTgl(TanggalSEP.getSelectedItem()+"")+"\"," +
                                            "\"kodedokter\": "+KdDPJP.getText()+"," +
                                            "\"namadokter\": \""+NmDPJP.getText()+"\"," +
                                            "\"jampraktek\": \""+jammulai.substring(0,5)+"-"+jamselesai.substring(0,5)+"\"," +
                                            "\"jeniskunjungan\": "+jeniskunjungan+"," +
                                            "\"nomorreferensi\": \""+NoRujukan.getText()+"\"," +
                                            "\"nomorantrean\": \""+nomorreg+"\"," +
                                            "\"angkaantrean\": "+Integer.parseInt(nomorreg)+"," +
                                            "\"estimasidilayani\": "+parsedDate.getTime()+"," +
                                            "\"sisakuotajkn\": "+(kuota-Integer.parseInt(nomorreg))+"," +
                                            "\"kuotajkn\": "+kuota+"," +
                                            "\"sisakuotanonjkn\": "+(kuota-Integer.parseInt(nomorreg))+"," +
                                            "\"kuotanonjkn\": "+kuota+"," +
                                            "\"keterangan\": \"Peserta harap 30 menit lebih awal guna pencatatan administrasi.\"" +
                                        "}";
                            System.out.println("JSON : "+requestJson+"\n");
                            requestEntity = new HttpEntity(requestJson,headers);
                            URL = koneksiDB.URLAPIMOBILEJKN()+"/antrean/add";	
                            System.out.println("URL : "+URL);
                            root = mapper.readTree(apiMobileJKN.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                            nameNode = root.path("metadata");  
                            respon=nameNode.path("code").asText();
                            System.out.println("respon WS BPJS Kirim Pakai NoRujukan : "+nameNode.path("code").asText()+" "+nameNode.path("message").asText()+"\n");
                            if (nameNode.path("code").asText().equals("201")) {
                                statusantrean = false;
                            }else if (nameNode.path("code").asText().equals("208") || nameNode.path("code").asText().equals("200")) { 
                                statusantrean = true;
                            }
                        } catch (Exception e) {
                            statusantrean=false;
                            System.out.println("Notif No.Rujuk : "+e);
                        }
                    }

                    if(respon.equals("201")){
                        if(!NoSKDP.getText().equals("")){
                            try {
                                headers = new HttpHeaders();
                                headers.setContentType(MediaType.APPLICATION_JSON);
                                headers.add("x-cons-id",koneksiDB.CONSIDAPIMOBILEJKN());
                                utc=String.valueOf(apiMobileJKN.GetUTCdatetimeAsString());
                                headers.add("x-timestamp",utc);
                                headers.add("x-signature",apiMobileJKN.getHmac(utc));
                                headers.add("user_key",koneksiDB.USERKEYAPIMOBILEJKN());

                                requestJson ="{" +
                                                "\"kodebooking\": \""+TNoRw.getText()+"\"," +
                                                "\"jenispasien\": \"JKN\"," +
                                                "\"nomorkartu\": \""+NoKartu.getText()+"\"," +
                                                "\"nik\": \""+NIK.getText()+"\"," +
                                                "\"nohp\": \""+NoTelp.getText()+"\"," +
                                                "\"kodepoli\": \""+KdPoli.getText()+"\"," +
                                                "\"namapoli\": \""+NmPoli.getText()+"\"," +
                                                "\"pasienbaru\": 0," +
                                                "\"norm\": \""+TNoRM.getText()+"\"," +
                                                "\"tanggalperiksa\": \""+Valid.SetTgl(TanggalSEP.getSelectedItem()+"")+"\"," +
                                                "\"kodedokter\": "+KdDPJP.getText()+"," +
                                                "\"namadokter\": \""+NmDPJP.getText()+"\"," +
                                                "\"jampraktek\": \""+jammulai.substring(0,5)+"-"+jamselesai.substring(0,5)+"\"," +
                                                "\"jeniskunjungan\": "+jeniskunjungan+"," +
                                                "\"nomorreferensi\": \""+NoSKDP.getText()+"\"," +
                                                "\"nomorantrean\": \""+nomorreg+"\"," +
                                                "\"angkaantrean\": "+Integer.parseInt(nomorreg)+"," +
                                                "\"estimasidilayani\": "+parsedDate.getTime()+"," +
                                                "\"sisakuotajkn\": "+(kuota-Integer.parseInt(nomorreg))+"," +
                                                "\"kuotajkn\": "+kuota+"," +
                                                "\"sisakuotanonjkn\": "+(kuota-Integer.parseInt(nomorreg))+"," +
                                                "\"kuotanonjkn\": "+kuota+"," +
                                                "\"keterangan\": \"Peserta harap 30 menit lebih awal guna pencatatan administrasi.\"" +
                                            "}";
                                System.out.println("JSON : "+requestJson+"\n");
                                requestEntity = new HttpEntity(requestJson,headers);
                                URL = koneksiDB.URLAPIMOBILEJKN()+"/antrean/add";	
                                System.out.println("URL : "+URL);
                                root = mapper.readTree(apiMobileJKN.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                                nameNode = root.path("metadata");  
                                System.out.println("respon WS BPJS Kirim Pakai SKDP : "+nameNode.path("code").asText()+" "+nameNode.path("message").asText()+"\n");
                                if(nameNode.path("code").asText().equals("201")){
                                    statusantrean=false;
                                }else if(nameNode.path("code").asText().equals("208") || nameNode.path("code").asText().equals("200")){
                                    statusantrean=true;
                                }
                            } catch (Exception e) {
                                statusantrean=false;
                                System.out.println("Notif SKDP : "+e);
                            }
                        }
                    }
                } catch (Exception e) {
                    statusantrean=false;
                    System.out.println("Notif : "+e);
                }
            }
        }
        return statusantrean;
    }
    

}
