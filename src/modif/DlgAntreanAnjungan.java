package modif;

import com.fasterxml.jackson.databind.JsonNode;
import fungsi.WarnaTable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

/**
 * Daftar tiket antrean anjungan hari ini untuk meja FO, dibuka dari DlgReg.
 * Panggil, panggil ulang, batal dan pindah lewat API aplikasi anjungan. Tiket selesai saat barcode dicetak di DlgReg.
 */
public class DlgAntreanAnjungan extends JDialog {
    private static final int JEDA_REFRESH=10000,KOLOM_STATUS=6;
    // filter terakhir diingat, karena dialog dibuka lagi setiap selesai menyimpan registrasi
    private static String politampil="",doktertampil="",mejasaya="";
    private final RegistrasiAnjungan registrasi=new RegistrasiAnjungan();
    private final ApiAnjungan api=new ApiAnjungan();
    private final KwitansiUI UI=new KwitansiUI();
    private final widget.Button BtnPanggil=tombol("Panggil (F8)",100),BtnUlang=tombol("Ulang (F9)",90),
            BtnBatal=tombol("Batal",70),BtnPindahDokter=tombol("Pindah Dokter",110),BtnPindahBayar=tombol("Pindah Bayar",105);
    private final widget.ComboBox CmbPoli=new widget.ComboBox(),CmbDokter=new widget.ComboBox(),CmbStatus=new widget.ComboBox();
    // poli dan dokter bisa dicari dengan mengetik nama
    private final ComboCari cariPoli=new ComboCari(CmbPoli,kode->{
        politampil=kode;
        isiDokter();
        tampil();
    });
    private final ComboCari cariDokter=new ComboCari(CmbDokter,kode->{
        doktertampil=kode;
        tampil();
    });
    private final widget.Label LCount=new widget.Label();
    private final widget.Table tbTiket=new widget.Table();
    private final List<TiketAnjungan> daftartiket=new ArrayList<>();
    private final Timer refresh=new Timer(JEDA_REFRESH,evt->tampil());
    private int urutanmuat=0;
    // tiket yang baru dipanggil (nomor dan dokter), dipilih setelah tabel dimuat ulang
    private String nomordipanggil="",dokterdipanggil="";
    private TiketAnjungan terpilih=null;
    // permintaan ke aplikasi anjungan sedang berjalan
    private boolean sibuk=false;
    private final DefaultTableModel tabMode=new DefaultTableModel(null,new Object[]{
        "No. FO","Dokter","Poliklinik","Jalur","Cara Bayar","Jam Cetak","Status","No.Reg","No.RM","Meja"
    }){
        @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
    };

    public DlgAntreanAnjungan(Window induk){
        super(induk,"Antrean Anjungan",ModalityType.APPLICATION_MODAL);
        initComponents();
        isiFilter();
        tampil();
        refresh.start();
    }

    private void initComponents(){
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e){
                refresh.stop();
            }
        });

        widget.InternalFrame internalFrame1=new widget.InternalFrame();
        internalFrame1.setBorder(UI.judul("::[ Antrean Anjungan ]::"));
        internalFrame1.setLayout(new BorderLayout(1,1));

        widget.PanelBiasa FormInput=new widget.PanelBiasa();
        FormInput.setLayout(null);
        FormInput.setPreferredSize(new Dimension(970,43));
        FormInput.add(UI.label("Poli :",0,10,45));
        CmbPoli.setBounds(48,10,230,23);
        CmbPoli.setToolTipText("Ketik sebagian nama poli untuk mencari");
        FormInput.add(CmbPoli);
        FormInput.add(UI.label("Dokter :",285,10,55));
        CmbDokter.setBounds(343,10,280,23);
        CmbDokter.setToolTipText("Ketik sebagian nama dokter untuk mencari");
        FormInput.add(CmbDokter);
        FormInput.add(UI.label("Status :",630,10,50));
        CmbStatus.addItem("Belum Selesai");
        CmbStatus.addItem("Semua");
        CmbStatus.setBounds(683,10,120,23);
        FormInput.add(CmbStatus);

        tbTiket.setModel(tabMode);
        tbTiket.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbTiket.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        int[] lebar={85,200,150,80,80,70,85,55,70,45};
        for(int i=0;i<lebar.length;i++){
            tbTiket.getColumnModel().getColumn(i).setPreferredWidth(lebar[i]);
        }
        tbTiket.setDefaultRenderer(Object.class,new WarnaTiket());
        tbTiket.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),"registrasi");
        tbTiket.getActionMap().put("registrasi",new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e){
                BtnRegistrasiActionPerformed();
            }
        });
        pasangPintasan(KeyEvent.VK_F8,"panggil",BtnPanggil);
        pasangPintasan(KeyEvent.VK_F9,"ulang",BtnUlang);
        tbTiket.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                if(e.getClickCount()==2){
                    BtnRegistrasiActionPerformed();
                }
            }
        });
        widget.ScrollPane Scroll=new widget.ScrollPane();
        Scroll.setOpaque(true);
        Scroll.setViewportView(tbTiket);

        widget.panelisi panelGlass8=new widget.panelisi();
        panelGlass8.setPreferredSize(new Dimension(44,44));
        panelGlass8.setLayout(new FlowLayout(FlowLayout.LEFT,5,9));
        widget.Button BtnRegistrasi=UI.tombol("Registrasi","accept.png",'R',110);
        BtnRegistrasi.setToolTipText("Enter / Alt+R");
        BtnRegistrasi.addActionListener(evt->BtnRegistrasiActionPerformed());
        widget.Button BtnKeluar=UI.tombol("Keluar","exit.png",'K',90);
        BtnKeluar.addActionListener(evt->dispose());
        BtnPanggil.setToolTipText("F8, panggil tiket berikutnya. Bila memilih tiket lain yang menunggu, tiket itu yang dipanggil");
        BtnPanggil.addActionListener(evt->BtnPanggilActionPerformed());
        BtnUlang.setToolTipText("F9, panggil ulang tiket terpilih yang berstatus DIPANGGIL");
        BtnUlang.addActionListener(evt->BtnUlangActionPerformed());
        panelGlass8.add(BtnPanggil);
        panelGlass8.add(BtnUlang);
        panelGlass8.add(BtnRegistrasi);
        BtnBatal.setToolTipText("Batalkan tiket Onsite yang belum terdaftar dan lepas No.Reg-nya");
        BtnBatal.addActionListener(evt->BtnBatalActionPerformed());
        panelGlass8.add(BtnBatal);
        BtnPindahDokter.setToolTipText("Ganti tiket Onsite yang belum terdaftar ke dokter lain");
        BtnPindahDokter.addActionListener(evt->BtnPindahDokterActionPerformed());
        BtnPindahBayar.setToolTipText("Ganti tiket Onsite yang belum terdaftar antara BPJS dan Umum/Asuransi");
        BtnPindahBayar.addActionListener(evt->BtnPindahBayarActionPerformed());
        panelGlass8.add(BtnPindahDokter);
        panelGlass8.add(BtnPindahBayar);
        panelGlass8.add(BtnKeluar);
        LCount.setPreferredSize(new Dimension(130,23));
        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        panelGlass8.add(LCount);

        internalFrame1.add(FormInput,BorderLayout.PAGE_START);
        internalFrame1.add(Scroll,BorderLayout.CENTER);
        internalFrame1.add(panelGlass8,BorderLayout.PAGE_END);
        setContentPane(internalFrame1);
        setSize(990,520);
        setLocationRelativeTo(getOwner());
    }

    private static widget.Button tombol(String teks,int lebar){
        widget.Button tombol=new widget.Button();
        tombol.setText(teks);
        tombol.setPreferredSize(new Dimension(lebar,30));
        return tombol;
    }

    // dipasang juga di tabel, karena JTable memakai F8 untuk pindah fokus ke header
    private void pasangPintasan(int tombolpapan,String nama,widget.Button tombol){
        AbstractAction aksi=new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e){
                tombol.doClick();
            }
        };
        KeyStroke pintasan=KeyStroke.getKeyStroke(tombolpapan,0);
        tbTiket.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(pintasan,nama);
        tbTiket.getActionMap().put(nama,aksi);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(pintasan,nama);
        getRootPane().getActionMap().put(nama,aksi);
    }

    private void isiFilter(){
        cariPoli.isi(registrasi.daftarPoli(),"Semua",politampil);
        politampil=cariPoli.kode();
        isiDokter();
        CmbStatus.addActionListener(evt->tampil());
    }

    // dokter yang tampil mengikuti poli terpilih, dokter yang tidak praktik di poli itu kembali ke Semua
    private void isiDokter(){
        cariDokter.isi(registrasi.daftarDokter(politampil),"Semua",doktertampil);
        doktertampil=cariDokter.kode();
    }

    /** Memuat tiket di background supaya layar tidak berhenti saat refresh otomatis. */
    private void tampil(){
        final String poli=politampil,dokter=doktertampil;
        final boolean semua=CmbStatus.getSelectedIndex()==1;
        final int urutan=++urutanmuat;
        new SwingWorker<List<TiketAnjungan>,Void>() {
            @Override
            protected List<TiketAnjungan> doInBackground() throws Exception {
                return registrasi.daftarTiket(poli,dokter,semua);
            }

            @Override
            protected void done(){
                // hasil lama diabaikan bila filter sudah diganti
                if(urutan==urutanmuat){
                    try {
                        isiTabel(get());
                    } catch (Exception e) {
                        System.out.println("Notifikasi : "+e);
                        LCount.setText("Gagal dibaca..!!");
                    }
                }
            }
        }.execute();
    }

    private void isiTabel(List<TiketAnjungan> daftar){
        long idterpilih=(tbTiket.getSelectedRow()== -1)?0:daftartiket.get(tbTiket.getSelectedRow()).id;
        int baris=-1,pertama=-1;
        daftartiket.clear();
        daftartiket.addAll(daftar);
        tabMode.setRowCount(0);
        for(int i=0;i<daftar.size();i++){
            TiketAnjungan tiket=daftar.get(i);
            tabMode.addRow(new Object[]{
                tiket.notiketfo,tiket.nmdokter,tiket.nmpoli,tiket.jalur,
                tiket.carabayar,tiket.jamcetak,tiket.status,tiket.noreg,tiket.norm,tiket.meja
            });
            if(nomordipanggil.equals("")?tiket.id==idterpilih:(tiket.notiketfo.equals(nomordipanggil)&&tiket.kddokter.equals(dokterdipanggil))){
                baris=i;
            }
            if((pertama== -1)&&tiket.isMenunggu()){
                pertama=i;
            }
        }
        if(baris== -1){
            baris=pertama;
        }
        if(baris!= -1){
            tbTiket.setRowSelectionInterval(baris,baris);
            if(!nomordipanggil.equals("")){
                tbTiket.scrollRectToVisible(tbTiket.getCellRect(baris,0,true));
            }
        }
        nomordipanggil="";
        LCount.setText("Jumlah : "+daftar.size());
        // refresh otomatis tidak boleh mengambil fokus dari panel cari poli / dokter
        if((idterpilih==0)&&!cariPoli.isTerbuka()&&!cariDokter.isTerbuka()){
            tbTiket.requestFocusInWindow();
        }
    }

    private void BtnRegistrasiActionPerformed(){
        if(sibuk){
            return;
        }
        if(tbTiket.getSelectedRow()== -1){
            JOptionPane.showMessageDialog(this,"Maaf, silahkan pilih dulu tiket pada tabel..!!");
            return;
        }
        TiketAnjungan tiket=daftartiket.get(tbTiket.getSelectedRow());
        if(tiket.isTerdaftar()&&tiket.isMenunggu()){
            if(!tiket.isRegistrasiAktif()){
                JOptionPane.showMessageDialog(this,"Registrasi "+tiket.norawat+" milik tiket "+tiket.notiketfo+" sudah dibatalkan atau dihapus..!!");
            }else if(JOptionPane.showConfirmDialog(this,"Layani tiket "+tiket.notiketfo+" ("+tiket.jalur+" "+tiket.carabayar+") ?\n"+
                    "No. RM : "+tiket.norm+" / No. Rawat : "+tiket.norawat,"Konfirmasi",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                layani(tiket);
            }
        }else if(tiket.isTerdaftar()||tiket.isBisaDidaftarkan()){
            pilih(tiket);
        }else{
            JOptionPane.showMessageDialog(this,"Tiket "+tiket.notiketfo+" ("+tiket.jalur+", "+tiket.status+") tidak bisa diregistrasikan..!!");
        }
    }

    private void pilih(TiketAnjungan tiket){
        terpilih=tiket;
        dispose();
    }

    /** Tiket yang sudah punya registrasi (WEBSITE/MJKN) menjadi DILAYANI, lalu registrasinya disorot di DlgReg. */
    private void layani(final TiketAnjungan tiket){
        kunci(true);
        new SwingWorker<Void,Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                api.layani(tiket.id);
                if(!registrasi.statusTiket(tiket.id).equals("DILAYANI")){
                    throw new Exception("Status tiket "+tiket.notiketfo+" tidak berubah di database. Cek URLAPIANJUNGAN..!!");
                }
                return null;
            }

            @Override
            protected void done(){
                kunci(false);
                // dialog sudah ditutup petugas sebelum jawaban diterima
                if(!isShowing()){
                    return;
                }
                try {
                    get();
                    pilih(tiket);
                } catch (ExecutionException e) {
                    System.out.println("Notifikasi : "+e.getCause());
                    if(e.getCause() instanceof ApiAnjungan.StatusBerubah){
                        JOptionPane.showMessageDialog(DlgAntreanAnjungan.this,"Status tiket "+tiket.notiketfo+" sudah berubah, kemungkinan diproses meja lain.\nRegistrasi tetap ditampilkan..!!");
                        pilih(tiket);
                    }else{
                        JOptionPane.showMessageDialog(DlgAntreanAnjungan.this,e.getCause().getMessage());
                        tampil();
                    }
                } catch (Exception e) {
                    System.out.println("Notifikasi : "+e);
                }
            }
        }.execute();
    }

    private void kunci(boolean proses){
        sibuk=proses;
        BtnPanggil.setEnabled(!proses);
        BtnUlang.setEnabled(!proses);
        BtnBatal.setEnabled(!proses);
        BtnPindahDokter.setEnabled(!proses);
        BtnPindahBayar.setEnabled(!proses);
    }

    /** Tiket terpilih bila boleh dipindah / dibatalkan, null bila tidak (petugas sudah diberi pesan). */
    private TiketAnjungan tiketOnsite(String aksi){
        TiketAnjungan tiket=(tbTiket.getSelectedRow()== -1)?null:daftartiket.get(tbTiket.getSelectedRow());
        if(tiket==null){
            JOptionPane.showMessageDialog(this,"Maaf, silahkan pilih dulu tiket pada tabel..!!");
        }else if(!tiket.jalur.equals("ONSITE")||tiket.isTerdaftar()){
            JOptionPane.showMessageDialog(this,"Hanya tiket ONSITE yang belum terdaftar yang bisa "+aksi+".\nTiket lain diubah atau dibatalkan lewat data registrasi..!!");
            tiket=null;
        }else if(tiket.status.equals("SELESAI")||tiket.status.equals("BATAL")){
            JOptionPane.showMessageDialog(this,"Tiket "+tiket.notiketfo+" berstatus "+tiket.status+", tidak bisa "+aksi+"..!!");
            tiket=null;
        }
        return tiket;
    }

    private void BtnBatalActionPerformed(){
        final TiketAnjungan tiket=tiketOnsite("dibatalkan");
        if((tiket==null)||(JOptionPane.showConfirmDialog(this,"Batalkan tiket "+tiket.notiketfo+" ("+tiket.nmdokter+", "+tiket.carabayar+") ?\n"+
                "No.Reg "+tiket.noreg+" dilepas dan bisa dipakai pasien lain.\nAmbil kembali Lembar 1 dan Lembar 2 dari pasien.",
                "Konfirmasi",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)){
            return;
        }
        kunci(true);
        new SwingWorker<Void,Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                api.batal(tiket);
                if(!registrasi.statusTiket(tiket.id).equals("BATAL")){
                    throw new Exception("Status tiket "+tiket.notiketfo+" tidak berubah di database. Cek URLAPIANJUNGAN..!!");
                }else if(registrasi.isReserveAda(tiket)){
                    // aplikasi anjungan versi lama hanya mengubah status
                    throw new Exception("Tiket "+tiket.notiketfo+" sudah dibatalkan, tetapi No.Reg "+tiket.noreg+" belum dilepas.\nPerbarui aplikasi anjungan..!!");
                }
                return null;
            }

            @Override
            protected void done(){
                kunci(false);
                try {
                    get();
                } catch (ExecutionException e) {
                    tampilGagal(e);
                } catch (Exception e) {
                    System.out.println("Notifikasi : "+e);
                } finally{
                    tampil();
                    tbTiket.requestFocusInWindow();
                }
            }
        }.execute();
    }

    private void BtnPindahDokterActionPerformed(){
        final TiketAnjungan tiket=tiketOnsite("dipindah");
        if(tiket==null){
            return;
        }
        kunci(true);
        new SwingWorker<JsonNode,Void>() {
            @Override
            protected JsonNode doInBackground() throws Exception {
                return api.dokterTujuan();
            }

            @Override
            protected void done(){
                kunci(false);
                if(!isShowing()){
                    return;
                }
                try {
                    pilihDokterTujuan(tiket,get());
                } catch (ExecutionException e) {
                    tampilGagal(e);
                } catch (Exception e) {
                    System.out.println("Notifikasi : "+e);
                }
            }
        }.execute();
    }

    private void pilihDokterTujuan(TiketAnjungan tiket,JsonNode daftar){
        List<JsonNode> tujuan=new ArrayList<>();
        widget.ComboBox CmbTujuan=new widget.ComboBox();
        for(JsonNode dokter:daftar){
            // dokter yang sama di poli lain tetap boleh, tiket per dokter + poli
            if(!(dokter.path("kode").asText().equals(tiket.kddokter)&&dokter.path("kd_poli").asText().equals(tiket.kdpoli))){
                tujuan.add(dokter);
                CmbTujuan.addItem(dokter.path("nama").asText()+" - "+dokter.path("poli").asText()+" ("+dokter.path("mulai").asText()+" - "+dokter.path("selesai").asText()+")"+
                    (dokter.path("penuh").asBoolean()?" PENUH":""));
            }
        }
        if(tujuan.isEmpty()){
            JOptionPane.showMessageDialog(this,"Tidak ada dokter lain yang masih buka hari ini..!!");
        }else if(tanya(tiket,"Pindah ke dokter :",CmbTujuan,"Pindah Dokter")){
            JsonNode dokter=tujuan.get(CmbTujuan.getSelectedIndex());
            if(dokter.path("penuh").asBoolean()){
                JOptionPane.showMessageDialog(this,"Kuota "+dokter.path("nama").asText()+" hari ini sudah penuh..!!");
            }else{
                pindah(tiket,dokter.path("kd_poli").asText(),dokter.path("kode").asText(),tiket.carabayar);
            }
        }
    }

    private void BtnPindahBayarActionPerformed(){
        TiketAnjungan tiket=tiketOnsite("dipindah");
        if(tiket!=null){
            // Umum <-> Asuransi sama-sama huruf A, cukup dipilih di Jenis Bayar saat registrasi
            widget.ComboBox CmbTujuan=new widget.ComboBox();
            if(tiket.isBpjs()){
                CmbTujuan.addItem("UMUM");
                CmbTujuan.addItem("ASURANSI");
            }else{
                CmbTujuan.addItem("BPJS");
            }
            if(tanya(tiket,"Pindah ke cara bayar :",CmbTujuan,"Pindah Bayar")){
                pindah(tiket,tiket.kdpoli,tiket.kddokter,CmbTujuan.getSelectedItem().toString());
            }
        }
    }

    private boolean tanya(TiketAnjungan tiket,String teks,widget.ComboBox CmbTujuan,String judul){
        Object[] isi={"Tiket "+tiket.notiketfo+" ("+tiket.nmdokter+", "+tiket.carabayar+") akan dibatalkan dan diganti tiket baru.",
            "Ambil kembali Lembar 1 dan Lembar 2 yang lama dari pasien.",teks,CmbTujuan};
        return JOptionPane.showConfirmDialog(this,isi,judul,JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE)==JOptionPane.OK_OPTION;
    }

    private void pindah(final TiketAnjungan tiket,final String kdpoli,final String kddokter,final String carabayar){
        kunci(true);
        new SwingWorker<JsonNode,Void>() {
            @Override
            protected JsonNode doInBackground() throws Exception {
                return api.pindah(tiket,kdpoli,kddokter,carabayar);
            }

            @Override
            protected void done(){
                kunci(false);
                try {
                    JsonNode hasil=get(),baru=hasil.path("onsite").path("tiket");
                    nomordipanggil=baru.path("no_fo").asText();
                    dokterdipanggil=kddokter;
                    // tiket baru harus terlihat walau filter poli / dokter masih yang lama
                    if(!politampil.equals("")&&!politampil.equals(kdpoli)){
                        cariPoli.pilih("",false);
                        politampil="";
                        isiDokter();
                    }
                    if(!doktertampil.equals("")&&!doktertampil.equals(kddokter)){
                        cariDokter.pilih("",false);
                        doktertampil="";
                    }
                    // tetap ditampilkan walau dialog sudah ditutup, nomor pasien berubah
                    JOptionPane.showMessageDialog(isShowing()?DlgAntreanAnjungan.this:null,"Tiket "+tiket.notiketfo+" diganti "+baru.path("no_fo").asText()+" (Lembar 2 : "+baru.path("no_poli").asText()+").\n"+
                        (hasil.path("tercetak").asBoolean()?"Tiket baru dicetak di printer anjungan.":"Tiket baru GAGAL dicetak : "+hasil.path("err_printer").asText()+"\nCatat nomor di atas untuk pasien."));
                } catch (ExecutionException e) {
                    tampilGagal(e);
                } catch (Exception e) {
                    System.out.println("Notifikasi : "+e);
                } finally{
                    tampil();
                    tbTiket.requestFocusInWindow();
                }
            }
        }.execute();
    }

    private void tampilGagal(ExecutionException e){
        System.out.println("Notifikasi : "+e.getCause());
        JOptionPane.showMessageDialog(isShowing()?this:null,e.getCause().getMessage());
    }

    /** Tiket menunggu paling atas dipanggil lewat urutan server, tiket menunggu lain dipanggil langsung setelah konfirmasi. */
    private void BtnPanggilActionPerformed(){
        TiketAnjungan pertama=null;
        for(TiketAnjungan tiket:daftartiket){
            if(tiket.status.equals("MENUNGGU")){
                pertama=tiket;
                break;
            }
        }
        if(pertama==null){
            JOptionPane.showMessageDialog(this,"Tidak ada tiket yang menunggu..!!");
            tampil();
            return;
        }
        final TiketAnjungan berikutnya=pertama;
        final TiketAnjungan pilihan=(tbTiket.getSelectedRow()== -1)?pertama:daftartiket.get(tbTiket.getSelectedRow());
        if(!pilihan.status.equals("MENUNGGU")||(pilihan.id==pertama.id)){
            panggil(()->api.panggilBerikutnya(berikutnya.kdpoli,berikutnya.kddokter));
        }else if(JOptionPane.showConfirmDialog(this,"Panggil "+pilihan.notiketfo+" ("+pilihan.nmdokter+") mendahului "+pertama.notiketfo+" ?",
                "Konfirmasi",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            panggil(()->{
                // API panggil juga menerima tiket DIPANGGIL, jadi dipastikan belum diambil meja lain
                String status=registrasi.statusTiket(pilihan.id);
                if(!status.equals("MENUNGGU")){
                    throw new Exception("Tiket "+pilihan.notiketfo+" sudah diambil meja lain atau statusnya berubah"+(status.equals("")?"":" ("+status+")")+"..!!");
                }
                return cocokkan(api.panggil(pilihan.id),pilihan);
            });
        }
    }

    private void BtnUlangActionPerformed(){
        final TiketAnjungan tiket=(tbTiket.getSelectedRow()== -1)?null:daftartiket.get(tbTiket.getSelectedRow());
        if((tiket==null)||!tiket.status.equals("DIPANGGIL")){
            JOptionPane.showMessageDialog(this,"Maaf, silahkan pilih dulu tiket yang berstatus DIPANGGIL..!!");
        }else if(mejasaya.equals("")||tiket.meja.equals("")||tiket.meja.equals(mejasaya)||
                (JOptionPane.showConfirmDialog(this,"Tiket "+tiket.notiketfo+" dipanggil oleh meja "+tiket.meja+".\nPanggil ulang ke meja "+mejasaya+" ?",
                "Konfirmasi",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)){
            panggil(()->{
                String status=registrasi.statusTiket(tiket.id);
                if(!status.equals("DIPANGGIL")){
                    throw new Exception("Status tiket "+tiket.notiketfo+" sudah berubah"+(status.equals("")?"":" menjadi "+status)+"..!!");
                }
                return cocokkan(api.panggil(tiket.id),tiket);
            });
        }
    }

    /** Nomor yang diumumkan harus sama dengan tiket, bila beda berarti URLAPIANJUNGAN mengarah ke database lain. */
    private JsonNode cocokkan(JsonNode panggilan,TiketAnjungan tiket) throws Exception {
        if(!panggilan.path("nomor").asText().equals(tiket.notiketfo)){
            throw new Exception("Aplikasi anjungan memanggil "+panggilan.path("nomor").asText()+", bukan "+tiket.notiketfo+". Cek URLAPIANJUNGAN..!!");
        }
        return panggilan;
    }

    /** Menjalankan panggilan di background, tombol dikunci supaya tidak terpanggil dua kali. */
    private void panggil(final Callable<JsonNode> permintaan){
        kunci(true);
        new SwingWorker<JsonNode,Void>() {
            @Override
            protected JsonNode doInBackground() throws Exception {
                return permintaan.call();
            }

            @Override
            protected void done(){
                try {
                    JsonNode panggilan=get();
                    nomordipanggil=panggilan.path("nomor").asText();
                    dokterdipanggil=panggilan.path("kd_dokter").asText();
                    mejasaya=panggilan.path("loket").asText();
                } catch (ExecutionException e) {
                    System.out.println("Notifikasi : "+e.getCause());
                    if(isShowing()){
                        JOptionPane.showMessageDialog(DlgAntreanAnjungan.this,e.getCause().getMessage());
                    }
                } catch (Exception e) {
                    System.out.println("Notifikasi : "+e);
                } finally{
                    kunci(false);
                    tampil();
                    tbTiket.requestFocusInWindow();
                }
            }
        }.execute();
    }

    /** Tiket yang dipilih petugas, null bila dialog ditutup tanpa memilih. */
    public TiketAnjungan getTerpilih(){
        return terpilih;
    }

    /** Warna baris mengikuti status tiket. */
    private static class WarnaTiket extends WarnaTable {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
            Component component=super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
            switch (table.getModel().getValueAt(row,KOLOM_STATUS).toString()) {
                case "DIPANGGIL":
                    component.setBackground(new Color(255,243,176));
                    break;
                case "DILAYANI":
                    component.setBackground(new Color(190,225,255));
                    break;
                case "SELESAI":
                    component.setBackground(new Color(200,240,200));
                    break;
                case "BATAL":
                    component.setBackground(new Color(255,205,205));
                    break;
                default:
                    break;
            }
            return component;
        }
    }
}
