package name.reidmiller.timeofemissions.model;

import org.joda.time.DateTime;

public class RegionEnergyGeneration {
	private Region region;
	private Iso iso;
	private String date;
	private GenerationMix geneartionMix;

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public Iso getIso() {
		return iso;
	}

	public void setIso(Iso iso) {
		this.iso = iso;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public GenerationMix getGeneartionMix() {
		return geneartionMix;
	}

	public void setGeneartionMix(GenerationMix geneartionMix) {
		this.geneartionMix = geneartionMix;
	}
}