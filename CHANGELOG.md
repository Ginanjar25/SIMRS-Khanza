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

## [Rilis]

### Ditambahkan

- **Pemasukan Lain-Lain**: Kwitansi Penerimaan (klik kanan tabel) kini dicetak langsung dari aplikasi tanpa membuka browser. Baris "Untuk Pembayaran" berisi nama kategori dengan isi Keperluan di bawahnya. Bila ada kendala, cetak lewat browser seperti dulu tetap tersedia di menu "Cetak Kwitansi Penerimaan (Web)". (Kasir)
- **Pemasukan Lain-Lain dari Billing Ranap**: Tombol "Pemasukan Lain" di Billing Ranap membuka Pemasukan Lain-Lain dengan pasien terisi, dan isian "Terima Dari" otomatis berisi No. Rawat, No. RM, dan nama pasien. Bila kategori yang dipilih NON CLAIM, muncul tombol klip di samping isian Pemasukan untuk memilih tindakan/paket yang ada di billing pasien. Total tindakan yang dicentang mengganti isi Pemasukan dan nama tindakannya ditambahkan ke Keperluan. (Kasir Ranap)
- **Daftar Tindakan/Paket Non Claim**: Daftar tindakan/paket yang bisa dipilih sebagai pemasukan lain kini bisa ditambah, diganti, dan dihapus sendiri lewat tombol "Daftar Tindakan" di jendela Pilih Tindakan/Paket. (Kasir, Keuangan)
- **Akses RME SATUSEHAT (SSRME)**: Rekam medis nasional pasien dari SATUSEHAT bisa dibuka dari tombol "Akses RME SATUSEHAT" di Rawat Jalan dan Rawat Inap, menu Bridging di IGD, dan tab baru "RME SATUSEHAT" di Riwayat Perawatan. Bila pasien belum memberi izin, muncul halaman persetujuan: minta Kode Akses dari aplikasi SATUSEHAT Mobile pasien, lalu rekam medis terbuka otomatis setelah kode diterima. Untuk kondisi darurat, centang "Kondisi Emergency" lalu isi alasan dan data pengantar/wali. Bila halaman tidak tampil sempurna, pakai tombol "Buka di Browser". (Dokter, Perawat, Rekam Medis)

### Ditambahkan

- **Surat Kontrol BPJS**: Pilihan "5 SKDP Terakhir" untuk menampilkan 5 surat kontrol terbaru dari hasil pencarian, tanpa batas tanggal. Aktif bila kolom pencarian terisi. (Pendaftaran, Poliklinik)

### Diubah

- **Registrasi**: Antrean Mobile JKN pasien BPJS kini langsung dikirim otomatis setelah registrasi disimpan, tanpa membuka jendela Antrian Online. Aplikasi tidak lagi berhenti menunggu jawaban BPJS, jadi petugas bisa langsung lanjut ke pasien berikutnya. Bila pengiriman gagal, server BPJS tidak menjawab dalam 20 detik, atau No. Rujukan/SKDP tidak ditemukan, muncul pesan berisi No. Rawat dan nama pasien; kirim ulang lewat tombol "Kirim Antrol BPJS" yang tetap membuka jendela Antrian Online seperti dulu. (Pendaftaran)
- **Pemasukan Lain-Lain**: Bila kategori yang dipilih kategori naik kelas, isian Keperluan otomatis terisi nama kategori. (Kasir)
- **Resume Ranap**: No. Reg SITB tidak lagi terisi otomatis saat form dibuka. Klik tombol "SITB" di sebelah tombol Pemeriksaan Penunjang Lab, lalu simpan resume. Nomor juga bisa dihapus dari isian dengan tombol "Hapus SITB", dan bila salah bisa sekaligus dihapus dari data pendaftaran TB. (Ranap, Rekam Medis)

### Diperbaiki

- **Cetak Laporan (Java 27)**: Gelang pasien (Permintaan Ranap, Kamar Inap, Registrasi, IGD, Ranap Gabung), Penilaian Awal Keperawatan (Ralan, Anak, Geriatri, Gigi, IGD), Penilaian Awal MCU, Asuhan Gizi, RM 2D, dan SBPK kembali bisa dicetak; sebelumnya muncul pesan "Report Can't view because ... Error evaluating expression". (Semua unit)
- **Semua Modul (Java 27)**: Nominal di tabel dan isian kembali tampil dengan koma ribuan (20,000), bukan titik (20.000), sehingga nilai yang disimpan tidak lagi terpotong (misalnya 20.000 tersimpan 20). (Semua unit)
- **Riwayat Perawatan**: Tampilan tabel yang berantakan pada Pengkajian Awal Medis Rawat Inap Umum dan beberapa asesmen lain sudah dirapikan. (Dokter, Perawat, Rekam Medis)
- **Registrasi**: Pencarian data registrasi kini jauh lebih cepat, dari sekitar 10-15 detik menjadi kurang dari 2 detik. Pencarian beberapa hari sekaligus yang dulu bisa sampai 1 menit kini juga selesai dalam hitungan detik. Isi tabel tidak berubah. (Pendaftaran)

### Catatan Teknis

- Daftar tindakan/paket NON CLAIM disimpan di file cache `./cache/tindakanpemasukanlain.iyem` (JSON, tanpa tabel database), diatur lewat `modif.DlgTindakanPemasukanLain` dengan hak akses Kategori Pemasukan Lain. Bila file belum ada, aplikasi memakai daftar bawaan di `modif.KwitansiUI.TINDAKAN`. File ini tersimpan per PC, jadi setelah diubah salin file tersebut ke folder `cache` PC kasir lain. Kode dicocokkan dengan 7 karakter pertama `kd_jenis_prw` di billing atau `nama_biaya` di tambahan biaya.
- Kwitansi pemasukan lain memakai `report/rptKwitansiPemasukanLainModif.jasper` (dikompilasi dengan JasperReports 6.8.0). Bila jrxml diubah, kompilasi ulang di Jaspersoft Studio.
- Cetak kwitansi desktop tidak lagi membuka form validasi NON CLAIM / BIAYA SELISIH, karena pengecekan tindakan/paket sudah dilakukan saat input lewat tombol klip. `modif.DlgKwitansiNonClaim`, `modif.DlgKwitansiSelisih`, dan `CetakKwitansiPemasukanLain.jenisKategori()` saat ini tidak dipakai. Validasi lama tetap berjalan di menu Web (`billing/LaporanKeuangan.php`).
- Kategori NON CLAIM dan naik kelas dikenali dari nama kategori yang mengandung teks "NON CLAIM" / "NAIK KELAS" (huruf besar/kecil tidak berpengaruh).
- Pencarian `DlgReg.tampil()` tidak lagi LEFT JOIN `referensi_mobilejkn_bpjs`, `side_db.reg_periksa_website`, dan `referensi_mobilejkn_bpjs_batal` (kolom `no_rawat` di tabel tsb tidak ber-index, menyebabkan full scan berulang). Kolom Asal (WEB/JKN/ONSITE) dan Status JKN kini diisi oleh file baru `modif.StatusJknRegistrasi` lewat query terpisah hanya untuk `no_rawat` yang tampil, dengan aturan CASE/IF yang sama persis termasuk baris ganda bila satu `no_rawat` punya lebih dari satu booking. Hasil sudah dibandingkan dengan query lama di data production (1 hari, 10 hari, mode Batal, tanggal dengan booking ganda): identik. Waktu query 1 hari 10 dtk -> 0,3 dtk, 10 hari 60 dtk -> 0,5 dtk.
- Antrol otomatis: file baru `modif.BPJSAntrianOnlineOtomatis`, dipanggil dari `DlgReg.isRegistrasi()` lewat `kirimAntrolBpjs(true)`. Proses bisnis sama dengan dialog `modif.BPJSAntrianOnline` (data pasien `setNoRm2`/`isRawat`, pemilihan rujukan/SKDP/tujuan kunjungan `formWindowOpened`, validasi tombol Simpan, pengiriman `SimpanAntrianOnSite`: request, JSON, kode 200/208/201, insert `referensi_antrean_online`, log `trackerjson` dan `System.out`). Berjalan di thread background (daemon); setting `koneksiDB` dibaca di EDT. Dialog `BPJSAntrianOnline` tidak diubah dan tetap dipakai tombol "Kirim Antrol BPJS" (`kirimAntrolBpjs(false)`). Request antrol otomatis memakai batas waktu tunggu koneksi 10 detik dan jawaban 20 detik (konstanta `WAKTU_TUNGGU_KONEKSI`/`WAKTU_TUNGGU_RESPON`), dipasang lewat `HttpConnectionParams` karena spring-web yang dimuat versi 3.1.0.M2. Hanya request factory yang diganti, interceptor `trackerjson` tetap jalan; `ApiBPJS`/`ApiMobileJKN` dan bridging lain tidak berubah. Bila timeout, tidak bisa terhubung, atau No. Rujukan/SKDP tidak ditemukan, petugas mendapat pesan (di dialog lama kondisi tanpa rujukan/SKDP dianggap sukses tanpa kirim).
- SSRME bisa dinonaktifkan per PC lewat entry `<entry key="AKTIFKANSSRME">no</entry>` di `setting/database.xml` (`koneksiDB.AKTIFKANSSRME()`). Bila entry tidak ada atau bukan `yes`, tombol "Akses RME SATUSEHAT" (Rawat Jalan/Inap), menu Bridging IGD, dan tab "RME SATUSEHAT" Riwayat Perawatan tidak tampil. Untuk mengaktifkan, ubah menjadi `yes` lalu buka ulang form.
- Dukungan JDK 24+ (diuji JDK 26 bawaan NetBeans 31 + JavaFX SDK 25.0.5 di `C:\Users\Public\lib`): `PanelSSRME` memanggil `netscape.javascript.JSObject` lewat reflection (tidak terlihat javac JDK baru untuk target 1.8); `nbproject/project.properties` menambah referensi `jdk.jsobject.jar` (modul ini pindah dari JDK ke JavaFX sejak JDK 24) dan `run.jvmargs=-Djava.library.path=C:\Users\Public\lib` agar dll JavaFX diambil dari folder lib, bukan dari JDK lain di PATH (dll Liberica JDK 15 menyebabkan `NoSuchMethodError: notifyThemeChanged` lalu JVM crash). Di JDK yang membawa JavaFX sendiri (Liberica Full), dll JDK tetap didahulukan.
- JavaFX sebelum 27 melempar `NullPointerException ... "this.sceneState" is null` (`GlassScene.updateSceneState`) saat JFXPanel dilepas lalu ditempel lagi (form ditutup/dibuka): pembaruan skala DPI yang masih antre untuk scene lama yang sudah dibuang. Tidak berpengaruh ke tampilan, tetapi Global Error Handler ikut menyimpannya ke database, jadi `PanelSSRME.pasangPenanganErrorFx()` memasang uncaught exception handler di thread JavaFX yang hanya membuang error tersebut; error lain tetap diteruskan ke handler global. JFXPanel juga baru ditempel ke form setelah scene terpasang.
- SSRME (Juknis V2.0): logika di file baru `modif.PanelSSRME` (POST `/v2/ntl/shl`, bila 403 `CONSENT_REQUIRED` POST `/v2/ntl/chl`, emergency `"type_medical_summary":"EMERGENCY"`; setelah CHL, SHL dicek ulang otomatis tiap 5 detik maks. 10 menit). Dipakai dialog `modif.SatuSehatRekamMedisElektronik` (Rawat Jalan/Inap/IGD) dan tab "RME SATUSEHAT" di `RMRiwayatPerawatan` (tab ditambahkan di konstruktor, bukan di .form; indeks tab 8). Praktisi: user login bila dokter, lalu dokter form pemanggil, lalu DPJP no_rawat/kunjungan terakhir. URL dari setting opsional `URLSSRMESATUSEHAT` di `setting/database.xml`, bila kosong diturunkan dari `URLAUTHSATUSEHAT` (`.../oauth2/v1` -> `.../ssrme`). WebView JavaFX belum punya `crypto.subtle`, jadi dipasang pengganti berbasis Java (`PanelSSRME.KriptoSSRME`: ECDH P-256/P-384, HKDF, HMAC, SHA, AES-GCM) mengikuti RMRiwayatPerawatan Khanza original. Token dan SHLink/verificationUrl tidak dicetak ke log, hanya request_id. Halaman SSRME memakai JavaScript modern yang tidak bisa dijalankan WebView JavaFX 15 (Liberica JDK 15 Full: `SyntaxError` class field). Bila error ini terdeteksi di log console WebView, aplikasi menawarkan membuka halaman di browser, dan selanjutnya CHL/SHL langsung dibuka di browser sampai aplikasi ditutup. Agar tampil di dalam aplikasi, runtime perlu JavaFX lebih baru (misal Liberica JDK 21 Full), uji dulu seluruh aplikasi sebelum mengganti JDK. User data WebView di `%TEMP%\khanza-ssrme-webview` agar `localStorage` tersedia.
- PC RS memakai region Windows "English (Indonesia)" sehingga locale Java `en_ID`. Data CLDR di JDK 15 belum punya `en_ID` (jatuh ke `en`, koma ribuan), sedangkan JDK 27 sudah punya `en_ID` dengan titik ribuan dan koma desimal; opsi `-Djava.locale.providers=COMPAT` juga sudah dihapus sejak JDK 23. Akibatnya `DecimalFormat`/`NumberFormat`/`String.format` tanpa locale eksplisit (misal `validasi.SetAngka` -> "20.000", `SetAngka7` -> "20000,5") lalu dibaca `Double.parseDouble`, sehingga nilainya salah atau gagal. `SIMRSKhanza.main` kini memanggil `Locale.setDefault(Locale.US)` paling awal. `DlgIKBBayi` (bawaan Khanza asli) memanggil `Locale.setDefault(id_ID)` saat cetak Surat Kelahiran (rptSKL, rptSKL2, rptSKL3) sehingga format angka seluruh aplikasi berubah sampai aplikasi ditutup; diganti `param.put("REPORT_LOCALE",locale)` agar locale Indonesia hanya dipakai laporan tersebut. Efek samping: nama bulan/hari dari `SimpleDateFormat` dan laporan Jasper tanpa locale eksplisit memakai bahasa Inggris, sama seperti di PC yang sebelumnya menampilkan koma ribuan.
- 114 laporan `.jrxml` (juga di Khanza asli) memakai `language="groovy"` dengan `groovy-all-2.4.7.jar`. Di JDK 16+ Groovy 2.4 tidak bisa lagi merefleksi class JDK, sehingga ekspresi seperti `$P{penjab}.equals("BPJS")` gagal (`groovy.lang.MissingMethodException: No signature of method: java.lang.String.equals()`). File baru `manifest.mf` di root project (dipakai `manifest.file` NetBeans, Main-Class dan Class-Path tetap ditambahkan otomatis saat build) berisi `Add-Opens: java.base/java.lang java.base/java.util java.base/java.text java.base/java.math java.base/java.io java.sql/java.sql`, setara `--add-opens ...=ALL-UNNAMED` dan hanya berlaku bila dijalankan dengan `java -jar`. Uji isi seluruh 114 laporan Groovy di JDK 27: tanpa Add-Opens 10 laporan gagal yang di JDK 15 berhasil, dengan Add-Opens hasilnya sama dengan JDK 15. Setelah build, cek `META-INF/MANIFEST.MF` di `SIMRSKhanza.jar` berisi baris `Add-Opens`. Solusi permanen (belum dikerjakan): ubah laporan ke `language="java"` dan kompilasi ulang dengan JasperReports 6.8.0. Run dari NetBeans tidak memakai `java -jar` sehingga Add-Opens yang sama ditambahkan di `run.jvmargs` (`nbproject/project.properties`) setelah `-Djava.library.path=C:\Users\Public\lib` (commit bc986ea1b). Run dari NetBeans harus memakai Java Platform JDK 25 Standard (+ OpenJFX 25 jar & DLL di `C:\Users\Public\lib`) atau JDK 27 Full: JDK 25 Standard butuh library path tersebut (tanpa itu memuat DLL JavaFX lain dari PATH, `NoSuchMethodError: notifyThemeChanged`), JDK 25 Full dan JDK 27 Full jalan dengan maupun tanpanya, sedangkan JDK 15 Full crash karena DLL OpenJFX 25 tidak cocok dengan JavaFX 15 bawaannya (`NoSuchMethodError: notifyPreferencesChanged` lalu `EXCEPTION_ACCESS_VIOLATION`).
- Disarankan (belum dijalankan) menambah index biasa, BUKAN UNIQUE (`referensi_mobilejkn_bpjs` punya `no_rawat` ganda): `referensi_mobilejkn_bpjs(no_rawat)`, `side_db.reg_periksa_website(no_rawat)`, `reg_periksa(tgl_registrasi, jam_reg)`. Jalankan di jam sepi dengan `SET SESSION lock_wait_timeout=5` dan `ALGORITHM=INPLACE, LOCK=NONE`. Index `reg_periksa` memengaruhi rencana query ratusan query lain, uji dulu di database development.
- Rujukan Keluar VClaim + Satu Sehat: form baru `modif.BPJSRujukanKeluarSatuSehat` (salinan `bridging.BPJSRujukanKeluar`, judul "Data Rujukan Keluar VClaim Satu Sehat"), dibuka dari DlgReg klik kanan > Bridging > "Rujuk Keluar BPJS Satu Sehat" (tepat di bawah "Rujuk Keluar BPJS", menu ditambahkan di `initRegistrasi`, bukan di blok NetBeans). `bridging.BPJSRujukanKeluar` dikembalikan ke versi asli (tidak diubah), jadi menu "Rujuk Keluar BPJS" di Registrasi, IGD, Kamar Inap, Kasir Ralan, SEP dan menu utama tetap alur lama. Di form baru, tombol PPK Rujukan mencari faskes lewat Satu Sehat, Simpan mengirim blok `satuSehatRujukan` ke `URL_INSERT`, Hapus memakai `URL_HAPUS` untuk rujukan yang punya data Satu Sehat (rujukan lama tetap lewat `/Rujukan/delete`). Rujukan yang dibuat langsung dari form SEP (`BPJSDataSEP`) belum diubah. Integrasi Satu Sehat hanya untuk Tipe Rujukan Penuh; Partial dan Rujuk Balik tetap lewat `/Rujukan/2.0/insert` dan referensi faskes lama. Kriteria rujukan: tepat satu dipilih (aturan Satu Sehat: hanya boleh salah satu dari Terapi, Tindakan Medis, atau Upaya Diagnosis; kolom Pilih bekerja seperti radio), Tindakan Medis (type text) wajib kode ICD-9 bila dipilih (dicari lewat `BPJSCekReferensiProsedur`), tidak dipilih dikirim `valueString` kosong. Validasi kesesuaian data sebelum kriteria diambil: SEP milik `no_rawat` yang sama, `bridging_sep.no_kartu` = `pasien.no_peserta`, dan `subject.reference` encounter di Satu Sehat (GET `Encounter/{id}`) = `Patient/{ID IHS pasien}`. Setelah simpan muncul No. Rujukan VClaim, No. Rujukan Satu Sehat, dan ServiceRequest ID; tabel form ditambah kolom "No.Rujukan SatuSehat" (kolom terakhir, LEFT JOIN `bridging_rujukan_bpjs_satusehat` lewat primary key `no_rujukan`). `tglRencanaKunjungan` GetFaskesRujukan dikirim `yyyy-MM-dd` (sudah dikonfirmasi). Surat rujukan `report/rptBridgingRujukanBPJS.jrxml` ditambah baris "No.Rujukan Satu Sehat" (LEFT JOIN tabel yang sama), hanya tercetak bila ada; dikompilasi dengan JasperReports 6.8.0 (compiler javac). Sebelum diubah, isi `.jasper` lama sudah dicek sama dengan `.jrxml` di repo. File baru `modif.RujukanSatuSehatVClaim` berisi fungsi pendukung sesuai dokumentasi BPJS "INTEGRASI SATU SEHAT RUJUKAN" folder "RUJUKAN RS via VCLAIM": cek encounter (`satu_sehat_encounter`) dan ID IHS pasien/dokter lewat NIK (`SatuSehatCekNIK`), `GetKriteriaRujukan`, jendela isian kriteria + wilayah jejaring, `GetFaskesRujukan` + jendela pilih faskes, blok JSON `satuSehatRujukan` untuk Insert/Delete, serta simpan/hapus ke tabel baru `bridging_rujukan_bpjs_satusehat` (insert memakai daftar kolom). Tabel `bridging_rujukan_bpjs` tidak diubah. Jendela kriteria dan pilih faskes dipisah ke `modif.DlgKriteriaRujukanSatuSehat` dan `modif.DlgFaskesRujukanSatuSehat` dengan tampilan gaya project (widget.InternalFrame berjudul, widget.Table, widget.ComboBox provinsi + kabupaten/kota, tombol Pilih/Cari ICD-9/Keluar berikon), menggantikan JOptionPane/JDialog polos; `pilihKriteria`/`pilihFaskes` di helper tetap sama sehingga form tidak perlu diubah. Panel log API untuk UAT (permintaan BPJS): jendela mengambang `modif.DlgLogApiRujukan` (selalu di atas, bisa digeser dan diubah ukurannya, tetap bisa dipakai saat jendela modal terbuka, ikut tampil/sembunyi bersama form, muncul lagi otomatis bila ditutup lalu ada hit baru; tombol Salin, Bersihkan, Tutup) yang menampilkan method, URL, body request dan response (response BPJS sudah didekripsi dan dirapikan, termasuk hit yang gagal) untuk GET Encounter Satu Sehat, GetKriteriaRujukan, GetFaskesRujukan, Insert, Update dan Hapus rujukan.  `user_key`/secret tidak ditampilkan. Aktif hanya bila kunci `TAMPILLOGAPIRUJUKAN`=`yes` di `setting/database.xml` (development sudah diisi); bila tidak ada, jendela log tidak dibuat, jadi production cukup tidak menambahkan kunci ini. Pencarian faskes lama (Partial/Rujuk Balik), referensi ICD-9, dan cek NIK pasien/dokter Satu Sehat lewat dialog/kelas lain belum ikut tercatat. Endpoint Satu Sehat memakai service terpisah `vclaim-sisrute-rest`, bukan base URL VClaim biasa: base URL dibaca helper dari kunci baru `URLAPIBPJSSISRUTE` di `setting/database.xml` (development `https://apijkn-dev.bpjs-kesehatan.go.id/vclaim-sisrute-rest/api/v1.0`, info BPJS 13-11-2026; production menunggu info BPJS, wajib ditambahkan sebelum dipakai), path sesuai dokumentasi di konstanta `URL_KRITERIA` (`/Rujukan/GetKriteriaRujukan`), `URL_FASKES` (`/Rujukan/GetFaskesRujukan`), `URL_INSERT` (`/Rujukan/Insert`), `URL_HAPUS` (`/Rujukan/Delete`). Service ini wajib `Content-Type: application/json` (form-urlencoded ditolak 415); rujukan non Satu Sehat tetap form-urlencoded ke VClaim biasa. Pesan error HTTP 4xx/5xx diambil dari `metaData.message` (`RujukanSatuSehatVClaim.pesanError`), termasuk saat hapus rujukan yang sebelumnya diam bila gagal. Catatan uji 12-11-2026 di `vclaim-rest-dev`: kriteria/faskes tidak ada (405) dan `/Rujukan/2.0/insert` menerima blok `satuSehatRujukan` tanpa mengembalikan `noRujukanSatuSehat`. Uji 13-11-2026 di `vclaim-sisrute-rest`: `GetKriteriaRujukan` diteruskan ke Satu Sehat dan dijawab "Data faskes dengan Org ID ... tidak ditemukan, silahkan konfirmasi petugas SATUSEHAT Rujukan" (Organization ID RS belum terdaftar di SATUSEHAT Rujukan). Dengan kode faskes angka contoh dokumentasi (`100005239`) kriteria dan faskes berhasil (200), jadi `kodeFaskesSatuSehat` harus kode faskes angka (format Organization ID production), bukan Organization ID staging berbentuk UUID. Format response development berbeda dari dokumentasi dan sudah ditangani helper: body dikirim `charset=utf-16` tanpa BOM (dibaca lewat `RujukanSatuSehatVClaim.teks`, juga untuk response Insert/Delete di form), wilayah jejaring berbentuk Questionnaire `JejaringWilayah` (linkId 1.1 Provinsi, 1.2 Kabupaten/Kota) sehingga jendela kriteria ditambah pilihan Kabupaten/Kota opsional dan provinsi default dari `PROPINSISATUSEHAT`, `kodeFaskesSatuSehat` faskes tujuan diawali `Organization/` (dibuang sebelum disimpan/dikirim), ada field tambahan `idKriteriaRujukan`, `idFaskesRujukan`, `sumberData`, `isRbkp` (belum dipakai), dan `linkId` kriteria berbeda per diagnosa sehingga jawaban kriteria harus memakai `linkId` dari panggilan GetKriteriaRujukan yang sama. Aturan tanggal dari BPJS: `tglRujukan` harus sama dengan tanggal SEP, `tglRencanaKunjungan` paling lambat 7 hari dari `tglRujukan`, dan GetFaskesRujukan menolak `tglRencanaKunjungan` sebelum hari ini; `tglRencanaKunjungan` hari ini (diuji 13-11-2026 jam 21:00) ditolak HTTP 500 hanya dengan "Response Satu Sehat: Task/<id>" sedangkan besoknya berhasil, sehingga pesan tersebut diberi petunjuk mencoba tanggal berikutnya (uji dengan SEP lama hanya bisa dengan tanggal faskes berbeda dari tanggal insert). Kode faskes asal rujukan dibaca dari kunci opsional baru `IDSATUSEHATRUJUKAN` di `setting/database.xml` (teks biasa, tanpa enkripsi), bila kosong memakai `IDSATUSEHAT`. Kunci ini hanya perlu di development: kredensial Satu Sehat staging terikat ke Organization ID staging (UUID) sehingga Encounter dengan kode resmi ditolak ("wrong organization ID provided by serviceProvider", RuleNumber 10124), sedangkan SATUSEHAT Rujukan hanya mengenal kode resmi. Di production kunci ini tidak perlu ditambahkan karena `IDSATUSEHAT` production sudah kode resmi. Development: `IDSATUSEHAT` tetap Organization ID staging, `IDSATUSEHATRUJUKAN`=`100027474` (kode resmi RS, didapat dari daftar GetFaskesRujukan untuk PPK `0184R018`); Encounter dan kriteria rujukan sudah diuji jalan bersamaan. Uji Insert development 13-11-2026 dengan kode resmi `100027474`: Insert `vclaim-sisrute-rest` `/Rujukan/Insert` berhasil, No. Rujukan VClaim `0184R0180126B000137`, No. Rujukan Satu Sehat `35040682611131001`, ServiceRequest `2b8d1369-d1c8-4ca5-80c0-c5a3fd530f75`, tersimpan di `bridging_rujukan_bpjs_satusehat` dan tercetak di surat rujukan. Kolom `id_pasien_ihs`, `id_dokter_ihs`, `kd_faskes_ss_asal`, `kd_faskes_ss_tujuan` di `bridging_rujukan_bpjs_satusehat` harus `varchar(40)` karena ID Satu Sehat staging berupa UUID 36 karakter (sudah diubah di `sik_dev`, jalankan juga di production: `ALTER TABLE bridging_rujukan_bpjs_satusehat MODIFY id_pasien_ihs varchar(40) NOT NULL, MODIFY id_dokter_ihs varchar(40) NOT NULL, MODIFY kd_faskes_ss_asal varchar(40) NOT NULL, MODIFY kd_faskes_ss_tujuan varchar(40) NOT NULL;`).

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
