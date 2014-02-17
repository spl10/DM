package dataPreprocessing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import model.Detection;
import model.Number_of_starts;

/**
 * Description: This module is a part of time-series conversion.
 * 
 */
public class TimeStepConversion {
	/**
	 * Description: Initial preparation for time-series conversion.
	 * 
	 * @param filepath
	 * @param timestep
	 * @param l
	 * @param selectedParameters
	 * @param selectedParameters
	 * @throws Exception
	 */
	public static void convertForTimeStep(String filepath, int timestep, int l,
			int[] selection, String[] selectedParameters) throws Exception {

		Detection detection = new Detection();
		Number_of_starts ns = new Number_of_starts();
		detection.setFilepath(filepath);
		detection.setTimestep(timestep);
		detection.setSelectedParameters(selectedParameters);
		int i = 0, j = 0, position = 0, threshold_nos = 0;
		// System.out.println("No. of Parameters Selected by the user: "
		// + selection.length);
		int[] temp = new int[selection.length];
		for (i = 0; i < selection.length; i++) {
			temp[i] = selection[i] + 1;
		}
		String[] type = new String[selection.length];
		BufferedReader br = new BufferedReader(new FileReader(filepath));
		String line = br.readLine();

		// System.out.println("temp \t type \t selectedParameters");
		type = ParameterTypeDefinition.definingParameterType(selection.length,
				temp, filepath, selectedParameters);
		detection.setType(type);
		/*
		 * Retrieving the column names in the csv file that matches the
		 * parameters selected by the user.
		 */
		for (i = 0; i < temp.length; i++) {
			// System.out.println(temp[i] + " \t " + type[i] + " \t "
			// + selectedParameters[i]);
			if (selectedParameters[i].toLowerCase()
					.contains("number_of_starts")) {
				position = selection[i] + 1;
				ns.setPosition(position);
			}
		}

		detection.setTemp(temp);
		String[][] date_time = new String[2][l];
		HashMap<String, String> dt = new HashMap<String, String>();

		while ((line = br.readLine()) != null) {
			date_time[0][j] = line.split(",")[0];
			date_time[1][j] = line.split(",")[1];
			dt.put(date_time[0][j], date_time[1][j]);
			if (j == 1
					&& line.split(",")[position].toLowerCase().contains(
							"number_of_starts")) {
				threshold_nos = Integer.parseInt(line.split(",")[position]);
				ns.setThreshold(threshold_nos);
			}
			j++;
		}
		br.close();

		ArrayList<String> date_list = new ArrayList<String>();

		// j is the length of the file
		for (i = 0; i < j; i++) {
			date_list.add(date_time[0][i]);
		}

		/* Processing for DATA CONTINUITY. */
		Set<String> complete_days = CompleteDaysIdentification
				.identifyingCompleteDays(date_list, j, date_time);
		detection.setComplete_days(complete_days);
		System.out.println("Complete_days without missing data: "
				+ complete_days);
		System.out.println("No. of complete Days: " + complete_days.size());
		detection.setFilepath(CompleteDaysIdentification.dayCompletion(
				complete_days, filepath));
		/*
		 * * Every line of the csv file contains values separated by comma. So
		 * we retrieve the values by splitting the line with comma.The
		 * corresponding parameters are retrieved based on the position in the
		 * csv file.
		 */
		MeanModeCalculation.meanModePreprocessing(detection, ns, l);
	}
}
