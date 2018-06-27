package igrek.forceawaken.service.ringtone;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import igrek.forceawaken.domain.ringtone.Ringtone;
import igrek.forceawaken.service.filesystem.ExternalCardService;

public class RingtoneManagerService {
	
	private ExternalCardService externalCardService;
	
	private Random random = new Random();
	
	public RingtoneManagerService(ExternalCardService externalCardService) {
		this.externalCardService = externalCardService;
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
