package data_preprocessing.userinput;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import predictive_model.AddResultsToDetection;

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
		System.err.println(java.lang.Runtime.getRuntime().maxMemory());
		final long startTime = System.nanoTime();
		int timestep = 0;
		String filepath = null;
		String[] dates = null;
		String file_act = null;
		String detection_filepath = null;
		String vis_path = null;
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
		File f = new File(filepath + "/output.csv");
		if (f.exists()) {
			f.delete();
		}
		String file_suffix = "_" + timestep + "ts_vis.txt";
		BufferedWriter output = new BufferedWriter(new FileWriter(filepath
				+ "/output.csv", true));
		output.write("Gateway,Algorithm,Date,Correctly_Classified_Instances,False_Positive,Precision,Recall,Predictive_Accuracy\n");
		output.flush();
		output.close();
		System.err.println("Gateways Location: " + filepath);
		System.err.println("Timestep: " + timestep);
		File location = new File(filepath);
		File[] gateways = location.listFiles();
		for (int i = gateways.length - 1; i >= 0; i--) {
			if (gateways[i].isDirectory()) {
				// List of dsm files.
				List<String> li_dsm = new ArrayList<String>();
				// List of main file from the EMS converter.
				List<String> li = new ArrayList<String>();
				if (detection_filepath == null
						|| !detection_filepath.contains(gateways[i].getName())) {
					detection_filepath = location + "/" + gateways[i].getName()
							+ "/out_gt" + gateways[i].getName().split("_")[1]
							+ "_Actual.csv";
					f = new File(detection_filepath);
					if (f.exists()) {
						f.delete();
					}
					f.createNewFile();
				}
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
								File[] output_folder = months[k].listFiles();
								for (int l = output_folder.length - 1; l >= 0; l--) {
									if (output_folder[l].isDirectory()
											&& output_folder[l].getName()
													.contains("output")) {
										if (file_act == null
												|| !file_act
														.contains(gateways[i]
																.getName())) {
											String file_s = "_" + timestep
													+ "ts_vis_Actual.csv";
											file_act = output_folder[l]
													+ "/out_gt"
													+ gateways[i].getName()
															.split("_")[1]
													+ "_"
													+ parseMonthInt(config_month)
													+ config_year + file_s;
										}
										File[] core_files = output_folder[l]
												.listFiles();
										if (core_files.length > 0) {
											li.add(core_files[0]
													.getAbsolutePath());
											for (int m = core_files.length - 1; m >= 0; m--) {
												if (core_files[m].getName()
														.contains(file_suffix)) {
													vis_path = core_files[m]
															.getAbsolutePath();
													li_dsm.add(vis_path);
													ff++;
												}
											}
										} else {
											String rest_folder = output_folder[l]
													.getPath()
													+ "/out_gt"
													+ gateways[i].getName()
															.split("_")[1]
													+ "_"
													+ parseMonthInt(months[k]
															.getName())
													+ years[j].getName()
													+ ".csv";
											li.add(rest_folder);
											f = new File(rest_folder);
											rest_folder = f.getPath().split(
													"\\.")[0]
													+ file_suffix;
											vis_path = rest_folder;
											li_dsm.add(vis_path);
										}
									}
								}
								if (ff == 0) {
									String rest_folder = months[k].getPath()
											+ "/output/";
									File dir = new File(rest_folder);
									if (!dir.exists()) {
										dir.mkdir();
										rest_folder = rest_folder
												+ "out_gt"
												+ gateways[i].getName().split(
														"_")[1]
												+ "_"
												+ parseMonthInt(months[k]
														.getName())
												+ years[j].getName() + ".csv";
										li.add(rest_folder);
										f = new File(rest_folder);
										rest_folder = f.getAbsolutePath()
												.split("\\.")[0] + file_suffix;
										vis_path = rest_folder;
										li_dsm.add(vis_path);
									}
								}
							}
						}
					}
				}
				System.out.println(li_dsm);
				// String dir = filepath.substring(0,
				// filepath.lastIndexOf("/"));
				// f = new File(filepath);
				// File Dir = new File(dir);
				// if (Dir.isDirectory()) {
				// File[] file_li = Dir.listFiles();
				// int l = 0;
				// while (l < file_li.length) {
				// if (!file_li[l].getName().equals(f.getName())) {
				// file_li[l].delete();
				// }
				// l++;
				// }
				// }
				ListIterator<String> it = li.listIterator();
				while (it.hasNext()) {
					filepath = it.next();
					f = new File(filepath);
					if (!f.exists()) {
						EMS_2_CSV.gui.EMSConverter ems = new EMS_2_CSV.gui.EMSConverter();
						ems.convertBinaryToCSV(filepath);
					}
					vis_path = filepath.split("\\.")[0] + file_suffix;
					f = new File(vis_path);
					if (!f.exists()) {
						f = new File(file_act);
						if (!f.exists()) {
							ProcessCSVOnUserInput.userInputProcessing(filepath,
									timestep);
						}
					}
				}
				dates = modifyDateArray(li_dsm, config_month, config_year);
				AddResultsToDetection.DetectionModel(dates, li_dsm,
						detection_filepath, timestep, file_act);
			}
		}
		final double duration = (System.nanoTime() - startTime) / 1000000000;
		System.out.println("Total Duration : " + duration / 60 + " min");
	}

	public static String[] modifyDateArray(List<String> li_dsm,
			String config_month, String config_year) throws Exception {
		List<String> dates = new ArrayList<String>();
		ListIterator<String> li = li_dsm.listIterator();
		while (li.hasNext()) {
			String dsm = li.next();
			BufferedReader br = new BufferedReader(new FileReader(dsm));
			String line = br.readLine();
			br.close();
			String[] content = line.split(",");
			for (int i = 2; i < content.length; i++) {
				if (!content[i].trim().equals("weekday")) {
					String dt = content[i].split("\\$")[1].split("_")[0];
					if (dt.split("\\.")[1].equals(config_month)
							&& dt.split("\\.")[2].equals(config_year)) {
						dates.add(dt);
					}
				}
			}
		}

		Set<String> complete_days = new LinkedHashSet<String>(dates);
		String[] date = new String[complete_days.size()];
		complete_days.toArray(date);
		return date;
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

}
