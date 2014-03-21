package name.reidmiller.timeofemissions.model;

public class DemandShift {
	private CommonFuelType commonFuelType;
	private double offerMW;
	private double scheduledMW;

	public CommonFuelType getCommonFuelType() {
		return commonFuelType;
	}

	public void setCommonFuelType(CommonFuelType commonFuelType) {
		this.commonFuelType = commonFuelType;
	}

	public double getOfferMW() {
		return offerMW;
	}

	public void setOfferMW(double offerMW) {
		this.offerMW = offerMW;
	}

	public double getScheduledMW() {
		return scheduledMW;
	}

	public void setScheduledMW(double scheduledMW) {
		this.scheduledMW = scheduledMW;
	}

}