package igrek.forceawaken.ringtone

import android.net.Uri
import java.io.File

class Ringtone(val file: File, val name: String) {
    val uri: Uri
        get() = Uri.fromFile(file)

    override fun toString(): String {
        return name
    }

    override fun equals(obj: Any?): Boolean {
        return obj != null && obj is Ringtone && file == obj.file
    }
}