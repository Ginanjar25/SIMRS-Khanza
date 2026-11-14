package modif;

import fungsi.akses;
import fungsi.sekuel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

/**
 * Riwayat penghapusan nota salah pada billing ranap dan ralan (tabel riwayat_buka_billing).
 */
public class RiwayatBukaBilling {

    private static final sekuel Sequel = new sekuel();

    /** Form isian alasan hapus nota salah, mengikuti desain WindowDiagnosaMasuk di DlgKamarInap. Mengembalikan null jika dibatalkan. */
    public static String tanyaAlasan(Component parent) {
        final JDialog windowAlasan = new JDialog((Frame) null, true);
        windowAlasan.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        windowAlasan.setUndecorated(true);
        windowAlasan.setResizable(false);
        windowAlasan.setSize(630, 155);

        final String[] hasil = {null};

        widget.InternalFrame panelAlasan = new widget.InternalFrame();
        panelAlasan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(240, 245, 235)), "::[ Hapus Nota Salah ]::", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        panelAlasan.setLayout(null);

        widget.Label lblAlasan = new widget.Label();
        lblAlasan.setText("Alasan :");
        panelAlasan.add(lblAlasan);
        lblAlasan.setBounds(0, 32, 95, 23);

        final widget.TextArea txtAlasan = new widget.TextArea();
        txtAlasan.setColumns(20);
        txtAlasan.setRows(3);
        widget.ScrollPane scrollAlasan = new widget.ScrollPane();
        scrollAlasan.setOpaque(true);
        scrollAlasan.setViewportView(txtAlasan);
        panelAlasan.add(scrollAlasan);
        scrollAlasan.setBounds(99, 32, 511, 70);

        widget.Button BtnSimpanAlasan = new widget.Button();
        BtnSimpanAlasan.setIcon(new ImageIcon(RiwayatBukaBilling.class.getResource("/picture/save-16x16.png")));
        BtnSimpanAlasan.setMnemonic('S');
        BtnSimpanAlasan.setText("Simpan");
        BtnSimpanAlasan.setToolTipText("Alt+S");
        BtnSimpanAlasan.addActionListener(e -> {
            String alasan = txtAlasan.getText().trim();
            if (alasan.isEmpty()) {
                JOptionPane.showMessageDialog(windowAlasan, "Alasan tidak boleh kosong..!!");
            } else if (alasan.length() > 255) {
                JOptionPane.showMessageDialog(windowAlasan, "Alasan maksimal 255 karakter..!!");
            } else {
                hasil[0] = alasan;
                windowAlasan.dispose();
            }
        });
        panelAlasan.add(BtnSimpanAlasan);
        BtnSimpanAlasan.setBounds(405, 110, 100, 30);

        widget.Button BtnBatalAlasan = new widget.Button();
        BtnBatalAlasan.setIcon(new ImageIcon(RiwayatBukaBilling.class.getResource("/picture/cross.png")));
        BtnBatalAlasan.setMnemonic('B');
        BtnBatalAlasan.setText("Batal");
        BtnBatalAlasan.setToolTipText("Alt+B");
        BtnBatalAlasan.addActionListener(e -> windowAlasan.dispose());
        panelAlasan.add(BtnBatalAlasan);
        BtnBatalAlasan.setBounds(510, 110, 100, 30);

        windowAlasan.getContentPane().add(panelAlasan, BorderLayout.CENTER);
        windowAlasan.setLocationRelativeTo(parent);
        windowAlasan.setVisible(true);
        return hasil[0];
    }

    /** Menyimpan riwayat hapus nota salah. Kolom nip berisi akses.getkode(), yaitu "Admin Utama" jika login sebagai admin utama. */
    public static boolean simpan(String norawat, String norm, String alasan) {
        return Sequel.menyimpantf2("riwayat_buka_billing (norawat,norm,alasan,asal,nip)", "?,?,?,?,?", 5,
                new String[]{norawat, norm, alasan, "manual", akses.getkode()});
    }
}
