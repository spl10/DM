package predictive_model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import descriptive_model.Weka_Algorithm;

public class Probablistic_Model {
	public static void algorithm(String Actual_filepath,
			String detection_filepath, String prediction_filepath,
			int timestep, String date_for_prediction) throws Exception {
		BufferedReader br_detect = new BufferedReader(new FileReader(
				detection_filepath));
		BufferedReader br_predict = new BufferedReader(new FileReader(
				prediction_filepath));

		String predict_final = prediction_filepath.substring(0,
				prediction_filepath.lastIndexOf("$")) + "_probabilty.csv";
		File f = new File(predict_final);
		if (!f.exists()) {

			BufferedWriter bw = new BufferedWriter(
					new FileWriter(predict_final));
			String[] label = new String[24 * (60 / timestep)];
			List<String> labels = null;
			String line_d = br_detect.readLine();
			String line_p = br_predict.readLine();
			bw.write(line_p + "\n");
			int i = 0;
			while ((line_p = br_predict.readLine()) != null && i < label.length) {
				br_detect = new BufferedReader(new FileReader(
						detection_filepath));
				labels = new ArrayList<String>();
				int count_usage = 0;
				int count_nousage = 0;
				while ((line_d = br_detect.readLine()) != null) {
					String time_d = line_d.split(",")[1];
					String time_p = line_p.split(",")[1];
					String day_d = line_d.split(",")[2];
					String day_p = line_p.split(",")[2];
					if (day_d.equals(day_p) && time_d.equals(time_p)) {
						labels.add(line_d.substring(line_d.lastIndexOf(",") + 1));
					}
				}
				br_detect.close();
				// System.out.println("size: " + labels.size() + " labels: " +
				// labels);
				Iterator<String> it = labels.listIterator();
				while (it.hasNext()) {
					String label_d = it.next();
					if (label_d.equals("USAGE")) {
						count_usage++;
					} else {
						count_nousage++;
					}
				}
				int threshold = calculateThreshold(count_usage, count_nousage);
				if ((count_usage + threshold) >= (count_nousage)) {
					label[i] = "USAGE";
				} else {
					label[i] = "NOUSAGE";
				}
				String[] params = line_p.split(",");
				for (int j = 0; j < params.length - 1; j++) {
					bw.write(params[j] + ",");
					// System.out.print(params[j] + ",");
				}
				bw.write(label[i] + "\n");
				// System.out.println(label[i]);
				i++;
			}

			br_predict.close();
			bw.flush();
			bw.close();
		}
		BufferedReader config = new BufferedReader(new FileReader(
				"thesis.config"));
		String line_c = null;
		String fp = null;
		while ((line_c = config.readLine()) != null) {
			if (line_c.contains("Location:")) {
				fp = line_c.split("n:")[1].trim();
			}
		}
		config.close();
		double dt_negatives = 0, rf_negatives = 0, ks_negatives = 0, bg_negatives = 0, cs_negatives = 0;
		predict_final = prediction_filepath.substring(0,
				prediction_filepath.lastIndexOf("$"))
				+ "Actual.csv";

		double high_negative = 0.0;
		int algo = 0;

		/* Classifier 1 */
		BufferedWriter output = new BufferedWriter(new FileWriter(fp
				+ "/output.csv", true));
		output.write(f.getName().split("_")[1] + "," + "Decision Table,"
				+ date_for_prediction + ",");
		output.flush();
		output.close();
		dt_negatives = Weka_Algorithm.applyWeka_DecisionTable(
				detection_filepath, predict_final);
		high_negative = dt_negatives;
		algo = 1;

		/* Classifier 2 */
		output = new BufferedWriter(new FileWriter(fp + "/output.csv", true));
		output.write(f.getName().split("_")[1] + "," + "Random Forest,"
				+ date_for_prediction + ",");
		output.flush();
		output.close();
		rf_negatives = Weka_Algorithm.applyWeka_RandomForest(
				detection_filepath, predict_final);
		if (rf_negatives > high_negative) {
			high_negative = rf_negatives;
			algo = 2;
		}

		/* Classifier 3 */
		output = new BufferedWriter(new FileWriter(fp + "/output.csv", true));
		output.write(f.getName().split("_")[1] + "," + "KStar,"
				+ date_for_prediction + ",");
		output.flush();
		output.close();
		ks_negatives = Weka_Algorithm.applyWeka_KStar(detection_filepath,
				predict_final);
		if (ks_negatives > high_negative) {
			high_negative = ks_negatives;
			algo = 3;
		}

		/* Classifier 4 */
		output = new BufferedWriter(new FileWriter(fp + "/output.csv", true));
		output.write(f.getName().split("_")[1] + "," + "BayesNet,"
				+ date_for_prediction + ",");
		output.flush();
		output.close();
		bg_negatives = Weka_Algorithm.applyWeka_Bagging(detection_filepath,
				predict_final);
		if (bg_negatives > high_negative) {
			high_negative = bg_negatives;
			algo = 4;
		}

		/* Classifier 5 */
		output = new BufferedWriter(new FileWriter(fp + "/output.csv", true));
		output.write(f.getName().split("_")[1] + ","
				+ "Cost Sensitive Classifier," + date_for_prediction + ",");
		output.flush();
		output.close();
		cs_negatives = Weka_Algorithm.applyWeka_CostSensitiveClassifier(
				detection_filepath, predict_final);
		if (cs_negatives > high_negative) {
			high_negative = cs_negatives;
			algo = 5;
		}

		/* Classifier 6 */
		output = new BufferedWriter(new FileWriter(fp + "/output.csv", true));
		output.write(f.getName().split("_")[1] + "," + "Vote,"
				+ date_for_prediction + ",");
		output.flush();
		output.close();
		if (algo == 1)
			Weka_Algorithm.applyWeka_DecisionTable(detection_filepath,
					predict_final);
		else if (algo == 2)
			Weka_Algorithm.applyWeka_RandomForest(detection_filepath,
					predict_final);
		else if (algo == 3)
			Weka_Algorithm.applyWeka_KStar(detection_filepath, predict_final);
		else if (algo == 4)
			Weka_Algorithm.applyWeka_Bagging(detection_filepath, predict_final);
		else if (algo == 5)
			Weka_Algorithm.applyWeka_CostSensitiveClassifier(
					detection_filepath, predict_final);
		else
			Weka_Algorithm.applyWeka_Vote(detection_filepath, predict_final);

	}

	public static int calculateThreshold(int count_usage, int count_nousage) {
		int threshold = 0;
		if (count_usage + count_nousage <= 1) {
			threshold = 0;
		} else {
			threshold = ((count_usage + count_nousage) / 2);
		}
		return threshold;
	}
}
