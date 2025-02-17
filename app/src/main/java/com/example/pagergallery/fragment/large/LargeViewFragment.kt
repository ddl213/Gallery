package com.example.pagergallery.fragment.large

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.pagergallery.R
import com.example.pagergallery.databinding.FragmentLargeViewBinding
import com.example.pagergallery.databinding.LargeViewCellBinding
import com.example.pagergallery.fragment.me.download.ITEM_TYPE
import com.example.pagergallery.fragment.me.download.PHOTO_LIST
import com.example.pagergallery.fragment.me.download.POSITION
import com.example.pagergallery.repository.api.Item
import com.example.pagergallery.unit.base.adapter.adapterOf
import com.example.pagergallery.unit.base.fragment.BaseBindFragment
import com.example.pagergallery.unit.enmu.FragmentFromEnum
import com.example.pagergallery.unit.launchAndRepeatLifecycle
import com.example.pagergallery.unit.loadImage
import com.example.pagergallery.unit.logD
import com.example.pagergallery.unit.shortToast
import com.example.pagergallery.unit.util.IConstStringUtil
import com.example.pagergallery.unit.util.KeyValueUtils
import com.example.pagergallery.unit.util.LogUtil
import com.example.pagergallery.unit.view.BottomAppBar
import kotlinx.coroutines.flow.MutableStateFlow


class LargeViewFragment :
    BaseBindFragment<FragmentLargeViewBinding>(FragmentLargeViewBinding::inflate) {

    private val viewModel by viewModels<LargeVIewModel>()
    private var shouldCache = false
    private var isDownLoad = false
    private var isVector = false

    private val window by lazy { requireActivity().window }
    private val controller by lazy {
        WindowCompat.getInsetsController(
            window,
            window.decorView
        )
    }
    private val propertyName = "translationY"

    private val mAdapter = adapterOf<Item, LargeViewCellBinding>(
        LargeViewCellBinding::class.java,
    ) { holder, _, item ->
        val url = if (isDownLoad) {
            item?.localUrl
        } else {
            item?.largeUrl
        }

        holder.itemView.context.loadImage(url, holder.binding.photoView, isDownLoad)
        isVector = item?.type == "vector/svg"
    }


    /**initData()*/
    @Suppress("DEPRECATION")
    override fun initView() {
        //适配低版本获取序列化查看的图片类型
        val itemType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(ITEM_TYPE, FragmentFromEnum::class.java)
                ?: FragmentFromEnum.Error
        } else {
            arguments?.getSerializable(ITEM_TYPE) as FragmentFromEnum
        }

        //当前选择的元素下标和图片数据
        val pos = arguments?.getInt(POSITION) ?: -1
        val list: MutableList<Item>

        //根据图片来源位置，选择是否显示底部工具栏
        when (itemType) {
            FragmentFromEnum.DownLoad -> {
                isDownLoad = true
                list = getItemList()
            }

            FragmentFromEnum.Collect,
            FragmentFromEnum.History,
            FragmentFromEnum.Gallery -> {
                list = getItemList()
                shouldShowBottomBar()
                if (itemType == FragmentFromEnum.Gallery) {
                    shouldCache = true
                }

            }

            else -> {
                requireContext().shortToast("发生了未知错误，请重新进入").show()
                return
            }
        }

        if (pos == -1 || list.isEmpty()) {
            LogUtil.d("$pos -- ${list.size}")
            requireContext().shortToast("获取图片信息失败，请重新进入").show()
            return
        }
        initUI()

        mAdapter.setNewInstance(list)

        binding.viewPager2.apply {
            offscreenPageLimit = 2
            adapter = mAdapter
            setCurrentItem(pos, false)
            setPageTransformer(MarginPageTransformer(30))
            setCollectState(currentItem)
        }
    }

    //设置侵入挖孔屏，否则系统将会留出挖孔区域，该区域通常为黑色
    private fun initUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        window.attributes = window.attributes.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                //如果没有这一行会显示异常
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun getItemList(): MutableList<Item> {
        val list = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            arguments?.getParcelableArrayList(PHOTO_LIST, Item::class.java) ?: mutableListOf()
        else arguments?.getParcelableArrayList(PHOTO_LIST) ?: mutableListOf()

        viewModel.setListLiveData(list)
        if (list.isNotEmpty() && list[0].type == "vector/svg") {
            isVector = true
        }
        return list
    }

    private fun shouldShowBottomBar() {
        binding.bottomToolBar.setContent {
            BottomAppBar(viewModel.starState.value) {
                if (it) {
                    viewModel.collectState(binding.viewPager2.currentItem)
                } else {
                    viewModel.saveImg(requireActivity(), binding.viewPager2.currentItem)
                }
            }
        }
    }


    /**initData()*/
    override fun initData() {
    }



    /**initEvent()*/
    override fun initEvent() {
        binding.layoutActionBar.tvBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.viewPager2.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCollectState(position)
                LogUtil.d("onPageSelected:")
            }
        })


        setPhotoOnTouchListener()
        initLifecycleScope()
    }


    //设置图片触摸监听
    private fun setPhotoOnTouchListener() {
        mAdapter.addDoubleAndScaleListener(R.id.photoView)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            isImmerseImageModel.value = !isImmerseImageModel.value
        }
        mAdapter.setOnItemScaleListener { adapter, view, position, scaleFactor ->
            if (scaleFactor > 1.0 && !isImmerseImageModel.value) {
                isImmerseImageModel.value = true
            }
        }


    }

    /**
     * 初始化协程
     */
    private fun initLifecycleScope() {
        launchAndRepeatLifecycle {
            viewModel.collectState.collect {

            }
        }

        //显示收藏按钮图标以及收藏功能
        launchAndRepeatLifecycle {
            isImmerseImageModel.collect {
                setPicMode(
                    it,
                    if (it) Color.BLACK else resources.getColor(R.color.app_color, null)
                )
            }
        }
    }

    /**设置收藏图标状态，并更改当前图片的收藏图标样式*/
    private fun setCollectState(pos: Int) {
        if (shouldCache) viewModel.cache(pos)
        if (!isDownLoad) {
            launchAndRepeatLifecycle {
                viewModel.setCollectState(pos)
                viewModel.photoListLiveData.value!![pos].isCollected.let {
                    viewModel.setCollectState(it)
                    viewModel.starState.value = it
                }
            }
        }
        binding.layoutActionBar.textView2.text = resources.getString(
            R.string.pic_count,
            pos + 1,
            mAdapter.itemCount
        )
    }

    /**沉浸看图模式*/
    private val isImmerseImageModel = MutableStateFlow(false)


    /**点击切换看图模式*/
    private fun setPicMode(shouldHide: Boolean, color: Int) {
        if (!isVector) {
            binding.root.setBackgroundColor(color)
        }
        if (shouldHide) {
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.hide(WindowInsetsCompat.Type.statusBars())
            translationUp()
        } else {
            controller.show(WindowInsetsCompat.Type.statusBars())
            translationDown()
        }
    }

    private fun translationUp() {
        val view1 = binding.layoutActionBar.layoutActionBar
        val items = mutableListOf<Animator>()

        items.add(ObjectAnimator.ofFloat(view1, propertyName, 0f, -240f))
        if (!isDownLoad) {
            items.add(ObjectAnimator.ofFloat(binding.bottomToolBar, propertyName, 0f, 150f))
        }

        AnimatorSet().apply animator@{
            playTogether(items)
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    view1.visibility = View.GONE
                    if (!isDownLoad) binding.bottomToolBar.visibility = View.GONE
                }
            })
            duration = 250
            start()
        }
    }

    private fun translationDown() {
        val view1 = binding.layoutActionBar.layoutActionBar
        val items = mutableListOf<Animator>()
        view1.visibility = View.VISIBLE
        items.add(ObjectAnimator.ofFloat(view1, propertyName, -60f, 0f))
        if (!isDownLoad) {
            binding.bottomToolBar.visibility = View.VISIBLE
            items.add(ObjectAnimator.ofFloat(binding.bottomToolBar, propertyName, 50f, 0f))
        }
        AnimatorSet().apply animator@{
            playTogether(items)
            duration = 300
            start()
        }
    }

    override fun onDestroy() {
        if (isImmerseImageModel.value || shouldCache) {
            controller.show(WindowInsetsCompat.Type.statusBars())
        }
        KeyValueUtils.setInt(IConstStringUtil.SCROLL_POSITION,binding.viewPager2.currentItem)
        super.onDestroy()
    }
}