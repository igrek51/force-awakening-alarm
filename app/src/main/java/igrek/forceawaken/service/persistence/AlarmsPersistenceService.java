package igrek.forceawaken.service.persistence;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import igrek.forceawaken.domain.alarm.AlarmsConfig;
import igrek.forceawaken.logger.Logger;
import igrek.forceawaken.logger.LoggerFactory;
import igrek.forceawaken.service.filesystem.InternalDataService;

public class AlarmsPersistenceService {
	
	private Logger logger = LoggerFactory.getLogger();
	private InternalDataService internalDataService;
	
	public AlarmsPersistenceService(InternalDataService internalDataService) {
		this.internalDataService = internalDataService;
	}
	
	public AlarmsConfig readAlarmsConfig() {
		File alarmsConfigFile = getAlarmsConfigFile();
		
		if (!alarmsConfigFile.exists()) {
			logger.warn(alarmsConfigFile.getAbsolutePath() + " file does not exist - creating new AlarmsConfig");
			return new AlarmsConfig();
		}
		
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(alarmsConfigFile))) {
			return (AlarmsConfig) ois.readObject();
		} catch (Exception ex) {
			logger.error(ex);
			return null;
		}
	}
	
	public void writeAlarmsConfig(AlarmsConfig alarmsConfig) {
		try {
			File alarmsConfigFile = getAlarmsConfigFile();
			
			FileOutputStream fout = new FileOutputStream(alarmsConfigFile);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(alarmsConfig);
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	@NonNull
	private File getAlarmsConfigFile() {
		File internalDataDir = internalDataService.getInternalDataDir();
		return new File(internalDataDir, "alarmsConfig");
	}
}
