package sku.app.lib_tracker.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import sku.app.lib_tracker.Package

@RunWith(AndroidJUnit4::class)
class PackageParserTest {


    @Test
    fun parseXMLResponse() {

        val expected = listOf(
            Package("androidx.activity"),
            Package("androidx.ads"),
            Package("androidx.annotation"),
        )

        val actual = PackageParser.parsePackages(response)

        assertThat(actual, `is`(equalTo(expected)))
    }

    private val response = """
        <?xml version='1.0' encoding='UTF-8'?>
        <metadata>
        <android.arch.core/>
        <android.arch.lifecycle/>
        <android.arch.persistence/>
        <android.arch.persistence.room/>
        <android.arch.work/>
        <androidx.activity/>
        <androidx.ads/>
        <androidx.annotation/>
        <com.android/>
        <com.android.application/>
        <com.android.asset-pack/>
        <com.android.asset-pack-bundle/>
        </metadata>
    """.trimIndent()

}