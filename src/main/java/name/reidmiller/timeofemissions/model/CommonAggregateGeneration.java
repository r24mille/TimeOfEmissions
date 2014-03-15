package name.reidmiller.timeofemissions.model;

import java.util.Date;

import org.joda.time.DateTime;

public class CommonAggregateGeneration {
	private CommonFuelType commonFuelType;
	private Date date;
	private DataPointType dataPointType;
	private double megawatts;

	public CommonAggregateGeneration(CommonFuelType commonFuelType,
			Date date, DataPointType dataPointType,
			double megawatts) {
		this.commonFuelType = commonFuelType;
		this.date = date;
		this.dataPointType = dataPointType;
		this.megawatts = megawatts;
	}

	public CommonFuelType getCommonFuelType() {
		return commonFuelType;
	}

	public void setCommonFuelType(CommonFuelType commonFuelType) {
		this.commonFuelType = commonFuelType;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public DataPointType getGenerationValueType() {
		return dataPointType;
	}

	public void setGenerationValueType(DataPointType dataPointType) {
		this.dataPointType = dataPointType;
	}

	public double getMegawatts() {
		return megawatts;
	}

	public void setMegawatts(double megawatts) {
		this.megawatts = megawatts;
	}

}
