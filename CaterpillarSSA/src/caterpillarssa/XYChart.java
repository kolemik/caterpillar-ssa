package caterpillarssa;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;

/**
 *
 * @author Васькин Александр
 */
public class XYChart {

    public static JFreeChart createChart(List data, String title, List<String> seriesTitle, String fileName, boolean shapeRenderer) {
		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        XYSeriesCollection dataset = new XYSeriesCollection();
		System.out.println(data.size());
		for (int i = 0; i < data.size(); i++) {
			XYSeries series = new XYSeries(seriesTitle.get(i));
			ArrayList list = (ArrayList)data.get(i);
			System.out.println(data.size() + " " + i + " " + seriesTitle.get(i));
			for (int j = 0; j < list.size(); j++) {
				series.add(j, (Double)list.get(j));
			}
			dataset.addSeries(series);
			
		}
        /*Iterator itr = data.iterator();
        int i = 0;
        while(itr.hasNext()) {
            i++;
            series.add(i, (Double)itr.next());
        }*/
       
        
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
		final XYPlot plot = chart.getXYPlot();
		if(shapeRenderer) {					
			plot.setRenderer(renderer);
		}
        return chart;
    }
}
