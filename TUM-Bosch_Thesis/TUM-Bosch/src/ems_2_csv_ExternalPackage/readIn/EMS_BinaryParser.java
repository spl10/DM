package ems_2_csv_ExternalPackage.readIn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class EMS_BinaryParser {

	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static EMS_Store parseFiles(String[] inputFileNames, File emspdFile)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub

		EMS_Store store = new EMS_Store();

		for (int a = 0; a < inputFileNames.length; a++) {

			String input_bin = inputFileNames[a];

			System.out
					.println("Status: " + (a + 1) + " / "
							+ inputFileNames.length + "  ---  Processing: "
							+ input_bin);

			File path_output = new File(
					"C:\\Users\\SIP2LOL\\Documents\\MyTool\\EMS_Converter\\SystemFiles\\out.csv");

			Process proc = Runtime
					.getRuntime()
					.exec("C:\\Users\\SIP2LOL\\Documents\\MyTool\\EMS_Converter\\SystemFiles\\EMS_Compressor.exe"
							+ " -i "
							+ input_bin
							+ " -p "
							+ "C:\\Users\\SIP2LOL\\Documents\\MyTool\\EMS_Converter\\SystemFiles\\auto_test.emspd"
							+ " -V "
							+ "C:\\Users\\SIP2LOL\\Documents\\MyTool\\EMS_Converter\\SystemFiles\\out.csv");

			// abwarten bis Prozess beendet!!
			int count = 0;
			while (true) {
				try {
					proc.exitValue();
					break;
				} catch (IllegalThreadStateException e) {
					Thread.sleep(70);
					count++;
					if (count > 1000) {
						store.addErrorLog(input_bin);
						System.out
								.println("Error bei File"
										+ input_bin
										+ " Datei konnte vom EMS Compressor nicht umgewandelt werden");
						return store;
					}
				}
			}

			BufferedReader in = new BufferedReader(new FileReader(
					path_output.getAbsolutePath()));

			String zeile = null;
			while ((zeile = in.readLine()) != null) {

				if (zeile.startsWith("Date")) {
					addHeader(zeile, store);
					continue;
				}
				addLine(zeile, store);
			}
			in.close();
			path_output.delete();
			java.lang.Runtime.getRuntime().gc();
		}

		return store;

	}

	private static void addHeader(String inputline, EMS_Store store) {
		String temp[] = inputline.split("\t");
		store.setHeader(temp);
	}

	private static void addLine(String inputline, EMS_Store store) {
		String temp[] = inputline.split("\t");

		// Häufigen Fehler abfangen
		if (temp[0].contains("01.01.2000")) {
			return;
		}
		store.addLine(
				temp[0].charAt(6) + "" + temp[0].charAt(7) + ""
						+ temp[0].charAt(8) + "" + temp[0].charAt(9) + "."
						+ temp[0].charAt(3) + "" + temp[0].charAt(4) + "."
						+ temp[0].charAt(0) + "" + temp[0].charAt(1) + " "
						+ temp[1], temp);
	}

}
