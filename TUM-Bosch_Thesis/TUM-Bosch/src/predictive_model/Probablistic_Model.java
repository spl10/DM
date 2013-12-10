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
			String detection_filepath, String prediction_filepath, int timestep)
			throws Exception {
		BufferedReader br_detect = new BufferedReader(new FileReader(
				detection_filepath));
		BufferedReader br_predict = new BufferedReader(new FileReader(
				prediction_filepath));

		String predict_final = prediction_filepath.substring(0,
				prediction_filepath.lastIndexOf("$")) + "_probabilty.csv";
		BufferedWriter bw = new BufferedWriter(new FileWriter(predict_final));
		String[] label = new String[24 * (60 / timestep)];
		List<String> labels = null;
		String month = null;
		String line_d = br_detect.readLine();
		String line_p = br_predict.readLine();
		bw.write(line_p + "\n");
		int i = 0;
		while ((line_p = br_predict.readLine()) != null) {
			br_detect = new BufferedReader(new FileReader(detection_filepath));
			labels = new ArrayList<String>();
			int count_usage = 0;
			int count_nousage = 0;
			while ((line_d = br_detect.readLine()) != null) {
				month = line_p.split(",")[0].split("\\.")[1];
				String time_d = line_d.split(",")[1];
				String time_p = line_p.split(",")[1];
				String day_d = line_d.split(",")[2];
				String day_p = line_p.split(",")[2];
				if (day_d.equals(day_p) && time_d.equals(time_p)) {
					labels.add(line_d.substring(line_d.lastIndexOf(",") + 1));
				}
			}
			br_detect.close();
			System.out.println("size: " + labels.size() + " labels: " + labels);
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
			if (count_usage >= (count_nousage - threshold)) {
				label[i] = "USAGE";
			} else {
				label[i] = "NO USAGE";
			}
			String[] params = line_p.split(",");
			for (int j = 0; j < params.length - 1; j++) {
				bw.write(params[j] + ",");
				System.out.print(params[j] + ",");
			}
			bw.write(label[i] + "\n");
			System.out.println(label[i]);
			i++;
		}
		File f = new File(predict_final);
		String new_path = predict_final.split(month)[0] + month
				+ "\\probabilistic_model\\" + f.getName();
		File f_new = new File(new_path);
		f_new.setExecutable(true, false);
		System.out.println(new_path);
		if (f.renameTo(f_new))
			System.err.println("File Moved Successfully!!!");
		else
			System.err.println("File not Moved!!!");
		br_predict.close();
		bw.close();
		Weka_Algorithm.applyWeka_Prediction(Actual_filepath, predict_final);
		Weka_Algorithm.applyWeka_Prediction(detection_filepath, predict_final);
	}

	public static int calculateThreshold(int count_usage, int count_nousage) {
		int threshold = 0;
		if (count_usage + count_nousage <= 5) {
			threshold = 0;
		} else {
			threshold = ((count_usage + count_nousage) / 6);
		}
		return threshold;
	}
}
