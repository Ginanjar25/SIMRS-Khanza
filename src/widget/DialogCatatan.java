/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package widget;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author IT
 */
public class DialogCatatan {
      public static void show(Component parent, String catatanPasien, String catatanReg) {

        boolean adaPasien = catatanPasien != null && !catatanPasien.trim().isEmpty();
        boolean adaReg    = catatanReg != null && !catatanReg.trim().isEmpty();

        if (!adaPasien && !adaReg) {
            JOptionPane.showMessageDialog(parent, "Tidak ada catatan", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JPanel panelUtama = new JPanel();
        panelUtama.setLayout(new BoxLayout(panelUtama, BoxLayout.Y_AXIS));
        panelUtama.setBackground(Color.WHITE);

        // ===== FUNCTION SECTION =====
        java.util.function.BiFunction<String, String, JPanel> createSection = (title, content) -> {

            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(new Color(245, 245, 245));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));

            JTextArea txt = new JTextArea(content);
            txt.setLineWrap(true);
            txt.setWrapStyleWord(true);
            txt.setEditable(false);
            txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            txt.setBackground(new Color(250, 250, 250));
            txt.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

            // 🔥 AUTO HEIGHT BERDASARKAN ISI
            txt.setSize(400, Short.MAX_VALUE);
            int height = txt.getPreferredSize().height;

            // Batasi biar tidak terlalu tinggi
            int maxHeight = 200;
            int finalHeight = Math.min(height, maxHeight);

            JScrollPane scroll = new JScrollPane(txt);
            scroll.setPreferredSize(new Dimension(400, finalHeight));
            scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

            panel.add(lblTitle, BorderLayout.NORTH);
            panel.add(scroll, BorderLayout.CENTER);

            return panel;
        };

        if (adaPasien) {
            panelUtama.add(createSection.apply("Catatan Pasien", catatanPasien));
        }

        if (adaPasien && adaReg) {
            panelUtama.add(Box.createVerticalStrut(10));
        }

        if (adaReg) {
            panelUtama.add(createSection.apply("Catatan Registrasi", catatanReg));
        }

        // ===== DIALOG CUSTOM (AUTO RESIZE) =====
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), "Informasi Catatan", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        dialog.getContentPane().add(panelUtama);
        dialog.pack(); // 🔥 AUTO SIZE SESUAI ISI
        dialog.setLocationRelativeTo(parent);

        // 🔥 BATASI MAX SIZE BIAR GA KEKECIL / KEBESARAN
        dialog.setMaximumSize(new Dimension(500, 500));

        dialog.setVisible(true);
    }
}
