package com.example.pagergallery.fragment.me

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LoginButtonCompose(
    onClick : () -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(144.dp)
            .background(Color(0XFF018786)),
        verticalArrangement = Arrangement.Center,//设置水平居中对齐
        horizontalAlignment =  Alignment.CenterHorizontally//设置垂直居中对齐
    ) {
        Button(
            onClick = { onClick() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFBEBEBE))
            ) {
            Text(
                text = "登录/注册",
                color = Color(0XFF009BE7)
            )
        }
    }
}