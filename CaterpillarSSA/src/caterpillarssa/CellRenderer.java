package caterpillarssa;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author Васькин Александр
 */
class CellRenderer extends JLabel implements ListCellRenderer {
    
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        ListObject obj = (ListObject) value;
        setText(obj.toString());
        if (isSelected) {
            setForeground(Color.RED);
        } else {
            setForeground(Color.BLACK);
        }
        return this;
    }
}
