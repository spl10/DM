package visualization;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class Pattern_Finding {

	public static void main(String[] args) throws Exception {
		String input = "C:\\Users\\SIP2LOL\\Documents\\MyTool\\Data\\working_gateways\\gateway_207010373\\2013\\08\\output\\out_gt207010373_aug2013_60ts_vis_Actual.csv";
		String output = "C:\\Users\\SIP2LOL\\Documents\\MyTool\\Data\\working_gateways\\gateway_207010373\\2013\\08\\output\\out_gt207010373_aug2013_60ts_vis_Pattern.csv";
		BufferedReader br = new BufferedReader(new FileReader(input));
		BufferedWriter bw = new BufferedWriter(new FileWriter(output));
		String line = br.readLine();
		bw.write("time");
		int l = 0, i = 0;
		String[] time = new String[24];
		while ((line = br.readLine()) != null) {
			String[] params = line.split("\\,");
			if (i < 24) {
				time[i] = params[1];
			}
			i++;
			if (l == 0) {
				bw.write("," + params[0]);
			}
			if (l < 24) {
				l++;
			} else {
				l = 0;
			}
		}
		bw.write("\n");
		br.close();
		for (i = 0; i < time.length; i++) {
			bw.write(time[i]);
			br = new BufferedReader(new FileReader(input));
			while ((line = br.readLine()) != null) {
				String[] params = line.split("\\,");
				if (params[1].equals(time[i])) {
					bw.write("," + params[10]);
				}
			}
			bw.write("\n");
			br.close();
		}
		bw.flush();
		bw.close();
		System.out.println("COMPLETED!!!");
	}
}
