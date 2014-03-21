package name.reidmiller.timeofemissions.model;

import java.util.Comparator;

public class TimeOfEmissionsGenerationComparator implements
		Comparator<CommonAggregateGeneration> {

	@Override
	public int compare(CommonAggregateGeneration o1,
			CommonAggregateGeneration o2) {

		if (o1.getTimeOfUseRate().compareTo(o2.getTimeOfUseRate()) == 0) {
			int emissionsComparison = Double.compare(
					o1.getCommonFuelType().getGramsCarbonDioxideForKWHeValue(
							o1.getScheduledMW()),
					o2.getCommonFuelType().getGramsCarbonDioxideForKWHeValue(
							o2.getScheduledMW()));
			if (emissionsComparison == 0) {
				// If emissions are the same, rank by available capacity
				// Note the inverse ordering of o2 and o1 because we want to 
				// rank according to the hour with more available capacity.
				int capacityComparison = Double.compare(o2.getAvailableCapacityMW(), o1.getAvailableCapacityMW());
				if (capacityComparison == 0) {
					// If capacity is the same, choose nearest hour
					return o1.getDate().compareTo(o2.getDate());
				} else {
					return capacityComparison;
				}
			} else {
				// Rank by scheduled emissions
				return emissionsComparison;
			}
		} else {
			// If Time-of-Use rates are not equal, return accordingly
			return o1.getTimeOfUseRate().compareTo(o2.getTimeOfUseRate());
		}
	}
}
