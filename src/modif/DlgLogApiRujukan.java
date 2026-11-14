package modif;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.WindowConstants;

/**
 * Jendela log request/response API rujukan (permintaan BPJS saat UAT), mengambang dan selalu di atas
 * agar tidak menutupi tabel data rujukan. Dipakai BPJSRujukanKeluar bila TAMPILLOGAPIRUJUKAN=yes.
 * Bila jendela ditutup, akan muncul lagi otomatis saat ada hit API baru.
 */
public class DlgLogApiRujukan extends javax.swing.JDialog implements RujukanSatuSehatVClaim.PencatatApi {
    private final widget.TextArea LogApi=new widget.TextArea();

    public DlgLogApiRujukan(java.awt.Window parent){
        super(parent,"Log Request & Response API Rujukan",ModalityType.MODELESS);
        initComponents();
    }

    private void initComponents(){
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setAlwaysOnTop(true);
        // tetap bisa digeser/scroll walau jendela kriteria, faskes, atau pesan sedang terbuka (modal)
        setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);

        widget.InternalFrame internalFrame1=new widget.InternalFrame();
        internalFrame1.setBorder(DlgKriteriaRujukanSatuSehat.judul("::[ Log Request & Response API BPJS / Satu Sehat ]::"));
        internalFrame1.setLayout(new BorderLayout(1,1));

        LogApi.setEditable(false);
        LogApi.setLineWrap(true);
        LogApi.setFont(new Font("Monospaced",Font.PLAIN,11));
        widget.ScrollPane Scroll=new widget.ScrollPane();
        Scroll.setOpaque(true);
        Scroll.setViewportView(LogApi);

        widget.panelisi panelGlass8=new widget.panelisi();
        panelGlass8.setPreferredSize(new Dimension(44,44));
        panelGlass8.setLayout(new FlowLayout(FlowLayout.LEFT,5,9));
        widget.Button BtnSalin=DlgKriteriaRujukanSatuSehat.tombol("Salin","editcopy.png",'S',90);
        BtnSalin.setToolTipText("Alt+S, Salin semua log ke clipboard");
        BtnSalin.addActionListener(evt->Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(LogApi.getText()),null));
        widget.Button BtnBersihkan=DlgKriteriaRujukanSatuSehat.tombol("Bersihkan","refresh.png",'B',110);
        BtnBersihkan.setToolTipText("Alt+B, Kosongkan log");
        BtnBersihkan.addActionListener(evt->LogApi.setText(""));
        widget.Button BtnTutup=DlgKriteriaRujukanSatuSehat.tombol("Tutup","exit.png",'T',90);
        BtnTutup.setToolTipText("Alt+T, Sembunyikan, muncul lagi saat ada hit API baru");
        BtnTutup.addActionListener(evt->setVisible(false));
        panelGlass8.add(BtnSalin);
        panelGlass8.add(BtnBersihkan);
        panelGlass8.add(BtnTutup);

        internalFrame1.add(Scroll,BorderLayout.CENTER);
        internalFrame1.add(panelGlass8,BorderLayout.PAGE_END);
        setContentPane(internalFrame1);

        // pojok kanan layar, setengah tinggi layar
        Rectangle layar=java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        setSize(520,Math.max(400,layar.height/2));
        setLocation(layar.x+layar.width-getWidth()-10,layar.y+60);
    }

    /** Menambahkan satu hit API ke log (paling bawah = paling baru) lalu menampilkan jendela bila tersembunyi. */
    @Override
    public void catat(String judul,String metode,String url,String request,String response){
        LogApi.append("===== ["+new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date())+"] "+judul+" =====\n"+
                      metode+" "+url+"\n"+
                      "--- Request ---\n"+(request.equals("")?"(tanpa body)":request)+"\n"+
                      "--- Response ---\n"+response+"\n\n");
        LogApi.setCaretPosition(LogApi.getDocument().getLength());
        if(!isVisible()){
            setVisible(true);
        }
    }
}
