package com.abhiumale.kartikmartapp.di

import com.abhiumale.kartikmartapp.data.local.dao.CartDao
import com.abhiumale.kartikmartapp.data.remote.FirebaseAuthSource
import com.abhiumale.kartikmartapp.data.repository.AuthRepositoryImpl
import com.abhiumale.kartikmartapp.data.repository.CheckoutRepository
import com.abhiumale.kartikmartapp.domain.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Singleton
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance("https://kartikmart-app-38ba3-default-rtdb.firebaseio.com")
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(firebaseSource: FirebaseAuthSource): AuthRepository {
        return AuthRepositoryImpl(firebaseSource)
    }

    @Provides
    @Singleton
    fun provideCheckoutRepository(
        database: FirebaseDatabase,
        auth: FirebaseAuth,
        cartDao: CartDao
    ): CheckoutRepository {
        return CheckoutRepository(database, auth, cartDao)
    }
}
