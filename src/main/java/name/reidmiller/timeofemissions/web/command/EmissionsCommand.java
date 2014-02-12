package name.reidmiller.timeofemissions.web.command;

public class EmissionsCommand {
	private String startDateString;
	private String endDateString;

	public String getStartDateString() {
		return startDateString;
	}

	public void setStartDateString(String startDateString) {
		this.startDateString = startDateString;
	}

	public String getEndDateString() {
		return endDateString;
	}

	public void setEndDateString(String endDateString) {
		this.endDateString = endDateString;
	}
}