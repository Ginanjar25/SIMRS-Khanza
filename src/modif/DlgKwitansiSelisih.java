package modif;

import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

/**
 * Form validasi kwitansi kategori BIAYA SELISIH
 * (pengganti "Validation form 2" di LaporanKeuangan.php dan ValidLaporanKeuangan2.php).
 */
public class DlgKwitansiSelisih extends JDialog {
    private final Connection koneksi=koneksiDB.condb();
    private final sekuel Sequel=new sekuel();
    private final validasi Valid=new validasi();
    private final CetakKwitansiPemasukanLain kwitansi=new CetakKwitansiPemasukanLain();
    private PreparedStatement ps;
    private ResultSet rs;
    private final String kode,tanggal,kategori,petugas,nip,nominal;
    private final widget.ComboBox CmbPasien=new widget.ComboBox();
    private final KwitansiUI UI=new KwitansiUI();
    private final widget.Tanggal TglPulang=UI.tanggal(553,70);

    /**
     * @param kategori kode + nama kategori dengan spasi diganti "_"
     */
    public DlgKwitansiSelisih(String kode,String tanggal,String kategori,String petugas,String nominal){
        super((java.awt.Frame)null,"Validation form 2",true);
        this.kode=kode;
        this.tanggal=tanggal;
        this.kategori=kategori;
        this.petugas=petugas;
        this.nominal=nominal;
        this.nip=Sequel.cariIsi("select petugas.nip from petugas where petugas.nama=?",petugas);

        UI.pasangPencarian(CmbPasien);
        UI.tampilPasien(CmbPasien,new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        initComponents();
        setSize(720,200);
        setLocationRelativeTo(null);
    }

    private void initComponents(){
        // Sama dengan PHP: elemen terakhir explode('_',$kategori)
        String[] bagian=kategori.split("_");
        String carabayar=bagian.length>0?bagian[bagian.length-1]:kategori;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        widget.InternalFrame internalFrame1=new widget.InternalFrame();
        internalFrame1.setBorder(UI.judul("::[ Validation Form Pemasukan Lain Lain ]::"));
        internalFrame1.setLayout(new BorderLayout(1,1));

        widget.PanelBiasa FormInput=new widget.PanelBiasa();
        FormInput.setLayout(null);
        FormInput.setPreferredSize(new Dimension(700,103));
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

        widget.panelisi panelGlass8=new widget.panelisi();
        panelGlass8.setPreferredSize(new Dimension(44,44));
        panelGlass8.setLayout(new FlowLayout(FlowLayout.LEFT,5,9));
        widget.Button BtnCetak=UI.tombol("Update dan Cetak","b_print.png",'U',150);
        BtnCetak.addActionListener(evt->BtnCetakActionPerformed());
        widget.Button BtnKeluar=UI.tombol("Keluar","exit.png",'K',100);
        BtnKeluar.addActionListener(evt->dispose());
        panelGlass8.add(BtnCetak);
        panelGlass8.add(BtnKeluar);

        internalFrame1.add(FormInput,BorderLayout.CENTER);
        internalFrame1.add(panelGlass8,BorderLayout.PAGE_END);
        setContentPane(internalFrame1);
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
        if(pasien==null){
            Valid.textKosong(CmbPasien,"Pasien");
            return;
        }
        // ValidLaporanKeuangan2.php: keterangan=data pasien, keperluan=kategori
        if(Sequel.mengedittf("pemasukan_lain","no_masuk=?","keterangan=?,keperluan=?",3,new String[]{
                pasien.value,kategori,kode
            })==false){
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            ps=koneksi.prepareStatement("select pemasukan_lain.no_masuk,pemasukan_lain.tanggal,pemasukan_lain.keterangan,pemasukan_lain.keperluan,pemasukan_lain.besar from pemasukan_lain where pemasukan_lain.no_masuk=?");
            try {
                ps.setString(1,kode);
                rs=ps.executeQuery();
                if(rs.next()){
                    // keterangan: no_rawat-nm_pasien-png_jawab-stts
                    String[] data=(rs.getString("keterangan")+"---").split("-",-1);
                    Map<String,Object> param=new HashMap<>();
                    param.put("nokwitansi",rs.getString("no_masuk"));
                    param.put("tanggal",rs.getString("tanggal"));
                    param.put("norawat",data[0]+"/"+data[2]+"/"+data[3]);
                    param.put("terimadari",data[1]);
                    param.put("untuk",rs.getString("keperluan"));
                    kwitansi.tampilkan(param,null,rs.getDouble("besar"),nip,tanggal,rs.getString("tanggal"));
                    dispose();
                }else{
                    JOptionPane.showMessageDialog(null,"Data pemasukan "+kode+" tidak ditemukan");
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
        setCursor(Cursor.getDefaultCursor());
    }
}
