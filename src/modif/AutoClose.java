package modif;

import java.util.*;
import javax.swing.*;

public class AutoClose {

    private static int lastShownMinute = -1; // simpan menit terakhir yg sudah ditampilkan

    public static void startAutoClose() {
        java.util.Timer timer = new java.util.Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Calendar now = Calendar.getInstance();
                int hour = now.get(Calendar.HOUR_OF_DAY);
                int minute = now.get(Calendar.MINUTE);

                // hanya jalankan jika menit berubah (supaya tidak spam setiap detik)
                if (minute != lastShownMinute) {
                    if (hour == 23 && minute == 00) {
                        showWarning("⚠️ Aplikasi akan otomatis ditutup 60 Menit lagi pada pukul 23:59.\nSilahkan segera simpan pekerjaan Anda.");
                    }else if (hour == 23 && minute == 30) {
                        showWarning("⚠️ Aplikasi akan otomatis ditutup 30 Menit lagi pada pukul 23:59.\nSilahkan segera simpan pekerjaan Anda.");
                    } else if (hour == 23 && minute == 45) {
                        showWarning("⚠️ Aplikasi akan otomatis ditutup 15 Menit lagi pada pukul 23:59.\nSilahkan segera simpan pekerjaan Anda.");
                    } else if (hour == 23 && minute == 55) {
                        showWarning("⚠️ Aplikasi akan otomatis ditutup 5 Menit lagi pada pukul 23:59.\nSilahkan simpan pekerjaan Anda sekarang.");
                    } else if (hour == 23 && minute == 59) {
//                        showWarning("⚠️ Aplikasi ditutup.\nSilahkan jalankan ulang Aplikasi SIMRS Khanza.");
                        System.exit(0);
                    }

                    // update menit terakhir supaya tidak muncul berkali-kali
                    lastShownMinute = minute;
                }
            }
        }, 0, 1000); // masih cek setiap detik, tapi popup cuma sekali per menit
    }

    private static void showWarning(String message) {
        SwingUtilities.invokeLater(() -> {
            JLabel label = new JLabel("<html><body style='width:300px'>" + message.replace("\n", "<br>") + "</body></html>");
            label.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18)); // ukuran pesan
            JOptionPane.showMessageDialog(
                    null,
                    label,
                    "⚠️ Peringatan",
                    JOptionPane.WARNING_MESSAGE
            );
        });
    }

}
