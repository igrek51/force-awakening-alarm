package igrek.forceawaken.activity.settings

import androidx.appcompat.app.AppCompatActivity
import igrek.forceawaken.activity.ActivityResultDispatcher
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory
import igrek.forceawaken.settings.preferences.PreferencesService
import igrek.forceawaken.system.SystemKeyDispatcher
import igrek.forceawaken.userdata.UserDataDao

class SettingsActivityData(
    private val _settingsActivityLayout: LazyInject<SettingsActivityLayout> = appFactory.settingsActivityLayout,
    private val _systemKeyDispatcher: LazyInject<SystemKeyDispatcher> = appFactory.systemKeyDispatcher,
    private val _activityResultDispatcher: LazyInject<ActivityResultDispatcher> = appFactory.activityResultDispatcher,
    private val _preferencesService: LazyInject<PreferencesService> = appFactory.preferencesService,
    private val _userDataDao: LazyInject<UserDataDao> = appFactory.userDataDao,
) : AppCompatActivity() {
    val settingsActivityLayout by LazyExtractor(_settingsActivityLayout)
    val systemKeyDispatcher by LazyExtractor(_systemKeyDispatcher)
    val activityResultDispatcher by LazyExtractor(_activityResultDispatcher)
    val preferencesService by LazyExtractor(_preferencesService)
    val userDataDao by LazyExtractor(_userDataDao)

    fun inflate() {
        _settingsActivityLayout.get()
        _systemKeyDispatcher.get()
        _activityResultDispatcher.get()
        _preferencesService.get()
        _userDataDao.get()
    }
}
