package caterpillarssa;

import java.awt.EventQueue;
import javax.swing.JFrame;

/**
 *
 * @author Васькин Александр
 */
public class Caterpillar {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                Frame f = new Frame();
                f.setExtendedState(JFrame.MAXIMIZED_BOTH);
                f.setVisible(true);
            }
        });

    }
}
