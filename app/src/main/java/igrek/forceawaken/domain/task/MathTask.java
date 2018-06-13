package igrek.forceawaken.domain.task;

import igrek.forceawaken.AwakenActivity;

public class MathTask implements AwakeTask {
	
	@Override
	public AwakeTask getInstance() {
		return new MathTask();
	}
	
	@Override
	public double getProbabilityWeight() {
		return 0; // TODO turn it on
	}
	
	@Override
	public void run(AwakenActivity activity) {
	
	}
}
