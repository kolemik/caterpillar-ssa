package caterpillarssa;

import java.util.ArrayList;
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
        double a[][] = {{1, 3, 4, 8}, {2, 2, 3, 9}, {1, 4, 6, 4}};
        double b[][] = transpositionMatrix(data.getInclosureMatrix());
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b[i].length; j++) {
                System.out.print(b[i][j] + " ");
            }
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
}
