package com.example.pagergallery

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.example.pagergallery.databinding.ActivityMainBinding
import com.example.pagergallery.repository.Repository
import com.example.pagergallery.unit.base.viewmodel.BaseViewModel
import com.example.pagergallery.unit.base.viewmodel.MyViewModelFactory
import com.example.pagergallery.unit.logD
import com.example.pagergallery.unit.view.AppBarCompose
import com.example.pagergallery.unit.view.BottomItem
import com.example.pagergallery.unit.view.BottomNavigationCompose
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val viewModel by viewModels<BaseViewModel> {
        MyViewModelFactory(application.applicationContext, BaseViewModel::class.java)
    }

    private val visible = mutableStateOf(false)
    private val canBack = mutableStateOf(false)
    private val title = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityMainBinding.inflate(layoutInflater).let { binding = it }
        enableEdgeToEdge()
        setContentView(binding.root)

        initView()
        MMKV.initialize(this)//78525483
        Repository.getInstance(this).initLoginState()

        //获取异步数据
        collect()
    }

    private fun initView() {
        //获取NavController
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHost.navController
        navController.addOnDestinationChangedListener(this)
        binding.topBar.setContent {
            AppBarCompose(visible.value, title.value ?: "", canBack.value) {
                navController.popBackStack()
            }
        }

        binding.botNavComposeView.setContent {
            BottomNavigationCompose(visible.value) {
                when (it) {
                    R.id.mineFragment, R.id.collectionFragment -> {
                        bnvNavigate(it)
                    }

                    R.id.baseFragment -> {
                        bnvNavigate(it)
                    }
                }
            }
        }
    }

    private fun collect() {
        //设置标题
        lifecycleScope.launch {
            viewModel.title.collect {
                title.value = it
                logD(it)
            }
        }
    }

    private fun bnvNavigate(itemId: Int): Boolean {
        if (navController.currentDestination?.id != itemId) {
            navController.popBackStack()
            navController.navigate(itemId)
            return false
        }
        return true
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.baseFragment, R.id.mineFragment -> {
                setTopAppBarVisible(visible = false, canBack = false)
            }

            R.id.collectionFragment -> {
                setTopAppBarVisible(visible = true, canBack = false)
            }

            R.id.queryFragment, R.id.showQueryFragment, R.id.largeViewFragment -> {
                setTopAppBarVisible(visible = false, canBack = false, botVisible = View.GONE)
            }

            else -> {
                setTopAppBarVisible(visible = true, canBack = true, botVisible = View.GONE)
            }
        }
    }

    //设置标题栏可见性
    private fun setTopAppBarVisible(
        visible: Boolean,
        canBack: Boolean,
        botVisible: Int = View.VISIBLE
    ) {
        if (this.visible.value != visible) {
            this.visible.value = visible
        }
        if (this.canBack.value != canBack && visible) {
            this.canBack.value = canBack
        }
        if (botVisible != binding.botNavComposeView.visibility) {
            binding.botNavComposeView.visibility = botVisible
        }
    }

    @Composable
    fun BottomNavigationCompose(
        shouldPadding: Boolean,
        onItemSelect: (Int) -> Unit
    ) {
        val mBottomTabItems =
            listOf(
                BottomItem(
                    "首页",
                    null,
                    R.drawable.ic_baseline_voicemail_24,
                    R.id.baseFragment
                ),
                BottomItem(
                    "收藏",
                    null,
                    R.drawable.ic_baseline_star_border_24,
                    R.id.collectionFragment
                ),
                BottomItem("我", null, R.drawable.ic_person_24, R.id.mineFragment)
            )
        BottomNavigationCompose(shouldPadding, mBottomTabItems, onItemSelect)
    }

    override fun onStop() {
        super.onStop()
        Repository.getInstance(this).saveUserInfo()
    }
}