package name.reidmiller.timeofemissions.model;

public enum TimeOfUseRate {
	OFF_PEAK, MID_PEAK, ON_PEAK;

	public static TimeOfUseRate valueOfHour(int wallHourNum,
			TimeOfUseSeason timeOfUseSeason, boolean weekend) {
		if (weekend || wallHourNum < 7 || wallHourNum >= 19) {
			return OFF_PEAK;
		} else if (wallHourNum >= 11 && wallHourNum < 17) {
			if (timeOfUseSeason == TimeOfUseSeason.SUMMER) {
				return ON_PEAK; // Summer
			} else {
				return MID_PEAK; // Winter
			}
		} else {
			if (timeOfUseSeason == TimeOfUseSeason.SUMMER) {
				return MID_PEAK; // Summer
			} else {
				return ON_PEAK; // Winter
			}
		}
	}
}
