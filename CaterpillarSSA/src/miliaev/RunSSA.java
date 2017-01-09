package miliaev;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class RunSSA {

	public static void main(String[] args) {
		SSA ssa = new SSA();
		List<Double> data = new ArrayList<Double>();

		String fname = "data/sin.dat"; int l = 48, e = 5, m = 48;
//		String fname = "data/fort.dat"; int l = 84, e = 33, m = 80;
//		String fname = "data/fort120.dat"; int l = 60, e = 33, m = 80;

		File file = new File(fname);
		
		try (Scanner scan = new Scanner(file)) {
			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				data.add(Double.parseDouble(line));
			}
		} catch (FileNotFoundException fnfe) {
		}
		
		System.out.println("{" + data.size() + "} " + data);

		double[] inputData = new double[data.size()];
		for (int i = 0; i < inputData.length; i++) {
			inputData[i] = data.get(i);
		}
		double[] result = ssa.forecasting(inputData, l, e, m);
		System.out.println(Arrays.toString(result));
	}

}
