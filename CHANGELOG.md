# Changelog SIMRS Khanza RSPW

Semua perubahan penting pada aplikasi dicatat di file ini, terbaru di atas.
Format mengacu pada [Keep a Changelog](https://keepachangelog.com/id/1.1.0/), versi memakai tanggal rilis (TAHUN.BULAN.TANGGAL).

<!--
PANDUAN MENGISI
- Selama pengembangan, catat perubahan di bagian [Belum Rilis].
- Saat deploy, ganti "[Belum Rilis]" menjadi "[2026.11.05] - 2026-11-05", lalu buat [Belum Rilis] baru yang kosong.
- Kategori: Ditambahkan, Diubah, Diperbaiki, Dihapus, Catatan Teknis.
- Format butir: - **Modul**: penjelasan dengan bahasa pengguna. (Unit pengguna)
- "Catatan Teknis" untuk tim IT (SQL, setting, file baru). Bagian ini dan [Belum Rilis]
  TIDAK ditampilkan di menu "Apa yang Baru" di aplikasi.
- Popup "Apa yang Baru" muncul otomatis sekali setelah login bila versi teratas berubah.
-->

## [Belum Rilis]
### Ditambahkan
- **Pemasukan Lain-Lain**: Kwitansi Penerimaan (klik kanan tabel) kini dicetak langsung dari aplikasi tanpa membuka browser. Baris "Untuk Pembayaran" berisi nama kategori dengan isi Keperluan di bawahnya. Bila ada kendala, cetak lewat browser seperti dulu tetap tersedia di menu "Cetak Kwitansi Penerimaan (Web)". (Kasir)
- **Pemasukan Lain-Lain dari Billing Ranap**: Tombol "Pemasukan Lain" di Billing Ranap membuka Pemasukan Lain-Lain dengan pasien terisi, dan isian "Terima Dari" otomatis berisi No. Rawat, No. RM, dan nama pasien. Bila kategori yang dipilih NON CLAIM, muncul tombol klip di samping isian Pemasukan untuk memilih tindakan/paket yang ada di billing pasien. Total tindakan yang dicentang mengganti isi Pemasukan dan nama tindakannya ditambahkan ke Keperluan. (Kasir Ranap)
- **Daftar Tindakan/Paket Non Claim**: Daftar tindakan/paket yang bisa dipilih sebagai pemasukan lain kini bisa ditambah, diganti, dan dihapus sendiri lewat tombol "Daftar Tindakan" di jendela Pilih Tindakan/Paket. (Kasir, Keuangan)

### Diubah
- **Pemasukan Lain-Lain**: Bila kategori yang dipilih kategori naik kelas, isian Keperluan otomatis terisi nama kategori. (Kasir)
- **Resume Ranap**: No. Reg SITB tidak lagi terisi otomatis saat form dibuka. Klik tombol "SITB" di sebelah tombol Pemeriksaan Penunjang Lab, lalu simpan resume. Nomor juga bisa dihapus dari isian dengan tombol "Hapus SITB", dan bila salah bisa sekaligus dihapus dari data pendaftaran TB. (Ranap, Rekam Medis)

### Diperbaiki
- **Riwayat Perawatan**: Tampilan tabel yang berantakan pada Pengkajian Awal Medis Rawat Inap Umum dan beberapa asesmen lain sudah dirapikan. (Dokter, Perawat, Rekam Medis)

### Catatan Teknis
- Daftar tindakan/paket NON CLAIM disimpan di file cache `./cache/tindakanpemasukanlain.iyem` (JSON, tanpa tabel database), diatur lewat `modif.DlgTindakanPemasukanLain` dengan hak akses Kategori Pemasukan Lain. Bila file belum ada, aplikasi memakai daftar bawaan di `modif.KwitansiUI.TINDAKAN`. File ini tersimpan per PC, jadi setelah diubah salin file tersebut ke folder `cache` PC kasir lain. Kode dicocokkan dengan 7 karakter pertama `kd_jenis_prw` di billing atau `nama_biaya` di tambahan biaya.
- Kwitansi pemasukan lain memakai `report/rptKwitansiPemasukanLainModif.jasper` (dikompilasi dengan JasperReports 6.8.0). Bila jrxml diubah, kompilasi ulang di Jaspersoft Studio.
- Cetak kwitansi desktop tidak lagi membuka form validasi NON CLAIM / BIAYA SELISIH, karena pengecekan tindakan/paket sudah dilakukan saat input lewat tombol klip. `modif.DlgKwitansiNonClaim`, `modif.DlgKwitansiSelisih`, dan `CetakKwitansiPemasukanLain.jenisKategori()` saat ini tidak dipakai. Validasi lama tetap berjalan di menu Web (`billing/LaporanKeuangan.php`).
- Kategori NON CLAIM dan naik kelas dikenali dari nama kategori yang mengandung teks "NON CLAIM" / "NAIK KELAS" (huruf besar/kecil tidak berpengaruh).

## [2026.11.03] - 2026-11-03
### Ditambahkan
- **Apa yang Baru**: Tombol "Apa yang Baru" di jendela About untuk melihat daftar perubahan aplikasi. (Semua unit)
- **Hapus Nota Salah**: Menghapus nota di Billing Ralan dan Ranap wajib mengisi alasan, lalu pasien, alasan, dan petugasnya tercatat sebagai riwayat. (Kasir)
- **Billing Ranap**: Peringatan bila status pulang pasien BPJS belum diisi. (Kasir Ranap)
- **Validasi Resep Pasien BPJS / Obat Kronis**: Jendela validasi khusus pasien BPJS dengan kolom "Jml Kronis" di samping "Jml Poli". Jml Poli masuk paket rawat jalan, Jml Kronis otomatis tercatat sebagai piutang. Total kronis tampil di samping total. (Farmasi)
- **Validasi Resep**: Waktu peresepan, waktu validasi, dan jenis resep (racikan/non racikan, mengikuti resep dokter) ikut tercatat otomatis. Validasi pasien BPJS / Obat Kronis punya pilihan "Jenis Resep" dan "Obat Karyawan". (Farmasi)
- **Lembar Telaah Resep**: Resep pasien BPJS yang punya obat kronis tercetak dalam dua bagian, "OBAT POLI" dan "OBAT KRONIS". (Farmasi)
- **Piutang Obat Karyawan dari Resep**: Menu "Piutang Obat Karyawan" di Daftar Permintaan Resep. Resep dokter divalidasi langsung sebagai piutang karyawan memakai tarif Karyawan, lalu dibayar lewat Bayar Piutang Karyawan. Cetak resep dan telaah tetap bisa dilakukan. Khusus resep non racikan. (Farmasi)

### Diubah
- **Peringatan SKDP Post RI**: Hanya muncul untuk pasien BPJS. (Kasir Ranap)
- **Resume Ranap**: Nomor registrasi SITB kini dicek langsung ke SITB memakai NIK pasien dan disimpan otomatis, lalu tampil di baris paling atas Hasil Laborat. Form tetap bisa dipakai selama proses berjalan. (Ranap, Rekam Medis)
- **Ranap Gabung / Bayi Baru Lahir**: Nomor peserta bayi tidak lagi disalin dari ibu. (Ranap, VK)

### Catatan Teknis
- Hapus Nota Salah menyimpan ke tabel `riwayat_buka_billing` (norawat, norm, alasan, asal, nip). Tabel ini wajib ada di server, karena nota tidak terhapus bila riwayat gagal disimpan.
- Sinkronisasi SITB memakai tabel `side_db.tb_register` (nik, no_reg_tb) dan layanan sulawesi.sitb.id. Lihat `modif.SITBResumeRanap`.
- Piutang obat karyawan dari resep (DlgPiutangKaryawan) memakai nota `HKyymmddxxx`, jns_jual "Karyawan", dan catatan "NIK-Nama" agar terbaca di Cari/Bayar Piutang Karyawan. Obat tidak masuk detail_pemberian_obat dan billing. Pembatalan lewat menu Data Piutang. Bila perlu, status validasi resep direset manual.
- Kolom tambahan untuk piutang karyawan (wajib, karena dipakai cetak resep): `ALTER TABLE side_db.resep_obat_detail ADD COLUMN nota_piutang varchar(20) NULL AFTER is_karyawan, ADD KEY nota_piutang (nota_piutang);`
- Tabel baru `side_db.resep_obat_detail` (lihat SQL di bawah) diisi saat validasi resep di DlgCariObat, DlgCariObat2 (rawat inap), dan DlgCariObatKronis. Kolom tgl/jam siap dan penyerahan diisi oleh aplikasi scan barcode terpisah, bukan oleh Khanza.
  `CREATE TABLE side_db.resep_obat_detail (no_resep varchar(14) NOT NULL, no_rawat varchar(17) NOT NULL, tgl_peresepan date NULL, jam_peresepan time NULL, tgl_validasi date NULL, jam_validasi time NULL, tgl_siap date NULL, jam_siap time NULL, tgl_penyerahan date NULL, jam_penyerahan time NULL, is_racikan tinyint(1) NOT NULL DEFAULT 0, is_karyawan tinyint(1) NOT NULL DEFAULT 0, nota_piutang varchar(20) NULL, PRIMARY KEY (no_resep), KEY nota_piutang (nota_piutang), KEY no_rawat (no_rawat)) ENGINE=InnoDB;`
- Bagian kronis Lembar Telaah diambil dari detailpiutang (nota = No. Rawat), disaring dengan obat di resep_dokter resep tersebut. Obat pengganti yang tidak ada di resep dokter tidak tampil, dan obat yang sama di dua resep pada satu kunjungan tampil dengan jumlah gabungan.
- Aktifkan dengan `<entry key="OTOMATISOBATKRONIS">yes</entry>` di setting/database.xml tiap PC farmasi. Bila "yes", validasi resep pasien BPJS (kd_pj = BPJ) dari Daftar Permintaan Resep membuka DlgCariObatKronis. Bila "no", tetap memakai DlgCariObat.
- Nota piutang kronis = No. Rawat. Validasi ulang pada kunjungan yang sama menambah ke piutang yang sudah ada.
- Pembatalan bagian kronis dilakukan dari menu Data Piutang (nota = No. Rawat), karena tidak ikut terhapus saat pemberian obat dihapus.

## [2026.10.28] - 2026-10-28
### Ditambahkan
- **Data Pasien Meninggal**: Kolom "Tgl Pulang SEP" dan "Stts Bridging" untuk memantau apakah tanggal pulang SEP sudah terkirim ke BPJS. Data terbaru tampil paling atas. (Rekam Medis, Casemix)
- **Ranap Gabung / Bayi Baru Lahir**: Isian data ayah, nomor KK, dan kode pos bayi, bisa diubah dan dibersihkan. (Ranap, VK)
- **Kamar Inap**: Tombol pulang khusus "Rujuk" dan "Meninggal" dengan status pulang otomatis. (Ranap)
- **Riwayat SEP**: Riwayat SEP diambil hingga lebih dari 90 hari ke belakang. (Pendaftaran, Casemix)
- **Registrasi**: Data pasien yang belum lengkap ditandai warna, dan ada menu untuk langsung melengkapinya. (Pendaftaran)
- **Konfirmasi Hapus**: Setiap penghapusan data kini meminta konfirmasi terlebih dahulu. (Semua unit)
- **Pasien Meninggal Otomatis**: Data pasien meninggal dibuat otomatis saat resume pasien disimpan dengan kondisi "Meninggal". IGD juga punya tombol cepat "Dirujuk" dan "Meninggal". (IGD, Ranap, Rekam Medis)

### Diubah
- **Mobile JKN**: Mengganti cara bayar dari BPJS ke umum/onsite kini meminta konfirmasi dan otomatis membatalkan antrean Mobile JKN. (Pendaftaran)
- **Tanggal Pulang SEP**: Update tanggal pulang SEP sekarang dilakukan saat membuat SKDP (Surat Kontrol). Pasien tidak bisa dipulangkan jika tanggal pulangnya berbeda dengan tanggal pulang SEP. (Ranap, Casemix)
- **Peringatan SKDP Post RI**: Tidak muncul lagi untuk pasien pulang Rujuk/Meninggal, dan riwayat pindah kamar tidak ikut dihitung. (Kasir Ranap)
- **Billing Ranap**: Beberapa peringatan digabung menjadi satu jendela. (Kasir Ranap)
- **Validasi Resep**: Resep yang sedang divalidasi dikunci agar tidak diproses dua petugas sekaligus. (Farmasi)
- **Registrasi**: Tanggal registrasi tidak bisa dimundurkan. (Pendaftaran)

## [2026.10.16] - 2026-10-16
### Ditambahkan
- **Rujukan & Ambulans**: Data ambulans, sopir, kilometer, dan petugas pendamping pada rujukan, beserta laporan rujukan/ambulans. (IGD, Ranap)
- **Surat Sakit**: Format surat sakit yang bisa disesuaikan. (Poliklinik, IGD)
- **Gabung Data Pasien**: Menu "Gabungkan Data Pasien" untuk menyatukan nomor RM ganda beserta seluruh riwayatnya. (Rekam Medis)
- **Nomor SEP**: No SEP tampil di Registrasi, IGD, Kasir Ralan, dan Kamar Inap. (Pendaftaran, Kasir)
- **Laporan Pelayanan Lab**: Ringkasan jumlah dan persentase pemeriksaan CITO dan reguler. (Laboratorium)
- **Resume Ranap**: Nomor registrasi SITB terisi otomatis untuk pasien TB. (Ranap, Rekam Medis)
- **Pasien Meninggal**: Tombol "Bridging SEP Pulang" dan konfirmasi sebelum menyimpan. (Rekam Medis)
- **Data Pasien**: Peringatan bila NIK sudah dipakai pasien lain. (Pendaftaran)
- **Ranap Gabung**: Mendukung lebih dari satu bayi dengan nomor RM otomatis. (Ranap, VK)
- **Asesmen Medis**: Isian "Alasan Pemeriksaan Penunjang" tampil di cetakan asesmen IGD dan Ranap. (Dokter)

### Diubah
- **Asesmen Medis IGD, Ranap Dewasa, dan Kandungan**: Isian wajib harus diisi sebelum menyimpan. Kursor otomatis pindah ke isian yang masih kosong. (Dokter)

### Diperbaiki
- **Bayi Baru Lahir**: Penyimpanan bayi tidak gagal lagi saat nomor RM yang dibuat otomatis ternyata sudah terpakai. (Ranap, VK)
- **Penelusuran Login**: Pencarian di tab kedua hanya berjalan bila kata kunci diisi. (IT)
- **Kamar Inap**: Pencarian tidak error lagi saat pasien belum memiliki SEP. (Ranap)

## [2026.09.30] - 2026-09-30
### Ditambahkan
- **Template Tambahan Biaya**: Template biaya tambahan yang bisa dipakai di Billing Ralan dan Ranap. (Kasir)
- **Hak Akses**: Banyak hak akses baru di Setup User, dan pencarian user lebih cepat. (IT)
- **Peresepan Dokter**: Kolom "Stok Gudang" dan peringatan resep BPJS yang sama dengan dua kunjungan sebelumnya (indikasi PRB/iterasi). (Dokter, Farmasi)
- **Daftar Permintaan Resep**: Tombol "Reset Antrean Validasi". (Farmasi)
- **Sensus Harian Ralan**: Kolom "Status" (dirujuk ke mana atau dirawat di bangsal mana). (Rekam Medis)
- **Rujukan**: Pencarian faskes tujuan dari referensi BPJS. (Poliklinik, IGD)
- **Referensi Diagnosa BPJS**: Tetap bisa mencari diagnosa dari data lokal saat layanan BPJS gangguan. (Poliklinik, Casemix)
- **Asesmen Medis IGD & Ranap Dewasa**: Isian "Alasan Pemeriksaan Penunjang". (Dokter)
- **Pemeriksaan Laboratorium**: Nomor permintaan dibuat otomatis, dan pemeriksaan dikelompokkan per permintaan. (Laboratorium)
- **Kasir Ralan**: Status "Rujukan Habis" ditandai warna. (Poliklinik, Kasir)

### Diubah
- **Cetakan Hasil & Permintaan Lab**: Tata letak baru, tanda tangan/QR diperbarui, dan saran/kesan terbaru ikut tercetak. (Laboratorium)
- **Surat Kontrol BPJS**: Konfirmasi sebelum menghapus. (Pendaftaran)
- **Booking Registrasi**: Cek data ganda kini memperhatikan poli dan dokter yang sama. (Pendaftaran)
- **Peresepan BPJS**: Pengecekan prasyarat lab memakai pemeriksaan HbA1c. (Dokter)
- **Kamar Inap**: Tanggal pulang SEP dikirim ke BPJS saat pasien pulang. (Ranap)
- **Registrasi**: Tarif karcis poli hanya bisa diubah oleh jabatan yang berwenang. (Pendaftaran)
- **Riwayat Barang Medis**: Filter bisa memakai lebih dari satu gudang, dan langsung terisi Apotek dan Depo Lt. 1. (Farmasi)
- **Antrean Poli**: Nomor antrean dan daftar tunggu yang dikirim ke sistem antrean diambil dari antrean yang masih aktif. (Poliklinik)

### Diperbaiki
- **Rawat Jalan & Rawat Inap**: Tinggi badan dan berat badan dari asesmen IGD tidak tertukar lagi. (Perawat, Dokter)
- **Template Tambahan Biaya**: Hak akses yang dipakai sudah benar (Billing Ralan). (Kasir)

## [2026.08.31] - 2026-08-31
### Ditambahkan
- **Analisa Keperawatan Rawat Jalan**: Pilihan analisa, etiologi, rencana, dan implementasi yang langsung mengisi catatan pemeriksaan. (Perawat)

### Diubah
- **Riwayat Perawatan**: Catatan operasi tampil berurutan, dari pra operasi, pra anestesi, pra induksi, sign-in/sign-out, laporan operasi, hingga transfer pasca operasi. (Dokter, Perawat, Rekam Medis)
- **Pemeriksaan Ralan & Ranap**: Label khusus keperawatan juga berlaku untuk jabatan casemix dan vaksinator, dan diagnosa keperawatan rawat inap memakai kata "berhubungan dengan". (Perawat)
- **Catatan Keseimbangan Cairan**: Isian tidak lagi dibatasi angka saja. (Perawat)
- **Surat Sakit Pihak Ke-2**: Tata letak cetakan menyesuaikan data pasien yang panjang dan posisi tanda tangan. (Poliklinik, Ranap)
- **Bayar Piutang**: Akun bayar ikut tercatat di keterangan pembayaran baru. (Keuangan)

### Diperbaiki
- **Pemeriksaan Ralan & Ranap**: Tanggal dan jam pemeriksaan tidak tertimpa lagi saat data diedit. (Perawat, Dokter)
- **Surat Sakit Pihak Ke-2**: DPJP tidak kosong lagi, karena memakai dokter yang menangani bila data DPJP belum ada. (Poliklinik, Ranap)

## [2026.08.14] - 2026-08-14
### Ditambahkan
- **SBAR / TBaK**: Form pencatatan SBAR, bisa dibuka dari asesmen medis IGD dan tersedia di Rawat Jalan dan Rawat Inap. (Perawat, Dokter)
- **Verifikasi CPPT**: DPJP bisa memverifikasi CPPT dan SBAR, dengan tanda tangan QR di Riwayat Perawatan. (Dokter, Perawat)
- **Riwayat Perawatan**: Pilihan untuk menampilkan SBAR, serta bagian asesmen pra dan pasca bedah. (Dokter, Perawat, Rekam Medis)
- **Analisa Keperawatan Rawat Inap**: Pilihan analisa, etiologi, rencana, dan implementasi yang langsung mengisi catatan pemeriksaan. (Perawat)
- **Kasir Ralan**: Status "PRB Bpjs" untuk pasien BPJS, ditandai warna dan tersedia di filter status. (Poliklinik, Kasir)
- **Laporan Pelayanan Lab**: Filter CITO, Reguler, dan Semua, serta ringkasan rata-rata waktu dan persentase kesesuaian. (Laboratorium)
- **Retur Beli**: Isian "Alasan Retur". (Farmasi)
- **SKDP**: Pembuatan SKDP dikunci bila berbeda dengan tanggal terbit SEP Rawat Jalan, dan tersedia tombol buka kunci. (Pendaftaran)
- **Billing Ranap**: Peringatan bila SKDP Post RI belum terbit. (Kasir Ranap)

### Diubah
- **Pemeriksaan Ralan & Ranap**: Judul isian menyesuaikan jabatan, misalnya "Analisis/Diagnosa Keperawatan" untuk perawat. (Perawat)

### Diperbaiki
- **Risiko Jatuh Dewasa & Anak**: Total skor dan hasilnya sudah benar, dan rekomendasi tindakan terisi otomatis. (Perawat)
- **Peresepan Dokter**: Isian terakhir di tabel resep tidak hilang saat disimpan. (Dokter)
- **Cetakan Time-Out Sebelum Insisi**: Salah ketik pada judul sudah diperbaiki. (Kamar Operasi)

### Catatan Teknis
- Kunci SKDP diatur dengan `LOCKEDSKDP` di setting/database.xml.
