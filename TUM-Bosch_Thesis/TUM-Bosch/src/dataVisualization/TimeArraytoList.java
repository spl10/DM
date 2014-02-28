package dataVisualization;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Description: Converts time array to list. Supplementary function.
 * 
 */
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

}
