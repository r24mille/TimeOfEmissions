package name.reidmiller.timeofemissions.model;

import java.util.Comparator;

public class TimeOfEmissionsTargettedGenerationComparator implements
		Comparator<CommonAggregateGeneration> {

	@Override
	public int compare(CommonAggregateGeneration o1,
			CommonAggregateGeneration o2) {

		// Inverse ordering of o2 and o1 ranks higher-cost time-of-use rates 
		// before lower-cost time-of-use rates.
		if (o2.getTimeOfUseRate().compareTo(o1.getTimeOfUseRate()) == 0) {
			// If time-of-use rates are the same, rank by emissions
			// Inverse ordering of emissions ranks higher emissions before lower emissions
			int emissionsComparison = Double.compare(
					o2.getCommonFuelType().getGramsCarbonDioxideForKWHeValue(
							o2.getScheduledMW()),
					o1.getCommonFuelType().getGramsCarbonDioxideForKWHeValue(
							o1.getScheduledMW()));
			if (emissionsComparison == 0) {
				// If emissions are the same, rank by available capacity. Fuels with less capacity 
				// should be ranked higher to be targetted with demand response to free up capacity.
				int capacityComparison = Double.compare(o1.getAvailableCapacityMW(), o2.getAvailableCapacityMW());
				if (capacityComparison == 0) {
					// If capacity is the same, choose nearest hour
					// TODO Misnomer, actually just orders dates rather than choosing hour nearest the oversupply hour in question.
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
			return o2.getTimeOfUseRate().compareTo(o1.getTimeOfUseRate());
		}
	}
}
