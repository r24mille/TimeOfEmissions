package name.reidmiller.timeofemissions.model.test;

import static org.junit.Assert.assertEquals;
import name.reidmiller.aesoreports.model.GeneratorType;
import name.reidmiller.iesoreports.client.GeneratorOutputCapabilityClient.FuelType;
import name.reidmiller.timeofemissions.model.CommonFuelType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.misoenergy.ria.binding.FuelCategory;

public class CommonFuelTypeTest {
	private Logger logger = LogManager.getLogger(this.getClass());

	@Test
	public void testValueOfFuelType() {
		CommonFuelType commonFuelType = CommonFuelType
				.valueOfFuelType(FuelType.COAL);
		assertEquals("FuelType not parsed into CommonFuelType properly",
				CommonFuelType.COAL, commonFuelType);
	}

	@Test
	public void testValueOfFuelCategory() {
		CommonFuelType commonFuelType = CommonFuelType
				.valueOfFuelCategory(FuelCategory.COAL);
		assertEquals("FuelCategory not parsed into CommonFuelType properly",
				CommonFuelType.COAL, commonFuelType);
	}

	@Test
	public void testValueOfGeneratorType() {
		CommonFuelType commonFuelType = CommonFuelType
				.valueOfGeneratorType(GeneratorType.COAL);
		assertEquals("GeneratorType not parsed into CommonFuelType properly",
				CommonFuelType.COAL, commonFuelType);
	}

	@Test
	public void testGetGraphColor() {
		CommonFuelType commonFuelType = CommonFuelType.COAL;
		assertEquals(commonFuelType + " graph color not set properly.",
				"#000000", commonFuelType.getGraphColor());
	}

	@Test
	public void testGetGramsCarbonDioxidePerKWHe() {
		CommonFuelType commonFuelType = CommonFuelType.COAL;
		assertEquals(commonFuelType
				+ " grams CO2 per kWhe ratio not set properly.", 1001,
				commonFuelType.getGramsCarbonDioxidePerKWHe(), 0);
	}

	@Test
	public void testGetGramsCarbonDioxideForKWHeValue() {
		CommonFuelType commonFuelType = CommonFuelType.COAL;
		assertEquals(
				commonFuelType
						+ " emissions for grams CO2 / kWhe value did not evaluate properly.",
				100100, commonFuelType.getGramsCarbonDioxideForKWHeValue(100),
				0);
	}

	@Test
	public void testGetKilogramsCarbonDioxidePerKWHe() {
		CommonFuelType commonFuelType = CommonFuelType.COAL;
		assertEquals(commonFuelType
				+ " kg CO2 per kWhe ratio not set properly.", 1.001,
				commonFuelType.getKilogramsCarbonDioxidePerKWHe(), 0);
	}

	@Test
	public void testGetKilogramsCarbonDioxideForKWHeValue() {
		CommonFuelType commonFuelType = CommonFuelType.COAL;
		assertEquals(
				commonFuelType
						+ " emissions for kg CO2 / kWhe value did not evaluate properly.",
				100.1,
				commonFuelType.getKilogramsCarbonDioxideForKWHeValue(100), 0);
	}

	@Test
	public void testGetKilogramsCarbonDioxidePerMWHe() {
		CommonFuelType commonFuelType = CommonFuelType.COAL;
		assertEquals(commonFuelType
				+ " kg CO2 per MWhe ratio not set properly.", 1001,
				commonFuelType.getKilogramsCarbonDioxidePerMWHe(), 0);
	}

	@Test
	public void testGetKilogramsCarbonDioxideForMWHeValue() {
		CommonFuelType commonFuelType = CommonFuelType.COAL;
		assertEquals(
				commonFuelType
						+ " emissions for kg CO2 / MWhe value did not evaluate properly.",
				100100,
				commonFuelType.getKilogramsCarbonDioxideForMWHeValue(100), 0);
	}

	@Test
	public void testGetMetricTonnesCarbonDioxidePerMWHe() {
		CommonFuelType commonFuelType = CommonFuelType.COAL;
		assertEquals(commonFuelType
				+ " tonnes CO2 per MWhe ratio not set properly.", 1.001,
				commonFuelType.getMetricTonnesCarbonDioxidePerMWHe(), 0);
	}

	@Test
	public void testGetMetricTonnesCarbonDioxideForMWHeValue() {
		CommonFuelType commonFuelType = CommonFuelType.COAL;
		assertEquals(
				commonFuelType
						+ " emissions for tonnes CO2 / MWhe value did not evaluate properly.",
				100.1,
				commonFuelType.getMetricTonnesCarbonDioxideForMWHeValue(100), 0);
	}

}
