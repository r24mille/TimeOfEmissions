package name.reidmiller.timeofemissions.model;

import java.util.Date;

import org.joda.time.DateTime;

public class CommonAggregateGeneration {
	private CommonFuelType commonFuelType;
	private Date date;
	private GenerationValueType generationValueType;
	private double megawatts;

	public CommonAggregateGeneration(CommonFuelType commonFuelType,
			Date date, GenerationValueType generationValueType,
			double megawatts) {
		this.commonFuelType = commonFuelType;
		this.date = date;
		this.generationValueType = generationValueType;
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

	public GenerationValueType getGenerationValueType() {
		return generationValueType;
	}

	public void setGenerationValueType(GenerationValueType generationValueType) {
		this.generationValueType = generationValueType;
	}

	public double getMegawatts() {
		return megawatts;
	}

	public void setMegawatts(double megawatts) {
		this.megawatts = megawatts;
	}

}
