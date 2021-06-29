package sku.app.lib_tracker.api

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import sku.app.lib_tracker.vo.Artifact
import sku.app.lib_tracker.vo.Artifact.Version
import sku.app.lib_tracker.vo.Library

class LibraryParser(private val response: String) {

    companion object {
        private const val VERSION_ATTRIBUTE_INDEX = 0

        fun parseLibrary(response: String): Library {
            return LibraryParser(response).getLibrary()
        }
    }

    private var packageName = ""

    private val artifacts = mutableListOf<Artifact>()

    private fun getLibrary(): Library {
        parseData()
        return Library(packageName, artifacts)
    }

    private fun parseData() {
        val parser = createXmlParser(response)
        readData(parser)
    }

    private fun createXmlParser(response: String): XmlPullParser {
        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(response.reader())
        return parser
    }

    private fun readData(parser: XmlPullParser) {
        parser.next()
        packageName = parser.name

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            readArtifact(parser)
            parser.next()
        }
    }

    private fun readArtifact(parser: XmlPullParser) {
        val versionString = parser.getAttributeValue(VERSION_ATTRIBUTE_INDEX)
        val version = parseVersion(versionString)
        artifacts.add(Artifact(parser.name, version, packageName))
    }

    private fun parseVersion(versionString: String): Version {
        val versions = versionString.split(",")
        val latest = versions.last()
        val stableVersions = versions.filter { version ->
            // replace it with regex
            version.length == 5
        }

        val latestStable =
            if (stableVersions.isEmpty()) Version.NO_STABLE_VERSION else stableVersions.last()

        return Version(latestStable, latest)
    }

}