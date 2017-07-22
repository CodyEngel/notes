package me.nickcruz.notes.view.camera

import android.arch.lifecycle.LifecycleActivity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import butterknife.OnClick
import me.nickcruz.notes.R
import me.nickcruz.notes.model.Note
import me.nickcruz.notes.view.camera.delegate.CameraDelegate


/**
 * Created by Nick Cruz on 6/24/17
 */
class CameraActivity : LifecycleActivity() {

    companion object {
        val EXTRA_NOTE = "note"

        fun getStartIntent(context: Context, note: Note) =
                Intent(context, CameraActivity::class.java).apply { putExtra(EXTRA_NOTE, note) }
    }

    val cameraDelegate = CameraDelegate(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        cameraDelegate.resultGranted(requestCode, permissions as Array<String>, grantResults)
    }

    @OnClick(R.id.fab)
    fun shutterClicked() {
        cameraDelegate.takePicture()

        // TODO: Show preview of photo.
    }
}