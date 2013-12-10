package data_preprocessing.userinput;

import java.io.BufferedReader;
import java.io.FileReader;

import data_preprocessing.timestep.TimeStepConversion;

public class ProcessCSVOnUserInput {
	/**
	 * @param csv
	 * @param filepath
	 * @param timestep
	 * @throws Exception
	 */
	public static void userInputProcessing(String filepath, int timestep)
			throws Exception {
		int l = 1;
		BufferedReader csv = new BufferedReader(new FileReader(filepath));
		String Line = csv.readLine();
		String[] Parameters = new String[Line.split(",").length];
		System.out.println();
		for (int i = 0; i < Line.split(",").length; i++) {
			Parameters[i] = Line.split(",")[i];
			if (i > 1) {
				System.out.println(i - 1 + ". " + Parameters[i]);
			}
		}
		System.out.println();
		System.out.print("Select the options seperated by comma ex.(1,2,3): ");

		// BufferedReader input = new BufferedReader(new InputStreamReader(
		// System.in));
		System.out.println();
		// String tmp = input.readLine();
		int sel_l = 0;
		String[] selectedParameters = new String[1];
		int[] selection = new int[sel_l];
		System.out.println();
		// if (tmp.contains(",")) {
		// sel_l = tmp.split(",").length;
		// selection = new int[sel_l];
		// selectedParameters = new String[sel_l];
		// System.out.println("The selected options are ");
		// for (int i = 0; i < sel_l; i++) {
		// selection[i] = Integer.parseInt(tmp.split(",")[i]);
		// if (selection[i] < (Parameters.length - 1)) {
		// selectedParameters[i] = Parameters[selection[i] + 1];
		// System.out.println(selectedParameters[i]);
		// } else {
		// System.out
		// .println("Invalid Option! Try Again with valid options.");
		// System.exit(0);
		// }
		// }
		// } else {
		// if (!tmp.isEmpty()) {
		// selectedParameters[0] = Parameters[Integer.parseInt(tmp) + 1];
		// System.out.println("The selected option is "
		// + selectedParameters[0]);
		//
		// } else {
		String tmp = "1,2,3,11,12";
		sel_l = tmp.split(",").length;
		selection = new int[sel_l];
		selectedParameters = new String[sel_l];
		System.out.println("The selected options are ");
		for (int i = 0; i < sel_l; i++) {
			selection[i] = Integer.parseInt(tmp.split(",")[i]);
			if (selection[i] < (Parameters.length - 1)) {
				selectedParameters[i] = Parameters[selection[i] + 1];
				System.out.println(selectedParameters[i]);
			} else {
				System.out
						.println("Invalid Option! Try Again with valid options.");
				System.exit(0);
			}
		}
		// }
		// }
		while (csv.readLine() != null) {
			l++;
		}
		csv.close();
		System.out.println("Length of the CSV file: " + l);

		final long startTime = System.nanoTime();

		/* Convert for time-step */
		TimeStepConversion.convertForTimeStep(filepath, timestep, l, selection,
				selectedParameters);

		final double duration = System.nanoTime() - startTime;
		System.out
				.println("Total Duration : " + duration / 1000000000 + " sec");
	}
}
