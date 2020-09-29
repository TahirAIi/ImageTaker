package tahir.imagetaker.logic

import androidx.lifecycle.LiveData
import tahir.imagetaker.data.Image

class ImageRepository(private val imageDao: ImageDao) {

    val getAllImages:LiveData<List<Image>> =imageDao.getAllImages()

    suspend fun addImage(image:Image)
    {
        imageDao.addImage(image)
    }

    suspend fun deleteImage(image: Image)
    {
        imageDao.deleteImage(image)
    }

    suspend fun deleteAllImages()
    {
        imageDao.deleteAllImages()
    }
}