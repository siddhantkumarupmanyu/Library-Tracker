package sku.app.lib_tracker.vo


data class Package(
    val packageName: String
) {
    val urlString: String
        get() = packageName.replace('.', '/')

}

