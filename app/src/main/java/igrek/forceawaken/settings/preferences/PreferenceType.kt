package igrek.forceawaken.settings.preferences

import android.content.SharedPreferences
import kotlin.reflect.KClass


abstract class PreferenceTypeDefinition<T : Any>(val defaultValue: T) {
    abstract fun load(sharedPreferences: SharedPreferences, propertyName: String): T

    abstract fun save(editor: SharedPreferences.Editor, propertyName: String, value: Any)

    open fun primitive2entity(primitive: Any): T {
        return primitive as T
    }

    open fun entity2primitive(entity: Any): Any {
        return entity
    }

    fun validClass(): KClass<out T> {
        return defaultValue::class
    }
}

class StringPreferenceType(
    defaultValue: String
) : PreferenceTypeDefinition<String>(defaultValue) {

    override fun load(sharedPreferences: SharedPreferences, propertyName: String): String {
        return sharedPreferences.getString(propertyName, defaultValue) ?: defaultValue
    }

    override fun save(editor: SharedPreferences.Editor, propertyName: String, value: Any) {
        editor.putString(propertyName, value as String)
    }
}

class LongPreferenceType(
    defaultValue: Long
) : PreferenceTypeDefinition<Long>(defaultValue) {

    override fun load(sharedPreferences: SharedPreferences, propertyName: String): Long {
        return sharedPreferences.getLong(propertyName, defaultValue)
    }

    override fun save(editor: SharedPreferences.Editor, propertyName: String, value: Any) {
        editor.putLong(propertyName, value as Long)
    }
}

class FloatPreferenceType(
    defaultValue: Float
) : PreferenceTypeDefinition<Float>(defaultValue) {

    override fun load(sharedPreferences: SharedPreferences, propertyName: String): Float {
        return sharedPreferences.getFloat(propertyName, defaultValue)
    }

    override fun save(editor: SharedPreferences.Editor, propertyName: String, value: Any) {
        editor.putFloat(propertyName, value as Float)
    }
}

class BooleanPreferenceType(
    defaultValue: Boolean
) : PreferenceTypeDefinition<Boolean>(defaultValue) {

    override fun load(sharedPreferences: SharedPreferences, propertyName: String): Boolean {
        return sharedPreferences.getBoolean(propertyName, defaultValue)
    }

    override fun save(editor: SharedPreferences.Editor, propertyName: String, value: Any) {
        editor.putBoolean(propertyName, value as Boolean)
    }
}

class GenericStringIdPreferenceType<T : Any>(
    defaultValue: T,
    private val serializer: (T) -> String,
    private val deserializer: (String) -> T?
) : PreferenceTypeDefinition<T>(defaultValue) {

    override fun load(sharedPreferences: SharedPreferences, propertyName: String): T {
        val stringVal: String =
            sharedPreferences.getString(propertyName, null) ?: return defaultValue
        return deserializer(stringVal) ?: defaultValue
    }

    override fun save(editor: SharedPreferences.Editor, propertyName: String, value: Any) {
        val serialized: String = serializer(value as T)
        editor.putString(propertyName, serialized)
    }

    override fun primitive2entity(primitive: Any): T {
        val id = primitive as? String ?: return defaultValue
        return deserializer(id) ?: defaultValue
    }

    override fun entity2primitive(entity: Any): String {
        return serializer((entity as? T) ?: defaultValue)
    }
}

class GenericLongIdPreferenceType<T : Any>(
    defaultValue: T,
    private val serializer: (T) -> Long,
    private val deserializer: (Long) -> T?
) : PreferenceTypeDefinition<T>(defaultValue) {

    override fun load(sharedPreferences: SharedPreferences, propertyName: String): T {
        val defaultSerialized = serializer(defaultValue)
        val longVal: Long = sharedPreferences.getLong(propertyName, defaultSerialized)
        return deserializer(longVal) ?: defaultValue
    }

    override fun save(editor: SharedPreferences.Editor, propertyName: String, value: Any) {
        val serialized: Long = serializer(value as T)
        editor.putLong(propertyName, serialized)
    }

    override fun primitive2entity(primitive: Any): T {
        val id = primitive as? Long ?: return defaultValue
        return deserializer(id) ?: defaultValue
    }

    override fun entity2primitive(entity: Any): Long {
        return serializer((entity as? T) ?: defaultValue)
    }
}
