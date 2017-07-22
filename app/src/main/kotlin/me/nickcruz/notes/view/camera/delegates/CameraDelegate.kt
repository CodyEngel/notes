package me.nickcruz.notes.view.camera.delegates

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.view.View
import io.fotoapparat.Fotoapparat
import io.fotoapparat.parameter.LensPosition
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.parameter.selector.*
import io.fotoapparat.photo.BitmapPhoto
import io.fotoapparat.view.CameraView
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_camera.*

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
    lateinit var camera: Fotoapparat
    lateinit var cameraView: CameraView

    companion object {
        private val PHOTO_DIR_LOCATION = "photos"
    }

    init {
        activity.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        cameraView = activity.cameraView
        hasCameraPermission = permissionsDelegate.hasCameraPermission()

        if (hasCameraPermission) {
            cameraView.visibility = View.VISIBLE
        } else {
            permissionsDelegate.requestCameraPermission()
        }

        camera = prepareCamera()
        cameraView.setOnLongClickListener {
            camera.autoFocus()
            true
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        if (hasCameraPermission) {
            camera.start()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        if (hasCameraPermission) {
            camera.stop()
        }
    }

    /**
     * Takes a picture. When the bitmap photo is available, this emits a Single of a [BitmapPhoto].
     */
    fun takePicture(): Single<BitmapPhoto> {
        return Single.create<BitmapPhoto> { observer ->
            camera.takePicture()
                    .toBitmap()
                    .whenAvailable { observer.onSuccess(it) }
        }
    }

    fun resultGranted(requestCode: Int,
                      permissions: Array<String>,
                      grantResults: IntArray) {
        if (permissionsDelegate.resultGranted(requestCode, permissions, grantResults)) {
            camera.start()
        }
    }

    private fun prepareCamera(): Fotoapparat {
        return Fotoapparat
                .with(activity)
                .into(cameraView)
                .previewScaleType(ScaleType.CENTER_INSIDE)
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