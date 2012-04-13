package caterpillarssa;

import java.awt.BorderLayout;
import javax.swing.JInternalFrame;
import org.jfree.chart.ChartPanel;

/**
 *
 * @author Васькин Александр
 */
public class InternalFrame {

    public static JInternalFrame createInternalFrame(ChartPanel chartPanel, String title) {
        JInternalFrame iframe = new JInternalFrame(title, true, true, true, true);
        iframe.add(chartPanel, BorderLayout.CENTER);
        return iframe;
    }
}