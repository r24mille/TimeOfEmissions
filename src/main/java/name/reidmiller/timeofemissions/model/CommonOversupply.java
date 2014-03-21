package name.reidmiller.timeofemissions.model;

import java.util.Date;

public class CommonOversupply {
	private Date date;
	private double megawatts;
	private double exportThreshold;
	private double excess;
	private DataPointType dataPointType;

	public CommonOversupply(Date date, double megawatts,
			double exportThreshold, DataPointType dataPointType) {
		this.date = date;
		this.megawatts = megawatts;
		this.exportThreshold = exportThreshold;
		this.dataPointType = dataPointType;
		
		if (megawatts > exportThreshold) {
			this.excess = megawatts - exportThreshold;
		} else {
			this.excess = 0;
		}
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getMegawatts() {
		return megawatts;
	}

	public void setMegawatts(double megawatts) {
		this.megawatts = megawatts;
	}

	public double getExportThreshold() {
		return exportThreshold;
	}

	public void setExportThreshold(double exportThreshold) {
		this.exportThreshold = exportThreshold;
	}

	public double getExcess() {
		return excess;
	}

	public void setExcess(double excess) {
		this.excess = excess;
	}

	public DataPointType getDataPointType() {
		return dataPointType;
	}

	public void setDataPointType(DataPointType dataPointType) {
		this.dataPointType = dataPointType;
	}
}
