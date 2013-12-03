package data_preprocessing.timestep;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author SIP2LOL
 * 
 */
public class CompleteDaysIdentification {
	/**
	 * This Function Identifies complete days based on the time information. For
	 * example, for each date, this function checks for every minute information
	 * starting from 00:00 to 23:59. If the information is available, then the
	 * day is counted as complete day.
	 * 
	 * @param date_list
	 * @param lof
	 * @param date_time
	 * @return
	 */
	public static Set<String> identifyingCompleteDays(
			ArrayList<String> date_list, int lof, String[][] date_time) {

		Set<String> complete_days = new LinkedHashSet<String>(date_list);
		System.out.println("All available days: " + complete_days);
		System.out.println("No. of Available Days: " + complete_days.size());
		Iterator<String> li = complete_days.iterator();
		ArrayList<String> remove_list = new ArrayList<String>();
		int[] li_length = new int[date_list.toArray().length];
		int i = 0;
		while (li.hasNext()) {
			String li_date = li.next();
			for (int k = 0; k < lof; k++) {
				if (li_date.equals(date_time[0][k]))
					li_length[i]++;
			}
			if (li_length[i] < 1440) {
				remove_list.add(li_date);
			}
			i++;
		}
		complete_days.removeAll(remove_list);
		return complete_days;
	}
}
