package name.reidmiller.timeofemissions.web.controller;

import java.text.SimpleDateFormat;

import name.reidmiller.timeofemissions.model.CommonAggregateGenerationMix;
import name.reidmiller.timeofemissions.model.Iso;
import name.reidmiller.timeofemissions.web.command.GeneratorOutputCommand;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;

@Controller
public class GeneratorOutputController {
	SimpleDateFormat jqueryTimeFormat = new SimpleDateFormat("MM/dd/yyyy");
	SimpleDateFormat genOutTimestampFormat = new SimpleDateFormat(
			"yyyy-MM-dd H:mm:ss");
	Logger logger = LogManager.getLogger(this.getClass());
	Gson gson = new Gson();

	@RequestMapping("/generator_output")
	public String generatorOutput(
			@ModelAttribute GeneratorOutputCommand command, Model model) {
		CommonAggregateGenerationMix commonAggregateGenerationMix = new CommonAggregateGenerationMix(Iso.IESO);
		
		model.addAttribute("forecast", gson.toJson(commonAggregateGenerationMix.getCommonAggregateGenerationForecast()));
		model.addAttribute("observations", gson.toJson(commonAggregateGenerationMix.getCommonAggregateGenerationObservations()));
		model.addAttribute("command", command);
		return "generator_output";
	}
}
