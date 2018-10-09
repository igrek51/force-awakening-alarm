package igrek.forceawaken.service.persistence;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import igrek.forceawaken.domain.alarm.AlarmTrigger;
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
		
		
		try (FileInputStream fis = new FileInputStream(alarmsConfigFile)) {
			byte fileContent[] = new byte[(int) alarmsConfigFile.length()];
			fis.read(fileContent);
			
			AlarmsConfig unmarshalled = ParcelableUtil.unmarshall(fileContent, AlarmsConfig.CREATOR);
			return unmarshalled;
			
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
		
	}
	
	public void writeAlarmsConfig(AlarmsConfig alarmsConfig) {
		try {
			File alarmsConfigFile = getAlarmsConfigFile();
			FileOutputStream fout = new FileOutputStream(alarmsConfigFile);
			byte[] bytes = ParcelableUtil.marshall(alarmsConfig);
			fout.write(bytes);
			fout.flush();
			fout.close();
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	@NonNull
	private File getAlarmsConfigFile() {
		File internalDataDir = internalDataService.getInternalDataDir();
		return new File(internalDataDir, "alarmsConfig");
	}
	
	public AlarmsConfig addAlarmTrigger(AlarmTrigger alarmTrigger) {
		AlarmsConfig alarmsConfig = readAlarmsConfig();
		alarmsConfig.getAlarmTriggers().add(alarmTrigger);
		writeAlarmsConfig(alarmsConfig);
		logger.info("Alarm trigger has been added: " + alarmTrigger);
		return alarmsConfig;
	}
	
	public AlarmsConfig removeAlarmTrigger(AlarmTrigger alarmTrigger) {
		AlarmsConfig alarmsConfig = readAlarmsConfig();
		if (alarmsConfig.getAlarmTriggers().remove(alarmTrigger)) {
			writeAlarmsConfig(alarmsConfig);
			logger.info("Alarm trigger has been removed: " + alarmTrigger);
		}
		return alarmsConfig;
	}
}
