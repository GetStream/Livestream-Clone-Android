package io.getstream.livestreamclone

import android.app.Application
import io.getstream.chat.android.client.ChatClient
import timber.log.Timber
import timber.log.Timber.DebugTree


class App : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

        ChatClient.Builder(BuildConfig.STREAM_API_KEY, this).build()
    }
}