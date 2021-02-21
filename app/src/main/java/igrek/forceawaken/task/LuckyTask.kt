package igrek.forceawaken.task

import igrek.forceawaken.activity.AwakenActivity
import igrek.forceawaken.info.UiInfoService
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory

class LuckyTask(
        uiInfoService: LazyInject<UiInfoService> = appFactory.uiInfoService,
) : AwakeTask {
    private val uiInfoService by LazyExtractor(uiInfoService)

    override val instance: AwakeTask
        get() = LuckyTask()
    override val probabilityWeight: Double
        get() = 1.0

    override fun run(activity: AwakenActivity) {
        uiInfoService.showSnackbar("You are lucky today :)")
    }
}