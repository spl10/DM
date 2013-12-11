package data_preprocessing.userinput;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

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
		final long startTime = System.nanoTime();
		int timestep = 0;
		EMS_2_CSV.gui.EMSConverter ems = new EMS_2_CSV.gui.EMSConverter();
		String filepath = null;
		BufferedReader config = new BufferedReader(new FileReader(
				"thesis.config"));
		String line_c = null;
		String config_month = null;
		String config_year = null;
		while ((line_c = config.readLine()) != null) {
			if (line_c.contains("Location:")) {
				filepath = line_c.split("n:")[1].trim();
			}
			if (line_c.contains("Timestep:")) {
				timestep = Integer.parseInt(line_c.split("p:")[1].trim());
			}
			if (line_c.contains("Prediction:")) {
				config_month = line_c.split("n:")[1].trim().split("\\.")[0];
				config_year = line_c.split("n:")[1].trim().split("\\.")[1];
			}
		}
		config.close();
		File f = new File(filepath + "\\output.csv");
		if (f.exists()) {
			f.delete();
		}
		BufferedWriter output = new BufferedWriter(new FileWriter(filepath
				+ "\\output.csv", true));
		output.write("Gateway,Algorithm,Date,Correctly_Classified_Instances,False_Positive\n");
		output.flush();
		output.close();
		System.err.println("Gateways Location: " + filepath);
		System.err.println("Timestep: " + timestep);
		File location = new File(filepath);
		File[] gateways = location.listFiles();
		for (int i = gateways.length - 1; i >= 0; i--) {
			if (gateways[i].isDirectory()) {
				File[] years = gateways[i].listFiles();
				gateway: for (int j = years.length - 1; j >= 0; j--) {
					if (years[j].isDirectory()) {
						File[] months = years[j].listFiles();
						for (int k = months.length - 1; k >= 0; k--) {
							if (months[k].isDirectory()
									&& (Integer.parseInt(months[k].getName()) <= Integer
											.parseInt(config_month) || (Integer
											.parseInt(years[j].getName()) <= Integer
											.parseInt(config_year)))) {
								filepath = months[k].getPath() + "\\output\\";
								File dir = new File(filepath);
								filepath = dir.listFiles()[0].getAbsolutePath();
								break gateway;
							}
						}
					}
				}
				f = new File(filepath);
				if (!f.exists()) {
					f = new File(filepath.substring(0,
							filepath.lastIndexOf("\\")));
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
				String file = filepath
						.substring(filepath.lastIndexOf("\\") + 1);
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
				ProcessCSVOnUserInput.userInputProcessing(filepath, timestep);
			}
		}
		final double duration = (System.nanoTime() - startTime) / 1000000000;
		System.out.println("Total Duration : " + duration / 60 + " min");
	}
}
