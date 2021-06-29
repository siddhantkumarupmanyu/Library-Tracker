package sku.app.lib_tracker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import sku.app.lib_tracker.vo.Artifact

@Database(
    entities = [Artifact::class],
    version = 1,
    exportSchema = false
)
abstract class TrackerDb : RoomDatabase() {

    abstract fun trackerDao(): TrackerDao

}