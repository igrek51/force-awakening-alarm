package igrek.forceawaken.dagger.test;

import android.app.Activity;

import javax.inject.Inject;

import igrek.forceawaken.logger.Logger;

public class BaseDaggerTest {
	
	@Inject
	protected Activity activity;
	
	@Inject
	protected Logger logger;
	
}
