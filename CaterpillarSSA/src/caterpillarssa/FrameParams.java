package caterpillarssa;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

/**
 *
 * @author vaskin
 */
public class FrameParams {

	public static void setInternalFrameParams(JInternalFrame iframe, JDesktopPane desctop, SSAData data) {
		// position frame
		int width = desctop.getWidth() / 2;
		int height = desctop.getHeight() / 2;
		iframe.reshape(data.getNextFrameX(), data.getNextFrameY(), width, height);

		iframe.show();



		data.setFrameDistance(iframe.getHeight() - iframe.getContentPane().getHeight());

		// compute placement for next frame

		data.setNextFrameX(data.getNextFrameX() + data.getFrameDistance());
		data.setNextFrameY(data.getNextFrameY() + data.getFrameDistance());
		if (data.getNextFrameX() + width > desctop.getWidth()) {
			data.setNextFrameX(0);
		}
		if (data.getNextFrameY() + height > desctop.getHeight()) {
			data.setNextFrameY(0);
		}
	}
}
