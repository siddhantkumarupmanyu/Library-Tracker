package sku.app.lib_tracker.api

import retrofit2.http.GET
import retrofit2.http.Path
import sku.app.lib_tracker.vo.Library
import sku.app.lib_tracker.vo.Package

interface ApiService {

    @GET("master-index.xml")
    suspend fun getPackages(): List<Package>

    @GET("{packagePath}/group-index.xml")
    suspend fun getLibrary(@Path("packagePath", encoded = true) packagePath: String): Library
}