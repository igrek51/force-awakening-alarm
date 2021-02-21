package igrek.forceawaken.activity

import android.app.Activity
import android.content.Intent
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory
import java.util.concurrent.atomic.AtomicInteger

class ActivityResultDispatcher(
        activity: LazyInject<Activity> = appFactory.activity,
) {
    private val activity by LazyExtractor(activity)

    private val requestCodeSequence: AtomicInteger = AtomicInteger(10)
    private val requestCodeReactions: HashMap<Int, (resultCode: Int, data: Intent?) -> Unit> = hashMapOf()

    fun startActivityForResult(intent: Intent, onResult: (resultCode: Int, data: Intent?) -> Unit) {
        val requestCode = requestCodeSequence.incrementAndGet()
        requestCodeReactions[requestCode] = onResult
        activity.startActivityForResult(intent, requestCode)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        requestCodeReactions[requestCode]?.let { onResult ->
            onResult(resultCode, data)
        }
    }
}