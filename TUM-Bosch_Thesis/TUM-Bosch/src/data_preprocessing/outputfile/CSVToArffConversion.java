package data_preprocessing.outputfile;

import java.io.File;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

public class CSVToArffConversion {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static String convertCSVtoArff(String input) throws Exception {
		String output = input.substring(0, input.indexOf(".")) + ".arff";
		File o = new File(output);
		if (!o.exists()) {
			CSVLoader csvLoader = new CSVLoader();
			csvLoader.setSource(new File(input));
			csvLoader.setFieldSeparator(",");
			Instances dataset = csvLoader.getDataSet();
			dataset.setClass(dataset.attribute("LABEL"));

			ArffSaver arffSaver = new ArffSaver();
			arffSaver.setInstances(dataset);
			arffSaver.setFile(new File(output));
			arffSaver.writeBatch();
			System.out.println("COMPLETED!!!");
		}
		return output;
	}
}
