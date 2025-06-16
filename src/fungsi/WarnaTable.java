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
public class WarnaTable extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (table.getColumnCount() >= 22) {
            if (table.getValueAt(row, 22).toString().equals("Tidak Aktif")) {
                component.setBackground(new Color(50 / 255f, 50 / 255f, 50 / 255f, 0.76f));
                component.setForeground(new Color(255, 255, 255));
                return component;
            }else{
                if (row % 2 == 1) {
                    component.setBackground(new Color(255, 244, 244));
                    component.setForeground(new Color(50, 50, 50));
                } else {
                    component.setBackground(new Color(255, 255, 255));
                    component.setForeground(new Color(50, 50, 50));
                }
                return component;
            }
        }
        
        if (row % 2 == 1) {
            component.setBackground(new Color(255, 244, 244));
        } else {
            component.setBackground(new Color(255, 255, 255));
        }
       
        return component;
    }
}
