package dataPreprocessing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import dataVisualization.Visualization;

/**
 * Writes the output of Time-Series Conversion into a csv file.
 * 
 */
public class WriteToCSV {
	/**
	 * @param filepath
	 * @param date_arr
	 * @param sampling_time
	 * @param mean
	 * @param index
	 * @param selectionlength
	 * @param timestep
	 * @param selectedParameters
	 * @param final_arr_len
	 * @throws Exception
	 */
	public static void csvWriter(String filepath, String[] date_arr,
			String[] sampling_time, String[][] mean, int index,
			int selectionlength, int timestep, String[] selectedParameters,
			int final_arr_len) throws Exception {
		filepath = filepath.substring(0, filepath.indexOf(".")) + "_"
				+ timestep + "ts.csv";
		BufferedWriter new_csv = new BufferedWriter(new FileWriter(filepath));
		new_csv.write("date_arr,day,time");
		/* System.out.print("date_arr \t day \t time"); */
		for (int i = 0; i < selectionlength; i++) {
			new_csv.write("," + selectedParameters[i]);
			// System.out.print("\t " + selectedParameters[i]);
		}
		new_csv.write("\n");
		// System.out.println();

		String weekday = null;
		for (int j = 0; j < index - 1; j++) {
			if (date_arr[j] != null && sampling_time[j] != null) {
				DateFormat sf = new SimpleDateFormat("dd.MM.yyyy");
				Date cur_date = sf.parse(date_arr[j]);
				DateFormat sdf1 = new SimpleDateFormat("EEEEEE");
				weekday = sdf1.format(cur_date);
				new_csv.write(date_arr[j] + "," + weekday + ","
						+ sampling_time[j]);
				// System.out.print("j: " + j + "\t " + date_arr[j] + "\t "
				// + weekday + " \t " + sampling_time[j]);
			}
			for (int i = 0; i < selectionlength; i++) {
				if (mean[i][j] != null) {
					if (!mean[i][j].isEmpty()) {
						new_csv.write("," + mean[i][j]);
						// System.out.print("\t mean[" + i + "][" + (j) + "] "
						// + mean[i][j]);
					} else {
						new_csv.write(",0");
						// System.out.print("\t mean[" + i + "][" + (j) +
						// "] 0");
					}
				}
			}
			new_csv.write("\n");
			// System.out.println();
		}

		new_csv.flush();
		new_csv.close();

		/* Prepares the file for visualization using BIRT Tool. */
		Visualization.visualize(filepath, date_arr, timestep, selectionlength,
				selectedParameters);
	}
}
