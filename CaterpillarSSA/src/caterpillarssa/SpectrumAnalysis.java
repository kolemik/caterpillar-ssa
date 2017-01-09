package caterpillarssa;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 *
 * @author Васькин Александр
 */
public class SpectrumAnalysis {

    /**
     * перевод исходного временного ряда в последовательность многомерных
     * векторов
     *
     * @param data данные для анализа
     */
    public static void inclosure(SSAData data) {
        int L = data.getL(); //длина окна
        int K = data.getTimeSeries().size() - L + 1; //количество векторов вложения
        double inclosureMatrix[][] = new double[L][K]; //траекторная матрица
        //формируем векторы вложения
        for (int i = 1; i <= K; i++) {
            int num = 0;
            for (int j = i - 1; j <= i + L - 2; j++) {
                inclosureMatrix[num][i - 1] = data.getTimeSeries().get(j);
                num++;
            }
        }
        data.setInclosureMatrix(inclosureMatrix);
    }

    /**
     * сингулярное разложение
     *
     * @param data данные для анализа
     */
    public static void singularDecomposition(SSAData data) {
        double inclosureMatrix[][] = data.getInclosureMatrix();
        double transp[][] = transpositionMatrix(inclosureMatrix);
        Matrix S = new Matrix(inclosureMatrix).times(new Matrix(transp));
        //int d = new Matrix(inclosureMatrix).rank(); //ранг матрицы вложений
        EigenvalueDecomposition decomposition = new EigenvalueDecomposition(S);
        Matrix eigenvalue = decomposition.getD();   //матрица с собственными значениями
        Matrix eigenvec = decomposition.getV();     //матрица собственных векторов
        List<Double> eigenvalueList = new ArrayList<Double>();
        
        double[] real = decomposition.getRealEigenvalues();
        double[] imag = decomposition.getImagEigenvalues();
        for (int i = 0; i < Math.min(real.length, imag.length); i++) {
        	System.out.println("EV = " + real[i] + " + i * " + imag[i]);
        }
        
        //формируем набор собственных значений, стоящих на диагонали
        for (int i = 0; i < eigenvalue.getRowDimension(); i++) {
            for (int j = 0; j < eigenvalue.getRowDimension(); j++) {
                if (i == j) {
                    eigenvalueList.add(eigenvalue.get(i, j));
                }
            }
        }

        System.out.print("Diagonal:");
        decomposition.getD().print(5, 3);
        System.out.println();

        Comparator comparator = Collections.reverseOrder();
        /*
         * собственные значения должны быть в убывающем порядке, поэтому
         * сортируем их в обратном порядке (изначально значения в возрастающем
         * порядке)
         */
        Collections.sort(eigenvalueList, comparator);
        for (int i = 0; i < eigenvalueList.size(); i++) {
        	Double d = eigenvalueList.get(i);
        	if (Math.abs(d) < Math.pow(10, -10)) {
        		eigenvalueList.set(i, 0.0);
        	}
        }
        System.out.println("EV LIST: " + eigenvalueList);
        data.setEigenValueList(eigenvalueList);
        double sumValueList = 0;
        List<Double> percentList;
        List<Double> accruePercentList;
        for (int i = 0; i < data.getEigenValueList().size(); i++) {
            sumValueList = sumValueList + data.getEigenValueList().get(i);
        }
        //формирование процентов собственных чисел и накопленных процентов
        percentList = new ArrayList<Double>();
        accruePercentList = new ArrayList<Double>();
        double accruePercent = 0;
        for (int i = 0; i < data.getEigenValueList().size(); i++) {
            percentList.add(data.getEigenValueList().get(i) / sumValueList * 100);
            accruePercent += percentList.get(i);
            accruePercentList.add(accruePercent);
        }
        data.setAccruePercentList(accruePercentList);
        data.setPercentList(percentList);

        int size = eigenvec.getColumnDimension();
        Matrix V[] = new Matrix[size];
        Matrix U[] = new Matrix[size];
        Matrix X[] = new Matrix[size]; //элементарные матрицы сингулярного разложения
        ArrayList listSeries = new ArrayList();
        for (int j = 0; j < eigenvec.getColumnDimension(); j++) {
            double uVec[][] = new double[size][1];
            ArrayList series = new ArrayList();
            for (int k = 0; k < eigenvec.getRowDimension(); k++) {
                /*
                 * векторы должны соответствовать собственным числа (!), поэтому
                 * начинаем с последнего собственного вектора
                 */
                uVec[k][0] = eigenvec.get(k, eigenvec.getColumnDimension() - j - 1);
                series.add(uVec[k][0]);
            }
            listSeries.add(series);
            U[j] = new Matrix(uVec);
            V[j] = new Matrix(transp).times(U[j]);
        }
        data.setEigenVectors(listSeries);
        for (int i = 0; i < V.length; i++) {
            for (int j = 0; j < V[i].getRowDimension(); j++) {
                for (int k = 0; k < V[i].getColumnDimension(); k++) {
                    double val = V[i].get(j, k) / Math.sqrt(eigenvalueList.get(i));
                    V[i].set(j, k, val);
                }
            }
        }
        data.setV(V);
        for (int i = 0; i < X.length; i++) {
            X[i] = U[i].times(V[i].transpose());
            for (int j = 0; j < X[i].getRowDimension(); j++) {
                for (int k = 0; k < X[i].getColumnDimension(); k++) {
                    double val = X[i].get(j, k) * Math.sqrt(eigenvalueList.get(i));
                    X[i].set(j, k, val);
                }
            }
        }
        data.setX(X);
    }

    /**
     * восстановление временного ряда (этап группировки)
     *
     * @param model модель JList (список групп)
     * @param data данные для анализа
     */
    public static void grouping(DefaultListModel model, SSAData data) {
        Matrix grouX[] = new Matrix[model.getSize()];
        for (int i = 0; i < model.getSize(); i++) {
            GroupListObject obj = (GroupListObject) model.get(i);
            for (int j = 0; j < obj.getGroups().size(); j++) {
                UnselectListObject unselect = (UnselectListObject) obj.getGroups().get(j);
                if (j == 0) {
                    grouX[i] = data.getX()[unselect.getIndex()];
                } else {
                    grouX[i] = grouX[i].plus(data.getX()[unselect.getIndex()]);
                }
            }
        }
        data.setGroupX(grouX);
    }

    /**
     * восстановление временного ряда (этап диагонального усреднения)
     *
     * @param data данные для анализа
     */
    public static void diagonalAveraging(SSAData data) {
        int L;
        int K;
        int N;
        List<List> list = new ArrayList<List>();
        for (int i = 0; i < data.getGroupX().length; i++) {
            if (data.getGroupX()[i].getRowDimension() < data.getGroupX()[i].getColumnDimension()) {
                L = data.getGroupX()[i].getRowDimension();
                K = data.getGroupX()[i].getColumnDimension();
            } else {
                K = data.getGroupX()[i].getRowDimension();
                L = data.getGroupX()[i].getColumnDimension();
            }
            N = data.getGroupX()[i].getRowDimension() + data.getGroupX()[i].getColumnDimension() - 1;
            List series = new ArrayList();
            double element;
            for (int k = 0; k <= N - 1; k++) {
                element = 0;
                if (k >= 0 && k < L - 1) {
                    for (int m = 0; m <= k; m++) {
                        if (data.getGroupX()[i].getRowDimension() <= data.getGroupX()[i].getColumnDimension()) {
                            element += data.getGroupX()[i].get(m, k - m);
                        } else if (data.getGroupX()[i].getRowDimension() > data.getGroupX()[i].getColumnDimension()) {
                            element += data.getGroupX()[i].get(k - m, m);
                        }
                    }
                    element = element * (1.0 / (k + 1));
                    series.add(element);
                }
                if (k >= L - 1 && k < K - 1) {
                    for (int m = 0; m <= L - 2; m++) {
                        if (data.getGroupX()[i].getRowDimension() <= data.getGroupX()[i].getColumnDimension()) {
                            element += data.getGroupX()[i].get(m, k - m);
                        } else if (data.getGroupX()[i].getRowDimension() > data.getGroupX()[i].getColumnDimension()) {
                            element += data.getGroupX()[i].get(k - m, m);
                        }
                    }
                    element = element * (1.0 / L);
                    series.add(element);
                }
                if (k >= K - 1 && k < N) {
                    for (int m = k - K + 1; m <= N - K; m++) {
                        if (data.getGroupX()[i].getRowDimension() <= data.getGroupX()[i].getColumnDimension()) {
                            element += data.getGroupX()[i].get(m, k - m);
                        } else if (data.getGroupX()[i].getRowDimension() > data.getGroupX()[i].getColumnDimension()) {
                            element += data.getGroupX()[i].get(k - m, m);
                        }
                    }
                    element = element * (1.0 / (N - k));
                    series.add(element);
                }
            }
            list.add(series);
        }
        double sum;
        //суммируем полученные ряды и получаем исходный ряд
        List<Double> reconstructionList = new ArrayList<Double>();
        for (int j = 0; j < list.get(0).size(); j++) {
            sum = 0;
            for (int i = 0; i < list.size(); i++) {
                sum += (Double) list.get(i).get(j);
            }
            reconstructionList.add(sum);
        }
        data.setReconstructionList(reconstructionList);
    }

    /**
     * транспонирование матрицы
     *
     * @param matrix исходная матрица
     * @return результирующая матрица
     */
    private static double[][] transpositionMatrix(double matrix[][]) {
        double transpMatrix[][] = new double[matrix[0].length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                transpMatrix[j][i] = matrix[i][j];
            }
        }
        return transpMatrix;
    }

    /**
     * формирование скользящих средних
     *
     * @param data данные для анализа
     */
    public static void setMovingAvarege(SSAData data) {
        List<Double> SMA = new ArrayList<Double>();
        int m = data.getTimeSeries().size() - data.getL() + 1; //период осреднения
        for (int i = 0; i < data.getL(); i++) {
            double sum = 0;
            double avg = 0;
            for (int j = i; j < m + i; j++) {
                sum += data.getTimeSeries().get(j);
            }
            avg = sum / m;
            SMA.add(avg);
            data.setSMA(SMA);
        }
    }
    
    /**
     * диагональное осреднение ковариационной матрицы 
     * (относительно побочной диагонали)
     * 
     * @param data данные для анализа
     */
    public static void averagedCovariance(SSAData data) {
        double avg;
        double K = data.getTimeSeries().size() - data.getL() + 1; //количество векторов вложения
        List<Double> covarianceList = new ArrayList<Double>();
        double transp[][] = transpositionMatrix(data.getInclosureMatrix());
        Matrix S = new Matrix(data.getInclosureMatrix()).times(new Matrix(transp));
        S = S.times(1.0 / K); //ковариационная матрица
        int size = S.getColumnDimension();
        int N = size + size - 1;
        int n;
        for (int k = 0; k < N; k++) {
            if ((k % 2) == 0) {
                if (k >= 0 && k < size) {
                    avg = 0;
                    n = 0;
                    for (int m = 0; m <= k; m++) {
                        avg += S.get(m, size - 1 - (k - m));
                        n++;
                    }
                    avg = avg / (n);
                    covarianceList.add(avg);
                }
                if (k >= size && k < N) {
                    avg = 0;
                    n = 0;
                    for (int m = k - size + 1; m <= N - size; m++) {
                        avg += S.get(m, size - 1 - (k - m));
                        n++;
                    }
                    avg = avg / (n);
                    covarianceList.add(avg);
                }
            }
        }
        data.setCov(covarianceList);
    }

    /**
     * формирование функций собственных чисел
     * 
     * @param data данные для анализа 
     */
    public static void functionEigenValue(SSAData data) {
        List<Double> lgList = new ArrayList<Double>();
        List<Double> sqrtList = new ArrayList<Double>();
        for (int i = 0; i < data.getEigenValueList().size(); i++) {
            lgList.add((Double) Math.log(data.getEigenValueList().get(i)));
            sqrtList.add(Math.sqrt(data.getEigenValueList().get(i)));
        }
        data.setLgEigenValue(lgList);
        data.setSqrtEigenValue(sqrtList);
    }
    
}
