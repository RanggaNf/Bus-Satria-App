package com.bussatriaappdriver.di


import android.content.Context
import android.content.SharedPreferences
import com.bussatriaappdriver.data.repository.BusLocationRepository
import com.bussatriaappdriver.data.repository.ChatRepository
import com.bussatriaappdriver.data.repository.LocationRepository
import com.bussatriaappdriver.data.repository.SettingsRepository
import com.bussatriaappdriver.data.repository.SettingsRepositoryImpl
import com.bussatriaappdriver.ui.viewmodel.BusLocationViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }
    @Provides
    @Singleton
    fun provideBusLocationRepository(): BusLocationRepository {
        return BusLocationRepository()
    }

    @Provides
    @Singleton
    fun provideBusLocationViewModel(repository: BusLocationRepository): BusLocationViewModel {
        return BusLocationViewModel(repository)
    }

    @Provides
    @Singleton
    fun provideLocationRepository(
        fusedLocationClient: FusedLocationProviderClient,
        firebaseDatabase: FirebaseDatabase
    ): LocationRepository {
        return LocationRepository(fusedLocationClient, firebaseDatabase)
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("location_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }
    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext context: Context
    ): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }

}