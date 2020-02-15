package io.keepcoding.eh_ho.database


import androidx.room.*
import java.util.*

@Entity(tableName = "latestNews_table")
data class LatestNewsEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "latestNews_title") val topic_title: String,
    @ColumnInfo(name = "latestNews_topicId") val topic_id: Int,
    @ColumnInfo(name = "latestNews_topic_slug") val topic_slug: String,
    @ColumnInfo(name = "latestNews_username") val username: String,
    @ColumnInfo(name = "latestNews_cooked") val cooked: String,
    @ColumnInfo(name = "latestNews_created_at") val created_at: String,
    @ColumnInfo(name = "latestNews_post_number") val post_number: Int,
    @ColumnInfo(name = "latestNews_score") val score: Double

)

@Dao
interface LatestNewsDao {
    @Query("SELECT * FROM latestNews_table")
    fun getLatestNews(): List<LatestNewsEntity>

    /*@Query("SELECT * FROM latestNews_table WHERE latestNews_id LIKE :id")
    fun getLatestNewById(id: String): LatestNewsEntity*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(latestNewsList: List<LatestNewsEntity>): List<Long>

    @Delete
    fun delete(latestNews: LatestNewsEntity)
}

@Database(entities = [LatestNewsEntity::class], version = 1)
abstract class LatestNewsDatabase : RoomDatabase() {
    abstract fun latestNewsDao(): LatestNewsDao
}
