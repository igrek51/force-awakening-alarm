package igrek.forceawaken.userdata

import android.annotation.SuppressLint
import android.app.Activity
import igrek.forceawaken.info.logger.LoggerFactory
import igrek.forceawaken.inject.LazyExtractor
import igrek.forceawaken.inject.LazyInject
import igrek.forceawaken.inject.appFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class LocalDbService(
    activity: LazyInject<Activity> = appFactory.activity,
) {
    private val activity by LazyExtractor(activity)

    private val logger = LoggerFactory.logger

    private val currentSchemaVersion = 2
    private val currentSongsDbFilename = "songs.$currentSchemaVersion.sqlite"

    val appFilesDir: File
        @SuppressLint("SdCardPath")
        get() {
            /*
            1. /data/data/PACKAGE/files or /data/user/0/PACKAGE/files
            2. INTERNAL_STORAGE/Android/data/PACKAGE/files/data
            3. /data/data/PACKAGE/files
            */
            var dir: File? = activity.filesDir
            if (dir != null && dir.isDirectory)
                return dir

            return File("/data/data/" + activity.packageName + "/files")
        }

    val appDataDir: File
        @SuppressLint("SdCardPath")
        get() {
            return appFilesDir.parentFile ?: File("/data/data/" + activity.packageName)
        }

    val songsDbFile: File
        get() = File(appFilesDir, currentSongsDbFilename)

    private fun removeFile(songsDbFile: File) {
        if (songsDbFile.exists()) {
            if (!songsDbFile.delete() || songsDbFile.exists())
                logger.error("failed to delete file: " + songsDbFile.absolutePath)
        }
    }

    private fun copyFileFromResources(resourceId: Int, targetPath: File) {
        val buff = ByteArray(1024)
        try {
            activity.resources.openRawResource(resourceId).use { input ->
                FileOutputStream(targetPath).use { out ->
                    while (true) {
                        val read = input.read(buff)
                        if (read <= 0)
                            break
                        out.write(buff, 0, read)
                    }
                    out.flush()
                }
            }
        } catch (e: IOException) {
            logger.error(e)
        }
    }

}
