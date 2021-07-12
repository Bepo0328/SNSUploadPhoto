package kr.co.bepo.snsuploadapp.gallery

sealed class GalleryState {

    object UnInitialized : GalleryState()

    object Loading : GalleryState()

    data class Success(
        val photoList: List<GalleryPhoto>,
    ) : GalleryState()

    data class Confirm(
        val photoList: List<GalleryPhoto>
    ) : GalleryState()
}