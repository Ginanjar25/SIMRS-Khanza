/*
  Dilarang keras menggandakan/mengcopy/menyebarkan/membajak/mendecompile 
  Software ini dalam bentuk apapun tanpa seijin pembuat software
  (Khanza.Soft Media). Bagi yang sengaja membajak softaware ini ta
  npa ijin, kami sumpahi sial 1000 turunan, miskin sampai 500 turu
  nan. Selalu mendapat kecelakaan sampai 400 turunan. Anak pertama
  nya cacat tidak punya kaki sampai 300 turunan. Susah cari jodoh
  sampai umur 50 tahun sampai 200 turunan. Ya Alloh maafkan kami 
  karena telah berdoa buruk, semua ini kami lakukan karena kami ti
  dak pernah rela karya kami dibajak tanpa ijin.
 */

package bridging;

import java.awt.Dimension;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.validasi;
import fungsi.batasInput;
import fungsi.koneksiDB;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import static javafx.concurrent.Worker.State.FAILED;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 *
 * @author dosen
 */
public final class ICareRiwayatPerawatan extends javax.swing.JDialog {
    private validasi Valid=new validasi();
    private ApiICareBPJS api=new ApiICareBPJS();
    private String link="",utc="",requestJson="";
    private HttpHeaders headers ;
    private HttpEntity requestEntity;
    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode root;
    private JsonNode nameNode;
    private JsonNode response;
    private final JFXPanel jfxPanel = new JFXPanel();
    private WebEngine engine;
 
    private final JLabel lblStatus = new JLabel();

    private final JTextField txtURL = new JTextField();
    private final JProgressBar progressBar = new JProgressBar();
        
    /** Creates new form DlgKamar
     * @param parent
     * @param modal */
    public ICareRiwayatPerawatan(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initComponents2();
        
        this.setLocation(10,2);
        setSize(628,674);

        
        NoKartu.setDocument(new batasInput((int)100).getKata(NoKartu));
        KdDPJPLayanan.setDocument(new batasInput((int)100).getKata(KdDPJPLayanan));
        
        try {
            link=koneksiDB.URLAPIICARE();
        } catch (Exception e) {
            System.out.println("E : "+e);
        }
    }
    
    private void initComponents2() {           
        txtURL.addActionListener((ActionEvent e) -> {
            loadURL(txtURL.getText());
        });
  
        progressBar.setPreferredSize(new Dimension(150, 18));
        progressBar.setStringPainted(true);
        
        PanelContent.add(jfxPanel, BorderLayout.CENTER);
        
        internalFrame1.add(PanelContent);        
    }
    
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        internalFrame1 = new widget.InternalFrame();
        panelGlass6 = new widget.panelisi();
        jLabel16 = new widget.Label();
        NoKartu = new widget.TextBox();
        LabelPoli6 = new widget.Label();
        KdDPJPLayanan = new widget.TextBox();
        BtnCari = new widget.Button();
        jLabel17 = new widget.Label();
        BtnKeluar = new widget.Button();
        PanelContent = new widget.panelisi();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(null);
        setIconImages(null);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Riwayat Perawatan ICare FKTL BPJS ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass6.setName("panelGlass6"); // NOI18N
        panelGlass6.setPreferredSize(new java.awt.Dimension(44, 54));
        panelGlass6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel16.setText("NIK/No.Kartu :");
        jLabel16.setName("jLabel16"); // NOI18N
        jLabel16.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass6.add(jLabel16);

        NoKartu.setName("NoKartu"); // NOI18N
        NoKartu.setPreferredSize(new java.awt.Dimension(170, 23));
        NoKartu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoKartuKeyPressed(evt);
            }
        });
        panelGlass6.add(NoKartu);

        LabelPoli6.setText("Kode Dokter :");
        LabelPoli6.setName("LabelPoli6"); // NOI18N
        LabelPoli6.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass6.add(LabelPoli6);

        KdDPJPLayanan.setEditable(false);
        KdDPJPLayanan.setBackground(new java.awt.Color(245, 250, 240));
        KdDPJPLayanan.setHighlighter(null);
        KdDPJPLayanan.setName("KdDPJPLayanan"); // NOI18N
        KdDPJPLayanan.setPreferredSize(new java.awt.Dimension(130, 23));
        panelGlass6.add(KdDPJPLayanan);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('6');
        BtnCari.setToolTipText("Alt+6");
        BtnCari.setName("BtnCari"); // NOI18N
        BtnCari.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariActionPerformed(evt);
            }
        });
        BtnCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCariKeyPressed(evt);
            }
        });
        panelGlass6.add(BtnCari);

        jLabel17.setName("jLabel17"); // NOI18N
        jLabel17.setPreferredSize(new java.awt.Dimension(15, 23));
        panelGlass6.add(jLabel17);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setName("BtnKeluar"); // NOI18N
        BtnKeluar.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluarActionPerformed(evt);
            }
        });
        BtnKeluar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnKeluarKeyPressed(evt);
            }
        });
        panelGlass6.add(BtnKeluar);

        internalFrame1.add(panelGlass6, java.awt.BorderLayout.PAGE_END);

        PanelContent.setName("PanelContent"); // NOI18N
        PanelContent.setPreferredSize(new java.awt.Dimension(55, 55));
        PanelContent.setLayout(new java.awt.BorderLayout());
        internalFrame1.add(PanelContent, java.awt.BorderLayout.CENTER);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            dispose();
        }else{Valid.pindah(evt,NoKartu,BtnKeluar);}
    }//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnCariActionPerformed(null);
        }else{
            Valid.pindah(evt,NoKartu,BtnKeluar);
        }
    }//GEN-LAST:event_BtnCariKeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if(NoKartu.getText().equals("")){
            Valid.textKosong(NoKartu,"Parameter");
        }else if(KdDPJPLayanan.getText().equals("")){
            Valid.textKosong(KdDPJPLayanan,"Dokter");
        }else{
            tampil();
        }
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_BtnCariActionPerformed

    private void NoKartuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoKartuKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            BtnCariActionPerformed(null);
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            BtnKeluar.requestFocus();
        }
    }//GEN-LAST:event_NoKartuKeyPressed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            ICareRiwayatPerawatan dialog = new ICareRiwayatPerawatan(new javax.swing.JFrame(), true);
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
    private widget.Button BtnCari;
    private widget.Button BtnKeluar;
    private widget.TextBox KdDPJPLayanan;
    private widget.Label LabelPoli6;
    private widget.TextBox NoKartu;
    private widget.panelisi PanelContent;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private widget.panelisi panelGlass6;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        try {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Content-Type","application/json");
            headers.add("x-cons-id",koneksiDB.CONSIDAPIICARE());
            utc=String.valueOf(api.GetUTCdatetimeAsString());
            headers.add("x-timestamp",utc);
            headers.add("x-signature",api.getHmac(utc));
            headers.add("user_key",koneksiDB.USERKEYAPIICARE());
            //System.out.println("x-signature:"+api.getHmac(utc));
            //System.out.println("x-timestamp:"+utc);
            //System.out.println("x-cons-id:"+koneksiDB.CONSIDAPIICARE());
            //System.out.println("user_key:"+koneksiDB.USERKEYAPIICARE());
            requestJson="{"+
                            "\"param\": \""+NoKartu.getText().trim()+"\","+
                            "\"kodedokter\": "+KdDPJPLayanan.getText().trim()+""+
                        "}";
            System.out.println("JSON : "+requestJson+"\n");
	    requestEntity = new HttpEntity(requestJson,headers);
            requestJson= mapper.writeValueAsString(api.getRest().exchange(link+"/validate", HttpMethod.POST, requestEntity,Object.class).getBody());
            System.out.println("URL:"+link+"/validate");
            System.out.println("JSON : "+requestJson);
            root = mapper.readTree(requestJson);
            nameNode = root.path("metaData");
            if(nameNode.path("code").asText().equals("200")){
                response = mapper.readTree(api.Decrypt(root.path("response").asText(),utc));
                System.out.println("Response : "+response.path("url"));
                try {
                    loadURL(response.path("url").asText());
                } catch (Exception ex) {
                    System.out.println("Notifikasi : "+ex);
                }
            }else {
                JOptionPane.showMessageDialog(null,nameNode.path("message").asText());                
            }   
        } catch (Exception ex) {
            System.out.println("Notifikasi : "+ex.getMessage());
            if(ex.toString().contains("UnknownHostException")){
                JOptionPane.showMessageDialog(rootPane,"Koneksi ke server BPJS terputus...!");
            }
        }
    } 
    
    public void setPasien(String param,String kodedokter){
        NoKartu.setText(param);
        KdDPJPLayanan.setText(kodedokter);
        tampil();
    }
    
    private void createScene() {        
        Platform.runLater(new Runnable() {

            public void run() {
                WebView view = new WebView();
                
                engine = view.getEngine();
                engine.setJavaScriptEnabled(true);
                
                engine.setCreatePopupHandler(new Callback<PopupFeatures, WebEngine>() {
                    @Override
                    public WebEngine call(PopupFeatures p) {
                        Stage stage = new Stage(StageStyle.TRANSPARENT);
                        return view.getEngine();
                    }
                });
                
                engine.titleProperty().addListener((ObservableValue<? extends String> observable, String oldValue, final String newValue) -> {
                    SwingUtilities.invokeLater(() -> {
                        ICareRiwayatPerawatan.this.setTitle(newValue);
                    });
                });
                
                
                engine.setOnStatusChanged((final WebEvent<String> event) -> {
                    SwingUtilities.invokeLater(() -> {
                        lblStatus.setText(event.getData());
                    });
                });
                
                
                engine.getLoadWorker().workDoneProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldValue, final Number newValue) -> {
                    SwingUtilities.invokeLater(() -> {
                        progressBar.setValue(newValue.intValue());
                    });                                                   
                });
                
                engine.getLoadWorker().exceptionProperty().addListener((ObservableValue<? extends Throwable> o, Throwable old, final Throwable value) -> {
                    if (engine.getLoadWorker().getState() == FAILED) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(
                                    PanelContent,
                                    (value != null) ?
                                            engine.getLocation() + "\n" + value.getMessage() :
                                            engine.getLocation() + "\nUnexpected Catatan.",
                                    "Loading Catatan...",
                                    JOptionPane.ERROR_MESSAGE);
                        });
                    }
                });
                
                
                engine.locationProperty().addListener((ObservableValue<? extends String> ov, String oldValue, final String newValue) -> {
                    SwingUtilities.invokeLater(() -> {
                        txtURL.setText(newValue);
                    });
                });
                
                engine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
                    @Override
                    public void changed(ObservableValue ov, State oldState, State newState) {
                        if (newState == State.SUCCEEDED) {
                            try {
                                System.out.println("URL : "+engine.getLocation());
                            } catch (Exception ex) {
                                System.out.println("Notifikasi : "+ex);
                            }
                        } 
                    }
                });
                
                jfxPanel.setScene(new Scene(view));
            }
        });
    }
 
    public void loadURL(String url) {  
        try {
            createScene();
        } catch (Exception e) {
        }
        
        Platform.runLater(() -> {
            try {
                engine.load(url);
            }catch (Exception exception) {
                engine.load(url);
            }
        });        
    }    
    
    public void CloseScane(){
        Platform.setImplicitExit(false);
    }
 
}
