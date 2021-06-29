package sku.app.lib_tracker.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import sku.app.lib_tracker.utils.enqueueResponse
import sku.app.lib_tracker.vo.Artifact.Version
import sku.app.lib_tracker.vo.Package
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class ApiServiceTest {

    private lateinit var mockWebServer: MockWebServer

    private lateinit var service: ApiService

    @Before
    fun startServer() {
        mockWebServer = MockWebServer()
        mockWebServer.start(8080)

        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(CustomConverterFactory())
            .build()
            .create(ApiService::class.java)
    }

    @After
    fun stopServer() {
        mockWebServer.shutdown()
    }


    @Test
    fun packages() = runBlocking {
        mockWebServer.enqueueResponse("master-index.xml")

        val packages = service.getPackages()

        val request = mockWebServer.takeRequest(2, TimeUnit.SECONDS)
        assertThat(request.path, `is`("/master-index.xml"))

        val activity = packages[0]
        assertThat(activity.packageName, `is`("androidx.activity"))

        val appSearch = packages[4]
        assertThat(appSearch.packageName, `is`("androidx.appsearch"))
    }

    @Test
    fun library() = runBlocking {
        mockWebServer.enqueueResponse("activity-group-index.xml")

        val pack = Package("androidx.activity")
        val activity = service.getLibrary(pack.urlString)

        val request = mockWebServer.takeRequest(2, TimeUnit.SECONDS)
        assertThat(request.path, `is`("/androidx/activity/group-index.xml"))

        val artifacts = activity.artifacts

        val activityArtifact = artifacts[0]
        assertThat(activityArtifact.name, `is`("activity"))
        assertThat(activityArtifact.version, `is`(Version("1.2.3", "1.3.0-beta02")))
        assertThat(activityArtifact.packageName, `is`("androidx.activity"))
    }
}