package name.reidmiller.timeofemissions.model;

import java.util.HashMap;
import java.util.SortedSet;

public class GenerationMix {
	private HashMap<String, SortedSet> generationMixTimes;

	public HashMap<String, SortedSet> getGenerationMixTimes() {
		return generationMixTimes;
	}

	public void setGenerationMixTimes(
			HashMap<String, SortedSet> generationMixTimes) {
		this.generationMixTimes = generationMixTimes;
	}
}