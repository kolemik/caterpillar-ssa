package mixa;

import Jama.Matrix;

public class MatrixTest {

	public static void main(String[] args) {
		Matrix a = new Matrix(new double[][] {
				{11,12,13},
				{21,22,23},
				{31,32,33}
		});
		a.print(5, 2);

		Matrix f = new Matrix(new double[][] {
			{0,0,0},
			{0,1,0},
			{0,0,1}
		});

		f.times(a).print(5, 2);
	}

}
