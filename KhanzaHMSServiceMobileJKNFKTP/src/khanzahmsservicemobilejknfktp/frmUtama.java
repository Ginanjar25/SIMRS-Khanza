/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package khanzahmsservicemobilejknfktp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.ApiMobileJKN;
import fungsi.koneksiDB;
import fungsi.sekuel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.Timer;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 *
 * @author windiartonugroho
 */
public class frmUtama extends javax.swing.JFrame {
    private  Connection koneksi=koneksiDB.condb();
    private  sekuel Sequel=new sekuel();
    private  String requestJson,URL="",utc="",link="",datajam="",
              nol_jam = "",nol_menit = "",nol_detik = "",jam="",menit="",detik="",hari="",otorisasi="",task1="",task2="",
              kodepoli="",kodedokter="",kodebpjs=Sequel.cariIsi("select password_asuransi.kd_pj from password_asuransi");
    private  ApiMobileJKN api=new ApiMobileJKN();
    private  HttpHeaders headers;
    private  HttpEntity requestEntity;
    private  ObjectMapper mapper= new ObjectMapper();
    private  JsonNode root;
    private  JsonNode nameNode;
    private  PreparedStatement ps,ps2,ps3;
    private  ResultSet rs,rs2,rs3;
    private  Calendar cal = Calendar.getInstance();
    private  int day = cal.get(Calendar.DAY_OF_WEEK);
    private  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private  SimpleDateFormat tanggalFormat = new SimpleDateFormat("yyyy-MM-dd");
    private  Date parsedDate;
    private  Date date = new Date();  

    /**
     * Creates new form frmUtama
     */
    public frmUtama() {
        initComponents();
        try {
            otorisasi=koneksiDB.USERMOBILEJKNFKTP()+":"+koneksiDB.PASSMOBILEJKNFKTP()+":095";
            link=koneksiDB.URLMOBILEJKNFKTP();
        } catch (Exception e) {
            System.out.println("E : "+e);
        }
        
        this.setSize(390,340);
        
        date = new Date();  
        Tanggal1.setText(tanggalFormat.format(date)); 
        Tanggal2.setText(tanggalFormat.format(date)); 
        jam();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        TeksArea = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        Tanggal1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        Tanggal2 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SIMKES Khanza Service Mobile JKN FKTP");

        TeksArea.setColumns(20);
        TeksArea.setRows(5);
        jScrollPane1.setViewportView(TeksArea);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Tanggal :");
        jLabel1.setPreferredSize(new java.awt.Dimension(70, 23));
        jPanel1.add(jLabel1);

        Tanggal1.setPreferredSize(new java.awt.Dimension(100, 23));
        jPanel1.add(Tanggal1);

        jLabel3.setText("s.d.");
        jLabel3.setPreferredSize(new java.awt.Dimension(28, 23));
        jPanel1.add(jLabel3);

        Tanggal2.setPreferredSize(new java.awt.Dimension(100, 23));
        jPanel1.add(Tanggal2);

        jLabel2.setPreferredSize(new java.awt.Dimension(30, 23));
        jPanel1.add(jLabel2);

        jButton1.setText("Keluar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(frmUtama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmUtama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmUtama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmUtama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmUtama().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField Tanggal1;
    private javax.swing.JTextField Tanggal2;
    private javax.swing.JTextArea TeksArea;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
    private void jam(){
        ActionListener taskPerformer = new ActionListener(){
            private int nilai_jam;
            private int nilai_menit;
            private int nilai_detik;
            public void actionPerformed(ActionEvent e) {
                nol_jam = "";
                nol_menit = "";
                nol_detik = "";
                Date now = Calendar.getInstance().getTime();
                // Mengambil nilaj JAM, MENIT, dan DETIK Sekarang
                nilai_jam = now.getHours();
                nilai_menit = now.getMinutes();
                nilai_detik = now.getSeconds();
                // Jika nilai JAM lebih kecil dari 10 (hanya 1 digit)
                if (nilai_jam <= 9) {
                    // Tambahkan "0" didepannya
                    nol_jam = "0";
                }
                // Jika nilai MENIT lebih kecil dari 10 (hanya 1 digit)
                if (nilai_menit <= 9) {
                    // Tambahkan "0" didepannya
                    nol_menit = "0";
                }
                // Jika nilai DETIK lebih kecil dari 10 (hanya 1 digit)
                if (nilai_detik <= 9) {
                    // Tambahkan "0" didepannya
                    nol_detik = "0";
                }
                // Membuat String JAM, MENIT, DETIK
                jam = nol_jam + Integer.toString(nilai_jam);
                menit = nol_menit + Integer.toString(nilai_menit);
                detik = nol_detik + Integer.toString(nilai_detik);
                if(jam.equals("01")&&menit.equals("01")&&detik.equals("01")){
                    TeksArea.setText("");
                    date = new Date();  
                    Tanggal1.setText(tanggalFormat.format(date)); 
                    Tanggal2.setText(tanggalFormat.format(date)); 
                }
                if(detik.equals("01")&&((nilai_menit%5)==0)){
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
                    
                    try {
                        koneksi=koneksiDB.condb();
                        TeksArea.append("Menjalankan WS tambah antrian Mobile JKN FKTP\n");
                        ps=koneksi.prepareStatement(
                                "select reg_periksa.no_reg,reg_periksa.no_rawat,reg_periksa.tgl_registrasi,reg_periksa.kd_dokter,dokter.nm_dokter,reg_periksa.kd_poli,poliklinik.nm_poli,reg_periksa.stts_daftar,reg_periksa.no_rkm_medis,reg_periksa.kd_pj, "+
                                "pasien.no_ktp,pasien.no_peserta,pasien.no_tlp from reg_periksa inner join dokter on reg_periksa.kd_dokter=dokter.kd_dokter inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli "+
                                "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis where reg_periksa.tgl_registrasi between '"+Tanggal1.getText()+"' and '"+Tanggal2.getText()+"'");
                        try {
                            rs=ps.executeQuery();
                            while(rs.next()){
                                ps2=koneksi.prepareStatement("select * from jadwal where jadwal.hari_kerja=? and jadwal.kd_dokter=? and jadwal.kd_poli=?");
                                try {
                                    ps2.setString(1,hari);
                                    ps2.setString(2,rs.getString("kd_dokter"));
                                    ps2.setString(3,rs.getString("kd_poli"));
                                    rs2=ps2.executeQuery();
                                    if(rs2.next()){
                                        kodedokter=Sequel.cariIsi("select maping_dokter_pcare.kd_dokter_pcare from maping_dokter_pcare where maping_dokter_pcare.kd_dokter=?",rs.getString("kd_dokter"));
                                        kodepoli=Sequel.cariIsi("select maping_poliklinik_pcare.kd_poli_pcare from maping_poliklinik_pcare where maping_poliklinik_pcare.kd_poli_rs=?",rs.getString("kd_poli"));
                                        if((!kodedokter.equals(""))&&(!kodepoli.equals(""))){
                                            task1="";task2="";
                                            ps3=koneksi.prepareStatement("select referensi_mobilejkn_bpjs_taskid.taskid from referensi_mobilejkn_bpjs_taskid where referensi_mobilejkn_bpjs_taskid.no_rawat=?");
                                            try {
                                               ps3.setString(1,rs.getString("no_rawat"));
                                               rs3=ps3.executeQuery();
                                               while(rs3.next()){
                                                   if(rs3.getString("taskid").equals("1")){
                                                       task1="Sudah";
                                                   }
                                                   if(rs3.getString("taskid").equals("2")){
                                                       task2="Sudah";
                                                   }
                                               }
                                            } catch (Exception ex) {
                                                System.out.println("Notif : "+ex);
                                            } finally{
                                                if(rs3!=null){
                                                    rs3.close();
                                                }
                                                if(ps3!=null){
                                                    ps3.close();
                                                }
                                            }
                                            
                                            if(task1.equals("")){
                                                if(!rs.getString("kd_pj").equals(kodebpjs)){
                                                    headers = new HttpHeaders();
                                                    headers.setContentType(MediaType.APPLICATION_JSON);
                                                    headers.add("X-cons-id",koneksiDB.CONSIDMOBILEJKNFKTP());
                                                    utc=String.valueOf(api.GetUTCdatetimeAsString());
                                                    headers.add("X-timestamp",utc);            
                                                    headers.add("X-signature",api.getHmac());
                                                    headers.add("X-authorization","Basic "+Base64.encodeBase64String(otorisasi.getBytes()));
                                                    headers.add("user_key",koneksiDB.USERKEYMOBILEJKNFKTP());
                                                    requestJson ="{" +
                                                                    "\"nomorkartu\": \"\"," +
                                                                    "\"nik\": \""+rs.getString("no_ktp")+"\"," +
                                                                    "\"nohp\": \""+rs.getString("no_tlp")+"\"," +
                                                                    "\"kodepoli\": \""+kodepoli+"\"," +
                                                                    "\"namapoli\": \""+rs.getString("nm_poli")+"\"," +
                                                                    "\"norm\": \""+rs.getString("no_rkm_medis")+"\"," +
                                                                    "\"tanggalperiksa\": \""+rs.getString("tgl_registrasi")+"\"," +
                                                                    "\"kodedokter\": "+kodedokter+"," +
                                                                    "\"namadokter\": \""+rs.getString("nm_dokter")+"\"," +
                                                                    "\"jampraktek\": \""+rs2.getString("jam_mulai").substring(0,5)+"-"+rs2.getString("jam_selesai").substring(0,5)+"\"," +
                                                                    "\"nomorantrean\": \""+rs.getString("no_reg")+"\"," +
                                                                    "\"angkaantrean\": "+Integer.parseInt(rs.getString("no_reg"))+"," +
                                                                    "\"keterangan\": \"Peserta harap 30 menit lebih awal guna pencatatan administrasi.\"" +
                                                                "}";
                                                    TeksArea.append("JSON : "+requestJson+"\n");
                                                    requestEntity = new HttpEntity(requestJson,headers);
                                                    URL = link+"/antrean/add";	
                                                    System.out.println("URL : "+URL);
                                                    //System.out.println(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                                                    root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                                                    nameNode = root.path("metadata");  
                                                    TeksArea.append("respon WS BPJS : "+nameNode.path("code").asText()+" "+nameNode.path("message").asText()+"\n");
                                                }else{
                                                    headers = new HttpHeaders();
                                                    headers.setContentType(MediaType.APPLICATION_JSON);
                                                    headers.add("X-cons-id",koneksiDB.CONSIDMOBILEJKNFKTP());
                                                    utc=String.valueOf(api.GetUTCdatetimeAsString());
                                                    headers.add("X-timestamp",utc);            
                                                    headers.add("X-signature",api.getHmac());
                                                    headers.add("X-authorization","Basic "+Base64.encodeBase64String(otorisasi.getBytes()));
                                                    headers.add("user_key",koneksiDB.USERKEYMOBILEJKNFKTP());
                                                    requestJson ="{" +
                                                                    "\"nomorkartu\": \""+rs.getString("no_peserta")+"\"," +
                                                                    "\"nik\": \""+rs.getString("no_ktp")+"\"," +
                                                                    "\"nohp\": \""+rs.getString("no_tlp")+"\"," +
                                                                    "\"kodepoli\": \""+kodepoli+"\"," +
                                                                    "\"namapoli\": \""+rs.getString("nm_poli")+"\"," +
                                                                    "\"norm\": \""+rs.getString("no_rkm_medis")+"\"," +
                                                                    "\"tanggalperiksa\": \""+rs.getString("tgl_registrasi")+"\"," +
                                                                    "\"kodedokter\": "+kodedokter+"," +
                                                                    "\"namadokter\": \""+rs.getString("nm_dokter")+"\"," +
                                                                    "\"jampraktek\": \""+rs2.getString("jam_mulai").substring(0,5)+"-"+rs2.getString("jam_selesai").substring(0,5)+"\"," +
                                                                    "\"nomorantrean\": \""+rs.getString("no_reg")+"\"," +
                                                                    "\"angkaantrean\": "+Integer.parseInt(rs.getString("no_reg"))+"," +
                                                                    "\"keterangan\": \"Peserta harap 30 menit lebih awal guna pencatatan administrasi.\"" +
                                                                "}";
                                                    TeksArea.append("JSON : "+requestJson+"\n");
                                                    requestEntity = new HttpEntity(requestJson,headers);
                                                    URL = link+"/antrean/add";	
                                                    System.out.println("URL : "+URL);
                                                    //System.out.println(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                                                    root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                                                    nameNode = root.path("metadata");  
                                                    TeksArea.append("respon WS BPJS : "+nameNode.path("code").asText()+" "+nameNode.path("message").asText()+"\n");
                                                }
                                                
                                                datajam=Sequel.cariIsi("select concat(pemeriksaan_ralan.tgl_perawatan,' ',pemeriksaan_ralan.jam_rawat) from pemeriksaan_ralan where pemeriksaan_ralan.no_rawat=?",rs.getString("no_rawat"));
                                                if(datajam.equals("")){
                                                    datajam=Sequel.cariIsi("select if(mutasi_berkas.diterima='0000-00-00 00:00:00','',mutasi_berkas.diterima) from mutasi_berkas where mutasi_berkas.no_rawat=?",rs.getString("no_rawat"));
                                                }
                                                if(!datajam.equals("")){
                                                    if(Sequel.menyimpantf2("referensi_mobilejkn_bpjs_taskid","?,?,?","task id",3,new String[]{rs.getString("no_rawat"),"1",datajam})==true){
                                                        parsedDate = dateFormat.parse(datajam);
                                                        try {     
                                                            TeksArea.append("Menjalankan WS taskid mulai pelayanan poli Mobile JKN Pasien Non BPJS/BPS\n");
                                                            headers = new HttpHeaders();
                                                            headers.setContentType(MediaType.APPLICATION_JSON);
                                                            headers.add("X-cons-id",koneksiDB.CONSIDMOBILEJKNFKTP());
                                                            utc=String.valueOf(api.GetUTCdatetimeAsString());
                                                            headers.add("X-timestamp",utc);            
                                                            headers.add("X-signature",api.getHmac());
                                                            headers.add("X-authorization","Basic "+Base64.encodeBase64String(otorisasi.getBytes()));
                                                            headers.add("user_key",koneksiDB.USERKEYMOBILEJKNFKTP());
                                                            requestJson ="{" +
                                                                             "\"tanggalperiksa\": \""+rs.getString("tgl_registrasi")+"\"," +
                                                                             "\"kodepoli\": \""+kodepoli+"\"," +
                                                                             "\"nomorkartu\": \""+(rs.getString("kd_pj").equals(kodebpjs)?rs.getString("no_peserta"):"")+"\"," +
                                                                             "\"status\": \"1\"," +
                                                                             "\"waktu\": \""+parsedDate.getTime()+"\"" +
                                                                          "}";
                                                            TeksArea.append("JSON : "+requestJson+"\n");
                                                            requestEntity = new HttpEntity(requestJson,headers);
                                                            URL = link+"/antrean/panggil";	
                                                            System.out.println("URL : "+URL);
                                                            //System.out.println(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                                                            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                                                            nameNode = root.path("metadata");
                                                            if(!nameNode.path("code").asText().equals("200")){
                                                                Sequel.queryu2("delete from referensi_mobilejkn_bpjs_taskid where taskid='1' and no_rawat='"+rs.getString("no_rawat")+"'");
                                                            }   
                                                            TeksArea.append("respon WS BPJS : "+nameNode.path("code").asText()+" "+nameNode.path("message").asText()+"\n");
                                                        }catch (Exception ex) {
                                                            System.out.println("Notifikasi Bridging : "+ex);
                                                        }
                                                    }
                                                }
                                            }

                                            if(task2.equals("")){
                                                datajam=Sequel.cariIsi("select now() from reg_periksa where reg_periksa.stts='Batal' and reg_periksa.no_rawat=?",rs.getString("no_rawat"));
                                                if(!datajam.equals("")){
                                                    if(Sequel.menyimpantf2("referensi_mobilejkn_bpjs_taskid","?,?,?","task id",3,new String[]{rs.getString("no_rawat"),"2",datajam})==true){
                                                        parsedDate = dateFormat.parse(datajam);
                                                        try {     
                                                            TeksArea.append("Menjalankan WS taskid batal pelayanan poli Mobile JKN Pasien Non BPJS/BPS Onsite\n");
                                                            headers = new HttpHeaders();
                                                            headers.setContentType(MediaType.APPLICATION_JSON);
                                                            headers.add("X-cons-id",koneksiDB.CONSIDMOBILEJKNFKTP());
                                                            utc=String.valueOf(api.GetUTCdatetimeAsString());
                                                            headers.add("X-timestamp",utc);            
                                                            headers.add("X-signature",api.getHmac());
                                                            headers.add("X-authorization","Basic "+Base64.encodeBase64String(otorisasi.getBytes()));
                                                            headers.add("user_key",koneksiDB.USERKEYMOBILEJKNFKTP());
                                                            requestJson ="{" +
                                                                             "\"tanggalperiksa\": \""+rs.getString("tgl_registrasi")+"\"," +
                                                                             "\"kodepoli\": \""+kodepoli+"\"," +
                                                                             "\"nomorkartu\": \""+(rs.getString("kd_pj").equals(kodebpjs)?rs.getString("no_peserta"):"")+"\"," +
                                                                             "\"status\": \"2\"," +
                                                                             "\"waktu\": \""+parsedDate.getTime()+"\"" +
                                                                          "}";
                                                            TeksArea.append("JSON : "+requestJson+"\n");
                                                            requestEntity = new HttpEntity(requestJson,headers);
                                                            URL = link+"/antrean/panggil";	
                                                            System.out.println("URL : "+URL);
                                                            //System.out.println(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                                                            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                                                            nameNode = root.path("metadata");
                                                            if(!nameNode.path("code").asText().equals("200")){
                                                                Sequel.queryu2("delete from referensi_mobilejkn_bpjs_taskid where taskid='2' and no_rawat='"+rs.getString("no_rawat")+"'");
                                                            }  
                                                            TeksArea.append("respon WS BPJS : "+nameNode.path("code").asText()+" "+nameNode.path("message").asText()+"\n");
                                                        }catch (Exception ex) {
                                                            System.out.println("Notifikasi Bridging : "+ex);
                                                        }
                                                        
                                                        try {     
                                                            headers = new HttpHeaders();
                                                            headers.setContentType(MediaType.APPLICATION_JSON);
                                                            headers.add("X-cons-id",koneksiDB.CONSIDMOBILEJKNFKTP());
                                                            utc=String.valueOf(api.GetUTCdatetimeAsString());
                                                            headers.add("X-timestamp",utc);            
                                                            headers.add("X-signature",api.getHmac());
                                                            headers.add("X-authorization","Basic "+Base64.encodeBase64String(otorisasi.getBytes()));
                                                            headers.add("user_key",koneksiDB.USERKEYMOBILEJKNFKTP());
                                                            requestJson ="{" +
                                                                             "\"tanggalperiksa\": \""+rs.getString("tgl_registrasi")+"\"," +
                                                                             "\"kodepoli\": \""+kodepoli+"\"," +
                                                                             "\"nomorkartu\": \""+(rs.getString("kd_pj").equals(kodebpjs)?rs.getString("no_peserta"):"")+"\"," +
                                                                             "\"alasan\": \"pasien batal periksa\"" +
                                                                          "}";
                                                            TeksArea.append("JSON : "+requestJson+"\n");
                                                            requestEntity = new HttpEntity(requestJson,headers);
                                                            URL = link+"/antrean/batal";	
                                                            System.out.println("URL : "+URL);
                                                            //System.out.println(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                                                            root = mapper.readTree(api.getRest().exchange(URL, HttpMethod.POST, requestEntity, String.class).getBody());
                                                            nameNode = root.path("metadata");
                                                            if(!nameNode.path("code").asText().equals("200")){
                                                                Sequel.queryu2("delete from referensi_mobilejkn_bpjs_taskid where taskid='2' and no_rawat='"+rs.getString("no_rawat")+"'");
                                                            }  
                                                            TeksArea.append("respon WS BPJS : "+nameNode.path("code").asText()+" "+nameNode.path("message").asText()+"\n");
                                                        }catch (Exception ex) {
                                                            System.out.println("Notifikasi Bridging : "+ex);
                                                        }
                                                    }
                                                }
                                            }
                                        }else{
                                            TeksArea.append("Mapping poli/dokter tidak ditemukan...\n");
                                        }
                                    }else{
                                        TeksArea.append("Jadwal poli/dokter tidak ditemukan...\n");
                                    }
                                } catch (Exception ex) {
                                    System.out.println("Notif : "+ex);
                                } finally{
                                    if(rs2!=null){
                                        rs2.close();
                                    }
                                    if(ps2!=null){
                                        ps2.close();
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            System.out.println("Notif : "+ex);
                        } finally{
                            if(rs!=null){
                                rs.close();
                            }
                            if(ps!=null){
                                ps.close();
                            }
                        }
                        
                        TeksArea.append("Proses update selesai\n");
                    } catch (Exception ez) {
                        System.out.println("Notif : "+ez);
                    }
                }
            }
        };
        // Timer
        new Timer(1000, taskPerformer).start();
    }
}
