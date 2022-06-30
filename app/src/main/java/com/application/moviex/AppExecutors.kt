package com.application.moviex

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

class AppExecutors {
    companion object {
        private var instance: AppExecutors = AppExecutors()

        fun getInstance(): AppExecutors {
            return instance
        }
    }

    private val mNetworkIO: ScheduledExecutorService = Executors.newScheduledThreadPool(3)

    fun networkIO(): ScheduledExecutorService {
        return mNetworkIO
    }
}