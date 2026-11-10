package modif;

import fungsi.batasInput;
import fungsi.sekuel;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import rekammedis.RMCari5SOAPTerakhir;
import widget.Button;
import widget.ComboBox;
import widget.InternalFrame;
import widget.Label;
import widget.ScrollPane;
import widget.TextArea;
import widget.TextBox;
import widget.panelisi;

/**
 * Dialog input SOAP Dokter Pre Operasi.
 * Dipakai ketika SOAP dokter belum tersedia saat menyimpan Laporan Operasi.
 * Data disimpan ke pemeriksaan_ranap / pemeriksaan_ralan (sesuai status),
 * dengan jam pemeriksaan sebelum jam mulai operasi.
 */
public class DlgSOAPPreOperasi extends JDialog {
    private final sekuel Sequel = new sekuel();

    private final InternalFrame internalFrame1 = new InternalFrame();
    private final panelisi panelisi1 = new panelisi();
    private final panelisi FormInput = new panelisi();
    private final Button BtnSimpan = new Button();
    private final Button BtnKeluar = new Button();
    private final Button Btn5Soap = new Button();
    private final RMCari5SOAPTerakhir soapterakhir = new RMCari5SOAPTerakhir(null, true);

    private final TextBox TNoRw = new TextBox();
    private final TextBox TTanggal = new TextBox();
    private final ComboBox cmbJam = new ComboBox();
    private final ComboBox cmbMnt = new ComboBox();
    private final ComboBox cmbDtk = new ComboBox();
    private final TextBox KdDok = new TextBox();
    private final TextBox TDokter = new TextBox();
    private final TextBox Jabatan = new TextBox();
    private final TextBox TSuhu = new TextBox();
    private final TextBox TTensi = new TextBox();
    private final TextBox TBerat = new TextBox();
    private final TextBox TTinggi = new TextBox();
    private final TextBox TRespirasi = new TextBox();
    private final TextBox TNadi = new TextBox();
    private final TextBox SpO2 = new TextBox();
    private final TextBox TGCS = new TextBox();
    private final TextBox TAlergi = new TextBox();
    private final ComboBox cmbKesadaran = new ComboBox();
    private final TextArea TKeluhan = new TextArea();
    private final TextArea TPemeriksaan = new TextArea();
    private final TextArea TPenilaian = new TextArea();
    private final TextArea TindakLanjut = new TextArea();
    private final TextArea TInstruksi = new TextArea();
    private final TextArea TEvaluasi = new TextArea();

    private String status = "Ranap";
    private String jamOperasi = "00:00:00";
    private boolean tersimpan = false;

    public DlgSOAPPreOperasi(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(1040, 440);
        setLocationRelativeTo(parent);

        soapterakhir.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                isiSoapTerakhir();
                Btn5Soap.requestFocus();
            }
        });
    }

    private void isiSoapTerakhir() {
        int r = soapterakhir.getTable().getSelectedRow();
        if (r != -1) {
            TKeluhan.setText(soapterakhir.getTable().getValueAt(r, 2).toString());
            TPemeriksaan.setText(soapterakhir.getTable().getValueAt(r, 3).toString());
            TPenilaian.setText(soapterakhir.getTable().getValueAt(r, 4).toString());
            TindakLanjut.setText(soapterakhir.getTable().getValueAt(r, 5).toString());
            TInstruksi.setText(soapterakhir.getTable().getValueAt(r, 6).toString());
            TEvaluasi.setText(soapterakhir.getTable().getValueAt(r, 7).toString());
        }
    }

    private void isiDokter(String kode, String nama) {
        KdDok.setText(kode);
        TDokter.setText(nama);
        String sps = Sequel.cariIsi("select spesialis.nm_sps from dokter inner join spesialis on dokter.kd_sps=spesialis.kd_sps where dokter.kd_dokter=?", kode);
        Jabatan.setText("Dokter" + (sps.equals("") ? "" : " / " + sps));
    }

    private static String[] angka(int n) {
        String[] r = new String[n];
        for (int i = 0; i < n; i++) {
            r[i] = (i < 10 ? "0" : "") + i;
        }
        return r;
    }

    private void label(String teks, int x, int y, int w) {
        Label l = new Label();
        l.setText(teks);
        l.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        FormInput.add(l);
        l.setBounds(x, y, w, 23);
    }

    private void box(TextBox t, int x, int y, int w, int max) {
        t.setDocument(new batasInput((byte) max).getKata(t));
        FormInput.add(t);
        t.setBounds(x, y, w, 23);
    }

    private void area(String teks, TextArea t, int lx, int lw, int x, int y, int w, int h) {
        label(teks, lx, y, lw);
        t.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        t.setColumns(20);
        t.setRows(5);
        t.setDocument(new batasInput((int) 2000).getKata(t));
        ScrollPane sp = new ScrollPane();
        sp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        sp.setViewportView(t);
        FormInput.add(sp);
        sp.setBounds(x, y, w, h);
    }

    private void initComponents() {
        setUndecorated(true);
        setResizable(false);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ SOAP Dokter Pre Operasi ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50)));
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        panelisi1.setPreferredSize(new java.awt.Dimension(100, 56));
        panelisi1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Simpan");
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnSimpan.addActionListener(e -> simpan());
        BtnSimpan.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_SPACE) {
                    simpan();
                }
            }
        });
        panelisi1.add(BtnSimpan);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluar.addActionListener(e -> dispose());
        BtnKeluar.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_SPACE) {
                    dispose();
                }
            }
        });
        panelisi1.add(BtnKeluar);
        internalFrame1.add(panelisi1, java.awt.BorderLayout.PAGE_END);

        FormInput.setBorder(null);
        FormInput.setLayout(null);

        // ===== Kolom kiri =====
        label("No.Rawat :", 5, 10, 122);
        TNoRw.setEditable(false);
        box(TNoRw, 130, 10, 130, 17);

        label("Tanggal / Jam :", 5, 38, 122);
        TTanggal.setEditable(false);
        box(TTanggal, 130, 38, 90, 10);
        cmbJam.setModel(new javax.swing.DefaultComboBoxModel(angka(24)));
        cmbMnt.setModel(new javax.swing.DefaultComboBoxModel(angka(60)));
        cmbDtk.setModel(new javax.swing.DefaultComboBoxModel(angka(60)));
        FormInput.add(cmbJam);
        cmbJam.setBounds(228, 38, 62, 23);
        FormInput.add(cmbMnt);
        cmbMnt.setBounds(293, 38, 62, 23);
        FormInput.add(cmbDtk);
        cmbDtk.setBounds(358, 38, 62, 23);

        label("Dilakukan :", 5, 66, 122);
        KdDok.setEditable(false);
        box(KdDok, 130, 66, 105, 20);
        TDokter.setEditable(false);
        box(TDokter, 242, 66, 249, 100);

        label("Profesi / Jabatan :", 5, 94, 122);
        Jabatan.setEditable(false);
        box(Jabatan, 130, 94, 327, 100);
        Btn5Soap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        Btn5Soap.setMnemonic('5');
        Btn5Soap.setToolTipText("Alt+5 (5 SOAP Terakhir)");
        Btn5Soap.addActionListener(e -> {
            if (KdDok.getText().trim().equals("") || TDokter.getText().trim().equals("")) {
                JOptionPane.showMessageDialog(null, "Maaf, dokter operator pada laporan operasi belum terisi...!!!");
                Btn5Soap.requestFocus();
            } else {
                setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
                soapterakhir.setNoRM(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?", TNoRw.getText()),
                        KdDok.getText(), status);
                soapterakhir.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                soapterakhir.setLocationRelativeTo(internalFrame1);
                soapterakhir.setVisible(true);
                setCursor(java.awt.Cursor.getDefaultCursor());
            }
        });
        FormInput.add(Btn5Soap);
        Btn5Soap.setBounds(463, 94, 28, 23);

        area("Subjek :", TKeluhan, 5, 122, 130, 122, 453, 50);
        area("Objek :", TPemeriksaan, 5, 122, 130, 178, 453, 50);

        label("Suhu (°C) :", 5, 234, 122);
        box(TSuhu, 130, 234, 60, 5);
        label("Tensi (mmHg) :", 195, 234, 90);
        box(TTensi, 288, 234, 70, 8);
        label("Berat (Kg) :", 362, 234, 68);
        box(TBerat, 433, 234, 60, 5);

        label("TB (Cm) :", 5, 262, 122);
        box(TTinggi, 130, 262, 60, 5);
        label("RR (/menit) :", 195, 262, 90);
        box(TRespirasi, 288, 262, 70, 3);
        label("Nadi (/menit) :", 362, 262, 68);
        box(TNadi, 433, 262, 60, 3);

        label("SpO2 (%) :", 5, 290, 122);
        box(SpO2, 130, 290, 60, 3);
        label("GCS (E,V,M) :", 195, 290, 90);
        box(TGCS, 288, 290, 70, 10);
        label("Kesadaran :", 362, 290, 68);
        cmbKesadaran.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Compos Mentis", "Somnolence", "Sopor", "Coma", "Apatis", "Delirium"}));
        FormInput.add(cmbKesadaran);
        cmbKesadaran.setBounds(433, 290, 150, 23);

        // ===== Kolom kanan =====
        label("Alergi :", 600, 10, 110);
        box(TAlergi, 715, 10, 300, 80);
        area("Asesmen :", TPenilaian, 600, 110, 715, 38, 300, 62);
        area("Plan :", TindakLanjut, 600, 110, 715, 106, 300, 62);
        area("Instruksi :", TInstruksi, 600, 110, 715, 174, 300, 62);
        area("Evaluasi :", TEvaluasi, 600, 110, 715, 242, 300, 62);

        FormInput.setPreferredSize(new java.awt.Dimension(1020, 330));
        internalFrame1.add(FormInput, java.awt.BorderLayout.CENTER);
        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);
    }

    /**
     * @param noRawat       nomor rawat pasien
     * @param statusLanjut  "Ralan" atau "Ranap"
     * @param tglOperasi    tanggal operasi format yyyy-MM-dd
     * @param jamMulai      jam mulai operasi format HH:mm:ss
     * @param kdDokterAwal  kode dokter yang dipilih awal (mis. operator 1), boleh kosong
     */
    public void setData(String noRawat, String statusLanjut, String tglOperasi, String jamMulai, String kdDokterAwal) {
        this.status = statusLanjut.equals("Ralan") ? "Ralan" : "Ranap";
        this.jamOperasi = jamMulai;
        this.tersimpan = false;
        TNoRw.setText(noRawat);
        TTanggal.setText(tglOperasi);

        // default: 1 jam sebelum jam mulai operasi, paling awal 00:00:00 (harus di tanggal yang sama)
        int jam = 0, mnt = 0, dtk = 0;
        try {
            String[] p = jamMulai.split(":");
            int total = Integer.parseInt(p[0]) * 3600 + Integer.parseInt(p[1]) * 60 + Integer.parseInt(p[2]) - 3600;
            if (total < 0) {
                total = 0;
            }
            jam = total / 3600;
            mnt = (total % 3600) / 60;
            dtk = total % 60;
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
        cmbJam.setSelectedIndex(jam);
        cmbMnt.setSelectedIndex(mnt);
        cmbDtk.setSelectedIndex(dtk);

        KdDok.setText("");
        TDokter.setText("");
        Jabatan.setText("");
        if (kdDokterAwal != null && !kdDokterAwal.trim().equals("")) {
            isiDokter(kdDokterAwal, Sequel.cariIsi("select nm_dokter from dokter where kd_dokter=?", kdDokterAwal));
        }
    }

    public boolean isTersimpan() {
        return tersimpan;
    }

    private void simpan() {
        String jam = cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem();
        if (KdDok.getText().trim().equals("") || TDokter.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Dokter harus dipilih...!!!");
            Btn5Soap.requestFocus();
        } else if (TKeluhan.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Subjek masih kosong...!!!");
            TKeluhan.requestFocus();
        } else if (TPemeriksaan.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Objek masih kosong...!!!");
            TPemeriksaan.requestFocus();
        } else if (TPenilaian.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Asesmen masih kosong...!!!");
            TPenilaian.requestFocus();
        } else if (TindakLanjut.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Plan masih kosong...!!!");
            TindakLanjut.requestFocus();
        } else if (jam.compareTo(jamOperasi) >= 0) {
            JOptionPane.showMessageDialog(null, "Jam SOAP harus lebih awal dari jam mulai operasi (" + jamOperasi + ")...!!!");
        } else {
            String nip = KdDok.getText().trim();
            String[] data;
            String tabel;
            if (status.equals("Ralan")) {
                tabel = "pemeriksaan_ralan";
                data = new String[]{
                    TNoRw.getText(), TTanggal.getText(), jam,
                    TSuhu.getText().trim(), TTensi.getText().trim(), TNadi.getText().trim(), TRespirasi.getText().trim(),
                    TTinggi.getText().trim(), TBerat.getText().trim(), SpO2.getText().trim(), TGCS.getText().trim(),
                    cmbKesadaran.getSelectedItem().toString(), TKeluhan.getText().trim(), TPemeriksaan.getText().trim(),
                    TAlergi.getText().trim(), "", TindakLanjut.getText().trim(), TPenilaian.getText().trim(),
                    TInstruksi.getText().trim(), TEvaluasi.getText().trim(), nip
                };
            } else {
                tabel = "pemeriksaan_ranap";
                data = new String[]{
                    TNoRw.getText(), TTanggal.getText(), jam,
                    TSuhu.getText().trim(), TTensi.getText().trim(), TNadi.getText().trim(), TRespirasi.getText().trim(),
                    TTinggi.getText().trim(), TBerat.getText().trim(), SpO2.getText().trim(), TGCS.getText().trim(),
                    cmbKesadaran.getSelectedItem().toString(), TKeluhan.getText().trim(), TPemeriksaan.getText().trim(),
                    TAlergi.getText().trim(), TPenilaian.getText().trim(), TindakLanjut.getText().trim(),
                    TInstruksi.getText().trim(), TEvaluasi.getText().trim(), nip
                };
            }
            StringBuilder q = new StringBuilder("?");
            for (int i = 1; i < data.length; i++) {
                q.append(",?");
            }
            if (Sequel.menyimpantf(tabel, q.toString(), "Data", data.length, data)) {
                tersimpan = true;
                dispose();
            }
        }
    }
}
