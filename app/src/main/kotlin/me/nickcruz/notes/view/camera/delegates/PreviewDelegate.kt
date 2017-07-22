package me.nickcruz.notes.view.camera.delegates

import android.support.design.widget.FloatingActionButton
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
                cameraView.visibility = View.GONE
                previewView.visibility = View.VISIBLE

                shutterView.toggleTo(savePreviewView)
            } else {
                cameraView.visibility = View.VISIBLE
                previewView.visibility = View.GONE

                savePreviewView.toggleTo(shutterView)
            }
        }
        showingPreview = showPreview
    }

    fun FloatingActionButton.toggleTo(thenShow: FloatingActionButton) {
        hide(object : FloatingActionButton.OnVisibilityChangedListener() {
            override fun onHidden(fab: FloatingActionButton?) {
                thenShow.show()
            }
        })
    }
}