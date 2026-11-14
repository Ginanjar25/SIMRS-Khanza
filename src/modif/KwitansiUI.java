package modif;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.koneksiDB;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 *
 * Fungsi bersama form validasi kwitansi pemasukan lain
 * (DlgKwitansiNonClaim, DlgKwitansiSelisih, DlgPilihTindakan)
 */
public class KwitansiUI {
    private final Connection koneksi=koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;
    private List<Opsi> semua;
    private double biaya=0;
    private int jumlah=0;
    private File file;
    private FileWriter fileWriter;
    private String iyem;
    private final ObjectMapper mapper=new ObjectMapper();
    private JsonNode root;
    private JsonNode response;
    private FileReader myObj;

    // file cache daftar tindakan/paket NON CLAIM, diatur di DlgTindakanPemasukanLain
    public static final String CACHE_TINDAKAN="./cache/tindakanpemasukanlain.iyem";

    // daftar bawaan tindakan/paket NON CLAIM (kode, nama), dipakai jika file cache belum ada
    public static final String[][] TINDAKAN={
        {"J000100","ICT AntigenSarCov-2"},{"J000110","PCR"},{"J000116","Swab Antigen Karyawan"},
        {"J000102","Screening"},{"J000117","Swab Antigen Keluarga Inti"},{"J000029","Free T4"},
        {"RI00214","Pasang IUD ( Dokter )"},{"J000065","PA"},{"RI10224","Pijat Bayi 0-1 th"},
        {"RI00287","MOW"},{"RI10288","Paket Foto + Golongan Darah Bayi"},{"J000026","TSHs"},
        {"RI10064","Foto Bayi"},{"RI10287","Paket Foto + Pijat + Golongan Darah Bayi"},{"J000004","Golongan Darah"},
        {"RI10297","Paket 1"},{"RI10298","Paket 2"},{"RI10299","Paket 3"},
        {"RI10300","Paket 4"},{"RI10301","Paket 5"},{"RI10302","Paket 6"},
        {"RI10303","Paket 7"},{"RI00346","Pendampingan Operasi Keluarga"},
        {"RI10328","Pengambilan Dokumentasi Persalinan BPJS"},{"J000154","Screening TCM"}
    };

    // item combo : value disimpan, label ditampilkan
    public static class Opsi {
        public final String value,label;

        public Opsi(String value,String label){
            this.value=value;
            this.label=label;
        }

        @Override
        public String toString(){
            return label;
        }
    }

    // daftar tindakan/paket NON CLAIM (kode, nama) dari file cache, jika belum ada memakai daftar bawaan
    public List<String[]> daftarTindakan(){
        List<String[]> daftar=new ArrayList<>();
        if(!new File(CACHE_TINDAKAN).exists()){
            for(String[] tindakan:TINDAKAN){
                daftar.add(tindakan);
            }
        }else{
            try {
                myObj=new FileReader(CACHE_TINDAKAN);
                root=mapper.readTree(myObj);
                response=root.path("tindakanpemasukanlain");
                if(response.isArray()){
                    for(JsonNode list:response){
                        daftar.add(new String[]{list.path("Kode").asText(),list.path("Nama").asText()});
                    }
                }
                myObj.close();
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            }
        }
        return daftar;
    }

    // menyimpan daftar tindakan/paket NON CLAIM ke file cache
    public boolean simpanDaftarTindakan(List<String[]> daftar){
        iyem="";
        for(String[] tindakan:daftar){
            iyem=iyem+"{\"Kode\":\""+json(tindakan[0])+"\",\"Nama\":\""+json(tindakan[1])+"\"},";
        }
        if(iyem.length()>0){
            iyem=iyem.substring(0,iyem.length()-1);
        }
        try {
            file=new File(CACHE_TINDAKAN);
            file.getParentFile().mkdirs();
            file.createNewFile();
            fileWriter=new FileWriter(file);
            fileWriter.write("{\"tindakanpemasukanlain\":["+iyem+"]}");
            fileWriter.flush();
            fileWriter.close();
            iyem=null;
            return true;
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
            JOptionPane.showMessageDialog(null,"Gagal menyimpan daftar tindakan ke "+CACHE_TINDAKAN);
            return false;
        }
    }

    private String json(String teks){
        return teks.replace("\\","\\\\").replace("\"","\\\"");
    }

    // biaya tindakan/tambahan biaya di billing pasien, -1 jika tidak tepat satu baris
    public double cariBiaya(String norawat,String kodetdk){
        biaya=-1;
        jumlah=0;
        try {
            ps=koneksi.prepareStatement(
                "select periksa_lab.kd_jenis_prw,periksa_lab.biaya from periksa_lab where periksa_lab.no_rawat=? and substring(periksa_lab.kd_jenis_prw,1,7)=? "+
                "union select rawat_inap_dr.kd_jenis_prw,rawat_inap_dr.biaya_rawat from rawat_inap_dr where rawat_inap_dr.no_rawat=? and substring(rawat_inap_dr.kd_jenis_prw,1,7)=? "+
                "union select rawat_inap_pr.kd_jenis_prw,rawat_inap_pr.biaya_rawat from rawat_inap_pr where rawat_inap_pr.no_rawat=? and substring(rawat_inap_pr.kd_jenis_prw,1,7)=? "+
                "union select tambahan_biaya.nama_biaya,tambahan_biaya.besar_biaya from tambahan_biaya where tambahan_biaya.no_rawat=? and tambahan_biaya.nama_biaya=?");
            try {
                ps.setString(1,norawat);
                ps.setString(2,kodetdk);
                ps.setString(3,norawat);
                ps.setString(4,kodetdk);
                ps.setString(5,norawat);
                ps.setString(6,kodetdk);
                ps.setString(7,norawat);
                ps.setString(8,kodetdk);
                rs=ps.executeQuery();
                while(rs.next()){
                    biaya=rs.getDouble(2);
                    jumlah++;
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
        if(jumlah==1){
            return biaya;
        }else{
            return -1;
        }
    }

    // pasien rawat inap aktif, rawat jalan BPJS hari ini, dan ranap gabung
    public int tampilPasien(JComboBox cmb,String tglregistrasi){
        semua=new ArrayList<>();
        try {
            ps=koneksi.prepareStatement(
                "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,'RI' as stts,concat(bangsal.nm_bangsal,' ',kamar.kd_kamar) as lokasi,penjab.png_jawab "+
                "from kamar_inap inner join kamar on kamar.kd_kamar=kamar_inap.kd_kamar inner join bangsal on bangsal.kd_bangsal=kamar.kd_bangsal "+
                "inner join reg_periksa on reg_periksa.no_rawat=kamar_inap.no_rawat inner join pasien on pasien.no_rkm_medis=reg_periksa.no_rkm_medis "+
                "inner join penjab on penjab.kd_pj=reg_periksa.kd_pj where kamar_inap.stts_pulang='-' "+
                "union "+
                "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,'RJ' as stts,poliklinik.nm_poli as lokasi,penjab.png_jawab "+
                "from reg_periksa inner join pasien on pasien.no_rkm_medis=reg_periksa.no_rkm_medis inner join poliklinik on poliklinik.kd_poli=reg_periksa.kd_poli "+
                "inner join penjab on penjab.kd_pj=reg_periksa.kd_pj where reg_periksa.tgl_registrasi=? and reg_periksa.kd_pj='BPJ' "+
                "union "+
                "select ranap_gabung.no_rawat2,reg_periksa.no_rkm_medis,pasien.nm_pasien,'RI' as stts,bangsal.nm_bangsal as lokasi,penjab.png_jawab "+
                "from ranap_gabung inner join kamar_inap on kamar_inap.no_rawat=ranap_gabung.no_rawat inner join reg_periksa on reg_periksa.no_rawat=ranap_gabung.no_rawat2 "+
                "inner join pasien on pasien.no_rkm_medis=reg_periksa.no_rkm_medis inner join kamar on kamar.kd_kamar=kamar_inap.kd_kamar "+
                "inner join bangsal on bangsal.kd_bangsal=kamar.kd_bangsal inner join penjab on penjab.kd_pj=reg_periksa.kd_pj where kamar_inap.stts_pulang='-'");
            try {
                ps.setString(1,tglregistrasi);
                rs=ps.executeQuery();
                while(rs.next()){
                    semua.add(opsiPasien(" - "));
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
        isiCombo(cmb,semua);
        return semua.size();
    }

    // pasien yang keluar/pulang pada tanggal tertentu
    public int tampilPasienPulang(JComboBox cmb,String tglkeluar){
        semua=new ArrayList<>();
        try {
            ps=koneksi.prepareStatement(
                "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,'RI' as stts,concat(bangsal.nm_bangsal,' ',kamar.kd_kamar) as lokasi,penjab.png_jawab "+
                "from kamar_inap inner join kamar on kamar.kd_kamar=kamar_inap.kd_kamar inner join bangsal on bangsal.kd_bangsal=kamar.kd_bangsal "+
                "inner join reg_periksa on reg_periksa.no_rawat=kamar_inap.no_rawat inner join pasien on pasien.no_rkm_medis=reg_periksa.no_rkm_medis "+
                "inner join penjab on penjab.kd_pj=reg_periksa.kd_pj where kamar_inap.tgl_keluar=? and kamar_inap.tgl_keluar<>'0000-00-00' "+
                "union "+
                "select ranap_gabung.no_rawat2,pasien.no_rkm_medis,pasien.nm_pasien,'RI' as stts,concat(bangsal.nm_bangsal,' ',kamar.kd_kamar) as lokasi,penjab.png_jawab "+
                "from kamar_inap inner join ranap_gabung on ranap_gabung.no_rawat=kamar_inap.no_rawat inner join kamar on kamar.kd_kamar=kamar_inap.kd_kamar "+
                "inner join bangsal on bangsal.kd_bangsal=kamar.kd_bangsal inner join reg_periksa on reg_periksa.no_rawat=ranap_gabung.no_rawat2 "+
                "inner join pasien on pasien.no_rkm_medis=reg_periksa.no_rkm_medis inner join penjab on penjab.kd_pj=reg_periksa.kd_pj "+
                "where kamar_inap.tgl_keluar=? and kamar_inap.tgl_keluar<>'0000-00-00'");
            try {
                ps.setString(1,tglkeluar);
                ps.setString(2,tglkeluar);
                rs=ps.executeQuery();
                while(rs.next()){
                    semua.add(opsiPasien("-"));
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
        isiCombo(cmb,semua);
        return semua.size();
    }

    // data satu pasien berdasarkan no_rawat, null jika tidak ditemukan
    public Opsi cariPasien(String norawat){
        Opsi pasien=null;
        try {
            ps=koneksi.prepareStatement(
                "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,if(reg_periksa.status_lanjut='Ranap','RI','RJ') as stts,"+
                "ifnull((select concat(bangsal.nm_bangsal,' ',kamar.kd_kamar) from kamar_inap inner join kamar on kamar.kd_kamar=kamar_inap.kd_kamar "+
                "inner join bangsal on bangsal.kd_bangsal=kamar.kd_bangsal where kamar_inap.no_rawat=reg_periksa.no_rawat "+
                "order by kamar_inap.tgl_masuk desc,kamar_inap.jam_masuk desc limit 1),poliklinik.nm_poli) as lokasi,penjab.png_jawab "+
                "from reg_periksa inner join pasien on pasien.no_rkm_medis=reg_periksa.no_rkm_medis inner join penjab on penjab.kd_pj=reg_periksa.kd_pj "+
                "inner join poliklinik on poliklinik.kd_poli=reg_periksa.kd_poli where reg_periksa.no_rawat=?");
            try {
                ps.setString(1,norawat);
                rs=ps.executeQuery();
                if(rs.next()){
                    pasien=opsiPasien(" - ");
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
        return pasien;
    }

    // memilih pasien di combo, jika belum ada di daftar (misal sudah pulang) diambil dari database
    public void pilihPasien(JComboBox cmb,String norawat){
        if((norawat==null)||norawat.trim().equals("")){
            return;
        }
        List<Opsi> daftar=(List<Opsi>)cmb.getClientProperty("semua");
        if(daftar==null){
            daftar=new ArrayList<>();
        }
        Opsi dipilih=null;
        for(Opsi opsi:daftar){
            if(opsi.value.startsWith(norawat+"-")){
                dipilih=opsi;
                break;
            }
        }
        if(dipilih==null){
            dipilih=cariPasien(norawat);
            if(dipilih==null){
                return;
            }
            List<Opsi> daftarbaru=new ArrayList<>(daftar);
            daftarbaru.add(0,dipilih);
            isiCombo(cmb,daftarbaru);
        }
        cmb.setSelectedItem(dipilih);
    }

    // pasien yang benar-benar dipilih, null jika belum memilih (hanya mengetik)
    public Opsi pilihan(JComboBox cmb){
        if((cmb.getSelectedItem() instanceof Opsi)&&!((Opsi)cmb.getSelectedItem()).value.equals("")){
            return (Opsi)cmb.getSelectedItem();
        }else{
            return null;
        }
    }

    // combo bisa dicari seperti select2 : ketik nama/no.rm/ruang, panah atas/bawah lalu enter untuk memilih
    public void pasangPencarian(final JComboBox cmb){
        cmb.setEditable(true);
        cmb.putClientProperty("JComboBox.isTableCellEditor",Boolean.TRUE);
        final JTextField editor=(JTextField)cmb.getEditor().getEditorComponent();
        editor.addKeyListener(new KeyAdapter(){
            @Override
            public void keyReleased(KeyEvent evt){
                switch(evt.getKeyCode()){
                    case KeyEvent.VK_UP: case KeyEvent.VK_DOWN: case KeyEvent.VK_ENTER: case KeyEvent.VK_ESCAPE:
                    case KeyEvent.VK_LEFT: case KeyEvent.VK_RIGHT: case KeyEvent.VK_TAB: case KeyEvent.VK_SHIFT:
                    case KeyEvent.VK_CONTROL: case KeyEvent.VK_ALT: case KeyEvent.VK_HOME: case KeyEvent.VK_END:
                        break;
                    default:
                        saring(cmb,editor);
                        break;
                }
            }
        });
        editor.addFocusListener(new FocusAdapter(){
            @Override
            public void focusGained(FocusEvent evt){
                if(pilihan(cmb)==null){
                    editor.setText("");
                }
            }
        });
    }

    // value : no_rawat-nm_pasien-png_jawab-stts, label : stts - nm_pasien - no_rkm_medis - lokasi
    private Opsi opsiPasien(String pemisah) throws Exception{
        return new Opsi(rs.getString("no_rawat")+"-"+rs.getString("nm_pasien")+"-"+rs.getString("png_jawab")+"-"+rs.getString("stts"),
                rs.getString("stts")+pemisah+rs.getString("nm_pasien")+pemisah+rs.getString("no_rkm_medis")+pemisah+rs.getString("lokasi"));
    }

    private void isiCombo(JComboBox cmb,List<Opsi> daftar){
        cmb.putClientProperty("semua",daftar);
        DefaultComboBoxModel model=new DefaultComboBoxModel();
        model.addElement(new Opsi("","Pilih nama pasien"));
        for(Opsi opsi:daftar){
            model.addElement(opsi);
        }
        cmb.setModel(model);
    }

    // setiap kata yang diketik harus ada di label, tidak peka huruf besar/kecil
    private void saring(JComboBox cmb,JTextField editor){
        String teks=editor.getText();
        int caret=editor.getCaretPosition();
        String[] kata=teks.toLowerCase().trim().split("\\s+");
        DefaultComboBoxModel model=new DefaultComboBoxModel();
        List<Opsi> daftar=(List<Opsi>)cmb.getClientProperty("semua");
        if(daftar!=null){
            for(Opsi opsi:daftar){
                boolean cocok=true;
                for(String k:kata){
                    if(!opsi.label.toLowerCase().contains(k)){
                        cocok=false;
                        break;
                    }
                }
                if(cocok){
                    model.addElement(opsi);
                }
            }
        }
        model.setSelectedItem(null);
        cmb.setModel(model);
        editor.setText(teks);
        editor.setCaretPosition(Math.min(caret,teks.length()));
        cmb.hidePopup();
        if(model.getSize()>0){
            cmb.showPopup();
        }
    }

    // border judul seperti "::[ Pemasukan Lain-Lain ]::"
    public Border judul(String teks){
        return javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240,245,235)),teks,
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma",0,11),new java.awt.Color(50,50,50));
    }

    public widget.Label label(String teks,int x,int y,int w){
        widget.Label label=new widget.Label();
        label.setText(teks);
        label.setBounds(x,y,w,23);
        return label;
    }

    // textbox hanya baca untuk menampilkan informasi
    public widget.TextBox info(String teks,int x,int y,int w){
        widget.TextBox textbox=new widget.TextBox();
        textbox.setText(teks);
        textbox.setEditable(false);
        textbox.setHighlighter(null);
        textbox.setBounds(x,y,w,23);
        return textbox;
    }

    public widget.Button tombol(String teks,String ikon,char mnemonic,int w){
        widget.Button button=new widget.Button();
        button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/"+ikon)));
        button.setMnemonic(mnemonic);
        button.setText(teks);
        button.setToolTipText("Alt+"+mnemonic);
        button.setPreferredSize(new java.awt.Dimension(w,30));
        return button;
    }

    // tombol kecil berikon seperti BtnCari
    public widget.Button tombolKecil(String ikon,int x,int y){
        widget.Button button=new widget.Button();
        button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/"+ikon)));
        button.setBounds(x,y,28,23);
        return button;
    }

    // tanggal dd-MM-yyyy hari ini
    public widget.Tanggal tanggal(int x,int y){
        widget.Tanggal tanggal=new widget.Tanggal();
        tanggal.setDisplayFormat("dd-MM-yyyy");
        tanggal.setDate(new java.util.Date());
        tanggal.setOpaque(false);
        tanggal.setBounds(x,y,95,23);
        return tanggal;
    }

    public widget.CekBox cekBox(String teks,String nama){
        widget.CekBox cekbox=new widget.CekBox();
        cekbox.setText(teks);
        cekbox.setName(nama);
        return cekbox;
    }
}
