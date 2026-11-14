package simrskhanza;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Menampilkan isi CHANGELOG.md sebagai informasi "Apa yang Baru".
 * File dibaca dari folder aplikasi, bila tidak ada dibaca dari dalam jar.
 * Versi terakhir yang sudah dilihat disimpan di cache/changelog.iyem (per komputer).
 */
public class DlgChangelog extends javax.swing.JDialog {

    private static final String FILECHANGELOG="CHANGELOG.md";
    private static final String FILEVERSIDILIHAT="cache/changelog.iyem";
    private static final Pattern POLAVERSI=Pattern.compile("^##\\s+\\[([^\\]]+)\\]");

    private final javax.swing.JEditorPane Isi=new javax.swing.JEditorPane();

    public DlgChangelog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle("::[ Apa yang Baru di SIMRS ]::");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(720,560);

        Isi.setContentType("text/html");
        Isi.setEditable(false);
        Isi.setText(keHTML(bacaChangelog()));
        Isi.setCaretPosition(0);

        widget.Button BtnKeluar=new widget.Button();
        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setMnemonic('T');
        BtnKeluar.setText("Tutup");
        BtnKeluar.setToolTipText("Alt+T");
        BtnKeluar.setPreferredSize(new java.awt.Dimension(100,30));
        BtnKeluar.addActionListener(evt -> dispose());

        javax.swing.JPanel panelBawah=new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        panelBawah.add(BtnKeluar);

        getContentPane().setLayout(new java.awt.BorderLayout());
        getContentPane().add(new javax.swing.JScrollPane(Isi),java.awt.BorderLayout.CENTER);
        getContentPane().add(panelBawah,java.awt.BorderLayout.SOUTH);
        getRootPane().setDefaultButton(BtnKeluar);
    }

    /** Dipanggil setelah login: tampilkan sekali bila ada versi baru yang belum dilihat di komputer ini. */
    public static void tampilkanJikaAdaUpdate(java.awt.Frame parent){
        try {
            String versi=versiTerbaru(bacaChangelog());
            if(versi.isEmpty()||versi.equals(bacaVersiDilihat())){
                return;
            }
            DlgChangelog dlg=new DlgChangelog(parent,true);
            dlg.setLocationRelativeTo(parent);
            dlg.setVisible(true);
            simpanVersiDilihat(versi);
        } catch (Exception e) {
            System.out.println("Notif Changelog : "+e);
        }
    }

    private static String bacaChangelog(){
        try {
            File file=new File(FILECHANGELOG);
            if(file.exists()){
                return new String(Files.readAllBytes(file.toPath()),StandardCharsets.UTF_8);
            }
            try (InputStream in=DlgChangelog.class.getResourceAsStream("/"+FILECHANGELOG)) {
                if(in!=null){
                    java.io.ByteArrayOutputStream out=new java.io.ByteArrayOutputStream();
                    byte[] buffer=new byte[4096];
                    int n;
                    while((n=in.read(buffer))!=-1){
                        out.write(buffer,0,n);
                    }
                    return new String(out.toByteArray(),StandardCharsets.UTF_8);
                }
            }
        } catch (Exception e) {
            System.out.println("Notif Changelog : "+e);
        }
        return "";
    }

    private static String versiTerbaru(String teks){
        for(String baris:teks.split("\\R")){
            Matcher m=POLAVERSI.matcher(baris.trim());
            if(m.find()&&!isBelumRilis(m.group(1))){
                return m.group(1).trim();
            }
        }
        return "";
    }

    private static boolean isBelumRilis(String versi){
        return versi.trim().equalsIgnoreCase("Belum Rilis")||versi.trim().equalsIgnoreCase("Unreleased");
    }

    private static String bacaVersiDilihat(){
        try {
            File file=new File(FILEVERSIDILIHAT);
            if(file.exists()){
                return new String(Files.readAllBytes(file.toPath()),StandardCharsets.UTF_8).trim();
            }
        } catch (Exception e) {
            System.out.println("Notif Changelog : "+e);
        }
        return "";
    }

    private static void simpanVersiDilihat(String versi){
        try {
            File file=new File(FILEVERSIDILIHAT);
            if(file.getParentFile()!=null){
                file.getParentFile().mkdirs();
            }
            Files.write(file.toPath(),versi.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.out.println("Notif Changelog : "+e);
        }
    }

    /**
     * Konversi markdown sederhana ke HTML untuk JEditorPane.
     * Bagian [Belum Rilis], Catatan Teknis, komentar HTML, dan teks sebelum versi pertama dilewati.
     */
    private static String keHTML(String teks){
        StringBuilder html=new StringBuilder();
        html.append("<html><head><style>")
            .append("body{font-family:Tahoma;font-size:11pt;color:#323232;margin:10px;}")
            .append("h1{font-size:16pt;color:#326432;}")
            .append("h2{font-size:13pt;color:#ffffff;background-color:#5a965a;padding:4px;margin-top:14px;}")
            .append("h3{font-size:11pt;color:#326432;margin-top:8px;margin-bottom:2px;}")
            .append("li{margin-bottom:3px;}")
            .append("code{font-family:Monospaced;}")
            .append("</style></head><body>")
            .append("<h1>Apa yang Baru di SIMRS</h1>");

        if(teks.isEmpty()){
            html.append("<p>Belum ada catatan perubahan.</p></body></html>");
            return html.toString();
        }

        boolean tampil=false,lewatiSub=false,dalamKomentar=false,dalamList=false;
        for(String baris:teks.split("\\R")){
            String b=baris.trim();
            if(dalamKomentar){
                if(b.contains("-->")){
                    dalamKomentar=false;
                }
                continue;
            }
            if(b.startsWith("<!--")){
                dalamKomentar=!b.contains("-->");
                continue;
            }

            Matcher m=POLAVERSI.matcher(b);
            if(m.find()){
                tampil=!isBelumRilis(m.group(1));
                lewatiSub=false;
            }else if(b.startsWith("### ")){
                lewatiSub=b.substring(4).trim().equalsIgnoreCase("Catatan Teknis");
            }else if(b.startsWith("# ")){
                continue;
            }
            if(!tampil||lewatiSub){
                continue;
            }

            boolean butir=b.startsWith("- ")||b.startsWith("* ");
            if(dalamList&&!butir){
                html.append("</ul>");
                dalamList=false;
            }

            if(b.isEmpty()){
                continue;
            }else if(b.startsWith("## ")){
                html.append("<h2>").append(formatInline(b.substring(3).trim().replaceFirst("^\\[([^\\]]+)\\]","Versi $1"))).append("</h2>");
            }else if(b.startsWith("### ")){
                html.append("<h3>").append(formatInline(b.substring(4).trim())).append("</h3>");
            }else if(butir){
                if(!dalamList){
                    html.append("<ul>");
                    dalamList=true;
                }
                html.append("<li>").append(formatInline(b.substring(2).trim())).append("</li>");
            }else{
                html.append("<p>").append(formatInline(b)).append("</p>");
            }
        }
        if(dalamList){
            html.append("</ul>");
        }
        return html.append("</body></html>").toString();
    }

    private static String formatInline(String teks){
        String hasil=teks.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
        hasil=hasil.replaceAll("`([^`]+)`","<code>$1</code>");
        hasil=hasil.replaceAll("\\*\\*([^*]+)\\*\\*","<b>$1</b>");
        hasil=hasil.replaceAll("\\[([^\\]]+)\\]\\(([^)]+)\\)","$1");
        return hasil;
    }
}
