package com.example.pagergallery.unit.view

import android.graphics.Color.TRANSPARENT
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppBarCompose(
    visible: Boolean,
    title: String,
    canBack: Boolean,
    navigationClick: () -> Unit
) {
    if (!visible) return
    var padding by remember { mutableStateOf(0) }
    Surface(
        color = Color(0xFF018786)
    ) {
        Column {
            Spacer(modifier = Modifier.statusBarsPadding())
            Box {
                Box(
                    modifier = Modifier
                        .height(48.dp)
                        .onSizeChanged {
                            padding = it.width
                        },
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (canBack) {
                        IconButton(onClick = {
                            navigationClick()
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = null
                            )
                        }
                    }
                }

                Box {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .wrapContentSize(if (canBack) Alignment.CenterStart else Alignment.Center)
                            .padding(horizontal = toDp(padding) + 8.dp)
                    )
                }
                Spacer(modifier = Modifier.padding(vertical = 4.dp))
            }


        }
    }
}

@Composable
fun toDp(int: Int) =
    with(LocalDensity.current) { int.toDp() }

data class TopBar(
    var color: Int? = null,
    var visible: Boolean,
    val canBack: Boolean = true
)
