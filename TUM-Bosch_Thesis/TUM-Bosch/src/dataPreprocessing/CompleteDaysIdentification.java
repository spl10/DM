package dataPreprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Description: This module ensures Data Continuity for Time-Series Analysis.
 * 
 */
public class CompleteDaysIdentification {
	/**
	 * DATA CONTINUITY: This Function Identifies complete days based on the time
	 * information. For example, for each date, this function checks for every
	 * minute information starting from 00:00 to 23:59 ie. each day should have
	 * 24*60=1440 rows of data. If the information is available, then the day is
	 * counted as complete day.
	 * 
	 * @param date_list
	 * @param lof
	 * @param date_time
	 * @param filepath
	 * @return
	 */
	public static Set<String> identifyingCompleteDays(
			ArrayList<String> date_list, int lof, String[][] date_time) {

		Set<String> complete_days = new LinkedHashSet<String>(date_list);
		// System.out.println("All available days: " + complete_days);
		// System.out.println("No. of Available Days: " + complete_days.size());
		Iterator<String> li = complete_days.iterator();
		ArrayList<String> remove_list = new ArrayList<String>();
		int[] li_length = new int[date_list.toArray().length];
		int i = 0;
		while (li.hasNext()) {
			String li_date = li.next();
			for (int k = 0; k < lof; k++) {
				if (li_date.equals(date_time[0][k]))
					li_length[i]++;
			}
			if (li_length[i] < 1440) {

				// System.out.println("i: " + i + " with length[" + i + "]: "
				// + li_length[i] + " for Date: " + li_date);
				if (li_length[i] < 1400)
					remove_list.add(li_date);
			}
			i++;
		}
		complete_days.removeAll(remove_list);
		return complete_days;
	}

	/**
	 * Description: This function implements the algorithm to fill the missing
	 * values on the chosen complete days. Initially, the missing values are
	 * initialized to 0 and then they are replaces by previous or next values
	 * depending upon the situation.
	 * 
	 * @param complete_days
	 * @param filepath
	 * @return new_fp
	 * @throws Exception
	 */
	public static String dayCompletion(Set<String> complete_days,
			String filepath) throws Exception {
		String[] time_array = computeTime();
		Iterator<String> li = complete_days.iterator();
		String new_fp = filepath.split("\\.")[0] + "_NEW.csv";
		BufferedWriter bw = new BufferedWriter(new FileWriter(new_fp));
		BufferedReader br = new BufferedReader(new FileReader(filepath));
		String line = br.readLine();
		bw.write(line + "\n");
		String[] params = line.split(",");
		String[][] features = new String[params.length][complete_days.size() * 1440];
		int k = 0;
		while (li.hasNext()) {
			String li_date = li.next();
			for (int i = 0; i < time_array.length; i++) {
				features[0][k] = li_date;
				features[1][k] = time_array[i];
				for (int j = 2; j < params.length; j++) {
					features[j][k] = "0";
				}
				k++;
			}

		}
		int len = k;
		k = 0;
		while ((line = br.readLine()) != null && k < len) {
			params = line.split(",");
			if (line.contains(features[0][k])) {
				if (line.contains(features[1][k])) {
					for (int j = 0; j < params.length; j++) {
						features[j][k] = params[j];
						// System.out.print("features[" + j + "][" + k + "]: "
						// + features[j][k] + "\t");
					}
				} else {
					while (!features[1][k].equals(params[1])) {
						for (int j = 2; j < params.length; j++) {
							features[j][k] = "0";
							// System.out.print("features[" + j + "][" + k +
							// "]: "
							// + features[j][k] + "\t");
						}
						k++;
						// System.out.println();
					}
					for (int j = 0; j < params.length; j++) {
						features[j][k] = params[j];
						// System.out.print("features[" + j + "][" + k + "]: "
						// + features[j][k] + "\t");
					}
				}
				k++;
				// System.out.println();
			} else {
				Iterator<String> li_sort = complete_days.iterator();
				while (li_sort.hasNext()) {
					String li_date = li_sort.next();
					if (line.contains(li_date)) {
						while (!features[1][k].equals(params[1])) {
							for (int j = 2; j < params.length; j++) {
								features[j][k] = "0";
								// System.out.print("features[" + j + "][" + k +
								// "]: "
								// + features[j][k] + "\t");
							}
							k++;
							// System.out.println();
						}
						for (int j = 0; j < params.length; j++) {
							features[j][k] = params[j];
							// System.out.print("features[" + j + "][" + k +
							// "]: "
							// + features[j][k] + "\t");
						}
						k++;
					}
				}
			}
		}

		for (int i = 0; i < (complete_days.size() * 1440); i++) {
			boolean replace = true;
			for (int j = 2; j < params.length; j++) {
				if (!features[j][i].equals("0")) {
					replace = false;
				}
			}
			if (replace && i > 0) {
				for (int j = 2; j < params.length; j++) {
					features[j][i] = features[j][i - 1];
				}
			}
		}
		for (int i = 0; i < (complete_days.size() * 1440); i++) {
			bw.write(features[0][i]);
			// System.out
			// .print("features[0][" + i + "]: " + features[0][i] + "\t");
			for (int j = 1; j < params.length; j++) {
				// System.out.print("features[" + j + "][" + i + "]: "
				// + features[j][i] + "\t");
				bw.write("," + features[j][i]);
			}
			// System.out.println();
			bw.write("\n");
		}
		// System.out.println(filepath);
		// System.out.println("K==complete_days.size *1440 | k: " + k
		// + " complete_days.size() * 1440: "
		// + (complete_days.size() * 1440));
		br.close();
		bw.flush();
		bw.close();

		return new_fp;
	}

	/**
	 * Description: This function is used initialize the time array with
	 * 24*60=1440 values, starting from 00:00 to 23:59.
	 * 
	 * @return
	 */
	public static String[] computeTime() {
		String[] time_array = new String[1440];
		String[] hrs = { "00", "01", "02", "03", "04", "05", "06", "07", "08",
				"09", "10", "11", "12", "13", "14", "15", "16", "17", "18",
				"19", "20", "21", "22", "23" };
		String[] mins = { "00", "01", "02", "03", "04", "05", "06", "07", "08",
				"09", "10", "11", "12", "13", "14", "15", "16", "17", "18",
				"19", "20", "21", "22", "23", "24", "25", "26", "27", "28",
				"29", "30", "31", "32", "33", "34", "35", "36", "37", "38",
				"39", "40", "41", "42", "43", "44", "45", "46", "47", "48",
				"49", "50", "51", "52", "53", "54", "55", "56", "57", "58",
				"59" };
		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 60; j++) {
				time_array[(i * 60) + j] = hrs[i] + ":" + mins[j];
			}
		}
		return time_array;
	}
}
