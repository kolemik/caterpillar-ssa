package caterpillarssa;

import java.awt.GridLayout;
import java.util.List;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;

/**
 *
 * @author Васькин Александр
 */
public class FrameParams {

	public static void setInternalFrameParams(JInternalFrame iframe, JDesktopPane desctop, SSAData data) {
		//расположение фрейма
		int width = desctop.getWidth() / 2;
		int height = desctop.getHeight() / 2;
		iframe.reshape(data.getNextFrameX(), data.getNextFrameY(), width, height);
		iframe.show();
		data.setFrameDistance(iframe.getHeight() - iframe.getContentPane().getHeight());
		//вычисляем расположение следующего фрейма
		data.setNextFrameX(data.getNextFrameX() + data.getFrameDistance());
		data.setNextFrameY(data.getNextFrameY() + data.getFrameDistance());
		if (data.getNextFrameX() + width > desctop.getWidth()) {
			data.setNextFrameX(0);
		}
		if (data.getNextFrameY() + height > desctop.getHeight()) {
			data.setNextFrameY(0);
		}
	}
	
	public static void updateInternalFrame(JInternalFrame iframe, int numPage, List<ChartPanel> chartPanels) {
			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(2, 2));
			int length  = numPage * 4 + 4;
			if(length > chartPanels.size()) {
				length = chartPanels.size();
			}
			for (int i = numPage * 4; i < length; i++) {
				panel.add(chartPanels.get(i));
			}
			iframe.getContentPane().removeAll();
			iframe.setContentPane(panel);
	}
}
