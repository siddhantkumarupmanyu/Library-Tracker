package sku.app.lib_tracker.vo

import java.time.LocalDate
import java.time.Month


// lack of better name

// this does not include any time zone info
// so it may enable fetching two results in a single day
class CustomDate private constructor(private val localDate: LocalDate) {

    /**
     * CustomDate with Today's Date
     */
    constructor() : this(LocalDate.now())

    constructor(day: Int, month: Month, year: Int) : this(
        LocalDate.of(
            year,
            month,
            day
        )
    )

    companion object {
        fun parse(dateInString: String): CustomDate {
            return CustomDate(LocalDate.parse(dateInString))
        }
    }

    val yesterday: CustomDate
        get() = CustomDate(localDate.minusDays(1))

    override fun toString(): String {
        return localDate.toString()
    }

    operator fun compareTo(other: CustomDate): Int {
        return this.localDate.compareTo(other.localDate)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CustomDate

        if (localDate != other.localDate) return false

        return true
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}