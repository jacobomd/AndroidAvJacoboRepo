package io.keepcoding.eh_ho.database

import androidx.room.*


@Entity(tableName = "topic_table")
data class TopicEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "topic_id") val topicId: String,
    @ColumnInfo(name = "topic_title") val title: String,
    @ColumnInfo(name = "topic_date") val date: String,
    @ColumnInfo(name = "topic_posts") val posts: Int,
    @ColumnInfo(name = "topic_views") val views: Int
)

@Dao
interface TopicDao {
    @Query("SELECT * FROM topic_table")
    fun getTopics(): List<TopicEntity>

    @Query("SELECT * FROM topic_table WHERE topic_id LIKE :id")
    fun getTopicById(id: String): TopicEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(topicList: List<TopicEntity>): List<Long>

    @Delete
    fun delete(topics: TopicEntity)
}

@Database(entities = [TopicEntity::class], version = 1)
abstract class TopicDatabase : RoomDatabase() {
    abstract fun topicDao(): TopicDao
}
