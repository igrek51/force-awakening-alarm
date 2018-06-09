package igrek.forceawaken.service.ringtone;

import android.app.Activity;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import igrek.forceawaken.dagger.DaggerIOC;
import igrek.forceawaken.domain.ringtone.Ringtone;

public class RingtoneManagerService {
	
	@Inject
	Activity activity;
	
	private Random random = new Random();
	
	public RingtoneManagerService() {
		DaggerIOC.getAppComponent().inject(this);
	}
	
	private String getExternalStorageDirectory() {
		String mExternalDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
		// WTF samsung workaround
		if (android.os.Build.DEVICE.contains("samsung") || android.os.Build.MANUFACTURER.contains("samsung")) {
			File f = new File("/storage/extSdCard");
			if (f.exists() && f.isDirectory()) {
				mExternalDirectory = "/storage/extSdCard";
			} else {
				f = new File("/storage/external_sd");
				if (f.exists() && f.isDirectory()) {
					mExternalDirectory = "/storage/external_sd";
				}
			}
		}
		return mExternalDirectory;
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
	
	private String getRingtoneName(File ringtone) {
		return ringtone.getName().replaceAll("\\.mp3$", "");
	}
	
}
