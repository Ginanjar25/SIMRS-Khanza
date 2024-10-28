package fungsi;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class WarnaTableKasirRalan extends DefaultTableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        // Atur warna dasar untuk baris genap dan ganjil
        if (row % 2 == 1) {
            component.setBackground(new Color(255, 244, 244));
            component.setForeground(new Color(50, 50, 50));
        } else {
            component.setBackground(new Color(255, 255, 255));
            component.setForeground(new Color(50, 50, 50));
        }
        
        // Kondisi berdasarkan nilai di kolom ke-10
        if (table.getValueAt(row, 10).toString().equals("Sudah")) {
            component.setBackground(new Color(255 / 255f, 51 / 255f, 51 / 255f, 0.76f));
            component.setForeground(new Color(245, 255, 245));
        } else if (table.getValueAt(row, 10).toString().equals("Batal")) {
            component.setBackground(new Color(255, 243, 109));
            component.setForeground(new Color(120, 110, 50));
        } else if (table.getValueAt(row, 10).toString().equals("Dirujuk") || 
                   table.getValueAt(row, 10).toString().equals("Meninggal") || 
                   table.getValueAt(row, 10).toString().equals("Pulang Paksa")) {
            component.setBackground(new Color(152, 152, 156));
            component.setForeground(new Color(245, 245, 255));
        } else if (table.getValueAt(row, 10).toString().equals("Dirawat")) {
            component.setBackground(new Color(119, 221, 119));
            component.setForeground(new Color(245, 255, 245));
        } else if (table.getValueAt(row, 10).toString().equals("Belum") && 
                   table.getValueAt(row, 9).toString().contains("BPJS")) {
            component.setBackground(new Color(0 / 255f, 160 / 255f, 42 / 255f, 0.61f));
            component.setForeground(new Color(0, 0, 0));
        } else if (table.getValueAt(row, 10).toString().equals("Belum") && 
                   table.getValueAt(row, 9).toString().contains("UMUM")) {
            component.setBackground(new Color(51 / 255f, 133 / 255f, 255 / 255f, 0.61f));
            component.setForeground(new Color(0, 0, 0));
        }
        
        // Warna khusus untuk kolom 15
        if (table.getValueAt(row, 15).toString().equals("Sudah Bayar")) {
            component.setBackground(new Color(50 / 255f, 50 / 255f, 50 / 255f, 0.76f));
            component.setForeground(new Color(255, 255, 255));
        }
        
        // Kondisi khusus untuk kolom 23, logika ini menimpa pengaturan warna sebelumnya
        if (column == 40) {
            Object jenis_bayar = table.getValueAt(row, 24); // Nilai di kolom ke-24
            Object status = table.getValueAt(row, 31);
            Object skdp = table.getValueAt(row, 40);

            if (jenis_bayar != null) {
                String jenis_bayarString = jenis_bayar.toString();
                String statusString = status.toString();
                String skdpString = skdp.toString();

                if (jenis_bayarString.contains("BPJS") && "Sudah".equals(statusString) && "Belum".equals(skdpString)) {
                    component.setBackground(new Color(255, 255, 0)); // Warna lain untuk kondisi lainnya
                    component.setForeground(Color.BLACK);
                }
            }
        }

        return component;
    }
}
