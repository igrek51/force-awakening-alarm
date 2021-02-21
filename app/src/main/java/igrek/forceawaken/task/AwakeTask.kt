package igrek.forceawaken.task

import igrek.forceawaken.activity.AwakenActivity

/**
 * Task to do by an user after waking up
 */
interface AwakeTask {
    /**
     * @return instance cloned from a prototype
     */
    val instance: AwakeTask

    val probabilityWeight: Double
        get() = 1.0

    fun run(activity: AwakenActivity)
}