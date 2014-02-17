package predictiveModel;

import java.io.File;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

public class CSVToArffConversion {

	/**
	 * @param args
	 * @throws Exception
	 *             Converts the csv file to arff file using weka library.
	 */
	public static String convertCSVtoArff(String input) throws Exception {

		/* ARFF filename. */
		String output = input.substring(0, input.indexOf(".")) + ".arff";
		File o = new File(output);
		if (!o.exists()) {

			/* WEKA libaray loads the csv file for conversion. */
			CSVLoader csvLoader = new CSVLoader();
			csvLoader.setSource(new File(input));
			csvLoader.setFieldSeparator(",");
			Instances dataset = csvLoader.getDataSet();
			dataset.setClass(dataset.attribute("LABEL"));

			/*
			 * WEKA libaray saves the csv file into arff file including the arff
			 * formatting.
			 */
			ArffSaver arffSaver = new ArffSaver();
			arffSaver.setInstances(dataset);
			arffSaver.setFile(new File(output));
			arffSaver.writeBatch();
			System.out.println("COMPLETED!!!");
		}
		return output;
	}
}
