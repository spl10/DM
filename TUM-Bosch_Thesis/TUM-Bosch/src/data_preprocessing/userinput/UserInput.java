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

	public int getCreated_files_count() {
		return created_files_count;
	}

	public void setCreated_files_count(int created_files_count) {
		this.created_files_count = created_files_count;
	}

	public static void main(String[] args) throws Exception {
		int timestep = 0;
		EMS_2_CSV.gui.EMSConverter ems = new EMS_2_CSV.gui.EMSConverter();
		System.out.print("Enter the Gateways Folder Location: ");
		BufferedReader input = new BufferedReader(new InputStreamReader(
				System.in));
		String filepath = input.readLine().replace("\"", "");
		File location = new File(filepath);
		File[] gateways = location.listFiles();
		for (int i = gateways.length - 1; i >= 0; i--) {
			File[] years = gateways[i].listFiles();
			gateway: for (int j = years.length - 1; j >= 0; j--) {
				if (years[j].isDirectory()) {
					File[] months = years[j].listFiles();
					for (int k = months.length - 1; k >= 0; k--) {
						if (months[k].isDirectory()) {
							filepath = months[k].getPath() + "\\output\\";
							File dir = new File(filepath);
							filepath = dir.listFiles()[0].getAbsolutePath();
							break gateway;
						}
					}
				}
			}

			File f = new File(filepath);
			if (!f.exists()) {
				f = new File(filepath.substring(0, filepath.lastIndexOf("\\")));
				if (!f.exists()) {
					f.mkdir();
				}
				ems.convertBinaryToCSV(filepath);
				f = new File(filepath);
				while (!f.exists()) {
					Thread.sleep(100);
				}
			}
			String dir = filepath.substring(0, filepath.lastIndexOf("\\"));
			String file = filepath.substring(filepath.lastIndexOf("\\") + 1);
			File Dir = new File(dir);
			if (Dir.isDirectory()) {
				File[] li = Dir.listFiles();
				int l = 0;
				while (l < li.length) {
					if (!li[l].getName().equals(file)) {
						li[l].delete();
					}
					l++;
				}
			}

			System.out.println();
			System.out.print("Enter the sampling timestep: ");
			input = new BufferedReader(new InputStreamReader(System.in));
			timestep = Integer.parseInt(input.readLine());
			ProcessCSVOnUserInput.userInputProcessing(filepath, timestep);
		}
	}
}
