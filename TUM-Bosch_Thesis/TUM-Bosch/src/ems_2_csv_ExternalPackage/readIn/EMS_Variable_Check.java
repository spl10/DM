package ems_2_csv_ExternalPackage.readIn;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.TreeMap;

public class EMS_Variable_Check {

	public static String[] checkFileonVars(String filenameInput)
			throws IOException {
		// Generate new File
		File fromFile = new File(filenameInput);
		// check if file is valid
		// Open the file and then get a channel from the stream
		FileInputStream fis = new FileInputStream(fromFile);
		FileChannel fc = fis.getChannel();

		MappedByteBuffer buff = fc.map(FileChannel.MapMode.READ_ONLY, 0,
				fc.size());

		TreeMap<String, String> vars = new TreeMap<String, String>();

		String msg = "";
		String dt;

		int steps = 0;
		while (buff.hasRemaining()) {

			steps++;
			if (steps > 4000000)
				break;

			byte currentByte = buff.get();

			msg = msg + String.format("%02X", currentByte);

			if (msg.endsWith("FFFF")) {

				if (msg.length() < 20) {
					msg = "";
					continue;
				}
				String index = "I"
						+ Integer.parseInt(
								"" + msg.charAt(18) + msg.charAt(19), 16);

				dt = "" + msg.charAt(14) + msg.charAt(15);

				if (dt.equals("FF")) {
					if (msg.length() < 28) {
						msg = "";
						continue;
					}
					int datatype = Integer.parseInt(
							"" + msg.charAt(22) + msg.charAt(23)
									+ msg.charAt(26) + msg.charAt(27), 16);
					if (datatype < 860)
						vars.put("DT255," + datatype + index, "");
				} else {
					vars.put("DT" + Integer.parseInt(dt, 16) + index, "");
				}
				msg = "";
			}
		}
		String[] erg = new String[vars.entrySet().size()];
		int y = 0;
		for (Map.Entry<String, String> e : vars.entrySet()) {
			erg[y] = "" + e.getKey();
			y++;
		}

		fc.close();
		fis.close();

		return erg;
	}

}
