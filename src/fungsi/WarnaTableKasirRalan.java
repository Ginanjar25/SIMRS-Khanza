/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fungsi;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Owner
 */
public class WarnaTableKasirRalan extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (row % 2 == 1){
            component.setBackground(new Color(255,244,244));
            component.setForeground(new Color(50,50,50));
        }else{
            component.setBackground(new Color(255,255,255));
            component.setForeground(new Color(50,50,50));
        }
        if(table.getValueAt(row,10).toString().equals("Sudah")){
            component.setBackground(new Color(255/255f, 51/255f, 51/255f, 0.76f));
            component.setForeground(new Color(245,255,245));
        }else if(table.getValueAt(row,10).toString().equals("Batal")){
            component.setBackground(new Color(255,243,109));
            component.setForeground(new Color(120,110,50));
        }else if(table.getValueAt(row,10).toString().equals("Dirujuk")||table.getValueAt(row,10).toString().equals("Meninggal")||table.getValueAt(row,10).toString().equals("Pulang Paksa")){
            component.setBackground(new Color(152,152,156));
            component.setForeground(new Color(245,245,255));
        }else if(table.getValueAt(row,10).toString().equals("Dirawat")){
            component.setBackground(new Color(119,221,119));
            component.setForeground(new Color(245,255,245));
        } else if(table.getValueAt(row,10).toString().equals("Belum") && table.getValueAt(row,9).toString().contains("BPJS")){
            component.setBackground(new Color(0/255f, 160/255f, 42/255f, 0.61f));
            component.setForeground(new Color(0,0,0));
        }else if(table.getValueAt(row,10).toString().equals("Belum") && table.getValueAt(row,9).toString().contains("UMUM")){
            component.setBackground(new Color(51/255f, 133/255f, 255/255f, 0.61f));
            component.setForeground(new Color(0,0,0));
        }
        if(table.getValueAt(row,15).toString().equals("Sudah Bayar")){
            component.setBackground(new Color(50/255f,50/255f,50/255f, 0.76f));
            component.setForeground(new Color(255,255,255));
        }
        return component;
    }

}
