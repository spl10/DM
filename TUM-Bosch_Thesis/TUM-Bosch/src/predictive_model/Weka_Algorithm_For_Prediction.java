package predictive_model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import model_class.Prediction;
import weka.classifiers.misc.InputMappedClassifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import data_preprocessing.outputfile.CSVToArffConversion;
import descriptive_model.Weka_Algorithm;

public class Weka_Algorithm_For_Prediction {
	public static void applyWeka(String filepath_detect,
			String filepath_predict, Prediction prediction, String[] names)
			throws Exception {
		System.out
				.println("=====================Weka_Algorithm_For_prediction.applyWeka()=========================");
		String file = CSVToArffConversion.convertCSVtoArff(filepath_predict);
		System.out.println("filepath_detect" + filepath_detect
				+ "\nfilepath_predict: " + filepath_predict);

		DataSource source = new DataSource(file);
		DataSource train_data = new DataSource(filepath_detect);

		Instances train = train_data.getDataSet();
		Instances test = source.getDataSet();

		train.setClassIndex(train.numAttributes() - 1);
		test.setClassIndex(test.numAttributes() - 1);

		RandomForest rf = new RandomForest();
		rf.setSeed(1);

		String[] options = new String[2];
		options[0] = "-R";
		options[1] = "4,5,6,7,8,9";
		Remove remove = new Remove();
		remove.setOptions(options);
		remove.setInputFormat(train);
		Instances newTrain = Filter.useFilter(train, remove);

		InputMappedClassifier classifier = new InputMappedClassifier();
		classifier.setModelHeader(test);
		classifier.setTrim(true);
		classifier.setIgnoreCaseForNames(true);
		classifier.constructMappedInstance(test.get(0));
		classifier.setClassifier(rf);
		classifier.buildClassifier(newTrain);

		System.out
				.println("train.numAttributes(): " + newTrain.numAttributes());
		String final_output = filepath_predict.substring(0,
				filepath_predict.indexOf("."))
				+ "_Final.csv";

		File fd = new File(final_output);
		if (fd.exists()) {
			fd.delete();
		}

		BufferedWriter bw_predict = new BufferedWriter(new FileWriter(
				final_output));

		// bw_predict.write("date,time,weekday");
		// // for (int n = 1; n < names.length; n++) {
		// // bw_predict.write("," + names[n]);
		// // }
		// bw_predict.write(",LABEL\n");

		int j = 0;
		String[] time = prediction.getTime();
		String[] date = prediction.getDate();
		// String[][] weekday = prediction.getWeekday();
		String[][] label_pred = new String[date.length][time.length];
		// Double[][][] param = prediction.getParam();
		System.out.println(test.numInstances());
		for (int i = 0; i < test.numInstances(); i++) {
			double pred = rf.classifyInstance(test.instance(i));
			System.out.print("ID: " + test.instance(i).value(1));
			System.out.print(",time: "
					+ time[i]
					+ ", actual: "
					+ test.classAttribute().value(
							(int) test.instance(i).classValue()));
			System.out.println(", predicted: "
					+ test.classAttribute().value((int) pred));
			if (pred == 1.0)
				label_pred[date.length - 1][i] = "USAGE";
			else
				label_pred[date.length - 1][i] = "NO USAGE";
		}
		// for (j = 0; j < time.length; j++) {
		// bw_predict.write(date[date.length - 1] + "," + time[j] + ","
		// + weekday[date.length - 1][j]);
		// // for (k = 1; k < names.length; k++) {
		// // bw_predict.write("," + param[k][i][j]);
		// // }
		// bw_predict.write("," + label_pred[date.length - 1][j] + "\n");
		// }
		BufferedReader br = new BufferedReader(new FileReader(filepath_predict));
		String line = br.readLine();
		bw_predict.write(line + "\n");
		System.out.println(line);
		j = 0;
		while ((line = br.readLine()) != null) {
			bw_predict.write(line.substring(0, line.lastIndexOf(",")) + ","
					+ label_pred[date.length - 1][j] + "\n");
			System.out.println(line.substring(0, line.lastIndexOf(",")) + ","
					+ label_pred[date.length - 1][j]);
			j++;
		}
		br.close();
		bw_predict.flush();
		bw_predict.close();
		Weka_Algorithm.applyWeka_DecisionTable(filepath_detect, final_output);
	}

	public static void dailyBasisFiles(String filepath_detect,
			String filepath_predict, Prediction prediction, String[] names)
			throws Exception {
		System.out
				.println("================================ Weka_Algorithm_For_prediction.dailyBasisFiles()=============================");
		String[] date = prediction.getDate();
		String[] time = prediction.getTime();
		String[][] weekday = prediction.getWeekday();
		String[][] label = prediction.getLabel();
		String[] file_inputs = new String[date.length];
		String[] file_outputs = new String[date.length];
		// Double[][][] param = prediction.getParam();
		int i = 0;
		while (i < date.length) {
			String file_act = filepath_detect.substring(0,
					filepath_detect.indexOf("."))
					+ "_$" + date[i] + "_Actual.csv";
			file_inputs[i] = file_act;
			BufferedWriter bw_actual = new BufferedWriter(new FileWriter(
					file_act));
			String file_pred = filepath_predict.substring(0,
					filepath_predict.indexOf("."))
					+ "_$" + date[i] + "_Predict.csv";
			file_outputs[i] = file_pred;
			BufferedWriter bw_pred = new BufferedWriter(new FileWriter(
					file_pred));
			bw_actual.write("date,time,weekday");
			bw_pred.write("date,time,weekday");
			// for (int k = 0; k < param.length; k++) {
			// bw_actual.write("," + names[k]);
			// bw_pred.write("," + names[k]);
			// }
			bw_actual.write(",LABEL\n");
			bw_pred.write(",LABEL\n");
			for (int j = 0; j < time.length; j++) {

				bw_actual.write(date[i] + "," + time[j] + "," + weekday[i][j]);
				bw_pred.write(date[i] + "," + time[j] + "," + weekday[i][j]);
				// for (int k = 0; k < param.length; k++) {
				// bw_actual.write("," + param[k][i][j]);
				// bw_pred.write("," + param[k][i][j]);
				// }
				bw_actual.write("," + label[i][j] + "\n");
				// if (j == 0)
				// bw_pred.write(",NO USAGE\n");
				// else if (j == 1)
				// bw_pred.write(",USAGE\n");
				// else
				bw_pred.write("," + label[i][j] + "\n");
			}
			i++;
			bw_actual.flush();
			bw_actual.close();
			bw_pred.flush();
			bw_pred.close();
		}
		applyDailyWeka_Prediction(filepath_detect, file_outputs, prediction,
				names);
	}

	public static void applyDailyWeka_Prediction(String filepath_detect,
			String[] file_out, Prediction prediction, String[] names)
			throws Exception {
		System.out
				.println("============================Weka_Algorithm_For_prediction.applyDailyWeka_Prediction()====================================");
		String[] date = prediction.getDate();
		String[] time = new String[prediction.getTime().length];
		String[][] weekday = prediction.getWeekday();
		String[][] label = prediction.getLabel();
		Double[][][] param = prediction.getParam();
		String day = "";
		int i = 0;
		int comp_date = 1;
		while (i < date.length) {
			for (int j = 0; j < time.length; j++) {
				label[i][j] = prediction.getLabel()[i][j];
				weekday[i][j] = prediction.getWeekday()[i][j];
				date[i] = prediction.getDate()[i];
				day = weekday[i][j];
				time[j] = prediction.getTime()[j];
				// for (int k = 0; k < param.length; k++) {
				// param[k][i][j] = prediction.getParam()[k][i][j];
				// }
				// System.out.println("i: " + i + " date[" + i + "]: " + date[i]
				// + " time[" + j + "]: " + time[j] + " label[" + i + "]["
				// + j + "]: " + label[i][j] + " weekday[" + i + "][" + j
				// + "]: " + weekday[i][j]);
			}
			comp_date = 1;
			for (int j = 1; j < i; j++) {
				if (day.equals(prediction.getWeekday()[j][0])) {
					comp_date = j;
				}
			}
			if (comp_date == 1) {
				comp_date = i - 1;
			}
			prediction.setLabel(label);
			prediction.setDate(date);
			prediction.setWeekday(weekday);
			prediction.setTime(time);
			prediction.setParam(param);

			if (i > 0) {
				applyDailyWeka_Algorithm(filepath_detect, file_out[i],
						prediction, names, i);
			}
			i++;
		}
	}

	public static void applyDailyWeka_Algorithm(String filepath_detect,
			String filepath_predict, Prediction prediction, String[] names,
			int pos) throws Exception {
		System.out
				.println("=====================Weka_Algorithm_For_prediction.applyDailyWeka_Algorithm()=========================");
		System.out.println(" filepath_detect: " + filepath_detect
				+ " filepath_predict: " + filepath_predict);
		String file_predict = CSVToArffConversion
				.convertCSVtoArff(filepath_predict);
		int k = 0;
		String date = prediction.getDate()[pos];
		String[] time = prediction.getTime();
		String[] weekday = prediction.getWeekday()[pos];
		String[] label_pred = new String[time.length];
		// Double[][][] param = prediction.getParam();

		DataSource test_data = new DataSource(file_predict);
		DataSource train_data = new DataSource(filepath_detect);

		Instances train = train_data.getDataSet();
		Instances test = test_data.getDataSet();

		train.setClassIndex(train.numAttributes() - 1);
		test.setClassIndex(test.numAttributes() - 1);

		RandomForest rf = new RandomForest();
		rf.setSeed(1);

		String[] options = new String[2];
		options[0] = "-R";
		options[1] = "4,5,6,7,8,9";
		Remove remove = new Remove();
		remove.setOptions(options);
		remove.setInputFormat(train);
		Instances newTrain = Filter.useFilter(train, remove);

		InputMappedClassifier classifier = new InputMappedClassifier();
		classifier.setModelHeader(test);
		classifier.setTrim(true);
		classifier.setIgnoreCaseForNames(true);
		classifier.constructMappedInstance(test.get(0));
		classifier.setClassifier(rf);
		classifier.buildClassifier(newTrain);

		String final_output = filepath_predict.substring(0,
				filepath_predict.indexOf("."))
				+ "_Final.csv";

		File fd = new File(final_output);
		if (fd.exists()) {
			fd.delete();
		}

		BufferedWriter bw_predict = new BufferedWriter(new FileWriter(
				final_output));

		bw_predict.write("date,time,weekday");
		// for (k = 0; k < param.length; k++) {
		// bw_predict.write("," + names[k]);
		// }
		bw_predict.write(",LABEL\n");
		for (k = 0; k < test.numInstances(); k++) {
			double pred = classifier.classifyInstance(test.instance(k));
			int id = (int) Math.round(test.instance(k).value(1));
			if (pred == 1.0)
				label_pred[k] = "USAGE";
			else
				label_pred[k] = "NO USAGE";
			System.out
					.println("pred: "
							+ pred
							+ " ID: "
							+ id
							+ ", actual: "
							+ test.classAttribute().value(
									(int) test.instance(k).classValue())
							+ ", predicted: "
							+ test.classAttribute().value((int) pred));
		}

		for (int j = 0; j < time.length; j++) {
			bw_predict.write(date + "," + time[j] + "," + weekday[j]);
			// for (k = 0; k < param.length; k++) {
			// bw_predict.write("," + param[k][pos][j]);
			// }
			bw_predict.write("," + label_pred[j] + "\n");
		}

		bw_predict.flush();
		bw_predict.close();

		Weka_Algorithm.applyWeka_DecisionTable(filepath_detect, final_output);
	}

}
