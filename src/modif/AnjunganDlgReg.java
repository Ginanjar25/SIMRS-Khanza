package modif;

import fungsi.akses;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;

/**
 * Antrean anjungan di form Registrasi (DlgReg). Semua logika ada di sini supaya DlgReg cukup memanggil satu baris
 * di tiap titik (lebih mudah digabung dengan versi Khanza asli).
 * Bila AKTIFKANANJUNGAN bukan yes : pasang() tidak menambah tombol, adaTiket() selalu false, dan semua method lain tidak berbuat apa-apa.
 */
public class AnjunganDlgReg {
    /** Komponen dan aksi DlgReg yang dipakai, diisi sekali di DlgReg lalu dikirim ke pasang(). */
    public static class Form {
        public JDialog dialog;
        public widget.PanelBiasa panel;
        public widget.TextBox noreg,norawat,norm,pasien,kddokter,nmdokter,kdpoli,nmpoli,kdpj,nmpj,stts,biaya;
        public widget.Tanggal tglreg;
        public widget.Button btndokter,btnpoli,btnpj;
        // cari registrasi (tab Data Registrasi)
        public JTabbedPane tab;
        public widget.RadioButton semuatanggal;
        public widget.Tanggal tglcari1,tglcari2;
        public widget.TextBox caripoli,caridokter,cari;
        public widget.Table tabel;
        public int kolomnorawat;
        /** emptTeks() DlgReg */
        public Runnable kosongkan;
        /** membuka panel input (ChkInput + isForm()) */
        public Runnable bukainput;
        /** tampil() DlgReg */
        public Runnable tampil;
        /** getData() DlgReg */
        public Runnable getdata;
    }

    private static final Color WARNA_TIKET=new Color(255,243,176);
    private static final int JEDA_JUMLAH=15000;
    private final boolean aktif=SettingAnjungan.aktif();
    private final sekuel Sequel=new sekuel();
    private final validasi Valid=new validasi();
    private final RegistrasiAnjungan registrasi=new RegistrasiAnjungan();
    private Form f;
    // tiket yang sedang diregistrasikan, null berarti form berperilaku normal
    private TiketAnjungan tiket=null;
    private boolean[] kuncisebelumtiket;
    private Color warnanoreg;
    private boolean daritiket=false;
    private widget.Button btnAntrean,btnLepas;
    private widget.Label labelTiket;

    /** Memasang tombol antrean dan penanda tiket di samping No.Reg. Tidak berbuat apa-apa bila fitur nonaktif. */
    public void pasang(Form form){
        if(!aktif){
            return;
        }
        f=form;
        btnAntrean=new widget.Button();
        btnAntrean.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inbox.png")));
        btnAntrean.setText("Tiket Anjungan Menunggu");
        btnAntrean.setHorizontalAlignment(SwingConstants.LEFT);
        btnAntrean.setName("btnAntreanAnjungan");
        btnAntrean.addActionListener(evt->bukaAntrean());
        f.panel.add(btnAntrean);
        btnAntrean.setBounds(1200,150,300,22);

        labelTiket=new widget.Label();
        labelTiket.setHorizontalAlignment(SwingConstants.LEFT);
        labelTiket.setName("labelTiketAnjungan");
        labelTiket.setVisible(false);
        f.panel.add(labelTiket);
        labelTiket.setBounds(198,12,192,23);

        btnLepas=new widget.Button();
        btnLepas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/cross.png")));
        btnLepas.setToolTipText("Lepas tiket anjungan");
        btnLepas.setName("btnLepasTiket");
        btnLepas.setVisible(false);
        btnLepas.addActionListener(evt->lepas());
        f.panel.add(btnLepas);
        btnLepas.setBounds(395,12,28,23);

        perbaruiJumlah();
        new Timer(JEDA_JUMLAH,evt->{
            if(f.dialog.isShowing()){
                perbaruiJumlah();
            }
        }).start();
    }

    /** true bila form sedang dipakai tiket anjungan. */
    public boolean adaTiket(){
        return tiket!=null;
    }

    /** Jumlah tiket dibaca di background supaya layar tidak berhenti. */
    private void perbaruiJumlah(){
        new SwingWorker<Integer,Void>() {
            @Override
            protected Integer doInBackground(){
                return registrasi.jumlahMenunggu();
            }

            @Override
            protected void done(){
                try {
                    btnAntrean.setText((get()<0?"":get()+" ")+"Tiket Anjungan Menunggu");
                } catch (Exception e) {
                    System.out.println("Notifikasi : "+e);
                }
            }
        }.execute();
    }

    private void bukaAntrean(){
        DlgAntreanAnjungan antrean=new DlgAntreanAnjungan(f.dialog);
        antrean.setVisible(true);
        perbaruiJumlah();
        TiketAnjungan pilihan=antrean.getTerpilih();
        if(pilihan!=null){
            if(tiket!=null){
                lepas();
            }
            if(pilihan.isTerdaftar()){
                sorotRegistrasi(pilihan);
            }else{
                mulai(pilihan);
            }
        }
    }

    /** Tiket yang sudah punya registrasi : tidak membuat registrasi baru, hanya menyorot barisnya. */
    private void sorotRegistrasi(TiketAnjungan pilihan){
        f.tab.setSelectedIndex(0);
        f.semuatanggal.setSelected(false);
        Valid.SetTgl(f.tglcari1,pilihan.norawat.substring(0,10).replace("/","-"));
        Valid.SetTgl(f.tglcari2,pilihan.norawat.substring(0,10).replace("/","-"));
        f.caripoli.setText("");
        f.caridokter.setText("");
        f.cari.setText(pilihan.norawat);
        f.tampil.run();
        for(int baris=0;baris<f.tabel.getRowCount();baris++){
            if(f.tabel.getValueAt(baris,f.kolomnorawat).toString().equals(pilihan.norawat)){
                f.tabel.setRowSelectionInterval(baris,baris);
                f.tabel.scrollRectToVisible(f.tabel.getCellRect(baris,0,true));
                f.getdata.run();
                f.tabel.requestFocus();
                return;
            }
        }
        JOptionPane.showMessageDialog(f.dialog,"Registrasi "+pilihan.norawat+" milik tiket "+pilihan.notiketfo+" tidak ditemukan pada tabel.\nKemungkinan sudah dibatalkan, dihapus, atau tersaring filter..!!");
    }

    private void mulai(TiketAnjungan pilihan){
        f.bukainput.run();
        f.kosongkan.run();
        // keadaan sebelum dikunci, dikembalikan saat tiket dilepas
        kuncisebelumtiket=new boolean[]{f.btndokter.isEnabled(),f.btnpoli.isEnabled(),f.btnpj.isEnabled(),f.tglreg.isEnabled(),
            f.kddokter.isEditable(),f.kdpoli.isEditable(),f.kdpj.isEditable()};
        warnanoreg=f.noreg.getBackground();
        tiket=pilihan;
        labelTiket.setText("Tiket "+pilihan.notiketfo+" · "+pilihan.jalur+" "+pilihan.carabayar);
        labelTiket.setVisible(true);
        btnLepas.setVisible(true);
        nomorRawat();
        f.norm.requestFocus();
    }

    /** Memasang lagi isian milik tiket. Dipanggil dari semua titik DlgReg yang bisa menimpanya. */
    public void terapkan(){
        if(tiket==null){
            return;
        }
        f.noreg.setText(tiket.noreg);
        f.noreg.setBackground(WARNA_TIKET);
        f.kddokter.setText(tiket.kddokter);
        f.nmdokter.setText(tiket.nmdokter);
        f.kdpoli.setText(tiket.kdpoli);
        f.nmpoli.setText(tiket.nmpoli);
        if(f.stts.getText().equals("Lama")){
            Sequel.cariIsi("select poliklinik.registrasilama from poliklinik where poliklinik.kd_poli=?",f.biaya,f.kdpoli.getText());
        }else{
            Sequel.cariIsi("select poliklinik.registrasi from poliklinik where poliklinik.kd_poli=?",f.biaya,f.kdpoli.getText());
        }
        if(tiket.isBpjs()){
            f.kdpj.setText("BPJ");
            f.nmpj.setText(Sequel.cariIsi("select penjab.png_jawab from penjab where penjab.kd_pj=?","BPJ"));
        }else if(f.kdpj.getText().trim().equalsIgnoreCase("BPJ")){
            f.kdpj.setText("");
            f.nmpj.setText("");
        }
        f.btndokter.setEnabled(false);
        f.btnpoli.setEnabled(false);
        f.btnpj.setEnabled(!tiket.isBpjs()&&kuncisebelumtiket[2]);
        f.tglreg.setEnabled(false);
        f.kddokter.setEditable(false);
        f.kdpoli.setEditable(false);
        f.kdpj.setEditable(!tiket.isBpjs()&&kuncisebelumtiket[6]);
    }

    /**
     * Pengganti isNumber() DlgReg saat mode tiket : No.Reg milik tiket tidak boleh tertimpa, No.Rawat hanya perkiraan
     * (dipastikan saat simpan). @return true bila sudah ditangani (isNumber() DlgReg berhenti)
     */
    public boolean nomorRawat(){
        if(tiket==null){
            return false;
        }
        terapkan();
        Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(reg_periksa.no_rawat,6),signed)),0) from reg_periksa where reg_periksa.tgl_registrasi='"+tiket.tanggal+"' ",
            tiket.tanggal.replace("-","/")+"/",6,f.norawat);
        return true;
    }

    /** Mengembalikan form ke mode normal, isian belum dikosongkan. */
    private void bukaKunci(){
        tiket=null;
        f.btndokter.setEnabled(kuncisebelumtiket[0]);
        f.btnpoli.setEnabled(kuncisebelumtiket[1]);
        f.btnpj.setEnabled(kuncisebelumtiket[2]);
        f.tglreg.setEnabled(kuncisebelumtiket[3]);
        f.kddokter.setEditable(kuncisebelumtiket[4]);
        f.kdpoli.setEditable(kuncisebelumtiket[5]);
        f.kdpj.setEditable(kuncisebelumtiket[6]);
        f.noreg.setBackground(warnanoreg);
        labelTiket.setVisible(false);
        btnLepas.setVisible(false);
    }

    private void lepas(){
        bukaKunci();
        f.kosongkan.run();
    }

    /** Awal getData() DlgReg : memilih baris registrasi saat mode tiket harus melepas tiket dulu. */
    public void cekPilihData(){
        if((tiket!=null)&&(f.tabel.getSelectedRow()!= -1)){
            if(JOptionPane.showConfirmDialog(f.dialog,"Form sedang dipakai tiket anjungan "+tiket.notiketfo+".\nLepas tiket dan tampilkan data registrasi yang dipilih ?",
                    "Konfirmasi",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                lepas();
            }else{
                f.tabel.clearSelection();
            }
        }
    }

    /** ganti() DlgReg tidak boleh dipakai saat mode tiket. @return true bila ditolak */
    public boolean tolakGanti(){
        if(tiket==null){
            return false;
        }
        JOptionPane.showMessageDialog(f.dialog,"Form sedang dipakai tiket anjungan "+tiket.notiketfo+", lepas tiket terlebih dahulu..!!");
        return true;
    }

    /** Validasi ulang isian terhadap tiket sebelum disimpan. */
    public boolean cekSebelumSimpan(){
        if(!Valid.SetTgl(f.tglreg.getSelectedItem()+"").equals(tiket.tanggal)){
            JOptionPane.showMessageDialog(f.dialog,"Tanggal registrasi tidak sama dengan tanggal tiket ("+tiket.tanggal+")..!!");
            return false;
        }else if(!f.kddokter.getText().equals(tiket.kddokter)||!f.kdpoli.getText().equals(tiket.kdpoli)||!f.noreg.getText().equals(tiket.noreg)){
            terapkan();
            JOptionPane.showMessageDialog(f.dialog,"Dokter, unit, atau No.Reg berbeda dengan tiket dan sudah dikembalikan. Silahkan cek lalu simpan lagi..!!");
            return false;
        }else if(tiket.isBpjs()!=f.kdpj.getText().trim().equalsIgnoreCase("BPJ")){
            JOptionPane.showMessageDialog(f.dialog,tiket.isBpjs()?"Tiket BPJS wajib memakai jenis bayar BPJS..!!":"Tiket Umum/Asuransi tidak boleh memakai jenis bayar BPJS..!!");
            terapkan();
            return false;
        }

        String sama="";
        if(Sequel.cariInteger("select count(reg_periksa.no_rawat) from reg_periksa where reg_periksa.tgl_registrasi=curdate() and reg_periksa.stts!='Batal' "+
                "and reg_periksa.no_rkm_medis=? and reg_periksa.kd_dokter=? and reg_periksa.kd_poli=?",f.norm.getText(),f.kddokter.getText(),f.kdpoli.getText())>0){
            sama="di dokter dan unit yang sama";
        }else if(Sequel.cariInteger("select count(reg_periksa.no_rawat) from reg_periksa where reg_periksa.tgl_registrasi=curdate() and reg_periksa.stts!='Batal' "+
                "and reg_periksa.no_rkm_medis=?",f.norm.getText())>0){
            sama="di hari yang sama";
        }
        return sama.equals("")||(JOptionPane.showConfirmDialog(f.dialog,"Pasien berikut sudah terdaftar "+sama+" :\n"
            +"No. RM : "+f.norm.getText()+" / Nama: "+f.pasien.getText()+"\nApakah tetap ingin melanjutkan pendaftaran ? ",
            "Konfirmasi",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION);
    }

    /**
     * Simpan reg_periksa dan menautkan tiket dalam satu transaksi.
     * @param regperiksa isi reg_periksa, urutan sama dengan insert DlgReg
     */
    public boolean simpan(String[] regperiksa){
        f.dialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        String norawat=registrasi.simpan(tiket,regperiksa,akses.getkode());
        f.dialog.setCursor(Cursor.getDefaultCursor());
        if(norawat.equals("")){
            JOptionPane.showMessageDialog(f.dialog,registrasi.getPesan());
            return false;
        }
        f.norawat.setText(norawat);
        return true;
    }

    /** Setelah reg_periksa tersimpan : registrasi langsung (tanpa tiket) dicatat ke antrean anjungan dengan nomor FO kosong. */
    public void catatLangsung(String norawat){
        if(aktif&&(tiket==null)&&!registrasi.catatLangsung(norawat,akses.getkode(),"ONSITE")){
            JOptionPane.showMessageDialog(f.dialog,"Registrasi tersimpan, tetapi gagal dicatat di antrean anjungan..!!");
        }
    }

    /** Dipanggil tepat sebelum emptTeks() setelah simpan berhasil : tiket dilepas dari form. */
    public void sebelumKosongkan(){
        daritiket=(tiket!=null);
        if(daritiket){
            bukaKunci();
        }
    }

    /** Dipanggil tepat sesudah emptTeks() setelah simpan berhasil : daftar antrean dibuka lagi untuk tiket berikutnya. */
    public void sesudahKosongkan(){
        if(daritiket){
            daritiket=false;
            EventQueue.invokeLater(this::bukaAntrean);
        }
    }

    /** Cetak barcode : tiket anjungan (onsite, MJKN, website, booking) selesai dilayani FO. Pasien tanpa tiket tidak terpengaruh. */
    public void selesaikan(boolean tercetak){
        // saat mode tiket No.Rawat masih perkiraan, bisa milik pasien lain
        if(!aktif||(tiket!=null)){
            return;
        }
        TiketAnjungan aktiftiket=registrasi.tiketAktif(f.norawat.getText());
        if(aktiftiket==null){
            return;
        }
        if(!tercetak){
            JOptionPane.showMessageDialog(f.dialog,"Barcode belum tercetak, tiket anjungan "+aktiftiket.notiketfo+" belum diselesaikan..!!");
            return;
        }
        if(aktiftiket.isBpjs()&&Sequel.cariInteger("select count(bridging_sep.no_sep) from bridging_sep where bridging_sep.no_rawat=?",f.norawat.getText())==0&&
                JOptionPane.showConfirmDialog(f.dialog,"SEP pasien BPJS tiket "+aktiftiket.notiketfo+" belum ada di bridging.\nApakah tetap ingin menyelesaikan tiket ?",
                "Konfirmasi",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION){
            return;
        }
        if(!registrasi.selesaikan(aktiftiket.id,akses.getkode())){
            JOptionPane.showMessageDialog(f.dialog,"Barcode tercetak, tetapi tiket anjungan "+aktiftiket.notiketfo+" gagal diselesaikan..!!");
        }
        perbaruiJumlah();
    }

    /** Cetak label rawat jalan lewat API : label dianggap tercetak bila antripoli menjadi 1 (2 = sudah dilayani poli). */
    public void selesaikanLabelRajal(){
        if(aktif&&(tiket==null)){
            selesaikan(Sequel.cariInteger("select count(antripoli.no_rawat) from antripoli where antripoli.no_rawat=? and antripoli.status in ('1','2')",f.norawat.getText())>0);
        }
    }
}
