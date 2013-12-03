package data_preprocessing.timestep;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ParameterTypeDefinition {
	/**
	 * @param selectionlength
	 * @param temp
	 * @param selectedParameters
	 * @param line
	 * @return
	 * @throws IOException
	 */
	/*
	 * 1. DT00233_1-2DHW_Actual_Temperature 2. DT00233_0DHW_setpoint 3.
	 * DT00209_0-1Outdoor_temperature_measured_value 4.
	 * DT00233_3-4DHW_cylinder_temperature 5. DT00048_0-1mains_voltage 6.
	 * DT00227_0.0Operation_State_Source__HEAT 7.
	 * DT00227_0.2Operation_State_Source__IDLE 8. DT00227_13System_Power 9.
	 * DT00228_23-24Appliance_Supply_Temperature 10. DT00022_0CH_switch_on_off
	 * 11. DT00231_0Circulation_pump_request 12.
	 * DT00233_17-19Number_of_starts_DHW 13. DT00233_14-16Total_working_time_DHW
	 * 14. DT00233_13.2DHW_status_info_2__Circulation_Pump_is_running 15.
	 * DT00233_12.6DHW_status_info_1___free
	 */
	public static String[] definingParameterType(int selectionlength,
			int[] temp, String filepath, String[] selectedParameters)
			throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filepath));
		String[] type = new String[selectionlength];
		int[] type_count = new int[selectionlength];
		// String line = br.readLine();
		// while ((line = br.readLine()) != null) {
		// for (int i = 0; i < temp.length; i++) {
		// if ((!line.split(",")[temp[i]].equals("1") &&
		// !line.split(",")[temp[i]]
		// .equals("0"))) {
		// type_count[i]++;
		// } else {
		// type[i] = "binary";
		// }
		// }
		// }
		for (int i = 0; i < temp.length; i++) {
			switch (selectedParameters[i].trim()) {
			case "DT00233_1-2DHW_Actual_Temperature":
				type[i] = "Numeric";
				break;
			case "DT00233_0DHW_setpoint":
				type[i] = "Numeric";
				break;
			case "DT00209_0-1Outdoor_temperature_measured_value":
				type[i] = "Numeric";
				break;
			case "DT00233_3-4DHW_cylinder_temperature":
				type[i] = "Numeric";
				break;
			case "DT00048_0-1mains_voltage":
				type[i] = "Numeric";
				break;
			case "DT00227_0.0Operation_State_Source__HEAT":
				type[i] = "Binary";
				break;
			case "DT00227_0.2Operation_State_Source__IDLE":
				type[i] = "Binary";
				break;
			case "DT00227_13System_Power":
				type[i] = "Numeric";
				break;
			case "DT00228_23-24Appliance_Supply_Temperature":
				type[i] = "Numeric";
				break;
			case "DT00022_0CH_switch_on_off":
				type[i] = "Binary";
				break;
			case "DT00233_17-19Number_of_starts_DHW":
				type[i] = "FindMax";
				break;
			case "DT00231_0Circulation_pump_request":
				type[i] = "Binary";
				break;
			case "DT00233_14-16Total_working_time_DHW":
				type[i] = "Numeric";
				break;
			default:
				if (type_count[i] != 0) {
					type[i] = "Numeric";
				} else {
					type[i] = "Binary";
				}
			}

		}
		br.close();
		return type;
	}
}
