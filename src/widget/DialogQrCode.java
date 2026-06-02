package widget;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class DialogQrCode {
    public static void show(Component parent, String data, String detail) {
        // 1. Generate QR Code
        BufferedImage qrImage = null;
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 350, 350);
            qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        } catch (WriterException e) {
            JOptionPane.showMessageDialog(parent, "Gagal generate QR: " + e.getMessage());
            return;
        }

        // 2. Setup Panel Utama
        JPanel panelUtama = new JPanel(new BorderLayout(10, 10));
        panelUtama.setPreferredSize(new Dimension(500, 500));
        panelUtama.setBackground(Color.WHITE);
        panelUtama.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Label Gambar QR
        JLabel lblQr = new JLabel(new ImageIcon(qrImage));
        panelUtama.add(lblQr, BorderLayout.NORTH);

        // Panel Info (Teks)
        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setBackground(Color.WHITE);

        JLabel lblInfo = new JLabel("Pemeriksaan Radiologi:");
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Gunakan JTextPane untuk Align Center
        JTextPane txtData = new JTextPane();
        txtData.setText(detail);
        txtData.setEditable(false);
        txtData.setBackground(new Color(245, 245, 245));
        txtData.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        
        // Pengaturan Rata Tengah pada JTextPane
        StyledDocument doc = txtData.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        panelInfo.add(lblInfo);
        panelInfo.add(Box.createVerticalStrut(8));
        panelInfo.add(txtData);
        panelUtama.add(panelInfo, BorderLayout.CENTER);

        // 3. Tombol Keluar (Merah)
        JButton btnKeluar = new JButton("Tutup");
        btnKeluar.setBackground(new Color(220, 53, 69)); // Merah
        btnKeluar.setForeground(Color.WHITE);
        btnKeluar.setFocusPainted(false);
        btnKeluar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelTombol.setBackground(Color.WHITE);
        panelTombol.add(btnKeluar);
        panelUtama.add(panelTombol, BorderLayout.SOUTH);

        // 4. Show Dialog
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), "QR Code Radiologi", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.getContentPane().add(panelUtama);
        dialog.setResizable(false);
        
        btnKeluar.addActionListener(e -> dialog.dispose());
        
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}