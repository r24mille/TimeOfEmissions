package name.reidmiller.timeofemissions.web.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import name.reidmiller.iesoreports.IesoPublicReportBindingsConfig;
import name.reidmiller.iesoreports.client.SurplusBaseloadGenerationClient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;

import ca.ieso.reports.schema.sbg.Document.DocBody.DailyForecast;
import ca.ieso.reports.schema.sbg.Document.DocBody.DailyForecast.HourlyForecast;

@Controller
public class SurplusBaseloadGenerationController {
	Logger logger = LogManager.getLogger(this.getClass());
	Gson gson = new Gson();

	@RequestMapping("/sbg")
	public String sbg(
			@RequestParam(value = "start", required = false) String start,
			@RequestParam(value = "end", required = false) String end,
			Model model) {
		SurplusBaseloadGenerationClient sbgClient = IesoPublicReportBindingsConfig
				.surplusBaseloadGenerationClient();
		List<DailyForecast> dailyForecasts = sbgClient.getDocBody()
				.getDailyForecast();

		int i = 0;
		List<Integer> labels = new ArrayList<Integer>();
		List<BigDecimal> data = new ArrayList<BigDecimal>();
		for (DailyForecast dailyForecast : dailyForecasts) {
			for (HourlyForecast hourlyForecast : dailyForecast.getHourlyForecast()) {
				labels.add(i);
				i++;
				data.add(hourlyForecast.getEnergyMW());
			}
		}

		logger.debug("labels=" + gson.toJson(labels));
		logger.debug("data=" + gson.toJson(data));
		
		model.addAttribute("labels", gson.toJson(labels));
		model.addAttribute("data", gson.toJson(data));
		return "sbg";
	}

}
