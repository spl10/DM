package EMS_2_CSV.emspd;

public class EMSVariable {

	/**
	 * @param args
	 */
	String identifier; // dataType + parameter
	String parameterName;
	String dataType;
	String parameter;
	String spec;
	String unit;
	String datatypeName;
	String dataTypeDescription;
	String dataTypeClass;
	String periodicallyBroadcasted;
	String ems1;
	String ems2;

	public EMSVariable(String identifier, String parameterName,
			String dataType, String parameter, String spec, String unit,
			String datatypeName, String dataTypeDescription,
			String dataTypeClass, String periodicallyBroadcasted, String ems1,
			String ems2) {
		super();
		this.identifier = identifier;
		this.parameterName = parameterName;
		this.dataType = dataType;
		this.parameter = parameter;
		this.spec = spec;
		this.unit = unit;
		this.datatypeName = datatypeName;
		this.dataTypeDescription = dataTypeDescription;
		this.dataTypeClass = dataTypeClass;
		this.periodicallyBroadcasted = periodicallyBroadcasted;
		this.ems1 = ems1;
		this.ems2 = ems2;
	}

	public EMSVariable() {
		// TODO Auto-generated constructor stub
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getDatatypeName() {
		return datatypeName;
	}

	public void setDatatypeName(String datatypeName) {
		this.datatypeName = datatypeName;
	}

	public String getDataTypeDescription() {
		return dataTypeDescription;
	}

	public void setDataTypeDescription(String dataTypeDescription) {
		this.dataTypeDescription = dataTypeDescription;
	}

	public String getDataTypeClass() {
		return dataTypeClass;
	}

	public void setDataTypeClass(String dataTypeClass) {
		this.dataTypeClass = dataTypeClass;
	}

	public String isPeriodicallyBroadcasted() {
		return periodicallyBroadcasted;
	}

	public void setPeriodicallyBroadcasted(String periodicallyBroadcasted) {
		this.periodicallyBroadcasted = periodicallyBroadcasted;
	}

	public String isEms1() {
		return ems1;
	}

	public void setEms1(String ems1) {
		this.ems1 = ems1;
	}

	public String isEms2() {
		return ems2;
	}

	public void setEms2(String ems2) {
		this.ems2 = ems2;
	}

	@Override
	public String toString() {
		return parameterName;

	}

}
