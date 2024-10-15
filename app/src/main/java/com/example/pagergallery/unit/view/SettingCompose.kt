package com.example.pagergallery.unit.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SettingCompose(
    onClick : (SettingSelectEnum) -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0XFFD3D3D3))
    ) {
        Spacer(modifier = Modifier.padding(4.dp))
        TextButton(
            onClick = { onClick(SettingSelectEnum.Clear) },
            modifier = Modifier.background(Color.White)
        ) {
            Text(
                text = "清除历史记录",
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally),
                color = Color.Black
            )
        }
        Spacer(modifier = Modifier.padding(vertical = 4.dp))
        TextButton(
            onClick = { onClick(SettingSelectEnum.Exit) },
            modifier = Modifier.background(Color.White)
        ) {
            Text(
                text = "退出",
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally),
                color = Color.Black
            )
        }
    }
}

enum class SettingSelectEnum {
    Clear,
    Exit
}