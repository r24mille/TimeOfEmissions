package name.reidmiller.timeofemissions.web.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import name.reidmiller.iesoreports.IesoPublicReportBindingsConfig;
import name.reidmiller.iesoreports.client.DayAheadAdequacyClient;
import name.reidmiller.iesoreports.client.SurplusBaseloadGenerationClient;
import name.reidmiller.timeofemissions.model.CommonAggregateGeneration;
import name.reidmiller.timeofemissions.model.CommonAggregateGenerationMix;
import name.reidmiller.timeofemissions.model.CommonFuelType;
import name.reidmiller.timeofemissions.model.CommonOversupply;
import name.reidmiller.timeofemissions.model.DataPointType;
import name.reidmiller.timeofemissions.model.Iso;
import name.reidmiller.timeofemissions.web.command.GeneratorOutputCommand;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.ieso.reports.schema.daadequacy.DocBody.System.InternalResources;
import ca.ieso.reports.schema.daadequacy.DocBody.System.InternalResources.InternalResource;
import ca.ieso.reports.schema.daadequacy.DocBody.System.InternalResources.InternalResource.FuelScheduled;
import ca.ieso.reports.schema.daadequacy.HourlyValue;
import ca.ieso.reports.schema.sbg.Document.DocBody.DailyForecast;
import ca.ieso.reports.schema.sbg.Document.DocBody.DailyForecast.HourlyForecast;

@Controller
public class SbgImpactController {
	private Logger logger = LogManager.getLogger(this.getClass());
	DateTimeFormatter sbgTimestampFormatter = DateTimeFormat
			.forPattern("yyyy-MM-dd H:mm:ss");
	
	@RequestMapping("/toe_impact/html")
	public String generatorOutput(Model model) {
		return "toe_impact";
	}

	@RequestMapping(value = "/toe_impact/iso/{isoString}/date/{datePart}/json", method = RequestMethod.GET)
	public @ResponseBody
	HashMap<String, Object> generatorsJson(@PathVariable String isoString, @PathVariable String datePart) {
		Iso iso = Iso.valueOf(isoString.toUpperCase());
		DateTime jsonDateTime = sbgTimestampFormatter.parseDateTime(datePart + " 0:00:00");
		HashMap<String, Object> json = new HashMap<String, Object>();

		switch (iso) {
		case IESO:
			json.put(
					"generation",
					this.getIesoAggregateDayAheadForecastMix(jsonDateTime));
			json.put("oversupply",
					this.getIesoSbgForecast(jsonDateTime));
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
	public HashMap<CommonFuelType, List<CommonAggregateGeneration>> getIesoAggregateDayAheadForecastMix(
			DateTime forecastedDateTime) {
		HashMap<CommonFuelType, List<CommonAggregateGeneration>> commonAggregateGenerationForecast = new HashMap<CommonFuelType, List<CommonAggregateGeneration>>();
		DayAheadAdequacyClient dayAheadAdequacyClient = IesoPublicReportBindingsConfig
				.dayAheadAdequacyClient();
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
							commonFuelType = CommonFuelType.SOLAR_PV;
							break;
						case 6:
							commonFuelType = CommonFuelType.OTHER;
							break;
						case 7:
							commonFuelType = CommonFuelType.DISPATCHABLE_LOAD;
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
						if (fuelScheduled != null) {
							List<HourlyValue> hourlyValues = fuelScheduled
									.getScheduled();
							for (HourlyValue hourlyValue : hourlyValues) {
								double megawatts = 0;
								if (hourlyValue.getEnergyMW() != null) {
									megawatts = hourlyValue.getEnergyMW()
											.doubleValue();
								}

								CommonAggregateGeneration commonAggregateGeneration = new CommonAggregateGeneration(
										commonFuelType,
										startOfForecastedDateTime
												.plusHours(
														hourlyValue
																.getDeliveryHour() - 1)
												.toDate(),
										DataPointType.FORECAST, megawatts);

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
}
