package caterpillarssa;

import Jama.Matrix;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Васькин Александр
 */
public class SSAData {

    private List<Double> timeSeries;	//исходный временной ряд
    private int L;						//длина окна
    private double inclosureMatrix[][]; //матрица вложений
    private Matrix X[];			//элементарные матрицы сингулярного разложения
	private List<Double> SMA;			//скользящие средние
	private List<Double> cov;			//диагональное осреднение ковариаций
	private List<Double> eigenValueList;//собственные числа
	private List<Double> lgEigenValue;  //логарифмы собственных чисел
	private List<Double> sqrtEigenValue;//корни собственных чисел
	private List eigenVectors;			//собственные векторы
	//для каскадного отображение InternalFrame
	private int nextFrameX;
	private int nextFrameY;
	private int frameDistance;
	
    public SSAData() {
        timeSeries = new ArrayList<Double>();
        L = 2;
    }

	public List getEigenVectors() {
		return eigenVectors;
	}

	public void setEigenVectors(List eigenVectors) {
		this.eigenVectors = eigenVectors;
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
	
	public List<Double> getCov() {
		return cov;
	}
	
	public void setCov(List<Double> cov) {
		this.cov = cov;
	}
	
	public void setLgEigenValue(List<Double> lgEigenValue) {
		this.lgEigenValue = lgEigenValue;
	}
	
	public List<Double> getLgEigenValue() {
		return lgEigenValue;
	}
	
	public void setSqrtEigenValue(List<Double> sqrtEigenValue) {
		this.sqrtEigenValue = sqrtEigenValue;
	}
	
	public List<Double> getSqrtEigenValue() {
		return sqrtEigenValue;
	}

	public List<Double> getEigenValueList() {
		return eigenValueList;
	}

	public void setEigenValueList(List<Double> eigenValueList) {
		this.eigenValueList = eigenValueList;
	}

	public void setFrameDistance(int frameDistance) {
		this.frameDistance = frameDistance;
	}

	public void setNextFrameX(int nextFrameX) {
		this.nextFrameX = nextFrameX;
	}

	public void setNextFrameY(int nextFrameY) {
		this.nextFrameY = nextFrameY;
	}

	public int getFrameDistance() {
		return frameDistance;
	}

	public int getNextFrameX() {
		return nextFrameX;
	}

	public int getNextFrameY() {
		return nextFrameY;
	}
	
	
}
