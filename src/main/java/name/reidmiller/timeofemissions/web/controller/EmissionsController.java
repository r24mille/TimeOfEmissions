package name.reidmiller.timeofemissions.web.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import name.reidmiller.iesoreports.IesoPublicReportBindingsConfig;
import name.reidmiller.iesoreports.client.GeneratorOutputCapabilityClient;
import name.reidmiller.iesoreports.client.GeneratorOutputCapabilityClient.FuelType;
import name.reidmiller.timeofemissions.model.FuelTypeMetadata;
import name.reidmiller.timeofemissions.web.command.EmissionsCommand;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import ca.ieso.reports.schema.genoutputcapability.IMODocBody;
import ca.ieso.reports.schema.genoutputcapability.IMODocBody.Generators.Generator;
import ca.ieso.reports.schema.genoutputcapability.IMODocBody.Generators.Generator.Outputs.Output;

import com.google.gson.Gson;

@Controller
public class EmissionsController {
	SimpleDateFormat jqueryTimeFormat = new SimpleDateFormat("MM/dd/yyyy");
	SimpleDateFormat genOutTimestampFormat = new SimpleDateFormat(
			"yyyy-MM-dd H:mm:ss");
	Logger logger = LogManager.getLogger(this.getClass());
	Gson gson = new Gson();

	@RequestMapping("/emissions")
	public String emissions(@ModelAttribute EmissionsCommand command,
			Model model) {
		GeneratorOutputCapabilityClient genOutCapClient = IesoPublicReportBindingsConfig
				.generatorOutputCapabilityClient();
		Calendar yestCal = Calendar.getInstance();
		yestCal.roll(Calendar.DATE, false);

		try {
			// Set up labels, colors, and data for output by individual
			// generator graph
			List<String> individualLabels = new ArrayList<String>();
			individualLabels.add("Date");
			List<String> individualColors = new ArrayList<String>();
			List<List<Object>> individualData = new ArrayList<List<Object>>();

			// Set up labels, colors, and data for aggregate output by fuel type
			// graph
			String[] aggregateLabels = new String[] { "Date", "Coal",
					"Natural Gas", "Wind", "Hydro", "Other", "Nuclear" };
			String[] aggregateColors = new String[] {
					FuelTypeMetadata.COAL_GRAPH_COLOR,
					FuelTypeMetadata.NATURAL_GAS_GRAPH_COLOR,
					FuelTypeMetadata.WIND_GRAPH_COLOR,
					FuelTypeMetadata.HYDROELECTRIC_GRAPH_COLOR,
					FuelTypeMetadata.BIOMASS_GRAPH_COLOR,
					FuelTypeMetadata.NUCLEAR_GRAPH_COLOR };
			List<List<Object>> aggregateData = new ArrayList<List<Object>>();

			// Get IMODocBodies for date range or for current day
			List<IMODocBody> imoDocBodies = null;
			if (command.getStartDateString() != null
					&& !command.getStartDateString().isEmpty()
					&& command.getEndDateString() != null
					&& !command.getEndDateString().isEmpty()) {
				Date startDate = jqueryTimeFormat.parse(command
						.getStartDateString());
				Date endDate = jqueryTimeFormat.parse(command
						.getEndDateString());
				imoDocBodies = genOutCapClient.getIMODocBodiesInDateRange(
						startDate, endDate);
			} else {
				IMODocBody imoDocBody = genOutCapClient.getDefaultIMODocBody();
				imoDocBodies = new ArrayList<IMODocBody>(1);
				imoDocBodies.add(imoDocBody);
			}

			// GeneratorOutputCapabilityClient creates a summary by fuel type
			HashMap<FuelType, LinkedHashMap<String, Float>> fuelTypeMap = genOutCapClient
					.getHourlyFuelTypeTotals(imoDocBodies);

			// Populate time strings used by both individual and aggregate
			// graphs
			LinkedHashMap<String, LinkedHashMap<String, Output>> individualGeneratorOutputs = new LinkedHashMap<String, LinkedHashMap<String, Output>>();
			HashMap<String, FuelType> generatorFuelTypes = new HashMap<String, FuelType>();
			List<String> timeStrings = new ArrayList<String>();
			for (IMODocBody imoDocBody : imoDocBodies) {
				for (Generator generator : imoDocBody.getGenerators()
						.getGenerator()) {
					for (Output output : generator.getOutputs().getOutput()) {
						int clockHour = output.getHour() - 1;
						String timeString = imoDocBody.getDate().toString()
								+ " " + clockHour + ":00:00";

						if (!timeStrings.contains(timeString)) {
							timeStrings.add(timeString);
						}

						// Create an individual generator output map keyed by
						// generator name
						if (individualGeneratorOutputs.containsKey(generator
								.getGeneratorName())) {
							LinkedHashMap<String, Output> timestampedOutputs = individualGeneratorOutputs
									.get(generator.getGeneratorName());
							timestampedOutputs.put(timeString, output);
							individualGeneratorOutputs.put(
									generator.getGeneratorName(),
									timestampedOutputs);
						} else {
							LinkedHashMap<String, Output> timestampedOutputs = new LinkedHashMap<String, Output>();
							timestampedOutputs.put(timeString, output);
							individualGeneratorOutputs.put(
									generator.getGeneratorName(),
									timestampedOutputs);
						}
					}

					// While iterating through generators, also create generator
					// fuel type map
					if (!generatorFuelTypes.containsKey(generator
							.getGeneratorName())) {
						generatorFuelTypes.put(generator.getGeneratorName(),
								FuelType.valueOf(generator.getFuelType()));
					}
				}
			}

			Set<String> generatorSet = individualGeneratorOutputs.keySet();
			for (String generatorName : generatorSet) {
				if (generatorFuelTypes.get(generatorName).equals(
						FuelType.NUCLEAR)) {
					individualColors.add(FuelTypeMetadata.NUCLEAR_GRAPH_COLOR);
				} else if (generatorFuelTypes.get(generatorName).equals(
						FuelType.COAL)) {
					individualColors.add(FuelTypeMetadata.COAL_GRAPH_COLOR);
				} else if (generatorFuelTypes.get(generatorName).equals(
						FuelType.OTHER)) {
					individualColors.add(FuelTypeMetadata.BIOMASS_GRAPH_COLOR);
				} else if (generatorFuelTypes.get(generatorName).equals(
						FuelType.GAS)) {
					individualColors
							.add(FuelTypeMetadata.NATURAL_GAS_GRAPH_COLOR);
				} else if (generatorFuelTypes.get(generatorName).equals(
						FuelType.HYDRO)) {
					individualColors
							.add(FuelTypeMetadata.HYDROELECTRIC_GRAPH_COLOR);
				} else if (generatorFuelTypes.get(generatorName).equals(
						FuelType.WIND)) {
					individualColors.add(FuelTypeMetadata.WIND_GRAPH_COLOR);
				}

				individualLabels.add(generatorName);

				// TODO Fix up, this is kind of shiesty, trusting that all
				// generators will have all entries
				int i = 0;
				LinkedHashMap<String, Output> generatorTimeseriesMap = individualGeneratorOutputs
						.get(generatorName);
				for (Entry<String, Output> timeseriesEntry : generatorTimeseriesMap
						.entrySet()) {
					if (individualData.size() > i) {
						List<Object> xVals = individualData.get(i);
						xVals.add(timeseriesEntry.getValue().getEnergyMW());
						individualData.set(i, xVals);
					} else {
						List<Object> xVals = new ArrayList<Object>();
						xVals.add(genOutTimestampFormat.parse(timeseriesEntry
								.getKey()));
						xVals.add(timeseriesEntry.getValue().getEnergyMW());
						individualData.add(xVals);
					}

					i++;
				}
			}

			for (String timeString : timeStrings) {
				try {
					List<Object> xVals = new ArrayList<Object>();
					xVals.add(genOutTimestampFormat.parseObject(timeString));
					xVals.add(fuelTypeMap.get(FuelType.COAL).get(timeString) * 1.001);
					xVals.add(fuelTypeMap.get(FuelType.GAS).get(timeString) * 0.469);
					xVals.add(fuelTypeMap.get(FuelType.WIND).get(timeString) * 0.012);
					xVals.add(fuelTypeMap.get(FuelType.HYDRO).get(timeString) * 0.004);
					xVals.add(fuelTypeMap.get(FuelType.OTHER).get(timeString) * 0.018);
					xVals.add(fuelTypeMap.get(FuelType.NUCLEAR).get(timeString) * 0.016);
					aggregateData.add(xVals);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			model.addAttribute("reportDate", imoDocBodies.get(0).getDate()); // TODO
																				// wrong
			model.addAttribute("aggregateLabels", gson.toJson(aggregateLabels));
			model.addAttribute("aggregateData", gson.toJson(aggregateData));
			model.addAttribute("aggregateColors", gson.toJson(aggregateColors));

			model.addAttribute("individualLabels",
					gson.toJson(individualLabels));
			model.addAttribute("individualData", gson.toJson(individualData));
			model.addAttribute("individualColors",
					gson.toJson(individualColors));
		} catch (ParseException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		model.addAttribute("command", command);
		return "emissions";
	}
}
