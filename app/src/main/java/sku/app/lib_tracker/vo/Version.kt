package sku.app.lib_tracker.vo

data class Version(
    val latestStable: String,
    val latest: String,
) {

    companion object {
        const val NO_STABLE_VERSION = "-"
    }

}