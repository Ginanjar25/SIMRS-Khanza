/*
 * Dialog SATUSEHAT Rekam Medis Elektronik (SSRME) untuk Rawat Jalan, Rawat Inap & IGD.
 * Seluruh logika ada di modif.PanelSSRME (dipakai juga di tab "RME SATUSEHAT" Riwayat Perawatan).
 */

package modif;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;

public final class SatuSehatRekamMedisElektronik extends javax.swing.JDialog {
    private final widget.InternalFrame internalFrame1=new widget.InternalFrame();
    private final PanelSSRME panelSSRME=new PanelSSRME();
    private final widget.Button btnKeluar=new widget.Button();

    public SatuSehatRekamMedisElektronik(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(240,245,235)),
                "::[ SATUSEHAT Rekam Medis Elektronik (SSRME) ]::",javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,new java.awt.Font("Tahoma",0,11),new Color(50,50,50)));
        internalFrame1.setLayout(new BorderLayout(1,1));
        internalFrame1.add(panelSSRME,BorderLayout.CENTER);

        btnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        btnKeluar.setText("Keluar");
        btnKeluar.setMnemonic('K');
        btnKeluar.setToolTipText("Alt+K");
        btnKeluar.setPreferredSize(new Dimension(100,30));
        btnKeluar.addActionListener(e->dispose());
        panelSSRME.tambahTombol(btnKeluar);

        getContentPane().add(internalFrame1,BorderLayout.CENTER);
        setSize(1000,680);
    }

    /**
     * @param norawat  no_rawat aktif (untuk menentukan DPJP bila user login bukan dokter), boleh kosong
     * @param norm     no_rkm_medis pasien
     * @param kddokter kode dokter pilihan form pemanggil (boleh kosong). Prioritas: user login dokter -> kddokter -> DPJP registrasi
     */
    public void setPasien(String norawat,String norm,String kddokter){
        panelSSRME.setPasien(norawat,norm,kddokter);
    }

    @Override
    public void dispose(){
        panelSSRME.tutup();
        super.dispose();
    }
}
