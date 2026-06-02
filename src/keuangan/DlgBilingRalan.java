package keuangan;

import bridging.ApiBRI;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.CheckPenjabMissmatch;
import fungsi.WarnaTable;
import fungsi.WarnaTable2;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import fungsi.akunbillingralan;
import fungsi.pengaturanbillingralan;
import inventory.DlgCariObat;
import inventory.DlgPenjualan;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariDokter;
import simrskhanza.DlgCariPeriksaLab;
import simrskhanza.DlgCariPeriksaRadiologi;
import simrskhanza.DlgCariPoli;
import inventory.DlgPemberianObat;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import javax.swing.event.DocumentEvent;
import modif.DlgGabungNota;
import modif.Eklaim.EklaimBridgingAPI;
import simrskhanza.DlgCariPeriksaLabPA;
import simrskhanza.DlgCariCaraBayar;
import simrskhanza.DlgCariPeriksaLabMB;
import simrskhanza.DlgPeriksaLaboratorium;
import simrskhanza.DlgPeriksaLaboratoriumMB;
import simrskhanza.DlgPeriksaLaboratoriumPA;
import simrskhanza.DlgPeriksaRadiologi;
import simrskhanza.DlgRawatJalan;
import simrskhanza.DlgTagihanOperasi;

/**
 *
 * @author perpustakaan
 */
public class DlgBilingRalan extends javax.swing.JDialog {
    private DefaultTableModel tabModeRwJlDr;
    private final DefaultTableModel tabModeTambahan, tabModePotongan, tabModeAkunBayar, tabModeAkunPiutang, tabModeLab,
            tabModeRad, tabModeApotek;
    public boolean sukses = false;
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private Jurnal jur = new Jurnal();
    private Connection koneksi = koneksiDB.condb();
    private ApiBRI apibri = new ApiBRI();    priate EklaimBridgingAPI eklaimApi = new EklaimBridgingAPI();
    private double ttl=0,y=0,su b ttl=0,ralanparamedis=0, piutang=0,itembayar=0,itempiutang=0, 
                   bayar=0, t otal=0,tamkur=0,detai ljs=0,d
                   ttlLaborat=0,tt l Radiologi=0,ttlObat=0,ttlR alan_Dokter=0,ttlRalan_Paramedis=0,
                   ttlTambahan=0,ttlPotongan=0,ttlRegistrasi=0,ttl           priate int i,r,cek,row2,countbayar=0,z=0,jml=0;
    private String not a _j a l an ="",do k te rrujukan="",po l ir ujukan= " ", status="" , bi aya="",tamb a ha
            medis ,  n m_pas i en , alam a t,  jk, umu r da ftar, tgl _ re gistrasi ,  n o_nota,tgl _ la
            kbilling=" s el ect count(bi l li ng.no_r a wa t) from billing   wh ere billing.no_raw a t=
            g="select r e g_ periksa.tgl _ re gistrasi,reg_ p er iksa.no_rkm_medis,reg _ pe riksa.k d _p oli,reg_pe r ik
              "reg_per i ks a.biaya_reg,current_time() as jam,reg_periksa.umurdaftar,reg_periksa.sttsumur "+
                       "fr om re g_periksa  w he r e  r eg_ p eriksa.no_rawat=?",
            sqlpscaripoli="se l ect  poliklinik.n m _po li from pol i kli nik wh e re  polik l ini k.kd_pol i =?"
            ,           
            sqlpscarialamat="select concat(pasien.alamat,', ',kelurahan.nm_kel,', ',k ecamatan.nm_kec,', ',kabupaten.nm_kab) from pasien "+
                        "in n er join kelurahan inner join kecamatan inner join kabupaten on pasien.kd_kel=kelurahan.kd_kel "+
                          "and pasien.kd_kec=kecamatan.kd_kec and pasien.kd_kab=kabupaten.kd_kab "+
                    
                       "where pasien.no_rkm_medis=?", 
                    erralan="select dokter.nm_dokter from reg_periksa "+
                              "inner join dokter on reg_periksa.kd_dokter=dokter.kd_dokter "+
                              "where reg_periksa.no_rawat=?",
                    
                    alan2="select dokter.nm_dokter from rujukan_internal_poli "+
                    
                        "inner join dokter on rujukan_internal_poli.kd_dokter=dokter.kd_dokt e
                        "where rujukan_internal_poli.no_rawat=?",
            sqlpscaripoli2=" s elect poliklinik.nm_poli from rujukan_inter n
                    "inner join poliklinik on rujukan_internal_poli.kd_poli=polikl i
                    "where rujukan_internal_poli.no_rawat=?",
            sqlpscariralandok t er="SELECT jns_perawatan.nm_perawatan,rawat_jl_dr.bia y
                    "SUM(rawat_jl_dr.biaya_rawat) AS biaya, SUM(rawat_jl_dr.bhp) AS totalbhp ,
                    "rawat_jl_dr.tarif_tindakandr,SUM(rawat_jl_dr.tarif_tindakandr) AS totaltarif_tindakandr,GROUP_CONCAT(dokter.nm_dokter SEPARATOR '; ') AS nama_dokter " +
                              "FROM rawat_jl_dr " + 
                    "INNER JOIN jns_perawatan ON rawat_jl_dr.kd_jenis_prw = jns_perawatan.kd_jen i
                    "INNER JOIN dokter ON rawat_jl_dr.kd_dokter = dokter.kd_dokter " +
                            "WHE R E rawat_jl_dr.no_rawat = ?" +
                    
                    "GROUP BY jns_perawatan.nm_perawatan",
                    
                    rawat="select jns_perawatan.nm_perawatan,rawat_jl_pr.biaya_rawat as total_byrpr,"+
                    
                    awat_jl_pr.kd_jenis_p
                    at_jl_pr.biaya_rawat) as biaya, "+
                    at_jl_pr.bhp) as totalbhp,"+
                    wat_jl_pr.material)+sum(rawat_jl_p
                    at_jl_pr.tarif_tindakanpr) as totaltarif_tindakanpr "+
                    "from rawat_j l _pr inner join jns_perawatan "+ 
                    "on rawat_jl_pr.kd_jenis_prw=jns_perawatan .kd_jenis_prw where "+
                    "rawat_jl_pr.no_rawat=? group by jns_pera watan.nm_perawatan ",
            sqlpscariralandrpr="select jns_perawatan.nm _perawatan,rawat_jl_drpr.biaya_rawat as total_byrdrpr,"+
                    "count(rawat_jl_drpr.kd_jenis_prw) as jml, "+ 
                    "sum(rawat_jl_drpr.biaya_rawat) as biaya,"+ 
                    "sum(rawat_jl_drpr.bhp) as totalbhp,"+ 
                    "(sum(rawat_jl_drpr.material)+sum(rawat_jl_drpr.menejemen)+sum( rawat_jl_drpr.kso)) as totalmaterial,"+
                    "rawat_jl_drpr.tarif_tindakandr,"+
                    "sum(rawat _ jl_drpr.tarif_tindakanpr) as totaltarif_tindakanpr, "+ 
                    "sum(rawat_jl_drpr.tarif_tindakandr) as tota ltarif_tindakandr "+
                    "from rawat_jl_drpr inner join jns_perawat an "+
                    "on rawat_jl_drpr.kd_jenis_prw=jns_pe rawatan.kd_jenis_prw where "+
                    "rawat_jl_drpr.no_rawat=? group by jns_perawatan.nm_perawatan",
                    
            sqlpscarilab="select jns_perawatan_lab.nm _perawatan, count(periksa_lab.kd_jenis_prw) as jml,periksa_lab.biaya as biaya, "+
                    "sum(periksa_lab.biaya) as total,jns_perawatan_lab.kd_jenis_prw, sum(periksa_lab.tarif_perujuk+periksa_lab.tarif_tindakan_dokter) as totaldokter, "+
                    "sum(periksa_lab.tarif_tindakan_petugas) as totalpetugas,sum(pe riksa_lab.kso) as totalkso,sum(periksa_lab.bhp) as totalbhp "+
                    " from periksa_lab inner join jns_perawatan_la b on jns_perawatan_lab.kd_jenis_prw=periksa_lab.kd_jenis_prw where "+
                    " periksa_lab.no_rawat=? group by periksa_lab.kd_jenis_prw  ", 
            sqlpscariobat="select databarang.nama_brng,jenis.nama,detail_pemberian_obat.biaya_obat,"+
                            "sum(detail_pemberian_obat.jml) as jml,sum(detail_pemberian_obat.embalase+detail_pemberian_obat.tuslah) as 
                    tambahan,"+
                          "(sum(detail_pemberian_obat.total)-sum(detail_pemberian_obat.embalase+detail_pemberian_obat.tuslah)) as total, "+
                    
                          "sum((detail_pemberian_obat.h_beli*detail_pemberian_obat.jml)) as totalbeli "+
                    
                          "from detail_pemberian_obat inner join databarang inner join jenis "+
                    
                          "on detail_pemberian_obat.kode_brng=databarang.kode_brng and databarang.kdjns=jenis.kdjns where "+
                            "detail_pemberian_obat.no_rawat=? group by detail_pemberian_obat.kode_brng  
                    ="select sum(detail_periksa_lab.biaya_item) as total,sum(detail_periksa_lab.bagian_perujuk+detail_periksa_lab.bagian_
                    d
                     "sum(detail_periksa_lab.bagian_laborat) as totalpetugas,sum(detail_periksa_lab.kso) as totalkso,sum(detail_peri
                    k
                     "from detail_periksa_lab where detail_periksa_lab.no_rawat=? "+ 
                     "and detail_periksa_lab.kd_jenis_prw=?", 
                    ung="select tagihan_obat_langsung.besar_tagihan from tagihan_obat_langsung where tagihan_obat_lan g
                    "select tambahan_biaya.nama_biaya,tambahan_biaya.besar_biaya from tambahan_biaya where tambahan_biaya.no_rawat=?  ",
            sqlpsbiling="i n sert into billing values(?,?,?,?,?,?,?,?,?,?,?)",
                    
                    "insert into temporary_bayar_ralan values(?,?,?,?,?,?,?,?,?,?,'','','','','','','','')",
                    
                    select pengurangan_biaya.nama_pengurangan,pengurangan_biaya.be s
                    elect billing.no,billing.nm_perawatan, if(billing.biaya<>0,billing.biaya,null) as satu, if(billing.jumlah<>0,billing.jumlah,null) as dua," +
                         "if( b illing.tambahan<>0,billing.tambahan,null) as tiga, if(billing.totalbiaya<>0,billing.totalbiaya,null) as empat,billing.pemisah,billing.status, detail_billing.rincian " +
                          " from billing LEFT JOIN detail_billing ON detail_billing.no_rawat = billing.no_rawat AND detail_billing.noindex = billing.noindex " +
                           "where billing.no_rawat=? order by billing.noindex",
            sqlpscariradio l ogi="select jns_perawatan_radiologi.nm_perawatan, count(periksa_radiologi.kd_jenis_prw) as jml,periksa_radiologi.biaya as biaya, "+
                    "sum( p eriksa_radiologi.biaya) as total,jns_perawatan_radiologi.kd_jenis_prw,sum(periksa_radiologi.tarif_perujuk+periksa_radiologi.tarif_tindakan_dokter) as totaldokter, "+
                    "sum ( periksa_radiologi.tarif_tindakan_petugas) as totalpetugas,sum(periksa_radiologi.kso) as totalkso,sum(periksa_radiologi.bhp) as totalbhp "+
                    
                    m periksa_radiologi inner join jns_perawatan_radiologi on jns_perawatan_radiologi.kd_jenis_prw=periksa_radiologi.kd_jenis_prw where "+
                    
                    iksa_radiologi.no_rawat=? group by periksa_radiologi.kd_jenis_prw  ",
                    
                    "select paket_operasi.nm_perawatan,(operasi.biayaoperator1+operasi.biayaoperator2+"+
                         "oper a si.biayaoperator3+operasi.biayaasisten_operator1+operasi.biayaasisten_operator2+"+
                    
                         "operasi.biayaasisten_operator3+operasi.biayainstrumen+operasi.biayadokter_anak+"+
                    
                         "operasi.biayaperawaat_resusitas+operasi.biayadokter_anestesi+operasi.biayaasisten_anestesi+"+
                    
                         "operasi.biayaasisten_anestesi2+operasi.biayabidan+operasi.biayabidan2+operasi.biayabidan3+"+
                    
                         "operasi.biayaperawat_luar+operasi.biayaalat+operasi.biayasewaok+operasi.akomodasi+"+
                           "operasi.bagian_rs+operasi.biaya_omloop+operasi.biaya_omloop2+operasi.biaya_omloop3 +
                    "operasi.biaya_omloop4+operasi.biaya_omloop5+operasi.biayasarpras+operasi.biaya_dokter_ p
                    "operasi.biaya_dokter_umum) as biaya,operasi.biayaoperator1,"+ 
                    "operasi.biayaoperator2,operasi.biayaoperator3,operasi.biayaasisten_operator1,operasi.biayaas i
                    "operasi.biayainstrumen,operasi.biayadokter_anak,operasi.biayaperawaat_resusitas,"+ 
                    "operasi.biayadokter_anestesi,operasi.biayaasisten_anestesi,operasi.biayaasisten_ane s
                    "operasi.biayaalat,operasi.biayasewaok,operasi.akomodasi,operasi.bagian_rs,operasi.bi a
                    "operasi.biayasarpras,operasi.biaya_dokter_pjanak,operasi.biaya_dokter_umum "+ 
                    "from operasi inner join paket_operasi "+ 
                    "on operasi.kode_paket=paket_operasi.kode_paket where "+
                    
                    "operasi.no_rawat=?", 
                    asi="select obatbhp_ok.nm_obat,beri_obat_operasi.hargasatuan,beri_obat_operasi.jumlah, "+
                    
                    i_obat_operasi.hargasatuan*beri_obat_operasi.jumlah) as total "+
                    
                     obatbhp_ok inner join beri_obat_operasi "+ 
                    eri_obat_operasi.kd_obat=obatbhp_ok.kd_o b
                    _obat_operasi.no_rawat=? group by obatbhp_ok.nm_obat", 
                    iling="insert into detail_billing values (?,?,?)",
            sqlpstamkur="sel e ct temporary_tambahan_potongan.biaya from temporary_tambahan_potongan where temporar y_tambahan_potongan.no_rawat=? and temporary_tambahan_potongan.nama_tambahan=? and temporary_tambahan_potongan.status=?",
            Host_to_Host_Bank_Jateng="",Akun_BRI_API="",Host_to_Host_Bank_Papua="",Host_ to_Host_Bank_Jabar="",Host_to_Host_Bank_Mandiri="",KodeBankJabar="",PPN_Keluaran="";
    private String[] Nama_Akun_Piutang,Kode_Rek_Piutang,Kd_PJ,Besar _Piutang,Jatuh_Tempo,
            Nama_Akun_Bayar,Kode_Rek_Bayar,Bayar,PPN_Persen,PPN_Besar; 
            
    private PreparedStatement  p scaripoli2,pscekbilling,pscarirm,pscaripasien,psreg,pscaripoli,pscarialamat,pssetnota,psrekening,
            psdokterral a n,psdokterralan2,pscariralandokter,pscariralanperawat,pscariralandrpr,pscarilab,pscariobat,psdetaillab,
            psobatlangsung,pstambaha n ,ps biling,pstem p ora ry,pspotongan,psbilling , psc
            ariradiologi,           
            pstamkur,psnota,psoperasi,p sobatoperasi,psak unbaya r,psakunpiutan g, psrincianbilling;
    private ResultSet rscekb illing,rscarirm ,rscar ipasien,rsr eg,rscarip

            rsobatlangsung,rstambahan,rspo tongan,rsbill ing,rscar iradiologi,rs tamkur ,rsoperasi, 
             
            rsakunbayar,rs akunpiutang,rsc aripoli2, rsrincia nbilling;  
             
    private WarnaTable2 war na=new Warn aTable2() ;       pri
    ate  File file ;    
    private FileWriter fileWriter;      
                priJsonNode root;
    private JsonNode response;      
                priate Fil
    eRea       
            
      
    /** Creates new form  DlgBiling 
     * @param parent  
     * @param modal */  
    public DlgBilingRalan(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        tabModeRwJlDr=new DefaultTableModel(null,new Object[]{
            "Pilih","Keterang

       
     *             if ((colIndex==6)||(colIndex==0)) {
       
     *                  a=true;
                   
     }
                    return a;
              }
              

                java. l ang.Boolean.class, java.lan g.Object.cla s
                    java .lang.Double. class, java.lang.Double.cl ass , java.l ang.Do uble.class,  java.lang.Dou ble
                class,   
              java.la
            g.Object.class, java.lang.Object.class,  
                
                de      
                    s   getCo
                r
                
            

            
                    gas.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbPetugas.getBack
                    g.setPreferredScrollableViewportSize(new Dimension(800,800));
                    g.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            

            i = 0; i 
            ableColumn column = tbBilling.getColumnModel().getColumn(i);
            if(i==0){
               column.setPreferredWidth(30);
            }else if(i==1){
                column.setPreferredWidth(160);
            }else if(i==2){
                 column.setPreferredWidth(420);
        // 
            }else if(i==3){ 
                column.setPreferredWidth(10);

                column.setPreferredWidth(95);
            }else if(i==5){
                  co lu mn.setPreferredWidth(30);
            }else if(i==6){
                 colu mn .s et PreferredWidth(80);
            }else if(i==7){
                 colu mn .s et PreferredWidth(100);
            }else if(i==8){
                 colu mn .s et MinWidth(0);
                column.setMaxWidth(0);
            } else if (i == 9) {
                column.setPreferredWidth(160);
            }     
        }
     
        tbBilling.setDefaultRenderer(Object.class, new WarnaTable());
             
        //tambahan biaya
        tabMo deTamba ha n= ne w DefaultTableModel(null,new Object[]{"Tambahan Biaya","Besar Biaya"}){
              @Override 
              public boolean isCellEditable(int rowIndex, int colIndex){return true;}
              Class[]  t yp es  = new Class[] {
                java.lang.Object.class, java.lang.Object.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];

        }; 
        tbTambahan.setM o del(tabModeTambahan);      
            
            ahan.setPreferredScrollableViewportSize(new Dimension(800 ,
                800));
            

            bahan.setAutoResizeMode(JTabl
                    
            i 

            ableColum
            f(i==0){
                column.setPrferredWidth(30
            else if(i==1){
                column.setPreferredWidth(150);
            }
        }
        tbTambahan.setDefaultRenderer(Object.class, new WarnaTable()); 
        
        //potongan biaya
        Object[] rowPotongan={"P", "Potongan Biaya","Besar Potongan"};
        tabModePotongan=new DefaultTableModel(null,rowPotongan){
               @O ve rr ide 
              public boolean isCellEditable(int rowIndex, int colIndex){return true;}
              Class[]  t yp es  = new Class[] {
                java.lang.Boolean.class,  java.lang.Object.class, java.lang.Object.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {

              }
        };     
        tbPotongan.setM o del(tabModePotongan);  
            
            ngan.setPreferredScrollableViewportSize(new Dimension(800 ,
                800));
            

            ongan.setAutoResizeMode(JTabl
                     
            i 

            ableColum
            f(i==0){
                column.setPrferredWidth(20
            else if(i==1){
                column.setPreferredWidth(300);
            }else if(i==2){
                column.setPreferredWidth(150);
            } 
        }
        tbPotongan.setDefaultRenderer(Object.class, new WarnaTable());
        
        tabModeAkunBayar=new DefaultTableModel(null,new Object[]{"Nama Akun","Kode Rek","Bayar","PPN(%)","PPN(Rp)"}){             
            @O ve rr id e public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = false;
                 if ( (c ol In dex==2)) {
                    a=true;
                 }    
                return a;
            }
             Class[] types = new Class[] {
                java.lang.Object.class, java.lang.Object.class, java.l

             };  
                        
             @Overrid
             
             public Class getColumnClass(int columnIndex) {
                return types  [c olumnIndex];
             }  
        };
        tbAkunBayar.setModel(tabModeAkunBayar);


            nBayar.setPreferredScrollable
                    yar.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    
            i 

            ableColum
            f(i==0){
                column.setPrferredWidth(34
            else if(i==1){
                //column.setPreferredWidth(70);
                column.setMinWidth(0);
                column.setMaxWidth(0);
            }else if(i==2){ 
                column.setPreferredWidth(112);
            }else if(i==3){
                column.setPreferredWidth(60);
            }else if(i==4){
                  co lu mn.setPreferredWidth(90);
            }
        }     
        warna.kolo m=2;
        tbAkunBayar.setDefaultRenderer(Object.class,warna);
        
        tabMo deAkunP iu ta ng =new DefaultTableModel(null,new Object[]{"Nama Akun","Kode Rek","Kd PJ","Piutang","Jatuh Tempo"}){
             @Override public boolean isCellEditable(int rowIndex, int colIndex){
                 bool ea n  a  = false;
                if ((colIndex==3)||(colIndex==4)) {
                      a= tr ue ;
                }
                return a;
             }
             Class[ ]  types = new Class[] {
                java.lang.Object.class, java.lang.Ob ject.cl

             };  
                        
            @Override
             
             public Class getColumnClass(int columnIndex) {
                return types  [c ol um nIndex];  
             }  
        };
        tbAkunPiutang.set
            

            nPiutang.setPreferredScrollab
                    utang.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    
            i 

            ableColum
            f(i==0){
                column.setPrferredWidth(40
            else if(i==1){
                //column.setPreferredWidth(70);
                column.setMinWidth(0);
                column.setMaxWidth(0);
            }else if(i==2){ 
                //column.setPreferredWidth(70);
                column.setMinWidth(0);
                column.setMaxWidth(0);
            }else if(i==3){
                  co lu mn.setPreferredWidth(112);
            }else if(i==4){
                 colu mn .s et PreferredWidth(90);
            } 
        }
        warna2.kolom=3;
        tbAku nPiutan g. se tD efaultRenderer(Object.class,warna2);
         
        tabModeLab=new DefaultTableModel(null,new Object[]{
            "No.Permintaan","Tanggal","Jam","Dokter Perujuk","Status"
            } ){    
              @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };     
        tbLab.setModel(tabModeLab);

        tbLab.setPreferredScrollableViewportSize(new Dimension(800,800));
        tbLab.setAut o ResizeMode(JTable.AUTO_RESIZE_OFF);
         

            TableC o lumn column = tbLab.getColu mnModel().ge t
                if(i==0){    
            
            lse if(i=
            1){ 
                
            
                column.setPreferredWidth(65);
            }else if(i==2){
                column.setPreferredWidth(55);
            }else if(i==3){ 
                column.setPreferredWidth(200);

                column.setPreferredWidth(100);
            }
        }    
        tbLab.setDefaultRenderer(Object.class, new WarnaTable());
             
        tabModeRad=new DefaultTableModel(null,new Object[]{
            " No.Perm in ta an ","Tanggal","Jam","Dokter Perujuk","Status"
            }){
               @Overr id e  pu blic boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        tbRad iologi. se tM od el(tabModeRad);

        tbRadiologi.setPreferredScrollableViewportSize(new Dimension(800,800));
        tbRadiologi.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        

            TableC o lumn column = tbRadiologi.g etColumnMode l
                if(i==0){    
            
            lse if(i=
            1){ 
                
            
                column.setPreferredWidth(65);
            }else if(i==2){
                column.setPreferredWidth(55);
            }else if(i==3){ 
                column.setPreferredWidth(200);

                column.setPreferredWidth(100);
            }
        }    
        tbRadiologi.setDefaultRenderer(Object.class, new WarnaTable());
             
        tabModeApotek=new DefaultTableModel(null,new Object[]{
            " No.Rese p" ," Ta nggal","Jam","Dokter Peresep","Status"
            }){
               @Overr id e  pu blic boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        tbApo tek.set Mo de l( tabModeApotek);

        tbApotek.setPreferredScrollableViewportSize(new Dimension(800,800));
        tbApotek.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        

            TableColu m n column = tbApotek.getColu mnModel().ge t
                if(i==0){    
            
            lse if(i=
            1){ 
                
            
                column.setPreferredWidth(65);
            }else if(i==2){
                column.setPreferredWidth(55);
            }else if(i==3){ 
                column.setPreferredWidth(200);

                column.setPreferredWidth(100);
            }
        }    
        tbApotek.setDefaultRenderer(Object.class, new WarnaTable());
     
        TNoRw.setDocument(new batasInput((byte)17).getKata(TNoRw));
        kdpol i.setDo cu me nt (new batasInput((byte)5).getKata(kdpoli));
        kddokter.setDocument(new batasInput((byte)20).getKata(kddokter));       
        Total Obat.se tD oc um ent(new batasInput((byte)17).getOnlyAngka(TotalObat));
        
        TCari .setDoc um en t( new batasInput((int)100).getKata(TCari));
        TCari1.setDocument(new batasInput((int)100).getKata(TCari1));
        
        if(pengaturanbillingralan.getCentangDokterRalan().equals("")){
            pengaturanbillingralan.SetBillingRalan();
        }
    } 
 
     
        pressWarnings("unchecked") 

        ate void initComponents() { 
 

        Mn InputTindakan = new javax.swing.JMenuItem(); 
        MnInputObat = new javax.swing.JMenuItem();
        MnPeriksaLab = new javax.swing.JMenuItem();
     

        MnTambahan = new javax.swing.JMenuItem();
        MnPotongan = new javax.swing.JMenuItem();
        MnOperasi = new javax.swing.JMenuItem();
        MnObatLangsung = new javax.swing.JMenuItem();
        MnPoli = new javax.swing.JMenuItem();
        MnDokter = new javax.swing.JMenuItem();
        MnPenjab = new javax.swing.JMenuItem();
        MnRawatJalan = new javax.swing.JMenuItem();
        MnPemberianObat = new javax.swing.JMenuItem();
        MnCariPeriksaLab = new javax.swing.JMenuItem();
        MnCariPeriksaLabPA = new javax.swing.JMenuItem();
        MnCariPeriksaLabMB = new javax.swing.JMenuItem();
        MnCariRadiologi = new javax.swing.JMenuItem();
        MnPenjualan = new javax.swing.JMenuItem();
        MnGabungNota = new javax.swing.JMenuItem();
        MnHapusTagihan = new javax.swing.JMenuItem();
        MnFreeKarcis = new javax.swing.JMenuItem();
        WindowGantiDokterPoli = new javax.swing.JDialog();
        internalFrame3 = new widget.InternalFrame();
        BtnCloseIn1 = new widget.Button();
        BtnSimpan1 = new widget.Button();
        jLabel13 = new widget.Label();
        kddokter = new widget.TextBox();
        TDokter = new widget.TextBox();
        btnCariDokter = new widget.Button();
        WindowObatLangsung = new javax.swing.JDialog();
        internalFrame2 = new widget.InternalFrame();
        TotalObat = new widget.TextBox();
        jLabel8 = new widget.Label();
        BtnCloseIn = new widget.Button();
        BtnSimpan2 = new widget.Button();
        BtnBatal1 = new widget.Button();
        WindowTambahanBiaya = new javax.swing.JDialog();
        internalFrame4 = new widget.InternalFrame();
        scrollPane1 = new widget.ScrollPane();
        tbTambahan = new widget.Table();
        panelisi1 = new widget.panelisi();
        label15 = new widget.Label();
        norawat = new widget.TextBox();
        BtnTambah = new widget.Button();
        BtnSimpan3 = new widget.Button();
        BtnHapus = new widget.Button();
        BtnKeluar1 = new widget.Button();
        WindowGantiPoli = new javax.swing.JDialog();
        internalFrame5 = new widget.InternalFrame();
        BtnCloseIn4 = new widget.Button();
        BtnSimpan4 = new widget.Button();
        jLabel14 = new widget.Label();
        kdpoli = new widget.TextBox();
        nmpoli = new widget.TextBox();
        btnCariPoli = new widget.Button();
        WindowPotonganBiaya = new javax.swing.JDialog();
        internalFrame6 = new widget.InternalFrame();
        scrollPane2 = new widget.ScrollPane();
        tbPotongan = new widget.Table();
        panelisi2 = new widget.panelisi();
        label16 = new widget.Label();
        norawatpotongan = new widget.TextBox();
        BtnTambahPotongan = new widget.Button();
        BtnSimpanPotongan = new widget.Button();
        BtnHapusPotongan = new widget.Button();
        BtnKeluarPotongan = new widget.Button();
        WindowGantiPenjab = new javax.swing.JDialog();
        internalFrame7 = new widget.InternalFrame();
        BtnCloseIn5 = new widget.Button();
        BtnSimpan5 = new widget.Button();
        jLabel17 = new widget.Label();
        kdpenjab = new widget.TextBox();
        nmpenjab = new widget.TextBox();
        btnPenjab = new widget.Button();
        PopupBayar = new javax.swing.JPopupMenu();
        ppBersihkan = new javax.swing.JMenuItem();
        PopupPiutang = new javax.swing.JPopupMenu();
        ppBersihkan1 = new javax.swing.JMenuItem();
        internalFrame1 = new widget.InternalFrame();
        panelGlass1 = new widget.panelisi();
        jLabel3 = new widget.Label();
        TNoRw = new widget.TextBox();
        TNoRM = new widget.TextBox();
        TPasien = new widget.TextBox();
        BtnCari = new widget.Button();
        jLabel4 = new widget.Label();
        DTPTgl = new widget.Tanggal();
        HKLabel = new widget.Label();
        THakKelas = new widget.TextBox();
        NKLabel = new widget.Label();
        TNaikKelas = new widget.TextBox();
        NoSEPLabel = new widget.Label();
        TNoSEP = new widget.TextBox();
        TabRawat = new javax.swing.JTabbedPane();
        Scroll = new widget.ScrollPane();
        tbBilling = new widget.Table();
        scrollPane8 = new widget.ScrollPane();
        panelBayar = new widget.panelisi();
        TtlSemua = new widget.TextBox();
        TKembali = new widget.TextBox();
        jLabel5 = new widget.Label();
        jLabel9 = new widget.Label();
        chkPotongan = new widget.CekBox();
        chkLaborat = new widget.CekBox();
        chkTarifDokter = new widget.CekBox();
        chkTarifPrm = new widget.CekBox();
        chkRadiologi = new widget.CekBox();
        chkTambahan = new widget.CekBox();
        chkObat = new widget.CekBox();
        jLabel12 = new widget.Label();
        chkSarpras = new widget.CekBox();
        TagihanPPn = new widget.TextBox();
        chkAdministrasi = new widget.CekBox();
        scrollPane3 = new widget.ScrollPane();
        tbAkunBayar = new widget.Table();
        jLabel6 = new widget.Label();
        scrollPane4 = new widget.ScrollPane();
        tbAkunPiutang = new widget.Table();
        jLabel16 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCariBayar = new widget.Button();
        TCari1 = new widget.TextBox();
        btnCariPiutang = new widget.Button();
        BtnAll = new widget.Button();
        BtnAll1 = new widget.Button();
        panelPermintaan = new widget.panelisi();
        scrollPane5 = new widget.ScrollPane();
        tbLab = new widget.Table();
        scrollPane6 = new widget.ScrollPane();
        tbRadiologi = new widget.Table();
        scrollPane7 = new widget.ScrollPane();
        tbApotek = new widget.Table();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnNota = new widget.Button();
        BtnView = new widget.Button();
        BtnKeluar = new widget.Button();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnInputTindakan.setBackground(new java.awt.Color(255, 255, 254));
        MnInputTindakan.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnInputTindakan.setForeground(new java.awt.Color(50, 50, 50));
        MnInputTindakan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnInputTindakan.setText("Input Tindakan Ralan");
        MnInputTindakan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnInputTindakan.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnInputTindakan.setName("MnInputTindakan"); // NOI18N
        MnInputTindakan.setPreferredSize(new java.awt.Dimension(250, 25));
        MnInputTindakan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnInputTindakanActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnInputTindakan);

        MnInputObat.setBackground(new java.awt.Color(255, 255, 254));
        MnInputObat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnInputObat.setForeground(new java.awt.Color(50, 50, 50));
        MnInputObat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnInputObat.setText("Input Obat/Barang/Alkes");
        MnInputObat.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnInputObat.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnInputObat.setName("MnInputObat"); // NOI18N
        MnInputObat.setPreferredSize(new java.awt.Dimension(250, 25));
        MnInputObat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnInputObatActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnInputObat);

        MnPeriksaLab.setBackground(new java.awt.Color(255, 255, 254));
        MnPeriksaLab.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnPeriksaLab.setForeground(new java.awt.Color(50, 50, 50));
        MnPeriksaLab.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnPeriksaLab.setText("Input Periksa Lab PK");
        MnPeriksaLab.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnPeriksaLab.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnPeriksaLab.setName("MnPeriksaLab"); // NOI18N
        MnPeriksaLab.setPreferredSize(new java.awt.Dimension(250, 28));
        MnPeriksaLab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnPeriksaLabActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnPeriksaLab);

        MnPeriksaLabPA.setBackground(new java.awt.Color(255, 255, 254));
        MnPeriksaLabPA.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnPeriksaLabPA.setForeground(new java.awt.Color(50, 50, 50));
        MnPeriksaLabPA.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnPeriksaLabPA.setText("Input Periksa Lab PA");
        MnPeriksaLabPA.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnPeriksaLabPA.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnPeriksaLabPA.setName("MnPeriksaLabPA"); // NOI18N
        MnPeriksaLabPA.setPreferredSize(new java.awt.Dimension(250, 28));
        MnPeriksaLabPA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnPeriksaLabPAActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnPeriksaLabPA);

        MnPeriksaLabMB.setBackground(new java.awt.Color(255, 255, 254));
        MnPeriksaLabMB.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnPeriksaLabMB.setForeground(new java.awt.Color(50, 50, 50));
        MnPeriksaLabMB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnPeriksaLabMB.setText("Input Periksa Lab MB");
        MnPeriksaLabMB.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnPeriksaLabMB.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnPeriksaLabMB.setName("MnPeriksaLabMB"); // NOI18N
        MnPeriksaLabMB.setPreferredSize(new java.awt.Dimension(250, 28));
        MnPeriksaLabMB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnPeriksaLabMBActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnPeriksaLabMB);

        MnPeriksaRadiologi.setBackground(new java.awt.Color(255, 255, 254));
        MnPeriksaRadiologi.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnPeriksaRadiologi.setForeground(new java.awt.Color(50, 50, 50));
        MnPeriksaRadiologi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnPeriksaRadiologi.setText("Input Periksa Radiologi");
        MnPeriksaRadiologi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnPeriksaRadiologi.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnPeriksaRadiologi.setName("MnPeriksaRadiologi"); // NOI18N
        MnPeriksaRadiologi.setPreferredSize(new java.awt.Dimension(250, 28));
        MnPeriksaRadiologi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnPeriksaRadiologiActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnPeriksaRadiologi);

        MnTambahan.setBackground(new java.awt.Color(255, 255, 254));
        MnTambahan.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnTambahan.setForeground(new java.awt.Color(50, 50, 50));
        MnTambahan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnTambahan.setText("Tambahan Biaya");
        MnTambahan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnTambahan.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnTambahan.setName("MnTambahan"); // NOI18N
        MnTambahan.setPreferredSize(new java.awt.Dimension(250, 25));
        MnTambahan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnTambahanActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnTambahan);

        MnPotongan.setBackground(new java.awt.Color(255, 255, 254));
        MnPotongan.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnPotongan.setForeground(new java.awt.Color(50, 50, 50));
        MnPotongan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnPotongan.setText("Potongan Biaya");
        MnPotongan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnPotongan.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnPotongan.setName("MnPotongan"); // NOI18N
        MnPotongan.setPreferredSize(new java.awt.Dimension(250, 25));
        MnPotongan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnPotonganActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnPotongan);

        MnOperasi.setBackground(new java.awt.Color(255, 255, 254));
        MnOperasi.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnOperasi.setForeground(new java.awt.Color(50, 50, 50));
        MnOperasi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnOperasi.setText("Tagihan Operasi/VK");
        MnOperasi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnOperasi.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnOperasi.setName("MnOperasi"); // NOI18N
        MnOperasi.setPreferredSize(new java.awt.Dimension(250, 28));
        MnOperasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnOperasiActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnOperasi);

        MnObatLangsung.setBackground(new java.awt.Color(255, 255, 254));
        MnObatLangsung.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnObatLangsung.setForeground(new java.awt.Color(50, 50, 50));
        MnObatLangsung.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnObatLangsung.setText("Tagihan BHP & Obat");
        MnObatLangsung.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnObatLangsung.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnObatLangsung.setName("MnObatLangsung"); // NOI18N
        MnObatLangsung.setPreferredSize(new java.awt.Dimension(250, 25));
        MnObatLangsung.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnObatLangsungActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnObatLangsung);

        MnPoli.setBackground(new java.awt.Color(255, 255, 254));
        MnPoli.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnPoli.setForeground(new java.awt.Color(50, 50, 50));
        MnPoli.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnPoli.setText("Ganti Poliklinik");
        MnPoli.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnPoli.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnPoli.setName("MnPoli"); // NOI18N
        MnPoli.setPreferredSize(new java.awt.Dimension(250, 25));
        MnPoli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnPoliActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnPoli);

        MnDokter.setBackground(new java.awt.Color(255, 255, 254));
        MnDokter.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnDokter.setForeground(new java.awt.Color(50, 50, 50));
        MnDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnDokter.setText("Ganti Dokter Poli");
        MnDokter.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnDokter.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnDokter.setName("MnDokter"); // NOI18N
        MnDokter.setPreferredSize(new java.awt.Dimension(250, 25));
        MnDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnDokterActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnDokter);

        MnPenjab.setBackground(new java.awt.Color(255, 255, 254));
        MnPenjab.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnPenjab.setForeground(new java.awt.Color(50, 50, 50));
        MnPenjab.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnPenjab.setText("Ganti Jenis Bayar");
        MnPenjab.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnPenjab.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnPenjab.setName("MnPenjab"); // NOI18N
        MnPenjab.setPreferredSize(new java.awt.Dimension(250, 28));
        MnPenjab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnPenjabActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnPenjab);

        MnRawatJalan.setBackground(new java.awt.Color(255, 255, 254));
        MnRawatJalan.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnRawatJalan.setForeground(new java.awt.Color(50, 50, 50));
        MnRawatJalan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnRawatJalan.setText("Data Tagihan/Tindakan Rawat Jalan");
        MnRawatJalan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnRawatJalan.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnRawatJalan.setName("MnRawatJalan"); // NOI18N
        MnRawatJalan.setPreferredSize(new java.awt.Dimension(250, 25));
        MnRawatJalan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnRawatJalanActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnRawatJalan);

        MnPemberianObat.setBackground(new java.awt.Color(255, 255, 254));
        MnPemberianObat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnPemberianObat.setForeground(new java.awt.Color(50, 50, 50));
        MnPemberianObat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnPemberianObat.setText("Data Pemberian Obat");
        MnPemberianObat.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnPemberianObat.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnPemberianObat.setName("MnPemberianObat"); // NOI18N
        MnPemberianObat.setPreferredSize(new java.awt.Dimension(250, 25));
        MnPemberianObat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnPemberianObatActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnPemberianObat);

        MnCariPeriksaLab.setBackground(new java.awt.Color(255, 255, 254));
        MnCariPeriksaLab.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnCariPeriksaLab.setForeground(new java.awt.Color(50, 50, 50));
        MnCariPeriksaLab.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnCariPeriksaLab.setText("Data Pemeriksaan Lab PK");
        MnCariPeriksaLab.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnCariPeriksaLab.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnCariPeriksaLab.setName("MnCariPeriksaLab"); // NOI18N
        MnCariPeriksaLab.setPreferredSize(new java.awt.Dimension(250, 25));
        MnCariPeriksaLab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnCariPeriksaLabActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnCariPeriksaLab);

        MnCariPeriksaLabPA.setBackground(new java.awt.Color(255, 255, 254));
        MnCariPeriksaLabPA.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnCariPeriksaLabPA.setForeground(new java.awt.Color(50, 50, 50));
        MnCariPeriksaLabPA.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnCariPeriksaLabPA.setText("Data Pemeriksaan Lab PA");
        MnCariPeriksaLabPA.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnCariPeriksaLabPA.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnCariPeriksaLabPA.setName("MnCariPeriksaLabPA"); // NOI18N
        MnCariPeriksaLabPA.setPreferredSize(new java.awt.Dimension(250, 25));
        MnCariPeriksaLabPA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnCariPeriksaLabPAActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnCariPeriksaLabPA);

        MnCariPeriksaLabMB.setBackground(new java.awt.Color(255, 255, 254));
        MnCariPeriksaLabMB.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnCariPeriksaLabMB.setForeground(new java.awt.Color(50, 50, 50));
        MnCariPeriksaLabMB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnCariPeriksaLabMB.setText("Data Pemeriksaan Lab MB");
        MnCariPeriksaLabMB.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnCariPeriksaLabMB.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnCariPeriksaLabMB.setName("MnCariPeriksaLabMB"); // NOI18N
        MnCariPeriksaLabMB.setPreferredSize(new java.awt.Dimension(250, 25));
        MnCariPeriksaLabMB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnCariPeriksaLabMBActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnCariPeriksaLabMB);

        MnCariRadiologi.setBackground(new java.awt.Color(255, 255, 254));
        MnCariRadiologi.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnCariRadiologi.setForeground(new java.awt.Color(50, 50, 50));
        MnCariRadiologi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnCariRadiologi.setText("Data Pemeriksaan Radiologi");
        MnCariRadiologi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnCariRadiologi.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnCariRadiologi.setName("MnCariRadiologi"); // NOI18N
        MnCariRadiologi.setPreferredSize(new java.awt.Dimension(250, 25));
        MnCariRadiologi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnCariRadiologiActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnCariRadiologi);

        MnPenjualan.setBackground(new java.awt.Color(255, 255, 254));
        MnPenjualan.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnPenjualan.setForeground(new java.awt.Color(50, 50, 50));
        MnPenjualan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnPenjualan.setText("Penjualan Obat/Alkes/Barang");
        MnPenjualan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnPenjualan.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnPenjualan.setName("MnPenjualan"); // NOI18N
        MnPenjualan.setPreferredSize(new java.awt.Dimension(250, 25));
        MnPenjualan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnPenjualanActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnPenjualan);

        MnGabungNota.setBackground(new java.awt.Color(255, 255, 254));
        MnGabungNota.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnGabungNota.setForeground(new java.awt.Color(50, 50, 50));
        MnGabungNota.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnGabungNota.setText("Cetak Gabung Nota");
        MnGabungNota.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnGabungNota.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnGabungNota.setName("MnGabungNota"); // NOI18N
        MnGabungNota.setPreferredSize(new java.awt.Dimension(250, 25));
        MnGabungNota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnGabungNotaActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnGabungNota);

        MnHapusTagihan.setBackground(new java.awt.Color(255, 255, 254));
        MnHapusTagihan.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnHapusTagihan.setForeground(new java.awt.Color(50, 50, 50));
        MnHapusTagihan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnHapusTagihan.setText("Hapus Nota Salah");
        MnHapusTagihan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnHapusTagihan.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnHapusTagihan.setName("MnHapusTagihan"); // NOI18N
        MnHapusTagihan.setPreferredSize(new java.awt.Dimension(250, 25));
        MnHapusTagihan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnHapusTagihanActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnHapusTagihan);

        MnFreeKarcis.setBackground(new java.awt.Color(255, 255, 254));
        MnFreeKarcis.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnFreeKarcis.setForeground(new java.awt.Color(50, 50, 50));
        MnFreeKarcis.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnFreeKarcis.setText("Free Karcis");
        MnFreeKarcis.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnFreeKarcis.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnFreeKarcis.setName("MnFreeKarcis"); // NOI18N
        MnFreeKarcis.setPreferredSize(new java.awt.Dimension(250, 25));
        MnFreeKarcis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnFreeKarcisActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnFreeKarcis);

        WindowGantiDokterPoli.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        WindowGantiDokterPoli.setName("WindowGantiDokterPoli"); // NOI18N
        WindowGantiDokterPoli.setUndecorated(true);
        WindowGantiDokterPoli.setResizable(false);

        internalFrame3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Ganti Dokter Poli ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame3.setName("internalFrame3"); // NOI18N
        internalFrame3.setLayout(null);

        BtnCloseIn1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/cross.png"))); // NOI18N
        BtnCloseIn1.setMnemonic('U');
        BtnCloseIn1.setText("Tutup");
        BtnCloseIn1.setToolTipText("Alt+U");
        BtnCloseIn1.setName("BtnCloseIn1"); // NOI18N
        BtnCloseIn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCloseIn1ActionPerformed(evt);
            }
        });
        BtnCloseIn1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCloseIn1KeyPressed(evt);
            }
        });
        internalFrame3.add(BtnCloseIn1);
        BtnCloseIn1.setBounds(510, 30, 100, 30);

        BtnSimpan1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan1.setMnemonic('S');
        BtnSimpan1.setText("Simpan");
        BtnSimpan1.setToolTipText("Alt+S");
        BtnSimpan1.setName("BtnSimpan1"); // NOI18N
        BtnSimpan1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpan1ActionPerformed(evt);
            }
        });
        BtnSimpan1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnSimpan1KeyPressed(evt);
            }
        });
        internalFrame3.add(BtnSimpan1);
        BtnSimpan1.setBounds(405, 30, 100, 30);

        jLabel13.setText("Dr Dituju :");
        jLabel13.setName("jLabel13"); // NOI18N
        internalFrame3.add(jLabel13);
        jLabel13.setBounds(0, 32, 77, 23);

        kddokter.setEditable(false);
        kddokter.setHighlighter(null);
        kddokter.setName("kddokter"); // NOI18N
        kddokter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kddokterKeyPressed(evt);
            }
        });
        internalFrame3.add(kddokter);
        kddokter.setBounds(81, 32, 100, 23);

        TDokter.setEditable(false);
        TDokter.setName("TDokter"); // NOI18N
        TDokter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TDokterKeyPressed(evt);
            }
        });
        internalFrame3.add(TDokter);
        TDokter.setBounds(183, 32, 181, 23);

        btnCariDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnCariDokter.setMnemonic('7');
        btnCariDokter.setToolTipText("ALt+7");
        btnCariDokter.setName("btnCariDokter"); // NOI18N
        btnCariDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariDokterActionPerformed(evt);
            }
        });
        internalFrame3.add(btnCariDokter);
        btnCariDokter.setBounds(366, 32, 28, 23);

        WindowGantiDokterPoli.getContentPane().add(internalFrame3, java.awt.BorderLayout.CENTER);

        WindowObatLangsung.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        WindowObatLangsung.setName("WindowObatLangsung"); // NOI18N
        WindowObatLangsung.setUndecorated(true);
        WindowObatLangsung.setResizable(false);

        internalFrame2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Input Total BHP & Obat ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame2.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        internalFrame2.setName("internalFrame2"); // NOI18N
        internalFrame2.setLayout(null);

        TotalObat.setHighlighter(null);
        TotalObat.setName("TotalObat"); // NOI18N
        TotalObat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TotalObatKeyPressed(evt);
            }
        });
        internalFrame2.add(TotalObat);
        TotalObat.setBounds(60, 30, 180, 23);

        jLabel8.setText("Total :");
        jLabel8.setName("jLabel8"); // NOI18N
        internalFrame2.add(jLabel8);
        jLabel8.setBounds(0, 30, 57, 23);

        BtnCloseIn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/cross.png"))); // NOI18N
        BtnCloseIn.setMnemonic('U');
        BtnCloseIn.setText("Tutup");
        BtnCloseIn.setToolTipText("Alt+U");
        BtnCloseIn.setName("BtnCloseIn"); // NOI18N
        BtnCloseIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCloseInActionPerformed(evt);
            }
        });
        BtnCloseIn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCloseInKeyPressed(evt);
            }
        });
        internalFrame2.add(BtnCloseIn);
        BtnCloseIn.setBounds(465, 30, 100, 30);

        BtnSimpan2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan2.setMnemonic('S');
        BtnSimpan2.setText("Simpan");
        BtnSimpan2.setToolTipText("Alt+S");
        BtnSimpan2.setName("BtnSimpan2"); // NOI18N
        BtnSimpan2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpan2ActionPerformed(evt);
            }
        });
        BtnSimpan2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnSimpan2KeyPressed(evt);
            }
        });
        internalFrame2.add(BtnSimpan2);
        BtnSimpan2.setBounds(255, 30, 100, 30);

        BtnBatal1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        BtnBatal1.setMnemonic('H');
        BtnBatal1.setText("Hapus");
        BtnBatal1.setToolTipText("Alt+H");
        BtnBatal1.setName("BtnBatal1"); // NOI18N
        BtnBatal1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBatal1ActionPerformed(evt);
            }
        });
        BtnBatal1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnBatal1KeyPressed(evt);
            }
        });
        internalFrame2.add(BtnBatal1);
        BtnBatal1.setBounds(360, 30, 100, 30);

        WindowObatLangsung.getContentPane().add(internalFrame2, java.awt.BorderLayout.CENTER);

        WindowTambahanBiaya.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        WindowTambahanBiaya.setName("WindowTambahanBiaya"); // NOI18N
        WindowTambahanBiaya.setUndecorated(true);
        WindowTambahanBiaya.setResizable(false);

        internalFrame4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Tambah Biaya ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame4.setName("internalFrame4"); // NOI18N
        internalFrame4.setLayout(new java.awt.BorderLayout(1, 1));

        scrollPane1.setName("scrollPane1"); // NOI18N
        scrollPane1.setOpaque(true);

        tbTambahan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbTambahan.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbTambahan.setName("tbTambahan"); // NOI18N
        scrollPane1.setViewportView(tbTambahan);

        internalFrame4.add(scrollPane1, java.awt.BorderLayout.CENTER);

        panelisi1.setName("panelisi1"); // NOI18N
        panelisi1.setPreferredSize(new java.awt.Dimension(100, 56));
        panelisi1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        label15.setText("No.Rawat :");
        label15.setName("label15"); // NOI18N
        label15.setPreferredSize(new java.awt.Dimension(60, 23));
        panelisi1.add(label15);

        norawat.setEditable(false);
        norawat.setName("norawat"); // NOI18N
        norawat.setPreferredSize(new java.awt.Dimension(150, 23));
        norawat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                norawatKeyPressed(evt);
            }
        });
        panelisi1.add(norawat);

        BtnTambah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/add-file-16x16.png"))); // NOI18N
        BtnTambah.setMnemonic('T');
        BtnTambah.setText("Tambah");
        BtnTambah.setToolTipText("Alt+T");
        BtnTambah.setName("BtnTambah"); // NOI18N
        BtnTambah.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnTambahActionPerformed(evt);
            }
        });
        panelisi1.add(BtnTambah);

        BtnSimpan3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan3.setMnemonic('S');
        BtnSimpan3.setText("Simpan");
        BtnSimpan3.setToolTipText("Alt+S");
        BtnSimpan3.setName("BtnSimpan3"); // NOI18N
        BtnSimpan3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpan3ActionPerformed(evt);
            }
        });
        panelisi1.add(BtnSimpan3);

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        BtnHapus.setMnemonic('H');
        BtnHapus.setText("Hapus");
        BtnHapus.setToolTipText("Alt+H");
        BtnHapus.setName("BtnHapus"); // NOI18N
        BtnHapus.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHapusActionPerformed(evt);
            }
        });
        panelisi1.add(BtnHapus);

        BtnKeluar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/cross.png"))); // NOI18N
        BtnKeluar1.setMnemonic('U');
        BtnKeluar1.setText("Tutup");
        BtnKeluar1.setToolTipText("Alt+U");
        BtnKeluar1.setName("BtnKeluar1"); // NOI18N
        BtnKeluar1.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluar1ActionPerformed(evt);
            }
        });
        panelisi1.add(BtnKeluar1);

        internalFrame4.add(panelisi1, java.awt.BorderLayout.PAGE_END);

        WindowTambahanBiaya.getContentPane().add(internalFrame4, java.awt.BorderLayout.CENTER);

        WindowGantiPoli.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        WindowGantiPoli.setName("WindowGantiPoli"); // NOI18N
        WindowGantiPoli.setUndecorated(true);
        WindowGantiPoli.setResizable(false);

        internalFrame5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Ganti Poliklinik ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame5.setName("internalFrame5"); // NOI18N
        internalFrame5.setLayout(null);

        BtnCloseIn4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/cross.png"))); // NOI18N
        BtnCloseIn4.setMnemonic('U');
        BtnCloseIn4.setText("Tutup");
        BtnCloseIn4.setToolTipText("Alt+U");
        BtnCloseIn4.setName("BtnCloseIn4"); // NOI18N
        BtnCloseIn4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCloseIn4ActionPerformed(evt);
            }
        });
        internalFrame5.add(BtnCloseIn4);
        BtnCloseIn4.setBounds(510, 30, 100, 30);

        BtnSimpan4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan4.setMnemonic('S');
        BtnSimpan4.setText("Simpan");
        BtnSimpan4.setToolTipText("Alt+S");
        BtnSimpan4.setName("BtnSimpan4"); // NOI18N
        BtnSimpan4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpan4ActionPerformed(evt);
            }
        });
        internalFrame5.add(BtnSimpan4);
        BtnSimpan4.setBounds(405, 30, 100, 30);

        jLabel14.setText("Poli Dituju :");
        jLabel14.setName("jLabel14"); // NOI18N
        internalFrame5.add(jLabel14);
        jLabel14.setBounds(0, 32, 77, 23);

        kdpoli.setEditable(false);
        kdpoli.setHighlighter(null);
        kdpoli.setName("kdpoli"); // NOI18N
        kdpoli.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdpoliKeyPressed(evt);
            }
        });
        internalFrame5.add(kdpoli);
        kdpoli.setBounds(81, 32, 100, 23);

        nmpoli.setEditable(false);
        nmpoli.setName("nmpoli"); // NOI18N
        internalFrame5.add(nmpoli);
        nmpoli.setBounds(183, 32, 181, 23);

        btnCariPoli.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnCariPoli.setMnemonic('7');
        btnCariPoli.setToolTipText("ALt+7");
        btnCariPoli.setName("btnCariPoli"); // NOI18N
        btnCariPoli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariPoliActionPerformed(evt);
            }
        });
        internalFrame5.add(btnCariPoli);
        btnCariPoli.setBounds(366, 32, 28, 23);

        WindowGantiPoli.getContentPane().add(internalFrame5, java.awt.BorderLayout.CENTER);

        WindowPotonganBiaya.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        WindowPotonganBiaya.setName("WindowPotonganBiaya"); // NOI18N
        WindowPotonganBiaya.setUndecorated(true);
        WindowPotonganBiaya.setResizable(false);

        internalFrame6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Potongan Biaya ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame6.setName("internalFrame6"); // NOI18N
        internalFrame6.setLayout(new java.awt.BorderLayout(1, 1));

        scrollPane2.setName("scrollPane2"); // NOI18N
        scrollPane2.setOpaque(true);

        tbPotongan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbPotongan.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbPotongan.setName("tbPotongan"); // NOI18N
        scrollPane2.setViewportView(tbPotongan);

        internalFrame6.add(scrollPane2, java.awt.BorderLayout.CENTER);

        panelisi2.setName("panelisi2"); // NOI18N
        panelisi2.setPreferredSize(new java.awt.Dimension(100, 56));
        panelisi2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        label16.setText("No.Rawat :");
        label16.setName("label16"); // NOI18N
        label16.setPreferredSize(new java.awt.Dimension(60, 23));
        panelisi2.add(label16);

        norawatpotongan.setEditable(false);
        norawatpotongan.setName("norawatpotongan"); // NOI18N
        norawatpotongan.setPreferredSize(new java.awt.Dimension(150, 23));
        norawatpotongan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                norawatpotonganKeyPressed(evt);
            }
        });
        panelisi2.add(norawatpotongan);

        BtnTambahPotongan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/add-file-16x16.png"))); // NOI18N
        BtnTambahPotongan.setMnemonic('T');
        BtnTambahPotongan.setText("Tambah");
        BtnTambahPotongan.setToolTipText("Alt+T");
        BtnTambahPotongan.setName("BtnTambahPotongan"); // NOI18N
        BtnTambahPotongan.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnTambahPotongan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnTambahPotonganActionPerformed(evt);
            }
        });
        panelisi2.add(BtnTambahPotongan);

        BtnSimpanPotongan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpanPotongan.setMnemonic('S');
        BtnSimpanPotongan.setText("Simpan");
        BtnSimpanPotongan.setToolTipText("Alt+S");
        BtnSimpanPotongan.setName("BtnSimpanPotongan"); // NOI18N
        BtnSimpanPotongan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanPotonganActionPerformed(evt);
            }
        });
        panelisi2.add(BtnSimpanPotongan);

        BtnHapusPotongan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        BtnHapusPotongan.setMnemonic('H');
        BtnHapusPotongan.setText("Hapus");
        BtnHapusPotongan.setToolTipText("Alt+H");
        BtnHapusPotongan.setName("BtnHapusPotongan"); // NOI18N
        BtnHapusPotongan.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnHapusPotongan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHapusPotonganActionPerformed(evt);
            }
        });
        panelisi2.add(BtnHapusPotongan);

        BtnKeluarPotongan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/cross.png"))); // NOI18N
        BtnKeluarPotongan.setMnemonic('U');
        BtnKeluarPotongan.setText("Tutup");
        BtnKeluarPotongan.setToolTipText("Alt+U");
        BtnKeluarPotongan.setName("BtnKeluarPotongan"); // NOI18N
        BtnKeluarPotongan.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluarPotongan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluarPotonganActionPerformed(evt);
            }
        });
        panelisi2.add(BtnKeluarPotongan);

        internalFrame6.add(panelisi2, java.awt.BorderLayout.PAGE_END);

        WindowPotonganBiaya.getContentPane().add(internalFrame6, java.awt.BorderLayout.CENTER);

        WindowGantiPenjab.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        WindowGantiPenjab.setName("WindowGantiPenjab"); // NOI18N
        WindowGantiPenjab.setUndecorated(true);
        WindowGantiPenjab.setResizable(false);

        internalFrame7.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Ganti Jenis Bayar ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame7.setName("internalFrame7"); // NOI18N
        internalFrame7.setLayout(null);

        BtnCloseIn5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/cross.png"))); // NOI18N
        BtnCloseIn5.setMnemonic('P');
        BtnCloseIn5.setText("Tutup");
        BtnCloseIn5.setToolTipText("Alt+P");
        BtnCloseIn5.setName("BtnCloseIn5"); // NOI18N
        BtnCloseIn5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCloseIn5ActionPerformed(evt);
            }
        });
        internalFrame7.add(BtnCloseIn5);
        BtnCloseIn5.setBounds(510, 30, 100, 30);

        BtnSimpan5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan5.setMnemonic('S');
        BtnSimpan5.setText("Simpan");
        BtnSimpan5.setToolTipText("Alt+S");
        BtnSimpan5.setName("BtnSimpan5"); // NOI18N
        BtnSimpan5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpan5ActionPerformed(evt);
            }
        });
        internalFrame7.add(BtnSimpan5);
        BtnSimpan5.setBounds(405, 30, 100, 30);

        jLabel17.setText("Jenis Bayar :");
        jLabel17.setName("jLabel17"); // NOI18N
        internalFrame7.add(jLabel17);
        jLabel17.setBounds(0, 32, 77, 23);

        kdpenjab.setEditable(false);
        kdpenjab.setName("kdpenjab"); // NOI18N
        kdpenjab.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdpenjabKeyPressed(evt);
            }
        });
        internalFrame7.add(kdpenjab);
        kdpenjab.setBounds(81, 32, 100, 23);

        nmpenjab.setEditable(false);
        nmpenjab.setName("nmpenjab"); // NOI18N
        internalFrame7.add(nmpenjab);
        nmpenjab.setBounds(183, 32, 181, 23);

        btnPenjab.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnPenjab.setMnemonic('7');
        btnPenjab.setToolTipText("ALt+7");
        btnPenjab.setName("btnPenjab"); // NOI18N
        btnPenjab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPenjabActionPerformed(evt);
            }
        });
        internalFrame7.add(btnPenjab);
        btnPenjab.setBounds(366, 32, 28, 23);

        WindowGantiPenjab.getContentPane().add(internalFrame7, java.awt.BorderLayout.CENTER);

        PopupBayar.setName("PopupBayar"); // NOI18N

        ppBersihkan.setBackground(new java.awt.Color(255, 255, 254));
        ppBersihkan.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppBersihkan.setForeground(new java.awt.Color(50, 50, 50));
        ppBersihkan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppBersihkan.setText("Bersihkan Pembayaran");
        ppBersihkan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppBersihkan.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppBersihkan.setName("ppBersihkan"); // NOI18N
        ppBersihkan.setPreferredSize(new java.awt.Dimension(200, 25));
        ppBersihkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppBersihkanActionPerformed(evt);
            }
        });
        PopupBayar.add(ppBersihkan);

        PopupPiutang.setName("PopupPiutang"); // NOI18N

        ppBersihkan1.setBackground(new java.awt.Color(255, 255, 254));
        ppBersihkan1.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppBersihkan1.setForeground(new java.awt.Color(50, 50, 50));
        ppBersihkan1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppBersihkan1.setText("Bersihkan Piutang");
        ppBersihkan1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppBersihkan1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppBersihkan1.setName("ppBersihkan1"); // NOI18N
        ppBersihkan1.setPreferredSize(new java.awt.Dimension(200, 25));
        ppBersihkan1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppBersihkan1ActionPerformed(evt);
            }
        });
        PopupPiutang.add(ppBersihkan1);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Billing/Pembayaran Ralan Pasien ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass1.setPreferredSize(new java.awt.Dimension(100, 70));
        panelGlass1.setLayout(null);

        jLabel3.setText("No.Rawat :");
        jLabel3.setName("jLabel3"); // NOI18N
        jLabel3.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass1.add(jLabel3);
        jLabel3.setBounds(5, 11, 70, 23);

        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw"); // NOI18N
        TNoRw.setPreferredSize(new java.awt.Dimension(150, 23));
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });
        panelGlass1.add(TNoRw);
        TNoRw.setBounds(79, 11, 150, 23);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM"); // NOI18N
        TNoRM.setPreferredSize(new java.awt.Dimension(100, 23));
        panelGlass1.add(TNoRM);
        TNoRM.setBounds(233, 11, 100, 23);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N
        TPasien.setPreferredSize(new java.awt.Dimension(320, 23));
        panelGlass1.add(TPasien);
        TPasien.setBounds(337, 11, 320, 23);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('R');
        BtnCari.setToolTipText("Alt+R");
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
        panelGlass1.add(BtnCari);
        BtnCari.setBounds(661, 11, 28, 23);

        jLabel4.setText("Tanggal :");
        jLabel4.setName("jLabel4"); // NOI18N
        jLabel4.setPreferredSize(new java.awt.Dimension(65, 23));
        panelGlass1.add(jLabel4);
        jLabel4.setBounds(693, 11, 65, 23);

        DTPTgl.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "29-03-2026 08:38:10" }));
        DTPTgl.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        DTPTgl.setName("DTPTgl"); // NOI18N
        DTPTgl.setOpaque(false);
        DTPTgl.setPreferredSize(new java.awt.Dimension(140, 23));
        DTPTgl.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DTPTglKeyPressed(evt);
            }
        });
        panelGlass1.add(DTPTgl);
        DTPTgl.setBounds(762, 11, 140, 23);

        HKLabel.setText("Hak Kelas :");
        HKLabel.setToolTipText("");
        HKLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        HKLabel.setName("HKLabel"); // NOI18N
        HKLabel.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass1.add(HKLabel);
        HKLabel.setBounds(5, 40, 70, 23);

        THakKelas.setEditable(false);
        THakKelas.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        THakKelas.setName("THakKelas"); // NOI18N
        THakKelas.setPreferredSize(new java.awt.Dimension(150, 23));
        THakKelas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                THakKelasKeyPressed(evt);
            }
        });
        panelGlass1.add(THakKelas);
        THakKelas.setBounds(80, 40, 150, 23);

        NKLabel.setText("Naik Kelas :");
        NKLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        NKLabel.setName("NKLabel"); // NOI18N
        NKLabel.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass1.add(NKLabel);
        NKLabel.setBounds(235, 40, 70, 23);

        TNaikKelas.setEditable(false);
        TNaikKelas.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        TNaikKelas.setName("TNaikKelas"); // NOI18N
        TNaikKelas.setPreferredSize(new java.awt.Dimension(150, 23));
        TNaikKelas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNaikKelasKeyPressed(evt);
            }
        });
        panelGlass1.add(TNaikKelas);
        TNaikKelas.setBounds(310, 40, 150, 23);

        NoSEPLabel.setText("No. SEP :");
        NoSEPLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        NoSEPLabel.setName("NoSEPLabel"); // NOI18N
        NoSEPLabel.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass1.add(NoSEPLabel);
        NoSEPLabel.setBounds(465, 40, 60, 23);

        TNoSEP.setEditable(false);
        TNoSEP.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        TNoSEP.setName("TNoSEP"); // NOI18N
        TNoSEP.setPreferredSize(new java.awt.Dimension(160, 23));
        TNoSEP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TNoSEPActionPerformed(evt);
            }
        });
        TNoSEP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoSEPKeyPressed(evt);
            }
        });
        panelGlass1.add(TNoSEP);
        TNoSEP.setBounds(530, 40, 190, 23);

        internalFrame1.add(panelGlass1, java.awt.BorderLayout.PAGE_START);

        TabRawat.setBackground(new java.awt.Color(255, 255, 255));
        TabRawat.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(241, 246, 236)));
        TabRawat.setForeground(new java.awt.Color(50, 50, 50));
        TabRawat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        TabRawat.setName("TabRawat"); // NOI18N
        TabRawat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TabRawatMouseClicked(evt);
            }
        });

        Scroll.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        Scroll.setComponentPopupMenu(jPopupMenu1);
        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbBilling.setToolTipText("");
        tbBilling.setComponentPopupMenu(jPopupMenu1);
        tbBilling.setName("tbBilling"); // NOI18N
        tbBilling.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbBillingMouseClicked(evt);
            }
        });
        tbBilling.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbBillingKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbBilling);

        TabRawat.addTab("Data Tagihan", Scroll);

        scrollPane8.setBorder(null);
        scrollPane8.setComponentPopupMenu(PopupBayar);
        scrollPane8.setName("scrollPane8"); // NOI18N
        scrollPane8.setOpaque(true);

        panelBayar.setBorder(null);
        panelBayar.setPreferredSize(new java.awt.Dimension(100, 415));
        panelBayar.setLayout(null);

        TtlSemua.setEditable(false);
        TtlSemua.setText("0");
        TtlSemua.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        TtlSemua.setHighlighter(null);
        TtlSemua.setName("TtlSemua"); // NOI18N
        TtlSemua.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TtlSemuaKeyPressed(evt);
            }
        });
        panelBayar.add(TtlSemua);
        TtlSemua.setBounds(110, 37, 230, 23);

        TKembali.setEditable(false);
        TKembali.setText("0");
        TKembali.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        TKembali.setHighlighter(null);
        TKembali.setName("TKembali"); // NOI18N
        panelBayar.add(TKembali);
        TKembali.setBounds(110, 377, 230, 23);

        jLabel5.setText("Bayar : Rp.");
        jLabel5.setName("jLabel5"); // NOI18N
        jLabel5.setPreferredSize(new java.awt.Dimension(95, 23));
        panelBayar.add(jLabel5);
        jLabel5.setBounds(19, 67, 90, 23);

        jLabel9.setText("Total Tagihan : Rp.");
        jLabel9.setName("jLabel9"); // NOI18N
        jLabel9.setPreferredSize(new java.awt.Dimension(95, 23));
        panelBayar.add(jLabel9);
        jLabel9.setBounds(0, 37, 109, 23);

        chkPotongan.setSelected(true);
        chkPotongan.setText("Potongan");
        chkPotongan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        chkPotongan.setName("chkPotongan"); // NOI18N
        chkPotongan.setOpaque(false);
        chkPotongan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPotonganActionPerformed(evt);
            }
        });
        panelBayar.add(chkPotongan);
        chkPotongan.setBounds(395, 8, 90, 23);

        chkLaborat.setSelected(true);
        chkLaborat.setText("Laboratorium");
        chkLaborat.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        chkLaborat.setName("chkLaborat"); // NOI18N
        chkLaborat.setOpaque(false);
        chkLaborat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkLaboratActionPerformed(evt);
            }
        });
        panelBayar.add(chkLaborat);
        chkLaborat.setBounds(15, 8, 95, 23);

        chkTarifDokter.setSelected(true);
        chkTarifDokter.setText("Tarif Dokter");
        chkTarifDokter.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        chkTarifDokter.setName("chkTarifDokter"); // NOI18N
        chkTarifDokter.setOpaque(false);
        chkTarifDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkTarifDokterActionPerformed(evt);
            }
        });
        panelBayar.add(chkTarifDokter);
        chkTarifDokter.setBounds(205, 8, 90, 23);

        chkTarifPrm.setSelected(true);
        chkTarifPrm.setText("Tarif Paramedis");
        chkTarifPrm.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        chkTarifPrm.setName("chkTarifPrm"); // NOI18N
        chkTarifPrm.setOpaque(false);
        chkTarifPrm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkTarifPrmActionPerformed(evt);
            }
        });
        panelBayar.add(chkTarifPrm);
        chkTarifPrm.setBounds(585, 8, 120, 23);

        chkRadiologi.setSelected(true);
        chkRadiologi.setText("Radiologi");
        chkRadiologi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        chkRadiologi.setName("chkRadiologi"); // NOI18N
        chkRadiologi.setOpaque(false);
        chkRadiologi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkRadiologiActionPerformed(evt);
            }
        });
        panelBayar.add(chkRadiologi);
        chkRadiologi.setBounds(110, 8, 90, 23);

        chkTambahan.setSelected(true);
        chkTambahan.setText("Tambahan");
        chkTambahan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        chkTambahan.setName("chkTambahan"); // NOI18N
        chkTambahan.setOpaque(false);
        chkTambahan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkTambahanActionPerformed(evt);
            }
        });
        panelBayar.add(chkTambahan);
        chkTambahan.setBounds(300, 8, 90, 23);

        chkObat.setSelected(true);
        chkObat.setText("Obat");
        chkObat.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        chkObat.setName("chkObat"); // NOI18N
        chkObat.setOpaque(false);
        chkObat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkObatActionPerformed(evt);
            }
        });
        panelBayar.add(chkObat);
        chkObat.setBounds(490, 8, 90, 23);

        jLabel12.setText("Tagihan + PPN : Rp.");
        jLabel12.setName("jLabel12"); // NOI18N
        jLabel12.setPreferredSize(new java.awt.Dimension(95, 23));
        panelBayar.add(jLabel12);
        jLabel12.setBounds(531, 37, 110, 23);

        chkSarpras.setSelected(true);
        chkSarpras.setText("Sarpras");
        chkSarpras.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        chkSarpras.setName("chkSarpras"); // NOI18N
        chkSarpras.setOpaque(false);
        chkSarpras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSarprasActionPerformed(evt);
            }
        });
        panelBayar.add(chkSarpras);
        chkSarpras.setBounds(805, 8, 90, 23);

        TagihanPPn.setEditable(false);
        TagihanPPn.setText("0");
        TagihanPPn.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        TagihanPPn.setHighlighter(null);
        TagihanPPn.setName("TagihanPPn"); // NOI18N
        panelBayar.add(TagihanPPn);
        TagihanPPn.setBounds(642, 37, 230, 23);

        chkAdministrasi.setSelected(true);
        chkAdministrasi.setText("Administrasi");
        chkAdministrasi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        chkAdministrasi.setName("chkAdministrasi"); // NOI18N
        chkAdministrasi.setOpaque(false);
        chkAdministrasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAdministrasiActionPerformed(evt);
            }
        });
        panelBayar.add(chkAdministrasi);
        chkAdministrasi.setBounds(710, 8, 95, 23);

        scrollPane3.setComponentPopupMenu(PopupBayar);
        scrollPane3.setName("scrollPane3"); // NOI18N
        scrollPane3.setOpaque(true);

        tbAkunBayar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbAkunBayar.setToolTipText("");
        tbAkunBayar.setComponentPopupMenu(PopupBayar);
        tbAkunBayar.setName("tbAkunBayar"); // NOI18N
        tbAkunBayar.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tbAkunBayarPropertyChange(evt);
            }
        });
        tbAkunBayar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbAkunBayarKeyPressed(evt);
            }
        });
        scrollPane3.setViewportView(tbAkunBayar);

        panelBayar.add(scrollPane3);
        scrollPane3.setBounds(110, 92, 790, 125);

        jLabel6.setText("Kembali : Rp.");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(95, 23));
        panelBayar.add(jLabel6);
        jLabel6.setBounds(19, 377, 90, 23);

        scrollPane4.setComponentPopupMenu(PopupPiutang);
        scrollPane4.setName("scrollPane4"); // NOI18N
        scrollPane4.setOpaque(true);

        tbAkunPiutang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbAkunPiutang.setToolTipText("");
        tbAkunPiutang.setComponentPopupMenu(PopupPiutang);
        tbAkunPiutang.setName("tbAkunPiutang"); // NOI18N
        tbAkunPiutang.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tbAkunPiutangPropertyChange(evt);
            }
        });
        tbAkunPiutang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbAkunPiutangKeyPressed(evt);
            }
        });
        scrollPane4.setViewportView(tbAkunPiutang);

        panelBayar.add(scrollPane4);
        scrollPane4.setBounds(110, 247, 790, 125);

        jLabel16.setText("Piutang : Rp.");
        jLabel16.setName("jLabel16"); // NOI18N
        jLabel16.setPreferredSize(new java.awt.Dimension(95, 23));
        panelBayar.add(jLabel16);
        jLabel16.setBounds(19, 222, 90, 23);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(340, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelBayar.add(TCari);
        TCari.setBounds(110, 67, 734, 23);

        BtnCariBayar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCariBayar.setMnemonic('3');
        BtnCariBayar.setToolTipText("Alt+3");
        BtnCariBayar.setName("BtnCariBayar"); // NOI18N
        BtnCariBayar.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCariBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariBayarActionPerformed(evt);
            }
        });
        BtnCariBayar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCariBayarKeyPressed(evt);
            }
        });
        panelBayar.add(BtnCariBayar);
        BtnCariBayar.setBounds(847, 67, 25, 23);

        TCari1.setName("TCari1"); // NOI18N
        TCari1.setPreferredSize(new java.awt.Dimension(340, 23));
        TCari1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCari1KeyPressed(evt);
            }
        });
        panelBayar.add(TCari1);
        TCari1.setBounds(110, 222, 734, 23);

        btnCariPiutang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        btnCariPiutang.setMnemonic('3');
        btnCariPiutang.setToolTipText("Alt+3");
        btnCariPiutang.setName("btnCariPiutang"); // NOI18N
        btnCariPiutang.setPreferredSize(new java.awt.Dimension(28, 23));
        btnCariPiutang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCariPiutangActionPerformed(evt);
            }
        });
        btnCariPiutang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnCariPiutangKeyPressed(evt);
            }
        });
        panelBayar.add(btnCariPiutang);
        btnCariPiutang.setBounds(847, 222, 25, 23);

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
        panelBayar.add(BtnAll);
        BtnAll.setBounds(875, 67, 25, 23);

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
        panelBayar.add(BtnAll1);
        BtnAll1.setBounds(875, 222, 25, 23);

        scrollPane8.setViewportView(panelBayar);

        TabRawat.addTab("Pembayaran", scrollPane8);

        panelPermintaan.setBorder(null);
        panelPermintaan.setName("panelPermintaan"); // NOI18N
        panelPermintaan.setPreferredSize(new java.awt.Dimension(100, 137));
        panelPermintaan.setLayout(new java.awt.GridLayout(3, 0));

        scrollPane5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)), "1. Permintaan Laborat : ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        scrollPane5.setComponentPopupMenu(PopupBayar);
        scrollPane5.setName("scrollPane5"); // NOI18N

        tbLab.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbLab.setToolTipText("");
        tbLab.setComponentPopupMenu(PopupBayar);
        tbLab.setName("tbLab"); // NOI18N
        scrollPane5.setViewportView(tbLab);

        panelPermintaan.add(scrollPane5);

        scrollPane6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)), "2. Permintaan Radiologi : ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        scrollPane6.setComponentPopupMenu(PopupBayar);
        scrollPane6.setName("scrollPane6"); // NOI18N

        tbRadiologi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbRadiologi.setToolTipText("");
        tbRadiologi.setComponentPopupMenu(PopupBayar);
        tbRadiologi.setName("tbRadiologi"); // NOI18N
        scrollPane6.setViewportView(tbRadiologi);

        panelPermintaan.add(scrollPane6);

        scrollPane7.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)), "3. Permintaan Resep : ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        scrollPane7.setComponentPopupMenu(PopupBayar);
        scrollPane7.setName("scrollPane7"); // NOI18N

        tbApotek.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbApotek.setToolTipText("");
        tbApotek.setComponentPopupMenu(PopupBayar);
        tbApotek.setName("tbApotek"); // NOI18N
        scrollPane7.setViewportView(tbApotek);

        panelPermintaan.add(scrollPane7);

        TabRawat.addTab("Status Permintaan", panelPermintaan);

        internalFrame1.add(TabRawat, java.awt.BorderLayout.CENTER);

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(55, 55));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Simpan");
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnSimpan.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
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
        panelGlass8.add(BtnSimpan);

        BtnNota.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Agenda-1-16x16.png"))); // NOI18N
        BtnNota.setMnemonic('N');
        BtnNota.setText(" Nota");
        BtnNota.setToolTipText("Alt+N");
        BtnNota.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnNota.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        BtnNota.setName("BtnNota"); // NOI18N
        BtnNota.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnNota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnNotaActionPerformed(evt);
            }
        });
        BtnNota.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnNotaKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnNota);

        BtnView.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnView.setMnemonic('L');
        BtnView.setText("Lihat");
        BtnView.setToolTipText("Alt+L");
        BtnView.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnView.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        BtnView.setName("BtnView"); // NOI18N
        BtnView.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnViewActionPerformed(evt);
            }
        });
        BtnView.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnViewKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnView);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnKeluar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
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
        panelGlass8.add(BtnKeluar);

        internalFrame1.add(panelGlass8, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRwKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            isRawat();
        }
}//GEN-LAST:event_TNoRwKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        if(akses.getbilling_ralan()==true){
            try {
                pscekbilling=koneksi.prepareStatement(sqlpscekbilling);
                try {
                    pscekbilling.setString(1,TNoRw.getText());
                    rscekbilling=pscekbilling.executeQuery();
                    if(rscekbilling.next()){
                        i=rscekbilling.getInt(1);
                    } 
                 } catch (Ex ce ption e) { 
                    i=0;
         
                     } finally{
                    if(rscekbilling != null){
                        rscekbilling.close(); 
                     }   
                    if(pscekbilling != null){
                        psce k billing.close();
                    }
                } 
  
                if(i<= 0){ 
                    int j a wab=JOptionPane.showConfirmDialog(null, "Data pembayaran belum tersimpan, apa anda mau menyimpannya...????","Konfirmasi",JOptionPane.YES_NO_OPTION);
                    if(jawab==JOptionPane.YES_OPTION){
                        chkLaborat.setSelected(true);
                          chkRadiologi.setSelected(true);
                        isRawat();  
                        B tnSimpanActionPerformed(evt);
                         dispose(); 
                    }else{
                        WindowObatLangsung.dispose();
                         WindowGantiDokterPol i.dispose();
                        WindowTambahanBiaya.dispose();
                        WindowGantiPoli.dispose();
                        WindowPotonganBiaya.dispose();
                        dispose();    
                      }                  
                }else if(i>0) { 
                             
                            
                    Wi ndowOb at Langsung.dispose(); 
                    WindowGantiDokterPoli.dispose();
                    WindowTambahanBiaya.dispose();
                    WindowGantiPoli.dispose();
                    WindowPotonganBiaya.dispose();
                    dispose();                
                }  
            }catch(Exception e){
                System.out.println(e);
            }
        }else{
            WindowObatLangsung.dispose();
            WindowGantiDokterPoli.
                    m
                o wGantiP ol i .d ispose();
            WindowPotonganBiaya.dispose();
            dispose(); 
        }
        sukses=false;   
}//GEN-LAST:event_BtnKeluarActionPerformed

                 BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(ev t.get KeyCode()==Ke yEvent.VK_SPACE){
            dispose();
        }else{Valid.pindah(evt,BtnView,BtnNota);}
}//GEN-LA ST:e vent_BtnKeluarKeyPressed

    private void TtlSemuaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TtlSemuaKeyPressed
        Valid.pindah(evt,BtnKeluar,BtnNota);
    }//GEN-LAST:event_TtlSemuaKeyPressed

    private void BtnNo
            int close_bill =Sequel.cariInteger("select count(billing.no_rawat) from billing where billing.no_rawat=?",TNoRw.getText());
            if ( TNoRw.
                     Valid.textKosong(TNoRw,"Pasien");
            }else if(tabModeRwJlDr.getRowCount()==0){
                JOptionPane.showMessageDialog(null,"Maaf, data sudah  habis. Tidak ada data yang bisa anda print...!!!!");
                 //TCari.req ue stFocus(); 
            }else if(close_bill == 0){
                
              JOptionPane.sho wMessage Dialog(nu
        l
                 }else if(tabModeRwJlDr.getRowCount()!=0){
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try{ 
                    Seque l.queryu2( "delete from temporary_bayar_ralan where temp9='"+akses.getkode()+"'"); 
                     for(i=0;i<tabModeRwJlDr.getRowCount();i++){  
                        if(tabModeRwJlDr.getValueAt(i,0).toString().equals("true")){
                            biaya=""; 
                         try {
                
                             biaya=Valid.SetA ng ka(Double.parseDouble(tabModeRwJl
                Dr .getValueAt(i,4).toString()));  
                        } catch (Ex ception e)
                              biaya="";   
                        }                             
                         tambahan=""
                          try { 
                            tambahan=Valid.SetA ngka(Double.parseDouble(tabModeRwJlDr.getValueAt(i,6).toString()))
                          } catch (Exception e )  { 
                            tambahan="";
                 
                        totals="";    
                            t r y {  
                             totals=Valid.SetAngka( Double.parseDouble(tabModeRwJ l
                        } cat c h (
                            t
                               
                        
                            mpora r y=k
                        t
                            pste m por
                            p
                            pstempor a ry.setString(3,tabModeRwJlDr.getValueAt(i,2).toString().repla ceAll("'",""));
                            pstemporary.setStri
                            pstempor a ry.
                         
                                  ps
                            }
                                ps t emporary.setString(6,""); 
                            }                  
                            pstemp o rar
                         

                               pste m porary.setString(9,tabModeRwJlDr.getValue
                            }
                                pstemporary.setStrin g ( 9,""
                            }       
                            pstemporary.setString(10 ,akses.getkode());  
                            pstemporary.executeUpdat e();                           
                            tch (Exception e) { 
                            Syste
                                y{   
                            if(pstemporary != null)
                                pstemporary.close(); 
                            }
                             
                                      
                            
                                   
                            0){   
                                 
                            e
                             
                            enyimpan("temporary_bayar_ra
                        
                            enyimpan("temporary_bayar_ralan"," ' "+i
                         
                            ku nBayar.getRowCount();  
                                w2;r++){
                            a
                         
                     
                 

                }e lse if(p iu ta n
                    i++;
                    Sequel.menyimpan("temporary_bayar_ralan", "'" + i + "','TOTAL TAGIHAN',':','','','','','"
                            + TtlSemua.getText() + "','Tagihan','" + akses.getkode() + "','','','','','','','',''",
                            "Tagihan");
                    i++;
                    Sequel.menyimpan("temporary_bayar_ralan",
                            "'" + i + "','PPN',':','','','','','" + Valid.SetAngka(besarppn) + "','Tagihan','"
                                    + akses.getkode() + "','','','','','','','',''",
                            "Tagihan");
                    i++;
                    Sequel.menyimpan("temporary_bayar_ralan", "'" + i + "','TAGIHAN + PPN',':','','','','',
                            ' "+TagihanPPn.getText ( )+"','Tagihan', ' "+akses.getkode ( )+"','','','','','','','',''
                            ","Tagihan"
                    i++;
                    Sequ e l.menyimpan("temporary_bay
                    i++ ;       
                        =t bAkunBayar.getRowCount();                    
                            r<row2;r++){
                                             
                                              
                                                      
                                                 
                                    
                            alid
                         
                     
                          }    
                    }
                    Sequel.menyimpan("temporary_bayar_ralan", "'" + i + "','SISA PIUTANG',':','','','','','"+
                            V alid.SetAngka(piut a ng)+"','Tagihan ' ,'"+akses.getko d e()+"','','','','','','','',
                            ''","Tagiha
                    i++;
                    row2=tabModeAkunPiutang.getRowCount();
                                    
                                       
                            
                    for(
                        if(Valid.SetAngka(tabModeAkunPiutang. get V a l ueAt(r,3).toString())>0){
                                   
                            
                        
                            i++;     
                                   
                            
                        
                    }  
                            
                             
                            
                                             
                                              
                                                      
                                                 
                                    
                            
                        a
                     
                            case "Nota":     
                                   
                            
                        
                                    break;
                                c a se "K wita n
                                   i=2;    
                                  break;     
                                        
                                      
                                          
                            case
                         
                     
                 

                      
                     
                    (Exception e){   
                             
                                 
                    i=0;
                         
                              
                                  
                        Cursor(Cursor.ge
                            a r iI
                            
                         1:
                            p a ng
                            
                         2:
                            t a ng
                            lid.pa
                     
                              }else i f
                          
                 

                            
                            Valid.panggilUrl("billing/LaporanBilling.php?petugas="+
                              if(piutang>0){
                            
                                
                               
                            }else if(piutang<=0){
                                        
                                        
                                         
                                Va
                               
                            }    break;   
                                
                                            
                                          
                                                ){  
                                                 
                                                      
                                             
                                              
                                              
                                             
                                 Vali d.panggi lU rl (
                                        "where reg_periksa.kd_pj='"+kd_pj+"' and reg_pe
                                        r iksa.tgl_registrasi like '%"+Va lid. S etTgl(DTPT
                                        g l.getSelectedItem()+"").substring(0,7)+"%'")+"/RJ/"+kd_pj+"/"+Valid.S e
                                                  
                                                 
                                                      
                                             
                                              
                                              
                                             
                             
                            JOptio
                            }  
                            ult:
                                        
                                        
                                         
                            br eak;   
                                
                                            
                                          
                                                r.getDefaultCursor());  
                                                 
                                                      
                                             
                                              
                                              
                                             
                                 
                                
                                            
                                          
                                                tDefaultCursor());  
                                                 
                                                      
                                             
                                              
                                              
                                             
                             
                            {
                        ut.prin
                                
                                
                                            
                                          
                                                  
                                                 
                                                      
                                             
                                              
                                              
                                             
                              
                                ava.awt.event.KeyEvent evt) {//GEN- FIRST:event_BtnNotaKeyPressed
                            E
                            t.VK_S
                        erformed
                            
                    (
                    
                n

                ViewActionPerformed(java.awt.event.ActionE
            c t[] o ptions = {"Tag i
                nput;
            0
         
                     input = (String)JOptionPane.showInputDialog(null,"Silahkan pilih yang mau ditampilkan!","Keuangan",JOptionPane.QUESTION_MESSAGE,null,options,"Tagihan Masuk");
                switch (input) {
                    case "Tagihan Masuk": 
                         i=1 ;  
                        break;
                      case "Piutang Pasien":
                        i=2;  
         
                         case "Data Pembayaran HtH BPD Jateng":
                        i=3;
                        break; 
                case "Data P embayaran HtH BPD Papua": 
                   
                    i
              
             
                    i=5;   
                       
                    break;
                case "Data Pembayaran
                    i = 6;
                    break;
                
                    t i on
                    
                 
                      
                    
                ==1){
                    . s et
                    htBiay
                billing.setSize(this.getWidth(),this.
                    i n g.
                    ing.se
                billing.setVisible(true);
                    . s et
                    (i==2)
             
                  DlgLhtPiuta n
                  
         

                    
                  bi ll i
                billing.setVisible(true);
                this.setCursor(Curs o r.getDefaultCursor()) ;
                e if(i==3){ 
                this.setCursor(Cursor.getPredefinedC
                DlgLhtBankJateng billing=new D
                billing.setSize(this.getW
                billing.setLocationRelativeTo(this);
                 bill in g. se t
                billing.setVisible(true);
                this.setCursor(Cursor . getDefaultCursor()); 
                e if(i==4){
                this.setCursor(C
                DlgLhtBankPapua billing=new DlgL htBankPapua(null,f
                billing.setSize(this.getWidth(),this
                billing.setLocationRelativeTo(
                billing.setAlwaysOnTop(fa
                billing.setVisible(true);
                 this .s et Cu r
                e if(i==5){
                this.setCursor(Cursor.ge t PredefinedCursor(Cursor.WA IT_CURS
                DlgLhtBankJabar billing=new DlgL htBankJabar(null,f
                billing.setSize(this.getWidth(),this
                billing.setLocationRelativeTo(
                billing.setAlwaysOnTop(fa
                billing.setVisible(true);
                 this .s et Cu r
                e if(i==6){
                this.setCursor(Cursor.g e tPredefinedCursor(Cursor. WAIT_CU
                DlgLhtBankMandiri billing=new Dl gLhtBankMandiri(nu
                billing.setSize(this.getWidth(),this
                billing.setLocationRelativeTo(
                billing.setAlwaysOnTop(fa
                billing.setVisible(true);
                 this .s et Cu r
                
                   
                t_BtnViewActionPerformed 
                
                ViewKeyPressed(java.awt.event.
                yCode()==KeyEvent.VK_SPAC
                ctionPerformed(null);
                 
                ndah(evt,BtnNota,BtnKeluar);
                   
                t_BtnViewKeyPressed 
                
                ngMouseClicked(java.awt.event.
                lDr.getRowCount()!=0){   
                ing.getSelectedRow()>-1){
            f
         
                     if(i==1){
                        try {
                            akses.setform("DlgBilingRalan"); 
                              s witch (tbBilling.g etValueAt(tbBilling.getSelectedRow(),i).toString()) {
                                case "Tindakan":
                                      if(akses.gettindakan_ralan()==true){
                                          MnInputTindakanActionPerformed(null);
                                    }                            
                                     bre

                                        if(akses.getberi_obat()==true){ 
                                            M n
                                      }           
                                      / /d is pose();
                                      break;
                                    case "Tambahan Biaya":
                                    if(akses.gettambahan_biaya()==true){
                                        MnTambahanActionPerformed(null);
                                    }                             
                                    break;
                                case " Potongan Biaya":   
                                    if(akses.getpotongan_biaya()==true){
                                     
                                    }
                                    break;
                            }                         
                        } catch (Exception e) {
                            akses.set
                                    tb Billing.getValueAt(tbBilling.getSelectedRow(),i).toString()) {
                                case "Tindakan":
                                    if(akses.gettindakan_ralan()==true){
                                         MnInputTindakanActionPer fo rmed( null);
                                    }                            
                                    b
                                     "Obat & BHP":
                                    if(akses.getberi_obat()==true){
                                         MnInputObatActionPerform ed (null );
                                    }                            
                                    //dispose();
                                    break;
                             
                                    if(akses.gettambahan_biaya()==true){
                                        MnTambahanActionPerformed(null);
                                    }                             
                                    break;
                                case " Potongan Biaya":   
                                    if(akses.getpotongan_biaya()==true){
                                     
                                    }
                                    break;
                            }                         
                        }                        
                    }
                                     
           }
        }
}//GEN-LAST:event_tbBillingMouseClicke d   

private void tbBillingKeyPressed(java
                                    0){
            if(tbBilling.getSelectedRow()>-1){
                if(evt.getKeyCode()==K eyEvent.VK_ENTER){   
                    i=tbBilling.getSelectedColumn();
                    if(i==6){  
                        if(akses.getbilling_ralan()==true){
                            t
                         
                                    case "Laborat":
                 
                                         isRawat();
         
                                         cas

                                            isRawat(); 
                                            break;
                                     defau l t: 
                                           try{ 
                                              if(Double.parseDouble(tbBilling.getValueAt(tbBilling.getSelectedRow(),6).toString())!=0){
                                 
                                                      T NoRw. getText(),tbBilling.getValueAt(tbBilling.getSelectedRow(),2).toString(),tbBilling.getValueAt(tbBilling.getSelectedRow(),8).toString()
                                                });
                                                Sequel.menyimpan("temporary_tambahan_pot ongan","?,?,?,?",4,new String[]{
                                                    TNoRw.getText(),tbBilling.getValueAt(tbBilling.getSelectedRow(),2).toString(),tbBilling.getValueAt(tbBilling.getSelectedRow(),6).toString(),
                                                    tbBilling.getValueAt(tbBill
                                                ing.getSelectedRow(),8).toString()
                                                });
                                            }else{
                                                Sequel.queryu2("delete from temporary_tambahan_potongan where no_rawat=? and nama_tambahan=? and status=?",3,new String[]{
                                                    TNoRw.getText(),tbBilling.g
                                                etValueAt(tbBilling.getSelectedRow(),2).toString(),tbBilling.getValueAt(tbBilling.getSelectedRow(),8).toString()
                                                });
                                                tbBilling.setValueAt(0,tbBilling.getSelectedRow(),0);
                                            }                                    
                                        }ca tch(Exception e){
                                            Se quel.queryu2("delete from temporary_tambahan_potongan where no_rawat =?
                                                     and nama_ta mb ah an=? and status=?",3,new String[]{
                                                TNoRw.getText()
                                                        ,tbBilling.getValueAt(tbBilling.getSelectedRow(),2).toString(),tbBilling.getValueAt(tbBillin
                                                        g. getSelectedR o
                                                                
                                                                 
                                                                        
                                                                 
                                                                        
                                                        lling.setValueAt(0,tbBilling.getSelectedRow(),0);
                                        }       
                                                         
                                                                
                                                                 
                                                                        
                                                                 
                                                                        
                                                                 
                                                                        
                                                        
                                }  
                            } catch (Exception e) {
                                                        
                                                          
                                                                
                                                                 
                                                                        
                                                                 
                                                                        
                                                                 
                        }                          
                    }                    
                                        ) ==Key Event.VK_SPAC E){
                    i=tbBilling.getSelectedColumn();
                                                    
                                                      
                                                            
                                                             
                                                                    
                                                             
                                                                    
                                                    
                            akses.setform("DlgBilingRalan");  
                            switch (tbBil

                                        kses.gettindakan_ralan()==true){
                                        MnInputTindakanActionPerformed(null);
                                    }
                                    break;
                                case "Obat & BHP":
                             
                         
                     
                                      //dis po se(); 
                                      break;
                                    case "Tambahan Biaya":
                                    if(akses.gettambahan_biaya()==true){
                                        MnTambahanActionPerformed(null);
                                    }                            
                                    break;
                                case " Potongan Biaya":   
                                    if(akses.getpotongan_biaya()==true){
                                        MnPotonganActionPerformed(null);
                                    }
                                    break;
                            }    
                        } catch (Exception e) {
                            akses.set
                                    tb Billing.getValueAt(tbBilling.getSelectedRow(), i).toString()) {
                                case "Tindakan":
                                    if(akses.gettindakan_ralan()==true){
                                         MnInputTindakanActionPer fo rmed( null);
                                    }
                                    b
                                     "Obat & BHP":
                                    if(akses.getberi_obat()==true){
                                         MnInputObatActionPerform ed (null );
                                    }                            
                                    //dispose();
                                    break;
                                case "Tambahan Biaya":
                                    if(akses.gettambahan_biaya()==true){
                                        MnTambahanActionPerformed(null);
                                    }                            
                                    break;
                                case " Potongan Biaya":   
                                    if(akses.getpotongan_biaya()==true){
                                        MnPotonganActionPerformed(null);
                                    }
                                    break;
                            }    
                        }                        
                    }
                                     
            }
        }
}//GEN-LAST:event_tbBillingKeyPressed    

private void MnRawatJalanActionPerfor
                                    equals("")){
            JOptionPane.showMessageDialog(null,"Maaf, Pasien belum dipilih...!!!");
            TNoRw.requestFocus();    
        }else{
            akses.setform("DlgBilingRalan");
            DlgRawatJalan dlgrwjl2=new DlgRawatJalan(null,false);
            dlgrwjl2.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
            dlgrwjl2.setL
                    .SetPoli("-");
            dlgrwjl2.SetPj(Sequel.cariIsi("select reg_periksa.kd_pj from reg_periksa where reg_periksa.no_rawat=?",TNoRw.getText()));
            dlgrwjl2.setNoRm(TNoRw.getText(),DTPTgl.getDate(),DTPTgl.getDate());    
         
                 dlgrwjl2.setVisible(true)

    }//GEN-LAST:event_MnRawatJalanActionPerformed 
  
private void MnPemberianObatActionPerformed(jav a.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnPemberianObatActionPerformed
        if(TPasien.getText().trim().equals("")){
             J OptionPane.showMessageDialog(null,"Maaf, Pasien belum dipilih...!!!");
            TNoRw.requestFocus();
        }else{   
            DlgPemberianObat dlgrwinap=new DlgPemberia n Oba t(null,false);  
            dlgrwinap.setSize(internalFrame1.getWidth(),internalFrame1.getHeight());
            dlgrwinap.setLocationRelativeTo(internalFrame1);
            dlgrwinap.isCek();
                    
            dlgrwinap.setNoRm(TNoRw.getText() ,DTPTgl.getDate() ,new Date(),"ralan
            dlgrwinap.tampilPO3();
            dlgrwinap.setAlwaysOnTop(false);
         
             }

     
private vo id DTPTglKeyPressed(java.awt.event.Ke yEvent evt) {//GEN-FIRST:event_DTPTglKeyPressed
       Valid.pindah(evt,TNoRw,BtnNota); 
}//GEN-LAST:event_DTPTglKeyPressed
  
private void MnHapusTagihanActionPerfo r med(java.awt.event.ActionE vent evt) {//GEN-FIRST:event_MnHapusTagihanActionPerformed
    try { 
        i=0;
        pscekbilling=koneksi.prepareStatement(sqlpscekbilling);
        try{   
            pscekbilling.setString(1,TNoRw.getText());
            rscekbilling=pscekbilling.executeQuery();
            if(rscekbilling.next()){
         
                 }

                i=0; 
             System.out.p rintln ("Notifik
             } finally{

                    rscekbilling.close(); 
             
                  
                    psce k billing.close();
                 
                 
                >0){  
                if (!tbBilling.getValueA t
                    J O ptionPane.showMessageDi
                }
                     this.setCursor
                      
                    sukses=true;     
                    S e
                     if((-1*ttlPotongan)> 0
                        if(Sequel.menyimp
                 
                         }      
                    }
                
             
                        
                             sukses=false;  
                        }     
                     } 
                    
                    if(ttlLaborat>0){
                        if ( Seque
                            sukses=false;
                         }       
                         
                                    
                                             
                                      
                                       
                              
                        t
                     

                         }           
                         
                                    
                                               
                                        
                                       
                              
                        l
                    p

                    if ((ttlObat-o b at l
                        if (Sequel.menyimpantf("tampjurnal",
                                "'" + akunbillingralan.getObat_Ralan()+"' , 'Obat Ralan','"+(ttlO b at-obatlan
                                        g sung-ppn
                                obat)+"','0'"," d ebet=debet+' " +(tt
                                lObat-obat l angsung-ppnobat)+"'","kd_rek='"+aku n bill in gralan .
                            sukses = false;
                        }
                    }

                    if (obatlangsung > 0) {
                        if (Sequel.menyimpantf("tampjurnal",
                                "'" + akunbillingralan.getObat_Langsung_Ral a n()+"','Obat Ralan','"+ o batlangsung+
                                        " ','0'","
                                debet=debet+'"+ o batlangsung+"' " ,"kd
                                _rek='"+ak u nbillingralan.getObat_Langsung_Ralan( ) +"'" )= =false )
                            sukses = false;
                        }
                    }

                    if(ppnobat>0 ) {
                            
                            
                        if( S equel.menyimpantf("t
                            ampjurnal","'"+PPN_Keluaran+"','PPN Keluaran','"+ppnobat+"','0'","debet=debet+'"+ppnobat+"'","kd_rek='"+PPN_Keluaran+"'")==false
                            ){

                         }         
                         
                                    
                                               
                                        
                                       
                              
                        t
                     

                         }    
                         
                                      
                                         
                                    
                                       
                              
                        t
                     

                         }      
                         
                                        
                                            
                              
                        t
                     

                         }       
                         
                                      
                                         
                                    
                                       
                              
                        u
                     

                             "on ak u n_ b
                         
                                      
                                         
                                    
                                       
                            unbaya r .setSt
                        r
                     

                                 s u ks e
                            }
                                      
                                         
                                    
                                       
                              
                        c
                     

                    } finally{  
                            sakunbayar != null){ 
                                    rsakunbayar.close();
                                    
                                    
                         
                            psakunbayar.close(); 
                        }   
                          
                             
                                          
                                                 
                                        
                                           
                                g=kone k si.pre
                             
                         
                             "akun_piutang 
                            "w h ere de
                          
                        psaku n
                        rs akunpiutang=psakunpiu t
                            e(rsakunpiutang.next
                         
                                 sukses=false; 
                            }
                        }
                    }

                        System.ou t .println("Notifikasi Akun
                            y{ 
                                    sakunpiutang != null){
                                    
                                    rsakunpiutang.close();
                                    
                                    
                         
                            psakunpiutang.close(); 
                        }   
                          
                             
                                        
                                               
                                               
                                        
                                           
                                rue){  
                            i
                         
                                 sukses=jur
                            }     
                        }else if(piutang<=0){  
                            i f
                                 sukses=jur.simpan J
                            }   
                        }
                          
                            
                        u
                     

                         Seque l. query u
                        Se quel.que r yu 2
                            el .queryu 2( "dele t
                                ueryu2 ( "delete from tagihan_bpd_jateng w here
                                         no_rkm_medis='"+TNoRM.getText()+"' and  n o_rawat='"+TNoR w .ge
                                                t Text()+"' and s t atu s _lanjut='Ralan' a n d status_bayar='Pend
                                                i ng'");
                            e
                        S equel.q ueryu2(" de le t
                            el .queryu 2( "dele t
                                ueryu2 ( "delete from tagihan_mandiri wher e no
                                        _rkm_medis='"+TNoRM.getText()+"' and no_raw a t='"+TNoRw.getT e xt(
                                                ) +"' and status_ l anj u t='Ralan' and sta t us_bayar='Pending'")
                                                ; 
                            d
                        V
                     

                         }   
                        Sequel.Commit();    
                        e{    
                        OptionPane.showMessageDialog(null,"Terjadi kesalahan saat pemrosesa n  data, transaks i  diba
                        equel.RollBack();    
                            
                          
                                   
                                 
                        l.AutoComitTrue();  
                                   
                                 
                        kses==true){  
                                   
                                 
                        JOptionPane.showMessageDialog(rootPane,"Proses hapus data Nota  S alah selesai..!
                                ! ");    
                                   
                                 
                        Valid.tabelKosong(tabModeAkunBayar);  
                                   
                                 
                        Valid.tabelKosong(tabModeAkunPi utang) ; 
                        isRawat();   
                        
                            ursor(Cursor.getDefaultCursor());
                        
                        0){
                    i onPa n
                        
                                
                        ion e) {
                    o

                    nt_MnHapusTagihanAction
                        
                        alanActionPerformed(java.awt.event.Acti onEvent evt) {//GEN-FIRST:event_MnPenjualanA
                        JlDr.getRowCount()==0){
                        ane.showMessageDialog(null,"Maaf, tabl
                        
                    P
                    jualan.isCek();
                p
                 
                penjualan.setSize(internalFrame1.getWid
                        th(),internalFrame1.getHeight());
             
                penjualan.setAl
                penjualan.setVisible(true);  
         
    }// GEN-LAST:event_MnPenjualanActionPerformed

    private void MnDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN -FIRST:event_MnDokterActionPerformed
    if(Seq uel.cariRegistrasi(TNoRw.get Te xt ())>0){
        JOptionPane.showMessageDialog(rootPane, "Data billing sudah terverifikasi..!!");
    }else {  
        WindowGantiDokterPoli.setS i ze(630,80); 
            owGantiDokterPoli.setLocationRelativeTo(internalFrame1);
        WindowGantiDokterPoli.setAlwaysOnTop(fa
                    lse); 
        WindowGantiDokterPoli.setVisible(true);
    } 
}//GEN-LAST:event_MnDokterActionPerformed

private void BtnCloseIn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCloseIn1ActionPerformed
    Windo
    }// GEN-LAST:event_BtnCloseIn1ActionPerformed

    private void BtnCloseIn1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-F IRST:event_BtnCloseIn1KeyPressed
        OD O add your handling code here:   
            AST:event_BtnCloseIn1KeyPressed 
          
            void BtnSimpan1ActionPerformed(jav a.aw
            if(TNoRw.getText().trim().equals("")){
                Valid.textKosong(TNoRw,"No.Rawat");
            }if(kddokter.getText().trim().equals(""
         
             }else{

                Valid.editTable(tabModeRwJlDr,"rawat_jl_dr","no_rawat",TNoRw,"  kd_dokter='"+kddokter.getText()+"'");
                Valid.editTable(tabModeR
                 isRawat();

            } 
        }//GEN-LAST:event_BtnSimpan1ActionPe
     

    // TODO add your handling code here: 
}//GEN-LAS T:event_BtnSimpan1KeyPressed 
 
private v
        oi d kddokterKeyPressed(java.awt.event.K ey Event evt) {//GEN-FIRST:event_kddokt erKeyPressed
        if(evt.getKeyCode()==KeyEvent. VK_UP){
             b tnCariDokterActionPerformed(null);
        }else{        
            Valid.pindah(evt,BtnCloseIn1,B tnSimpan1);       
        }   
                        
}//GEN-LAST:event_kddokterKeyPressed

private v
             

     
        private void btnCariDokterActionPerf
         DlgCariDokter dokter=new DlgCariDo

            @Override 
        pu blic void windowO pe ned(WindowEvent  e) {}
        @Override
        p ubli c void windowClosing(WindowEvent e) {}
        @Override  
        p
                 if(dokter.getTable().get

                    TDokter.setText(dokter.getTable().getValueAt(do kter.getTable().getSelectedRow(),

                 kddokter.requestFocus()

            @Override 
            public void wind o wIconified(WindowEvent  e) {}
            @Override
            public vo
            @Override
            

            public vo
            @Override
            

            public vo
            
                sC ek();  
                    ze(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20); 
                    cationRelativeTo(internalFrame1); 
                e
                etVisible(true);
            A

            
            void MnObatLangsungActionPerformed(java.awt.
            e

            lObat.set
            owObatLangsung.setSize(590,80);
            

            owObatLan
            lObat.requestFocus();
            

            owObatLan
            owObatLangsung.setVisible(true);
            
        EN-
        
        ate void TotalObatKeyPressed(java.awt.ev e nt. KeyEvent evt) {//GEN-FIRST : even
            Valid.pindah(evt,BtnCloseIn,BtnNota);
        EN-LAST:event_TotalObatKeyPre
        
    pri vate void BtnCloseInActionPerformed(java.aw

    }//GEN-LAST:event_BtnCloseInActionPerformed 
        
                
                
        ate void BtnCloseInKeyPressed(j ava.
            if(evt.getKeyCode()==KeyEvent.VK_SPACE){
                WindowObatLangsun
            }else{Valid.pindah(evt, BtnNota, Tota
        EN-LAST:event_BtnCloseInKeyPressed
     

            if(TNoRw.getText().trim().equals("")){ 
            Valid.textKos ong(TNoRw," No.Rawat"
             }else{

                WindowObatLangsung.dispose(); 
            isRawat();
                 isKembali();

    }//GEN-LAST:event_BtnSimpan2ActionPerformed 
    
private void BtnSimpan2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpan2KeyPressed
        V alid .
            pindah(evt,TotalObat,BtnBatal1);
        
    }// GEN-LAST:event_BtnSimpan2KeyPressed

    private void BtnBatal1ActionPerformed(java.awt.event.ActionEvent evt) {//G EN-FIRST:event_BtnBatal1ActionPerformed
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else {
            Sequel.queryu("delete from tagihan_obat_l ang s ung where no_ra w at=?  " ,TNoRw.getText());  
                    
            WindowObatLangsung.dispose();
            isRawat();isKembali();
        }
}//GEN-LA
     

            if(evt.getKeyCode()==KeyEvent.VK_SPACE){ 
            BtnBatal1Acti onPerforme d(null);
             }else{Valid.pindah(evt, BtnNot

     
private vo id MnTambahanActionPerformed(java.a wt.event.ActionEvent evt) {//GEN-FIRST:event_MnTambahanActionPerformed
    if(Sequel.cariRegistrasi(TNoRw. getText())>0){
        J Opti onPane.showMessageDialog(rootPane,"Data billing sudah terverifikasi..!!");
    }else{  
        norawat.setText(TNoRw.getText());
        tampilTambahan
            (norawat.getText());
        W
             WindowTambahanBiaya.setLocationRel

            WindowTambahanBiaya.setVisible(true);              
    }       
}//GEN-LAST:event_MnTambahanActionPerformed
  
            
        
    pri vate void norawatKeyPressed(java.a

    }//GEN-LAST:event_norawatKeyPressed 
            
            void BtnTambahActionPerformed(java.awt. event.ActionEvent evt) {//GEN-FIRST:even
        t abMo d
            AST:event_BtnTambahActionPerforme
            
            void BtnSimpan3ActionPerformed(java.awt.event.ActionEv ent evt) {//GEN-FIRST:event_
            orawat.getText().trim().equals("")||(tbTambahan.getRowCoun
            Valid.textKosong(norawat,"Data");
            e{
         
                 if(Valid.SetAngka(tbTambahan.ge

                            "','"+tbTambahan.getValueAt(i,1).toStri ng()+"'","Tambahan Biaya");
                }
             }

            isKembali(); 
            WindowTambahanBiaya.dispose();    
         }

     
        at e void BtnHapusActionPerformed(java. aw t.event.ActionEvent evt)  {/ /GE N
            el.queryu("delete from ta mbahan_b
                
            ode Ta m ba h a n.removeRow(tbTambahan.ge tSel e
                );     
                    
                                    
                                    usAct i onPerformed   
                            
                
            v
            owTambahan
            AST:event_Bt
            
        a
         WindowGantiPoli.dispose();

     
        ate void BtnSimpan4ActionPerformed(java.awt.event.ActionEve n t evt) {//GEN-FIR S
                oRw.getText().trim() . equals("")){  
                Valid.textKosong(TNoRw,"No.Rawat");
            }if(kd
                Vali
             }else{

                isRawat(); 
                WindowGantiPoli.dispos
             }

     
        ate void kdpoliKeyPressed(
             if(evt.getKeyCode()==KeyEvent.VK_PAG

            }else if(evt.getKeyCode()==KeyEvent.VK_UP){ 
              btnCariPoliActionPerformed(null) ;
        }else{ 
         
            Valid.pindah(evt,BtnCloseIn4,BtnSi mp an4); 
        } 
}//GEN-LA ST:e vent_kdpoliKeyPressed
        
private void btnCariPoliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariPoliActionPerformed
        akses.setform("DlgBilingRalan");
        D
             poli.addWindowListener(new WindowLi

                public void windowOpened(WindowEvent e) {} 
             @Override   
            public void windowClosing(WindowEvent e) {}
                    
             @Ove rride   
            public void windowClosed(WindowEvent e) {
                  if(akses.getform().equals("DlgBilingRalan")){
                    if(poli.g etTable().ge tSelectedRow()!= -1){
         
                             nmpoli.set

                        kdpoli.requestFocus(); 
                }
            }   
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            

            public void windowDeiconified(WindowEvent e) {}
            @Override
            

            public void windowActivated(WindowEvent e) {}
            @Override
            public  void windowDeactivated(WindowEvent e) {} 
        });   
        poli.isCek(); 
        poli.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20); 
        poli.setLocat
                    ysOnTop(false);
        poli.setVisible(true);
}//GEN-LAST:e

            
private void MnPoliActionPerformed(java.awt.event.Action
            E

            ntiPoli.setSize(630,80);
    WindowGantiPoli.setLocationRelativeTo(internalFrame1);
            

            ntiPoli.setAlwaysOnTop(false);
    WindowGantiPoli.setVisible(true);
            

            event_MnPoliActionPerformed

            
private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        isRawat();
}//GEN-LAST:event_BtnCariActionPerformed     

private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if(evt.getKeyCode()==K
                 BtnCariActionPerformed(null);

                Valid.pindah(evt, TNoRw,DTPTgl); 
            } 
        EN-LAST:event_BtnCariKeyPressed
        
        ate void norawatpotonganKeyPresse
    //  TODO add your handling code here:

     
private void BtnTa
      t abModePotongan.addRow(new Object[]{tr

     
private vo id BtnSimpanPoton ga nActionPerformed(j ava.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanPotonganActionPerformed
   if(norawatpotongan.getText().trim().equals("")||(tbPotongan.getRowCount()<=0)){
        V alid .textKosong(norawat,"Data");
    }else{ 
        f
                 if(tbPotongan.getValueA

                               "','"+tbPotongan.getValueAt(r,2).toString()+ "'","Potongan Biaya");
                    }
             }

            isKembali(); 
              WindowPotonganBiaya.dispose() ;    
         }

     
        va te void BtnHapusPotonganActionPerformed(java .a wt.event.ActionEvent evt)  { //G E
            el.queryu("delete from pe nguranga
                
            ode Po t on g a n.removeRow(tbPotongan.ge tSel e
                to ngan(norawatpotongan.getT ext());
                            
                    
                                    
                                         
                            
                e
            
            void BtnKe
            PotonganBiay
            AST:event_BtnKeluarPotonganAct
        
    pri vate void MnPotonganActionPerformed(java.awt.ev

            JOptionPane.showMessageDialog(rootPane,"Data billing sudah terverifikasi  ..!!");
        }else{    
                watpotongan.setText(TNoRw. g etText());  
            tampilPotongan(norawatpotongan.getText());
            WindowPotonganBiaya.setSize(internalFr
            Window
            WindowPo
             WindowPotonganBiaya.setVisible(true);    

    }//GEN-LAST:event_MnPotonganActionPerformed 
        
    pri vate void MnPeriksaLabActionPerformed(java.awt.

                JOptionPane.showMessageDialog(null,"Maaf, Silahkan anda pilih  dulu dengan menklik data pada table...!!!
             }else{        
                if(Sequel.cariRegistrasi(TNoRw.getT ext())>0){
                
                }else{ 
                    DlgPeriksaLaboratorium periksalab=
                    periksalab.setSize(internalFrame1.getWidth(),i nternalFrame1.getHeight());
                    periksalab.setLocationRelativeTo(internalFrame1);
                    periksalab.emptTeks();
                    periksalab.setNoRm(TNoRw.getT
         
                     periksalab.setAlwaysOnTop(f

                } 
        }  
}//GEN-LAST:event_MnPeriksaLabActionPerformed 
  
            vo id BtnSimpanActionPerformed(java.awt.ev e nt .ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
         
//        if  (!ch e
                turn; // Keluar dari method jika  a da mismatch 
//        } 
        
        try {
            pscekbilling = koneksi.prepareStatement (sqlpscek
                {
                pscekbilling.setString(1, TNoRw.getText());
                rscekbilling = pscekbilling.executeQuery();
                if (rscekbilling.next()) {
         
                     }
            } catch (Exception e) {
                cek = 0; 

             } finally {
             if (rscekbilling != null) {
            

                if (pscekbilling != null) {
                    pscekbilling.close();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        if (TNoRw.getText().trim().equals("") || TNoRM.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "Pasien");
        } else if ((chkObat.isSelected() == false) || (chkPotongan.isSelected() == false)
                || (chkTambahan.isSelected() == false) || (chkTarifDokter.isSelected() == false) || (chkTarifPrm.isSelected() == false)) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan tampilkan semua pilihan tagihan...!!!");
        } else if (cek > 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data tagihan pasien dengan No.Rawat tersebut sudah pernah disimpan...!!!");
        } else if (cek == 0) {
            if (piutang <= 0) {
                if (kekurangan < 0) {
                    JOptionPane.showMessageDialog(null, "Maaf, pembayaran pasien masih kurang ...!!!");
                } else if (kekurangan > 0) {
                    if (countbayar > 1) {
                        JOptionPane.showMessageDialog(null, "Maaf, kembali harus bernilai 0 untuk cara bayar lebih dari 1...!!!");
                    } else {
                        isSimpan();
                
                    }
                } else if (kekurangan == 0) {
                    isSimpan();
                
                }
            } else if (piutang >= 1) {
                if (kekurangan < 0) {
                    
                    JOptionPane.showMessageDialog(null, "Maaf, piutang belum genap. Silahkan isi di jumlah piutang ...!!!");
                } else if (kekurangan > 0) {
                    JOptionPane.showMessageDialog(null, "Maaf, terjadi kelebihan piutang ...!!!");
                } else {
                    isSimpan();
                }
            }
                                
        }
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnSimpanKeyPressed

    private void chkPotonganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPotonganActionPerformed
        isRawat();
                            
    }//GEN-LAST:event_chkPotonganActionPerformed

    private void chkLaboratActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkLaboratActionPerformed
        isRawat();
    }//GEN-LAST:event_chkLaboratActionPerformed

    private void chkTarifDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkTarifDokterActionPerformed
         isRawat();
    }//GEN-LAST:event_chkTarifDokterActionPerformed
 
    private void chkTarifPrmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkTarifPrmActionPerformed
         isRawat();
    }//GEN-LAST:event_chkTarifPrmActionPerformed
 
    private void chkTambahanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkTambahanActionPerformed
         isRawat();
    }//GEN-LAST:event_chkTambahanActionPerformed
 
    private void chkObatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkObatActionPerformed
         isRawat();
    }//GEN-LAST:event_chkObatActionPerformed
 
    private void MnInputObatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnInputObatActionPerformed
         if(TPasien.getText().trim().equals("")){
            JOptionPane.showMessageDialog(null,"Maaf, Pasien belum dipilih...!!!");
            TNoRw.requestFocus(); 
        }else{
             if(Sequel.cariRegistrasi(TNoRw.getText())>0){
                JOptionPane.showMessageDialog(rootPane,"Data billing sudah terverifikasi ..!!");
            }else{  
                DlgCariObat dlgobt=new DlgCariObat(null,false);
                 dlgobt.emptTeksobat();
                dlgobt.setNoRm(TNoRw.getText(),TNoRM.getText(),TPasien.getText(),Sequel.cariIsi("select reg_periksa.tgl_registrasi from reg_periksa where reg_periksa.no_rawat=?",TNoRw.getText()),
                        Sequel.cariIsi("select reg_periksa.jam_reg from reg _periksa where reg_periksa.no_rawat=?",TNoRw.getText()));
                dlgobt.isCek();
                 dlgobt.tampilobat();
                dlgobt.setSize(internalFrame1.getWidth(),internalFrame1.getHeight());
                dlgobt.setLocationRelativeTo(internalFrame1); 
                 dlgobt.setAlwaysOnTop(false); 
                dlgobt.setVisible(true);   
            }
        }  
    }//GEN-LAS T:event_MnInputObatActionPerformed   
 
    private v oid  M
                en.getText().trim( ) .equals("")){ 
            JOptionPane.showMessageDialog(null,"Maaf, Pasien belum dipilih...!!!");
            TNoRw.requestFocus();  
                        
                                
                                
        }else{
                                
            if(Sequel.cariRegistrasi(TNoRw.getText())>0){
                JOptionPane.showMessageDialog(rootPane,"Data billing sudah terverifikasi ..!!");
            }else{  
                kdptg=Sequel.cariIsi("select reg_periksa.kd_dokter from reg_periksa where reg_periksa.no_rawat=?",TNoRw.getText());
                nmptg=Sequel.CariDokter(kdptg);
                DlgCariPerawatanRalan dl
                dlgrwjl.setNoRm(TNoRw.getText(),kdptg,nmptg,"rawat_jl_dr","-","-");
                dlgrwjl.isCek();
                 dlgrwjl.setSize(internalFrame1.getWidth(),internalFrame1.getHeight());
                dlgrwjl.setLocationRelativeTo(internalFrame1);
                dlgrwjl.setAlwaysOnTop(false); 
                 dlgrwjl.setVisible(true);                             
            }             
        }                            
    }//GE N-LA ST:event_MnInputTindakanActionPerformed
    
    private void MnCariPeriksaLabActionPerformed(java.a wt.event.ActionEvent evt) {//GEN-FIRST:event_MnCariPeriksaLabActionPerformed
        if(TP asie n
                ionPa n e.showMessageDialog(null,"Maaf, Silahkan anda pilih dulu dengan menklik data pada table...!!
                        !");
        }else{             
            DlgCariPeriksaLab periksalab=new  D lgCariPeriksaLab(null,false); 
            periksalab.setSize(internalFrame1.ge tWidth (),int ernalFrame1.ge tHei ght());
            periksalab.setLocationRelativeTo(internalFrame1);
            periksalab.SetNoRw(TNoRw.getText());   
            periksalab.setAlwaysOnTop(false);
            periksalab.setVisible(true);
        }
            A
        
    pri vate void chkRadiologiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRadiologiActionPerformed
        isRawat();
    }//GEN-LAST:event_chkRadiologiActionPerformed 
  
    private void MnPeriksaRadiologiActionPerfor med(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnPeriksaRadiologiActionPerformed
        i f(TP a
            JOptionPane.showMessageDialo g (null,"Maaf, Silahkan anda  pilih dulu dengan menklik data pada table...!!!");
        }else{                
            if(Sequel.cariRegistrasi(TNoRw.getText())>0){
                JOptionPane.showMessageDialog(ro
            }else{ 
                DlgPeriksaRadiologi periksalab=new DlgPeriksaRadiologi(null,false);
                periksalab.setSize(internalFrame1.getWidth(),internalFrame1.getHeight());
                 periksalab.setLocationRelativeTo(internalFrame1);
                periksalab.emptTeks();
                periksalab.setNoRm(TNoRw.getText(),"Ralan");   
                periksalab.isCek();
                 periksalab.setAlwaysOnTop(false);
                periksalab.setVisible(true);                            
            } 
        }  
    }//GEN-LAST:event_MnPeriksaRadiologiActionP erformed
  
            vo id MnCariRadiologiActionPerformed(java. a wt .event.ActionEvent evt) {//GEN-FIRST:event_MnCariRadiologiActionPerformed
        if(TPasien.getText().trim().equals("")){ 
            J Opti o
                            
            DlgCariPeriksaRadiologi periksalab=new DlgCariPer iksaRadiologi(null,false);
            periksalab.setSize(internalFrame1.getWidth(),internalFrame1.getHeight());
            periksalab.setLocationRelativeTo(internalFrame1);
            periksalab.SetNoRw(TNoRw.getText());   
                ksalab.setAlwaysOnTop(false);
            periksalab.setVisible(true);
        }
            AST:event_MnCariRadiologiActionPerformed

    pri vate void chkAdministrasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAdministrasiActionPerformed
        isRawat();
    }//GEN-LAST:event_chkAdministrasiActionPerformed 
  
    private void chkSarprasActionPerformed(java .awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSarprasActionPerformed
        i sRaw a
            AST:event_chkSarprasActionPerforme d  
 
    private void MnOperasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnOperasiActionPerformed
        if(TPasien.getText().trim().equals("")){
            JOptionPane.showMessageDialog(null,"Maaf, Silahkan anda pilih dulu dengan menklik data pada table...!!!");
        }else{ 
            DlgTagihanOperasi dlgro=new DlgTagihanOperasi(null,false);
             dlgro.setSize(internalFrame1.getWidth(),internalFrame1.getHeight());
            dlgro.setLocationRelativeTo(internalFrame1);
            dlgro.setNoRm(TNoRw.getText(),TNoRM.getText()+", "+TPasien.getText(),"R alan");
            dlgro.setVisible(true);
         }
    }//GEN-LAST:event_MnOperasiActionPerformed
 
    private void MnPenjabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnPenjabActionPerformed
         WindowGantiPenjab.setSize(630,80);
        WindowGantiPenjab.setLocationRelativeTo(internalFrame1);
        WindowGantiPenjab.setAlwaysOnTop(false); 
        Wi ndowGantiPenjab.setVisible(true); 
    }//GEN-LAST:event_MnPenjabActionPerformed 
  
            void btnPenjabActionPer f ormed(java.awt.event.Action Event evt) {//GEN-FIRST:event_btnPenjabActionPerformed
        DlgCariCaraBayar penjab=new DlgCariCaraBayar (null,false);
        penjab.addWindowListener(new WindowListener() {
            @Override      
            public void windowOpened(WindowEvent e) {}
            @Override
             public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) { 
                if(penjab.getTable().g etSelectedRow()!= -1){
                    kdpenjab.setText(penjab.getTable().getValueAt(penjab.getTable().getSelectedRow(),1).toString());
                    nmpenjab.setText(penjab.getTable().getValueAt(penjab.getTable().getSelectedRow(),2).toString());
                } 
                 kdpenjab.requestFocus();
            }
            @Override 
            public void windowI c onified(WindowEvent e) {} 
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            

            public void windowActivated(WindowEvent e) {}
            @Override
            

            public void windowDeactivated(WindowEvent e) {}
        });
           
        penjab.getTable().addKeyListener(new KeyListener() { 
            @Override 
            publi
                rride
            p

                if(e.getKeyCode()==KeyEvent.VK_SPACE){
                    penjab.dispose();
            

                }
            }
            

            @Override
            public void keyReleased(KeyEvent e) {}
            

            
        penjab.isCek();
            
        pen

        penjab.setAlwaysOnTop(false);
        penjab.setVisible(true);
    }//GEN-LAST:event_btnPenjabActionPerformed
            

            
    private void kdpenjabKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kdpenjabKeyPressed
        if(evt.get KeyCode()==KeyE ve nt.VK_PAGE_DOWN){ 
            Sequel.cariIsi("select poliklinik.nm_poli from poliklinik where poliklinik.kd_poli=?", nmpenjab,kdpenjab.getText());
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            b

            e{
            Valid.pindah(evt,BtnCloseIn4,BtnSimpa
            n4);
        }
        EN-LAST:event_kdpenjabKeyPressed
     
    private void BtnSimpan5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpan5ActionPerformed
        if(kdpenjab.getText().trim().equals("")||nmpenjab.getText().trim().equals("")){
            Valid.textKosong(kdpenjab,"Jenis Bayar");
         }else{
            Sequel.mengedit("reg_periksa","no_rawat=?"," kd_pj=?",2,new String[]{kdpenjab.getText(),TNoRw.getText()});
            isRawat(); 
             WindowGantiPenj ab .dispose(); 
        }
                    
    }//GE N-LAST: event_BtnSimpan5A ct ionPerformed 

    priva te v oid BtnCloseIn5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCloseIn5ActionPerformed
        WindowGantiPenjab.dis pose(); 
    }//GEN-LAST:event_BtnCloseIn5ActionPerformed
 
    private void tbAkunBayarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbAkunBayarKeyPressed
        if(tabModeAkunBayar.getRowCount()!=0){ 
             if(evt.getKeyCode()==KeyEvent.VK_EN TE R){ 
                if(tbAkunBayar.getRowC ount()!=0){
                      if((tbAkunBayar.getSelectedColumn()==2)||(tbAkunBayar.getSelectedColumn()==3)||(tbAkunBayar.getSelectedColumn()==4)){
                        if(!tabModeAkunBay ar.getValueAt (tbAkunBaya r.
                    getSelectedR o w(),2).toString().e quals("")){ 
                            tbAkunBayar.setValueAt(
                                    Valid.roundUp((Valid.SetAngka(tbAkunBayar.getValueAt(tbAkunBayar.getSelectedRow(),3).toString())/100)*
                                    Valid.SetAngka(tbAkunBayar.getValueAt(tbAkunBayar.getSelectedRow(),2).toString()),100),tbAkunBayar.getSelectedRow(),4);
                         }else{
                            tbAkunBayar.setValueAt("",tbAkunBayar.getSelectedRow(),4);                        
                        }                             
                    }
                 }
                isKembali();
            } 
        }    
    }//GEN-LAS T:event_tbAkunBay ar KeyPressed 
    
    private void tbAku nPiutangKeyPressed(java.awt.event .K ey Ev ent evt) {//GEN-FIRST:event_tbAk un Pi
                            ut angKeyPressed   
        if(tabModeAkunBaya r.getRowCount()!=0){  
            if(evt.getKeyCode()==KeyEvent.VK_ENTER){
                isKembali();
                                            
                                                     
                                                      
                                                    
                                                             
                                            
                                     
        }  
    }//GEN-LAST:event_tbAkunPiutangKeyPressed  
                        
                    kunBayarPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbAkunBayarPropertyChange
        if(this.isVisible()==true){
              isKembali();
        }
    }//GEN-LAST:event_tbAkunBayarPropertyChange
 
    private void tbAkunPiutangPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbAkunPiutangPropertyChange
        if(this.isVisible()==true){ 
               isKembali();   
        }    
    }//GEN-LAST:event_tbAkunPiutangPropertyChange

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
         if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            BtnCariBayarActionPerformed(null);
        } 
    }//GEN -LAST:event_TCari Ke yPres s
            
    private void BtnCariBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariBayarActionPerformed
         if(status.equals("belum")){
            tampilAkunBayar2();
        }else if(status.equals("sudah")){ 
             tampilAkunBayar Te rsimp a
            
        isHitung();
         isKembali();
    }//GEN-LAST:event_BtnCariBayarActionPerformed
 
    privat e void BtnCariBay ar KeyPressed(java.aw t.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariBayarKeyPressed
        
    }//GEN-LAST:event_BtnCariBayarKeyPressed
 
    private void TCari1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCari1KeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){ 
             btnCariPiutangActionPe rformed(null);
        }
    }//GE N-LAST: event_TCari1KeyPressed 

    private void btnCariPiutangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariPiutangActionPerformed
        if(status.equals("belum")){
            tampilAkunPiutang2();
         }else if(status.equals("sudah")){
            tampilAkunPiutangTersimpan();
        } 

         isKembali();
    }//GEN-LAST:event_btnCariPiutangActionPerformed
 
    privat e void btnCariPiu ta ngKeyPressed(java. awt.event.KeyEvent evt) {//GEN-FIRST:event_btnCariPiutangKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCariPiutangKeyPressed
 
    private void ppBersihkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppBersihkanActionPerformed
        Valid.tabelKosong(tabModeAkunBayar); 
        if (status.equals("belum")) {
            tampilAkunBayar2();
        } else if (status.equals("sudah")) {
            tampilAkunBayarTersimpan();
        }
    }//GEN-LAST:event_ppBersihkanActionPerformed

    pri vate void ppBersihkan1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppBersihkan1ActionPerformed
        Valid.tabelKosong(tabModeAkunPiutang);
        if(status.equals("belum")){ 
            tampilAkunPiutang2();
         }else if(status.equals("sudah")){
            tampilAkunPiutangTersimpan();
        } 
    }//GEN-LAST:event_ppBersihkan1ActionPerformed
  
    private void TabRawatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TabRawatMouseClicked
        i f(TabRa wat.getSelectedIndex()== 2){
            try {
                Valid.tabelKosong(tabModeLab);
                 pscarilab=koneksi.prepareStatement("select permintaan_lab.noorder,permintaan_lab.tgl_permintaan,"+
                    "if(permintaan_lab.jam_permintaan='00:00:00','',permintaan_lab.jam_permintaan) as jam_permintaan,"+
                    "if(permintaan_lab.tgl_hasil='0000-00-00','Belum Terlayani', 'Sudah Terlayani') as status,"+
                    "dokter.nm_dokter from permintaan_lab inner join dokter on permintaan_lab.dokter_perujuk=dokter.kd_dokter "+
                     "where permint aan_lab.status='ralan' and permintaan_lab.no_rawat=? order by permintaan_lab.tgl_permintaan,permintaan_lab.jam_permintaan desc");
                try {
                      pscarilab.setString( 1,TNoRw.getText());
                    rscarilab=pscarilab.executeQuery();
                    while(rscarilab.next()){
                         tabModeLab.addRow(new Object[]{
                            rscarilab.getString("noorder"),rscarilab.getString("tgl_permintaan"),rscarilab.getString("jam_permintaan"),rscarilab.getString("nm_dokter"),rscarilab.getString("status")
                        }); 
                     }   
                } catch (Exception e) {
                    System.out.println("Notif : "+e);
                } finally {  
                        if(rscarilab!=null){
                        
                            rscarilab.close(); 
                        }
                        
                        if(pscarilab!=null){
                        pscarilab.close();
                    } 
                }  
                  
                pscarilab=koneksi.prepareStatement("se l
                                intaan_labpa.jam_permintaan='00 :00:00','',permintaan_labpa.jam_permin
                                taan) as jam_permintaan,"+ 
                                
                    "if(permintaan_labpa.tgl_hasil='0000-00-00','Belum Terlayani','Sudah Terlayani') as status,"+
                    "dokter.nm_dokter from permintaan_labpa inner join dokter on permintaan_labpa.dokter_perujuk=dokter.kd_dokter "+
                    "where permintaan_labpa.status='ralan' and permintaan_labpa.no_rawat=? order by permintaan_labpa.tgl_permintaan,permintaan_labpa.jam_permintaan desc");
                try {  
                    pscar ilab.setString(1,TNoRw.getText());
                    rs carilab=ps ca rilab .executeQuery();
                    while(rscarilab.next()){
                        tabModeLab.addRow(new Object[]{
                             rsca ri lab.g etString("noorder"),rscarilab.getString("tgl_permintaan"),rscarilab.getString("jam_permintaan"),rscarilab.getString("nm_dokter"),rscarilab.getString("status")
                        });
                    }
                }

                } finally { 
                        
                        if(rscarilab!=null){
                        
                            rscarilab.close(); 
                        }
                        
                        if(pscarilab!=null){
                        pscarilab.close();
                    } 
                }  
                  
                pscarilab=koneksi.prepareStatement("se l
                                intaan_labmb.jam_permintaan='00 :00:00','',permintaan_labmb.jam_permin
                                taan) as jam_permintaan,"+ 
                                
                    "if(permintaan_labmb.tgl_hasil='0000-00-00','Belum Terlayani','Sudah Terlayani') as status,"+
                    "dokter.nm_dokter from permintaan_labmb inner join dokter on permintaan_labmb.dokter_perujuk=dokter.kd_dokter "+
                    "where permintaan_labmb.status='ralan' and permintaan_labmb.no_rawat=? order by permintaan_labmb.tgl_permintaan,permintaan_labmb.jam_permintaan desc");
                try {  
                    pscar ilab.setString(1,TNoRw.getText());
                    rs carilab=ps ca rilab .executeQuery();
                    while(rscarilab.next()){
                        tabModeLab.addRow(new Object[]{
                             rsca ri lab.g etString("noorder"),rscarilab.getString("tgl_permintaan"),rscarilab.getString("jam_permintaan"),rscarilab.getString("nm_dokter"),rscarilab.getString("status")
                        });
                    }
                }

                } finally { 
                        
                        if(rscarilab!=null){
                        
                            rscarilab.close(); 
                        }
                        
                        if(pscarilab!=null){
                        pscarilab.close();
                    } 
                }  
                  
                Valid.tabelKosong(tabModeRad); 
                                ogi=koneksi.prepareStatement("s elect permintaan_radiologi.noorder,per
                                mintaan_radiologi.tgl_permintaan,"+ 
                                
                    "if(permintaan_radiologi.jam_permintaan='00:00:00','',permintaan_radiologi.jam_permintaan) as jam_permintaan,"+
                    "if(permintaan_radiologi.tgl_hasil='0000-00-00','Belum Terlayani','Sudah Terlayani') as status,"+
                    "dokter.nm_dokter from permintaan_radiologi inner join dokter on permintaan_radiologi.dokter_perujuk=dokter.kd_dokter "+
                    "where permintaan_radiologi.s t atus='ralan' and permintaan_radiologi.no_rawat=? order by permintaan_radiologi.tgl_permintaan,permintaan_radiologi.jam_permintaan desc");
                try { 
                    ps cariradiol og i.set String(1,TNoRw.getText());
                    rscariradiologi=pscariradiologi.executeQuery();
                    while(rscariradiologi.next()){
                         tabModeR ad .addR ow(new Object[]{
                            rscariradiologi.getString("noorder"),rscariradiologi.getString("tgl_permintaan"),rscariradiologi.getString("jam_permintaan"),rscariradiologi.getString("nm_dokter"),rscariradiologi.getString("status")
                        });
                 

                    System.out.println("Notif : "+e);
                } finally{  
                         
                                if(rscariradiologi!=null){
                                
                                    rscariradiologi.close();
                                
                                }
                                
                                if(pscariradiologi!=null){
                        pscariradiologi.close();
                    } 
                }  
                  
                Valid.tabelKosong(tabModeApotek); 
                                ng=koneksi.prepareStatement("select r esep_obat.no_resep,resep_obat.tgl_peresepan,
                                resep_obat.jam_peresepan,"+ 
                                
                    " dokter.nm_dokter,if(resep_obat.tgl_perawatan='0000-00-00','Belum Terlayani','Sudah Terlayani') as status "+
                    " from resep_obat inner join dokter on resep_obat.kd_dokter=dokter.kd_dokter "+
                    " where resep_obat.tgl_peresepan<>'0000-00-00' and resep_obat.status='ralan' and resep_obat.no_rawat=? order by resep_obat.tgl_perawatan desc,resep_obat.jam desc");
                try {  
                    psoba tlangsung.setString(1,TNoRw.getText());
                    rs cariobat=psobatl an gsung .executeQuery();
                    while(rscariobat.next()){
                        tabModeApotek.addRow(new Object[]{
                             rscariobat .g etStr ing("no_resep"),rscariobat.getString("tgl_peresepan"),rscariobat.getString("jam_peresepan"),rscariobat.getString("nm_dokter"),rscariobat.getString("status")
                        });
                    }
                }

                } finally{
                    if(rscario b at!=null){
                         
                                    rscariobat.close();
                                
                                } 
                                if(psobatlangsung!=null){
                        psobatlangsung.close();
                    } 
                }  
            } catch (Exce ption e) { 
                System.out.println("Notif : "+e); 
                                 
                                 
                                
        }
    }//GEN-LAST:event_TabRawatMouseClicked

    private void MnPeriksaLabPAActionPerformed(ja v a.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnPeriksaLabPAActionPerformed
        if(TPasien.getTex t().trim().equals("")){
            JOptionPan e.showMessa ge Dialo g(null,"Maaf, Silahkan anda pilih dulu dengan menklik data pada table...!!!");
        }else{     
            if(Sequel.cariRegistrasi(TNoRw.getText())>0){
                JOptio nPane.showMessa ge Dialo g(rootPane,"Data billing sudah terverifikasi ..!!");
            }else{ 
                DlgPeriksaLaboratoriumPA periksalab=new DlgPeriksaLaboratoriumPA(null,false);
                periksalab.setSize(internalFrame1.getWidth(),internalFrame1.getHeight());
                periksalab.setLocationRelativeTo(internalFrame1);
                periksalab.emptTeks();  
                periksalab.setNoRm(TNoRw.getText(),"Ralan");  
                periksalab.isCek();
                 periksalab.setAlwaysOnTop(false);
                periksalab.setVisible(true);
            } 
        }  
    }//GEN-LAST:event_MnPeriksaLabPAActionPerfo rmed
  
            vo id MnCariPeriksaLabPAActionPerformed(ja v a. awt.event.ActionEvent evt) {//GEN-FIRST:event_MnCariPeriksaLabPAActionPerformed
        if(TPasien.getText().trim().equals("")){ 
            J Opti o
                            
            DlgCariPeriksaLabPA periksalab=new DlgCariPeriksa LabPA(null,false);
            periksalab.setSize(internalFrame1.getWidth(),internalFrame1.getHeight());
            periksalab.setLocationRelativeTo(internalFrame1);
            periksalab.SetNoRw(TNoRw.getText());   
                ksalab.setAlwaysOnTop(false);
            periksalab.setVisible(true);
        }
    }//GEN-LAST:event_MnCariPeriksaLabPAActionPerformed

    pri vate void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        if(status.equals("belum")){
            TCari.setText(""); 
             tampilAkunBayar(); 
            tampilAkunBankJateng(); 
             t a
            tampilAkunBankBRI();   
            tampilAkunBankJabar(); 
            tampilAkunBankMandiri();
        }else if(status.equals("sudah")){
            tampilAkunBayarTersimpan();
        }
        isHitung();
         isKembali();
    }//GEN-LAST:event_BtnAllActionPerformed
 
    privat e void BtnAllKeyPressed( java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnAllActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnCari,BtnSimpan);
        }
    }//GEN-LAST:event_BtnAllKeyPressed

    priva te void  BtnAll1ActionPerformed( java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAll1ActionPerformed
        if(status.equals("belum")){
            TCari1.setText("");
            tampilAkunPiutang();
        }else if(status.equals("sudah")){
             tampilAkunPiutangTersimpan();
        }
        isHitung(); 
        is Kembali();   
    }//GEN-LAST:event_BtnAll1ActionPerformed
  
    private void formWindowOpened(java .awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        try {
             if(Valid.daysOld("./cache/akunpiutang.iyem")>30){
                tampilAkunPiutang3();
            } 
  
            if(Valid.daysOld("./cache/akunbayar.iyem")>30){
                tampilAkunBayar3();
             }  

            if(Valid.daysOld("./cache/akunbankjateng.iyem")>30){
                tampilAkunBankJateng();
            }else{
                 tampilAkunBankJateng2();
            }
 
            if(Valid.daysOld("./cache/akunbankpapua.iyem")>30){
                 tampilAkunBankPapua();   
            }else{
                tampilAkunBankPapua2();
            }
    
            if(Valid.daysOld("./cache/akunbankjabar.iyem")>30){
                tampilAkunBankJabar();
            }else{
                 tampilAkunBankJabar2();   
            }
  
            if(Valid.daysOld("./cache/akunbankbri.iyem")>30){
                tampilAkunBankBRI();
            }else{
                 tampilAkunBankBRI2();   
            }
  
            if(Valid.daysOld("./cache/akunbankmandiri.iyem")>30){
                tampilAkunBankMandiri();
            }else{
                 tampilAkunBankMandiri2();   
            }
        } cat ch ( Exception e) {
        }
        
        if(akunbillingralan.getTindakan_Ralan().equals("")){
            ak unbillingralan.SetAkunBillingRalan();   
        }
          
        if(pengaturanbillingralan.getTampilkanPpnObatRalan().equals("Yes")){
            PPN_Keluaran=akunbillingralan.getPPNKeluaran();
        }
            
        if(koneksiDB.CARICEPAT().equals("aktif")){
            T Cari .getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
         

                 } 
                @Override
         

                         BtnCariBayarActionPerformed(null); 
                    }  
         

                 public void changedUpdate(Documen tEvent e) {
                    if(TCari.getText().length()>2){ 
                        BtnCariBayarActionPerformed(null);
                    }
                }    
            });
            TCari1.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @

                public void insertUpdate(DocumentEvent e) {
                    if(TCari1.getText().length()>2){
                         btnCariPiutangActionPer f or med(null);
                    }
                }
                @

                public void removeUpdate(DocumentEvent e) {
                    if(TCari1.getText().length()>2){
                         btnCariPiutangActionPer f or med(null);
                    }
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    if(TCari1.getText().length()>2){ 
                        btnCariPiutangActionPerformed(null);
                    }
                }    
            });
        }
    }//GEN-LAST:e

                
    private void BtnAll1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAll1KeyPressed
        if(evt.getKeyC ode()==KeyEvent.VK_SPACE){   
            BtnAll1ActionPerformed(null);
        }else{
            Valid

                
    }//GEN-LAST:event_BtnAll1KeyPressed
    
    private void MnPeriksaLabMBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnPeriksaLabMBActionPerformed
        if(TPasien.getText().trim().equals("")){
            JOptionPane.showMessageDialog(null,"Maaf, Silahkan anda pilih dulu dengan menklik data pada table...!!!");
        }else{     
            if(Sequel.cariRegistrasi(TNoRw.getText())>0){
                 JOptionPane.showMessageDialog(rootPane,"Data billing sudah terverifikasi ..!!");
            }else{ 
                DlgPeriksaLaboratoriumMB periksalab=new DlgPeriksaL aboratoriumMB(null,false);
                 periksalab. se tSize(internalFram e1.getWidth(),internalFrame1.getHeight());
                periksalab.setLocationRelativeTo(internalFrame1);
                  periksalab.emptTeks();
                periksalab.setNoRm(TNoRw.getT ext(),"Ralan");  
                periksalab.isCek();
                 periksalab.setAlwaysOnTop(false);
                periksalab.setVisible(true);
            } 
        }  
    }//GEN-LAST:event_MnPeriksaLabMBActionPerfo rmed
  
            vo id MnCariPeriksaLabMBActionPerformed(ja v a. awt.event.ActionEvent evt) {//GEN-FIRST:event_MnCariPeriksaLabMBActionPerformed
        if(TPasien.getText().trim().equals("")){ 
            J Opti o
                            
            DlgCariPeriksaLabMB periksalab=new DlgCariPeriksa LabMB(null,false);
            periksalab.setSize(internalFrame1.getWidth(),internalFrame1.getHeight());
            periksalab.setLocationRelativeTo(internalFrame1);
            periksalab.SetNoRw(TNoRw.getText());   
                ksalab.setAlwaysOnTop(false);
            periksalab.setVisible(true);
        }
    }//GEN-LAST:event_MnCariPeriksaLabMBActionPerformed

    pri vate void THakKelasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_THakKelasKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_THakKelasKeyPressed 
  
    private void TNaikKelasKeyPressed(java.awt. event.KeyEvent evt) {//GEN-FIRST:event_TNaikKelasKeyPressed
        / / TO D
            AST:event_TNaikKelasKeyPressed   
 
    private void TNoSEPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TNoSEPActionPerformed
        // TODO add your handling code here:
            AST:event_TNoSEPActionPerformed

    private void TNoSEPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoSEPKeyPressed
         // TODO add your handling code here:
    }//GEN-LAST:event_TNoSEPKeyPressed
 
    private void MnGabungNotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnGabungNotaActionPerformed
         this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        DlgGabungNota gabungnota = new DlgGabungNota(null, false);
        gabungnota.setSize(980, 500); 
        gabungnota.setLocationRelativeTo(internalFrame1);
         gabungnota.setNoRm(TNoRM.getText(), "Ralan");
        gabungnota.tampilGabungNota();
        gabungnota.setVisible(true); 
        this.setCursor(Cursor.getDefaultCursor());
    }// GEN-LAST:event_MnGabungNotaActionPerformed

    private void MnFreeKarcisActionPerformed(java.awt.event.Action Event evt) {//GEN-FIRST:event_MnFreeKarcisActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         Sequel.mengedit("reg_periksa", "no_rawat = '"+TNoRw.getText()+"'", "biaya_reg=0");
        this.setCursor(Cursor.getDefaultCursor());
        BtnAllActionPerformed(null); 
    }//GEN-LAST:event_MnFreeKarcisActionPerformed

 
    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
             DlgBilingRalan dialog = new DlgBilingRalan(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override 
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);    
                }
            });
             dialog.setVisible(true);

    
     // Variables declaration - do not modify
     private widget.Button BtnAll;
    private widget.Button BtnAll1;
    private widget.Button BtnBatal1;
    private widget.Button BtnCari;
    private widget.Button BtnCariBayar;
    private widget.Button BtnCloseIn;
    private widget.Button BtnCloseIn1;
    private widget.Button BtnCloseIn4;
    private widget.Button BtnCloseIn5;
    private widget.Button BtnHapus;
    private widget.Button BtnHapusPotongan;
    private widget.Button BtnKeluar;
    private widget.Button BtnKeluar1;
    private widget.Button BtnKeluarPotongan;
    private widget.Button BtnNota;
    private widget.Button BtnSimpan;
    private widget.Button BtnSimpan1;
    private widget.Button BtnSimpan2;
    private widget.Button BtnSimpan3;
    private widget.Button BtnSimpan4;
    private widget.Button BtnSimpan5;
    private widget.Button BtnSimpanPotongan;
    private widget.Button BtnTambah;
    private widget.Button BtnTambahPotongan;
    private widget.Button BtnView;
    private widget.Tanggal DTPTgl;
    private widget.Label HKLabel;
    private javax.swing.JMenuItem MnCariPeriksaLab;
    private javax.swing.JMenuItem MnCariPeriksaLabMB;
    private javax.swing.JMenuItem MnCariPeriksaLabPA;
    private javax.swing.JMenuItem MnCariRadiologi;
    private javax.swing.JMenuItem MnDokter;
    private javax.swing.JMenuItem MnFreeKarcis;
    private javax.swing.JMenuItem MnGabungNota;
    private javax.swing.JMenuItem MnHapusTagihan;
    private javax.swing.JMenuItem MnInputObat;
    private javax.swing.JMenuItem MnInputTindakan;
    private javax.swing.JMenuItem MnObatLangsung;
    private javax.swing.JMenuItem MnOperasi;
    private javax.swing.JMenuItem MnPemberianObat;
    private javax.swing.JMenuItem MnPenjab;
    private javax.swing.JMenuItem MnPenjualan;
    private javax.swing.JMenuItem MnPeriksaLab;
    private javax.swing.JMenuItem MnPeriksaLabMB;
    private javax.swing.JMenuItem MnPeriksaLabPA;
    private javax.swing.JMenuItem MnPeriksaRadiologi;
    private javax.swing.JMenuItem MnPoli;
    private javax.swing.JMenuItem MnPotongan;
    private javax.swing.JMenuItem MnRawatJalan;
    private javax.swing.JMenuItem MnTambahan;
    private widget.Label NKLabel;
    private widget.Label NoSEPLabel;
    private javax.swing.JPopupMenu PopupBayar;
    private javax.swing.JPopupMenu PopupPiutang;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TCari1;
    private widget.TextBox TDokter;
    public widget.TextBox THakKelas;
    public widget.TextBox TKembali;
    public widget.TextBox TNaikKelas;
    private widget.TextBox TNoRM;
    public widget.TextBox TNoRw;
    public widget.TextBox TNoSEP;
    private widget.TextBox TPasien;
    private javax.swing.JTabbedPane TabRawat;
    private widget.TextBox TagihanPPn;
    private widget.TextBox TotalObat;
    private widget.TextBox TtlSemua;
    private javax.swing.JDialog WindowGantiDokterPoli;
    private javax.swing.JDialog WindowGantiPenjab;
    private javax.swing.JDialog WindowGantiPoli;
    private javax.swing.JDialog WindowObatLangsung;
    private javax.swing.JDialog WindowPotonganBiaya;
    private javax.swing.JDialog WindowTambahanBiaya;
    private widget.Button btnCariDokter;
    private widget.Button btnCariPiutang;
    private widget.Button btnCariPoli;
    private widget.Button btnPenjab;
    private widget.CekBox chkAdministrasi;
    private widget.CekBox chkLaborat;
    private widget.CekBox chkObat;
    private widget.CekBox chkPotongan;
    private widget.CekBox chkRadiologi;
    private widget.CekBox chkSarpras;
    private widget.CekBox chkTambahan;
    private widget.CekBox chkTarifDokter;
    private widget.CekBox chkTarifPrm;
    private widget.InternalFrame internalFrame1;
    private widget.InternalFrame internalFrame2;
    private widget.InternalFrame internalFrame3;
    private widget.InternalFrame internalFrame4;
    private widget.InternalFrame internalFrame5;
    private widget.InternalFrame internalFrame6;
    private widget.InternalFrame internalFrame7;
    private widget.Label jLabel12;
    private widget.Label jLabel13;
    private widget.Label jLabel14;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private widget.Label jLabel3;
    private widget.Label jLabel4;
    private widget.Label jLabel5;
    private widget.Label jLabel6;
    private widget.Label jLabel8;
    private widget.Label jLabel9;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.TextBox kddokter;
    private widget.TextBox kdpenjab;
    private widget.TextBox kdpoli;
    private widget.Label label15;
    private widget.Label label16;
    private widget.TextBox nmpenjab;
    private widget.TextBox nmpoli;
    private widget.TextBox norawat;
    private widget.TextBox norawatpotongan;
    private widget.panelisi panelBayar;
    private widget.panelisi panelGlass1;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelPermintaan;
    private widget.panelisi panelisi1;
    private widget.panelisi panelisi2;
    private javax.swing.JMenuItem ppBersihkan;
    private javax.swing.JMenuItem ppBersihkan1;
    private widget.ScrollPane scrollPane1;
    private widget.ScrollPane scrollPane2;
    private widget.ScrollPane scrollPane3;
    private widget.ScrollPane scrollPane4;
    private widget.ScrollPane scrollPane5;
    private widget.ScrollPane scrollPane6;
    private widget.ScrollPane scrollPane7;
    private widget.ScrollPane scrollPane8;
    private widget.Table tbAkunBayar;
    private widget.Table tbAkunPiutang;
    private widget.Table tbApotek;
    private widget.Table tbBilling;
    private widget.Table tbLab;
    private widget.Table tbPotongan;
    private widget.Table tbRadiologi;
    private widget.Table tbTambahan;
    // End of variables declaration//GEN-END:variables

    public void isRawat() {
        try {    
            pscekbilling=koneksi.prepareStatement(sqlpscekbilling);
	    try{
                pscekbilling.setString(1,TNoRw.getText());
                rscekbilling=pscekbilling.executeQuery();
                if(rscekbilling.next()){
                    i=rscekbilling.getInt(1);
                }
            }catch (Exception e) {
                i=0;
                System.out.println("Notifikasi : "+e);
            } finally{
                if(rscekbilling != null){
                    rscekbilling.close();
                }
             
                    psce k billing.close();
                        }
            } 
                              
            pscari rm=koneksi.prepareSta tement("select reg_periksa.no_rkm_medis from reg_periksa where reg_periksa.no_rawat=?");
            try{  
                pscarirm.setString(1,TNoRw.getText());
                 rscarirm=pscarirm.executeQuery();
                i f (rscarirm.next()){
                    TNoRM.setText(rscarirm.getStri n g(1));
                    n o_rkm_medis=rscarirm.getString(1);
                }  
	    }catch (Exception e) {
                TNoRM.setText("");
                Sy stem.out.println("Noti fikasi : "+e);
            } finally{
                if(rscarirm != null){
             

                if(p s carirm 
                    != null){
                     pscarirm.close();
                } 
            }  
              
            pscaripasien=koneksi.prepareStatement("select pasien.nm_pasien,pasien.jk,pasien.tgl_lahir from pasien where pasien.no_rkm_medis=? ");
            try{  
                p
                        rscaripasien=pscaripasien.executeQuery();
                if(rscaripasien.next()){
                    TPasien.setText(rscaripasien.g e tString(1));
                    n m_pasien=rscaripasien.getString(1);
                     jk=rscaripasien. getString(2);
                    tgl_lahir=rscaripasien.getString(3);
                }
	    }catch (Excep tion e) { 
                TPasien.setText("");
                System.out.println("Notifikasi : "+e);
            }

                    rsca r ipasien.close();
                    
                 }
                if(pscaripasien != null){ 
                    pscaripa s ien.close();
                }  
            }
        } catch (Exception ex )  {
            System.out . println(ex);
        }  
        
                if(i<=0){
             prosesCariReg();    
             if((chkLaborat.isSelected()==true)||( c hkTarifDokter.isSelected()==true)||(chkTarifPrm.isSelected()==true)||(chkRadiologi.isSelected()==true)){
                 tabM odeRwJlDr.addRow(new Object[]{true,"Tindakan",":","",null,null,null,null,"Ralan Dokter"});
             }               
             if(chkTarifDokter.isSelected()==true){prosesCariRwJlDr();prosesCariRwJlDrPr();}
             if(chkTarifPrm.isSelected()==true){prosesCariRwJlPr();}
             if(ch kLaborat.isSelected()= =true){prosesCariPeriksaLab();}
             if(chkRadiologi.isSelected()==true){prosesCariRadiologi();}    
             prosesCariOperasi();
             if(chkSarpras.isSelected()==true){
                if(detailjs>0){
                   tabModeRwJlDr.addRow(new Object[]{true,"","Jasa Sarana dan Prasarana",":",null,null,null,detailjs,"Ralan Dokter"});
         

               i f( c
               tabModeRwJlDr
                prosesCariObat();       
                            
                f(detailbhp>0
                        ){           
             
                }   
                
                
            
            }    
                
            
            if (chkTambahan.isSelected( )= =true )
                {                      
             
                try {   
                
            
                   pstambahan=ko
                    try {   
                         pst a mb a
                         rstambahan=pstambahan.execut e Query ();     
                              
                 
             
                            tabModeR wJ lDr.a ddRow(new Object[]{true,"Tambahan Biaya",":","",null,null,null,null,"Tambahan"});
                        }else{           
                            tabMo
                         }   
                        rstambahan.beforeFirst();         
                             
                 
             
                                       r st ambah a
                        } 
                    } catch (E x ception e) {
                        System.out.println("Notifikasi : "+e);
                    } finally{ 
                        if(rstamba h an != null){
                            rstambahan.close();
                        }    
                        if(pstambahan != null){         
                                     
                             p stambahan.close();
                        }        
                                      
                    }
                } catch (SQLException ex) {
                   System.out .println("Notifikas i
                                 
                                       
                                     
             if(chkPotong
                    {
                    pspotongan=koneksi.prepareStatement(sq l pspotongan);
                    try{ 
                        ps potongan.setString(1 ,TNoRw.getText());
                        rspotongan=pspotongan.executeQuery();
                        rspotongan.last();
                        if (rspotongan.getRow() >0){
                            tabModeRwJlDr.addRow(new Object[]{true,"Potongan Biaya",":","",null,null,null,null,"Potongan"});  
                        }else{
                            tabModeRwJlDr.addRow(new Object[]{false,"Potongan Biaya",":","",null,null,null,null,"Potongan"});  
                        }
                         rspotongan.beforeFirst();  
                 
             
                                       r sp otong a
                        }
                    } catch (E x ception e) {
                         System.out.println("Notifikasi : "+e);
                    } finally{ 
                        if(rspoton g an != null){
                            rspotongan.close();
                        }     
                        if(pspotongan != null){         
                                     
                             p spotongan.close();
                        }         
                                      
                        
                } catch (Exception ex) {
                   System.out .println("Notifikas i
                                 
                                      
                                        
             TCari.setText("");
             TCari1.setText("");
             setHakNaikKelas();  
             tampilAkunBayar2 ();
             tampilAkunPiu tang2(); 
             isHitung(); 
             status="belu
                          
             Valid.SetTgl2(DTPTgl,Sequel.cariIsi("select concat(nota_jalan.tanggal,' ',nota_jalan.jam) from nota_jalan where nota_jalan.no_rawat='"+TNoRw.getText()+"'"));
             Valid.tabelK
                                 
                psbilling=koneksi.prepar
                     {  
                 
             
                   while(rsbil
                       if(!rsbi
                           tab
                               
                                 
                       
                             
                           
                                        rsbilling
                    .getObject("empat"),
                               
                                       rsbill
                 
                              });  
                        }
                    }  
                } catch (Exce p tion e) {
                    Syste m.out.println("Not ifikasi : "+e);
                } finally{  
                    if(rsbilling != null){   
                                    );
                                    
                                    ){
                                    );
                                    
                                    
                                    
                                    
                TCari1.setText(
                        ikKelas();
                tampi
                tampilAkunPiutangTersimpan();
                isHitung();   
                status="s udah";
            }catch(Exc eption e){ 
                System.out.println("Notifikasi : "+e);
            }        
                      
         isKembali();
    }
                

                riReg();    
        if((chkLaborat.isSelected()
                odeRwJlDr.addRow(new Object[]{true,"Tindakan",":","",null,null,null,null,"Ralan Dokter"});
        }
        if(chkTarifDokter.isSelected()==true){prosesCariRwJlDr();prosesCariRwJlDrPr();}
        if(chkTarifPrm.isSe
                riOper a si();
        if(ch kLabo rat.isSelecte d()==true){prosesCariPeriksaLab();}
        if(chkRadiologi.isSelected()==true){proses C ariRadiologi();}             
        if(ch
         
              tabModeRwJlDr.addRow(new Object[]{true,"","Jasa Sarana dan Prasarana",":",null,null,null,detailjs,"Ralan Dokter"});
     

        if(chkObat.isSelected()==true){
            tabModeRwJlD
             prosesCariObat();       
                        
            if(detailbhp>0){           
                tabModeRwJlDr.addRow(new Object[]{true,"","Paket Obat/BHP",":",null,null,null,detailbhp,"Ralan Dokter"});
             }   
            
            
        
        }    
            
        
        if(chkTambahan.isSelected()==true){                           
             try {   
            
        
                 pstambahan=koneksi.p re pareS t
            atement(sqlpstambahan)
        ;
                 try {   
                     pst a mb a
                     rstambahan=pstambahan.execut e Query ();     
                          
             
         
                         tabMode Rw JlDr. addRow(new Object[]{true,"Tambahan Biaya",":","",null,null,null,null,"Tambahan"});
                    }else{           
                        tabMo
                     }   
                    rstambahan.before
                        First();           
                    while(rstambahan.next()){                    
                        tabModeRwJlDr.addRow(new Object[]{true,"",rstambahan.getString("nama_biaya"),":",
                                     rs tamba h
                    } 
                } catch (E x ception e) {
                    System.out.println("Notifikasi : "+e);
                } finally{ 
                    if(rstamba h an != null){
                        rstambahan.close();
                    }    
                    if(pstambahan != null){
                                           
                         p stambahan.close();
                    }
                                           
                }
            } catch (SQLException ex) {
               System.out .println("Notifikas i
                             
                                   
                                 
        if(chkPotonga
                {
                pspotongan=koneksi.prepareStatement(sq l pspotongan);
                try{ 
                    ps potongan.setString(1 ,TNoRw.getText());
                    rspotongan=pspotongan.executeQuery();
                    rspotongan.last();
                    if (rspotongan.getRow() >0){
                        tabModeRwJlDr.addRow(new Object[]{true,"Potongan Biaya",":","",null,null,null,null,"Potongan"});  
                    }else{
                        tabModeRwJlDr.addRow(new Object[]{false,"Potongan Biaya",":","",null,null,null,null,"Potongan"});  
                    }
                     rspotongan.beforeFirst();  
                    while(rspotongan.next()){                    
                        tabModeRwJlDr.addRow(new Object[]{true,"",rspotongan.getString("nama_pengurangan"),":",
                                     rs poton g
                    }
                } catch (E x ception e) {
                     System.out.println("Notifikasi : "+e);
                } finally{ 
                    if(rspoton g an != null){
                        rspotongan.close();
                    }     
                    if(pspotongan != null){
                                           
                         p spotongan.close();
                    } 
                                           
                    
            } catch (SQLException ex) {
               System.out .println("Notifikas i
                             
                                  
                                    
         
        isHitung();          
        isKembali();  
    } 
  
    private void prosesCariReg() {        
        Valid.tabelKo
                      
            psreg=koneksi.prepareStatement(sqlpsreg);
            try{
                psreg.setString(1,TNoRw.getText());
                rsreg=psreg.executeQuer
                 if(rsreg.next()){  
                    tabModeRwJlDr.addRow(new Object[]{true,"No.Nota",": "+Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(nota_jalan.no_nota,4),signed)),0) from nota_jalan where nota_jalan.tanggal='"+Valid.SetTgl(DTPTgl.getSelectedItem()+"").substring(0,10)+"' ",Valid.SetTgl(DTPTgl.getSelectedItem()+"").substring(0,10).replaceAll("-","/")+"/RJ",4),"",null,null,null,null,"-"});                
         

                   
                        rscaripoli=pscaripoli.executeQuery();
                        if(rscaripoli.next()){
                            polirujukan="";
                            pscari
                            try {
             
                                  rscaripoli2=pscaripoli2.executeQuery();
                                 while(rscaripoli2.next()){
                                     polirujukan=polirujukan+", "+rscaripoli2.getString("nm_poli");
                                  }
                             } ca tch (Exception e) {
                                System.out.println("P o li :  "+e);   
                            
                                          
                                  
                                   
                            }  f inally{
                                 if(rscaripoli2!=null){
                                    rscaripoli2 .close();
                                }  
                                 if(pscaripoli 2!=null){
                                    psc a ripoli2.close();
                                }  
                            }
                            tabModeRwJlDr.addRow(new Obj ect[]{true,"Unit/Instansi",": "+rscaripoli.getString(1)+polirujukan,"",null,null,null,null,"-"});
                        }  
                    }catch (Exception  e) { 
                        tabModeRwJlDr.addRow(ne w  Object[]{t r ue," U nit/Instansi",": ","",null,null,null,null,"-"});
                        System.out.println("Notifikasi : "+e);
                    } finally{
                        if(rscaripoli != null){  
                            rscaripol i.close();
                        }    
                        if(pscaripoli != null){
                            pscaripoli.close();
                        }    
                    }
                         
                    umurdaftar=rsreg.getString("umurdaftar")+rsreg.getString("sttsumur");
                    String jk = Sequel.cariIsi("SELECT jk FRO M  pasi en ps WHERE ps.n
                                    o_rk m _medis =?",TNoRM.getTex t ());       
                    tgl_registrasi=rsreg.getString("tgl_registrasi");
                    t abModeRwJlDr.addRow(new Object[]{true,"Tanggal & Jam",": "+rsreg.getString("tgl_registrasi")+" "+rsreg.getString("jam"),"",null,null,null,null,"-"});
                    tabModeRwJlDr.add
                                Row(new Object[]{tru e ,"No. RM",": "+TNoRM.g etTex t() ,"",n ull,n ull,n ull,n ull ,"-"});
                    tabModeRwJlDr.addRow(new Object[]{true , "Nama Pasien",": "+TPasien.getText()+" ("+jk+") ("+rsreg.getString("umurdaftar")+rsreg.getString("sttsumur")+")","",null,null,null,null,"-"});
                    pscariala mat=koneksi.prepareStatement(sqlpscarialamat); 
                    try{  
                        pscarialamat.setString(1,TNoRM.getText());
                        rscarialamat=pscarialamat.executeQuery();
                        if (rscarialamat.next() ){
                            tabModeRwJlDr.addRow(new Object[]{true,"Alamat Pasien",": "+rscarialamat.getString(1),"",null,null,null,null,"-"});
                            alamat=rscarialamat.getString(1);
                     

                        tabMod e RwJlDr.addRow(new Object[]{tr u e,"Alamat Pasien",": ","",null,null,null,null,"-"});
                        alamat=""; 
                        System.out . println("Notifikasi : "+e);
                    } finally{   
                                     
                               
                        if(rscarialamat !
                            = null){             
                            rscarialamat.close(); 
                                      
                                         
                                  
                        }  
                         if(pscarialamat != null){
                            pscarialamat.close(); 
                        }  
                    }  
                    //cari dokter yang menangani        
                                          
  
                    psdokterralan=koneksi.prepareStatement(sqlpsdokterralan);
                    t ry{
                        psdokterralan
                                .setString(1,TNoRw.g e tText ());        
                        rsdokt e rralan=psdokterralan.executeQuery();
                        if(pengaturanbillingralan.getCenta n gDokterRalan().equals("Yes")){
                            i f(rsdokterralan.next()){
                                 tabModeRwJlDr.ad dRow(new Object[]{true,"Dokter ",":","",null,null,null,null,"-"});  
                            }
                            rsdokterralan.beforeFirst();
                             if(rsdokterralan.nex t()){
                                dokterrujukan="";
                                psdokterralan2=koneksi.prepareStatement(sqlpsdokterralan2);
                                try {
                                     psdokterrala.setString(1,TNoRw.getText());
                                    rsdokterralan2=psdokterralan2.executeQuery();
                                      while(rsdokterralan2.next()){
                                         dokterrujukan=dokterrujukan+", "+rsdokterralan2.getString("nm_dokter");
                                    } 
                                } cat c h (Exception e) {
                                     System.out.println("Dokter : "+e); 
                                 } finally{ 
                                    if(rsdokt
                                        erralan2!=null){           
                                        rsdokterralan2.close();
                                    }
                                     if(psdokterralan 2!=null){
                                        psdok t erralan2.close();
                                    }  
                                }
                                tabModeRwJlDr.addRow(new Object []{true,"",rsdokterralan.getString("nm_dokter")+dokterrujukan,"",null,null,null,null,"Dokter"});   
                            }  
                        }else{  
                            if(rsdokterralan.next()){      
                                tabModeRwJlDr.addRow(new Object[]{false,"Dokter ",":","",null,null,null,null,"-"});  
                            }
                            rsdokterralan.beforeFirst();  
                            if(rsdokterra lan.next()){
                                dokter rujukan="";   
                                psdokterralan2=koneksi.prepareStatement(sqlpsdokterralan2);
                                try {
                                    ps dokterralan2.se tS tring (1,TNoRw.getText());
                                    rsdokterralan2=psdokterralan2.executeQuery();
                                    while(rsdokterralan2.next()){
                                        dokterrujukan=dokterrujukan+", "+rsdokterralan2.getString("nm_dokter");
                                    }
                                              
                                                      
                                } catch (Exception e) {
                                      System.out.println("Dokter : "+e);
                                 } finally{ 
                                    if(rsdokterralan2
                                        !=null){           
                                        rsdokterralan2.close();
                                    }
                                     if(psdokterralan 2!=null){
                                        psdok t erralan2.close();
                                    }  
                                }
                                tabModeRwJlDr.addRow(new Object []{false,"",rsdokterralan.getString("nm_dokter")+dokterrujukan,"",null,null,null,null,"Dokter"});   
                            }  
                        }     
                    } catch (Exception e) {      
                        System.out.println("Notifikasi : "+e);
                    } finally{
                        if(rsdokterralan != null){  
                            rsdokterralan .close();
                        }    
                        if(psdokterralan != null){
                            psdokterralan.close();
                        }    
                    }
                    

                    if(chkAdministrasi.isSelected()==
                                        true){      
                                                      
                            odeRwJlDr.addRow(new Object[]{true,"Registrasi",":","",null,null,null,rsreg.getDouble("biaya_reg"),"Registrasi"});
                    }
                        
	    }catch (Exception e) {  
                System.out.pr intln("Notifikasi : "+e);
            } finally{  
                if(rsreg != null){
                    rsreg.close();
                }  
                if(psreg != null){
                    psreg.close();
                }

                    ut .println("Notifikasi : "+e);   
        }        
                                  
        
    }
             
    private void prosesCariRwJlDr() {  
        try{   
            pscari ralandokter=kon eksi.prepareStatement(sqlpscariralandokter);
            try {
                pscariralandokter.setString(1,TNoRw.getText());
                rs cariralandokter =pscariralandokter.executeQuery();
                subttl=0;
                detailbhp=0;
                detailjs=0;
                  while(rscar iralandokter.next()){
                    tamkur=0;  
         

                        pstamkur.setString(1,TNoRw.getText());
                        pstamkur.setString(2,rscariralandokter.getString("nm_perawatan"));
                        pstamkur.setString(3,"Ralan Dokter");
             
                        if(rs t amkur.next()){
                            tamkur=rstamkur.getDouble(1);
                        } 
                    } catch (Exce p tion e) {
                          System.out.println("Notifikasi : "+e);
                    } fin a lly{
                         i f(rstamkur != null){
                             rstamkur.close(); 
                        }   
                        if(p s tamkur != null){
                            pstamkur.close();
                        }  
                    } 
                    if(pengaturanbillingralan .getRincianDokterRalan().equals("Yes")){
                        detailbh p =detailbhp+rscariralandokter.getDouble("totalbhp");
                        de tailjs=detailjs+r scariralandokter.getDouble("totalmaterial");
                        tabModeRwJ l Dr.addRow(new Object[]{true,"",rscariralandokter.getString("nm_perawatan"),":",
                                       rscariralandokter.getDouble("tarif_tindakandr"),rscariralandokter.getDouble("jml"),tamkur,(rscariralandokter.getDouble("totaltarif_tindakandr")+tamkur),"Ralan Dokter", rscariralandokter.getString("nama_dokter")});
                        subttl=subttl+rscariralandokter.getDouble("totaltarif_tindakandr")+tamkur; 
                    }else{  
                        tabMo deRwJlDr.addRow(new Object[]{true,"",rscariralandokter.getString("nm_perawatan"),":",
                                        rscar iralandokter.getDouble("total_byrdr"),rscariralandokter.getDouble("jml"),tamkur,(rscariralandokter.getDouble("biaya")+tamkur),"Ralan Dokter",rscariralandokter.getString("nama_dokter")});
                        subttl=subttl+rscariralandokter.getDouble("biaya")+tamkur;
                    }    
                          
            } catch (Exception e) {
                System.ou
                    y{
                if(rsc ariralandokter != null){ 
                    rscariralando k ter.close ( );
                }    
                if(pscariralandokter != null){     
                                lose(); 
                                    
                                 
                }      
                      
        }catch(Exception e){     
                                asi : "+e);  
                                   
                                 
        }      
    }
                
    private void prosesCariRwJlDrPr() {
        try{              
            pscariral andrpr=koneksi.prepareStatement(sqlpscariralandrpr);
            try {  
                pscariralandrpr.setString(1,TNoRw.getText());
                rscariralandrpr=pscariralandrpr.executeQuery();
                su bttl=0; 
                while(rscariralandrpr.next()){
                    tamkur=0;
                    pstamkur=koneksi.prepareStatement(sqlpstamkur);
                      try{ 
                        pstamkur.setString(1,T N oRw.getText());
                        pstamkur.setString(2,rscariralandrpr.getString("nm_perawatan"));
     

                        if(rstamkur.next()){
             
                        }  
                    }catch (Exception e) {
                        System.out.println(" Notifikasi : "+e);
                    } finally{  
                          if(rstamkur != null){
                             rstamkur.close(); 
                        }   
                        if(p s tamkur != null){
                             pstamkur.close();
                        }  
                    } 
                         
                    if(pengatura n billingralan.getRincianDokterRalan().equals("Yes")){
                        de tailbhp=detailbhp +rscariralandrpr.getDouble("totalbhp");
                        detailjs=d e tailjs+rscariralandrpr.getDouble("totalmaterial")+rscariralandrpr.getDouble("totaltarif_tindakanpr");
                        tabModeRwJlDr.addRow(new Object[]{true,"",rscariralandrpr.getString("nm_perawatan"),":",
                                        rscariralandrpr.getDouble("tarif_tindakandr"),rscariralandrpr.getDouble("jml"),tamkur,(rscariralandrpr.getDouble("totaltarif_tindakandr")+tamkur),"Ralan Dokter Paramedis"});
                        subttl=subttl+rscariralandrpr.getD o uble("totaltarif_tindakandr")+tamkur; 
                    }else{ 
                        ta bModeRwJlDr.addRow (new Object[]{true,"",rscariralandrpr.getString("nm_perawatan"),":",
                                       rscariralandrpr.getDouble("total_byrdrpr"),rscariralandrpr.getDouble("jml"),tamkur,(rscariralandrpr.getDouble("biaya")+tamkur),"Ralan Dokter Paramedis"});
                        s
                          

                }
                    (

                    y{  
                if(rscariralandrp r !=null){  
                    rscariraland r pr.close ( );
                                 
                }     
                                l){  
                                  
                                 
                    pscarirala n drpr.c l ose();  
                      
            }     
                                  
                                    
        }catch(Exception e){      
            System.out.println("Notifikasi : "+e);
        }
    }

    private void prosesCariRwJlPr() {  
                     
            pscari ralanperawat=kon ek si.pr epareStatement(sqlpscariralanperawat);
            try {
                pscariralanperawat.setString(1,TNoRw.getText());
                rs cariralanperawat =p scari ralanperawat.executeQuery();
                subttl=0;
                while(rscariralanperawat.next()){
                    tamkur=0;
                     pstamkur=koneksi.prepareStatement(sqlpstamkur);
                      try{ 
                        pstamkur.setString(1,T N oRw.getText());
                        pstamkur.setString(2,rscariralanperawat.getString("nm_perawatan"));
                        pstamkur.setString(3,"Ralan Paramedis");
                        rstamkur=pstamkur.executeQuery();
                        if(rstamkur.next()){
             
                        }  
                    } catch (Exception e) {
                        System.out.println("Not ifikasi : "+e);
                    } finally{  
                          if(rstamkur != null){
                             rstamkur.close(); 
                        }   
                        if(p s tamkur != null){
                             pstamkur.close();
                        }  
                    } 
                         
                    tabModeRwJlD r .addRow(new Object[]{true,"",rscariralanperawat.getString("nm_perawatan"),":",
                                    rscarira lanperawat.getDouble("total_byrpr"),rscariralanperawat.getDouble("jml"),tamkur,(rscariralanperawat.getDouble("biaya")+tamkur),"Ralan Paramedis"});
                    subttl=subttl+ r scariralanperawat.getDouble("biaya")+tamkur;
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);   
            } finally{ 
                if(rscarir alanperawat!=null) {
                    rscariralanperawat.close();
                }
                        ir alanperawat!=null) {
                    pscariralanperawat.close();
                }
                    

                    ut.println("Notifikasi : "+e);     
                              
                                
    }      
    
    private void prosesCariPeriksaLab() {
        try{  
            pscarilab =koneksi.prepareStatement(sqlpscarilab);
            try {    
                pscarilab.setString(1,TNoRw.getText());
                rscarilab=pscarilab.executeQuery();
                su bttl=0;   
                while(rscarilab.next()){
                    psdetaillab=koneksi.prepareStatement(sqlpsdetaillab);
                    try {
                          psdeta illab.setString(1,TNoRw.getText());
                        psdetaillab.setString( 2 ,rscarilab.getString("kd_jenis_prw"));
                        rsdetaillab=psdetaillab.executeQuery();
     

                            ralanparamedis=rsdetaillab.getDouble("total");               
                         }
                    }   catch (Exception e) {
                        ralanparamedis=0;
                        System.out.pri ntln("Notifikasi : "+e); 
                    } fin a lly{
                          if(rsdetaillab!=null){
                             rsdetaillab .close();
                        }  
                        if(psdetaillab!=null){
                            psdetaillab.close(); 
                        } 
                    }  
                    tabModeRwJlDr.addR o w(new Object[]{true,"",rscarilab.getString("nm_perawatan"),":",
                                    rscarilab.getD o
                            ubttl+rscarila b .getDouble("total")+ralanparame
                        
            } catch (Exception e) {
                System.out.println("No t ifikasi : "+e); 
            } finally{  
                    scarilab! =null){
                    rscari lab.close();   
                }
                if(pscarilab!=null){
                    pscari lab.close();   
                }
            }
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);     
                              
                                
    }      
    
    private void prosesCariRadiologi() {
        try{  
            pscarirad iologi=koneksi.prepareStatement(sqlpscariradiologi);
            try {    
                pscariradiologi.setString(1,TNoRw.getText());
                rscariradiologi=pscariradiologi.executeQuery();
                su bttl=0;   
                while(rscariradiologi.next()){
                    tamkur=0;
                    pstamkur=koneksi.prepareStatement(sqlpstamkur);
                      try{ 
                        pstamkur.setString(1,T N oRw.getText());
                        pstamkur.setString(2,rscariradiologi.getString("nm_perawatan"));
     

                        if(rstamkur.next()){
                             tamkur=rstamkur.getDouble(1);
                        }  
                    }catch (Exception e) {
                        System.out.println(" Notifikasi : "+e);
                    } finally{  
                          if(rstamkur != null){
                             rstamkur.close(); 
                        }   
                        if(p s tamkur != null){
                             pstamkur.close();
                        }  
                    } 
                         
                    tabModeRwJlD r .addRow(new Object[]{true,"",rscariradiologi.getString("nm_perawatan"),":",
                                    rscarira diologi.getDouble("biaya"),rscariradiologi.getDouble("jml"),tamkur,(rscariradiologi.getDouble("total")+tamkur),"Radiologi"});
                    subttl=subttl+ r scariradiologi.getDouble("total")+tamkur;
                }
            } catch ( Exception e) {
                System.out.println("Notifikasi : "+e);   
            } finally{ 
                if(rscarir adiologi!=null){ 
                    rscariradiologi.close();
                }
                        ir adiologi!=null){ 
                    pscariradiologi.close();
                }
                     

                    ut.println("Notifikasi : "+e);     
                              
                                
    }      

    private void prosesCariObat() { 
        subttl=0;  
            langsung= 0;
        try{         
            psobatlangsung=koneksi.prepareStatement(sqlpsobatlangsung);
            try {
                ps obatlangsung.set St ring( 1,TNoRw.getText());
                rsobatlangsung=psobatlangsung.executeQuery();
                if(rsobatlangsung.next()){
             
                      obatlan gsung=rsobatlangsung.getDouble("besar_tagihan");
                }  
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e); 
            } finally{
                if(rsobatlangsung!=
                      rsobatlangsung.close();
                }  
             
                    psobat l angsung.close();
                }
            } 
        }catch(Exception e){  
            System .out.println("Notifikas i : "+e);
        }
                                   
                                       
          
        try{     
            psobatoperasi=koneksi.prepareStatement(sqlpsobatoperasi);
            try{  
                psoba toperasi.setString(1,TNoRw.getText());
                rs obatoperasi=pso ba toper asi.executeQuery();
                if(pengaturanbillingralan.getCentangObatRalan().equals("Yes")){
                    while(rsobatoperasi.next()){
                         tabModeRw Jl Dr.ad dRow(new Object[]{true,"                           ",rsobatoperasi.getString("nm_obat"),":",
                                       rsobatoperasi.getDouble("hargasatuan"),rsobatoperasi.getDouble("jumlah"),0,
                                       rsobatoperasi.getDouble("total"),"Obat"});
                    }
                  }else{ 
                    while(rsobatoperasi.next() ) {
         

             
                    }  
                 }                    
            } catch (Exception e) { 
                System.out.pr i ntln("Notifikasi : "+e); 
            } fina lly{ 
                if(rsobat operasi!=null){ 
                    rsobatoperasi.close();   
                                 
                                  
                                {  
                    psobatoperasi.close();
                }  
            }  
            //rs.close();   
                                 
                                  
                                asi : "+e);  
        }
        
                  
            pscariobat=koneksi.prepareStatement(sq l psc
            try { 
                ps cariobat.setSt ri ng(1, TNoRw.getText());
                rscariobat=pscariobat.executeQuery();
                //embalase=0;
                if (pengaturanbil li ngral an.getCentangObatRalan().equals("Yes")){
                    while(rscariobat.next()){
                        tabModeRwJlDr.addRow(new Object[]{true,"",rscariobat.getString("nama_brng")+" ("+rscariobat.getString("nama")+")",":",
                                       rscariobat.getDouble("biaya_obat"),rscariobat.getDouble("jml"),rscariobat.getDouble("tambahan"),
                                        (rscariobat.getDouble("total")+rscariobat.getDouble("tambahan")),"Obat"});
                          sub ttl=subttl+rscariobat.getDouble("total")+rscariobat.getDouble("tambahan");
                    }  
         

             
                                         rscariobat.getDouble("biaya_obat"),rscariobat.getDouble("jml"),rscariobat.getDouble("tambahan"),
                                       (rscariobat.getDouble("total")+rscariobat.getDouble("tambahan")),"Obat"});
                        subttl=subttl+r scariobat.getDouble("total")+rscariobat.getDouble("tambahan");
                    }  
                }                     
            } catc h (Exception e) { 
                System.ou t.println("Notifika si : "+e); 
            } finally{   
                                       
                                 
                                
                                    
                }      
                if(pscariobat!=null){
                     p scariobat.close();
                }  
            }               
                                       
                                 
                                
                                asi : "+e);    
        }           
        
        ppnobat=0
            ubttl>0){ 
            if(pengaturanbillingralan.getTampilkan P pnO
                ppnob at=Math.round(subttl*0.11);
                if (pengaturan bi lling ralan.getCentangObatRalan().equals("Yes")){
                    tabModeRwJlDr.addRow(new Object[]{true,"","PPN Obat",":",ppnobat,1,0,ppnobat,"Obat"});
                }else{
                     tabModeRw Jl Dr.ad dRow(new Object[]{false,"","PPN Obat",":",ppnobat,1,0,ppnobat,"Obat"});
                }
                tabModeRwJlDr.addRow(new Object[]{true,"",""+Valid.SetAngka3(subttl+ppnobat),"",null,null,null,null,"TtlObat"});            
            }
                  tabModeRwJl Dr.addRow(new Object[]{true,"",""+Valid.SetAngka3(subttl),"",null,null,null,null,"TtlObat"});            
            }                  
        }

          
    
            vo id isHitung() {    
        ttl=0;    
        y=0;  
        ttlLaborat=0;ttlRadiologi=0;ttlObat=0;ttlRala n _Dokt er= 0;ttlRalan_ Para medis=0; tt lT ambahan= 0; 
        ttlPotong an=0 ;ttlRegistrasi=0;ttlRalan_Dokter_Param=0;ttlOperasi=0;
        int a=tbBilling.getRowCount();           
        for(r=0;r<a;r++){ 
            try {                           
                           
                y= Double.parseDouble(tabModeRwJlDr.getValueAt(r,7).toString());  
            } catch (Exception e) {
                                     
             
            }
     

                        break
                  case "Radiologi":
                          ttlRadiologi=ttlRadiologi+y;
                       
           break;  
          
          
          
          
                cas e  "
        Obat":  
          
          
                          ttlObat=ttlObat+y;
                                b
                c
                           ttlRalan_Dokter=ttlRalan_Dokter+y; 
                        break;     
                c a se
                        ttlRalan_Dokter_Param=ttlRalan_Dokter_Param+y;
                        break;     
                case "Ralan Par
                    ttlRalan_P a ramedis=tt l Ra
                    break;
                case "Tambahan":
                    ttlTambahan= t tlTambahan+y ; 
                    break;
                case "Potong
                    ttlPoto n gan=ttl P ot
                    break;
                case "Registrasi":
                    ttlRegistrasi=t t lRegistrasi+y;  
                    break;
                case "Operasi":
                    ttlOperasi=ttlOperasi + y;  
                    break;
                                       
                                   
                    
        TtlSemua.setText(Valid.S
                        
                    
    
                        
                    
        Valid.tabelKosong(tabModeA
                    g(tabModeAkun P iutang);  
                    ew Date());
        BtnNota.setEnabled(akse
                    bled(akses . getbilling _ ra
                    ed(akses.getbilling_ralan());
        MnRaw
            put T ind a ka
        MnPemberianObat.setEnabled(akses.getberi_obat());
        MnInputObat.setEnabled(akses.getberi_obat());
     

        MnPeriksaLab.se tEnabled(akses.getperiksa_lab());
        MnCariPeriksaLab.setEnabled(akses.getperiksa_lab());
        MnPeriksaLabPA.setEnabled(akses.getpemeriksaan_lab_pa());
        MnCariPeriksaLabPA.setEnabled(akses.getpemeriksaan_lab_pa());
        MnPeriksaLabMB.setEnabled(akses.getpemeriksaan_lab_mb());
        MnCariPeriksaLabMB.setEnabled(akses.getpemeriksaan_lab_mb());
        MnPeriksaRadiologi.setEnabled(akses.getperiksa_radiologi());
        MnCariRadiologi.setEnabled(akses.getperiksa_radiologi());
        MnPenjualan.setEnabled(akses.getpenjualan_obat());        
        MnHapusTagihan.setEnabled(akses.gethapus_nota_salah());  
        MnPoli.setEnabled(akses.getregistrasi());
        MnDokter.setEnabled(akses.getregistrasi());
        MnPenjab.setEnabled(akses.getregistrasi());
        
        if(pengaturanbillingralan.getTampilkanTombolNotaRalan().equals("Yes")){
            BtnNota.setVisible(true);
        }else{
            if(akses.getkode().equals("Admin Utama")){
                BtnNota.setVisible(true);
            }else{
                BtnNota.setVisible(false);
            }            
        }
    } 
        
        ic void isKembali(){
        bayar=0;total=0;besarppn=0;tagihanppn=0;y=0;piutang=0;kekurangan=0;countbayar=0;
        

        fo r(r=0;r<row2;r++){  
            if(!tabModeAkunBayar.getValueAt(r,2).toString().equals("")){
                  countbayar++;
                 try { 
                    bayar=bayar+Double.parseDouble(tabModeAkunBayar.getValueAt(r,2).toString()); 
                 }  catch (Exception e) {
                    bayar=bayar+0;
             
            }  
     

                try { 
                  
            b e sa
        rppn=bes a rp
        pn+Valid.r o un
        d U p(
        Double. p ar
        seDouble(t a bM
        odeAkunBay a r.

                      besarppn=besarppn+0;
                    }               
            }      
        }
        
        row2=tabModeAkunP i utang . getRowCount(); 
                r<row2;r++){ 
            if(!tabModeAk u nPiut a ng.getValueAt(r,3).toString().equals("")){
                t
             

                     piutang=piutang+0;  
                }               
            }               
                               
                
            
        if(ttl>0)
            t
        }

          
        tag ih a np p n =besa rppn +
            ha nPPn.setText(Valid.SetAngka3(tagih anppn)); 
        
        if(piutang<=0){     
                rangan=(bayar+besarppn)-tagihanppn;
            jLabel5.setText ( "Bayar  :  Rp.");
            if(ke
             
         

             }  
                   
         

            jLabel 5 .setText ( "Uang Muka : Rp.");
            if(kekurangan>0){

             }else{   
                jLabel 6 .setTe x t("Kekura n gan : Rp.");
            }
                    
            TKembali.setText(Valid.SetAngka3(kekurangan));  
        }    
    }
    

            oid tampilTambahan(String NoRawat) {
        n oraw at.setText(NoRawat);
        Valid.tabelKos o ng(tabModeT a mbahan ) ;   
        try{     
            ps tambahan=ko n ek si.prepareStatement(sqlpstambahan);
            try{
                 p stambahan.setString(1,norawat.getText());
                rstambahan=pstambahan.executeQuery();
             

                } 
         
     

                    rstambahan.close();
                } 
                if(pstambahan != null){
             
                }   
            }       
        }catch(Exception e){ 
            System.out.pri n tln("Notifikasi : "+e);
        }              
    }      
    
            v oid tampilPotongan(String NoRawat) {
             norawatpotongan.setText(NoRawat);  
             Valid.ta belKosong(tabModePotongan);
             try{             
                 pspotongan=koneksi.prepareStatement(sqlpspotongan);
                 
                      pspotongan.setStr ing(1,TNoRw.getText());
                     rspotongan=pspotongan.executeQuery();
                 
             
                       }  
                 }catch (Exception e) {  
         
     

                         rspotongan.close();
                } 
                if(pspotongan != null){
             
                }   
            } 
                 
                Exception  e x) {
                m.out .println("Notifikas i
                        
                
            {            
                tongan=koneksi.prepareStatement("S E LEC
               "    C O
                     t.biaya_rawat\n" + 
                     (\n" +
                 
                     UNION ALL\n" + 
                    SELECT no_rawat, kd
                )
             

                "WHERE t.no_rawa
                "AND NOT EXISTS (\n" +  
         

             
               "    AN D  pb.nama_pengurangan = CONCAT(t.kd_jenis_prw,'#', jp.nm_perawatan)\n" +
                    ");");
                 try{
                     pspotongan.setString(1,TNoRw.getText());
                     rspotongan=pspotongan.executeQuery();
                     while(rspotongan.next()){                    
                         tabModePotongan.addRow(new Object[]{false, rspotongan.getString(1),rspotongan.getString(2)});
                     } 
                 }catch (Exception e) {
                     System.out.println("Notifikasi : "+e);
                 } finally{
                     if(rspotongan != null){
                         rspotongan.close();
                     } 
                     if(pspotongan != null){
                         pspotongan.close();
                     } 
            } 
                 
                Exception  e x) {
                m.out .println("Notifikas i
                        
                
             
                esCariOperasi(){  
                    
                  
                    eksi.prepareStateme
                
                ra si.setString(1,TNoRw .
                    =psoperasi.executeQ
                o
             

            rsoperasi.beforeFirs
            if(pengaturanbillingralan.getRinci a nOpe
               while(rsoperasi.next()){                        
     

                           tabModeRw JlDr.addRow(new Object[]{true,"                           ","  Biaya Operator 1",":",rsoperasi.getDouble("biayaoperator1"),1,0,rsoperasi.getDouble("biayaoperator1"),"Operasi"}); 
             
              
                          if(rsoperasi.getDouble("biayaoperator2")>0){
                           tabModeRwJlDr.addRow(new Object[]{true,"                           ","  Biaya Operator 2",":",rsoperasi.getDouble("biayaoperator2"),1,0,rsoperasi.getDouble("biayaoperator2"),"Operasi"}); 
                        } 
  
                         if(rsoperasi .
                        tabModeRwJlDr.addRow(new Obje c t[]{t rue,"                                 ","   Biaya Ope rat
                        }

                         if(rsoperasi.getDouble("biayaasisten_operator1")>0){ 
                            tabModeRwJlDr.ad d
                        }   
                                       
    
                            rsoperasi.getDouble("biayaasisten _ opera tor2")>0){
                                        
                                      
                           tabModeRwJlDr.addRow(new Object[]{true,"                           ","  Biaya Asisten Operator 2",":",rsoperasi.getDouble("biayaasisten_operator2"),1,0,rsoperasi.getDouble("biayaasisten_operator2"),"Operasi"}); 
                        }
                            
                            rsoperasi.getDouble("biayaasisten _ opera tor3")>0){
                                        
                                      
                           tabModeRwJlDr.addRow(new Object[]{true,"                           ","  Biaya Asisten Operator 3",":",rsoperasi.getDouble("biayaasisten_operator3"),1,0,rsoperasi.getDouble("biayaasisten_operator3"),"Operasi"}); 
                        }
    
                            rsoperasi.getDouble("biayainstrum e n")>0 ){
                                        
                                      
                           tabModeRwJlDr.addRow(new Object[]{true,"                           ","  Biaya Instrumen",":",rsoperasi.getDouble("biayainstrumen"),1,0,rsoperasi.getDouble("biayainstrumen"),"Operasi"}); 
                        }
    
                            rsoperasi.getDouble("biayadokter_ a nak") >0){
                                       
                                       
                           tabModeRwJlDr.addRow(new Object[]{true,"                           ","  Biaya Dokter Anak",":",rsoperasi.getDouble("biayadokter_anak"),1,0,rsoperasi.getDouble("biayadokter_anak"),"Operasi"}); 
                        }
    
                            rsoperasi.getDouble("biayaperawaa t _resu sitas")>0){
                                       
                                       
                         

                            
                            rsoperasi.getDouble("biayadokter_ a neste si")>0){
                                       
                                       
                           tabModeRwJlDr.addRow(new Object[]{true,"                           ","  Biaya Dokter Anastesi",":",rsoperasi.getDouble("biayadokter_anestesi"),1,0,rsoperasi.getDouble("biayadokter_anestesi"),"Operasi"}); 
                        }
                            
                            rsoperasi.getDouble("biayaasisten _ anest esi")>0){
                                        
                                      
                           tabModeRwJlDr.addRow(new Object[]{true,"                           ","  Biaya Asisten Anastesi 1",":",rsoperasi.getDouble("biayaasisten_anestesi"),1,0,rsoperasi.getDouble("biayaasisten_anestesi"),"Operasi"}); 
                        }
                            
                            rsoperasi.getDouble("biayaasisten _ anest esi2")>0){
                                        
                                      
                           tabModeRwJlDr.addRow(new Object[]{true,"                           ","  Biaya Asisten Anastesi 2",":",rsoperasi.getDouble("biayaasisten_anestesi2"),1,0,rsoperasi.getDouble("biayaasisten_anestesi2"),"Operasi"}); 
                        }
                            
                            rsoperasi.getDouble("biayabidan") > 0){ 
                                       
                                       
                         

                            
                            rsoperasi.getDouble("biayabidan2" ) >0){ 
                                        
                                      
                         

                            
                            rsoperasi.getDouble("biayabidan3" ) >0){ 
                                       
                                       
                         

                            
                            rsoperasi.getDouble("biayaperawat _ luar" )>0){
                                       
                                       
                         

                            
                            rsoperasi.getDouble("biayaalat")> 0 ){  
                                        
                                     
                         

                            
                            rsoperasi.getDouble("biayasewaok" ) >0){  
                                        
                                     
                         

                            
                            rsoperasi.getDouble("akomodasi")> 0 ){  
                                        
                                     
                         

                            
                            rsoperasi.getDouble("biaya_omloop " )>0){ 
                                        
                                      
                           tabModeRwJlDr.addRow(new Object[]{true,"                           ","  Biaya Onloop 1",":",rsoperasi.getDouble("biaya_omloop"),1,0,rsoperasi.getDouble("biaya_omloop"),"Operasi"}); 
                        }
                            
                            rsoperasi.getDouble("biaya_omloop 2 ")>0) { 
                                        
                                     
                           tabModeRwJlDr.addRow(new Object[]{true,"                           ","  Biaya Onloop 2",":",rsoperasi.getDouble("biaya_omloop2"),1,0,rsoperasi.getDouble("biaya_omloop2"),"Operasi"}); 
                        }
                            
                            rsoperasi.getDouble("biaya_omloop 3 ")>0) {
                                        
                                      
                           tabModeRwJlDr.addRow(new Object[]{true,"                           ","  Biaya Onloop 3",":",rsoperasi.getDouble("biaya_omloop3"),1,0,rsoperasi.getDouble("biaya_omloop3"),"Operasi"}); 
                        }
                            
                            rsoperasi.getDouble("biaya_omloop 4 ")>0) {
                                        
                                      
                         

                            
                            rsoperasi.getDouble("biaya_omloop 5 ")>0) { 
                                        
                                     
                         

                            
                            rsoperasi.getDouble("bagian_rs")> 0 ){  
                                       
                                      
                         

                            
                            rsoperasi.getDouble("biayasarpras " )>0){  
                                       
                                      
                         

                            
                            rsoperasi.getDouble("biaya_dokter _ pjana k")>0){ 
                                       
                                      
                         

                            
                            rsoperasi.getDouble("biaya_dokter _ umum" )>0){ 
                                       
                                      
                           tabModeRwJlDr.addRow(new Object[]{true,"                           ","  Biaya Dokter Umum",":",rsoperasi.getDouble("biaya_dokter_umum"),1,0,rsoperasi.getDouble("biaya_dokter_umum"),"Operasi"}); 
                        }
                        su bttl=subttl+rsoperasi.getDouble(" b ia y
                                 
                                       
                                     
                        
                    while(rsoperasi.next()){
                        ta bModeRwJlDr.addRow(new Object[]{true , "   
                            ttl=subttl+rsoperasi.getDouble("b i aya") ;  
                                       
                                     
                        

                        ep tion e) {   
                            println("Notifikasi : "+e);   
                                        
                                      
                        

                        er asi.close();   
                               
                                        
                                      
                        rasi != null){
                    psoperasi. c lose() ; 
                } 
            }  
        }catch(Exception  e){ 
            System.out.println(e);   
                                    
                                  
        }    
    }
    
    public void setPiutang(){
        chkRadiologi.setSelected(true);  
        chkLaborat.se tSelected(true);
    }  
    
    private void 
                    
             file=new File("./cache/akunbankjateng.iyem");
             file
             fileWriter = new FileWriter(file);
              H ost_to_Host_B ank_Jateng=Sequel.cariIsi("select set_akun_bankjateng.kd_rek from set_akun_bankjateng");
             fileWriter.write("{\"akunbankjateng\":\""+Host_to_Host_Bank_Jateng+"\"}");
             fileWriter.flush();
     

             Host_to_Host_Ba nk_Jateng="";
        }finally {
            if (fileWriter != null) try { fileWriter.close(); } catch (Exception e) {}
     

    
        ate  
                   
            file=new File("./cach
            file.createNewFile();
            fileWriter = new FileWri t er(file);
            Host_to_Host_Bank_Papua=Sequel.cariIsi("s e lect set_akun_bankpapua. k d_rek f
            fileWriter.write("{
            fileWriter.flush();
             fileWriter.close()
            ch (Exception e) {  
              Host_to_Host_Bank_Papua="";
        } finally {
                
                    
                
                
            if (fileWriter != null) try { fileWriter.close(); } catch (Exception e) {}
     

    
        ate  
                   
            file=new File("./cach
            file.createNewFile();
            fileWriter = new FileWr i ter(file);
            Host_to_Host_Bank_Jabar=Sequel.cariIsi(" s elect set_akun_bankjaba r .kd_rek
            KodeBankJabar=Seque
            fileWriter.write("{\"akunbankjabar\":\""+Host_to_Host_Bank_Jabar+"\",\"kodebankjabar\":\""+KodeBankJabar+"\"}");
             fileWriter.flush()
            fileWriter.close();  
        } catch (Exception e) {
             Host_to_Host_Bank_Jaba
                ="";
                    
                
                
             KodeBankJabar="";
     

        }
         
              
            oid tampilAkunBankBRI
                 
            file=new File("./cache/ a kunbankbri.iyem");
            file.createNe w File();
            fileWriter = new FileWriter(file);    
                       
            Akun_BRI_API=Sequel
            fileWriter.write("{\"akunbankbri\":\""+Akun_BRI_API+"\"}");
             fileWriter.flush()
            fileWriter.close();  
            ch (Exception   e) {
             Akun_BRI_API="";
        } finally {
                
                    
                
                
            if (fileWriter != null) try { fileWriter.close(); } catch (Exception e) {}
     

    
        ate  
                  
            srekening=koneksi.pre
                   "select set_akun_mandiri.kd
            ry {  
               rsrekening=psrekening.executeQuery( ) ;  
               if(rsrekening.ne
                   try{
                        file=ne
                       f i le.createNewFile();
                        fileWriter = new FileWriter(file);
                        Host_to_Hos
                _Bank
                    Mandiri=rsrekening.
                etString("kd_rek");
                
                        fileWriter.write("{\"akunbankmandiri\":\""+Host_to_Host_Bank_Mandiri+"\",\"kodemcm\":\""+rsrekening.getString("kode_mcm")+"\",\"akunbiayabankmandiri\":\""+rsrekening.getString("kd_rek_biaya")+"\",\"norekening\":\""+rsrekening.getString("no_rekening")+"\"}");
     

                    } catch (Exception e) 
             
                    }  f inally {
                        if (fileWriter != null) try { fileWriter.close(); } catch (Exception e) {}
                    }
                }  
            } catc h (Exception e) { 
                Host_to _Host_Bank_Mandiri="";
                System.out.p r intln("Notif Set Nota : "+e);
            } finally{
                if(rsrekening!=null){
                    rsrekening.close();  
                }    
                                   
                                   
                                   
                if(psrekening!=null){
                    psrekening.close();
                }
            }  
        } catch (Exception e) {
             Host_to_Host_Bank_Mandiri="";
                            
                                
                            
                            
        }
    }
    
    private void tampilAkunBankJateng2()  {  
        try{        
             myObj =  new FileReader("./cache/akunbankjateng.iyem");
             root  = mapper.re ad Tree( myObj);
             response = root.path("akunbankjateng");
             Host_to_Host_Bank_Jateng=response.asText();
             myObj .close();   
        } catch (Exception e) {
             Host_to_Host_Bank_Jateng="";
        } finally {
            if (myObj != null) 
            esponse = null;  
            root = null;
     

    
        ate  
                 
            myObj = new FileReader("./cach
            root = mapper.readTree(myObj);
            response = root.path("ak u nbankpapua");
            Host_to_Host_Bank_Papua=response.asText();
             myObj.close();
            ch (Exception e) {  
             Host_to_Host_Bank_Papua="";
        } finally {
                
                    
                
                
            if (myObj != null) try { myObj.close(); } catch (Exception e) {}
            response = null;
            root = null;
     

    
        ate  
                 
            myObj = new FileReader("./cach
            root = mapper.readTree(myObj);
            response = root.path("a k unbankjabar");
            Host_to_Host_Bank_Jabar=response.asText();
             response = root.pa
            KodeBankJabar=response. a sText();
             myObj.close();
        } catch (Exception e) 
                
                    
                
                
             Host_to_Host_Bank_Jabar="";
             KodeBankJabar="";
        } finally {
     

            root = null;
        } 
            
            
            oid tampilAkunBankBRI2() { 
                   
            myObj = new FileReader("./cache/akunba
            root = mapper . readTree(myObj);
            response = root.path("akunbankbri");
             Akun_BRI_API=respo
            myObj.close();  
            ch (Exception   e) {
             Akun_BRI_API="";
        } finally {
                
                    
                
                
            if (myObj != null) try { myObj.close(); } catch (Exception e) {}
            response = null;
            root = null;
     

    
        ate  
                 
            myObj = new FileReader("./cach
            root = mapper.readTree(myObj);
            response = r o ot.path("akunbankm
            Host_to_Host_Bank_Mandiri=response.asText();
             myObj.close();
            ch (Exceptio n  e) {
             Host_to_Host_Bank_Mandiri="";
        } finally {
                
                    
                
                
            if (myObj != null) try { myObj.close(); } catch (Exception e) {}
            response = null;
            root = null;
     

    
        ate  
                 
            Valid.tabelKosong(tabModeAkunB
            file=new File("./cache/akunbayar.iyem");
            file.createNewFile();  
            fileWriter = new FileWriter(file);
             StringBuilder iyem
            psakunbayar=koneksi.prepa r eStatement("select * from akun_bayar");
             try{
                 rsakunbayar=p
                akunb
                    yar.executeQue
                y();
                
                 while(rsakunbayar.next()){      
                     tabModeAkunBayar.addRow(new Object[]{rsakunbayar.getString(1),rsakunbayar.getString(2),"",rsakunbayar.getDouble(3),""});
                     iyembuilder.append("{\"NamaAkun\":\"").append(rsakunbayar.getString(1).replaceAll("\"","")).append("\",\"KodeRek\":\"").append(rsakunbayar.getString(2)).append("\",\"PPN\":\"").append(rsakunbayar.getDouble(3)).append("\"},");
     

                 System.out.println(
             
                if(rsakunbayar != null){
                      rsakunbayar.close();
                } 
                if(psakunbayar != null){
                    psakunbayar.close();
                }   
            } 
                  
                iyemb uilder.length() > 0)  
                    uilder.setLength(iyembuilder.length( )  - 1);  
                              
                    riter.write("{\"akunbayar\":["+iyembuilder+"]}"); 
                            
                            
                i
            } 
                  
            fileWrite r
                bu ilder=null; 
                    ption e) {
                m
                 {  
                    riter != null) try {
                
            

            oid tampilAkunBayar2() {         
         try{           
             jml=0;    
             for(z=0;z<tbAkunBayar.
             

               }
            }  
             
             Nama_Akun_Bayar=new String[jml];  
             Kode_Rek_Bayar=new String[jml];
             Bayar=new String[jml];
                
                    
                
                
             PPN_Persen=new String[jml];
     

             jml=0;
             
                i f(
                      N a m a_Akun_Bayar[jml]=tbAkunBa yar. getValueAt(z,0).toString();
                     Kode_Rek_Bayar[jml]=tbAku nBayar.getValueAt(z,1).to String();
                    Bayar[jml]=tbAkunBayar.getValueAt(z,2).toString();
                 
             

               }  
            }  
              
            Valid.tabe l Kosong(tabModeAk
              

                t ab
                      N a m a_Akun_Bayar[z],Kode_Rek_B ayar [z],Bayar[z],PPN_Persen[z],PPN_Besar[z]
                }) ;  
             }   
                
             Nama_Akun_Bayar=n u ll; 
             Kode_Rek_Bayar=null;   
             Bayar=null;   
             PPN_Persen=null;
             PPN_
            

            root = mapper.readTree(myObj);

            if( re s po n s e.is Arra y()){
                for(JsonNode list:response){
                        if(list.path("NamaA kun").asText().toL owerCase( ).contains(TCa ri.getText().toLowerCase())){
                   
             

                   }  
               }  
            }  
            myObj.clos e ();
            ch (Excep t ion e

            ally {
            f (myObj != null) try { myObj.
            esponse = null;
            oo t = null; 
        }    
    }  
     
                                ayar3() {           
                                 
         try{      
             file=new File("./cache/akunbayar.iyem");
             file
            f
            StringBuilder 
             psakunbayar=koneksi.prepareStatement("select * from akun_bayar");
             try{  
                 rsakunbayar=psakunbayar.executeQuery();
                 while(rsakunb
                yar.n
                    xt()){      
                
                
                     iyembuilder.append("{\"NamaAkun\":\"").append(rsakunbayar.getString(1).replaceAll("\"","")).append("\",\"KodeRek\":\"").append(rsakunbayar.getString(2)).append("\",\"PPN\":\"").append(rsakunbayar.getDouble(3)).append("\"},");
                 }
             }catch (Exception e) {
     

                 if(rsakunbayar != nu
             
                 }  
                if(psakunbayar !=
                    psakunbayar.close();
                } 
            }  
             
                iyembuilder . length() > 0) {
                yembu ilder.setLength(iyem b
                    riter.write("{\"akunbayar\":["+iyembuilder+"]}"); 
                            
                            
                i
            } 
                  
            fileWrite r
                bu ilder=null; 
                    ption e) {
                m
                 {  
                    riter != null) try {
                
            

            oid tampilAkunBayarTersimpan() {
         try{           
             Valid.tabelKosong(tabModeAkunBayar);    
             
            p

                    "akun_bayar
                    "on   akun_bayar.nama_bayar=detail_nota_jalan.nama_bayar where detail_nota_jalan.no_rawat=?"+
                     (TCari.getText().trim().equals("")?"":"and akun_bayar.nama_bayar like ?") 
             );  
             try{                 
                 psakunbayar.setStr
                ng(1,
                    NoRw.getText());
                
                
                 if(!TCari.getText().trim().equals("")){
     

                 rsakunbayar=psakunbayar.exec
             
                   tabModeAkunBayar.addRow(new O

            }catch (Exc e ption e) {
                    em.out.println("Notifikasi Akun Bayar Tersimpan : "+e); 
                            y{ 
                            sakunbayar != null){ 
                            rsakunbayar.close();      
                 
                    psakunbayar.close(); 
                }   
             }     
                
                Exception e x ) {
                m.out .println("Notifikasi  : "+ex);
         }   
                               
                
             
                tampilAkunPiutang() {  
                     
                d. tabelKosong(tabModeAk u
                     File("./cache/akunp
                .
                Wr iter = new FileWriter (
                    ilder iyembuilder = 
                u
            t

                 while(rsakunpiutang.next()){                    
                     tabModeAkunPiutang.addRow ( new 
                    iyembuilder.append("{\"NamaAkun\":\"").append(rsakunpiutang.getString(1).replaceAll("\"","")).append("\",\"KodeRek\":\"").append(rsakunpiutang.getString(2)).append("\",\"KdPJ\":\"").append(rsakunpiutang.getString(3)).append("\"},");
     

                 System.out.println("N
             
                if(rsakunpiutang != null){
                      rsakunpiutang.close();
                } 
                if(psakunpiutang != null){
                    psakunpiutang.close();
                }   
            } 
                  
                iyemb uilder.length() > 0) { 
                    uilder.setLength(iyembuilder.length()  -  1); 
                                
                    riter.write("{\"akunpiutang\":["+iyembuilder+"]}"); 
                            
                            
                i
            } 
                  
            fileWrite r
                bu ilder=null; 
                    ion e){
                e
                {  
                    riter != null) try { f
                
            

            oid tampilAkunPiutang2() {
         try{        
             jml=0;    
             for(z=0;z<tbAkunPiutan
             

               }
            }  
               
            Nama_Akun_Piutang=new String[jml];  
              Kode_Rek_Piutang=new String[jml];
             Kd_PJ=new String[jml];
                
                    
                
                
             Besar_Piutang=new String[jml];
     

             jml=0;             
             
                i f(
                      N a m a_Akun_Piutang[jml]=tbAkunPi utan g.getValueAt(z,0).toString();
                     Kode_Rek_Piutang[jml]=tbAku nPiutang.getValueAt(z,1). toString();
                    Kd_PJ[jml]=tbAkunPiutang.getValueAt(z,2).toString();
                 
             

               }  
            }  
              
            Valid.tabelKo s ong(tabModeAkunP
              

                t ab
                      N a m a_Akun_Piutang[z],Kode_Rek_P iuta ng[z],Kd_PJ[z],Besar_Piutang[z],Jatuh_Tempo[z]
                }) ;  
             }   
                
             Nama_Akun_Piutang = null; 
             Kode_Rek_Piutang=null;   
             Kd_PJ=null;   
                    iutang=null;
             Jatu
            

            root = mapper.readTree(myObj);

            if( re s po n s e.is Arra y()){
                for(JsonNode list:response){
                        if(list.path("NamaAku n").asText().toLower Case().co ntains(TCari1.get Text().toLowerCase())){
                   
             

                   }  
               }  
            }  
            myObj.close() ; 
            h(Exception   e){

            lly {
            f (myObj != null) try { myObj.
            esponse = null;
            oo t = null; 
        }    
    }  
     
                                iutang3() { 
                                   
         try{        
             file=new File("./cache/akunpiutang.iyem");
             file
            f
            StringBuilder iyembuilder = new StringBuilder();
              p sakunpiutang= k
            try{  
                  rsakunpiutang=psakunpiutang.executeQuery();
                 while(rsakunp
                utang
                    next()){      
                             
                
                     iyembuilder.append("{\"NamaAkun\":\"").append(rsakunpiutang.getString(1).replaceAll("\"","")).append("\",\"KodeRek\":\"").append(rsakunpiutang.getString(2)).append("\",\"KdPJ\":\"").append(rsakunpiutang.getString(3)).append("\"},");
                 } 
             }catch (Exception e) {
     

                 if(rsakunpiutang != nu
             
                 }  
                if(psakunpiutang 
                    psakunpiutang.close();
                } 
            }  
            if  (
                yembuilder.se t Length(iyembuilder.length() -
                ileWr iter.write("{\"akunpiu t
                    riter.flush(); 
                            
                            
                
             
                Writer.close();  
            iyembuild e
                ce ption e){ 
                    ut.println("Notifikasi
                {
                il eWriter != null) try {  f
                    
                
            
            oid tampilAkunPiutangTersimpan() {
         try{           
             Valid.tabelKosong(tabModeAkunPiutang);      
             psakunpiutang=koneksi.
             

                    "akun_piuta
                    "wh e re detail_piutang_pasien.no_rawat=? "+(TCari1.getText().trim().equals("")?"":"and akun_piutang.nama_bayar like ?")
              ) ; 
            try{  
                  psakunpiutang.setString(1,TNoRw.getText());
                 if(!TCari1.getText
                ).tri
                    ().equals("")){
                
                
                    psakunpiutang.setString(2,"%"+TCari1.getText()+"%"); 
     

                 while(rsakunpiutang.next()){  
             
                } 
            }catch (Excep t ion e) {
                    em.out.println("Notifikasi Akun Piutang Tersimpan : "+e); 
                            y{
                            
                            sakunpiutang != null){
                            
                            rsakunpiutang.close();
                                   
                 
                    psakunpiutang.close(); 
                }   
             }     
                
                Exception ex)   {
                m.out .println("Notifikasi :  
                       
                               
                
             
                isSimpan(){  
            ngaturanb i
                bo rat.setSelected(true); 
                    ogi.setSelected(true);
                t
                mb ahan.setSelected(true);  
                    etSelected(true);  
                m
            h

            chkTarifPrm.setSelected(true);  
            isRawat2();  
        


            JOptionPane.showMessageDialog(null,"Maaf, Silahkan tampilkan semua pilihan tagihan...!!!");
        }else{
            try {
                psnota=koneksi.prepareStatement("insert into nota_jalan values(?,?,?,?)");
                try {    
                    no_nota=Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(nota_jalan.no_nota,4),signed)),0) from nota_jalan where nota_jalan.tanggal='"+Valid.SetTgl(DTPTgl.getSelectedItem()+"").substring(0,10)+"' ",Valid.SetTgl(DTPTgl.getSelectedItem()+"").substring(0,10).replaceAll("-","/")+"/RJ",4);
                    psnota.setString(1,TNoRw.getText());
                    psnota.setString(2,no_nota);
                    psnota.setString(3,Valid.SetTgl(DTPTgl.getSelectedItem()+""));
                    psnota.setString(4,DTPTgl.getSelectedItem().toString().substring(11,19));
                    psnota.executeUpdate();
                } catch (Exception e) {
                    nota_jalan=Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(nota_jalan.no_nota,4),signed)),0) from nota_jalan where nota_jalan.tanggal='"+Valid.SetTgl(DTPTgl.getSelectedItem()+"").substring(0,10)+"' ",Valid.SetTgl(DTPTgl.getSelectedItem()+"").substring(0,10).replaceAll("-","/")+"/RJ",4);
                    Sequel.meghapus("nota_jalan","no_rawat",TNoRw.getText());               
                    tbBilling.setValueAt(": "+nota_jalan,0,2);
                    no_nota=nota_jalan;
                    psnota.setString(1,TNoRw.getText());
                    psnota.setString(2,nota_jalan);
                    psnota.setString(3,Valid.SetTgl(DTPTgl.getSelectedItem()+""));
                    psnota.setString(4,DTPTgl.getSelectedItem().toString().substring(11,19));
                    psnota.executeUpdate();
                } finally{
                    if(psnota != null){
                        psnota.close();
                    } 
                }

                Sequel.AutoComitFalse();
                sukses=true; 
                for(i=0;i<tbBilling.getRowCount();i++){  
                    psbiling=koneksi.prepareStatement(sqlpsbiling);
                    psrincianbilling=koneksi.prepareStatement(sqlpsrincianbiling);
                    try {
                        psbiling.setInt(1,i);
                        psbiling.setString(2,TNoRw.getText());
                        psbiling.setString(3,Valid.SetTgl(DTPTgl.getSelectedItem()+""));
                        psbiling.setString(4,tbBilling.getValueAt(i,1).toString());
                        psbiling.setString(5,tbBilling.getValueAt(i,2).toString().replaceAll("'",""));
                        psbiling.setString(6,tbBilling.getValueAt(i,3).toString());                    
                        try {                        
                            psbiling.setDouble(7,Valid.SetAngka(tbBilling.getValueAt(i,4).toString()));
                        } catch (Exception e) {
                            psbiling.setDouble(7,0);
                        }
                        try {
                            psbiling.setDouble(8,Valid.SetAngka(tbBilling.getValueAt(i,5).toString()));
                        } catch (Exception e) {
                            psbiling.setDouble(8,0);
                        }
                        subttl=0;
                        try {
                            if((!tbBilling.getValueAt(i,8).toString().equals("Laborat"))&&(!tbBilling.getValueAt(i,8).toString().equals("Obat"))){
                                subttl=Valid.SetAngka(tbBilling.getValueAt(i,6).toString());
                            }                        
                            psbiling.setDouble(9,Valid.SetAngka(tbBilling.getValueAt(i,6).toString()));                        
                        } catch (Exception e) {
                            subttl=0;
                            psbiling.setDouble(9,0);   
                        }
                        if(subttl>0){
                            Sequel.queryu2("delete from tambahan_biaya where no_rawat=? and nama_biaya=?",2,new String[]{
                                TNoRw.getText(),"Tambahan "+tbBilling.getValueAt(i,2).toString()
                            });
                            Sequel.menyimpan("tambahan_biaya","'"+TNoRw.getText()+"','Tambahan "+tbBilling.getValueAt(i,2).toString()+
                                    "','"+tbBilling.getValueAt(i,6).toString()+"'","Tambahan Biaya");                        
                        }
                        if(subttl<0){
                            Sequel.queryu2("delete from pengurangan_biaya where no_rawat=? and nama_pengurangan=?",2,new String[]{
                                TNoRw.getText(),"Potongan "+tbBilling.getValueAt(i,2).toString()
                            });
                            Sequel.menyimpan("pengurangan_biaya","'"+TNoRw.getText()+"','Potongan "+tbBilling.getValueAt(i,2).toString()+
                                    "','"+tbBilling.getValueAt(i,6).toString()+"'","Potongan Biaya");                        
                        }
                        try {
                            psbiling.setDouble(10,Valid.SetAngka(tbBilling.getValueAt(i,7).toString())); 
                        } catch (Exception e) {
                            psbiling.setDouble(10,0);
                        }                    
                        psbiling.setString(11,tbBilling.getValueAt(i,8).toString());
                        psbiling.executeUpdate();
                        // ADD DETAIL RINCIAN BILLING
                        try {
                             psrincianbilling.setInt(1,i);
                             psrincianbilling.setString(2,TNoRw.getText());
                             psrincianbilling.setString(3, tbBilling.getValueAt(i, 9) != null ? tbBilling.getValueAt(i, 9).toString() : "");                             
                             psrincianbilling.executeUpdate();
                        } catch (Exception e) {
                            System.out.println("Notifikasi : "+e);
                        }finally{
                            if(psrincianbilling != null){
                               psrincianbilling.close();
                            } 
                        }
                        //END DETAIL RINCIAN BILLING
                    } catch (Exception e) {
                        sukses=false;
                        System.out.println("Notifikasi : "+e);
                    } finally{
                        if(psbiling != null){
                            psbiling.close();
                        } 
                    }
                }

                if(sukses==true){
                    Sequel.queryu2("delete from tampjurnal");
                    itembayar=0;besarppn=0;
                    row2=tbAkunBayar.getRowCount();                
                    for(r=0;r<row2;r++){
                        if(Valid.SetAngka(tbAkunBayar.getValueAt(r,2).toString())>0){
                            try {
                                itembayar=Double.parseDouble(tbAkunBayar.getValueAt(r,2).toString()); 
                            } catch (Exception e) {
                                itembayar=0;
                            }    

                            if(!tbAkunBayar.getValueAt(r,4).toString().equals("")){
                                try {
                                    besarppn=Valid.roundUp(Double.parseDouble(tbAkunBayar.getValueAt(r,4).toString()),100); 
                                } catch (Exception e) {
                                    besarppn=0;
                                }               
                            }  

                            if(countbayar>1){
                                if(Sequel.menyimpantf2("detail_nota_jalan","?,?,?,?","Akun bayar",4,new String[]{
                                        TNoRw.getText(),tbAkunBayar.getValueAt(r,0).toString(),Double.toString(besarppn),Double.toString(itembayar)
                                    })==true){
                                        if(Sequel.menyimpantf("tampjurnal","'"+tbAkunBayar.getValueAt(r,1).toString()+"','"+tbAkunBayar.getValueAt(r,0).toString()+"','"+Double.toString(itembayar)+"','0'","debet=debet+'"+Double.toString(itembayar)+"'","kd_rek='"+tbAkunBayar.getValueAt(r,1).toString()+"'")==false){
                                            sukses=false;
                                        }else{
                                            if(Host_to_Host_Bank_Jateng.equals(tbAkunBayar.getValueAt(r,1).toString())){
                                                if(Sequel.menyimpantf2("tagihan_bpd_jateng","?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,''",16,new String[]{
                                                    no_rkm_medis,nm_pasien,alamat,jk,tgl_lahir,umurdaftar,tgl_registrasi,no_nota.replaceAll("/",""),Double.toString(itembayar),"Pembayaran Pasien Rawat Jalan",TNoRw.getText(),"Ralan",Valid.SetTgl(DTPTgl.getSelectedItem()+""),"Pending",akses.getkode(),"0000-00-00"
                                                })==false){
                                                    sukses=false;
                                                }
                                            }
                                            if(Host_to_Host_Bank_Papua.equals(tbAkunBayar.getValueAt(r,1).toString())){
                                                if(Sequel.menyimpantf2("tagihan_bpd_papua","?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,''",16,new String[]{
                                                    no_rkm_medis,nm_pasien,alamat,jk,tgl_lahir,umurdaftar,tgl_registrasi,no_nota.replaceAll("/",""),Double.toString(itembayar),"Pembayaran Pasien Rawat Jalan",TNoRw.getText(),"Ralan",Valid.SetTgl(DTPTgl.getSelectedItem()+""),"Pending",akses.getkode(),"0000-00-00"
                                                })==false){
                                                    sukses=false;
                                                }
                                            }
                                            if(Host_to_Host_Bank_Jabar.equals(tbAkunBayar.getValueAt(r,1).toString())){
                                                if(Sequel.menyimpantf2("tagihan_bpd_jabar","?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,''",16,new String[]{
                                                    no_rkm_medis,nm_pasien,alamat,jk,tgl_lahir,umurdaftar,tgl_registrasi,no_nota.replaceAll("/",""),Double.toString(itembayar),KodeBankJabar+"1011312"+Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(tagihan_bpd_jabar.keterangan,4),signed)),0) from tagihan_bpd_jabar where year(tagihan_bpd_jabar.tgl_closing)='"+DTPTgl.getSelectedItem().toString().substring(6,10)+"' and tagihan_bpd_jabar.status_lanjut='Ralan'",DTPTgl.getSelectedItem().toString().substring(8,10),4),TNoRw.getText(),"Ralan",Valid.SetTgl(DTPTgl.getSelectedItem()+""),"Pending",akses.getkode(),"0000-00-00"
                                                })==false){
                                                    sukses=false;
                                                }
                                            }
                                            if(Akun_BRI_API.equals(tbAkunBayar.getValueAt(r,1).toString())){
                                                if(apibri.buatVA(TNoRw.getText().replaceAll("/","").substring(2,8)+TNoRw.getText().replaceAll("/","").substring(10,14),(nm_pasien.length()>38?nm_pasien.substring(0,38):nm_pasien),Valid.SetAngka2(itembayar),TNoRw.getText())==true){
                                                    if(Sequel.menyimpantf2("tagihan_briva","?,?,?,?,?,?,?,?,?,?,?,?,?,?,''",14,new String[]{
                                                            no_rkm_medis,nm_pasien,alamat,jk,tgl_lahir,tgl_registrasi,TNoRw.getText().replaceAll("/","").substring(2,8)+TNoRw.getText().replaceAll("/","").substring(10,14),Double.toString(itembayar),TNoRw.getText(),"Ralan",Valid.SetTgl(DTPTgl.getSelectedItem()+""),"Pending",akses.getkode(),"0000-00-00"
                                                        })==false){
                                                        sukses=false;
                                                    }
                                                }
                                            }
                                            if(Host_to_Host_Bank_Mandiri.equals(tbAkunBayar.getValueAt(r,1).toString())){
                                                if(Sequel.menyimpantf2("tagihan_mandiri","?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,''",22,new String[]{
                                                    no_rkm_medis, nm_pasien, alamat, jk, tgl_lahir, umurdaftar, tgl_registrasi,no_nota.replaceAll("/","").replaceAll("RJ","01"),Double.toString(itembayar),TNoRw.getText(),TNoRw.getText().replaceAll("/",""),"Ralan",Valid.SetTgl(DTPTgl.getSelectedItem()+"")+" "+DTPTgl.getSelectedItem().toString().substring(11,19),"Pending","","","0",akses.getkode(),"","","","0000-00-00",""
                                                })==false){
                                                    sukses=false;
                                                }
                                            }
                                        }
                                }else{
                                    sukses=false;
                                }
                            }else if(countbayar==1){
                                if(piutang<=0){
                                    if(Sequel.menyimpantf2("detail_nota_jalan","?,?,?,?","Akun bayar",4,new String[]{
                                            TNoRw.getText(),tbAkunBayar.getValueAt(r,0).toString(),Double.toString(besarppn),Double.toString(total)
                                        })==true){
                                            if(Sequel.menyimpantf("tampjurnal","'"+tbAkunBayar.getValueAt(r,1).toString()+"','"+tbAkunBayar.getValueAt(r,0).toString()+"','"+Double.toString(total)+"','0'","debet=debet+'"+Double.toString(total)+"'","kd_rek='"+tbAkunBayar.getValueAt(r,1).toString()+"'")==false){
                                                sukses=false;
                                            }else{
                                                if(Host_to_Host_Bank_Jateng.equals(tbAkunBayar.getValueAt(r,1).toString())){
                                                    if(Sequel.menyimpantf2("tagihan_bpd_jateng","?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,''",16,new String[]{
                                                        no_rkm_medis,nm_pasien,alamat,jk,tgl_lahir,umurdaftar,tgl_registrasi,no_nota.replaceAll("/",""),Double.toString(total),"Pembayaran Pasien Rawat Jalan",TNoRw.getText(),"Ralan",Valid.SetTgl(DTPTgl.getSelectedItem()+""),"Pending",akses.getkode(),"0000-00-00"
                                                    })==false){
                                                        sukses=false;
                                                    }
                                                }
                                                if(Host_to_Host_Bank_Papua.equals(tbAkunBayar.getValueAt(r,1).toString())){
                                                    if(Sequel.menyimpantf2("tagihan_bpd_papua","?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,''",16,new String[]{
                                                        no_rkm_medis,nm_pasien,alamat,jk,tgl_lahir,umurdaftar,tgl_registrasi,no_nota.replaceAll("/",""),Double.toString(total),"Pembayaran Pasien Rawat Jalan",TNoRw.getText(),"Ralan",Valid.SetTgl(DTPTgl.getSelectedItem()+""),"Pending",akses.getkode(),"0000-00-00"
                                                    })==false){
                                                        sukses=false;
                                                    }
                                                }
                                                if(Host_to_Host_Bank_Jabar.equals(tbAkunBayar.getValueAt(r,1).toString())){
                                                    if(Sequel.menyimpantf2("tagihan_bpd_jabar","?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,''",16,new String[]{
                                                        no_rkm_medis,nm_pasien,alamat,jk,tgl_lahir,umurdaftar,tgl_registrasi,no_nota.replaceAll("/",""),Double.toString(total),KodeBankJabar+"1011312"+Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(tagihan_bpd_jabar.keterangan,4),signed)),0) from tagihan_bpd_jabar where year(tagihan_bpd_jabar.tgl_closing)='"+DTPTgl.getSelectedItem().toString().substring(6,10)+"' and tagihan_bpd_jabar.status_lanjut='Ralan'",DTPTgl.getSelectedItem().toString().substring(8,10),4),TNoRw.getText(),"Ralan",Valid.SetTgl(DTPTgl.getSelectedItem()+""),"Pending",akses.getkode(),"0000-00-00"
                                                    })==false){
                                                        sukses=false;
                                                    }
                                                }
                                                if(Akun_BRI_API.equals(tbAkunBayar.getValueAt(r,1).toString())){
                                                    if(apibri.buatVA(TNoRw.getText().replaceAll("/","").substring(2,8)+TNoRw.getText().replaceAll("/","").substring(10,14),(nm_pasien.length()>38?nm_pasien.substring(0,38):nm_pasien),Valid.SetAngka2(total),TNoRw.getText())==true){
                                                        if(Sequel.menyimpantf2("tagihan_briva","?,?,?,?,?,?,?,?,?,?,?,?,?,?,''",14,new String[]{
                                                            no_rkm_medis,nm_pasien,alamat,jk,tgl_lahir,tgl_registrasi,TNoRw.getText().replaceAll("/","").substring(2,8)+TNoRw.getText().replaceAll("/","").substring(10,14),Double.toString(total),TNoRw.getText(),"Ralan",Valid.SetTgl(DTPTgl.getSelectedItem()+""),"Pending",akses.getkode(),"0000-00-00"
                                                        })==false){
                                                            sukses=false;
                                                        }
                                                    }
                                                }
                                                if(Host_to_Host_Bank_Mandiri.equals(tbAkunBayar.getValueAt(r,1).toString())){
                                                    if(Sequel.menyimpantf2("tagihan_mandiri","?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,''",22,new String[]{
                                                        no_rkm_medis, nm_pasien, alamat, jk, tgl_lahir, umurdaftar, tgl_registrasi,no_nota.replaceAll("/","").replaceAll("RJ","01"),Double.toString(total),TNoRw.getText(),TNoRw.getText().replaceAll("/",""),"Ralan",Valid.SetTgl(DTPTgl.getSelectedItem()+"")+" "+DTPTgl.getSelectedItem().toString().substring(11,19),"Pending","","","0",akses.getkode(),"","","","0000-00-00",""
                                                    })==false){
                                                        sukses=false;
                                                    }
                                                }
                                            }
                                    }else{
                                        sukses=false;
                                    } 
                                }else{
                                    if(Sequel.menyimpantf2("detail_nota_jalan","?,?,?,?","Akun bayar",4,new String[]{
                                            TNoRw.getText(),tbAkunBayar.getValueAt(r,0).toString(),Double.toString(besarppn),Double.toString(itembayar)
                                        })==true){
                                            if(Sequel.menyimpantf("tampjurnal","'"+tbAkunBayar.getValueAt(r,1).toString()+"','"+tbAkunBayar.getValueAt(r,0).toString()+"','"+Double.toString(itembayar)+"','0'","debet=debet+'"+Double.toString(itembayar)+"'","kd_rek='"+tbAkunBayar.getValueAt(r,1).toString()+"'")==false){
                                                sukses=false;
                                            }else{
                                                if(Host_to_Host_Bank_Jateng.equals(tbAkunBayar.getValueAt(r,1).toString())){
                                                    if(Sequel.menyimpantf2("tagihan_bpd_jateng","?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,''",16,new String[]{
                                                        no_rkm_medis,nm_pasien,alamat,jk,tgl_lahir,umurdaftar,tgl_registrasi,no_nota.replaceAll("/",""),Double.toString(itembayar),"Pembayaran Pasien Rawat Jalan",TNoRw.getText(),"Ralan",Valid.SetTgl(DTPTgl.getSelectedItem()+""),"Pending",akses.getkode(),"0000-00-00"
                                                    })==false){
                                                        sukses=false;
                                                    }
                                                }
                                                if(Host_to_Host_Bank_Papua.equals(tbAkunBayar.getValueAt(r,1).toString())){
                                                    if(Sequel.menyimpantf2("tagihan_bpd_papua","?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,''",16,new String[]{
                                                        no_rkm_medis,nm_pasien,alamat,jk,tgl_lahir,umurdaftar,tgl_registrasi,no_nota.replaceAll("/",""),Double.toString(itembayar),"Pembayaran Pasien Rawat Jalan",TNoRw.getText(),"Ralan",Valid.SetTgl(DTPTgl.getSelectedItem()+""),"Pending",akses.getkode(),"0000-00-00"
                                                    })==false){
                                                        sukses=false;
                                                    }
                                                }
                                                if(Host_to_Host_Bank_Jabar.equals(tbAkunBayar.getValueAt(r,1).toString())){
                                                    if(Sequel.menyimpantf2("tagihan_bpd_jabar","?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,''",16,new String[]{
                                                        no_rkm_medis,nm_pasien,alamat,jk,tgl_lahir,umurdaftar,tgl_registrasi,no_nota.replaceAll("/",""),Double.toString(itembayar),KodeBankJabar+"1011312"+Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(tagihan_bpd_jabar.keterangan,4),signed)),0) from tagihan_bpd_jabar where year(tagihan_bpd_jabar.tgl_closing)='"+DTPTgl.getSelectedItem().toString().substring(6,10)+"' and tagihan_bpd_jabar.status_lanjut='Ralan'",DTPTgl.getSelectedItem().toString().substring(8,10),4),TNoRw.getText(),"Ralan",Valid.SetTgl(DTPTgl.getSelectedItem()+""),"Pending",akses.getkode(),"0000-00-00"
                                                    })==false){
                                                        sukses=false;
                                                    }
                                                }
                                                if(Akun_BRI_API.equals(tbAkunBayar.getValueAt(r,1).toString())){
                                                    if(apibri.buatVA(TNoRw.getText().replaceAll("/","").substring(2,8)+TNoRw.getText().replaceAll("/","").substring(10,14),(nm_pasien.length()>38?nm_pasien.substring(0,38):nm_pasien),Valid.SetAngka2(itembayar),TNoRw.getText())==true){
                                                        if(Sequel.menyimpantf2("tagihan_briva","?,?,?,?,?,?,?,?,?,?,?,?,?,?,''",14,new String[]{
                                                            no_rkm_medis,nm_pasien,alamat,jk,tgl_lahir,tgl_registrasi,TNoRw.getText().replaceAll("/","").substring(2,8)+TNoRw.getText().replaceAll("/","").substring(10,14),Double.toString(itembayar),TNoRw.getText(),"Ralan",Valid.SetTgl(DTPTgl.getSelectedItem()+""),"Pending",akses.getkode(),"0000-00-00"
                                                        })==false){
                                                            sukses=false;
                                                        }
                                                    }
                                                }
                                                if(Host_to_Host_Bank_Mandiri.equals(tbAkunBayar.getValueAt(r,1).toString())){
                                                    if(Sequel.menyimpantf2("tagihan_mandiri","?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,''",22,new String[]{
                                                        no_rkm_medis, nm_pasien, alamat, jk, tgl_lahir, umurdaftar, tgl_registrasi,no_nota.replaceAll("/","").replaceAll("RJ","01"),Double.toString(itembayar),TNoRw.getText(),TNoRw.getText().replaceAll("/",""),"Ralan",Valid.SetTgl(DTPTgl.getSelectedItem()+"")+" "+DTPTgl.getSelectedItem().toString().substring(11,19),"Pending","","","0",akses.getkode(),"","","","0000-00-00",""
                                                    })==false){
                                                        sukses=false;
                                                    }
                                                }
                                            } 
                                    }else{
                                        sukses=false;
                                    }                                
                                }                                
                            }                        
                        } 
                    }

                    itempiutang=0;
                    row2=tabModeAkunPiutang.getRowCount();
                    for(r=0;r<row2;r++){ 
                        if(!tabModeAkunPiutang.getValueAt(r,3).toString().equals("")){
                            try {
                                itempiutang=Double.parseDouble(tabModeAkunPiutang.getValueAt(r,3).toString()); 
                            } catch (Exception e) {
                                itempiutang=0;
                            } 

                            if(Sequel.menyimpantf2("detail_piutang_pasien","?,?,?,?,?,?","Aku Piutang",6,new String[]{
                                    TNoRw.getText(),tabModeAkunPiutang.getValueAt(r,0).toString(),tabModeAkunPiutang.getValueAt(r,2).toString(),
                                    Double.toString(itempiutang),Double.toString(itempiutang),Valid.SetTgl(tabModeAkunPiutang.getValueAt(r,4).toString())
                                })==true){
                                    if(Sequel.menyimpantf("tampjurnal","'"+tabModeAkunPiutang.getValueAt(r,1).toString()+"','"+tabModeAkunPiutang.getValueAt(r,0).toString()+"','"+Double.toString(itempiutang)+"','0'","debet=debet+'"+Double.toString(itempiutang)+"'","kd_rek='"+tabModeAkunPiutang.getValueAt(r,1).toString()+"'")==false){
                                        sukses=false;
                                    }                 
                            }else{
                                sukses=false;
                            }
                        }             
                    }
                    
                    if(sukses==true){
                        if((-1*ttlPotongan)>0){
                            if(Sequel.menyimpantf("tampjurnal","'"+akunbillingralan.getPotongan_Ralan()+"','Potongan_Ralan','"+(-1*ttlPotongan)+"','0'","debet=debet+'"+(-1*ttlPotongan)+"'","kd_rek='"+akunbillingralan.getPotongan_Ralan()+"'")==false){
                                sukses=false;
                            }   
                        }

                        if((ttlRalan_Dokter+ttlRalan_Dokter_Param+ttlRalan_Paramedis)>0){
                            if(Sequel.menyimpantf("tampjurnal","'"+akunbillingralan.getTindakan_Ralan()+"','Tindakan Ralan','0','"+(ttlRalan_Dokter+ttlRalan_Dokter_Param+ttlRalan_Paramedis)+"'","kredit=kredit+'"+(ttlRalan_Dokter+ttlRalan_Dokter_Param+ttlRalan_Paramedis)+"'","kd_rek='"+akunbillingralan.getTindakan_Ralan()+"'")==false){
                                sukses=false;
                            }    
                        }

                        if(ttlLaborat>0){
                            if(Sequel.menyimpantf("tampjurnal","'"+akunbillingralan.getLaborat_Ralan()+"','Laborat Ralan','0','"+ttlLaborat+"'","kredit=kredit+'"+(ttlLaborat)+"'","kd_rek='"+akunbillingralan.getLaborat_Ralan()+"'")==false){
                                sukses=false;
                            }    
                        }

                        if(ttlRadiologi>0){
                            if(Sequel.menyimpantf("tampjurnal","'"+akunbillingralan.getRadiologi_Ralan()+"','Radiologi Ralan','0','"+ttlRadiologi+"'","kredit=kredit+'"+(ttlRadiologi)+"'","kd_rek='"+akunbillingralan.getRadiologi_Ralan()+"'")==false){
                                sukses=false;
                            }    
                        }

                        if((ttlObat-obatlangsung-ppnobat)>0){
                            if(Sequel.menyimpantf("tampjurnal","'"+akunbillingralan.getObat_Ralan()+"','Obat Ralan','0','"+(ttlObat-obatlangsung-ppnobat)+"'","kredit=kredit+'"+(ttlObat-obatlangsung-ppnobat)+"'","kd_rek='"+akunbillingralan.getObat_Ralan()+"'")==false){
                                sukses=false;
                            }    
                        }
                        
                        if(obatlangsung>0){
                            if(Sequel.menyimpantf("tampjurnal","'"+akunbillingralan.getObat_Langsung_Ralan()+"','Obat Ralan','0','"+obatlangsung+"'","kredit=kredit+'"+(obatlangsung)+"'","kd_rek='"+akunbillingralan.getObat_Langsung_Ralan()+"'")==false){
                                sukses=false;
                            }    
                        }
                        
                        if(ppnobat>0){
                            if(Sequel.menyimpantf("tampjurnal","'"+PPN_Keluaran+"','PPN Keluaran','0','"+ppnobat+"'","kredit=kredit+'"+(ppnobat)+"'","kd_rek='"+PPN_Keluaran+"'")==false){
                                sukses=false;
                            }    
                        }
                        
                        if(ttlRegistrasi>0){
                            if(Sequel.menyimpantf("tampjurnal","'"+akunbillingralan.getRegistrasi_Ralan()+"','Registrasi Ralan','0','"+ttlRegistrasi+"'","kredit=kredit+'"+(ttlRegistrasi)+"'","kd_rek='"+akunbillingralan.getRegistrasi_Ralan()+"'")==false){
                                sukses=false;
                            }    
                        }

                        if(ttlTambahan>0){
                            if(Sequel.menyimpantf("tampjurnal","'"+akunbillingralan.getTambahan_Ralan()+"','Tambahan Ralan','0','"+ttlTambahan+"'","kredit=kredit+'"+(ttlTambahan)+"'","kd_rek='"+akunbillingralan.getTambahan_Ralan()+"'")==false){
                                sukses=false;
                            }    
                        }

                        if(ttlOperasi>0){
                            if(Sequel.menyimpantf("tampjurnal","'"+akunbillingralan.getOperasi_Ralan()+"','Operasi Ralan','0','"+ttlOperasi+"'","kredit=kredit+'"+(ttlOperasi)+"'","kd_rek='"+akunbillingralan.getOperasi_Ralan()+"'")==false){
                                sukses=false;
                            }    
                        }

                        alamat=Sequel.cariIsi("select reg_periksa.almt_pj from reg_periksa where reg_periksa.no_rawat=? ",TNoRw.getText());

                        if(piutang>0){
                            if(sukses==true){
                                sukses=jur.simpanJurnal(TNoRw.getText(),"U","PIUTANG PASIEN RAWAT JALAN "+TNoRw.getText()+" "+TNoRM.getText()+" "+TPasien.getText()+", DIPOSTING OLEH "+akses.getkode());
                            }
                            if(bayar>0){
                                if(Sequel.menyimpantf2("tagihan_sadewa","'"+TNoRw.getText()+"','"+TNoRM.getText()+"','"+TPasien.getText().replaceAll("'","")+"','"+alamat.replaceAll("'","")+"','"+Valid.SetTgl(DTPTgl.getSelectedItem()+"")+" "+DTPTgl.getSelectedItem().toString().substring(11,19)+"','Uang Muka','"+total+"','"+bayar+"','Belum','"+akses.getkode()+"'","No.Rawat")==false){
                                    sukses=false;
                                }
                            }
                            if(Sequel.queryutf2("insert into piutang_pasien values ('"+TNoRw.getText()+"','"+Valid.SetTgl(DTPTgl.getSelectedItem()+"")+"','"+
                                    TNoRM.getText()+"','Belum Lunas','"+total+"','"+bayar+"','"+piutang+"','"+Valid.SetTgl(DTPTgl.getSelectedItem()+"")+"')")==false){
                                sukses=false;
                            }
                        }else if(piutang<=0){
                            if(sukses==true){
                                sukses=jur.simpanJurnal(TNoRw.getText(),"U","PEMBAYARAN PASIEN RAWAT JALAN "+TNoRw.getText()+" "+TNoRM.getText()+" "+TPasien.getText()+", DIPOSTING OLEH "+akses.getkode());
                            }
                            if(Sequel.menyimpantf2("tagihan_sadewa","'"+TNoRw.getText()+"','"+TNoRM.getText()+"','"+TPasien.getText().replaceAll("'","")+"','"+alamat.replaceAll("'","")+"','"+Valid.SetTgl(DTPTgl.getSelectedItem()+"")+" "+DTPTgl.getSelectedItem().toString().substring(11,19)+"','Pelunasan','"+total+"','"+total+"','Sudah','"+akses.getkode()+"'","No.Rawat")==false){
                                sukses=false;
                            }
                        }
                    }
                }
                    
                if(sukses==true){
                    Valid.editTable(tabModeRwJlDr,"reg_periksa","no_rawat",TNoRw,"status_bayar='Sudah Bayar'");
                    Sequel.meghapus("temporary_tambahan_potongan","no_rawat",TNoRw.getText());
                    Sequel.Commit();
                    JOptionPane.showMessageDialog(null,"Proses simpan selesai...!");   
                     if(notaralan.equals("Yes")){
                        BtnNotaActionPerformed(null);
                    }
                }else{
                    JOptionPane.showMessageDialog(null,"Terjadi kesalahan saat pemrosesan data, transaksi dibatalkan.\nPeriksa kembali data sebelum melanjutkan menyimpan..!!");
                    Sequel.RollBack();
                }
                Sequel.AutoComitTrue();
                
                if (sukses == true) {
                    Sequel.meghapus("temporary_tambahan_potongan","no_rawat",TNoRw.getText());
                    JOptionPane.showMessageDialog(null,"Proses simpan selesai...!"); 
                    if (!TNoSEP.getText().equals("")) {
//                        Sequel.mengedit("bridging_eklaim", " no_sep='"+TNoSEP.getText()+"'", " status='1'");
                        eklaimApi.bridgingInit(TNoSEP.getText());
                    }
                    if (pengaturanbillingralan.getCetakNotaSimpanRalan().equals("Yes")) {
                        this.dispose();
                    }
                }
            }catch (Exception ex) {
                System.out.println("Notifikasi : "+ex);            
                JOptionPane.showMessageDialog(null,"Maaf, gagal menyimpan data. Data yang sama dimasukkan sebelumnya...!");
            }
        }
    }
    
    private void setHakNaikKelas() {
        String noRawat = TNoRw.getText();
        String naikKelas = Sequel.cariIsi("select klsnaik from bridging_sep where no_rawat = ?", noRawat);
        String hakKelas =   Sequel.cariIsi("select klsrawat from bridging_sep where no_rawat = ?", noRawat);
        Sequel.cariIsi("select no_sep from bridging_sep where no_rawat = ?", TNoSEP, noRawat);

        switch (naikKelas) {
            case "8":
                TNaikKelas.setText("Kelas VIP/VVIP");
                break;
            case "3":
                TNaikKelas.setText("Kelas 1");
                break;
            default:
                TNaikKelas.setText("-");
                break;
        }
        
        switch (hakKelas) {
            case "1":
                THakKelas.setText("Kelas 1");
                break;
            case "2":
            

            case "3":
                THakKelas.setText("Kelas 3");
                break;
            default: 
                THakKelas.setText("-");
                break;
        }
    }
    
     public boolean checkMismatch() {
        String noRawat = TNoRw.getText().trim();
        CheckPenjabMissmatch penjabChecker = new CheckPenjabMissmatch();
        List<CheckPenjabMissmatch.PenjabMismatch> mismatches = penjabChecker.checkMismatchByNoRawatRalan(noRawat);
        
        if (mismatches.size() >= 1) {
            StringBuilder pesan = new StringBuilder();
         

            pesan.append("Detail Mismatch:\n");
            pesan.append("=".repeat(50)).append("\n");
            
            int counter = 1;
            for (CheckPenjabMissmatch.PenjabMismatch mismatch : mismatches) {
                pesan.append(counter).append(". ").append(mismatch.getJenisTindakan()).append("-").append(mismatch.getNmPerawatan()).append(" (").append(mismatch.getKdJenisPrw()).append(")").append("\n");
                counter++;
            }
            pesan.append("Silahkan ganti tindakan tersebut terlebih dahulu !!!");
            
            JOptionPane.showMessageDialog(null, pesan.toString(), "Konfirmasi Mismatch Penjab", JOptionPane.WARNING_MESSAGE);
            
            return false; // Ada mismatch, return false
        }
     

    
    
}


    