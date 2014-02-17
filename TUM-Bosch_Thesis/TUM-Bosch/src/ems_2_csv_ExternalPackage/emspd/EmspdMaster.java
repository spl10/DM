package ems_2_csv_ExternalPackage.emspd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class EmspdMaster {

	/**
	 * @param args
	 */

	public static void createEMSPD(File f, EMSVariable[] vars) {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(f));

			for (int i = 0; i < vars.length; i++) {
				writer.write(vars[i].getParameterName() + "\t"
						+ vars[i].getDataType() + "\t" + vars[i].getParameter()
						+ "\t" + vars[i].getSpec() + "\t" + vars[i].getUnit()
						+ "\n");
			}

			// Close writer
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static ArrayList<EMSVariable> getDefaultVars() {

		ArrayList<EMSVariable> allVars = new ArrayList<EMSVariable>();

		try {
			File input = new File("SystemFiles\\master.emspd");

			BufferedReader in = new BufferedReader(new FileReader(
					input.getAbsolutePath()));

			String zeile = null;

			while ((zeile = in.readLine()) != null) {

				if (zeile.startsWith("Parameter")) {
					continue;
					// header überspringen
				}
				String[] temp = zeile.split(";");
				EMSVariable newVar = new EMSVariable(temp[1] + temp[2],
						temp[0], temp[1], temp[2], temp[3], temp[4], temp[5],
						temp[6], temp[7], temp[8], temp[9], temp[10]);
				allVars.add(newVar);

			}

			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return allVars;
	}

	public static ArrayList<EMSVariable> readInEmspdFile(File input) {

		ArrayList<EMSVariable> allVars = new ArrayList<EMSVariable>();

		try {
			BufferedReader in = new BufferedReader(new FileReader(
					input.getAbsolutePath()));

			String zeile = null;

			while ((zeile = in.readLine()) != null) {

				String[] temp = zeile.split("\t");
				EMSVariable newVar = new EMSVariable();
				newVar.setDataType(temp[1]);
				newVar.setParameter(temp[2]);
				newVar.setIdentifier(temp[1] + temp[2]);
				allVars.add(newVar);
			}
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return allVars;
	}
}
