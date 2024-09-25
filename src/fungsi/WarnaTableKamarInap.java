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
public class WarnaTableKamarInap extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (row % 2 == 1){
            component.setBackground(new Color(255,244,244));
        }else{
            component.setBackground(new Color(255,255,255));
        } 
        if(table.getValueAt(row,6).toString().equals("UMUM") && table.getValueAt(row,7).toString().contains("Kelas 3")){
            component.setBackground(new Color(255/255f, 51/255f, 51/255f, 0.76f));
//            component.setBackground(new Color(51/255f, 133/255f, 255/255f, 0.61f));
        }
       
        
        
        return component;
    }

}
