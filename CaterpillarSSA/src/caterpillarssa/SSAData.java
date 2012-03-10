package caterpillarssa;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Васькин Александр
 */
public class SSAData {

    private List<Double> timeSeries;
    private int L;
    private double inclosureMatrix[][];

    public SSAData() {
        timeSeries = new ArrayList<Double>();
        L = 2;
    }

    public List<Double> getTimeSeries() {
        return timeSeries;
    }

    public void setTimeSeries(List<Double> timeSeries) {
        this.timeSeries = timeSeries;
    }

    public int getL() {
        return L;
    }

    public void setL(int L) {
        this.L = L;
    }

    public double[][] getInclosureMatrix() {
        return inclosureMatrix;
    }

    public void setInclosureMatrix(double matrix[][]) {
        inclosureMatrix = matrix;
    }
}
