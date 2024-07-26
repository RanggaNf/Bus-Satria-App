package com.bussatriaappdriver

import android.app.Application
import com.google.android.gms.maps.MapsInitializer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BusSatriaAppDriver : Application(){
    override fun onCreate() {
        super.onCreate()
        MapsInitializer.initialize(this)
    }
}