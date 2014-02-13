package name.reidmiller.timeofemissions.model.test;

import static org.junit.Assert.*;
import name.reidmiller.iesoreports.client.GeneratorOutputCapabilityClient.FuelType;
import name.reidmiller.timeofemissions.model.FuelTypeMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.Test;

public class FuelTypeMetadataTest {
	private Logger logger = LogManager.getLogger(this.getClass());

	@Test
	public void testFuelTypeMetadata() {
		FuelTypeMetadata fuelTypeMetaData = new FuelTypeMetadata(FuelType.COAL);
		assertNotNull(FuelTypeMetadata.class.toString()
				+ " not instantiated correctly.", fuelTypeMetaData);
	}

	@Test
	public void testGetGraphColor() {
		FuelTypeMetadata fuelTypeMetaData = new FuelTypeMetadata(FuelType.COAL);
		assertEquals(FuelTypeMetadata.class.toString()
				+ " graph color not set properly.",
				FuelTypeMetadata.COAL_GRAPH_COLOR,
				fuelTypeMetaData.getGraphColor());
	}

	@Test
	public void testGetGramsCarbonDioxidePerKWHe() {
		FuelTypeMetadata fuelTypeMetaData = new FuelTypeMetadata(FuelType.COAL);
		assertEquals(FuelTypeMetadata.class.toString()
				+ " grams CO2 per kWhe ratio not set properly.",
				FuelTypeMetadata.COAL_GRAMS_CO2_PER_KWHE,
				fuelTypeMetaData.getGramsCarbonDioxidePerKWHe(), 0);
	}

	@Test
	public void testGetGramsCarbonDioxideForKWHeValue() {
		FuelTypeMetadata fuelTypeMetaData = new FuelTypeMetadata(FuelType.COAL);
		assertEquals(
				FuelTypeMetadata.class.toString()
						+ " emissions for grams CO2 / kWhe value did not evaluate properly.",
				100100,
				fuelTypeMetaData.getGramsCarbonDioxideForKWHeValue(100), 0);
	}

	@Test
	public void testGetKilogramsCarbonDioxidePerKWHe() {
		FuelTypeMetadata fuelTypeMetaData = new FuelTypeMetadata(FuelType.COAL);
		assertEquals(FuelTypeMetadata.class.toString()
				+ " kg CO2 per kWhe ratio not set properly.", 1.001,
				fuelTypeMetaData.getKilogramsCarbonDioxidePerKWHe(), 0);
	}

	@Test
	public void testGetKilogramsCarbonDioxideForKWHeValue() {
		FuelTypeMetadata fuelTypeMetaData = new FuelTypeMetadata(FuelType.COAL);
		assertEquals(
				FuelTypeMetadata.class.toString()
						+ " emissions for kg CO2 / kWhe value did not evaluate properly.",
				100.1,
				fuelTypeMetaData.getKilogramsCarbonDioxideForKWHeValue(100), 0);
	}

	@Test
	public void testGetKilogramsCarbonDioxidePerMWHe() {
		FuelTypeMetadata fuelTypeMetaData = new FuelTypeMetadata(FuelType.COAL);
		assertEquals(FuelTypeMetadata.class.toString()
				+ " kg CO2 per MWhe ratio not set properly.", 1001,
				fuelTypeMetaData.getKilogramsCarbonDioxidePerMWHe(), 0);
	}

	@Test
	public void testGetKilogramsCarbonDioxideForMWHeValue() {
		FuelTypeMetadata fuelTypeMetaData = new FuelTypeMetadata(FuelType.COAL);
		assertEquals(
				FuelTypeMetadata.class.toString()
						+ " emissions for kg CO2 / MWhe value did not evaluate properly.",
				100100,
				fuelTypeMetaData.getKilogramsCarbonDioxideForMWHeValue(100), 0);
	}

	@Test
	public void testGetMetricTonnesCarbonDioxidePerMWHe() {
		FuelTypeMetadata fuelTypeMetaData = new FuelTypeMetadata(FuelType.COAL);
		assertEquals(FuelTypeMetadata.class.toString()
				+ " tonnes CO2 per MWhe ratio not set properly.", 1.001,
				fuelTypeMetaData.getMetricTonnesCarbonDioxidePerMWHe(), 0);
	}

	@Test
	public void testGetMetricTonnesCarbonDioxideForMWHeValue() {
		FuelTypeMetadata fuelTypeMetaData = new FuelTypeMetadata(FuelType.COAL);
		assertEquals(
				FuelTypeMetadata.class.toString()
						+ " emissions for tonnes CO2 / MWhe value did not evaluate properly.",
				100.1,
				fuelTypeMetaData.getMetricTonnesCarbonDioxideForMWHeValue(100),
				0);
	}

}
