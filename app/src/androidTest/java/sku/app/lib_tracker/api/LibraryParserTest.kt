package sku.app.lib_tracker.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import sku.app.lib_tracker.Artifact
import sku.app.lib_tracker.Library
import sku.app.lib_tracker.Version


@RunWith(AndroidJUnit4::class)
class LibraryParserTest {

    @Test
    fun parserResponse() {
        val packageName = "androidx.activity"
        val expected = Library(
            packageName,
            listOf(
                Artifact("activity", Version("1.2.3", "1.3.0-beta02"), packageName),
                Artifact("activity-compose", Version("-", "1.3.0-beta02"), packageName),
                Artifact("activity-ktx", Version("1.2.3", "1.3.0-beta02"), packageName)
            )
        )

        val actual = LibraryParser.parseLibrary(response)

        assertThat(actual, `is`(equalTo(expected)))
    }

    private val response = """
        <?xml version='1.0' encoding='UTF-8'?>
        <androidx.activity>
        <activity versions="1.0.0-alpha01,1.0.0-alpha02,1.0.0-alpha03,1.0.0-alpha04,1.0.0-alpha05,1.0.0-alpha06,1.0.0-alpha07,1.0.0-alpha08,1.0.0-beta01,1.0.0-rc01,1.0.0,1.1.0-alpha01,1.1.0-alpha02,1.1.0-alpha03,1.1.0-beta01,1.1.0-rc01,1.1.0-rc02,1.1.0-rc03,1.1.0,1.2.0-alpha01,1.2.0-alpha02,1.2.0-alpha03,1.2.0-alpha04,1.2.0-alpha05,1.2.0-alpha06,1.2.0-alpha07,1.2.0-alpha08,1.2.0-beta01,1.2.0-beta02,1.2.0-rc01,1.2.0,1.2.1,1.2.2,1.2.3,1.3.0-alpha01,1.3.0-alpha02,1.3.0-alpha03,1.3.0-alpha04,1.3.0-alpha05,1.3.0-alpha06,1.3.0-alpha07,1.3.0-alpha08,1.3.0-beta01,1.3.0-beta02"/>
        <activity-compose versions="1.3.0-alpha01,1.3.0-alpha02,1.3.0-alpha03,1.3.0-alpha04,1.3.0-alpha05,1.3.0-alpha06,1.3.0-alpha07,1.3.0-alpha08,1.3.0-beta01,1.3.0-beta02"/>
        <activity-ktx versions="1.0.0-alpha01,1.0.0-alpha02,1.0.0-alpha03,1.0.0-alpha04,1.0.0-alpha05,1.0.0-alpha06,1.0.0-alpha07,1.0.0-alpha08,1.0.0-beta01,1.0.0-rc01,1.0.0,1.1.0-alpha01,1.1.0-alpha02,1.1.0-alpha03,1.1.0-beta01,1.1.0-rc01,1.1.0-rc02,1.1.0-rc03,1.1.0,1.2.0-alpha01,1.2.0-alpha02,1.2.0-alpha03,1.2.0-alpha04,1.2.0-alpha05,1.2.0-alpha06,1.2.0-alpha07,1.2.0-alpha08,1.2.0-beta01,1.2.0-beta02,1.2.0-rc01,1.2.0,1.2.1,1.2.2,1.2.3,1.3.0-alpha01,1.3.0-alpha02,1.3.0-alpha03,1.3.0-alpha04,1.3.0-alpha05,1.3.0-alpha06,1.3.0-alpha07,1.3.0-alpha08,1.3.0-beta01,1.3.0-beta02"/>
        </androidx.activity>
    """.trimIndent()

}