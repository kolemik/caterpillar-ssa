package caterpillarssa;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
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
        //double S[][] = new double[inclosureMatrix.length][transp[0].length];
        Matrix S = new Matrix(inclosureMatrix).times(new Matrix(transp));
        // double ss[][] = S.getArray();
        int d = new Matrix(inclosureMatrix).rank(); //ранг матрицы вложений
        EigenvalueDecomposition decomposition = new EigenvalueDecomposition(S);
        Matrix eigenvalue = decomposition.getD(); //матрица с собственными значениями
        double eigenvalueArray[][] = eigenvalue.getArray();
        List<Double> eigenvalueList = new ArrayList<Double>();
        Double eigenvalueOrder[] = new Double[eigenvalueArray.length];
        for (int i = 0; i < eigenvalueArray.length; i++) {
            for (int j = 0; j < eigenvalueArray[i].length; j++) {
                if (i == j) {
                    eigenvalueList.add(eigenvalueArray[i][j]);
                }
            }
        }
       /* Arrays.sort(eigenvalueOrder);
                for (int i = 0; i < eigenvalueOrder.length; i++) {
            System.out.println(eigenvalueOrder[i]);
        }
        Arrays.sort(eigenvalueOrder, Collections.reverseOrder());
        //Comparator comparator = Collections.reverseOrder();
        // Collections.sort(eigenvalueList);
         System.out.println("!");
        for (int i = 0; i < eigenvalueOrder.length; i++) {
            System.out.println(eigenvalueOrder[i]);
        }*/
        /*Collections.sort(eigenvalueList, Collections.reverseOrder());
        for (int i = 0; i < eigenvalueList.size(); i++) {
        System.out.println(eigenvalueList.get(i));
        }*/



        Matrix eigenvec = decomposition.getV();
        double eigen[][] = eigenvalue.getArray();
        for (int i = 0; i < eigen.length; i++) {
            for (int j = 0; j < eigen[i].length; j++) {
                System.out.print(eigen[i][j] + " ");
            }
            System.out.println("");
        }

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
