package caterpillarssa;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import Jama.SingularValueDecomposition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Васькин Александр
 */
public class SpectrumAnalysis {

    /**
     * перевод исходного временного ряда в последовательность
     * многомерных векторов
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
                //System.out.print(inclosureMatrix[num][i - 1] + " ");
                num++;

            }
            //System.out.println(" ");
        }
        data.setInclosureMatrix(inclosureMatrix);
    }

    /**
     * сингулярное разложение
     * @param data данные для анализа
     */
    public static void singularDecomposition(SSAData data) {
        //double a[][] = {{1, 3, 4, 8}, {2, 2, 3, 9}, {1, 4, 6, 4}};
        //double aa[][] = {{2, 3}, {0, -2}, {-1, 4}};
        //double bb[][] = {{1, -1, 0, 3}, {2, 1, -2, -4}};
        double inclosureMatrix[][] = data.getInclosureMatrix();
        double transp[][] = transpositionMatrix(inclosureMatrix);
        Matrix S = new Matrix(inclosureMatrix).times(new Matrix(transp));
        int d = new Matrix(inclosureMatrix).rank(); //ранг матрицы вложений
        EigenvalueDecomposition decomposition = new EigenvalueDecomposition(S);
        Matrix eigenvalue = decomposition.getD();   //матрица с собственными значениями
        Matrix eigenvec = decomposition.getV();     //матрица собственных векторов
        List<Double> eigenvalueList = new ArrayList<Double>();
        for (int i = 0; i < eigenvalue.getRowDimension(); i++) {
            for (int j = 0; j < eigenvalue.getRowDimension(); j++) {
                if (i == j) {
                    eigenvalueList.add(eigenvalue.get(i, j));
                }
            }
        }
        for (int i = 0; i < transp.length; i++) {
            for (int j = 0; j < transp[i].length; j++) {
                // System.out.print(transp[i][j]);
            }
            // System.out.println("");
        }

        Matrix V[] = new Matrix[d];
        Matrix U[] = new Matrix[d];
        Matrix X[] = new Matrix[d]; //элементарные матрицы сингулярного разложения
        for (int j = 0; j < eigenvec.getColumnDimension(); j++) {
            double uVec[][] = new double[d][1];
            for (int k = 0; k < eigenvec.getRowDimension(); k++) {
                uVec[k][0] = eigenvec.get(k, j);
            }
            U[j] = new Matrix(uVec);
            V[j] = new Matrix(transp).times(U[j]);
        }
        for (int i = 0; i < V.length; i++) {
            for (int j = 0; j < V[i].getRowDimension(); j++) {
                for (int k = 0; k < V[i].getColumnDimension(); k++) {
                    double val = V[i].get(j, k) / Math.sqrt(eigenvalueList.get(i));
                    V[i].set(j, k, val);
                }
            }
        }
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
        for (int i = 0; i < X[0].getRowDimension(); i++) {
            for (int j = 0; j < X[0].getColumnDimension(); j++) {

                System.out.print(X[0].get(i, j));
            }
            System.out.println();
        }
       // System.out.println(X[0].getColumnDimension() + " " + X[0].getRowDimension() + " " + X[0].rank());
    }

    /**
     * транспонирование матрицы
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

    /*private static double[][] multiplicationMatrix(double a[][], double b[][]) {
    double c[][] = new double[a.length][b[0].length];
    for (int i = 0; i < a.length; i++) {
    for (int j = 0; j < b[0].length; j++) {
    c[i][j] = 0;
    for (int k = 0; k < b.length; k++) {
    c[i][j] += a[i][k] + b[k][j];
    }
    }
    }
    return c;
    }*/
}
