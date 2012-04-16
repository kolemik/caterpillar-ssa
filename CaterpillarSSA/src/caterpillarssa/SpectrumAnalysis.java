package caterpillarssa;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
		//формируем набор собственных значений, стоящих на диагонали
		for (int i = 0; i < eigenvalue.getRowDimension(); i++) {
			for (int j = 0; j < eigenvalue.getRowDimension(); j++) {
				if (i == j) {
					eigenvalueList.add(eigenvalue.get(i, j));
				}
			}
		}
		Comparator comparator = Collections.reverseOrder();
		/*
		 * собственные значения должны быть в убывающем порядке, поэтому
		 * сортируем их в обратном порядке (изначально значения в возрастающем
		 * порядке)
		 */
		Collections.sort(eigenvalueList,comparator);
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
		
		Matrix V[] = new Matrix[d];
		Matrix U[] = new Matrix[d];
		Matrix X[] = new Matrix[d]; //элементарные матрицы сингулярного разложения
		ArrayList listSeries = new ArrayList();
		for (int j = 0; j < eigenvec.getColumnDimension(); j++) {
			double uVec[][] = new double[d][1];
			ArrayList series = new ArrayList();
			for (int k = 0; k < eigenvec.getRowDimension(); k++) {
				/*
				 * векторы должны соответствовать собственным числа  (!), поэтому
				 * начинаем с последнего собственного вектора
				 */
				uVec[k][0] = eigenvec.get(k, eigenvec.getColumnDimension() - j - 1);
				series.add(uVec[k][0]);
				System.out.println(uVec[k][0]);
			}
			System.out.println("-----------------------");
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
		/*for (int i = 0; i < X.length; i++) {
			Matrix matrix = X[i];
			//System.out.println("rank " + i + "= " + matrix.rank() + " " + matrix.getRowDimension() + " " + matrix.getColumnDimension());
			
		}
		for (int i = 0; i < X[0].getRowDimension(); i++) {
		for (int j = 0; j < X[0].getColumnDimension(); j++) {
		
		System.out.print(X[0].get(i, j) + " ");
		}
		System.out.println();
		}*/

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

	/**
	 * формирование скользящих средних
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

	public static void averagedCovariance(SSAData data) {
		int N = 0;
		double avg = 0;
		List<Double> covarianceList = new ArrayList<Double>();
		double transp[][] = transpositionMatrix(data.getInclosureMatrix());
		Matrix S = new Matrix(data.getInclosureMatrix()).times(new Matrix(transp));
		int size = S.getColumnDimension();
		N = size + size - 1;	
		for (int k = 0; k < N; k++) {
			if ((k % 2) == 0) {
				if (k >= 0 && k < size) {
					avg = 0;
					for (int m = 0; m <= k; m++) {
						avg += S.get(m, k - m);
					}
					avg = avg / (k + 1);
					covarianceList.add(avg);
				}
				if(k >= size && k < N) {
					for (int m = k - size + 1; m <= N - size; m++) {
						System.out.println("m = " + m + " k - m + 2 = " + (k - m) + " k = " + k);
						avg += S.get(m, k - m);
					}
					avg = avg / (k + 1);
					covarianceList.add(avg);
				}

			}
		}
		data.setCov(covarianceList);
	}
	
	public static void functionEigenValue(SSAData data) {
		List<Double> lgList = new ArrayList<Double>();
		List<Double> sqrtList = new ArrayList<Double>();
		for (int i = 0; i < data.getEigenValueList().size(); i++) {
			lgList.add((Double)Math.log(data.getEigenValueList().get(i)));
			sqrtList.add(Math.sqrt(data.getEigenValueList().get(i)));
		}
		data.setLgEigenValue(lgList);
		data.setSqrtEigenValue(sqrtList);
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
