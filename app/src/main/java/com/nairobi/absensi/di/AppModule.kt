package com.nairobi.absensi.di

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    @Named("userCollection")
    fun provideUserCollection(): CollectionReference {
        return FirebaseFirestore.getInstance().collection("users")
    }

    @Provides
    @Singleton
    fun provideStorageReference(): StorageReference {
        return FirebaseStorage.getInstance().reference
    }

    @Provides
    @Singleton
    @Named("officeCollection")
    fun provideOfficeCollection(): CollectionReference {
        return FirebaseFirestore.getInstance().collection("offices")
    }

    @Provides
    @Singleton
    @Named("overtimeCollection")
    fun provideOvertimeCollection(): CollectionReference {
        return FirebaseFirestore.getInstance().collection("overtimes")
    }

    @Provides
    @Singleton
    @Named("leaveCollection")
    fun provideLeaveCollection(): CollectionReference {
        return FirebaseFirestore.getInstance().collection("leaves")
    }

    @Provides
    @Singleton
    @Named("attendanceCollection")
    fun provideAttendanceCollection(): CollectionReference {
        return FirebaseFirestore.getInstance().collection("attendances")
    }

    @Provides
    @Singleton
    fun provideHolidayApi(): String {
        return "https://api-harilibur.vercel.app/api"
    }
}