package com.example.pagergallery.fragment.large

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pagergallery.repository.Repository
import com.example.pagergallery.repository.api.Item
import com.example.pagergallery.repository.local.tables.cache.Cache
import com.example.pagergallery.repository.local.tables.collection.Collection
import com.example.pagergallery.repository.local.tables.collection.CollectionDaoUtil
import com.example.pagergallery.unit.isNetWorkAvailable
import com.example.pagergallery.unit.logD
import com.example.pagergallery.unit.saveImage
import com.example.pagergallery.unit.shortToast
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LargeVIewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository.getInstance(getApplication())
    private val collectionDaoUtil: CollectionDaoUtil get() = repository.getCollectionDaoUtil()

    private val _collectState = MutableStateFlow(false)
    val collectState : StateFlow<Boolean> get() = _collectState
    fun setCollectState(state : Boolean) {
        _collectState.value = state
    }
    val starState = mutableStateOf(false)

    private val _photoListLiveData = MutableLiveData<List<Item>>()
    val photoListLiveData : LiveData<List<Item>> get() = _photoListLiveData
    fun setListLiveData(list: List<Item>){
        _photoListLiveData.value = list
    }

    private val cacheDaoUtil = repository.getCacheDaoUtil()
    fun cache(pos: Int){
        val uid = repository.user.value?.id ?: return
        viewModelScope.launch {
            photoListLiveData.value!![pos].apply {
                cacheDaoUtil.insertItemList(Cache(this.id,photoListLiveData.value!![pos],System.currentTimeMillis(),uid))
            }
        }
    }


    //设置每个图片是否收藏
    private var leftIndex = -1
    private var rightIndex = -1
    suspend fun setCollectState(pos: Int) {
        if (pos in leftIndex + 5..rightIndex - 5) return

        if (leftIndex == -1 && rightIndex == -1) {
            logD("setCollectedState : $pos true")
            setCollectState(pos, true)
        } else {
            logD("setCollectedState : $pos  false")
            setCollectState(pos, false)
        }

    }

    private suspend fun setCollectState(pos: Int, isFirstLoad: Boolean) {
        val left: Int?
        val right: Int?



        if (photoListLiveData.value!!.size <= 25){
            left = 0
            right =photoListLiveData.value!!.size
            leftIndex = 0
            rightIndex = photoListLiveData.value!!.size
        }else {
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
    private suspend fun isCollected(id: Long) = collectionDaoUtil.isCollection(id)

    //点击收藏或移除收藏
    fun collectState(pos: Int) {
        val uid = repository.user.value?.id
        if (uid == null){
            getApplication<Application>().shortToast("请登录之后再使用该功能").show()
            return
        }
        viewModelScope.launch {
            _photoListLiveData.value?.get(pos)?.apply item@{
                isCollected(this@item.id).apply state@{
                    if (this@state) {
                        removeColl(this@item.id)
                    } else {
                        addColl(this@item,uid)
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
    private suspend fun removeColl(id: Long) { collectionDaoUtil.deleteCollById(id) }

    //添加收藏
    private suspend fun addColl(item: Item,uid : Int){
        collectionDaoUtil.insertCollections(Collection(item.id, Gson().toJson(item),System.currentTimeMillis(),uid))
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
            if (this == null){
                context.shortToast("请登录")
                return
            }
            context.saveImage(photoListLiveData.value!![pos].webFormatURL,this)
        }


    }


    fun setTitle(title: String){
        repository.setTitle(title)
    }
    fun setVisible(visible : Boolean){
        repository.setVisible(visible)
    }

}