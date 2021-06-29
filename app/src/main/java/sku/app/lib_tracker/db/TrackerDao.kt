package sku.app.lib_tracker.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import sku.app.lib_tracker.vo.Artifact

@Dao
abstract class TrackerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertArtifacts(libraries: List<Artifact>)

    @Query("SELECT * FROM artifact")
    abstract fun loadArtifacts(): Flow<List<Artifact>>

}