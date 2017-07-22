package me.nickcruz.notes.view.camera

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.activity_camera.*
import me.nickcruz.notes.R
import me.nickcruz.notes.model.Note
import me.nickcruz.notes.view.base.BaseActivity
import me.nickcruz.notes.view.camera.delegates.CameraDelegate
import me.nickcruz.notes.view.camera.delegates.PreviewDelegate
import me.nickcruz.notes.viewmodel.camera.CameraViewModel


/**
 * Created by Nick Cruz on 6/24/17
 */
class CameraActivity : BaseActivity() {

    companion object {
        val EXTRA_NOTE = "note"

        fun getStartIntent(context: Context, note: Note) =
                Intent(context, CameraActivity::class.java).apply { putExtra(EXTRA_NOTE, note) }
    }

    private val cameraDelegate = CameraDelegate(this)
    private val previewDelegate = PreviewDelegate(this)
    private lateinit var cameraViewModel: CameraViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        cameraViewModel = ViewModelProviders.of(this)
                .get(CameraViewModel::class.java)

        shutterView.clicks()
                .flatMapSingle { cameraDelegate.takePicture() }
                .subscribe {
                    cameraViewModel.bitmapPhoto = it
                    previewDelegate.show(it)
                }
                .attachToLifecycle()

        savePreviewView
                .clicks()
                .subscribe { previewDelegate.hide() }
                .attachToLifecycle()
    }

    override fun onBackPressed() {
        if (!previewDelegate.onBackPressed()) {
            super.onBackPressed()
        }
    }


    @Suppress("UNCHECKED_CAST")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        cameraDelegate.resultGranted(requestCode, permissions as Array<String>, grantResults)
    }
}