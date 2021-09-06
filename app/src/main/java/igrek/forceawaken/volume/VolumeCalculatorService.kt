package igrek.forceawaken.volume

import igrek.forceawaken.info.logger.Logger
import igrek.forceawaken.info.logger.LoggerFactory
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory
import igrek.forceawaken.sensors.AccelerometerService
import igrek.forceawaken.settings.preferences.PreferencesState

class VolumeCalculatorService(
    accelerometerService: LazyInject<AccelerometerService> = appFactory.accelerometerService,
    preferencesState: LazyInject<PreferencesState> = appFactory.preferencesState,
) {
    private val accelerometerService by LazyExtractor(accelerometerService)
    private val preferencesState by LazyExtractor(preferencesState)

    private val logger: Logger = LoggerFactory.logger

    val noiseVolTransform = doubleArrayOf( // noise dB -> alarm volume
        35.0, 0.4,  // low limit
        70.0, 0.8
    )
    private val speakerDownCompensation = 1.1
    private val globalVolume = 0.12

    fun calcVolumeByNoise(noiseLevel: Double): Double {
        if (noiseLevel <= noiseVolTransform[0]) return noiseVolTransform[1]
        if (noiseLevel >= noiseVolTransform[2]) return noiseVolTransform[3]
        val fraction = (noiseLevel - noiseVolTransform[0]) / (noiseVolTransform[2] - noiseVolTransform[0])
        return noiseVolTransform[1] + fraction * (noiseVolTransform[3] - noiseVolTransform[1])
    }

    fun calcFinalVolume(noiseLevel: Double): Double {
        var vol1 = calcVolumeByNoise(noiseLevel)
        val speakerDown: Boolean? = accelerometerService.isSpeakerRotatedDown
        if (speakerDown == true) { // speaker rotated down - increase volume
            logger.debug("Speaker is rotated down - boosting volume level")
            vol1 *= speakerDownCompensation
            if (vol1 > 1.0) // cut off
                vol1 = 1.0
        }
        return vol1 * globalVolume * preferencesState.ringtoneGlobalVolume
    }
}