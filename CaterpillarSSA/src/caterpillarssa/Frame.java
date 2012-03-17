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
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
//import org.jfree.d

/**
 *
 * @author Васькин Александр
 */
public class Frame extends javax.swing.JFrame {

    private Dimension frameSize;
    private JFileChooser chooserOpen;
    private UIManager.LookAndFeelInfo l[];
    private JDesktopPane desctop;
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
        /*JToolBar toolbar = new JToolBar("Toolbar", JToolBar.HORIZONTAL);
        JButton b =  new JButton(new ImageIcon("folder_32.png"));
        toolbar.add(b);
        this.getContentPane().add(toolbar, BorderLayout.NORTH);*/
        data = new SSAData();
        desctop = new JDesktopPane();
        setContentPane(desctop);
        openFileItem.addActionListener((new OpenFile(data)));
        analysisItem.addActionListener(new Analysis(data, this, desctop));
        calcItem.addMenuListener(new MenuListener() {

            public void menuSelected(MenuEvent e) {
                if(data.getTimeSeries().isEmpty()) {
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
                        //System.out.println(scn.nextDouble());
                    }
                    scn.close();
                    inpt.close();
                    timeSeries.setTimeSeries(timeSeriesList);
                    JInternalFrame timeSeriesFrame = InternalFrame.createInternalFrame(
                            XYChart.createChart(timeSeriesList, "Временной ряд", "Исходный", fileName), "Временной ряд");
                    desctop.add(timeSeriesFrame);
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

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 279, Short.MAX_VALUE)
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
    private javax.swing.JMenu fileItem;
    private javax.swing.JMenu infoItem;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JMenuItem openFileItem;
    // End of variables declaration//GEN-END:variables
}
