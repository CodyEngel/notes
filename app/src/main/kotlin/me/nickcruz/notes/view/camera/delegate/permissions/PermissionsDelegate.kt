package me.nickcruz.notes.view.camera.delegate.permissions

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

/**
 * Created by Nick Cruz on 7/22/17
 */
class PermissionsDelegate(private val activity: Activity) {

    companion object {
        private val REQUEST_CODE = 10
    }

    internal fun hasCameraPermission(): Boolean {
        val permissionCheckResult = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
        )
        return permissionCheckResult == PackageManager.PERMISSION_GRANTED
    }

    internal fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CODE
        )
    }

    internal fun resultGranted(requestCode: Int,
                               permissions: Array<String>,
                               grantResults: IntArray): Boolean {

        if (requestCode != REQUEST_CODE) {
            return false
        }

        if (grantResults.isEmpty()) {
            return false
        }

        if (permissions[0] != Manifest.permission.CAMERA) {
            return false
        }

        //        View noPermissionView = activity.findViewById(R.id.no_permission);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //            noPermissionView.setVisibility(View.GONE);
            return true
        }

        requestCameraPermission()
        //        noPermissionView.setVisibility(View.VISIBLE);
        return false
    }
}

