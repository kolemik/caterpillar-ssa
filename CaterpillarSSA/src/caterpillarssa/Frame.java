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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.help.*;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;

/**
 *
 * @author Васькин Александр
 */
public class Frame extends javax.swing.JFrame implements Dialog {

    private Dimension frameSize;
    private JFileChooser chooserOpen;
    private UIManager.LookAndFeelInfo l[];
    private SSAData data;
    private AboutDialog aboutDialog;
    private HelpSet hs;
    private HelpBroker hb;

    /**
     * Creates new form Frame
     */
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
        javaHelp();
        Resource.rusification();
        aboutDialog = new AboutDialog(this, "О программе");
        data = new SSAData();

        openFileItem.addActionListener((new OpenFile(data)));
        analysisItem.addActionListener(new Analysis(data, this, desctop));
        calcItem.addMenuListener(new MenuListener() {

            public void menuSelected(MenuEvent e) {
                if (data.getTimeSeries().isEmpty()) {
                    analysisItem.setEnabled(false);
                } else {
                    analysisItem.setEnabled(true);
                }
                if (data.getPercentList() == null) {
                    reconstructionItem.setEnabled(false);
                } else {
                    reconstructionItem.setEnabled(true);
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
        maximize.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setMaximizeWindows();
            }
        });
        openToolBar.addActionListener(new OpenFile(data));
        backChart.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JInternalFrame frame = desctop.getSelectedFrame();
                if (frame.getName().equals("eigenFunc")) {
                    int currentNum = data.getEigenFuncPage();
                    if (((currentNum - 1) * 4) >= 0) {
                        currentNum--;
                        data.setEigenFuncPage(currentNum);
                        FrameParams.updateInternalFrame(frame, data.getEigenFuncPage(), data.getEigenVecListCharts());
                    }
                }
                if (frame.getName().equals("mainComponent")) {
                    int currentNum = data.getMainCompPage();
                    if (((currentNum - 1) * 4) >= 0) {
                        currentNum--;
                        data.setMainCompPage(currentNum);
                        FrameParams.updateInternalFrame(frame, data.getMainCompPage(), data.getMainCompListCharts());
                    }
                }
            }
        });
        nextChart.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JInternalFrame frame = desctop.getSelectedFrame();
                if (frame.getName().equals("eigenFunc")) {
                    int currentNum = data.getEigenFuncPage();
                    if ((((currentNum * 4) + 4) < data.getEigenVecListCharts().size() && (((currentNum + 1) * 4 + 4)) >= data.getEigenVecListCharts().size())
                            || (((currentNum + 1) * 4) < data.getEigenVecListCharts().size())) {
                        currentNum++;
                        data.setEigenFuncPage(currentNum);
                        FrameParams.updateInternalFrame(frame, data.getEigenFuncPage(), data.getEigenVecListCharts());
                    }
                }
                if (frame.getName().equals("mainComponent")) {
                    int currentNum = data.getMainCompPage();
                    if ((((currentNum * 4) + 4) < data.getMainCompListCharts().size() && (((currentNum + 1) * 4 + 4)) >= data.getMainCompListCharts().size())
                            || (((currentNum + 1) * 4) < data.getMainCompListCharts().size())) {
                        currentNum++;
                        data.setMainCompPage(currentNum);
                        FrameParams.updateInternalFrame(frame, data.getMainCompPage(), data.getMainCompListCharts());
                    }
                }
            }
        });

        reconstructionItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                GroupingDialog groupingDialog = new GroupingDialog(Frame.this, true, data, desctop);
                groupingDialog.setVisible(true);
            }
        });

        exitItem.addActionListener(new ExitListener());
        aboutItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                aboutDialog.setVisible(true);
            }
        });
        helpItem.addActionListener(new CSH.DisplayHelpFromSource(hb));

    }

    public JButton getBackChart() {
        return backChart;
    }

    public JButton getNextChart() {
        return nextChart;
    }

    /**
     * метод, центрирующий приложение на экране
     */
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

    /**
     * инициализация help set
     */
    private void javaHelp() {
        String pathToHS = "/docs/helpset.hs";
        ClassLoader cl = getClass().getClassLoader();
        try {
            URL hsURL = getClass().getResource(pathToHS);
            hs = new HelpSet(cl, hsURL);
            hs.setTitle("Помощь");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        // Create a HelpBroker object for manipulating the help set
        hb = hs.createHelpBroker();
    }

    /**
     * метод используется для изменения иконки приложения
     *
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
            }
            int result = chooserOpen.showDialog(Frame.this, "Открыть");
            String fileName = chooserOpen.getSelectedFile().getPath();
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
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
                    timeSeriesFrame.setName("timeSeries");
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

                    x += data.getFrameDistance();
                    y += data.getFrameDistance();
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

    public void setMaximizeWindows() {
        JInternalFrame[] frames = desctop.getAllFrames();
        for (int i = 0; i < frames.length; i++) {
            try {
                frames[i].setMaximum(true);
            } catch (PropertyVetoException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolBar = new javax.swing.JToolBar();
        openToolBar = new javax.swing.JButton();
        nextToolBar = new javax.swing.JButton();
        cascadeToolBar = new javax.swing.JButton();
        tileToolBar = new javax.swing.JButton();
        maximize = new javax.swing.JButton();
        backChart = new javax.swing.JButton();
        nextChart = new javax.swing.JButton();
        desctop = new javax.swing.JDesktopPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileItem = new javax.swing.JMenu();
        openFileItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        exitItem = new javax.swing.JMenuItem();
        calcItem = new javax.swing.JMenu();
        analysisItem = new javax.swing.JMenuItem();
        reconstructionItem = new javax.swing.JMenuItem();
        infoItem = new javax.swing.JMenu();
        helpItem = new javax.swing.JMenuItem();
        aboutItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Гусеница-SSA");

        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        openToolBar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/folder.png"))); // NOI18N
        openToolBar.setToolTipText("Открыть");
        openToolBar.setFocusable(false);
        openToolBar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openToolBar.setMargin(new java.awt.Insets(3, 3, 3, 3));
        openToolBar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(openToolBar);

        nextToolBar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/next.png"))); // NOI18N
        nextToolBar.setToolTipText("Следующее окно");
        nextToolBar.setFocusable(false);
        nextToolBar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        nextToolBar.setMargin(new java.awt.Insets(3, 3, 3, 3));
        nextToolBar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(nextToolBar);

        cascadeToolBar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/cascade.png"))); // NOI18N
        cascadeToolBar.setToolTipText("Каскад");
        cascadeToolBar.setFocusable(false);
        cascadeToolBar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cascadeToolBar.setMargin(new java.awt.Insets(3, 3, 3, 3));
        cascadeToolBar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(cascadeToolBar);

        tileToolBar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/tile.png"))); // NOI18N
        tileToolBar.setToolTipText("Мозаика");
        tileToolBar.setFocusable(false);
        tileToolBar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tileToolBar.setMargin(new java.awt.Insets(3, 3, 3, 3));
        tileToolBar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(tileToolBar);

        maximize.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/mazimaze.png"))); // NOI18N
        maximize.setToolTipText("Максимизировать окна");
        maximize.setFocusable(false);
        maximize.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        maximize.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(maximize);

        backChart.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/2left.png"))); // NOI18N
        backChart.setToolTipText("Предыдущая группа");
        backChart.setEnabled(false);
        backChart.setFocusable(false);
        backChart.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        backChart.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(backChart);

        nextChart.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/2right.png"))); // NOI18N
        nextChart.setToolTipText("Следующая группа");
        nextChart.setEnabled(false);
        nextChart.setFocusable(false);
        nextChart.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        nextChart.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(nextChart);

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

        exitItem.setText("Выход");
        fileItem.add(exitItem);

        jMenuBar1.add(fileItem);

        calcItem.setText("Вычисление");

        analysisItem.setText("Разложение");
        analysisItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                analysisItemActionPerformed(evt);
            }
        });
        calcItem.add(analysisItem);

        reconstructionItem.setText("Группировка и восстановление");
        calcItem.add(reconstructionItem);

        jMenuBar1.add(calcItem);

        infoItem.setText("Справка");

        helpItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/help_16.png"))); // NOI18N
        helpItem.setText("Помощь");
        infoItem.add(helpItem);

        aboutItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/info_16.png"))); // NOI18N
        aboutItem.setText("О программе");
        infoItem.add(aboutItem);

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
    private javax.swing.JMenuItem aboutItem;
    private javax.swing.JMenuItem analysisItem;
    private javax.swing.JButton backChart;
    private javax.swing.JMenu calcItem;
    private javax.swing.JButton cascadeToolBar;
    private javax.swing.JDesktopPane desctop;
    private javax.swing.JMenuItem exitItem;
    private javax.swing.JMenu fileItem;
    private javax.swing.JMenuItem helpItem;
    private javax.swing.JMenu infoItem;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JButton maximize;
    private javax.swing.JButton nextChart;
    private javax.swing.JButton nextToolBar;
    private javax.swing.JMenuItem openFileItem;
    private javax.swing.JButton openToolBar;
    private javax.swing.JMenuItem reconstructionItem;
    private javax.swing.JButton tileToolBar;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables
}
