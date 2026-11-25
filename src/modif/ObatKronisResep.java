package modif;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Query obat kronis (porsi luar paket INA-CBG) sebuah resep, dipakai bagian "OBAT KRONIS" lembar telaah/cetak resep
 * (DlgDaftarPermintaanResep, DlgResepObat) dan pengisian jumlah form Apotek Online BPJS (ApotekBPJSInputResepObat).
 *
 * Obat kronis disimpan DlgCariObatKronis sebagai piutang:
 * - resep biasa (bulan ke-1): nota_piutang = no_rawat, tanpa nomor resep;
 * - resep iterasi (bulan ke-2/3, permintaan_resep_iterasi_bpjs): nota_piutang = no_resep iterasi.
 * Untuk resep biasa obat kronis dicocokkan ke resep sebagai berikut:
 * 1. kode barang ada di resep dokter (non racikan atau racikan) resep tersebut, atau
 * 2. kode barang tidak ada di resep dokter mana pun pada kunjungan itu (obat ditambahkan farmasi saat validasi,
 *    mis. Seretide/Symbicort yang hanya diisi Jml Kronis) dan resep tersebut adalah resep yang terakhir divalidasi.
 */
public final class ObatKronisResep {
    private static final String WHERE=
        "where detailpiutang.nota_piutang=? and ("+
            "detailpiutang.kode_brng in (select resep_dokter.kode_brng from resep_dokter where resep_dokter.no_resep=?) "+
            "or detailpiutang.kode_brng in (select resep_dokter_racikan_detail.kode_brng from resep_dokter_racikan_detail where resep_dokter_racikan_detail.no_resep=?) "+
            "or (?=(select resep_obat.no_resep from resep_obat where resep_obat.no_rawat=? order by resep_obat.tgl_perawatan desc,resep_obat.jam desc limit 1) "+
                "and detailpiutang.kode_brng not in (select resep_dokter.kode_brng from resep_dokter inner join resep_obat on resep_obat.no_resep=resep_dokter.no_resep where resep_obat.no_rawat=?) "+
                "and detailpiutang.kode_brng not in (select resep_dokter_racikan_detail.kode_brng from resep_dokter_racikan_detail inner join resep_obat on resep_obat.no_resep=resep_dokter_racikan_detail.no_resep where resep_obat.no_rawat=?))) ";

    /** Untuk cetak resep: nama_brng, aturan_pakai, jumlah, satuan. Parameter: {@link #isiParameter(PreparedStatement, String, String)}. */
    public static final String SQL=
        "select databarang.nama_brng,max(detailpiutang.aturan_pakai) as aturan_pakai,sum(detailpiutang.jumlah) as jumlah,kodesatuan.satuan "+
        "from detailpiutang inner join databarang on detailpiutang.kode_brng=databarang.kode_brng "+
        "inner join kodesatuan on kodesatuan.kode_sat=databarang.kode_sat "+WHERE+
        "group by databarang.kode_brng,databarang.nama_brng,kodesatuan.satuan order by databarang.nama_brng";

    /** Per kode barang RS: kode_brng, aturan_pakai, jumlah. Parameter: {@link #isiParameter(PreparedStatement, String, String)}. */
    public static final String SQL_KODE=
        "select detailpiutang.kode_brng,max(detailpiutang.aturan_pakai) as aturan_pakai,sum(detailpiutang.jumlah) as jumlah "+
        "from detailpiutang "+WHERE+"group by detailpiutang.kode_brng";

    private ObatKronisResep(){}

    /**
     * Mengisi parameter {@link #SQL} / {@link #SQL_KODE} untuk resep noresep pada kunjungan norawat.
     * Nota piutang: no_resep untuk resep iterasi, no_rawat untuk resep biasa.
     */
    public static void isiParameter(PreparedStatement ps,String noresep,String norawat) throws SQLException {
        ps.setString(1,ResepIterasiBPJS.isIterasi(noresep)?noresep:norawat);
        ps.setString(2,noresep);
        ps.setString(3,noresep);
        ps.setString(4,noresep);
        ps.setString(5,norawat);
        ps.setString(6,norawat);
        ps.setString(7,norawat);
    }
}
