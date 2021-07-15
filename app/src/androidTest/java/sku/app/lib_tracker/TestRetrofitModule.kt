package sku.app.lib_tracker

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import sku.app.lib_tracker.api.ApiService
import sku.app.lib_tracker.api.CustomConverterFactory
import sku.app.lib_tracker.di.RetrofitModule
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RetrofitModule::class]
)
object TestRetrofitModule {

    @Singleton
    @Provides
    fun providesApiService(client: OkHttpClient): ApiService {
        // val client = OkHttpClient()

        return Retrofit.Builder()
            .baseUrl("http://127.0.0.1:8080/")
            .client(client)
            .addConverterFactory(CustomConverterFactory())
            // .addCallAdapterFactory()
            .build()
            .create(ApiService::class.java)
    }

}
