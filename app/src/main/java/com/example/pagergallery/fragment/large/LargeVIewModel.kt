package com.example.pagergallery.fragment.large

import android.app.Application
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.pagergallery.repository.Repository
import com.example.pagergallery.repository.api.Item
import com.example.pagergallery.repository.local.tables.cache.Cache
import com.example.pagergallery.repository.local.tables.collection.Collection
import com.example.pagergallery.repository.local.tables.download.DownLoad
import com.example.pagergallery.unit.isNetWorkAvailable
import com.example.pagergallery.unit.logD
import com.example.pagergallery.unit.saveImage
import com.example.pagergallery.unit.shortToast
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LargeVIewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository.getInstance(getApplication())
    private val collectionDaoUtil = repository.getCollectionDaoUtil()
    private val _collectState = MutableStateFlow(false)
    val collectState: StateFlow<Boolean> get() = _collectState
    fun setCollectState(state: Boolean) {
        _collectState.value = state
    }

    val starState = mutableStateOf(false)

    private val _photoListLiveData = MutableLiveData<List<Item>>()
    val photoListLiveData: LiveData<List<Item>> get() = _photoListLiveData
    fun setListLiveData(list: List<Item>) {
        _photoListLiveData.value = list
    }

    private val cacheDaoUtil = repository.getCacheDaoUtil()
    fun cache(pos: Int) {
        val uid = repository.user.value?.id ?: return
        viewModelScope.launch {
            photoListLiveData.value!![pos].apply {
                cacheDaoUtil.insert(Cache(this.id, this, System.currentTimeMillis(), uid))
            }
        }
    }


    //设置每个图片是否收藏
    private var leftIndex = -1
    private var rightIndex = -1
    suspend fun setCollectState(pos: Int) {
        if (repository.user.value == null) return
        if (pos in leftIndex + 5..rightIndex - 5) return

        if (leftIndex == -1 && rightIndex == -1) {
            setCollectState(pos, true)
        } else {
            setCollectState(pos, false)
        }

    }

    private suspend fun setCollectState(pos: Int, isFirstLoad: Boolean) {
        val left: Int?
        val right: Int?

        if (photoListLiveData.value!!.size <= 25) {
            left = 0
            right = photoListLiveData.value!!.size
            leftIndex = 0
            rightIndex = photoListLiveData.value!!.size
        } else {
            if (pos <= 15) {
                left = 0
                right = if (!isFirstLoad) pos - 5 else pos.plus(10).also { rightIndex = it }
                leftIndex = -5

                logD("setCollectedState1 : $leftIndex : $rightIndex")
            } else if (pos >= photoListLiveData.value!!.size - 15) {
                right = photoListLiveData.value!!.size.also { rightIndex = it + 5 }
                left = if (!isFirstLoad) pos + 5 else pos.minus(10).also { leftIndex = it }
            } else if (!isFirstLoad) {
                if (pos - 5 <= leftIndex) {
                    pos.minus(5).apply {
                        left = pos - 10
                        right = pos
                        leftIndex = this
                    }
                } else {
                    pos.plus(5).apply {
                        left = pos
                        right = pos + 10
                        rightIndex = this
                    }
                }
            } else {
                left = pos.minus(10).also { leftIndex = it }
                right = pos.plus(10).also { rightIndex = it }
            }
        }

        _photoListLiveData.value!!.subList(left!!, right!!).apply {
            logD("left:$left,right:$right")
            forEach { item ->
                item.isCollected = isCollected(item.id)
            }
        }
    }

    //判断当前图片是否被收藏
    private suspend fun isCollected(id: Long) = collectionDaoUtil.isCollection(id,repository.user.value?.id!!)

    //点击收藏或移除收藏
    fun collectState(pos: Int) {
        val uid = repository.user.value?.id
        if (uid == null) {
            getApplication<Application>().shortToast("请登录之后再使用该功能").show()
            return
        }
        viewModelScope.launch {
            _photoListLiveData.value?.get(pos)?.apply item@{

                isCollected(this@item.id).apply state@{
                    if (this@item.isCollected) {
                        this@item.isCollected = false
                        removeColl(this@item.id, uid)
                    } else {
                        this@item.isCollected = true
                        addColl(this@item, uid)
                        logD("addColl")
                    }
                    _collectState.value = !this
                    starState.value = !this
                    this@item.isCollected = !this
                }
            }
            logD("${photoListLiveData.value?.get(pos)}")
        }
    }

    //移除收藏
    private suspend fun removeColl(id: Long, uid: Int) {
        collectionDaoUtil.deleteCollById(id, uid)
    }

    //添加收藏
    private suspend fun addColl(item: Item, uid: Int) {
        collectionDaoUtil.insertAllIgnore(
            Collection(
                item.id,
                Gson().toJson(item),
                System.currentTimeMillis(),
                uid
            )
        )
    }

    //获取图片类型
    fun getPicType() = repository.getPicType()


    //保存图片到相册
    fun saveImg(context: FragmentActivity, pos: Int) {
        if (!context.isNetWorkAvailable()) {
            context.shortToast("当前无网络").show()
            return
        }
        if (photoListLiveData.value.isNullOrEmpty()) {
            context.shortToast("出错了，下载失败").show()
        }
        repository.user.value?.account.apply {
            if (this == null) {
                context.shortToast("请登录")
                return
            }
            val item = photoListLiveData.value!![pos]
            context.saveImage(item, this)

        }
    }

    fun FragmentActivity.saveImage(item: Item, account: Long) {
        Glide.with(this)
            .asBitmap()
            .load(item.largeUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    shortToast("下载中....").show()
                    CoroutineScope(Dispatchers.IO).launch {
                        withContext(Dispatchers.IO) {
                            //当安卓版本大于或等于29时 就可以直接进行包保存图片
//                            val url = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                                resource.saveImageQ(this@saveImage, account)
//                            } else {
//                                resource.saveImage(this@saveImage, account)
//                            }
                            val url = resource.saveImage(this@saveImage, account)
                            if (!url.isNullOrEmpty()) {
                                item.localUrl = url
                            }
                            logD("保存到数据库： ${item.localUrl}")
                            val time = System.currentTimeMillis()
                            val uid = repository.user.value?.id ?: return@withContext
                            val value = DownLoad(item.id, item, time, uid)
                            repository.getDownLoadDaoUtil().insert(value)
                        }
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }


    fun setTitle(title: String) {
        repository.setTitle(title)
    }

    fun setVisible(visible: Boolean) {
        repository.setVisible(visible)
    }

}