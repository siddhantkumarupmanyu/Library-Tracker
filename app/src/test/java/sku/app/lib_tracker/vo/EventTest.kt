package sku.app.lib_tracker.vo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class EventTest {

    @Rule
    @JvmField
    val instantExecutor = InstantTaskExecutorRule()

    @Test
    fun oneEventCanBeObservedOnlyOnce() {
        val event = Event("event")
        val eventLiveDate = MutableLiveData(event)

        assertThat(getEventValue(eventLiveDate), `is`("event"))

        // TODO: fix this
        // bottleneck; it always waits for 2 secs before continuing
        assertThat(getEventValue(eventLiveDate), `is`(nullValue()))
    }

    @Test
    fun newEvent() {
        val event1 = Event("event1")
        val event2 = Event("event2")
        val eventLiveDate = MutableLiveData(event1)

        assertThat(getEventValue(eventLiveDate), `is`("event1"))

        eventLiveDate.postValue(event2)

        assertThat(getEventValue(eventLiveDate), `is`("event2"))
    }

    private fun <T> getEventValue(eventLiveData: LiveData<Event<T>>): T? {
        var content: T? = null
        val latch = CountDownLatch(1)
        val eventObserver = EventObserver<T> {
            content = it
            latch.countDown()
        }
        eventLiveData.observeForever(eventObserver)
        latch.await(2, TimeUnit.SECONDS)
        eventLiveData.removeObserver(eventObserver)
        return content
    }

}