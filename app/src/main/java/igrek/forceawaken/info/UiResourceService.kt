package igrek.forceawaken.info

import android.app.Activity
import android.content.res.Resources.NotFoundException
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory

open class UiResourceService(
        activity: LazyInject<Activity> = appFactory.activity,
) {
    private val activity: Activity by LazyExtractor(activity)

    open fun resString(resourceId: Int): String {
        return try {
            activity.resources.getString(resourceId)
        } catch (e: NotFoundException) {
            ""
        }
    }

    open fun resString(resourceId: Int, vararg args: Any?): String {
        val message = resString(resourceId)
        return if (args.isNotEmpty()) {
            String.format(message, *args)
        } else {
            message
        }
    }

    @ColorInt
    fun getColor(@ColorRes resourceId: Int): Int {
        return ContextCompat.getColor(activity, resourceId)
    }

}
