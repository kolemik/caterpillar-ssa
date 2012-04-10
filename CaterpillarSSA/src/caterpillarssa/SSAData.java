package caterpillarssa;

import Jama.Matrix;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Васькин Александр
 */
public class SSAData {

    private List<Double> timeSeries; //исходный временной ряд
    private int L; //длина окна
    private double inclosureMatrix[][]; //матрица вложений
    private Matrix X[]; //элементарные матрицы сингулярного разложения
	private List<Double> SMA; //скользящие средние

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

    public Matrix[] getX() {
        return X;
    }
	
    public void setX(Matrix X[]) {
        this.X = X;
    }
	
	public List<Double> getSMA() {
		return SMA;
	}
	
	public void setSMA(List<Double> SMA) {
		this.SMA = SMA;
	}
	
}
