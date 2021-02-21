package igrek.forceawaken.system

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import igrek.forceawaken.info.logger.LoggerFactory
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory

class SoftKeyboardService(
        appCompatActivity: LazyInject<AppCompatActivity> = appFactory.appCompatActivity,
) {
    private val activity by LazyExtractor(appCompatActivity)

    private val imm: InputMethodManager?
    private val logger = LoggerFactory.logger

    init {
        imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    }

    fun hideSoftKeyboard(view: View?) {
        if (imm == null) {
            logger.error("no input method manager")
            return
        }
        if (view == null) {
            logger.error("view = null")
            return
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun hideSoftKeyboard() {
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        hideSoftKeyboard(view)
    }

    fun showSoftKeyboard(view: View?) {
        imm?.showSoftInput(view, 0)
    }
}
