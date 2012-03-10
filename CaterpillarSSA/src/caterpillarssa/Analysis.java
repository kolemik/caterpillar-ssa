package caterpillarssa;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Васькин Александр
 */
public class Analysis implements ActionListener {

    private SSAData data;
    private java.awt.Frame parent;

    public Analysis(SSAData data, java.awt.Frame parent) {
        this.data = data;
        this.parent = parent;
    }

    public void actionPerformed(ActionEvent e) {
            ParamsDialog dialog = new ParamsDialog(parent, true, data);
            dialog.setVisible(true);
    }

}
