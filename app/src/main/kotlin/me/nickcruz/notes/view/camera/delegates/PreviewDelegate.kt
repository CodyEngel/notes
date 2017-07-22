package me.nickcruz.notes.view.camera.delegates

import android.view.View
import io.fotoapparat.photo.BitmapPhoto
import kotlinx.android.synthetic.main.activity_camera.*
import me.nickcruz.notes.view.camera.CameraActivity

/**
 * Created by Nick Cruz on 7/22/17
 */
class PreviewDelegate(private val cameraActivity: CameraActivity) {

    private var showingPreview = false

    /**
     * Show the preview. A [BitmapPhoto] must be provided.
     * @param photo A [BitmapPhoto] to show in the preview.
     */
    fun show(photo: BitmapPhoto) {
        with (cameraActivity.previewView) {
            setImageBitmap(photo.bitmap)
            rotation = (-photo.rotationDegrees).toFloat()
        }

        togglePreview(true)
    }

    /**
     * @return true if this was handled, false if not.
     */
    fun onBackPressed() : Boolean {
        if (showingPreview) {
            togglePreview(false)
            return true
        }
        return false
    }

    /**
     * Hide the preview.
     */
    fun hide() {
        togglePreview(false)
    }

    private fun togglePreview(showPreview: Boolean) {
        with(cameraActivity) {
            if (showPreview) {
                cameraLayout.visibility = View.GONE

                previewView.visibility = View.VISIBLE
                closePreviewView.visibility = View.VISIBLE
                savePreviewView.show()
            } else {
                cameraLayout.visibility = View.VISIBLE

                previewView.visibility = View.GONE
                closePreviewView.visibility = View.GONE
                savePreviewView.hide()
            }
        }
        showingPreview = showPreview
    }
}