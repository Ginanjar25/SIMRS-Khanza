package modif;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class GenerateQR {

    public static BufferedImage generateQR(String text, int width, int height) throws Exception {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix matrix = new MultiFormatWriter().encode(
                text,
                BarcodeFormat.QR_CODE,
                width,
                height,
                hints
        );

        return MatrixToImageWriter.toBufferedImage(matrix);
    }

    public static String generateBase64(String text, int width, int height) {
        try {
            BufferedImage image = generateQR(text, width, height);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);

            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    
    public static String generateFile(String text, int width, int height) {
        try {
            BufferedImage image = generateQR(text, width, height);

            // Pastikan folder cache/qr ada
            File folder = new File("cache/qr");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // Nama file berdasarkan hash isi QR
            String namaFile = Integer.toHexString(text.hashCode()) + ".png";
            File file = new File(folder, namaFile);

            // Jika belum ada, baru generate
            if (!file.exists()) {
                ImageIO.write(image, "png", file);
            }

            return file.toURI().toURL().toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    
    public static void clearCache() {
        try {
            File folder = new File("cache/qr");

            if (folder.exists()) {
                File[] files = folder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile()) {
                            file.delete();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}