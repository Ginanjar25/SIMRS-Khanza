package widget;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractButton;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

/**
 * Menampilkan konfirmasi sebelum menjalankan aksi hapus pada seluruh form,
 * tanpa perlu mengubah form satu per satu. Tombol/menu dikenali dari namanya
 * (BtnHapus..., ppHapus..., MnHapus...), lalu listener bawaannya dibungkus
 * sehingga baru dijalankan jika user memilih "Yes".
 *
 * Dipasang otomatis dari static initializer widget.Button.
 */
public final class KonfirmasiHapus {
    private static final String TERPASANG="KonfirmasiHapus.terpasang";
    private static final long JEDA_KEPUTUSAN=500;
    private static boolean aktif=false;

    // Aksi yang sudah punya konfirmasi sendiri / bukan hapus data di database
    private static final Set<String> KECUALI=new HashSet<>(Arrays.asList(
        "BPJSDataSEP.BtnHapus","BPJSSuratKontrol.BtnHapus","DlgBookingPeriksa.BtnHapus",
        "DlgCariPemesanan.ppHapus","DlgCariPeriksaLab.BtnHapus","DlgCariPeriksaLabMB.BtnHapus",
        "DlgCariPeriksaLabPA.BtnHapus","DlgCariPeriksaRadiologi.BtnHapus","DlgCariPermintaanLab.BtnHapus",
        "DlgCariPermintaanLabMB.BtnHapus","DlgCariPermintaanLabPA.BtnHapus",
        "DlgCariTagihanOperasi.MnHapusObatOperasi","DlgCariTagihanOperasi.MnHapusTagihanOperasi",
        "DlgIGD.BtnHapus","DlgIGD.BtnHapusDiagnosa","DlgKamarInap.MnHapusDataSalah",
        "DlgPeresepanDokter.ppHapusObat","DlgPeresepanDokter.ppHapusRacikan",
        "DlgPermintaanFotoBayi.BtnHapus","DlgPermintaanRanap.BtnHapus","DlgRanapGabung.BtnHapus",
        "DlgReg.BtnHapus","UTDCariPenyerahanDarah.ppHapus","UTDCariPenyerahanDarah.ppHapusBHPMedis",
        "UTDCariPenyerahanDarah.ppHapusBHPMedisDanNonMedis","UTDCariPenyerahanDarah.ppHapusBHPNonMedis",
        "UTDDonor.ppHapusBHPMedis","UTDDonor.ppHapusBHPMedisDanNonMedis","UTDDonor.ppHapusBHPNonMedis",
        "UTDPemisahanDarah.ppHapusBHPMedis","UTDPemisahanDarah.ppHapusBHPMedisDanNonMedis",
        "UTDPemisahanDarah.ppHapusBHPNonMedis","UTDPemisahanDarah.ppHapusPemisahan",
        "frmPengaduan.BtnHapusTotal",
        "DlgCariObat.BtnHapus","DlgCariObat2.BtnHapus","DlgCatatan.BtnHapusCatatanReg",
        "DlgPenjualan.BtnHapus","DlgPeresepanDokter.BtnHapus","DlgPeriksaLaboratorium.BtnHapus",
        "DlgPeriksaLaboratoriumMB.BtnHapus","DlgPeriksaLaboratoriumPA.BtnHapus",
        "DlgPermintaanLaboratorium.BtnHapus","DlgRanapGabung.BtnHapusFieldBayi2",
        "DlgRanapGabung.BtnHapusFieldBayi3","DlgUbahPeriksaLab.BtnHapus","InventoryResepLuar.BtnHapus",
        "MasterTemplatePemeriksaanDokter.BtnHapus1","RMRekonsiliasiObat.BtnHapus"
    ));

    // Tombol keyboard pemicu hapus pada handler KeyPressed (default: spasi)
    private static final Map<String,Integer> TOMBOL_KHUSUS=new HashMap<>();
    static {
        TOMBOL_KHUSUS.put("ApotekBPJSDaftarPelayananObat.BtnHapus",KeyEvent.VK_PAGE_DOWN);
        TOMBOL_KHUSUS.put("DlgUser.BtnHapus",KeyEvent.VK_PAGE_DOWN);
        TOMBOL_KHUSUS.put("DlgReturPiutang.BtnHapus",KeyEvent.VK_ENTER);
        TOMBOL_KHUSUS.put("DlgBulanan.BtnHapus",KeyEvent.VK_ENTER);
        TOMBOL_KHUSUS.put("DlgHarian.BtnHapus",KeyEvent.VK_ENTER);
        TOMBOL_KHUSUS.put("DlgTemporaryPresensi.BtnHapus",KeyEvent.VK_ENTER);
        TOMBOL_KHUSUS.put("DlgRunTeks.BtnHapus",KeyEvent.VK_ENTER);
        TOMBOL_KHUSUS.put("DlgRunTeksApotek.BtnHapus",KeyEvent.VK_ENTER);
    }

    private KonfirmasiHapus(){}

    public static synchronized void pasang(){
        if(aktif){
            return;
        }
        aktif=true;
        Toolkit.getDefaultToolkit().addAWTEventListener(e->{
            if(e instanceof HierarchyEvent){
                HierarchyEvent he=(HierarchyEvent)e;
                if((he.getChangeFlags()&HierarchyEvent.SHOWING_CHANGED)!=0&&he.getComponent().isShowing()){
                    periksa(he.getComponent());
                }
            }else if(e.getID()==MouseEvent.MOUSE_ENTERED||e.getID()==MouseEvent.MOUSE_PRESSED||e.getID()==FocusEvent.FOCUS_GAINED){
                if(e.getSource() instanceof Component){
                    periksa((Component)e.getSource());
                }
            }
        },AWTEvent.HIERARCHY_EVENT_MASK|AWTEvent.MOUSE_EVENT_MASK|AWTEvent.FOCUS_EVENT_MASK);
    }

    private static void periksa(Component c){
        if(!(c instanceof AbstractButton)){
            return;
        }
        AbstractButton b=(AbstractButton)c;
        if(b.getClientProperty(TERPASANG)!=null){
            return;
        }
        b.putClientProperty(TERPASANG,Boolean.TRUE);
        try {
            String id=identitas(b);
            if(id==null||KECUALI.contains(id)){
                return;
            }
            bungkus(b,TOMBOL_KHUSUS.getOrDefault(id,KeyEvent.VK_SPACE));
        } catch (Exception ex) {
            System.out.println("Notifikasi : "+ex);
        }
    }

    // Menghasilkan "NamaForm.namaKomponen" untuk tombol/menu hapus, atau null jika bukan
    private static String identitas(AbstractButton b){
        String nama=b.getName();
        if(nama!=null){
            if(!isNamaHapus(nama)){
                return null;
            }
            ActionListener[] ls=b.getActionListeners();
            for(ActionListener l:ls){
                if(l!=b.getAction()){
                    return kelasForm(l.getClass())+"."+nama;
                }
            }
            return null;
        }
        // Komponen tanpa setName: cari nama field-nya di form induk
        String teks=b.getText();
        if(teks==null||!teks.toLowerCase().contains("hapus")){
            return null;
        }
        Component induk=b;
        while(induk!=null){
            for(Class<?> k=induk.getClass();k!=null&&!k.getName().startsWith("java");k=k.getSuperclass()){
                for(Field f:k.getDeclaredFields()){
                    if(AbstractButton.class.isAssignableFrom(f.getType())){
                        try {
                            f.setAccessible(true);
                            if(f.get(induk)==b){
                                return isNamaHapus(f.getName())?k.getSimpleName()+"."+f.getName():null;
                            }
                        } catch (Exception ex) {
                        }
                    }
                }
            }
            induk=(induk instanceof JPopupMenu)?((JPopupMenu)induk).getInvoker():induk.getParent();
        }
        return null;
    }

    private static boolean isNamaHapus(String nama){
        return nama.startsWith("BtnHapus")||nama.startsWith("ppHapus")||nama.startsWith("MnHapus");
    }

    private static String kelasForm(Class<?> k){
        String n=k.getName();
        if(n.indexOf('$')>0){
            n=n.substring(0,n.indexOf('$'));
        }
        return n.substring(n.lastIndexOf('.')+1);
    }

    private static void bungkus(AbstractButton b,int tombolPemicu){
        final long[] waktu={0};
        final boolean[] setuju={false};
        final Runnable[] tanya=new Runnable[1];
        tanya[0]=()->{
            // Satu aksi bisa memicu KeyPressed lalu ActionPerformed; pakai keputusan yang sama
            if(System.currentTimeMillis()-waktu[0]>JEDA_KEPUTUSAN){
                setuju[0]=JOptionPane.showConfirmDialog(
                    KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow(),
                    "Apakah Anda yakin ingin menghapus data ini...??","Konfirmasi Hapus",
                    JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION;
                waktu[0]=System.currentTimeMillis();
            }
        };

        final ActionListener[] asli=b.getActionListeners();
        boolean adaListener=false;
        for(ActionListener l:asli){
            if(l!=b.getAction()){
                b.removeActionListener(l);
                adaListener=true;
            }
        }
        if(adaListener){
            b.addActionListener(evt->{
                tanya[0].run();
                if(setuju[0]){
                    for(ActionListener l:asli){
                        if(l!=b.getAction()){
                            l.actionPerformed(evt);
                        }
                    }
                }
            });
        }

        final KeyListener[] asliKey=b.getKeyListeners();
        if(asliKey.length>0){
            for(KeyListener l:asliKey){
                b.removeKeyListener(l);
            }
            b.addKeyListener(new KeyListener(){
                @Override
                public void keyPressed(KeyEvent evt){
                    if(evt.getKeyCode()==tombolPemicu){
                        tanya[0].run();
                        if(!setuju[0]){
                            evt.consume();
                            return;
                        }
                    }
                    for(KeyListener l:asliKey){
                        l.keyPressed(evt);
                    }
                }
                @Override
                public void keyReleased(KeyEvent evt){
                    for(KeyListener l:asliKey){
                        l.keyReleased(evt);
                    }
                }
                @Override
                public void keyTyped(KeyEvent evt){
                    for(KeyListener l:asliKey){
                        l.keyTyped(evt);
                    }
                }
            });
        }
    }
}
