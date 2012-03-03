package caterpillarssa;

import java.awt.BorderLayout;
import javax.swing.JInternalFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

/**
 *
 * @author Васькин Александр
 */
public class InternalFrame {

    public static JInternalFrame createInternalFrame(JFreeChart chart, String title) {
        JInternalFrame iframe = new JInternalFrame(title, true, true, true, true);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setDisplayToolTips(true);
        chartPanel.setInitialDelay(0);
        iframe.setSize(400, 400);
        iframe.add(chartPanel, BorderLayout.CENTER);
        //
        iframe.setVisible(true);
        return iframe;
    }
}