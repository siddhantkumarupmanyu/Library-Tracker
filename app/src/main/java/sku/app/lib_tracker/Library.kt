package sku.app.lib_tracker

data class Library(
    val packageName: String,
    val artifacts: List<Artifact>
)

data class Artifact(
    val name: String,
    val version: Version,
    val packageName: String
)

data class Version(
    val latestStable: String,
    val latest: String,
) {

    companion object {
        const val NO_STABLE_VERSION = "-"
    }

}