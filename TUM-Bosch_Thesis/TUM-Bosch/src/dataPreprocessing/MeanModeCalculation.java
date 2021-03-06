package dataPreprocessing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Set;

import model.Detection;
import model.Number_of_starts;

public class MeanModeCalculation {

	/**
	 * Description: This function is also a part of time-series analysis. Mean
	 * is calculated on numerical attributes, mode is calculated for the binary
	 * attributes.
	 * 
	 * @param detection
	 * @param ns
	 * @param l
	 * @throws Exception
	 */
	public static void meanModePreprocessing(Detection detection,
			Number_of_starts ns, int l) throws Exception {
		Set<String> complete_days = detection.getComplete_days();
		int timestep = detection.getTimestep();
		int index = 0, j = 0, m = 0, k = 0, time = 0;
		if ((60 % timestep) == 0)
			index = ((24 * 60 / timestep)) * (complete_days.toArray().length)
					+ 1;
		String[] selectedParameters = detection.getSelectedParameters();
		int selectionlength = selectedParameters.length;
		String[] date_arr = new String[index];
		String[] sampling_time = new String[index];
		String[][] mean = new String[selectionlength][index];
		int[] count = new int[selectionlength];
		float[] sum = new float[selectionlength];
		Double[] param = new Double[selectionlength];

		int[] temp = detection.getTemp();
		String[] type = detection.getType();
		int pos_nos = ns.getPosition();
		int threshold_nos = ns.getThreshold();

		String filepath = detection.getFilepath();
		BufferedReader br = new BufferedReader(new FileReader(filepath));
		String line = br.readLine();

		// System.out.println();
		while ((line = br.readLine()) != null) {
			int mins = Integer.parseInt(line.split(",")[1].split(":")[1]);
			int hrs = Integer.parseInt(line.split(",")[1].split(":")[0]);
			time = (hrs * 60) + mins;

			for (int c = 0; c < complete_days.toArray().length; c++) {
				if (complete_days.toArray()[c].equals(line.split(",")[0])
						&& j < index) {

					date_arr[j] = line.split(",")[0];
					sampling_time[j] = formatTime(mins,
							line.split(",")[1].split(":")[0]);
					for (int i = 0; i < temp.length; i++) {
						String tmp = null;
						if (temp[i] < line.split("\\,").length)
							tmp = line.split("\\,")[temp[i]];

						if (tmp == null || tmp.isEmpty()) {
							param[i] = 0.0;
							tmp = "0";
						}
						if (temp[i] < line.split("\\,").length
								&& !tmp.isEmpty()) {
							param[i] = Double.parseDouble(tmp);
							if (temp[i] == pos_nos) {
								if (sampling_time[j].equals("00:00")) {
									threshold_nos = Integer.parseInt(line
											.split(",")[pos_nos]);
									param[i] = 0.0;
								} else {
									param[i] = Double.parseDouble(tmp)
											- threshold_nos;
								}
							}
						} else {
							param[i] = 0.0;
						}
						if (type[i].equals("Binary")) {
							if ((time % timestep == 0)
									|| (c == complete_days.toArray().length - 1 && time == 1439)) {
								if (j > 0) {
									if (count[i] > (k / 2))
										mean[i][j - 1] = "1";
									else {
										mean[i][j - 1] = "0";
									}
									count[i] = 0;
									count[i] += param[i];
								}
								if (i == (selectionlength - 1))
									j++;
							} else {
								if (param[i] == 1)
									count[i]++;
							}
						}
						if (type[i].equals("FindMax")) {
							if ((time % timestep == 0)
									|| (c == complete_days.toArray().length - 1 && time == 1439)) {
								if (j > 0) {
									mean[i][j - 1] = String
											.valueOf(Math
													.round((sum[i] / timestep) * 100.0) / 100.0);
									sum[i] = 0;
									sum[i] += param[i];
								}
								if (i == (selectionlength - 1))
									j++;
							} else {
								sum[i] += param[i];
							}
						}
						if (type[i].equals("Numeric")) {
							if ((time % timestep == 0)
									|| (c == complete_days.toArray().length - 1 && time == 1439)) {
								if (j > 0) {
									mean[i][j - 1] = String
											.valueOf(Math
													.round((sum[i] / k) * 100.0) / 100.0);
									sum[i] = 0;
									sum[i] += param[i];
								}

								if (i == (selectionlength - 1))
									j++;
							} else {
								sum[i] += param[i];
							}
						}
					}
					if (time % timestep == 0 && j != 0) {
						k = 0;
					}
					k++;

					m++;
				}
			}

		}

		// for (j = 0; j < index - 1; j++) {
		// for (int i = 0; i < selectionlength; i++) {
		// System.out.println("j: " + j + "\t date_arr[" + j + "]: "
		// + date_arr[j] + " \t sampling_time[" + j + "]: "
		// + sampling_time[j] + "\t i: " + i + "\t mean[" + i
		// + "][" + j + "]: " + mean[i][j]);
		// }
		// }
		// System.out.println(" \t m:" + m + " \t no. of complete days: "
		// + complete_days.toArray().length);
		br.close();

		/* Write the output to the CSV file */
		WriteToCSV.csvWriter(filepath, date_arr, sampling_time, mean, index,
				selectionlength, timestep, selectedParameters, j);
	}

	/**
	 * Description: Time format has to be maintained for 0 to 9 minutes. So, a
	 * preceding 0 is added to them.
	 * 
	 * @param mins
	 * @param line_hr
	 * @return
	 */
	private static String formatTime(int mins, String line_hr) {
		String sampling_time;
		if (mins < 10) {
			sampling_time = line_hr + ":0" + mins;
		} else {
			sampling_time = line_hr + ":" + mins;
		}
		return sampling_time;
	}
}
