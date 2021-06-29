package sku.app.lib_tracker

import androidx.test.espresso.IdlingRegistry
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import sku.app.lib_tracker.api.ApiService
import sku.app.lib_tracker.api.CustomConverterFactory
import sku.app.lib_tracker.di.RetrofitModule
import sku.app.lib_tracker.utils.OkHttp3IdlingResource
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RetrofitModule::class]
)
object TestRetrofitModule {

    @Singleton
    @Provides
    fun providesApiService(): ApiService {
        val client = OkHttpClient()

        val resource = OkHttp3IdlingResource.create("okHttp", client)

        IdlingRegistry.getInstance().register(resource)

        return Retrofit.Builder()
            .baseUrl("http://127.0.0.1:8080/")
            .client(client)
            .addConverterFactory(CustomConverterFactory())
            // .addCallAdapterFactory()
            .build()
            .create(ApiService::class.java)
    }

}
