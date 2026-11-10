package modif;

import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

/**
 * Cetak kwitansi pemasukan lain-lain (pengganti billing/LaporanKeuangan.php).
 * Kwitansi memakai report/rptKwitansiPemasukanLainModif.jasper dan ditampilkan di JasperViewer.
 * Form validasi kategori NON CLAIM dan BIAYA SELISIH ada di DlgKwitansiNonClaim dan DlgKwitansiSelisih.
 */
public class CetakKwitansiPemasukanLain {
    public static final String NON_CLAIM="NON_CLAIM",SELISIH="SELISIH";
    private final Connection koneksi=koneksiDB.condb();
    private final sekuel Sequel=new sekuel();
    private final validasi Valid=new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private String jenis="";

    /**
     * Menentukan jenis form validasi untuk kategori.
     * @param kategori kode + nama kategori dengan spasi diganti "_"
     * @return NON_CLAIM, SELISIH, atau "" jika kwitansi langsung dicetak
     */
    public String jenisKategori(String kategori){
        jenis="";
        try {
            ps=koneksi.prepareStatement(
                "select kategori_pemasukan_lain.nama_kategori,replace(concat(kategori_pemasukan_lain.kode_kategori,' ',kategori_pemasukan_lain.nama_kategori),' ','_') "+
                "from kategori_pemasukan_lain where kategori_pemasukan_lain.nama_kategori like '%BIAYA SELISIH%' or kategori_pemasukan_lain.nama_kategori like '%NON CLAIM%'");
            try {
                rs=ps.executeQuery();
                while(rs.next()){
                    if(rs.getString(2).equals(kategori)){
                        if(rs.getString(1).toUpperCase().contains("BIAYA SELISIH")){
                            jenis=SELISIH;
                        }else if(rs.getString(1).toUpperCase().contains("NON CLAIM")){
                            jenis=NON_CLAIM;
                        }
                        break;
                    }
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
        return jenis;
    }

    /** Kwitansi untuk kategori tanpa form validasi. */
    public void cetak(String kode,String tanggal,String kategori,String petugas,String keterangan,String nominal){
        Map<String,Object> param=new HashMap<>();
        param.put("nokwitansi",kode);
        param.put("tanggal",tanggal);
        param.put("terimadari",keterangan);
        param.put("untuk",kategori);
        tampilkan(param,null,Valid.SetAngka(nominal),Sequel.cariIsi("select petugas.nip from petugas where petugas.nama=?",petugas),
                tanggal,new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
    }

    /**
     * Mengisi report kwitansi lalu menampilkannya.
     * @param param    nokwitansi, tanggal, norawat (opsional), terimadari, untuk (opsional)
     * @param rincian  baris rincian dengan key no, nama, biaya; boleh null
     * @param total    jumlah uang untuk terbilang dan nominal
     * @param nip      nip petugas yang menandatangani
     * @param tanggal  tanggal pada QR tanda tangan
     * @param tglcetak tanggal yang ditulis di samping kota
     */
    public void tampilkan(Map<String,Object> param,List<Map<String,?>> rincian,double total,String nip,String tanggal,String tglcetak){
        try {
            String namapetugas=Sequel.CariPetugas(nip);
            String finger=Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?",nip);
            param.put("namars",akses.getnamars());
            param.put("alamatrs",akses.getalamatrs()+", "+akses.getkabupatenrs()+", "+akses.getpropinsirs());
            param.put("kontakrs","Telp / Fax "+akses.getkontakrs()+", E-mail : "+akses.getemailrs());
            param.put("kota",akses.getkabupatenrs());
            param.put("logo",Sequel.cariGambar("select setting.logo from setting"));
            param.put("terbilang",Valid.terbilang(total).replaceAll("\\s+"," ").trim()+" rupiah");
            param.put("nominal",Valid.SetAngka(total));
            param.put("tglcetak",tglcetak);
            param.put("namattd",namapetugas);
            param.put("finger","Dikeluarkan di "+akses.getnamars()+", Kabupaten/Kota "+akses.getkabupatenrs()+
                    "\nDitandatangani secara elektronik oleh "+namapetugas+"\nID "+(finger.equals("")?nip:finger)+"\n"+tanggal);

            JasperPrint jasperPrint;
            if(rincian==null||rincian.isEmpty()){
                jasperPrint=JasperFillManager.fillReport("./report/rptKwitansiPemasukanLainModif.jasper",param,new JREmptyDataSource(0));
            }else{
                jasperPrint=JasperFillManager.fillReport("./report/rptKwitansiPemasukanLainModif.jasper",param,new JRMapCollectionDataSource(new ArrayList<Map<String,?>>(rincian)));
            }
            JasperViewer jasperViewer=new JasperViewer(jasperPrint,false);
            jasperViewer.setTitle("::[ Kwitansi Pemasukan Lain-Lain ]::");
            Dimension screen=Toolkit.getDefaultToolkit().getScreenSize();
            jasperViewer.setSize(screen.width-50,screen.height-50);
            jasperViewer.setModalExclusionType(Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);
            jasperViewer.setLocationRelativeTo(null);
            jasperViewer.setVisible(true);
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
            JOptionPane.showMessageDialog(null,"Report Can't view because : "+e);
        }
    }

    /** Satu baris rincian untuk detail report. */
    public Map<String,?> rincian(int no,String nama,double biaya){
        Map<String,Object> baris=new HashMap<>();
        baris.put("no",no);
        baris.put("nama",nama);
        baris.put("biaya",Valid.SetAngka(biaya));
        return baris;
    }
}
