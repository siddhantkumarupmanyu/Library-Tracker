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
        val eventLiveData = MutableLiveData(event)

        assertThat(eventLiveData.getEventValue(), `is`("event"))

        // fix this
        // bottleneck; it always waits for 100 secs before continuing
        assertThat(
            eventLiveData.getEventValue(timeout = 100, timeUnit = TimeUnit.MILLISECONDS),
            `is`(nullValue())
        )
    }

    @Test
    fun newEvent() {
        val event1 = Event("event1")
        val event2 = Event("event2")
        val eventLiveData = MutableLiveData(event1)

        assertThat(eventLiveData.getEventValue(), `is`("event1"))

        eventLiveData.postValue(event2)

        assertThat(eventLiveData.getEventValue(), `is`("event2"))
    }

    @Test
    fun differentTypeOfEvents() {
        val event1 = Event("string")
        val event2 = Event(2)

        val eventLiveData = MutableLiveData<Event<Any>>()

        eventLiveData.postValue(event1)
        assertThat(eventLiveData.getEventValue(), `is`("string"))

        eventLiveData.postValue(event2)
        assertThat(eventLiveData.getEventValue(), `is`(2))
    }

}

fun <T> LiveData<Event<T>>.getEventValue(
    timeout: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS
): T? {
    var content: T? = null
    val latch = CountDownLatch(1)
    val eventObserver = EventObserver<T> {
        content = it
        latch.countDown()
    }
    this.observeForever(eventObserver)
    latch.await(timeout, timeUnit)
    this.removeObserver(eventObserver)
    return content
}