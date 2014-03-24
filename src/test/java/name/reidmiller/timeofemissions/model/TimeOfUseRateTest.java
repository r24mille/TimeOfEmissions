package name.reidmiller.timeofemissions.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class TimeOfUseRateTest {

	@Test
	public void testNaturalOrdering() {
		assertTrue(TimeOfUseRate.OFF_PEAK.compareTo(TimeOfUseRate.OFF_PEAK) == 0);
		assertTrue(TimeOfUseRate.OFF_PEAK.compareTo(TimeOfUseRate.MID_PEAK) < 0);
		assertTrue(TimeOfUseRate.OFF_PEAK.compareTo(TimeOfUseRate.ON_PEAK) < 0);
		
		assertTrue(TimeOfUseRate.MID_PEAK.compareTo(TimeOfUseRate.OFF_PEAK) > 0);
		assertTrue(TimeOfUseRate.MID_PEAK.compareTo(TimeOfUseRate.MID_PEAK) == 0);
		assertTrue(TimeOfUseRate.MID_PEAK.compareTo(TimeOfUseRate.ON_PEAK) < 0);
		
		assertTrue(TimeOfUseRate.ON_PEAK.compareTo(TimeOfUseRate.OFF_PEAK) > 0);
		assertTrue(TimeOfUseRate.ON_PEAK.compareTo(TimeOfUseRate.MID_PEAK) > 0);
		assertTrue(TimeOfUseRate.ON_PEAK.compareTo(TimeOfUseRate.ON_PEAK) == 0);
	}
	
	@Test
	public void testValueOfHour() {
		assertEquals("Weekend summer should be off-peak", TimeOfUseRate.OFF_PEAK,
				TimeOfUseRate.valueOfHour(12, TimeOfUseSeason.SUMMER, true));
		assertEquals("Weekend winter should be off-peak", TimeOfUseRate.OFF_PEAK,
				TimeOfUseRate.valueOfHour(12, TimeOfUseSeason.WINTER, true));
		
		assertEquals("6am summer should be off-peak", TimeOfUseRate.OFF_PEAK,
				TimeOfUseRate.valueOfHour(6, TimeOfUseSeason.SUMMER, false));
		assertEquals("7am summer should be mid-peak", TimeOfUseRate.MID_PEAK,
				TimeOfUseRate.valueOfHour(7, TimeOfUseSeason.SUMMER, false));
		assertEquals("11am summer should be on-peak", TimeOfUseRate.ON_PEAK,
				TimeOfUseRate.valueOfHour(11, TimeOfUseSeason.SUMMER, false));
		assertEquals("5pm summer should be mid-peak", TimeOfUseRate.MID_PEAK,
				TimeOfUseRate.valueOfHour(17, TimeOfUseSeason.SUMMER, false));
		assertEquals("7pm summer should be off-peak", TimeOfUseRate.OFF_PEAK,
				TimeOfUseRate.valueOfHour(19, TimeOfUseSeason.SUMMER, false));
		
		
		assertEquals("6am winter should be off-peak", TimeOfUseRate.OFF_PEAK,
				TimeOfUseRate.valueOfHour(6, TimeOfUseSeason.WINTER, false));
		assertEquals("7am winter should be on-peak", TimeOfUseRate.ON_PEAK,
				TimeOfUseRate.valueOfHour(7, TimeOfUseSeason.WINTER, false));
		assertEquals("11am winter should be mid-peak", TimeOfUseRate.MID_PEAK,
				TimeOfUseRate.valueOfHour(11, TimeOfUseSeason.WINTER, false));
		assertEquals("5pm winter should be on-peak", TimeOfUseRate.ON_PEAK,
				TimeOfUseRate.valueOfHour(17, TimeOfUseSeason.WINTER, false));
		assertEquals("7pm winter should be off-peak", TimeOfUseRate.OFF_PEAK,
				TimeOfUseRate.valueOfHour(19, TimeOfUseSeason.WINTER, false));
	}
}
