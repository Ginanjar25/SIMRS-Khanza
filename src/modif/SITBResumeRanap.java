package modif;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.sekuel;
import java.awt.Cursor;
import java.awt.Window;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

/**
 * Sinkronisasi No. Reg SITB (person_id) ke resume pasien ranap.
 * Alur: NIK pasien -> cek tb_register -> cek API SITB (sama: biarkan, belum ada: insert, beda: update)
 * -> tulis "No. Reg SITB: xxx" di baris paling atas hasil laborat.
 * Semua query DB dan hit API dijalankan di background agar form tidak tertahan.
 */
public class SITBResumeRanap {

    private static final String URL_VALIDASI_NIK = "https://sulawesi.sitb.id/sitb2024/person/validasi_nik";

    // Thread daemon agar tidak menahan aplikasi saat ditutup
    private static final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "SITB-Resume-Ranap");
        t.setDaemon(true);
        return t;
    });

    // Form yang prosesnya sedang berjalan, agar tidak dobel per form
    private static final Set<Window> sedangProses = ConcurrentHashMap.newKeySet();

    // Instance terpisah agar ps/rs tidak bentrok dengan sekuel milik form di EDT
    private static final sekuel Sequel = new sekuel();

    /**
     * Dipanggil dari EDT (setNoRm / setNoRm2 di RMDataResumePasienRanap).
     */
    public static void autoInsert(Window owner, String noRM, JTextComponent TNoRw, JTextComponent HasilLaborat) {
        final String rm = noRM == null ? "" : noRM.trim();
        final String noRawat = TNoRw.getText().trim();
        if (rm.isEmpty()) {
            return; // Keluar jika No. RM kosong
        }

        runBackground(owner, () -> proses(owner, rm, noRawat, TNoRw, HasilLaborat));
    }

    private static void runBackground(Window owner, Runnable task) {
        if (!owner.isDisplayable()) return;
        if (!sedangProses.add(owner)) return;

        owner.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            executor.submit(() -> {
                try {
                    task.run();
                } catch (Exception e) {
                    System.out.println("Notif SITB : " + e);
                } finally {
                    sedangProses.remove(owner);
                    SwingUtilities.invokeLater(() -> {
                        if (owner.isDisplayable()) {
                            owner.setCursor(Cursor.getDefaultCursor());
                        }
                    });
                }
            });
        } catch (RejectedExecutionException ex) {
            sedangProses.remove(owner);
            owner.setCursor(Cursor.getDefaultCursor());
        }
    }

    private static void proses(Window owner, String noRM, String noRawat, JTextComponent TNoRw, JTextComponent HasilLaborat) {
        // 1. Ambil NIK pasien berdasarkan No. RM
        String nik = Sequel.cariIsi("SELECT IFNULL(no_ktp,'') FROM pasien WHERE no_rkm_medis = ?", noRM).trim();
        if (nik.isEmpty()) {
            return;
        }

        // 2. Cari nomor registrasi SITB di tabel tb_register
        String noregDB = Sequel.cariIsi("SELECT IFNULL(no_reg_tb,'') FROM side_db.tb_register WHERE nik = ?", nik).trim();

        // 3. Cek ke API SITB, lalu sinkronkan dengan tb_register
        //    sama -> biarkan, belum ada -> insert, beda -> update
        String noregSITB = noregDB;
        String noregAPI = cekPersonId(nik);
        if (!noregAPI.isEmpty()) {
            if (noregDB.isEmpty()) {
                Sequel.queryu2tf("INSERT INTO side_db.tb_register (nik, no_reg_tb) VALUES (?, ?)", 2, new String[]{
                    nik, noregAPI
                });
            } else if (!noregDB.equals(noregAPI)) {
                Sequel.queryu2tf("UPDATE side_db.tb_register SET no_reg_tb=? WHERE nik=?", 2, new String[]{
                    noregAPI, nik
                });
            }
            noregSITB = noregAPI;
        }

        // Jika nomor SITB tidak ditemukan di tb_register maupun API, hentikan proses
        if (noregSITB.isEmpty()) {
            return;
        }

        final String noregFinal = noregSITB;
        final String labelSITB = "No. Reg SITB: " + noregSITB;
        boolean isResumeBaru = noRawat.isEmpty() || !isDataSudahTersimpan(noRawat);

        if (isResumeBaru) {
            // Kondisi 1: Resume baru (belum tersimpan) -> Taruh di paling atas
            // Komponen Swing hanya boleh diubah dari EDT
            SwingUtilities.invokeLater(() -> {
                // Lewati jika form sudah ditutup / sudah pindah ke pasien lain
                if (!owner.isDisplayable() || !TNoRw.getText().trim().equals(noRawat)) {
                    return;
                }
                String textSaatIni = HasilLaborat.getText().trim();
                if (textSaatIni.isEmpty()) {
                    HasilLaborat.setText(labelSITB + "\n");
                } else if (!textSaatIni.contains(noregFinal)) {
                    HasilLaborat.setText(labelSITB + "\n" + textSaatIni);
                }
            });
        } else {
            // Kondisi 2: Resume sudah ada / sudah tersimpan di database -> Update ke posisi paling atas
            String hasilLabDb = getHasilLaboratFromDatabase(noRawat);

            boolean perluUpdate = false;
            String teksBaru = "";

            if (hasilLabDb == null || hasilLabDb.trim().isEmpty()) {
                teksBaru = labelSITB;
                perluUpdate = true;
            } else {
                // Bersihkan teks lama dari format SITB manapun agar tidak terjadi duplikat ganda
                String bersihText = hasilLabDb.replaceAll("(?m)^No\\. Reg SITB:.*$", "").trim();

                // Cek apakah posisi saat ini belum di paling atas atau belum ada sama sekali
                if (!hasilLabDb.startsWith(labelSITB)) {
                    if (bersihText.isEmpty()) {
                        teksBaru = labelSITB;
                    } else {
                        teksBaru = labelSITB + "\n" + bersihText;
                    }
                    perluUpdate = true;
                }
            }

            if (perluUpdate) {
                // Update langsung ke database resume_pasien_ranap
                Sequel.queryu2tf("UPDATE resume_pasien_ranap SET hasil_laborat=? WHERE no_rawat=?", 2, new String[]{
                    teksBaru, noRawat
                });
            }
        }
    }

    // Mengambil person_id (No. Reg TB) dari API SITB berdasarkan NIK.
    // Mengembalikan string kosong jika tidak ditemukan atau API gagal diakses.
    private static String cekPersonId(String nik) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(URL_VALIDASI_NIK).openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            OutputStream os = conn.getOutputStream();
            try {
                os.write(("nik=" + URLEncoder.encode(nik, "UTF-8")).getBytes("UTF-8"));
            } finally {
                os.close();
            }

            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            try {
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            } finally {
                br.close();
            }
            String respon = sb.toString();
            System.out.println("Respon SITB : " + respon);

            JsonNode root = new ObjectMapper().readTree(respon);
            if (root.path("error").asBoolean(false)) {
                return "";
            }
            JsonNode personId = root.path("person_id");
            if (personId.isMissingNode() || personId.isNull()) {
                personId = root.path("data").path("person_id");
            }
            if (personId.isMissingNode() || personId.isNull()) {
                return "";
            }
            return personId.asText().trim();
        } catch (Exception e) {
            System.out.println("Notif SITB : " + e);
            return "";
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    // Mengecek apakah no_rawat sudah tersimpan di resume
    private static boolean isDataSudahTersimpan(String noRawat) {
        return Sequel.cariInteger("SELECT count(no_rawat) FROM resume_pasien_ranap WHERE no_rawat=?", noRawat) > 0;
    }

    // Mengambil nilai asli hasil_laborat dari database
    private static String getHasilLaboratFromDatabase(String noRawat) {
        return Sequel.cariIsi("SELECT hasil_laborat FROM resume_pasien_ranap WHERE no_rawat=?", noRawat);
    }
}
