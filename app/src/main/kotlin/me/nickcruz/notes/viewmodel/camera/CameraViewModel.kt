package me.nickcruz.notes.viewmodel.camera

import android.arch.lifecycle.ViewModel
import io.fotoapparat.photo.BitmapPhoto

/**
 * Created by Nick Cruz on 7/22/17
 */
class CameraViewModel : ViewModel() {
    var bitmapPhoto: BitmapPhoto? = null
}