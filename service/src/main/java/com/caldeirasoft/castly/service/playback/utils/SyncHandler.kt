package com.caldeirasoft.castly.service.playback.utils

import android.R.attr.countDown
import android.os.Handler
import android.os.Looper
import androidx.media2.exoplayer.external.util.Util.getLooper
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


/**
 * Handler that always waits until the Runnable finishes.
 */
class SyncHandler(looper: Looper) : Handler(looper) {

    @Throws(InterruptedException::class)
    fun postAndSync(runnable: Runnable) {
        if (getLooper() == Looper.myLooper()) {
            runnable.run()
        } else {
            val latch = CountDownLatch(1)
            post(Runnable {
                runnable.run()
                latch.countDown()
            })
        }
    }
}