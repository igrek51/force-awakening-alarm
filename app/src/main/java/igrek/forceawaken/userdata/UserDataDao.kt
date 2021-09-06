package igrek.forceawaken.userdata

import igrek.forceawaken.info.errorcheck.UiErrorHandler
import igrek.forceawaken.info.logger.LoggerFactory
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory
import igrek.forceawaken.settings.preferences.PreferencesDao
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class UserDataDao(
    localDbService: LazyInject<LocalDbService> = appFactory.localDbService,
) {
    internal val localDbService by LazyExtractor(localDbService)

    var preferencesDao: PreferencesDao by LazyDaoLoader { path -> PreferencesDao(path) }

    private var saveRequestSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val logger = LoggerFactory.logger

    init {
        saveRequestSubject
            .throttleLast(2, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ toSave ->
                if (toSave)
                    save()
            }, UiErrorHandler::handleError)
    }

    fun init() {
        reload()
    }

    fun reload() {
        val path = localDbService.appFilesDir.absolutePath

        preferencesDao = PreferencesDao(path)

        logger.debug("user data reloaded")
    }

    @Synchronized
    fun save() {
        preferencesDao.save()
        logger.info("user data saved")
    }

    fun factoryReset() {
        preferencesDao.factoryReset()
    }

    fun requestSave(toSave: Boolean) {
        saveRequestSubject.onNext(toSave)
    }

    fun saveNow() {
        requestSave(false)
        save()
    }

}

class LazyDaoLoader<T : AbstractJsonDao<out Any>>(
    private val loader: (path: String) -> T,
) : ReadWriteProperty<UserDataDao, T> {

    private var loaded: T? = null

    override fun getValue(thisRef: UserDataDao, property: KProperty<*>): T {
        val loadedVal = loaded
        if (loadedVal != null)
            return loadedVal

        val path = thisRef.localDbService.appFilesDir.absolutePath
        val loadedNN = loader.invoke(path)
        loaded = loadedNN
        return loadedNN
    }

    override fun setValue(thisRef: UserDataDao, property: KProperty<*>, value: T) {
        loaded = value
    }
}
