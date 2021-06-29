package sku.app.lib_tracker.utils

import sku.app.lib_tracker.vo.Artifact
import sku.app.lib_tracker.vo.Library
import sku.app.lib_tracker.vo.Artifact.Version

object TestUtils {

    fun createLibrary(packageName: String) = Library(
        packageName,
        createArtifacts(2, packageName)
    )

    fun createArtifacts(count: Int, packageName: String): List<Artifact> {
        return (0 until count).map {
            createArtifact("artifact$it", packageName)
        }
    }

    fun createArtifact(name: String, packageName: String) = Artifact(
        name,
        Version("1.2.3", "1.3.0-beta01"),
        packageName
    )

}