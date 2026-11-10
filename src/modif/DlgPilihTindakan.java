package modif;

import fungsi.koneksiDB;
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
 * Pilih tindakan/paket NON CLAIM dari billing pasien untuk mengisi nominal di DlgPemasukanLain.
 * Biaya diambil dari billing pasien (sama dengan pengecekan di DlgKwitansiNonClaim),
 * sehingga nominal pemasukan nanti sesuai saat validasi kwitansi.
 */
public class DlgPilihTindakan extends JDialog {
    private final Connection koneksi=koneksiDB.condb();
    private final validasi Valid=new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private final widget.ComboBox CmbPasien=new widget.ComboBox();
    private final KwitansiUI UI=new KwitansiUI();
    private final widget.Tanggal TglPulang=UI.tanggal(553,10);
    private final widget.PanelBiasa PanelTindakan=new widget.PanelBiasa(new GridLayout(0,3,5,2)),
            PanelTambahan=new widget.PanelBiasa(new GridLayout(0,3,5,2));
    private final List<widget.CekBox> ChkTindakan=new ArrayList<>(),ChkTambahan=new ArrayList<>();
    private final Map<widget.CekBox,Double> biaya=new HashMap<>();
    private final Map<widget.CekBox,String> nama=new HashMap<>();
    private final widget.Label LTotal=new widget.Label();
    private final widget.ScrollPane ScrollTindakan=new widget.ScrollPane(),ScrollTambahan=new widget.ScrollPane();
    private String pasienterpilih=null;
    private KwitansiUI.Opsi pasientetap;
    private double total=0;
    private boolean disimpan=false;

    /**
     * @param norawat no_rawat pasien jika dibuka dari halaman lain (misal billing ranap): pasien tetap,
     *                tanpa pilihan pasien. Kosong jika dibuka dari menu: pasien dipilih dari daftar.
     */
    public DlgPilihTindakan(String norawat){
        super((java.awt.Frame)null,"Pilih Tindakan / Paket",true);
        if((norawat!=null)&&!norawat.trim().equals("")){
            pasientetap=UI.cariPasien(norawat);
        }
        if(pasientetap==null){
            UI.pasangPencarian(CmbPasien);
            UI.tampilPasien(CmbPasien,new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            CmbPasien.addActionListener(evt->tampil());
        }
        initComponents();
        tampil();
    }

    private void initComponents(){
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        widget.InternalFrame internalFrame1=new widget.InternalFrame();
        internalFrame1.setBorder(UI.judul("::[ Pilih Tindakan / Paket ]::"));
        internalFrame1.setLayout(new BorderLayout(1,1));

        widget.PanelBiasa FormInput=new widget.PanelBiasa();
        FormInput.setLayout(null);
        FormInput.setPreferredSize(new Dimension(930,43));
        FormInput.add(UI.label("Pasien :",0,10,95));
        if(pasientetap!=null){
            // dari billing: pasien sudah pasti, cukup ditampilkan
            FormInput.add(UI.info(pasientetap.label,98,10,820));
        }else{
            CmbPasien.setBounds(98,10,380,23);
            FormInput.add(CmbPasien);
            FormInput.add(UI.label("Pulang Tgl :",480,10,70));
            FormInput.add(TglPulang);
            widget.Button BtnCariPulang=UI.tombolKecil("accept.png",650,10);
            BtnCariPulang.setToolTipText("Cari pasien pulang");
            BtnCariPulang.addActionListener(evt->BtnCariPulangActionPerformed());
            FormInput.add(BtnCariPulang);
        }

        ScrollTindakan.setOpaque(true);
        ScrollTindakan.setBorder(UI.judul("::[ Tindakan / Paket ]::"));
        ScrollTindakan.setViewportView(rapat(PanelTindakan));
        ScrollTindakan.getVerticalScrollBar().setUnitIncrement(16);

        ScrollTambahan.setOpaque(true);
        ScrollTambahan.setBorder(UI.judul("::[ Tambahan Biaya Pasien ]::"));
        ScrollTambahan.setViewportView(rapat(PanelTambahan));
        ScrollTambahan.getVerticalScrollBar().setUnitIncrement(16);

        JPanel PanelTengah=new JPanel(new BorderLayout(1,1));
        PanelTengah.setOpaque(false);
        PanelTengah.add(ScrollTindakan,BorderLayout.PAGE_START);
        PanelTengah.add(ScrollTambahan,BorderLayout.CENTER);

        widget.panelisi panelGlass8=new widget.panelisi();
        panelGlass8.setPreferredSize(new Dimension(44,44));
        panelGlass8.setLayout(new FlowLayout(FlowLayout.LEFT,5,9));
        widget.Button BtnSimpan=UI.tombol("Simpan","save-16x16.png",'S',100);
        BtnSimpan.addActionListener(evt->BtnSimpanActionPerformed());
        widget.Button BtnKeluar=UI.tombol("Keluar","exit.png",'K',100);
        BtnKeluar.addActionListener(evt->dispose());
        widget.Button BtnDaftar=UI.tombol("Daftar Tindakan","category.png",'D',150);
        BtnDaftar.setToolTipText("Alt+D, Atur daftar tindakan/paket pemasukan lain");
        BtnDaftar.addActionListener(evt->BtnDaftarActionPerformed());
        LTotal.setText("Total : Rp. 0");
        LTotal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LTotal.setPreferredSize(new Dimension(250,30));
        panelGlass8.add(BtnSimpan);
        panelGlass8.add(BtnDaftar);
        panelGlass8.add(BtnKeluar);
        panelGlass8.add(LTotal);

        internalFrame1.add(FormInput,BorderLayout.PAGE_START);
        internalFrame1.add(PanelTengah,BorderLayout.CENTER);
        internalFrame1.add(panelGlass8,BorderLayout.PAGE_END);
        setContentPane(internalFrame1);
    }

    /** Menampilkan checkbox tindakan/paket dan tambahan biaya beserta biaya di billing pasien terpilih. */
    private void tampil(){
        KwitansiUI.Opsi pasien=pasienAktif();
        String pasienbaru=(pasien==null)?"":pasien.value;
        if(pasienbaru.equals(pasienterpilih)){
            return;
        }
        pasienterpilih=pasienbaru;
        String norawat=pasienbaru.length()>17?pasienbaru.substring(0,17):pasienbaru;

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        PanelTindakan.removeAll();
        PanelTambahan.removeAll();
        ChkTindakan.clear();
        ChkTambahan.clear();
        biaya.clear();
        nama.clear();
        if(!norawat.equals("")){
            // hanya tindakan/paket yang ditemukan di billing pasien
            for(String[] tindakan:UI.daftarTindakan()){
                double biayatindakan=UI.cariBiaya(norawat,tindakan[0]);
                if(biayatindakan>=0){
                    widget.CekBox chk=UI.cekBox(tindakan[1]+" - Rp. "+Valid.SetAngka(biayatindakan),tindakan[0]);
                    biaya.put(chk,biayatindakan);
                    nama.put(chk,tindakan[1]);
                    chk.addActionListener(evt->hitungTotal());
                    ChkTindakan.add(chk);
                    PanelTindakan.add(chk);
                }
            }
        }
        if(ChkTindakan.isEmpty()){
            PanelTindakan.add(keterangan(norawat.equals("")?"Pilih pasien terlebih dahulu":"Tidak ada tindakan/paket di billing pasien"));
        }

        if(!norawat.equals("")){
            try {
                ps=koneksi.prepareStatement("select tambahan_biaya.nama_biaya,tambahan_biaya.besar_biaya from tambahan_biaya where tambahan_biaya.no_rawat=?");
                try {
                    ps.setString(1,norawat);
                    rs=ps.executeQuery();
                    while(rs.next()){
                        widget.CekBox chk=UI.cekBox(rs.getString("nama_biaya")+" - Rp. "+Valid.SetAngka(rs.getDouble("besar_biaya")),rs.getString("nama_biaya"));
                        biaya.put(chk,rs.getDouble("besar_biaya"));
                        nama.put(chk,rs.getString("nama_biaya"));
                        chk.addActionListener(evt->hitungTotal());
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
        if(ChkTambahan.isEmpty()){
            PanelTambahan.add(keterangan(norawat.equals("")?"Pilih pasien terlebih dahulu":"Tidak ada tambahan biaya pasien"));
        }
        PanelTindakan.revalidate();
        PanelTindakan.repaint();
        PanelTambahan.revalidate();
        PanelTambahan.repaint();
        hitungTotal();
        aturUkuran();
        setCursor(Cursor.getDefaultCursor());
    }

    // panel checkbox ditaruh di atas agar baris tidak meregang mengikuti tinggi jendela
    private JPanel rapat(JPanel panel){
        JPanel wadah=new JPanel(new BorderLayout());
        wadah.setOpaque(false);
        wadah.add(panel,BorderLayout.PAGE_START);
        return wadah;
    }

    // tinggi bagian tindakan/paket dan tambahan biaya mengikuti jumlah checkbox yang tampil,
    // jika terlalu banyak diberi batas lalu bisa di-scroll. Tinggi jendela ikut menyesuaikan.
    private void aturUkuran(){
        ScrollTindakan.setPreferredSize(new Dimension(930,tinggi(ScrollTindakan,PanelTindakan,110,400)));
        ScrollTambahan.setPreferredSize(new Dimension(930,tinggi(ScrollTambahan,PanelTambahan,80,200)));
        pack();
        setSize(950,getHeight());
        setLocationRelativeTo(null);
    }

    // tinggi isi + padding bawah, dibatasi minimal dan maksimal
    private int tinggi(widget.ScrollPane scroll,JPanel panel,int minimal,int maksimal){
        return Math.max(minimal,Math.min(panel.getPreferredSize().height+scroll.getInsets().top+scroll.getInsets().bottom+20,maksimal));
    }

    private widget.Label keterangan(String teks){
        widget.Label label=new widget.Label();
        label.setText(teks);
        return label;
    }

    private void hitungTotal(){
        total=0;
        for(Map.Entry<widget.CekBox,Double> entry:biaya.entrySet()){
            if(entry.getKey().isSelected()){
                total=total+entry.getValue();
            }
        }
        LTotal.setText("Total : Rp. "+Valid.SetAngka(total));
    }

    private void BtnCariPulangActionPerformed(){
        String tgl=Valid.SetTgl(TglPulang.getSelectedItem()+"");
        if(UI.tampilPasienPulang(CmbPasien,tgl)==0){
            JOptionPane.showMessageDialog(null,"Tanggal "+tgl+" tidak ada pasien pulang");
        }
        tampil();
    }

    private void BtnDaftarActionPerformed(){
        DlgTindakanPemasukanLain daftar=new DlgTindakanPemasukanLain();
        daftar.setVisible(true);
        if(daftar.isDiubah()){
            // muat ulang checkbox sesuai daftar terbaru
            pasienterpilih="-";
            tampil();
        }
    }

    private void BtnSimpanActionPerformed(){
        if(pasienAktif()==null){
            Valid.textKosong(CmbPasien,"Pasien");
        }else if(total<=0){
            JOptionPane.showMessageDialog(null,"Pilih minimal satu tindakan/paket");
        }else{
            disimpan=true;
            dispose();
        }
    }

    /** Pasien tetap (dari billing) atau pasien yang dipilih di daftar. */
    private KwitansiUI.Opsi pasienAktif(){
        return pasientetap!=null?pasientetap:UI.pilihan(CmbPasien);
    }

    /** true jika pengguna menekan Simpan. */
    public boolean isDisimpan(){
        return disimpan;
    }

    /** Total nominal tindakan/paket yang dicentang. */
    public double getTotal(){
        return total;
    }

    /** Nama tindakan/paket yang dicentang, dipisah koma. */
    public String getNamaTindakan(){
        StringBuilder hasil=new StringBuilder();
        for(widget.CekBox chk:ChkTindakan){
            if(chk.isSelected()){
                hasil.append(hasil.length()>0?", ":"").append(nama.get(chk));
            }
        }
        for(widget.CekBox chk:ChkTambahan){
            if(chk.isSelected()){
                hasil.append(hasil.length()>0?", ":"").append(nama.get(chk));
            }
        }
        return hasil.toString();
    }
}
