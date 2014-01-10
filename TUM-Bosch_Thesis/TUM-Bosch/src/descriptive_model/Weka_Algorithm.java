package descriptive_model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;

import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.lazy.KStar;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.misc.InputMappedClassifier;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
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

		DecisionTable dt = new DecisionTable();
		weka.attributeSelection.GreedyStepwise gs = new weka.attributeSelection.GreedyStepwise();
		gs.setThreshold(-1.7976931348623157E308);
		gs.setNumExecutionSlots(1);
		gs.setNumToSelect(-1);
		dt.setSearch(gs);
		dt.setUseIBk(false);
		dt.setCrossVal(1);
		dt.buildClassifier(data);
		Remove rm = new Remove();
		rm.setAttributeIndices("4,5,6,7,8,9");
		// Classifier classifier = bag;
		FilteredClassifier classifier = new FilteredClassifier();
		classifier.setFilter(rm);
		classifier.setClassifier(dt);
		classifier.buildClassifier(data);

		Evaluation evaluation = new Evaluation(data);
		evaluation.crossValidateModel(classifier, data, 10, new Random(1));

		System.out.println(evaluation.toCumulativeMarginDistributionString());
		System.out.println(evaluation.toClassDetailsString());
		System.out.println(evaluation.toSummaryString());
		System.out.println(evaluation.toMatrixString());
		return file;

	}

	public static void applyWeka_DecisionTable(String detection_filepath,
			String final_output) throws Exception {
		System.out
				.println("\n================================== DECISION TABLE FOR PREDICTION =======================================\n");
		System.out.println("detection_filepath: " + detection_filepath);
		System.out.println("final_output: " + final_output);
		String file = CSVToArffConversion.convertCSVtoArff(final_output);

		DataSource test_ds = new DataSource(file);
		DataSource train_ds = new DataSource(detection_filepath);

		Instances test = test_ds.getDataSet();
		Instances train = train_ds.getDataSet();

		train.setClassIndex(train.numAttributes() - 1);
		test.setClassIndex(test.numAttributes() - 1);

		DecisionTable dt = new DecisionTable();
		weka.attributeSelection.BestFirst bfs = new weka.attributeSelection.BestFirst();
		bfs.setSearchTermination(5);
		String[] options = new String[4];
		options[0] = "-D";
		options[1] = "1";
		options[2] = "-N";
		options[3] = "5";
		bfs.setOptions(options);
		dt.setSearch(bfs);
		dt.setUseIBk(false);
		dt.setCrossVal(1);
		dt.buildClassifier(train);

		options = new String[2];
		options[0] = "-R";
		options[1] = "4,5,6,7,8,9,10";
		Remove remove = new Remove();
		remove.setOptions(options);
		remove.setInputFormat(train);
		Instances newTrain = Filter.useFilter(train, remove);

		InputMappedClassifier imc = new InputMappedClassifier();
		imc.setModelHeader(test);
		imc.setTrim(true);
		imc.setIgnoreCaseForNames(true);
		imc.constructMappedInstance(test.get(0));
		imc.setClassifier(dt);
		imc.buildClassifier(newTrain);

		Evaluation evaluation = new Evaluation(newTrain);
		evaluation.evaluateModel(imc, test);

		BufferedReader config = new BufferedReader(new FileReader(
				"thesis.config"));
		String line_c = null;
		String fp = null;
		int timestep = 0;
		while ((line_c = config.readLine()) != null) {
			if (line_c.contains("Location:")) {
				fp = line_c.split("n:")[1].trim();
			}
			if (line_c.contains("Timestep:")) {
				timestep = Integer.parseInt(line_c.split(":")[1].trim());
			}
		}
		config.close();
		BufferedWriter output = new BufferedWriter(new FileWriter(fp
				+ "/output.csv", true));
		double predictive_accuracy = (evaluation.numTruePositives(0) + evaluation
				.numTrueNegatives(0))
				/ (evaluation.numTruePositives(0)
						+ evaluation.numTrueNegatives(0)
						+ evaluation.numFalsePositives(0) + evaluation
							.numFalseNegatives(0));
		output.write(Math.round((evaluation.pctCorrect() * 100) / 100)
				+ ","
				+ Math.round((((evaluation.numFalsePositives(0) / (24 * (60 / timestep))) * 100) * 100) / 100)
				+ ","
				+ Math.round(((evaluation.precision(0) * 100) * 100) / 100)
				+ "," + Math.round(((evaluation.recall(0) * 100) * 100) / 100)
				+ "," + Math.round(((predictive_accuracy) * 100) * 100 / 100)
				+ ","
				+ Math.round((evaluation.relativeAbsoluteError()) * 100 / 100)
				+ "\n");
		output.flush();
		output.close();
		// System.out.println(evaluation.toSummaryString("\nResults\n======\n",
		// false));
		//
		// System.out.println(evaluation.toCumulativeMarginDistributionString());
		// System.out.println(evaluation.toClassDetailsString());
		// System.out.println(evaluation.toSummaryString());
		System.out.println(evaluation.toMatrixString());
	}

	public static void applyWeka_RandomForest(String detection_filepath,
			String final_output) throws Exception {
		System.out
				.println("\n================================== RANDOM FOREST FOR PREDICTION =======================================\n");
		System.out.println("detection_filepath: " + detection_filepath);
		System.out.println("final_output: " + final_output);
		String file = CSVToArffConversion.convertCSVtoArff(final_output);

		DataSource test_ds = new DataSource(file);
		DataSource train_ds = new DataSource(detection_filepath);

		Instances test = test_ds.getDataSet();
		Instances train = train_ds.getDataSet();

		train.setClassIndex(train.numAttributes() - 1);
		test.setClassIndex(test.numAttributes() - 1);

		RandomForest rf = new RandomForest();
		rf.setSeed(5);
		rf.buildClassifier(train);

		String[] options = new String[2];
		options[0] = "-R";
		options[1] = "4,5,6,7,8,9,10";
		Remove remove = new Remove();
		remove.setOptions(options);
		remove.setInputFormat(train);
		Instances newTrain = Filter.useFilter(train, remove);

		InputMappedClassifier imc = new InputMappedClassifier();
		imc.setModelHeader(test);
		imc.setTrim(true);
		imc.setIgnoreCaseForNames(true);
		imc.constructMappedInstance(test.get(0));
		imc.setClassifier(rf);
		imc.buildClassifier(newTrain);
		Evaluation evaluation = new Evaluation(newTrain);

		evaluation.evaluateModel(imc, test);
		BufferedReader config = new BufferedReader(new FileReader(
				"thesis.config"));
		String line_c = null;
		String fp = null;
		int timestep = 0;
		while ((line_c = config.readLine()) != null) {
			if (line_c.contains("Location:")) {
				fp = line_c.split("n:")[1].trim();
			}
			if (line_c.contains("Timestep:")) {
				timestep = Integer.parseInt(line_c.split(":")[1].trim());
			}
		}
		config.close();
		BufferedWriter output = new BufferedWriter(new FileWriter(fp
				+ "/output.csv", true));

		double predictive_accuracy = (evaluation.numTruePositives(0) + evaluation
				.numTrueNegatives(0))
				/ (evaluation.numTruePositives(0)
						+ evaluation.numTrueNegatives(0)
						+ evaluation.numFalsePositives(0) + evaluation
							.numFalseNegatives(0));
		output.write(Math.round((evaluation.pctCorrect() * 100) / 100)
				+ ","
				+ Math.round((((evaluation.numFalsePositives(0) / (24 * (60 / timestep))) * 100) * 100) / 100)
				+ ","
				+ Math.round(((evaluation.precision(0) * 100) * 100) / 100)
				+ "," + Math.round(((evaluation.recall(0) * 100) * 100) / 100)
				+ "," + Math.round(((predictive_accuracy) * 100) * 100 / 100)
				+ ","
				+ Math.round((evaluation.relativeAbsoluteError()) * 100 / 100)
				+ "\n");
		output.flush();
		output.close();
		// System.out.println(evaluation.toSummaryString("\nResults\n======\n",
		// false));

		// System.out.println(evaluation.toCumulativeMarginDistributionString());
		// System.out.println(evaluation.toClassDetailsString());
		// System.out.println(evaluation.toSummaryString());
		System.out.println(evaluation.toMatrixString());
	}

	public static void applyWeka_Bagging(String detection_filepath,
			String final_output) throws Exception {
		System.out
				.println("\n================================== BAGGING FOR PREDICTION =======================================\n");
		System.out.println("detection_filepath: " + detection_filepath);
		System.out.println("final_output: " + final_output);
		String file = CSVToArffConversion.convertCSVtoArff(final_output);

		DataSource test_ds = new DataSource(file);
		DataSource train_ds = new DataSource(detection_filepath);

		Instances test = test_ds.getDataSet();
		Instances train = train_ds.getDataSet();

		train.setClassIndex(train.numAttributes() - 1);
		test.setClassIndex(test.numAttributes() - 1);

		RandomTree rt = new RandomTree();
		rt.setSeed(1);
		rt.setKValue(0);
		rt.setNumFolds(0);
		rt.setMinNum(1.0);
		rt.setMaxDepth(0);

		Bagging bag = new Bagging();
		bag.setBagSizePercent(100);
		bag.setSeed(1);
		bag.setClassifier(rt);
		bag.setNumExecutionSlots(1);
		bag.setNumIterations(10);
		bag.buildClassifier(train);

		String[] options = new String[2];
		options[0] = "-R";
		options[1] = "4,5,6,7,8,9,10";
		Remove remove = new Remove();
		remove.setOptions(options);
		remove.setInputFormat(train);
		Instances newTrain = Filter.useFilter(train, remove);

		InputMappedClassifier imc = new InputMappedClassifier();
		imc.setModelHeader(test);
		imc.setTrim(true);
		imc.setIgnoreCaseForNames(true);
		imc.constructMappedInstance(test.get(0));
		imc.setClassifier(bag);
		imc.buildClassifier(newTrain);
		Evaluation evaluation = new Evaluation(newTrain);

		evaluation.evaluateModel(imc, test);
		BufferedReader config = new BufferedReader(new FileReader(
				"thesis.config"));
		String line_c = null;
		String fp = null;
		int timestep = 0;
		while ((line_c = config.readLine()) != null) {
			if (line_c.contains("Location:")) {
				fp = line_c.split("n:")[1].trim();
			}
			if (line_c.contains("Timestep:")) {
				timestep = Integer.parseInt(line_c.split(":")[1].trim());
			}
		}
		config.close();
		BufferedWriter output = new BufferedWriter(new FileWriter(fp
				+ "/output.csv", true));

		double predictive_accuracy = (evaluation.numTruePositives(0) + evaluation
				.numTrueNegatives(0))
				/ (evaluation.numTruePositives(0)
						+ evaluation.numTrueNegatives(0)
						+ evaluation.numFalsePositives(0) + evaluation
							.numFalseNegatives(0));
		output.write(Math.round((evaluation.pctCorrect() * 100) / 100)
				+ ","
				+ Math.round((((evaluation.numFalsePositives(0) / (24 * (60 / timestep))) * 100) * 100) / 100)
				+ ","
				+ Math.round(((evaluation.precision(0) * 100) * 100) / 100)
				+ "," + Math.round(((evaluation.recall(0) * 100) * 100) / 100)
				+ "," + Math.round(((predictive_accuracy) * 100) * 100 / 100)
				+ ","
				+ Math.round((evaluation.relativeAbsoluteError()) * 100 / 100)
				+ "\n");
		output.flush();
		output.close();
		// System.out.println(evaluation.toSummaryString("\nResults\n======\n",
		// false));
		//
		// System.out.println(evaluation.toCumulativeMarginDistributionString());
		// System.out.println(evaluation.toClassDetailsString());
		// System.out.println(evaluation.toSummaryString());
		System.out.println(evaluation.toMatrixString());
	}

	public static void applyWeka_KStar(String detection_filepath,
			String final_output) throws Exception {
		System.out
				.println("\n================================== KStar FOR PREDICTION =======================================\n");
		System.out.println("detection_filepath: " + detection_filepath);
		System.out.println("final_output: " + final_output);
		String file = CSVToArffConversion.convertCSVtoArff(final_output);

		DataSource test_ds = new DataSource(file);
		DataSource train_ds = new DataSource(detection_filepath);

		Instances test = test_ds.getDataSet();
		Instances train = train_ds.getDataSet();

		train.setClassIndex(train.numAttributes() - 1);
		test.setClassIndex(test.numAttributes() - 1);

		KStar ks = new KStar();
		ks.setGlobalBlend(20);
		String[] options = new String[2];
		options[0] = "-M";
		options[1] = "a";
		ks.setOptions(options);
		ks.buildClassifier(train);

		options = new String[2];
		options[0] = "-R";
		options[1] = "4,5,6,7,8,9,10";
		Remove remove = new Remove();
		remove.setOptions(options);
		remove.setInputFormat(train);
		Instances newTrain = Filter.useFilter(train, remove);

		InputMappedClassifier imc = new InputMappedClassifier();
		imc.setModelHeader(test);
		imc.setTrim(true);
		imc.setIgnoreCaseForNames(true);
		imc.constructMappedInstance(test.get(0));
		imc.setClassifier(ks);
		imc.buildClassifier(newTrain);
		Evaluation evaluation = new Evaluation(newTrain);

		evaluation.evaluateModel(imc, test);
		BufferedReader config = new BufferedReader(new FileReader(
				"thesis.config"));
		String line_c = null;
		String fp = null;
		int timestep = 0;
		while ((line_c = config.readLine()) != null) {
			if (line_c.contains("Location:")) {
				fp = line_c.split("n:")[1].trim();
			}
			if (line_c.contains("Timestep:")) {
				timestep = Integer.parseInt(line_c.split(":")[1].trim());
			}
		}
		config.close();
		BufferedWriter output = new BufferedWriter(new FileWriter(fp
				+ "/output.csv", true));

		double predictive_accuracy = (evaluation.numTruePositives(0) + evaluation
				.numTrueNegatives(0))
				/ (evaluation.numTruePositives(0)
						+ evaluation.numTrueNegatives(0)
						+ evaluation.numFalsePositives(0) + evaluation
							.numFalseNegatives(0));
		output.write(Math.round((evaluation.pctCorrect() * 100) / 100)
				+ ","
				+ Math.round((((evaluation.numFalsePositives(0) / (24 * (60 / timestep))) * 100) * 100) / 100)
				+ ","
				+ Math.round(((evaluation.precision(0) * 100) * 100) / 100)
				+ "," + Math.round(((evaluation.recall(0) * 100) * 100) / 100)
				+ "," + Math.round(((predictive_accuracy) * 100) * 100 / 100)
				+ ","
				+ Math.round((evaluation.relativeAbsoluteError()) * 100 / 100)
				+ "\n");
		output.flush();
		output.close();
		// System.out.println(evaluation.toSummaryString("\nResults\n======\n",
		// false));
		//
		// System.out.println(evaluation.toCumulativeMarginDistributionString());
		// System.out.println(evaluation.toClassDetailsString());
		// System.out.println(evaluation.toSummaryString());
		System.out.println(evaluation.toMatrixString());
	}

	public static void applyWeka_CostSensitiveClassifier(
			String detection_filepath, String final_output) throws Exception {
		System.out
				.println("\n================================== COST SENSITIVE CLASSIFIER FOR PREDICTION =======================================\n");
		System.out.println("detection_filepath: " + detection_filepath);
		System.out.println("final_output: " + final_output);
		String file = CSVToArffConversion.convertCSVtoArff(final_output);

		DataSource test_ds = new DataSource(file);
		DataSource train_ds = new DataSource(detection_filepath);

		Instances test = test_ds.getDataSet();
		Instances train = train_ds.getDataSet();

		train.setClassIndex(train.numAttributes() - 1);
		test.setClassIndex(test.numAttributes() - 1);

		DecisionTable dt = new DecisionTable();
		weka.attributeSelection.GreedyStepwise gs = new weka.attributeSelection.GreedyStepwise();
		gs.setThreshold(-1.7976931348623157E308);
		gs.setNumExecutionSlots(1);
		gs.setNumToSelect(-1);
		dt.setSearch(gs);
		dt.setUseIBk(false);
		dt.setCrossVal(1);
		dt.buildClassifier(train);

		CostSensitiveClassifier csc = new CostSensitiveClassifier();
		String[] options = new String[2];
		options[0] = "-cost-matrix";
		options[1] = "[0.0 1.0;0.5 0.0]";
		csc.setOptions(options);
		csc.setClassifier(dt);

		options = new String[2];
		options[0] = "-R";
		options[1] = "4,5,6,7,8,9,10";
		Remove remove = new Remove();
		remove.setOptions(options);
		remove.setInputFormat(train);
		Instances newTrain = Filter.useFilter(train, remove);

		InputMappedClassifier imc = new InputMappedClassifier();
		imc.setModelHeader(test);
		imc.setTrim(true);
		imc.setIgnoreCaseForNames(true);
		imc.constructMappedInstance(test.get(0));
		imc.setClassifier(csc);
		imc.buildClassifier(newTrain);
		Evaluation evaluation = new Evaluation(newTrain);

		evaluation.evaluateModel(imc, test);
		BufferedReader config = new BufferedReader(new FileReader(
				"thesis.config"));
		String line_c = null;
		String fp = null;
		int timestep = 0;
		while ((line_c = config.readLine()) != null) {
			if (line_c.contains("Location:")) {
				fp = line_c.split("n:")[1].trim();
			}
			if (line_c.contains("Timestep:")) {
				timestep = Integer.parseInt(line_c.split(":")[1].trim());
			}
		}
		config.close();
		BufferedWriter output = new BufferedWriter(new FileWriter(fp
				+ "/output.csv", true));

		double predictive_accuracy = (evaluation.numTruePositives(0) + evaluation
				.numTrueNegatives(0))
				/ (evaluation.numTruePositives(0)
						+ evaluation.numTrueNegatives(0)
						+ evaluation.numFalsePositives(0) + evaluation
							.numFalseNegatives(0));
		output.write(Math.round((evaluation.pctCorrect() * 100) / 100)
				+ ","
				+ Math.round((((evaluation.numFalsePositives(0) / (24 * (60 / timestep))) * 100) * 100) / 100)
				+ ","
				+ Math.round(((evaluation.precision(0) * 100) * 100) / 100)
				+ "," + Math.round(((evaluation.recall(0) * 100) * 100) / 100)
				+ "," + Math.round(((predictive_accuracy) * 100) * 100 / 100)
				+ ","
				+ Math.round((evaluation.relativeAbsoluteError()) * 100 / 100)
				+ "\n");
		output.flush();
		output.close();
		// System.out.println(evaluation.toSummaryString("\nResults\n======\n",
		// false));
		//
		// System.out.println(evaluation.toCumulativeMarginDistributionString());
		// System.out.println(evaluation.toClassDetailsString());
		// System.out.println(evaluation.toSummaryString());
		System.out.println(evaluation.toMatrixString());
	}
}
