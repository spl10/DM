package model_class;

import java.util.Set;

public class Detection {
	private String filepath;
	private Set<String> complete_days;
	private int timestep;
	private int[] temp;
	private String[] type;
	private String[] selectedParameters;

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public Set<String> getComplete_days() {
		return complete_days;
	}

	public void setComplete_days(Set<String> complete_days) {
		this.complete_days = complete_days;
	}

	public int getTimestep() {
		return timestep;
	}

	public void setTimestep(int timestep) {
		this.timestep = timestep;
	}

	public int[] getTemp() {
		return temp;
	}

	public void setTemp(int[] temp) {
		this.temp = temp;
	}

	public String[] getType() {
		return type;
	}

	public void setType(String[] type) {
		this.type = type;
	}

	public String[] getSelectedParameters() {
		return selectedParameters;
	}

	public void setSelectedParameters(String[] selectedParameters) {
		this.selectedParameters = selectedParameters;
	}
}
