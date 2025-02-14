package com.example.pagergallery.unit.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.example.pagergallery.unit.logD
import com.example.pagergallery.unit.shortToast
import com.example.pagergallery.unit.util.LogUtil

class PermissionUtil constructor(val context: Context, val function : () -> Unit,
     private val requestPermission : ActivityResultLauncher<String>
) {

    private val permissionScreenState: MutableState<PermissionScreenState> by lazy {
        mutableStateOf(getState())
    }

    private fun handleClick(pos: Int) {
        when {
            isPermissionGranted() -> {
                context.shortToast("download...").show()
                function()
            }

            shouldShowRequestPermissionRationale(context as Activity,Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                LogUtil.d("shouldShowRequestPermissionRationale: ")
                permissionScreenState.value = PermissionScreenState(
                    title = "Download a picture",
                    buttonText = "Grant permission",
                    rationale = "In order to perform the call you need to grant access by accepting the next permission dialog.\n\nWould you like to continue?"
                )
            }

            else -> {
                LogUtil.d("requestPermission.launch: ")
                requestPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private fun getState(): PermissionScreenState {
        return if (isPermissionGranted()) {
            PermissionScreenState(
                title = "You can download a picture!", buttonText = "Download"
            )
        } else {
            PermissionScreenState(
                title = "Download a picture", buttonText = "Grant permission"
            )
        }
    }

    private fun isPermissionGranted() = ContextCompat.checkSelfPermission(
        context, Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED



}