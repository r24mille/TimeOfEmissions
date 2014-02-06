package name.reidmiller.timeofemissions.web.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import name.reidmiller.iesoreports.IesoPublicReportBindingsConfig;
import name.reidmiller.iesoreports.client.SurplusBaseloadGenerationClient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.ieso.reports.schema.sbg.Document.DocBody.DailyForecast;
import ca.ieso.reports.schema.sbg.Document.DocBody.DailyForecast.HourlyForecast;

import com.google.gson.Gson;

@Controller
public class SurplusBaseloadGenerationController {
	SimpleDateFormat sbgTimestampFormat = new SimpleDateFormat(
			"yyyy-MM-dd H:mm:ss");
	Logger logger = LogManager.getLogger(this.getClass());
	Gson gson = new Gson();

	@RequestMapping("/sbg")
	public String sbg(
			@RequestParam(value = "start", required = false) String start,
			@RequestParam(value = "end", required = false) String end,
			Model model) {
		SurplusBaseloadGenerationClient sbgClient = IesoPublicReportBindingsConfig
				.surplusBaseloadGenerationClient();
		Calendar yestCal = Calendar.getInstance();
		
		// TODO Implement dates back to 20130103
		sbgClient.setUrlDate(yestCal.getTime());
		
		List<DailyForecast> dailyForecasts = sbgClient.getDocBody()
				.getDailyForecast();
		model.addAttribute("reportDate", sbgClient.getUrlDate());

		List<String> labels = new ArrayList<String>();
		labels.add("Date");
		labels.add("Megawatts (MW)");
		List<Object[]> data = new ArrayList<Object[]>();
		try {
			for (DailyForecast dailyForecast : dailyForecasts) {
				for (HourlyForecast hourlyForecast : dailyForecast
						.getHourlyForecast()) {
					int clockHour = hourlyForecast.getHour() - 1;
					String timeString = dailyForecast.getDateForecast()
							.toString() + " " + clockHour + ":00:00";

					data.add(new Object[] {
							sbgTimestampFormat.parse(timeString),
							hourlyForecast.getEnergyMW() });

				}
			}
		} catch (ParseException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
		}

		logger.debug("labels=" + gson.toJson(labels));
		logger.debug("data=" + gson.toJson(data));

		model.addAttribute("labels", gson.toJson(labels));
		model.addAttribute("data", gson.toJson(data));
		return "sbg";
	}
}
