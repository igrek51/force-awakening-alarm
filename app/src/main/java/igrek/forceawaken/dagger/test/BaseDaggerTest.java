package igrek.forceawaken.dagger.test;

import android.app.Activity;

import javax.inject.Inject;

import igrek.forceawaken.logger.Logger;
import igrek.forceawaken.service.volume.VolumeCalculatorService;

public class BaseDaggerTest {
	
	@Inject
	protected Activity activity;
	
	@Inject
	protected Logger logger;
	
	// FIXME dagger in tests
	@Inject
	protected VolumeCalculatorService volumeCalculatorService;
	
}
