package igrek.forceawaken.dagger;

import javax.inject.Singleton;

import dagger.Component;
import igrek.forceawaken.activity.AwakenActivity;
import igrek.forceawaken.activity.MainActivity;
import igrek.forceawaken.domain.task.AnswerAgainTask;
import igrek.forceawaken.domain.task.LuckyTask;
import igrek.forceawaken.service.filesystem.FilesystemService;
import igrek.forceawaken.service.ringtone.RingtoneManagerService;
import igrek.forceawaken.service.system.PermissionService;
import igrek.forceawaken.ui.errorcheck.UIErrorHandler;

/**
 * Dagger will be injecting to those classes
 */
@Singleton
@Component(modules = {FactoryModule.class})
public interface FactoryComponent {
	
	void inject(MainActivity there);
	
	void inject(AwakenActivity there);
	
	void inject(UIErrorHandler there);
	
	void inject(FilesystemService there);
	
	void inject(PermissionService there);
	
	void inject(RingtoneManagerService there);
	
	/* Tasks */
	
	void inject(LuckyTask there);
	
	void inject(AnswerAgainTask there);
	
}
