-- Apotek Online BPJS: Input Resep, Daftar Resep (Obat ApOL) dan Resep Iterasi dari upstream Khanza (SYNC KHANZA).
-- Menyatukan struktur tabel bridging_resep_apotek_bpjs* RSPW (dipakai modif.ApotekBPJSResepFKTL)
-- dengan upstream (bridging.ApotekBPJSInputResepObat dkk). Tidak ada kolom yang dihapus.
--
-- Semua insert ke tabel ini sudah memakai daftar kolom eksplisit:
--   modif.ApotekBPJSResepFKTL           : bridging_resep_apotek_bpjs (21 kolom RSPW termasuk tgl_entry)
--   bridging.ApotekBPJSInputResepObat   : bridging_resep_apotek_bpjs, _nonracikan, _racikan (kolom upstream)
-- Kolom NOT NULL RSPW diberi DEFAULT agar insert upstream (tanpa kolom tersebut) tidak ditolak STRICT_TRANS_TABLES.
--
-- Jalankan di database development dulu. Di production: jam sepi, cek dulu SHOW CREATE TABLE ketiga tabel,
-- karena kolom tgl_entry mungkin sudah ada (memakai IF NOT EXISTS).

SET SESSION lock_wait_timeout=5;

ALTER TABLE `bridging_resep_apotek_bpjs`
  MODIFY COLUMN `kdpoli` varchar(15) NOT NULL DEFAULT '',
  MODIFY COLUMN `nmpoli` varchar(50) NOT NULL DEFAULT '',
  MODIFY COLUMN `kdjenis` enum('1. Obat PRB','2. Obat Kronis Blm Stabil','3. Obat Kemoterapi','2. Obat Kronis Belum Stabil') DEFAULT NULL,
  MODIFY COLUMN `kodedpjp` varchar(10) NOT NULL DEFAULT '',
  MODIFY COLUMN `nmdpjp` varchar(100) NOT NULL DEFAULT '',
  MODIFY COLUMN `iterasi` enum('0. Non Iterasi','1. Iterasi','0. Tanpa Iterasi','1. Iterasi 1x','2. Iterasi 2x') NOT NULL,
  MODIFY COLUMN `no_kartu` varchar(25) NOT NULL DEFAULT '',
  MODIFY COLUMN `nama_pasien` varchar(100) NOT NULL DEFAULT '',
  MODIFY COLUMN `nmppkpelayanan` varchar(200) NOT NULL DEFAULT '',
  MODIFY COLUMN `byTagRsp` double NOT NULL DEFAULT 0,
  MODIFY COLUMN `byVerRsp` double NOT NULL DEFAULT 0,
  MODIFY COLUMN `status` enum('Piutang','Non Piutang') NOT NULL DEFAULT 'Non Piutang',
  ADD COLUMN IF NOT EXISTS `tgl_entry` datetime DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `no_resep` varchar(14) DEFAULT NULL,
  ADD KEY IF NOT EXISTS `no_resep` (`no_resep`),
  ALGORITHM=INPLACE, LOCK=NONE;

ALTER TABLE `bridging_resep_apotek_bpjs_nonracikan`
  MODIFY COLUMN `signa1` varchar(10) DEFAULT NULL,
  MODIFY COLUMN `signa2` varchar(10) DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `no_resep` varchar(14) DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `kode_brng_apotek_bpjs` varchar(15) DEFAULT NULL,
  ADD KEY IF NOT EXISTS `no_resep` (`no_resep`),
  ADD KEY IF NOT EXISTS `kode_brng_apotek_bpjs` (`kode_brng_apotek_bpjs`),
  ALGORITHM=INPLACE, LOCK=NONE;

ALTER TABLE `bridging_resep_apotek_bpjs_racikan`
  MODIFY COLUMN `nomor_racik` varchar(6) DEFAULT NULL,
  MODIFY COLUMN `signa1` varchar(10) DEFAULT NULL,
  MODIFY COLUMN `signa2` varchar(10) DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `no_resep` varchar(14) DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `kode_brng_apotek_bpjs` varchar(15) DEFAULT NULL,
  ADD KEY IF NOT EXISTS `no_resep` (`no_resep`),
  ADD KEY IF NOT EXISTS `kode_brng_apotek_bpjs` (`kode_brng_apotek_bpjs`),
  ALGORITHM=INPLACE, LOCK=NONE;

-- Tabel baru (sama dengan upstream Khanza)
CREATE TABLE IF NOT EXISTS `permintaan_resep_iterasi_bpjs` (
  `no_resep_awal` varchar(14) NOT NULL,
  `no_resep` varchar(14) NOT NULL,
  `status_iter` enum('Iterasi Ke 1','Iterasi Ke 2') DEFAULT NULL,
  PRIMARY KEY (`no_resep`),
  KEY `no_resep_awal` (`no_resep_awal`,`status_iter`),
  CONSTRAINT `permintaan_resep_iterasi_bpjs_ibfk_1` FOREIGN KEY (`no_resep`) REFERENCES `resep_obat` (`no_resep`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `permintaan_resep_iterasi_bpjs_ibfk_2` FOREIGN KEY (`no_resep_awal`) REFERENCES `bridging_resep_apotek_bpjs` (`no_resep`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Rollback (jalankan hanya bila belum ada data dari form baru; nilai enum baru harus belum terpakai):
-- DROP TABLE IF EXISTS permintaan_resep_iterasi_bpjs;
-- ALTER TABLE bridging_resep_apotek_bpjs_racikan DROP KEY kode_brng_apotek_bpjs, DROP KEY no_resep, DROP COLUMN kode_brng_apotek_bpjs, DROP COLUMN no_resep,
--   MODIFY COLUMN nomor_racik varchar(3) DEFAULT NULL, MODIFY COLUMN signa1 varchar(5) DEFAULT NULL, MODIFY COLUMN signa2 varchar(5) DEFAULT NULL;
-- ALTER TABLE bridging_resep_apotek_bpjs_nonracikan DROP KEY kode_brng_apotek_bpjs, DROP KEY no_resep, DROP COLUMN kode_brng_apotek_bpjs, DROP COLUMN no_resep,
--   MODIFY COLUMN signa1 varchar(5) DEFAULT NULL, MODIFY COLUMN signa2 varchar(5) DEFAULT NULL;
-- ALTER TABLE bridging_resep_apotek_bpjs DROP KEY no_resep, DROP COLUMN no_resep,
--   MODIFY COLUMN kdjenis enum('1. Obat PRB','2. Obat Kronis Blm Stabil','3. Obat Kemoterapi') DEFAULT NULL,
--   MODIFY COLUMN iterasi enum('0. Non Iterasi','1. Iterasi') NOT NULL;
--   (tgl_entry dibiarkan karena dipakai modif.ApotekBPJSResepFKTL; DEFAULT kolom NOT NULL tidak perlu dikembalikan)
