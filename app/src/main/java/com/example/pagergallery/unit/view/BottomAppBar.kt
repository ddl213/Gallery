package com.example.pagergallery.unit.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.pagergallery.R


@Composable
fun BottomAppBar(
    isCollect : Boolean,
    onClick : (Boolean) -> Unit
){
    Column(
        modifier = Modifier.background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(top = 8.dp, bottom = 10.dp)
                .wrapContentWidth(Alignment.End)
        ) {
            Image(
                painter = painterResource(if (isCollect) R.drawable.ic_baseline_star_24 else R.drawable.ic_baseline_star_border_24),
                contentDescription = "收藏",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        onClick(true)
                    }
            )
            Spacer(modifier = Modifier.width(16.dp))

            Image(
                painter = painterResource(R.drawable.baseline_download_24),
                contentDescription = "下载",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onClick(false) }
            )
            Spacer(modifier = Modifier.width(16.dp))

        }
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}