package caterpillarssa;

import Jama.Matrix;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.ChartPanel;

/**
 *
 * @author Васькин Александр
 */
public class SSAData {

    private List<Double> timeSeries;	//исходный временной ряд
    private int L;						//длина окна
    private double inclosureMatrix[][]; //матрица вложений
    private Matrix X[];			//элементарные матрицы сингулярного разложения
    private Matrix V[];
    private List<Double> SMA;			//скользящие средние
    private List<Double> cov;			//диагональное осреднение ковариаций
    private List<Double> eigenValueList;//собственные числа
    private List<Double> lgEigenValue;  //логарифмы собственных чисел
    private List<Double> sqrtEigenValue;//корни собственных чисел
    private List eigenVectors;			//собственные векторы
    private List<Double> percentList;   //проценты собтвенных чисел
    private List<Double> accruePercentList; //накопленные проценты собственных чисел
    /*
     * для каскадного отображение InternalFrame
     */
    private int nextFrameX;
    private int nextFrameY;
    private int frameDistance;
    private int eigenFuncPage;
    private int mainCompPage;
    private List<ChartPanel> eigenVecListCharts;
    private List<ChartPanel> mainCompListCharts;

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

    public Matrix[] getV() {
        return V;
    }

    public void setV(Matrix[] V) {
        this.V = V;
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

    public List<Double> getAccruePercentList() {
        return accruePercentList;
    }

    public void setAccruePercentList(List<Double> accruePercentList) {
        this.accruePercentList = accruePercentList;
    }

    public List<Double> getPercentList() {
        return percentList;
    }

    public void setPercentList(List<Double> percentList) {
        this.percentList = percentList;
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

    public int getEigenFuncPage() {
        return eigenFuncPage;
    }

    public void setEigenFuncPage(int eigenFuncPage) {
        this.eigenFuncPage = eigenFuncPage;
    }

    public List<ChartPanel> getEigenVecListCharts() {
        return eigenVecListCharts;
    }

    public void setEigenVecListCharts(List<ChartPanel> eigenVecListCharts) {
        this.eigenVecListCharts = eigenVecListCharts;
    }

    public List<ChartPanel> getMainCompListCharts() {
        return mainCompListCharts;
    }

    public void setMainCompListCharts(List<ChartPanel> mainCompListCharts) {
        this.mainCompListCharts = mainCompListCharts;
    }

    public int getMainCompPage() {
        return mainCompPage;
    }

    public void setMainCompPage(int mainCompPage) {
        this.mainCompPage = mainCompPage;
    }
}
