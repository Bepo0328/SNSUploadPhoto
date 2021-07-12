package kr.co.bepo.snsuploadapp.gallery

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kr.co.bepo.snsuploadapp.MainApplication.Companion.appContext

class GalleryViewModel : ViewModel() {

    private val galleryPhotoRepository by lazy { GalleryPhotoRepository(appContext!!) }

    private lateinit var photoList: MutableList<GalleryPhoto>

    val galleryStateLiveData = MutableLiveData<GalleryState>(GalleryState.UnInitialized)

    @RequiresApi(Build.VERSION_CODES.Q)
    fun fetchData() = viewModelScope.launch {
        setState(GalleryState.Loading)
        photoList = galleryPhotoRepository.getAllPhotos()
        setState(GalleryState.Success(photoList))
    }

    fun selectPhoto(galleryPhoto: GalleryPhoto) {
        val findGalleryPhoto = photoList.find { it.id == galleryPhoto.id }
        findGalleryPhoto?.let { photo ->
            photoList[photoList.indexOf(photo)] =
                photo.copy(
                    isSelected = photo.isSelected.not()
                )
            setState(GalleryState.Success(photoList))
        }
    }

    fun confirmCheckedPhoto() {
        setState(GalleryState.Loading)
        setState(GalleryState.Confirm(photoList.filter { it.isSelected }))
    }

    private fun setState(state: GalleryState) {
        galleryStateLiveData.postValue(state)
    }
}