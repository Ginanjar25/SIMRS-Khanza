-- Tabel fitur Rujukan Pasien SATUSEHAT (modif.DlgRujukanSatuSehat / modif.SatuSehatRujukan).
-- Hanya membuat tabel BARU, tidak mengubah tabel yang sudah ada. Aman dijalankan ulang (IF NOT EXISTS / INSERT IGNORE).
-- Uji dulu di database development.

-- Proses rujukan keluar (RS sebagai faskes perujuk), satu baris per proses
CREATE TABLE IF NOT EXISTS `satu_sehat_rujukan` (
  `no_rawat` varchar(17) NOT NULL,
  `tgl_rujukan` datetime NOT NULL,
  `jenis` enum('Rawat Inap','Gawat Darurat') NOT NULL,
  `kd_dokter` varchar(20) DEFAULT NULL,
  `id_task_pra` varchar(40) DEFAULT NULL,
  `id_task_kandidat` varchar(40) DEFAULT NULL,
  `id_task_respon_kandidat` varchar(40) DEFAULT NULL,
  `id_careplan` varchar(40) DEFAULT NULL,
  `id_servicerequest` varchar(40) DEFAULT NULL,
  `no_rujukan_satusehat` varchar(40) DEFAULT NULL,
  `id_org_tujuan` varchar(40) DEFAULT NULL,
  `nm_org_tujuan` varchar(150) DEFAULT NULL,
  `status` enum('Pra Permintaan','Pencarian Kandidat','Menunggu Persetujuan','Disetujui','Rujukan Terkirim','Dibatalkan') NOT NULL,
  PRIMARY KEY (`no_rawat`,`tgl_rujukan`),
  KEY `id_servicerequest` (`id_servicerequest`),
  KEY `no_rujukan_satusehat` (`no_rujukan_satusehat`),
  CONSTRAINT `satu_sehat_rujukan_ibfk_1` FOREIGN KEY (`no_rawat`) REFERENCES `reg_periksa` (`no_rawat`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Task persetujuan (referral-approval-request) per faskes tujuan
CREATE TABLE IF NOT EXISTS `satu_sehat_rujukan_task` (
  `id_task` varchar(40) NOT NULL,
  `no_rawat` varchar(17) NOT NULL,
  `tgl_rujukan` datetime NOT NULL,
  `id_org` varchar(40) NOT NULL,
  `nm_org` varchar(150) DEFAULT NULL,
  `kode_bpjs` varchar(20) DEFAULT NULL,
  `jarak` varchar(20) DEFAULT NULL,
  `strata` varchar(30) DEFAULT NULL,
  `id_careplan` varchar(40) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `respon` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id_task`),
  KEY `no_rawat` (`no_rawat`,`tgl_rujukan`),
  CONSTRAINT `satu_sehat_rujukan_task_ibfk_1` FOREIGN KEY (`no_rawat`,`tgl_rujukan`) REFERENCES `satu_sehat_rujukan` (`no_rawat`,`tgl_rujukan`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Rujukan masuk (RS sebagai faskes rujukan) yang sudah dihubungkan ke kunjungan
CREATE TABLE IF NOT EXISTS `satu_sehat_rujukan_masuk` (
  `id_servicerequest` varchar(40) NOT NULL,
  `no_rujukan_satusehat` varchar(40) DEFAULT NULL,
  `id_task` varchar(40) DEFAULT NULL,
  `id_org_perujuk` varchar(40) DEFAULT NULL,
  `nm_org_perujuk` varchar(150) DEFAULT NULL,
  `id_pasien` varchar(40) DEFAULT NULL,
  `no_rawat` varchar(17) DEFAULT NULL,
  `id_encounter` varchar(40) DEFAULT NULL,
  `tgl_hubung` datetime DEFAULT NULL,
  PRIMARY KEY (`id_servicerequest`),
  KEY `no_rawat` (`no_rawat`),
  KEY `no_rujukan_satusehat` (`no_rujukan_satusehat`),
  CONSTRAINT `satu_sehat_rujukan_masuk_ibfk_1` FOREIGN KEY (`no_rawat`) REFERENCES `reg_periksa` (`no_rawat`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Pilihan kode yang belum tersedia daftar resminya. Kode baru yang diketik petugas
-- dengan format "KODE - Nama" otomatis ditambahkan setelah berhasil terkirim.
CREATE TABLE IF NOT EXISTS `satu_sehat_rujukan_referensi` (
  `jenis` enum('Kelompok Layanan','Spesialisasi Layanan','Jenis Tenaga Medis') NOT NULL,
  `kode` varchar(30) NOT NULL,
  `nama` varchar(200) NOT NULL,
  PRIMARY KEY (`jenis`,`kode`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Contoh dari Postman "30. Use Case - Rujukan Pasien"
INSERT IGNORE INTO `satu_sehat_rujukan_referensi` VALUES
('Kelompok Layanan','TK000563','Kelompok Layanan Jantung dan Pembuluh Darah'),
('Spesialisasi Layanan','LY133','Syaraf - Stroke dan Cerebro Vaskuler'),
('Jenis Tenaga Medis','39677007','Internal medicine specialist');

-- Rollback:
-- DROP TABLE IF EXISTS satu_sehat_rujukan_task;
-- DROP TABLE IF EXISTS satu_sehat_rujukan;
-- DROP TABLE IF EXISTS satu_sehat_rujukan_masuk;
-- DROP TABLE IF EXISTS satu_sehat_rujukan_referensi;
