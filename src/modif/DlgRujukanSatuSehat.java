/*
 * Dialog Rujukan Pasien SATUSEHAT (Rawat Inap & Gawat Darurat).
 * Dibuka dari menu Bridging IGD dan tombol "Rujukan SATUSEHAT" Rawat Inap.
 * Logika API & penyimpanan ada di modif.SatuSehatRujukan.
 *
 * Tab "Rujuk Keluar" (RS sebagai Faskes Perujuk):
 *   1. Kriteria & Wilayah   : Pra Permintaan (server mengirim Questionnaire), isi kuesioner, cari kandidat
 *   2. Kandidat & Persetujuan: pilih faskes tujuan, kirim Task persetujuan + CarePlan, cek respon, batalkan
 *   3. Kirim Rujukan        : ServiceRequest ke faskes yang menerima, nomor rujukan SATUSEHAT
 * Tab "Rujukan Masuk" (RS sebagai Faskes Rujukan):
 *   daftar Task persetujuan untuk RS ini, Terima/Tolak, cari ServiceRequest rujukan dan
 *   hubungkan ke Encounter kunjungan pasien yang sedang dibuka.
 *
 * Form dibuat manual (tanpa .form NetBeans) dengan susunan standar Khanza: header data pasien di FormInput
 * (PanelBiasa, layout null, TextBox hanya baca), toolbar langkah di widget.panelisi, tombol Baru/Muat Ulang/Keluar
 * di panel bawah (panelGlass8).
 * Request ke SATUSEHAT berjalan di thread background; isian form dibaca di EDT sebelum thread jalan.
 */

package modif;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fungsi.WarnaTable;
import fungsi.koneksiDB;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import modif.SatuSehatRujukan.DataPasien;
import modif.SatuSehatRujukan.DataPendukung;
import modif.SatuSehatRujukan.Diagnosa;
import modif.SatuSehatRujukan.Kandidat;
import modif.SatuSehatRujukan.Proses;
import modif.SatuSehatRujukan.Respon;

public final class DlgRujukanSatuSehat extends javax.swing.JDialog {
    private final SatuSehatRujukan rujukan=new SatuSehatRujukan();
    private DataPasien data;
    private Proses proses;
    /** resource Task pencarian kandidat dari server (dipakai Task respon kandidat Rawat Inap) */
    private JsonNode taskKandidat;
    private final List<Kandidat> daftarKandidat=new ArrayList<>();
    private final List<DataPendukung> daftarPendukung=new ArrayList<>();
    private final List<JsonNode> daftarTaskMasuk=new ArrayList<>();
    private final Map<String,JsonNode> carePlanMasuk=new LinkedHashMap<>();
    private FormKuesioner formKriteria,formWilayah;
    private JsonNode serviceRequestMasuk;
    private String noRawat="",kdDokter="";
    private volatile boolean sibuk=false;

    private final widget.InternalFrame internalFrame1=new widget.InternalFrame();
    private final widget.TabPane tabUtama=new widget.TabPane();
    private final widget.TabPane tabLangkah=new widget.TabPane();
    // header data pasien (hanya baca)
    private final widget.TextBox TNoRw=new widget.TextBox();
    private final widget.TextBox TNoRM=new widget.TextBox();
    private final widget.TextBox TPasien=new widget.TextBox();
    private final widget.TextBox TCaraBayar=new widget.TextBox();
    private final widget.TextBox TIHSPasien=new widget.TextBox();
    private final widget.TextBox TEncounter=new widget.TextBox();
    private final widget.TextBox TDokter=new widget.TextBox();
    private final widget.TextBox TIHSDokter=new widget.TextBox();
    private final widget.TextBox TDiagnosa=new widget.TextBox();
    private final widget.TextBox TStatus=new widget.TextBox();
    private final widget.Label lblProses=new widget.Label();
    private final widget.ComboBox cmbJenis=new widget.ComboBox();
    private final widget.TextArea txtLog=new widget.TextArea();

    // langkah 1
    private final JPanel panelKuesioner=new JPanel();
    private final widget.ComboBox cmbKelompokLayanan=new widget.ComboBox();
    // langkah 2
    private DefaultTableModel tabModeKandidat;
    private final widget.Table tbKandidat=new widget.Table();
    private final widget.TextBox txtCariFaskes=new widget.TextBox();
    private final widget.Label lblBed=new widget.Label();
    private final widget.TextBox txtJudul=new widget.TextBox();
    private final widget.ComboBox cmbSpesialisasi=new widget.ComboBox();
    private final widget.TextArea txtDeskripsi=new widget.TextArea();
    // langkah 3
    private DefaultTableModel tabModePendukung;
    private final widget.Table tbPendukung=new widget.Table();
    private final widget.ComboBox cmbTujuan=new widget.ComboBox();
    private final widget.ComboBox cmbTenaga=new widget.ComboBox();
    private final widget.TextBox txtRencana=new widget.TextBox();
    private final widget.TextBox txtKeterangan=new widget.TextBox();
    private final widget.TextBox txtNoRujukanBpjs=new widget.TextBox();
    private final widget.TextBox txtInstruksi=new widget.TextBox();
    private final widget.Label lblHasil=new widget.Label();
    // rujukan masuk
    private DefaultTableModel tabModeMasuk;
    private final widget.Table tbMasuk=new widget.Table();
    private final widget.ComboBox cmbFilterMasuk=new widget.ComboBox();
    private final widget.ComboBox cmbCariSR=new widget.ComboBox();
    private final widget.TextBox txtCariSR=new widget.TextBox();
    private final widget.TextArea txtDetailMasuk=new widget.TextArea();
    private final widget.Button btnHubungkan=new widget.Button();

    public DlgRujukanSatuSehat(java.awt.Frame parent,boolean modal){
        super(parent,modal);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        initKomponen();
        setSize(1100,700);
    }

    // ===================================================================================
    // Susunan form
    // ===================================================================================

    private void initKomponen(){
        internalFrame1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(240,245,235)),
                "::[ Rujukan Pasien SATUSEHAT ]::",TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,new Font("Tahoma",0,11),new Color(50,50,50)));
        internalFrame1.setName("internalFrame1");
        internalFrame1.setLayout(new BorderLayout(1,1));

        tabUtama.setBackground(new Color(255,255,254));
        tabUtama.setForeground(new Color(50,50,50));
        tabUtama.setFont(new Font("Tahoma",0,11));
        tabUtama.setName("TabRawat");
        tabUtama.addTab("Rujuk Keluar (Faskes Perujuk)",panelRujukKeluar());
        tabUtama.addTab("Rujukan Masuk (Faskes Rujukan)",panelRujukanMasuk());
        tabUtama.addTab("Log Proses",panelLog());
        internalFrame1.add(tabUtama,BorderLayout.CENTER);

        // panel tombol bawah standar Khanza
        widget.panelisi panelGlass8=panelisi(54);
        widget.Button BtnBaru=tombol("Baru","/picture/addressbook-edit24.png",100);
        BtnBaru.setMnemonic('B');
        BtnBaru.setToolTipText("Alt+B, mulai proses rujukan baru (proses sebelumnya tetap tersimpan)");
        BtnBaru.addActionListener(e->rujukanBaru());
        panelGlass8.add(BtnBaru);
        widget.Button BtnMuat=tombol("Muat Ulang","/picture/refresh.png",125);
        BtnMuat.setMnemonic('M');
        BtnMuat.setToolTipText("Alt+M, baca ulang data pasien, diagnosa dan data SATUSEHAT kunjungan");
        BtnMuat.addActionListener(e->muatData());
        panelGlass8.add(BtnMuat);
        panelGlass8.add(label("Proses :",60));
        lblProses.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblProses.setText("-");
        lblProses.setPreferredSize(new Dimension(560,23));
        panelGlass8.add(lblProses);
        widget.Button BtnKeluar=tombol("Keluar","/picture/exit.png",100);
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.addActionListener(e->dispose());
        panelGlass8.add(BtnKeluar);
        internalFrame1.add(panelGlass8,BorderLayout.PAGE_END);

        getContentPane().add(internalFrame1,BorderLayout.CENTER);
    }

    private widget.InternalFrame panelRujukKeluar(){
        widget.InternalFrame internalFrame2=frame();

        widget.PanelBiasa FormInput=new widget.PanelBiasa();
        FormInput.setBorder(null);
        FormInput.setName("FormInput");
        FormInput.setLayout(null);
        FormInput.setPreferredSize(new Dimension(750,165));
        tempel(FormInput,label("Data Pasien :",95),0,10,95);
        tempel(FormInput,teksBaca(TNoRw),99,10,140);
        tempel(FormInput,teksBaca(TNoRM),241,10,100);
        tempel(FormInput,teksBaca(TPasien),343,10,300);
        tempel(FormInput,label("Cara Bayar :",80),646,10,80);
        tempel(FormInput,teksBaca(TCaraBayar),730,10,190);
        tempel(FormInput,label("IHS Pasien :",95),0,40,95);
        tempel(FormInput,teksBaca(TIHSPasien),99,40,242);
        tempel(FormInput,label("Encounter :",80),343,40,80);
        tempel(FormInput,teksBaca(TEncounter),427,40,216);
        tempel(FormInput,label("Jenis Rujukan :",80),646,40,80);
        cmbJenis.addItem(SatuSehatRujukan.JENIS_RANAP);
        cmbJenis.addItem(SatuSehatRujukan.JENIS_IGD);
        tempel(FormInput,cmbJenis,730,40,190);
        tempel(FormInput,label("Dokter :",95),0,70,95);
        tempel(FormInput,teksBaca(TDokter),99,70,242);
        tempel(FormInput,label("IHS Dokter :",80),343,70,80);
        tempel(FormInput,teksBaca(TIHSDokter),427,70,216);
        tempel(FormInput,label("Diagnosa :",95),0,100,95);
        tempel(FormInput,teksBaca(TDiagnosa),99,100,821);
        tempel(FormInput,label("Status :",95),0,130,95);
        teksBaca(TStatus).setForeground(new Color(0,100,80));
        tempel(FormInput,TStatus,99,130,821);
        internalFrame2.add(FormInput,BorderLayout.PAGE_START);

        tabLangkah.setBackground(new Color(255,255,254));
        tabLangkah.setForeground(new Color(50,50,50));
        tabLangkah.setFont(new Font("Tahoma",0,11));
        tabLangkah.setName("TabLangkah");
        tabLangkah.addTab("1. Kriteria & Wilayah",panelLangkah1());
        tabLangkah.addTab("2. Kandidat & Persetujuan",panelLangkah2());
        tabLangkah.addTab("3. Kirim Rujukan",panelLangkah3());
        internalFrame2.add(tabLangkah,BorderLayout.CENTER);
        return internalFrame2;
    }

    private widget.InternalFrame panelLangkah1(){
        widget.InternalFrame panel=frame();
        widget.panelisi atas=panelisi(44);
        widget.Button btnPra=tombol("Ambil Kriteria Rujukan","/picture/inbox.png",190);
        btnPra.setToolTipText("Kirim Task Pra Permintaan Rujukan, SATUSEHAT membalas kuesioner kriteria & wilayah");
        btnPra.addActionListener(e->praPermintaan());
        atas.add(btnPra);
        widget.Label petunjuk=label("Isi kuesioner dari SATUSEHAT, lalu klik Cari Kandidat Faskes.",450);
        petunjuk.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        atas.add(petunjuk);
        panel.add(atas,BorderLayout.PAGE_START);

        panelKuesioner.setLayout(new BoxLayout(panelKuesioner,BoxLayout.Y_AXIS));
        panelKuesioner.setBackground(new Color(255,255,255));
        panel.add(gulung(panelKuesioner),BorderLayout.CENTER);

        widget.panelisi bawah=panelisi(44);
        bawah.add(label("Kelompok Layanan :",110));
        siapkanKombo(cmbKelompokLayanan,420,"Pilih atau ketik dengan format KODE - Nama, misal TK000563 - Kelompok Layanan Jantung dan Pembuluh Darah");
        bawah.add(cmbKelompokLayanan);
        widget.Button btnKandidat=tombol("Cari Kandidat Faskes","/picture/Search-16x16.png",180);
        btnKandidat.addActionListener(e->cariKandidat());
        bawah.add(btnKandidat);
        panel.add(bawah,BorderLayout.PAGE_END);
        return panel;
    }

    private widget.InternalFrame panelLangkah2(){
        widget.InternalFrame panel=frame();
        widget.panelisi atas=panelisi(44);
        atas.add(label("Tambah Faskes :",90));
        txtCariFaskes.setPreferredSize(new Dimension(220,23));
        txtCariFaskes.setToolTipText("Nama faskes bila tidak ada di daftar kandidat");
        txtCariFaskes.addActionListener(e->cariFaskes());
        atas.add(txtCariFaskes);
        atas.add(tombolCari(e->cariFaskes(),"Cari faskes berdasarkan nama"));
        widget.Button btnMuatKandidat=tombol("Muat Ulang Kandidat","/picture/refresh.png",170);
        btnMuatKandidat.addActionListener(e->muatUlangKandidat());
        atas.add(btnMuatKandidat);
        widget.Button btnCek=tombol("Cek Status Persetujuan","/picture/inbox.png",190);
        btnCek.addActionListener(e->cekStatusPersetujuan());
        atas.add(btnCek);
        widget.Button btnBatal=tombol("Batalkan Task Dicentang","/picture/Cancel-2-16x16.png",190);
        btnBatal.addActionListener(e->batalkanTask());
        atas.add(btnBatal);
        panel.add(atas,BorderLayout.PAGE_START);

        tabModeKandidat=new DefaultTableModel(null,new Object[]{
            "P","Nama Faskes","ID Organisasi","Jarak (km)","Waktu (menit)","Strata","Kode BPJS","Bed Tersedia","ID Task Persetujuan","Status Task","Respon"}){
            @Override
            public boolean isCellEditable(int baris,int kolom){
                return kolom==0;
            }
            @Override
            public Class getColumnClass(int kolom){
                return kolom==0?Boolean.class:Object.class;
            }
        };
        tbKandidat.setModel(tabModeKandidat);
        tbKandidat.setToolTipText("Centang faskes tujuan yang akan dikirimi permintaan persetujuan");
        lebarKolom(tbKandidat,new int[]{25,230,90,70,80,80,80,80,230,90,80});
        tbKandidat.getSelectionModel().addListSelectionListener(e->{
            int baris=tbKandidat.getSelectedRow();
            if(baris>=0&&baris<daftarKandidat.size()){
                String detail=daftarKandidat.get(baris).bedDetail;
                lblBed.setText(detail.equals("")?" ":"<html>Ketersediaan bed : "+detail.replace("\n"," | ")+"</html>");
            }
        });
        panel.add(gulung(tbKandidat),BorderLayout.CENTER);

        widget.PanelBiasa FormInput=new widget.PanelBiasa();
        FormInput.setLayout(null);
        FormInput.setPreferredSize(new Dimension(750,120));
        lblBed.setText(" ");
        lblBed.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        tempel(FormInput,lblBed,10,8,910);
        tempel(FormInput,label("Judul Rencana :",115),0,36,115);
        txtJudul.setText("Rencana Rujukan Pasien");
        tempel(FormInput,txtJudul,119,36,300);
        tempel(FormInput,label("Layanan Diminta :",105),421,36,105);
        siapkanKombo(cmbSpesialisasi,390,"Spesialisasi layanan (clinical-speciality), format KODE - Nama, misal LY133 - Syaraf - Stroke dan Cerebro Vaskuler");
        tempel(FormInput,cmbSpesialisasi,530,36,390);
        tempel(FormInput,label("Alasan/Deskripsi :",115),0,66,115);
        txtDeskripsi.setLineWrap(true);
        txtDeskripsi.setWrapStyleWord(true);
        txtDeskripsi.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
        widget.ScrollPane scrollDeskripsi=gulung(txtDeskripsi);
        scrollDeskripsi.setBorder(BorderFactory.createLineBorder(new Color(0,0,0)));
        FormInput.add(scrollDeskripsi);
        scrollDeskripsi.setBounds(119,66,575,46);
        widget.Button btnKirim=tombol("Kirim Permintaan Persetujuan","/picture/save-16x16.png",222);
        btnKirim.setToolTipText("Kirim Task persetujuan ke faskes yang dicentang beserta CarePlan rencana rujukan");
        btnKirim.addActionListener(e->kirimPersetujuan());
        FormInput.add(btnKirim);
        btnKirim.setBounds(698,74,222,30);
        panel.add(FormInput,BorderLayout.PAGE_END);
        return panel;
    }

    private widget.InternalFrame panelLangkah3(){
        widget.InternalFrame panel=frame();
        widget.panelisi atas=panelisi(44);
        widget.Label petunjuk=label("Data pendukung yang dicentang ikut dikirim (supportingInfo). Data diambil dari kiriman SATUSEHAT kunjungan ini.",700);
        petunjuk.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        atas.add(petunjuk);
        panel.add(atas,BorderLayout.PAGE_START);

        tabModePendukung=new DefaultTableModel(null,new Object[]{"P","Data Pendukung","Resource","ID SATUSEHAT"}){
            @Override
            public boolean isCellEditable(int baris,int kolom){
                return kolom==0;
            }
            @Override
            public Class getColumnClass(int kolom){
                return kolom==0?Boolean.class:Object.class;
            }
        };
        tbPendukung.setModel(tabModePendukung);
        lebarKolom(tbPendukung,new int[]{25,560,130,250});
        panel.add(gulung(tbPendukung),BorderLayout.CENTER);

        widget.PanelBiasa FormInput=new widget.PanelBiasa();
        FormInput.setLayout(null);
        FormInput.setPreferredSize(new Dimension(750,106));
        tempel(FormInput,label("Faskes Tujuan :",115),0,10,115);
        cmbTujuan.setToolTipText("Hanya faskes yang sudah menerima permintaan persetujuan");
        tempel(FormInput,cmbTujuan,119,10,330);
        tempel(FormInput,label("Dokter Dituju :",105),451,10,105);
        siapkanKombo(cmbTenaga,360,"Jenis tenaga medis SNOMED (performerType), format KODE - Nama, misal 39677007 - Internal medicine specialist");
        tempel(FormInput,cmbTenaga,560,10,360);
        tempel(FormInput,label("Rencana Tgl/Jam :",115),0,40,115);
        txtRencana.setToolTipText("Format yyyy-MM-dd HH:mm");
        tempel(FormInput,txtRencana,119,40,130);
        tempel(FormInput,label("Keterangan :",75),251,40,75);
        txtKeterangan.setToolTipText("Alasan rujukan (ServiceRequest.code.text)");
        tempel(FormInput,txtKeterangan,330,40,320);
        tempel(FormInput,label("No.Rujukan BPJS :",100),652,40,100);
        txtNoRujukanBpjs.setToolTipText("Opsional, nomor rujukan BPJS (referral-number-pcare)");
        tempel(FormInput,txtNoRujukanBpjs,756,40,164);
        tempel(FormInput,label("Instruksi Pasien :",115),0,70,115);
        tempel(FormInput,txtInstruksi,119,70,420);
        widget.Button btnKirim=tombol("Kirim Rujukan","/picture/save-16x16.png",150);
        btnKirim.setToolTipText("Kirim ServiceRequest rujukan ke faskes yang menerima");
        btnKirim.addActionListener(e->kirimRujukan());
        FormInput.add(btnKirim);
        btnKirim.setBounds(543,67,150,30);
        lblHasil.setForeground(new Color(0,100,80));
        lblHasil.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        tempel(FormInput,lblHasil,700,70,220);
        panel.add(FormInput,BorderLayout.PAGE_END);
        return panel;
    }

    private widget.InternalFrame panelRujukanMasuk(){
        widget.InternalFrame panel=frame();
        widget.panelisi atas=panelisi(44);
        atas.add(label("Tampilkan :",65));
        cmbFilterMasuk.addItem("Menunggu Respon");
        cmbFilterMasuk.addItem("Semua");
        cmbFilterMasuk.setPreferredSize(new Dimension(140,23));
        atas.add(cmbFilterMasuk);
        atas.add(tombolCari(e->tampilTaskMasuk(),"Tampilkan permintaan rujukan untuk RS ini"));
        widget.Button btnTerima=tombol("Terima","/picture/accept.png",100);
        btnTerima.addActionListener(e->responTaskMasuk(true));
        atas.add(btnTerima);
        widget.Button btnTolak=tombol("Tolak","/picture/cross.png",100);
        btnTolak.addActionListener(e->responTaskMasuk(false));
        atas.add(btnTolak);
        panel.add(atas,BorderLayout.PAGE_START);

        tabModeMasuk=new DefaultTableModel(null,new Object[]{
            "ID Task","Tanggal","Faskes Perujuk","ID Pasien","Nama Pasien","Diagnosa","Rencana","Layanan Diminta","Status","Respon"}){
            @Override
            public boolean isCellEditable(int baris,int kolom){
                return false;
            }
        };
        tbMasuk.setModel(tabModeMasuk);
        tbMasuk.setToolTipText("Silahkan klik untuk memilih permintaan yang akan diterima/ditolak");
        lebarKolom(tbMasuk,new int[]{230,130,180,110,170,150,250,200,80,80});
        tbMasuk.getSelectionModel().addListSelectionListener(e->{
            if(!e.getValueIsAdjusting()){
                tampilDetailTaskMasuk();
            }
        });

        widget.InternalFrame detail=frame();
        widget.panelisi barisCari=panelisi(44);
        barisCari.add(label("Cari Rujukan :",80));
        cmbCariSR.addItem("No.Rujukan SATUSEHAT");
        cmbCariSR.addItem("No.Rujukan BPJS/PCare");
        cmbCariSR.addItem("ID ServiceRequest");
        cmbCariSR.setPreferredSize(new Dimension(170,23));
        barisCari.add(cmbCariSR);
        txtCariSR.setPreferredSize(new Dimension(220,23));
        txtCariSR.addActionListener(e->cariServiceRequest());
        barisCari.add(txtCariSR);
        barisCari.add(tombolCari(e->cariServiceRequest(),"Cari ServiceRequest rujukan"));
        btnHubungkan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inbox.png")));
        btnHubungkan.setText("Hubungkan ke Kunjungan Pasien Ini");
        btnHubungkan.setPreferredSize(new Dimension(250,30));
        btnHubungkan.setToolTipText("Tambahkan ServiceRequest rujukan ke Encounter kunjungan yang sedang dibuka (Encounter.basedOn)");
        btnHubungkan.addActionListener(e->hubungkanKunjungan());
        barisCari.add(btnHubungkan);
        detail.add(barisCari,BorderLayout.PAGE_START);
        txtDetailMasuk.setEditable(false);
        txtDetailMasuk.setLineWrap(true);
        txtDetailMasuk.setWrapStyleWord(true);
        detail.add(gulung(txtDetailMasuk),BorderLayout.CENTER);

        JSplitPane split=new JSplitPane(JSplitPane.VERTICAL_SPLIT,gulung(tbMasuk),detail);
        split.setResizeWeight(0.5);
        split.setDividerLocation(220);
        split.setBorder(null);
        panel.add(split,BorderLayout.CENTER);
        return panel;
    }

    private widget.InternalFrame panelLog(){
        widget.InternalFrame panel=frame();
        txtLog.setEditable(false);
        txtLog.setLineWrap(true);
        txtLog.setWrapStyleWord(true);
        panel.add(gulung(txtLog),BorderLayout.CENTER);
        return panel;
    }

    /** InternalFrame tanpa border dengan BorderLayout, seperti internalFrame2/internalFrame4 form Khanza. */
    private static widget.InternalFrame frame(){
        widget.InternalFrame frame=new widget.InternalFrame();
        frame.setBorder(null);
        frame.setLayout(new BorderLayout(1,1));
        return frame;
    }

    /** Panel tombol/pencarian standar Khanza (panelGlass), FlowLayout kiri. */
    private static widget.panelisi panelisi(int tinggi){
        widget.panelisi panel=new widget.panelisi();
        panel.setBorder(null);
        panel.setPreferredSize(new Dimension(44,tinggi));
        panel.setLayout(new FlowLayout(FlowLayout.LEFT,5,9));
        return panel;
    }

    private static widget.ScrollPane gulung(Component isi){
        widget.ScrollPane scroll=new widget.ScrollPane();
        scroll.setBorder(BorderFactory.createLineBorder(new Color(255,255,255)));
        scroll.setOpaque(true);
        scroll.setViewportView(isi);
        return scroll;
    }

    /** Menempel komponen ke panel layout null dengan tinggi 23. */
    private static void tempel(JPanel panel,JComponent komponen,int x,int y,int lebar){
        panel.add(komponen);
        komponen.setBounds(x,y,lebar,23);
    }

    /** TextBox hanya baca seperti TNoRw/TPasien di form Khanza. */
    private static widget.TextBox teksBaca(widget.TextBox teks){
        teks.setEditable(false);
        teks.setBackground(new Color(245,250,240));
        teks.setHighlighter(null);
        return teks;
    }

    private static JPanel baris(){
        JPanel panel=new JPanel(new FlowLayout(FlowLayout.LEFT,5,4));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    private static widget.Label label(String teks,int lebar){
        widget.Label label=new widget.Label();
        label.setText(teks);
        label.setPreferredSize(new Dimension(lebar,23));
        return label;
    }

    private widget.Button tombol(String teks,String ikon,int lebar){
        widget.Button tombol=new widget.Button();
        try {
            tombol.setIcon(new javax.swing.ImageIcon(getClass().getResource(ikon)));
        } catch (Exception e) {
            // ikon tidak ada, tombol tetap tampil tanpa ikon
        }
        tombol.setText(teks);
        tombol.setPreferredSize(new Dimension(lebar,30));
        return tombol;
    }

    /** Tombol cari kecil (ikon accept, 28x23) seperti BtnCari form Khanza. */
    private widget.Button tombolCari(java.awt.event.ActionListener aksi,String tip){
        widget.Button tombol=new widget.Button();
        tombol.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        tombol.setToolTipText(tip);
        tombol.setPreferredSize(new Dimension(28,23));
        tombol.addActionListener(aksi);
        return tombol;
    }

    private static void siapkanKombo(widget.ComboBox kombo,int lebar,String tip){
        kombo.setEditable(true);
        kombo.setPreferredSize(new Dimension(lebar,23));
        kombo.setToolTipText(tip);
    }

    private static void lebarKolom(widget.Table tabel,int[] lebar){
        tabel.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        for(int i=0;i<lebar.length;i++){
            tabel.getColumnModel().getColumn(i).setPreferredWidth(lebar[i]);
        }
        tabel.setDefaultRenderer(Object.class,new WarnaTable());
    }

    // ===================================================================================
    // Pembantu proses
    // ===================================================================================

    /** Menjalankan kerja di thread background, satu proses dalam satu waktu. */
    private void jalankan(String keterangan,Runnable kerja){
        if(sibuk){
            JOptionPane.showMessageDialog(this,"Masih ada proses yang berjalan, silahkan tunggu...");
            return;
        }
        sibuk=true;
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        log(keterangan+"...");
        Thread thread=new Thread(()->{
            try {
                kerja.run();
            } catch (Exception e) {
                System.out.println("Notifikasi Rujukan SATUSEHAT : "+e);
                pesan("Terjadi kesalahan : "+e.getMessage());
            } finally{
                sibuk=false;
                SwingUtilities.invokeLater(()->setCursor(Cursor.getDefaultCursor()));
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void log(String teks){
        String baris=new SimpleDateFormat("HH:mm:ss").format(new Date())+"  "+teks;
        SwingUtilities.invokeLater(()->{
            txtLog.append((txtLog.getText().equals("")?"":"\n")+baris);
            txtLog.setCaretPosition(txtLog.getDocument().getLength());
            lblProses.setText(baris);
            lblProses.setToolTipText(baris);
        });
    }

    private void pesan(String teks){
        log(teks.replace("\n"," "));
        SwingUtilities.invokeLater(()->JOptionPane.showMessageDialog(this,teks));
    }

    /** Pesan gagal dari balasan SATUSEHAT. */
    private void gagal(String langkah,Respon respon){
        pesan(langkah+" gagal :\n"+respon.pesan());
    }

    private void isiKombo(widget.ComboBox kombo,List<String> isi,String bawaan){
        Object pilihan=kombo.getEditor().getItem();
        kombo.removeAllItems();
        for(String item:isi){
            kombo.addItem(item);
        }
        if(isi.isEmpty()&&bawaan!=null){
            kombo.addItem(bawaan);
        }
        if(pilihan!=null&&!pilihan.toString().trim().equals("")){
            kombo.setSelectedItem(pilihan);
        }else if(kombo.getItemCount()>0){
            kombo.setSelectedIndex(0);
        }
    }

    private String teksKombo(widget.ComboBox kombo){
        Object item=kombo.isEditable()?kombo.getEditor().getItem():kombo.getSelectedItem();
        return item==null?"":item.toString().trim();
    }

    private boolean dataSiap(boolean perluDokter){
        if(data==null){
            JOptionPane.showMessageDialog(this,"Data pasien belum dimuat...");
            return false;
        }
        if(data.idPasien.equals("")){
            JOptionPane.showMessageDialog(this,"IHS Pasien tidak ditemukan. Pastikan No.KTP pasien benar dan sudah terdaftar di SATUSEHAT.");
            return false;
        }
        if(data.idEncounter.equals("")){
            JOptionPane.showMessageDialog(this,"Encounter kunjungan ini belum dikirim ke SATUSEHAT. Kirim dulu lewat menu SATUSEHAT Kirim Encounter.");
            return false;
        }
        if(data.diagnosaUtama()==null){
            JOptionPane.showMessageDialog(this,"Diagnosa pasien (ICD 10) belum diisi.");
            return false;
        }
        if(perluDokter&&data.idDokter.equals("")){
            JOptionPane.showMessageDialog(this,"IHS Practitioner dokter "+data.nmDokter+" tidak ditemukan. Pastikan No.KTP dokter benar.");
            return false;
        }
        return true;
    }

    private String sekarangDb(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    // ===================================================================================
    // Pemanggilan dari form lain
    // ===================================================================================

    /**
     * @param norawat  no_rawat kunjungan aktif
     * @param kddokter dokter pilihan form pemanggil (boleh kosong, dipakai DPJP ranap / dokter registrasi)
     * @param jenis    SatuSehatRujukan.JENIS_RANAP atau JENIS_IGD (jenis rujukan bawaan)
     */
    public void setPasien(String norawat,String kddokter,String jenis){
        noRawat=norawat==null?"":norawat.trim();
        kdDokter=kddokter==null?"":kddokter.trim();
        if(jenis!=null){
            cmbJenis.setSelectedItem(jenis);
        }
        btnHubungkan.setEnabled(!noRawat.equals(""));
        if(!rujukan.tabelTersedia()){
            pesan("Tabel rujukan SATUSEHAT belum dibuat. Minta tim IT menjalankan sik_satu_sehat_rujukan.sql di database.");
            return;
        }
        if(rujukan.getIdOrg().equals("")){
            pesan("ID Organisasi SATUSEHAT (IDSATUSEHAT) belum diatur di setting/database.xml.");
        }
        muatReferensi();
        if(!noRawat.equals("")){
            muatData();
        }
    }

    private void muatReferensi(){
        isiKombo(cmbKelompokLayanan,rujukan.muatReferensi(SatuSehatRujukan.REF_KELOMPOK_LAYANAN),null);
        isiKombo(cmbSpesialisasi,rujukan.muatReferensi(SatuSehatRujukan.REF_SPESIALISASI),null);
        isiKombo(cmbTenaga,rujukan.muatReferensi(SatuSehatRujukan.REF_TENAGA),null);
    }

    /** Membaca data pasien dan memulihkan proses rujukan terakhir (bila ada). */
    private void muatData(){
        if(noRawat.equals("")){
            JOptionPane.showMessageDialog(this,"Dialog dibuka tanpa pasien. Buka dari IGD/Rawat Inap untuk rujuk keluar.");
            return;
        }
        jalankan("Membaca data pasien "+noRawat,()->{
            DataPasien hasil=rujukan.muatPasien(noRawat,kdDokter);
            Proses prosesTerakhir=rujukan.muatProsesTerakhir(noRawat);
            JsonNode taskPra=null,taskCari=null;
            List<Kandidat> kandidat=new ArrayList<>();
            if(prosesTerakhir!=null&&!prosesTerakhir.status.equals(SatuSehatRujukan.STATUS_BATAL)){
                if(!prosesTerakhir.idTaskPra.equals("")&&prosesTerakhir.idTaskKandidat.equals("")){
                    Respon respon=rujukan.kirim("GET","/Task/"+prosesTerakhir.idTaskPra,null);
                    if(respon.sukses()){
                        taskPra=respon.json;
                    }
                }
                if(!prosesTerakhir.idTaskKandidat.equals("")){
                    Respon respon=rujukan.kirim("GET","/Task/"+prosesTerakhir.idTaskKandidat,null);
                    if(respon.sukses()){
                        taskCari=respon.json;
                        kandidat.addAll(SatuSehatRujukan.bacaKandidat(taskCari));
                    }
                }
                gabungTask(kandidat,rujukan.muatTask(prosesTerakhir));
            }
            final JsonNode taskPraAkhir=taskPra,taskCariAkhir=taskCari;
            SwingUtilities.invokeLater(()->{
                data=hasil;
                proses=prosesTerakhir;
                taskKandidat=taskCariAkhir;
                daftarKandidat.clear();
                daftarKandidat.addAll(kandidat);
                daftarPendukung.clear();
                daftarPendukung.addAll(hasil.pendukung);
                if(proses!=null&&!proses.jenis.equals("")){
                    cmbJenis.setSelectedItem(proses.jenis);
                }
                tampilPasien();
                tampilPendukung();
                tampilKandidat();
                panelKuesioner.removeAll();
                formKriteria=null;
                formWilayah=null;
                if(taskPraAkhir!=null){
                    tampilKuesioner(taskPraAkhir);
                }
                panelKuesioner.revalidate();
                panelKuesioner.repaint();
                if(txtDeskripsi.getText().trim().equals("")&&hasil.diagnosaUtama()!=null){
                    txtDeskripsi.setText("Pasien dengan diagnosa "+hasil.diagnosaUtama().nama+" ("+hasil.diagnosaUtama().kode+"), membutuhkan penanganan lebih lanjut di faskes rujukan");
                }
                if(txtKeterangan.getText().trim().equals("")&&hasil.diagnosaUtama()!=null){
                    txtKeterangan.setText("Rujukan "+cmbJenis.getSelectedItem().toString().toLowerCase()+" untuk penanganan lebih lanjut pasien "+hasil.diagnosaUtama().nama);
                }
                if(txtRencana.getText().trim().equals("")){
                    txtRencana.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
                }
                txtNoRujukanBpjs.setText(hasil.noRujukanBpjs);
                if(proses!=null&&!proses.idTaskKandidat.equals("")){
                    tabLangkah.setSelectedIndex(proses.status.equals(SatuSehatRujukan.STATUS_DISETUJUI)||proses.status.equals(SatuSehatRujukan.STATUS_TERKIRIM)?2:1);
                }else{
                    tabLangkah.setSelectedIndex(0);
                }
            });
            log("Data pasien dimuat. Data pendukung SATUSEHAT : "+hasil.pendukung.size()+" item.");
        });
    }

    /** Menggabungkan Task persetujuan tersimpan ke daftar kandidat (berdasarkan ID organisasi). */
    private static void gabungTask(List<Kandidat> kandidat,List<Kandidat> task){
        for(Kandidat tersimpan:task){
            Kandidat cocok=null;
            for(Kandidat item:kandidat){
                if(item.idOrg.equals(tersimpan.idOrg)&&item.idTask.equals("")){
                    cocok=item;
                    break;
                }
            }
            if(cocok==null){
                kandidat.add(tersimpan);
            }else{
                cocok.idTask=tersimpan.idTask;
                cocok.idCarePlan=tersimpan.idCarePlan;
                cocok.status=tersimpan.status;
                cocok.respon=tersimpan.respon;
            }
        }
    }

    private void tampilPasien(){
        if(data==null){
            return;
        }
        TNoRw.setText(data.noRawat);
        TNoRM.setText(data.noRM);
        TPasien.setText(data.nama);
        TCaraBayar.setText(data.caraBayar);
        TIHSPasien.setText(data.idPasien.equals("")?"TIDAK DITEMUKAN":data.idPasien);
        TEncounter.setText(data.idEncounter.equals("")?"BELUM DIKIRIM":data.idEncounter);
        TDokter.setText(data.nmDokter);
        TIHSDokter.setText(data.idDokter.equals("")?"IHS tidak ditemukan":data.idDokter);
        StringBuilder diagnosa=new StringBuilder();
        for(Diagnosa item:data.diagnosa){
            if(diagnosa.length()>0){
                diagnosa.append(", ");
            }
            diagnosa.append(item.kode).append(" ").append(item.nama).append(item.idCondition.equals("")?" (Condition belum dikirim)":"");
        }
        TDiagnosa.setText(diagnosa.length()==0?"BELUM ADA":diagnosa.toString());
        TDiagnosa.setToolTipText(TDiagnosa.getText());
        TDiagnosa.setCaretPosition(0);
        tampilStatus();
    }

    private void tampilStatus(){
        if(proses==null){
            TStatus.setText("Belum ada proses rujukan. Mulai dari langkah 1 (Ambil Kriteria Rujukan).");
        }else{
            TStatus.setText(proses.status+" ("+proses.jenis+", "+proses.tglRujukan+")"+
                    (proses.noRujukanSatuSehat.equals("")?"":"   |   No.Rujukan SATUSEHAT : "+proses.noRujukanSatuSehat+" ke "+proses.nmOrgTujuan));
        }
        lblHasil.setText(proses==null||proses.idServiceRequest.equals("")?"":"No.Rujukan SATUSEHAT : "+proses.noRujukanSatuSehat);
    }

    private void tampilPendukung(){
        tabModePendukung.setRowCount(0);
        for(DataPendukung item:daftarPendukung){
            tabModePendukung.addRow(new Object[]{item.dipilih,item.keterangan,item.resource,item.id});
        }
    }

    private void tampilKandidat(){
        tabModeKandidat.setRowCount(0);
        cmbTujuan.removeAllItems();
        for(Kandidat item:daftarKandidat){
            tabModeKandidat.addRow(new Object[]{item.dipilih,item.nama,item.idOrg,item.jarak,item.waktu,item.strata,item.kodeBpjs,item.bed,
                item.idTask,teksStatus(item.status),teksRespon(item.respon)});
            if(item.respon.equals("accepted")&&!item.status.equals("cancelled")){
                cmbTujuan.addItem(item.nama+" ["+item.idOrg+"]");
            }
        }
    }

    private static String teksStatus(String status){
        switch (status) {
            case "requested":
                return "Menunggu";
            case "completed":
                return "Selesai";
            case "cancelled":
                return "Dibatalkan";
            default:
                return status;
        }
    }

    private static String teksRespon(String respon){
        switch (respon) {
            case "accepted":
                return "Diterima";
            case "rejected":
                return "Ditolak";
            default:
                return respon;
        }
    }

    private void rujukanBaru(){
        if(sibuk){
            JOptionPane.showMessageDialog(this,"Masih ada proses yang berjalan, silahkan tunggu...");
            return;
        }
        if(proses!=null&&!proses.status.equals(SatuSehatRujukan.STATUS_TERKIRIM)&&!proses.status.equals(SatuSehatRujukan.STATUS_BATAL)){
            if(JOptionPane.showConfirmDialog(this,"Proses rujukan sebelumnya ("+proses.status+") ditandai Dibatalkan. Lanjutkan?","Konfirmasi",
                    JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION){
                return;
            }
            proses.status=SatuSehatRujukan.STATUS_BATAL;
            rujukan.ubahProses(proses);
        }
        proses=null;
        taskKandidat=null;
        daftarKandidat.clear();
        tampilKandidat();
        panelKuesioner.removeAll();
        panelKuesioner.revalidate();
        panelKuesioner.repaint();
        formKriteria=null;
        formWilayah=null;
        tampilStatus();
        tabLangkah.setSelectedIndex(0);
        log("Siap untuk proses rujukan baru.");
    }

    // ===================================================================================
    // Langkah 1 : Pra Permintaan & kuesioner
    // ===================================================================================

    private void praPermintaan(){
        if(!dataSiap(false)){
            return;
        }
        if(proses!=null&&!proses.idTaskKandidat.equals("")&&!proses.status.equals(SatuSehatRujukan.STATUS_TERKIRIM)&&!proses.status.equals(SatuSehatRujukan.STATUS_BATAL)){
            JOptionPane.showMessageDialog(this,"Pencarian kandidat sudah dilakukan untuk proses ini. Klik Rujukan Baru bila ingin mengulang dari awal.");
            return;
        }
        String jenis=cmbJenis.getSelectedItem().toString();
        ObjectNode task=rujukan.bangunTaskPraPermintaan(data,jenis,SatuSehatRujukan.idLokal(data.noRawat,"PRA"));
        jalankan("Mengirim Task Pra Permintaan Rujukan",()->{
            Respon respon=rujukan.kirim("POST","/Task",task);
            if(!respon.sukses()){
                gagal("Pra Permintaan Rujukan",respon);
                return;
            }
            Proses baru=proses;
            if(baru==null||!baru.idTaskKandidat.equals("")||baru.status.equals(SatuSehatRujukan.STATUS_TERKIRIM)||baru.status.equals(SatuSehatRujukan.STATUS_BATAL)){
                baru=new Proses();
                baru.noRawat=data.noRawat;
                baru.tglRujukan=sekarangDb();
                baru.jenis=jenis;
                baru.kdDokter=data.kdDokter;
                baru.status=SatuSehatRujukan.STATUS_PRA;
                baru.idTaskPra=respon.json.path("id").asText();
                if(!rujukan.simpanProses(baru)){
                    pesan("Task Pra Permintaan terkirim ("+baru.idTaskPra+") tetapi gagal disimpan ke database.");
                }
            }else{
                baru.jenis=jenis;
                baru.idTaskPra=respon.json.path("id").asText();
                rujukan.ubahProses(baru);
            }
            final Proses akhir=baru;
            SwingUtilities.invokeLater(()->{
                proses=akhir;
                panelKuesioner.removeAll();
                tampilKuesioner(respon.json);
                panelKuesioner.revalidate();
                panelKuesioner.repaint();
                tampilStatus();
            });
            log("Pra Permintaan terkirim, ID Task : "+baru.idTaskPra);
        });
    }

    /** Membangun form dari Questionnaire contained balasan Pra Permintaan. */
    private void tampilKuesioner(JsonNode task){
        String idKriteria="",idWilayah="";
        for(JsonNode output:task.path("output")){
            String kode=SatuSehatRujukan.kodePertama(output.path("type"));
            String ref=output.path("valueReference").path("reference").asText().replace("#","");
            if(kode.equals("referral-criteria")){
                idKriteria=ref;
            }else if(kode.equals("referral-network-area")||kode.equals("area")){
                idWilayah=ref;
            }
        }
        JsonNode kriteria=null,wilayah=null;
        for(JsonNode contained:task.path("contained")){
            if(!contained.path("resourceType").asText().equals("Questionnaire")){
                continue;
            }
            String id=contained.path("id").asText();
            String judul=contained.path("title").asText().toLowerCase();
            if(id.equals(idKriteria)||(idKriteria.equals("")&&judul.contains("kriteria"))){
                kriteria=contained;
            }else if(id.equals(idWilayah)||judul.contains("wilayah")){
                wilayah=contained;
            }
        }
        formKriteria=null;
        formWilayah=null;
        if(kriteria==null){
            widget.Label kosong=label("SATUSEHAT tidak mengirim kuesioner kriteria rujukan. Periksa diagnosa dan jenis rujukan, lalu ulangi.",800);
            kosong.setForeground(Color.RED);
            panelKuesioner.add(kosong);
            return;
        }
        formKriteria=new FormKuesioner(kriteria);
        panelKuesioner.add(formKriteria.panel);
        if(wilayah==null){
            wilayah=wilayahBawaan();
            log("SATUSEHAT tidak mengirim kuesioner wilayah, dipakai wilayah RS dari setting.");
        }
        formWilayah=new FormKuesioner(wilayah);
        panelKuesioner.add(formWilayah.panel);
    }

    /** Kuesioner wilayah cadangan dari PROPINSISATUSEHAT/KABUPATENSATUSEHAT bila server tidak mengirimnya. */
    private JsonNode wilayahBawaan(){
        ObjectNode questionnaire=rujukan.getMapper().createObjectNode();
        questionnaire.put("resourceType","Questionnaire");
        questionnaire.put("title","Jejaring Wilayah Rujukan");
        ObjectNode grup=questionnaire.putArray("item").addObject();
        grup.put("linkId","1");
        grup.put("text","Jejaring wilayah rujukan");
        grup.put("type","group");
        ArrayNode isi=grup.putArray("item");
        fungsi.sekuel sequel=new fungsi.sekuel();
        String[][] wilayah={{"1.1","Provinsi",koneksiDB.PROPINSISATUSEHAT()},
                            {"1.2","Kabupaten/Kota",koneksiDB.KABUPATENSATUSEHAT()}};
        String namaProvinsi=sequel.cariIsi("select setting.propinsi from setting");
        String namaKabupaten=sequel.cariIsi("select setting.kabupaten from setting");
        for(int i=0;i<wilayah.length;i++){
            ObjectNode item=isi.addObject();
            item.put("linkId",wilayah[i][0]);
            item.put("required",true);
            item.put("text",wilayah[i][1]);
            item.put("type","choice");
            ObjectNode coding=item.putArray("answerOption").addObject().putObject("valueCoding");
            coding.put("code",wilayah[i][2]==null?"":wilayah[i][2]);
            coding.put("display",i==0?namaProvinsi:namaKabupaten);
            coding.put("system","http://sys-ids.kemkes.go.id/administrative-area");
        }
        return questionnaire;
    }

    // ===================================================================================
    // Langkah 1b : Pencarian kandidat
    // ===================================================================================

    private void cariKandidat(){
        if(!dataSiap(false)){
            return;
        }
        if(proses==null||proses.idTaskPra.equals("")||formKriteria==null){
            JOptionPane.showMessageDialog(this,"Klik Ambil Kriteria Rujukan terlebih dahulu.");
            return;
        }
        if(!proses.idTaskKandidat.equals("")){
            if(JOptionPane.showConfirmDialog(this,"Pencarian kandidat sudah pernah dilakukan. Cari ulang dengan isian sekarang?","Konfirmasi",
                    JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION){
                return;
            }
        }
        String salah=formKriteria.validasi();
        if(salah.equals("")&&formWilayah!=null){
            salah=formWilayah.validasi();
        }
        if(!salah.equals("")){
            JOptionPane.showMessageDialog(this,salah);
            return;
        }
        String teksKelompok=teksKombo(cmbKelompokLayanan);
        String[] kelompok=SatuSehatRujukan.pisahKode(teksKelompok);
        if(!teksKelompok.equals("")&&kelompok==null){
            JOptionPane.showMessageDialog(this,"Format Kelompok Layanan harus KODE - Nama.");
            return;
        }
        String jenis=cmbJenis.getSelectedItem().toString();
        ArrayNode jawabanWilayah=formWilayah==null?rujukan.getMapper().createArrayNode():formWilayah.jawaban();
        ObjectNode task=rujukan.bangunTaskKandidat(data,jenis,SatuSehatRujukan.idLokal(data.noRawat,"KDT"),formKriteria.jawaban(),jawabanWilayah,kelompok);
        jalankan("Mencari kandidat faskes rujukan",()->{
            Respon respon=rujukan.kirim("POST","/Task",task);
            if(!respon.sukses()){
                gagal("Pencarian Kandidat Faskes",respon);
                return;
            }
            rujukan.simpanReferensi(SatuSehatRujukan.REF_KELOMPOK_LAYANAN,kelompok);
            proses.jenis=jenis;
            proses.idTaskKandidat=respon.json.path("id").asText();
            proses.status=SatuSehatRujukan.STATUS_KANDIDAT;
            rujukan.ubahProses(proses);
            List<Kandidat> kandidat=SatuSehatRujukan.bacaKandidat(respon.json);
            gabungTask(kandidat,rujukan.muatTask(proses));
            SwingUtilities.invokeLater(()->{
                taskKandidat=respon.json;
                daftarKandidat.clear();
                daftarKandidat.addAll(kandidat);
                tampilKandidat();
                tampilStatus();
                tabLangkah.setSelectedIndex(1);
            });
            if(kandidat.isEmpty()){
                pesan("Task pencarian kandidat terkirim, tetapi SATUSEHAT belum mengirim kandidat faskes.\n"+
                        "Klik Muat Ulang Kandidat beberapa saat lagi, atau tambah faskes lewat pencarian nama.");
            }else{
                log("Ditemukan "+kandidat.size()+" kandidat faskes rujukan.");
            }
        });
    }

    private void muatUlangKandidat(){
        if(proses==null||proses.idTaskKandidat.equals("")){
            JOptionPane.showMessageDialog(this,"Belum ada pencarian kandidat.");
            return;
        }
        jalankan("Membaca ulang kandidat faskes",()->{
            Respon respon=rujukan.kirim("GET","/Task/"+proses.idTaskKandidat,null);
            if(!respon.sukses()){
                gagal("Membaca Task kandidat",respon);
                return;
            }
            List<Kandidat> kandidat=SatuSehatRujukan.bacaKandidat(respon.json);
            gabungTask(kandidat,rujukan.muatTask(proses));
            SwingUtilities.invokeLater(()->{
                taskKandidat=respon.json;
                // faskes tambahan manual yang belum dikirim tetap dipertahankan
                for(Kandidat lama:daftarKandidat){
                    boolean ada=false;
                    for(Kandidat item:kandidat){
                        if(item.idOrg.equals(lama.idOrg)){
                            ada=true;
                            break;
                        }
                    }
                    if(!ada){
                        kandidat.add(lama);
                    }
                }
                daftarKandidat.clear();
                daftarKandidat.addAll(kandidat);
                tampilKandidat();
            });
            log("Kandidat faskes : "+kandidat.size());
        });
    }

    private void cariFaskes(){
        String nama=txtCariFaskes.getText().trim();
        if(nama.length()<3){
            JOptionPane.showMessageDialog(this,"Isi nama faskes minimal 3 huruf.");
            return;
        }
        jalankan("Mencari faskes "+nama,()->{
            Respon respon;
            try {
                respon=rujukan.kirim("GET","/Organization?name="+URLEncoder.encode(nama,"UTF-8"),null);
            } catch (Exception e) {
                pesan("Nama faskes tidak valid.");
                return;
            }
            if(!respon.sukses()){
                gagal("Pencarian faskes",respon);
                return;
            }
            List<Kandidat> hasil=new ArrayList<>();
            for(JsonNode entry:respon.json.path("entry")){
                JsonNode org=entry.path("resource");
                if(!org.path("resourceType").asText().equals("Organization")||org.path("partOf").has("reference")){
                    continue;
                }
                Kandidat kandidat=new Kandidat();
                kandidat.idOrg=org.path("id").asText();
                kandidat.nama=org.path("name").asText();
                kandidat.strata="(manual)";
                hasil.add(kandidat);
            }
            if(hasil.isEmpty()){
                pesan("Faskes dengan nama \""+nama+"\" tidak ditemukan.");
                return;
            }
            SwingUtilities.invokeLater(()->{
                List<String> pilihan=new ArrayList<>();
                for(Kandidat item:hasil){
                    pilihan.add(item.nama+" ["+item.idOrg+"]");
                }
                Object dipilih=JOptionPane.showInputDialog(this,"Pilih faskes yang ditambahkan ke daftar kandidat :","Tambah Faskes",
                        JOptionPane.QUESTION_MESSAGE,null,pilihan.toArray(),pilihan.get(0));
                if(dipilih==null){
                    return;
                }
                Kandidat tambah=hasil.get(pilihan.indexOf(dipilih.toString()));
                for(Kandidat item:daftarKandidat){
                    if(item.idOrg.equals(tambah.idOrg)&&item.idTask.equals("")){
                        JOptionPane.showMessageDialog(this,"Faskes sudah ada di daftar.");
                        return;
                    }
                }
                tambah.dipilih=true;
                daftarKandidat.add(tambah);
                tampilKandidat();
            });
        });
    }

    /** Menyalin centang tabel kandidat ke daftarKandidat. */
    private void bacaCentangKandidat(){
        for(int i=0;i<tabModeKandidat.getRowCount()&&i<daftarKandidat.size();i++){
            daftarKandidat.get(i).dipilih=Boolean.TRUE.equals(tabModeKandidat.getValueAt(i,0));
        }
    }

    private List<DataPendukung> pendukungDipilih(){
        List<DataPendukung> hasil=new ArrayList<>();
        for(int i=0;i<tabModePendukung.getRowCount()&&i<daftarPendukung.size();i++){
            DataPendukung item=daftarPendukung.get(i);
            item.dipilih=Boolean.TRUE.equals(tabModePendukung.getValueAt(i,0));
            if(item.dipilih){
                hasil.add(item);
            }
        }
        return hasil;
    }

    // ===================================================================================
    // Langkah 2 : Permintaan persetujuan
    // ===================================================================================

    private void kirimPersetujuan(){
        if(!dataSiap(true)){
            return;
        }
        if(proses==null||proses.idTaskKandidat.equals("")){
            JOptionPane.showMessageDialog(this,"Lakukan pencarian kandidat faskes terlebih dahulu (langkah 1).");
            return;
        }
        bacaCentangKandidat();
        List<Kandidat> tujuan=new ArrayList<>();
        for(Kandidat item:daftarKandidat){
            if(item.dipilih&&item.idTask.equals("")){
                tujuan.add(item);
            }
        }
        if(tujuan.isEmpty()){
            JOptionPane.showMessageDialog(this,"Centang minimal satu faskes yang belum dikirimi permintaan persetujuan.");
            return;
        }
        String[] spesialisasi=SatuSehatRujukan.pisahKode(teksKombo(cmbSpesialisasi));
        if(spesialisasi==null){
            JOptionPane.showMessageDialog(this,"Layanan Diminta wajib diisi dengan format KODE - Nama.");
            return;
        }
        if(txtJudul.getText().trim().equals("")||txtDeskripsi.getText().trim().equals("")){
            JOptionPane.showMessageDialog(this,"Judul dan Alasan/Deskripsi rencana rujukan wajib diisi.");
            return;
        }
        if(data.diagnosaUtama().idCondition.equals("")){
            if(JOptionPane.showConfirmDialog(this,"Diagnosa utama belum dikirim sebagai Condition SATUSEHAT, sehingga Task dikirim tanpa reasonReference. Lanjutkan?",
                    "Konfirmasi",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION){
                return;
            }
        }
        String jenis=proses.jenis.equals("")?cmbJenis.getSelectedItem().toString():proses.jenis;
        ObjectNode bundle=rujukan.bangunBundlePersetujuan(data,jenis,tujuan,txtJudul.getText().trim(),txtDeskripsi.getText().trim(),
                spesialisasi,pendukungDipilih(),taskKandidat,proses.idTaskKandidat);
        jalankan("Mengirim permintaan persetujuan ke "+tujuan.size()+" faskes",()->{
            Respon respon=rujukan.kirim("POST","",bundle);
            if(!respon.sukses()){
                gagal("Permintaan persetujuan",respon);
                return;
            }
            List<String[]> hasil=SatuSehatRujukan.bacaHasilBundle(respon.json);
            if(hasil.size()<tujuan.size()+1){
                pesan("Balasan Bundle tidak lengkap, periksa log.\n"+respon.pesan());
                return;
            }
            String idCarePlan=hasil.get(tujuan.size())[1];
            for(int i=0;i<tujuan.size();i++){
                Kandidat item=tujuan.get(i);
                item.idTask=hasil.get(i)[1];
                item.idCarePlan=idCarePlan;
                item.status="requested";
                item.respon="";
                item.dipilih=false;
                rujukan.simpanTask(proses,item);
            }
            proses.idCarePlan=idCarePlan;
            if(hasil.size()>tujuan.size()+1){
                proses.idTaskResponKandidat=hasil.get(tujuan.size()+1)[1];
            }
            proses.status=SatuSehatRujukan.STATUS_MENUNGGU;
            rujukan.ubahProses(proses);
            rujukan.simpanReferensi(SatuSehatRujukan.REF_SPESIALISASI,spesialisasi);
            SwingUtilities.invokeLater(()->{
                tampilKandidat();
                tampilStatus();
            });
            pesan("Permintaan persetujuan terkirim ke "+tujuan.size()+" faskes. Klik Cek Status Persetujuan secara berkala untuk melihat respon.");
        });
    }

    private void cekStatusPersetujuan(){
        List<Kandidat> task=new ArrayList<>();
        for(Kandidat item:daftarKandidat){
            if(!item.idTask.equals("")){
                task.add(item);
            }
        }
        if(task.isEmpty()){
            JOptionPane.showMessageDialog(this,"Belum ada permintaan persetujuan yang dikirim.");
            return;
        }
        jalankan("Membaca status persetujuan "+task.size()+" faskes",()->{
            int diterima=0;
            for(Kandidat item:task){
                Respon respon=rujukan.kirim("GET","/Task/"+item.idTask,null);
                if(!respon.sukses()){
                    log("Gagal membaca Task "+item.idTask+" : "+respon.pesan());
                    continue;
                }
                item.status=respon.json.path("status").asText();
                item.respon=SatuSehatRujukan.bacaRespon(respon.json);
                rujukan.ubahTask(item);
                if(item.respon.equals("accepted")&&!item.status.equals("cancelled")){
                    diterima++;
                }
            }
            if(diterima>0&&proses!=null&&!proses.status.equals(SatuSehatRujukan.STATUS_TERKIRIM)){
                proses.status=SatuSehatRujukan.STATUS_DISETUJUI;
                rujukan.ubahProses(proses);
            }
            final int jumlah=diterima;
            SwingUtilities.invokeLater(()->{
                tampilKandidat();
                tampilStatus();
                if(jumlah>0){
                    tabLangkah.setSelectedIndex(2);
                }
            });
            log(jumlah>0?jumlah+" faskes menerima. Lanjutkan ke langkah 3 Kirim Rujukan.":"Belum ada faskes yang menerima.");
        });
    }

    private void batalkanTask(){
        bacaCentangKandidat();
        List<Kandidat> batal=new ArrayList<>();
        for(Kandidat item:daftarKandidat){
            if(item.dipilih&&!item.idTask.equals("")&&!item.status.equals("cancelled")){
                batal.add(item);
            }
        }
        if(batal.isEmpty()){
            JOptionPane.showMessageDialog(this,"Centang Task persetujuan yang akan dibatalkan.");
            return;
        }
        if(JOptionPane.showConfirmDialog(this,"Batalkan "+batal.size()+" permintaan persetujuan?","Konfirmasi",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION){
            return;
        }
        jalankan("Membatalkan "+batal.size()+" Task persetujuan",()->{
            batalkan(batal);
            SwingUtilities.invokeLater(this::tampilKandidat);
        });
    }

    /** PATCH status cancelled, dipanggil dari thread background. */
    private void batalkan(List<Kandidat> batal){
        for(Kandidat item:batal){
            Respon respon=rujukan.kirim("PATCH","/Task/"+item.idTask,rujukan.bangunPatchBatal());
            if(respon.sukses()){
                item.status="cancelled";
                item.dipilih=false;
                rujukan.ubahTask(item);
                log("Task "+item.nama+" dibatalkan.");
            }else{
                log("Gagal membatalkan Task "+item.nama+" : "+respon.pesan());
            }
        }
    }

    // ===================================================================================
    // Langkah 3 : ServiceRequest rujukan
    // ===================================================================================

    private void kirimRujukan(){
        if(!dataSiap(false)){
            return;
        }
        if(proses==null){
            JOptionPane.showMessageDialog(this,"Belum ada proses rujukan.");
            return;
        }
        if(!proses.idServiceRequest.equals("")){
            JOptionPane.showMessageDialog(this,"Rujukan sudah terkirim dengan No.Rujukan SATUSEHAT "+proses.noRujukanSatuSehat+". Klik Rujukan Baru untuk rujukan lain.");
            return;
        }
        Kandidat tujuan=null;
        String pilihan=cmbTujuan.getSelectedItem()==null?"":cmbTujuan.getSelectedItem().toString();
        for(Kandidat item:daftarKandidat){
            if(item.respon.equals("accepted")&&!item.status.equals("cancelled")&&pilihan.equals(item.nama+" ["+item.idOrg+"]")){
                tujuan=item;
                break;
            }
        }
        if(tujuan==null){
            JOptionPane.showMessageDialog(this,"Belum ada faskes tujuan yang menerima. Klik Cek Status Persetujuan di langkah 2.");
            return;
        }
        String[] tenaga=SatuSehatRujukan.pisahKode(teksKombo(cmbTenaga));
        if(tenaga==null){
            JOptionPane.showMessageDialog(this,"Dokter Dituju wajib diisi dengan format KODE - Nama.");
            return;
        }
        String rencana;
        try {
            rencana=SatuSehatRujukan.waktuFhir(txtRencana.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,"Format Rencana Tgl/Jam harus yyyy-MM-dd HH:mm.");
            return;
        }
        if(txtKeterangan.getText().trim().equals("")){
            JOptionPane.showMessageDialog(this,"Keterangan rujukan wajib diisi.");
            return;
        }
        String instruksi=txtInstruksi.getText().trim().equals("")?"Rujukan ke "+tujuan.nama:txtInstruksi.getText().trim();
        String jenis=proses.jenis.equals("")?cmbJenis.getSelectedItem().toString():proses.jenis;
        ObjectNode sr=rujukan.bangunServiceRequest(data,jenis,proses,tujuan,tenaga,rencana,txtKeterangan.getText().trim(),
                txtNoRujukanBpjs.getText(),instruksi,pendukungDipilih());
        final Kandidat faskes=tujuan;
        jalankan("Mengirim rujukan ke "+tujuan.nama,()->{
            Respon respon=rujukan.kirim("POST","/ServiceRequest",sr);
            if(!respon.sukses()){
                gagal("Kirim Rujukan",respon);
                return;
            }
            proses.idServiceRequest=respon.json.path("id").asText();
            proses.noRujukanSatuSehat=SatuSehatRujukan.bacaIdentifier(respon.json,SatuSehatRujukan.SYS_NO_RUJUKAN_SATUSEHAT);
            if(proses.noRujukanSatuSehat.equals("")){
                Respon ulang=rujukan.kirim("GET","/ServiceRequest/"+proses.idServiceRequest,null);
                if(ulang.sukses()){
                    proses.noRujukanSatuSehat=SatuSehatRujukan.bacaIdentifier(ulang.json,SatuSehatRujukan.SYS_NO_RUJUKAN_SATUSEHAT);
                }
            }
            proses.idOrgTujuan=faskes.idOrg;
            proses.nmOrgTujuan=faskes.nama;
            proses.status=SatuSehatRujukan.STATUS_TERKIRIM;
            rujukan.ubahProses(proses);
            rujukan.simpanReferensi(SatuSehatRujukan.REF_TENAGA,tenaga);
            SwingUtilities.invokeLater(this::tampilStatus);
            pesan("Rujukan terkirim ke "+faskes.nama+".\nNo.Rujukan SATUSEHAT : "+
                    (proses.noRujukanSatuSehat.equals("")?"(belum diberikan)":proses.noRujukanSatuSehat)+"\nID ServiceRequest : "+proses.idServiceRequest);

            List<Kandidat> sisa=new ArrayList<>();
            for(Kandidat item:daftarKandidat){
                if(item!=faskes&&!item.idTask.equals("")&&item.status.equals("requested")){
                    sisa.add(item);
                }
            }
            if(!sisa.isEmpty()){
                SwingUtilities.invokeLater(()->{
                    if(JOptionPane.showConfirmDialog(this,"Batalkan "+sisa.size()+" permintaan persetujuan ke faskes lain yang belum merespon?",
                            "Konfirmasi",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                        jalankan("Membatalkan permintaan ke faskes lain",()->{
                            batalkan(sisa);
                            SwingUtilities.invokeLater(this::tampilKandidat);
                        });
                    }
                });
            }
        });
    }

    // ===================================================================================
    // Rujukan masuk (RS sebagai faskes rujukan)
    // ===================================================================================

    private void tampilTaskMasuk(){
        if(rujukan.getIdOrg().equals("")){
            JOptionPane.showMessageDialog(this,"ID Organisasi SATUSEHAT belum diatur.");
            return;
        }
        boolean menunggu=cmbFilterMasuk.getSelectedIndex()==0;
        jalankan("Membaca permintaan rujukan masuk",()->{
            Respon respon=rujukan.kirim("GET","/Task?owner="+rujukan.getIdOrg()+"&code=referral-approval-request"+
                    (menunggu?"&status=requested":"")+"&_include=Task:based-on",null);
            if(!respon.sukses()){
                gagal("Membaca rujukan masuk",respon);
                return;
            }
            List<JsonNode> task=new ArrayList<>();
            Map<String,JsonNode> careplan=new LinkedHashMap<>();
            for(JsonNode entry:respon.json.path("entry")){
                JsonNode resource=entry.path("resource");
                String tipe=resource.path("resourceType").asText();
                if(tipe.equals("Task")){
                    if(!menunggu||resource.path("status").asText().equals("requested")){
                        task.add(resource);
                    }
                }else if(tipe.equals("CarePlan")){
                    careplan.put(resource.path("id").asText(),resource);
                }
            }
            List<Object[]> baris=new ArrayList<>();
            for(JsonNode item:task){
                JsonNode cp=careplan.get(SatuSehatRujukan.idDariReferensi(item.path("basedOn").path(0).path("reference").asText()));
                String idPerujuk=SatuSehatRujukan.idDariReferensi(item.path("requester").path("reference").asText());
                String namaPerujuk=rujukan.namaOrganisasi(idPerujuk);
                baris.add(new Object[]{
                    item.path("id").asText(),
                    item.path("authoredOn").asText().replace("T"," "),
                    namaPerujuk.equals("")?idPerujuk:namaPerujuk+" ["+idPerujuk+"]",
                    SatuSehatRujukan.idDariReferensi(item.path("for").path("reference").asText()),
                    cp==null?"":cp.path("subject").path("display").asText(),
                    SatuSehatRujukan.idDariReferensi(item.path("reasonReference").path("reference").asText()),
                    cp==null?"":cp.path("title").asText()+" : "+cp.path("description").asText(),
                    cp==null?"":cp.path("activity").path(0).path("detail").path("code").path("coding").path(0).path("display").asText(),
                    teksStatus(item.path("status").asText()),
                    teksRespon(SatuSehatRujukan.bacaRespon(item))
                });
            }
            SwingUtilities.invokeLater(()->{
                daftarTaskMasuk.clear();
                daftarTaskMasuk.addAll(task);
                carePlanMasuk.clear();
                carePlanMasuk.putAll(careplan);
                tabModeMasuk.setRowCount(0);
                for(Object[] item:baris){
                    tabModeMasuk.addRow(item);
                }
                txtDetailMasuk.setText("");
            });
            log("Permintaan rujukan masuk : "+task.size());
        });
    }

    private void tampilDetailTaskMasuk(){
        int baris=tbMasuk.getSelectedRow();
        if(baris<0||baris>=daftarTaskMasuk.size()){
            return;
        }
        JsonNode task=daftarTaskMasuk.get(baris);
        JsonNode cp=carePlanMasuk.get(SatuSehatRujukan.idDariReferensi(task.path("basedOn").path(0).path("reference").asText()));
        StringBuilder teks=new StringBuilder();
        teks.append("Task Persetujuan : ").append(task.path("id").asText()).append("\n");
        teks.append("Faskes Perujuk   : ").append(tabModeMasuk.getValueAt(baris,2)).append("\n");
        teks.append("Pasien           : ").append(tabModeMasuk.getValueAt(baris,4)).append(" (").append(tabModeMasuk.getValueAt(baris,3)).append(")\n");
        teks.append("Encounter Perujuk: ").append(SatuSehatRujukan.idDariReferensi(task.path("encounter").path("reference").asText())).append("\n");
        teks.append("Status / Respon  : ").append(tabModeMasuk.getValueAt(baris,8)).append(" / ").append(tabModeMasuk.getValueAt(baris,9)).append("\n");
        if(cp!=null){
            teks.append("\nRencana Rujukan (CarePlan ").append(cp.path("id").asText()).append(")\n");
            teks.append(cp.path("title").asText()).append("\n").append(cp.path("description").asText()).append("\n");
            teks.append("Layanan diminta  : ").append(tabModeMasuk.getValueAt(baris,7)).append("\n");
            teks.append("Dokter perujuk   : ").append(cp.path("author").path("display").asText()).append("\n");
        }
        teks.append("\nSetelah perujuk mengirim rujukan (ServiceRequest), cari dengan No.Rujukan SATUSEHAT di atas untuk melihat data pendukung.");
        txtDetailMasuk.setText(teks.toString());
        txtDetailMasuk.setCaretPosition(0);
    }

    private void responTaskMasuk(boolean terima){
        int baris=tbMasuk.getSelectedRow();
        if(baris<0||baris>=daftarTaskMasuk.size()){
            JOptionPane.showMessageDialog(this,"Pilih permintaan rujukan pada tabel.");
            return;
        }
        JsonNode task=daftarTaskMasuk.get(baris);
        if(!task.path("status").asText().equals("requested")){
            JOptionPane.showMessageDialog(this,"Permintaan ini sudah "+teksStatus(task.path("status").asText())+".");
            return;
        }
        if(JOptionPane.showConfirmDialog(this,(terima?"TERIMA":"TOLAK")+" rujukan pasien "+tabModeMasuk.getValueAt(baris,4)+" dari "+tabModeMasuk.getValueAt(baris,2)+"?",
                "Konfirmasi",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION){
            return;
        }
        String idTask=task.path("id").asText();
        jalankan((terima?"Menerima":"Menolak")+" rujukan "+idTask,()->{
            Respon respon=rujukan.kirim("PATCH","/Task/"+idTask,rujukan.bangunPatchRespon(terima));
            if(!respon.sukses()){
                gagal(terima?"Terima rujukan":"Tolak rujukan",respon);
                return;
            }
            SwingUtilities.invokeLater(()->{
                if(baris<daftarTaskMasuk.size()&&daftarTaskMasuk.get(baris).path("id").asText().equals(idTask)){
                    daftarTaskMasuk.set(baris,respon.json);
                    tabModeMasuk.setValueAt(teksStatus(respon.json.path("status").asText()),baris,8);
                    tabModeMasuk.setValueAt(teksRespon(SatuSehatRujukan.bacaRespon(respon.json)),baris,9);
                }
            });
            pesan("Rujukan "+(terima?"diterima":"ditolak")+". Faskes perujuk akan melihat respon ini.");
        });
    }

    private void cariServiceRequest(){
        String nilai=txtCariSR.getText().trim();
        if(nilai.equals("")){
            JOptionPane.showMessageDialog(this,"Isi nomor rujukan / ID ServiceRequest.");
            return;
        }
        int mode=cmbCariSR.getSelectedIndex();
        jalankan("Mencari rujukan "+nilai,()->{
            Respon respon;
            JsonNode sr=null;
            try {
                if(mode==2){
                    respon=rujukan.kirim("GET","/ServiceRequest/"+URLEncoder.encode(nilai,"UTF-8"),null);
                    if(respon.sukses()&&respon.json.path("resourceType").asText().equals("ServiceRequest")){
                        sr=respon.json;
                    }
                }else{
                    String system=mode==0?SatuSehatRujukan.SYS_NO_RUJUKAN_SATUSEHAT:SatuSehatRujukan.SYS_NO_RUJUKAN_PCARE;
                    respon=rujukan.kirim("GET","/ServiceRequest?identifier="+system+"|"+URLEncoder.encode(nilai,"UTF-8"),null);
                    if(respon.sukses()){
                        for(JsonNode entry:respon.json.path("entry")){
                            if(entry.path("resource").path("resourceType").asText().equals("ServiceRequest")){
                                sr=entry.path("resource");
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                pesan("Nomor tidak valid.");
                return;
            }
            if(!respon.sukses()){
                gagal("Pencarian rujukan",respon);
                return;
            }
            if(sr==null){
                pesan("Rujukan "+nilai+" tidak ditemukan.");
                return;
            }
            String idSR=sr.path("id").asText();
            String idPasien=SatuSehatRujukan.idDariReferensi(sr.path("subject").path("reference").asText());
            List<JsonNode> pendukung=new ArrayList<>();
            Respon lengkap=rujukan.kirim("GET","/ServiceRequest?_id="+idSR+"&subject="+idPasien+"&_include=ServiceRequest:supportingInfo",null);
            if(lengkap.sukses()){
                for(JsonNode entry:lengkap.json.path("entry")){
                    if(!entry.path("resource").path("resourceType").asText().equals("ServiceRequest")){
                        pendukung.add(entry.path("resource"));
                    }
                }
            }else{
                log("Data pendukung tidak bisa dibaca : "+lengkap.pesan());
            }
            String teks=ringkasServiceRequest(sr,pendukung);
            final JsonNode hasil=sr;
            SwingUtilities.invokeLater(()->{
                serviceRequestMasuk=hasil;
                txtDetailMasuk.setText(teks);
                txtDetailMasuk.setCaretPosition(0);
            });
            log("Rujukan ditemukan, ID ServiceRequest : "+idSR);
        });
    }

    private String ringkasServiceRequest(JsonNode sr,List<JsonNode> pendukung){
        StringBuilder teks=new StringBuilder();
        teks.append("RUJUKAN (ServiceRequest ").append(sr.path("id").asText()).append(")\n");
        teks.append("No.Rujukan SATUSEHAT : ").append(SatuSehatRujukan.bacaIdentifier(sr,SatuSehatRujukan.SYS_NO_RUJUKAN_SATUSEHAT)).append("\n");
        String pcare=SatuSehatRujukan.bacaIdentifier(sr,SatuSehatRujukan.SYS_NO_RUJUKAN_PCARE);
        if(!pcare.equals("")){
            teks.append("No.Rujukan BPJS      : ").append(pcare).append("\n");
        }
        teks.append("Status               : ").append(sr.path("status").asText()).append(" / prioritas ").append(sr.path("priority").asText()).append("\n");
        teks.append("Faskes Perujuk       : ").append(sr.path("requester").path("display").asText()).append(" [")
                .append(SatuSehatRujukan.idDariReferensi(sr.path("requester").path("reference").asText())).append("]\n");
        teks.append("Faskes Tujuan        : ").append(sr.path("performer").path(0).path("display").asText()).append(" [")
                .append(SatuSehatRujukan.idDariReferensi(sr.path("performer").path(0).path("reference").asText())).append("]\n");
        teks.append("Pasien (IHS)         : ").append(SatuSehatRujukan.idDariReferensi(sr.path("subject").path("reference").asText())).append("\n");
        teks.append("Jenis                : ").append(sr.path("code").path("coding").path(0).path("display").asText()).append("\n");
        teks.append("Keterangan           : ").append(sr.path("code").path("text").asText()).append("\n");
        teks.append("Dokter dituju        : ").append(sr.path("performerType").path("coding").path(0).path("display").asText()).append("\n");
        teks.append("Rencana              : ").append(sr.path("occurrenceDateTime").asText().replace("T"," ")).append("\n");
        teks.append("Instruksi            : ").append(sr.path("patientInstruction").asText()).append("\n");
        teks.append("\nDATA PENDUKUNG (").append(pendukung.size()).append(")\n");
        for(JsonNode item:pendukung){
            teks.append("- ").append(SatuSehatRujukan.ringkas(item)).append("\n");
        }
        if(pendukung.isEmpty()){
            for(JsonNode ref:sr.path("supportingInfo")){
                teks.append("- ").append(ref.path("display").asText()).append(" : ").append(ref.path("reference").asText()).append("\n");
            }
        }
        return teks.toString();
    }

    private void hubungkanKunjungan(){
        if(serviceRequestMasuk==null){
            JOptionPane.showMessageDialog(this,"Cari rujukan (ServiceRequest) terlebih dahulu.");
            return;
        }
        if(noRawat.equals("")||data==null){
            JOptionPane.showMessageDialog(this,"Buka dialog ini dari kunjungan pasien (IGD/Rawat Inap) yang menerima rujukan.");
            return;
        }
        if(data.idEncounter.equals("")){
            JOptionPane.showMessageDialog(this,"Encounter kunjungan "+noRawat+" belum dikirim ke SATUSEHAT. Kirim dulu lewat menu SATUSEHAT Kirim Encounter.");
            return;
        }
        JsonNode sr=serviceRequestMasuk;
        String idPasienRujukan=SatuSehatRujukan.idDariReferensi(sr.path("subject").path("reference").asText());
        String idTujuan=SatuSehatRujukan.idDariReferensi(sr.path("performer").path(0).path("reference").asText());
        if(!idTujuan.equals("")&&!idTujuan.equals(rujukan.getIdOrg())){
            JOptionPane.showMessageDialog(this,"Rujukan ini ditujukan ke faskes lain ("+idTujuan+").");
            return;
        }
        if(!data.idPasien.equals(idPasienRujukan)){
            if(JOptionPane.showConfirmDialog(this,"IHS pasien rujukan ("+idPasienRujukan+") berbeda dengan pasien kunjungan ini ("+
                    (data.idPasien.equals("")?"tidak ditemukan":data.idPasien)+"). Tetap hubungkan?","Konfirmasi",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION){
                return;
            }
        }else if(JOptionPane.showConfirmDialog(this,"Hubungkan rujukan "+SatuSehatRujukan.bacaIdentifier(sr,SatuSehatRujukan.SYS_NO_RUJUKAN_SATUSEHAT)+
                " ke kunjungan "+noRawat+"?","Konfirmasi",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION){
            return;
        }
        String idSR=sr.path("id").asText();
        String idEncounter=data.idEncounter;
        jalankan("Menghubungkan rujukan ke Encounter "+idEncounter,()->{
            Respon respon=rujukan.kirim("PATCH","/Encounter/"+idEncounter,rujukan.bangunPatchEncounterRujukan(idSR));
            if(!respon.sukses()){
                gagal("Hubungkan rujukan ke kunjungan",respon);
                return;
            }
            String idTask="";
            for(JsonNode ref:sr.path("supportingInfo")){
                if(ref.path("reference").asText().startsWith("Task/")){
                    idTask=SatuSehatRujukan.idDariReferensi(ref.path("reference").asText());
                    break;
                }
            }
            String idPerujuk=SatuSehatRujukan.idDariReferensi(sr.path("requester").path("reference").asText());
            rujukan.simpanRujukanMasuk(idSR,SatuSehatRujukan.bacaIdentifier(sr,SatuSehatRujukan.SYS_NO_RUJUKAN_SATUSEHAT),idTask,idPerujuk,
                    sr.path("requester").path("display").asText(),idPasienRujukan,noRawat,idEncounter);
            pesan("Rujukan berhasil dihubungkan ke kunjungan "+noRawat+".");
        });
    }

    // ===================================================================================
    // Form kuesioner dinamis dari Questionnaire SATUSEHAT
    // ===================================================================================

    /** Pilihan answerOption valueCoding. */
    private static final class Opsi {
        final JsonNode coding;

        Opsi(JsonNode coding){
            this.coding=coding;
        }

        String kode(){
            return coding.path("code").asText();
        }

        @Override
        public String toString(){
            return kode()+" - "+coding.path("display").asText();
        }
    }

    /**
     * Membentuk komponen isian untuk setiap item Questionnaire (group/boolean/text/string/integer/decimal/choice)
     * dan menyusun jawaban QuestionnaireResponse dengan linkId yang sama.
     * Dua item choice berurutan dalam satu group dengan kode anak berawalan kode induk (Provinsi -> Kabupaten/Kota)
     * dibuat bertingkat: pilihan anak disaring sesuai pilihan induk.
     */
    private final class FormKuesioner {
        final JPanel panel=new JPanel();
        final List<Item> daftar=new ArrayList<>();

        final class Item {
            JsonNode definisi;
            String linkId="",teks="",tipe="";
            boolean wajib=false;
            JComponent isian;
            final List<Item> anak=new ArrayList<>();
            final List<Opsi> opsi=new ArrayList<>();
        }

        FormKuesioner(JsonNode questionnaire){
            panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
            panel.setBackground(Color.WHITE);
            panel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(239,244,234)),
                    questionnaire.path("title").asText(),TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION,
                    new Font("Tahoma",Font.BOLD,11),new Color(50,50,50)));
            bangun(questionnaire.path("item"),panel,daftar,0);
        }

        private void bangun(JsonNode items,JPanel target,List<Item> tampung,int indent){
            Item sebelumnya=null;
            for(JsonNode definisi:items){
                Item item=new Item();
                item.definisi=definisi;
                item.linkId=definisi.path("linkId").asText();
                item.teks=definisi.path("text").asText();
                item.tipe=definisi.path("type").asText();
                item.wajib=definisi.path("required").asBoolean(false);
                tampung.add(item);
                if(item.tipe.equals("group")){
                    widget.Label judul=label((indent>0?"   ":"")+item.teks,800);
                    judul.setFont(new Font("Tahoma",Font.BOLD,11));
                    JPanel barisJudul=baris();
                    barisJudul.add(judul);
                    target.add(barisJudul);
                    bangun(definisi.path("item"),target,item.anak,indent+1);
                    sebelumnya=null;
                    continue;
                }
                JPanel barisItem=baris();
                barisItem.add(label(spasi(indent)+item.teks+(item.wajib?" *":""),Math.max(260,Math.min(520,item.teks.length()*6+40))));
                switch (item.tipe) {
                    case "boolean":
                        widget.CekBox cek=new widget.CekBox();
                        cek.setText("Ya");
                        cek.setOpaque(false);
                        item.isian=cek;
                        break;
                    case "choice":
                    case "open-choice":
                        for(JsonNode opsi:definisi.path("answerOption")){
                            if(opsi.has("valueCoding")){
                                item.opsi.add(new Opsi(opsi.path("valueCoding")));
                            }
                        }
                        if(item.opsi.isEmpty()){
                            widget.TextBox teks=new widget.TextBox();
                            teks.setPreferredSize(new Dimension(250,23));
                            item.isian=teks;
                        }else{
                            widget.ComboBox kombo=new widget.ComboBox();
                            kombo.setPreferredSize(new Dimension(320,23));
                            item.isian=kombo;
                            if(sebelumnya!=null&&!sebelumnya.opsi.isEmpty()&&anakDari(sebelumnya,item)){
                                final Item induk=sebelumnya;
                                widget.ComboBox komboInduk=(widget.ComboBox)induk.isian;
                                komboInduk.addActionListener(e->saring(induk,item));
                                saring(induk,item);
                            }else{
                                isiOpsi(item,"");
                            }
                        }
                        break;
                    default:
                        widget.TextBox teks=new widget.TextBox();
                        teks.setPreferredSize(new Dimension(item.tipe.equals("integer")||item.tipe.equals("decimal")?100:300,23));
                        if(item.tipe.equals("text")){
                            teks.setToolTipText("Misal kode ICD 9 tindakan yang dibutuhkan");
                        }
                        item.isian=teks;
                        break;
                }
                barisItem.add(item.isian);
                target.add(barisItem);
                sebelumnya=item;
            }
        }

        private String spasi(int indent){
            StringBuilder teks=new StringBuilder();
            for(int i=0;i<indent;i++){
                teks.append("   ");
            }
            return teks.toString();
        }

        /** true bila seluruh kode pilihan anak berawalan salah satu kode pilihan induk dan lebih panjang. */
        private boolean anakDari(Item induk,Item anak){
            if(anak.opsi.isEmpty()){
                return false;
            }
            int cek=0;
            for(Opsi opsi:anak.opsi){
                boolean cocok=false;
                for(Opsi pilihanInduk:induk.opsi){
                    if(opsi.kode().length()>pilihanInduk.kode().length()&&opsi.kode().startsWith(pilihanInduk.kode())){
                        cocok=true;
                        break;
                    }
                }
                if(!cocok){
                    return false;
                }
                if(++cek>=50){
                    break;
                }
            }
            return true;
        }

        private void saring(Item induk,Item anak){
            Object pilihan=((widget.ComboBox)induk.isian).getSelectedItem();
            isiOpsi(anak,pilihan instanceof Opsi?((Opsi)pilihan).kode():"");
        }

        /** Mengisi kombo dengan pilihan berawalan kode induk; memilih wilayah RS dari setting bila ada. */
        private void isiOpsi(Item item,String awalan){
            widget.ComboBox kombo=(widget.ComboBox)item.isian;
            kombo.removeAllItems();
            String propinsi=koneksiDB.PROPINSISATUSEHAT(),kabupaten=koneksiDB.KABUPATENSATUSEHAT();
            Opsi bawaan=null;
            for(Opsi opsi:item.opsi){
                if(awalan.equals("")||opsi.kode().startsWith(awalan)){
                    kombo.addItem(opsi);
                    if(opsi.kode().equals(propinsi)||opsi.kode().equals(kabupaten)){
                        bawaan=opsi;
                    }
                }
            }
            if(bawaan!=null){
                kombo.setSelectedItem(bawaan);
            }
        }

        /** Pesan item wajib yang belum diisi, "" bila lengkap. */
        String validasi(){
            return validasi(daftar);
        }

        private String validasi(List<Item> items){
            for(Item item:items){
                if(item.tipe.equals("group")){
                    String salah=validasi(item.anak);
                    if(!salah.equals("")){
                        return salah;
                    }
                }else if(item.wajib&&!item.tipe.equals("boolean")&&nilaiTeks(item).equals("")){
                    return item.teks+" wajib diisi.";
                }else if(item.tipe.equals("integer")&&!nilaiTeks(item).equals("")&&!nilaiTeks(item).matches("-?[0-9]+")){
                    return item.teks+" harus berupa angka.";
                }else if(item.tipe.equals("decimal")&&!nilaiTeks(item).equals("")&&!nilaiTeks(item).matches("-?[0-9]+(\\.[0-9]+)?")){
                    return item.teks+" harus berupa angka.";
                }
            }
            return "";
        }

        private String nilaiTeks(Item item){
            if(item.isian instanceof widget.TextBox){
                return ((widget.TextBox)item.isian).getText().trim();
            }
            if(item.isian instanceof widget.ComboBox){
                Object pilihan=((widget.ComboBox)item.isian).getSelectedItem();
                return pilihan==null?"":pilihan.toString();
            }
            return "";
        }

        /** item[] QuestionnaireResponse. */
        ArrayNode jawaban(){
            ArrayNode hasil=rujukan.getMapper().createArrayNode();
            jawaban(daftar,hasil);
            return hasil;
        }

        private void jawaban(List<Item> items,ArrayNode hasil){
            for(Item item:items){
                ObjectNode node=rujukan.getMapper().createObjectNode();
                node.put("linkId",item.linkId);
                node.put("text",item.teks);
                if(item.tipe.equals("group")){
                    jawaban(item.anak,node.putArray("item"));
                    hasil.add(node);
                    continue;
                }
                if(item.isian instanceof widget.CekBox){
                    node.putArray("answer").addObject().put("valueBoolean",((widget.CekBox)item.isian).isSelected());
                }else if(item.isian instanceof widget.ComboBox){
                    Object pilihan=((widget.ComboBox)item.isian).getSelectedItem();
                    if(!(pilihan instanceof Opsi)){
                        continue;
                    }
                    node.putArray("answer").addObject().set("valueCoding",((Opsi)pilihan).coding.deepCopy());
                }else{
                    String nilai=nilaiTeks(item);
                    if(nilai.equals("")){
                        continue;
                    }
                    ObjectNode answer=node.putArray("answer").addObject();
                    if(item.tipe.equals("integer")){
                        answer.put("valueInteger",Integer.parseInt(nilai));
                    }else if(item.tipe.equals("decimal")){
                        answer.put("valueDecimal",Double.parseDouble(nilai));
                    }else{
                        answer.put("valueString",nilai);
                    }
                }
                hasil.add(node);
            }
        }
    }
}
