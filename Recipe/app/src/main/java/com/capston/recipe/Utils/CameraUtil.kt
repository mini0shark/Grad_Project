package com.capston.recipe.Utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CameraUtil(private val activity:Activity, private val context:Context){

    private val REQUEST_IMAGE_CAPTURE = 1

    private val PERMISSION_REQUEST_CODE: Int = 101

    private var mCurrentPhotoPath: String? = null

    fun takePicture(imageUri: Uri?, fragment: Fragment) {
        CropImage.activity(imageUri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setMultiTouchEnabled(true)
            .start(context, fragment)
    }



    fun checkPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context,
            android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    fun requestPermission() {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE)
    }
    fun checkRequestPermissionResult(requestCode: Int, grantResults: IntArray ){
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {

                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

//                    takePicture(null, )

                } else {
                    Toast.makeText(context, "카메라 권한이 필요한 작업입니다.  Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }

            else -> {

            }
        }
    }
}