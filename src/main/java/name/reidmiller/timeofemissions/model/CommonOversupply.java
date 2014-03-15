package name.reidmiller.timeofemissions.model;

import java.util.Date;

public class CommonOversupply {
	private Date date;
	private double megawatts;
	private double exportThreshold;
	private DataPointType dataPointType;
	
	public CommonOversupply(Date date, double megawatts, double exportThreshold, DataPointType dataPointType) {
		this.date = date;
		this.megawatts = megawatts;
		this.exportThreshold = exportThreshold;
		this.dataPointType = dataPointType;
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
	public DataPointType getDataPointType() {
		return dataPointType;
	}
	public void setDataPointType(DataPointType dataPointType) {
		this.dataPointType = dataPointType;
	}
}
