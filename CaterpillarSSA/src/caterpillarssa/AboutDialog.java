package caterpillarssa;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author Васькин Александр
 */
public class AboutDialog extends JDialog implements Dialog {

	private Dimension frameSize;
	private UIManager.LookAndFeelInfo l[];
	private boolean flag;

	public AboutDialog(JFrame owner, String title) {
		super(owner, title, true);
		this.setSize(590, 350);
		centered();
		l = UIManager.getInstalledLookAndFeels();
		try {
			//загружаем соответствующий интерфейс
			UIManager.setLookAndFeel(l[1].getClassName());
			//обновляем все элементы графического интерфейса
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ImageComponent component = new ImageComponent();
		this.add(component);
		JPanel p = new JPanel();
		JButton b = new JButton("OK");
		b.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				AboutDialog.this.setVisible(false);
			}
		});
		p.add(b);
		this.add(BorderLayout.SOUTH, p);


	}

	public void centered() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frameSize = this.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		this.setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);
	}
	
}
