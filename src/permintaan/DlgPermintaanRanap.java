package permintaan;

import bridging.BPJSCekReferensiPenyakit;
import bridging.BPJSDataSEP;
import bridging.BPJSSPRI;
import fungsi.BackgroundMusic;
import fungsi.WarnaTable;
import fungsi.WarnaTablePermintaanRanap;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariDokter;
import keuangan.DlgKamar;
import laporan.DlgCariPenyakit;
import modif.DlgCariKamar;
import simrskhanza.DlgKamarInap;
import rekammedis.RMRiwayatPerawatan;
import simrskhanza.DlgCariPasien;
import surat.SuratPernyataanMemilihDPJP;
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
//    private DlgKamar kamar=new DlgKamar(null,false);
    private DlgCariKamar kamar=new DlgCariKamar(null,false);
    private boolean aktif=false;
    private BackgroundMusic music;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean ceksukses = false;
    private BPJSCekReferensiPenyakit penyakitvclaim=new BPJSCekReferensiPenyakit(null,false);
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
        
        tabMode=new DefaultTableModel(null,new Object[]{
                "No.Rawat","No.RM","Nama Pasien","J.K.","Umur","No.Telp","Cara Bayar","Asal Poli/Unit","Dokter Yang Memeriksa",
                "Tanggal","No.Bad/Kamar","Kode Bangsal","Kamar Diminta","Tarif Kamar","Diagnosa Awal","Alasan Dirawat","Catatan",
                "KodeDokter","Dokter DPJP","Biometric"
            }){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        tbObat.setModel(tabMode);

        //tbObat.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbObat.getBackground()));
        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 19; i++) {
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
            } else if (i == 16 || i == 17 || i == 18|| i == 19) {
                column.setMinWidth(0);
                column.setMaxWidth(0);
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTablePermintaanRanap());

        NoRw.setDocument(new batasInput((byte)17).getKata(NoRw));
        TCari.setDocument(new batasInput((byte)100).getKata(TCari));
        Catatan.setDocument(new batasInput((int)150).getKata(Catatan));
        Diagnosa.setDocument(new batasInput((byte)50).getKata(Diagnosa));
        AlasanDirawat.setDocument(new batasInput((byte)50).getKata(AlasanDirawat));
        
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

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void NoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoRwKeyPressed
        //Valid.pindah(evt,Status,KdDokter);
        
}//GEN-LAST:event_NoRwKeyPressed

    private void DTPTglKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DTPTglKeyPressed
        Valid.pindah(evt,TCari,Diagnosa);
}//GEN-LAST:event_DTPTglKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (NoRw.getText().trim().equals("") || NoRM.getText().trim().equals("") || NmPasien.getText().trim().equals("") || TDokter.getText().trim().equals("")) {
            Valid.textKosong(TCari, "Pasien");
        } else if (KdBangsal.getText().trim().equals("") || KdKamar.getText().trim().equals("") || NmBangsal.getText().trim().equals("")) {
            Valid.textKosong(btnKamar, "Kamar/Bangsal");
        } else if (Diagnosa.getText().trim().equals("")) {
            Valid.textKosong(Diagnosa, "Diagnosa");
        } else if (Sequel.cariIsi("select no_rawat2 from ranap_gabung where no_rawat2 = ?", NoRw.getText()).equals(NoRw.getText())) {
            JOptionPane.showMessageDialog(null, "Pasien dengan No. RM : " + NoRM.getText() + " " + NmPasien.getText() + " Sudah terdaftar di Ranap Gabung Ibu & Bayi ");
        } else if (KdKamar.getText().trim().equals("-") || KdBangsal.getText().trim().equals("IGD")) {
            JOptionPane.showMessageDialog(null, "Kamar Inap belum dipilih !!");
        } else {
            if (R3.isSelected()) {
                BtnEditActionPerformed(null);
            } else {
                String titip_kamar = "-";
                if (ChkAccor2.isSelected()) {
                    titip_kamar = "Titip";
                } else {
                    titip_kamar = "-";
                }
                if (Sequel.menyimpantf("permintaan_ranap", "?,?,?,?,?,?", "Pasien", 5, new String[]{
                    NoRw.getText(),
                    Valid.SetTgl(DTPTgl.getSelectedItem() + ""),
                    KdKamar.getText(),
                    Diagnosa.getText(),
                    AlasanDirawat.getText(),
//                    Catatan.getText() + "#" + titip_kamar
                    "IGD~"+CatatanIGD.getText()+"~FO~"+Catatan.getText()+"#"+titip_kamar
                }) == true) {
                    Sequel.mengedit("kamar", "kd_kamar=?", "status='DIBOOKING'", 1, new String[]{KdKamar.getText()});
                    if (Sequel.menyimpantf("dpjp_ranap", "?,?", "Dokter", 2, new String[]{
                        NoRw.getText(),
                        KdDokter1.getText()
                    }) == true) {
                        System.out.println("simpan dpjp Berhasil" + NoRw.getText() + KdDokter1.getText());
                        emptTeks();
                    }
                    runBackground(() ->tampil());
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
        if (Sequel.cariInteger("select count(no_rawat) from kamar_inap where no_rawat = ?", NoRw.getText()) >= 1) {
            JOptionPane.showMessageDialog(null, "Pasien sudah masuk Kamar Inap !!");
        } else {
            int reply = JOptionPane.showConfirmDialog(rootPane, "Apakah Anda Ingin Menghapus Permintaan Ranap : \n"
                    + "No Rawat: " + NoRw.getText() + "/ Nama: " + NmPasien.getText() + "(" + NoRM.getText() + ")", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                if (R1.isSelected()) {
                    String titip_kamar = "-";
                    if (ChkAccor2.isSelected()) {
                        titip_kamar = "Titip";
                    } else {
                        titip_kamar = "-";
                    }
                    if (Sequel.mengedittf("permintaan_ranap", "no_rawat=?", "no_rawat=?,tanggal=?,kd_kamar=?,diagnosa=?,catatan=?", 6, new String[]{
                        NoRw.getText(), Valid.SetTgl(DTPTgl.getSelectedItem() + ""), "-", Diagnosa.getText(), "IGD~" + CatatanIGD.getText(), tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString()
                    }) == true) {
                        Sequel.mengedit("kamar", "kd_kamar=?", "status='KOSONG'", 1, new String[]{KdKamar.getText()});
                        tampil();
                        emptTeks();
                    }
                } else {
                    if (Valid.hapusTabletf(tabMode, NoRw, "permintaan_ranap", "no_rawat") == true) {
                        if (Sequel.meghapustf("dpjp_ranap", "no_rawat", NoRw.getText()) == true) {
                            Sequel.mengedit("kamar", "kd_kamar=?", "status='KOSONG'", 1, new String[]{KdKamar.getText()});
                            Sequel.meghapustf("ranap_gabung", "no_rawat", NoRw.getText());
                            tabMode.removeRow(tbObat.getSelectedRow());
                        }
                        //            Valid.hapusTable(tabMode, NoRw, "dpjp_ranap", "no_rawat");
                        //            Valid.hapusTable(tabMode, NoRawatGabung, "dpjp_ranap", "no_rawat");
                        //            Valid.hapusTable(tabMode, NoRw, "ranap_gabung", "no_rawat");
                        //            Valid.hapusTable(tabMode, NoRawatGabung, "reg_periksa", "no_rawat");
                        emptTeks();
                        tampil();
                    }
                }

            }
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
                    "bangsal.nm_bangsal,kamar.trf_kamar,permintaan_ranap.diagnosa,permintaan_ranap.alasan,permintaan_ranap.catatan from permintaan_ranap "+
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
                    "bangsal.nm_bangsal,kamar.trf_kamar,permintaan_ranap.diagnosa,permintaan_ranap.alasan,permintaan_ranap.catatan from permintaan_ranap "+
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
        runBackground(() ->tampil());
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
        runBackground(() ->tampil());
}//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            TCari.setText("");
            runBackground(() ->tampil());
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
        } else if(Sequel.cariInteger("select count(no_rawat) from kamar_inap where no_rawat = ?", NoRw.getText()) > 0){
            JOptionPane.showMessageDialog(null,"Permintaan tidak dapat di ubah, Pasien sudah masuk kamar inap !");
        }else {
            if (tbObat.getSelectedRow() > -1) {
                String titip_kamar = "-";
                if (ChkAccor2.isSelected()) {
                    titip_kamar = "Titip";
                } else {
                    titip_kamar = "-";
                }
                String tanggal=Valid.SetTgl(DTPTgl.getSelectedItem() + "");
                if (R3.isSelected()) {
                    tanggal = dateFormat2.format(new Date());
                }
                if (Sequel.mengedittf("permintaan_ranap", "no_rawat=?", "no_rawat=?,tanggal=?,kd_kamar=?,diagnosa=?,alasan=?,catatan=?", 7, new String[]{
                    NoRw.getText(), tanggal, KdKamar.getText(), Diagnosa.getText(), AlasanDirawat.getText(), "IGD~"+CatatanIGD.getText()+"~FO~"+Catatan.getText()+"#"+titip_kamar, tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString()
                }) == true) {
                    if(!tbObat.getValueAt(tbObat.getSelectedRow(),10).toString().equals(KdKamar.getText())){
                        Sequel.mengedit("kamar", "kd_kamar=?", "status='KOSONG'", 1, new String[]{tbObat.getValueAt(tbObat.getSelectedRow(),10).toString()});
                        Sequel.mengedit("kamar", "kd_kamar=?", "status='DIBOOKING'", 1, new String[]{KdKamar.getText()});
                        Sequel.mengedit("kamar_inap", "no_rawat=? and kd_kamar=?", "diagnosa_awal=?", 3, new String[]{Diagnosa.getText(),NoRw.getText(),KdKamar.getText()});
                    }                   
                    Sequel.mengedittf("dpjp_ranap", "no_rawat=?", "kd_dokter=?", 2, new String[]{KdDokter1.getText(), NoRw.getText()});
                    runBackground(() ->tampil());
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
        if(koneksiDB.CARICEPAT().equals("aktif")){
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        runBackground(() ->tampil());
                    }
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        runBackground(() ->tampil());
                    }
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        runBackground(() ->tampil());
                    }
                }
            });
        }
    }//GEN-LAST:event_formWindowOpened

    private void btnKamarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKamarActionPerformed
        DlgKamar kamar=new DlgKamar(null,false);
        kamar.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if (kamar.getTable().getSelectedRow() != -1) {
                    if (kamar.getTable().getValueAt(kamar.getTable().getSelectedRow(), 6).toString().equals("KOSONG")) {
                        KdKamar.setText(kamar.getTable().getValueAt(kamar.getTable().getSelectedRow(), 1).toString());
                        KdBangsal.setText(kamar.getTable().getValueAt(kamar.getTable().getSelectedRow(), 2).toString());
                        NmBangsal.setText(kamar.getTable().getValueAt(kamar.getTable().getSelectedRow(), 3).toString() + " ( " + kamar.getTable().getValueAt(kamar.getTable().getSelectedRow(), 4).toString() + ")");
                        HargaKamar.setText(Valid.SetAngka(Valid.SetAngka(kamar.getTable().getValueAt(kamar.getTable().getSelectedRow(), 5).toString())));
                    }else if(kamar.getTable().getValueAt(kamar.getTable().getSelectedRow(), 6).toString().equals("ISI")){
                        JOptionPane.showMessageDialog(null, "Maaf, Kamar Yang dipilih berstatus ISI");
                    }else{
                        JOptionPane.showMessageDialog(null, "Maaf, Kamar Yang dipilih berstatus DIBOOKING");
                    }
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
//        kamar.load();
        kamar.isCek();
        kamar.emptTeks();
        kamar.tampil2();
        kamar.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        kamar.setLocationRelativeTo(internalFrame1);
        kamar.setVisible(true);        
    }//GEN-LAST:event_btnKamarActionPerformed

    private void DiagnosaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DiagnosaKeyPressed
        Valid.pindah(evt,DTPTgl,AlasanDirawat);
    }//GEN-LAST:event_DiagnosaKeyPressed

    private void CatatanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CatatanKeyPressed
        Valid.pindah(evt,AlasanDirawat,BtnSimpan);
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
    }//GEN-LAST:event_formWindowClosed

    private void btnDiagnosaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiagnosaActionPerformed
        if (Sequel.cariIsi("select kd_pj from reg_periksa where no_rawat = ?", NoRw.getText()).equals("BPJ")) {
            penyakitvclaim.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            penyakitvclaim.setLocationRelativeTo(internalFrame1);
            penyakitvclaim.setVisible(true);
        } else {
            DlgCariPenyakit penyakit=new DlgCariPenyakit(null,false);
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
        penyakit.isCek();
            penyakit.emptTeks();
            penyakit.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            penyakit.setLocationRelativeTo(internalFrame1);
            penyakit.setVisible(true);
        }
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
        } else if((Sequel.cariIsi("select concat(no_rawat, tanggal, kd_kamar, diagnosa, catatan) from permintaan_ranap where no_rawat = ?", NoRw.getText())).isBlank()){
            JOptionPane.showMessageDialog(null,"Permintaan Ranap tidak tersedia, silahkan hubungi pendaftaran!!");
        } else if(Sequel.cariIsi("select no_rawat2 from ranap_gabung where no_rawat2 = ?", NoRw.getText()).equals(NoRw.getText())){
                JOptionPane.showMessageDialog(null, "Pasien dengan No. RM : \n" + NoRM.getText()+" " + NmPasien.getText() + " No Rawat : "+NoRw.getText() +" /nSudah terdaftar di Ranap Gabung Ibu & Bayi. ");
        }else if(Sequel.cariInteger("select count(no_rawat) from kamar_inap where no_rawat = ?", NoRw.getText()) > 0){
                JOptionPane.showMessageDialog(null,"Pasien sudah masuk kamar inap !");
                tampil();
        }else {
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
                                hargakmr2 = rs.getString(1).replaceAll("[^0-9]", ""); 
                            } else {
                                //TTarif.setText(hargakamar + "");
                                hargakmr2 = HargaKamar.getText().replaceAll("[^0-9]", ""); 
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
//                            + Valid.SetTgl(DTPTgl.getSelectedItem() + "") + "','"
//                            + tbObat.getValueAt(tbObat.getSelectedRow(),9).toString() + "','"
                            + dateFormat2.format(new Date()) + "','"
                            + now.substring(11, 13) + ":" + now.substring(14, 16) + ":" + now.substring(17, 19)
                            + "','0000-00-00','00:00:00','" + jml_hari + "','"
                            + hargakmr2 + "','-'", "No.Rawat") == true) {
                        Sequel.mengedit("reg_periksa", "no_rawat='" + NoRw.getText() + "'", "status_lanjut='Ranap'");
                        Sequel.mengedit("kamar", "kd_kamar='" + KdKamar.getText() + "'", "status='ISI'");

                        String titip_kamar = "-";
                        if (ChkAccor2.isSelected()) {
                            titip_kamar = "Titip";
                        } else {
                            titip_kamar = "-";
                        }
                        
                        Sequel.menyimpan("side_db.detail_pindah_kamar",
                                "'" + NoRw.getText() + "','" + KdKamar.getText() + "','" + dateFormat2.format(new Date()) + "','" + now.substring(11, 13) + ":" + now.substring(14, 16) + ":" + now.substring(17, 19) + "','" + dateFormat2.format(new Date()) + "','" + hargakmr2 + "','"+titip_kamar+"','-'", "No.Rawat");
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
                        "bangsal.nm_bangsal,kamar.trf_kamar,permintaan_ranap.diagnosa,permintaan_ranap.alasan,permintaan_ranap.catatan,reg_periksa.kd_dokter from permintaan_ranap "+
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
                this.setCursor(Cursor.getDefaultCursor());
            }else{
                JOptionPane.showMessageDialog(null,"Maaf, silahkan pilih data...!!!!");
            }      
        }
    }//GEN-LAST:event_BtnPersetujuanRanapActionPerformed

    private void BtnSuratPRI1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSuratPRI1ActionPerformed
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
            param.put("nama_pasien", Valid.singkatNama(Sequel.cariIsi("select pasien.nm_pasien from reg_periksa inner join pasien on pasien.no_rkm_medis = reg_periksa.no_rkm_medis where reg_periksa.no_rawat = ?", NoRw.getText())));
            Valid.MyReport("rptBarcodeRawat4_2.jasper", param, "::[ Label ]::");
            this.setCursor(Cursor.getDefaultCursor());           
        }
    }//GEN-LAST:event_BtnSuratPRI1ActionPerformed

    private void BtnSuratPRI2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSuratPRI2ActionPerformed
       if (tabMode.getRowCount() == 0 || NoRw.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, data kamar inap pasien sudah habis...!!!!");
            //BtnIn.requestFocus();
        } else {
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
                param.put("no_rawat", NoRw.getText());
                param.put("kls_bpjs", Sequel.cariIsi("select bsep.klsrawat FROM bridging_sep bsep WHERE bsep.no_rawat =? ", NoRw.getText()));
                param.put("penjab", CaraBayar.getText());
                param.put("dpjp", Sequel.cariIsi("select dokter.nm_dokter from dpjp_ranap inner join dokter on dpjp_ranap.kd_dokter=dokter.kd_dokter where dpjp_ranap.no_rawat=? ", NoRw.getText()));
                param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
                Valid.MyReportqry("rptGelangPasienDewasa.jasper", "report", "::[ Gelang Pasien ]::", "select pasien.no_rkm_medis, pasien.nm_pasien, pasien.no_ktp, pasien.jk, "
                    + "pasien.tmp_lahir, pasien.tgl_lahir,pasien.nm_ibu, concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) as alamat, pasien.gol_darah, pasien.pekerjaan,"
                    + "pasien.stts_nikah,pasien.agama,pasien.tgl_daftar,pasien.no_tlp,pasien.umur,"
                    + "pasien.pnd, pasien.keluarga, pasien.namakeluarga,penjab.png_jawab,pasien.pekerjaanpj,"
                    + "concat(pasien.alamatpj,', ',pasien.kelurahanpj,', ',pasien.kecamatanpj,', ',pasien.kabupatenpj) as alamatpj from pasien "
                    + "inner join kelurahan inner join kecamatan inner join kabupaten "
                    + "inner join penjab on pasien.kd_pj=penjab.kd_pj and pasien.kd_kel=kelurahan.kd_kel "
                    + "and pasien.kd_kec=kecamatan.kd_kec and pasien.kd_kab=kabupaten.kd_kab  where pasien.no_rkm_medis='" + NoRM.getText() + "' ", param);
                this.setCursor(Cursor.getDefaultCursor());
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

        dokter.isCek();
        dokter.TCari.requestFocus();
        dokter.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setVisible(true);
    }//GEN-LAST:event_BtnDokterActionPerformed

    private void ChkAccor2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkAccor2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ChkAccor2ActionPerformed

    private void R3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_R3ActionPerformed
        tampil();
    }//GEN-LAST:event_R3ActionPerformed

    private void CatatanIGDKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CatatanIGDKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_CatatanIGDKeyPressed

    private void R1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_R1ActionPerformed
        tampil();
    }//GEN-LAST:event_R1ActionPerformed

    private void R2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_R2ActionPerformed
        tampil();
    }//GEN-LAST:event_R2ActionPerformed

    private void BtnPernyataanMemilihDPJPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPernyataanMemilihDPJPActionPerformed
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        }else{
            if(tbObat.getSelectedRow()!= -1){
                SuratPernyataanMemilihDPJP resume=new SuratPernyataanMemilihDPJP(null,false);
                resume.isCek();
                resume.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
                resume.setLocationRelativeTo(internalFrame1);
                resume.setVisible(true);
                resume.emptTeks();
                resume.setNoRm(NoRw.getText(),DTPCari2.getDate());
            }else{
                JOptionPane.showMessageDialog(null,"Maaf, silahkan pilih data...!!!!");
            }      
        }
    }//GEN-LAST:event_BtnPernyataanMemilihDPJPActionPerformed

    private void AlasanDirawatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_AlasanDirawatKeyPressed
        Valid.pindah(evt,Diagnosa,Catatan);
    }//GEN-LAST:event_AlasanDirawatKeyPressed

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
    private widget.TextBox AlasanDirawat;
    private widget.Button BtnAll;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnDokter;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKamarInap;
    private widget.Button BtnKeluar;
    private widget.Button BtnPernyataanMemilihDPJP;
    private widget.Button BtnPersetujuanRanap;
    private widget.Button BtnPrint;
    private widget.Button BtnRiwayatPasien;
    private widget.Button BtnSimpan;
    private widget.Button BtnSuratPRI;
    private widget.Button BtnSuratPRI1;
    private widget.Button BtnSuratPRI2;
    private widget.Button BtnSuratPRI3;
    private widget.Button BtnSuratPermintaan;
    private widget.TextBox CaraBayar;
    private widget.TextBox Catatan;
    private widget.TextBox CatatanIGD;
    private widget.CekBox ChkAccor;
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
    private widget.TextBox KdKamar;
    private widget.Label LCount;
    private widget.TextBox NmBangsal;
    private widget.TextBox NmPasien;
    private widget.TextBox NoRM;
    private widget.TextBox NoRw;
    private widget.TextBox NoTelp;
    private widget.PanelBiasa PanelAccor;
    private javax.swing.JPanel PanelInput;
    private widget.TextBox Poli;
    private widget.RadioButton R1;
    private widget.RadioButton R2;
    private widget.RadioButton R3;
    private widget.ScrollPane Scroll;
    private widget.ScrollPane ScrollMenu;
    private widget.TextBox TCari;
    private widget.TextBox TDokter;
    private widget.Button btnDiagnosa;
    private widget.Button btnKamar;
    private javax.swing.ButtonGroup buttonGroup1;
    private widget.InternalFrame internalFrame1;
    private javax.swing.JLabel jLabel1;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel12;
    private widget.Label jLabel13;
    private widget.Label jLabel14;
    private widget.Label jLabel15;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private javax.swing.JLabel jLabel2;
    private widget.Label jLabel18;
    private widget.Label jLabel20;
    private widget.Label jLabel25;
    private javax.swing.JLabel jLabel3;
    private widget.Label jLabel5;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.Label jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private widget.panelisi panelCari;
    private widget.panelisi panelGlass10;
    private widget.panelisi panelGlass8;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

    private void tampil() {     
        Valid.tabelKosong(tabMode);
        DTPTgl.setDate(new Date());
        try{ 
            if(R1.isSelected()==true){
                ps=koneksi.prepareStatement(
                    "select permintaan_ranap.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.jk,reg_periksa.umurdaftar,reg_periksa.sttsumur,"+
                    "pasien.no_tlp,penjab.png_jawab,poliklinik.nm_poli,dokter.nm_dokter,permintaan_ranap.tanggal,permintaan_ranap.kd_kamar,kamar.kd_bangsal,"+
                    "CONCAT(bangsal.nm_bangsal, '', SUBSTRING_INDEX(kamar.kd_kamar, '-', -1)) AS nm_bangsal,kamar.trf_kamar,permintaan_ranap.diagnosa,permintaan_ranap.alasan,permintaan_ranap.catatan,reg_periksa.kd_dokter, drpj.kd_dokter AS dp1, drpj.nm_dokter AS dp2, "+
                    "if(ISNULL(fr.nokartu),\"Belum\",\"Sudah\") AS fingerprint from permintaan_ranap "+
                    "inner join reg_periksa on permintaan_ranap.no_rawat=reg_periksa.no_rawat "+
                    "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                    "inner join penjab on reg_periksa.kd_pj=penjab.kd_pj "+
                    "inner join dokter on reg_periksa.kd_dokter=dokter.kd_dokter "+
                    "inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli "+
                    "inner join kamar on permintaan_ranap.kd_kamar=kamar.kd_kamar "+
                    "inner join bangsal on kamar.kd_bangsal=bangsal.kd_bangsal INNER JOIN dpjp_ranap ON dpjp_ranap.no_rawat = permintaan_ranap.no_rawat INNER JOIN dokter drpj  ON drpj.kd_dokter = dpjp_ranap.kd_dokter LEFT JOIN side_db.fingerprint_bpjs fr ON fr.nokartu = pasien.no_peserta AND fr.tanggal = reg_periksa.tgl_registrasi "+
                    "where permintaan_ranap.kd_kamar != '-' and permintaan_ranap.no_rawat not in (select DISTINCT no_rawat from kamar_inap UNION select DISTINCT no_rawat2 from ranap_gabung) "+
                    (TCari.getText().equals("")?"":"and (permintaan_ranap.no_rawat like ? or reg_periksa.no_rkm_medis like ? or pasien.nm_pasien like ? "+
                    "or penjab.png_jawab like ? or poliklinik.nm_poli like ? or dokter.nm_dokter like ? or bangsal.nm_bangsal like ? "+
                    "or permintaan_ranap.diagnosa like ?)")+" order by permintaan_ranap.tanggal"
                );
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
                        tabMode.addRow(new Object[]{
                            rs.getString("no_rawat"),rs.getString("no_rkm_medis"),rs.getString("nm_pasien"),rs.getString("jk"),rs.getString("umurdaftar")+" "+rs.getString("sttsumur"),
                            rs.getString("no_tlp"),rs.getString("png_jawab"),rs.getString("nm_poli"),rs.getString("nm_dokter"),rs.getString("tanggal"),rs.getString("kd_kamar"),
                            rs.getString("kd_bangsal"),rs.getString("kd_kamar")+" "+rs.getString("nm_bangsal"),Valid.SetAngka(rs.getDouble("trf_kamar")),rs.getString("diagnosa"),
                            rs.getString("alasan"),rs.getString("catatan"),rs.getString("kd_dokter"), rs.getString("dp1"), rs.getString("dp2"), rs.getString("fingerprint")
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
            }else if(R2.isSelected()==true){
                ps=koneksi.prepareStatement("select permintaan_ranap.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.jk,reg_periksa.umurdaftar,reg_periksa.sttsumur,"+
                    "pasien.no_tlp,penjab.png_jawab,poliklinik.nm_poli,dokter.nm_dokter,permintaan_ranap.tanggal,permintaan_ranap.kd_kamar,kamar.kd_bangsal,"+
                    "CONCAT(bangsal.nm_bangsal, '', SUBSTRING_INDEX(kamar.kd_kamar, '-', -1)) AS nm_bangsal,kamar.trf_kamar,permintaan_ranap.diagnosa,permintaan_ranap.alasan,permintaan_ranap.catatan,reg_periksa.kd_dokter, drpj.kd_dokter AS dp1, drpj.nm_dokter AS dp2, "+ 
                    "if(ISNULL(fr.nokartu),\"Belum\",\"Sudah\") AS fingerprint from permintaan_ranap "+
                    "inner join reg_periksa on permintaan_ranap.no_rawat=reg_periksa.no_rawat "+
                    "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                    "inner join penjab on reg_periksa.kd_pj=penjab.kd_pj "+
                    "inner join dokter on reg_periksa.kd_dokter=dokter.kd_dokter "+
                    "inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli "+
                    "inner join kamar on permintaan_ranap.kd_kamar=kamar.kd_kamar "+
                    "inner join bangsal on kamar.kd_bangsal=bangsal.kd_bangsal INNER JOIN dpjp_ranap ON dpjp_ranap.no_rawat = permintaan_ranap.no_rawat INNER JOIN dokter drpj  ON drpj.kd_dokter = dpjp_ranap.kd_dokter LEFT JOIN side_db.fingerprint_bpjs fr ON fr.nokartu = pasien.no_peserta AND fr.tanggal = reg_periksa.tgl_registrasi "+
                    "where permintaan_ranap.no_rawat in (select DISTINCT no_rawat from kamar_inap UNION select DISTINCT no_rawat2 from ranap_gabung) and permintaan_ranap.tanggal between ? and ? "+
                    (TCari.getText().equals("")?"":"and (permintaan_ranap.no_rawat like ? or reg_periksa.no_rkm_medis like ? or pasien.nm_pasien like ? "+
                    "or penjab.png_jawab like ? or poliklinik.nm_poli like ? or dokter.nm_dokter like ? or bangsal.nm_bangsal like ? "+
                    "or permintaan_ranap.diagnosa like ?)")+" order by permintaan_ranap.tanggal"
                );
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
                        tabMode.addRow(new Object[]{
                            rs.getString("no_rawat"),rs.getString("no_rkm_medis"),rs.getString("nm_pasien"),rs.getString("jk"),rs.getString("umurdaftar")+" "+rs.getString("sttsumur"),
                            rs.getString("no_tlp"),rs.getString("png_jawab"),rs.getString("nm_poli"),rs.getString("nm_dokter"),rs.getString("tanggal"),rs.getString("kd_kamar"),
                            rs.getString("kd_bangsal"),rs.getString("kd_kamar")+" "+rs.getString("nm_bangsal"),Valid.SetAngka(rs.getDouble("trf_kamar")),rs.getString("diagnosa"),
                            rs.getString("alasan"),rs.getString("catatan"),rs.getString("kd_dokter"), rs.getString("dp1"), rs.getString("dp2"), rs.getString("fingerprint")
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
            }else if(R3.isSelected()==true){
                ps=koneksi.prepareStatement("select permintaan_ranap.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.jk,reg_periksa.umurdaftar,reg_periksa.sttsumur,"+
                    "pasien.no_tlp,penjab.png_jawab,poliklinik.nm_poli,dokter.nm_dokter,permintaan_ranap.tanggal,permintaan_ranap.kd_kamar,kamar.kd_bangsal,"+
                    "CONCAT(bangsal.nm_bangsal, '', SUBSTRING_INDEX(kamar.kd_kamar, '-', -1)) AS nm_bangsal,kamar.trf_kamar,permintaan_ranap.diagnosa,permintaan_ranap.catatan,reg_periksa.kd_dokter, drpj.kd_dokter AS dp1, drpj.nm_dokter AS dp2, "+
                    "if(ISNULL(fr.nokartu),\"Belum\",\"Sudah\") AS fingerprint from permintaan_ranap "+
                    "inner join reg_periksa on permintaan_ranap.no_rawat=reg_periksa.no_rawat "+
                    "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                    "inner join penjab on reg_periksa.kd_pj=penjab.kd_pj "+
                    "inner join dokter on reg_periksa.kd_dokter=dokter.kd_dokter "+
                    "inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli "+
                    "inner join kamar on permintaan_ranap.kd_kamar=kamar.kd_kamar "+
                    "inner join bangsal on kamar.kd_bangsal=bangsal.kd_bangsal INNER JOIN dpjp_ranap ON dpjp_ranap.no_rawat = permintaan_ranap.no_rawat INNER JOIN dokter drpj  ON drpj.kd_dokter = dpjp_ranap.kd_dokter LEFT JOIN side_db.fingerprint_bpjs fr ON fr.nokartu = pasien.no_peserta AND fr.tanggal = reg_periksa.tgl_registrasi "+
                    "where permintaan_ranap.kd_kamar = '-' and permintaan_ranap.no_rawat not in (select DISTINCT no_rawat from kamar_inap UNION select DISTINCT no_rawat2 from ranap_gabung) "+
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
                            rs.getString("kd_bangsal"),rs.getString("kd_kamar")+" "+rs.getString("nm_bangsal"),Valid.SetAngka(rs.getDouble("trf_kamar")),rs.getString("diagnosa"),rs.getString("alasan"),rs.getString("catatan"),
                            rs.getString("kd_dokter"), rs.getString("dp1"), rs.getString("dp2"), rs.getString("fingerprint")
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

    public void tampil2() { 
        runBackground(() ->tampil());
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
        AlasanDirawat.setText("");
        Catatan.setText("");
        KdDokter1.setText("");
        TDokter.setText("");
        DTPTgl.setDate(new Date());
        Diagnosa.requestFocus();
        date = new Date();
        now = dateFormat.format(date);
        CatatanIGD.setText("");
        Catatan.setText(now.substring(11, 13) + now.substring(14, 16) + now.substring(17, 19));

    }

    private void getData() {
        if(tbObat.getSelectedRow()!= -1){
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
            AlasanDirawat.setText(tbObat.getValueAt(tbObat.getSelectedRow(),15).toString());
            Catatan.setText(tbObat.getValueAt(tbObat.getSelectedRow(),16).toString());
            KdDokter.setText(tbObat.getValueAt(tbObat.getSelectedRow(),16).toString());
            Valid.SetTgl(DTPTgl,tbObat.getValueAt(tbObat.getSelectedRow(),9).toString());
            
           String value = tbObat.getValueAt(tbObat.getSelectedRow(), 15).toString();
           if(value.contains("IGD") || value.contains("FO")){
                CatatanIGD.setText(getJam(value, "IGD~"));
                Catatan.setText(getJam(value, "FO~"));
           }else{
                String[] splitcatatan = tbObat.getValueAt(tbObat.getSelectedRow(), 15).toString().split("#");            
                if (splitcatatan.length == 2) {
                    Catatan.setText(splitcatatan[0]);
                    if (splitcatatan[1].equals("Titip")) {
                        ChkAccor2.setSelected(true);
                    } else if (splitcatatan[1].equals("-")) {
                        ChkAccor2.setSelected(false);
                    }
                    CatatanIGD.setText(getJam(value, "IGD~"));
                } else if (splitcatatan.length == 1) {
                    Catatan.setText(splitcatatan[0]);
                    ChkAccor2.setSelected(false);
                    CatatanIGD.setText(getJam(value, "IGD~"));
                }
           }
           
        }
    }
    
    String getJam(String val, String prefix) {
        int idx = val.indexOf(prefix);
        return (idx != -1) ? val.substring(idx + prefix.length(), idx + prefix.length() + 8) : now.substring(11, 13) + ":" + now.substring(14, 16) + ":" + now.substring(17, 19);
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
        DTPTgl.setDate(new Date());
        
        if(Sequel.cariIsi("select kd_jbtn from petugas where nip =?", akses.getkode()).equals("J014") && Sequel.cariIsi("select kd_kamar from permintaan_ranap where no_rawat = ?", norwt).equals("-")){
            R3.setSelected(true);
            String value = Sequel.cariIsi("select catatan from permintaan_ranap where no_rawat = ?", norwt);
            CatatanIGD.setText(getJam(value, "IGD~"));
            Catatan.setText(getJam(value, "FO~"));
        }else{
            R1.setSelected(true);
            String value = Sequel.cariIsi("select catatan from permintaan_ranap where no_rawat = ?", norwt);
            CatatanIGD.setText(getJam(value, "IGD~"));
            Catatan.setText(getJam(value, "FO~"));
        }
        aktif=false;
        isForm();
    }
    
    private void isForm(){
        if(ChkInput.isSelected()==true){
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH,216));
            FormInput.setVisible(true);      
            ChkInput.setVisible(true);
        }else if(ChkInput.isSelected()==false){           
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH,186));
            FormInput.setVisible(true);      
            ChkInput.setVisible(true);
        }
        
         if(Sequel.cariIsi("select kd_jbtn from petugas where nip =?", akses.getkode()).equals("J014")){
            R3.setSelected(true);
        }else{
            R1.setSelected(true);
         }
    }
    
    public void isCek(){
        BtnSimpan.setEnabled(akses.getpermintaan_ranap());
        BtnHapus.setEnabled(akses.getpermintaan_ranap());
        BtnPrint.setEnabled(akses.getpermintaan_ranap());
        BtnKamarInap.setEnabled(akses.getkamar_inap());
        BtnRiwayatPasien.setEnabled(akses.getresume_pasien());
        BtnPernyataanMemilihDPJP.setEnabled(akses.getsurat_pernyataan_memilih_dpjp());
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
                    bookingbaru=Sequel.cariInteger("select count(*) from permintaan_ranap where permintaan_ranap.no_rawat not in (select DISTINCT kamar_inap.no_rawat from kamar_inap) ");
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
                            runBackground(() ->tampil());
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
            PanelAccor.setPreferredSize(new Dimension(180,HEIGHT));
            FormMenu.setVisible(true); 
            ChkAccor.setVisible(true);
        }else if(ChkAccor.isSelected()==false){  
            ChkAccor.setVisible(false);
            PanelAccor.setPreferredSize(new Dimension(175,HEIGHT));
            FormMenu.setVisible(true); 
            ChkAccor.setVisible(true);
        }
    }
    
    private void runBackground(Runnable task) {
        if (ceksukses) return;
        if (executor.isShutdown() || executor.isTerminated()) return;
        if (!isDisplayable()) return;

        ceksukses = true;
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            executor.submit(() -> {
                try {
                    task.run();
                } finally {
                    ceksukses = false;
                    SwingUtilities.invokeLater(() -> {
                        if (isDisplayable()) {
                            setCursor(Cursor.getDefaultCursor());
                        }
                    });
                }
            });
        } catch (RejectedExecutionException ex) {
            ceksukses = false;
        }
    }
    
    @Override
    public void dispose() {
        executor.shutdownNow();
        super.dispose();
    }
}
