package ems_2_csv_ExternalPackage.preprocess;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import ems_2_csv_ExternalPackage.readIn.EMS_Store;

public class EMS_RegularTimeSeries {

	/**
	 * @param args
	 */
	public static EMS_Store getTimeSeries(EMS_Store inputStore,
			boolean tenMinutes) {

		// boolean tenMinutes = true -> auf 10 minuten
		// boolean tenMinutes = true -> auf 1 minute

		EMS_Store outputStore = new EMS_Store();

		outputStore.setHeader(inputStore.getHeader());
		outputStore.setErrorLog(inputStore.getErrorLog());

		Iterator<Entry<String, String[]>> it = inputStore
				.getAllMessagesTreeMap().entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<String, String[]> me = it.next();
			String[] value = me.getValue();
			String key = me.getKey();

			if (tenMinutes == false) {
				// durch wegschneiden der Sekunden beim Key gibt es nur noch
				// einen Wert pro Minute ( der zuletzt gespeicherte)
				String newKey = key.substring(0, 16);

				value[1] = value[1].substring(0, 5); // auch in den Werten
														// anpassen

				outputStore.addLine(newKey, value);
			} else {
				// durch wegschneiden der Sekunden beim Key gibt es nur noch
				// einen Wert pro Minute ( der zuletzt gespeicherte)
				String newKey = key.substring(0, 15);

				value[1] = value[1].substring(0, 4); // auch in den Werten
														// anpassen

				outputStore.addLine(newKey, value);
			}
		}

		return outputStore;
	}

}
