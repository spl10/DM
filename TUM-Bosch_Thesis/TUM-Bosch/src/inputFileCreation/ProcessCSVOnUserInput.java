package inputFileCreation;

import java.io.BufferedReader;
import java.io.FileReader;

import dataPreprocessing.TimeStepConversion;

/**
 * Description: This class is a part of input file creation module.
 * 
 */
public class ProcessCSVOnUserInput {
	/**
	 * Description: Listing out the parameters out of dimension reduction.
	 * 
	 * @param csv
	 * @param filepath
	 * @param timestep
	 * @throws Exception
	 */
	public static void userInputProcessing(String filepath, int timestep)
			throws Exception {
		int l = 1;
		BufferedReader csv = new BufferedReader(new FileReader(filepath));
		String[] Parameters = csv.readLine().split(",");
		int sel_l = 0;
		String[] selectedParameters = new String[1];
		int[] selection = new int[sel_l];
		String tmp = null;
		BufferedReader config = new BufferedReader(new FileReader(
				"thesis.config"));
		String line_c = null;
		while ((line_c = config.readLine()) != null) {
			if (line_c.contains("Parameters:")) {
				tmp = line_c.split("s:")[1].trim();
			}
		}
		config.close();
		// System.err.println("Selected options are: " + tmp);
		sel_l = tmp.split(",").length;
		selection = new int[sel_l];
		selectedParameters = new String[sel_l];
		for (int i = 0; i < sel_l; i++) {
			selection[i] = Integer.parseInt(tmp.split(",")[i]);
			if (selection[i] < (Parameters.length - 1)) {
				selectedParameters[i] = Parameters[selection[i] + 1];
				// System.out.println(selectedParameters[i]);
			} else {
				// System.out
				// .println("Invalid Option! Try Again with valid options.");
				System.exit(0);
			}
		}

		while (csv.readLine() != null) {
			l++;
		}
		csv.close();

		/* Conversion for input timestep */
		TimeStepConversion.convertForTimeStep(filepath, timestep, l, selection,
				selectedParameters);

	}
}
