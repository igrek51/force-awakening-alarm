package igrek.forceawaken.domain.task;

import igrek.forceawaken.AwakenActivity;

/**
 * Task to do by an user after waking up
 */
public interface AwakeTask {
	
	/**
	 * @return instance cloned from a prototype
	 */
	AwakeTask getInstance();
	
	default double getProbabilityWeight() {
		return 1;
	}
	
	void run(AwakenActivity activity);
}
