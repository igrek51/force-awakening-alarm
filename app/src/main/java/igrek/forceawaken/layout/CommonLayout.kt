package igrek.forceawaken.layout

import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import igrek.forceawaken.R
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory
import igrek.forceawaken.layout.navigation.NavigationMenuController

class CommonLayout(
        appCompatActivity: LazyInject<AppCompatActivity> = appFactory.appCompatActivity,
        navigationMenuController: LazyInject<NavigationMenuController> = appFactory.navigationMenuController,
) {
    private val activity by LazyExtractor(appCompatActivity)
    private val navigationMenuController by LazyExtractor(navigationMenuController)

    fun init() {
        setupToolbar()
        setupNavigationMenu()
    }

    private fun setupNavigationMenu() {
        activity.findViewById<ImageButton>(R.id.navMenuButton)?.run {
            setOnClickListener { navigationMenuController.navDrawerShow() }
        }
    }

    private fun setupToolbar() {
        activity.findViewById<Toolbar>(R.id.toolbar1)?.let { toolbar ->
            activity.setSupportActionBar(toolbar)
            activity.supportActionBar?.run {
                setDisplayHomeAsUpEnabled(false)
                setDisplayShowHomeEnabled(false)
            }
        }
    }
}