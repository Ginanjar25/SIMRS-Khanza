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
public class WarnaTableIGD extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (row % 2 == 1){
            component.setBackground(new Color(255,244,244));
        }else{
            component.setBackground(new Color(255,255,255));
        }
        
        if (column == 2) {
            Object cellValue = table.getValueAt(row, 21); // Get the value of the cell in column 25
            Object kdpj = table.getValueAt(row, 19);
            if (cellValue != null) {
                String cellString = cellValue.toString(); // Convert the value to a string
                String StringKdpj = kdpj.toString();
                if ("Sudah".equals(cellString) && "BPJ".equals(StringKdpj)) {
                    component.setBackground(new Color(192, 202, 51)); // Set background color for "JKN"
                }else if ("Belum".equals(cellString) && "BPJ".equals(StringKdpj)) {
                    component.setBackground(new Color(255, 112, 67)); // Set background color for "JKN"
                }
            }
        }
        return component;
    }

}
