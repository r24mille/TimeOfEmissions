package name.reidmiller.timeofemissions.model;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import name.reidmiller.iesoreports.IesoPublicReportBindingsConfig;
import name.reidmiller.iesoreports.client.AdequacyClient;
import name.reidmiller.iesoreports.client.GeneratorOutputCapabilityClient;
import name.reidmiller.iesoreports.client.GeneratorOutputCapabilityClient.FuelType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import ca.ieso.reports.schema.adequacy.DocBody;
import ca.ieso.reports.schema.adequacy.DocBody.System;
import ca.ieso.reports.schema.adequacy.DocBody.System.InternalResources;
import ca.ieso.reports.schema.adequacy.DocBody.System.InternalResources.InternalResource;
import ca.ieso.reports.schema.adequacy.DocBody.System.InternalResources.InternalResource.FuelScheduled;
import ca.ieso.reports.schema.adequacy.HourlyValue;
import ca.ieso.reports.schema.genoutputcapability.IMODocBody;

public class CommonAggregateGenerationMix {
	private Logger logger = LogManager.getLogger(this.getClass());

	private Iso iso;
	private HashMap<CommonFuelType, List<CommonAggregateGeneration>> commonAggregateGenerationObservations;
	private HashMap<CommonFuelType, List<CommonAggregateGeneration>> commonAggregateGenerationForecast;

	public CommonAggregateGenerationMix(Iso iso) {
		this.setIso(iso);

		switch (this.iso) {
		case IESO:
			this.setCommonAggregateGenerationObservations(this
					.populateDefaultIesoAggregateGenerationMix());
			this.setCommonAggregateGenerationForecast(this
					.populateDefaultIesoAggregateDayAheadForecastMix());
		default:
			logger.error("ISOs other than IESO have not been implemented yet.");
			break;
		}
	}

	/**
	 * Gets the default (day-ahead) aggregate generation forecast by fuel type
	 * from the IESO's public reports. For hour's in the current day which have
	 * not been reached, the prior day's day-ahead forecast is used as forecast
	 * values.
	 */
	public HashMap<CommonFuelType, List<CommonAggregateGeneration>> populateDefaultIesoAggregateDayAheadForecastMix() {
		HashMap<CommonFuelType, List<CommonAggregateGeneration>> commonAggregateGenerationForecast = new HashMap<CommonFuelType, List<CommonAggregateGeneration>>();
		AdequacyClient adequacyClient = IesoPublicReportBindingsConfig
				.adequacyClient();
		try {
			DateTime today = DateTime.now().withTimeAtStartOfDay();
			DateTime yesterday = today.minusDays(1);
			List<DocBody> docBodies = adequacyClient.getDocBodiesInDateRange(
					yesterday.toDate(), today.toDate());
			int docBodyIndex = 0;
			for (DocBody docBody : docBodies) {
				// Because XML client is returning null deliveryDate, assume
				// date from ordering of docBodies
				DateTime deliveryDateTime;
				if (docBodyIndex == 0) {
					deliveryDateTime = DateTime.now().withTimeAtStartOfDay();
				} else {
					deliveryDateTime = today.plusDays(1); // tomorrow
				}

				// Because XML client is returning null systemName, assume
				// "Ontario" system from ordering of systems
				List<System> systems = docBody.getSystem();
				System ontarioSystem = systems.get(0);

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
							if (docBodyIndex == 0) {
								commonAggregateGenerationForecast
										.put(commonFuelType,
												new ArrayList<CommonAggregateGeneration>());
							}

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
											deliveryDateTime
													.plusHours(
															hourlyValue
																	.getDeliveryHour() - 1)
													.toDate(),
											DataPointType.FORECAST,
											megawatts);

									commonAggregateGenerationForecast.get(
											commonFuelType).add(
											commonAggregateGeneration);
								}
							}

							internalResourceIndex++;
						}

					}
				}

				docBodyIndex++;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return commonAggregateGenerationForecast;
	}

	/**
	 * Gets the default (current day) aggregate generation by fuel type from the
	 * IESO's public reports.
	 * 
	 * @return
	 */
	public HashMap<CommonFuelType, List<CommonAggregateGeneration>> populateDefaultIesoAggregateGenerationMix() {
		DateTimeFormatter genOutTimestampFormatter = DateTimeFormat
				.forPattern("yyyy-MM-dd H:mm:ss Z");
		GeneratorOutputCapabilityClient genOutputCapClient = IesoPublicReportBindingsConfig
				.generatorOutputCapabilityClient();
		HashMap<CommonFuelType, List<CommonAggregateGeneration>> commonAggregateGenerationObservations = new HashMap<CommonFuelType, List<CommonAggregateGeneration>>();

		try {
			IMODocBody imoDocBody = genOutputCapClient.getDefaultIMODocBody();
			HashMap<FuelType, LinkedHashMap<String, Float>> fuelTypeTotals = genOutputCapClient
					.getHourlyFuelTypeTotals(imoDocBody);

			// IESO client provides totals by fuel type as a Map
			Set<FuelType> fuelTypeKeys = fuelTypeTotals.keySet();
			for (FuelType fuelType : fuelTypeKeys) {
				// Convert fuelType key to CommonFuelType
				CommonFuelType commonFuelType = CommonFuelType
						.valueOfFuelType(fuelType);
				commonAggregateGenerationObservations.put(commonFuelType,
						new ArrayList<CommonAggregateGeneration>());
				LinkedHashMap<String, Float> fuelTypeTimeseries = fuelTypeTotals
						.get(fuelType);
				Set<String> timestampKeys = fuelTypeTimeseries.keySet();

				// Parse timestamp Strings and create CommonAggregateGeneration
				// objects for CommonAggregateGeneration Map
				for (String timestamp : timestampKeys) {
					Float entryMegawatts = fuelTypeTimeseries.get(timestamp);
					CommonAggregateGeneration commonAggregateGeneration = new CommonAggregateGeneration(
							commonFuelType, genOutTimestampFormatter
									.parseDateTime(timestamp + " -0500")
									.toDate(), DataPointType.OBSERVED,
							entryMegawatts);
					commonAggregateGenerationObservations.get(commonFuelType)
							.add(commonAggregateGeneration);
				}
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return commonAggregateGenerationObservations;
	}

	public Iso getIso() {
		return iso;
	}

	public void setIso(Iso iso) {
		this.iso = iso;
	}

	public HashMap<CommonFuelType, List<CommonAggregateGeneration>> getCommonAggregateGenerationObservations() {
		return commonAggregateGenerationObservations;
	}

	public void setCommonAggregateGenerationObservations(
			HashMap<CommonFuelType, List<CommonAggregateGeneration>> commonAggregateGenerationObservations) {
		this.commonAggregateGenerationObservations = commonAggregateGenerationObservations;
	}

	public HashMap<CommonFuelType, List<CommonAggregateGeneration>> getCommonAggregateGenerationForecast() {
		return commonAggregateGenerationForecast;
	}

	public void setCommonAggregateGenerationForecast(
			HashMap<CommonFuelType, List<CommonAggregateGeneration>> commonAggregateGenerationForecast) {
		this.commonAggregateGenerationForecast = commonAggregateGenerationForecast;
	}

}
