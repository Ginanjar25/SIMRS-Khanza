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
public class WarnaTableKasirRalan1 extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (row % 2 == 1){
            component.setBackground(new Color(255,244,244));
        }else{
            component.setBackground(new Color(255,255,255));
        } 
        if (column == 2) {
            Object cellValue = table.getValueAt(row, 25); // Get the value of the cell in column 25
            if (cellValue != null) {
                String cellString = cellValue.toString(); // Convert the value to a string
                if ("JKN".equals(cellString)) {
                    component.setBackground(new Color(0, 165, 82)); // Set background color for "JKN"
                } else if ("WEB".equals(cellString)) {
                    component.setBackground(new Color(51, 153, 255)); // Set background color for "WEB"
                } else if ("ONSITE".equals(cellString)) {
                    component.setBackground(new Color(153, 153, 153)); // Set background color for "WEB"
                }
            }
        }
        
        if (column == 3) {
            Object cellValue = table.getValueAt(row, 24); // Get the value of the cell in column 25
            Object kdpj = table.getValueAt(row, 12);
            if (cellValue != null) {
                String cellString = cellValue.toString(); // Convert the value to a string
                String StringKdpj = kdpj.toString();
                if ("BRD".equals(cellString) && "BPJS".equals(StringKdpj)) {
                    component.setBackground(new Color(102, 204, 0)); // Set background color for "JKN"
                }else if ("VCL".equals(cellString) && "BPJS".equals(StringKdpj)) {
                    component.setBackground(new Color(255, 204, 0)); // Set background color for "JKN"
                }
            }
        }
        
        if (column == 4) {
            Object cellValue = table.getValueAt(row, 26); // Get the value of the cell in column 25
            if (cellValue != null) {
                String cellString = cellValue.toString(); // Convert the value to a string
                if ("Belum".equals(cellString)) {
                    component.setBackground(new Color(153, 153, 255)); // Set background color for "JKN"
                }else if ("Checkin".equals(cellString)) {
                    component.setBackground(new Color(0, 204, 204)); // Set background color for "JKN"
                }else if ("Batal".equals(cellString)) {
                    component.setBackground(new Color(255, 51, 51)); // Set background color for "JKN"
                }
            }
        }
        return component;
    }

}
