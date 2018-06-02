package igrek.forceawaken.ui.errorcheck;

import javax.inject.Inject;

import igrek.forceawaken.dagger.DaggerIOC;
import igrek.forceawaken.logger.Logger;
import igrek.forceawaken.service.info.UserInfoService;

public class UIErrorHandler {
	
	@Inject
	UserInfoService userInfoService;
	
	@Inject
	Logger logger;
	
	private void _handleError(Throwable t) {
		DaggerIOC.getAppComponent().inject(this);
		logger.error(t);
		userInfoService.showInfo("Error occurred: " + t.getMessage());
	}
	
	public static void showError(Throwable t) {
		new UIErrorHandler()._handleError(t);
	}
	
}
