package caterpillarssa;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame.JDesktopIcon;

/**
 *
 * @author Васькин Александр
 */
public class Analysis implements ActionListener {

    private SSAData data;
    private java.awt.Frame parent;
    private JDesktopPane desctop;

    public Analysis(SSAData data, java.awt.Frame parent, JDesktopPane desctop) {
        this.data = data;
        this.parent = parent;
        this.desctop = desctop;
    }

    public void actionPerformed(ActionEvent e) {
            ParamsDialog dialog = new ParamsDialog(parent, true, data, desctop);
            dialog.setVisible(true);
    }

}
