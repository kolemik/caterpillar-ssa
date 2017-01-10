package mixa;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

public class SSA {

	/**
	 * ssa_an.pdf
	 * ssa_for.pdf
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		List<Double> data = new ArrayList<Double>();
		
//		String fname = "data/linear.dat"; int L = 10; int componentsCount = 5;
//		String fname = "data/saw.dat"; int L = 16; int componentsCount = 8;
		String fname = "data/sin.dat"; int L = 48; int componentsCount = 5;
//		String fname = "data/fort.dat"; int L = 84; int componentsCount = 33;
//		String fname = "data/fort120.dat"; int L = 60; int componentsCount = 33;

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
		
		Matrix X = new Matrix(L, N - L + 1);
		for (int i = 0; i < X.getRowDimension(); i++) {
			for (int j = 0; j < X.getColumnDimension(); j++) {
				X.set(i, j, data.get(i + j));
			}
		}
		X.print(5, 0);
		
		Matrix S = X.times(X.transpose());// .times(1.0d / L); // TODO why not to multiple on 1 / L ???
		System.out.println("X: " + X.getRowDimension() + "/" + X.getColumnDimension());
		System.out.println("S: " + S.getRowDimension() + "/" + S.getColumnDimension());

		EigenvalueDecomposition decompose = new EigenvalueDecomposition(S);
		Matrix diagonal = decompose.getD();
		Matrix U = decompose.getV();
		
		Matrix [] Ui = new Matrix[L]; // eigenvectors ordered by eigenvalues desc
//		Matrix [] eigenVectors = Ui;
		Matrix [] Pi = Ui;
		Matrix [] Vi = new Matrix[L];
		double [] eigenValues = new double[L];
		for (int i = U.getColumnDimension() - 1; i >= 0; i--) {
			Matrix eigenVector = U.getMatrix(0, U.getRowDimension() - 1, i, i);
//			eigenVector.print(5, 3);
//			System.out.println(eigenVector.getRowDimension() + "/" + eigenVector.getColumnDimension());
			assert Math.abs(eigenVector.normF() - 1.0) < 0.001;
			int j = L - i - 1;
			Ui[j] = eigenVector;
			double eigenValue = diagonal.get(i, i);
			eigenValues[j] = eigenValue;
			if (eigenValue > 0.0) {
				Vi[j] = X.transpose().times(eigenVector).times(1.0 / Math.sqrt(eigenValues[j]));
//				Vi[j].transpose().print(5, 3);
			} else {
				System.err.println("Can't calc Vi for eigen: " + eigenValue + ". At position: " + j);
			}
		}
		for (int i = 0; i < L; i++) {
			for (int j = i + 1; j < L; j++) {
				assert Math.abs(Ui[i].transpose().times(Ui[j]).get(0, 0)) < 0.001;;
			}
		}
		
		Matrix [] Xi = new Matrix[componentsCount];
		for (int i = 0; i < componentsCount; i++ ) {
			double eigenValue = eigenValues[i];
			if (eigenValue > 0.0) {
				Xi[i] = Ui[i].times(Vi[i].transpose()).times(Math.sqrt(eigenValue));
				System.out.println("X_" + i + ": " + Xi[i].getRowDimension() + "/" + Xi[i].getColumnDimension());
			} else {
				System.err.println("Can't calc Xi for eigen: " + eigenValue + ". At position: " + i);
			}
		}

		System.out.println(Arrays.toString(eigenValues));
		
		Matrix X_sum = Xi[0];
		for (int i = 1; i < componentsCount; i++) {
			X_sum = X_sum.plus(Xi[i]);
		}
		X_sum.print(5, 2);

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
			for (l = 0; d_start_row - l >= 0 && d_start_col + l < X.getColumnDimension(); l++) {
				sum += X_sum.get(d_start_row - l, d_start_col + l);
			}
			result[k] = Math.round(100 * sum / ((double)l)) / 100.0;
			diff[k] = Math.round(100.0 * ((result[k] - data.get(k)) / data.get(k))) / 100.0;
		}
			
		System.out.println("RESULT: " + Arrays.toString(result));
		System.out.println("DIFF: " + Arrays.toString(diff));
		
/*
 		X_sum = new Matrix(X.getRowDimension(), X.getColumnDimension());
		for (int i = 0; i < L; i++) {
			X_sum = X_sum.plus(Pi[i].times(Pi[i].transpose()).times(X));
		}
		X_sum.minus(X).print(5, 2); // all zeroes!!!
*/		
		double Nu2 = 0.0;
		Matrix R = new Matrix(L - 1, 1);
		for (int i = 0; i < componentsCount; i++) {
//			System.out.println("Pi[" + i + "]: " + Pi[i].getRowDimension() + "/" + Pi[i].getColumnDimension());
			double pi = Pi[i].get(L - 1, 0);
			System.out.print(pi + "\t");
			R = R.plus(new Matrix(Arrays.copyOf(Pi[i].transpose().getArray()[0], L - 1), L - 1).times(pi));
			Nu2 += pi*pi;
		}
		System.out.println();
		System.out.println("Nu2 = " + Nu2 + "\talpha = " + (180 * Math.acos(Math.sqrt(Nu2)) / Math.PI));
		R = R.times(1.0 / (1.0 - Nu2));
		R.print(9, 7);
		
		int m = 1;
		Matrix Y = new Matrix(L - 1, 1);
		double lastRes = X_sum.get(X_sum.getRowDimension() - 1, X_sum.getColumnDimension() - 1);
		do {
			for (int i = 0; i < L - m; i++) {
				Y.set(i, 0, result[N - L + i + m]);
			}
			if (m > 1) {
				for (int i = L - m; i < L - 2; i++) {
					Y.set(i, 0, Y.get(i + 1, 0));
				}
				Y.set(L - 2, 0, lastRes);
			}
//			System.out.print("Step " + m + ": "); Y.transpose().print(7, 5);
			lastRes = R.transpose().times(Y).get(0, 0);
			System.out.println(lastRes);
		} while (m++ < L);
		System.out.println();
	}

}
