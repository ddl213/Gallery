package com.example.pagergallery.unit

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.example.pagergallery.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

//加载图片
fun Context.loadImage(url: String?, view: ImageView) = Glide.with(this)
    .load(url)
    .placeholder(R.drawable.gray)
    .listener(object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>,
            isFirstResource: Boolean
        ): Boolean {
            logD("${e?.printStackTrace()}")
            return false
        }

        override fun onResourceReady(
            resource: Drawable,
            model: Any,
            target: Target<Drawable>?,
            dataSource: DataSource,
            isFirstResource: Boolean
        ): Boolean {
            return false
        }
    })
    .into(view)

fun Context.loadImage(url: Bitmap?, view: ImageView) = Glide.with(this)
    .load(url)
    .placeholder(R.drawable.gray)
    .listener(object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>,
            isFirstResource: Boolean
        ): Boolean {
            logD("${e?.printStackTrace()}")
            return false
        }

        override fun onResourceReady(
            resource: Drawable,
            model: Any,
            target: Target<Drawable>?,
            dataSource: DataSource,
            isFirstResource: Boolean
        ): Boolean {
            return false
        }
    })
    .into(view)

//获取网络服务是否可用
fun Context.isNetWorkAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    return getNetWorkAvailableQ(connectivityManager)
}

//获取网络状态
fun getNetWorkAvailableQ(connectivityManager: ConnectivityManager?): Boolean {
    return (connectivityManager?.getNetworkCapabilities(connectivityManager.activeNetwork)) != null
}

//下载图片
fun FragmentActivity.saveImage(path: String,account: Long) = Glide.with(this)
    .asBitmap()
    .load(path)
    .into(object : CustomTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO) {
                    //当安卓版本大于或等于29时 就可以直接进行包保存图片
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        resource.saveImageQ(this@saveImage,account)
                    } else {
                        resource.saveImage(this@saveImage,account)
                    }
                }
            }
            shortToast("下载中....").show()
        }

//        override fun onLoadStarted(placeholder: Drawable?) {
//            super.onLoadStarted(placeholder)
//        }

        override fun onLoadCleared(placeholder: Drawable?) {
        }
    })

//手机api小于29时调用
fun Bitmap.saveImage(activity: Activity,account : Long) {
    //拿到图片在sd卡中的路径
    val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path.toString()
    //创建一个文件夹去存储下载的图片
    val dir = File("$root${File.separator}${account}_gallery")
    //该文件夹不存在就创建一个出来
    if (!dir.exists()) dir.mkdirs()
    //创建该图片的下载路径（加上图片的名字）
    val file = File(dir, "pixabay_${System.currentTimeMillis()}.png")

    println(file.path)
    if (saveImageToStream(FileOutputStream(file)) != SAVE_IMAGE_SUCCESS) {
        activity.runOnUiThread { activity.shortToast("下载异常，已中断").show() }
        return
    }
    MediaScannerConnection.scanFile(activity, arrayOf(file.toString()), null) { _, _ -> }
    activity.runOnUiThread { activity.shortToast("下载完成").show() }
}

//手机api大于29时调用
@RequiresApi(Build.VERSION_CODES.Q)
fun Bitmap.saveImageQ(activity: Activity,account : Long) {
    //图片的描述
    val values = ContentValues().apply {
        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000)
        put(MediaStore.MediaColumns.DATE_TAKEN, System.currentTimeMillis())
        put(MediaStore.MediaColumns.DISPLAY_NAME,"pixabay_${System.currentTimeMillis()}")
        put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures${File.separator}${account}_gallery")
        put(MediaStore.MediaColumns.IS_PENDING, true)
    }
    //找到下载图片的路径
    val uri: Uri? = activity.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    if (uri != null) {
        //判断下载是否成功
        if (saveImageToStream(activity.contentResolver.openOutputStream(uri)) != SAVE_IMAGE_SUCCESS) {
            activity.runOnUiThread { activity.shortToast("下载异常，已中断").show() }
            return
        }
        values.put(MediaStore.MediaColumns.IS_PENDING, false)
        //在相册中刷新图片
        activity.contentResolver.update(uri, values, null, null)
    }
    activity.runOnUiThread { activity.shortToast("下载完成").show() }
}

//开始下载图片
fun Bitmap.saveImageToStream(outputStream: OutputStream?): Int? = outputStream?.let {
    try {
        compress(Bitmap.CompressFormat.PNG, 100, it)
        it.flush()
        SAVE_IMAGE_SUCCESS
    } catch (e: Exception) {
        SAVE_IMAGE_FAIL
    } finally {
        it.close()
    }
}

//弹出键盘
fun Context.showSoftInput(view: View) {
    (this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(
        view,
        0
    )
}

//隐藏键盘
fun Context.hideSoftInput(view: View) {
    (this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
        view.windowToken,
        0
    )
}

//弹出提示
fun Context.shortToast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT)!!

//打印日志
fun logD(text: String) { Log.d("myLogD", text) }

//开启fragment协程
inline fun Fragment.launchAndRepeatLifecycle(
    mLifecycleState : Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block : suspend CoroutineScope.() -> Unit
){
    lifecycleScope.launch {
        repeatOnLifecycle(mLifecycleState){
            block()
        }
    }
}

/**
 * Compose
 * */



