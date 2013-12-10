package predictive_model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import model_class.CalculateFilePath;
import model_class.ListFiles;
import model_class.WEKAInputFiles;
import data_preprocessing.userinput.ProcessCSVOnUserInput;
import data_preprocessing.userinput.UserInput;

/**
 * @author SIP2LOL
 * 
 */
public class User_Input_For_Prediction extends UserInput {

	/**
	 * Information is processed for prediction based on user input.
	 * 
	 * @return
	 * @throws IOException
	 */
	public static String options() throws IOException {
		System.out.println("Learning Strategies: ");
		System.out.println("1.Learn from current month. \n"
				+ "2.Learn from past two months. \n"
				+ "3.Learn from past six months. \n"
				+ "4.Learn from the whole year.");
		System.out.print("Select any one option (Default Option is 4): ");
		// BufferedReader input = new BufferedReader(new InputStreamReader(
		// System.in));
		// String inp = input.readLine();
		String inp = "4";
		return inp;
	}

	public static WEKAInputFiles userInput(String[] date, String filepath,
			int timestep) throws Exception {
		// Prints out the option.
		String inp = options();
		// To control the while loop when invalid path is found.
		boolean ff = true;
		UserInput ui = new UserInput();
		// List of dsm files.
		List<String> li_dsm = new ArrayList<String>();
		List<String> first_li_dsm = new ArrayList<String>();
		// List of main file from the EMS converter.
		List<String> li = new ArrayList<String>();

		if (inp.isEmpty()) { // set default input
			inp = "4";
		}
		// To get the date, month and year in an array.
		String[] dt = date[date.length - 1].split("\\.");

		if (Integer.parseInt(dt[1]) < 3 && inp.equals("2")) {
			System.err
					.println("Past two months not available in the year.Please Select Other Options.");
			inp = options();
		} else if (Integer.parseInt(dt[1]) < 6 && inp.equals("3")) {
			System.err
					.println("Past six months not available in the year.Please Select Other Options.");
			inp = options();
		} else if (!(inp.equals("1") || inp.equals("2") || inp.equals("3") || inp
				.equals("4"))) {
			System.err
					.println("Please Select only one valid option from the list ex. 2.");
			inp = options();
		}

		// Restrict the list size for option 1, 2 and 3
		int size = 0;

		if (inp.equals("1")) {
			size = 1;
		} else if (inp.equals("2")) {
			size = 2;
		} else if (inp.equals("3")) {
			size = 6;
		}

		// Gets the path of the current file which is processed now.
		File f = new File(filepath);

		// Gets the unique name for dsm files.
		String dsm_suffix = f.getName().split(parseMonthInt(dt[1]))[1];
		String fyear = dt[2];
		String detection_filepath = f.getAbsolutePath().split(fyear + "\\\\")[0]
				+ f.getName().split(parseMonthInt(dt[1]))[0] + "Actual.csv";
		f = new File(detection_filepath);
		if (f.exists()) {
			f.delete();
		}
		System.out.println(dsm_suffix);
		/*
		 * Loops until valid file is not found! Looks into all the main folders
		 * in the gateway.
		 */
		String dsm_path = filepath;
		loop: while (ff) {
			li_dsm.add(dsm_path);
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

			int i = 2; // omitting the file name in the filepath.

			/*
			 * Looks for the month folder and year folder in the current
			 * filepath.
			 */

			while (i < path.length) {
				/*
				 * Contains the dsm_path and main_file path.
				 */
				CalculateFilePath cfp = new CalculateFilePath();
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

			System.out.println("inp: " + inp + " month: " + fmonth
					+ " mnt folder: " + path[path.length - 2]
					+ " rest_folders: " + rest_folders + " filename[0]: "
					+ filename[0] + " fmonth: " + parseMonthInt(fmonth)
					+ " filename[1]: " + filename[1]
					+ "filename[1].split(_)[0].split(.)[0] : "
					+ filename[1].split("_")[0].split("\\.")[0]
					+ "\ntemp_path: " + temp_path + " \ndsm_path: " + dsm_path);

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
			li.add(filepath);
			filepath = temp_path;

			if ((inp.equals("1") || inp.equals("2") || inp.equals("3"))
					&& li.size() > size) {
				break loop;
			}
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
		ui.setCreated_files_count(CreateDsmFiles(lf, timestep,
				ui.getCreated_files_count()));
		System.out.println("created_files_count: "
				+ ui.getCreated_files_count());

		WEKAInputFiles wif = AddResultsToDetection.DetectionModel(date,
				first_li_dsm, detection_filepath);
		wif.setDetection_filepath(detection_filepath);
		System.out.println(detection_filepath);
		return wif;
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
						+ dsm_suffix.substring(2);
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
	public static int CreateDsmFiles(ListFiles lf, int timestep,
			int created_files_count) throws Exception {
		ListIterator<String> it = lf.getDsm_li().listIterator();
		int index = 0;
		while (it.hasNext()) {
			File dsm_file = new File(it.next());
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
					final EMS_2_CSV.gui.EMSConverter ems = new EMS_2_CSV.gui.EMSConverter();

					ems.convertBinaryToCSV(main_file.getAbsolutePath());
					while (!main_file.exists()) {
						Thread.sleep(100);
					}
					created_files_count++;
					System.err.println("Processing File: "
							+ main_file.getAbsolutePath());
					ProcessCSVOnUserInput.userInputProcessing(
							main_file.getAbsolutePath(), timestep);
				} else {
					created_files_count++;
					System.err.println("Processing File: "
							+ main_file.getAbsolutePath());
					ProcessCSVOnUserInput.userInputProcessing(
							main_file.getAbsolutePath(), timestep);
				}
			}
		}
		return created_files_count;
	}

}
