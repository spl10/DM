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

public class AddResultsToDetection {
	/**
	 * Building the Detection File: The previous data is written into the
	 * detection file. Example Detection File:
	 * "..\gateway_206010347\out_gt206010347_Actual.csv"
	 * 
	 * @param date
	 * @param li_dsm
	 * @param detection_filepath
	 * @throws Exception
	 */
	public static String DetectionModel(String[] date, List<String> li_dsm,
			String detection_filepath) throws Exception {
		// To check for empty file, to write the header to the file.
		Boolean emptyFile = false;
		File f = new File(detection_filepath);

		// If the data is not found in detection_filepath, it is written into
		// the detection_filepath.
		BufferedWriter bw = new BufferedWriter(new FileWriter(
				detection_filepath, true));
		String prediction_filepath = null;

		// List of previously available detection data.
		List<String> actual_files = new ArrayList<String>();

		if (f.length() == 0) {
			System.out.println("Empty File");
			emptyFile = true;
		}

		for (int i = li_dsm.size(); i > 0; i--) {
			String filepath = li_dsm.get(i - 1);
			filepath = filepath.split("\\.")[0] + "_Actual.csv";
			actual_files.add(filepath);
			System.out.println(filepath);
			String line = "";
			BufferedReader br = new BufferedReader(new FileReader(filepath));

			/*
			 * If the file is empty, the header line is written into the
			 * detection file along with the content from the first month in the
			 * gateway.
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

				// Checks whether the content of filepath is already available,
				// if not writes the content into the filepath.
				preventRewriting_days(date, detection_filepath, filepath);

				// Creates prediction file for the last date in the last month
				// in the list of files.
				if (i == 1)
					prediction_filepath = createPredictionFile(filepath, date);
			}
			br.close();
		}
		return prediction_filepath;
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
	public static String createPredictionFile(String filepath, String[] dates)
			throws IOException {

		// Manipulates unique filename with the corresponding date for which
		// prediction has to be done.
		String prediction_filepath = filepath.split("\\.")[0] + "_"
				+ dates[dates.length - 1] + "_$Prediction.csv";

		BufferedReader br = new BufferedReader(new FileReader(filepath));
		BufferedWriter bw = new BufferedWriter(new FileWriter(
				prediction_filepath, true));
		String line = br.readLine();
		int i = 0;

		bw.write("date,time,weekday,LABEL\n");
		while ((line = br.readLine()) != null) {
			if (line.split(",")[0].equals(dates[dates.length - 1])) {
				if (i == 0)
					bw.write(line.split(",")[0] + "," + line.split(",")[1]
							+ "," + line.split(",")[2] + ",USAGE" + "\n");
				else if (i == 1)
					bw.write(line.split(",")[0] + "," + line.split(",")[1]
							+ "," + line.split(",")[2] + ",NO USAGE" + "\n");
				else
					bw.write(line.split(",")[0] + "," + line.split(",")[1]
							+ "," + line.split(",")[2] + ",?" + "\n");
				i++;
			}
		}
		bw.close();
		br.close();
		return prediction_filepath;
	}

	/**
	 * With the array of missing dates, writes the data with missing dates into
	 * detection file.
	 * 
	 * @param dates
	 * 
	 * @param detection_filepath
	 * @param filepath
	 * @throws Exception
	 */
	public static void preventRewriting_days(String[] dates,
			String detection_filepath, String filepath) throws Exception {

		String[] missing_dates = findMissingDates(detection_filepath, filepath);

		BufferedWriter bw = new BufferedWriter(new FileWriter(
				detection_filepath, true));
		int i = 0;
		while (i < missing_dates.length && missing_dates[i] != null) {
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			String header = br.readLine();
			String line = header;
			while ((line = br.readLine()) != null)
				if (line.split(",")[0].equals(missing_dates[i])) {
					if (!line.split(",")[0].equals(dates[dates.length - 1])) {
						bw.write(line + "\n");
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
		System.out.println("size: " + dates.size() + " unique dates: " + dates);
		String missing_dates[] = new String[dates.size()];
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

			if (!found) {
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
