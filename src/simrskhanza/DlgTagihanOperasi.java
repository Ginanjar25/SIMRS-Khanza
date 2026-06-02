package simrskhanza;

import kepegawaian.DlgCariDokter;
import kepegawaian.DlgCariPetugas;
import keuangan.DlgJnsPerawatanOperasi;
import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import fungsi.akuntindakanoperasi;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;import ava.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.ArrayList;
import java.util.Hashimport java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;import avax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import keuangan.Jurnal;
import rekammedis.MasterCariTemplateLaporanOperasi;

public class DlgTagihanOperasi extends javax.swing.JDialog {
    private final DefaultTableModel tabMode,tabMode2;
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private final ExecutorService executor =  Executors.newSingleThreadExecutor();
    private volatile bool e an ceksukses = false;
    private Jurnal jur=new   Jurnal();
    private Connection koneksi=koneksiDB.condb();
    private PreparedStatement pstindakan,psobat,psset_tarif;
    private ResultSet  r sobat,rstindakan,rsset_tarif, rs2;
    private DlgCariPetugas pet u gas;    priate DlgCariDokter dokter;
    private String kelas_operasi="Yes",ke las="",cara_ bayar_operas i="Yes",kd_p j="",st atus=""; 
    private double ttljmd okter=0,ttlj mpetugas=0,ttlpendapatan=0,ttlbhp=0;
    private String norawatibu="";   
    private double y=0,biayatind a kan=0,biayaobat=0; 
    private int jml=0,i=0,index=0;       private boolean[] pilih; 
    private boolean sukses=true;  
    private String[] kode_pak et, nm_pera watan,kategori,kd_obat,nm_obat, satuan;
    private double[] operator1, operator2, operator3, asisten_operator1, asisten_operator2,asisten_operator3,dokter_pjanak,dokter_umum,
                  instrumen, dokt                 perawat_luar, sewa_ok, alat,akomodasi,bagian_rs,omloop,omloop2,omloop3,omloop4,omloop5,sarpras,ttltindakan,jmlobat,hargasatuan,ttlobat;
              
           
    /** Creates new form DlgP r ogramStudi
     * @param parent        
     * @param modal * /      
    public DlgTagihanOperasi
        super(parent, moda l );
        initComponents();   
 
             
            ow={"P","Kode Paket","Nama Operasi","Kategori","Operator 1","Operator 2","Oper ator 3",
             
                "Asisten Op 1","Asisten  Op 2","Asi sten Op 3" ,"Instr umen","d r Anak", "Perawat  Resus", "dr Anas
            tesi",   

                      "Sarpras","dr Pj Anak","dr Umum","Total"};
        tabMode=new DefaultTableModel(null,row){
            @Override public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = true;
                if ((colIndex==1)||(colIndex==2)||(colIndex==3)||(colIndex==29)) {
                    a=false;
                }
                return a;
             }
             Class[] types = new Class[] {
                 java.lang.Boolean.class, java.lang.Object.class,java.lang.Object.class,java.lang.Object.class, java.lang.Double.class, 
                 java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, 
                 java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, 
                 java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, 
                 java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, 
                 java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, 
                 java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, 
                 java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        tbtindakan.setModel(tabMode);

        tbtindakan.setPreferredScrollableViewportSize(new Dimension(800,800));
        tbtindakan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 33; i++) {
            TableColumn column = tbtindakan.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(20);
            }else if(i==1){
                column.setPreferredWidth(100);
            }else if(i==2){
                column.setPreferredWidth(250);
            }else if(i==3){
                column.setPreferredWidth(100);
            }else{
                column.setPreferredWidth(85);
            }
        }
        tbtindakan.setDefaultRenderer(Object.class, new WarnaTable());
        
        //tagihan obat
        Object[] row2={"Jumlah",
        "Kode",
        "Nama",
        "Satuan",
        "Harga",
        "Total"};
        
        tabMode2=new DefaultTableModel(null,row2){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = false;
                if ((colIndex==0)||(colIndex==4)) {
                    a=true;
                }
                return a;
             }
             Class[] types = new Class[] {
                java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, 
                java.lang.Object.class, java.lang.Object.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };

        tbObat.setModel(tabMode2);
        //tampil();

        //tbBangsal.setDefaultRenderer(Object.class, new WarnaTable(jPanel2.getBackground(),tbBangsal.getBackground()));
        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 6; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(50);
            }else if(i==1){
                column.setPreferredWidth(80);
            }else if(i==2){
                column.setPreferredWidth(150);
            }else if(i==3){
                column.setPreferredWidth(70);
            }else if(i==4){
                column.setPreferredWidth(90);
            }else if(i==5){
                column.setPreferredWidth(90);
            }
        }

        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRw.setDocument(new batasInput((byte)17).getKata(TNoRw));
        jenis.setDocument(new batasInput((byte)8).getKata(jenis));
        kdoperator1.setDocument(new batasInput((byte)20).getKata(kdoperator1));
        kdoperator2.setDocument(new batasInput((byte)20).getKata(kdoperator2));
        kdoperator3.setDocument(new batasInput((byte)20).getKata(kdoperator3));
        kdasistoperator1.setDocument(new batasInput((byte)20).getKata(kdasistoperator1));
        kdasistoperator2.setDocument(new batasInput((byte)20).getKata(kdasistoperator2));
        kdasistoperator3.setDocument(new batasInput((byte)20).getKata(kdasistoperator3));
        kdInstrumen.setDocument(new batasInput((byte)20).getKata(kdInstrumen));
        kdanestesi.setDocument(new batasInput((byte)20).getKata(kdanestesi));
        kdasistanestesi.setDocument(new batasInput((byte)20).getKata(kdasistanestesi));
        kdasistanestesi2.setDocument(new batasInput((byte)20).getKata(kdasistanestesi2));
        kddranak.setDocument(new batasInput((byte)20).getKata(kddranak));
        kdprwresust.setDocument(new batasInput((byte)20).getKata(kdprwresust));
        kdbidan.setDocument(new batasInput((byte)20).getKata(kdbidan));
        kdbidan2.setDocument(new batasInput((byte)20).getKata(kdbidan2));
        kdbidan3.setDocument(new batasInput((byte)20).getKata(kdbidan3));
        kdprwluar.setDocument(new batasInput((byte)20).getKata(kdprwluar));
        kdonloop1.setDocument(new batasInput((byte)20).getKata(kdonloop1));
        kdonloop2.setDocument(new batasInput((byte)20).getKata(kdonloop2));
        kdonloop3.setDocument(new batasInput((byte)20).getKata(kdonloop3));
        kdonloop4.setDocument(new batasInput((byte)20).getKata(kdonloop4));        
        kdonloop5.setDocument(new batasInput((byte)20).getKata(kdonloop5));
        kdpjanak.setDocument(new batasInput((byte)20).getKata(kdpjanak));        
        kddrumum.setDocument(new batasInput((byte)20).getKata(kddrumum));      
//        PreOp.setDocument(new batasInput((int)100).getKata(PreOp));      
//        PostOp.setDocument(new batasInput((int)100).getKata(PostOp));    
        NomorImplant.setDocument(new batasInput((int)50).getKata(NomorImplant));      
//        Jaringan.setDocument(new batasInput((int)100).getKata(Jaringan));
//        Laporan.setDocument(new batasInput((int)8000).getKata(Laporan));
        
        TCariPaket.setDocument(new batasInput((byte)100).getKata(TCari)); 
        TCari.setDocument(new batasInput((byte)100).getKata(TCari)); 
        if(koneksiDB.CARICEPAT().equals("aktif")){
            TCariPaket.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if(TCariPaket.getText().length()>2){
                        tampil();
                    }
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    if(TCariPaket.getText().length()>2){
                        tampil();
                    }
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    if(TCariPaket.getText().length()>2){
                        tampil();
                    }
                }
            });
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil2();
                    }
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil2();
                    }
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil2();
                    }
                }
            });
        }  
        
        dokter.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(dokter.getTable().getSelectedRow()!= -1){                    
                    if(pilihan==1){
                        kdoperator1.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),0).toString());
                        nmoperator1.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),1).toString());
                        kdoperator1.requestFocus();
                    }else if(pilihan==2){
                        kdoperator2.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),0).toString());
                        nmoperator2.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),1).toString());
                        kdoperator2.requestFocus();
                    }else if(pilihan==3){
                        kdoperator3.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),0).toString());
                        nmoperator3.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),1).toString());
                        kdoperator3.requestFocus();
                    }else if(pilihan==4){
                        kdanestesi.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),0).toString());
                        nmanestesi.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),1).toString());
                        kdanestesi.requestFocus();
                    }else if(pilihan==5){
                        kddranak.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),0).toString());
                        nmdranak.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),1).toString());
                        kddranak.requestFocus();
                    }else if(pilihan==6){
                        kdpjanak.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),0).toString());
                        nmpjanak.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),1).toString());
                        kdpjanak.requestFocus();
                    }else if(pilihan==7){
                        kddrumum.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),0).toString());
                        nmdrumum.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),1).toString());
                        kddrumum.requestFocus();
                    }                   
                }                
            }
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
        
        petugas.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(petugas.getTable().getSelectedRow()!= -1){    
                    if(pilihan==1){
                        kdasistoperator1.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                        nmasistoperator1.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                        kdasistoperator1.requestFocus();
                    }else if(pilihan==2){
                        kdasistoperator2.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                        nmasistoperator2.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                        kdasistoperator2.requestFocus();
                    }else if(pilihan==3){
                        kdInstrumen.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                        nminstrumen.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                        kdInstrumen.requestFocus();
                    }else if(pilihan==4){
                        kdasistanestesi.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                        nmasistanestesi.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                        kdasistanestesi.requestFocus();
                    }else if(pilihan==5){
                        kdprwresust.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                        nmprwresust.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                        kdprwresust.requestFocus();
                    }else if(pilihan==6){
                        kdprwluar.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                        nmprwluar.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                        kdprwluar.requestFocus();
                    }else if(pilihan==7){
                        kdbidan.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                        nmbidan.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                        kdbidan.requestFocus();
                    }else if(pilihan==8){
                        kdbidan2.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                        nmbidan2.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                        kdbidan2.requestFocus();
                    }else if(pilihan==9){
                        kdbidan3.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                        nmbidan3.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                        kdbidan3.requestFocus();
                    }else if(pilihan==10){
                        kdonloop1.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                        nmonloop1.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                        kdonloop1.requestFocus();
                    }else if(pilihan==11){
                        kdonloop2.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                        nmonloop2.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                        kdonloop2.requestFocus();
                    }else if(pilihan==12){
                        kdonloop3.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                        nmonloop3.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                        kdonloop3.requestFocus();
                    }else if(pilihan==13){
                        kdonloop4.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                        nmonloop4.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                        kdonloop4.requestFocus();
                    }else if(pilihan==14){
                        kdonloop5.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                        nmonloop5.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                        kdonloop5.requestFocus();
                    }else if(pilihan==15){
                        kdasistoperator3.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                        nmasistoperator3.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                        kdasistoperator3.requestFocus();
                    }else if(pilihan==16){
                        kdasistanestesi2.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                        nmasistanestesi2.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                        kdasistanestesi2.requestFocus();
                    }               
                }            
            }
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
        
        template.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(template.getTable().getSelectedRow()!= -1){  
//                    PreOp.setText(template.getTable().getValueAt(template.getTable().getSelectedRow(),2).toString());
//                    PostOp.setText(template.getTable().getValueAt(template.getTable().getSelectedRow(),3).toString());
//                    Jaringan.setText(template.getTable().getValueAt(template.getTable().getSelectedRow(),4).toString());
//                    DikirimPA.setSelectedItem(template.getTable().getValueAt(template.getTable().getSelectedRow(),5).toString());
//                    Laporan.setText(template.getTable().getValueAt(template.getTable().getSelectedRow(),6).toString());
//                    Laporan.requestFocus();
                }            
            }
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
        
        TCari.requestFocus();
        ChkInput.setSelected(false);
        isForm();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Kd2 = new widget.TextBox();
        Popup = new javax.swing.JPopupMenu();
        ppBersihkan = new javax.swing.JMenuItem();
        internalFrame1 = new widget.InternalFrame();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        panelisi5 = new widget.panelisi();
        label10 = new widget.Label();
        TCariPaket = new widget.TextBox();
        BtnCari2 = new widget.Button();
     

        Scroll1 = new widget.ScrollPane();
        tbtindakan = new widget.Table();
        jPanel2 = new javax.swing.JPanel();
        panelisi4 = new widget.panelisi();
        label9 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari1 = new widget.Button();
        BtnAll = new widget.Button();
        BtnTambah = new widget.Button();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        panelisi1 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnEdit = new widget.Button();
        LTotal = new widget.Label();
        BtnCari = new widget.Button();
        BtnKeluar = new widget.Button();
        PanelInput = new javax.swing.JPanel();
        ChkInput = new widget.CekBox();
        scrollPane1 = new widget.ScrollPane();
        FormInput = new widget.panelisi();
        label14 = new widget.Label();
        kdoperator1 = new widget.TextBox();
        nmoperator1 = new widget.TextBox();
        BtnOperator1 = new widget.Button();
        label11 = new widget.Label();
        tgl = new widget.Tanggal();
        jLabel3 = new widget.Label();
        TNoRw = new widget.TextBox();
        TPasien = new widget.TextBox();
        jLabel4 = new widget.Label();
        jenis = new widget.TextBox();
        label17 = new widget.Label();
        kdasistoperator1 = new widget.TextBox();
        nmasistoperator1 = new widget.TextBox();
        btnAsis1 = new widget.Button();
        label19 = new widget.Label();
        kdoperator2 = new widget.TextBox();
        nmoperator2 = new widget.TextBox();
        BtnOperator2 = new widget.Button();
        label20 = new widget.Label();
        kdoperator3 = new widget.TextBox();
        nmoperator3 = new widget.TextBox();
        btnOperator3 = new widget.Button();
        label21 = new widget.Label();
        kdanestesi = new widget.TextBox();
        nmanestesi = new widget.TextBox();
        BtnAnastesi = new widget.Button();
        label22 = new widget.Label();
        kddranak = new widget.TextBox();
        nmdranak = new widget.TextBox();
        btnAnak = new widget.Button();
        btnAsis2 = new widget.Button();
        nmasistoperator2 = new widget.TextBox();
        kdasistoperator2 = new widget.TextBox();
        label18 = new widget.Label();
        btnAsis3 = new widget.Button();
        nminstrumen = new widget.TextBox();
        kdInstrumen = new widget.TextBox();
        label23 = new widget.Label();
        btnPrwRes = new widget.Button();
        nmprwresust = new widget.TextBox();
        kdprwresust = new widget.TextBox();
        label24 = new widget.Label();
        label26 = new widget.Label();
        kdasistanestesi = new widget.TextBox();
        nmasistanestesi = new widget.TextBox();
        BtnAsnes = new widget.Button();
        label27 = new widget.Label();
        kdbidan = new widget.TextBox();
        nmbidan = new widget.TextBox();
        btnBidan = new widget.Button();
        label28 = new widget.Label();
        kdprwluar = new widget.TextBox();
        nmprwluar = new widget.TextBox();
        btnPrwLuar = new widget.Button();
        jLabel5 = new widget.Label();
        Kategori = new widget.ComboBox();
        btnBidan2 = new widget.Button();
        nmbidan2 = new widget.TextBox();
        kdbidan2 = new widget.TextBox();
        label29 = new widget.Label();
        label30 = new widget.Label();
        kdbidan3 = new widget.TextBox();
        nmbidan3 = new widget.TextBox();
        btnBidan3 = new widget.Button();
        label25 = new widget.Label();
        kdonloop1 = new widget.TextBox();
        nmonloop1 = new widget.TextBox();
        btnOnloop1 = new widget.Button();
        btnOnloop2 = new widget.Button();
        nmonloop2 = new widget.TextBox();
        kdonloop2 = new widget.TextBox();
        label31 = new widget.Label();
        label32 = new widget.Label();
        btnOnloop3 = new widget.Button();
        nmonloop3 = new widget.TextBox();
        kdonloop3 = new widget.TextBox();
        label33 = new widget.Label();
        kdpjanak = new widget.TextBox();
        nmpjanak = new widget.TextBox();
        btndrpjanak = new widget.Button();
        label34 = new widget.Label();
        kddrumum = new widget.TextBox();
        nmdrumum = new widget.TextBox();
        btndrumum = new widget.Button();
        label35 = new widget.Label();
        kdasistoperator3 = new widget.TextBox();
        nmasistoperator3 = new widget.TextBox();
        btnAsis4 = new widget.Button();
        label36 = new widget.Label();
        kdasistanestesi2 = new widget.TextBox();
        nmasistanestesi2 = new widget.TextBox();
        BtnAsnes1 = new widget.Button();
        label37 = new widget.Label();
        kdonloop4 = new widget.TextBox();
        nmonloop4 = new widget.TextBox();
        btnOnloop4 = new widget.Button();
        btnOnloop5 = new widget.Button();
        nmonloop5 = new widget.TextBox();
        kdonloop5 = new widget.TextBox();
        label38 = new widget.Label();
        jLabel11 = new widget.Label();
        NomorImplant = new widget.TextBox();

        Kd2.setName("Kd2"); // NOI18N
        Kd2.setPreferredSize(new java.awt.Dimension(207, 23));

        Popup.setName("Popup"); // NOI18N

        ppBersihkan.setBackground(new java.awt.Color(255, 255, 254));
        ppBersihkan.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        ppBersihkan.setForeground(new java.awt.Color(50, 50, 50));
        ppBersihkan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        ppBersihkan.setText("Bersihkan Jumlah");
        ppBersihkan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppBersihkan.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppBersihkan.setName("ppBersihkan"); // NOI18N
        ppBersihkan.setPreferredSize(new java.awt.Dimension(200, 25));
        ppBersihkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppBersihkanActionPerformed(evt);
            }
        });
        Popup.add(ppBersihkan);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Tagihan Operasi ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setOpaque(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(816, 102));
        jPanel1.setLayout(new java.awt.GridLayout(1, 2));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)), " Tindakan ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(300, 102));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelisi5.setBorder(null);
        panelisi5.setName("panelisi5"); // NOI18N
        panelisi5.setPreferredSize(new java.awt.Dimension(100, 43));
        panelisi5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 9));

        label10.setText("Key Word :");
        label10.setName("label10"); // NOI18N
        label10.setPreferredSize(new java.awt.Dimension(68, 23));
        panelisi5.add(label10);

        TCariPaket.setToolTipText("Alt+C");
        TCariPaket.setName("TCariPaket"); // NOI18N
        TCariPaket.setPreferredSize(new java.awt.Dimension(215, 23));
        TCariPaket.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariPaketKeyPressed(evt);
            }
        });
        panelisi5.add(TCariPaket);

        BtnCari2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari2.setMnemonic('1');
        BtnCari2.setToolTipText("Alt+1");
        BtnCari2.setName("BtnCari2"); // NOI18N
        BtnCari2.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCari2ActionPerformed(evt);
            }
        });
        BtnCari2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCari2KeyPressed(evt);
            }
        });
        panelisi5.add(BtnCari2);

        BtnAll1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll1.setMnemonic('2');
        BtnAll1.setToolTipText("Alt+2");
        BtnAll1.setName("BtnAll1"); // NOI18N
        BtnAll1.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAll1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAll1ActionPerformed(evt);
            }
        });
        BtnAll1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnAll1KeyPressed(evt);
            }
        });
        panelisi5.add(BtnAll1);

        BtnTambahOperasi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/plus_16.png"))); // NOI18N
        BtnTambahOperasi.setMnemonic('3');
        BtnTambahOperasi.setToolTipText("Alt+3");
        BtnTambahOperasi.setName("BtnTambahOperasi"); // NOI18N
        BtnTambahOperasi.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnTambahOperasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnTambahOperasiActionPerformed(evt);
            }
        });
        panelisi5.add(BtnTambahOperasi);

        jPanel3.add(panelisi5, java.awt.BorderLayout.PAGE_END);

        Scroll1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 253)));
        Scroll1.setName("Scroll1"); // NOI18N
        Scroll1.setOpaque(true);

        tbtindakan.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbtindakan.setName("tbtindakan"); // NOI18N
        tbtindakan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbtindakanMouseClicked(evt);
            }
        });
        tbtindakan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbtindakanKeyPressed(evt);
            }
        });
        Scroll1.setViewportView(tbtindakan);

        jPanel3.add(Scroll1, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel3);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)), " Obat & BHP ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setOpaque(false);
        jPanel2.setPreferredSize(new java.awt.Dimension(350, 102));
        jPanel2.setLayout(new java.awt.BorderLayout(1, 1));

        panelisi4.setBorder(null);
        panelisi4.setName("panelisi4"); // NOI18N
        panelisi4.setPreferredSize(new java.awt.Dimension(100, 43));
        panelisi4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 9));

        label9.setText("Key Word :");
        label9.setName("label9"); // NOI18N
        label9.setPreferredSize(new java.awt.Dimension(68, 23));
        panelisi4.add(label9);

        TCari.setToolTipText("Alt+C");
        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(215, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelisi4.add(TCari);

        BtnCari1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari1.setMnemonic('1');
        BtnCari1.setToolTipText("Alt+1");
        BtnCari1.setName("BtnCari1"); // NOI18N
        BtnCari1.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCari1ActionPerformed(evt);
            }
        });
        BtnCari1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCari1KeyPressed(evt);
            }
        });
        panelisi4.add(BtnCari1);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('2');
        BtnAll.setToolTipText("Alt+2");
        BtnAll.setName("BtnAll"); // NOI18N
        BtnAll.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAllActionPerformed(evt);
            }
        });
        BtnAll.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnAllKeyPressed(evt);
            }
        });
        panelisi4.add(BtnAll);

        BtnTambah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/plus_16.png"))); // NOI18N
        BtnTambah.setMnemonic('3');
        BtnTambah.setToolTipText("Alt+3");
        BtnTambah.setName("BtnTambah"); // NOI18N
        BtnTambah.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnTambahActionPerformed(evt);
            }
        });
        panelisi4.add(BtnTambah);

        jPanel2.add(panelisi4, java.awt.BorderLayout.PAGE_END);

        Scroll.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 253)));
        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbObat.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbObat.setName("tbObat"); // NOI18N
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbObatMouseClicked(evt);
            }
        });
        tbObat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbObatKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbObat);

        jPanel2.add(Scroll, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel2);

        internalFrame1.add(jPanel1, java.awt.BorderLayout.CENTER);

        panelisi1.setName("panelisi1"); // NOI18N
        panelisi1.setPreferredSize(new java.awt.Dimension(100, 56));
        panelisi1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Simpan");
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setName("BtnSimpan"); // NOI18N
        BtnSimpan.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanActionPerformed(evt);
            }
        });
        BtnSimpan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnSimpanKeyPressed(evt);
            }
        });
        panelisi1.add(BtnSimpan);

        BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/file-edit-16x16.png"))); // NOI18N
        BtnEdit.setMnemonic('S');
        BtnEdit.setText("Ganti");
        BtnEdit.setToolTipText("Alt+S");
        BtnEdit.setEnabled(false);
        BtnEdit.setName("BtnEdit"); // NOI18N
        BtnEdit.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnEditActionPerformed(evt);
            }
        });
        BtnEdit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnEditKeyPressed(evt);
            }
        });
        panelisi1.add(BtnEdit);

        LTotal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LTotal.setText("Total Biaya : 0");
        LTotal.setName("LTotal"); // NOI18N
        LTotal.setPreferredSize(new java.awt.Dimension(400, 30));
        panelisi1.add(LTotal);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnCari.setMnemonic('E');
        BtnCari.setText("Cari");
        BtnCari.setToolTipText("Alt+E");
        BtnCari.setName("BtnCari"); // NOI18N
        BtnCari.setPreferredSize(new java.awt.Dimension(100, 30));
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
        panelisi1.add(BtnCari);

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
        panelisi1.add(BtnKeluar);

        internalFrame1.add(panelisi1, java.awt.BorderLayout.PAGE_END);

        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(560, 444));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setMnemonic('I');
        ChkInput.setText(".: Input Data");
        ChkInput.setToolTipText("Alt+I");
        ChkInput.setBorderPainted(true);
        ChkInput.setBorderPaintedFlat(true);
        ChkInput.setFocusable(false);
        ChkInput.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInput.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInput.setName("ChkInput"); // NOI18N
        ChkInput.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInput.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInputActionPerformed(evt);
            }
        });
        PanelInput.add(ChkInput, java.awt.BorderLayout.PAGE_END);

        scrollPane1.setName("scrollPane1"); // NOI18N

        FormInput.setBorder(null);
        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(89, 583));
        FormInput.setLayout(null);

        label14.setText("Operator 1 :");
        label14.setName("label14"); // NOI18N
        label14.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label14);
        label14.setBounds(0, 70, 81, 23);

        kdoperator1.setEditable(false);
        kdoperator1.setName("kdoperator1"); // NOI18N
        kdoperator1.setPreferredSize(new java.awt.Dimension(80, 23));
        kdoperator1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdoperator1KeyPressed(evt);
            }
        });
        FormInput.add(kdoperator1);
        kdoperator1.setBounds(84, 70, 100, 23);

        nmoperator1.setEditable(false);
        nmoperator1.setName("nmoperator1"); // NOI18N
        nmoperator1.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(nmoperator1);
        nmoperator1.setBounds(185, 70, 190, 23);

        BtnOperator1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnOperator1.setMnemonic('2');
        BtnOperator1.setToolTipText("Alt+2");
        BtnOperator1.setName("BtnOperator1"); // NOI18N
        BtnOperator1.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnOperator1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnOperator1ActionPerformed(evt);
            }
        });
        BtnOperator1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnOperator1KeyPressed(evt);
            }
        });
        FormInput.add(BtnOperator1);
        BtnOperator1.setBounds(376, 70, 28, 23);

        label11.setText("Tanggal :");
        label11.setName("label11"); // NOI18N
        label11.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label11);
        label11.setBounds(406, 40, 101, 23);

        tgl.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        tgl.setName("tgl"); // NOI18N
        tgl.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tglKeyPressed(evt);
            }
        });
        FormInput.add(tgl);
        tgl.setBounds(510, 40, 150, 23);

        jLabel3.setText("No.Rawat :");
        jLabel3.setName("jLabel3"); // NOI18N
        FormInput.add(jLabel3);
        jLabel3.setBounds(0, 10, 81, 23);

        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw"); // NOI18N
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });
        FormInput.add(TNoRw);
        TNoRw.setBounds(84, 10, 180, 23);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N
        FormInput.add(TPasien);
        TPasien.setBounds(265, 10, 535, 23);

        jLabel4.setText("Jenis Anasthesi :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(219, 40, 90, 23);

        jenis.setHighlighter(null);
        jenis.setName("jenis"); // NOI18N
        jenis.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jenisKeyPressed(evt);
            }
        });
        FormInput.add(jenis);
        jenis.setBounds(312, 40, 92, 23);

        label17.setText("Ast. Operator 1 :");
        label17.setName("label17"); // NOI18N
        label17.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label17);
        label17.setBounds(406, 70, 101, 23);

        kdasistoperator1.setEditable(false);
        kdasistoperator1.setName("kdasistoperator1"); // NOI18N
        kdasistoperator1.setPreferredSize(new java.awt.Dimension(80, 23));
        kdasistoperator1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdasistoperator1KeyPressed(evt);
            }
        });
        FormInput.add(kdasistoperator1);
        kdasistoperator1.setBounds(510, 70, 100, 23);

        nmasistoperator1.setEditable(false);
        nmasistoperator1.setName("nmasistoperator1"); // NOI18N
        nmasistoperator1.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(nmasistoperator1);
        nmasistoperator1.setBounds(611, 70, 190, 23);

        btnAsis1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnAsis1.setMnemonic('2');
        btnAsis1.setToolTipText("Alt+2");
        btnAsis1.setName("btnAsis1"); // NOI18N
        btnAsis1.setPreferredSize(new java.awt.Dimension(28, 23));
        btnAsis1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAsis1ActionPerformed(evt);
            }
        });
        FormInput.add(btnAsis1);
        btnAsis1.setBounds(802, 70, 28, 23);

        label19.setText("Operator 2 :");
        label19.setName("label19"); // NOI18N
        label19.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label19);
        label19.setBounds(0, 100, 81, 23);

        kdoperator2.setEditable(false);
        kdoperator2.setName("kdoperator2"); // NOI18N
        kdoperator2.setPreferredSize(new java.awt.Dimension(80, 23));
        kdoperator2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdoperator2KeyPressed(evt);
            }
        });
        FormInput.add(kdoperator2);
        kdoperator2.setBounds(84, 100, 100, 23);

        nmoperator2.setEditable(false);
        nmoperator2.setName("nmoperator2"); // NOI18N
        nmoperator2.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(nmoperator2);
        nmoperator2.setBounds(185, 100, 190, 23);

        BtnOperator2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnOperator2.setMnemonic('2');
        BtnOperator2.setToolTipText("Alt+2");
        BtnOperator2.setName("BtnOperator2"); // NOI18N
        BtnOperator2.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnOperator2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnOperator2ActionPerformed(evt);
            }
        });
        BtnOperator2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnOperator2KeyPressed(evt);
            }
        });
        FormInput.add(BtnOperator2);
        BtnOperator2.setBounds(376, 100, 28, 23);

        label20.setText("Operator 3 :");
        label20.setName("label20"); // NOI18N
        label20.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label20);
        label20.setBounds(0, 130, 81, 23);

        kdoperator3.setEditable(false);
        kdoperator3.setName("kdoperator3"); // NOI18N
        kdoperator3.setPreferredSize(new java.awt.Dimension(80, 23));
        kdoperator3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdoperator3KeyPressed(evt);
            }
        });
        FormInput.add(kdoperator3);
        kdoperator3.setBounds(84, 130, 100, 23);

        nmoperator3.setEditable(false);
        nmoperator3.setName("nmoperator3"); // NOI18N
        nmoperator3.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(nmoperator3);
        nmoperator3.setBounds(185, 130, 190, 23);

        btnOperator3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnOperator3.setMnemonic('2');
        btnOperator3.setToolTipText("Alt+2");
        btnOperator3.setName("btnOperator3"); // NOI18N
        btnOperator3.setPreferredSize(new java.awt.Dimension(28, 23));
        btnOperator3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOperator3ActionPerformed(evt);
            }
        });
        btnOperator3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnOperator3KeyPressed(evt);
            }
        });
        FormInput.add(btnOperator3);
        btnOperator3.setBounds(376, 130, 28, 23);

        label21.setText("dr Anestesi :");
        label21.setName("label21"); // NOI18N
        label21.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label21);
        label21.setBounds(0, 160, 81, 23);

        kdanestesi.setEditable(false);
        kdanestesi.setName("kdanestesi"); // NOI18N
        kdanestesi.setPreferredSize(new java.awt.Dimension(80, 23));
        kdanestesi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdanestesiKeyPressed(evt);
            }
        });
        FormInput.add(kdanestesi);
        kdanestesi.setBounds(84, 160, 100, 23);

        nmanestesi.setEditable(false);
        nmanestesi.setName("nmanestesi"); // NOI18N
        nmanestesi.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(nmanestesi);
        nmanestesi.setBounds(185, 160, 190, 23);

        BtnAnastesi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnAnastesi.setMnemonic('2');
        BtnAnastesi.setToolTipText("Alt+2");
        BtnAnastesi.setName("BtnAnastesi"); // NOI18N
        BtnAnastesi.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAnastesi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAnastesiActionPerformed(evt);
            }
        });
        FormInput.add(BtnAnastesi);
        BtnAnastesi.setBounds(376, 160, 28, 23);

        label22.setText("dr Anak :");
        label22.setName("label22"); // NOI18N
        label22.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label22);
        label22.setBounds(0, 190, 81, 23);

        kddranak.setEditable(false);
        kddranak.setName("kddranak"); // NOI18N
        kddranak.setPreferredSize(new java.awt.Dimension(80, 23));
        kddranak.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kddranakKeyPressed(evt);
            }
        });
        FormInput.add(kddranak);
        kddranak.setBounds(84, 190, 100, 23);

        nmdranak.setEditable(false);
        nmdranak.setName("nmdranak"); // NOI18N
        nmdranak.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(nmdranak);
        nmdranak.setBounds(185, 190, 190, 23);

        btnAnak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnAnak.setMnemonic('2');
        btnAnak.setToolTipText("Alt+2");
        btnAnak.setName("btnAnak"); // NOI18N
        btnAnak.setPreferredSize(new java.awt.Dimension(28, 23));
        btnAnak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnakActionPerformed(evt);
            }
        });
        FormInput.add(btnAnak);
        btnAnak.setBounds(376, 190, 28, 23);

        btnAsis2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnAsis2.setMnemonic('2');
        btnAsis2.setToolTipText("Alt+2");
        btnAsis2.setName("btnAsis2"); // NOI18N
        btnAsis2.setPreferredSize(new java.awt.Dimension(28, 23));
        btnAsis2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAsis2ActionPerformed(evt);
            }
        });
        FormInput.add(btnAsis2);
        btnAsis2.setBounds(802, 100, 28, 23);

        nmasistoperator2.setEditable(false);
        nmasistoperator2.setName("nmasistoperator2"); // NOI18N
        nmasistoperator2.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(nmasistoperator2);
        nmasistoperator2.setBounds(611, 100, 190, 23);

        kdasistoperator2.setEditable(false);
        kdasistoperator2.setName("kdasistoperator2"); // NOI18N
        kdasistoperator2.setPreferredSize(new java.awt.Dimension(80, 23));
        kdasistoperator2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdasistoperator2KeyPressed(evt);
            }
        });
        FormInput.add(kdasistoperator2);
        kdasistoperator2.setBounds(510, 100, 100, 23);

        label18.setText("Ast. Operator 2 :");
        label18.setName("label18"); // NOI18N
        label18.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label18);
        label18.setBounds(406, 100, 101, 23);

        btnAsis3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnAsis3.setMnemonic('2');
        btnAsis3.setToolTipText("Alt+2");
        btnAsis3.setName("btnAsis3"); // NOI18N
        btnAsis3.setPreferredSize(new java.awt.Dimension(28, 23));
        btnAsis3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAsis3ActionPerformed(evt);
            }
        });
        FormInput.add(btnAsis3);
        btnAsis3.setBounds(376, 340, 28, 23);

        nminstrumen.setEditable(false);
        nminstrumen.setName("nminstrumen"); // NOI18N
        nminstrumen.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(nminstrumen);
        nminstrumen.setBounds(185, 340, 190, 23);

        kdInstrumen.setEditable(false);
        kdInstrumen.setName("kdInstrumen"); // NOI18N
        kdInstrumen.setPreferredSize(new java.awt.Dimension(80, 23));
        kdInstrumen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdInstrumenKeyPressed(evt);
            }
        });
        FormInput.add(kdInstrumen);
        kdInstrumen.setBounds(84, 340, 100, 23);

        label23.setText("Instrumen :");
        label23.setName("label23"); // NOI18N
        label23.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label23);
        label23.setBounds(0, 340, 81, 23);

        btnPrwRes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnPrwRes.setMnemonic('2');
        btnPrwRes.setToolTipText("Alt+2");
        btnPrwRes.setName("btnPrwRes"); // NOI18N
        btnPrwRes.setPreferredSize(new java.awt.Dimension(28, 23));
        btnPrwRes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrwResActionPerformed(evt);
            }
        });
        FormInput.add(btnPrwRes);
        btnPrwRes.setBounds(802, 220, 28, 23);

        nmprwresust.setEditable(false);
        nmprwresust.setName("nmprwresust"); // NOI18N
        nmprwresust.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(nmprwresust);
        nmprwresust.setBounds(611, 220, 190, 23);

        kdprwresust.setEditable(false);
        kdprwresust.setName("kdprwresust"); // NOI18N
        kdprwresust.setPreferredSize(new java.awt.Dimension(80, 23));
        kdprwresust.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdprwresustKeyPressed(evt);
            }
        });
        FormInput.add(kdprwresust);
        kdprwresust.setBounds(510, 220, 100, 23);

        label24.setText("Prw.Resusitasi :");
        label24.setName("label24"); // NOI18N
        label24.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label24);
        label24.setBounds(406, 220, 101, 23);

        label26.setText("Ast. Anestesi 1 :");
        label26.setName("label26"); // NOI18N
        label26.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label26);
        label26.setBounds(406, 160, 101, 23);

        kdasistanestesi.setEditable(false);
        kdasistanestesi.setName("kdasistanestesi"); // NOI18N
        kdasistanestesi.setPreferredSize(new java.awt.Dimension(80, 23));
        kdasistanestesi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdasistanestesiKeyPressed(evt);
            }
        });
        FormInput.add(kdasistanestesi);
        kdasistanestesi.setBounds(510, 160, 100, 23);

        nmasistanestesi.setEditable(false);
        nmasistanestesi.setName("nmasistanestesi"); // NOI18N
        nmasistanestesi.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(nmasistanestesi);
        nmasistanestesi.setBounds(611, 160, 190, 23);

        BtnAsnes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnAsnes.setMnemonic('2');
        BtnAsnes.setToolTipText("Alt+2");
        BtnAsnes.setName("BtnAsnes"); // NOI18N
        BtnAsnes.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAsnes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAsnesActionPerformed(evt);
            }
        });
        FormInput.add(BtnAsnes);
        BtnAsnes.setBounds(802, 160, 28, 23);

        label27.setText("Bidan 1 :");
        label27.setName("label27"); // NOI18N
        label27.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label27);
        label27.setBounds(0, 220, 81, 23);

        kdbidan.setEditable(false);
        kdbidan.setName("kdbidan"); // NOI18N
        kdbidan.setPreferredSize(new java.awt.Dimension(80, 23));
        kdbidan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdbidanKeyPressed(evt);
            }
        });
        FormInput.add(kdbidan);
        kdbidan.setBounds(84, 220, 100, 23);

        nmbidan.setEditable(false);
        nmbidan.setName("nmbidan"); // NOI18N
        nmbidan.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(nmbidan);
        nmbidan.setBounds(185, 220, 190, 23);

        btnBidan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnBidan.setMnemonic('2');
        btnBidan.setToolTipText("Alt+2");
        btnBidan.setName("btnBidan"); // NOI18N
        btnBidan.setPreferredSize(new java.awt.Dimension(28, 23));
        btnBidan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBidanActionPerformed(evt);
            }
        });
        FormInput.add(btnBidan);
        btnBidan.setBounds(376, 220, 28, 23);

        label28.setText("Prwat Luar :");
        label28.setName("label28"); // NOI18N
        label28.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label28);
        label28.setBounds(0, 310, 81, 23);

        kdprwluar.setEditable(false);
        kdprwluar.setName("kdprwluar"); // NOI18N
        kdprwluar.setPreferredSize(new java.awt.Dimension(80, 23));
        kdprwluar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdprwluarKeyPressed(evt);
            }
        });
        FormInput.add(kdprwluar);
        kdprwluar.setBounds(84, 310, 100, 23);

        nmprwluar.setEditable(false);
        nmprwluar.setName("nmprwluar"); // NOI18N
        nmprwluar.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(nmprwluar);
        nmprwluar.setBounds(185, 310, 190, 23);

        btnPrwLuar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnPrwLuar.setMnemonic('2');
        btnPrwLuar.setToolTipText("Alt+2");
        btnPrwLuar.setName("btnPrwLuar"); // NOI18N
        btnPrwLuar.setPreferredSize(new java.awt.Dimension(28, 23));
        btnPrwLuar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrwLuarActionPerformed(evt);
            }
        });
        FormInput.add(btnPrwLuar);
        btnPrwLuar.setBounds(376, 310, 28, 23);

        jLabel5.setText("Kategori :");
        jLabel5.setName("jLabel5"); // NOI18N
        FormInput.add(jLabel5);
        jLabel5.setBounds(0, 40, 81, 23);

        Kategori.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-", "Ringan", "Ringan Cito", "Ringan Khusus", "Ringan Khusus Cito", "Ringan Khusus Cito Ganda", "Sedang", "Sedang Cito", "Sedang Khusus", "Sedang Khusus Cito", "Sedang Khusus Cito Ganda", "Besar", "Besar Cito", "Besar Khusus", "Besar Khusus Cito", "Besar Khusus Cito Ganda", "Khusus", "Kecil", "Elektive", "Emergency" }));
        Kategori.setName("Kategori"); // NOI18N
        Kategori.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KategoriKeyPressed(evt);
            }
        });
        FormInput.add(Kategori);
        Kategori.setBounds(84, 40, 122, 23);

        btnBidan2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnBidan2.setMnemonic('2');
        btnBidan2.setToolTipText("Alt+2");
        btnBidan2.setName("btnBidan2"); // NOI18N
        btnBidan2.setPreferredSize(new java.awt.Dimension(28, 23));
        btnBidan2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBidan2ActionPerformed(evt);
            }
        });
        FormInput.add(btnBidan2);
        btnBidan2.setBounds(376, 250, 28, 23);

        nmbidan2.setEditable(false);
        nmbidan2.setName("nmbidan2"); // NOI18N
        nmbidan2.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(nmbidan2);
        nmbidan2.setBounds(185, 250, 190, 23);

        kdbidan2.setEditable(false);
        kdbidan2.setName("kdbidan2"); // NOI18N
        kdbidan2.setPreferredSize(new java.awt.Dimension(80, 23));
        kdbidan2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdbidan2KeyPressed(evt);
            }
        });
        FormInput.add(kdbidan2);
        kdbidan2.setBounds(84, 250, 100, 23);

        label29.setText("Bidan 2 :");
        label29.setName("label29"); // NOI18N
        label29.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label29);
        label29.setBounds(0, 250, 81, 23);

        label30.setText("Bidan 3 :");
        label30.setName("label30"); // NOI18N
        label30.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label30);
        label30.setBounds(0, 280, 81, 23);

        kdbidan3.setEditable(false);
        kdbidan3.setName("kdbidan3"); // NOI18N
        kdbidan3.setPreferredSize(new java.awt.Dimension(80, 23));
        kdbidan3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdbidan3KeyPressed(evt);
            }
        });
        FormInput.add(kdbidan3);
        kdbidan3.setBounds(84, 280, 100, 23);

        nmbidan3.setEditable(false);
        nmbidan3.setName("nmbidan3"); // NOI18N
        nmbidan3.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(nmbidan3);
        nmbidan3.setBounds(185, 280, 190, 23);

        btnBidan3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnBidan3.setMnemonic('2');
        btnBidan3.setToolTipText("Alt+2");
        btnBidan3.setName("btnBidan3"); // NOI18N
        btnBidan3.setPreferredSize(new java.awt.Dimension(28, 23));
        btnBidan3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBidan3ActionPerformed(evt);
            }
        });
        FormInput.add(btnBidan3);
        btnBidan3.setBounds(376, 280, 28, 23);

        label25.setText("Onloop 1 :");
        label25.setName("label25"); // NOI18N
        label25.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label25);
        label25.setBounds(406, 250, 101, 23);

        kdonloop1.setEditable(false);
        kdonloop1.setName("kdonloop1"); // NOI18N
        kdonloop1.setPreferredSize(new java.awt.Dimension(80, 23));
        kdonloop1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdonloop1KeyPressed(evt);
            }
        });
        FormInput.add(kdonloop1);
        kdonloop1.setBounds(510, 250, 100, 23);

        nmonloop1.setEditable(false);
        nmonloop1.setName("nmonloop1"); // NOI18N
        nmonloop1.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(nmonloop1);
        nmonloop1.setBounds(611, 250, 190, 23);

        btnOnloop1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnOnloop1.setMnemonic('2');
        btnOnloop1.setToolTipText("Alt+2");
        btnOnloop1.setName("btnOnloop1"); // NOI18N
        btnOnloop1.setPreferredSize(new java.awt.Dimension(28, 23));
        btnOnloop1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOnloop1ActionPerformed(evt);
            }
        });
        FormInput.add(btnOnloop1);
        btnOnloop1.setBounds(802, 250, 28, 23);

        btnOnloop2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnOnloop2.setMnemonic('2');
        btnOnloop2.setToolTipText("Alt+2");
        btnOnloop2.setName("btnOnloop2"); // NOI18N
        btnOnloop2.setPreferredSize(new java.awt.Dimension(28, 23));
        btnOnloop2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOnloop2ActionPerformed(evt);
            }
        });
        FormInput.add(btnOnloop2);
        btnOnloop2.setBounds(802, 280, 28, 23);

        nmonloop2.setEditable(false);
        nmonloop2.setName("nmonloop2"); // NOI18N
        nmonloop2.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(nmonloop2);
        nmonloop2.setBounds(611, 280, 190, 23);

        kdonloop2.setEditable(false);
        kdonloop2.setName("kdonloop2"); // NOI18N
        kdonloop2.setPreferredSize(new java.awt.Dimension(80, 23));
        kdonloop2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdonloop2KeyPressed(evt);
            }
        });
        FormInput.add(kdonloop2);
        kdonloop2.setBounds(510, 280, 100, 23);

        label31.setText("Onloop 2 :");
        label31.setName("label31"); // NOI18N
        label31.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label31);
        label31.setBounds(406, 280, 101, 23);

        label32.setText("Onloop 3 :");
        label32.setName("label32"); // NOI18N
        label32.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label32);
        label32.setBounds(406, 310, 101, 23);

        btnOnloop3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnOnloop3.setMnemonic('2');
        btnOnloop3.setToolTipText("Alt+2");
        btnOnloop3.setName("btnOnloop3"); // NOI18N
        btnOnloop3.setPreferredSize(new java.awt.Dimension(28, 23));
        btnOnloop3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOnloop3ActionPerformed(evt);
            }
        });
        FormInput.add(btnOnloop3);
        btnOnloop3.setBounds(802, 310, 28, 23);

        nmonloop3.setEditable(false);
        nmonloop3.setName("nmonloop3"); // NOI18N
        nmonloop3.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(nmonloop3);
        nmonloop3.setBounds(611, 310, 190, 23);

        kdonloop3.setEditable(false);
        kdonloop3.setName("kdonloop3"); // NOI18N
        kdonloop3.setPreferredSize(new java.awt.Dimension(80, 23));
        kdonloop3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdonloop3KeyPressed(evt);
            }
        });
        FormInput.add(kdonloop3);
        kdonloop3.setBounds(510, 310, 100, 23);

        label33.setText("dr Pj. Anak :");
        label33.setName("label33"); // NOI18N
        label33.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label33);
        label33.setBounds(0, 370, 81, 23);

        kdpjanak.setEditable(false);
        kdpjanak.setName("kdpjanak"); // NOI18N
        kdpjanak.setPreferredSize(new java.awt.Dimension(80, 23));
        kdpjanak.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdpjanakKeyPressed(evt);
            }
        });
        FormInput.add(kdpjanak);
        kdpjanak.setBounds(84, 370, 100, 23);

        nmpjanak.setEditable(false);
        nmpjanak.setName("nmpjanak"); // NOI18N
        nmpjanak.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(nmpjanak);
        nmpjanak.setBounds(185, 370, 190, 23);

        btndrpjanak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btndrpjanak.setMnemonic('2');
        btndrpjanak.setToolTipText("Alt+2");
        btndrpjanak.setName("btndrpjanak"); // NOI18N
        btndrpjanak.setPreferredSize(new java.awt.Dimension(28, 23));
        btndrpjanak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btndrpjanakActionPerformed(evt);
            }
        });
        FormInput.add(btndrpjanak);
        btndrpjanak.setBounds(376, 370, 28, 23);

        label34.setText("dr Umum :");
        label34.setName("label34"); // NOI18N
        label34.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label34);
        label34.setBounds(0, 400, 81, 23);

        kddrumum.setEditable(false);
        kddrumum.setName("kddrumum"); // NOI18N
        kddrumum.setPreferredSize(new java.awt.Dimension(80, 23));
        kddrumum.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kddrumumKeyPressed(evt);
            }
        });
        FormInput.add(kddrumum);
        kddrumum.setBounds(84, 400, 100, 23);

        nmdrumum.setEditable(false);
        nmdrumum.setName("nmdrumum"); // NOI18N
        nmdrumum.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(nmdrumum);
        nmdrumum.setBounds(185, 400, 190, 23);

        btndrumum.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btndrumum.setMnemonic('2');
        btndrumum.setToolTipText("Alt+2");
        btndrumum.setName("btndrumum"); // NOI18N
        btndrumum.setPreferredSize(new java.awt.Dimension(28, 23));
        btndrumum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btndrumumActionPerformed(evt);
            }
        });
        FormInput.add(btndrumum);
        btndrumum.setBounds(376, 400, 28, 23);

        label35.setText("Ast. Operator 3 :");
        label35.setName("label35"); // NOI18N
        label35.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label35);
        label35.setBounds(406, 130, 101, 23);

        kdasistoperator3.setEditable(false);
        kdasistoperator3.setName("kdasistoperator3"); // NOI18N
        kdasistoperator3.setPreferredSize(new java.awt.Dimension(80, 23));
        kdasistoperator3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdasistoperator3KeyPressed(evt);
            }
        });
        FormInput.add(kdasistoperator3);
        kdasistoperator3.setBounds(510, 130, 100, 23);

        nmasistoperator3.setEditable(false);
        nmasistoperator3.setName("nmasistoperator3"); // NOI18N
        nmasistoperator3.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(nmasistoperator3);
        nmasistoperator3.setBounds(611, 130, 190, 23);

        btnAsis4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnAsis4.setMnemonic('2');
        btnAsis4.setToolTipText("Alt+2");
        btnAsis4.setName("btnAsis4"); // NOI18N
        btnAsis4.setPreferredSize(new java.awt.Dimension(28, 23));
        btnAsis4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAsis4ActionPerformed(evt);
            }
        });
        FormInput.add(btnAsis4);
        btnAsis4.setBounds(802, 130, 28, 23);

        label36.setText("Ast. Anestesi 2 :");
        label36.setName("label36"); // NOI18N
        label36.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label36);
        label36.setBounds(406, 190, 101, 23);

        kdasistanestesi2.setEditable(false);
        kdasistanestesi2.setName("kdasistanestesi2"); // NOI18N
        kdasistanestesi2.setPreferredSize(new java.awt.Dimension(80, 23));
        kdasistanestesi2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdasistanestesi2KeyPressed(evt);
            }
        });
        FormInput.add(kdasistanestesi2);
        kdasistanestesi2.setBounds(510, 190, 100, 23);

        nmasistanestesi2.setEditable(false);
        nmasistanestesi2.setName("nmasistanestesi2"); // NOI18N
        nmasistanestesi2.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(nmasistanestesi2);
        nmasistanestesi2.setBounds(611, 190, 190, 23);

        BtnAsnes1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnAsnes1.setMnemonic('2');
        BtnAsnes1.setToolTipText("Alt+2");
        BtnAsnes1.setName("BtnAsnes1"); // NOI18N
        BtnAsnes1.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAsnes1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAsnes1ActionPerformed(evt);
            }
        });
        FormInput.add(BtnAsnes1);
        BtnAsnes1.setBounds(802, 190, 28, 23);

        label37.setText("Onloop 4 :");
        label37.setName("label37"); // NOI18N
        label37.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label37);
        label37.setBounds(406, 340, 101, 23);

        kdonloop4.setEditable(false);
        kdonloop4.setName("kdonloop4"); // NOI18N
        kdonloop4.setPreferredSize(new java.awt.Dimension(80, 23));
        kdonloop4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdonloop4KeyPressed(evt);
            }
        });
        FormInput.add(kdonloop4);
        kdonloop4.setBounds(510, 340, 100, 23);

        nmonloop4.setEditable(false);
        nmonloop4.setName("nmonloop4"); // NOI18N
        nmonloop4.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(nmonloop4);
        nmonloop4.setBounds(611, 340, 190, 23);

        btnOnloop4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnOnloop4.setMnemonic('2');
        btnOnloop4.setToolTipText("Alt+2");
        btnOnloop4.setName("btnOnloop4"); // NOI18N
        btnOnloop4.setPreferredSize(new java.awt.Dimension(28, 23));
        btnOnloop4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOnloop4ActionPerformed(evt);
            }
        });
        FormInput.add(btnOnloop4);
        btnOnloop4.setBounds(802, 340, 28, 23);

        btnOnloop5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnOnloop5.setMnemonic('2');
        btnOnloop5.setToolTipText("Alt+2");
        btnOnloop5.setName("btnOnloop5"); // NOI18N
        btnOnloop5.setPreferredSize(new java.awt.Dimension(28, 23));
        btnOnloop5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOnloop5ActionPerformed(evt);
            }
        });
        FormInput.add(btnOnloop5);
        btnOnloop5.setBounds(802, 370, 28, 23);

        nmonloop5.setEditable(false);
        nmonloop5.setName("nmonloop5"); // NOI18N
        nmonloop5.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(nmonloop5);
        nmonloop5.setBounds(611, 370, 190, 23);

        kdonloop5.setEditable(false);
        kdonloop5.setName("kdonloop5"); // NOI18N
        kdonloop5.setPreferredSize(new java.awt.Dimension(80, 23));
        kdonloop5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdonloop5KeyPressed(evt);
            }
        });
        FormInput.add(kdonloop5);
        kdonloop5.setBounds(510, 370, 100, 23);

        label38.setText("Onloop 5 :");
        label38.setName("label38"); // NOI18N
        label38.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label38);
        label38.setBounds(406, 370, 101, 23);

        label12.setText("Selesai :");
        label12.setName("label12"); // NOI18N
        label12.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label12);
        label12.setBounds(406, 400, 101, 23);

        tgl2.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        tgl2.setName("tgl2"); // NOI18N
        tgl2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tgl2KeyPressed(evt);
            }
        });
        FormInput.add(tgl2);
        tgl2.setBounds(510, 400, 150, 23);

        PreOp.setHighlighter(null);
        PreOp.setName("PreOp"); // NOI18N
        PreOp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PreOpKeyPressed(evt);
            }
        });
        FormInput.add(PreOp);
        PreOp.setBounds(148, 430, 256, 23);

        jLabel6.setText("Diagnosis Pre-operatif :");
        jLabel6.setName("jLabel6"); // NOI18N
        FormInput.add(jLabel6);
        jLabel6.setBounds(0, 430, 145, 23);

        jLabel7.setText("Diagnosis Post-operatif :");
        jLabel7.setName("jLabel7"); // NOI18N
        FormInput.add(jLabel7);
        jLabel7.setBounds(0, 460, 145, 23);

        PostOp.setHighlighter(null);
        PostOp.setName("PostOp"); // NOI18N
        PostOp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PostOpKeyPressed(evt);
            }
        });
        FormInput.add(PostOp);
        PostOp.setBounds(148, 460, 256, 23);

        jLabel8.setText("Jaringan di-Eksisi / -Insisi :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(0, 490, 145, 23);

        Jaringan.setHighlighter(null);
        Jaringan.setName("Jaringan"); // NOI18N
        Jaringan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JaringanKeyPressed(evt);
            }
        });
        FormInput.add(Jaringan);
        Jaringan.setBounds(148, 490, 256, 23);

        jLabel9.setText("Dikirim Pemeriksaan PA :");
        jLabel9.setName("jLabel9"); // NOI18N
        FormInput.add(jLabel9);
        jLabel9.setBounds(0, 520, 145, 23);

        DikirimPA.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ya", "Tidak" }));
        DikirimPA.setName("DikirimPA"); // NOI18N
        DikirimPA.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DikirimPAKeyPressed(evt);
            }
        });
        FormInput.add(DikirimPA);
        DikirimPA.setBounds(148, 520, 130, 23);

        scrollPane2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane2.setName("scrollPane2"); // NOI18N

        Laporan.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        Laporan.setColumns(20);
        Laporan.setRows(30);
        Laporan.setName("Laporan"); // NOI18N
        scrollPane2.setViewportView(Laporan);

        FormInput.add(scrollPane2);
        scrollPane2.setBounds(510, 430, 320, 143);

        jLabel10.setText("Laporan Operasi :");
        jLabel10.setName("jLabel10"); // NOI18N
        FormInput.add(jLabel10);
        jLabel10.setBounds(406, 430, 101, 23);

        btnTemplate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnTemplate.setMnemonic('2');
        btnTemplate.setToolTipText("Alt+2");
        btnTemplate.setName("btnTemplate"); // NOI18N
        btnTemplate.setPreferredSize(new java.awt.Dimension(28, 23));
        btnTemplate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTemplateActionPerformed(evt);
            }
        });
        FormInput.add(btnTemplate);
        btnTemplate.setBounds(479, 460, 28, 23);

        jLabel11.setText("Nomor Implan :");
        jLabel11.setName("jLabel11"); // NOI18N
        FormInput.add(jLabel11);
        jLabel11.setBounds(0, 550, 145, 23);

        NomorImplant.setHighlighter(null);
        NomorImplant.setName("NomorImplant"); // NOI18N
        NomorImplant.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NomorImplantKeyPressed(evt);
            }
        });
        FormInput.add(NomorImplant);
        NomorImplant.setBounds(148, 550, 256, 23);

        scrollPane1.setViewportView(FormInput);

        PanelInput.add(scrollPane1, java.awt.BorderLayout.CENTER);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        DlgCariTagihanOperasi form=new DlgCariTagihanOperasi(null,false);
        //form.emptTeks();      
        form.setPasien(TNoRw.getText());
        form.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        form.setLocationRelativeTo(internalFrame1);
        form.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
//        dispose();
}//GEN-LAST:event_BtnCariActionPerformed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
            dispose();  
}//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){            
            dispose();              
        }else{Valid.pindah(evt,TCariPaket,TCari);}
}//GEN-LAST:event_BtnKeluarKeyPressed
/*
private void KdKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TKdKeyPressed
    Valid.pindah(evt,BtnCari,Nm);
}//GEN-LAST:event_TKdKeyPressed
*/

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnCariActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnSimpan, BtnKeluar);
        }
    }//GEN-LAST:event_BtnCariKeyPressed

private void kdoperator1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kdoperator1KeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            nmoperator1.setText(Sequel.CariDokter(kdoperator1.getText()));
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            BtnOperator1ActionPerformed(null);
        }else{
            Valid.pindah(evt,tgl,kdoperator2);
        }
}//GEN-LAST:event_kdoperator1KeyPressed

private void BtnOperator1ActionPerformed(java.awt.event.ActionEvent evt) {/ /GEN-FIRST:event_BtnOperator1ActionPerformed
        if (dokter == null || !dokter.isDisplayable()) {
            dokter=new DlgCariDokt e r(null,false); 
             dokter.setDefaseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dokter.addWindowListener(new WindowAdapter() {
                @Override     
                public void windowClosed(WindowEvent e) {
                    if(dokter.getTable().getSelectedRow()!= -1){
                        kdoperator1.setText(dokter
                     
                             kdoperator1.requestFocus();
                    }  
                    dokter=null; 
            }
                 });
            dokter.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
            dokter.setLocationRelativeTo(internalFrame1); 
        }    
            
        i f (d o
            kter == null) ret urn; 
        
             if (!dokter.isVisible()) {
      
     *             dokter.emptTeks();
     * 
     *     }  
     *         if (dokter.isVisible())
                 dokter.toFront();
            return;
        }     
        do kter.setVisible(t ru e); 
}//GEN-LAST:event_BtnOperator1ActionPerformed
  
private void tglKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tglKeyPressed
        Valid.pindah(evt,jenis,BtnOperator1);
}//GEN- LAST:event_tglKeyPressed

    private void ppBersihkanActionPerformed(java.awt.event.ActionEvent  evt) {//GEN-FIRST:event_ppBersihkanActionPerformed
             int row2=tabMod e. getRowCount(); 
            for(int r=0;r<row2;r++){ 
                  tabMode.setValueA t( "",r,0); 
            }
}//GEN-LA ST:e vent_ppBersihkanActionPerformed
  
private v
             if(evt.getKeyCode()==KeyEvent.V

                            " on pasien.no_rkm_medis=reg_periksa.no_rkm_medis wh ere reg_periksa.no_rawat=? ",TPasien,TNoRw.getText());
        }else{            
            Valid. p indah(evt,TCari,kdopera tor1);
        }
}//GEN-LAST:event_TNoRwKeyPressed

private void jenisKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jenisKeyPressed
    Valid.pindah(evt,K ategori,tgl);  
}//GEN-LAST:event_jenisKeyPressed
                                 

                                 
private void kdasistoperator1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kdasistoperator1KeyPressed
        if(evt.getKey
                    Action P erformed(null);
        }else{
            Valid.pindah(evt,kdInstrumen,kdasistoperator2);
        }     
          
}//GEN-LA

        void btnAsis1Action
            erformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAsis1ActionPerformed
        if (petugas == null || !petugas.isDisplayable()) {
            petugas=new Dlg
            petugas.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
         
                @Override
                public void windowClosed(WindowEvent e) {
                    if(petugas.getTable().getSelectedRow()!= -1){                   
         
                        nmasisto
                             kdasistoperator1.requ

                        petugas=null; 
                }  
                 });

                petugas.setSize(internalFrame1.getWidth()-20,internalFrame1.get Height()-20);
        petugas. s etLocationRelativeTo(i
                
              
        p
             if (!petugas.isVisible()) {

                petugas.emptTeks(); 
        }      
        if (petugas.isVisib
                    le()) { 
                            ont();
                     
             r e
               
        p
    }// GEN-LAST:event_btnAsis1ActionP

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_TCariKeyPressed
            if(evt.getKey Code()==K eyEve
                 BtnCariActionPerforme

                BtnCari.requestFocus(); 
        }e lse if(evt.getKey Co de()==KeyEvent. VK_PAGE_UP){
            BtnKeluar.requestFocus();
        }  
}//GEN-LAST:event_TCariKeyPre ssed 


             runBackground(() ->tampil2());

     
private void BtnCari1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCari1KeyPressed
        if(evt.getK e yCode()==KeyEvent.VK_SPA CE){
            BtnCari1ActionPerformed(null);
        }else{
            Valid.pindah(evt, TCari, BtnAll);
        }
}//GEN-LAST:event_BtnC ari1KeyPressed  
                        
                                 
private void BtnAllActionPerformed(java.awt.event
                                .ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed 
        TCari.setText("");
        runBackground
                    nAllAct i onPerformed

private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            BtnAllActionPerformed(null);     
        }else{
         

        AST:event_BtnAllKeyP
            essed

private void BtnTambahAction
            .setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        D
        produsen.emptTeks();   
        produsen.setSize(internalFrame1.getWidth(),internalFrame1.getHeight());
        produsen.setLocationRelativeTo(internalFrame1);
        p
        this.setCursor(Cursor.get
    }// GEN-LAST:event_BtnTambahActionPerforme

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt ) {//GEN-FIRST:event_tbObatMouseClicked
        if (tbObat.getRowCou nt ()!=0){ 
            try {
                  getData();   
            } catch (java.lang.NullPointerException e) {
             }    
        }
        L
    }// GEN-LAST:event_tbObatMouseClic

    private void tbObatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST: event_tbObatKeyPressed
        if(tbObat.getRowCou nt()!=0){
                 if(evt.getKeyCode()==KeyEvent

                        getData(); 
                     int row =t bObat.getSelectedC olumn();
                    if(row==1){
                          TCari.setText("");
                        TCari.requestFocus();
         
                     } catch (java.lang.N

                }else if((evt.getKeyCode()==KeyEvent.VK_UP)||(evt.getKeyCo de()==KeyEvent.VK_DOWN)){
                try {
                    getData ();
                     } catch (java.lang.Null

                }else if(evt.getKeyCode()==KeyEvent.VK_DELETE){ 
                 int row=tbO ba t.getSelectedRow(); 
                if(row!= -1){
                      tabMode.setValueAt("", row,0);
                }
         
             }

    }//GEN-LAST:event_tbObatKeyPressed 

private void TCariPaketKeyPress e d(java.awt.event.KeyEven t evt) {//GEN-FIRST:event_TCariPaketKeyPressed
    if(evt.getKeyCode()==Key
            BtnCari2ActionPerformed(null); 
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            BtnCari2.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAG
                 BtnKeluar.requestFocus();

    }//GEN-LAST:event_TCariPaketKeyPressed 
    
private void BtnCari2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCari2ActionPerformed
   runBackground(() ->tampil());
}//GEN-LAST:event_BtnCari2ActionPerformed

private void BtnCari2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCari2KeyPressed
// TODO add your handling code here:    
    }// GEN-LAST:event_BtnCari2KeyPressed

    private void BtnAll1ActionPerformed(java.awt.event.ActionEvent  evt) {//GEN-FIRST:event_BtnAll1ActionPerformed
  TCariPak et.setText("");   
  runBackgroun d(() ->tampil());   
}//GEN-LAST:event_BtnAll1ActionPerformed

private void BtnAll1KeyPres s ed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAll1KeyPressed
// TODO add your handl ing  co de  here:
}//GEN-LAST:event_BtnAll1KeyPressed

private void BtnTambahOperasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnTambahOperasiActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        DlgJnsPerawatanOperasi produsen=new DlgJnsPerawatanOperasi(null,false);
        produ sen.emp tTeks();          
        produsen.isCek();
        produsen.setSize(internalFrame1.getWidth(),internalFrame1.getHeight());
        produsen.setLocationRelativeTo(internalFrame1);
        produsen.setVisible(true);
        this. setCurs or(Cursor.getDefa ul tCursor());  
}//GEN-LAST:event_BtnTa m bahOperasiActionPerformed
   
private void tbtindakanMouseClicked(java.awt.ev ent.MouseEvent evt) {//GEN-FIRST:event_tbtindakanMouseClicked
       if(tbtindakan.getRowCount()!=0){
            t
                getData2();
            } catch (java.lang.NullPoin t erException e) {  
                     System.out.println

            } 
               
        LTotal.setText("Total Biaya : "+Valid.SetAngka(biayaobat+biayatindakan));
}//GEN-LA ST:even t_tbtindakanMouse Cl icked 

private v oid tbt indakanKeyPressed (j ava.awt.event.KeyEve nt evt) {//GEN-FIRST:event_tbtindakanKeyPressed
    if(tbtindakan.getRowCount()!=0){
         
                     try {

                        if((row!=0)||(row!=28)){ 
                              if(tbtin
                               tbtindakan.setV

                            TCariPaket.setText(""); 
                                TCariPaket.r
                         }               

                    } catch (java.lang.NullPointerException e) { 
                      }
                  }else if( (evt.getKe
                     try {

                    } catch (java.lang.NullPointerException e) { 
                        }
                 }

        LTotal.setText("Total Biaya : "+Valid.SetAngka(biayaobat+biayatindakan)); 
}//GEN-LAST:event_tbtindakanKeyPressed
   
private void kdoperator2KeyP
        vt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            nmoperator2.setText(Sequel.CariDokter(k doperator2.getText()));
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            BtnOperator2ActionPerformed(null);
        }else{
                 Valid.pindah(evt,kdoperator1,kdoperat

    }//GEN-LAST:event_kdoperator2KeyPressed 
            
private void BtnOperator2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnOperator2ActionPerformed
        if (dokter == null || !dokter.isDisplayable()) {
            dokter=new DlgCariDokter(null,false);
            dokter.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dokter.addWindowListener(new WindowAdapter() {
         

                    if(dokter.getTable( ) .getSelectedRow()!= -1){  
                             kdoperator2.setT

                            kdoperator2.requestFocus(); 
                         }     
                     dokter=null ;  
                }
            });  
            dokter.set Size( in te rn alFr am e1.g etWidth()-20,internalFrame1.getHeight()-20);
            dokter.setLoca tionRelativeTo(internalFrame 1 ); 
                              
                        
                        ll) return;
        if (!dokter.isVisible()) {
            dokter.is
                    mptTeks();
        }  
        if (dokter.isVisible()) {
            d okter.t oFront();       
            return;
        }    
        dokter.setVisible(true);
}//GEN-LAST:event_BtnOperator2ActionPerformed

        a
        f(evt.getKeyCode()==KeyEvent.VK _ PAGE_DOWN){  
                 nmoperator3.setText(Sequel

                btnOperator3ActionPerformed(null); 
             }else{   
            Valid.pindah(evt,kdoperator2,kdanestesi);
        }     
}//GEN-LAST:event_kdoperator3KeyPressed
  
private void btnOperator3Acti onPerformed( java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOperator3ActionPerformed
        i
                 dokter=new DlgCariDokter(nu

                dokter.addWindowListener(new WindowAdapter() { 
                @Override
                pu b lic void windowClosed(W indowEvent e) {
                    if(dokter.getTable().getSelectedRow()!= -1){
                        kdoperator3.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),0).toString());
                        nmoperator3.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),1).toString());
                        kdoperator3.requestFocus();
                    }     
                    dokter=null;
                                 
                }
                                 
            });
            dokter.se
                    etLoca t ionRelativeTo(internalFrame1);
        }
            
        if (dokter == null) return;     
        if (!dokter.isVisible()) {
         

        }  
            
        if (dokter.isVisible()) {
            dokter.toFront(
            return;
        }
        dokter.setVisible(true);
}//GEN-LAST:event_btnOperator3ActionPerformed

private v
        t.getKeyCode()==KeyEvent
                 nmanestesi.setText(Sequel.CariDok

                BtnAnastesiActionPerformed(null); 
              }else{   
            Valid.pindah(evt,kdoperator3,kddranak);
        }     
}//GEN-LAST:event_kdanestesiKeyPressed
  
private void BtnAnastesiActio nPerformed(j ava.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAnastesiActionPerformed
        i
                 dokter=new DlgCariDokter(nu

                dokter.addWindowListener(new WindowAdapter() { 
                @Override
                pu b lic void windowClosed(W indowEvent e) {
                    if(dokter.getTable().getSelectedRow()!= -1){
                        kdanestesi.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),0).toString());
                        nmanestesi.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),1).toString());
                        kdanestesi.requestFocus();
                    }     
                    dokter=null;
                                 
                }
                                 
            });
            dokter.se
                    etLoca t ionRelativeTo(internalFrame1);
        }
            
        if (dokter == null) return;     
        if (!dokter.isVisible()) {
         

        }  
            
        if (dokter.isVisible()) {
            dokter.toFront(
            return;
        }
        dokter.setVisible(true);
}//GEN-LAST:event_BtnAnastesiActionPerformed

private v
        vt.getKeyCode()==KeyEven
                 nmdranak.setText(Sequel.CariDokte

                btnAnakActionPerformed(null); 
              }else{   
            Valid.pindah(evt,kdanestesi,kdbidan);
        }     
}//GEN-LAST:event_kddranakKeyPressed
  
private void btnAnakActionPer formed(java. awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnakActionPerformed
        i
                 dokter=new DlgCariDokter(n

                dokter.addWindowListener(new WindowAdapter() { 
                @Override
                pu b lic void windowClosed(W indowEvent e) {
                    if(dokter.getTable().getSelectedRow()!= -1){
                        kddranak.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),0).toString());
                        nmdranak.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),1).toString());
                        kddranak.requestFocus();
                    }     
                    dokter=null;
                                 
                }
                                 
            });
            dokter.se
                    etLoca t ionRelativeTo(internalFrame1);
        }
            
        if (dokter == null) return;     
        if (!dokter.isVisible()) {
         

        }  
            
        if (dokter.isVisible()) {
            dokter.toFront(
            return;
        }
        dokter.setVisible(true);
}//GEN-LAST:event_btnAnakActionPerformed

private v
        if (petugas == null || !
                 petugas=new DlgCariPetugas(null,

                petugas.addWindowListener(new WindowAdapter() { 
                     @Overri de  
                public void windowClosed(WindowEvent e) {
                      if(petugas.ge tT able().getSelec tedRow()!= -1){                   
                        kdasistoperator2.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                          nmasistoperator2.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                        kdasi stoperator2 .requestFocus();
         
                         petugas=null;

                }); 

            petuga s .setSize(internalFrame1 .getWidth()-20,internalFrame1.getHeight()-20);
            petugas.setLocationRelativeTo(internalFrame1);
        }
            
        if (petugas == null) return;
        if (!petugas.i sVisible()) {  
            petugas.isCek();    
                                 
            petugas.emptTeks();
                                 
        }  
        if (petugas.i
                    toFron t ();
            return;
        }    
        petugas.setVisible(true);     
}//GEN-LAST:event_btnAsis2ActionPerformed


        if(evt.getKeyCode()
            =KeyEvent.VK_UP){
            btnAsis2ActionPerformed(null);
        }else{
            Valid.pindah(evt,kdasistoperator1,kdasistoperator3);
        }
        AST:event_kdasistoperator2KeyPressed

private void btnAsis3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAsis3ActionPerformed
        i
            petugas=new DlgCariP
                 petugas.setDefaultCloseOpera

                    @Override 
                public void windowClosed(WindowEvent e) {
                      if(petugas.getTable().ge tSelectedRow()!= -1){                   
                        kdInstrumen.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                        nminstrumen.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                        kdInstrumen.requestFocus();
                    } 
                    pe tugas=null;  
                        
                                 
            });
                                 

            petugas.s
                    setLoca t ionRelativeTo(internalFrame1);
        }
            
        if (petugas == null) return;
        if (!petugas.isVisible()) {     
            petugas.isCek();    
         

        if (petugas.isVisibl
            ()) {
            petugas.toFront();
            return;
             
        p
        AST:event_btnAsis3ActionPerformed

private void kdInstrumenKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kdInstrumenKeyPressed
   if(evt
            Sequel.cariIsi("selec
             }else if(evt.getKeyCode()==KeyEve

            }else{ 
             Valid.pindah(ev t, kdprwluar,kdasi stoperator1);
        }         
}//GEN-LA ST:e vent_kdInstrumenKeyPressed
  
private v
             if (petugas == null || !petugas.isDi

                petugas.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_ CLOSE);
            petugas.addWindowListener(new WindowAdapter() {
                @Ov e rride 
                public void windowClosed(WindowEvent e) {
                    if(petugas.getTable().getSelectedRow()!= -1){                   
                        kdprwresust.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                        nmprwresust.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                         kdprwresust.requestFocus();  
                        
                                 
                    petugas=null;
                                 
                }
            });
                      
            petugas.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
            petugas.setLocationRelativeTo(internalFrame1);
        }
                 
        if (petugas == null) return;
        i

            petugas.emptTeks
            );
        }  
        if (petugas.isVisibl
            petugas.toFront();
         
        }    
        petugas.setVisible(true);
}//GEN-LAST:event_btnPrwResActionPerformed

        void kdprwresustKeyPresse
         Valid.pindah(evt,kdasistanestesi2,kdo

     
        va te void kdasistan es tesiKeyPressed(java.aw t.event.KeyEvent evt) {//GEN-FIRST:event_kdasistanestesiKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_UP){     
             BtnA snesActionPerform ed (null); 
        }else{
             V alid.pindah(evt,kdasistoperator3,kdasistanestesi2);
        }  
}//GEN-LA
     

            if (petugas == null || !petugas.isDisplayable()) { 
            petugas=new DlgCariPetugas(null,false);
            petugas . setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
            petugas.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    if(petugas.getTable().getSelectedRow()!= -1){                   
                         kdasistanestesi.setText(petugas.ge tTable (
                        nmasistanestesi.setT
                                ext(petugas.getTable().getValueAt(petugas.getTable().getSelectedRo w(),1).toString());
                        kdasistanestesi.requ
                                estFocus(); 
                    } 
                    p
                      
            });

            petugas.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
            petugas.setLocationRelativeTo(internalFra m e1) ;  
        }
         

        if (!petugas.isVisib
            e()) {
            petugas.isCek();    
            petugas.emptTeks
            
        i
            petugas.toFront();
            return;
        }    
        p
        AST:event_BtnAsnesActionP
     

            if(evt.getKeyCode()==KeyEvent.VK_UP){ 
                btnBidanA ctionPerformed(nu ll);
             }else{

            } 
}//GEN-LAS T:event_kdbidanKe yP ressed 

private v oid  btnBidanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBidanActionPerformed
        if (petugas == null | | !petugas.isDisp layable()) {
         
                 petugas.setDefaultCloseOperatio

                    @Override 
                public void windowClosed(WindowEvent e) {
                      if(petugas.getTable().ge tSelectedRow()!= -1){                   
                        kdbidan.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                        nmbidan.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                        kdbidan.requestFocus();
                    } 
                    pe tugas=null;  
                        
                                 
            });
                                 

            petugas.s
                    setLoca t ionRelativeTo(internalFrame1);
        }
            
        if (petugas == null) return;
        if (!petugas.isVisible()) {     
            petugas.isCek();    
         

        if (petugas.isVisibl
            ()) {
            petugas.toFront();
            return;
             
        p
        AST:event_btnBidanActionPerformed

private void kdprwluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kdprwluarKeyPressed
        i
            btnPrwLuarActionPerfo
             }else{

            } 
}//GEN-LAS T:event_kdprwluar Ke yPressed 

private v oid  btnPrwLuarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrwLuarActionPerformed
        if (petugas == null | | !petuga s.isDisplayable()) {
         
                 petugas.setDefaultClose

                    @Override 
                public void windowClosed(WindowEvent e) {
                      if(petugas.getTable().ge tSelectedRow()!= -1){                   
                        kdprwluar.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                        nmprwluar.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                        kdprwluar.requestFocus();
                    } 
                    pe tugas=null;  
                        
                                 
            });
                                 

            petugas.s
                    setLoca t ionRelativeTo(internalFrame1);
        }
            
        if (petugas == null) return;
        if (!petugas.isVisible()) {     
            petugas.isCek();    
         

        if (petugas.isVisibl
            ()) {
            petugas.toFront();
            return;
             
        p
        AST:event_btnPrwLuarActionPerformed

private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInputActionPerformed
   isForm
        AST:event_ChkInputActionP
     

            Valid.pindah(evt,TCariPaket,jenis); 
    }//GEN -LAST:event_Kateg or iKeyPressed 

    priva te v oid btnBidan2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBidan2ActionPerformed
        if (petugas == null | | !petuga s.isDisplayable()) {
         
                 petugas.setDefaultCloseOp

                    @Override 
                public void windowClosed(WindowEvent e) {
                      if(petugas.getTable().ge tSelectedRow()!= -1){                   
                        kdbidan2.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                        nmbidan2.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                        kdbidan2.requestFocus();
                    } 
                    pe tugas=null;  
                        
                                 
            });
                                 

            petugas.s
                    setLoca t ionRelativeTo(internalFrame1);
        }
            
        if (petugas == null) return;
        if (!petugas.isVisible()) {     
            petugas.isCek();    
         

        if (petugas.isVisibl
            ()) {
            petugas.toFront();
            return;
             
        p
        EN-LAST:event_btnBidan2ActionPerformed

    private void kdbidan2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kdbidan2KeyPressed
        i
            btnBidan2ActionPerfor
             }else{

            } 
         }//GEN-L
     
    private void kdbidan3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kdbidan3KeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_UP){ 
            btnBidan3Acti onPerformed (null);
         }else{
            Valid.pindah(evt,kdbidan2,kdprwluar);
        } 
    }//GEN-LAST:event_kdbidan3KeyPressed
   
    private void btnBidan3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBidan3ActionPerformed
        if (petugas == null || !petugas.isDisplayable()) {
            petugas=new DlgCariPetugas(null,false);
            petugas.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            petugas.ad dWindowListener(new WindowAdapter()  { 
                        e
                                 
                public void windowClosed(
                                WindowEvent e) { 
                    if(petugas.getTable().getSelectedRow()!= -1){                   
                     
                        nmb i dan3.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                        kdbidan3.requestFocus();
                    } 
                    petugas=null;
                }     
            });


            petugas.setLocat
            onRelativeTo(internalFrame1);
        }
            
            petugas == null) return;
        i
            petugas.isCek();    
            petugas.emptTeks();
        }  
        i
            petugas.toFront();
             return;
        }    
        petugas.setVisible(true); 
    }//GEN -LAST:event_btnBi da n3ActionPerform ed

    priva te v oid kdonloop1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kdonloop1KeyPressed
        Valid.pindah(evt,kdpr wresust, kdonloop2);
    }//GEN-LAST:event_kdonloop1KeyPressed
 
    private void btnOnloop1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOnloop1ActionPerformed
        if (petugas == null || !petugas.isDisplayable()) { 
             petugas=new Dlg Ca riPetugas(null, false);
            petugas.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
             p etugas.addWindowListener(new WindowAdapter() {
                @Override  
                public void windowClosed(WindowEvent e) {
                     if(petugas.getTable().getSelectedRow()!= -1){                   
                        kdonloop1.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                        nmonloop1.setText(petugas.getTable().getValueAt(petug as.getTable().getSelectedRow(),1).toString());
                        kdonloop1.requestFocus();
                      }  
                    petugas=null;
                }
            });

            petugas.se tSize(internalFrame1.getWidth()-20,i nterna l
                        ocationRelativeTo
                                (internalFrame1); 
        }
                                 
            
        if (petugas =
                    .isVisi b le()) {
            petugas.isCek();    
            petugas.emptTeks();
        }  
        if (petugas.isVisible()) {     
            petugas.toFront();
         

        petugas.setVisible(t
            ue);
    }//GEN-LAST:event_btnOnloop1ActionPerformed

            void btnOnloop2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOnloop2ActionPerformed
        i
            petugas=new DlgCariPetugas(null,false);
            petugas.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            petugas.addWindowListener(new WindowAdapter() {
         
                public void windowClosed(WindowEvent e) {
                     if(petugas.getTable().getSelectedRow()!= -1){                   
                        kdonloop2.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                        nmonloop2.setText(petugas.getTable().getValue At(petugas.getTable().getSelectedRow(),1).toString());
                        k donloop2.req uestFocus();
                     } 
                    petugas=null;
                } 
            });
   
            petugas.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
            petugas.setLocationRelativeTo(internalFrame1);
        }
            
        if (petugas ==  null) return;  
                        isible()) {
                                 
            petugas.isCek();    
                                 
            petugas.emptTeks();
        }  
                    isVisib l e()) {
            petugas.toFront();
            return;
        }    
        petugas.setVisible(true);     
    }//GEN-LAST:event_btnOnloop2ActionPerformed


        Valid.pindah(evt,kdo
            loop1,kdonloop3);
    }//GEN-LAST:event_kdonloop2KeyPressed

            void btnOnloop3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOnloop3ActionPerformed
        i
            petugas=new DlgCariPetugas(null,false);
            petugas.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            petugas.addWindowListener(new WindowAdapter() {
         
                public void windowClosed(WindowEvent e) {
                     if(petugas.getTable().getSelectedRow()!= -1){                   
                        kdonloop3.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                        nmonloop3.setText(petugas.getTable().getValueAt(petuga s.getTable().getSelectedRow(),1).toString());
                        kdonloop3.requestFocus();
                      }  
                    petugas=null;
                }
            });

            petugas.se tSize(internalFrame1.getWidth()-20,i nterna l
                        ocationRelativeTo(
                                internalFrame1); 
        }
                                 
            
        if (petugas =
                    .isVisi b le()) {
            petugas.isCek();    
            petugas.emptTeks();
        }  
        if (petugas.isVisible()) {     
            petugas.toFront();
         

        petugas.setVisible(t
            ue);
    }//GEN-LAST:event_btnOnloop3ActionPerformed

            void kdonloop3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kdonloop3KeyPressed
        V
        EN-LAST:event_kdonloop3KeyPressed

    private void kdpjanakKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kdpjanakKeyPressed
        i
            nmpjanak.setText(Sequel.CariDokter(kdpjanak.getText()));
         }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            btndrpjanakActionPerformed(null);
        }else{ 
            Valid.pindah( evt,kdonlo op3,kddrumum);
         }
    }//GEN-LAST:event_kdpjanakKeyPressed
 
    private void btndrpjanakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btndrpjanakActionPerformed
        if (dokter  = = null || !dokter.isDisp layable()) {
            dokter=new DlgCariDokter(null,false);
            dokter.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dokter.addWindowListener(new WindowAdapter() {
                @Override
                public  void windowClosed(WindowEvent e) {  
                        okter.getTable().g
                                etSelectedRow()!= -1){ 
                        kdpjanak.setText(d
                                okter.getTable().getValueAt(dokter.getTable().getSelectedRow(),0). toString());
                        nmpjanak.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),1).toString());
                     
                    }    
                    dokter=null;
                }
            });
            dokter.setSize(internalFrame1.getWidth()- 2 0,i nternalFrame1.getHeight()- 2 0);
            dokter.setLocationRelativeTo(internalFrame1);
        }

        if (dokter == null) 
            eturn;
        if (!dokter.isVisible()) {
            dokter.isCek(); 
            dokter.emptTeks();
        }
        if (dokter.isVisible()) {
            dokter.toFront();
            return;
        }
        dokter.setVisible(true);
    }// GEN-LAST:event_btndrpjanakActionPerformed

    private void kddrumumKeyPressed(java.awt.event.KeyEvent evt) {//G EN-FIRST:event_kddrumumKeyPressed
        if(evt.getKeyCode ()==KeyEve nt.VK_PAGE_DOWN){
             nmdranak.setText(Sequel.CariDokter(kddranak.getText()));
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            btndrumumActionPerformed(null); 
        }e lse{   
            Valid.pindah(evt,kdpjanak,BtnSimpan);
        }     
    }//GEN-LAST:event_kddrumumKeyPressed
  
    private void btndrumumAct ionPerform ed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btndrumumActionPerformed
        if (dokter == null || !dokter.isDisplayable()) {
             dokter=new DlgCariDokter(null,false);
            dokter.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dokter.addWindowListener(new WindowAdapter() { 
                @Override
                pu b lic void windowClosed(W indowEvent e) {
                    if(dokter.getTable().getSelectedRow()!= -1){
                        kddrumum.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),0).toString());
                        nmdrumum.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),1).toString());
                        kddrumum.requestFocus();
                    }     
                    dokter=null;
                                 
                }
                                 
            });
            dokter.se
                    etLoca t ionRelativeTo(internalFrame1);
        }
            
        if (dokter == null) return;     
        if (!dokter.isVisible()) {
         

        }  
            
        if (dokter.isVisible()) {
            dokter.toFront(
            return;
        }
        dokter.setVisible(true);
    }//GEN-LAST:event_btndrumumActionPerformed

    priva
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
             BtnSimpanActionPerformed(null);
        }else{
            Valid.pindah(evt,kddrumum,BtnKeluar); 
        }    
    }//GEN-LAST:event_BtnSimpanKeyPressed
     
    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        i f(kd operator2.getText().trim().equals("")||nmoperator2.getText().trim().equals("")){
            kdoperator2.setTe xt("-"); 
            nmoperator2.setText("-");
         }

        if(kdoperator3.getText().trim().equals("")||nmoperator3.getText().tri m().equals("")){
            kdoperator3.setText("-");
            nmoper a tor3.setText("-"); 
        }

        if(kdanestesi.getText().trim().equals("")||nmanestesi.getText().trim().equals("")){
            kdanestesi.setText("-");
            nmanestesi .setText("-");  
        }
                                 

                                 
        if(kddranak.getText().trim().equals("")||nmdranak.getText().trim().equals("")){
            kddranak.
                    .setTe x t("-");
        }

        if(kdbidan.getText().trim().equals("")||nmbi d an. getText().trim().equals("" ) ){
            kdbidan.setText("-");
         

        
            
        if(kdbidan2.getText().trim().equals("")||nmbidan2.getText().trim().equals("")){
            kdbidan2.setTex
            nmbidan2.setText("-");
        }
        
        if(kdbidan3.getText().trim().equals("")||nmbidan3.getText().trim().equals("")){
            kdbidan3.setText("-");
         
        }
 
        if(kdonloop1.getText().trim().equals("")||nmonloop1.getText().trim().equals("")){
            kdonloop1.setText("-"); 
             nmonloop1.setTe xt ("-"); 
        }
  
        if(kdonloop2.getText( ).trim(). equals("")||nmonloop2.getText().trim().equals("")){
            kdonloop2.setText("-");
             nmonloop2.setText("-");
        }

        if(kdonloop3.getText().trim().equals("")||nmonloop3.getText().trim().equals("")){
            kdonloop3.setText("-");
            nmonloop3.setText("-");
        }
        
        if(kdonloop4.getText().trim().equals("")||nmonloop4.getText().trim().equals("")){
            kdonloop4.setText("-");
            nmonloop4.setText("-");
        }
        
        if(kdonloop5.getText().trim().equals("")||nmonloop5.getText().trim().equals("")){
            kdonloop5.setText("-");
            nmonloop5.setText("-");
        }

        if(kdasistoperator1.getText().trim().equals("")||nmasistoperator1.getText().trim().equals("")){
            kdasistoperator1.setText("-");
            nmasistoperator1.setText("-");
        }

        if(kdasistoperator2.getText().trim().equals("")||nmasistoperator2.getText().trim().equals("")){
            kdasistoperator2.setText("-");
            nmasistoperator2.setText("-");
        }
        
        if(kdasistoperator3.getText().trim().equals("")||nmasistoperator3.getText().trim().equals("")){
            kdasistoperator3.setText("-");
            nmasistoperator3.setText("-");
        }

        if(kdInstrumen.getText().trim().equals("")||nminstrumen.getText().trim().equals("")){
            kdInstrumen.setText("-");
            nminstrumen.setText("-");
        }

        if(kdasistanestesi.getText().trim().equals("")||nmasistanestesi.getText().trim().equals("")){
            kdasistanestesi.setText("-");
            nmasistanestesi.setText("-");
        }
        
        if(kdasistanestesi2.getText().trim().equals("")||nmasistanestesi2.getText().trim().equals("")){
            kdasistanestesi2.setText("-");
            nmasistanestesi2.setText("-");
        }

        if(kdprwresust.getText().trim().equals("")||nmprwresust.getText().trim().equals("")){
            kdprwresust.setText("-");
            nmprwresust.setText("-");
        }

        if(kdprwluar.getText().trim().equals("")||nmprwluar.getText().trim().equals("")){
            kdprwluar.setText("-");
            nmprwluar.setText("-");
        }
        
        if(kdpjanak.getText().trim().equals("")||nmpjanak.getText().trim().equals("")){
            kdpjanak.setText("-");
            nmpjanak.setText("-");
        }
        
        if(kddrumum.getText().trim().equals("")||nmdrumum.getText().trim().equals("")){
            kddrumum.setText("-");
            nmdrumum.setText("-");
        }

        jml=0;
        for(i=0;i<tbtindakan.getRowCount();i++){
            if(tabMode.getValueAt(i,0).toString().equals("true")){
                jml++;
            }
        }
        
        if(TNoRw.getText().trim().equals("")||TPasien.getText().trim().equals("")){
            Valid.textKosong(TNoRw,"Pasien");
        }else if(jenis.getText().trim().equals("")){
            Valid.textKosong(jenis,"Jenis");
        }else if(kdoperator1.getText().trim().equals("")||nmoperator1.getText().trim().equals("")){
            Valid.textKosong(kdoperator1,"Operator 1");
        }else if(kdoperator2.getText().trim().equals("")||nmoperator2.getText().trim().equals("")){
            Valid.textKosong(kdoperator2,"Operator 2");
        }else if(kdoperator3.getText().trim().equals("")||nmoperator3.getText().trim().equals("")){
            Valid.textKosong(kdoperator3,"Operator 3");
        }else if(kdanestesi.getText().trim().equals("")||nmanestesi.getText().trim().equals("")){
            Valid.textKosong(kdanestesi,"dr Anestesi");
        }else if(kddranak.getText().trim().equals("")||nmdranak.getText().trim().equals("")){
            Valid.textKosong(kddranak,"dr Anak");
        }else if(kdbidan.getText().trim().equals("")||nmbidan.getText().trim().equals("")){
            Valid.textKosong(kdbidan,"Bidan 1");
        }else if(kdbidan2.getText().trim().equals("")||nmbidan2.getText().trim().equals("")){
            Valid.textKosong(kdbidan,"Bidan 2");
        }else if(kdbidan3.getText().trim().equals("")||nmbidan3.getText().trim().equals("")){
            Valid.textKosong(kdbidan,"Bidan 3");
        }else if(kdonloop1.getText().trim().equals("")||nmonloop1.getText().trim().equals("")){
            Valid.textKosong(kdonloop1,"Onloop 1");
        }else if(kdonloop2.getText().trim().equals("")||nmonloop2.getText().trim().equals("")){
            Valid.textKosong(kdonloop2,"Onloop 2");
        }else if(kdonloop3.getText().trim().equals("")||nmonloop3.getText().trim().equals("")){
            Valid.textKosong(kdonloop3,"Onloop 3");
        }else if(kdonloop4.getText().trim().equals("")||nmonloop4.getText().trim().equals("")){
            Valid.textKosong(kdonloop4,"Onloop 4");
        }else if(kdonloop5.getText().trim().equals("")||nmonloop5.getText().trim().equals("")){
            Valid.textKosong(kdonloop5,"Onloop 5");
        }else if(kdasistoperator1.getText().trim().equals("")||nmasistoperator1.getText().trim().equals("")){
            Valid.textKosong(kdasistoperator1,"Asisten Operator 1");
        }else if(kdasistoperator2.getText().trim().equals("")||nmasistoperator2.getText().trim().equals("")){
            Valid.textKosong(kdasistoperator2,"Asisten Operator 2");
        }else if(kdasistoperator3.getText().trim().equals("")||nmasistoperator3.getText().trim().equals("")){
            Valid.textKosong(kdasistoperator3,"Asisten Operator 3");
        }else if(kdInstrumen.getText().trim().equals("")||nminstrumen.getText().trim().equals("")){
            Valid.textKosong(kdInstrumen,"Instrumen");
        }else if(kdasistanestesi.getText().trim().equals("")||nmasistanestesi.getText().trim().equals("")){
            Valid.textKosong(kdasistanestesi,"Asisten Anastesi 1");
        }else if(kdasistanestesi2.getText().trim().equals("")||nmasistanestesi2.getText().trim().equals("")){
            Valid.textKosong(kdasistanestesi2,"Asisten Anastesi 2");
        }else if(kdprwresust.getText().trim().equals("")||nmprwresust.getText().trim().equals("")){
            Valid.textKosong(kdprwresust,"Perawat Resusitas");
        }else if(kdprwluar.getText().trim().equals("")||nmprwluar.getText().trim().equals("")){
            Valid.textKosong(kdprwluar,"Perawat Luar");
        }else if(kdpjanak.getText().trim().equals("")||nmpjanak.getText().trim().equals("")){
            Valid.textKosong(kdpjanak,"dr Pj Anak");
        }else if(kddrumum.getText().trim().equals("")||nmdrumum.getText().trim().equals("")){
            Valid.textKosong(kddrumum,"dr Umum");
        }else if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, pilihan operasi kosong...!!!!");
            TCari.requestFocus();
        }else if(jml==0){
            JOptionPane.showMessageDialog(null,"Maaf, silahkan pilih operasi...!!!!");
            TCari.requestFocus();
        }else{            
            if(Sequel.cariRegistrasi(TNoRw.getText())>0){
                JOptionPane.showMessageDialog(rootPane,"Data billing sudah terverifikasi, data tidak boleh dihapus.\nSilahkan hubungi bagian kasir/keuangan ..!!");
                TCariPaket.requestFocus();
            }else if(Sequel.cariInteger("SELECT COUNT(aa.tgl_perawatan) FROM "+(status.equals("Ralan")?"pemeriksaan_ralan":"pemeriksaan_ranap")+" aa JOIN dokter dr ON dr.kd_dokter = aa.nip "
                    + "WHERE aa.no_rawat = ? and aa.tgl_perawatan = '"+Valid.SetTgl(tgl.getSelectedItem()+"")+"' and "
                    + "aa.jam_rawat < '"+tgl.getSelectedItem().toString().substring(11,19)+"'  ", TNoRw.getText())==0 ){
                        JOptionPane.showMessageDialog(rootPane, "SOAP Dokter Pre operasi belum tersedia, Silahkan isi SOAP dokter dulu !!!");
                        dispose();                    
            }else{
                Map<String, String> reportMap = new HashMap<>();                
                try{    
                    psobat=koneksi.prepareStatement("SELECT * FROM laporan_operasi lo JOIN dokter dr ON dr.kd_dokter = lo.kd_dokter WHERE lo.no_rawat =?");
                    try{
                        psobat.setString(1,TNoRw.getText());
                        rs=psobat.executeQuery();
                        while(rs.next()){
                            //reportList.add(rs.getString("nm_perawatan"));
                            String kodePaket = rs.getString("tanggal").substring(0, 19);
                            String namaPerawatan = rs.getString("tanggal").substring(0, 19)+" "+rs.getString("nm_dokter");
                            reportMap.put(namaPerawatan, kodePaket);
                        }
                    }catch(SQLException e){
                        System.out.println(e);
                    }finally{
                        if(rs!=null){
                            rs.close();
                        }
                        if(psobat!=null){
                            psobat.close();
                        }
                    }
                }catch(SQLException e){
                    System.out.println("Notifikasi : "+e);
                }               
                
                List<String> reportLabels = new ArrayList<>(reportMap.keySet());
                String[] reportArray = reportLabels.toArray(new String[0]);
                
                if(reportArray.length>0){
                    String selectedLabel = (String) JOptionPane.showInputDialog(
                            null,
                            "Silahkan pilih Laporan Operasi..!",
                            "Laporan operasi",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            reportArray,
                            reportArray.length > 0 ? reportArray[0] : null // Default to the first report if available
                    );

                    if(selectedLabel != null){
                        String selectedValue = reportMap.get(selectedLabel);
                        //System.out.println("Selected report: " + selectedLabel + ", Value: " + selectedValue);
                        Valid.SetTgl2(tgl,selectedValue);
                    }else{
                        System.out.println("No report selected.");
                    }
                }
                
                Sequel.AutoComitFalse();
                sukses=true;
                ttljmdokter=0;ttljmpetugas=0;ttlpendapatan=0;ttlbhp=0;
                for(i=0;i<tbtindakan.getRowCount();i++){
                    if(tabMode.getValueAt(i,0).toString().equals("true")){
                        if(Sequel.menyimpantf2("operasi","'"+TNoRw.getText()+"','"+Valid.SetTgl(tgl.getSelectedItem()+"")+" "+tgl.getSelectedItem().toString().substring(11,19)
                            +"','"+jenis.getText()+"','"+Kategori.getSelectedItem()+"','"+kdoperator1.getText()+"','"+kdoperator2.getText()+"','"+kdoperator3.getText()
                            +"','"+kdasistoperator1.getText()+"','"+kdasistoperator2.getText()+"','"+kdasistoperator3.getText()+"','"+kdInstrumen.getText()
                            +"','"+kddranak.getText()+"','"+kdprwresust.getText()+"','"+kdanestesi.getText()+"','"+kdasistanestesi.getText()+"','"+kdasistanestesi2.getText()
                            +"','"+kdbidan.getText()+"','"+kdbidan2.getText()+"','"+kdbidan3.getText()+"','"+kdprwluar.getText()
                            +"','"+kdonloop1.getText()+"','"+kdonloop2.getText()+"','"+kdonloop3.getText()+"','"+kdonloop4.getText()+"','"+kdonloop5.getText()
                            +"','"+kdpjanak.getText()+"','"+kddrumum.getText()
                            +"','"+tbtindakan.getValueAt(i,1).toString()
                            +"','"+tbtindakan.getValueAt(i,4).toString()
                            +"','"+tbtindakan.getValueAt(i,5).toString()
                            +"','"+tbtindakan.getValueAt(i,6).toString()
                            +"','"+tbtindakan.getValueAt(i,7).toString()
                            +"','"+tbtindakan.getValueAt(i,8).toString()
                            +"','"+tbtindakan.getValueAt(i,9).toString()
                            +"','"+tbtindakan.getValueAt(i,10).toString()
                            +"','"+tbtindakan.getValueAt(i,11).toString()
                            +"','"+tbtindakan.getValueAt(i,12).toString()
                            +"','"+tbtindakan.getValueAt(i,13).toString()
                            +"','"+tbtindakan.getValueAt(i,14).toString()
                            +"','"+tbtindakan.getValueAt(i,15).toString()
                            +"','"+tbtindakan.getValueAt(i,16).toString()
                            +"','"+tbtindakan.getValueAt(i,17).toString()
                            +"','"+tbtindakan.getValueAt(i,18).toString()
                            +"','"+tbtindakan.getValueAt(i,19).toString()
                            +"','"+tbtindakan.getValueAt(i,20).toString()
                            +"','"+tbtindakan.getValueAt(i,21).toString()
                            +"','"+tbtindakan.getValueAt(i,22).toString()
                            +"','"+tbtindakan.getValueAt(i,23).toString()
                            +"','"+tbtindakan.getValueAt(i,24).toString()
                            +"','"+tbtindakan.getValueAt(i,25).toString()
                            +"','"+tbtindakan.getValueAt(i,26).toString()
                            +"','"+tbtindakan.getValueAt(i,27).toString()
                            +"','"+tbtindakan.getValueAt(i,28).toString()
                            +"','"+tbtindakan.getValueAt(i,29).toString()
                            +"','"+tbtindakan.getValueAt(i,30).toString()
                            +"','"+tbtindakan.getValueAt(i,31).toString()+"','"+status+"'","data")==true){
                            ttljmdokter=ttljmdokter+Double.parseDouble(tbtindakan.getValueAt(i,4).toString())+
                                    Double.parseDouble(tbtindakan.getValueAt(i,5).toString())+
                                    Double.parseDouble(tbtindakan.getValueAt(i,6).toString())+
                                    Double.parseDouble(tbtindakan.getValueAt(i,11).toString())+
                                    Double.parseDouble(tbtindakan.getValueAt(i,13).toString())+
                                    Double.parseDouble(tbtindakan.getValueAt(i,30).toString())+
                                    Double.parseDouble(tbtindakan.getValueAt(i,31).toString());
                            ttljmpetugas=ttljmpetugas+Double.parseDouble(tbtindakan.getValueAt(i,7).toString())+
                                    Double.parseDouble(tbtindakan.getValueAt(i,8).toString())+
                                    Double.parseDouble(tbtindakan.getValueAt(i,9).toString())+
                                    Double.parseDouble(tbtindakan.getValueAt(i,10).toString())+
                                    Double.parseDouble(tbtindakan.getValueAt(i,12).toString())+
                                    Double.parseDouble(tbtindakan.getValueAt(i,14).toString())+
                                    Double.parseDouble(tbtindakan.getValueAt(i,15).toString())+
                                    Double.parseDouble(tbtindakan.getValueAt(i,16).toString())+
                                    Double.parseDouble(tbtindakan.getValueAt(i,17).toString())+
                                    Double.parseDouble(tbtindakan.getValueAt(i,18).toString())+
                                    Double.parseDouble(tbtindakan.getValueAt(i,19).toString())+
                                    Double.parseDouble(tbtindakan.getValueAt(i,24).toString())+
                                    Double.parseDouble(tbtindakan.getValueAt(i,25).toString())+
                                    Double.parseDouble(tbtindakan.getValueAt(i,26).toString())+
                                    Double.parseDouble(tbtindakan.getValueAt(i,27).toString())+
                                    Double.parseDouble(tbtindakan.getValueAt(i,28).toString());
                            ttlpendapatan=ttlpendapatan+Double.parseDouble(tbtindakan.getValueAt(i,32).toString()); 
                        }else{
                            sukses=false;
                        }
                    }
                }
                
                if(sukses==true){
                    for(int r=0;r<tbObat.getRowCount();r++){
                        if(Valid.SetAngka(tbObat.getValueAt(r,0).toString())>0){
                            if(Sequel.menyimpantf2("beri_obat_operasi","'"+TNoRw.getText()+"','"+Valid.SetTgl(tgl.getSelectedItem()+"")+" "+tgl.getSelectedItem().toString().substring(11,19)+
                                "','"+tbObat.getValueAt(r,1).toString()+"','"+tbObat.getValueAt(r,4).toString()+
                                "','"+tbObat.getValueAt(r,0).toString()+"'","data")==true){
                                ttlbhp=ttlbhp+Double.parseDouble(tbObat.getValueAt(r,5).toString());
                            }else{
                                sukses=false;
                            }
                        }
                    }
                    ttlpendapatan=ttlpendapatan+ttlbhp;
                }
                    
//                if(sukses==true){
//                    if(!Laporan.getText().equals("")){
//                        if(Sequel.menyimpantf2("laporan_operasi","?,?,?,?,?,?,?,?","laporan operasi",8,new String[]{
//                                TNoRw.getText(),
//                                Valid.SetTgl(tgl.getSelectedItem()+"")+" "+tgl.getSelectedItem().toString().substring(11,19),
//                                PreOp.getText(),
//                                PostOp.getText(),
//                                Jaringan.getText(),
//                                Valid.SetTgl(tgl2.getSelectedItem()+"")+" "+tgl2.getSelectedItem().toString().substring(11,19),
//                                DikirimPA.getSelectedItem().toString(),
//                                Laporan.getText()
//                            })==false){
//                            sukses=false;
//                        }
//                    }
//                }   
                
                if(sukses==true){
                    if(status.equals("Ranap")){
                        Sequel.queryu("delete from tampjurnal");    
                        if(ttlpendapatan>0){
                            if(Sequel.menyimpantf("tampjurnal","'"+akuntindakanoperasi.getSuspen_Piutang_Operasi_Ranap()+"','Suspen Piutang Operasi Ranap','"+ttlpendapatan+"','0'","debet=debet+'"+(ttlpendapatan)+"'","kd_rek='"+akuntindakanoperasi.getSuspen_Piutang_Operasi_Ranap()+"'")==false){
                                sukses=false;
                            }    
                            if(Sequel.menyimpantf("tampjurnal","'"+akuntindakanoperasi.getOperasi_Ranap()+"','Pendapatan Operasi Rawat Inap','0','"+ttlpendapatan+"'","kredit=kredit+'"+(ttlpendapatan)+"'","kd_rek='"+akuntindakanoperasi.getOperasi_Ranap()+"'")==false){
                                sukses=false;
                            }                                
                        }
                        if(ttljmdokter>0){
                            if(Sequel.menyimpantf("tampjurnal","'"+akuntindakanoperasi.getBeban_Jasa_Medik_Dokter_Operasi_Ranap()+"','Beban Jasa Medik Dokter Operasi Ranap','"+ttljmdokter+"','0'","debet=debet+'"+(ttljmdokter)+"'","kd_rek='"+akuntindakanoperasi.getBeban_Jasa_Medik_Dokter_Operasi_Ranap()+"'")==false){
                                sukses=false;
                            }  
                            if(Sequel.menyimpantf("tampjurnal","'"+akuntindakanoperasi.getUtang_Jasa_Medik_Dokter_Operasi_Ranap()+"','Utang Jasa Medik Dokter Operasi Ranap','0','"+ttljmdokter+"'","kredit=kredit+'"+(ttljmdokter)+"'","kd_rek='"+akuntindakanoperasi.getUtang_Jasa_Medik_Dokter_Operasi_Ranap()+"'")==false){
                                sukses=false;
                            }                              
                        }
                        if(ttljmpetugas>0){
                            if(Sequel.menyimpantf("tampjurnal","'"+akuntindakanoperasi.getBeban_Jasa_Medik_Paramedis_Operasi_Ranap()+"','Beban Jasa Medik Petugas Operasi Ranap','"+ttljmpetugas+"','0'","debet=debet+'"+(ttljmpetugas)+"'","kd_rek='"+akuntindakanoperasi.getBeban_Jasa_Medik_Paramedis_Operasi_Ranap()+"'")==false){
                                sukses=false;
                            }   
                            if(Sequel.menyimpantf("tampjurnal","'"+akuntindakanoperasi.getUtang_Jasa_Medik_Paramedis_Operasi_Ranap()+"','Utang Jasa Medik Petugas Operasi Ranap','0','"+ttljmpetugas+"'","kredit=kredit+'"+(ttljmpetugas)+"'","kd_rek='"+akuntindakanoperasi.getUtang_Jasa_Medik_Paramedis_Operasi_Ranap()+"'")==false){
                                sukses=false;
                            }                                 
                        }
                        if(ttlbhp>0){
                            if(Sequel.menyimpantf("tampjurnal","'"+akuntindakanoperasi.getHPP_Obat_Operasi_Ranap()+"','HPP Persediaan Operasi Rawat Inap','"+ttlbhp+"','0'","debet=debet+'"+(ttlbhp)+"'","kd_rek='"+akuntindakanoperasi.getHPP_Obat_Operasi_Ranap()+"'")==false){
                                sukses=false;
                            }     
                            if(Sequel.menyimpantf("tampjurnal","'"+akuntindakanoperasi.getPersediaan_Obat_Kamar_Operasi_Ranap()+"','Persediaan BHP Operasi Rawat Inap','0','"+ttlbhp+"'","kredit=kredit+'"+(ttlbhp)+"'","kd_rek='"+akuntindakanoperasi.getPersediaan_Obat_Kamar_Operasi_Ranap()+"'")==false){
                                sukses=false;
                            }                                
                        }
                        if(sukses==true){
                            sukses=jur.simpanJurnal(TNoRw.getText(),"U","OPERASI RAWAT INAP PASIEN "+TPasien.getText()+" DIPOSTING OLEH "+akses.getkode()); 
                        }                                             
                    }else if(status.equals("Ralan")){
                        Sequel.queryu("delete from tampjurnal");    
                        if(ttlpendapatan>0){
                            if(Sequel.menyimpantf("tampjurnal","'"+akuntindakanoperasi.getSuspen_Piutang_Operasi_Ralan()+"','Suspen Piutang Operasi Ralan','"+ttlpendapatan+"','0'","debet=debet+'"+(ttlpendapatan)+"'","kd_rek='"+akuntindakanoperasi.getSuspen_Piutang_Operasi_Ralan()+"'")==false){
                                sukses=false;
                            }    
                            if(Sequel.menyimpantf("tampjurnal","'"+akuntindakanoperasi.getOperasi_Ralan()+"','Pendapatan Operasi Rawat Inap','0','"+ttlpendapatan+"'","kredit=kredit+'"+(ttlpendapatan)+"'","kd_rek='"+akuntindakanoperasi.getOperasi_Ralan()+"'")==false){
                                sukses=false;
                            }                                
                        }
                        if(ttljmdokter>0){
                            if(Sequel.menyimpantf("tampjurnal","'"+akuntindakanoperasi.getBeban_Jasa_Medik_Dokter_Operasi_Ralan()+"','Beban Jasa Medik Dokter Operasi Ralan','"+ttljmdokter+"','0'","debet=debet+'"+(ttljmdokter)+"'","kd_rek='"+akuntindakanoperasi.getBeban_Jasa_Medik_Dokter_Operasi_Ralan()+"'")==false){
                                sukses=false;
                            }  
                            if(Sequel.menyimpantf("tampjurnal","'"+akuntindakanoperasi.getUtang_Jasa_Medik_Dokter_Operasi_Ralan()+"','Utang Jasa Medik Dokter Operasi Ralan','0','"+ttljmdokter+"'","kredit=kredit+'"+(ttljmdokter)+"'","kd_rek='"+akuntindakanoperasi.getUtang_Jasa_Medik_Dokter_Operasi_Ralan()+"'")==false){
                                sukses=false;
                            }                              
                        }
                        if(ttljmpetugas>0){
                            if(Sequel.menyimpantf("tampjurnal","'"+akuntindakanoperasi.getBeban_Jasa_Medik_Paramedis_Operasi_Ralan()+"','Beban Jasa Medik Petugas Operasi Ralan','"+ttljmpetugas+"','0'","debet=debet+'"+(ttljmpetugas)+"'","kd_rek='"+akuntindakanoperasi.getBeban_Jasa_Medik_Paramedis_Operasi_Ralan()+"'")==false){
                                sukses=false;
                            }   
                            if(Sequel.menyimpantf("tampjurnal","'"+akuntindakanoperasi.getUtang_Jasa_Medik_Paramedis_Operasi_Ralan()+"','Utang Jasa Medik Petugas Operasi Ralan','0','"+ttljmpetugas+"'","kredit=kredit+'"+(ttljmpetugas)+"'","kd_rek='"+akuntindakanoperasi.getUtang_Jasa_Medik_Paramedis_Operasi_Ralan()+"'")==false){
                                sukses=false;
                            }                                 
                        }
                        if(ttlbhp>0){
                            if(Sequel.menyimpantf("tampjurnal","'"+akuntindakanoperasi.getHPP_Obat_Operasi_Ralan()+"','HPP Persediaan Operasi Rawat Jalan','"+ttlbhp+"','0'","debet=debet+'"+(ttlbhp)+"'","kd_rek='"+akuntindakanoperasi.getHPP_Obat_Operasi_Ralan()+"'")==false){
                                sukses=false;
                            }     
                            if(Sequel.menyimpantf("tampjurnal","'"+akuntindakanoperasi.getPersediaan_Obat_Kamar_Operasi_Ralan()+"','Persediaan BHP Operasi Rawat Jalan','0','"+ttlbhp+"'","kredit=kredit+'"+(ttlbhp)+"'","kd_rek='"+akuntindakanoperasi.getPersediaan_Obat_Kamar_Operasi_Ralan()+"'")==false){
                                sukses=false;
                            }                                
                        }
                        if(sukses==true){
                            sukses=jur.simpanJurnal(TNoRw.getText(),"U","OPERASI RAWAT JALAN PASIEN "+TPasien.getText()+" DIPOSTING OLEH "+akses.getkode()); 
                        }                                             
                    }
                }
                    
                if(sukses==true){
                    Sequel.Commit();
                    for(int r=0;r<tbtindakan.getRowCount();r++){
                        tbtindakan.setValueAt(false,r,0);
                    }
                    runBackground(() ->tampil());
                    for(int r=0;r<tbObat.getRowCount();r++){
                        tbObat.setValueAt("",r,0);
                    }
                    runBackground(() ->tampil2());
                    LTotal.setText("Total Biaya : 0");
//                    PreOp.setText("");
//                    PostOp.setText("");
//                    Jaringan.setText("");
//                    Laporan.setText("");
//                    jenis.setText("");
                    JOptionPane.showMessageDialog(rootPane,"Proses simpan selesai...!");
                }else{
                    JOptionPane.showMessageDialog(null,"Terjadi kesalahan saat pemrosesan data, transaksi dibatalkan.\nPeriksa kembali data sebelum melanjutkan menyimpan..!!");
                    Sequel.RollBack();
                }
                Sequel.AutoComitTrue();
            }
        }
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void kdasistoperator3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kdasistoperator3KeyPressed
        Valid.pindah(evt,kdasistoperator2,kdasistanestesi);
    }//GEN-LAST:event_kdasistoperator3KeyPressed

    private void btnAsis4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAsis4ActionPerformed
        if (petugas == null || !petugas.isDisplayable()) {
            petugas=new DlgCariPetugas(null,false);
            petugas.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            petugas.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    if(petugas.getTable().getSelectedRow()!= -1){                   
                        kdasistoperator3.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                        nmasistoperator3.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                        kdasistoperator3.requestFocus();
                    } 
                    petugas=null;
                }
            });

            petugas.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
            petugas.setLocationRelativeTo(internalFrame1);
        }
            
        if (petugas == null) return;
        if (!petugas.isVisible()) {
            petugas.isCek();    
            petugas.emptTeks();
        }  
        if (petugas.isVisible()) {
            petugas.toFront();
            return;
        }    
        petugas.setVisible(true);
    }//GEN-LAST:event_btnAsis4ActionPerformed

    private void kdasistanestesi2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kdasistanestesi2KeyPressed
        Valid.pindah(evt,kdasistanestesi,kdprwresust);
    }//GEN-LAST:event_kdasistanestesi2KeyPressed

    private void BtnAsnes1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAsnes1ActionPerformed
        if (petugas == null || !petugas.isDisplayable()) {
            petugas=new DlgCariPetugas(null,false);
            petugas.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            petugas.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    if(petugas.getTable().getSelectedRow()!= -1){                   
                        kdasistanestesi2.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                        nmasistanestesi2.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                        kdasistanestesi2.requestFocus();
                    } 
                    petugas=null;
                }
            });

            petugas.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
             petugas.setLocationRelativeTo(internalFrame1);
        }
             
        if (petugas == nu ll) return; 
         if (!petugas.isVisible()) {
            petugas.isCek();    
            petugas.emptTeks(); 
        }  
        if (petugas . isVisible()) { 
            petugas.toFront();
            return;
        }    
        petugas.setVisible(true);
    }//GEN-LAST:event_ BtnAsnes1ActionPerformed  
                        
                                 
    private void kdonloop4KeyPressed(java.awt.eve
                                nt.KeyEvent evt) {//GEN-FIRST:event_kdonloop4KeyPressed 
        Valid.pindah(evt,kdonloop3,kdonloop5);
    }//GEN-LAST:event
                      
    private void btnOnloop4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOnloop4ActionPerformed
        if (petugas == null || !petugas.isDisplayable()) {
            petugas=new DlgCariPetugas(null,false);
            petugas.setDefaultCloseOperation(WindowCo n sta nts.DISPOSE_ON_CLOSE);  
            petugas.addWindowListener(new WindowAdapter() {
         

                    if(petug
            s.getTable().getSelectedRow()!= -1){                   
                        kdonloop4.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                        nmon
                        kdonloop4.requestFocus();
         
                    petugas=null;
                }
            });

            petugas.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
             petugas.setLocationRelativeTo(internalFrame1);
        }
             
        if (petugas == nu ll) return; 
         if (!petugas.isVisible()) {
            petugas.isCek();    
            petugas.emptTeks(); 
        }  
        if (petugas . isVisible()) { 
            petugas.toFront();
            return;
        }    
        petugas.setVisible(true);
    }//GEN-LAST:event_ btnOnloop4ActionPerformed  
                        
                                 
    private void btnOnloop5ActionPerformed(java.a
                                wt.event.ActionEvent evt) {//GEN-FIRST:event_btnOnloop5ActionPerfo rmed
        if (petugas == null || !petugas.isDisplayable()) {
            petugas=n
                    setDefa u ltCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            petugas.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    if(petugas.getTable().getSelected R ow( )!= -1){                     
                        kdonloop5.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
         

                    } 
            
                    petugas=null;
                }
            });

            petugas.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
            petugas.setLocationRelativeTo(internalFrame1);
        }
         
        if (petugas == null) return;
         if (!petugas.isVisible()) {
            petugas.isCek();    
            petugas.emptTeks(); 
        }    
         if (petugas.isVisible()) {
            petugas.toFront();
            return; 
        }    
        petugas.set V isible(true); 
    }//GEN-LAST:event_btnOnloop5ActionPerformed

    private void kdonloop5KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kdonloop5KeyPressed
        //Valid.pindah(evt,kdonloop4,tgl2);
    }//GEN-LAST:event_ kdonloop5KeyPressed  
                        
                                 
    private void tgl2KeyPressed(java.awt.e
                                vent.KeyEvent evt) {//GEN-FIRST:event_tgl2KeyPressed 
        Valid.pindah(evt,kdonloop5,PreOp);
    }//GEN-LAST:event
                      
    private void PreOpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PreOpKeyPressed
        Valid.pindah(evt,tgl2,PostOp);
    }//GEN-LAST:event_PreOpKeyPressed
     
    private void PostOpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PostOpKeyPressed
        V

        
            
    private void JaringanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_JaringanKeyPressed
        Valid.pindah(evt,Pos
            AST:event_JaringanKeyPressed

        ate void DikirimPAKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DikirimPAKeyPressed
        Valid.pindah(evt,Jaringan,NomorImplant);
    }//GEN-LAST:event_DikirimPAKeyPressed

        ate void BtnOperator1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnOperator1KeyPressed
         Valid.pindah(evt,tgl,BtnOperator2);
    }//GEN-LAST:event_BtnOperator1KeyPressed
 
    private void BtnOperator2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnOperator2KeyPressed
        Valid.pinda h (evt,BtnOperator1,btnOpe rator3);
    }//GEN-LAST:event_BtnOperator2KeyPressed

    private void btnOperator3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnOperator3KeyPressed
        Valid.pindah(evt,BtnOperator2,BtnAnastesi);
    }//GEN-LAST:event_ btnOperator3KeyPressed  
                        
                                 
    private void BtnEditActionPerformed(ja
                                va.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerf ormed
        if(kdoperator2.getText().trim().equals("")||nmoperator2.getText().trim().equals("")){
            kdoperato
                    or2.set T ext("-");
        }

        if(kdoperator3.getText().trim().equals("")||nmoperator3.getText().trim().equals("")){
            kdoperator3.setText("-");     
            nmoperator3.setText("-");
        }

        if(kdanestesi.getTex
            ().trim().equals("")||nmanestesi.getText().trim().equals("")){
            kdanestesi.setText("-");
            nmanestesi.setTe
            

        if(kddranak.getText().trim().equals("")||nmdranak.getText().trim().equals("")){
            kddranak.setText("-");
            nmdranak.setText("-");
        }
        
         if(kdbidan.getText().trim().equals("")||nmbidan.getText().trim().equals("")){
            kdbidan.setText("-");
            nmbidan.setText("-"); 
        } 
 

                      

        } 
  
         if(kdbidan3.getText().trim().equals("")||nmbidan3.getText().trim().equals("")){
            kdbidan3.setText("-");
            nmbidan3.setText("-"); 
        }  
 
        if(kdonloop1.getText().trim().equals("")||nmonloop1.getText().trim().equals("")){
            kdonloop1.setText("-"); 
            nmonloop1.set Text(" -");
         }

        if(kdonloop2.getText().trim().equals("")||nmonloop2.getText( ).trim().equals("")){
            kdonloop2.set Text("- ");
             nmonloop2.setText("-");
        }

        if(kdonloop3.getText().trim().equals("")||nmonloop3.getText().trim().equals("")){
             kdonloop3.setText("-");

           }

         
        if(kdonloop4.getT ext( ).trim().equals("")||nmonloop4.getText().trim().equals("")){
             kdonloop4.setText("-");
            nmonloop4.setText("-");
        } 
          
         if(kdonloop5.getText().trim().equals("")||nmonloop5.getText().trim().equals("")){
            kdonloop5.setText("-");
            nmonloop5.setText("-"); 
        }  
 

               

            nmasistoperator1.setText("-"); 
        }    

        if(kdasistoperator2.getText().trim().equals("")||nmasistoperator2.getText().trim().equals("")){
            kdasistoperator2.setText("-");
            nmasistoperator2.setText("-");
        }    
        
        if(kdasistoperator3.getText().trim().equals("")||nmasistoperator3.getText().trim().equals("")){
            kdasistoperator3.setText("-");
            nmasistoperator3.setText("-");
        }    

        if(kdInstrumen.getText().trim().equals("")||nminstrumen.getText().trim().equals("")){
            kdInstrumen.setText("-");
            nminstrumen.setText("-");
        }    

        if(kdasistanestesi.getText().trim().equals("")||nmasistanestesi.getText().trim().equals("")){
            kdasistanestesi.setText("-");
            nmasistanestesi.setText("-");
        }    
        
        if(kdasistanestesi2.getText().trim().equals("")||nmasistanestesi2.getText().trim().equals("")){
            kdasistanestesi2.setText("-");
            nmasistanestesi2.setText("-");
        }    

        if(kdprwresust.getText().trim().equals("")||nmprwresust.getText().trim().equals("")){
            kdprwresust.setText("-");
            nmprwresust.setText("-");
        }    

        if(kdprwluar.getText().trim().equals("")||nmprwluar.getText().trim().equals("")){
            kdprwluar.setText("-");
            nmprwluar.setText("-");
        }    
        
        if(kdpjanak.getText().trim().equals("")||nmpjanak.getText().trim().equals("")){
            kdpjanak.setText("-");
            nmpjanak.setText("-");
        }    
        
        if(kddrumum.getText().trim().equals("")||nmdrumum.getText().trim().equals("")){
            kddrumum.setText("-");
            nmdrumum.setText("-");
        }    

        jml=0;
        f

                 jml++;   
            }
        }
        

             Valid.textKosong(TNoRw,"Pasien");   
        }else if(jenis.getText().trim().equals("")){
            Valid.textKosong(jenis,"Jenis");
        }else if(kdoperator1.getText().trim().equals("")||nmoperator1.getText().trim().equals("")){
            Valid.textKosong(kdoperator1,"Operator 1");
        }e lse if(kdoperator2.getText().trim().equals("" )| |nmoperator2.getText().trim().equals("")){ 
            Valid.textKosong(kdoperator2,"Operator 2");
        }else if(kdoperator3.getText().trim().equals("")||nmoperator3.getText().trim().equals("")){
            Valid.textKosong(kdoperator3,"Operator 3");
        }else if(kdanestesi.getText().trim().equals("")||nmanestesi.getText().trim().equals("")){
             Valid.textKosong(kdanestesi,"dr Anestesi");   
        }else if(kddranak.getText().trim().equals("")||nmdranak.getText().trim().equals("")){
            Valid.textKosong(kddranak,"dr Anak");
        }

        }e lse if(kdbidan2.getText().trim().equals("")|| nm bidan2.getText().trim().equals("")){ 
            Valid.textKosong(kdbidan,"Bidan 2");
        }else if(kdbidan3.getText().trim().equals("")||nmbidan3.getText().trim().equals("")){
            Valid.textKosong(kdbidan,"Bidan 3");
        }else if(kdonloop1.getText().trim().equals("")||nmonloop1.getText().trim().equals("")){
             Valid.textKosong(kdonloop1,"Onloop 1") ;  
        }else if(kdonloop2.getText().trim().equals("")||nmonloop2.getText().trim().equals("")){
            Valid.textKosong(kdonloop2,"Onloop 2");
        }else if(kdonloop3.getText().trim().equals("")||nmonloop3.getText().trim().equals("")){
            Valid.textKosong(kdonloop3,"Onloop 3");
        }e lse if(kdonloop4.getText().trim().equals("") || nmonloop4.getText().trim().equals("")){ 
            Valid.textKosong(kdonloop4,"Onloop 4");
        }else if(kdonloop5.getText().trim().equals("")||nmonloop5.getText().trim().equals("")){
         

             Valid.textKosong(kdasistoperator1,"Asisten  Op erator 1"); 
        }else if(kdasistoperator2.getText().trim().equals("")||nmasistoperator2.getText().trim().equals("")){
            Valid.textKosong(kdasistoperator2,"Asisten Operator 2");
        }else if(kdasistoperator3.getText().trim().equals("")||nmasistoperator3.getText().trim().equals("")){
            Valid.textKosong(kdasistoperator3,"Asisten Operator 3");
        }e lse if(kdInstrumen.getText().trim().equa ls ("")||nminstrumen.getText().trim().equal s("")){
            Valid.textKosong(kdInstrumen,"Instrumen");
        }else if(kdasistanestesi.getText().trim().equals("")||nmasistanestesi.getText().trim().equals("")){
            Valid.textKosong(kdasistanestesi,"Asisten Anastesi 1");
        }else if(kdasistanestesi2.getText().trim().equals("")||nmasistanestesi2.getText().trim().equals("")){
             Valid.textKosong(kdasistanestesi2,"A si sten Anastesi 2"); 
        }else if(kdprwresust.getText().trim().equals("")||nmprwresust.getText().trim().equals("")){
            Valid.textKosong(kdprwresust,"Perawat Resusitas");
        }

        }e lse if(kdpjanak.getText().trim().equa ls ("")||nmpjanak.getText().trim().equal s("")){
            Valid.textKosong(kdpjanak,"dr Pj Anak");
        }else if(kddrumum.getText().trim().equals("")||nmdrumum.getText().trim().equals("")){
         

             JOptionPane.showMessageDialog(null, "M aaf, pilihan operasi kosong...!!!!"); 
            TCari.requestFocus();
        }else if(jml==0){
            JOptionPane.showMessageDialog(null,"Maaf, silahkan pilih operasi...!!!!");
            TCari.requestFocus();
        }el s e{            
             i f (S e q uel.cariRegistrasi(TNoRw. getT ext())>0){
                 JOptionPane.showMess ageDialog(rootPane,"Data bill ing sudah terverifikasi, data tidak boleh dihapus.\nSilahkan hubungi bagian kasir/keuangan ..!!");
                TCariPaket.requestFocus();
            }else{
         

        }    
    }//GEN-LAST:event_BtnEditAction Performed
   
    private void BtnEditKeyPressed( java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEditKeyPressed
        / / TODO  add your handling code here:   
    }//GEN-LAST:event_BtnEditKeyPressed 
    priva te void  btnTemplateActionPerformed(java.awt.eve nt .ActionEvent evt) {//GEN-FIRST:event_btn TemplateActionPerformed
        MasterCariTemplateLaporanOperasi  template=new MasterCariTemplateLaporanOperasi(null,false);
        t emplate .addWindowListener(new WindowListener()  {  
            @Override 
             publ ic void windowOpened(WindowEvent e) {}   
            @Override 
             publ ic void windowClosing(WindowEvent e)  {}  
            @Override 
             publ ic void windowClosed(WindowEvent e)  {  
                if(template.getTable( ).getSelectedRow()!= -1){  
                      PreOp.setText(template.getTable() .g etValueAt(template.getTable().getSele ctedRow(),2).toString());
                    PostOp.setText(te mplate.getTable().getValueAt(template.getTable().getSelectedRow(),3).toString());
                      Jaringan.setText(template.getTabl e( ).getValueAt(template.getTable().getS electedRow(),4).toString());
                    DikirimPA.setSele ctedItem(template.getTable().getValueAt(template.getTable().getSelectedRow(),5).toString());
                      Laporan.setText(template.getTable( ). getValueAt(template.getTable().getSele ctedRow(),6).toString());
                    Laporan.requestFocu s();
                  }               
            } 
             @Ove rride   
            public void windowIconified (WindowEvent e) {}
             @Ove rride   
            public void windowDeiconifi ed(WindowEvent e) {}
             @Ove rride   
            public void windowActivated (WindowEvent e) {}
             @Ove rride   
            public void windowDeactivated(Wind owEvent e) {}
        } );    
        template.emptTeks(); 
        t emplate .isCek();   
        template.setSize(internalFrame1.getWid th()-20,internalFrame1.getHeight()-20);
        t emplate .setLocationRelativeTo(internalFrame1);   
        template.setVisible(true); 
    }//GE N-LAST: event_btnTemplateActionPerformed   
 
    priva te void  NomorImplantKeyPressed(java.awt.event.KeyEve nt  evt) {//GEN-FIRST:event_NomorImplantKeyPress ed
        Valid.pindah(evt,DikirimPA,Laporan); 
    }//GE N-LAST: event_NomorImplantKeyPressed   
 
    priva te void  formWindowOpened(java.awt.event.Windo wE vent evt) {//GEN-FIRST:event_formWindo wOpened
        if(akuntindakanoperasi.getSuspe n_Piutang_Operasi_Ralan().equals("")||akuntindakanoperasi.getSuspen_Piutang_Operasi_Ranap().equals("")){
             akun tindakanoperasi.SetAkunTindakanOperas i( ); 
        } 
        t ry {    
            psset_tarif=koneksi.prepar eStatement("select set_tarif.cara_bayar_operasi,set_tarif.kelas_operasi from set_tarif");
             try  {   
                rsset_tarif=psset_tarif.execute Query();
                if(rsset_tarif.next()){
                       ca ra _bayar_operasi=rsset_tarif.getString("cara_bayar_operasi");
                    kelas_operasi=rsset_tarif.g etString("kelas_operasi");
                }else{
                
                     kelas_operasi="Yes";   
                }  
                        
            } catch (Exception e) {
                 S ystem.out.println("Notifikasi : "+e);
            }finally{
                if(rsset_tarif != null){
                    rsset_tarif.close();
                 }
                if(psset_tarif != null){
                    psset_tarif.close(); 
                }
             }
           

            System.out.println("Notifikasi : "+e);
        } 
        
        if(koneksiDB.CARICEPAT().equals("aktif")){
            TCariPaket.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if(TCariPaket.getText().length()>2){
                        runBackground(() ->tampil());
                    }
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    if(TCariPaket.getText().length()>2){
                        runBackground(() ->tampil());
                    }
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    if(TCariPaket.getText().length()>2){
                        runBackground(() ->tampil());
                    }
                }
            });
            
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        runBackground(() ->tampil2());
                    }
                }
                 @Override
                   public void removeUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        runBackground(() ->tampil2()); 
                    }  
                 }
                @Override
                public void changedUpdate(DocumentEvent e) { 
                     if(TCari.getText().length()>2){
                  
                        runBackground(() ->tampil2());
                    }
                }
            });  
                    
        }  
    }//GEN-LAST:event_formW i ndowOpened
  
    /**  
    * @param args the command lin e  arguments
    */  
    public static void main(String arg s []) {
        java.awt.EventQueue.invok e Later(() -> {
            DlgTa
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override  
                 publ ic void windowClosing(java.awt.event.WindowEvent e) {
                     System.exit(0); 
                }
            });
            dialog .setVisible(true); 
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.Button BtnAll;  
    priva

        at e widget.Button BtnAsnes; 
    private widget.Button BtnAsnes1; 
    private widget.Button BtnCari;
    private widget.Button BtnCari1;
    private widget.But ton BtnCari2;   
    private widget.Button BtnEdit; 
    private widget.Button BtnKeluar;
    private widge

                et.Button BtnOperator2;
    private widget.Button BtnSimpan;
    private widget.But ton BtnTambah;   
    private widget.Button BtnTambahOperasi; 
    private widget.CekBox ChkInput;
    private widge

                et.ComboBox Kategori;
    private widget.TextBox Kd2;
    private widget.Lab el LTotal;   
    private widget.TextBox NomorImplant; 
    private javax.swing.JPanel PanelInput;
    private javax.swing.JPopupMenu Popup;
    private wid

            widget.TextBox TCari; 
    private widget.TextBox TCariPaket;
    private widget.TextBox TNoRw;
    private widget.Tex tBox TPasien;   
    private widget.Button btnAnak; 
    private widget.Button btnAsis1;
    private widge

                et.Button btnAsis3;
    private widget.Button btnAsis4;
    private widget.But ton btnBidan;   
    private widget.Button btnBidan2; 
    private widget.Button btnBidan3;
    private widge

                et.Button btnOnloop2;
    private widget.Button btnOnloop3;
    private widget.But ton btnOnloop4;   
    private widget.Button btnOnloop5; 
    private widget.Button btnOperator3;
    private widget.Button btnPrwLuar;
    private widget.Button btnPrwRes;
    priva
    pri vate widget.Button btndrumum;
    private widget.InternalFrame internalFrame1;
    pri
     private widget.Label jLabel3;
     private widget.Label jLabel4;
    private widget.Label jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private widget.TextBox jenis;
    private widget.TextBox kdInstrumen;
    private widget.TextBox kdanestesi;
    private widget.TextBox kdasistanestesi;
    private widget.TextBox kdasistanestesi2;
    private widget.TextBox kdasistoperator1;
    private widget.TextBox kdasistoperator2;
    private widget.TextBox kdasistoperator3;
    private widget.TextBox kdbidan;
    private widget.TextBox kdbidan2;
    private widget.TextBox kdbidan3;
    private widget.TextBox kddranak;
    private widget.TextBox kddrumum;
    private widget.TextBox kdonloop1;
    private widget.TextBox kdonloop2;
    private widget.TextBox kdonloop3;
    private widget.TextBox kdonloop4;
    private widget.TextBox kdonloop5;
    private widget.TextBox kdoperator1;
    private widget.TextBox kdoperator2;
    private widget.TextBox kdoperator3;
    private widget.TextBox kdpjanak;
    private widget.TextBox kdprwluar;
    private widget.TextBox kdprwresust;
    private widget.Label label10;
    private widget.Label label11;
    private widget.Label label14;
    private widget.Label label17;
    private widget.Label label18;
    private widget.Label label19    priate     private widget.Label label21;
    private widget.Label label22;
    private widget.Label label23;    priate widget.Label label24;
    private widget.Label label25;
    private widget.Label label26;
    private widget.Label label27;
    private widget.Label label28;
    private widget.Label label29;
    private widget.Label label30;
    private widget.Label label31;
    private widget.Label label32;
    private widget.Label label33;
    private widget.Label label34;
    private widget.Label label35;
    private widget.Label label36;
    private widget.Label label37;
    private widget.Label label38;
    private widget.Label label9;
    private widget.TextBox nmanestesi;
    private widget.TextBox nmasistanestesi;
    private widget.TextBox nmasistanestesi2;
    private widget.TextBox nmasistoperator1;
    private widget.TextBox nmasistoperator2;
    private widget.TextBox nmasistoperator3;
    private widget.TextBox nmbidan;
    private widget.TextBox nmbidan2;
    private widget.TextBox nmbidan3;
    private widget.TextBox nmdranak;
    private widget.TextBox nmdrumum;
    private widget.TextBox nminstrumen;    priate     private widget.TextBox nmonloop2;
    private widget.TextBox nmonloop3;
    private widget.TextBox nmonloo    priate widget.TextBox nmonloop5;
    private widget.TextBox nmoperator1;
    private widget.TextBox nmoperator2;
    private widget.TextBox nmoperator3;
    private widget.TextBox nmpjanak;
    private widget.TextBox nmprwluar;
    private widget.TextBox nmprwresust;
    private widget.panelisi panelisi1;
    private widget.panelisi panelisi4;
    private widget.panelisi panelisi5;
    private javax.swing.JMenuItem ppBersihkan;
    private widget.ScrollPane scrollPane1;
    private widget.Table tbObat;
    private widget.Table tbtindakan;
    private widget.Tanggal tgl;
    // End of variables declaration//GEN-END:variables

    private void tampil() {  
        jml=0;
        for(i=0;i<tabMode.getRowCount();i++){
            if(tabMode.getValueAt(i,0).toString().equals("true")){
                jml++;
            }
        }
        
        pilih=null;
        pilih=new boolean[jml]; 
        kode_paket=null;
        kode_paket=new String[jml];
        kategori=null;
        kategori=new String[jml];
        nm_perawatan=null;
        nm_perawatan=new String[jml];
        operator1=null;
        operator1=new double[jml];
        operator2=null;
        operator2=new double[jml];
        operator3=null;
        operator3=new double[jml];
        asisten_operator1=null;
        asisten_operator1=new double[jml];
        asisten_operator2=null;
        asisten_operator2=new double[jml];
        asisten_operator3=null;
        asisten_operator3=new double[jml];
        instrumen=null;
        instrumen=new double[jml];
        dokter_anak=null;
        dokter_anak=new double[jml];
        perawaat_resusitas=null;
        perawaat_resusitas=new double[jml];
        dokter_anestesi=null;
        dokter_anestesi=new double[jml];
        asisten_anestesi=null;
        asisten_anestesi=new double[jml];
        asisten_anestesi2=null;
        asisten_anestesi2=new double[jml];
        bidan=null;
        bidan=new double[jml];
        bidan2=null;
        bidan2=new double[jml];
        bidan3=null;
        bidan3=new double[jml];
        bidan=new double[jml];
        perawat_luar=null;
        perawat_luar=new double[jml];   
        sewa_ok=null;
        sewa_ok=new double[jml];
        akomodasi=null;
        akomodasi=new double[jml];
        bagian_rs=null;
        bagian_rs=new double[jml];
        omloop=null;
        omloop=new double[jml];
        omloop2=null;
        omloop2=new double[jml];
        omloop3=null;
        omloop3=new double[jml];
        omloop4=null;
        omloop4=new double[jml];
        omloop5=null;
        omloop5=new double[jml];
        sarpras=null;
        sarpras=new double[jml];
        alat=null;
        alat=new double[jml];   
        dokter_pjanak=null;
        dokter_pjanak=new double[jml]; 
        dokter_umum=null;
        dokter_umum=new double[jml]; 
        ttltindakan=null;
        ttl t indakan=new double[jml];        
        ind ex = 0;             
        for(i= 0;i<tabMode.getRowCoun t();i++){ 
            if(tabMode.getValueAt(i,0).toString().equals("true")){
                pilih[index]=true;
         

                  kategori[index]=tabMode.getValueAt(i,3).toString();
                  operator1[index
                op e rator2[index]=Double.parseDouble(tabMode.getValueAt(i,5).toString());
                op e rator3[index]=Double.parseDouble(tabMode.getValueAt(i,6).toString());
                 a sisten_operator1[index]=Double.parseDouble(tabMode.getValueAt(i,7).toString());
                 a sisten_operator2[index]=Double.parseDouble(tabMode.getValueAt(i,8).toString());
                asis t en_operator3[index]=Double.parseDouble(tabMode.getValueAt(i,9).toString());
                inst r umen[index]=Double.parseDouble(tabMode.getValueAt(i,10).toString());
                d o kter_anak[index]=Double.parseDouble(tabMode.getValueAt(i,11).toString());
                p e rawaat_resusitas[index]=Double.parseDouble(tabMode.getValueAt(i,12).toString());
                d o kter_anestesi[index]=Double.parseDouble(tabMode.getValueAt(i,13).toString());
                a s isten_anestesi[index]=Double.parseDouble(tabMode.getValueAt(i,14).toString());
                a s isten_anestesi2[index]=Double.parseDouble(tabMode.getValueAt(i,15).toString());
                b i dan[index]=Double.parseDouble(tabMode.getValueAt(i,16).toString());
                bidan2[in d ex]=Double.parseDouble(tabMode.getValueAt(i,17).toString());
                bidan3[in d ex]=Double.parseDouble(tabMode.getValueAt(i,18).toString());
                perawat_l u ar[index]=Double.parseDouble(tabMode.getValueAt(i,19).toString());
                alat[inde x ]=Double.parseDouble(tabMode.getValueAt(i,20).toString());   
                sewa_ok[i n dex]=Double.parseDouble(tabMode.getValueAt(i,21).toString());
                akomodasi [ index]=Double.parseDouble(tabMode.getValueAt(i,22).toString());  
                b a gian_rs[index]=Double.parseDouble(tabMode.getValueAt(i,23).toString());  
                o m loop[index]=Double.parseDouble(tabMode.getValueAt(i,24).toString()); 
                oml o op2[index]=Double.parseDouble(tabMode.getValueAt(i,25).toString()); 
                oml o op3[index]=Double.parseDouble(tabMode.getValueAt(i,26).toString());   
                omloop4[in d ex]=Double.parseDouble(tabMode.getValueAt(i,27).toString());   
                omloop5[in d ex]=Double.parseDouble(tabMode.getValueAt(i,28).toString());   
                sarpras [ index]=Double.parseDouble(tabMode.getValueAt(i,29).toString()); 
                dokter_ p janak[index]=Double.parseDouble(tabMode.getValueAt(i,30).toString()); 
                dokter_u m um[index]=Double.parseDouble(tabMode.getValueAt(i,31).toString()); 
                ttltinda k an[index]=Double.parseDouble(tabMode.getValueAt(i,32).toString());                
                index++;  
            }  
        }  
          
        Valid. t abelKosong(tabMode);
        for(i= 0 ;i<jml;i++){
            ta b Mode.addRow(new Object[]{pilih[i],kode_paket[i],nm_perawatan[i],kategori[i],operator1[i],
                  operator2[i],operator3[i],asisten_operator1[i],asisten_operator2[i],asisten_operator3[i],
                  instrumen[i],dokter_anak[i],perawaat_resusitas[i],dokter_anestesi[i],
                asis t en_anestesi[i],asisten_anestesi2[i],bidan[i],bidan2[i],bidan3[i],perawat_luar[i],
                alat [ i],sewa_ok[i],ak
                  omloop3[i],omloop4[i],omloop5[i],sarpras[i],dokter_pjanak[i],dokter_umum[i],ttltindakan[i]
            });  
        }  
          
        try{  
            if(ca r a_bayar_operasi.equals("Yes")&&kelas_operasi.equals("No")){
                  pstindakan=koneksi.prepareStatement("select kode_paket, nm_perawatan,kategori, operator1, operator2, operator3, "+
                     "asisten_operator1, asisten_operator2,asisten_operator3, instrumen, dokter_anak,perawaat_resusitas,"+
                     "dokter_anestesi, asisten_anestesi, asisten_anestesi2, bidan, bidan2, bidan3, perawat_luar, alat,"+
                     "sewa_ok,akomodasi,bagian_rs,omloop,omloop2,omloop3,omloop4,omloop5,sarpras,dokter_pjanak,dokter_umum,(operator1+operator2+operator3+"+
                     "asisten_operator1+asisten_operator2+asisten_operator3+instrumen+dokter_anak+perawaat_resusitas+"+
                     "alat+dokter_anestesi+asisten_anestesi+asisten_anestesi2+bidan+bidan2+bidan3+perawat_luar+sewa_ok+"+
                     "akomodasi+bagian_rs+omloop+omloop2+omloop3+omloop4+omloop5+sarpras+dokter_pjanak+dokter_umum) as jumlah "+
                     "from paket_operasi where status='1' and (kd_pj=? or kd_pj='-') "+
                     (TCariPaket.getText().trim().equals("")?"":"and (kode_paket like ? or nm_perawatan like ?) ")+"order by nm_perawatan ");
            }el s e if(cara_bayar_operasi.equals("No")&&kelas_operasi.equals("No")){
                  pstindakan=koneksi.prepareStatement("select kode_paket, nm_perawatan,kategori, operator1, operator2, operator3, "+
                     "asisten_operator1, asisten_operator2,asisten_operator3, instrumen, dokter_anak,perawaat_resusitas,"+
                     "dokter_anestesi, asisten_anestesi, asisten_anestesi2, bidan, bidan2, bidan3, perawat_luar, alat,"+
                     "sewa_ok,a
                   "a s isten_operator1+asisten_operator2+asisten_operator3+instrumen+dokter_anak+perawaat_resusitas+"+
                   "a l at+dokter_aneste
                    " akomodasi+bagian_rs+omloop+omloop2+omloop3+omloop4+omloop5+sarpras+dokter_pjanak+dokter_umum) as jumlah "+
                    " from paket_opera
                    ( TCariPaket.getText().trim().equals("")?"":"and (kode_paket like ? or nm_perawatan like ?) ")+"order by nm_perawatan ");
            }else i f (cara_bayar_oper
                  
                         "asisten_operator1, a sist en_operator2,asisten_operator3, instrumen, dokter_anak,perawaat_resusitas,"+
                    "dokter_anestesi,  asisten_anestesi, asisten_an estesi2, bidan, bidan2, bidan3, perawat_luar, alat,"+
                   "sewa_ok, a komodasi,bagian_rs,omloop,omloop2,omloop3,omloop4,omloop5,sarpras,dokter_pjanak,dokter_umum,(operator1+operator2+operator3+"+
                   "asisten_opera t or1+asisten_operator2 +asisten_operator3+instrumen+dokter_anak+perawaat_resusitas+"+
                   "alat+dokter_ane s tesi+asisten_anestesi +asisten_anestesi2+bidan+bidan2+bidan3+perawat_luar+sewa_ok+"+
                   "akomodasi+b a gian_rs+omloop+omloop 2+omloop3+omloop4+omloop5+sarpras+dokter_pjanak+dokter_umum) as jumlah "+
                   "from paket_o p erasi where status='1' and (kd_pj=? or k d_pj='-') and (kelas=? or kelas='-') "+
                   (TCariPaket.g e tText().trim().equals("")?"":"and (kode_ paket like ? or nm_perawatan like ?) ")+"order by nm_perawatan ");
            }else if(cara_bayar_ o perasi.equals("No")&&kelas_operasi.equal s("Yes")){
                pstindakan=koneksi.prepa r eStatement("select kode_paket, nm_perawa tan,kategori, operator1, operator2, operator3, "+
                   "asisten_operator1, a s isten_operator2,asisten_operator3, instr umen, dokter_anak,perawaat_resusitas,"+
                   "dokter_anestesi, asi s ten_anestesi, asisten_anestesi2, bidan,  bidan2, bidan3, perawat_luar, alat,"+
                   "sewa_ok,akom o dasi,bagian_rs,omloop,omloop2,omloop3,om loop4,omloop5,sarpras,dokter_pjanak,dokter_umum,(operator1+operator2+operator3+"+
                   "asisten_operat o r1+asisten_operator2+asisten_operator3+i nstrumen+dokter_anak+perawaat_resusitas+"+
                   "alat+dokter_anestesi+ a sisten_anestesi+asisten_anestesi2+bidan+ bidan2+bidan3+perawat_luar+sewa_ok+"+
                   "akomodasi+bagian_r s +omloop+omloop2+omloop3+omloop4+omloop5+ sarpras+dokter_pjanak+dokter_umum) as jumlah "+
                   "from paket_operasi  w here status='1' and (kelas=? or kelas='- ') "+
                   (TCariPaket.getText() . trim().equals("")?"":"and (kode_paket li ke ? or nm_perawatan like ?) ")+"order by nm_perawatan ");
            }   
               
            try {   
                if(cara_bayar_opera s i.equals("Yes")&&kelas_operasi.equals("N o")){
                    pstinda k an.setString(1,kd_pj.trim()); 
                    if(!TCariP a ket.getText().trim().equals("")){ 
                        pstindak a n.setString(2,"%"+TCariPaket.getText()+" %");
                        pstindak a n.setString(3,"%"+TCariPaket.getText()+" %");
                    }   
                    rstindakan = pstindakan.executeQuery(); 
                }else if(cara_ b ayar_operasi.equals("No")&&kelas_operasi .equals("No")){
                    if(!TCariP a ket.getText().trim().equals("")){ 
                        pstind a kan.setString(1,"%"+TCariPaket.getText() +"%");
                        pstind a kan.setString(2,"%"+TCariPaket.getText() +"%");
                    }   
                    rstindakan=pst i ndakan.executeQuery(); 
                }else if(cara_baya r _operasi.equals("Yes")&&kelas_operasi.eq uals("Yes")){
                    pstindakan.setString(1,kd_pj.trim());
                    pstindakan.setString(2,kelas.trim());
         

                        pstindakan.setString(4,"%"+TCariPaket.getText()+"%");
                          }  
                    rstindakan=pstindak a n.execute Query();   
                    }else if(cara _bayar_operas i.equals("No")&&kelas _operasi.equals("Yes" )){
                        pstindaka n.setString(1,k elas.trim()); 
                        if(!TCariPaket.g etText().trim().equal s("")){   
                             pstindakan. setString(2," %"+TCariPaket .getText() +"%");
                            pst indakan.set String(3,"% "+TCariPake t.getText()+"%");  
                    }
         

                 
                 while(rstindakan.next()){   
                    tabMod e .addRow(new Object[]{fals
                        e,rstindakan.getString("kode_paket"), 
                                                rstindakan.getString("nm_perawatan"),
                                
                                                rstindakan.getString("kategori"), 
                                
                                                rstindakan.getDouble("operator1"), 
                                
                                                rstindakan.getDouble("operator2"), 
                                
                                                rstindakan.getDouble("operator3"), 
                                
                                                rstindakan.getDouble("asisten_operator1"), 
                                
                                                rstindakan.getDouble("asisten_operator2"),  
                                                rstindakan.getDouble("a s is
                                        t en_operator3"), 
                                 
                                     rstindakan.getDou bl e("instrumen"),  
                                     rstindakan.getDou
                        ble("dokter_anak"),  
                                                rstindakan.getDouble("perawaat_resusitas"), 
                                
                                                rstindakan.getDouble("dokter_anestesi"), 
                                
                                                rstindakan.getDouble("asisten_anestesi"), 
                                
                                                rstindakan.getDouble("asisten_anestesi2"), 
                                
                                                rstindakan.getDouble("bidan"), 
                                
                                                rstindakan.getDouble("bidan2"), 
                                
                                                rstindakan.getDouble(" b
                                                rstindakan.getDouble("p e ra
                                        w at_luar"), 
                                 
                                     rstindakan.getDoub le ("alat"),  
                                     rstindakan.getDou
                        ble("sewa_ok"),  
                                                rstindakan.getDouble("akomodasi"), 
                                
                                                rstindakan.getDouble("bagian_rs"), 
                                
                                                rstindakan.getDouble("omloop"), 
                                
                                                rstindakan.getDouble("omloop2"), 
                                
                                                rstindakan.getDouble("omloop3"), 
                                
                                                rstindakan.getDouble("omloop4"), 
                                
                                                rstindakan.getDouble("omloop5"), 
                                
                                                rstindakan.getDouble("s a rp
                                        r as"), 
                                 
                                     rstindakan.getDou bl e("dokter_pjanak"),  
                                     rstindakan.getDou
                        ble("dokter_umum"),  
                                                rstindakan.getDouble("jumlah")});
                                
                                
                                
                                 (Exception e) {
                                
                                tem.out.println("Notifikasi : "+e);
                                
                                ly{
                                
                                rstindakan!=null){
                                
                                 rstindakan.close(); 
                                  
                                         
                                 
             

                }
            }                      
        }catch(SQLException e){ 
            System.out .println("Notifikasi : "+e); 
        }     
             
    }
      
    //obat     
    private void tampi l2() { 
        jml=0;     
        for(i=0;i<tbObat.getRowCount();i++){     
            //System.out.println(tbObat.getValueAt(i,0).toString());
            if(!tbObat.getValu e At(i,0).toString().equals("")){
                j ml++;    
            } 
        } 
          
        jmlobat=new double[jml];     
        kd_obat=new String[jml];     
        nm_obat=new String[jml];
        satuan=new String[jml] ; 
        hargasatu an=new  double[jml];   
        ttlobat=new double[jml]; 
          
        index=0;             
        for(i=0;i<tabMode2.getRowCount();i++){     
            if(!tabMode2.getValueAt(i,0).toString().equals("")){
                jmlobat[index] = Double.parseDouble(tabMode2.getValueAt(i,0).toString());
                k

                satua n[index]=tabMode2.g etValueAt(i,3).toString();
                hargasatuan[index]=Double.parse D ouble( tabMode2.getValueAt(i,4).toString()
                            le.parseDouble(tabMode2.getValueAt(i,
                            
                            
                            
                            
                            ;
                            
                            
                            ect[]{jmlobat[i],kd_obat[i],nm_oba
                            
                            
                            
                            tatement("select obatbhp_ok.kd_obat, obat
                            hargasatuan from obatbhp_ok inner join kod
                            ok.kode_sat=kodesatuan.kode_sa
                            hp_ok.kd_obat like ? or "+
                            nm_obat like ? or "+
                            satuan like ? "+
                            atbhp_ok.kd_obat");
                            
                            "%"+TCari.getText()+"%");
                            "%"+TCari.getText()+"%");
                            "%"+TCari.getText()+"%");
                            teQuery();
                            ){
                            (new Object[]{"",rsobat.getStrin
                            rsobat.getString(2),
                            rsobat.getString(3),
                            rsobat.getString(4),0});
                            
                             
                System.out.println(e);
            }finally{
                if(rsobat!=null){  
                    r sobat.close();
                }    
                if(psobat!=null){
                    psobat.close();
                }    
            }
        }catch(SQLException e){
            S
        }   
    }  


     

        int row=tbObat.getSelectedRow();
        if(row!= -1){         
            i nt kolom=tbObat.getSelectedColumn();  
            if ( (k o l om==0)||(kolom==1)){      
                if(!tbObat.getValueAt(row,0).toString().equals("")){
                    try {  
                       tbObat.setValueAt(Valid.SetAngka2(Double.parseDouble(tbObat.getValueAt(row,0).toString())*Double.parseDouble(tbObat.getValueAt(row,4).toString())), row,5);                    
                   } catch (Exception e) {
         

                  }else if(tbObat.getValueAt(row,0).toString().equals("")){
                      tbObat.setValueAt(0, row,5);   
                  }                 
            }                
              
            bia y aobat=0;

            i n t 
             f o r( i n t r=0;r<row2;r++){   
                 if(!tbObat.getValueAt( r,5).toString().isEmpty() ){
                    try {   
                        y=Doub l e.parseDouble(tbObat.g etValueAt(r,5).toString()); 
                    } catch (E x ception e) { 
                        y=0;   
                    }                                      
                }else if(tbOba t .getValueAt(r,5).toString().isEmpty()){ 
                    y=0;                
                }
         

        }

                
    private void getData2(){        
       in

             
                tr y  { 
                    indakan.setValueAt(Double.parseDouble(tbtindakan.getValueAt(row ,
                                  Double.parseDouble(tbtindakan.g e
                                  Double.parseDouble(tbti n
                                  Double.parseDoubl e
                                  Double.parseD o
                                  Double.parseDouble(tbtindakan.getValueAt(row,9).toString())+
                                      Double.parseDouble(tbtindakan.getValueAt(row,10).toString())+
                                      D o uble.parseDoubl e (tbtindakan.getValueAt(row,11).toString())+
                                      D o uble.parseDoubl e (tbtindakan.getValueAt(row,12).toString())+
                                      D o uble.parseDoubl e (tbtindakan.getValueAt(row,13).toString())+
                                       Double.parseDouble(tbtindakan.getValueAt(row,14).toString())+
                                       Double.parseDouble(tbtindakan.getValueAt(row,15).toString())+
                                     Double.pars e Dou ble(tbtindakan.getVa
                              Double.parseDouble
                              Double.parseDouble
                              Double.parseDouble ( tbtindakan.getValueAt(row,19).toString())+
                                     Double.parseDouble(tbtindakan.getValueAt(row,20).toString())+
                                        Double.parseDouble(tbtindakan.getValueAt(row,21).toString())+
                                     Double.parseDouble(tbtindakan.getValueAt(row,22).toString())+
                                       Double.parseDouble(tbtindakan.getValueAt(row,23).toString())+
                                         Double.parseDouble(tbtindakan.getValueAt(row,24).toString())+
                                     Double.parseDouble(tbtindakan.getValueAt(row,25).toString())+
                                     Double.parseDouble(tbtindakan.getValueAt(row,26).toString())+
                                         Double.parseDouble(tbtindakan.getValueAt(row,27).toString())+
                                     Double.parseDouble(tbtindakan.getValueAt(row,28).toString())+
                                     Double.parseDouble(tbtindakan.getValueAt(row,29).toString())+
                                     Double.parseDouble(tbtindakan.getValueAt(row,30).toString())+
                                        Double.parseDouble(tbtindakan.getValueAt(row,31).toString()), row,32);
                } catch (Exception e) {  
                    tbtindakan.setValueAt(0, row,32);
     

            biayatindakan= 0
             y= 0 ;
             in t row2 =
             for(int  r =0;r<row2;r++){ 
                  swit ch  ( tb tindak an .ge t
                      case "true":  
                         
                             y=Double.parseDouble(tbtindakan.getValueAt(r,32).toString());                        
                                        
                         } catch (Exception
                             y=0; 
                         }                        
                          break;                  
                    case "false": 
                 
             

                biaya t indakan=biayatindakan+y;
            }              
        }  
    }        
                   

       
                    k(){
       // Valid.autoNomer 3 ("select ifnull(MAX(CONVERT(RIGHT(nota_jual,6),signed)),0) from penjualan ","PJ",6,NoNota); 
        TCari.request
                . getjml2 ()>=1){  
            BtnSimpan . se
                ambahOperasi.setEnabled(akses.gettarif_operasi());
            BtnTambah.set E nabled(ak s es.getoperasi());
        }    
        
    

        TNoRw.setText(norm) ;
         TPasie n .setText(nama);
        th is.s tatus= p
            .k d_pj=Sequel.cariIsi("select reg_periksa.kd_pj from  reg_periksa where reg_periksa .no_rawat=?",norm);        
        if(status.equals("Ranap")){
            norawatibu=Sequel.cariIsi("select ranap_gabung.no_rawat from ranap_gabung w here ranap_gab u
                              
                            ){  
                              
                            as from kamar inner join kamar_inap "+  
                            r=kamar_inap.kd_kamar where no_rawat=? "+  
                            tts_pulang='-' order by STR_TO_DATE(concat(ka mar_inap.tgl_ma s
                              
                              
                            as from kamar inner join kamar_inap "+  
                            r=kamar_inap.kd_kamar where no_rawat=? "+  
                            tts_pulang='-' order by STR_TO_DATE(concat(ka mar_inap.tgl_ma s
                              
                            ")){  
                              
                              
                              
                            ;  
                              
                              
                              
                              
                            tring nama,String posisi, String KodeOperator ,String NamaOpe r
                              
                              
                              
                            lect reg_periksa.kd_pj from reg_periksa where  reg_periksa.no _
                              
            norawatibu=Sequel.cariIsi("select ranap_gabung.no_rawat from ranap_gabung where ranap_gabung.no_rawat2=?",TNoRw.getText());
         
            if(!norawatibu.equals("")){
                kelas=Sequel.cariIsi(   
             

                    "and  k amar_inap.stts_pulang='-' order by STR_TO_DATE(concat(kamar_inap.tgl_masuk,' ',kamar_inap.jam_masuk),'%Y-%m-%d %H:%i:%s') desc limit 1",norawatibu);
            } e lse{
                kela s =Sequel.cariIsi(
                     " s el e c t kam ar.k e
                    "on kamar.kd_kamar=kamar_ina p.kd_kamar where no_rawat=? "+
                    "and kamar_inap.stts_pulang='-' order by STR_TO_DATE(concat(kamar_inap.tgl_masuk,' ',kamar_inap.jam_masuk),'%Y-%m-%d %H:%i:%s') desc limit 1",TNoRw.getText());
            } 
        }else if(status.equal s ("Ralan")){ 
                         Jalan";
        }  
        runBackground(() 
                         ->tam
                    setText(KodeOperator);
        nmoperator1.setTe x t(NamaOperator);
    }
    
    public void setNoRmEdit(S t ring norw, St r ing nama, String posisi, String tanggal) {
        TNoRw
        TPasien.setText(nama);
     

                    "se l
                     + "operasi.asisten_operator2,operasi.asisten_operator3, operasi.
        // nstrumen, operasi.dokter_anak, oerasi.perawaat_resusitas, "
                    + "operasi.dokter_anestesi, operasi.asisten_anestesi,operasi.asisten_anestesi2, operasi.bidan, operasi.bidan2, operasi.bidan3, operasi.perawat_luar, "
                     + "ope ra si .omloop,operasi.omloop2,operasi.omloop3,operasi.omloop4,operasi.omloop5,operasi.dokter_pjanak,operasi.dokter_umum "
                    + "from operasi where operasi.no_rawat='" + TNoRw.getText() + "' and operasi.tgl_operasi='" +Valid.SetTgl(tgl.getSelectedItem()+"")+" "+tgl.getSelectedItem().toString().substring(11,19)+"'").executeQuery();
            while (rs2.next()) {
                kdoperator1.setText(rs2.getString("operator1"));
         
     

                kdoperator3.setText(rs2.getString("operator3"));
                nmoperator3.setText(dokter.tampil3(rs2.getString("operator3")));
                kdasistoperator1.setText(rs2.getString("asisten_operator1"));
                nmasistoperator1.setText(petugas.tampil3(rs2.getString("asisten_operator1")));
                kdasistoperator2.setText(rs2.getString("asisten_operator2"));
                nmasistoperator2.setText(petugas.tampil3(rs2.getString("asisten_operator2")));
                kdasistoperator3.setText(rs2.getString("asisten_operator3"));
                nmasistoperator3.setText(petugas.tampil3(rs2.getString("asisten_operator3")));
                kdInstrumen.setText(rs2.getString("instrumen"));
                nminstrumen.setText(petugas.tampil3(rs2.getString("instrumen")));
                kddranak.setText(rs2.getString("dokter_anak"));
                nmdranak.setText(dokter.tampil3(rs2.getString("dokter_anak")));
                kdprwresust.setText(rs2.getString("perawaat_resusitas"));
                nmprwresust.setText(petugas.tampil3(rs2.getString("perawaat_resusitas")));
                kdanestesi.setText(rs2.getString("dokter_anestesi"));
                nmanestesi.setText(dokter.tampil3(rs2.getString("dokter_anestesi")));
                kdasistanestesi.setText(rs2.getString("asisten_anestesi"));
                nmasistanestesi.setText(petugas.tampil3(rs2.getString("asisten_anestesi")));
                kdasistanestesi2.setText(rs2.getString("asisten_anestesi2"));
                nmasistanestesi2.setText(petugas.tampil3(rs2.getString("asisten_anestesi2")));
                kdbidan.setText(rs2.getString("bidan"));
                nmbidan.setText(petugas.tampil3(rs2.getString("bidan")));
                kdbidan2.setText(rs2.getString("bidan2"));
                nmbidan2.setText(petugas.tampil3(rs2.getString("bidan2")));
                kdbidan3.setText(rs2.getString("bidan3"));
                nmbidan3.setText(petugas.tampil3(rs2.getString("bidan3")));
                kdprwluar.setText(rs2.getString("perawat_luar"));
                nmprwluar.setText(petugas.tampil3(rs2.getString("perawat_luar")));
                kdonloop1.setText(rs2.getString("omloop"));
                nmonloop1.setText(petugas.tampil3(rs2.getString("omloop")));
                kdonloop2.setText(rs2.getString("omloop2"));
     

                nmonloop3.setText(pe tugas.tampil 3(rs2.getString("omloop3")));  
                kdonloop4.setText(rs2.getString("omloop4"));
                nmonloop4.setText(petugas.tampil3(rs2.getString("omloop4")));
                kdo n loop5.setText(rs2.getString("omloop5"));
                nm o nloop5.setText(petugas.tampil3(rs2.getString("omloop5"))); 
                 kdpjanak.setText(r s2.getString("dokter_pjanak"));
                nmpjan a k.setText(dokter.tampil3(rs2.getString("dokter_pjanak")));
                    

                 nmdrumum.setText(dokte r.tampil3(rs2.getString("dokter_umum")));
                this. s tatus = rs2.get
                        aket = rs2.getString("kode_paket"); 
                                gori.setSelectedItem(rs2.getString("kategori")); 
                                s.setText(rs2.getString("jenis_anasthesi"));
                        
                  
                tampi l PaketOperasi(kd
                        ilObatBHPOperasi(); 
                                 
                                != null) {
                        
             
             }  
        } catch ( E xception e) {
            System.out.println("Notifikasi Cari Data : " + e);
        } 
//        this.status=posis i;
        this.kd_pj = Sequel.cariIsi("select reg_periksa.kd_pj from reg_periksa where reg_periksa.no_rawat=?", norw);
        if (status.equals("Ranap")) {
     

            if (!norawatibu.equals("")) {
                kelas = Sequel.cariIsi(
                        "select kamar.kelas from kamar inner join kamar_inap "
                        + "on kamar.kd_kamar=kamar_inap.kd_kamar where no_rawat=? "
                        + "and kamar_inap.stts_pulang='-' order by STR_TO_DATE(concat(kamar_inap.tgl_masuk,' ',kamar_inap.jam_masuk),'%Y-%m-%d %H:%i:%s') desc limit 1", norawatibu);
            } else {
                kelas = Sequel.cariIsi(
                        "select kamar.kelas from kamar inner join kamar_inap "
                                + "on kamar.kd_kamar=kamar_inap.kd_kamar where no_rawat=? "
                                + "and kamar_inap.stts_pulang='-' order by STR_TO_DATE(concat(kamar_inap.tgl_masuk,' ',kamar_inap.jam_masuk),'%Y-%m-%d %H:%i:%s') desc limit 1", T
                            
                            tatus.equals("Ralan")) {
                                 
                                
                    
            kelas = "Rawat Jalan";
        }

//        tampil();
//        tampil2();
       
        BtnEdit.setVisible(true);
        BtnEdit.setEnabled(true);

    }
    
    private void isForm(){
        if(ChkInput.isSelected()==true){
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH,internalFrame1.getHeight()-79));
//            PanelInput.setPreferredSize(new Dimension(WIDTH,500));
            FormInput.setVisible(true);      
            ChkInput.setVisible(true);
        }else if(ChkInput.isSelected()==false){           
            ChkInput.setVisible(false); 
            PanelInput.setPreferredSize(new Dimension(WIDTH,20));
            FormInput.setVisible(false);  
//            PanelInput.setPreferredSize(new Dimension(WIDTH,500));
//            FormInput.setVisible(true);      
            ChkInput.setVisible(true);
        }
    }
    
    public void SetCariOperasi(String Operasi,String kodedokter,String namadokter){
        TCariPaket.setText(Operasi);
        kdoperator1.setText(kodedokter);
        nmoperator1.setText(namadokter);
    }
     
    
    private void tampilPaketOperasi(String kd_paket) {
        jml=1;
        for(i=0;i<tabMode.getRowCount();i++){
            if(tabMode.getValueAt(i,0).toString().equals("true")){
                jml++;
            }
        }

        pilih = null;
        pilih = new boolean[jml];
        kode_paket = null;
        kode_paket = new String[jml];
        kategori = null;
        kategori = new String[jml];
        nm_perawatan = null;
        nm_perawatan = new String[jml];

                1 = new double[jml];
        operator2 = null;
        operator2 = new double[jml];
        operator3 = null;
        operator3 = new double[jml];
        asisten_operator1 = null;
        asisten_operator1 = new double[jml];
        asisten_operator2 = null;
        a
           isten_operator3 = null;
        asisten_operator3 = new double[jml];
        instrumen = null;
        instrumen = new double[jml];
                    
        dokter_anak = null;
        dokter_anak = new double[jml];
        perawaat_resusitas = null;
        perawaat_resusitas = new double[jml];
                                = null;
                                = new double[jml];
                        
        asisten_anestesi = null;
        asisten_anestesi = new double[jml];
        asisten_anestesi2 = null;
                                2 = new double[jml];
                                
                        
        bidan = new double[jml];
        bidan2 = null;
        bidan2 = new double[jml];
        b

           dan = new
           rawat_luar

        sewa_ok = null;
        sewa_ok = new double[jml];
        akomodasi = null;
     

        bagian_rs = new d ouble[jml];
        om loop = null;   
        omloop = new double[jml];
        omloop2 = null;   
               2 = new double[jml];
        omloop3 = null;
            op3 = new double[jml];
        o mloop4  = null;   
            op4 = new double[jml];
            op5 = null; 
        omloop5 = new double[jml];
               s = null;
               s = new double[jml];
        alat = null;
        alat = new double[jml];
     

        dokter_umum = null;   
        dokter_umum = new double[jml];
        ttltindakan = null;
        ttltindakan = new double[jml];
     

            pstindakan = koneksi.prepareStatement("select kode_paket, nm_perawatan,kategori, operator1, operator2, operator3, "
                      + "asisten_operator1, asisten_operator2,asisten_operator3, instrumen, dokter_anak,perawaat_resusitas,"
                          + "dokter_anestesi,  asis ten_anestesi, asisten_anestesi2, bidan, bidan2, bidan3, perawat_luar, alat,"
                     + "sewa_ok,akomo dasi,bagian_rs,omloop,omloop2 ,omloop3,omloop4,omloop5,sarpras,dokter_pjanak,dokter_umum,(operator1+operator2+operator3+"
                    + "asisten_operator1+asisten_operator2+asisten_operator3+instrumen+dokter_anak+perawaat_resusitas+"
                    + "alat+dokter_anestesi+asisten_anestesi+asisten_anestesi2+bidan+bidan2+bidan3+perawat_luar+sewa_ok+"
                    + "akomodasi+bagian_rs+omloop+omloop2+omloop3+omloop4+omloop5+sarpras+dokter_pjanak+dokter_umum) as jumlah "
                    + "from paket_operasi "
                    + "where status='1' and kode_paket = ? ");
            try {
                pstindakan.setString(1, kd_paket.trim());
                rs = pstindakan.executeQuery();
                while (rs.next()) {
                    pilih[index] = true;
                    kode_paket[index] = rs.getString("kode_paket");
                    nm_perawatan[index] = rs.getString("nm_perawatan");
                    kategori[index] = rs.getString("kategori");
                    operator1[index] = rs.getDouble("operator1");
                    operator2[index] = rs.getDouble("operator2");
                    operator3[index] = rs.getDouble("operator3");
                    asisten_operator1[index] = rs.getDouble("asisten_operator1");
                    asisten_operator2[index] = rs.getDouble("asisten_operator2");
                    asisten_operator3[index] = rs.getDouble("asisten_operator3");
                    instrumen[index] = rs.getDouble("instrumen");
                    dokter_anak[index] = rs.getDouble("dokter_anak");
                    perawaat_resusitas[index] = rs.getDouble("perawaat_resusitas");
                    dokter_anestesi[index] = rs.getDouble("dokter_anestesi");
                    asisten_anestesi[index] = rs.getDouble("asisten_anestesi");
                    asisten_anestesi2[index] = rs.getDouble("asisten_anestesi2");
                    bidan[index] = rs.getDouble("bidan");
                    bidan2[index] = rs.getDouble("bidan2");
                    bidan3[index] = rs.getDouble("bidan3");
                    perawat_luar[index] = rs.getDouble("perawat_luar");
                    alat[index] = rs.getDouble("alat");
                    sewa_ok[index] = rs.getDouble("sewa_ok");
                    akomodasi[index] = rs.getDouble("akomodasi");
                    bagian_rs[index] = rs.getDouble("bagian_rs");
                    omloop[index] = rs.getDouble("omloop");
                    omloop2[index] = rs.getDouble("omloop2");
                    omloop3[index] = rs.getDouble("omloop3");
                    omloop4[index] = rs.getDouble("omloop4");
                    omloop5[index] = rs.getDouble("omloop5");
                    sarpras[index] = rs.getDouble("sarpras");
                    dokter_pjanak[index] = rs.getDouble("dokter_pjanak");
                    dokter_umum[index] = rs.getDouble("dokter_umum");
                    ttltindakan[index] = rs.getDouble("jumlah");
                    index++;
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (pstindakan != null) {
                    pstindakan.close();
                }
            }
        } catch (SQLException e) {
            System.out.println("Notifikasi : " + e);
        }

        Valid.tabelKosong(tabMode);
        for (i = 0; i < jml; i++) {
            tabMode.addRow(new Object[]{pilih[i], kode_paket[i], nm_perawatan[i], kategori[i], operator1[i],
                operator2[i], operator3[i], asisten_operator1[i], asisten_operator2[i], asisten_operator3[i],
                instrumen[i], dokter_anak[i], perawaat_resusitas[i], dokter_anestesi[i],
                asisten_anestesi[i], asisten_anestesi2[i], bidan[i], bidan2[i], bidan3[i], perawat_luar[i],
                alat[i], sewa_ok[i], akomodasi[i], bagian_rs[i], omloop[i], omloop2[i],
                omloop3[i], omloop4[i], omloop5[i], sarpras[i], dokter_pjanak[i], dokter_umum[i], ttltindakan[i]
            });
        }

        try {
            pstindakan = koneksi.prepareStatement("select kode_paket, nm_perawatan,kategori, operator1, operator2, operator3, "
                    + "asisten_operator1, asisten_operator2,asisten_operator3, instrumen, dokter_anak,perawaat_resusitas,"
                    + "dokter_anestesi, asisten_anestesi, asisten_anestesi2, bidan, bidan2, bidan3, perawat_luar, alat,"
                    + "sewa_ok,akomodasi,bagian_rs,omloop,omloop2,omloop3,omloop4,omloop5,sarpras,dokter_pjanak,dokter_umum,(operator1+operator2+operator3+"
                    + "asisten_o
                    perator1+asisten_operator2+asisten_operator3+instrumen+dokter_anak+perawaat_resusitas+"
                            + "alat+dokter_anestesi+asisten_anestesi+asisten_anestesi2+bidan+bidan2+bidan3+perawat_luar+sewa_ok+"
                            + "akomodasi+bagian_rs+omloop+omloop2+omloop3+omloop4+omloop5+sarpras+dokter_pjanak+dokter_umum) as 
                            + "from paket_operasi "
                            + "where status='1' and (kd_pj=? or kd_pj='-') and (kode_paket like ? or nm_perawatan like ?) order
                            an2 = koneksi.prepareStatement("select kode_paket, nm_perawatan,kategori, operator1, operator2, opera
                            + "asisten_operator1, asisten_operator2,asisten_operator3, instrumen, dokter_anak,perawaat_resusitas,"
                            + "dokter_anestesi, asi
                            + "sewa_ok,akomodasi,bagian_rs,omloop,omloop2,omloop3,omloop4,omloop5,sarpras,dokter_pjanak,dokter_umum,(operator1+operator2+operator3+"
                    + "asisten_operator1+asisten_operator2+asisten_operator3+instrumen+dokter_anak+perawaat_resusitas+"
                    + "alat+dokter_anestesi+asisten_anestesi+asisten_anestesi2+bidan+bidan2+bidan3+perawat_luar+sewa_ok+"
                    + "akomodasi+bagian_rs+omloop+omloop2+omloop3+omloop4+omloop5+sarpras+dokter_pjanak+dokter_umum) as jumlah "
                    + "from paket_operasi "
                    + "where status='1' and (kode_paket like ? or nm_perawatan like ?) order by nm_perawatan ");
            pstindakan3 = koneksi.prepareStatement("select kode_paket, nm_perawatan,kategori, operator1, operator2, operator3, "
                    + "asisten_operator1, asisten_operator2,asisten_operator3, instrumen, dokter_anak,perawaat_resusitas,"
                    + "dokter_anestesi, asisten_anestesi, asisten_anestesi2, bidan, bidan2, bidan3, perawat_luar, alat,"
                    + "sewa_ok,akomodasi,bagian_rs,omloop,omloop2,omloop3,omloop4,omloop5,sarpras,dokter_pjanak,dokter_umum,(operator1+operator2+operator3+"
                    + "asisten_operator1+asisten_operator2+asisten_operator3+instrumen+dokter_anak+perawaat_resusitas+"
                    + "alat+dokter_anestesi+asisten_anestesi+asisten_anestesi2+bidan+bidan2+bidan3+perawat_luar+sewa_ok+"
                    + "akomodasi+bagian_rs+omloop+omloop2+omloop3+omloop4+omloop5+sarpras+dokter_pjanak+dokter_umum) as jumlah "
                    + "from paket_operasi "
                    + "where status='1' and (kd_pj=? or kd_pj='-') and (kelas=? or kelas='-') and (kode_paket like ? or nm_perawatan like ?) order by nm_perawatan ");
            pstindakan4 = koneksi.prepareStatement("select kode_paket, nm_perawatan,kategori, operator1, operator2, operator3, "
                    + "asisten_operator1, asisten_operator2,asisten_operator3, instrumen, dokter_anak,perawaat_resusitas,"
                    + "dokter_anestesi, asisten_anestesi, asisten_anestesi2, bidan, bidan2, bidan3, perawat_luar, alat,"
                    + "sewa_ok,akomodasi,bagian_rs,omloop,omloop2,omloop3,omloop4,omloop5,sarpras,dokter_pjanak,dokter_umum,(operator1+operator2+operator3+"
                    + "asisten_operator1+asisten_operator2+asisten_operator3+instrumen+dokter_anak+perawaat_resusitas+"
                    + "alat+dokter_anestesi+asisten_anestesi+asisten_anestesi2+bidan+bidan2+bidan3+perawat_luar+sewa_ok+"
                    + "akomodasi+bagian_rs+omloop+omloop2+omloop3+omloop4+omloop5+sarpras+dokter_pjanak+dokter_umum) as jumlah "
                    + "from paket_operasi "
                    + "where status='1' and (kelas=? or kelas='-') and (kode_paket like ? or nm_perawatan like ?) order by nm_perawatan ");

            try {
                if (cara_bayar_operasi.equals("Yes") && kelas_operasi.equals("No")) {
                    pstindakan.setString(1, kd_pj.trim());
                    pstindakan.setString(2, "%" + TCariPaket.getText() + "%");
                    pstindakan.setString(3, "%" + TCariPaket.getText() + "%");
                    rs = pstindakan.executeQuery();
                } else if (cara_bayar_operasi.equals("No") && kelas_operasi.equals("No")) {
                    pstindakan2.setString(1, "%" + TCariPaket.getText() + "%");
                    pstindakan2.setString(2, "%" + TCariPaket.getText() + "%");
                    rs = pstindakan2.executeQuery();
                } else if (cara_bayar_operasi.equals("Yes") && kelas_operasi.equals("Yes")) {
                    pstindakan3.setString(1, kd_pj.trim());
                    pstindakan3.setString(2, kelas.trim());
                    pstindakan3.setString(3, "%" + TCariPaket.getText() + "%");
                    pstindakan3.setString(4, "%" + TCariPaket.getText() + "%");
                    rs = pstindakan3.executeQuery();
                } else if (cara_bayar_operasi.equals("No") && kelas_operasi.equals("Yes")) {
                    pstindakan4.setString(1, kelas.trim());
                    pstindakan4.setString(2, "%" + TCariPaket.getText() + "%");
                    pstindakan4.setString(3, "%" + TCariPaket.getText() + "%");
                    rs = pstindakan4.executeQuery();
                }

                while (rs.next()) {
                    tabMode.addRow(new Object[]{false, rs.getString("kode_paket"),
                        rs.getString("nm_perawatan"),
                        rs.getString("kategori"),
                        rs.getDouble("operator1"),
                        rs.getDouble("operator2"),
                        rs.getDouble("operator3"),
                        rs.getDouble("asisten_operator1"),
                        rs.getDouble("a s isten_operator2"),
                            rs.getDouble("asisten_operator3"),
                            rs.getDouble("instrumen"),
                            rs.getDouble("dokter_anak"),
                            rs.getDouble("perawaat_resusitas"),
                            rs.getDouble("dokter_anestesi"),
                        rs.getDouble("asisten_anestesi"),
                        rs.getDouble("asisten_anestesi2"),
                        rs.getDouble("bidan"),
                        rs.getDouble("bidan2"),
                        rs.getDo
                    uble("bidan3"),
                                rs.getDouble("perawat_luar"),
                                rs.getDouble("alat"),
                                rs.getDouble("sewa_ok"),
                                rs.getDouble("akomodasi"),
                                rs.getDouble("bagian_rs"),
                                rs.getDouble("omloop"),
                                rs.getDouble("omloo
                                rs.getDouble("omloop3"),
                        rs.getDou
                    ble("omloop4"),
                                rs.getDouble("omloop5"),
                                rs.getDouble("sarpras"),
                                rs.getDouble("dokter_pjanak"),
                                rs.getDouble("dokter_umum"),
                                rs.getDouble("jumlah")});
                            
                            (Exception e) {
                            em.out.println("Notifikasi : " + e);
            } finally {
                    
                            rs != null) {
                            rs.close();
                            
                            pstindakan != null) {
                            pstindakan.close();
                            
                            pstindakan2 != null) {
                            pstindakan2.close();
                }
                    
                            pstindakan3 != null) {
                            pstindakan3.close();
                            
                            pstindakan4 != null) {
                            pstindakan4.close();
                            
                            
                            Exception e) {
            System.out.println("Notifikasi : " + e);
        }
    }
    
    
    private void tampilObatBHPOperasi() {
        jml = Sequel.cariInteger("select count(kd_obat) from beri_obat_operasi where no_rawat ='" + TNoRw.getText() + "' and tanggal = '" + Valid.SetTgl(tgl.getSelectedItem() + "") + " " + tgl.getSelectedItem().toString().substring(11, 19) + "'");

        jmlobat = new double[jml];
        kd_obat = new String[jml];
        nm_obat = new String[jml];
        satuan = new String[jml];
        hargasatuan = new double[jml];
        ttlobat = new double[jml];
        index = 0;

        try {
            rs2 = koneksi.prepareStatement("select no_rawat, tanggal, kd_obat, hargasatuan, jumlah from beri_obat_operasi where no_rawat ='" + TNoRw.getText() + "' and tanggal = '" + Valid.SetTgl(tgl.getSelectedItem() + "") + " " + tgl.getSelectedItem().toString().substring(11, 19) + "'").executeQuery();
            while (rs2.next()) {
                try {
                    psobat = koneksi.prepareStatement("select obatbhp_ok.kd_obat, obatbhp_ok.nm_obat, kodesatuan.satuan, "
                            + "obatbhp_ok.hargasatuan from obatbhp_ok inner join kodesatuan "
                            + "on obatbhp_ok.kode_sat=kodesatuan.kode_sat "
                            + "where obatbhp_ok.kd_obat = ? ");
                    try {
                        psobat.setString(1, rs2 . getString("kd_obat"));
                            rs = psobat.executeQuery();
                            while (rs.next()) {
                                jmlobat[index] = rs2.g
                                kd_obat[index] = rs.ge
                                nm_obat[index] = rs.ge
                                satuan[index] = rs.getString("
                                hargasatuan[index] = rs.getDou
                                ttlobat[index] = jmlobat[index
                                index++;
                            }
                            tch (SQLException e) {
                            System.out.println(e);
                            nally {
                            if (rs != null) {
                                rs.close();
                            }
                            if (psobat != null) {
                                psobat.close();
                            }
                            
                            (SQLException e) {
                            em.out.println("Notifikasi
                            
                            
                            
                            osong(tabMode2);
                            
                            i < jml; i++) {
                            .addRow(new Object[]{jmlobat[i
                            
                             
            try {
                psobat = koneksi.prepareStatement("select obatbhp_ok.kd_obat, obatbhp_ok.nm_obat, kodesatuan.satuan, "
                        + "obatbhp_ok.hargasatuan from obatbhp_ok inner join kodesatuan "
                        + "on obatbhp_ok.kode_sat=kodesatuan.kode_sat "
                        + "where obatbhp_ok.kd_obat like ? or "
                        + "obatbhp_ok.nm_obat like ? or "
                        + "kodesatuan.satuan like ? "
                        + "order by obatbhp_ok.kd_obat");
                try {
                    psobat.setString(1, "%" + TCari.getText() + "%");
                    psobat.setString(2, "%" + TCari.getText() + "%");
                    psobat.setString(3, "%" + TCari.getText() + "%");
                    rs = psobat.executeQuery();
                    while (rs.next()) {
                        tabMode2.addRow(new Object[]{"", rs.getString(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4), 0});
                    }
                } catch (SQLException e) {
                    System.out.println(e);
                } finally {
                    if (rs != null) {
     

                        psobat.close();
                    }
                
                
                }
            } catch (SQLException e) {
                System.out.println("Notifikasi : " + e);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    private void isGanti() {
                    
                            
                            
                    
        String no_rw = TNoRw.getText();
        String tgl_op = Valid.SetTgl(tgl.getSelectedItem() + "") + " " + tgl.getSelectedItem().toString().substring(11, 19);
        Sequel.AutoComitFalse();
                            
                                    
                                    
                                    
            ttljmdokter = 0;
            ttljmpetugas = 0;
            ttlpendapatan = 0;
            ttlbhp = 0;
            status = "";
            status = Sequel.cariIsi("select status from operasi where no_rawat='" + no_rw + "' and tgl_operasi='" + tgl_op + "'");
            ttljmdokter = Sequel.cariIsiAngka("select sum(biayaoperator1+biayaoperator2+biayaoperator3+biayadokter_anak+"
                    + "biayadokter_anestesi+biaya_dokter_pjanak+biaya_dokter_umum) from operasi where no_rawat='" + no_rw + "' and tgl_operasi='" + tgl_op + "'");
            ttljmpetugas = Sequel.cariIsiAngka("select sum(biayaasisten_operator1+biayaasisten_operator2+biayaasisten_operator3+"
                    + "biayainstrumen+biayaperawaat_resusitas+biayaasisten_anestesi+biayaasisten_anestesi2+biayabidan+biayabidan2+"
                    + "biayabidan3+biayaperawat_luar+biaya_omloop+biaya_omloop2+biaya_omloop3+biaya_omloop4+biaya_omloop5) "
                    + "from operasi where no_rawat='" + no_rw + "' and tgl_operasi='" + tgl_op + "'");
            ttlpendapatan = Sequel.cariIsiAngka("select sum(operasi.biayaoperator1+operasi.biayaoperator2+"
                    + "operasi.biayaoperator3+operasi.biayaasisten_operator1+operasi.biayaasisten_operator2+"
                    + "operasi.biayaasisten_operator3+operasi.biayainstrumen+operasi.biayadokter_anak+"
                    + "operasi.biayaperawaat_resusitas+operasi.biayadokter_anestesi+operasi.biayaasisten_anestesi+"
                    + "operasi.biayaasisten_anestesi2+operasi.biayabidan+operasi.biayabidan2+operasi.biayabidan3+"
                    + "operasi.biayaperawat_luar+operasi.biayaalat+operasi.biayasewaok+operasi.akomodasi+"
                    + "operasi.bagian_rs+operasi.biaya_omloop+operasi.biaya_omloop2+operasi.biaya_omloop3+"
                    + "operasi.biaya_omloop4+operasi.biaya_omloop5+operasi.biayasarpras+operasi.biaya_dokter_pjanak+"
                    + "operasi.biaya_dokter_umum) from operasi where no_rawat='" + no_rw + "' and tgl_operasi='" + tgl_op + "'");
            ttlbhp = Sequel.cariIsiAngka("select sum(jumlah*hargasatuan) from beri_obat_operasi where no_rawat='" + no_rw + "' and tanggal='" + tgl_op + "'");

            ttlpendapatan = ttlpendapatan + ttlbhp;
            if (Sequel.queryutf("delete from operasi where no_rawat='" + no_rw + "' and tgl_operasi='" + tgl_op + "'") == true) {
                if (Sequel.queryutf("delete from beri_obat_operasi where no_rawat='" + no_rw + "' and tanggal='" + tgl_op + "'") == false) {
                    ttlbhp = 0;
                    sukses = false;
                }
            } else {
                sukses = false;
                           
            }

            if (sukses == true) {
//                Sequel.queryu("delete from laporan_operasi where no_rawat='" + no_rw + "' and tanggal='" + no_rw + "'");
                if (status.equals("Ranap")) {
                    Sequel.queryu("delete from tampjurnal");
                    if (ttlpendapatan > 0) {
                        Sequel.menyimpan("tampjurnal", "'" + Suspen_Piutang_Operasi_Ranap + "','Suspen Piutang Operasi Ranap','0','" + ttlpendapatan + "'", "kredit=kredit+'" + ttlpendapatan + "'", "kd_rek='" + Suspen_Piutang_Operasi_Ranap + "'");
                        Sequel.menyimpan("tampjurnal", "'" + Operasi_Ranap + "','Pendapatan Operasi Rawat Inap','" + ttlpendapatan + "','0'", "debet=debet+'" + ttlpendapatan + "'", "kd_rek='" + Operasi_Ranap + "'");
                    }
                    if (ttljmdokter > 0) {
                        Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Medik_Dokter_Operasi_Ranap + "','Beban Jasa Medik Dokter Operasi Ranap','0','" + ttljmdokter + "'", "kredit=kredit+'" + ttljmdokter + "'", "kd_rek='" + Beban_Jasa_Medik_Dokter_Operasi_Ranap + "'");
                        Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Medik_Dokter_Operasi_Ranap + "','Utang Jasa Medik Dokter Operasi Ranap','" + ttljmdokter + "','0'", "debet=debet+'" + ttljmdokter + "'", "kd_rek='" + Utang_Jasa_Medik_Dokter_Operasi_Ranap + "'");
                    }
                    if (ttljmpetugas > 0) {
                        Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Medik_Paramedis_Operasi_Ranap + "','Beban Jasa Medik Petugas Operasi Ranap','0','" + ttljmpetugas + "'", "kredit=kredit+'" + ttljmpetugas + "'", "kd_rek='" + Beban_Jasa_Medik_Paramedis_Operasi_Ranap + "'");
                        Sequel.menyimpan("tampjurnal " , "'" + Utang_Jasa_M
                                
                                hp > 0) {
                                el.menyimpan("tamp jurnal", "'" + HPP_Obat_Operasi_Ranap + "','HPP Persediaan Operasi Rawat Inap','0','" + ttlbhp + "'", "kredit=kredit+'" + ttlbhp + "'", "kd_rek='" + HPP_Obat_Operasi_Ranap + "'");
                        Sequel.menyimpan("tampjurnal", "'" + Persediaan_Obat_Kamar_Operasi_Ranap + "','Persediaan BHP Operasi Rawat Inap','" + ttlbhp + "','0'", "debet=debet+'" + ttlbhp + "'", "kd_rek='" + Persediaan_Obat_Kamar_Operasi_Ranap + "'");
                    }
                    sukses = jur.simpanJurnal(no_rw, "U", "PEMBATALAN OPERASI RAWAT INAP PASIEN OLEH " + akses.getkode());
                } else if (status.equals("Ralan")) {
                    Sequel.queryu("delete from tampjurnal");
                    if (ttlpendapatan > 0) {
                        Sequel.menyimpan("tampjurnal", "'" + Suspen_Piutang_Operasi_Ralan + "','Suspen Piutang Operasi Ralan','0','" + ttlpendapatan + "'", "kredit=kredit+'" + ttlpendapatan + "'", "kd_rek='" + Suspen_Piutang_Operasi_Ralan + "'");
                        Sequel.menyimpan("tampjurnal", "'" + Operasi_Ralan + "','Pendapatan Operasi Rawat Jalan','" + ttlpendapatan + "','0'", "debet=debet+'" + ttlpendapatan + "'", "kd_rek='" + Operasi_Ralan + "'");
                    }
                    if (ttljmdokter > 0) {
                        Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Medik_Dokter_Operasi_Ralan + "','Beban Jasa Medik Dokter Operasi Ralan','0','" + ttljmdokter + "'", "kredit=kredit+'" + ttljmdokter + "'", "kd_rek='" + Beban_Jasa_Medik_Dokter_Operasi_Ralan + "'");
                        Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Medik_Dokter_Operasi_Ralan + "','Utang Jasa Medik Dokter Operasi Ralan','" + ttljmdokter + "','0'", "debet=debet+'" + ttljmdokter + "'", "kd_rek='" + Utang_Jasa_Medik_Dokter_Operasi_Ralan + "'");
                    }
                    if (ttljmpetugas > 0) {
                        Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Medik_Paramedis_Operasi_Ralan + "','Beban Jasa Medik Petugas Operasi Ralan','0','" + ttljmpetugas + "'", "kredit=kredit+'" + ttljmpetugas + "'", "kd_rek='" + Beban_Jasa_Medik_Paramedis_Operasi_Ralan + "'");
                        Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Medik_Paramedis_Operasi_Ralan + "','Utang Jasa Medik Petugas Operasi Ralan','" + ttljmpetugas + "','0'", "debet=debet+'" + ttljmpetugas + "'", "kd_rek='" + Utang_Jasa_Medik_Paramedis_Operasi_Ralan + "'");
                    }
                    if (ttlbhp > 0) {
     

                    }
                    sukses = jur.simpanJurnal(no_rw, "U", "PEMBATALAN OPERASI RAWAT JALAN PASIEN OLEH " + akses.getkode());
                }
                
            }

            if (sukses == true) {
                Map<String, String> reportMap = new HashMap<>();
                try {
                    psobat = koneksi.prepareStatement("SELECT * FROM laporan_operasi lo JOIN dokter dr ON dr.kd_dokter = lo.kd_dokter WHERE lo.no_rawat =?");
                    try {
                        psobat.setString(1, TNoRw.getText());
                        rs = psobat.executeQuery();
                        while (rs.ne
                    xt()) {
                            //re
                    portList.add(rs.getString("nm_perawatan"));
                                    String kodePaket = rs.getString("tanggal").substring(0, 19);
                            
                            Strin
                    g namaPerawatan = rs.getString("tanggal").substring(0, 19) + " " + rs.getString("nm_dokter");
                                    reportMap.put(namaPerawatan, kodePaket);
                                }
                            } catch (SQLException e) {
                        System.out.println(e);
                    } finally {
                        if (rs != null) {
                            rs.close();
                        }
                        if (psobat != null) {
                            psobat.close();
                        }
                    }
                    
                } catch (SQLException e) {
                    
                    System.out.println("Notifikasi : " + e);
                }

                    
                List<String> reportLabels = new ArrayList<>(reportMap.keySet());
                        
                String[] reportArray = reportLabels.toArray(new String[0]);

                if (reportArray.length > 0) {
                    String selectedLabel = (String) JOptionPane.showInputDialog(
                            null,
                            "Silahkan pilih Laporan Operasi..!",
                            "Laporan operasi",
                            JOpti
                             null,
                // 
                            reportArray,
                            reportArray.length > 0 ? reportArray[0] : null // Default to the first report if available
                    );

                                
                                        
                                
                                
                    if (selectedLabel != null) {
                                
                                
                        String selectedValue = reportMap.get(selectedLabel);
                        //System.out.println("Selected report: " + selectedLabel + ", Value: " + selectedValue);
                        Valid.SetTgl2(tgl, selectedVal
                                e);
                                        
                                
                                
                    } else {
                                
                                        
                                
                                
                        System.out.println("No report selected.");
                    }
                }
                                
                                        
                                
                                

                                
                                        
                                
                                
                Sequel.AutoComitFalse();
                sukses = true;
                ttljmdokter = 0;
                                
                                        
                                
                ttljmpetugas = 0;
                                
                                        
                                
                ttlpendapatan = 0;
                ttlbhp = 0;
                            
                for (i = 0; i < tbtindakan.getRowCount(); i++) {
                    if (tabMode.getValueAt(i, 0).toString().equals("true")) {
                        if (Sequel.menyimpantf2("operasi", "'" + TNoRw.getText() + "','" + Valid.SetTgl(tgl.getSelectedItem() + "") + " " + tgl.getSelectedItem().toString().substring(11, 19)
                                + "','" + jenis.getTex
                                () + "','" + Kategori.getSelectedItem() + "','" + kdoperator1.getText() + "',
                                        " + kdoperator2.getTex
                                () + "','" + kdoperator3.getText()
                                
                                + "','" + kdasistopera
                                or1.getText() + "','" + kdasistoperator2.getText() + "','" + kdasistoperator3.getText()
                                + "','" + kdInstrumen.getText()
                                + "','" + kddranak.getText() + "','" + kdprwresust.getText() + "','" + kdanestesi.getText() + "','" + kdasistanestesi.getText() + "','" + kdasistanestesi2.getText()
                                + "','" + kdbidan.getText() + "','" + kdbidan2.getText() + "','" + kdbidan3.getText() + "','" + kdprwluar.getText()
                                + "','" + kdonloop1.ge
                                Text() + "','" + kdonloop2.getText() + "','
                                         + kdonloop3.getText() + "','" + kdonloop4.getText() + "','" + kdonloop5
                                getText()
                                
                                + "','" + kdpjanak.get
                                ext() + "','" + kddrumum.getText()
                                        
                                
                                
                                + "','" + tbtindakan.getValueAt(i, 1).toString()
                                + "','" + tbtindakan.getValueAt(i, 4).toString()
                                + "','" + tbtindakan.g
                                tValueAt(i, 5).toString()
                                        
                                
                                
                                + "','" + tbtindakan.g
                                tValueAt(i, 6).toString()
                                        
                                
                                
                                + "','" + tbtindakan.getValueAt(i, 7).toString()
                                + "','" + tbtindakan.getValueAt(i, 8).toString()
                                + "','" + tbtindakan.g
                                tValueAt(i, 9).toString()
                                        
                                
                                + "','" + tbtindakan.g
                                tValueAt(i, 10).toString()
                                        
                                
                                + "','" + tbtindakan.getValueAt(i, 11).toString()
                                + "','" + tbtindakan.getV
                            lueAt(i, 12).toString()
                                + "','" + tbtindakan.getValueAt(i, 13).toString()
                                + "','" + tbtindakan.getValueAt(i, 14).toString()
                                + "','" + tbtindakan.getValueAt(i, 15).toString()
                                + "','" + tbtindakan.getValueAt(i, 16).toString()
                                + "','" + tbtindakan.getValueAt(i, 17).toString()
                                + "','" + tbtindakan.getValueAt(i, 18).toString()
                                + "','" + tbtindakan.g
                            etValueAt(i, 19).toString()
                                + "','" + tbtindakan.getValueAt(i, 20).toString()
                                + "','" + tbtindakan.getValueAt(i, 21).toString()
                                + "','" + tbtindakan.getValueAt(i, 22).toString()
                                + "','" + tbtindakan.getValueAt(i, 23).toString()
                                 + "','" + tbtindakan.getValueAt(i, 24).toString()
                                + "','" + tbtindakan.getValueAt(i, 25).toString()
                                + "','" + tbtindakan.getValueAt(i, 26).toString()
                                    
                                + "','" + tbtindakan.getValueAt(i, 27).toString()
                                + "','" + tbtindakan.getValueAt(i, 28).toString()
                                + "','" + tbtindakan.getValueAt(i, 29).toString()
                                + "','" + tbtindakan.getValueAt(i, 30).toString()
                                + "','" + tbtindakan.getValueAt(i, 31).toString() + "','" + status + "'", "data") == true) {
                            ttljmdokter = ttljmdokter + Double.parseDouble(tbtindakan.getValueAt(i, 4).toString())
                                    + Double.parseDouble(tbtindakan.getValueAt(i, 5).toString())
                                    + Double.parseDouble(tbtindakan.getValueAt(i, 6).toString())
                                    + Double.parseDouble(tbtindakan.getValueAt(i, 11).toString())
                                    + Double.parseDouble(tbtindakan.getValueAt(i, 13).toString())
                                    + Double.parseDouble(tbtindakan.getValueAt(i, 30).toString())
                                    + Double.parseDouble(tbtindakan.getValueAt(i, 31).toString());
                            ttljmpetugas = ttljmpetugas + Double.parseDouble(tbtindakan.getValueAt(i, 7).toString())
                                    + Double.parseDouble(tbtindakan.getValueAt(i, 8).toString())
                                    + Double.parseDouble(tbtindakan.getValueAt(i, 9).toString())
                                    + Double.parseDouble(tbtindakan.getValueAt(i, 10).toString())
                                    + Double.parseDouble(tbtindakan.getValueAt(i, 12).toString())
                                    + Double.parseDouble(tbtindakan.getValueAt(i, 14).toString())
                                    + Double.parseDouble(tbtindakan.getValueAt(i, 15).toString())
                                    + Double.parseDouble(tbtindakan.getValueAt(i, 16).toString())
                                    + Double.parseDouble(tbtindakan.getValueAt(i, 17).toString())
                                    + Double.parseDouble(tbtindakan.getValueAt(i, 18).toString())
                                    + Double.parseDouble(tbtindakan.getValueAt(i, 19).toString())
                                    + Double.parseDouble(tbtindakan.getValueAt(i, 24).toString())
                                    + Double.parseDouble(tbtindakan.getValueAt(i, 25).toString())
                                    + Double.parseDouble(tbtindakan.getValueAt(i, 26).toString())
                                    + Double.parseDouble(tbtindakan.getValueAt(i, 27).toString())
                                    + Double.parseDouble(tbtindakan.getValueAt(i, 28).toString());
                            ttlpendapatan = ttlpendapatan + Double.parseDouble(tbtindakan.getValueAt(i, 32).toString());
                        } else {
                            sukses = false;
                        }
                    } 
                        // 
                }

                if (sukses == true) {
                    for (int r = 0; r < tbObat.getRowCount(); r++) {
                        if (Valid.SetAngka(tbObat.getValueAt(r, 0).toString()) > 0) {
                            if (Sequel.menyimpantf2("beri_obat_operasi", "'" + TNoRw.getText() + "','" + Valid.SetTgl(tgl.getSelectedItem() + "") + " " + tgl.getSelectedItem().toString().substring(11, 19)
                                    + "','" + tbObat.getValueAt(r, 1).toString() + "','" + tbObat.getValueAt(r, 4).toString()
                                    + "','" + tbObat.getValueAt(r, 0).toString() + "'", "data") == true) {
                                ttlbhp = ttlbhp + Double.parseDouble(tbObat.getValueAt(r, 5).toString());
                            } else {
                                sukses = false;
                            }
                        }
                    }
                    ttlpendapatan = ttlpendapatan + ttlbhp;
                                
                                
                }
                                
                if (sukses == true) {
                                
                    if (status.equals("Ranap")) {
                                
                                
                        Sequel.queryu("delete from tampjurnal");
                                
                        if (ttlpendapatan > 0) {
                                
                            Sequel.menyimpan("tampjurnal", "'" + Suspen_Piutang_Operasi_Ranap + "','Suspen Piutang Operasi Ranap','" + ttlpendapatan + "','0'", "debet=debet+'" + (ttlpendapatan) + "'", "kd_rek='" + Suspen_Piutang_Operasi_Ranap + "'");
                            Sequel.menyimpan("tampjurnal", "'" + Operasi_Ranap + "','Pendapatan Operasi Rawat Inap','0','" + ttlpendapatan + "'", "kredit=kredit+'" + (ttlpendapatan) + "'", "kd_rek='" + Operasi_Ranap + "'");
                        }
                        if (ttljmdokter > 0) {
                            Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Medik_Dokter_Operasi_Ranap + "','Beban Jasa Medik Dokter Operasi Ranap','" + ttljmdokter + "','0'", "debet=debet+'" + (ttljmdokter) + "'", "kd_rek='" + Beban_Jasa_Medik_Dokter_Operasi_Ranap + "'");
                            Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Medik_Dokter_Operasi_Ranap + "','Utang Jasa Medik Dokter Operasi Ranap','0','" + ttljmdokter + "'", "kredit=kredit+'" + (ttljmdokter) + "'", "kd_rek='" + Utang_Jasa_Medik_Dokter_Operasi_Ranap + "'");
                        }
                        if (ttljmpetugas > 0) {
                            Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Medik_Paramedis_Operasi_Ranap + "','Beban Jasa Medik Petugas Operasi Ranap','" + ttljmpetugas + "','0'", "debet=debet+'" + (ttljmpetugas) + "'", "kd_rek='" + Beban_Jasa_Medik_Paramedis_Operasi_Ranap + "'");
                            Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Medik_Paramedis_Operasi_Ranap + "','Utang Jasa Medik Petugas Operasi Ranap','0','" + ttljmpetugas + "'", "kredit=kredit+'" + (ttljmpetugas) + "'", "kd_rek='" + Utang_Jasa_Medik_Paramedis_Operasi_Ranap + "'");
                        }
                        if (ttlbhp > 0) {
                            Sequel.menyimpan("tampjurnal", "'" + HPP_Obat_Operasi_Ranap + "','HPP Persediaan Operasi Rawat Inap','" + ttlbhp + "','0'", "debet=debet+'" + (ttlbhp) + "'", "kd_rek='" + HPP_Obat_Operasi_Ranap + "'");
                            Sequel.menyimpan("tampjurnal", "'" + Persediaan_Obat_Kamar_Operasi_Ranap + "','Persediaan BHP Operasi Rawat Inap','0','" + ttlbhp + "'", "kredit=kredit+'" + (ttlbhp) + "'", "kd_rek='" + Persediaan_Obat_Kamar_Operasi_Ranap + "'");
                        }
                        sukses = jur.simpanJurnal(TNoRw.getText(), "U", "OPERASI RAWAT INAP PASIEN " + TPasien.getText() + " DIPOSTING OLEH " + akses.getkode());
                    } else if (status.equals("Ralan")) {
                        Sequel.queryu("delete from tampjurnal");
                        if (ttlpendapatan > 0) {
                            Sequel.menyimpan("tampjurnal", "'" + Suspen_Piutang_Operasi_Ralan + "','Suspen Piutang Operasi Ralan','" + ttlpendapatan + "','0'", "debet=debet+'" + (ttlpendapatan) + "'", "kd_rek='" + Suspen_Piutang_Operasi_Ralan + "'");
                            Sequel.menyimpan("tampjurnal", "'" + Operasi_Ralan + "','Pendapatan Operasi Rawat Inap','0','" + ttlpendapatan + "'", "kredit=kredit+'" + (ttlpendapatan) + "'", "kd_rek='" + Operasi_Ralan + "'");
                        }
                        if (ttljmdokter > 0) {
                            Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Medik_Dokter_Operasi_Ralan + "','Beban Jasa Medik Dokter Operasi Ralan','" + ttljmdokter + "','0'", "debet=debet+'" + (ttljmdokter) + "'", "kd_rek='" + Beban_Jasa_Medik_Dokter_Operasi_Ralan + "'");
                            Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Medik_Dokter_Operasi_Ralan + "','Utang Jasa Medik Dokter Operasi Ralan','0','" + ttljmdokter + "'", "kredit=kredit+'" + (ttljmdokter) + "'", "kd_rek='" + Utang_Jasa_Medik_Dokter_Operasi_Ralan + "'");
                        }
                        if (ttljmpetugas > 0) {
                            Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Medik_Paramedis_Operasi_Ralan + "','Beban Jasa Medik Petugas Operasi Ralan','" + ttljmpetugas + "','0'", "debet=debet+'" + (ttljmpetugas) + "'", "kd_rek='" + Beban_Jasa_Medik_Paramedis_Operasi_Ralan + "'");
                            Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Medik_Paramedis_Operasi_Ralan + "','Utang Jasa Medik Petugas Operasi Ralan','0','" + ttljmpetugas + "'", "kredit=kredit+'" + (ttljmpetugas) + "'", "kd_rek='" + Utang_Jasa_Medik_Paramedis_Operasi_Ralan + "'");
                        }
                                
                        if (ttlbhp > 0) {
                            Sequel.menyimpan("tampjurnal", "'" + HPP_Obat_Operasi_Ralan + "','HPP Persediaan Operasi Rawat Jalan','" + ttlbhp + "','0'", "debet=debet+'" + (ttlbhp) + "'", "kd_rek='" + HPP_Obat_Operasi_Ralan + "'");
                            Sequel.menyimpan("tampjurnal", "'" + Persediaan_Obat_Kamar_Operasi_Ralan + "','Persediaan BHP Operasi Rawat Jalan','0','" + ttlbhp + "'", "kredit=kredit+'" + (ttlbhp) + "'", "kd_rek='" + Persediaan_Obat_Kamar_Operasi_Ralan + "'");
                        }
                        sukses = jur.simpanJurnal(TNoRw.getText(), "U", "OPERASI RAWAT JALAN PASIEN " + TPasien.getText() + " DIPOSTING OLEH " + akses.getkode());
                    }
                }

                if (sukses == true) {
                    Sequel.Commit();
                    for (int r = 0; r < tbtindakan.getRowCount(); r++) {
                        tbtindakan.setValueAt(false, r, 0);
                    }
                    tampil();
                    for (int r = 0; r < tbObat.getRowCount(); r++) {
                        tbObat.setValueAt("", r, 0);
                    }
                    tampil2();
                    LTotal.setText("Total Biaya : 0");
                    JOptionPane.showMessageDialog(rootPane, "Proses simpan selesai...!");
                } else {
                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat pemrosesan data, transaksi dibatalkan.\nPeriksa kembali data sebelum melanjutkan menyimpan..!!");
                    Sequel.RollBack();
                }
                Sequel.AutoComitTrue();
            }

            if (sukses == true) {
                Sequel.Commit();
//            tampil();
            } else {
                JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat pemrosesan data, transaksi dibatalkan.\nPeriksa kembali data sebelum melanjutkan menyimpan..!!");
                Sequel.RollBack();
            }
                                    
                                            
                                            
                                            
                                            
                                    
            System.out.println(e);
        }

    }
    
    public void SetKategoriOperasi(String nama_paket, String kd_paket ) {
        String KodePaket = "";
        if (nama_paket.contains("CITO") || nama_paket.contains("2 Tindakan")) {
            KodePaket = Sequel.cariIsi("SELECT SUBSTRING(po.kode_paket,2,3) FROM paket_operasi po WHERE po.kode_paket = ?", kd_paket);
        } else {
            KodePaket = Sequel.cariIsi("SELECT SUBSTRING(po.kode_paket,1,3) FROM paket_operasi po WHERE po.kode_paket = ?", kd_paket);
        }
        switch (KodePaket) {
                                    
                                            
                                    
                                    
            case "OBK":
                                    
                                            
                                    
                Kategori.setSelectedIndex(1);
                break;
            case "OBS":
                                    
                                            
                                    
                                    
                Kategori.setSelectedIndex(2);
                                    
                                            
                                    
                                    
                break;
            case "OSD":
                Kategori.setSelectedIndex(3);
                                    
                                            
                                    
                                    
                break;
                                    
                                            
                                    
                                    
            case "OKC":
                Kategori.setSelectedIndex(4);
                break;
                                    
                                            
                                    
            default:
                                    
                                            
                                    
                                    
                Kategori.setSelectedIndex(0);
        }
                                
    }
 
    private void runBackground(Runnable task) {
        if (ceksukses) return;
                                    
                                            
                                    
                                    
        if (executor.isShutdown() || executor.isTerminated
                                    )) return;
                                            
                                    
        if (!isDisplayable()) return;

        ceksukses = true;
                                    
                                            
                                    
                                    
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_C
                                    RSOR));
                                            
                                    
                                    

        try {
            executor.submit(() -> {
                                    
                                            
                                    
                                    
                try {
                                    
                                            
                                    
                                    
                    task.run();
                } finally {
                    ceksukses = false;
                                    
                                            
                                    
                    SwingUtilities.invokeLater(() -> {
                                    
                                            
                                    
                                    
                        if (isDisplayable()) {
                            setCursor(Cursor.getDefaultCursor());
                                
                        }
                    });
                }
            });
        } catch (RejectedExecutionException ex) {
            ceksukses = false;
        }
    }
    
    @Override
    public void dispose() {
        executor.shutdownNow();
        super.dispose();
    }
}

                            
                 
                        

    
                    
                    

    
            
            
            

    