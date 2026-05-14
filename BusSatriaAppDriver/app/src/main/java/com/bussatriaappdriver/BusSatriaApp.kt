package com.bussatriaappdriver

import android.app.Application
import com.google.android.gms.maps.MapsInitializer
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BusSatriaAppDriver : Application(){
    override fun onCreate() {
        super.onCreate()
        MapsInitializer.initialize(this)
        FirebaseApp.initializeApp(this)
    }
}