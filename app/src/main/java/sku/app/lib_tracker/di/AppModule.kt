package sku.app.lib_tracker.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import sku.app.lib_tracker.repository.TrackerRepository
import sku.app.lib_tracker.repository.TrackerRepositoryImpl
import sku.app.lib_tracker.work.TrackerWorkManager
import sku.app.lib_tracker.work.TrackerWorkManagerImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface AppModule {

    @Singleton
    @Binds
    fun provideRepository(repository: TrackerRepositoryImpl): TrackerRepository

    @Singleton
    @Binds
    fun provideTackerWorkManager(m: TrackerWorkManagerImpl): TrackerWorkManager

}

