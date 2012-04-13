/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Frame.java
 *
 * Created on 27.02.2012, 22:41:44
 */
package caterpillarssa;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
//import org.jfree.d

/**
 *
 * @author Васькин Александр
 */
public class Frame extends javax.swing.JFrame {

	private Dimension frameSize;
	private JFileChooser chooserOpen;
	private UIManager.LookAndFeelInfo l[];
	private SSAData data;


	/** Creates new form Frame */
	public Frame() {
		initComponents();
		centered();
		this.setIconImage(getImage());
		//вид приложения
		l = UIManager.getInstalledLookAndFeels();
		try {
			//загружаем соответствующий интерфейс
			UIManager.setLookAndFeel(l[1].getClassName());
			//обновляем все элементы графического интерфейса
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception e) {
			e.printStackTrace();
		}

		data = new SSAData();
		//desctop = new JDesktopPane();
		//setContentPane(desctop);

		openFileItem.addActionListener((new OpenFile(data)));
		analysisItem.addActionListener(new Analysis(data, this, desctop));
		calcItem.addMenuListener(new MenuListener() {

			public void menuSelected(MenuEvent e) {
				if (data.getTimeSeries().isEmpty()) {
					analysisItem.setEnabled(false);
				} else {
					analysisItem.setEnabled(true);
				}
			}

			public void menuDeselected(MenuEvent e) {
			}

			public void menuCanceled(MenuEvent e) {
			}
		});
		nextToolBar.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				selectNextWindow();
			}
		});
		tileToolBar.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				tileWindows();
			}
		});
		cascadeToolBar.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				cascadeWindows();
			}
		});
		openToolBar.addActionListener(new OpenFile(data));

	}

	/**
	 * метод, центрирующий приложение на экране
	 */
	private void centered() {
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

	/**
	 * метод используется для изменения иконки приложения
	 * @return объект ImageIcon
	 */
	protected static Image getImage() {
		java.net.URL imgURL = caterpillarssa.Frame.class.getResource("/image/gnibbles.png");
		if (imgURL != null) {
			return new ImageIcon(imgURL).getImage();
		} else {
			return null;
		}
	}

	private class OpenFile implements ActionListener {

		private SSAData timeSeries;

		public OpenFile(SSAData timeSeries) {
			this.timeSeries = timeSeries;
		}

		public void actionPerformed(ActionEvent e) {
			List<Double> timeSeriesList = new ArrayList<Double>();
			List<String> seriesTitle;
			ArrayList listSeries;
			if (chooserOpen == null) {
				chooserOpen = new JFileChooser();
				chooserOpen.setCurrentDirectory(new File("."));
				//FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt", "doc", "docx");
				//chooserOpen.setFileFilter(filter);
			}
			int result = chooserOpen.showDialog(Frame.this, "Открыть");
			String fileName = chooserOpen.getSelectedFile().getPath();
			if (result == JFileChooser.APPROVE_OPTION) {
				try {
					double n;
					FileReader inpt = new FileReader(fileName);
					Scanner scn = new Scanner(inpt);
					while (scn.hasNextDouble()) {
						timeSeriesList.add(scn.nextDouble());
					}
					scn.close();
					inpt.close();
					timeSeries.setTimeSeries(timeSeriesList);
					listSeries = new ArrayList();
					listSeries.add(timeSeriesList);
					seriesTitle = new ArrayList<String>();
					seriesTitle.add("Исходный");
					ChartPanel chart = XYChart.createChart(listSeries, "Временной ряд", seriesTitle, fileName, false);
					JInternalFrame timeSeriesFrame = InternalFrame.createInternalFrame(chart, "Временной ряд");
					final XYPlot plot = chart.getChart().getXYPlot();
					NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
					NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
					rangeAxis.setLowerBound(timeSeriesList.get(0));
					domainAxis.setRange(1, timeSeriesList.size());
					desctop.add(timeSeriesFrame);
					FrameParams.setInternalFrameParams(timeSeriesFrame, desctop, data);
					try {
						timeSeriesFrame.setMaximum(true);
					} catch (PropertyVetoException ex) {
						ex.printStackTrace();
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public void cascadeWindows() {
		int x = 0;
		int y = 0;
		int width = desctop.getWidth() / 2;
		int height = desctop.getHeight() / 2;

		for (JInternalFrame frame : desctop.getAllFrames()) {
			if (!frame.isIcon()) {
				try {
					// try to make maximized frames resizable; this might be vetoed
					frame.setMaximum(false);
					frame.reshape(x, y, width, height);

					x += 650;
					y += 650;
					// wrap around at the desktop edge
					if (x + width > desctop.getWidth()) {
						x = 0;
					}
					if (y + height > desctop.getHeight()) {
						y = 0;
					}
				} catch (PropertyVetoException e) {
				}
			}
		}
	}

	/**
	 * Tiles the non-iconified internal frames of the desktop.
	 */
	public void tileWindows() {
		// count frames that aren't iconized
		int frameCount = 0;
		for (JInternalFrame frame : desctop.getAllFrames()) {
			if (!frame.isIcon()) {
				frameCount++;
			}
		}
		if (frameCount == 0) {
			return;
		}

		int rows = (int) Math.sqrt(frameCount);
		int cols = frameCount / rows;
		int extra = frameCount % rows;
		// number of columns with an extra row

		int width = desctop.getWidth() / cols;
		int height = desctop.getHeight() / rows;
		int r = 0;
		int c = 0;
		for (JInternalFrame frame : desctop.getAllFrames()) {
			if (!frame.isIcon()) {
				try {
					frame.setMaximum(false);
					frame.reshape(c * width, r * height, width, height);
					r++;
					if (r == rows) {
						r = 0;
						c++;
						if (c == cols - extra) {
							// start adding an extra row
							rows++;
							height = desctop.getHeight() / rows;
						}
					}
				} catch (PropertyVetoException e) {
				}
			}
		}
	}

	/**
	 * Brings the next non-iconified internal frame to the front.
	 */
	public void selectNextWindow() {
		JInternalFrame[] frames = desctop.getAllFrames();
		for (int i = 0; i < frames.length; i++) {
			if (frames[i].isSelected()) {
				// find next frame that isn't an icon and can be selected
				int next = (i + 1) % frames.length;
				while (next != i) {
					if (!frames[next].isIcon()) {
						try {
							// all other frames are icons or veto selection
							frames[next].setSelected(true);
							frames[next].toFront();
							frames[i].toBack();
							return;
						} catch (PropertyVetoException e) {
						}
					}
					next = (next + 1) % frames.length;
				}
			}
		}
	}
	
	//public void

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolBar = new javax.swing.JToolBar();
        openToolBar = new javax.swing.JButton();
        nextToolBar = new javax.swing.JButton();
        cascadeToolBar = new javax.swing.JButton();
        tileToolBar = new javax.swing.JButton();
        desctop = new javax.swing.JDesktopPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileItem = new javax.swing.JMenu();
        openFileItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem2 = new javax.swing.JMenuItem();
        calcItem = new javax.swing.JMenu();
        analysisItem = new javax.swing.JMenuItem();
        infoItem = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Гусеница-SSA");

        toolBar.setRollover(true);

        openToolBar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/folder.png"))); // NOI18N
        openToolBar.setFocusable(false);
        openToolBar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openToolBar.setMargin(new java.awt.Insets(3, 3, 3, 3));
        openToolBar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(openToolBar);

        nextToolBar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/next.png"))); // NOI18N
        nextToolBar.setFocusable(false);
        nextToolBar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        nextToolBar.setMargin(new java.awt.Insets(3, 3, 3, 3));
        nextToolBar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(nextToolBar);

        cascadeToolBar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/cascade.png"))); // NOI18N
        cascadeToolBar.setFocusable(false);
        cascadeToolBar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cascadeToolBar.setLabel("");
        cascadeToolBar.setMargin(new java.awt.Insets(3, 3, 3, 3));
        cascadeToolBar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(cascadeToolBar);

        tileToolBar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/tile.png"))); // NOI18N
        tileToolBar.setFocusable(false);
        tileToolBar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tileToolBar.setMargin(new java.awt.Insets(3, 3, 3, 3));
        tileToolBar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(tileToolBar);

        fileItem.setText("Файл");

        openFileItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/folder_16.png"))); // NOI18N
        openFileItem.setText("Открыть");
        openFileItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openFileItemActionPerformed(evt);
            }
        });
        fileItem.add(openFileItem);
        fileItem.add(jSeparator1);

        jMenuItem2.setText("Выход");
        fileItem.add(jMenuItem2);

        jMenuBar1.add(fileItem);

        calcItem.setText("Вычисление");

        analysisItem.setText("Разложение");
        analysisItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                analysisItemActionPerformed(evt);
            }
        });
        calcItem.add(analysisItem);

        jMenuBar1.add(calcItem);

        jMenuItem3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/help_16.png"))); // NOI18N
        jMenuItem3.setText("Помощь");
        infoItem.add(jMenuItem3);

        jMenuItem4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/info_16.png"))); // NOI18N
        jMenuItem4.setText("О программе");
        infoItem.add(jMenuItem4);

        jMenuBar1.add(infoItem);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(desctop, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(desctop, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void openFileItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openFileItemActionPerformed
    }//GEN-LAST:event_openFileItemActionPerformed

    private void analysisItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_analysisItemActionPerformed
		// TODO add your handling code here:
    }//GEN-LAST:event_analysisItemActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem analysisItem;
    private javax.swing.JMenu calcItem;
    private javax.swing.JButton cascadeToolBar;
    private javax.swing.JDesktopPane desctop;
    private javax.swing.JMenu fileItem;
    private javax.swing.JMenu infoItem;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JButton nextToolBar;
    private javax.swing.JMenuItem openFileItem;
    private javax.swing.JButton openToolBar;
    private javax.swing.JButton tileToolBar;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables
}
