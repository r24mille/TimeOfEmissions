package name.reidmiller.timeofemissions.model;

import java.util.SortedSet;
import java.util.TreeSet;

public enum Iso {
	AESO, IESO, MISO, SPP;

	public SortedSet<Region> getServiceRegions() {
		SortedSet<Region> regions = new TreeSet<Region>();
		switch (this) {
		case AESO:
			regions.add(Region.ALBERTA);
			break;
		case IESO:
			regions.add(Region.ONTARIO);
			break;
		case MISO:
			regions.add(Region.ARKANSAS);
			regions.add(Region.ILLINOIS);
			regions.add(Region.INDIANA);
			regions.add(Region.IOWA);
			regions.add(Region.LOUISIANA);
			regions.add(Region.MANITOBA);
			regions.add(Region.MINNESOTA);
			regions.add(Region.NORTH_DAKOTA);
			regions.add(Region.SOUTH_DAKOTA);
			regions.add(Region.WISCONSIN);
			regions.add(Region.MICHIGAN);
			// TODO Complete MISO regions
		case SPP:
			regions.add(Region.KANSAS);
			regions.add(Region.NEBRASKA);
			regions.add(Region.OKLAHOMA);
			// TODO Complete SPP regions
		}
		return regions;
	}
}
