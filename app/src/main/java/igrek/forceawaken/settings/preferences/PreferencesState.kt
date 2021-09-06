package igrek.forceawaken.settings.preferences

import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PreferencesState(
    preferencesService: LazyInject<PreferencesService> = appFactory.preferencesService,
) {
    internal val preferencesService by LazyExtractor(preferencesService)

    var ringtoneGlobalVolume: Float by PreferenceDelegate(PreferencesField.RingtoneGlobalVolume)

}

class PreferenceDelegate<T : Any>(
    private val field: PreferencesField
) : ReadWriteProperty<PreferencesState, T> {

    override fun getValue(thisRef: PreferencesState, property: KProperty<*>): T {
        return thisRef.preferencesService.getValue(field)
    }

    override fun setValue(thisRef: PreferencesState, property: KProperty<*>, value: T) {
        thisRef.preferencesService.setValue(field, value)
    }
}
