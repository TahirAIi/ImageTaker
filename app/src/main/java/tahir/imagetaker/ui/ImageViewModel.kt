package tahir.imagetaker.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tahir.imagetaker.data.Image
import tahir.imagetaker.data.ImageDatabase
import tahir.imagetaker.logic.ImageRepository

class ImageViewModel(application: Application):AndroidViewModel(application) {

    private val repository:ImageRepository
    val getAllImages:LiveData<List<Image>>

    init {
        val imageDao=ImageDatabase.getDatabase(application).imageDao()
        repository= ImageRepository(imageDao)

        getAllImages=repository.getAllImages
    }

    fun addImage(image:Image)
    {
        viewModelScope.launch(Dispatchers.Main) {

            repository.addImage(image)
        }
    }

    fun deleteImage(image: Image)
    {
        viewModelScope.launch(Dispatchers.Main) {

            repository.deleteImage(image)
        }
    }

    fun deleteAllImages()
    {
        viewModelScope.launch(Dispatchers.Main) {

            repository.deleteAllImages()
        }
    }
}