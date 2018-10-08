package igrek.forceawaken.service.ringtone;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import igrek.forceawaken.dagger.DaggerIOC;
import igrek.forceawaken.domain.ringtone.Ringtone;
import igrek.forceawaken.logger.Logger;
import igrek.forceawaken.logger.LoggerFactory;
import igrek.forceawaken.service.filesystem.ExternalCardService;
import igrek.forceawaken.service.filesystem.FilesystemService;

public class RingtoneManagerService {
	
	@Inject
	FilesystemService filesystemService;
	
	private Logger logger = LoggerFactory.getLogger();
	private ExternalCardService externalCardService;
	
	private Random random = new Random();
	
	public RingtoneManagerService(ExternalCardService externalCardService) {
		this.externalCardService = externalCardService;
		DaggerIOC.getFactoryComponent().inject(this);
		filesystemService.ensureAppDataDirExists();
	}
	
	public Ringtone getRandomRingtone() {
		List<Ringtone> ringtones = getAllRingtones();
		return ringtones.get(random.nextInt(ringtones.size()));
	}
	
	public List<Ringtone> getAllRingtones() {
		String ringtonesPath = getExternalStorageDirectory() + "/Android/data/igrek.forceawaken/ringtones";
		File ringtonesDir = new File(ringtonesPath);
		if (!ringtonesDir.exists()) {
			logger.warn("ringtones dir does not exist");
			ringtonesDir.mkdirs();
		}
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
