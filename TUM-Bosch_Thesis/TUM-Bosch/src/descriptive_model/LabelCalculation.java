package descriptive_model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import model_class.Prediction;
import model_class.WEKAInputFiles;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import predictive_model.User_Input_For_Prediction;
import predictive_model.Weka_Algorithm_For_Prediction;

/* Label is calculated based on the parameters 
 * selected by the user. 
 */
/**
 * @author SIP2LOL
 * 
 */
public class LabelCalculation {
	@SuppressWarnings("unchecked")
	public static void calculateLabelBasedonActTemp(String filepath,
			int timestep, Set<String> uniqueSet, int selectionlength)
			throws Exception {
		int timecount = 24 * (60 / timestep);
		int uniquedate_l = uniqueSet.toArray().length;
		String[] Date = new String[uniquedate_l];
		for (int i = 0; i < uniquedate_l; i++) {
			Date[i] = uniqueSet.toArray()[i].toString(); // For User Input
		}

		BufferedReader br = new BufferedReader(new FileReader(filepath));
		String file_predict = filepath.substring(0, filepath.indexOf("."))
				+ "_Predict.csv";
		BufferedWriter bw_predicted = new BufferedWriter(new FileWriter(
				file_predict, true));

		String line = br.readLine();
		Double[][][] param = new Double[selectionlength][uniquedate_l][timecount];
		Double[][] at_val = new Double[uniquedate_l][timecount];
		String[][] weekday = new String[uniquedate_l][timecount];
		String[][] label = new String[uniquedate_l][timecount];
		Double[][] diff = new Double[uniquedate_l][timecount];
		String[] time = new String[timecount];
		int[] at = new int[uniquedate_l];
		Double[] median = new Double[uniquedate_l];
		String[] names = new String[selectionlength];
		DescriptiveStatistics stats = new DescriptiveStatistics();
		Map<Double, Integer> hm = new TreeMap<Double, Integer>();
		Prediction prediction = new Prediction();
		Map.Entry<Double, Integer> tmp = null;
		int j = 0;

		for (int n = 0; n < selectionlength; n++) {
			names[n] = line.split(",")[n + 2].toLowerCase().substring(0,
					line.split(",")[n + 2].indexOf("$") - 1);
		}
		for (int i = 0; i < line.split(",").length; i++) {
			String act_temp = line.split(",")[i].toLowerCase();
			if (act_temp.contains("actual_temperature")) {
				at[j] = i;
				j++;
			}
		}
		System.out.println();
		int i = 0, k = 0;
		while (((line = br.readLine()) != null) && (i < timecount)) {
			time[i] = line.split(",")[0];

			for (j = 0; j < at.length; j++) {
				String l = line.split(",")[at[j]];
				String day = line.split(",")[at[j] - 1];
				weekday[j][i] = day;
				for (k = 0; k < selectionlength; k++) {
					param[k][j][i] = Math.round((Double.parseDouble(line
							.split(",")[at[j] + k])) * 100.0) / 100.0;
					// System.out.print("param[" + k + "][" + j + "][" + i
					// + "] : " + param[k][j][i] + "\t");
				}
				// System.out.println("weekday[" + j + "][" + i + "]"
				// + weekday[j][i]);
				at_val[j][i] = Math.round((Double.parseDouble(l)) * 100.0) / 100.0;
			}
			i++;
		}
		for (j = 0; j < at.length; j++) {
			hm = new TreeMap<Double, Integer>();
			for (k = 0; k < timecount; k++) {
				if (k > 0) {
					diff[j][k] = Math
							.round((param[0][j][k - 1] - param[0][j][k]) * 100.0) / 100.0;
					if (diff[j][k] != 0.0 && diff[j][k] != -1 && diff[j][k] > 0) {
						stats.addValue(diff[j][k]);
						if (hm != null && hm.get(diff[j][k]) != null)
							hm.put(diff[j][k], hm.get(diff[j][k]) + 1);
						else
							hm.put(diff[j][k], 1);
					}
				} else {
					if (j > 0)
						diff[j][k] = Math
								.round((param[0][j - 1][timecount - 1] - param[0][j][k]) * 100.0) / 100.0;
					else
						diff[j][k] = 0.0;
				}
			}
			Iterator<Map<Double, Integer>> im = valueIterator(hm);
			median[j] = stats.getPercentile(60);
			System.out.println("median: " + median[j]);
			while (im.hasNext()) {
				tmp = (Entry<Double, Integer>) im.next();
				System.out.print(tmp + " , ");
			}
			System.out.println();
		}
		System.out.println();
		for (j = 0; j < at.length; j++) {
			for (k = 0; k < timecount; k++) {
				if (label[j][k] == null)
					label[j][k] = "NO USAGE";
				if (j > 0 && k < (timecount) && (diff[j][k]) >= median[j - 1]) {
					label[j][k] = "USAGE";
				} else if (j == 0 && k < (timecount) && (diff[j][k]) >= 0.5) {
					label[j][k] = "USAGE";
				}
			}
		}
		String file_act = filepath.substring(0, filepath.indexOf("."))
				+ "_Actual.csv";
		BufferedWriter bw_actual = new BufferedWriter(new FileWriter(file_act));

		bw_actual.write("date,time,weekday");
		bw_predicted.write("date,time,weekday");
		for (int n = 0; n < selectionlength; n++) {
			bw_actual.write("," + names[n]);
			bw_predicted.write("," + names[n]);
		}
		bw_actual.write(",Act_Diff");
		bw_actual.write(",LABEL\n");
		bw_predicted.write(",LABEL\n");
		j = 0;
		while (j < at.length) {
			for (i = 0; i < timecount; i++) {
				Date[j] = uniqueSet.toArray()[j].toString();
				bw_actual.write(uniqueSet.toArray()[j] + "," + time[i] + ","
						+ weekday[j][i]);
				bw_predicted.write(uniqueSet.toArray()[j] + "," + time[i] + ","
						+ weekday[j][i]);

				for (k = 0; k < selectionlength; k++) {
					bw_actual.write("," + param[k][j][i]);
					bw_predicted.write("," + param[k][j][i]);
				}
				bw_actual.write("," + diff[j][i]);
				bw_actual.write("," + label[j][i] + "\n");
				if (i == 0)
					bw_predicted.write(",USAGE\n");
				else if (i == 1)
					bw_predicted.write(",NO USAGE\n");
				else
					bw_predicted.write(",?\n");
			}
			j++;
		}

		prediction.setWeekday(weekday);
		prediction.setLabel(label);
		prediction.setTime(time);
		prediction.setParam(param);
		prediction.setDate(Date);
		for (i = 0; i < at.length; i++) {
			for (j = 0; j < timecount; j++) {
				System.out.print("date[" + i + "]: " + prediction.getDate()[i]
						+ " weekday[" + i + "][" + j + "]: "
						+ prediction.getWeekday()[i][j] + " time[" + j + "]: "
						+ prediction.getTime()[j]);
				for (k = 0; k < selectionlength; k++) {
					System.out.print("\tparam[" + k + "][" + i + "][" + j + "]"
							+ prediction.getParam()[k][i][j]);
				}
				System.out.println(" label[" + i + "][" + j + "]: "
						+ label[i][j]);
			}
		}

		bw_predicted.flush();
		bw_predicted.close();

		bw_actual.flush();
		bw_actual.close();
		br.close();
		WEKAInputFiles wif = User_Input_For_Prediction.userInput(Date,
				filepath, timestep);
		System.out.println(file_predict);
		String file = wif.getDetection_filepath().substring(0,
				wif.getDetection_filepath().indexOf("."))
				+ ".arff";
		File f = new File(file);
		if (f.exists()) {
			f.delete();
		}
		file = Weka_Algorithm.applyWeka(wif.getDetection_filepath());

		System.out.println("\n" + wif.getPrediction_filepath() + "\n");
		Weka_Algorithm_For_Prediction.applyWeka(file,
				wif.getPrediction_filepath(), prediction, names);
		Weka_Algorithm_For_Prediction.dailyBasisFiles(file, file_predict,
				prediction, names);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Iterator<Map<Double, Integer>> valueIterator(
			Map<Double, Integer> map) {
		Set set = new TreeSet(new Comparator<Map.Entry<Double, Integer>>() {
			@Override
			public int compare(Entry<Double, Integer> o1,
					Entry<Double, Integer> o2) {
				return o2.getKey().compareTo(o1.getKey()) > 0 ? 1 : -1;
			}
		});
		set.addAll(map.entrySet());
		return set.iterator();
	}
}
