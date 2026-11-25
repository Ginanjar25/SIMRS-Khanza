package modif;

import fungsi.WarnaTable;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;

/**
 * Renderer tabel Daftar Permintaan Resep: sama dengan WarnaTable, tetapi baris resep iterasi
 * (kolom Iterasi terisi) diberi latar kuning agar mudah dikenali petugas farmasi.
 */
public class WarnaTableIterasi extends WarnaTable {
    private final int kolomIterasi;

    /** @param kolomIterasi indeks kolom Iterasi pada model tabel */
    public WarnaTableIterasi(int kolomIterasi){
        this.kolomIterasi=kolomIterasi;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column){
        Component component=super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
        if(!isSelected){
            try {
                Object iter=table.getModel().getValueAt(table.convertRowIndexToModel(row),kolomIterasi);
                if(iter!=null&&!iter.toString().equals("")){
                    component.setBackground(new Color(255,236,179));
                }
            } catch (Exception e) {
                // kolom belum ada / baris berubah saat tabel dimuat ulang: pakai warna biasa
            }
        }
        return component;
    }

    /**
     * Teks kolom Iterasi.
     * @param iter        side_db.resep_obat_info.iter pilihan dokter ("0","1","2")
     * @param statusIter  permintaan_resep_iterasi_bpjs.status_iter bila resep ini hasil iterasi ("Iterasi Ke 1/2")
     */
    public static String teks(String iter,String statusIter){
        if(statusIter!=null&&!statusIter.equals("")){
            return statusIter;
        }
        if("1".equals(iter)){
            return "Iterasi 1x";
        }else if("2".equals(iter)){
            return "Iterasi 2x";
        }
        return "";
    }
}
