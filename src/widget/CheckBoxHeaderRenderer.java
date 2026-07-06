/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package widget;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 *
 * @author IT
 */
public class CheckBoxHeaderRenderer extends JCheckBox implements TableCellRenderer {

    public CheckBoxHeaderRenderer() {
        setHorizontalAlignment(SwingConstants.CENTER);
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) {

        JTableHeader header = table.getTableHeader();
        if (header != null) {
            setForeground(header.getForeground());
            setBackground(header.getBackground());
            setFont(header.getFont());
        }

        return this;
    }
}
