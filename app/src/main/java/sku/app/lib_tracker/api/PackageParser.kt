package sku.app.lib_tracker.api

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import sku.app.lib_tracker.vo.Package

class PackageParser(private val response: String) {

    companion object {
        private const val ANDROIDX_PREFIX = "androidx"
        private const val STARTING_TAG = "metadata"

        fun parsePackages(response: String): List<Package> {
            return PackageParser(response).getPackages()
        }
    }

    private val packages = mutableListOf<Package>()

    private fun getPackages(): List<Package> {
        parseData()
        return packages
    }

    private fun parseData() {
        val parser = createXmlParser(response)
        parser.nextTag()
        readData(parser)
    }

    private fun createXmlParser(response: String): XmlPullParser {
        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(response.reader())
        return parser
    }

    private fun readData(parser: XmlPullParser) {
        parser.require(XmlPullParser.START_TAG, null, STARTING_TAG)

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            parser.name?.let { name ->
                readPackage(name)
            }
            parser.next()
        }
    }

    private fun readPackage(packageName: String) {
        if (packageName.startsWith(ANDROIDX_PREFIX)) {
            packages.add(Package(packageName))
        }
    }

}