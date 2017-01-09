package mixa;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

public class Forecast {

	/**
	 * http://www.gistatgroup.com/gus/book1/algor.html
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		List<Double> data = new ArrayList<Double>();
		
//		String fname = "data/sin.dat"; int M = 24; int componentsCount = 5;

		String fname = "data/fort.dat"; int M = 84; int componentsCount = 33;
//		String fname = "data/fort120.dat"; int M = 60; int componentsCount = 33;

		File file = new File(fname);
		
		try (Scanner scan = new Scanner(file)) {
			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				data.add(Double.parseDouble(line));
			}
		} catch (FileNotFoundException fnfe) {
		}
		
		System.out.println("{" + data.size() + "} " + data);
		
		int N = data.size();
		
		Matrix X = new Matrix(N - M + 1, M);
		for (int i = 0; i < X.getRowDimension(); i++) {
			for (int j = 0; j < X.getColumnDimension(); j++) {
				X.set(i, j, data.get(i + j));
			}
		}
		X.print(5, 0);
		
		Matrix V = X.transpose().times(X).times(1.0d / M);
		
		V.print(5, 2);
		
		EigenvalueDecomposition decomposition = new EigenvalueDecomposition(V);
		Matrix L = decomposition.getD();
		Matrix P = decomposition.getV();
		
		L.print(8, 0);
		P.print(5,2);
		
		Matrix Y = X.times(P);
		
		Matrix filter = new Matrix(M,M);
		for (int i = M - componentsCount; i < M; i++) {
			filter.set(i, i, 1);
		}
		Matrix Y_filtered = Y.times(filter);
		Matrix P_filtered = P.times(filter);
		
		
		Matrix X_resulted = Y_filtered.times(P_filtered.transpose());
		
		double [] result = new double[N];
		double [] diff = new double[N];
		for (int k = 0; k < N; k++) {
			int d_start_row;
			int d_start_col;
			if (k < X.getRowDimension()) {
				d_start_row = k;
				d_start_col = 0;
			} else {
				d_start_row = X.getRowDimension() - 1;
				d_start_col = k - X.getRowDimension() + 1;
			}
			double sum = 0.0;
			int l;
			for (l = 0; d_start_row - l >= 0 && d_start_col + l < M; l++) {
				sum += X_resulted.get(d_start_row - l, d_start_col + l);
			}
			result[k] = Math.round(100 * sum / l) / 100;
			diff[k] = Math.round(100.0 * ((result[k] - data.get(k)) / data.get(k))) / 100.0;
		}
		
		System.out.println(Arrays.toString(result));
		System.out.println(Arrays.toString(diff));
		// Y_filtered.print(5, 2);
	}

}
