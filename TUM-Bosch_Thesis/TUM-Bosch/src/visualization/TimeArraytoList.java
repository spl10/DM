package visualization;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import dataPreprocessing.CompleteDaysIdentification;

public class TimeArraytoList {
	public static Set<String> arraytoListConversion(String[][] time,
			String[][] act_date, int timestep) throws ParseException {

		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < time.length; i++) {
			for (int j = 0; j < time[i].length; j++) {
				if (time[i][j] != null) {
					list.add(time[i][j]);
				}
			}
		}
		// list = listSorting(timestep);
		Set<String> unique_time = new LinkedHashSet<String>(list);
		return unique_time;
	}

	public static ArrayList<String> listSorting(int timestep) {
		String[] time_array = CompleteDaysIdentification.computeTime();
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < time_array.length; i++) {
			if (i % timestep == 0) {
				list.add(time_array[i]);
				System.out.println("i: " + i + " time_array[" + i + "]: "
						+ time_array[i]);
			}
		}

		return list;

	}
}
