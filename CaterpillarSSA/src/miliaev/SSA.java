package miliaev;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Math.round;

public class SSA
{
    private Logger log = LoggerFactory.getLogger(SSA.class);

    private Matrix getTrajectoryMatrix(double[] array, int l)
    {
        int k = array.length - l + 1;
        Matrix matrix = new Matrix(l, k);
        for (int i = 0; i < l; i++)
        {
            for (int j = 0; j < k; j++)
            {
                matrix.set(i, j, array[i + j]);
            }
        }
        return matrix;
    }

    private Matrix matrixLineRevert(Matrix matrix)
    {
        Matrix matrixClone = (Matrix) matrix.clone();
        int n = matrixClone.getRowDimension();
        int m = matrixClone.getColumnDimension();
        for (int i = 0; i < n; i++)
        {
            if (m % 2 == 0)
            {
                for (int j = 0; j < m / 2; j++)
                {
                    double z = matrixClone.get(i, j);
                    matrixClone.set(i, j, matrixClone.get(i, m - 1 - j));
                    matrixClone.set(i, m - 1 - j, z);
                }
            }
            else
            {
                for (int j = 0; j <= m / 2; j++)
                {
                    double z = matrixClone.get(i, j);
                    matrixClone.set(i, j, matrixClone.get(i, m - 1 - j));
                    matrixClone.set(i, m - 1 - j, z);
                }
            }
        }
        return matrixClone;
    }

    /*V = X.'*U(:,i)*/
    private Matrix getV(Matrix matrix1, Matrix matrix2)
    {
        Matrix matrix = new Matrix(matrix1.getRowDimension(), matrix1.getColumnDimension());
        for (int i = 0; i < matrix2.getColumnDimension(); i++)
        {
            Matrix eigenVectorLine = new Matrix(matrix2.getColumnDimension(), 1);
            for (int z = 0; z < matrix2.getColumnDimension(); z++)
            {
                eigenVectorLine.set(z, 0, matrix2.get(z, i));
            }
            Matrix multipleResult = matrix1.times(eigenVectorLine);
            for (int j = 0; j < matrix1.getRowDimension(); j++)
            {
                matrix.set(j, i, multipleResult.get(j, 0));
            }
        }
        return matrix;
    }

    /*Q - reconstructed components matrix, each column being a separate component*/
    private Matrix getQ(Matrix matrix1, Matrix matrix2, int k, int l)
    {
        Matrix Q = new Matrix(k + l - 1, l);
        for (int i = 0; i < l; i++)
        {
            Matrix U = new Matrix(matrix1.getRowDimension(), 1);
            for (int j = 0; j < matrix1.getRowDimension(); j++)
            {
                U.set(j, 0, matrix1.get(j, i));
            }
            Matrix V = new Matrix(1, matrix2.getRowDimension());
            for (int j = 0; j < matrix2.getRowDimension(); j++)
            {
                V.set(0, j, matrix2.get(j, i));
            }
            Matrix Z = U.times(V);
            Z = matrixLineRevert(Z);
            for (int j = (-l + 1); j < k; j++)
            {
                Q.set(l + j - 1, i, getDiagonalSum(Z, j));
            }
        }
        return matrixColumnRevert(Q);
    }

    private double getDiagonalSum(Matrix matrix, int diagonal)
    {
        double sum = 0;
        int count = 0;
        for (int i = 0; i < matrix.getRowDimension(); i++)
        {
            for (int j = 0; j < matrix.getColumnDimension(); j++)
            {
                if (j == diagonal + i)
                {
                    sum = sum + matrix.get(i, j);
                    count++;
                }
            }
        }
        if (count != 0)
        {
            return sum / count;
        }
        return sum;
    }

    private Matrix matrixColumnRevert(Matrix matrix)
    {
        Matrix result = (Matrix) matrix.clone();
        int m = result.getRowDimension();
        for (int j = 0; j < matrix.getColumnDimension(); j++)
        {
            if (m % 2 == 0)
            {
                for (int i = 0; i < m / 2; i++)
                {
                    double x = result.get(i, j);
                    result.set(i, j, result.get(result.getRowDimension() - i - 1, j));
                    result.set(result.getRowDimension() - i - 1, j, x);
                }
            }
            else
            {
                for (int i = 0; i <= m / 2; i++)
                {
                    double x = result.get(i, j);
                    result.set(i, j, result.get(result.getRowDimension() - i - 1, j));
                    result.set(result.getRowDimension() - i - 1, j, x);
                }
            }
            for (int i = 0; i < matrix.getRowDimension(); i++)
            {
                double x = result.get(i, j);
                result.set(i, j, result.get(result.getRowDimension() - i - 1, j));
                result.set(result.getRowDimension() - i - 1, j, x);
            }
        }
        return result;
    }

    /*return regenerated vector from matrix*/
    private double[] regeneratedValue(Matrix matrix)
    {
        double[] result = new double[matrix.getRowDimension()];
        int m;
        if(matrix.getColumnDimension()<5)
        {
            m=matrix.getColumnDimension();
        }
        else
        {
            m = 5;
        }
        for (int i = 0; i < matrix.getRowDimension(); i++)
        {
            for (int j = 0; j < m; j++)
            {
                result[i] = result[i] + matrix.get(i, j);
            }
        }
        return result;
    }
    /**
     * @param T - number of input data
     * @param L - number of reconstructed components input data series to be decomposed to
     * @param N - number of reconstructed components to be used for the forecast
     * @param M - number of points to be forecasted
     * @param U - eigenVectorRevert
     * @param Q - reconstructed matrix
    * */
    private double[] calculateForecasting(int T, int L, int N, int M, Matrix U, Matrix Q)
    {
        Matrix A = new Matrix(L - 1, 1);
        for (int i = 0; i < N; i++)
        {

            Matrix UM = new Matrix(L - 1, 1);
            for (int j = 0; j < L - 1; j++)
            {
                UM.set(j, 0, U.get(j, i));
            }
            A.plusEquals(UM.times(U.get(L - 1, i)));
        }
        Matrix UM = new Matrix(1, N);
        for (int i = 0; i < N; i++)
        {
            UM.set(0, i, U.get(L - 1, i));
        }
        double v = UM.norm2();
        A.timesEquals(1 / (1 - Math.pow(v, 2)));
        A = matrixColumnRevert(A);
        Matrix G = new Matrix(Q.getRowDimension(), 1);
        for (int i = 0; i < G.getRowDimension(); i++)
        {
            double sum = 0;
            for (int j = 0; j < N; j++)
            {
                sum = sum + Q.get(i, j);
            }
            G.set(i, 0, sum);
        }
        Matrix F = new Matrix(G.getRowDimension() + M, 1);
        for (int i = 0; i < G.getRowDimension(); i++)
        {
            F.set(i, 0, G.get(i, 0));
        }
        for (int i = T; i < T + M; i++)
        {
            double res = 0;
            for (int j = 1; j < L; j++)
            {
                res = (res + (F.get(i, 0) + A.get(j - 1, 0) * F.get(i - j, 0)));
            }
            F.set(i, 0, res);
        }
        double[] res = new double[G.getRowDimension() + M];new Matrix(G.getRowDimension() + M, 1);
        for (int i = 0; i < G.getRowDimension(); i++)
        {
            res[i] = G.get(i,0);
        }
        for (int i = G.getRowDimension(); i < res.length; i++)
        {
            res[i] = round(F.get(i,0));
        }
        return res;
    }

    /**
     * @param inputData - vector of input data
     * @param m - count of forecasting elements
     * @return - return vector which contain elements of input data and forecasting elements
     * */
    public double[] forecasting(double[] inputData, int m)
    {
        int n = inputData.length;
        if (n<=2)
        {
            log.info("For SSA Method length of an input vector must be more than 2. Size of input vector = " + n);
            return null;
        }
        int l;
        if(n>=6)
        {
            l = n / 2 - 1;
        }
        else
        {
            l = n-1;
        }
        int k = n - l + 1;
        Matrix trajectoryMatrix = getTrajectoryMatrix(inputData, l);
        Matrix C = trajectoryMatrix.times(trajectoryMatrix.transpose());
        SingularValueDecomposition singularValueDecomposition = new SingularValueDecomposition(C);
        Matrix eigenVector = singularValueDecomposition.getV();
        Matrix V = getV(trajectoryMatrix.transpose(), eigenVector);
        Matrix Q = getQ(eigenVector, V, k, l);
        double [] forecast = calculateForecasting(n, l, l / 2, m, eigenVector, Q);
        return forecast;
    }

}