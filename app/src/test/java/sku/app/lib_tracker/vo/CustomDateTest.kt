package sku.app.lib_tracker.vo

import junit.framework.TestCase.assertTrue
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import java.time.LocalDate
import java.time.Month

class CustomDateTest {

    @Test
    fun customDateTakesTodayAsDefault() {
        val date = CustomDate()

        val expected = LocalDate.now().toString()

        assertThat(date.toString(), `is`(equalTo(expected)))
    }

    @Test
    fun yesterday() {
        val date = CustomDate(12, Month.JANUARY, 2021)
        val yesterday = CustomDate(11, Month.JANUARY, 2021)

        assertThat(date.yesterday, `is`(equalTo(yesterday)))
    }

    @Test
    fun parse() {
        val dateString = "2021-01-12"

        val date = CustomDate(12, Month.JANUARY, 2021)

        assertThat(CustomDate.parse(dateString), `is`(equalTo(date)))
    }

    @Test
    fun comparison() {
        val date1 = CustomDate(12, Month.JANUARY, 2021)
        val date2 = CustomDate(13, Month.JANUARY, 2021)

        assertTrue("date2 should be greater than date1", date2 > date1)
    }

    @Test
    fun equals() {
        val date1 = CustomDate(12, Month.JANUARY, 2021)
        val date2 = CustomDate(12, Month.JANUARY, 2021)

        assertTrue("date2 should be equal to date1", date2 == date1)
    }

    @Test
    fun testToString() {
        val date = CustomDate(12, Month.JANUARY, 2021)

        assertThat(date.toString(), `is`(equalTo("2021-01-12")))
    }

}