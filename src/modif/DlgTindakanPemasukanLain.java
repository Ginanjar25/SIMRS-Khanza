package modif;

import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.validasi;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

/**
 *
 * Master daftar tindakan/paket NON CLAIM yang masuk pemasukan lain-lain.
 * Disimpan di file cache ./cache/tindakanpemasukanlain.iyem (tanpa tabel database),
 * daftar ini yang tampil sebagai checkbox di DlgPilihTindakan dan DlgKwitansiNonClaim.
 * Kode dicocokkan dengan 7 karakter pertama kd_jenis_prw di billing, atau nama_biaya di tambahan biaya.
 */
public class DlgTindakanPemasukanLain extends JDialog {
    private final DefaultTableModel tabMode;
    private final Connection koneksi=koneksiDB.condb();
    private final validasi Valid=new validasi();
    private final KwitansiUI UI=new KwitansiUI();
    private PreparedStatement ps;
    private ResultSet rs;
    private final widget.TextBox Kd=new widget.TextBox(),Nm=new widget.TextBox(),TCari=new widget.TextBox();
    private final widget.Table tbTindakan=new widget.Table();
    private final widget.Label LCount=new widget.Label();
    private widget.Button BtnSimpan,BtnBatal,BtnHapus,BtnEdit,BtnKeluar;
    private List<String[]> semua;
    private boolean diubah=false;

    public DlgTindakanPemasukanLain(){
        super((java.awt.Frame)null,"Tindakan/Paket Pemasukan Lain",true);
        tabMode=new DefaultTableModel(null,new Object[]{"Kode","Nama Tindakan/Paket"}){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        tbTindakan.setModel(tabMode);
        tbTindakan.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbTindakan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbTindakan.getColumnModel().getColumn(0).setPreferredWidth(90);
        tbTindakan.getColumnModel().getColumn(1).setPreferredWidth(450);
        tbTindakan.setDefaultRenderer(Object.class,new WarnaTable());

        // kode dicocokkan dengan substring(kd_jenis_prw,1,7) di billing
        Kd.setDocument(new batasInput((byte)7).getKata(Kd));
        Nm.setDocument(new batasInput((byte)100).getKata(Nm));
        TCari.setDocument(new batasInput((byte)100).getKata(TCari));

        initComponents();
        isCek();
        semua=UI.daftarTindakan();
        tampil();
        setSize(628,560);
        setLocationRelativeTo(null);
    }

    private void initComponents(){
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        widget.InternalFrame internalFrame1=new widget.InternalFrame();
        internalFrame1.setBorder(UI.judul("::[ Tindakan/Paket Pemasukan Lain-Lain ]::"));
        internalFrame1.setLayout(new BorderLayout(1,1));

        widget.ScrollPane Scroll=new widget.ScrollPane();
        Scroll.setOpaque(true);
        tbTindakan.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbTindakan.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                getData();
            }
        });
        tbTindakan.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                if((evt.getKeyCode()==KeyEvent.VK_UP)||(evt.getKeyCode()==KeyEvent.VK_DOWN)||(evt.getKeyCode()==KeyEvent.VK_ENTER)){
                    getData();
                }
            }
        });
        Scroll.setViewportView(tbTindakan);

        widget.panelisi panelisi4=new widget.panelisi();
        panelisi4.setPreferredSize(new Dimension(100,44));
        panelisi4.setLayout(null);
        panelisi4.add(UI.label("Kode :",0,12,45));
        Kd.setBounds(48,12,80,23);
        Kd.setToolTipText("Kode tindakan (7 karakter) atau nama tambahan biaya, Enter untuk mengambil nama tindakan");
        Kd.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdKeyPressed(evt);
            }
        });
        panelisi4.add(Kd);
        panelisi4.add(UI.label("Nama :",130,12,45));
        Nm.setBounds(178,12,420,23);
        Nm.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Valid.pindah(evt,Kd,BtnSimpan);
            }
        });
        panelisi4.add(Nm);

        widget.panelisi panelisi3=new widget.panelisi();
        panelisi3.setPreferredSize(new Dimension(100,43));
        panelisi3.setLayout(new FlowLayout(FlowLayout.LEFT,4,9));
        widget.Label label9=new widget.Label();
        label9.setText("Key Word :");
        label9.setPreferredSize(new Dimension(68,23));
        panelisi3.add(label9);
        TCari.setPreferredSize(new Dimension(300,23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if(evt.getKeyCode()==KeyEvent.VK_ENTER){
                    tampil();
                }
            }
        });
        panelisi3.add(TCari);
        widget.Button BtnCari=new widget.Button();
        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCari.setMnemonic('1');
        BtnCari.setToolTipText("Alt+1");
        BtnCari.setPreferredSize(new Dimension(28,23));
        BtnCari.addActionListener(evt->tampil());
        panelisi3.add(BtnCari);
        widget.Button BtnAll=new widget.Button();
        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png")));
        BtnAll.setMnemonic('2');
        BtnAll.setToolTipText("Alt+2");
        BtnAll.setPreferredSize(new Dimension(28,23));
        BtnAll.addActionListener(evt->{
            TCari.setText("");
            tampil();
        });
        panelisi3.add(BtnAll);
        widget.Label label10=new widget.Label();
        label10.setText("Record :");
        label10.setPreferredSize(new Dimension(60,23));
        panelisi3.add(label10);
        LCount.setText("0");
        LCount.setPreferredSize(new Dimension(50,23));
        panelisi3.add(LCount);

        widget.panelisi panelisi1=new widget.panelisi();
        panelisi1.setPreferredSize(new Dimension(55,55));
        panelisi1.setLayout(new FlowLayout(FlowLayout.LEFT,5,9));
        BtnSimpan=UI.tombol("Simpan","save-16x16.png",'S',100);
        BtnSimpan.addActionListener(evt->BtnSimpanActionPerformed());
        BtnBatal=UI.tombol("Baru","Cancel-2-16x16.png",'B',100);
        BtnBatal.addActionListener(evt->emptTeks());
        BtnHapus=UI.tombol("Hapus","stop_f2.png",'H',100);
        BtnHapus.addActionListener(evt->BtnHapusActionPerformed());
        BtnEdit=UI.tombol("Ganti","inventaris.png",'G',100);
        BtnEdit.addActionListener(evt->BtnEditActionPerformed());
        BtnKeluar=UI.tombol("Keluar","exit.png",'K',100);
        BtnKeluar.addActionListener(evt->dispose());
        panelisi1.add(BtnSimpan);
        panelisi1.add(BtnBatal);
        panelisi1.add(BtnHapus);
        panelisi1.add(BtnEdit);
        panelisi1.add(BtnKeluar);

        JPanel jPanel1=new JPanel(new BorderLayout(1,1));
        jPanel1.setOpaque(false);
        jPanel1.add(panelisi4,BorderLayout.PAGE_START);
        jPanel1.add(panelisi3,BorderLayout.CENTER);
        jPanel1.add(panelisi1,BorderLayout.PAGE_END);

        internalFrame1.add(Scroll,BorderLayout.CENTER);
        internalFrame1.add(jPanel1,BorderLayout.PAGE_END);
        setContentPane(internalFrame1);
    }

    private void KdKeyPressed(java.awt.event.KeyEvent evt){
        if((evt.getKeyCode()==KeyEvent.VK_ENTER)||(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN)){
            if(Nm.getText().trim().equals("")){
                Nm.setText(cariNama(Kd.getText().trim()));
            }
            Nm.requestFocus();
        }else{
            Valid.pindah(evt,TCari,Nm);
        }
    }

    private void BtnSimpanActionPerformed(){
        if(Kd.getText().trim().equals("")){
            Valid.textKosong(Kd,"Kode Tindakan");
        }else if(Nm.getText().trim().equals("")){
            Valid.textKosong(Nm,"Nama Tindakan/Paket");
        }else if(cariBaris(Kd.getText().trim())>-1){
            JOptionPane.showMessageDialog(null,"Maaf, Kode Tindakan "+Kd.getText().trim()+" sudah ada...!!!!");
            Kd.requestFocus();
        }else{
            semua.add(new String[]{Kd.getText().trim(),Nm.getText().trim()});
            simpan();
        }
    }

    private void BtnEditActionPerformed(){
        if(Kd.getText().trim().equals("")){
            Valid.textKosong(Kd,"Kode Tindakan");
        }else if(Nm.getText().trim().equals("")){
            Valid.textKosong(Nm,"Nama Tindakan/Paket");
        }else if(tbTindakan.getSelectedRow()>-1){
            int baris=cariBaris(tbTindakan.getValueAt(tbTindakan.getSelectedRow(),0).toString());
            int sama=cariBaris(Kd.getText().trim());
            if((sama>-1)&&(sama!=baris)){
                JOptionPane.showMessageDialog(null,"Maaf, Kode Tindakan "+Kd.getText().trim()+" sudah ada...!!!!");
                Kd.requestFocus();
            }else if(baris>-1){
                semua.set(baris,new String[]{Kd.getText().trim(),Nm.getText().trim()});
                simpan();
            }
        }else{
            JOptionPane.showMessageDialog(null,"Maaf, Silahkan pilih data yang mau diganti...!!!!");
        }
    }

    private void BtnHapusActionPerformed(){
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis...!!!!");
            Kd.requestFocus();
        }else if(cariBaris(Kd.getText().trim())==-1){
            JOptionPane.showMessageDialog(null,"Maaf, Gagal menghapus. Pilih dulu data yang mau dihapus.\nKlik data pada table untuk memilih...!!!!");
        }else if(JOptionPane.showConfirmDialog(null,"Eeiiiiiits, udah bener belum data yang mau dihapus..??","Konfirmasi",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            semua.remove(cariBaris(Kd.getText().trim()));
            simpan();
        }
    }

    // tulis ke file cache lalu tampilkan ulang
    private void simpan(){
        if(UI.simpanDaftarTindakan(semua)){
            diubah=true;
        }
        semua=UI.daftarTindakan();
        tampil();
        emptTeks();
    }

    private int cariBaris(String kode){
        for(int i=0;i<semua.size();i++){
            if(semua.get(i)[0].equalsIgnoreCase(kode)){
                return i;
            }
        }
        return -1;
    }

    public void tampil(){
        Valid.tabelKosong(tabMode);
        for(String[] tindakan:semua){
            if(tindakan[0].toLowerCase().contains(TCari.getText().trim().toLowerCase())||tindakan[1].toLowerCase().contains(TCari.getText().trim().toLowerCase())){
                tabMode.addRow(new Object[]{
                    tindakan[0],tindakan[1]
                });
            }
        }
        LCount.setText(""+tabMode.getRowCount());
    }

    public void emptTeks(){
        Kd.setText("");
        Nm.setText("");
        Kd.requestFocus();
    }

    private void getData(){
        if(tbTindakan.getSelectedRow()!=-1){
            Kd.setText(tbTindakan.getValueAt(tbTindakan.getSelectedRow(),0).toString());
            Nm.setText(tbTindakan.getValueAt(tbTindakan.getSelectedRow(),1).toString());
        }
    }

    // nama tindakan dari master lab/ranap berdasarkan 7 karakter pertama kode
    private String cariNama(String kode){
        String nama="";
        try {
            ps=koneksi.prepareStatement(
                "select jns_perawatan_lab.nm_perawatan from jns_perawatan_lab where substring(jns_perawatan_lab.kd_jenis_prw,1,7)=? "+
                "union select jns_perawatan_inap.nm_perawatan from jns_perawatan_inap where substring(jns_perawatan_inap.kd_jenis_prw,1,7)=? limit 1");
            try {
                ps.setString(1,kode);
                ps.setString(2,kode);
                rs=ps.executeQuery();
                if(rs.next()){
                    nama=rs.getString("nm_perawatan");
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
        return nama;
    }

    public void isCek(){
        BtnSimpan.setEnabled(akses.getkategori_pemasukan_lain());
        BtnBatal.setEnabled(akses.getkategori_pemasukan_lain());
        BtnEdit.setEnabled(akses.getkategori_pemasukan_lain());
        BtnHapus.setEnabled(akses.getkategori_pemasukan_lain());
    }

    // true jika ada data yang disimpan/diganti/dihapus
    public boolean isDiubah(){
        return diubah;
    }
}
