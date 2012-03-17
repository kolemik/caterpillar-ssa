package caterpillarssa;

import java.awt.Font;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;

/**
 *
 * @author Васькин Александр
 */
public class XYChart {

    public static JFreeChart createChart(List<Double> data, String title, String seriesTitle, String fileName) {
        XYSeries series = new XYSeries(seriesTitle);
        Iterator itr = data.iterator();
        int i = 0;
        while(itr.hasNext()) {
            i++;
            series.add(i, (Double)itr.next());
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                "",
                "",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);
        if(!title.equals("")) {
            TextTitle source = new TextTitle(fileName);
            source.setFont(new Font("SanSerif", Font.PLAIN, 12));
            source.setPosition(RectangleEdge.TOP);
            chart.addSubtitle(source);
        }

        return chart;
    }
}
