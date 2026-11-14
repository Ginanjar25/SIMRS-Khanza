package modif;

import bridging.BPJSCekReferensiProsedur;
import fungsi.WarnaTable;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 * Jendela kemampuan layanan / kriteria rujukan Satu Sehat dan wilayah jejaring, dibuka dari
 * RujukanSatuSehatVClaim.pilihKriteria(). Aturan Satu Sehat: hanya satu kriteria yang boleh dipilih
 * (Terapi, Tindakan Medis, atau Upaya Diagnosis), kolom P bekerja seperti radio. Tindakan Medis wajib kode ICD-9,
 * bisa diketik atau dicari lewat tombol Cari ICD-9 (referensi prosedur VClaim).
 */
public class DlgKriteriaRujukanSatuSehat extends javax.swing.JDialog {
    private final List<RujukanSatuSehatVClaim.Kriteria> daftarkriteria;
    private final List<RujukanSatuSehatVClaim.Wilayah> daftarpropinsi,daftarkabupaten;
    private final DefaultTableModel tabMode;
    private final widget.Table tbKriteria=new widget.Table();
    private final widget.ComboBox CmbPropinsi=new widget.ComboBox(),CmbKabupaten=new widget.ComboBox();
    private RujukanSatuSehatVClaim.Wilayah wilayah;
    private boolean dipilih=false;
    private int i=0;

    /**
     * @param daftarkriteria  kriteria dari GetKriteriaRujukan, isian dipilih/kode tindakan diubah langsung saat Pilih
     * @param daftarpropinsi  wilayah jejaring tingkat provinsi
     * @param daftarkabupaten wilayah jejaring tingkat kabupaten/kota, boleh kosong
     * @param wilayahawal     wilayah yang terakhir dipilih, null bila belum pernah
     * @param propinsiawal    kode provinsi bawaan bila wilayahawal null (PROPINSISATUSEHAT)
     */
    public DlgKriteriaRujukanSatuSehat(List<RujukanSatuSehatVClaim.Kriteria> daftarkriteria,List<RujukanSatuSehatVClaim.Wilayah> daftarpropinsi,
            List<RujukanSatuSehatVClaim.Wilayah> daftarkabupaten,RujukanSatuSehatVClaim.Wilayah wilayahawal,String propinsiawal,
            String nokartu,String idpasien,String idencounter,String kddiagnosa){
        super((java.awt.Frame)null,"Kriteria Rujukan Satu Sehat",true);
        this.daftarkriteria=daftarkriteria;
        this.daftarpropinsi=daftarpropinsi;
        this.daftarkabupaten=daftarkabupaten;

        tabMode=new DefaultTableModel(null,new Object[]{"P","Kriteria","Kode ICD-9","Nama Tindakan"}){
            @Override
            public boolean isCellEditable(int rowIndex,int colIndex){
                if(colIndex==0){
                    return true;
                }else if(colIndex==2){
                    return DlgKriteriaRujukanSatuSehat.this.daftarkriteria.get(rowIndex).isText();
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
            // kolom P seperti radio: mencentang satu kriteria melepas centang kriteria lain
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
            }
        };
        tbKriteria.setModel(tabMode);
        tbKriteria.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbKriteria.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for(i=0;i<4;i++){
            TableColumn column=tbKriteria.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(20);
            }else if(i==1){
                column.setPreferredWidth(200);
            }else if(i==2){
                column.setPreferredWidth(90);
            }else if(i==3){
                column.setPreferredWidth(360);
            }
        }
        tbKriteria.setDefaultRenderer(Object.class,new WarnaTable());

        for(RujukanSatuSehatVClaim.Kriteria kriteria:daftarkriteria){
            tabMode.addRow(new Object[]{
                kriteria.dipilih,kriteria.text,kriteria.isText()?kriteria.kodetindakan:"-",kriteria.isText()?kriteria.namatindakan:"-"
            });
        }

        String kodepropinsi=wilayahawal!=null?wilayahawal.kodepropinsi:propinsiawal;
        for(RujukanSatuSehatVClaim.Wilayah propinsi:daftarpropinsi){
            CmbPropinsi.addItem(propinsi);
            if(propinsi.kodepropinsi.equals(kodepropinsi)){
                CmbPropinsi.setSelectedItem(propinsi);
            }
        }
        isiKabupaten();
        if(wilayahawal!=null){
            for(i=0;i<CmbKabupaten.getItemCount();i++){
                if(((RujukanSatuSehatVClaim.Wilayah)CmbKabupaten.getItemAt(i)).kodekabupaten.equals(wilayahawal.kodekabupaten)){
                    CmbKabupaten.setSelectedIndex(i);
                }
            }
        }
        CmbPropinsi.addActionListener(evt->isiKabupaten());

        initComponents(nokartu,idpasien,idencounter,kddiagnosa);
    }

    private void initComponents(String nokartu,String idpasien,String idencounter,String kddiagnosa){
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        widget.InternalFrame internalFrame1=new widget.InternalFrame();
        internalFrame1.setBorder(judul("::[ Kriteria Rujukan Satu Sehat ]::"));
        internalFrame1.setLayout(new BorderLayout(1,1));

        widget.PanelBiasa FormInput=new widget.PanelBiasa();
        FormInput.setLayout(null);
        FormInput.setPreferredSize(new Dimension(700,73));
        FormInput.add(label("No.Kartu :",0,10,85));
        FormInput.add(info(nokartu,88,10,130));
        FormInput.add(label("ID Pasien Satu Sehat :",220,10,125));
        FormInput.add(info(idpasien,348,10,120));
        FormInput.add(label("Diagnosa :",470,10,65));
        FormInput.add(info(kddiagnosa,538,10,140));
        FormInput.add(label("Encounter :",0,40,85));
        FormInput.add(info(idencounter,88,40,380));

        widget.ScrollPane Scroll=new widget.ScrollPane();
        Scroll.setOpaque(true);
        Scroll.setBorder(judul("::[ Kemampuan Layanan / Kriteria Rujukan, Pilih Salah Satu ]::"));
        Scroll.setViewportView(tbKriteria);

        widget.PanelBiasa FormWilayah=new widget.PanelBiasa();
        FormWilayah.setLayout(null);
        FormWilayah.setPreferredSize(new Dimension(700,43));
        FormWilayah.add(label("Wilayah Jejaring :",0,10,100));
        CmbPropinsi.setBounds(103,10,230,23);
        FormWilayah.add(CmbPropinsi);
        if(!daftarkabupaten.isEmpty()){
            CmbKabupaten.setBounds(336,10,230,23);
            FormWilayah.add(CmbKabupaten);
        }

        JPanel PanelTengah=new JPanel(new BorderLayout(1,1));
        PanelTengah.setOpaque(false);
        PanelTengah.add(Scroll,BorderLayout.CENTER);
        PanelTengah.add(FormWilayah,BorderLayout.PAGE_END);

        widget.panelisi panelGlass8=new widget.panelisi();
        panelGlass8.setPreferredSize(new Dimension(44,44));
        panelGlass8.setLayout(new FlowLayout(FlowLayout.LEFT,5,9));
        widget.Button BtnPilih=tombol("Pilih","accept.png",'P',100);
        BtnPilih.addActionListener(evt->BtnPilihActionPerformed());
        widget.Button BtnCariICD9=tombol("Cari ICD-9","Search-16x16.png",'C',120);
        BtnCariICD9.setToolTipText("Alt+C, Cari kode ICD-9 untuk Tindakan Medis");
        BtnCariICD9.addActionListener(evt->BtnCariICD9ActionPerformed());
        widget.Button BtnKeluar=tombol("Keluar","exit.png",'K',100);
        BtnKeluar.addActionListener(evt->dispose());
        panelGlass8.add(BtnPilih);
        for(RujukanSatuSehatVClaim.Kriteria kriteria:daftarkriteria){
            if(kriteria.isText()){
                panelGlass8.add(BtnCariICD9);
                break;
            }
        }
        panelGlass8.add(BtnKeluar);

        internalFrame1.add(FormInput,BorderLayout.PAGE_START);
        internalFrame1.add(PanelTengah,BorderLayout.CENTER);
        internalFrame1.add(panelGlass8,BorderLayout.PAGE_END);
        setContentPane(internalFrame1);
        setSize(720,370);
        setLocationRelativeTo(null);
    }

    /** Pilihan kabupaten/kota sesuai provinsi terpilih (kode kabupaten diawali kode provinsi). */
    private void isiKabupaten(){
        CmbKabupaten.removeAllItems();
        CmbKabupaten.addItem(new RujukanSatuSehatVClaim.Wilayah());
        RujukanSatuSehatVClaim.Wilayah propinsi=(RujukanSatuSehatVClaim.Wilayah)CmbPropinsi.getSelectedItem();
        if(propinsi==null){
            return;
        }
        for(RujukanSatuSehatVClaim.Wilayah kabupaten:daftarkabupaten){
            if(kabupaten.kodekabupaten.startsWith(propinsi.kodepropinsi)){
                CmbKabupaten.addItem(kabupaten);
            }
        }
    }

    private void BtnPilihActionPerformed(){
        if(tbKriteria.isEditing()){
            tbKriteria.getCellEditor().stopCellEditing();
        }
        String kurang="";
        int jumlahdipilih=0;
        for(i=0;i<daftarkriteria.size();i++){
            if(Boolean.TRUE.equals(tabMode.getValueAt(i,0))){
                jumlahdipilih++;
                if(daftarkriteria.get(i).isText()&&tabMode.getValueAt(i,2).toString().trim().equals("")){
                    kurang=kurang+"- Kode ICD-9 untuk "+daftarkriteria.get(i).text+"\n";
                }
            }
        }
        if(jumlahdipilih!=1){
            JOptionPane.showMessageDialog(null,"Pilih salah satu kriteria rujukan");
        }else if(!kurang.equals("")){
            JOptionPane.showMessageDialog(null,"Isian berikut belum diisi :\n"+kurang);
        }else if(CmbPropinsi.getSelectedItem()==null){
            JOptionPane.showMessageDialog(null,"Wilayah jejaring belum dipilih");
            CmbPropinsi.requestFocus();
        }else{
            for(i=0;i<daftarkriteria.size();i++){
                RujukanSatuSehatVClaim.Kriteria kriteria=daftarkriteria.get(i);
                kriteria.dipilih=Boolean.TRUE.equals(tabMode.getValueAt(i,0));
                if(kriteria.isText()){
                    kriteria.kodetindakan=tabMode.getValueAt(i,2).toString().trim();
                    kriteria.namatindakan=tabMode.getValueAt(i,3).toString().trim();
                }
            }
            RujukanSatuSehatVClaim.Wilayah propinsi=(RujukanSatuSehatVClaim.Wilayah)CmbPropinsi.getSelectedItem();
            wilayah=new RujukanSatuSehatVClaim.Wilayah();
            wilayah.kodepropinsi=propinsi.kodepropinsi;
            wilayah.namapropinsi=propinsi.namapropinsi;
            RujukanSatuSehatVClaim.Wilayah kabupaten=(RujukanSatuSehatVClaim.Wilayah)CmbKabupaten.getSelectedItem();
            if(kabupaten!=null){
                wilayah.kodekabupaten=kabupaten.kodekabupaten;
                wilayah.namakabupaten=kabupaten.namakabupaten;
            }
            dipilih=true;
            dispose();
        }
    }

    /**
     * Cari kode ICD-9 lewat referensi prosedur VClaim untuk baris Tindakan Medis
     * (baris terpilih bila bertipe tindakan, selain itu baris tindakan pertama).
     * Dobel klik atau Enter memilih, kode dan nama tindakan terisi lalu kriteria otomatis tercentang.
     */
    private void BtnCariICD9ActionPerformed(){
        if(tbKriteria.isEditing()){
            tbKriteria.getCellEditor().stopCellEditing();
        }
        int baris=tbKriteria.getSelectedRow();
        if((baris<0)||!daftarkriteria.get(baris).isText()){
            baris=-1;
            for(i=0;i<daftarkriteria.size();i++){
                if(daftarkriteria.get(i).isText()){
                    baris=i;
                    break;
                }
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
        prosedur.setSize(getWidth()+100,getHeight()+100);
        prosedur.setLocationRelativeTo(this);
        prosedur.setVisible(true);
        JTable hasil=prosedur.getTable();
        if(terpilih[0]&&(hasil.getSelectedRow()>-1)){
            tabMode.setValueAt(hasil.getValueAt(hasil.getSelectedRow(),1).toString(),baris,2);
            tabMode.setValueAt(hasil.getValueAt(hasil.getSelectedRow(),2).toString(),baris,3);
            tabMode.setValueAt(Boolean.TRUE,baris,0);
        }
    }

    /** true bila petugas menekan Pilih dan isian lengkap. */
    public boolean isDipilih(){
        return dipilih;
    }

    /** Wilayah jejaring terpilih (provinsi, kabupaten/kota opsional). */
    public RujukanSatuSehatVClaim.Wilayah getWilayah(){
        return wilayah;
    }

    static Border judul(String teks){
        return javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240,245,235)),teks,
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma",0,11),new java.awt.Color(50,50,50));
    }

    static widget.Button tombol(String teks,String ikon,char mnemonic,int w){
        widget.Button button=new widget.Button();
        button.setIcon(new javax.swing.ImageIcon(DlgKriteriaRujukanSatuSehat.class.getResource("/picture/"+ikon)));
        button.setMnemonic(mnemonic);
        button.setText(teks);
        button.setToolTipText("Alt+"+mnemonic);
        button.setPreferredSize(new Dimension(w,30));
        return button;
    }

    private widget.Label label(String teks,int x,int y,int w){
        widget.Label label=new widget.Label();
        label.setText(teks);
        label.setBounds(x,y,w,23);
        return label;
    }

    // textbox hanya baca untuk menampilkan informasi
    private widget.TextBox info(String teks,int x,int y,int w){
        widget.TextBox textbox=new widget.TextBox();
        textbox.setText(teks);
        textbox.setEditable(false);
        textbox.setHighlighter(null);
        textbox.setBounds(x,y,w,23);
        return textbox;
    }
}
