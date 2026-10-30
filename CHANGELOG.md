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
- **Validasi Resep Pasien BPJS / Obat Kronis**: Jendela validasi khusus pasien BPJS dengan kolom "Jml Kronis" di samping "Jml Poli". Jml Poli masuk paket rawat jalan, Jml Kronis otomatis tercatat sebagai piutang. Total kronis tampil di samping total. (Farmasi)

### Catatan Teknis
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

### Diperbaiki
- **Bayi Baru Lahir**: Penyimpanan bayi tidak gagal lagi saat nomor RM yang dibuat otomatis ternyata sudah terpakai. (Ranap, VK)

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

### Diubah
- **Cetakan Hasil & Permintaan Lab**: Tata letak baru, tanda tangan/QR diperbarui, dan saran/kesan terbaru ikut tercetak. (Laboratorium)
- **Surat Kontrol BPJS**: Konfirmasi sebelum menghapus. (Pendaftaran)
- **Booking Registrasi**: Cek data ganda kini memperhatikan poli dan dokter yang sama. (Pendaftaran)
- **Peresepan BPJS**: Pengecekan prasyarat lab memakai pemeriksaan HbA1c. (Dokter)
- **Kamar Inap**: Tanggal pulang SEP dikirim ke BPJS saat pasien pulang. (Ranap)

### Diperbaiki
- **Rawat Jalan & Rawat Inap**: Tinggi badan dan berat badan dari asesmen IGD tidak tertukar lagi. (Perawat, Dokter)
- **Template Tambahan Biaya**: Hak akses yang dipakai sudah benar (Billing Ralan). (Kasir)
