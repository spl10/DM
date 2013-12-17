package visualization;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import descriptive_model.LabelCalculation;

public class Visualization {
	/**
	 * @param filepath
	 * @param date
	 * @param weekday2
	 * @param timestep
	 * @param selectionlength
	 * @param selectedParameters
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public static void visualize(String filepath, String[] date, int timestep,
			int selectionlength, String[] selectedParameters) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(filepath));
		filepath = filepath.substring(0, filepath.indexOf(".")) + "_vis.txt";
		BufferedWriter bw = new BufferedWriter(new FileWriter(filepath));

		/*
		 * uniqueSet - To get the set of unique days, which is done
		 * intentionally for visualization.
		 */
		Set<String> uniqueSet = new LinkedHashSet<String>(Arrays.asList(date));
		int uniquedate_l = uniqueSet.toArray().length, i = 0;
		System.out.println(uniqueSet);
		/*
		 * new_l is the number of values calculated for every hour(60) in a
		 * day(24).
		 */
		int timecount = 24 * (60 / timestep), j = 0, k = 0;
		String[][] act_date = new String[uniquedate_l][timecount];
		String[][] time = new String[uniquedate_l][timecount];
		String[][][] mean = new String[uniquedate_l][selectionlength][timecount];
		String Line = br.readLine();
		bw.write("time");
		// System.out.print("time");
		String[][] weekday = new String[uniquedate_l][selectionlength];
		for (int a = 0; a < uniquedate_l; a++) {
			bw.write(",  weekday");
			// System.out.print("\t weekday");
			for (i = 0; i < selectionlength; i++) {
				if (uniqueSet.toArray()[a] != null) {
					DateFormat sf = new SimpleDateFormat("dd.MM.yyyy");
					Date cur_date = sf.parse((String) uniqueSet.toArray()[a]);
					DateFormat sdf1 = new SimpleDateFormat("EEEEEE");
					weekday[a][i] = sdf1.format(cur_date);
					bw.write(" , " + Line.split(",")[i + 3] + "_$"
							+ uniqueSet.toArray()[a] + "_" + weekday[a][i]);
					// System.out.print("\t" + Line.split(",")[i + 3] + "_$"
					// + uniqueSet.toArray()[a] + "_" + weekday[a][i]);
				}
			}
		}
		while ((Line = br.readLine()) != null) {
			String cur_date = Line.split(",")[0];
			if (uniqueSet.contains(cur_date)) {
				/*
				 * index is the position of the current date cur_date in the
				 * uniqueSet. j is the length of array for each day, calculated
				 * based on the time step.
				 */
				int index = Arrays.asList(uniqueSet.toArray())
						.indexOf(cur_date);
				act_date[index][j] = cur_date;
				time[index][j] = Line.split(",")[2];
				for (i = 0; i < selectionlength; i++) {
					if (Line.split(",")[i + 3] != null) {
						mean[index][i][j] = Line.split(",")[i + 3];
					} else {
						mean[index][i][j] = "0";
					}
					// System.out.println("act_date[" + index + "][" + j + "]:"
					// + act_date[index][j] + " \t time[" + index + "]["
					// + j + "]:" + time[index][j] + " \t mean[" + index
					// + "][" + i + "][" + j + "]" + mean[index][i][j]);
				}
				if (j == 0 && j < timecount - 2) {
					j++;
				} else if (j > 0 && j < timecount - 1) {
					j++;
				} else {
					j = 0;
				}
			}
		}
		/* Get unique set of time based on the timestep */
		Set<String> unique_time = TimeArraytoList.arraytoListConversion(time,
				act_date);

		int uniquetime_l = unique_time.toArray().length;
		String[][] tot_mean = new String[selectionlength * uniquedate_l][uniquetime_l];
		k = 0;
		while (k < uniquetime_l) {
			// System.out.println();
			bw.write("\n");
			// System.out.print("" + unique_time.toArray()[k]);
			bw.write("" + unique_time.toArray()[k]);
			i = 0;
			while (i < uniquedate_l) {
				// System.out.print(" \t weekday[" + i + "][" + 0 + "] "
				// + weekday[i][0]);
				bw.write("," + weekday[i][0]);
				for (j = 0; j < selectionlength; j++) {
					if (mean[i][j][k] != null) {
						tot_mean[(selectionlength * i) + j][k] = mean[i][j][k];
						// System.out.print("\t tot_mean["
						// + ((selectionlength * i) + j) + "][" + k + "]"
						// + tot_mean[(selectionlength * i) + j][k]);
						bw.write("," + tot_mean[(selectionlength * i) + j][k]);
					} else {
						if ((k != 0)
								&& (tot_mean[(selectionlength * i) + j][k - 1] != null)) {
							tot_mean[(selectionlength * i) + j][k] = tot_mean[(selectionlength * i)
									+ j][k - 1];
							bw.write(","
									+ tot_mean[(selectionlength * i) + j][k]);
							// System.out.print(" \t tot_mean["
							// + ((selectionlength * i) + j) + "][" + k
							// + "]"
							// + tot_mean[(selectionlength * i) + j][k]);
						} else {
							bw.write(",0");
							// System.out.print(" \t tot_mean["
							// + ((selectionlength * i) + j) + "][" + k
							// + "]0");
						}
					}
				}
				i++;
			}
			k++;
		}

		// System.out.println();

		System.out.println("length of the array acc. to timestep: " + timecount
				+ " \t k:" + uniquetime_l + "\t unique_date:" + uniquedate_l);

		bw.flush();
		bw.close();
		LabelCalculation.calculateLabelBasedonActTemp(filepath, timestep,
				uniqueSet, selectionlength);
	}
}
