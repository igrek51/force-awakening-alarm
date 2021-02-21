package igrek.forceawaken.time

import org.joda.time.DateTime
import java.util.*

class AlarmTimeService {
    private val random = Random()

    // 2 hours forward
    val fakeCurrentTime: DateTime
        get() =// 2 hours forward
            DateTime.now().plusMinutes(random.nextInt(2 * 60))
}