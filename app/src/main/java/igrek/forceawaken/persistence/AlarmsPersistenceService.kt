package igrek.forceawaken.persistence

import igrek.forceawaken.alarm.AlarmTrigger
import igrek.forceawaken.alarm.AlarmsConfig
import igrek.forceawaken.alarm.RepetitiveAlarm
import igrek.forceawaken.info.logger.Logger
import igrek.forceawaken.info.logger.LoggerFactory
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory
import igrek.forceawaken.system.filesystem.InternalDataService
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class AlarmsPersistenceService(
        internalDataService: LazyInject<InternalDataService> = appFactory.internalDataService,
) {
    private val internalDataService by LazyExtractor(internalDataService)

    private val logger: Logger = LoggerFactory.logger

    fun readAlarmsConfig(): AlarmsConfig {
        val alarmsConfigFile = alarmsConfigFile
        if (!alarmsConfigFile.exists()) {
            logger.warn(alarmsConfigFile.absolutePath + " file does not exist - creating new AlarmsConfig")
            return AlarmsConfig()
        }
        FileInputStream(alarmsConfigFile).use { fis ->
            val fileContent = ByteArray(alarmsConfigFile.length().toInt())
            fis.read(fileContent)
            val config = ParcelableUtil.unmarshall(fileContent, AlarmsConfig.CREATOR)

            config.alarmTriggers.sortBy { it.triggerTime }
            config.repetitiveAlarms.sortBy { it.triggerTime }

            return config
        }
    }

    private fun writeAlarmsConfig(alarmsConfig: AlarmsConfig) {
        try {
            val alarmsConfigFile = alarmsConfigFile
            val fout = FileOutputStream(alarmsConfigFile)
            val bytes = ParcelableUtil.marshall(alarmsConfig)
            fout.write(bytes)
            fout.flush()
            fout.close()
        } catch (e: IOException) {
            logger.error(e)
        }
    }

    private val alarmsConfigFile: File
        get() {
            val internalDataDir: File = internalDataService.internalDataDir
            return File(internalDataDir, "alarmsConfig")
        }

    fun addAlarmTrigger(alarmTrigger: AlarmTrigger): AlarmsConfig {
        val alarmsConfig: AlarmsConfig = readAlarmsConfig()
        alarmsConfig.alarmTriggers.add(alarmTrigger)
        writeAlarmsConfig(alarmsConfig)
        logger.info("Alarm trigger has been added: $alarmTrigger")
        return alarmsConfig
    }

    fun removeAlarmTrigger(alarmTrigger: AlarmTrigger): AlarmsConfig {
        val alarmsConfig: AlarmsConfig = readAlarmsConfig()
        if (alarmsConfig.alarmTriggers.remove(alarmTrigger)) {
            writeAlarmsConfig(alarmsConfig)
            logger.info("Alarm trigger has been removed: $alarmTrigger")
        }
        return alarmsConfig
    }

    fun addRepetitiveAlarm(repetitiveAlarm: RepetitiveAlarm): AlarmsConfig {
        val alarmsConfig: AlarmsConfig = readAlarmsConfig()
        alarmsConfig.repetitiveAlarms.add(repetitiveAlarm)
        writeAlarmsConfig(alarmsConfig)
        logger.info("Repetitive alarm has been added: $repetitiveAlarm")
        return alarmsConfig
    }

    fun removeRepetitiveAlarm(repetitiveAlarm: RepetitiveAlarm): AlarmsConfig {
        val alarmsConfig: AlarmsConfig = readAlarmsConfig()
        if (alarmsConfig.repetitiveAlarms.remove(repetitiveAlarm)) {
            writeAlarmsConfig(alarmsConfig)
            logger.info("Repetitive alarm has been removed: $repetitiveAlarm")
        }
        return alarmsConfig
    }

    fun updateRepetitiveAlarm(
        repetitiveAlarm: RepetitiveAlarm,
        applyBlock: RepetitiveAlarm.() -> Unit
    ): RepetitiveAlarm {
        val alarmsConfig: AlarmsConfig = readAlarmsConfig()
        val configAlarm = alarmsConfig.repetitiveAlarms.find { it == repetitiveAlarm }
        if (configAlarm == null) {
            logger.error("Cant find alarm in config")
            return repetitiveAlarm
        }
        configAlarm.applyBlock()
        writeAlarmsConfig(alarmsConfig)
        logger.info("Repetitive alarm has been updated: $configAlarm")
        return configAlarm
    }

}