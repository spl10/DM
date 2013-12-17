package predictive_model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import model_class.CalculateFilePath;
import model_class.ListFiles;
import data_preprocessing.userinput.ProcessCSVOnUserInput;
import data_preprocessing.userinput.UserInput;

/**
 * @author SIP2LOL
 * 
 */
public class User_Input_For_Prediction extends UserInput {
	public static void userInput(String[] date, String filepath, int timestep,
			String file_act) throws Exception {
		File f = new File(filepath);
		String filename = f.getName();
		String location = null;
		String detection_filepath = null;

		BufferedReader config = new BufferedReader(new FileReader(
				"thesis.config"));
		String line_c = null;
		String config_month = null;
		String config_year = null;
		while ((line_c = config.readLine()) != null) {
			if (line_c.contains("Location:")) {
				location = line_c.split("n:")[1].trim();
			}
			if (line_c.contains("Prediction:")) {
				config_month = line_c.split("n:")[1].trim().split("\\.")[0];
				config_year = line_c.split("n:")[1].trim().split("\\.")[1];
			}
		}
		config.close();
		String file_suffix = filename.split(date[0].split("\\.")[2])[1];
		f = new File(location);
		File[] gateways = f.listFiles();
		for (int i = gateways.length - 1; i >= 0; i--) {
			if (gateways[i].isDirectory()) {
				// List of dsm files.
				List<String> li_dsm = new ArrayList<String>();
				// List of main file from the EMS converter.
				List<String> li = new ArrayList<String>();
				ListFiles lf = new ListFiles();

				detection_filepath = location + "\\" + gateways[i].getName()
						+ "\\out_gt" + gateways[i].getName().split("_")[1]
						+ "_Actual.csv";
				f = new File(detection_filepath);
				if (f.exists()) {
					f.delete();
				}
				f.createNewFile();
				File[] years = gateways[i].listFiles();
				for (int j = years.length - 1; j >= 0; j--) {
					if (years[j].isDirectory()) {
						File[] months = years[j].listFiles();
						for (int k = months.length - 1; k >= 0; k--) {
							int ff = 0;
							if (months[k].isDirectory()
									&& ((Integer.parseInt(months[k].getName()) <= Integer
											.parseInt(config_month) && (Integer
											.parseInt(years[j].getName()) == Integer
											.parseInt(config_year))) || ((Integer
											.parseInt(years[j].getName()) < Integer
											.parseInt(config_year))))) {
								File[] output = months[k].listFiles();
								for (int l = output.length - 1; l >= 0; l--) {
									if (output[l].isDirectory()
											&& output[l].getName().contains(
													"output")) {
										File[] core_files = output[l]
												.listFiles();
										if (core_files.length > 0) {
											li.add(core_files[0]
													.getAbsolutePath());
											for (int m = core_files.length - 1; m >= 0; m--) {
												if (core_files[m].getName()
														.contains(file_suffix)) {
													li_dsm.add(core_files[m]
															.getAbsolutePath());
													ff++;
												}
											}
										} else {
											String rest_folder = output[l]
													.getPath()
													+ "\\out_gt"
													+ gateways[i].getName()
															.split("_")[1]
													+ "_"
													+ User_Input_For_Prediction
															.parseMonthInt(months[k]
																	.getName())
													+ years[j].getName()
													+ ".csv";
											li.add(rest_folder);
											f = new File(rest_folder);
											rest_folder = f.getPath().split(
													"\\.")[0]
													+ file_suffix;
											li_dsm.add(rest_folder);

										}
									}
								}
								if (ff == 0) {
									String rest_folder = months[k].getPath()
											+ "\\output\\";
									File dir = new File(rest_folder);
									if (!dir.exists()) {
										dir.mkdir();
										rest_folder = rest_folder
												+ "out_gt"
												+ gateways[i].getName().split(
														"_")[1]
												+ "_"
												+ User_Input_For_Prediction
														.parseMonthInt(months[k]
																.getName())
												+ years[j].getName() + ".csv";
										li.add(rest_folder);
										f = new File(rest_folder);
										rest_folder = f.getAbsolutePath()
												.split("\\.")[0] + file_suffix;
										li_dsm.add(rest_folder);
									}

								}
							}
						}
					}
				}

				lf.setDsm_li(li_dsm);
				lf.setMain_li(li);
				ListIterator<String> it = li_dsm.listIterator();
				while (it.hasNext()) {
					System.out.println("li_dsm: " + it.next());
				}
				// System.out.println();
				// it = li.listIterator();
				// while (it.hasNext()) {
				// System.out.println("main file: " + it.next());
				// }
				CreateDsmFiles(lf, timestep);
				AddResultsToDetection.DetectionModel(date, li_dsm,
						detection_filepath, timestep, file_act);
			}
		}
	}

	public static void userInput1(String[] date, String filepath, int timestep,
			String file_act) throws Exception {

		// To control the while loop when invalid path is found.
		boolean ff = true;
		UserInput ui = new UserInput();
		// List of dsm files.
		List<String> li_dsm = new ArrayList<String>();
		List<String> first_li_dsm = new ArrayList<String>();
		// List of main file from the EMS converter.
		List<String> li = new ArrayList<String>();
		CalculateFilePath cfp = null;
		String config_month = null;
		String config_year = null;
		BufferedReader config = new BufferedReader(new FileReader(
				"thesis.config"));
		String line_c = null;
		while ((line_c = config.readLine()) != null) {
			if (line_c.contains("Prediction:")) {
				config_month = line_c.split("n:")[1].trim().split("\\.")[0];
				config_year = line_c.split("n:")[1].trim().split("\\.")[1];
			}
		}
		config.close();
		// Gets the path of the current file which is processed now.
		File f = new File(filepath);
		// To get the date, month and year in an array.
		String[] dt = new String[3];
		String fyear = null;
		String dsm_suffix = null;
		String detection_filepath = null;
		if (date.length != 0 && !date[0].equals("")) {
			dt = date[date.length - 1].split("\\.");
			fyear = dt[2];
			// Gets the unique name for dsm files.
			dsm_suffix = f.getName().split(parseMonthInt(dt[1]))[1];
			detection_filepath = f.getAbsolutePath().split(fyear + "\\\\")[0]
					+ f.getName().split(parseMonthInt(dt[1]))[0] + "Actual.csv";
		}
		if (detection_filepath != null) {
			f = new File(detection_filepath);
			if (f.exists()) {
				f.delete();
			}
		}
		System.out.println(dsm_suffix);
		/*
		 * Loops until valid file is not found! Looks into all the main folders
		 * in the gateway.
		 */
		String dsm_path = filepath;
		while (ff) {
			f = new File(filepath);
			String fmonth = f.getName().split("_")[2].substring(0, 3);
			fyear = dt[2];
			String[] filename = f.getName().split(fmonth);
			fmonth = parseMonthStr(fmonth);
			String temp_path = f.getPath().split(fmonth + "\\\\")[0];

			// Gets the inner folders inside the month folder in the gateway.
			String rest_folders = f.getPath().split(fmonth + "\\\\")[1]
					.split("\\\\")[0] + "\\";

			// Gets the value of path in an array split on slash /.
			String[] path = f.getPath().split("\\\\");
			System.out.println("START month: " + fmonth + " mnt folder: "
					+ path[path.length - 2] + " rest_folders: " + rest_folders
					+ " filename[0]: " + filename[0] + " fmonth: "
					+ parseMonthInt(fmonth) + " fyear: " + fyear
					+ " filename[1].split(_)[0].split(.)[0] : "
					+ filename[1].split("_")[0].split("\\.")[0]
					+ "\ntemp_path: " + temp_path + " \ndsm_path: " + dsm_path
					+ "\nconfig_year: " + config_year + " config_month: "
					+ config_month);
			if (date.length != 0
					&& !date[0].equals("")
					&& ((Integer.parseInt(fmonth) <= Integer
							.parseInt(config_month)) || (Integer
							.parseInt(filename[1].split("_")[0].split("\\.")[0]) < Integer
							.parseInt(config_year)))) {
				li_dsm.add(dsm_path);
			}
			int i = 2; // omitting the file name in the filepath.

			/*
			 * Looks for the month folder and year folder in the current
			 * filepath.
			 */
			while (i < path.length) {
				/*
				 * Contains the dsm_path and main_file path.
				 */
				cfp = new CalculateFilePath();
				cfp.setDsm_path(dsm_path);
				cfp.setPath(temp_path);
				/*
				 * Checks for the month. If the month is 0, then looks for last
				 * year folder for the folder named 12.
				 */
				cfp = nameCalculation(path, i, fmonth, cfp, rest_folders,
						dsm_suffix, filename, fyear);

				temp_path = cfp.getPath();
				dsm_path = cfp.getDsm_path();

				i++;
			}
			File dsm_f = new File(dsm_path);
			if (!dsm_f.exists()) {
				f = new File(temp_path);
				if (!f.isFile()) {
					int mnt = 0;
					if (Integer.parseInt(fmonth) > 1) {
						mnt = Integer.parseInt(fmonth) - 1;
					} else {
						mnt = 12;
					}
					String month = null;
					if (mnt < 10) {
						month = "0" + String.valueOf(mnt);
					} else {
						month = String.valueOf(mnt);
					}
					String month_folder = temp_path.split(month + "\\\\")[0]
							+ month + "\\";
					File dir = new File(month_folder);
					if (!dir.exists()) {

						ff = false;
					}
				}
			}
			if (date.length != 0
					&& !date[0].equals("")
					&& (((Integer.parseInt(fmonth)) <= Integer
							.parseInt(config_month)) || (Integer
							.parseInt(filename[1].split("_")[0].split("\\.")[0]) < Integer
							.parseInt(config_year)))) {
				li.add(filepath);
			}
			filepath = temp_path;
		}

		ListIterator<String> it = li_dsm.listIterator();
		while (it.hasNext()) {
			System.out.println("li_dsm: " + it.next());
		}
		System.out.println();
		it = li.listIterator();
		while (it.hasNext()) {
			System.out.println("main file: " + it.next());
		}
		/*
		 * returns list of all dsm files and main files to visualization.
		 */

		ListFiles lf = new ListFiles();
		lf.setDsm_li(li_dsm);
		lf.setMain_li(li);
		if (ui.getCreated_files_count() == 0)
			first_li_dsm.addAll(li_dsm);
		it = li.listIterator();
		while (it.hasNext()) {
			System.out.println("first_li_dsm: " + it.next());
		}
		// ui.setCreated_files_count(CreateDsmFiles(lf, timestep,
		// ui.getCreated_files_count()));
		System.out.println("created_files_count: "
				+ ui.getCreated_files_count());

		AddResultsToDetection.DetectionModel(date, first_li_dsm,
				detection_filepath, timestep, file_act);

	}

	/**
	 * Converts Month from String to Integer.
	 * 
	 * @param mt
	 * @return mnt
	 */
	public static String parseMonthStr(String mt) {
		String mnt = "01";
		if (mt.equals("jan")) {
			mnt = "01";
		} else if (mt.equals("feb")) {
			mnt = "02";
		} else if (mt.equals("mar")) {
			mnt = "03";
		} else if (mt.equals("apr")) {
			mnt = "04";
		} else if (mt.equals("may")) {
			mnt = "05";
		} else if (mt.equals("jun")) {
			mnt = "06";
		} else if (mt.equals("jul")) {
			mnt = "07";
		} else if (mt.equals("aug")) {
			mnt = "08";
		} else if (mt.equals("sep")) {
			mnt = "09";
		} else if (mt.equals("oct")) {
			mnt = "10";
		} else if (mt.equals("nov")) {
			mnt = "11";
		} else if (mt.equals("dec")) {
			mnt = "12";
		}
		return mnt;
	}

	/**
	 * Converts month from Integer to String.
	 * 
	 * @param mnt
	 * @return mt
	 */
	public static String parseMonthInt(String mnt) {
		String mt = "jan";
		if (mnt.equals("01")) {
			mt = "jan";
		} else if (mnt.equals("02")) {
			mt = "feb";
		} else if (mnt.equals("03")) {
			mt = "mar";
		} else if (mnt.equals("04")) {
			mt = "apr";
		} else if (mnt.equals("05")) {
			mt = "may";
		} else if (mnt.equals("06")) {
			mt = "jun";
		} else if (mnt.equals("07")) {
			mt = "jul";
		} else if (mnt.equals("08")) {
			mt = "aug";
		} else if (mnt.equals("09")) {
			mt = "sep";
		} else if (mnt.equals("10")) {
			mt = "oct";
		} else if (mnt.equals("11")) {
			mt = "nov";
		} else if (mnt.equals("12")) {
			mt = "dec";
		}
		return mt;
	}

	/**
	 * Gets the File_Path of main csv file out of ESM converter and dsm_file out
	 * of label calculation of all the available folders. The example path is,
	 * temp_file = "/../gt206010347/2013/09/../out_gt206010347_sep13.csv"
	 * Dsm_file =
	 * "/../gt206010347/2013/09/../out_gt206010347_sep13_15ts_vis_15dsm.txt"
	 * 
	 * @param path
	 * @param i
	 * @param fmonth
	 * @param cfp
	 * @param rest_folders
	 * @param dsm_suffix
	 * @param filename
	 * @param fyear
	 * @return CalculateFilePath
	 */
	public static CalculateFilePath nameCalculation(String[] path, int i,
			String fmonth, CalculateFilePath cfp, String rest_folders,
			String dsm_suffix, String[] filename, String fyear) {
		String temp_path = cfp.getPath();
		String dsm_path = cfp.getDsm_path();
		if (path[path.length - i].equals(fmonth)) {
			if (Integer.parseInt(fmonth) > 1) {
				if (Integer.parseInt(fmonth) <= 10) {
					fmonth = "0" + (Integer.parseInt(fmonth) - 1);
				} else {
					fmonth = String.valueOf(Integer.parseInt(fmonth) - 1);
				}
				temp_path = temp_path + fmonth + "\\" + rest_folders;
				dsm_path = temp_path + filename[0] + parseMonthInt(fmonth)
						+ dsm_suffix;
				temp_path = temp_path + filename[0] + parseMonthInt(fmonth)
						+ filename[1].split("_")[0].split("\\.")[0] + ".csv";

			} else {
				System.out.println("fyear: " + fyear);
				temp_path = temp_path.replace(fyear,
						String.valueOf(Integer.parseInt(fyear) - 1));
				fmonth = "12";
				temp_path = temp_path + fmonth + "\\" + rest_folders;
				dsm_path = temp_path
						+ filename[0]
						+ parseMonthInt(fmonth)
						+ (Integer.parseInt(filename[1].split("_")[0]
								.split("\\.")[0]) - 1)
						+ dsm_suffix.substring(4);
				temp_path = temp_path
						+ filename[0]
						+ parseMonthInt(fmonth)
						+ (Integer.parseInt(filename[1].split("_")[0]
								.split("\\.")[0]) - 1) + ".csv";
			}

		}
		cfp.setDsm_path(dsm_path);
		cfp.setPath(temp_path);
		return cfp;
	}

	/**
	 * Creates the file with actual data, if not available previously.
	 * 
	 * @param lf
	 * @param timestep
	 * @param created_files_count
	 * @return
	 * @throws Exception
	 */
	public static void CreateDsmFiles(ListFiles lf, int timestep)
			throws Exception {
		ListIterator<String> it = lf.getDsm_li().listIterator();
		int index = 0;
		while (it.hasNext()) {
			File dsm_file = new File(it.next());
			System.out.println("li_dsm: " + dsm_file);
			if (!dsm_file.exists()) {
				index = it.nextIndex() - 1;
				String filepath = lf.getMain_li().get(index);
				final File main_file = new File(filepath);
				if (!main_file.exists()) {
					File f = new File(filepath.substring(0,
							filepath.lastIndexOf("\\")));
					if (!f.exists()) {
						f.mkdir();
					}
					System.err
							.println(main_file.getName()
									+ " does not exist!!! \n Creating the file using EMS Converter...");
					EMS_2_CSV.gui.EMSConverter ems = new EMS_2_CSV.gui.EMSConverter();
					ems.convertBinaryToCSV(main_file.getAbsolutePath());
					System.err.println("Processing File: "
							+ main_file.getAbsolutePath());
					ProcessCSVOnUserInput.userInputProcessing(
							main_file.getAbsolutePath(), timestep);
				} else {
					System.err.println("Processing File: "
							+ main_file.getAbsolutePath());
					ProcessCSVOnUserInput.userInputProcessing(
							main_file.getAbsolutePath(), timestep);
				}
			}
		}
	}

}
