package igrek.forceawaken.inject

interface LazyInject<T> {
    fun get(): T
}

class SingletonInject<T>(private val supplier: () -> T) : LazyInject<T> {
    private var cached: T? = null

    override fun get(): T {
        val cachedSnapshot = cached
        if (cachedSnapshot == null) {
            val notNull = supplier.invoke()
            cached = notNull
            return notNull
        }
        return cachedSnapshot
    }
}

class PrototypeInject<T>(private val supplier: () -> T) : LazyInject<T> {
    override fun get(): T = supplier.invoke()
}
