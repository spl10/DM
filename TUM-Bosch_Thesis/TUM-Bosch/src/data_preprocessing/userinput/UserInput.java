package data_preprocessing.userinput;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * @author SIP2LOL
 * 
 */
public class UserInput {
	public int created_files_count = 0;

	public static void main(String[] args) throws Exception {
		int timestep = 0;

		System.out.print("Enter the CSV File Path: ");
		BufferedReader input = new BufferedReader(new InputStreamReader(
				System.in));
		String filepath = input.readLine().replace("\"", "");

		String dir = filepath.substring(0, filepath.lastIndexOf("\\"));
		String file = filepath.substring(filepath.lastIndexOf("\\") + 1);
		File Dir = new File(dir);
		if (Dir.isDirectory()) {
			File[] li = Dir.listFiles();
			int i = 0;
			while (i < li.length) {
				if (!li[i].getName().equals(file)) {
					li[i].delete();
				}
				i++;
			}
		}

		System.out.println();
		System.out.print("Enter the sampling timestep: ");
		input = new BufferedReader(new InputStreamReader(System.in));
		timestep = Integer.parseInt(input.readLine());
		ProcessCSVOnUserInput.userInputProcessing(filepath, timestep);
	}
}
