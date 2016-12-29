package miliaev;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.swing.DefaultListModel;

import caterpillarssa.GroupListObject;
import caterpillarssa.SSAData;
import caterpillarssa.SpectrumAnalysis;
import caterpillarssa.UnselectListObject;

public class Test {

	public static void main(String[] args) throws Exception {
//		Matrix matrix = new Matrix(new double[][] {{1, 0, 5}, {0, 2, -8}, {-7, 1, 3}});
//		EigenvalueDecomposition decomp = new EigenvalueDecomposition(matrix);
//		System.out.println(Arrays.toString(decomp.getRealEigenvalues()));
//		System.out.println(Arrays.toString(decomp.getImagEigenvalues()));
//		decomp.getD().print(5, 3);
//		decomp.getV().print(5, 3);
		
		List<Double> sourceList = new ArrayList<>();
		try (Scanner scan = new Scanner(new FileInputStream("linear.dat"))) {
			while (scan.hasNextLine()) {
				sourceList.add(Double.parseDouble(scan.nextLine()));
			}
		}
		
		double[] source = new double[sourceList.size()]; // {1,2,3,2,0,2,3,2,1,2,3,2,0,2,3,2,1,2,3,2,0,2,3,2,1,2,3,2,0,2,3,2,1,2,3,2,0,2,3,2};
		for (int i = 0; i < source.length; i++) {
			source[i] = sourceList.get(i);
		}

		print(source, doMiliaev(Arrays.copyOf(source, (int) (source.length * 0.8))));
//		print(source, doVaskin(source));
	}

	private static void print(double[] source, double[] result) {
		System.out.println(source.length + " / " + result.length);
		for (int i = 0; i < result.length; i++) {
			double delta = 0.0;
			if (i < source.length) {
				System.out.printf("%4.0f", source[i]);
				delta = Math.abs(100 * (source[i] - result[i]) / source[i]);
			}
			System.out.printf("\t%.1f\t%3.0f\n", result[i], delta);
		}
	}

	public static double[] doMiliaev(double[] source) {
		SSA ssa = new SSA();
		return ssa.forecasting(source, 16, 3, 37);
	}

	public static double[] doVaskin(double[] source) {
		SSAData data = new SSAData();
		List<Double> timeSeries = new ArrayList<>(source.length);
		for (double d : source) {
			timeSeries.add(d);
		}
		data.setTimeSeries(timeSeries);
		data.setL(16);

		SpectrumAnalysis.inclosure(data);
        SpectrumAnalysis.singularDecomposition(data);
        SpectrumAnalysis.setMovingAvarege(data);
        SpectrumAnalysis.averagedCovariance(data);
        SpectrumAnalysis.functionEigenValue(data);
        
        DefaultListModel<GroupListObject> groupsModel = new DefaultListModel<>();
        List<UnselectListObject> groups = new ArrayList<>();
        System.out.println("DATA X LEN: " + data.getX().length);
        for (int i = 0; i < data.getX().length / 2; i++) {
            UnselectListObject e = new UnselectListObject(i, 0);
    		groups.add(e);        	
        }
		GroupListObject glo = new GroupListObject(groups);
		groupsModel.addElement(glo);
		//восстановление временного ряда (группировка) 
        SpectrumAnalysis.grouping(groupsModel, data);

        for (int i = 0; i < data.getGroupX().length; i++) {
        	System.out.println(data.getGroupX()[i].getColumnDimension() + "/" + data.getGroupX()[i].getRowDimension());
        }

        //восстановление временного ряда (диагональное усреднение)
        SpectrumAnalysis.diagonalAveraging(data);

		List<Double> recons = data.getReconstructionList();
		double [] result = new double[recons.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = recons.get(i);
		}
		return result;
	}

}
