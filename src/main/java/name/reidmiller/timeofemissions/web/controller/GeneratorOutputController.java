package name.reidmiller.timeofemissions.web.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import name.reidmiller.iesoreports.IesoPublicReportBindingsConfig;
import name.reidmiller.iesoreports.client.GeneratorOutputCapabilityClient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.ieso.reports.schema.genoutputcapability.IMODocBody;
import ca.ieso.reports.schema.genoutputcapability.IMODocBody.Generators.Generator;
import ca.ieso.reports.schema.genoutputcapability.IMODocBody.Generators.Generator.Outputs.Output;

import com.google.gson.Gson;

@Controller
public class GeneratorOutputController {
	SimpleDateFormat genOutTimestampFormat = new SimpleDateFormat(
			"yyyy-MM-dd H:mm:ss");
	Logger logger = LogManager.getLogger(this.getClass());
	Gson gson = new Gson();

	@RequestMapping("/generator_output")
	public String generatorOutput(
			@RequestParam(value = "start", required = false) String start,
			@RequestParam(value = "end", required = false) String end,
			Model model) {
		GeneratorOutputCapabilityClient genOutCapClient = IesoPublicReportBindingsConfig
				.generatorOutputCapabilityClient();
		Calendar yestCal = Calendar.getInstance();
		yestCal.roll(Calendar.DATE, false);

		IMODocBody imoDocBody = genOutCapClient.getIMODocBody();
		List<Generator> generators = imoDocBody.getGenerators().getGenerator();

		model.addAttribute("reportDate", new Date());

		List<String> individualLabels = new ArrayList<String>();
		individualLabels.add("Date");
		String[] aggregateLabels = new String[] { "Date", "Nuclear", "Coal",
				"Other", "Gas", "Hydro", "Wind" };

		List<String> individualColors = new ArrayList<String>();
		String[] aggregateColors = new String[] { "orange", "black", "green",
				"brown", "blue", "yellow" };

		List<List<Object>> individualData = new ArrayList<List<Object>>();
		List<List<Object>> aggregateData = new ArrayList<List<Object>>();

		LinkedHashMap<String, HashMap<String, Float>> fuelTypeMap = new LinkedHashMap<String, HashMap<String, Float>>();
		fuelTypeMap.put("NUCLEAR", new HashMap<String, Float>());
		fuelTypeMap.put("COAL", new HashMap<String, Float>());
		fuelTypeMap.put("OTHER", new HashMap<String, Float>());
		fuelTypeMap.put("GAS", new HashMap<String, Float>());
		fuelTypeMap.put("HYDRO", new HashMap<String, Float>());
		fuelTypeMap.put("WIND", new HashMap<String, Float>());

		List<String> timeStrings = new ArrayList<String>();

		try {
			for (Generator generator : generators) {
				if (generator.getFuelType().equals("NUCLEAR")) {
					individualColors.add("orange");
				} else if (generator.getFuelType().equals("COAL")) {
					individualColors.add("black");
				} else if (generator.getFuelType().equals("OTHER")) {
					individualColors.add("green");
				} else if (generator.getFuelType().equals("GAS")) {
					individualColors.add("brown");
				} else if (generator.getFuelType().equals("HYDRO")) {
					individualColors.add("blue");
				} else if (generator.getFuelType().equals("WIND")) {
					individualColors.add("yellow");
				}

				individualLabels.add(generator.getGeneratorName());

				int i = 0;
				for (Output hourlyOutput : generator.getOutputs().getOutput()) {
					int clockHour = hourlyOutput.getHour() - 1;
					String timeString = imoDocBody.getDate().toString() + " "
							+ clockHour + ":00:00";

					if (!timeStrings.contains(timeString)) {
						timeStrings.add(timeString);
					}

					if (individualData.size() > i) {
						List<Object> xVals = individualData.get(i);
						xVals.add(hourlyOutput.getEnergyMW());
						individualData.set(i, xVals);
					} else {
						List<Object> xVals = new ArrayList<Object>();
						xVals.add(genOutTimestampFormat.parse(timeString));
						xVals.add(hourlyOutput.getEnergyMW());
						individualData.add(xVals);
					}

					HashMap<String, Float> fuelHourMap = fuelTypeMap
							.get(generator.getFuelType());
					if (fuelHourMap.keySet().contains(timeString)) {
						float fuelHourVal = fuelHourMap.get(timeString);
						logger.debug("generatorName="+generator.getGeneratorName());
						logger.debug("fuelHourVal="+fuelHourVal);
						logger.debug("hourlyOutput="+hourlyOutput);
						logger.debug("energyMW="+hourlyOutput.getEnergyMW());
						if (hourlyOutput.getEnergyMW() != null) {
							fuelHourVal = fuelHourVal + hourlyOutput.getEnergyMW();
						}
						fuelHourMap.put(timeString, fuelHourVal);
						fuelTypeMap.put(generator.getFuelType(), fuelHourMap);
					} else {
						fuelHourMap.put(timeString, hourlyOutput.getEnergyMW());
						fuelTypeMap.put(generator.getFuelType(), fuelHourMap);
					}

					i++;
				}
			}
		} catch (ParseException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
		}

		for (String timeString : timeStrings) {
			try {
				List<Object> xVals = new ArrayList<Object>();
				xVals.add(genOutTimestampFormat.parseObject(timeString));
				xVals.add(fuelTypeMap.get("NUCLEAR").get(timeString));
				xVals.add(fuelTypeMap.get("COAL").get(timeString));
				xVals.add(fuelTypeMap.get("OTHER").get(timeString));
				xVals.add(fuelTypeMap.get("GAS").get(timeString));
				xVals.add(fuelTypeMap.get("HYDRO").get(timeString));
				xVals.add(fuelTypeMap.get("WIND").get(timeString));
				aggregateData.add(xVals);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		model.addAttribute("aggregateLabels", gson.toJson(aggregateLabels));
		model.addAttribute("aggregateData", gson.toJson(aggregateData));
		model.addAttribute("aggregateColors", gson.toJson(aggregateColors));

		model.addAttribute("individualLabels", gson.toJson(individualLabels));
		model.addAttribute("individualData", gson.toJson(individualData));
		model.addAttribute("individualColors", gson.toJson(individualColors));

		return "generator_output";
	}
}
