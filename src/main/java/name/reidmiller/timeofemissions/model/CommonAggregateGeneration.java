package name.reidmiller.timeofemissions.model;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

public class CommonAggregateGeneration implements Cloneable {
	private Logger logger = LogManager.getLogger(this.getClass());
	private CommonFuelType commonFuelType;
	private Date date;
	private DataPointType dataPointType;
	private double scheduledMW;
	private double offeredMW;
	private double availableCapacityMW;
	private TimeOfUseSeason timeOfUseSeason;
	private TimeOfUseRate timeOfUseRate;

	public CommonAggregateGeneration(CommonFuelType commonFuelType, Date date,
			DataPointType dataPointType, double scheduledMW, double offeredMW) {
		this.commonFuelType = commonFuelType;
		this.date = date;
		this.dataPointType = dataPointType;
		this.scheduledMW = scheduledMW;
		this.offeredMW = offeredMW;
		this.availableCapacityMW = offeredMW - scheduledMW;

		DateTime dateTime = new DateTime(date);
		this.timeOfUseSeason = TimeOfUseSeason.valueOfDateTime(dateTime);
		this.timeOfUseRate = TimeOfUseRate.valueOfHour(dateTime.getHourOfDay(),
				this.timeOfUseSeason, (dateTime.getDayOfWeek() > 5));
	}

	@Override
	public CommonAggregateGeneration clone() {
		return new CommonAggregateGeneration(this.commonFuelType,
				(Date) this.date.clone(), this.dataPointType, this.scheduledMW,
				this.offeredMW);
	}

	/**
	 * Schedule a change to the generation plan. This adds to scheduledMW,
	 * subtracts from offeredMW, and subtracts from availableCapacityMW
	 */
	public void scheduleGenerationMW(double megawatts) {
		logger.debug("Before " + this.commonFuelType
				+ " generation change scheduledMW=" + this.scheduledMW
				+ ", offeredMW=" + this.offeredMW + ", availableCapacityMW="
				+ this.availableCapacityMW);
		this.scheduledMW = this.scheduledMW + megawatts;
		this.availableCapacityMW = this.offeredMW - this.scheduledMW;
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

	public DataPointType getDataPointType() {
		return dataPointType;
	}

	public void setDataPointType(DataPointType dataPointType) {
		this.dataPointType = dataPointType;
	}

	public double getScheduledMW() {
		return scheduledMW;
	}

	public void setScheduledMW(double scheduledMW) {
		this.scheduledMW = scheduledMW;
	}

	public double getOfferedMW() {
		return offeredMW;
	}

	public void setOfferedMW(double offeredMW) {
		this.offeredMW = offeredMW;
	}

	public double getAvailableCapacityMW() {
		return availableCapacityMW;
	}

	public void setAvailableCapacityMW(double availableCapacityMW) {
		this.availableCapacityMW = availableCapacityMW;
	}

	public TimeOfUseSeason getTimeOfUseSeason() {
		return timeOfUseSeason;
	}

	public void setTimeOfUseSeason(TimeOfUseSeason timeOfUseSeason) {
		this.timeOfUseSeason = timeOfUseSeason;
	}

	public TimeOfUseRate getTimeOfUseRate() {
		return timeOfUseRate;
	}

	public void setTimeOfUseRate(TimeOfUseRate timeOfUseRate) {
		this.timeOfUseRate = timeOfUseRate;
	}
}