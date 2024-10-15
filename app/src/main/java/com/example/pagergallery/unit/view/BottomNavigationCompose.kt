package com.example.pagergallery.unit.view

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BottomItem(val label: String, val selectItemRes: Int?, val unSelectItemRes: Int,val layoutId : Int)

@Composable
fun BottomNavigationCompose(
    shouldPadding : Boolean,
    bottomItems: List<BottomItem>,
    onItemSelected: (Int) -> Unit
) {

    var selectedItem by remember { mutableStateOf(0) }
    val modifier =  if (shouldPadding) Modifier.navigationBarsPadding() else Modifier
    BottomNavigation(
        modifier = modifier,
        backgroundColor = Color.White.copy(alpha = 0.95f)
    ) {
        bottomItems.forEachIndexed { index, item ->
            var iconColor = Color.Black
            BottomNavigationItem(
                selected = (selectedItem == index),
                onClick = {
                    selectedItem = index
                    onItemSelected.invoke(item.layoutId)
                },
                icon = {
                    val iconRes = item.unSelectItemRes

                    if (selectedItem == index) {
                        //iconRes = item.selectItemRes
                        iconColor = Color(0xFF5599E9)
                    }
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        modifier = Modifier
                            .size(if (selectedItem != index) 24.dp else 30.dp)
                            .padding(bottom = 4.dp),
                        tint = iconColor,
                    )
                },
                label = {
                    val labelStyle = if (selectedItem == index) {
                        TextStyle(
                            fontWeight = FontWeight.Medium,
                            color = iconColor,
                            fontSize = 12.sp
                        )
                    } else {
                        TextStyle(
                            fontWeight = FontWeight.Normal,
                            color = iconColor,
                            fontSize = 11.sp
                        )
                    }
                    Text(
                        text = bottomItems[index].label,
                        style = labelStyle,
                    )
                },
            )
        }
    }
}

