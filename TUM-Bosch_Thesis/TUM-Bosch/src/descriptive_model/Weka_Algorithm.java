package descriptive_model;

import java.util.Random;

import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.unsupervised.attribute.Remove;
import data_preprocessing.outputfile.CSVToArffConversion;

public class Weka_Algorithm {
	public static String applyWeka(String filepath_detect) throws Exception {
		System.out
				.println("\n================================== DETECTION USING WEKA =======================================\n");
		String file = CSVToArffConversion.convertCSVtoArff(filepath_detect);
		DataSource source = new DataSource(file);
		Instances data = source.getDataSet();
		data.setClassIndex(data.numAttributes() - 1);

		RandomForest rt = new RandomForest();
		rt.setSeed(5);
		rt.buildClassifier(data);
		Remove rm = new Remove();
		rm.setAttributeIndices("4,5,6,7,8,9");
		// Classifier classifier = bag;
		FilteredClassifier classifier = new FilteredClassifier();
		classifier.setFilter(rm);
		classifier.setClassifier(rt);
		classifier.buildClassifier(data);

		Evaluation evaluation = new Evaluation(data);
		evaluation.crossValidateModel(classifier, data, 10, new Random(1));

		System.out.println(evaluation.toCumulativeMarginDistributionString());
		System.out.println(evaluation.toClassDetailsString());
		System.out.println(evaluation.toSummaryString());
		System.out.println(evaluation.toMatrixString());
		return file;

	}

	public static void applyWeka_Prediction(String final_output)
			throws Exception {
		System.out
				.println("\n================================== FINAL PREDICTION USING WEKA =======================================\n");
		String file = CSVToArffConversion.convertCSVtoArff(final_output);
		DataSource source = new DataSource(file);
		Instances data = source.getDataSet();
		data.setClassIndex(data.numAttributes() - 1);

		RandomForest rt = new RandomForest();
		rt.setSeed(5);
		rt.buildClassifier(data);
		Remove rm = new Remove();
		// rm.setAttributeIndices("4,5,6,7,8,9");
		// Classifier classifier = bag;
		FilteredClassifier classifier = new FilteredClassifier();
		classifier.setFilter(rm);
		classifier.setClassifier(rt);
		classifier.buildClassifier(data);

		Evaluation evaluation = new Evaluation(data);
		evaluation.crossValidateModel(classifier, data, 10, new Random(1));

		System.out.println(evaluation.toCumulativeMarginDistributionString());
		System.out.println(evaluation.toClassDetailsString());
		System.out.println(evaluation.toSummaryString());
		System.out.println(evaluation.toMatrixString());
	}
}
