/*
 * Panel SATUSEHAT Rekam Medis Elektronik (SSRME) - Juknis V2.0 (1 September 2026)
 * Dipakai oleh dialog modif.SatuSehatRekamMedisElektronik dan tab "RME SATUSEHAT" di
 * rekammedis.RMRiwayatPerawatan.
 *
 * Alur:
 *  1. Siapkan 6 atribut : patient_id, patient_name, practitioner_id, practitioner_name,
 *                         organization_id, organization_name
 *  2. Generate token OAuth2 (client_credentials) via ApiSatuSehat
 *  3. POST {base}/v2/ntl/shl -> jika sukses langsung buka shlinkUrl
 *     jika 403 CONSENT_REQUIRED -> POST {base}/v2/ntl/chl (consent), buka verificationUrl,
 *     dokter mengisi Kode Akses Pasien dari SATUSEHAT Mobile. SHL dicek ulang otomatis tiap 5 detik
 *     (maks. 10 menit), atau klik "Lanjut Buka Rekam Medis".
 *  4. Emergency : CHL dikirim dengan "type_medical_summary":"EMERGENCY" (bypass consent pasien,
 *     petugas mengisi alasan + data pengantar/wali di halaman SSRME).
 *
 * Halaman SSRME memakai WebCrypto (crypto.subtle) yang tidak tersedia di WebView JavaFX, sehingga
 * dipasang pengganti crypto.subtle berbasis Java (KriptoSSRME), mengikuti RMRiwayatPerawatan Khanza original.
 *
 * Keamanan (sesuai juknis): token & client secret tidak dicetak ke log, SHLink/verificationUrl
 * dianggap URL sensitif (tidak dicetak ke log, halaman dikosongkan saat ditutup).
 */

package modif;

import bridging.ApiSatuSehat;
import bridging.SatuSehatCekNIK;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.sekuel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.net.URI;
import java.util.Base64;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

public class PanelSSRME extends JPanel {
    private final sekuel Sequel=new sekuel();
    private final ApiSatuSehat api=new ApiSatuSehat();
    private final ObjectMapper mapper=new ObjectMapper();
    /** referensi kuat agar objek jembatan JS tidak di-GC */
    private final KriptoSSRME kripto=new KriptoSSRME();
    private JFXPanel jfxPanel;
    private WebEngine engine;
    private String link="",urlAktif="",
            noRM="",nikPasien="",patientId="",patientName="",
            kdDokter="",nikDokter="",practitionerId="",practitionerName="",
            organizationId="",organizationName="";
    private volatile boolean sedangProses=false;
    /** naik setiap ganti pasien/proses baru, untuk menghentikan pengecekan consent yang lama */
    private volatile int sesi=0;

    private final widget.panelisi panelInfo=new widget.panelisi();
    private final widget.panelisi panelTombol=new widget.panelisi();
    private final widget.PanelBiasa panelContent=new widget.PanelBiasa();
    private final widget.Label lblPasien=new widget.Label();
    private final widget.Label lblDokter=new widget.Label();
    private final widget.Label lblStatus=new widget.Label();
    private final widget.CekBox chkEmergency=new widget.CekBox();
    private final widget.Button btnBuka=new widget.Button();
    private final widget.Button btnLanjut=new widget.Button();
    private final widget.Button btnBrowser=new widget.Button();

    public PanelSSRME() {
        initKomponen();
        try {
            link=koneksiDB.URLSSRMESATUSEHAT();
            organizationId=koneksiDB.IDSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notifikasi SSRME : "+e);
        }
        organizationName=Sequel.cariIsi("select setting.nama_instansi from setting");
    }

    private void initKomponen(){
        setLayout(new BorderLayout(1,1));
        setBackground(Color.WHITE);

        panelInfo.setLayout(new GridLayout(3,1));
        panelInfo.setPreferredSize(new Dimension(44,66));
        panelInfo.add(lblPasien);
        panelInfo.add(lblDokter);
        lblStatus.setForeground(new Color(0,100,80));
        panelInfo.add(lblStatus);
        add(panelInfo,BorderLayout.PAGE_START);

        panelContent.setBorder(null);
        panelContent.setLayout(new BorderLayout());
        add(panelContent,BorderLayout.CENTER);

        panelTombol.setPreferredSize(new Dimension(44,54));
        panelTombol.setLayout(new FlowLayout(FlowLayout.LEFT,5,9));

        chkEmergency.setText("Kondisi Emergency (bypass consent)");
        chkEmergency.setToolTipText("Hanya untuk kondisi darurat yang mengancam jiwa. Alasan & data pengantar/wali wajib diisi di halaman SSRME.");
        chkEmergency.setPreferredSize(new Dimension(230,23));
        chkEmergency.setOpaque(false);
        panelTombol.add(chkEmergency);

        btnBuka.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        btnBuka.setText("Buka SSRME");
        btnBuka.setToolTipText("Minta akses RME Nasional pasien");
        btnBuka.setPreferredSize(new Dimension(130,30));
        btnBuka.addActionListener(e->bukaSSRME());
        panelTombol.add(btnBuka);

        btnLanjut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inbox.png")));
        btnLanjut.setText("Lanjut Buka Rekam Medis");
        btnLanjut.setToolTipText("Klik setelah persetujuan (consent) pasien berhasil dicatat");
        btnLanjut.setPreferredSize(new Dimension(200,30));
        btnLanjut.setEnabled(false);
        btnLanjut.addActionListener(e->prosesAsync(false,true));
        panelTombol.add(btnLanjut);

        btnBrowser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inbox.png")));
        btnBrowser.setText("Buka di Browser");
        btnBrowser.setToolTipText("Gunakan jika halaman tidak tampil sempurna di dalam aplikasi");
        btnBrowser.setPreferredSize(new Dimension(150,30));
        btnBrowser.setEnabled(false);
        btnBrowser.addActionListener(e->bukaDiBrowser());
        panelTombol.add(btnBrowser);

        add(panelTombol,BorderLayout.PAGE_END);
    }

    /** menambah tombol milik form pemanggil (misal tombol Keluar) di baris tombol panel */
    public void tambahTombol(Component tombol){
        panelTombol.add(tombol);
    }

    /** no_rkm_medis pasien yang sedang dibuka, kosong bila belum ada */
    public String getNoRM(){
        return noRM;
    }

    /**
     * @param norawat  no_rawat aktif (untuk menentukan DPJP bila user login bukan dokter), boleh kosong.
     *                 Bila kosong dipakai kunjungan terakhir pasien.
     * @param norm     no_rkm_medis pasien
     * @param kddokter kode dokter pilihan form pemanggil (boleh kosong). Prioritas: user login dokter -> kddokter -> DPJP registrasi
     */
    public void setPasien(String norawat,String norm,String kddokter){
        sesi++;
        kosongkanHalaman();
        noRM=norm;
        nikPasien=Sequel.cariIsi("select pasien.no_ktp from pasien where pasien.no_rkm_medis=?",norm).trim();
        patientName=Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis=?",norm).trim();

        kdDokter="";
        if(!akses.getkode().equals("")&&Sequel.cariInteger("select count(dokter.kd_dokter) from dokter where dokter.kd_dokter=?",akses.getkode())>0){
            kdDokter=akses.getkode();
        }
        if(kdDokter.equals("")&&kddokter!=null&&!kddokter.trim().equals("")){
            kdDokter=kddokter.trim();
        }
        if(kdDokter.equals("")&&norawat!=null&&!norawat.trim().equals("")){
            kdDokter=Sequel.cariIsi("select reg_periksa.kd_dokter from reg_periksa where reg_periksa.no_rawat=?",norawat.trim());
            if(!Sequel.cariIsi("select reg_periksa.no_rkm_medis from reg_periksa where reg_periksa.no_rawat=?",norawat.trim()).equals(norm)){
                kdDokter=""; // no_rawat milik pasien lain
            }
        }
        if(kdDokter.equals("")){
            // DPJP kunjungan terakhir pasien
            kdDokter=Sequel.cariIsi("select reg_periksa.kd_dokter from reg_periksa where reg_periksa.no_rkm_medis=? "+
                    "order by reg_periksa.tgl_registrasi desc,reg_periksa.jam_reg desc limit 1",norm);
        }
        nikDokter=Sequel.cariIsi("select pegawai.no_ktp from pegawai where pegawai.nik=?",kdDokter).trim();
        practitionerName=Sequel.cariIsi("select dokter.nm_dokter from dokter where dokter.kd_dokter=?",kdDokter).trim();
        patientId="";
        practitionerId="";
        tampilInfo();
        prosesAsync(false,false);
    }

    private void tampilInfo(){
        lblPasien.setText("  Pasien : "+noRM+" - "+patientName+"   |   NIK : "+nikPasien+"   |   ID SATUSEHAT : "+(patientId.equals("")?"-":patientId));
        lblDokter.setText("  Dokter : "+kdDokter+" - "+practitionerName+"   |   ID SATUSEHAT : "+(practitionerId.equals("")?"-":practitionerId)+
                "   |   Fasyankes : "+organizationName+" ("+organizationId+")");
    }

    private void status(String teks){
        SwingUtilities.invokeLater(()->lblStatus.setText("  "+teks));
    }

    private void bukaSSRME(){
        if(noRM.equals("")){
            JOptionPane.showMessageDialog(this,"Pasien masih kosong...!!!");
            return;
        }
        if(chkEmergency.isSelected()){
            int jawab=JOptionPane.showConfirmDialog(this,
                    "Akses EMERGENCY akan membuka RME Nasional pasien tanpa kode akses pasien.\n"+
                    "Hanya boleh digunakan pada kondisi darurat demi keselamatan jiwa pasien,\n"+
                    "seluruh alasan dan data pengantar/wali akan dicatat oleh SATUSEHAT.\n\nLanjutkan?",
                    "Konfirmasi Akses Emergency",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
            if(jawab!=JOptionPane.YES_OPTION){
                return;
            }
            prosesAsync(true,false);
        }else{
            prosesAsync(false,false);
        }
    }

    /**
     * @param emergency  true -> langsung buat CHL emergency
     * @param shlSaja    true -> hanya request SHL (dipakai setelah consent selesai), tanpa membuat CHL baru
     */
    private void prosesAsync(boolean emergency,boolean shlSaja){
        if(sedangProses){
            return;
        }
        sedangProses=true;
        final int sesiProses=++sesi;
        setTombol(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        new Thread(()->{
            try{
                proses(emergency,shlSaja,sesiProses);
            }catch(Exception e){
                System.out.println("Notifikasi SSRME : "+e);
                pesan("Gagal terhubung ke SATUSEHAT Rekam Medis :\n"+e.getMessage());
                status("Gagal : "+e.getMessage());
            }finally{
                sedangProses=false;
                SwingUtilities.invokeLater(()->{
                    setTombol(true);
                    setCursor(Cursor.getDefaultCursor());
                    // pasien diganti saat proses berjalan -> proses ulang untuk pasien baru
                    if(sesiProses!=sesi&&!noRM.equals("")&&urlAktif.equals("")){
                        prosesAsync(false,false);
                    }
                });
            }
        },"SSRME").start();
    }

    private void setTombol(boolean aktif){
        btnBuka.setEnabled(aktif);
        chkEmergency.setEnabled(aktif);
        if(!aktif){
            btnLanjut.setEnabled(false);
        }
    }

    private void proses(boolean emergency,boolean shlSaja,int sesiProses) throws Exception {
        if(!validasiData()){
            return;
        }
        status("Mengambil token SATUSEHAT...");
        String token=token();
        if(token==null||token.equals("")){
            pesan("Gagal mendapatkan access token SATUSEHAT.\nPeriksa CLIENTIDSATUSEHAT / SECRETKEYSATUSEHAT / URLAUTHSATUSEHAT.");
            status("Gagal mendapatkan access token");
            return;
        }

        String idPasien=patientId,idDokter=practitionerId;
        if(idPasien.equals("")){
            status("Mencari ID SATUSEHAT pasien...");
            idPasien=new SatuSehatCekNIK().tampilIDPasien(nikPasien);
        }
        if(idDokter.equals("")){
            status("Mencari ID SATUSEHAT dokter...");
            idDokter=new SatuSehatCekNIK().tampilIDParktisi(nikDokter);
        }
        if(sesiProses!=sesi){
            return; // pasien sudah diganti saat proses berjalan
        }
        patientId=idPasien;
        practitionerId=idDokter;
        SwingUtilities.invokeLater(this::tampilInfo);
        if(patientId.equals("")){
            pesan("NIK pasien "+nikPasien+" tidak ditemukan di SATUSEHAT (Master Patient Index).");
            status("ID SATUSEHAT pasien tidak ditemukan");
            return;
        }
        if(practitionerId.equals("")){
            pesan("NIK dokter "+practitionerName+" tidak ditemukan di SATUSEHAT (Practitioner).");
            status("ID SATUSEHAT dokter tidak ditemukan");
            return;
        }

        if(!emergency){
            status("Membuat SHLink (akses RME Nasional)...");
            ResponseEntity<String> res=post(token,"/v2/ntl/shl",payload(false));
            JsonNode root=baca(res.getBody());
            if(sesiProses!=sesi){
                return;
            }
            if(res.getStatusCode().value()==200&&root.path("success").asBoolean(false)){
                bukaSHL(root);
                return;
            }
            boolean consentRequired=res.getStatusCode().value()==403||
                    root.path("data").path("code").asText().equals("CONSENT_REQUIRED");
            if(!consentRequired){
                logRequest("SHL",root);
                tampilError("SHLink",res.getStatusCode().value(),root);
                return;
            }
            if(shlSaja){
                status("Persetujuan pasien belum tercatat. Selesaikan pengisian Kode Akses Pasien di halaman consent.");
                pesan("Persetujuan (consent) pasien belum tercatat di SATUSEHAT.\nSilakan selesaikan pengisian Kode Akses Pasien terlebih dahulu.");
                SwingUtilities.invokeLater(()->btnLanjut.setEnabled(true));
                tungguConsent(sesiProses,token);
                return;
            }
        }

        status(emergency?"Membuat link persetujuan EMERGENCY...":"Consent diperlukan. Membuat link persetujuan pasien (CHLink)...");
        ResponseEntity<String> res=post(token,"/v2/ntl/chl",payload(emergency));
        JsonNode root=baca(res.getBody());
        logRequest(emergency?"CHL-EMERGENCY":"CHL",root);
        if(sesiProses!=sesi){
            return;
        }
        if(res.getStatusCode().value()==200&&root.path("success").asBoolean(false)){
            urlAktif=root.path("data").path("verificationUrl").asText();
            status((emergency?"Isi alasan emergency dan data pengantar/wali pasien":"Minta Kode Akses dari SATUSEHAT Mobile pasien lalu isi di halaman ini")+
                    ". RME Nasional terbuka otomatis setelah persetujuan tercatat. Link berlaku s.d. "+root.path("data").path("expiredAt").asText());
            tampilkan(urlAktif);
            SwingUtilities.invokeLater(()->{
                btnBrowser.setEnabled(true);
                btnLanjut.setEnabled(true);
            });
            tungguConsent(sesiProses,token);
        }else{
            tampilError("CHLink",res.getStatusCode().value(),root);
        }
    }

    /** menampilkan SHLink hasil request SHL yang sukses */
    private void bukaSHL(JsonNode root){
        urlAktif=root.path("data").path("shlinkUrl").asText();
        String peringatan="";
        if(root.path("data").path("partial").asBoolean(false)){
            peringatan=" (sebagian data gagal dihimpun)";
        }
        if(root.path("data").path("warnings").size()>0){
            peringatan=peringatan+" Peringatan : "+root.path("data").path("warnings").toString();
        }
        logRequest("SHL",root);
        status("Rekam medis nasional terbuka. Berlaku s.d. "+root.path("data").path("expiredAt").asText()+peringatan);
        tampilkan(urlAktif);
        SwingUtilities.invokeLater(()->{
            btnBrowser.setEnabled(true);
            btnLanjut.setEnabled(false);
        });
    }

    /**
     * Mengecek SHL tiap 5 detik (maks. 10 menit) sampai consent pasien tercatat, lalu membuka RME Nasional.
     * Berhenti bila pasien diganti, proses baru dimulai, atau panel tidak tampil.
     */
    private void tungguConsent(final int sesiCek,final String tokenAwal){
        Thread cek=new Thread(()->{
            String token=tokenAwal;
            for(int i=0;i<120;i++){
                try{
                    Thread.sleep(5000);
                }catch(InterruptedException e){
                    return;
                }
                if(sesiCek!=sesi||!isShowing()){
                    return;
                }
                if(sedangProses){
                    continue;
                }
                try{
                    ResponseEntity<String> res=post(token,"/v2/ntl/shl",payload(false));
                    if(res.getStatusCode().value()==401){
                        token=token();
                        continue;
                    }
                    JsonNode root=baca(res.getBody());
                    if(sesiCek!=sesi){
                        return;
                    }
                    if(res.getStatusCode().value()==200&&root.path("success").asBoolean(false)){
                        bukaSHL(root);
                        return;
                    }
                }catch(Exception e){
                    System.out.println("Notifikasi SSRME : "+e);
                }
            }
            if(sesiCek==sesi){
                status("Waktu tunggu persetujuan habis. Klik \"Lanjut Buka Rekam Medis\" setelah consent selesai.");
            }
        },"SSRME-consent");
        cek.setDaemon(true);
        cek.start();
    }

    private boolean validasiData(){
        String kurang="";
        if(link.equals("")) kurang=kurang+"- URL SSRME (URLSSRMESATUSEHAT/URLAUTHSATUSEHAT) di setting/database.xml\n";
        if(organizationId==null||organizationId.equals("")) kurang=kurang+"- IDSATUSEHAT (Organization ID) di setting/database.xml\n";
        if(nikPasien.length()!=16) kurang=kurang+"- NIK pasien belum valid (harus 16 digit)\n";
        if(kdDokter.equals("")) kurang=kurang+"- Dokter belum dipilih\n";
        if(nikDokter.length()!=16) kurang=kurang+"- NIK (No.KTP) dokter di data pegawai belum valid\n";
        if(!kurang.equals("")){
            pesan("Data untuk akses SSRME belum lengkap :\n"+kurang);
            status("Data belum lengkap");
            return false;
        }
        return true;
    }

    private String payload(boolean emergency) throws Exception {
        ObjectNode body=mapper.createObjectNode();
        body.put("patient_id",patientId);
        body.put("patient_name",patientName);
        body.put("practitioner_id",practitionerId);
        body.put("practitioner_name",practitionerName);
        body.put("organization_id",organizationId);
        body.put("organization_name",organizationName);
        if(emergency){
            body.put("type_medical_summary","EMERGENCY");
        }
        return mapper.writeValueAsString(body);
    }

    /** ApiSatuSehat menyimpan state di field, jadi pemakaian dari beberapa thread diserialkan */
    private synchronized String token(){
        return api.TokenSatuSehat();
    }

    private synchronized ResponseEntity<String> post(String token,String path,String json) throws Exception {
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization","Bearer "+token);
        RestTemplate rest=api.getRest();
        // jangan lempar exception untuk 4xx/5xx agar body error SSRME bisa dibaca
        rest.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }
        });
        return rest.exchange(link+path,HttpMethod.POST,new HttpEntity<>(json,headers),String.class);
    }

    private JsonNode baca(String body){
        try{
            return mapper.readTree(body==null||body.equals("")?"{}":body);
        }catch(Exception e){
            return mapper.createObjectNode();
        }
    }

    /** hanya mencatat request_id & status untuk audit trail, tanpa token/URL sensitif */
    private void logRequest(String jenis,JsonNode root){
        String reqId=teks(root.path("request_id"),teks(root.path("fault").path("detail").path("request_id"),""));
        System.out.println("SSRME "+jenis+" | RM "+noRM+" | user "+akses.getkode()+" | dokter "+kdDokter+
                " | success "+teks(root.path("success"),"-")+" | request_id "+reqId);
    }

    /** pengganti JsonNode.asText(default) yang belum ada di jackson-databind 2.2.x */
    private static String teks(JsonNode node,String bawaan){
        if(node==null||node.isMissingNode()||node.isNull()){
            return bawaan;
        }
        return node.asText();
    }

    private void tampilError(String jenis,int httpCode,JsonNode root){
        String msg;
        if(!root.path("fault").isMissingNode()){
            msg=root.path("fault").path("faultstring").asText()+" ("+root.path("fault").path("detail").path("errorcode").asText()+")";
        }else if(root.path("message").isTextual()){
            msg=root.path("message").asText();
        }else if(root.path("data").isTextual()){
            msg=root.path("data").asText();
        }else{
            msg=root.toString();
        }
        String saran="";
        if(httpCode==401||!root.path("fault").isMissingNode()){
            saran="\n\nPeriksa credential SATUSEHAT (Client ID/Secret) dan masa berlaku token.";
        }else if(msg.toLowerCase().contains("wrong organization")){
            saran="\n\nOrganization ID (IDSATUSEHAT) tidak sesuai dengan credential yang digunakan.";
        }else if(msg.toLowerCase().contains("practitioner")){
            saran="\n\nPastikan dokter memiliki identitas SATUSEHAT dan SIP aktif di fasyankes ini.";
        }else if(msg.toLowerCase().contains("dibagikan")){
            saran="\n\nMinta pasien membuka SATUSEHAT Mobile, verifikasi identitas (KYC),\nlalu aktifkan 'Bagikan Resume Medis'.";
        }
        String reqId=teks(root.path("request_id"),"");
        status(jenis+" gagal ("+httpCode+") : "+msg);
        pesan(jenis+" gagal (HTTP "+httpCode+")\n"+msg+(reqId.equals("")?"":"\nRequest ID : "+reqId)+saran);
    }

    private void pesan(String teks){
        SwingUtilities.invokeLater(()->JOptionPane.showMessageDialog(this,teks,"SATUSEHAT Rekam Medis",JOptionPane.INFORMATION_MESSAGE));
    }

    private void bukaDiBrowser(){
        if(urlAktif.equals("")){
            return;
        }
        try{
            Desktop.getDesktop().browse(new URI(urlAktif));
        }catch(Exception e){
            JOptionPane.showMessageDialog(this,"Tidak dapat membuka browser : "+e.getMessage());
        }
    }

    /**
     * true bila WebView JavaFX terbukti tidak bisa menjalankan halaman SSRME (JavaFX lama, sintaks JS modern
     * tidak dikenali), sehingga halaman berikutnya langsung dibuka di browser. Berlaku sampai aplikasi ditutup.
     */
    private static volatile boolean modeBrowser=false;
    private static volatile boolean sudahTanyaBrowser=false;
    /** panel yang terakhir memuat halaman, tujuan laporan error dari log console WebView */
    private static volatile PanelSSRME panelAktif;

    /** dipanggil dari log console bila halaman SSRME gagal dibaca WebView (SyntaxError) */
    private void halamanTidakDidukung(){
        if(modeBrowser||sudahTanyaBrowser){
            return;
        }
        sudahTanyaBrowser=true;
        final String versi=System.getProperty("javafx.runtime.version","-");
        SwingUtilities.invokeLater(()->{
            status("Halaman SSRME tidak didukung WebView JavaFX "+versi+". Gunakan \"Buka di Browser\".");
            int jawab=JOptionPane.showConfirmDialog(this,
                    "Halaman SSRME tidak dapat dijalankan oleh WebView JavaFX "+versi+" di aplikasi ini\n"+
                    "(halaman memakai JavaScript versi baru).\n\n"+
                    "Buka di browser sekarang? Selanjutnya halaman SSRME langsung dibuka di browser,\n"+
                    "dan rekam medis nasional ikut terbuka di browser setelah persetujuan pasien tercatat.",
                    "SATUSEHAT Rekam Medis",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
            if(jawab==JOptionPane.YES_OPTION){
                modeBrowser=true;
                bukaDiBrowser();
            }
        });
    }

    /** buka URL SSRME di WebView, atau di browser bila WebView sudah terbukti tidak mendukung */
    private void tampilkan(String url){
        if(modeBrowser){
            SwingUtilities.invokeLater(this::bukaDiBrowser);
            return;
        }
        loadURL(url);
    }

    /** WebView dibuat saat pertama kali dibutuhkan agar form pemanggil tidak ikut memuat JavaFX */
    private void loadURL(String url){
        panelAktif=this;
        SwingUtilities.invokeLater(()->{
            if(jfxPanel==null){
                Platform.setImplicitExit(false);
                // JFXPanel baru ditempel ke form setelah scene terpasang: bila ditempel lebih dulu, JavaFX baru
                // (JDK 24+) melempar NullPointerException "sceneState is null" saat mengirim skala DPI
                jfxPanel=new JFXPanel();
                Platform.runLater(()->{
                    pasangLogConsole();
                    WebView view=new WebView();
                    engine=view.getEngine();
                    engine.setJavaScriptEnabled(true);
                    // sama dengan Khanza original: halaman SSRME tidak berjalan dengan User-Agent bawaan JavaFX
                    engine.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.5 Safari/605.1.15");
                    // folder data sendiri agar localStorage tersedia (null bila folder bawaan dikunci proses lain)
                    final java.io.File folderTmp=new java.io.File(System.getProperty("java.io.tmpdir"));
                    engine.setUserDataDirectory(new java.io.File(folderTmp,"khanza-ssrme-webview"));
                    engine.setOnError(ev->{
                        System.out.println("Notifikasi WebView SSRME : "+ev.getMessage());
                        if(ev.getEventType()==javafx.scene.web.WebErrorEvent.USER_DATA_DIRECTORY_ALREADY_IN_USE){
                            engine.setUserDataDirectory(new java.io.File(folderTmp,"khanza-ssrme-webview-"+java.util.UUID.randomUUID()));
                        }
                    });
                    // popup (target=_blank) dibuka di view yang sama
                    engine.setCreatePopupHandler(p->engine);
                    engine.setOnAlert(e->System.out.println("Notifikasi WebView SSRME : "+e.getData()));
                    engine.documentProperty().addListener((o,lama,baru)->{
                        if(baru!=null){
                            pasangKripto();
                        }
                    });
                    engine.getLoadWorker().exceptionProperty().addListener((o,old,value)->{
                        if(value!=null){
                            status("Halaman gagal dimuat : "+value.getMessage()+" - coba \"Buka di Browser\"");
                        }
                    });
                    jfxPanel.setScene(new Scene(view));
                    SwingUtilities.invokeLater(()->{
                        panelContent.add(jfxPanel,BorderLayout.CENTER);
                        panelContent.revalidate();
                        panelContent.repaint();
                    });
                });
            }
            Platform.runLater(()->{
                if(engine!=null){
                    engine.load(url);
                }
            });
        });
    }

    private void kosongkanHalaman(){
        urlAktif="";
        btnBrowser.setEnabled(false);
        btnLanjut.setEnabled(false);
        if(jfxPanel!=null){
            Platform.runLater(()->{
                if(engine!=null){
                    engine.load("about:blank");
                }
            });
        }
    }

    /** hentikan pengecekan consent dan kosongkan halaman (URL/data sensitif tidak tertinggal di WebView) */
    public void tutup(){
        sesi++;
        noRM="";
        kosongkanHalaman();
    }

    private void pasangKripto(){
        try{
            // netscape.javascript.JSObject dipanggil lewat reflection: tidak terlihat oleh javac JDK 25 untuk
            // target 1.8, tetapi tersedia saat runtime (modul jdk.jsobject / JavaFX)
            Object jendela=engine.executeScript("window");
            Class.forName("netscape.javascript.JSObject")
                    .getMethod("setMember",String.class,Object.class)
                    .invoke(jendela,"kriptoJava",kripto);
            engine.executeScript(JS_KRIPTO);
        }catch(Exception e){
            System.out.println("Notifikasi Kripto WebView SSRME : "+e);
        }
    }

    private static boolean logConsoleTerpasang=false;

    /**
     * Bug JavaFX sebelum 27 (GlassScene.updateSceneState tanpa cek null): saat JFXPanel dilepas lalu ditempel
     * lagi (form ditutup/dibuka), pembaruan skala DPI yang masih antre untuk scene lama melempar
     * NullPointerException "sceneState". Tidak berpengaruh ke tampilan, jadi khusus error itu tidak diteruskan
     * ke Global Error Handler (yang juga menyimpan log ke database). Error lain tetap diteruskan.
     * Harus dipanggil di thread JavaFX.
     */
    private static void pasangPenanganErrorFx(){
        final Thread threadFx=Thread.currentThread();
        final Thread.UncaughtExceptionHandler penangan=threadFx.getUncaughtExceptionHandler();
        threadFx.setUncaughtExceptionHandler((t,e)->{
            if(e instanceof NullPointerException&&e.getStackTrace().length>0
                    &&"com.sun.javafx.tk.quantum.GlassScene".equals(e.getStackTrace()[0].getClassName())
                    &&"updateSceneState".equals(e.getStackTrace()[0].getMethodName())){
                return;
            }
            if(penangan!=null){
                penangan.uncaughtException(t,e);
            }
        });
    }

    /**
     * Mencetak pesan console & error JavaScript WebView ke log (lewat com.sun.javafx.webkit.WebConsoleListener,
     * dipanggil dengan reflection karena API internal). Hanya nama file sumber yang dicetak, tanpa URL/token.
     */
    private static void pasangLogConsole(){
        if(logConsoleTerpasang){
            return;
        }
        logConsoleTerpasang=true;
        pasangPenanganErrorFx();
        try{
            Class<?> kelas=Class.forName("com.sun.javafx.webkit.WebConsoleListener");
            Object pendengar=java.lang.reflect.Proxy.newProxyInstance(kelas.getClassLoader(),new Class<?>[]{kelas},(proxy,method,args)->{
                if(method.getName().equals("messageAdded")&&args!=null&&args.length>=4){
                    String sumber=String.valueOf(args[3]);
                    sumber=sumber.replaceAll("[?#].*$","");
                    sumber=sumber.substring(sumber.lastIndexOf('/')+1);
                    String isi=String.valueOf(args[1]);
                    if(isi.startsWith("Unrecognized Content-Security-Policy")||isi.contains("source list for Content Security Policy")){
                        return null; // peringatan CSP yang tidak dikenali WebView, tidak berpengaruh
                    }
                    System.out.println("Console WebView SSRME : "+isi.trim()+" ["+sumber+":"+args[2]+"]");
                    PanelSSRME panel=panelAktif;
                    if(isi.contains("SyntaxError")&&panel!=null&&!panel.urlAktif.equals("")){
                        panel.halamanTidakDidukung();
                    }
                }
                return null;
            });
            kelas.getMethod("setDefaultListener",kelas).invoke(null,pendengar);
        }catch(Throwable e){
            System.out.println("Notifikasi SSRME : log console WebView tidak tersedia ("+e+")");
        }
    }

    /** pengganti crypto.subtle (digest, ECDH, HKDF, HMAC, AES-GCM) yang memanggil KriptoSSRME */
    private static final String JS_KRIPTO=
        "(function(){"+
        "if(!window.kriptoJava)return;"+
        "var J=window.kriptoJava;"+
        "if(!window.__logSSRME){window.__logSSRME=1;"+
            "window.addEventListener('error',function(e){J.log('JS error : '+e.message+' ['+String(e.filename||'').split('?')[0].split('/').pop()+':'+e.lineno+']');});"+
            "window.addEventListener('unhandledrejection',function(e){var r=e.reason;J.log('Promise ditolak : '+(r&&r.message?r.message:r));});}"+
        "if(!window.crypto){J.log('window.crypto tidak ada');return;}"+
        "if(window.crypto.subtle){J.log('crypto.subtle bawaan WebView dipakai');return;}"+
        "J.log('crypto.subtle pengganti dipasang');"+
        "var HL={'SHA-1':20,'SHA-256':32,'SHA-384':48,'SHA-512':64};"+
        "function P(f){return new Promise(function(ok,gagal){try{ok(f());}catch(e){gagal(e);}});}"+
        "function u8(x){return x instanceof ArrayBuffer?new Uint8Array(x):new Uint8Array(x.buffer,x.byteOffset,x.byteLength);}"+
        "function enc(x){var a=u8(x),s='',i;for(i=0;i<a.length;i+=8192)s+=String.fromCharCode.apply(null,a.subarray(i,i+8192));return btoa(s).split('+').join('-').split('/').join('_').replace(/=+$/,'');}"+
        "function dec(s){s=String(s).split('-').join('+').split('_').join('/');while(s.length%4)s+='=';var t=atob(s),a=new Uint8Array(t.length),i;for(i=0;i<t.length;i++)a[i]=t.charCodeAt(i);return a;}"+
        "function buf(a){return a.buffer.slice(a.byteOffset,a.byteOffset+a.byteLength);}"+
        "function cat(){var n=0,i,o,p=0;for(i=0;i<arguments.length;i++)n+=arguments[i].length;o=new Uint8Array(n);for(i=0;i<arguments.length;i++){o.set(arguments[i],p);p+=arguments[i].length;}return o;}"+
        "function nm(a){return typeof a==='string'?a:a.name;}"+
        "function hs(h){return (typeof h==='string'?h:h.name).toUpperCase();}"+
        "function sz(c){return c==='P-384'?48:32;}"+
        "function Kunci(t,a,e,u,o){this.type=t;this.algorithm=a;this.extractable=e;this.usages=u;this._=o;}"+
        "function sec(a,e,u,k){var n=nm(a),al={name:n};if(a.hash)al.hash={name:hs(a.hash)};if(n==='HMAC')al.length=a.length||k.length*8;if(n.indexOf('AES')===0)al.length=k.length*8;return new Kunci('secret',al,e,u,{k:k});}"+
        "function ec(a,e,u,c,d,x,y){return new Kunci(d?'private':'public',{name:'ECDH',namedCurve:c},e,u,{c:c,d:d,x:x,y:y});}"+
        "function hmac(h,k,d){return dec(J.hmac(h,enc(k),enc(d)));}"+
        "function hkdf(h,ikm,salt,info,n){var l=HL[h],prk=hmac(h,salt.length?salt:new Uint8Array(l),ikm),t=new Uint8Array(0),o=new Uint8Array(0),i=1;while(o.length<n){t=hmac(h,prk,cat(t,info,new Uint8Array([i++])));o=cat(o,t);}return o.subarray(0,n);}"+
        "var S={"+
        "digest:function(a,d){return P(function(){return buf(dec(J.digest(hs(a),enc(d))));});},"+
        "generateKey:function(a,e,u){return P(function(){var n=nm(a),p;"+
            "if(n==='ECDH'){p=String(J.ecGenerate(a.namedCurve)).split('.');return{privateKey:ec(a,e,u,a.namedCurve,p[0],p[1],p[2]),publicKey:ec(a,true,[],a.namedCurve,null,p[1],p[2])};}"+
            "if(n==='AES-GCM'){p=new Uint8Array(a.length/8);window.crypto.getRandomValues(p);return sec(a,e,u,p);}"+
            "throw new Error('NotSupportedError: generateKey '+n);});},"+
        "importKey:function(f,d,a,e,u){return P(function(){var n=nm(a),r,s=sz(a.namedCurve);"+
            "if(n==='ECDH'){if(f==='raw'){r=u8(d);if(r.length!==2*s+1||r[0]!==4)throw new Error('DataError: raw EC key');return ec(a,e,u,a.namedCurve,null,enc(r.subarray(1,1+s)),enc(r.subarray(1+s)));}"+
            "if(f==='jwk'){if(d.kty!=='EC'||d.crv!==a.namedCurve||!d.x||!d.y)throw new Error('DataError: jwk EC key');return ec(a,e,u,a.namedCurve,d.d||null,d.x,d.y);}"+
            "throw new Error('NotSupportedError: importKey '+f+' '+n);}"+
            "if(f==='raw')return sec(a,e,u,new Uint8Array(u8(d)));"+
            "if(f==='jwk'&&d.kty==='oct')return sec(a,e,u,dec(d.k));"+
            "throw new Error('NotSupportedError: importKey '+f+' '+n);});},"+
        "exportKey:function(f,k){return P(function(){var o=k._,r;if(!k.extractable)throw new Error('InvalidAccessError: key not extractable');"+
            "if(k.algorithm.name==='ECDH'){if(f==='raw'){if(o.d)throw new Error('InvalidAccessError: private key');return buf(cat(new Uint8Array([4]),dec(o.x),dec(o.y)));}"+
            "if(f==='jwk'){r={kty:'EC',crv:o.c,x:o.x,y:o.y,ext:true,key_ops:k.usages};if(o.d)r.d=o.d;return r;}}"+
            "else{if(f==='raw')return buf(o.k);if(f==='jwk')return{kty:'oct',k:enc(o.k),ext:true,key_ops:k.usages};}"+
            "throw new Error('NotSupportedError: exportKey '+f);});},"+
        "deriveBits:function(a,k,n){return P(function(){var o=k._,r;"+
            "if(nm(a)==='ECDH'){r=dec(J.ecdh(o.c,o.d,a.public._.x,a.public._.y));return buf(n?r.subarray(0,n>>3):r);}"+
            "if(nm(a)==='HKDF')return buf(hkdf(hs(a.hash),o.k,u8(a.salt),u8(a.info),n>>3));"+
            "throw new Error('NotSupportedError: deriveBits '+nm(a));});},"+
        "sign:function(a,k,d){return P(function(){if(nm(a)!=='HMAC')throw new Error('NotSupportedError: sign '+nm(a));return buf(hmac(k.algorithm.hash.name,k._.k,u8(d)));});},"+
        "encrypt:function(a,k,d){return P(function(){return buf(dec(J.gcm(true,enc(k._.k),enc(a.iv),enc(a.additionalData||new Uint8Array(0)),enc(d))));});},"+
        "decrypt:function(a,k,d){return P(function(){return buf(dec(J.gcm(false,enc(k._.k),enc(a.iv),enc(a.additionalData||new Uint8Array(0)),enc(d))));});}"+
        "};"+
        // deriveKey = deriveBits (ECDH/HKDF) lalu dijadikan kunci rahasia AES/HMAC
        "S.deriveKey=function(a,k,t,e,u){var n=t.length||(nm(t)==='HMAC'?HL[hs(t.hash)]*8:256);"+
            "return S.deriveBits(a,k,n).then(function(b){return sec(t,e,u,new Uint8Array(b));});};"+
        // setiap kegagalan dicatat ke log Java agar mudah ditelusuri
        "Object.keys(S).forEach(function(n){var f=S[n];S[n]=function(){var r;try{r=f.apply(S,arguments);}catch(e){r=Promise.reject(e);}"+
            "return r.catch(function(e){J.log('crypto.subtle.'+n+' gagal : '+(e&&e.message?e.message:e));throw e;});};});"+
        "Object.defineProperty(window.crypto,'subtle',{value:S,configurable:true});"+
        "})();";

    /** jembatan kriptografi Java untuk JS_KRIPTO, semua data dalam base64url tanpa padding */
    public static class KriptoSSRME {
        private static java.security.spec.ECParameterSpec kurva(String nama) throws Exception {
            java.security.AlgorithmParameters ap=java.security.AlgorithmParameters.getInstance("EC");
            ap.init(new java.security.spec.ECGenParameterSpec(nama.equals("P-384")?"secp384r1":"secp256r1"));
            return ap.getParameterSpec(java.security.spec.ECParameterSpec.class);
        }

        private static byte[] tetap(java.math.BigInteger v,int n){
            byte[] b=v.toByteArray(),h=new byte[n];
            if(b.length>n){
                System.arraycopy(b,b.length-n,h,0,n);
            }else{
                System.arraycopy(b,0,h,n-b.length,b.length);
            }
            return h;
        }

        /** dipanggil JS_KRIPTO untuk mencatat status/error halaman SSRME */
        public void log(String pesan){
            System.out.println("WebView SSRME : "+pesan);
        }

        private static String enc(byte[] b){
            return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
        }

        private static byte[] dec(String s){
            return Base64.getUrlDecoder().decode(s);
        }

        public String ecGenerate(String crv) throws Exception {
            java.security.spec.ECParameterSpec sp=kurva(crv);
            java.security.KeyPairGenerator kpg=java.security.KeyPairGenerator.getInstance("EC");
            kpg.initialize(sp);
            java.security.KeyPair kp=kpg.generateKeyPair();
            int n=(sp.getOrder().bitLength()+7)/8;
            java.security.spec.ECPoint w=((java.security.interfaces.ECPublicKey) kp.getPublic()).getW();
            return enc(tetap(((java.security.interfaces.ECPrivateKey) kp.getPrivate()).getS(),n))+"."+enc(tetap(w.getAffineX(),n))+"."+enc(tetap(w.getAffineY(),n));
        }

        public String ecdh(String crv,String d,String x,String y) throws Exception {
            java.security.spec.ECParameterSpec sp=kurva(crv);
            java.security.KeyFactory kf=java.security.KeyFactory.getInstance("EC");
            javax.crypto.KeyAgreement ka=javax.crypto.KeyAgreement.getInstance("ECDH");
            ka.init(kf.generatePrivate(new java.security.spec.ECPrivateKeySpec(new java.math.BigInteger(1,dec(d)),sp)));
            ka.doPhase(kf.generatePublic(new java.security.spec.ECPublicKeySpec(new java.security.spec.ECPoint(new java.math.BigInteger(1,dec(x)),new java.math.BigInteger(1,dec(y))),sp)),true);
            return enc(ka.generateSecret());
        }

        public String hmac(String hash,String key,String data) throws Exception {
            javax.crypto.Mac m=javax.crypto.Mac.getInstance("Hmac"+hash.replace("-",""));
            byte[] k=dec(key);
            m.init(new javax.crypto.spec.SecretKeySpec(k.length==0?new byte[1]:k,m.getAlgorithm()));
            return enc(m.doFinal(dec(data)));
        }

        public String digest(String hash,String data) throws Exception {
            return enc(java.security.MessageDigest.getInstance(hash).digest(dec(data)));
        }

        public String gcm(boolean sandi,String key,String iv,String aad,String data) throws Exception {
            javax.crypto.Cipher c=javax.crypto.Cipher.getInstance("AES/GCM/NoPadding");
            c.init(sandi?javax.crypto.Cipher.ENCRYPT_MODE:javax.crypto.Cipher.DECRYPT_MODE,new javax.crypto.spec.SecretKeySpec(dec(key),"AES"),new javax.crypto.spec.GCMParameterSpec(128,dec(iv)));
            byte[] a=dec(aad);
            if(a.length>0){
                c.updateAAD(a);
            }
            return enc(c.doFinal(dec(data)));
        }
    }
}
