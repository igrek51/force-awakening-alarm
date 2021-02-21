package igrek.forceawaken.task

import igrek.forceawaken.activity.AwakenActivity
import igrek.forceawaken.activity.AwakenActivityLayout
import igrek.forceawaken.info.UiInfoService
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory

class AnswerAgainTask(
        uiInfoService: LazyInject<UiInfoService> = appFactory.uiInfoService,
        awakenActivityLayout: LazyInject<AwakenActivityLayout> = appFactory.awakenActivityLayout,
) : AwakeTask {
    private val uiInfoService by LazyExtractor(uiInfoService)
    private val awakenActivityLayout by LazyExtractor(awakenActivityLayout)

    override val instance: AwakeTask
        get() = AnswerAgainTask()
    override val probabilityWeight: Double
        get() = 0.0

    override fun run(activity: AwakenActivity) {
        uiInfoService.showSnackbar("Once again quest.")
        awakenActivityLayout.startAlarmPlaying(0.0, System.currentTimeMillis())
    }
}