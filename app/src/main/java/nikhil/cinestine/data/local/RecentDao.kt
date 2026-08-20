package nikhil.cinestine.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentDao {
    @Query("SELECT * FROM recently_viewed ORDER BY viewedAt DESC LIMIT :limit")
    fun observeNewest(limit: Int): Flow<List<RecentEntity>>

    @Query("SELECT * FROM recently_viewed ORDER BY viewedAt DESC")
    suspend fun allNewestFirst(): List<RecentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: RecentEntity)

    @Query("DELETE FROM recently_viewed WHERE id = :id AND mediaType = :mediaType")
    suspend fun delete(id: String, mediaType: String)
}
