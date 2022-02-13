package com.example.foods

import android.app.Application
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration

class FoodsApp : Application(){

    val foodsApp : App by lazy {
        App(
            AppConfiguration.Builder(BuildConfig.MONGODB_RELAM_APP_ID)
            .build()
        )
    }

    override fun onCreate() {
        super.onCreate()
    }
}