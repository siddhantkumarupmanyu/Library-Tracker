package sku.app.lib_tracker.vo

import androidx.room.Embedded
import androidx.room.Entity

@Entity(
    primaryKeys = ["packageName", "name"]
)
data class Artifact(
    val name: String,
    @field:Embedded(prefix = "version_")
    val version: Version,
    val packageName: String
) {

    data class Version(
        val latestStable: String,
        val latest: String,
    ) {

        companion object {
            const val NO_STABLE_VERSION = "-"
        }

    }

}