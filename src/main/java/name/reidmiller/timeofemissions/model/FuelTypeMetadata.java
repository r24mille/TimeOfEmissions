package name.reidmiller.timeofemissions.model;

import name.reidmiller.iesoreports.client.GeneratorOutputCapabilityClient.FuelType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FuelTypeMetadata {
	private Logger logger = LogManager.getLogger(this.getClass());

	public static final double HYDROELECTRIC_GRAMS_CO2_PER_KWHE = 4;
	public static final double WIND_GRAMS_CO2_PER_KWHE = 12;
	public static final double NUCLEAR_GRAMS_CO2_PER_KWHE = 16;
	public static final double BIOMASS_GRAMS_CO2_PER_KWHE = 18;
	public static final double SOLAR_THERMAL_GRAMS_CO2_PER_KWHE = 22;
	public static final double GEOTHERMAL_GRAMS_CO2_PER_KWHE = 45;
	public static final double SOLAR_PV_GRAMS_CO2_PER_KWHE = 46;
	public static final double NATURAL_GAS_GRAMS_CO2_PER_KWHE = 469;
	public static final double COAL_GRAMS_CO2_PER_KWHE = 1001;

	public static final String HYDROELECTRIC_GRAPH_COLOR = "#204C79";
	public static final String WIND_GRAPH_COLOR = "#79D24C";
	public static final String NUCLEAR_GRAPH_COLOR = "#F9A848";
	public static final String BIOMASS_GRAPH_COLOR = "#999966";
	public static final String NATURAL_GAS_GRAPH_COLOR = "#A52A2A";
	public static final String COAL_GRAPH_COLOR = "#000000";

	private double gramsCarbonDioxidePerKWHe;
	private String graphColor;

	public FuelTypeMetadata(FuelType fuelType) {
		switch (fuelType) {
		case COAL:
			this.gramsCarbonDioxidePerKWHe = COAL_GRAMS_CO2_PER_KWHE;
			this.graphColor = COAL_GRAPH_COLOR;
			break;
		case HYDRO:
			this.gramsCarbonDioxidePerKWHe = HYDROELECTRIC_GRAMS_CO2_PER_KWHE;
			this.graphColor = HYDROELECTRIC_GRAPH_COLOR;
			break;
		case GAS:
			this.gramsCarbonDioxidePerKWHe = NATURAL_GAS_GRAMS_CO2_PER_KWHE;
			this.graphColor = NATURAL_GAS_GRAPH_COLOR;
			break;
		case NUCLEAR:
			this.gramsCarbonDioxidePerKWHe = NUCLEAR_GRAMS_CO2_PER_KWHE;
			this.graphColor = NUCLEAR_GRAPH_COLOR;
			break;
		case OTHER:
			this.gramsCarbonDioxidePerKWHe = BIOMASS_GRAMS_CO2_PER_KWHE;
			this.graphColor = BIOMASS_GRAPH_COLOR;
			break;
		case WIND:
			this.gramsCarbonDioxidePerKWHe = WIND_GRAMS_CO2_PER_KWHE;
			this.graphColor = WIND_GRAPH_COLOR;
			break;
		default:
			logger.warn("default case statement reached, which should not happen. fuelType variable is "
					+ fuelType);
			break;
		}
	}

	public String getGraphColor() {
		return graphColor;
	}

	public double getGramsCarbonDioxidePerKWHe() {
		return this.gramsCarbonDioxidePerKWHe;
	}

	public double getGramsCarbonDioxideForKWHeValue(double kilowattHours) {
		return (this.getGramsCarbonDioxidePerKWHe() * kilowattHours);
	}

	public double getKilogramsCarbonDioxidePerKWHe() {
		return (this.gramsCarbonDioxidePerKWHe / 1000);
	}

	public double getKilogramsCarbonDioxideForKWHeValue(double kilowattHours) {
		return (this.getKilogramsCarbonDioxidePerKWHe() * kilowattHours);
	}

	public double getKilogramsCarbonDioxidePerMWHe() {
		return this.gramsCarbonDioxidePerKWHe;
	}

	public double getKilogramsCarbonDioxideForMWHeValue(double megawattHours) {
		return (this.getKilogramsCarbonDioxidePerMWHe() * megawattHours);
	}

	public double getMetricTonnesCarbonDioxidePerMWHe() {
		return (this.getKilogramsCarbonDioxidePerMWHe() / 1000);
	}

	public double getMetricTonnesCarbonDioxideForMWHeValue(double megawattHours) {
		return (this.getMetricTonnesCarbonDioxidePerMWHe() * megawattHours);
	}
}
