package ems_2_csv_ExternalPackage.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ems_2_csv_ExternalPackage.preprocess.EMS_RegularTimeSeries;
import ems_2_csv_ExternalPackage.readIn.EMS_BinaryParser;
import ems_2_csv_ExternalPackage.readIn.EMS_Store;

public class EMSConverter {
	public void convertBinaryToCSV(String filepath) throws IOException,
			InterruptedException {
		// public static void main(String[] args) throws IOException,
		// InterruptedException {
		// String filepath =
		// "C:\\Users\\SIP2LOL\\Documents\\MyTool\\Data\\gateway_207010369\\2013\\09\\output\\out_gt207010369_sep13.csv";
		// "C:\\Users\\SIP2LOL\\Documents\\MyTool\\EMS_Converter\\SystemFiles\\auto_test.emspd"
		final String fp = filepath;
		File f = new File(fp);
		String path = f.getPath().split("output")[0];
		List<String> bin_li = getBinaryFiles(path);
		File emspdFile = new File(
				"C:\\Users\\SIP2LOL\\Documents\\MyTool\\EMS_Converter\\SystemFiles\\auto_test.emspd");
		String[] inFiles = new String[bin_li.size()];
		bin_li.toArray(inFiles);
		EMS_Store store = null;
		try {
			store = EMS_BinaryParser.parseFiles(inFiles, emspdFile);
			EMS_Store timeseriesStore = EMS_RegularTimeSeries.getTimeSeries(
					store, false);
			timeseriesStore.write(fp, ",", true);
			store = timeseriesStore;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<String> getBinaryFiles(String path) {
		File Dir = new File(path);
		File[] bin = Dir.listFiles();
		List<String> bin_li = new ArrayList<String>();
		for (int i = 0; i < bin.length - 2; i++) {
			if (bin[i].isFile()) {
				String first = bin[i].getName().split("\\.")[1];
				if (first.equals("bin")) {
					bin_li.add(bin[i].getAbsolutePath());
				}
			}
		}
		return bin_li;
	}
}
