package name.reidmiller.timeofemissions.web.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import name.reidmiller.iesoreports.IesoPublicReportBindingsConfig;
import name.reidmiller.iesoreports.client.DayAheadAdequacyClient;
import name.reidmiller.iesoreports.client.SurplusBaseloadGenerationClient;
import name.reidmiller.timeofemissions.model.CommonAggregateGeneration;
import name.reidmiller.timeofemissions.model.CommonFuelType;
import name.reidmiller.timeofemissions.model.CommonOversupply;
import name.reidmiller.timeofemissions.model.DataPointType;
import name.reidmiller.timeofemissions.model.Iso;
import name.reidmiller.timeofemissions.model.TimeOfEmissionsTargettedGenerationComparator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.ieso.reports.schema.daadequacy.DocBody.System.InternalResources;
import ca.ieso.reports.schema.daadequacy.DocBody.System.InternalResources.InternalResource;
import ca.ieso.reports.schema.daadequacy.DocBody.System.InternalResources.InternalResource.FuelOffered;
import ca.ieso.reports.schema.daadequacy.DocBody.System.InternalResources.InternalResource.FuelScheduled;
import ca.ieso.reports.schema.daadequacy.HourlyValue;
import ca.ieso.reports.schema.sbg.Document.DocBody.DailyForecast;
import ca.ieso.reports.schema.sbg.Document.DocBody.DailyForecast.HourlyForecast;

@Controller
public class SbgImpactController {
	private Logger logger = LogManager.getLogger(this.getClass());
	private static final int MAX_HOUR_SHIFT = 4;
	private static final String GENERATION_SHIFT = "generationShift";
	private static final String OVERSUPPLY_SHIFT = "oversupplyShift";
	DateTimeFormatter sbgTimestampFormatter = DateTimeFormat
			.forPattern("yyyy-MM-dd H:mm:ss");

	@RequestMapping(value = "/toe_impact/date/{datePart}/html", method = RequestMethod.GET)
	public String generatorOutput(@PathVariable String datePart, Model model) {
		model.addAttribute("date", datePart);
		return "toe_impact";
	}

	@RequestMapping(value = "/toe_impact/iso/{isoString}/date/{datePart}/json", method = RequestMethod.GET)
	public @ResponseBody
	HashMap<String, Object> generatorsJson(@PathVariable String isoString,
			@PathVariable String datePart) {
		Iso iso = Iso.valueOf(isoString.toUpperCase());
		DateTime jsonDateTime = sbgTimestampFormatter.parseDateTime(datePart
				+ " 0:00:00");
		HashMap<String, Object> json = new HashMap<String, Object>();

		switch (iso) {
		case IESO:
			TreeMap<CommonFuelType, List<CommonAggregateGeneration>> generation = this
					.getIesoAggregateDayAheadForecastMix(jsonDateTime);
			List<CommonOversupply> oversupply = this
					.getIesoSbgForecast(jsonDateTime);
			HashMap<String, Object> supplyShift = this.shiftSupply(generation,
					oversupply);

			json.put("generation", generation);
			json.put("oversupply", oversupply);
			json.put(GENERATION_SHIFT, supplyShift.get(GENERATION_SHIFT));
			json.put(OVERSUPPLY_SHIFT, supplyShift.get(OVERSUPPLY_SHIFT));

			HashMap<String, String> colors = new HashMap<String, String>();
			for (CommonFuelType commonFuelType : generation.keySet()) {
				colors.put(commonFuelType.toString(),
						commonFuelType.getGraphColor());
			}
			colors.put("OVERSUPPLY", "#787878");
			json.put("colors", colors);

			break;
		default:
			json.put("null", null);
			break;
		}

		return json;
	}

	private List<CommonOversupply> getIesoSbgForecast(
			DateTime forecastedDateTime) {
		List<CommonOversupply> commonOversupplyForecast = new ArrayList<CommonOversupply>();
		SurplusBaseloadGenerationClient sbgClient = IesoPublicReportBindingsConfig
				.surplusBaseloadGenerationClient();

		try {
			DateTime dateTimeBehind = forecastedDateTime.minusDays(1)
					.withTimeAtStartOfDay();
			ca.ieso.reports.schema.sbg.Document.DocBody docBody = sbgClient
					.getDocBodyForDate(dateTimeBehind.toDate());

			List<DailyForecast> dailyForecasts = docBody.getDailyForecast();

			for (DailyForecast dailyForecast : dailyForecasts) {
				// Only return the forecast for the desired date
				if (dailyForecast.getDateForecast().getDay() == forecastedDateTime
						.getDayOfMonth()) {
					for (HourlyForecast hourlyForecast : dailyForecast
							.getHourlyForecast()) {

						int clockHour = hourlyForecast.getHour() - 1;
						String timeString = dailyForecast.getDateForecast()
								.toString() + " " + clockHour + ":00:00";

						DateTime sbgDateTime = sbgTimestampFormatter
								.parseDateTime(timeString);
						CommonOversupply commonOversupply = new CommonOversupply(
								sbgDateTime.toDate(),
								hourlyForecast.getEnergyMW().doubleValue(),
								dailyForecast.getExportForecast().doubleValue(),
								DataPointType.FORECAST);
						commonOversupplyForecast.add(commonOversupply);
					}
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return commonOversupplyForecast;
	}

	/**
	 * Gets the default (day-ahead) aggregate generation forecast by fuel type
	 * from the IESO's public reports. For hour's in the current day which have
	 * not been reached, the prior day's day-ahead forecast is used as forecast
	 * values.
	 */
	public TreeMap<CommonFuelType, List<CommonAggregateGeneration>> getIesoAggregateDayAheadForecastMix(
			DateTime forecastedDateTime) {
		DateTime daaSolarChange = sbgTimestampFormatter
				.parseDateTime("2013-06-30 23:59:59");
		DateTime daaLocalCache = sbgTimestampFormatter
				.parseDateTime("2013-10-31 23:59:59");
		TreeMap<CommonFuelType, List<CommonAggregateGeneration>> commonAggregateGenerationForecast = new TreeMap<CommonFuelType, List<CommonAggregateGeneration>>();
		DayAheadAdequacyClient dayAheadAdequacyClient = IesoPublicReportBindingsConfig
				.dayAheadAdequacyClient();
		if (forecastedDateTime.isBefore(daaLocalCache)) {
			dayAheadAdequacyClient
					.setDefaultUrlString("http://localhost:8080/time-of-emissions/resources/xml/daadequacy/PUB_DAAdequacy.xml");
		}
		try {
			ca.ieso.reports.schema.daadequacy.DocBody docBody = dayAheadAdequacyClient
					.getDocBodyForDate(forecastedDateTime.toDate());

			// Because XML client is returning null deliveryDate, assume
			// date from ordering of docBodies
			DateTime startOfForecastedDateTime = forecastedDateTime
					.withTimeAtStartOfDay();

			// Because XML client is returning null systemName, assume
			// "Ontario" system from ordering of systems
			List<ca.ieso.reports.schema.daadequacy.DocBody.System> systems = docBody
					.getSystem();
			ca.ieso.reports.schema.daadequacy.DocBody.System ontarioSystem = systems
					.get(0);

			InternalResources internalResources = ontarioSystem
					.getInternalResources();
			if (internalResources != null) {
				List<InternalResource> internalResourceList = internalResources
						.getInternalResource();
				if (internalResourceList != null) {
					int internalResourceIndex = 0;
					for (InternalResource internalResource : internalResourceList) {
						CommonFuelType commonFuelType;
						// Because XML client is returning null fuelType,
						// assume CommonFuelType from ordering of
						// internalResources
						switch (internalResourceIndex) {
						case 0:
							commonFuelType = CommonFuelType.HYDROELECTRIC;
							break;
						case 1:
							commonFuelType = CommonFuelType.COAL;
							break;
						case 2:
							commonFuelType = CommonFuelType.NATURAL_GAS;
							break;
						case 3:
							commonFuelType = CommonFuelType.NUCLEAR;
							break;
						case 4:
							commonFuelType = CommonFuelType.WIND;
							break;
						case 5:
							if (forecastedDateTime.isAfter(daaSolarChange)) {
								commonFuelType = CommonFuelType.SOLAR_PV;
							} else {
								commonFuelType = CommonFuelType.OTHER;
							}
							break;
						case 6:
							if (forecastedDateTime.isAfter(daaSolarChange)) {
								commonFuelType = CommonFuelType.OTHER;
							} else {
								commonFuelType = CommonFuelType.DISPATCHABLE_LOAD;
							}
							break;
						case 7:
							if (forecastedDateTime.isAfter(daaSolarChange)) {
								commonFuelType = CommonFuelType.DISPATCHABLE_LOAD;
							} else {
								commonFuelType = null;
							}
							break;
						default:
							commonFuelType = null;
							break;
						}

						// If first DocBody being parsed, initialize
						// CommonFuelType key
						commonAggregateGenerationForecast.put(commonFuelType,
								new ArrayList<CommonAggregateGeneration>());

						FuelScheduled fuelScheduled = internalResource
								.getFuelScheduled();
						FuelOffered fueldOffered = internalResource
								.getFuelOffered();
						if (fuelScheduled != null && fueldOffered != null) {
							List<HourlyValue> scheduledHourlyValues = fuelScheduled
									.getScheduled();
							List<HourlyValue> offeredHourlyValues = fueldOffered
									.getOffered();

							for (int h = 0; h < scheduledHourlyValues.size()
									&& h < offeredHourlyValues.size(); h++) {
								HourlyValue scheduledHourlyValue = scheduledHourlyValues
										.get(h);
								double scheduledMW = 0;
								if (scheduledHourlyValue.getEnergyMW() != null) {
									scheduledMW = scheduledHourlyValue
											.getEnergyMW().doubleValue();
								}

								HourlyValue offeredHourlyValue = offeredHourlyValues
										.get(h);
								double offeredMW = 0;
								if (offeredHourlyValue.getEnergyMW() != null) {
									offeredMW = offeredHourlyValue
											.getEnergyMW().doubleValue();
								}

								CommonAggregateGeneration commonAggregateGeneration = new CommonAggregateGeneration(
										commonFuelType,
										startOfForecastedDateTime.plusHours(
												scheduledHourlyValue
														.getDeliveryHour() - 1)
												.toDate(),
										DataPointType.FORECAST, scheduledMW,
										offeredMW);

								commonAggregateGenerationForecast.get(
										commonFuelType).add(
										commonAggregateGeneration);
							}
						}

						internalResourceIndex++;
					}

				}
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return commonAggregateGenerationForecast;
	}

	private HashMap<String, Object> shiftSupply(
			TreeMap<CommonFuelType, List<CommonAggregateGeneration>> generation,
			List<CommonOversupply> oversupply) {
		// Deep clone both generation and oversupply
		TreeMap<CommonFuelType, List<CommonAggregateGeneration>> generationPlan = new TreeMap<CommonFuelType, List<CommonAggregateGeneration>>();
		for (Entry<CommonFuelType, List<CommonAggregateGeneration>> entry : generation
				.entrySet()) {
			ArrayList<CommonAggregateGeneration> clonedValue = new ArrayList<CommonAggregateGeneration>(
					entry.getValue().size());
			for (CommonAggregateGeneration commonAggregateGeneration : entry
					.getValue()) {
				clonedValue.add(commonAggregateGeneration.clone());
			}
			generationPlan.put(entry.getKey(), clonedValue);
		}

		List<CommonOversupply> oversupplyPlan = new ArrayList<CommonOversupply>(
				oversupply.size());
		for (CommonOversupply commonOversupply : oversupply) {
			oversupplyPlan.add(commonOversupply.clone());
		}

		// Order that low-emission generators will be targeted with dispatchable
		// load
		List<CommonFuelType> lowEmissionFuelTypes = new ArrayList<CommonFuelType>(
				4);
		lowEmissionFuelTypes.add(CommonFuelType.WIND);
		if (generationPlan.containsKey(CommonFuelType.SOLAR_PV)) {
			lowEmissionFuelTypes.add(CommonFuelType.SOLAR_PV);
		}
		lowEmissionFuelTypes.add(CommonFuelType.HYDROELECTRIC);
		lowEmissionFuelTypes.add(CommonFuelType.NUCLEAR);

		// Order that high-emission generators will be curtailed
		List<CommonFuelType> highEmissionFuelTypes = new ArrayList<CommonFuelType>(
				3);
		highEmissionFuelTypes.add(CommonFuelType.COAL);
		highEmissionFuelTypes.add(CommonFuelType.NATURAL_GAS);
		highEmissionFuelTypes.add(CommonFuelType.OTHER);

		logger.debug("Starting oversupply shift");
		for (CommonFuelType highEmissionFuelType : highEmissionFuelTypes) {
			for (CommonFuelType lowEmissionFuelType : lowEmissionFuelTypes) {
				for (int osHour = 0; osHour < oversupplyPlan.size(); osHour++) {
					int hourShiftStart = 0;
					if (osHour - MAX_HOUR_SHIFT > 0) {
						hourShiftStart = osHour - MAX_HOUR_SHIFT;
					}
					int hourShiftEnd = 23;
					if (osHour + MAX_HOUR_SHIFT < 23) {
						hourShiftEnd = osHour + MAX_HOUR_SHIFT;
					}
					logger.debug("Oversupply Hour = " + osHour
							+ ", Minimum Shiftable Hour = " + hourShiftStart
							+ ", Maximum Shiftable Hour = " + hourShiftEnd
							+ ".");

					// For each oversupply hour, check if there is excess above
					// exportThreshold
					if (oversupplyPlan.get(osHour).getExcess() > 0) {
						oversupplyPlan.get(osHour).getExcess();
						logger.debug(oversupplyPlan.get(osHour).getExcess()
								+ " MW excess oversupply in hour " + osHour);

						// Add all CommonAggregateGeneration elements in the
						// range,
						// ranked by Time-of-Emissions preference
						TreeSet<CommonAggregateGeneration> shiftRanks = new TreeSet<CommonAggregateGeneration>(
								new TimeOfEmissionsTargettedGenerationComparator());
						shiftRanks.addAll(generationPlan.get(
								highEmissionFuelType).subList(hourShiftStart,
								hourShiftEnd + 1));

						// Iterate through generators ranked by shiftability
						int rank = 0;
						for (CommonAggregateGeneration rankedGeneration : shiftRanks) {
							// TODO Daylight savings time error here, duplicate
							// indexes
							// as well
							int hourShift = new DateTime(
									rankedGeneration.getDate()).getHourOfDay();
							logger.debug("fuel="
									+ rankedGeneration.getCommonFuelType()
									+ ", rank="
									+ rank
									+ ", hour="
									+ hourShift
									+ ", rate="
									+ rankedGeneration.getTimeOfUseRate()
									+ ", emissions="
									+ rankedGeneration.getCommonFuelType()
											.getGramsCarbonDioxideForKWHeValue(
													rankedGeneration
															.getScheduledMW())
									+ ", capacity="
									+ rankedGeneration.getAvailableCapacityMW());

							// Shift as much consumption from excess as there is
							// excess
							// scheduled high-emissions, available low-emissions
							// capacity, and remaining excess
							double shiftMW = Math.min(Math.min(
									rankedGeneration.getScheduledMW(),
									generationPlan.get(lowEmissionFuelType)
											.get(osHour)
											.getAvailableCapacityMW()),
									oversupplyPlan.get(osHour).getExcess());
							// Sometimes generators will be scheduled for

							generationPlan.get(highEmissionFuelType)
									.get(hourShift)
									.scheduleGenerationMW(0 - shiftMW);
							logger.debug("After "
									+ highEmissionFuelType
									+ " generation change scheduledMW="
									+ generationPlan.get(highEmissionFuelType)
											.get(hourShift).getScheduledMW()
									+ ", offeredMW="
									+ generationPlan.get(highEmissionFuelType)
											.get(hourShift).getOfferedMW()
									+ ", availableCapacityMW="
									+ generationPlan.get(highEmissionFuelType)
											.get(hourShift)
											.getAvailableCapacityMW());

							generationPlan.get(lowEmissionFuelType).get(osHour)
									.scheduleGenerationMW(shiftMW);
							logger.debug("After "
									+ lowEmissionFuelType
									+ " generation change scheduledMW="
									+ generationPlan.get(lowEmissionFuelType)
											.get(osHour).getScheduledMW()
									+ ", offeredMW="
									+ generationPlan.get(lowEmissionFuelType)
											.get(osHour).getOfferedMW()
									+ ", availableCapacityMW="
									+ generationPlan.get(lowEmissionFuelType)
											.get(osHour)
											.getAvailableCapacityMW());

							// TODO Add shiftMW to DISPATCHABLE_LOAD's offerMW
							// for shiftHour

							logger.debug("excessMW="
									+ oversupplyPlan.get(osHour).getExcess()
									+ " lowered by shiftMW=" + shiftMW);
							oversupplyPlan.get(osHour).setExcess(
									oversupplyPlan.get(osHour).getExcess()
											- shiftMW);

							// TODO Add shiftMW to DISPATCHABLE_LOAD's
							// scheduleMW for osHour
							// TODO Remove shiftMW from DISPATCHABLE_LOAD's
							// offerMW for osHour

							// Step rank if there is still excess, break if no
							// excess
							// left
							if (oversupplyPlan.get(osHour).getExcess() <= 0) {
								break;
							} else {
								rank++;
							}
						}
					}
				}
			}
		}

		HashMap<String, Object> supplyShift = new HashMap<String, Object>(2);
		supplyShift.put(GENERATION_SHIFT, generationPlan);
		supplyShift.put(OVERSUPPLY_SHIFT, oversupplyPlan);
		return supplyShift;
	}
}
