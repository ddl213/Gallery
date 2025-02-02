package com.example.pagergallery.repository

import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.pagergallery.R
import com.example.pagergallery.repository.api.Item
import com.example.pagergallery.repository.api.PagingDataSource
import com.example.pagergallery.repository.api.PixabayService
import com.example.pagergallery.repository.local.GalleryDatabase
import com.example.pagergallery.repository.local.tables.user.User
import com.example.pagergallery.unit.KeyValueUtils
import com.example.pagergallery.unit.enmu.ImageTypeEnum
import com.example.pagergallery.unit.logD
import com.example.pagergallery.unit.view.TopBar
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext


const val CURRENT_LOGIN_USER = "current_login_user"
class Repository private constructor(context: Context) {

    //设置单例模式，只能实例化一个对象
    companion object {
        private var repository: Repository? = null
        fun getInstance(context: Context) =
            repository ?: synchronized(this) {
                Repository(context).also { repository = it }
            }
    }

    //当前用户实例
    private val loginState = MutableStateFlow(false)
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    //是否保存用户信息
    private val saveState = MutableStateFlow(false)

    fun getLoginState() = loginState.value

    init {
        initLoginState()
    }

    //获取登录状态
    fun initLoginState() {
        val value = KeyValueUtils.getString(CURRENT_LOGIN_USER)
        if (value.isNullOrEmpty()) return

        _user.value = Gson().fromJson(value, User::class.java)
        loginState.value = true
    }

    fun saveUserInfo() {
        if (!saveState.value) return
        saveState.value = false
        val value = if (user.value == null) "" else Gson().toJson(user.value)
        KeyValueUtils.setString(CURRENT_LOGIN_USER,value)
    }

    fun setUser(user: User?) {
        if (_user.value == user) return
        if (!saveState.value) saveState.value = true

        _user.value = user
        loginState.value = user != null
    }



    //获取数据库实例
    private val galleryDB by lazy {
        GalleryDatabase.getInstance(context)
    }

    //收藏表
    private val collDao by lazy { galleryDB.getCollectionDao() }
    fun getCollectionDaoUtil() = collDao

    //查询表
    private val queryDao by lazy { galleryDB.getQueryDao() }
    fun getQueryDaoUtil() = queryDao

    //缓存表
    private val cacheDao by lazy { galleryDB.getCacheDao() }
    fun getCacheDaoUtil() = cacheDao

    //下载表
    private val downloadDao by lazy { galleryDB.getDownLoadDao() }
    fun getDownLoadDaoUtil() = downloadDao

    //缓存表
    private val userDao by lazy { galleryDB.getUserDao() }
    fun getUserDaoUtil() = userDao

    private val service by lazy { PixabayService().createPixabayService() }
    private val _galleryListLiveDate = MutableLiveData<MutableMap<String, List<Item>>>()
    private val _galleryQueryListLiveDate = MutableLiveData<MutableMap<String, List<Item>>>()

    private val imageTypeLiveData = MutableLiveData(ImageTypeEnum.ALL)//图片类别
    private val imageTypeStrLiveData = MutableStateFlow("all")//图片类别

    private val orderLiveData = MutableLiveData("popular")//图片排序顺序
    private val queryStrLiveData = MutableLiveData("")//图片查询信息

    //暴露给外界获取只读数据
    val galleryListLiveDate: LiveData<MutableMap<String, List<Item>>>
        get() {
            return if (queryStrLiveData.value.isNullOrEmpty())
                _galleryListLiveDate
            else
                _galleryQueryListLiveDate

        }

    //获取图片,开启协程防止阻塞主线程
    suspend fun getPic(q: String?, page: Int): List<Item> {
        val list = mutableListOf<Item>()
        val type = imageTypeStrLiveData.value
        //开启IO协程
        withContext(Dispatchers.IO) {
            //获取图片信息列表
            val respPixabay =
                service.getData(q, page, 20, type, orderLiveData.value.toString())//从Api上获取图片
            respPixabay.body()?.hits?.let {
                //存储到变量中
                list.addAll(it)
            }
        }

        setGalleryListLiveData(list, type)
        return list
    }

    //获取分页数据
    fun getPagingData(): Flow<PagingData<Item>> = Pager(
        config = PagingConfig(20),
        pagingSourceFactory = { PagingDataSource(this, queryStrLiveData.value) }
    ).flow

    //重新加载
    private val _reFresh = MutableStateFlow(false)
    fun setReFresh(state: Boolean) {
        _reFresh.value = state
    }

    val reFresh: StateFlow<Boolean> get() = _reFresh


    /**
     * 获取与赋值图片列表
     */
    //获取图片列表
    private fun setGalleryListLiveData(list: List<Item>, type: String) {
        if (list.isEmpty()) {
            return
        }
        val isDefault = queryStrLiveData.value.isNullOrEmpty()
        logD("$isDefault")
        if (galleryListLiveDate.value.isNullOrEmpty()) {
            if (isDefault)
                _galleryListLiveDate.value = mutableMapOf(type to list)
            else
                _galleryQueryListLiveDate.value = mutableMapOf(type to list)
            return
        }
        if (isDefault) {
            if (!_galleryListLiveDate.value!!.containsKey(type)) {
                _galleryListLiveDate.value!![type] = list
            } else {
                _galleryListLiveDate.value.apply { this!![type] = this[type]!!.plus(list) }
            }
            logD("_galleryListLiveDate:size ${_galleryListLiveDate.value!![type]?.size}")
        } else {
            if (!_galleryQueryListLiveDate.value!!.containsKey(type)) {
                _galleryQueryListLiveDate.value!![type] = list
            } else {
                _galleryQueryListLiveDate.value.apply { this!![type] = this[type]!!.plus(list) }
            }
        }
    }

    fun clearQueryList() {
        _galleryQueryListLiveDate.value?.clear()
    }

    //设置图片类别
    fun setImageType(type: ImageTypeEnum) {
        imageTypeLiveData.value = type
    }

    fun getPicType() = imageTypeLiveData.value

    fun setImageTypeStr(type: String) {
        imageTypeStrLiveData.value = type
    }

    fun getPicTypeStr() = imageTypeStrLiveData

    //设置图片排序方式
    fun setOrder(order: String) {
        orderLiveData.value = order
    }

    fun getOrderLive() = orderLiveData

    //获取查询字符串
    fun setQuery(queryStr: String?) {
        if (queryStrLiveData.value != queryStr)
            queryStrLiveData.value = queryStr
    }

    fun getQuery() = queryStrLiveData

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> get() = _title

    private val _visible = MutableStateFlow(false)
    val visible: StateFlow<Boolean> get() = _visible

    private val _canBack = MutableStateFlow(false)
    val canBack: StateFlow<Boolean> get() = _canBack

    private val _color = MutableStateFlow(TopBar(R.color.teal_700, visible = false, false))
    val color: StateFlow<TopBar> get() = _color

    //设置标题栏可见性
    fun setTopAppBarVisible(
        activity: FragmentActivity,
        visible: Boolean,
        canBack: Boolean,
        color: Int?,
        title: String = ""
    ) {
        if (this.visible.value != visible) {
            _visible.value = visible
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                activity.window.setDecorFitsSystemWindows(!visible)
            }
            if (this.canBack.value != canBack && visible) {
                _canBack.value = canBack
            }
        }

        setStatusBarColor(activity, color)
        _title.value = title
    }

    private fun setStatusBarColor(activity: FragmentActivity, color: Int?) {
        if (activity.window.statusBarColor != color) {
            activity.window.statusBarColor = if (color != null) activity.resources.getColor(
                color,
                null
            ) else Color.TRANSPARENT
        }
    }

    fun setTitle(title: String) {
        _title.value = title
    }

    fun setVisible(visible: Boolean) {
        _visible.value = visible
    }
}