# CLAUDE.md

Panduan untuk Claude Code saat bekerja di repo SIMRS Khanza RSPW (fork dari mas-elkhanza/SIMRS-Khanza).

## Project

- Aplikasi desktop Java Swing (NetBeans, Ant), target Java 1.8. Database MariaDB 10.4 (InnoDB, charset utama latin1).
- Branch kerja: `dev-fo`. `master` = mendekati repo asli Khanza; pakai `git show master:<file>` untuk membandingkan dengan versi asli, jangan menaruh salinan file asli di `src/` (bentrok nama class saat build).
- Kode tambahan/modifikasi RSPW ditaruh di package `modif` (`src/modif/`), bukan di package asli, agar mudah dipisahkan dari kode upstream.
- Semua perubahan dicatat di `CHANGELOG.md` bagian `[Belum Rilis]` sesuai panduan di bagian atas file tersebut.

## Gaya kode

Ikuti gaya project agar mudah dibaca tim:
- Penulisan rapat: `a=b`, `if(x){`, `}else{`, `for(Object[] baris:daftar){`.
- Koneksi `koneksiDB.condb()`, field `ps`/`rs` dengan pola `try { ... } catch (Exception e) { System.out.println("Notifikasi : "+e); } finally{ close }`.
- Nama variabel/method bahasa Indonesia, Javadoc singkat bahasa Indonesia. Contoh acuan: `src/modif/CetakKwitansiPemasukanLain.java`, `src/modif/StatusJknRegistrasi.java`.
- Ubah seperlunya. Perubahan besar pada form NetBeans (misal mengubah alur jadi SwingWorker) harus didiskusikan dulu.

## Database

- **Jangan menambah kolom** pada tabel yang sudah ada tanpa mengecek semua insert-nya: `fungsi.sekuel` membentuk `insert into <tabel> values(...)` tanpa daftar kolom, sehingga kolom baru membuat insert gagal (column count doesn't match). Menambah index biasa aman.
- Index harus index biasa, bukan UNIQUE, kecuali data sudah dipastikan unik (contoh: `referensi_mobilejkn_bpjs.no_rawat` punya duplikat).
- DDL di production: jam sepi, `SET SESSION lock_wait_timeout=5`, `ALGORITHM=INPLACE, LOCK=NONE`, siapkan rollback. Uji dulu di database development.
- `setting/database.xml` berisi kredensial terenkripsi (`AESsecurity.EnkripsiAES`). Jangan menampilkan isi kredensial. Konfirmasi dulu database mana yang dituju (development/production) sebelum menjalankan apa pun selain SELECT.
- Query cache server aktif: saat mengukur waktu query pakai `SQL_NO_CACHE` dan jalankan beberapa kali.
- `side_db.reg_periksa_website.no_rawat` utf8mb4, sedangkan `reg_periksa.no_rawat` latin1.

## Catatan modul

- **DlgReg.tampil()**: jangan menambah LEFT JOIN ke `referensi_mobilejkn_bpjs`, `side_db.reg_periksa_website`, atau `referensi_mobilejkn_bpjs_batal` di query utama (tanpa index `no_rawat`, pencarian jadi 10 detik - 1 menit). Kolom Asal & Status JKN diisi `modif.StatusJknRegistrasi.lengkapi(...)`; indeks kolom tabel ada di konstanta `KOLOM_NO_RAWAT`, `KOLOM_STTS`, `KOLOM_ASAL`, `KOLOM_STATUS_JKN` di `DlgReg`. Bila urutan kolom `tabMode` berubah, sesuaikan konstanta tersebut.

## Kompilasi cepat (tanpa build NetBeans penuh)

```
javac -nowarn -encoding UTF-8 -cp "build/classes;dist/lib/*" -d <folder-output> src/modif/StatusJknRegistrasi.java src/simrskhanza/DlgReg.java
```
Saat menjalankan program uji yang memakai `EnkripsiAES`, taruh `dist/lib/commons-codec-1.10.jar` sebelum `dist/lib/*` di classpath (ada versi commons-codec lama yang bentrok).
