package igrek.forceawaken.layout.navigation

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import igrek.forceawaken.R
import igrek.forceawaken.activity.ActivityController
import igrek.forceawaken.activity.MainActivity
import igrek.forceawaken.activity.list.ListActivity
import igrek.forceawaken.activity.schedule.ScheduleActivity
import igrek.forceawaken.activity.settings.SettingsActivity
import igrek.forceawaken.info.errorcheck.SafeExecutor
import igrek.forceawaken.info.logger.LoggerFactory
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory
import igrek.forceawaken.system.SoftKeyboardService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class NavigationMenuController(
        activity: LazyInject<Activity> = appFactory.activity,
        activityController: LazyInject<ActivityController> = appFactory.activityController,
        softKeyboardService: LazyInject<SoftKeyboardService> = appFactory.softKeyboardService,
) {
    private val activity by LazyExtractor(activity)
    private val activityController by LazyExtractor(activityController)
    private val softKeyboardService by LazyExtractor(softKeyboardService)

    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    private val actionsMap = HashMap<Int, () -> Unit>()
    private val logger = LoggerFactory.logger

    init {
        initOptionActionsMap()
    }

    private fun initOptionActionsMap() {
        actionsMap[R.id.nav_setup] = {
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)
        }
        actionsMap[R.id.nav_list_alarms] = {
            val intent = Intent(activity, ListActivity::class.java)
            activity.startActivity(intent)
        }
        actionsMap[R.id.nav_schedule] = {
            val intent = Intent(activity, ScheduleActivity::class.java)
            activity.startActivity(intent)
        }
        actionsMap[R.id.nav_settings] = {
            val intent = Intent(activity, SettingsActivity::class.java)
            activity.startActivity(intent)
        }
        actionsMap[R.id.nav_exit] = { activityController.quit() }
    }

    fun init() {
        drawerLayout = activity.findViewById(R.id.drawer_layout)
        navigationView = activity.findViewById(R.id.nav_view)

        navigationView?.setNavigationItemSelectedListener { menuItem ->
            GlobalScope.launch(Dispatchers.Main) {
                // set item as selected to persist highlight
                menuItem.isChecked = true
                drawerLayout?.closeDrawers()
                val id = menuItem.itemId
                if (actionsMap.containsKey(id)) {
                    val action = actionsMap[id]
                    // postpone action - smoother navigation hide
                    Handler(Looper.getMainLooper()).post {
                        SafeExecutor {
                            action?.invoke()
                        }
                    }
                } else {
                    logger.warn("unknown navigation item has been selected.")
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    // unhighlight all menu items
                    navigationView?.let { navigationView ->
                        for (id1 in 0 until navigationView.menu.size())
                            navigationView.menu.getItem(id1).isChecked = false
                    }
                }, 500)
            }
            true
        }
    }

    fun navDrawerShow() {
        drawerLayout?.openDrawer(GravityCompat.START)
        softKeyboardService.hideSoftKeyboard()
    }

    fun navDrawerHide() {
        drawerLayout?.closeDrawers()
    }

    fun isDrawerShown(): Boolean {
        return drawerLayout?.isDrawerOpen(GravityCompat.START) ?: false
    }

}
