package com.example.pagergallery

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.example.pagergallery.databinding.ActivityMainBinding
import com.example.pagergallery.unit.base.BaseViewModel
import com.example.pagergallery.unit.base.MyViewModelFactory
import com.example.pagergallery.unit.logD
import com.example.pagergallery.unit.view.AppBarCompose
import com.example.pagergallery.unit.view.BottomItem
import com.example.pagergallery.unit.view.BottomNavigationCompose
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel by viewModels<BaseViewModel>{
        MyViewModelFactory(application.applicationContext,BaseViewModel::class.java)
    }
    private val visible = mutableStateOf(false)
    private val canBack = mutableStateOf(false)
    private val title = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityMainBinding.inflate(layoutInflater).let { binding = it }
        window.statusBarColor = resources.getColor(R.color.teal_700, null)
        setContentView(binding.root)

        //获取NavController
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHost.navController
        navController.addOnDestinationChangedListener(this)
        binding.topBar.setContent { AppBarCompose(visible.value,title.value?:"",canBack.value){
            navController.popBackStack()
        } }

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

        //获取异步数据
        collect()
    }

    private fun collect(){
        //设置标题
        lifecycleScope.launch {
            viewModel.title.collect{
                title.value = it
                logD(it)
            }
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

    //设置标题栏可见性
    private fun setTopAppBarVisible(visible: Boolean,canBack: Boolean, color: Int?) {
        if (this.visible.value != visible) {
            logD("visible:$visible")
            this.visible.value = visible
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.setDecorFitsSystemWindows(!visible)
            }
        }
        if (this.canBack.value != canBack && visible){
            this.canBack.value = canBack
        }
        setStatusBarColor(color)
    }

    private fun setStatusBarColor(color: Int?) {
        if (window.statusBarColor != color) {
            window.statusBarColor = if (color != null) resources.getColor(
                color,
                null
            ) else Color.TRANSPARENT
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
                setTopAppBarVisible(false, canBack = false, color = R.color.teal_700)
                //setStatusBarColor(R.color.teal_700)
                setVisible(View.VISIBLE)
            }

            R.id.collectionFragment -> {
                setTopAppBarVisible(true, canBack = false, color = null)
                setVisible(View.VISIBLE)
            }
            R.id.queryFragment,R.id.showQueryFragment ,
            R.id.largeViewFragment->{
                setTopAppBarVisible(false, canBack = false, color = null)
                setVisible(View.GONE)
            }
            else -> {
                setTopAppBarVisible(true, canBack = true, color = null)
                setVisible(View.GONE)
            }
        }
    }

    private fun setVisible(visible: Int) {
        if (binding.botNavComposeView.visibility != visible) {
            binding.botNavComposeView.visibility = visible
        }
    }

}

data class TopBarInfo(
    var title: String? = null,
    var visible: Boolean,
    var canBack: Boolean,
    var color: Int? = android.graphics.Color.TRANSPARENT
)