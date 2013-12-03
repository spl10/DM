package model_class;

public class Prediction {
	private String[] date;
	private String[] time;
	private String[][] weekday;
	private String[][] label;
	private Double[][][] param;

	public String[] getDate() {
		return date;
	}

	public void setDate(String[] date) {
		this.date = date;
	}

	public String[] getTime() {
		return time;
	}

	public void setTime(String[] time) {
		this.time = time;
	}

	public String[][] getLabel() {
		return label;
	}

	public void setLabel(String[][] label) {
		this.label = label;
	}

	public String[][] getWeekday() {
		return weekday;
	}

	public void setWeekday(String[][] weekday) {
		this.weekday = weekday;
	}

	public Double[][][] getParam() {
		return param;
	}

	public void setParam(Double[][][] param) {
		this.param = param;
	}
}
