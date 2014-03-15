package name.reidmiller.timeofemissions.web.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import name.reidmiller.iesoreports.IesoPublicReportBindingsConfig;
import name.reidmiller.iesoreports.client.SurplusBaseloadGenerationClient;
import name.reidmiller.timeofemissions.model.CommonAggregateGenerationMix;
import name.reidmiller.timeofemissions.model.Iso;
import name.reidmiller.timeofemissions.model.SbgImpactDayAheadForecast;
import name.reidmiller.timeofemissions.web.command.GeneratorOutputCommand;
import name.reidmiller.timeofemissions.web.command.SurplusBaseloadGenerationCommand;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import ca.ieso.reports.schema.sbg.DocHeader;
import ca.ieso.reports.schema.sbg.Document.DocBody;
import ca.ieso.reports.schema.sbg.Document.DocBody.DailyForecast;
import ca.ieso.reports.schema.sbg.Document.DocBody.DailyForecast.HourlyForecast;

import com.google.gson.Gson;

@Controller
public class SurplusBaseloadGenerationController {
	SimpleDateFormat jqueryTimeFormat = new SimpleDateFormat("MM/dd/yyyy");
	SimpleDateFormat sbgTimestampFormat = new SimpleDateFormat(
			"yyyy-MM-dd H:mm:ss");
	Logger logger = LogManager.getLogger(this.getClass());
	Gson gson = new Gson();
	
	@RequestMapping("/sbg")
	public String generatorOutput(
			@ModelAttribute GeneratorOutputCommand command, Model model) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd H:mm:ss");
		DateTime forecastedDateTime = formatter.parseDateTime("2014-03-13 0:00:00");
		SbgImpactDayAheadForecast sbgImpactDayAheadForecast = new SbgImpactDayAheadForecast(Iso.IESO, forecastedDateTime);
		
		model.addAttribute("generation", gson.toJson(sbgImpactDayAheadForecast.getCommonAggregateGenerationForecast()));
		model.addAttribute("oversupply", gson.toJson(sbgImpactDayAheadForecast.getCommonOversupplyForecast()));
		model.addAttribute("command", command);
		return "sbg";
	}

	@RequestMapping("/sbg_old")
	public String sbg(
			@ModelAttribute SurplusBaseloadGenerationCommand command,
			Model model) {
		SurplusBaseloadGenerationClient sbgClient = IesoPublicReportBindingsConfig
				.surplusBaseloadGenerationClient();

		List<String> labels = new ArrayList<String>();
		List<Object[]> data = new ArrayList<Object[]>();
		try {
			DocBody docBody = null;
			DocHeader docHeader = null;
			if (command.getStartDateString() != null && !command.getStartDateString().isEmpty()) {
				Date startDate = jqueryTimeFormat.parse(command.getStartDateString());
				docBody = sbgClient.getDocBodyForDate(startDate);
				docHeader = sbgClient.getDocHeaderForDate(startDate);
			} else {
				docBody = sbgClient.getDefaultDocBody();
				docHeader = sbgClient.getDefaultDocHeader();
			}
			List<DailyForecast> dailyForecasts = docBody.getDailyForecast();
			model.addAttribute("reportDate", docHeader.getCreatedAt().toString());

			labels.add("Date");
			labels.add("Megawatts (MW)");
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
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.debug("labels=" + gson.toJson(labels));
		logger.debug("data=" + gson.toJson(data));

		model.addAttribute("command", command);
		model.addAttribute("labels", gson.toJson(labels));
		model.addAttribute("data", gson.toJson(data));
		return "sbg";
	}
}
