package com.example.pagergallery.unit

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.pagergallery.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//加载图片
fun Context.loadImage(url: String?, view: ImageView, isDownLoad: Boolean) {

//    val u = if (isDownLoad) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            Uri.parse(url ?: "")
//        } else {
//            logD("")
//            url
//        }
//        url
//    } else url

    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.gray)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                logD("图片加载失败：${e}")
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
        .thumbnail(
            Glide.with(this)
                .load(url)
                .sizeMultiplier(0.1f) // 加载原图的 10% 作为缩略图
        )
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(view)
}


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


//通过图片路径转换为bitmap
fun compressBitmapFromPath(context: Context, imagePath: String, quality: Int = 100): ByteArray? {
    return try {
        // 创建字节输出流，用于存储压缩后的数据
        val outputStream = ByteArrayOutputStream()
        CoroutineScope(Dispatchers.IO).launch {
            val inputStream = context.contentResolver.openInputStream(imagePath.toUri())
            // 从文件路径加载 Bitmap
            val bitmap = BitmapFactory.decodeStream(inputStream)

            withContext(Dispatchers.IO) {
                inputStream?.close()
            }

            // 压缩 Bitmap 到指定质量
            bitmap.compress(Bitmap.CompressFormat.WEBP, quality, outputStream)
            bitmap.recycle()

        }
        // 返回字节数组
        outputStream.toByteArray()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


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

//手机api小于29时调用
fun Bitmap.saveImage(activity: Activity, account: Long): String? {
    //拿到图片在sd卡中的路径
    val root =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path.toString()
    //创建一个文件夹去存储下载的图片
    val dir = File("$root${File.separator}${account}_gallery")
    //该文件夹不存在就创建一个出来
    if (!dir.exists()) dir.mkdirs()
    //创建该图片的下载路径（加上图片的名字）
    val file = File(dir, "pixabay_${System.currentTimeMillis()}.png")

    if (saveImageToStream(FileOutputStream(file)) != SAVE_IMAGE_SUCCESS) {
        activity.runOnUiThread { activity.shortToast("下载异常，已中断").show() }
        return null
    }
    MediaScannerConnection.scanFile(activity, arrayOf(file.toString()), null) { _, _ -> }
    activity.runOnUiThread { activity.shortToast("下载完成").show() }

    return file.path
}

//手机api大于29时调用
@RequiresApi(Build.VERSION_CODES.Q)
fun Bitmap.saveImageQ(activity: Activity, account: Long): String? {
    val contentResolver = activity.contentResolver
    //图片的描述
    val values = ContentValues().apply {
        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000)
        put(MediaStore.MediaColumns.DATE_TAKEN, System.currentTimeMillis())
        put(
            MediaStore.MediaColumns.DISPLAY_NAME, "pixabay_${
                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
                    Date()
                )
            }"
        )
        put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures${File.separator}${account}_gallery")
        put(MediaStore.MediaColumns.IS_PENDING, true)
    }
    //找到下载图片的路径
    val uri: Uri? = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    logD(uri.toString())
    if (uri != null) {
        //判断下载是否成功
        if (saveImageToStream(contentResolver.openOutputStream(uri)) != SAVE_IMAGE_SUCCESS) {
            activity.runOnUiThread { activity.shortToast("下载异常，已中断").show() }
            return null
        }
        values.put(MediaStore.MediaColumns.IS_PENDING, false)
        //在相册中刷新图片
        contentResolver.update(uri, values, null, null)
    }
    activity.runOnUiThread { activity.shortToast("下载完成").show() }
    val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
    intent.data = uri
    activity.sendBroadcast(intent)

    return uri.toString()
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
fun logD(text: String) {
    Log.d("myLogD", text)
}

fun logD(tag : String ,text: String) {
    Log.d(tag, text)
}

//开启fragment协程
inline fun Fragment.launchAndRepeatLifecycle(
    mLifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(mLifecycleState) {
            block()
        }
    }
}

/**
 * Compose
 * */



