package miscellaneous;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class Pattern_Finding_Comparison {

	public static void main(String[] args) throws Exception {
		File f = new File(
				"C:\\Users\\SIP2LOL\\Documents\\MyTool\\Data\\working_gateways\\gateway_207010358\\2013\\12\\output");
		BufferedWriter bw = new BufferedWriter(
				new FileWriter(
						"C:\\Users\\SIP2LOL\\Documents\\MyTool\\Data\\working_gateways\\gateway_207010358\\2013\\12\\output\\out_207010358_dec2013_60ts_vis_Pattern.csv"));
		bw.write("Date,00:00,01:00,02:00,03:00,04:00,05:00,06:00,07:00,08:00,09:00,10:00,11:00,12:00,13:00,14:00,15:00,16:00,17:00,18:00,19:00,20:00,21:00,22:00,23:00\n");
		File[] files = f.listFiles();
		for (File file : files) {
			String fname = file.getAbsolutePath();
			if (fname.contains("__prediction") && fname.contains("_60ts_")) {
				String afname = fname.split("__prediction")[0] + "_Actual.csv";
				BufferedReader br_a = new BufferedReader(new FileReader(afname));
				BufferedReader br_p = new BufferedReader(new FileReader(fname));

				String line_a = br_a.readLine();
				line_a = br_a.readLine();
				bw.write(line_a.split(",")[0] + "_a," + line_a.split(",")[5]);
				while ((line_a = br_a.readLine()) != null) {
					bw.write("_a," + line_a.split(",")[5]);
				}
				bw.write("_a\n");
				br_a.close();

				String line_p = br_p.readLine();
				line_p = br_p.readLine();
				bw.write(line_p.split(",")[0] + "_p," + line_p.split(",")[5]);
				while ((line_p = br_p.readLine()) != null) {
					bw.write("_p," + line_p.split(",")[5]);
				}
				bw.write("_p\n");
				br_p.close();
			}
		}
		bw.flush();
		bw.close();
		System.out.println("COMPLETED!!!");
	}
}
