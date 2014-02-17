package ems_2_csv_ExternalPackage.readIn;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class EMS_Store {

	String[] header;
	TreeMap<String, String[]> allMessagesTreeMap = new TreeMap<String, String[]>();
	ArrayList<String> errorLog = new ArrayList<String>();

	public void addLine(String key, String[] line) {
		allMessagesTreeMap.put(key, line);
	}

	public void setHeader(String[] headerLine) {
		header = headerLine;
	}

	public ArrayList<String> getErrorLog() {
		return errorLog;
	}

	public void setErrorLog(ArrayList<String> errorLog) {
		this.errorLog = errorLog;
	}

	public String[] getHeader() {
		return header;
	}

	public void addErrorLog(String errorFilePath) {
		errorLog.add(errorFilePath);
	}

	public void print() {

		Iterator<Entry<String, String[]>> it = allMessagesTreeMap.entrySet()
				.iterator();

		while (it.hasNext()) {
			Map.Entry<String, String[]> me = it.next();
			String[] uu = me.getValue();

			System.out.print(me.getKey() + "   ");
			for (String s : uu) {
				System.out.print(s + "   ");
			}
			System.out.println();
		}
		System.out.println(size());

	}

	public TreeMap<String, String[]> getAllMessagesTreeMap() {
		return allMessagesTreeMap;
	}

	public void write(String filepath, String separator, boolean bHeader) {

		try {
			PrintWriter pWriter = new PrintWriter(new FileWriter(filepath));

			// Header ausgeben
			if (bHeader == true) {
				for (int i = 0; i < header.length; i++) {

					String s = header[i] + separator;
					if (i == header.length - 1) {
						s = header[i];
					}

					pWriter.print(s);
				}
				pWriter.println();
			}

			// Rest ausgeben
			Iterator<Entry<String, String[]>> it = allMessagesTreeMap
					.entrySet().iterator();

			while (it.hasNext()) {
				Map.Entry<String, String[]> me = it.next();

				String[] uu = me.getValue();
				if (uu.length < header.length) {
					for (int i = 0; i < header.length; i++) {
						String s = null;
						if (i < uu.length)
							s = uu[i] + separator;
						else {
							if (i < header.length) {
								s = "" + separator;
								if (i == header.length - 1) {
									s = "";
								}
							}
						}
						pWriter.print(s);
					}
				} else {
					for (int i = 0; i < uu.length; i++) {
						String s = null;
						s = uu[i] + separator;
						if ((i == uu.length - 1)) {
							s = uu[i];
						}
						pWriter.print(s);
					}
				}
				pWriter.println();
			}
			pWriter.flush();
			pWriter.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public int size() {
		return allMessagesTreeMap.size();
	}

}
