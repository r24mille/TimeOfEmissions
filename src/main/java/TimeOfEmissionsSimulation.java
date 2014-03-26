import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import name.reidmiller.timeofemissions.model.CommonAggregateGeneration;
import name.reidmiller.timeofemissions.model.CommonFuelType;
import name.reidmiller.timeofemissions.model.CommonOversupply;
import name.reidmiller.timeofemissions.web.controller.SbgImpactController;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TimeOfEmissionsSimulation {
	private static Logger logger = LogManager
			.getLogger(TimeOfEmissionsSimulation.class);

	public static void main(String[] args) {
		DateTimeFormatter timestampFormatter = DateTimeFormat
				.forPattern("yyyy-MM-dd H:mm:ss");
		DateTime startDateTime = timestampFormatter
				.parseDateTime("2013-06-01 0:00:00");
		DateTime endDateTime = timestampFormatter
				.parseDateTime("2013-06-30 0:00:00");
		DateTime currentDateTime = new DateTime(startDateTime.toInstant());
		SbgImpactController sbgImpactController = new SbgImpactController();
		
		double oversupplyMonthlyShifted = 0;
		double coalMonthlyCurtailed = 0;
		double naturalGasMonthlyCurtailed = 0;
		double otherMonthlyCurtailed = 0;
		double windOfferUsedMonthly = 0;
		double hydroOfferUsedMonthly = 0;
		double nuclearOfferUsedMonthly = 0;

		while (currentDateTime.isBefore(endDateTime)
				|| currentDateTime.isEqual(endDateTime)) {
			double oversupplyDailyShifted = 0;
			double coalDailyCurtailed = 0;
			double naturalGasDailyCurtailed = 0;
			double otherDailyCurtailed = 0;
			double windOfferUsedDaily = 0;
			double hydroOfferUsedDaily = 0;
			double nuclearOfferUsedDaily = 0;

			TreeMap<CommonFuelType, List<CommonAggregateGeneration>> generation = sbgImpactController
					.getIesoAggregateDayAheadForecastMix(currentDateTime);
			List<CommonOversupply> oversupply = sbgImpactController
					.getIesoSbgForecast(currentDateTime);
			HashMap<String, Object> supplyShift = sbgImpactController
					.shiftSupply(generation, oversupply);
			@SuppressWarnings("unchecked")
			TreeMap<CommonFuelType, List<CommonAggregateGeneration>> generationShift = (TreeMap<CommonFuelType, List<CommonAggregateGeneration>>) supplyShift
					.get(SbgImpactController.GENERATION_SHIFT);
			@SuppressWarnings("unchecked")
			List<CommonOversupply> oversupplyShift = (List<CommonOversupply>) supplyShift
					.get(SbgImpactController.OVERSUPPLY_SHIFT);

			for (int i = 0; i < oversupply.size(); i++) {
				oversupplyDailyShifted = oversupplyDailyShifted
						+ oversupply.get(i).getExcess()
						- oversupplyShift.get(i).getExcess();
				oversupplyMonthlyShifted = oversupplyMonthlyShifted + oversupplyDailyShifted;

				coalDailyCurtailed = coalDailyCurtailed
						+ generation.get(CommonFuelType.COAL).get(i)
								.getScheduledMW()
						- generationShift.get(CommonFuelType.COAL).get(i)
								.getScheduledMW();
				coalMonthlyCurtailed = coalMonthlyCurtailed + coalDailyCurtailed;

				naturalGasDailyCurtailed = naturalGasDailyCurtailed
						+ generation.get(CommonFuelType.NATURAL_GAS).get(i)
								.getScheduledMW()
						- generationShift.get(CommonFuelType.NATURAL_GAS)
								.get(i).getScheduledMW();
				naturalGasMonthlyCurtailed = naturalGasMonthlyCurtailed + naturalGasDailyCurtailed;

				otherDailyCurtailed = otherDailyCurtailed
						+ generation.get(CommonFuelType.OTHER).get(i)
								.getScheduledMW()
						- generationShift.get(CommonFuelType.OTHER).get(i)
								.getScheduledMW();
				otherMonthlyCurtailed = otherMonthlyCurtailed + otherDailyCurtailed;

				windOfferUsedDaily = windOfferUsedDaily
						+ generationShift.get(CommonFuelType.WIND).get(i)
								.getScheduledMW()
						- generation.get(CommonFuelType.WIND).get(i)
								.getScheduledMW();
				windOfferUsedMonthly = windOfferUsedMonthly + windOfferUsedDaily;

				hydroOfferUsedDaily = hydroOfferUsedDaily
						+ generationShift.get(CommonFuelType.HYDROELECTRIC)
								.get(i).getScheduledMW()
						- generation.get(CommonFuelType.HYDROELECTRIC).get(i)
								.getScheduledMW();
				hydroOfferUsedMonthly = hydroOfferUsedMonthly + hydroOfferUsedDaily;

				nuclearOfferUsedDaily = nuclearOfferUsedDaily
						+ generationShift.get(CommonFuelType.NUCLEAR).get(i)
								.getScheduledMW()
						- generation.get(CommonFuelType.NUCLEAR).get(i)
								.getScheduledMW();
				nuclearOfferUsedMonthly = nuclearOfferUsedMonthly + nuclearOfferUsedDaily;
			}
			
			logger.info("--------------------------");
			logger.info("Date: " + currentDateTime.toDate());
			logger.info("Oversupply Shifted: " + oversupplyDailyShifted);
			logger.info("Coal Curtailed: " + coalDailyCurtailed);
			logger.info("Natural Gas Curtailed: " + naturalGasDailyCurtailed);
			logger.info("Other Curtailed: " + otherDailyCurtailed);
			logger.info("Additional Wind Scheduled: " + windOfferUsedDaily);
			logger.info("Additional Hydro Scheduled: " + hydroOfferUsedDaily);
			logger.info("Additional Nuclear Scheduled: " + nuclearOfferUsedDaily);
			
			currentDateTime = currentDateTime.plusDays(1);
		}
		
		logger.info("=========================================");
		logger.info("Montly Total");
		logger.info("Oversupply Shifted: " + oversupplyMonthlyShifted);
		logger.info("Coal Curtailed: " + coalMonthlyCurtailed);
		logger.info("Natural Gas Curtailed: " + naturalGasMonthlyCurtailed);
		logger.info("Other Curtailed: " + otherMonthlyCurtailed);
		logger.info("Additional Wind Scheduled: " + windOfferUsedMonthly);
		logger.info("Additional Hydro Scheduled: " + hydroOfferUsedMonthly);
		logger.info("Additional Nuclear Scheduled: " + nuclearOfferUsedMonthly);
	}
}
