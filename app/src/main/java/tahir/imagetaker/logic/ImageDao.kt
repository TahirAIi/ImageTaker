package tahir.imagetaker.logic

import androidx.lifecycle.LiveData
import androidx.room.*
import tahir.imagetaker.data.Image

@Dao
interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addImage(image:Image)

    @Delete
    suspend fun deleteImage(image: Image)

    @Query("SELECT * FROM imageTbl")
    fun getAllImages():LiveData<List<Image>>

    @Query("DELETE FROM imageTbl")
    suspend fun deleteAllImages()
}