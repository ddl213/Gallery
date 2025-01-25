package com.example.pagergallery.unit.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pagergallery.R
import com.example.pagergallery.unit.enmu.LoginNavigateTo

val imageList = listOf(R.drawable.qq, R.drawable.we_chat, R.drawable.phone)
val appNameList = listOf("QQ", "微信", "手机")
val appNavigateTo = listOf(LoginNavigateTo.QQ,LoginNavigateTo.WeChat,LoginNavigateTo.Phone)

@Composable
fun LoginCompose(
    loginState : Boolean,
    mAccount : Long?,
    navigateTo : (LoginNavigateTo,String?,String?) -> Unit
) {
    Column{
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.app_bg_star),
                contentDescription = "背景",
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.fillMaxHeight()
                    .fillMaxWidth()
            )
            Column(
                modifier = Modifier.statusBarsPadding()
            ) {
                var showPwd by remember { mutableStateOf(false) }//193343363
                var account by remember { mutableStateOf(if (mAccount == null) TextFieldValue() else TextFieldValue(mAccount.toString()) )}
                var pwd by remember { mutableStateOf(TextFieldValue()) }
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    modifier = Modifier
                        .weight(9f)
                        .background(Color.White.copy(0.7f))
                        .padding(40.dp)
                        .fillMaxWidth()
                ) {
                    //输入账号及密码
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = account,
                        onValueChange = { account = filterMaxLength(it,account,10) },
                        placeholder = { Text(text = "请输入账号") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null
                            )
                        },
                        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.NumberPassword,
                            imeAction = ImeAction.Next
                        )
                    )
                    Spacer(modifier = Modifier.padding(vertical = 2.dp))
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = pwd,
                        onValueChange = {pwd = filterMaxLength(it,pwd,16) },
                        placeholder = { Text(text = "请输入密码") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null
                            )
                        },
                        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
                        trailingIcon = {
                            IconButton(onClick = { showPwd = !showPwd }) {
                                Icon(
                                    painter = painterResource(
                                        id = if (showPwd) R.drawable.ic_pwd_hide else R.drawable.ic_pwd_show
                                    ),
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (!showPwd) PasswordVisualTransformation() else VisualTransformation.None,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        )
                    )

                    //重置密码及注册
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        TextButton(onClick = { navigateTo(LoginNavigateTo.Register,null,null) }) {
                            Text(text = "立即注册", fontSize = 16.sp, color = Color.Gray)
                        }
                        TextButton(onClick = { navigateTo(LoginNavigateTo.Reset,null,null) }) {
                            Text(text = "忘记密码", fontSize = 16.sp, color = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.padding(vertical = 4.dp))

                    //登录按钮
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            navigateTo(LoginNavigateTo.Login,account.text,pwd.text)
                            if (!loginState) pwd = TextFieldValue()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xff5c59fe)),
                        contentPadding = PaddingValues(8.dp, 12.dp)
                    ) {
                        Text(text = "登录", color = Color.White, fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.padding(vertical = 115.dp))

                    //第三方app及手机号登录
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier
                                    .height(1.dp)
                                    .weight(1f)
                                    .background(Color(0xFFCFC5C5))
                                    .padding(end = 10.dp)
                            ) {}
                            Text(text = "第三方登录", color = Color.Gray, fontSize = 16.sp)
                            Row(
                                modifier = Modifier
                                    .height(1.dp)
                                    .weight(1f)
                                    .background(Color(0xFFCFC5C5))
                                    .padding(start = 10.dp)
                            ) {}
                        }

                        Spacer(modifier = Modifier.padding(bottom = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            repeat(imageList.size) {index ->
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .pointerInput(Unit) {
                                            detectTapGestures(
                                                onTap = {
                                                    navigateTo(appNavigateTo[index], null, null)
                                                }
                                            )
                                        },
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Image(
                                        painter = painterResource(id = imageList[index]),
                                        contentDescription = null,
                                        modifier = Modifier.size(32.dp).padding(bottom = 4.dp)
                                    )
                                    Text(
                                        text = appNameList[index],
                                        color = Color.Gray,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.navigationBarsPadding())
                }
            }
        }
    }
}

