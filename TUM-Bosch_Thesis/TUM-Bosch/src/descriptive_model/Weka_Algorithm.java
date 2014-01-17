package descriptive_model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;

import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.classifiers.lazy.KStar;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.meta.Vote;
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

		KStar ks = new KStar();
		ks.setGlobalBlend(20);
		String[] options = new String[2];
		options[0] = "-M";
		options[1] = "a";
		ks.setOptions(options);
		ks.buildClassifier(data);
		Remove rm = new Remove();
		rm.setAttributeIndices("4,7,8,9,10");
		// Classifier classifier = bag;
		FilteredClassifier classifier = new FilteredClassifier();
		classifier.setFilter(rm);
		classifier.setClassifier(ks);
		classifier.buildClassifier(data);

		Evaluation evaluation = new Evaluation(data);
		evaluation.crossValidateModel(classifier, data, 10, new Random(1));

		System.out.println(evaluation.toCumulativeMarginDistributionString());
		System.out.println(evaluation.toClassDetailsString());
		System.out.println(evaluation.toSummaryString());
		System.out.println(evaluation.toMatrixString());
		return file;

	}

	public static double applyWeka_DecisionTable(String detection_filepath,
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
		options[1] = "4,7,8,9,10";
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
		System.out.println(evaluation.toSummaryString("\nResults\n======\n",
				false));

		System.out.println(evaluation.toCumulativeMarginDistributionString());
		System.out.println(evaluation.toClassDetailsString());
		System.out.println(evaluation.toMatrixString());
		return (evaluation.numFalseNegatives(0) + evaluation
				.numTrueNegatives(0));
	}

	public static double applyWeka_RandomForest(String detection_filepath,
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
		options[1] = "4,7,8,9,10";
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
		System.out.println(evaluation.toSummaryString("\nResults\n======\n",
				false));

		System.out.println(evaluation.toCumulativeMarginDistributionString());
		System.out.println(evaluation.toClassDetailsString());
		System.out.println(evaluation.toMatrixString());
		return (evaluation.numFalseNegatives(0) + evaluation
				.numTrueNegatives(0));
	}

	public static double applyWeka_Bagging(String detection_filepath,
			String final_output) throws Exception {
		System.out
				.println("\n================================== BAYESNET FOR PREDICTION =======================================\n");
		System.out.println("detection_filepath: " + detection_filepath);
		System.out.println("final_output: " + final_output);
		String file = CSVToArffConversion.convertCSVtoArff(final_output);

		DataSource test_ds = new DataSource(file);
		DataSource train_ds = new DataSource(detection_filepath);

		Instances test = test_ds.getDataSet();
		Instances train = train_ds.getDataSet();

		train.setClassIndex(train.numAttributes() - 1);
		test.setClassIndex(test.numAttributes() - 1);

		weka.classifiers.bayes.net.search.local.K2 k2 = new weka.classifiers.bayes.net.search.local.K2();
		String[] options = new String[4];
		options[0] = "-P";
		options[1] = "1";
		options[2] = "-S";
		options[3] = "BAYES";
		k2.setOptions(options);

		weka.classifiers.bayes.net.estimate.SimpleEstimator se = new weka.classifiers.bayes.net.estimate.SimpleEstimator();
		options = new String[2];
		options[0] = "-A";
		options[1] = "0.5";
		se.setOptions(options);

		BayesNet bn = new BayesNet();
		options = new String[1];
		options[0] = "-D";
		bn.setSearchAlgorithm(k2);
		bn.setEstimator(se);
		bn.setOptions(options);
		bn.buildClassifier(train);

		options = new String[2];
		options[0] = "-R";
		options[1] = "4,7,8,9,10";
		Remove remove = new Remove();
		remove.setOptions(options);
		remove.setInputFormat(train);
		Instances newTrain = Filter.useFilter(train, remove);

		InputMappedClassifier imc = new InputMappedClassifier();
		imc.setModelHeader(test);
		imc.setTrim(true);
		imc.setIgnoreCaseForNames(true);
		imc.constructMappedInstance(test.get(0));
		imc.setClassifier(bn);
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
		System.out.println(evaluation.toSummaryString("\nResults\n======\n",
				false));

		System.out.println(evaluation.toCumulativeMarginDistributionString());
		System.out.println(evaluation.toClassDetailsString());
		System.out.println(evaluation.toMatrixString());
		return (evaluation.numFalseNegatives(0) + evaluation
				.numTrueNegatives(0));
	}

	public static double applyWeka_KStar(String detection_filepath,
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
		ks.setGlobalBlend(10);
		String[] options = new String[2];
		options[0] = "-M";
		options[1] = "a";
		ks.setOptions(options);
		ks.buildClassifier(train);

		options = new String[2];
		options[0] = "-R";
		options[1] = "4,7,8,9,10";
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
		System.out.println(evaluation.toSummaryString("\nResults\n======\n",
				false));

		System.out.println(evaluation.toCumulativeMarginDistributionString());
		System.out.println(evaluation.toClassDetailsString());
		System.out.println(evaluation.toMatrixString());
		return (evaluation.numFalseNegatives(0) + evaluation
				.numTrueNegatives(0));
	}

	public static double applyWeka_CostSensitiveClassifier(
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
		/* Classifier 1 */
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
		options[1] = "[0.0 10.0;5.0 0.0]";
		csc.setOptions(options);
		csc.setClassifier(dt);

		options = new String[2];
		options[0] = "-R";
		options[1] = "4,7,8,9,10";
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
		System.out.println(evaluation.toSummaryString("\nResults\n======\n",
				false));

		System.out.println(evaluation.toCumulativeMarginDistributionString());
		System.out.println(evaluation.toClassDetailsString());
		System.out.println(evaluation.toMatrixString());
		return (evaluation.numFalseNegatives(0) + evaluation
				.numTrueNegatives(0));
	}

	public static void applyWeka_Vote(String detection_filepath,
			String final_output) throws Exception {
		System.out
				.println("\n================================== VOTE FOR PREDICTION =======================================\n");
		System.out.println("detection_filepath: " + detection_filepath);
		System.out.println("final_output: " + final_output);
		String file = CSVToArffConversion.convertCSVtoArff(final_output);

		DataSource test_ds = new DataSource(file);
		DataSource train_ds = new DataSource(detection_filepath);

		Instances test = test_ds.getDataSet();
		Instances train = train_ds.getDataSet();

		train.setClassIndex(train.numAttributes() - 1);
		test.setClassIndex(test.numAttributes() - 1);
		String[] options = new String[2];
		options[0] = "-R";
		options[1] = "4,7,8,9,10";
		Remove remove = new Remove();
		remove.setOptions(options);
		remove.setInputFormat(train);
		Instances newTrain = Filter.useFilter(train, remove);
		/* Classifier 1 */
		DecisionTable dt = new DecisionTable();
		weka.attributeSelection.GreedyStepwise gs = new weka.attributeSelection.GreedyStepwise();
		gs.setThreshold(-1.7976931348623157E308);
		gs.setNumExecutionSlots(1);
		gs.setNumToSelect(-1);
		dt.setSearch(gs);
		dt.setUseIBk(false);
		dt.setCrossVal(1);
		dt.buildClassifier(newTrain);

		/* Classifier 2 */
		KStar ks = new KStar();
		ks.setGlobalBlend(10);
		options = new String[2];
		options[0] = "-M";
		options[1] = "a";
		ks.setOptions(options);
		ks.buildClassifier(newTrain);

		/* Classifier 3 */
		RandomTree rt = new RandomTree();
		rt.setSeed(1);
		rt.setKValue(0);
		rt.setNumFolds(0);
		rt.setMinNum(1.0);
		rt.setMaxDepth(0);
		rt.buildClassifier(newTrain);

		/* Classifier 4 */
		IBk ibk = new IBk();
		ibk.setCrossValidate(false);
		ibk.setKNN(1);
		options = new String[4];
		options[0] = "-W";
		options[1] = "0";
		options[2] = "-A";
		options[3] = "weka.core.neighboursearch.LinearNNSearch -A \"weka.core.EuclideanDistance -R first-last\"";
		ibk.setOptions(options);
		ibk.buildClassifier(newTrain);

		/* Classifier 5 */
		weka.classifiers.bayes.net.search.local.K2 k2 = new weka.classifiers.bayes.net.search.local.K2();
		options = new String[4];
		options[0] = "-P";
		options[1] = "1";
		options[2] = "-S";
		options[3] = "BAYES";
		k2.setOptions(options);

		weka.classifiers.bayes.net.estimate.SimpleEstimator se = new weka.classifiers.bayes.net.estimate.SimpleEstimator();
		options = new String[2];
		options[0] = "-A";
		options[1] = "0.5";
		se.setOptions(options);

		BayesNet bn = new BayesNet();
		options = new String[1];
		options[0] = "-D";
		bn.setSearchAlgorithm(k2);
		bn.setEstimator(se);
		bn.setOptions(options);
		bn.buildClassifier(newTrain);

		/* Classifier 6 */
		NaiveBayesUpdateable nbu = new NaiveBayesUpdateable();
		nbu.buildClassifier(train);

		/* Maximum Probability */
		Vote vt = new Vote();
		vt.addPreBuiltClassifier(dt);
		vt.addPreBuiltClassifier(rt);
		vt.addPreBuiltClassifier(ks);
		vt.addPreBuiltClassifier(ibk);
		vt.addPreBuiltClassifier(bn);
		vt.addPreBuiltClassifier(nbu);
		options = new String[2];
		options[0] = "-R";
		options[1] = "MAX";
		vt.setSeed(1);
		vt.setOptions(options);
		vt.buildClassifier(newTrain);

		InputMappedClassifier imc = new InputMappedClassifier();
		imc.setModelHeader(test);
		imc.setTrim(true);
		imc.setIgnoreCaseForNames(true);
		imc.constructMappedInstance(test.get(0));
		imc.setClassifier(vt);
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
		System.out.println(evaluation.toSummaryString("\nResults\n======\n",
				false));

		System.out.println(evaluation.toCumulativeMarginDistributionString());
		System.out.println(evaluation.toClassDetailsString());
		System.out.println(evaluation.toMatrixString());
	}
}
