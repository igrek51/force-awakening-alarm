package igrek.forceawaken.domain.task;

import javax.inject.Inject;

import igrek.forceawaken.AwakenActivity;
import igrek.forceawaken.dagger.DaggerIOC;
import igrek.forceawaken.service.ui.info.UserInfoService;

public class LuckyTask implements AwakeTask {
	
	@Inject
	UserInfoService userInfoService;
	
	public LuckyTask() {
		DaggerIOC.getFactoryComponent().inject(this);
	}
	
	@Override
	public AwakeTask getInstance() {
		return new LuckyTask();
	}
	
	@Override
	public double getProbabilityWeight() {
		return 1;
	}
	
	@Override
	public void run(AwakenActivity activity) {
		userInfoService.showInfoBar("You are lucky today :)");
	}
}
