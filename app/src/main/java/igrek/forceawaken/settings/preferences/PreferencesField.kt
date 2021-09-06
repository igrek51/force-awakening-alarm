package igrek.forceawaken.settings.preferences


enum class PreferencesField constructor(
    val typeDef: PreferenceTypeDefinition<*>
) {

    RingtoneGlobalVolume(1.0f),

    ;

    constructor(defaultValue: String) : this(StringPreferenceType(defaultValue))

    constructor(defaultValue: Long) : this(LongPreferenceType(defaultValue))

    constructor(defaultValue: Float) : this(FloatPreferenceType(defaultValue))

    constructor(defaultValue: Boolean) : this(BooleanPreferenceType(defaultValue))

    fun preferenceName(): String {
        return this.name.replaceFirstChar { it.lowercase() }
    }

}
