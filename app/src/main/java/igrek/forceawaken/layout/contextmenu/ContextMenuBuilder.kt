package igrek.forceawaken.layout.contextmenu

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import igrek.forceawaken.info.UiInfoService
import igrek.forceawaken.info.errorcheck.SafeExecutor
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory

class ContextMenuBuilder(
    activity: LazyInject<Activity> = appFactory.activity,
    uiInfoService: LazyInject<UiInfoService> = appFactory.uiInfoService,
) {
    private val activity by LazyExtractor(activity)
    private val uiInfoService by LazyExtractor(uiInfoService)

    fun showContextMenu(titleResId: Int, actions: List<Action>) {
        val actionNames = actions.map { action -> actionName(action) }.toTypedArray()

        val builder = AlertDialog.Builder(activity)
            .setTitle(uiInfoService.resString(titleResId))
            .setItems(actionNames) { _, item ->
                SafeExecutor {
                    actions[item].executor()
                }
            }
            .setCancelable(true)
        if (!activity.isFinishing) {
            builder.create().show()
        }
    }

    fun showContextMenu(actions: List<Action>) {
        val actionNames = actions.map { action -> actionName(action) }.toTypedArray()

        val builder = AlertDialog.Builder(activity)
            .setItems(actionNames) { _, item ->
                SafeExecutor {
                    actions[item].executor()
                }
            }
            .setCancelable(true)
        if (!activity.isFinishing) {
            builder.create().show()
        }
    }

    private fun actionName(action: Action): String {
        if (action.name == null) {
            action.name = uiInfoService.resString(action.nameResId!!)
        }
        return action.name!!
    }

    data class Action(
        var name: String?,
        val nameResId: Int?,
        val executor: () -> Unit,
    ) {

        constructor(name: String, executor: () -> Unit) : this(name, null, executor)

        constructor(nameResId: Int, executor: () -> Unit) : this(null, nameResId, executor)

    }


}