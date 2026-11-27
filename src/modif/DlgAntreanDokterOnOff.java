package modif;

import fungsi.AntrianPoli;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Antrean Dokter Hari Ini (On/Off): perawat rawat jalan membuka / menutup antrean dokter + poli hari ini.
 *
 * Daftar diambil dari anjungan (GET {URLAPIANTRIAN}/tutup): hanya dokter + poli yang ada di jadwal hari ini.
 * Kolom "Buka" dicentang = antrean buka. Hilangkan centang = antrean DITUTUP: kartu dokter hilang dari layar
 * display FO dan dokter tidak bisa dipilih di menu Daftar Onsite anjungan. Status tersimpan di
 * side_db.anjungan_tutup_antrean dan hanya berlaku hari ini.
 *
 * Form ini dibuat dengan kode (tanpa .form NetBeans).
 */
public class DlgAntreanDokterOnOff extends javax.swing.JDialog {
    private static final int KOL_BUKA = 0, KOL_POLI = 1, KOL_DOKTER = 2, KOL_JAM = 3, KOL_PASIEN = 4, KOL_KET = 5;
    /** ukuran form seukuran layar hp (potret) */
    private static final int LEBAR = 420, TINGGI = 720;

    private final AntrianPoli antriPoli = new AntrianPoli();
    private final List<String[]> kode = new ArrayList<>(); // per baris model: {kd_poli, kd_dokter}
    private final DefaultTableModel model;
    private final widget.Table tabel = new widget.Table();
    private final TableRowSorter<DefaultTableModel> urut;
    private final widget.TextBox TCari = new widget.TextBox();
    private final widget.Label LInfo = new widget.Label();
    private boolean memuat = false;

    public DlgAntreanDokterOnOff(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle("Antrean Dokter Hari Ini (On/Off)");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(null, new String[]{"Antrean", "Poliklinik", "Dokter", "Jam Praktik", "Pasien", "Keterangan"}) {
            @Override public Class<?> getColumnClass(int c) {
                return c == KOL_BUKA ? Boolean.class : c == KOL_PASIEN ? Integer.class : String.class;
            }
            // tidak ada editor: kolom Antrean diubah lewat klik toggle (lihat klikToggle)
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabel.setModel(model);
        urut = new TableRowSorter<>(model);
        tabel.setRowSorter(urut);
        // form seukuran hp: hanya kolom Antrean, Dokter, Pasien yang tampil; Poliklinik, Jam Praktik, dan
        // Keterangan tetap ada di model (dipakai pencarian) dan ditampilkan di bawah nama dokter (KartuDokter)
        TableColumnModel kolom = tabel.getColumnModel();
        TableColumn kBuka = kolom.getColumn(KOL_BUKA), kDokter = kolom.getColumn(KOL_DOKTER), kPasien = kolom.getColumn(KOL_PASIEN);
        kolom.removeColumn(kolom.getColumn(KOL_KET));
        kolom.removeColumn(kolom.getColumn(KOL_JAM));
        kolom.removeColumn(kolom.getColumn(KOL_POLI));
        tabel.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabel.setRowHeight(54);
        kBuka.setMinWidth(75);
        kBuka.setMaxWidth(75);
        kPasien.setMinWidth(55);
        kPasien.setMaxWidth(55);
        kDokter.setHeaderValue("Dokter / Poliklinik");
        tabel.setDefaultRenderer(Object.class, new WarnaStatus());
        tabel.setDefaultRenderer(Integer.class, new WarnaStatus());
        kBuka.setCellRenderer(new Toggle());
        kDokter.setCellRenderer(new KartuDokter());
        tabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                klikToggle(e);
            }
        });
        tabel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override public void mouseMoved(java.awt.event.MouseEvent e) {
                boolean diToggle = tabel.columnAtPoint(e.getPoint()) == tabel.convertColumnIndexToView(KOL_BUKA)
                    && tabel.rowAtPoint(e.getPoint()) >= 0;
                tabel.setCursor(Cursor.getPredefinedCursor(diToggle ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            }
        });
        tabel.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent e) {
                // spasi pada baris terpilih = klik toggle
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_SPACE && tabel.getSelectedRow() >= 0 && !memuat) {
                    int baris = tabel.convertRowIndexToModel(tabel.getSelectedRow());
                    ubahStatus(baris, !Boolean.TRUE.equals(model.getValueAt(baris, KOL_BUKA)));
                    e.consume();
                }
            }
        });

        widget.InternalFrame bingkai = new widget.InternalFrame();
        bingkai.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Antrean Dokter Hari Ini (On/Off) ]::", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
            new Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        bingkai.setLayout(new BorderLayout(1, 1));

        widget.Label petunjuk = new widget.Label();
        // lebar teks dibatasi agar membungkus ke bawah di form yang sempit (px pada HTML Swing dibesarkan 4/3, jadi dikali 3/4)
        petunjuk.setText("<html><body style='width:" + ((LEBAR - 50) * 3 / 4) + "px'>Klik toggle <b>Antrean</b>: <b>ON</b> = antrean dokter buka, "
            + "<b>OFF</b> = antrean <b>DITUTUP</b> hari ini (kartu dokter hilang dari layar display FO dan dokter tidak bisa "
            + "dipilih di menu Daftar Onsite anjungan).</body></html>");
        petunjuk.setBorder(BorderFactory.createEmptyBorder(6, 8, 2, 8));

        widget.panelisi atas = new widget.panelisi();
        atas.setLayout(new BorderLayout());
        atas.add(petunjuk, BorderLayout.NORTH);
        javax.swing.JPanel barisCari = new javax.swing.JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        barisCari.setOpaque(false);
        widget.Label lCari = new widget.Label();
        lCari.setText("Cari :");
        lCari.setPreferredSize(new Dimension(40, 23));
        barisCari.add(lCari);
        TCari.setPreferredSize(new Dimension(LEBAR - 85, 23));
        TCari.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { saring(); }
            @Override public void removeUpdate(DocumentEvent e) { saring(); }
            @Override public void changedUpdate(DocumentEvent e) { saring(); }
        });
        barisCari.add(TCari);
        atas.add(barisCari, BorderLayout.SOUTH);
        bingkai.add(atas, BorderLayout.NORTH);

        widget.ScrollPane gulir = new widget.ScrollPane();
        gulir.setViewportView(tabel);
        bingkai.add(gulir, BorderLayout.CENTER);

        widget.panelisi bawah = new widget.panelisi();
        bawah.setLayout(new BorderLayout());
        LInfo.setBorder(BorderFactory.createEmptyBorder(4, 8, 0, 8));
        bawah.add(LInfo, BorderLayout.NORTH);
        javax.swing.JPanel barisTombol = new javax.swing.JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 6));
        barisTombol.setOpaque(false);
        bawah.add(barisTombol, BorderLayout.CENTER);

        widget.Button BtnMuat = new widget.Button();
        BtnMuat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/refresh.png")));
        BtnMuat.setMnemonic('M');
        BtnMuat.setText("Muat Ulang");
        BtnMuat.setPreferredSize(new Dimension(120, 30));
        BtnMuat.addActionListener(e -> muat());
        barisTombol.add(BtnMuat);

        widget.Button BtnKeluar = new widget.Button();
        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setPreferredSize(new Dimension(100, 30));
        BtnKeluar.addActionListener(e -> dispose());
        barisTombol.add(BtnKeluar);
        bingkai.add(bawah, BorderLayout.SOUTH);

        getContentPane().add(bingkai, BorderLayout.CENTER);
        // seukuran hp (potret); tinggi menyesuaikan layar bila monitor pendek (misal 1366x768)
        int tinggiLayar = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
        setSize(LEBAR, Math.min(TINGGI, tinggiLayar - 20));
        setMinimumSize(new Dimension(360, 400));
    }

    /** muat: ambil daftar dokter + poli hari ini dari anjungan (di belakang layar agar form tidak macet). */
    public void muat() {
        memuat = true;
        LInfo.setText("Memuat dari anjungan...");
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        new SwingWorker<JSONArray, Void>() {
            @Override protected JSONArray doInBackground() throws Exception {
                return antriPoli.daftarAntreanHariIni();
            }
            @Override protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    isiTabel(get());
                } catch (Exception e) {
                    Throwable sebab = e.getCause() != null ? e.getCause() : e;
                    LInfo.setText("Gagal memuat.");
                    JOptionPane.showMessageDialog(DlgAntreanDokterOnOff.this,
                        "Daftar antrean dokter tidak bisa diambil dari anjungan:\n" + sebab.getMessage()
                        + "\n\nPastikan aplikasi anjungan berjalan dan URLAPIANTRIAN di setting/database.xml benar.",
                        "Antrean Dokter", JOptionPane.ERROR_MESSAGE);
                } finally {
                    memuat = false;
                }
            }
        }.execute();
    }

    private void isiTabel(JSONArray daftar) {
        model.setRowCount(0);
        kode.clear();
        int ditutup = 0;
        for (int i = 0; i < daftar.length(); i++) {
            JSONObject d = daftar.optJSONObject(i);
            if (d == null) continue;
            boolean tutup = d.optBoolean("ditutup");
            String ket;
            if (tutup) {
                ditutup++;
                String oleh = d.optString("ditutup_oleh");
                ket = "DITUTUP pukul " + d.optString("jam_tutup") + (oleh.isEmpty() ? "" : " oleh " + oleh);
            } else if (!d.optBoolean("jadwal_buka")) {
                ket = "Buka (jam praktik sudah lewat)";
            } else {
                ket = "Buka";
            }
            model.addRow(new Object[]{!tutup, d.optString("nm_poli"), d.optString("nm_dokter"),
                d.optString("jam_mulai") + " - " + d.optString("jam_selesai"), d.optInt("pasien"), ket});
            kode.add(new String[]{d.optString("kd_poli"), d.optString("kd_dokter")});
        }
        LInfo.setText(daftar.length() + " dokter praktik hari ini, " + ditutup + " antrean ditutup.");
        saring();
    }

    /** Klik pada kolom Antrean membalik status toggle baris tersebut. */
    private void klikToggle(java.awt.event.MouseEvent e) {
        if (memuat || !SwingUtilities.isLeftMouseButton(e)) {
            return;
        }
        int kolom = tabel.columnAtPoint(e.getPoint());
        int baris = tabel.rowAtPoint(e.getPoint());
        if (baris < 0 || kolom != tabel.convertColumnIndexToView(KOL_BUKA)) {
            return;
        }
        int b = tabel.convertRowIndexToModel(baris);
        ubahStatus(b, !Boolean.TRUE.equals(model.getValueAt(b, KOL_BUKA)));
    }

    private void ubahStatus(int baris, boolean buka) {
        String[] k = kode.get(baris);
        String siapa = model.getValueAt(baris, KOL_DOKTER) + " (" + model.getValueAt(baris, KOL_POLI) + ")";
        if (!buka) {
            // menutup antrean berdampak ke anjungan, tetap dikonfirmasi; membuka kembali langsung
            int jawab = JOptionPane.showConfirmDialog(this,
                "Tutup antrean " + siapa + " hari ini?\n\n"
                + "- Kartu dokter ini hilang dari layar display FO.\n"
                + "- Dokter ini tidak bisa dipilih lagi di menu Daftar Onsite anjungan.",
                "Tutup Antrean Dokter", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (jawab != JOptionPane.YES_OPTION) {
                return;
            }
        }
        model.setValueAt(buka, baris, KOL_BUKA); // toggle langsung berpindah, status pasti dari anjungan saat muat ulang
        tabel.repaint();
        try {
            antriPoli.setAntreanTutup(k[0], k[1], !buka);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal mengubah status antrean " + siapa + ":\n" + e.getMessage());
        }
        SwingUtilities.invokeLater(this::muat);
    }

    private void saring() {
        String t = TCari.getText().trim();
        urut.setRowFilter(t.isEmpty() ? null : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(t), KOL_POLI, KOL_DOKTER));
    }

    /** Kolom Antrean digambar sebagai toggle switch: ON (hijau, tombol di kanan) / OFF (merah, tombol di kiri). */
    private class Toggle extends javax.swing.JComponent implements javax.swing.table.TableCellRenderer {
        private boolean on;
        private Color latar = Color.WHITE;

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean pilih, boolean fokus, int r, int c) {
            on = Boolean.TRUE.equals(v);
            latar = pilih ? t.getSelectionBackground()
                : (!on ? new Color(255, 225, 225) : (r % 2 == 0 ? Color.WHITE : new Color(245, 250, 240)));
            setToolTipText(on ? "Antrean buka, klik untuk menutup" : "Antrean ditutup, klik untuk membuka");
            return this;
        }

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(latar);
            g2.fillRect(0, 0, getWidth(), getHeight());
            int w = 52, h = 20, x = (getWidth() - w) / 2, y = (getHeight() - h) / 2;
            g2.setColor(on ? new Color(46, 160, 67) : new Color(205, 55, 55));
            g2.fillRoundRect(x, y, w, h, h, h);
            int d = h - 4;
            g2.setColor(Color.WHITE);
            g2.fillOval(on ? x + w - d - 2 : x + 2, y + 2, d, d);
            g2.setFont(new Font("Tahoma", Font.BOLD, 10));
            java.awt.FontMetrics fm = g2.getFontMetrics();
            String teks = on ? "ON" : "OFF";
            int tx = on ? x + 7 : x + w - 7 - fm.stringWidth(teks);
            g2.drawString(teks, tx, y + (h - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }

    /** Baris antrean yang ditutup diberi warna merah muda. */
    private class WarnaStatus extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean pilih, boolean fokus, int r, int c) {
            Component k = super.getTableCellRendererComponent(t, v, pilih, fokus, r, c);
            // sebagian kolom disembunyikan, jadi indeks baris/kolom tampilan dikonversi ke model
            boolean tutup = Boolean.FALSE.equals(model.getValueAt(t.convertRowIndexToModel(r), KOL_BUKA));
            if (!pilih) {
                k.setBackground(tutup ? new Color(255, 225, 225) : (r % 2 == 0 ? Color.WHITE : new Color(245, 250, 240)));
                k.setForeground(tutup ? new Color(160, 20, 20) : new Color(50, 50, 50));
            }
            if (t.convertColumnIndexToModel(c) == KOL_PASIEN) {
                setHorizontalAlignment(CENTER);
            } else {
                setHorizontalAlignment(LEFT);
            }
            return k;
        }
    }

    /** Kolom Dokter: nama dokter tebal, di bawahnya poliklinik + jam praktik, lalu keterangan status. */
    private class KartuDokter extends WarnaStatus {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean pilih, boolean fokus, int r, int c) {
            int b = t.convertRowIndexToModel(r);
            String dokter = String.valueOf(model.getValueAt(b, KOL_DOKTER)), poli = String.valueOf(model.getValueAt(b, KOL_POLI)),
                jam = String.valueOf(model.getValueAt(b, KOL_JAM)), ket = String.valueOf(model.getValueAt(b, KOL_KET));
            String teks = "<html><b>" + html(dokter) + "</b><br>" + html(poli) + " &middot; " + html(jam) + "<br><i>" + html(ket) + "</i></html>";
            Component k = super.getTableCellRendererComponent(t, teks, pilih, fokus, r, c);
            setToolTipText(dokter + " | " + poli + " | " + jam + " | " + ket);
            return k;
        }

        private String html(String s) {
            return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        }
    }
}
