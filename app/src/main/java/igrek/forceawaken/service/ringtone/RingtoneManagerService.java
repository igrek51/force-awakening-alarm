package igrek.forceawaken.service.ringtone;

import android.app.Activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import igrek.forceawaken.dagger.DaggerIOC;
import igrek.forceawaken.domain.ringtone.Ringtone;
import igrek.forceawaken.service.filesystem.ExternalCardService;

public class RingtoneManagerService {
	
	@Inject
	Activity activity;
	
	@Inject
	ExternalCardService externalCardService;
	
	private Random random = new Random();
	
	public RingtoneManagerService() {
		DaggerIOC.getAppComponent().inject(this);
	}
	
	public Ringtone getRandomRingtone() {
		List<Ringtone> ringtones = getAllRingtones();
		return ringtones.get(random.nextInt(ringtones.size()));
	}
	
	public List<Ringtone> getAllRingtones() {
		String ringtonesPath = getExternalStorageDirectory() + "/Android/data/igrek.forceawaken/ringtones";
		File ringtonesDir = new File(ringtonesPath);
		List<Ringtone> ringtones = new ArrayList<>();
		for (File file : ringtonesDir.listFiles()) {
			String name = getRingtoneName(file);
			ringtones.add(new Ringtone(file, name));
		}
		return ringtones;
	}
	
	private String getExternalStorageDirectory() {
		return externalCardService.getExternalSDPath();
	}
	
	private String getRingtoneName(File ringtone) {
		return ringtone.getName().replaceAll("\\.mp3$", "");
	}
	
}
