package com.example.pagergallery.fragment.large

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.pagergallery.R
import com.example.pagergallery.databinding.FragmentLargeViewBinding
import com.example.pagergallery.databinding.LargeViewCellBinding
import com.example.pagergallery.fragment.mine.download.ITEM_TYPE
import com.example.pagergallery.fragment.mine.download.PHOTO_LIST
import com.example.pagergallery.fragment.mine.download.POSITION
import com.example.pagergallery.repository.api.Item
import com.example.pagergallery.unit.base.BaseBindFragment
import com.example.pagergallery.unit.base.BaseViewHolder
import com.example.pagergallery.unit.base.adapterOf
import com.example.pagergallery.unit.enmu.FragmentFromEnum
import com.example.pagergallery.unit.launchAndRepeatLifecycle
import com.example.pagergallery.unit.loadImage
import com.example.pagergallery.unit.logD
import com.example.pagergallery.unit.shortToast
import com.example.pagergallery.unit.view.BottomAppBar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class LargeViewFragment :
    BaseBindFragment<FragmentLargeViewBinding>(FragmentLargeViewBinding::inflate) {

    private val viewModel by viewModels<LargeVIewModel>()
    private var shouldCache = false
    private var isDownLoad = false
    private var isVector = false

    private var itemCount = 0


    @Suppress("DEPRECATION")
    override fun initView() {
        val itemType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(ITEM_TYPE, FragmentFromEnum::class.java)
                ?: FragmentFromEnum.Error
        } else {
            arguments?.getSerializable(ITEM_TYPE) as FragmentFromEnum
        }
        val pos = arguments?.getInt(POSITION) ?: -1
        val list: List<Any>

        when (itemType) {
            FragmentFromEnum.DownLoad -> {
                isDownLoad = true
                list = getStringList()
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
            logD("$pos -- ${list.size}")
            requireContext().shortToast("发生了未知错误，请重新进入").show()
            return
        }
        itemCount = list.size
        //初始化状态栏
        initUI()

        //recyclerView适配器
        val mAdapter = adapterOf(
            list, LargeViewCellBinding::class.java,
            initViewHolder
        ) { holder, _, item ->
            if (!isDownLoad) {
                (item as Item).apply {
                    holder.itemView.context.loadImage((item).largeUrl, holder.binding.photoView)
                    isVector = this.type == "vector/svg"
                }
            } else {
                holder.itemView.context.loadImage((item as String), holder.binding.photoView)
            }
            logD("$pos,${holder.absoluteAdapterPosition},${item}")
        }

        binding.viewPager2.apply {
            offscreenPageLimit = 2
            adapter = mAdapter
            setCurrentItem(pos, false)
            registerOnPageChangeCallback(pageChangeCallBack)
        }
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

    private val pageChangeCallBack = object : OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            setCollectState(position)
            logD("onPageSelected:")
        }
    }

    private fun getStringList(): List<String> {
        return arguments?.getStringArrayList(PHOTO_LIST)?.toList() ?: listOf()
    }

    @Suppress("DEPRECATION")
    private fun getItemList(): List<Item> {
        val list = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            arguments?.getParcelableArrayList(PHOTO_LIST, Item::class.java) ?: listOf()
        else arguments?.getParcelableArrayList(PHOTO_LIST) ?: listOf()
        viewModel.setListLiveData(list)
        if (list.isNotEmpty() && list[0].type == "vector/svg") {
            isVector = true
        }
        return list
    }

    private fun initUI() {
        binding.layoutActionBar.layoutActionBar.setPadding(0, 120, 0, 0)
        requireActivity().window.apply {
            WindowCompat.setDecorFitsSystemWindows(this, false)
            attributes = attributes.apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    //如果没有这一行会显示异常
                    this.layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                }
            }
        }
    }

    override fun initData() {
        setCollectState(binding.viewPager2.currentItem)
    }

    override fun initEvent() {
        binding.layoutActionBar.tvBack.setOnClickListener {
            findNavController().navigateUp()
        }

        launchAndRepeatLifecycle {
            viewModel.collectState.collect{

            }
        }

        //显示收藏按钮图标以及收藏功能
        launchAndRepeatLifecycle {
            isImmerseImageModel.collect {
                setPicMode(
                    it,
                    if (it) R.color.black else R.color.white
                )
            }
        }
    }

    //设置收藏图标状态，并更改当前图片的收藏图标样式
    private fun setCollectState(pos: Int) {
        if (shouldCache) viewModel.cache(pos)
        if (!isDownLoad) {
            lifecycleScope.launch {
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
            itemCount
        )
    }

    private val isImmerseImageModel = MutableStateFlow(false)
    private val initViewHolder: (BaseViewHolder<LargeViewCellBinding>) -> Unit
        get() = {
            it.binding.photoView.apply {
                setOnViewTapListener { _, _, _ ->
                    isImmerseImageModel.value = !isImmerseImageModel.value
                }
                setOnScaleChangeListener { scaleFactor, _, _ ->
                    if (scaleFactor > 1.0 && !isImmerseImageModel.value) {
                        isImmerseImageModel.value = true
                    }
                }
            }
        }

    //点击切换看图模式
    private fun setPicMode(shouldHide: Boolean, color: Int) {
        if (shouldHide) {
            hideStatusBar()
            translationUp()
        } else {
            showStatusBar()
            translationDown()
        }
        if (!isVector) {
            binding.viewPager2.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    color
                )
            )
        }
    }

    //隐藏和显示状态栏
    @Suppress("DEPRECATION")
    private fun hideStatusBar() {
        requireActivity().window.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                insetsController?.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                insetsController?.hide(WindowInsets.Type.statusBars())
            } else {
                setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun showStatusBar() {
        requireActivity().window.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                insetsController?.show(WindowInsets.Type.statusBars())
            } else {
                clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            }
        }
    }

    private fun translationUp() {
        val view1 = binding.layoutActionBar.layoutActionBar
        val items = mutableListOf<Animator>()

        if (!isDownLoad) {
            items.add(ObjectAnimator.ofFloat(binding.bottomToolBar, "translationY", 0f, 50f))
            items.add(ObjectAnimator.ofFloat(binding.bottomToolBar, "alpha", 0.7f, 0f))
        }

        items.add(ObjectAnimator.ofFloat(view1, "translationY", 0f, -80f))
        items.add(ObjectAnimator.ofFloat(view1, "alpha", 0.7f, 0f))

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
            duration = 150
            start()
        }
    }

    private fun translationDown() {
        val view1 = binding.layoutActionBar.layoutActionBar
        val items = mutableListOf<Animator>()

        items.add(ObjectAnimator.ofFloat(view1, "translationY", -80f, 0f))
        items.add(ObjectAnimator.ofFloat(view1, "alpha", 1f))
        if (!isDownLoad) {
            items.add(ObjectAnimator.ofFloat(binding.bottomToolBar, "translationY", 50f, 0f))
            items.add(ObjectAnimator.ofFloat(binding.bottomToolBar, "alpha", 1f))
            binding.bottomToolBar.visibility = View.VISIBLE
        }
        view1.visibility = View.VISIBLE
        AnimatorSet().apply animator@{
            playTogether(items)
            duration = 150
            start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isImmerseImageModel.value) {
            showStatusBar()
        }
        if (shouldCache) WindowCompat.setDecorFitsSystemWindows(requireActivity().window, true)
    }
}