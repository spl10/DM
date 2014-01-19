package data_preprocessing.timestep;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author SIP2LOL
 * 
 */
public class CompleteDaysIdentification {
	/**
	 * This Function Identifies complete days based on the time information. For
	 * example, for each date, this function checks for every minute information
	 * starting from 00:00 to 23:59. If the information is available, then the
	 * day is counted as complete day.
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
		System.out.println("All available days: " + complete_days);
		System.out.println("No. of Available Days: " + complete_days.size());
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

				System.out.println("i: " + i + " with length[" + i + "]: "
						+ li_length[i] + " for Date: " + li_date);
				if (li_length[i] < 1400)
					remove_list.add(li_date);
			}
			i++;
		}
		complete_days.removeAll(remove_list);
		return complete_days;
	}

	public static String dayCompletion(Set<String> complete_days,
			String filepath) throws Exception {
		String[] time_array = computeTime();
		Iterator<String> li = complete_days.iterator();

		String tmp = filepath.split("\\.")[0] + "_NEW.csv";
		// File temp = new File(tmp);
		// temp.deleteOnExit();
		BufferedWriter bw = new BufferedWriter(new FileWriter(tmp));
		BufferedReader br = new BufferedReader(new FileReader(filepath));
		String line = br.readLine();
		bw.write(line + "\n");
		// System.out.println(line);
		int i = 0;
		while (li.hasNext()) {
			String li_date = li.next();
			String prev_line = null;

			while ((line = br.readLine()) != null) {
				String rest_line = line.split(":")[1].substring(2);
				String[] params = line.split(",");
				while (!line.contains(time_array[i])) {
					if (prev_line == null) {
						bw.write(li_date + "," + time_array[i]);
						// System.out.print(li_date + "," + time_array[i]);
						for (int l = 2; l < params.length; l++) {
							bw.write(",0");
							// System.out.print(",0");
						}
						bw.write("\n");
						// System.out.println();
					} else {
						bw.write(li_date + "," + time_array[i] + prev_line
								+ "\n");
						// System.out.println(li_date + "," + time_array[i]
						// + prev_line);
					}
					i++;
					if (i == 1440) {
						i = 0;
					}
				}
				bw.write(line + "\n");
				// System.out.println(line);
				prev_line = rest_line;
				i++;
				if (i == 1440) {
					i = 0;
				}
			}

		}
		br.close();
		bw.flush();
		bw.close();
		String new_fp = filepath.split("\\.")[0] + "_tmp.csv";
		return tmp;
	}

	public static String copyValues(String new_fp, String tmp) throws Exception {

		boolean repeat = true;
		while (repeat) {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new_fp));
			BufferedReader br = new BufferedReader(new FileReader(tmp));
			String line = br.readLine();
			lo: while ((line = br.readLine()) != null) {
				String[] params = line.split(",");
				int flip = 0;
				for (int l = 2; l < params.length; l++) {
					if (!params[l].equals("0")) {
						flip = 1;
						repeat = false;
					}
					if (flip == 0) {
						repeat = true;
						break lo;
					}
				}
			}
			br.close();
			br = new BufferedReader(new FileReader(tmp));
			line = br.readLine();
			bw.write(line + "\n");
			String prev_line = null;
			while ((line = br.readLine()) != null) {
				if (prev_line != null) {
					String[] params = prev_line.split(",");
					String date = prev_line.split(",")[0];
					String time = prev_line.split(",")[1];
					String rest_line = line.split(":")[1].substring(2);
					boolean replace = true;
					for (int l = 2; l < params.length; l++) {
						if (!params[l].equals("0")) {
							replace = false;
						}
					}
					if (replace) {
						bw.write(date + "," + time + rest_line + "\n");
					} else {
						bw.write(prev_line + "\n");
					}
				}
				prev_line = line;
			}
			bw.write(prev_line + "\n");
			br.close();
			bw.flush();
			bw.close();
			String mp = null;
			mp = new_fp;
			new_fp = tmp;
			tmp = mp;
		}
		File f = new File(new_fp);
		f.delete();
		return tmp;
	}

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
