package me.nickcruz.notes.view.camera.delegate

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.view.View
import io.fotoapparat.Fotoapparat
import io.fotoapparat.parameter.LensPosition
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.parameter.selector.*
import io.fotoapparat.view.CameraView
import io.reactivex.Completable
import kotlinx.android.synthetic.main.activity_camera.*
import me.nickcruz.notes.view.camera.delegate.permissions.PermissionsDelegate
import org.joda.time.DateTime
import java.io.File

/**
 * A CameraDelegate handles preparing and setting up a camera. It also manages its own lifecycle
 * events for whenever the camera should stop or start.
 *
 * This is best used in an activity that is wholly responsible for taking a picture.
 *
 * Created by Nick Cruz on 7/22/17
 */
class CameraDelegate(private val activity: LifecycleActivity) : LifecycleObserver {

    val permissionsDelegate = PermissionsDelegate(activity)
    var hasCameraPermission = false
    lateinit var fotoapparat: Fotoapparat
    lateinit var cameraView: CameraView

    companion object {
        private val PHOTO_DIR_LOCATION = "photos"
    }

    init {
        activity.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun prepareCamera() {
        cameraView = activity.cameraView
        hasCameraPermission = permissionsDelegate.hasCameraPermission()

        if (hasCameraPermission) {
            cameraView.visibility = View.VISIBLE
        } else {
            permissionsDelegate.requestCameraPermission()
        }

        fotoapparat = createFotoapparat()
        cameraView.setOnLongClickListener {
            fotoapparat.autoFocus()
            true
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun startCamera() {
        if (hasCameraPermission) {
            fotoapparat.start()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stopCamera() {
        if (hasCameraPermission) {
            fotoapparat.stop()
        }
    }

    fun takePicture() {
        val photoResult = fotoapparat.takePicture()
        val photoName = "${DateTime.now()}.jpg"

        photoResult?.saveToFile(File(activity.getExternalFilesDir(PHOTO_DIR_LOCATION), photoName))
    }

    fun resultGranted(requestCode: Int,
                      permissions: Array<String>,
                      grantResults: IntArray) {
        if (permissionsDelegate.resultGranted(requestCode, permissions, grantResults)) {
            fotoapparat.start()
        }
    }

    private fun createFotoapparat(): Fotoapparat {
        return Fotoapparat
                .with(activity)
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
                .build()
    }
}