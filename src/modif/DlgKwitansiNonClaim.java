package modif;

import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * Form validasi kwitansi kategori NON CLAIM
 * (pengganti "Validation Form Pemasukan Lain Lain" di LaporanKeuangan.php dan ValidLaporanKeuangan.php).
 */
public class DlgKwitansiNonClaim extends JDialog {
    private final Connection koneksi=koneksiDB.condb();
    private final sekuel Sequel=new sekuel();
    private final validasi Valid=new validasi();
    private final CetakKwitansiPemasukanLain kwitansi=new CetakKwitansiPemasukanLain();
    private PreparedStatement ps;
    private ResultSet rs;
    private final String kode,tanggal,petugas,nip,nominal;
    private String pasientambahan="";
    private final widget.ComboBox CmbPasien=new widget.ComboBox(),CmbNaik=new widget.ComboBox();
    private final KwitansiUI UI=new KwitansiUI();
    private final widget.Tanggal TglPulang=UI.tanggal(553,70);
    private final widget.PanelBiasa PanelTindakan=new widget.PanelBiasa(new GridLayout(0,3,5,2)),
            PanelTambahan=new widget.PanelBiasa(new GridLayout(0,3,5,2));
    private final List<widget.CekBox> ChkTindakan=new ArrayList<>(),ChkTambahan=new ArrayList<>();

    /**
     * @param kategori kode + nama kategori dengan spasi diganti "_"
     */
    public DlgKwitansiNonClaim(String kode,String tanggal,String kategori,String petugas,String nominal){
        super((java.awt.Frame)null,"Validation Form Pemasukan Lain Lain",true);
        this.kode=kode;
        this.tanggal=tanggal;
        this.petugas=petugas;
        this.nominal=nominal;
        this.nip=Sequel.cariIsi("select petugas.nip from petugas where petugas.nama=?",petugas);

        String hariini=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        UI.pasangPencarian(CmbPasien);
        UI.tampilPasien(CmbPasien,hariini);
        CmbPasien.addActionListener(evt->tampilTambahan());
        tampilNaik(hariini);
        for(String[] tindakan:UI.daftarTindakan()){
            widget.CekBox chk=UI.cekBox(tindakan[1],tindakan[0]);
            ChkTindakan.add(chk);
            PanelTindakan.add(chk);
        }

        initComponents(kategori);
        setSize(720,560);
        setLocationRelativeTo(null);
    }

    private void initComponents(String kategori){
        // Sama dengan PHP: explode('_',$kategori)[5]
        String[] bagian=kategori.split("_",-1);
        String carabayar=bagian.length>5?bagian[5]:"";

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        widget.InternalFrame internalFrame1=new widget.InternalFrame();
        internalFrame1.setBorder(UI.judul("::[ Validation Form Pemasukan Lain Lain ]::"));
        internalFrame1.setLayout(new BorderLayout(1,1));

        widget.PanelBiasa FormInput=new widget.PanelBiasa();
        FormInput.setLayout(null);
        FormInput.setPreferredSize(new Dimension(700,133));
        FormInput.add(UI.label("Cara Bayar :",0,10,95));
        FormInput.add(UI.info(carabayar,98,10,200));
        FormInput.add(UI.label("Petugas :",300,10,70));
        FormInput.add(UI.info(petugas,373,10,302));
        FormInput.add(UI.label("No. Pemasukan :",0,40,95));
        FormInput.add(UI.info(kode+" - Rp. "+Valid.SetAngka(Valid.SetAngka(nominal)),98,40,577));
        FormInput.add(UI.label("Pasien :",0,70,95));
        CmbPasien.setBounds(98,70,380,23);
        FormInput.add(CmbPasien);
        FormInput.add(UI.label("Pulang Tgl :",480,70,70));
        FormInput.add(TglPulang);
        widget.Button BtnCariPulang=UI.tombolKecil("accept.png",650,70);
        BtnCariPulang.setToolTipText("Cari pasien pulang");
        BtnCariPulang.addActionListener(evt->BtnCariPulangActionPerformed());
        FormInput.add(BtnCariPulang);
        FormInput.add(UI.label("Naik Kelas :",0,100,95));
        CmbNaik.setBounds(98,100,577,23);
        FormInput.add(CmbNaik);

        widget.ScrollPane ScrollTindakan=new widget.ScrollPane();
        ScrollTindakan.setOpaque(true);
        ScrollTindakan.setBorder(UI.judul("::[ Tindakan / Paket ]::"));
        ScrollTindakan.setViewportView(PanelTindakan);

        widget.ScrollPane ScrollTambahan=new widget.ScrollPane();
        ScrollTambahan.setOpaque(true);
        ScrollTambahan.setBorder(UI.judul("::[ Tambahan Biaya Pasien ]::"));
        ScrollTambahan.setPreferredSize(new Dimension(700,100));
        ScrollTambahan.setViewportView(PanelTambahan);

        JPanel PanelTengah=new JPanel(new BorderLayout(1,1));
        PanelTengah.setOpaque(false);
        PanelTengah.add(ScrollTindakan,BorderLayout.CENTER);
        PanelTengah.add(ScrollTambahan,BorderLayout.PAGE_END);

        widget.panelisi panelGlass8=new widget.panelisi();
        panelGlass8.setPreferredSize(new Dimension(44,44));
        panelGlass8.setLayout(new FlowLayout(FlowLayout.LEFT,5,9));
        widget.Button BtnCetak=UI.tombol("Update dan Cetak","b_print.png",'U',150);
        BtnCetak.addActionListener(evt->BtnCetakActionPerformed());
        widget.Button BtnKeluar=UI.tombol("Keluar","exit.png",'K',100);
        BtnKeluar.addActionListener(evt->dispose());
        panelGlass8.add(BtnCetak);
        panelGlass8.add(BtnKeluar);

        internalFrame1.add(FormInput,BorderLayout.PAGE_START);
        internalFrame1.add(PanelTengah,BorderLayout.CENTER);
        internalFrame1.add(panelGlass8,BorderLayout.PAGE_END);
        setContentPane(internalFrame1);
    }

    /** Pemasukan naik kelas milik petugas hari ini yang belum dipakai. */
    private void tampilNaik(String hariini){
        CmbNaik.addItem(new KwitansiUI.Opsi("","Pilih strip jika tidak ada biaya naik kelas"));
        CmbNaik.addItem(new KwitansiUI.Opsi("no","-"));
        try {
            ps=koneksi.prepareStatement(
                "select pemasukan_lain.no_masuk,pemasukan_lain.besar from pemasukan_lain where substring(pemasukan_lain.tanggal,1,10)=? and pemasukan_lain.nip=? "+
                "and pemasukan_lain.kode_kategori in ('04','03','02','28') and pemasukan_lain.keterangan='-' and pemasukan_lain.keperluan='-'");
            try {
                ps.setString(1,hariini);
                ps.setString(2,nip);
                rs=ps.executeQuery();
                while(rs.next()){
                    CmbNaik.addItem(new KwitansiUI.Opsi(rs.getString("no_masuk"),rs.getString("no_masuk")+" - Rp. "+Valid.SetAngka(rs.getDouble("besar"))));
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            } finally{
                if(rs!=null){
                    rs.close();
                }
                if(ps!=null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }

    /** Checkbox tambahan biaya milik pasien terpilih. */
    private void tampilTambahan(){
        KwitansiUI.Opsi pasien=UI.pilihan(CmbPasien);
        String pasienbaru=(pasien==null)?"":pasien.value;
        if(pasienbaru.equals(pasientambahan)){
            return;
        }
        pasientambahan=pasienbaru;
        for(widget.CekBox chk:ChkTambahan){
            PanelTambahan.remove(chk);
        }
        ChkTambahan.clear();
        if(pasien!=null){
            try {
                ps=koneksi.prepareStatement("select tambahan_biaya.nama_biaya,tambahan_biaya.besar_biaya from tambahan_biaya where tambahan_biaya.no_rawat=?");
                try {
                    ps.setString(1,norawat(pasien.value));
                    rs=ps.executeQuery();
                    while(rs.next()){
                        widget.CekBox chk=UI.cekBox(rs.getString("nama_biaya")+" - Rp. "+Valid.SetAngka(rs.getDouble("besar_biaya")),rs.getString("nama_biaya"));
                        ChkTambahan.add(chk);
                        PanelTambahan.add(chk);
                    }
                } catch (Exception e) {
                    System.out.println("Notifikasi : "+e);
                } finally{
                    if(rs!=null){
                        rs.close();
                    }
                    if(ps!=null){
                        ps.close();
                    }
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            }
        }
        PanelTambahan.revalidate();
        PanelTambahan.repaint();
    }

    /** Memilih pasien otomatis jika form pemasukan dibuka dari halaman lain (misal billing ranap). */
    public void setPasien(String norawat){
        UI.pilihPasien(CmbPasien,norawat);
    }

    private void BtnCariPulangActionPerformed(){
        String tgl=Valid.SetTgl(TglPulang.getSelectedItem()+"");
        if(UI.tampilPasienPulang(CmbPasien,tgl)==0){
            JOptionPane.showMessageDialog(null,"Tanggal "+tgl+" tidak ada pasien pulang");
        }
    }

    private void BtnCetakActionPerformed(){
        KwitansiUI.Opsi pasien=UI.pilihan(CmbPasien);
        KwitansiUI.Opsi naik=(KwitansiUI.Opsi)CmbNaik.getSelectedItem();
        List<String> kodetindakan=new ArrayList<>();
        for(widget.CekBox chk:ChkTindakan){
            if(chk.isSelected()){
                kodetindakan.add(chk.getName());
            }
        }
        for(widget.CekBox chk:ChkTambahan){
            if(chk.isSelected()){
                kodetindakan.add(chk.getName());
            }
        }

        if(pasien==null){
            Valid.textKosong(CmbPasien,"Pasien");
        }else if((naik==null)||naik.value.equals("")){
            JOptionPane.showMessageDialog(null,"Pilih strip jika tidak ada biaya naik kelas");
            CmbNaik.requestFocus();
        }else if(kodetindakan.isEmpty()){
            JOptionPane.showMessageDialog(null,"Pilih minimal satu tindakan");
        }else{
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            simpanDanCetak(pasien.value,naik.value,kodetindakan);
            setCursor(Cursor.getDefaultCursor());
        }
    }

    /** Port ValidLaporanKeuangan.php: cek tindakan di billing, update pemasukan, lalu cetak kwitansi. */
    private void simpanDanCetak(String datapasien,String naik,List<String> kodetindakan){
        String norawat=norawat(datapasien),namatindakan;
        boolean adanaik=!naik.equals("no");
        double nominalpemasukan=Valid.SetAngka(nominal),total=0,biaya,besarnaik=0;
        List<Map<String,?>> rincian=new ArrayList<>();
        StringBuilder keperluan=new StringBuilder();

        for(String kodetdk:kodetindakan){
            biaya=UI.cariBiaya(norawat,kodetdk);
            if(biaya>=0){
                namatindakan=cariNamaTindakan(norawat,kodetdk);
                rincian.add(kwitansi.rincian(rincian.size()+1,namatindakan,biaya));
                keperluan.append(namatindakan).append(", ");
                total=total+biaya;
            }
        }

        boolean normal=(rincian.size()==kodetindakan.size())&&(total==nominalpemasukan);
        boolean paket=(kodetindakan.size()==1)&&kodetindakan.get(0).equals("RI10287");
        if(!normal&&!paket){
            JOptionPane.showMessageDialog(null,"Terdapat tindakan yang dipilih tidak terdapat pada biling pasien/Nominal pemasukan tidak sesuai");
            return;
        }

        if(Sequel.mengedittf("pemasukan_lain","no_masuk=?","keterangan=?,keperluan=?",3,new String[]{
                datapasien,keperluan.toString(),kode
            })==false){
            return;
        }
        if(normal&&adanaik){
            besarnaik=Sequel.cariIsiAngka("select pemasukan_lain.besar from pemasukan_lain where pemasukan_lain.no_masuk=?",naik);
            Sequel.mengedittf("pemasukan_lain","no_masuk=?","keterangan=?,keperluan='Biaya Naik Kelas BPJS'",2,new String[]{
                datapasien,naik
            });
            rincian.add(kwitansi.rincian(rincian.size()+1,"Biaya Naik kelas BPJS",besarnaik));
        }

        String tglmasuk=Sequel.cariIsi("select pemasukan_lain.tanggal from pemasukan_lain where pemasukan_lain.no_masuk=?",kode);
        // keterangan: no_rawat-nm_pasien-png_jawab-stts
        String[] data=(Sequel.cariIsi("select pemasukan_lain.keterangan from pemasukan_lain where pemasukan_lain.no_masuk=?",kode)+"---").split("-",-1);
        Map<String,Object> param=new HashMap<>();
        param.put("nokwitansi",adanaik?kode+", "+naik:kode);
        param.put("tanggal",tglmasuk);
        param.put("norawat",data[0]+"/"+data[2]+"/"+data[3]);
        param.put("terimadari",data[1]);
        if(!normal){
            param.put("untuk",labelPaket(nominalpemasukan));
            rincian.clear();
        }
        kwitansi.tampilkan(param,rincian,(normal&&adanaik)?besarnaik+nominalpemasukan:nominalpemasukan,nip,tanggal,tglmasuk);
        dispose();
    }

    private String cariNamaTindakan(String norawat,String kodetdk){
        String nama="";
        try {
            ps=koneksi.prepareStatement(
                "select jns_perawatan_lab.nm_perawatan from jns_perawatan_lab where substring(jns_perawatan_lab.kd_jenis_prw,1,7)=? "+
                "union select jns_perawatan_inap.nm_perawatan from jns_perawatan_inap where substring(jns_perawatan_inap.kd_jenis_prw,1,7)=? "+
                "union select tambahan_biaya.nama_biaya from tambahan_biaya where tambahan_biaya.no_rawat=? and tambahan_biaya.nama_biaya=?");
            try {
                ps.setString(1,kodetdk);
                ps.setString(2,kodetdk);
                ps.setString(3,norawat);
                ps.setString(4,kodetdk);
                rs=ps.executeQuery();
                if(rs.next()){
                    nama=rs.getString(1);
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            } finally{
                if(rs!=null){
                    rs.close();
                }
                if(ps!=null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
        return nama;
    }

    private String labelPaket(double nominal){
        if(nominal==125000){
            return "- Paket Foto Bayi";
        }else if(nominal==60000){
            return "- Paket Pijat Bayi";
        }else if(nominal==15000){
            return "- Paket Golongan Darah";
        }
        return "Terdapat nominal yang salah input";
    }

    /** no_rawat = 17 karakter pertama data pasien (sama dengan substr di PHP). */
    private String norawat(String datapasien){
        return datapasien.length()>17?datapasien.substring(0,17):datapasien;
    }
}
