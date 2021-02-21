package igrek.forceawaken.task

import android.app.Activity
import igrek.forceawaken.info.logger.Logger
import igrek.forceawaken.info.logger.LoggerFactory
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory
import java.util.*

class AwakeTaskService(
        activity: LazyInject<Activity> = appFactory.activity,
) {
    private val activity by LazyExtractor(activity)

    private val logger: Logger = LoggerFactory.logger

    private val registeredTasks: LinkedList<AwakeTask> = LinkedList<AwakeTask>()
    private val random = Random()

    init {
        enableTasks()
    }

    private fun enableTasks() {
        registeredTasks.add(LuckyTask())
        registeredTasks.add(AnswerAgainTask())
    }

    private fun sumTasksProbability(): Double {
        var sum = 0.0
        for (registeredTask in registeredTasks) {
            sum += registeredTask.probabilityWeight
        }
        return sum
    }

    // not uniform random
    val randomTask: AwakeTask
        get() {
            // not uniform random
            val sumP = sumTasksProbability()
            val offset = random.nextDouble() * sumP
            var sum = 0.0
            for (registeredTask in registeredTasks) {
                sum += registeredTask.probabilityWeight
                if (sum > offset) return registeredTask.instance
            }
            return registeredTasks.last
        }

}