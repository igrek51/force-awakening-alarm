package igrek.forceawaken.system.filesystem

import android.content.Context
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory
import java.io.File

class InternalDataService(
        context: LazyInject<Context> = appFactory.context,
) {
    private val context by LazyExtractor(context)

    // /data/data/igrek.forceawaken/app_data
    val internalDataDir: File
        // chmod 777 on directory
        get() {
            // /data/data/igrek.forceawaken/app_data
            val dataDir = context.getDir("data", Context.MODE_PRIVATE)
            // chmod 777 on directory
            dataDir.setReadable(true, false)
            dataDir.setWritable(true, false)
            dataDir.setExecutable(true, false)
            return dataDir
        }
}