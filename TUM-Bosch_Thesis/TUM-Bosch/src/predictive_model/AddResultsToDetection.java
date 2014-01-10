package predictive_model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import descriptive_model.Weka_Algorithm;

public class AddResultsToDetection {
	/**
	 * Building the Detection File: The previous data is written into the
	 * detection file. Example Detection File:
	 * "..\gateway_206010347\out_gt206010347_Actual.csv"
	 * 
	 * @param dates
	 * @param li_dsm
	 * @param detection_filepath
	 * @param file_act
	 * @param timestep
	 * @throws Exception
	 */
	public static void DetectionModel(String[] dates, List<String> li_dsm,
			String detection_filepath, int timestep, String file_act)
			throws Exception {
		// To check for empty file, to write the header to the file.
		Boolean emptyFile = false;
		String prediction_filepath = null;
		File f = new File(detection_filepath);
		// If the data is not found in detection_filepath, it is written into
		// the detection_filepath.
		BufferedWriter bw = new BufferedWriter(new FileWriter(
				detection_filepath, true));
		BufferedReader config = new BufferedReader(new FileReader(
				"thesis.config"));
		String line_c = null;
		String fp = null;
		String config_month = null;
		String config_year = null;
		while ((line_c = config.readLine()) != null) {
			if (line_c.contains("Location:")) {
				fp = line_c.split("n:")[1].trim();
			}
			if (line_c.contains("Prediction:")) {
				config_month = line_c.split("n:")[1].trim().split("\\.")[0];
				config_year = line_c.split("n:")[1].trim().split("\\.")[1];
			}
		}
		config.close();
		// List of previously available detection data.
		List<String> actual_files = new ArrayList<String>();

		if (f.length() == 0) {
			// System.out.println("Empty File");
			emptyFile = true;
		}
		for (int j = 0; j < dates.length; j++) {
			String date_for_prediction = dates[j]; // dateSelectedByUser(dates);

			List<String> date = TrimDateArray(date_for_prediction, dates,
					config_month, config_year);
			String[] new_dates = new String[date.size()];
			date.toArray(new_dates);
			// System.out.println("New Dates: " + date);

			for (int i = li_dsm.size() - 1; i >= 0; i--) {
				String filepath = li_dsm.get(i);
				filepath = filepath.split("\\.")[0] + "_Actual.csv";
				f = new File(filepath);
				if (f.exists()) {
					actual_files.add(filepath);
					// System.out.println(filepath);
					String line = "";
					BufferedReader br = new BufferedReader(new FileReader(
							filepath));

					/*
					 * If the file is empty, the header line is written into the
					 * detection file along with the content from the first
					 * month in the gateway.
					 */
					if (emptyFile) {
						while ((line = br.readLine()) != null) {
							bw.write(line + "\n");
						}
						emptyFile = false;
						bw.flush();
						bw.close();
					} else {
						br.close();

						// Checks whether the content of filepath is already
						// available,
						// if not writes the content into the filepath.
						preventRewriting_days(new_dates, detection_filepath,
								filepath);

						// Creates prediction file for the last date in the
						// last
						// month
						// in the list of files.
						if (new_dates[0].split("\\.")[1].equals(config_month)
								&& new_dates[0].split("\\.")[2]
										.equals(config_year))
							prediction_filepath = createPredictionFile(
									filepath, date_for_prediction);

					}
					br.close();
				}
			}
			// System.out.println(detection_filepath);
			String file = detection_filepath.substring(0,
					detection_filepath.indexOf("."))
					+ ".arff";
			f = new File(file);
			if (f.exists()) {
				f.delete();
			}

			if (new_dates[0].split("\\.")[1].equals(config_month)
					&& new_dates[0].split("\\.")[2].equals(config_year)) {
				if (j == 0)
					file = Weka_Algorithm.applyWeka(detection_filepath);
				BufferedWriter output = new BufferedWriter(new FileWriter(fp
						+ "/output.csv", true));
				output.write(f.getName().split("_")[1] + ","
						+ "Decision Table," + date_for_prediction + ",");
				output.flush();
				output.close();
				// System.out.println("\n" + wif.getPrediction_filepath() +
				// "\n");
				Probablistic_Model.algorithm(file_act, detection_filepath,
						prediction_filepath, timestep, 1);
				output = new BufferedWriter(new FileWriter(fp + "/output.csv",
						true));
				output.write(f.getName().split("_")[1] + "," + "Random Forest,"
						+ date_for_prediction + ",");
				output.flush();
				output.close();
				// System.out.println("\n" + wif.getPrediction_filepath() +
				// "\n");
				Probablistic_Model.algorithm(file_act, detection_filepath,
						prediction_filepath, timestep, 2);
				output = new BufferedWriter(new FileWriter(fp + "/output.csv",
						true));
				output.write(f.getName().split("_")[1] + "," + "KStar,"
						+ date_for_prediction + ",");
				output.flush();
				output.close();
				// System.out.println("\n" + wif.getPrediction_filepath() +
				// "\n");
				Probablistic_Model.algorithm(file_act, detection_filepath,
						prediction_filepath, timestep, 3);
				output = new BufferedWriter(new FileWriter(fp + "/output.csv",
						true));
				output.write(f.getName().split("_")[1] + "," + "Bagging,"
						+ date_for_prediction + ",");
				output.flush();
				output.close();
				// System.out.println("\n" + wif.getPrediction_filepath() +
				// "\n");
				Probablistic_Model.algorithm(file_act, detection_filepath,
						prediction_filepath, timestep, 4);
				output = new BufferedWriter(new FileWriter(fp + "/output.csv",
						true));
				output.write(f.getName().split("_")[1] + ","
						+ "Cost Sensitive Classifier," + date_for_prediction
						+ ",");
				output.flush();
				output.close();
				// System.out.println("\n" + wif.getPrediction_filepath() +
				// "\n");
				Probablistic_Model.algorithm(file_act, detection_filepath,
						prediction_filepath, timestep, 5);
			}
		}
	}

	/**
	 * Remove all the dates after the date selected by the user.For prediction,
	 * we use only the past information, future information is not necessary.
	 * 
	 * @param date_for_prediction
	 * @param dates
	 * @param config_year
	 * @param config_month
	 * @return
	 */
	public static List<String> TrimDateArray(String date_for_prediction,
			String[] dates, String config_month, String config_year) {
		List<String> new_dates = new ArrayList<String>();
		dates: for (int i = 0; i < dates.length; i++) {
			if (!(date_for_prediction.split("\\.")[1].equals(config_month) && date_for_prediction
					.split("\\.")[2].equals(config_year))) {
				new_dates.add(dates[i]);
			} else {
				if (!dates[i].equals(date_for_prediction)) {
					new_dates.add(dates[i]);
				} else {
					new_dates.add(dates[i]);
					break dates;
				}
			}
		}
		return new_dates;
	}

	/**
	 * Creates Prediction WEKA Input File. Gets the filepath of the last month
	 * as input and creates the prediction file for the last day in the file.
	 * 
	 * @param filepath
	 * @param dates
	 * @return prediction_filepath
	 * @throws IOException
	 */
	public static String createPredictionFile(String filepath,
			String date_for_prediction) throws IOException {

		// Manipulates unique filename with the corresponding date for which
		// prediction has to be done.
		String prediction_filepath = filepath.split("\\.")[0] + "_"
				+ date_for_prediction + "_$Prediction.csv";
		String actual_filepath = filepath.split("\\.")[0] + "_"
				+ date_for_prediction + "_Actual.csv";
		File f = new File(prediction_filepath);

		BufferedReader br = new BufferedReader(new FileReader(filepath));
		BufferedWriter bw = new BufferedWriter(new FileWriter(
				prediction_filepath, true));
		BufferedWriter bw_actual = new BufferedWriter(new FileWriter(
				actual_filepath));
		String line = br.readLine();
		int i = 0;
		if (f.length() == 0) {
			bw.write("date,time,weekday, dt00233_0dhw_setpoint, dt00209_0-1outdoor_temperature_measured_value,LABEL\n");
			bw_actual
					.write("date,time,weekday, dt00233_0dhw_setpoint, dt00209_0-1outdoor_temperature_measured_value,LABEL\n");
		}
		while ((line = br.readLine()) != null) {
			if (line.split(",")[0].equals(date_for_prediction)) {
				if (i == 0)
					bw.write(line.split(",")[0] + "," + line.split(",")[1]
							+ "," + line.split(",")[2] + ","
							+ line.split(",")[4] + "," + line.split(",")[5]
							+ ",USAGE" + "\n");
				else if (i == 1)
					bw.write(line.split(",")[0] + "," + line.split(",")[1]
							+ "," + line.split(",")[2] + ","
							+ line.split(",")[4] + "," + line.split(",")[5]
							+ ",NOUSAGE" + "\n");
				else
					bw.write(line.split(",")[0] + "," + line.split(",")[1]
							+ "," + line.split(",")[2] + ","
							+ line.split(",")[4] + "," + line.split(",")[5]
							+ ",?" + "\n");
				bw_actual.write(line.split(",")[0] + "," + line.split(",")[1]
						+ "," + line.split(",")[2] + "," + line.split(",")[4]
						+ "," + line.split(",")[5] + "," + line.split(",")[10]
						+ "\n");
				i++;
			}
		}
		bw_actual.flush();
		bw_actual.close();
		bw.flush();
		bw.close();
		br.close();
		return prediction_filepath;
	}

	/**
	 * With the array of missing dates, writes the data with missing dates into
	 * detection file.
	 * 
	 * @param new_dates
	 * 
	 * @param detection_filepath
	 * @param filepath
	 * @throws Exception
	 */
	public static void preventRewriting_days(String[] new_dates,
			String detection_filepath, String filepath) throws Exception {

		String[] missing_dates = findMissingDates(detection_filepath, filepath);

		BufferedWriter bw = new BufferedWriter(new FileWriter(
				detection_filepath, true));
		int i = 0;
		dates: while (i < missing_dates.length && missing_dates[i] != null) {
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			String header = br.readLine();
			String line = header;
			while ((line = br.readLine()) != null)
				if (line.split(",")[0].equals(missing_dates[i])) {
					if (!line.split(",")[0]
							.equals(new_dates[new_dates.length - 1])) {
						bw.write(line + "\n");
					} else {
						break dates;
					}
				}
			br.close();
			i++;
		}
		bw.close();
	}

	/**
	 * With the array of unique dates for each month, checks for the missing
	 * dates in the detection file, all the missing dates are stored into the
	 * array missing_dates[].
	 * 
	 * @param detection_filepath
	 * @param filepath
	 * @return missing_dates
	 * @throws Exception
	 */
	public static String[] findMissingDates(String detection_filepath,
			String filepath) throws Exception {
		Set<String> dates = collectUniqueDatesFromFile(filepath);
		// System.out.println("size: " + dates.size() + " unique dates: " +
		// dates);
		String missing_dates[] = new String[dates.size()];
		BufferedReader config = new BufferedReader(new FileReader(
				"thesis.config"));
		String line_c = null;

		String config_date = null;
		while ((line_c = config.readLine()) != null) {
			if (line_c.contains("Prediction:")) {
				config_date = line_c.split("n:")[1].trim();
			}
		}
		config.close();
		BufferedReader bw_br = new BufferedReader(new FileReader(
				detection_filepath));
		String bw_line = bw_br.readLine();
		Boolean found = false;
		int j = 0;
		for (int i = 0; i < dates.size(); i++) {
			found = false;
			find_date: while ((bw_line = bw_br.readLine()) != null) {
				if (bw_line.contains(dates.toArray()[i].toString())) {
					found = true;
					break find_date;
				}
			}

			if (!found && !dates.toArray()[i].toString().contains(config_date)) {
				missing_dates[j] = dates.toArray()[i].toString();
				System.out.println("Missing date: " + missing_dates[j]);
				j++;
			}
		}
		bw_br.close();
		return missing_dates;
	}

	/**
	 * Collects the unique dates from the filepath.
	 * 
	 * @param filepath
	 * @return dates
	 * @throws IOException
	 */
	public static Set<String> collectUniqueDatesFromFile(String filepath)
			throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filepath));
		List<String> date = new ArrayList<String>();
		String line = br.readLine();
		while ((line = br.readLine()) != null) {
			date.add(line.split(",")[0]);
		}
		br.close();
		Set<String> dates = new LinkedHashSet<String>(date);
		return dates;
	}
}
