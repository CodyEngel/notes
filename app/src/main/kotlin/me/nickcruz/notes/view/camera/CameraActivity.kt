package me.nickcruz.notes.view.camera

import android.arch.lifecycle.LifecycleActivity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import io.fotoapparat.Fotoapparat
import io.fotoapparat.parameter.LensPosition
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.parameter.selector.*
import io.fotoapparat.result.transformer.SizeTransformers
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.content_camera.*
import me.nickcruz.notes.R
import me.nickcruz.notes.model.Note
import me.nickcruz.notes.view.camera.permissions.PermissionsDelegate
import java.io.File


/**
 * Created by Nick Cruz on 6/24/17
 */
class CameraActivity : LifecycleActivity() {

    companion object {
        val EXTRA_NOTE = "note"

        fun getStartIntent(context: Context, note: Note) =
                Intent(context, CameraActivity::class.java).apply { putExtra(EXTRA_NOTE, note) }
    }

    val permissionsDelegate = PermissionsDelegate(this)
    var hasCameraPosition = false

    private var fotoapparat: Fotoapparat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        setActionBar(toolbar)

        hasCameraPosition = permissionsDelegate.hasCameraPermission()

        if (hasCameraPosition) {
            cameraView.visibility = View.VISIBLE
        } else {
            permissionsDelegate.requestCameraPermission()
        }

        createFotoapparat()
        cameraView.setOnClickListener { takePicture() }
        cameraView.setOnLongClickListener {
            fotoapparat?.autoFocus()
            true
        }
    }

    override fun onStart() {
        super.onStart()
        if (hasCameraPosition) {
            fotoapparat?.start()
        }
    }

    override fun onStop() {
        super.onStop()
        if (hasCameraPosition) {
            fotoapparat?.stop()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (permissionsDelegate.resultGranted(requestCode, permissions as Array<String>, grantResults)) {
            fotoapparat?.start()
        }
    }

    private fun createFotoapparat() {
        fotoapparat = Fotoapparat
                .with(this)
                .into(cameraView)
                .previewScaleType(ScaleType.CENTER_CROP)
                .photoSize(AspectRatioSelectors.standardRatio(SizeSelectors.biggestSize()))
                .lensPosition(LensPositionSelectors.lensPosition(LensPosition.BACK))
                .focusMode(Selectors.firstAvailable(
                        FocusModeSelectors.continuousFocus(),
                        FocusModeSelectors.autoFocus(),
                        FocusModeSelectors.fixed()
                ))
                .flash(Selectors.firstAvailable(
                        FlashSelectors.autoRedEye(),
                        FlashSelectors.autoFlash(),
                        FlashSelectors.torch(),
                        FlashSelectors.off()
                ))
//                .frameProcessor(SampleFrameProcessor())
//                .logger(loggers(
//                        logcat(),
//                        fileLogger(this)
//                ))
                .build()
    }

    private fun takePicture() {
        val photoResult = fotoapparat?.takePicture()

        photoResult?.saveToFile(File(
                getExternalFilesDir("photos"),
                "photo.jpg"
        ))

        photoResult
                ?.toBitmap(SizeTransformers.scaled(0.25f))
                ?.whenAvailable({ result ->
                    val imageView = findViewById(R.id.result) as ImageView

                    imageView.setImageBitmap(result.bitmap)
                    imageView.rotation = (-result.rotationDegrees).toFloat()
                })
    }
}