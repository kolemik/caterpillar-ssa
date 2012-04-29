/*
 * ParamsDialog.java
 *
 * Created on 09.03.2012, 16:24:18
 */
package caterpillarssa;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

/**
 *
 * @author Васькин Александр
 */
public class ParamsDialog extends javax.swing.JDialog {

    private Dimension frameSize;
    private UIManager.LookAndFeelInfo l[];
    private SSAData data;
    private JDesktopPane desctop;
    private Frame parent;

    /**
     * Creates new form ParamsDialog
     */
    public ParamsDialog(Frame parent, boolean modal, SSAData data, JDesktopPane desctop) {
        super(parent, modal);
        initComponents();
        this.data = data;
        this.desctop = desctop;
        this.parent = parent;
        centered();
        countPoint.setText(Integer.toString(data.getTimeSeries().size()));
        SpinnerNumberModel model = new SpinnerNumberModel(2, 2, data.getTimeSeries().size() - 1, 1);
        lengthWindowControl.setModel(model);
        okButton.addActionListener(new OKPressListener());
        cancelButton.addActionListener(new CancelListener());
    }

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

    private class OKPressListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            //удаляем все internal frames
            JInternalFrame frames[] = desctop.getAllFrames();
            for (int i = 0; i < frames.length; i++) {
                frames[i].dispose();
            }

            ArrayList listSeries;
            List<String> seriesTitle;
            JPanel panel;
            int firstCharts = 0;

            data.setL((Integer) lengthWindowControl.getValue());
            SpectrumAnalysis.inclosure(data);
            SpectrumAnalysis.singularDecomposition(data);
            SpectrumAnalysis.setMovingAvarege(data);
            SpectrumAnalysis.averagedCovariance(data);
            SpectrumAnalysis.functionEigenValue(data);

            JInternalFrame secondMomentFrame = new JInternalFrame("Вторые моменты", true, true, true, true);

            listSeries = new ArrayList();
            seriesTitle = new ArrayList<String>();
            listSeries.add(data.getSMA());
            seriesTitle.add("Средние");
            ChartPanel chart = XYChart.createChart(listSeries, "Скользящие средние", seriesTitle, "", true);

            listSeries = new ArrayList();
            seriesTitle = new ArrayList<String>();
            listSeries.add(data.getCov());
            seriesTitle.add("Осреднённые ковариации");
            ChartPanel avgChart = XYChart.createChart(listSeries, "Осреднённые ковариации", seriesTitle, "", true);
            final XYPlot plotAvg = avgChart.getChart().getXYPlot();
            NumberAxis rangeAxisCov = (NumberAxis) plotAvg.getRangeAxis();
            rangeAxisCov.setRange((Double) Collections.min(data.getCov()), (Double) Collections.max(data.getCov()));
            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, chart, avgChart);
            secondMomentFrame.add(splitPane);
            desctop.add(secondMomentFrame);
            FrameParams.setInternalFrameParams(secondMomentFrame, desctop, data);

            listSeries = new ArrayList();
            seriesTitle = new ArrayList<String>();
            listSeries.add(data.getPercentList());
            listSeries.add(data.getAccruePercentList());
            seriesTitle.add("Проценты собственных чисел");
            seriesTitle.add("Накопленные проценты");
            ChartPanel percentChart = XYChart.createChart(listSeries, "Собственные числа в процентах", seriesTitle, "", true);
            JInternalFrame percentFrame = InternalFrame.createInternalFrame(percentChart, "Собственные числа в процентах");
            desctop.add(percentFrame);
            FrameParams.setInternalFrameParams(percentFrame, desctop, data);

            listSeries = new ArrayList();
            seriesTitle = new ArrayList<String>();
            listSeries.add(data.getSqrtEigenValue());
            listSeries.add(data.getLgEigenValue());
            seriesTitle.add("Корни из собственных чисел");
            seriesTitle.add("Логарифмы собственных чисел");
            ChartPanel funcChart = XYChart.createChart(listSeries, "Функции собственных чисел", seriesTitle, "", true);
            JInternalFrame funcFrame = InternalFrame.createInternalFrame(funcChart, "Функции собственных чисел");
            desctop.add(funcFrame);
            FrameParams.setInternalFrameParams(funcFrame, desctop, data);

            JInternalFrame eigenFuncFrame = new JInternalFrame("Собственные функции", true, true, true, true);
            eigenFuncFrame.setName("eigenFunc");
            eigenFuncFrame.addInternalFrameListener(new InternalFrameAdapter() {

                @Override
                public void internalFrameActivated(InternalFrameEvent e) {
                    parent.getNextChart().setEnabled(true);
                    parent.getBackChart().setEnabled(true);
                }

                @Override
                public void internalFrameDeactivated(InternalFrameEvent e) {
                    parent.getNextChart().setEnabled(false);
                    parent.getBackChart().setEnabled(false);
                }
            });
            panel = new JPanel();
            panel.setLayout(new GridLayout(2, 2));
            List<ChartPanel> eigenVecListCharts = new ArrayList<ChartPanel>();
            setEigenChartList(listSeries, seriesTitle, eigenVecListCharts, data.getEigenVectors());
            data.setEigenVecListCharts(eigenVecListCharts);

            if (eigenVecListCharts.size() >= 4) {
                firstCharts = 4;
            } else {
                firstCharts = eigenVecListCharts.size();
            }
            for (int i = 0; i < firstCharts; i++) {
                panel.add(eigenVecListCharts.get(i));
            }
            eigenFuncFrame.add(panel, BorderLayout.CENTER);
            eigenFuncFrame.setVisible(true);
            desctop.add(eigenFuncFrame);
            FrameParams.setInternalFrameParams(eigenFuncFrame, desctop, data);

            JInternalFrame mainComponentFrame = new JInternalFrame("Главные компоненты", true, true, true, true);
            mainComponentFrame.setName("mainComponent");
            mainComponentFrame.addInternalFrameListener(new InternalFrameAdapter() {

                @Override
                public void internalFrameActivated(InternalFrameEvent e) {
                    parent.getNextChart().setEnabled(true);
                    parent.getBackChart().setEnabled(true);
                }

                @Override
                public void internalFrameDeactivated(InternalFrameEvent e) {
                    parent.getNextChart().setEnabled(false);
                    parent.getBackChart().setEnabled(false);
                }
            });
            panel = new JPanel();
            panel.setLayout(new GridLayout(2, 2));
            List<ChartPanel> mainCompListCharts = new ArrayList<ChartPanel>();
            setMainCompChartList(listSeries, seriesTitle, mainCompListCharts);
            data.setMainCompListCharts(mainCompListCharts);
            if (mainCompListCharts.size() >= 4) {
                firstCharts = 4;
            } else {
                firstCharts = mainCompListCharts.size();
            }
            for (int i = 0; i < firstCharts; i++) {
                panel.add(mainCompListCharts.get(i));
            }
            mainComponentFrame.add(panel, BorderLayout.CENTER);
            mainComponentFrame.setVisible(true);
            desctop.add(mainComponentFrame);
            FrameParams.setInternalFrameParams(mainComponentFrame, desctop, data);
            //setEigenChartList(listSeries, seriesTitle, mainCompListCharts, data.getEigenVectors());

            try {
                mainComponentFrame.setMaximum(true);
                eigenFuncFrame.setMaximum(true);
                funcFrame.setMaximum(true);
                percentFrame.setMaximum(true);
                secondMomentFrame.setMaximum(true);
            } catch (PropertyVetoException ex) {
                ex.printStackTrace();
            }
            ParamsDialog.this.setVisible(false);
        }
    }

    /**
     *
     * @param listSeries коллекция с данными для одного графика
     * @param seriesTitle заголовки графиков
     * @param charts графики
     * @param data коллекция с данными для каждого из графиков (если их
     * несколько на одном InternalFrame)
     */
    private void setEigenChartList(ArrayList listSeries, List<String> seriesTitle, List<ChartPanel> charts, List eigenVec) {
        for (int i = 0; i < eigenVec.size(); i++) {
            BigDecimal percent = new BigDecimal(data.getPercentList().get(i));
            double num = percent.setScale(2, RoundingMode.HALF_EVEN).doubleValue();
            listSeries = new ArrayList();
            seriesTitle = new ArrayList<String>();
            seriesTitle.add("" + (i + 1));
            ArrayList list = (ArrayList) eigenVec.get(i);
            listSeries.add(list);
            ChartPanel chartPanel = XYChart.createChart(listSeries, ("" + (i + 1) + " (" + num + "%)"), seriesTitle, "", true);
            final XYPlot plot = chartPanel.getChart().getXYPlot();
            NumberAxis rangeAxisVec = (NumberAxis) plot.getRangeAxis();
            rangeAxisVec.setRange((Double) Collections.min(list), (Double) Collections.max(list));
            charts.add(chartPanel);
        }
    }

    private void setMainCompChartList(ArrayList listSeries, List<String> seriesTitle, List<ChartPanel> charts) {
        for (int i = 0; i < data.getV().length; i++) {
            BigDecimal percent = new BigDecimal(data.getPercentList().get(i));
            double num = percent.setScale(2, RoundingMode.HALF_EVEN).doubleValue();
            listSeries = new ArrayList();
            seriesTitle = new ArrayList<String>();
            seriesTitle.add("" + (i + 1));
            List<Double> list = new ArrayList<Double>();
            for (int j = 0; j < data.getV()[i].getRowDimension(); j++) {
                for (int k = 0; k < data.getV()[i].getColumnDimension(); k++) {
                    list.add(data.getV()[i].get(j, k));
                }
            }
            listSeries.add(list);
            ChartPanel chartPanel = XYChart.createChart(listSeries, ("" + (i + 1) + " (" + num + "%)"), seriesTitle, "", true);
            final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
            final XYPlot plot = chartPanel.getChart().getXYPlot();
            renderer.setSeriesShapesVisible(0, false);
            plot.setRenderer(renderer);
            NumberAxis rangeAxisVec = (NumberAxis) plot.getRangeAxis();
            rangeAxisVec.setRange((Double) Collections.min(list), (Double) Collections.max(list));
            charts.add(chartPanel);
        }
    }

    private class CancelListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            ParamsDialog.this.setVisible(false);
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lengthWindowControl = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        countPoint = new javax.swing.JLabel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Параметры разложения");
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Параметры окна"));

        jLabel1.setText("Длина окна (L):");

        jLabel2.setText("Исходный ряд (кол. точек):");

        countPoint.setText("jLabel3");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(countPoint))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lengthWindowControl, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(countPoint))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lengthWindowControl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        okButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/tick.png"))); // NOI18N
        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/cancel.png"))); // NOI18N
        cancelButton.setText("Отмена");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(109, Short.MAX_VALUE)
                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton)
                .addGap(103, 103, 103))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(152, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 183, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cancelButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel countPoint;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSpinner lengthWindowControl;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables
}
