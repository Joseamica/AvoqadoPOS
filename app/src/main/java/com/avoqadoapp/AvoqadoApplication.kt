package com.avoqadoapp

import android.app.Application
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class AvoqadoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
    }


    private class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority <= Log.DEBUG) {
                return
            }

            FirebaseCrashlytics.getInstance().log(message)

            if (t != null) {
                FirebaseCrashlytics.getInstance().recordException(t)
            }
        }
    }
}