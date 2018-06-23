package igrek.forceawaken.domain.task;

import android.annotation.SuppressLint;

import javax.inject.Inject;

import igrek.forceawaken.AwakenActivity;
import igrek.forceawaken.dagger.DaggerIOC;
import igrek.forceawaken.service.ui.info.UserInfoService;

public class AnswerAgainTask implements AwakeTask {
	
	@Inject
	UserInfoService userInfoService;
	
	public AnswerAgainTask() {
		DaggerIOC.getFactoryComponent().inject(this);
	}
	
	@Override
	public AwakeTask getInstance() {
		return new AnswerAgainTask();
	}
	
	@Override
	public double getProbabilityWeight() {
		return 0; // TODO turn it on
	}
	
	@SuppressLint("NewApi")
	@Override
	public void run(AwakenActivity activity) {
		userInfoService.showInfoBar("Once again quest.");
		activity.startAlarmPlaying(0);
	}
}
