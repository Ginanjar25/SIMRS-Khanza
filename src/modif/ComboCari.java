package modif;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Combo pilihan seperti select2 : kotak tetap tampil seperti combo biasa, saat diklik (atau Spasi / panah bawah)
 * terbuka panel berisi kolom pencarian sendiri dan daftar pilihan yang tersaring sesuai ketikan
 * (tidak peka huruf besar/kecil, cocok di bagian mana saja). Panah + Enter / klik untuk memilih, Esc untuk menutup.
 * Contoh : new ComboCari(CmbDokter,kode->tampil()).isi(daftar,"Semua","");
 */
public class ComboCari {
    /** Dipanggil setelah pilihan berganti, berisi kode pilihan ("" = Semua). */
    public interface SaatDipilih {
        void dipilih(String kode);
    }

    private static class Pilihan {
        final String kode,nama;

        Pilihan(String kode,String nama){
            this.kode=kode;
            this.nama=nama;
        }

        @Override
        public String toString(){
            return nama;
        }
    }

    private static final int BARIS_TAMPIL=10;
    private static final Color WARNA_GARIS=new Color(170,170,170);
    private final JComboBox combo;
    private final SaatDipilih saatdipilih;
    private final List<Pilihan> semua=new ArrayList<>();
    private Pilihan terpilih=null;
    private final JPopupMenu panel=new JPopupMenu();
    private final JTextField TCari=new JTextField();
    private final DefaultListModel<Pilihan> tersaring=new DefaultListModel<>();
    private final JList<Pilihan> daftar=new JList<>(tersaring);
    private final JLabel LKosong=new JLabel("Tidak ditemukan");
    private final JScrollPane gulir=new JScrollPane(daftar);

    public ComboCari(JComboBox combo,SaatDipilih saatdipilih){
        this.combo=combo;
        this.saatdipilih=saatdipilih;
        combo.setEditable(false);
        susunPanel();
        gantiPembuka(combo);
        for(Component bagian:combo.getComponents()){
            gantiPembuka(bagian);
        }
        // keyboard : Spasi, Alt+Bawah, panah bawah membuka panel cari
        AbstractAction buka=new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e){
                buka();
            }
        };
        for(String aksi:new String[]{"togglePopup","spacePopup","selectNext","selectNext2","selectPrevious","selectPrevious2"}){
            combo.getActionMap().put(aksi,buka);
        }
    }

    // klik di combo dan tombol panahnya membuka panel cari, bukan daftar bawaan combo
    private void gantiPembuka(Component bagian){
        for(MouseListener lama:bagian.getMouseListeners()){
            if(lama.getClass().getName().startsWith("javax.swing.plaf")||lama.getClass().getName().contains("ComboPopup")){
                bagian.removeMouseListener(lama);
            }
        }
        bagian.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e){
                if(combo.isEnabled()&&SwingUtilities.isLeftMouseButton(e)){
                    if(panel.isVisible()){
                        panel.setVisible(false);
                    }else{
                        buka();
                    }
                }
            }
        });
    }

    private void susunPanel(){
        TCari.setFont(combo.getFont());
        TCari.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4,4,4,4),BorderFactory.createLineBorder(WARNA_GARIS)),
            BorderFactory.createEmptyBorder(3,5,3,5)));
        TCari.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e){saring();}
            @Override public void removeUpdate(DocumentEvent e){saring();}
            @Override public void changedUpdate(DocumentEvent e){saring();}
        });
        tombol("BAWAH",KeyEvent.VK_DOWN,()->geser(1));
        tombol("ATAS",KeyEvent.VK_UP,()->geser(-1));
        tombol("BAWAH_HALAMAN",KeyEvent.VK_PAGE_DOWN,()->geser(BARIS_TAMPIL));
        tombol("ATAS_HALAMAN",KeyEvent.VK_PAGE_UP,()->geser(-BARIS_TAMPIL));
        tombol("PILIH",KeyEvent.VK_ENTER,()->pilihSorotan());
        tombol("TUTUP",KeyEvent.VK_ESCAPE,()->tutup());

        daftar.setFont(combo.getFont());
        daftar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        daftar.setFocusable(false);
        daftar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e){
                int baris=daftar.locationToIndex(e.getPoint());
                if((baris>=0)&&daftar.getCellBounds(baris,baris).contains(e.getPoint())){
                    daftar.setSelectedIndex(baris);
                    pilihSorotan();
                }
            }
        });
        daftar.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e){
                int baris=daftar.locationToIndex(e.getPoint());
                if(baris>=0){
                    daftar.setSelectedIndex(baris);
                }
            }
        });
        LKosong.setFont(combo.getFont());
        LKosong.setForeground(Color.GRAY);
        LKosong.setBorder(BorderFactory.createEmptyBorder(4,8,6,8));

        gulir.setBorder(BorderFactory.createEmptyBorder());
        JPanel isi=new JPanel(new BorderLayout());
        isi.setBackground(Color.WHITE);
        isi.add(TCari,BorderLayout.NORTH);
        isi.add(gulir,BorderLayout.CENTER);
        isi.add(LKosong,BorderLayout.SOUTH);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(WARNA_GARIS));
        panel.setFocusable(true);
        panel.add(isi,BorderLayout.CENTER);
    }

    private void tombol(String nama,int kunci,Runnable aksi){
        TCari.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(kunci,0),nama);
        TCari.getActionMap().put(nama,new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e){
                aksi.run();
            }
        });
    }

    /**
     * Mengisi pilihan. labelsemua diisi (mis. "Semua") untuk pilihan pertama berkode "".
     * @param data kode → nama, urut sesuai tampilan
     * @param kodeterpilih kode yang dipilih, bila tidak ada maka pilihan pertama
     */
    public void isi(Map<String,String> data,String labelsemua,String kodeterpilih){
        semua.clear();
        if(labelsemua!=null){
            semua.add(new Pilihan("",labelsemua));
        }
        for(Map.Entry<String,String> baris:data.entrySet()){
            semua.add(new Pilihan(baris.getKey(),baris.getValue()));
        }
        terpilih=semua.isEmpty()?null:semua.get(0);
        for(Pilihan p:semua){
            if(p.kode.equals(kodeterpilih)){
                terpilih=p;
            }
        }
        combo.setModel(new DefaultComboBoxModel(semua.toArray()));
        combo.setSelectedItem(terpilih);
    }

    /** true selama panel cari terbuka (petugas sedang mengetik / memilih). */
    public boolean isTerbuka(){
        return panel.isVisible();
    }

    /** Kode pilihan saat ini, "" bila Semua / kosong. */
    public String kode(){
        return terpilih==null?"":terpilih.kode;
    }

    /** Memilih kode dari program (tanpa panel). beritahu=true menjalankan SaatDipilih. */
    public void pilih(String kode,boolean beritahu){
        for(Pilihan p:semua){
            if(p.kode.equals(kode)){
                pilih(p,beritahu);
                return;
            }
        }
    }

    private void pilih(Pilihan p,boolean beritahu){
        boolean berganti=(terpilih==null)||!terpilih.kode.equals(p.kode);
        terpilih=p;
        combo.setSelectedItem(p);
        if(berganti&&beritahu&&(saatdipilih!=null)){
            saatdipilih.dipilih(p.kode);
        }
    }

    private void buka(){
        if(!combo.isEnabled()||!combo.isShowing()||semua.isEmpty()){
            return;
        }
        TCari.setText("");
        saring();
        aturUkuran();
        daftar.setSelectedValue(terpilih,true);
        panel.show(combo,0,combo.getHeight());
        SwingUtilities.invokeLater(TCari::requestFocusInWindow);
    }

    // Ukuran diatur sekali saat panel dibuka : lebar mengikuti combo, tinggi mengikuti jumlah pilihan (maks. BARIS_TAMPIL).
    // Jangan pack() saat panel terbuka, JPopupMenu membuat ulang jendelanya sehingga fokus ketikan pindah.
    private void aturUkuran(){
        daftar.setVisibleRowCount(Math.min(BARIS_TAMPIL,Math.max(1,semua.size())));
        gulir.setVisible(true);
        LKosong.setVisible(false);
        panel.setPreferredSize(null);
        Dimension ukuran=panel.getPreferredSize();
        panel.setPreferredSize(new Dimension(Math.max(combo.getWidth(),220),ukuran.height));
    }

    private void tutup(){
        panel.setVisible(false);
        combo.requestFocusInWindow();
    }

    private void saring(){
        String cari=TCari.getText().trim().toLowerCase();
        tersaring.clear();
        for(Pilihan p:semua){
            if(cari.equals("")||p.nama.toLowerCase().contains(cari)){
                tersaring.addElement(p);
            }
        }
        LKosong.setVisible(tersaring.isEmpty());
        gulir.setVisible(!tersaring.isEmpty());
        if(!tersaring.isEmpty()){
            daftar.setSelectedIndex(0);
            daftar.ensureIndexIsVisible(0);
        }
        panel.revalidate();
        panel.repaint();
    }

    private void geser(int langkah){
        if(tersaring.isEmpty()){
            return;
        }
        int baris=Math.max(0,Math.min(tersaring.size()-1,daftar.getSelectedIndex()+langkah));
        daftar.setSelectedIndex(baris);
        daftar.ensureIndexIsVisible(baris);
    }

    private void pilihSorotan(){
        Pilihan p=daftar.getSelectedValue();
        if(p!=null){
            tutup();
            pilih(p,true);
        }
    }
}
