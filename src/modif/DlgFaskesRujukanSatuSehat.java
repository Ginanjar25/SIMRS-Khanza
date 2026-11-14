package modif;

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
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 * Jendela pilih faskes tujuan hasil GetFaskesRujukan, dibuka dari RujukanSatuSehatVClaim.pilihFaskes().
 * Dobel klik, Enter, atau tombol Pilih untuk memilih.
 */
public class DlgFaskesRujukanSatuSehat extends javax.swing.JDialog {
    private final List<RujukanSatuSehatVClaim.Faskes> daftarfaskes;
    private final DefaultTableModel tabMode;
    private final widget.Table tbFaskes=new widget.Table();
    private RujukanSatuSehatVClaim.Faskes terpilih;
    private int i=0;

    /**
     * @param daftarfaskes faskes tujuan dari GetFaskesRujukan
     * @param keterangan   ringkasan pencarian, ditampilkan di judul (diagnosa, spesialis, tanggal rencana, wilayah)
     */
    public DlgFaskesRujukanSatuSehat(List<RujukanSatuSehatVClaim.Faskes> daftarfaskes,String keterangan){
        super((java.awt.Frame)null,"Faskes Tujuan Rujukan Satu Sehat",true);
        this.daftarfaskes=daftarfaskes;

        tabMode=new DefaultTableModel(null,new Object[]{
            "Kode PPK","Nama PPK","Kelas","Strata","Kota/Kab","Alamat","Telp","Jarak (m)","Jml Rujuk","Kapasitas","%","Jadwal"}){
            @Override
            public boolean isCellEditable(int rowIndex,int colIndex){return false;}
        };
        tbFaskes.setModel(tabMode);
        tbFaskes.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbFaskes.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for(i=0;i<12;i++){
            TableColumn column=tbFaskes.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(70);
            }else if(i==1){
                column.setPreferredWidth(200);
            }else if(i==2){
                column.setPreferredWidth(40);
            }else if(i==3){
                column.setPreferredWidth(60);
            }else if(i==4){
                column.setPreferredWidth(110);
            }else if(i==5){
                column.setPreferredWidth(200);
            }else if(i==6){
                column.setPreferredWidth(90);
            }else if(i==7){
                column.setPreferredWidth(60);
            }else if(i==8){
                column.setPreferredWidth(60);
            }else if(i==9){
                column.setPreferredWidth(60);
            }else if(i==10){
                column.setPreferredWidth(30);
            }else if(i==11){
                column.setPreferredWidth(250);
            }
        }
        tbFaskes.setDefaultRenderer(Object.class,new WarnaTable());

        for(RujukanSatuSehatVClaim.Faskes faskes:daftarfaskes){
            tabMode.addRow(new Object[]{
                faskes.kdppk,faskes.nmppk,faskes.kelas,faskes.strata,faskes.nmkc,faskes.alamat,faskes.telp,
                Math.round(faskes.jarak),faskes.jmlrujuk,faskes.kapasitas,faskes.persentase,faskes.jadwal
            });
        }
        tbFaskes.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                if(e.getClickCount()==2){
                    BtnPilihActionPerformed();
                }
            }
        });
        tbFaskes.addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e){
                if(e.getKeyCode()==KeyEvent.VK_ENTER){
                    e.consume();
                    BtnPilihActionPerformed();
                }
            }
        });

        initComponents(keterangan);
    }

    private void initComponents(String keterangan){
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        widget.InternalFrame internalFrame1=new widget.InternalFrame();
        internalFrame1.setBorder(DlgKriteriaRujukanSatuSehat.judul("::[ Faskes Tujuan Rujukan Satu Sehat ]::"));
        internalFrame1.setLayout(new BorderLayout(1,1));

        widget.ScrollPane Scroll=new widget.ScrollPane();
        Scroll.setOpaque(true);
        Scroll.setBorder(DlgKriteriaRujukanSatuSehat.judul("::[ "+keterangan+" ]::"));
        Scroll.setViewportView(tbFaskes);

        widget.panelisi panelGlass8=new widget.panelisi();
        panelGlass8.setPreferredSize(new Dimension(44,44));
        panelGlass8.setLayout(new FlowLayout(FlowLayout.LEFT,5,9));
        widget.Button BtnPilih=DlgKriteriaRujukanSatuSehat.tombol("Pilih","accept.png",'P',100);
        BtnPilih.addActionListener(evt->BtnPilihActionPerformed());
        widget.Button BtnKeluar=DlgKriteriaRujukanSatuSehat.tombol("Keluar","exit.png",'K',100);
        BtnKeluar.addActionListener(evt->dispose());
        widget.Label LCount=new widget.Label();
        LCount.setText("Record : "+daftarfaskes.size());
        LCount.setPreferredSize(new Dimension(150,30));
        panelGlass8.add(BtnPilih);
        panelGlass8.add(BtnKeluar);
        panelGlass8.add(LCount);

        internalFrame1.add(Scroll,BorderLayout.CENTER);
        internalFrame1.add(panelGlass8,BorderLayout.PAGE_END);
        setContentPane(internalFrame1);
        setSize(900,420);
        setLocationRelativeTo(null);
    }

    private void BtnPilihActionPerformed(){
        if(tbFaskes.getSelectedRow()>-1){
            terpilih=daftarfaskes.get(tbFaskes.convertRowIndexToModel(tbFaskes.getSelectedRow()));
            dispose();
        }else{
            JOptionPane.showMessageDialog(null,"Silahkan pilih faskes tujuan terlebih dahulu");
            tbFaskes.requestFocus();
        }
    }

    /** Faskes yang dipilih, null bila Keluar. */
    public RujukanSatuSehatVClaim.Faskes getTerpilih(){
        return terpilih;
    }
}
