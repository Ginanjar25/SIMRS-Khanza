package fungsi;

import java.io.BufferedReader;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Properties;
import javax.swing.JOptionPane;

public class RedirectSystemOutToDatabase {

    private static Properties prop = new Properties();
    private String LOGPATHNAS = koneksiDB.AKTIFKANTRACKSQL();
    private static int interval = 0;
    

    public static void redirectSystemOut() {
        System.out.println("Global Error Handler, System.out aktif .");
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            StringWriter stringWriter = new StringWriter();
            throwable.printStackTrace(new PrintWriter(stringWriter));
            String errorLog = stringWriter.toString();
            String cleanedLog = errorLog.replace("\n", "").replace("\r", "");
            System.out.println("Error Global : " + cleanedLog);
        });
        PrintStream combinedPrintStream = new PrintStream(
                new CombinedOutputStream(System.out, new DatabaseOutputStream())
        );
        System.setOut(combinedPrintStream);
    }

    // Custom OutputStream untuk menggabungkan ke console, database, dan file log
    private static class CombinedOutputStream extends OutputStream {

        private final OutputStream consoleStream;
        private final OutputStream databaseStream;

        public CombinedOutputStream(OutputStream consoleStream, OutputStream databaseStream) {
            this.consoleStream = consoleStream;
            this.databaseStream = databaseStream;
        }

        @Override
        public void write(int b) {
            try {
                consoleStream.write(b);  // Tulis ke console
                databaseStream.write(b); // Tulis ke database
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void flush() {
            try {
                consoleStream.flush();
                databaseStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void close() {
            try {
                consoleStream.close();
                databaseStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // OutputStream untuk menyimpan log ke database
    private static class DatabaseOutputStream extends OutputStream {

        private StringBuilder buffer = new StringBuilder();

        @Override
        public void write(int b) {
            buffer.append((char) b);
            if ((char) b == '\n') {
                saveToDatabase(buffer.toString().trim());
                buffer.setLength(0); // Kosongkan buffer
            }
        }

        private void saveToDatabase(String message) {
            
            try {
                prop.loadFromXML(new FileInputStream("setting/database.xml"));
                interval = Integer.parseInt(prop.getProperty("INTERVALSAVELOG"));
            } catch (NumberFormatException e) {
                // Jika terjadi kesalahan konversi, gunakan nilai default
                interval = 3;
            } catch (Exception e) {
                // Jika terjadi kesalahan saat membaca file atau properti
                interval = 3;
            }
            
            final Connection connect = koneksiDB.condb();
            PreparedStatement ps;

            if (!akses.getkode().equals("")) {

                //untuk menyimpan ke data base
//                try {
//                    ps = connect.prepareStatement("insert into trackersql values(now(),?,?)");
//                    try {
//                        ps.setString(1, akses.getalamatip() + " " + message);
//                        ps.setString(2, akses.getkode());
//                        ps.executeUpdate();
//                    } catch (Exception e) {
//
//                    } finally {
//                        if (ps != null) {
//                            ps.close();
//                        }
//                    }
//                } catch (Exception e) {
//
//                }

                //untuk menyompan ke file LOG NAS/Local
                String nasPath = ""; // jalur ke NAS
                String fallbackPath = "C:\\Logs\\KhanzaLogTemp.log";
                File file = new File(fallbackPath);
                File folder = file.getParentFile(); // Mendapatkan folder dari path

                // Cek apakah folder ada, jika tidak buat foldernya
                if (folder != null && !folder.exists()) {
                    if (folder.mkdirs()) {
                        //System.out.println("Folder berhasil dibuat: " + folder.getAbsolutePath());
                    } else {
                        //System.err.println("Gagal membuat folder: " + folder.getAbsolutePath());
                    }
                }
                //String fallbackFolderPath = System.getProperty("user.dir") + File.separator + "LocalLogs" + File.separator + folderName;
                try {
                    prop.loadFromXML(new FileInputStream("setting/database.xml"));
                    nasPath = prop.getProperty("LOGPATHNAS");
                } catch (Exception e) {

                }
                if (!nasPath.equals("")) {
                    String logMessage = java.time.LocalDateTime.now()
                            + " [" + akses.getalamatip() + "] [" + akses.getkode() + "]"
                            + " : " + message;
                    String fixNasPath = nasPath + "Log" + akses.getalamatip().replace(".", "_") + ".log";

                    File logFileNas = new File(fixNasPath);
                    if (logFileNas.exists()) {
                        try {
                            Path path = logFileNas.toPath();
                            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
                            FileTime creationTime = attributes.creationTime();

                            long currentTime = System.currentTimeMillis();
                            long creationTimeMillis = creationTime.toMillis();
                            long threeDaysInMillis = interval * 24 * 60 * 60 * 1000; // 3 hari dalam milidetik
//                            long threeDaysInMillis = 3 * 24 * 60 * 60 * 1000; // 3 hari dalam milidetik

                            if (currentTime - creationTimeMillis > threeDaysInMillis) {
                                boolean deleted = logFileNas.delete();
                            }
                        } catch (Exception e) {
                        }
                    }

                    boolean savedToNas = false;
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fixNasPath, true))) {
                        File fallbackFolder = new File("C:\\Logs");
                        File[] logFiles = fallbackFolder.listFiles((dir, name) -> name.endsWith(".log"));
                        if (logFiles != null) {
                            for (File logFile : logFiles) {
                                try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        // Menulis log yang dibaca ke file NAS
                                        writer.write(line);
                                        writer.newLine();  // Menambahkan baris baru
                                    }
                                    reader.close(); // menutup file log local,
                                    logFile.delete(); // menghapus file log local setalah di append ke logfile di NAS
                                } catch (IOException e) {
                                    //System.err.println("Gagal memproses file log: " + logFile.getName() + " - " + e.getMessage());
                                }
                            }
                        }
                        writer.write(logMessage);
                        writer.newLine(); // Tambahkan baris baru untuk setiap log
                        savedToNas = true;
                    } catch (IOException e) {
                        //JOptionPane.showMessageDialog(null,e.getMessage());
                    }

                    if (!savedToNas) {
                        try {
                            saveToLocalFallback(fallbackPath, "[LOKAL] " + logMessage);
                        } catch (IOException fallbackException) {
                            //JOptionPane.showMessageDialog(null,fallbackException.printStackTrace());
                        }
                    }
                }
            }
        }

        private void saveToLocalFallback(String fallbackPath, String logMessage) throws IOException {
            // Gunakan try-with-resources untuk memastikan resource ditutup
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fallbackPath, true))) {
                //System.out.println("Attempting to save to local fallback: " + fallbackPath);
                writer.write(logMessage);
                writer.newLine();
                //System.out.println("Log saved to local fallback file: " + fallbackPath);
            }
        }

    }
}
