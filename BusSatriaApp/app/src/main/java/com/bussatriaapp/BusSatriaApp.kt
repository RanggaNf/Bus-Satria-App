package com.bussatriaapp

import android.app.Application
import com.google.android.gms.maps.MapsInitializer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BusSatriaApp : Application(){
    override fun onCreate() {
        super.onCreate()
        MapsInitializer.initialize(this)
    }
}