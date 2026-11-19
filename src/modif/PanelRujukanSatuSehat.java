package modif;

import bridging.BPJSCekReferensiProsedur;
import fungsi.WarnaTable;
import fungsi.koneksiDB;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 * Blok isian rujukan Satu Sehat di BPJSRujukanKeluarSatuSehat (tanpa pop-up), berurutan:
 * 3. Data Satu Sehat (cek kartu, ID IHS pasien dan encounter),
 * 4. Kriteria Rujukan (pilih salah satu, kelompok layanan, wilayah jejaring),
 * 5. Faskes Tujuan (cari lalu klik baris untuk memilih, PPK di form ikut terisi).
 * Data dan request tetap lewat RujukanSatuSehatVClaim. Hanya aktif untuk tipe rujukan Penuh.
 */
public class PanelRujukanSatuSehat extends JPanel {
    /** Isian form yang dibaca panel dan tempat hasil pilihan faskes dikembalikan. */
    public interface Isian {
        String getNoRawat();
        String getNoSep();
        String getKdDiagnosa();
        String getKdPoli();
        /** Tanggal rencana kunjungan format yyyy-MM-dd. */
        String getTglRencana();
        void setFaskes(String kdppk,String nmppk);
    }

    private final RujukanSatuSehatVClaim rujukanss;
    private final Isian isian;
    private final DefaultTableModel tabModeKriteria,tabModeFaskes;
    private final widget.Table tbKriteria=new widget.Table(),tbFaskes=new widget.Table();
    private final widget.ComboBox CmbPropinsi=new widget.ComboBox(),CmbKabupaten=new widget.ComboBox(),CmbKelompokLayanan=new widget.ComboBox();
    private final widget.TextBox TNoKartu=info(),TIdPasien=info(),TEncounter=info(),TDiagnosaKriteria=info();
    private final widget.Label LStatusData=new widget.Label(),LKeteranganFaskes=new widget.Label();
    private final widget.PanelBiasa FormPpkRujukan=new widget.PanelBiasa(),FormData=new widget.PanelBiasa(),FormKriteria=new widget.PanelBiasa(),
            FormFaskes=new widget.PanelBiasa(),FormFaskesAtas=new widget.PanelBiasa();
    private final widget.ScrollPane ScrollFaskes=new widget.ScrollPane();
    private final widget.Button BtnCekData=tombol("Cek Data","accept.png",'D',105),
            BtnAmbilKriteria=tombol("Ambil Kriteria","category.png",'A',125),
            BtnCariICD9=tombol("Cari ICD-9","Search-16x16.png",'I',110),
            BtnCariFaskes=tombol("Cari Faskes","Search-16x16.png",'F',115);
    private final List<RujukanSatuSehatVClaim.Kriteria> daftarkriteria;
    private List<RujukanSatuSehatVClaim.Faskes> daftarfaskes=new java.util.ArrayList<>();
    private boolean aktif=true,adatindakan=false;
    private int i=0;

    public PanelRujukanSatuSehat(RujukanSatuSehatVClaim rujukanss,Isian isian){
        this.rujukanss=rujukanss;
        this.isian=isian;
        this.daftarkriteria=rujukanss.getDaftarKriteria();

        tabModeKriteria=new DefaultTableModel(null,new Object[]{"P","Kriteria","Kode ICD-9","Nama Tindakan"}){
            @Override
            public boolean isCellEditable(int rowIndex,int colIndex){
                if(colIndex==0){
                    return true;
                }else if(colIndex==2){
                    return PanelRujukanSatuSehat.this.daftarkriteria.get(rowIndex).isText();
                }
                return false;
            }
            Class[] types=new Class[]{
                java.lang.Boolean.class,java.lang.Object.class,java.lang.Object.class,java.lang.Object.class
            };
            @Override
            public Class getColumnClass(int columnIndex){
                return types[columnIndex];
            }
            // kolom P seperti radio: aturan Satu Sehat hanya satu kriteria yang boleh dipilih
            @Override
            public void setValueAt(Object nilai,int rowIndex,int colIndex){
                super.setValueAt(nilai,rowIndex,colIndex);
                if((colIndex==0)&&Boolean.TRUE.equals(nilai)){
                    for(int baris=0;baris<getRowCount();baris++){
                        if((baris!=rowIndex)&&Boolean.TRUE.equals(getValueAt(baris,0))){
                            super.setValueAt(false,baris,0);
                        }
                    }
                }
                faskesBerubah();
            }
        };
        tbKriteria.setModel(tabModeKriteria);
        tbKriteria.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] lebarkriteria={20,200,90,400};
        for(i=0;i<lebarkriteria.length;i++){
            tbKriteria.getColumnModel().getColumn(i).setPreferredWidth(lebarkriteria[i]);
        }
        tbKriteria.setDefaultRenderer(Object.class,new WarnaTable());

        tabModeFaskes=new DefaultTableModel(null,new Object[]{
            "Kode PPK","Nama PPK","Kelas","Strata","Kota/Kab","Alamat","Telp","Jarak (m)","Jml Rujuk","Kapasitas","%","Jadwal"}){
            @Override
            public boolean isCellEditable(int rowIndex,int colIndex){return false;}
        };
        tbFaskes.setModel(tabModeFaskes);
        tbFaskes.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] lebarfaskes={70,200,40,60,110,200,90,60,60,60,35,250};
        for(i=0;i<lebarfaskes.length;i++){
            TableColumn column=tbFaskes.getColumnModel().getColumn(i);
            column.setPreferredWidth(lebarfaskes[i]);
        }
        tbFaskes.setDefaultRenderer(Object.class,new WarnaTable());
        tbFaskes.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                pilihFaskes();
            }
        });
        tbFaskes.addKeyListener(new KeyAdapter(){
            @Override
            public void keyReleased(KeyEvent e){
                if((e.getKeyCode()==KeyEvent.VK_ENTER)||(e.getKeyCode()==KeyEvent.VK_UP)||(e.getKeyCode()==KeyEvent.VK_DOWN)){
                    pilihFaskes();
                }
            }
        });

        CmbPropinsi.addActionListener(evt->{
            isiKabupaten();
            faskesBerubah();
        });
        CmbKabupaten.addActionListener(evt->faskesBerubah());
        CmbKelompokLayanan.addActionListener(evt->faskesBerubah());
        BtnCekData.setToolTipText("Alt+D, Cek no.kartu, ID pasien Satu Sehat dan encounter kunjungan");
        BtnCekData.addActionListener(evt->BtnCekDataActionPerformed());
        BtnAmbilKriteria.setToolTipText("Alt+A, Ambil kriteria rujukan dan kelompok layanan sesuai diagnosa");
        BtnAmbilKriteria.addActionListener(evt->BtnAmbilKriteriaActionPerformed());
        BtnCariICD9.setToolTipText("Alt+I, Cari kode ICD-9 untuk Tindakan Medis");
        BtnCariICD9.addActionListener(evt->BtnCariICD9ActionPerformed());
        BtnCariFaskes.setToolTipText("Alt+F, Cari faskes tujuan sesuai kriteria, lalu klik baris untuk memilih");
        BtnCariFaskes.addActionListener(evt->cariFaskes());

        initComponents();
        kosongkan();
    }

    private void initComponents(){
        setOpaque(false);
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

        // 3. Data Satu Sehat
        FormData.setLayout(null);
        FormData.setBorder(judul("::[ 3. Data Satu Sehat ]::"));
        // satu baris: data hasil cek, lalu tombol Cek Data dan status di kanannya
        FormData.setPreferredSize(new Dimension(860,56));
        FormData.setMaximumSize(new Dimension(Integer.MAX_VALUE,56));
        FormData.add(label("No.Kartu :",0,20,102));
        TNoKartu.setBounds(110,20,120,23);
        FormData.add(TNoKartu);
        FormData.add(label("ID Pasien SS :",232,20,80));
        TIdPasien.setBounds(315,20,110,23);
        FormData.add(TIdPasien);
        FormData.add(label("Encounter :",427,20,65));
        TEncounter.setBounds(495,20,250,23);
        FormData.add(TEncounter);
        BtnCekData.setBounds(750,19,105,26);
        FormData.add(BtnCekData);
        LStatusData.setBounds(860,20,300,23);
        LStatusData.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        FormData.add(LStatusData);

        // 4. Kriteria Rujukan
        FormKriteria.setLayout(new BorderLayout(1,1));
        FormKriteria.setBorder(judul("::[ 4. Kriteria Rujukan, Pilih Salah Satu ]::"));
        FormKriteria.setPreferredSize(new Dimension(830,215));
        FormKriteria.setMaximumSize(new Dimension(Integer.MAX_VALUE,215));
        widget.PanelBiasa FormKriteriaAtas=new widget.PanelBiasa();
        FormKriteriaAtas.setLayout(null);
        FormKriteriaAtas.setPreferredSize(new Dimension(830,32));
        BtnAmbilKriteria.setBounds(5,2,125,26);
        FormKriteriaAtas.add(BtnAmbilKriteria);
        BtnCariICD9.setBounds(135,2,110,26);
        FormKriteriaAtas.add(BtnCariICD9);
        FormKriteriaAtas.add(label("Diagnosa :",250,4,60));
        TDiagnosaKriteria.setBounds(313,4,80,23);
        FormKriteriaAtas.add(TDiagnosaKriteria);
        widget.ScrollPane ScrollKriteria=new widget.ScrollPane();
        ScrollKriteria.setOpaque(true);
        ScrollKriteria.setViewportView(tbKriteria);
        widget.PanelBiasa FormKriteriaBawah=new widget.PanelBiasa();
        FormKriteriaBawah.setLayout(null);
        FormKriteriaBawah.setPreferredSize(new Dimension(830,60));
        FormKriteriaBawah.add(label("Kelompok Layanan :",0,3,110));
        CmbKelompokLayanan.setBounds(113,3,463,23);
        FormKriteriaBawah.add(CmbKelompokLayanan);
        FormKriteriaBawah.add(label("Wilayah Jejaring :",0,32,110));
        CmbPropinsi.setBounds(113,32,230,23);
        FormKriteriaBawah.add(CmbPropinsi);
        CmbKabupaten.setBounds(346,32,230,23);
        FormKriteriaBawah.add(CmbKabupaten);
        FormKriteria.add(FormKriteriaAtas,BorderLayout.PAGE_START);
        FormKriteria.add(ScrollKriteria,BorderLayout.CENTER);
        FormKriteria.add(FormKriteriaBawah,BorderLayout.PAGE_END);

        // 5. Faskes Tujuan
        FormFaskes.setLayout(new BorderLayout(1,1));
        FormFaskes.setBorder(judul("::[ 5. Faskes Tujuan, Klik Baris Untuk Memilih ]::"));
        FormFaskes.setPreferredSize(new Dimension(830,222));
        FormFaskes.setMaximumSize(new Dimension(Integer.MAX_VALUE,222));
        FormFaskesAtas.setLayout(null);
        FormFaskesAtas.setPreferredSize(new Dimension(830,32));
        BtnCariFaskes.setBounds(5,2,115,26);
        FormFaskesAtas.add(BtnCariFaskes);
        LKeteranganFaskes.setBounds(125,4,700,23);
        LKeteranganFaskes.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        FormFaskesAtas.add(LKeteranganFaskes);
        ScrollFaskes.setOpaque(true);
        ScrollFaskes.setViewportView(tbFaskes);
        // PPK Rujukan hasil pilihan faskes, komponennya dipasang form lewat getPanelPpkRujukan()
        FormPpkRujukan.setLayout(null);
        FormPpkRujukan.setPreferredSize(new Dimension(830,32));
        FormFaskes.add(FormFaskesAtas,BorderLayout.PAGE_START);
        FormFaskes.add(ScrollFaskes,BorderLayout.CENTER);
        FormFaskes.add(FormPpkRujukan,BorderLayout.PAGE_END);

        add(FormData);
        add(FormKriteria);
        add(FormFaskes);
    }

    /** Mengosongkan blok 3-5, dipanggil saat form dikosongkan atau baris data rujukan dipilih. */
    public final void kosongkan(){
        rujukanss.reset();
        TNoKartu.setText("");
        TIdPasien.setText("");
        TEncounter.setText("");
        TDiagnosaKriteria.setText("");
        LStatusData.setText(aktif?"Tekan Cek Data setelah pasien, SEP dan diagnosa terisi":"Hanya untuk tipe rujukan Penuh");
        tampilKriteria();
        kosongkanFaskes("");
        aturTombol();
    }

    /**
     * Baris paling bawah blok 5 untuk field PPK Rujukan form (layout null). PPK diisi otomatis dari tabel faskes
     * (tipe Penuh) atau lewat tombol cari faskes lama (tipe lain), jadi diletakkan setelah proses pengecekan.
     */
    public widget.PanelBiasa getPanelPpkRujukan(){
        return FormPpkRujukan;
    }

    /** Blok Satu Sehat hanya untuk tipe rujukan Penuh. */
    public void setAktif(boolean aktif){
        if(this.aktif!=aktif){
            this.aktif=aktif;
            kosongkan();
            tampilBlok();
        }
    }

    /**
     * Tipe Partial/Rujuk Balik: blok 3-4 dan pencarian faskes Satu Sehat disembunyikan agar tidak membingungkan,
     * tinggal baris PPK Rujukan (diisi lewat referensi faskes lama).
     */
    private void tampilBlok(){
        FormData.setVisible(aktif);
        FormKriteria.setVisible(aktif);
        FormFaskesAtas.setVisible(aktif);
        ScrollFaskes.setVisible(aktif);
        int tinggi=aktif?222:58;
        FormFaskes.setBorder(judul(aktif?"::[ 5. Faskes Tujuan, Klik Baris Untuk Memilih ]::":"::[ PPK Rujukan ]::"));
        FormFaskes.setPreferredSize(new Dimension(830,tinggi));
        FormFaskes.setMaximumSize(new Dimension(Integer.MAX_VALUE,tinggi));
        revalidate();
        repaint();
    }

    private void aturTombol(){
        boolean adadata=!TEncounter.getText().equals("");
        BtnCekData.setEnabled(aktif);
        BtnAmbilKriteria.setEnabled(aktif&&adadata);
        BtnCariICD9.setEnabled(aktif&&adatindakan);
        BtnCariFaskes.setEnabled(aktif&&!daftarkriteria.isEmpty());
        tbKriteria.setEnabled(aktif);
        CmbKelompokLayanan.setEnabled(aktif);
        CmbPropinsi.setEnabled(aktif);
        CmbKabupaten.setEnabled(aktif);
        tbFaskes.setEnabled(aktif);
    }

    private void BtnCekDataActionPerformed(){
        if(isian.getNoRawat().trim().equals("")||isian.getNoSep().trim().equals("")){
            JOptionPane.showMessageDialog(null,"Pasien dan No.SEP belum terisi");
            return;
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        boolean siap=rujukanss.siapkan(isian.getNoRawat(),isian.getNoSep());
        setCursor(Cursor.getDefaultCursor());
        TNoKartu.setText(siap?rujukanss.getNoKartu():"");
        TIdPasien.setText(siap?rujukanss.getIdPasien():"");
        TEncounter.setText(siap?rujukanss.getIdEncounter():"");
        if(!siap){
            LStatusData.setText("Data belum sesuai : "+rujukanss.getPesan().replace("\n"," "));
        }else if(isian.getKdDiagnosa().trim().equals("")){
            LStatusData.setText("Data sesuai, isi diagnosa lalu tekan Ambil Kriteria");
        }else{
            LStatusData.setText("Data sesuai");
        }
        LStatusData.setToolTipText(LStatusData.getText());
        tampilKriteria();
        kosongkanFaskes("");
        aturTombol();
        if(!siap){
            JOptionPane.showMessageDialog(null,rujukanss.getPesan());
        }else if(!isian.getKdDiagnosa().trim().equals("")){
            // data sesuai dan diagnosa sudah ada: langsung ambil kriteria, satu kali tekan
            BtnAmbilKriteriaActionPerformed();
        }
    }

    private void BtnAmbilKriteriaActionPerformed(){
        if(isian.getKdDiagnosa().trim().equals("")){
            JOptionPane.showMessageDialog(null,"Diagnosa rujukan belum diisi");
            return;
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        boolean sukses=rujukanss.ambilKriteria(isian.getKdDiagnosa());
        setCursor(Cursor.getDefaultCursor());
        TDiagnosaKriteria.setText(sukses?isian.getKdDiagnosa():"");
        tampilKriteria();
        kosongkanFaskes("");
        aturTombol();
        if(!sukses){
            JOptionPane.showMessageDialog(null,rujukanss.getPesan());
        }
    }

    /** Isi tabel kriteria, kelompok layanan dan wilayah dari data helper. */
    private void tampilKriteria(){
        if(tbKriteria.isEditing()){
            tbKriteria.getCellEditor().cancelCellEditing();
        }
        tabModeKriteria.setRowCount(0);
        adatindakan=false;
        for(RujukanSatuSehatVClaim.Kriteria kriteria:daftarkriteria){
            tabModeKriteria.addRow(new Object[]{
                kriteria.dipilih,kriteria.text,kriteria.isText()?kriteria.kodetindakan:"-",kriteria.isText()?kriteria.namatindakan:"-"
            });
            if(kriteria.isText()){
                adatindakan=true;
            }
        }
        CmbKelompokLayanan.removeAllItems();
        for(RujukanSatuSehatVClaim.KelompokLayanan kelompok:rujukanss.getDaftarKelompokLayanan()){
            CmbKelompokLayanan.addItem(kelompok);
        }
        CmbPropinsi.removeAllItems();
        for(RujukanSatuSehatVClaim.Wilayah propinsi:rujukanss.getDaftarWilayah()){
            CmbPropinsi.addItem(propinsi);
            if(propinsi.kodepropinsi.equals(koneksiDB.PROPINSISATUSEHAT())){
                CmbPropinsi.setSelectedItem(propinsi);
            }
        }
        isiKabupaten();
    }

    /** Pilihan kabupaten/kota sesuai provinsi terpilih (kode kabupaten diawali kode provinsi). */
    private void isiKabupaten(){
        CmbKabupaten.removeAllItems();
        RujukanSatuSehatVClaim.Wilayah propinsi=(RujukanSatuSehatVClaim.Wilayah)CmbPropinsi.getSelectedItem();
        if(propinsi==null){
            return;
        }
        // BPJS development 19-11-2026: kodeKabupaten wajib ("tidak boleh kosong"), jadi tanpa pilihan Semua Kabupaten/Kota.
        // Bawaan kabupaten RS (KABUPATENSATUSEHAT).
        for(RujukanSatuSehatVClaim.Wilayah kabupaten:rujukanss.getDaftarKabupaten()){
            if(kabupaten.kodekabupaten.startsWith(propinsi.kodepropinsi)){
                CmbKabupaten.addItem(kabupaten);
                if(kabupaten.kodekabupaten.equals(koneksiDB.KABUPATENSATUSEHAT())){
                    CmbKabupaten.setSelectedItem(kabupaten);
                }
            }
        }
    }

    /**
     * Cari kode ICD-9 lewat referensi prosedur VClaim untuk baris Tindakan Medis.
     * Dobel klik atau Enter memilih, kode dan nama tindakan terisi lalu kriteria otomatis tercentang.
     */
    private void BtnCariICD9ActionPerformed(){
        if(tbKriteria.isEditing()){
            tbKriteria.getCellEditor().stopCellEditing();
        }
        int baris=-1;
        for(i=0;i<daftarkriteria.size();i++){
            if(daftarkriteria.get(i).isText()){
                baris=i;
                break;
            }
        }
        if(baris<0){
            return;
        }
        final BPJSCekReferensiProsedur prosedur=new BPJSCekReferensiProsedur(null,true);
        final boolean[] terpilih={false};
        prosedur.getTable().addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                if(e.getClickCount()==2){
                    terpilih[0]=true;
                    prosedur.dispose();
                }
            }
        });
        prosedur.getTable().addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e){
                if(e.getKeyCode()==KeyEvent.VK_ENTER){
                    terpilih[0]=true;
                    prosedur.dispose();
                }
            }
        });
        prosedur.setSize(900,500);
        prosedur.setLocationRelativeTo(null);
        prosedur.setVisible(true);
        JTable hasil=prosedur.getTable();
        if(terpilih[0]&&(hasil.getSelectedRow()>-1)){
            tabModeKriteria.setValueAt(hasil.getValueAt(hasil.getSelectedRow(),1).toString(),baris,2);
            tabModeKriteria.setValueAt(hasil.getValueAt(hasil.getSelectedRow(),2).toString(),baris,3);
            tabModeKriteria.setValueAt(Boolean.TRUE,baris,0);
        }
    }

    /**
     * Validasi isian kriteria lalu cari faskes tujuan. Tanggal rencana kunjungan dibaca dari form saat tombol ditekan.
     * Dipanggil juga dari tombol PPK Rujukan di form.
     */
    public void cariFaskes(){
        if(!aktif){
            return;
        }
        if(tbKriteria.isEditing()){
            tbKriteria.getCellEditor().stopCellEditing();
        }
        if(TEncounter.getText().equals("")){
            JOptionPane.showMessageDialog(null,"Tekan Cek Data di blok 3. Data Satu Sehat terlebih dahulu");
            return;
        }
        if(daftarkriteria.isEmpty()){
            JOptionPane.showMessageDialog(null,"Tekan Ambil Kriteria di blok 4. Kriteria Rujukan terlebih dahulu");
            return;
        }
        if(!TDiagnosaKriteria.getText().equals(isian.getKdDiagnosa())){
            JOptionPane.showMessageDialog(null,"Diagnosa berubah, tekan Ambil Kriteria lagi");
            return;
        }
        if(isian.getKdPoli().trim().equals("")){
            JOptionPane.showMessageDialog(null,"Poli tujuan belum diisi");
            return;
        }
        String kurang="";
        int jumlahdipilih=0;
        for(i=0;i<daftarkriteria.size();i++){
            if(Boolean.TRUE.equals(tabModeKriteria.getValueAt(i,0))){
                jumlahdipilih++;
                if(daftarkriteria.get(i).isText()&&tabModeKriteria.getValueAt(i,2).toString().trim().equals("")){
                    kurang=kurang+"- Kode ICD-9 untuk "+daftarkriteria.get(i).text+"\n";
                }
            }
        }
        if(jumlahdipilih!=1){
            JOptionPane.showMessageDialog(null,"Pilih salah satu kriteria rujukan");
        }else if(!kurang.equals("")){
            JOptionPane.showMessageDialog(null,"Isian berikut belum diisi :\n"+kurang);
        }else if(CmbKelompokLayanan.getSelectedItem()==null){
            JOptionPane.showMessageDialog(null,"Kelompok layanan belum dipilih");
        }else if(CmbPropinsi.getSelectedItem()==null){
            JOptionPane.showMessageDialog(null,"Wilayah jejaring belum dipilih");
        }else if((CmbKabupaten.getSelectedItem()==null)||((RujukanSatuSehatVClaim.Wilayah)CmbKabupaten.getSelectedItem()).kodekabupaten.equals("")){
            JOptionPane.showMessageDialog(null,"Kabupaten/kota wilayah jejaring belum dipilih");
        }else{
            for(i=0;i<daftarkriteria.size();i++){
                RujukanSatuSehatVClaim.Kriteria kriteria=daftarkriteria.get(i);
                kriteria.dipilih=Boolean.TRUE.equals(tabModeKriteria.getValueAt(i,0));
                if(kriteria.isText()){
                    kriteria.kodetindakan=tabModeKriteria.getValueAt(i,2).toString().trim();
                    kriteria.namatindakan=tabModeKriteria.getValueAt(i,3).toString().trim();
                }
            }
            RujukanSatuSehatVClaim.Wilayah propinsi=(RujukanSatuSehatVClaim.Wilayah)CmbPropinsi.getSelectedItem();
            RujukanSatuSehatVClaim.Wilayah wilayah=new RujukanSatuSehatVClaim.Wilayah();
            wilayah.kodepropinsi=propinsi.kodepropinsi;
            wilayah.namapropinsi=propinsi.namapropinsi;
            RujukanSatuSehatVClaim.Wilayah kabupaten=(RujukanSatuSehatVClaim.Wilayah)CmbKabupaten.getSelectedItem();
            if(kabupaten!=null){
                wilayah.kodekabupaten=kabupaten.kodekabupaten;
                wilayah.namakabupaten=kabupaten.namakabupaten;
            }
            rujukanss.setWilayah(wilayah);
            rujukanss.setKelompokLayanan((RujukanSatuSehatVClaim.KelompokLayanan)CmbKelompokLayanan.getSelectedItem());

            kosongkanFaskes("");
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            List<RujukanSatuSehatVClaim.Faskes> hasil=rujukanss.cariFaskes(isian.getKdDiagnosa(),isian.getKdPoli(),isian.getTglRencana());
            setCursor(Cursor.getDefaultCursor());
            daftarfaskes=hasil;
            LKeteranganFaskes.setText(daftarfaskes.isEmpty()?"":daftarfaskes.size()+" faskes, rencana kunjungan "+isian.getTglRencana()+". Klik baris untuk memilih");
            for(RujukanSatuSehatVClaim.Faskes faskes:daftarfaskes){
                tabModeFaskes.addRow(new Object[]{
                    faskes.kdppk,faskes.nmppk,faskes.kelas,faskes.strata,faskes.nmkc,faskes.alamat,faskes.telp,
                    Math.round(faskes.jarak),faskes.jmlrujuk,faskes.kapasitas,faskes.persentase,faskes.jadwal
                });
            }
            if(daftarfaskes.isEmpty()){
                JOptionPane.showMessageDialog(null,rujukanss.getPesan());
            }else{
                tbFaskes.requestFocus();
            }
        }
    }

    private void pilihFaskes(){
        if(aktif&&(tbFaskes.getSelectedRow()>-1)&&(tbFaskes.getSelectedRow()<daftarfaskes.size())){
            RujukanSatuSehatVClaim.Faskes faskes=daftarfaskes.get(tbFaskes.convertRowIndexToModel(tbFaskes.getSelectedRow()));
            rujukanss.setFaskesTujuan(faskes);
            isian.setFaskes(faskes.kdppk,faskes.nmppk);
            LKeteranganFaskes.setText("Terpilih : "+faskes.kdppk+" "+faskes.nmppk);
        }
    }

    /** Kriteria, kelompok layanan atau wilayah diubah: hasil faskes sebelumnya tidak berlaku lagi. */
    private void faskesBerubah(){
        if(!daftarfaskes.isEmpty()){
            kosongkanFaskes("Kriteria berubah, tekan Cari Faskes lagi");
        }
    }

    private void kosongkanFaskes(String keterangan){
        daftarfaskes=new java.util.ArrayList<>();
        tabModeFaskes.setRowCount(0);
        rujukanss.setFaskesTujuan(null);
        isian.setFaskes("","");
        LKeteranganFaskes.setText(keterangan);
    }

    static Border judul(String teks){
        return javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240,245,235)),teks,
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma",0,11),new java.awt.Color(50,50,50));
    }

    static widget.Button tombol(String teks,String ikon,char mnemonic,int w){
        widget.Button button=new widget.Button();
        button.setIcon(new javax.swing.ImageIcon(PanelRujukanSatuSehat.class.getResource("/picture/"+ikon)));
        button.setMnemonic(mnemonic);
        button.setText(teks);
        button.setToolTipText("Alt+"+mnemonic);
        button.setPreferredSize(new Dimension(w,30));
        return button;
    }

    private static widget.Label label(String teks,int x,int y,int w){
        widget.Label label=new widget.Label();
        label.setText(teks);
        label.setBounds(x,y,w,23);
        return label;
    }

    // textbox hanya baca untuk menampilkan informasi
    private static widget.TextBox info(){
        widget.TextBox textbox=new widget.TextBox();
        textbox.setEditable(false);
        textbox.setHighlighter(null);
        return textbox;
    }
}
