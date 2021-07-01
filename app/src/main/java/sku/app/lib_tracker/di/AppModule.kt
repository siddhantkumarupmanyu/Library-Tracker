package sku.app.lib_tracker.di

import android.app.Application
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import sku.app.lib_tracker.api.ApiService
import sku.app.lib_tracker.api.CustomConverterFactory
import sku.app.lib_tracker.db.TrackerDao
import sku.app.lib_tracker.db.TrackerDb
import sku.app.lib_tracker.repository.TrackerRepository
import sku.app.lib_tracker.repository.TrackerRepositoryImpl
import javax.inject.Singleton

class AppModule {
}


@InstallIn(SingletonComponent::class)
@Module
object RetrofitModule {

    @Singleton
    @Provides
    fun providesApiService(): ApiService {
        return Retrofit.Builder()
            .baseUrl("https://maven.google.com/")
            .addConverterFactory(CustomConverterFactory())
            // .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(ApiService::class.java)
    }

}

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {

    @Singleton
    @Binds
    fun provideRepository(repository: TrackerRepositoryImpl): TrackerRepository
}

@InstallIn(SingletonComponent::class)
@Module
object DbModule {
    @Singleton
    @Provides
    fun providesDb(app: Application): TrackerDb {
        return Room.databaseBuilder(app, TrackerDb::class.java, "tracker.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun providesTrackerDao(db: TrackerDb): TrackerDao {
        return db.trackerDao()
    }
}