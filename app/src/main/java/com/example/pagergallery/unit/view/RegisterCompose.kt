package com.example.pagergallery.unit.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
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
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pagergallery.R

@Composable
fun RegisterCompose(
    account : Long,
    isReset : Boolean,
    dialogClick: (Boolean) -> Unit,
    click: (String, String, String, String?) -> Unit
) {
    var showDialog =account != -1L
    var mAccount by remember { mutableStateOf(TextFieldValue()) }
    Column {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.app_bg_star),
                contentDescription = "背景",
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.fillMaxHeight()
            )
            Column {
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    modifier = Modifier
                        .weight(9f)
                        .background(Color.White.copy(0.7f))
                        .padding(40.dp)
                        .fillMaxWidth()
                ) {
                    var phone by remember { mutableStateOf(TextFieldValue()) }
                    var pwd by remember { mutableStateOf(TextFieldValue()) }
                    var confirmPwd by remember { mutableStateOf(TextFieldValue()) }
                    var showPwd by remember { mutableStateOf(false) }

                    if (isReset) {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = mAccount,
                            onValueChange = { mAccount = filterMaxLength(it, mAccount, 10) },
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
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Next
                            )
                        )
                        Spacer(modifier = Modifier.padding(vertical = 2.dp))
                    }
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = phone,
                        onValueChange = { phone = filterMaxLength(it, phone, 11) },
                        placeholder = { Text(text = "请输入手机号码") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null
                            )
                        },
                        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        )
                    )

                    repeat(2) { index ->
                        Spacer(modifier = Modifier.padding(vertical = 2.dp))
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = if (index == 0) pwd else confirmPwd,
                            onValueChange = {
                                if (index == 0) pwd = filterMaxLength(it, pwd, 16)
                                else confirmPwd = filterMaxLength(it, confirmPwd, 16)
                            },
                            placeholder = {
                                Text(
                                    text =
                                    if (index == 0) "请输入密码" else "再次确认密码"
                                )
                            },
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
                                imeAction = if (index == 0) ImeAction.Next else ImeAction.Done
                            )
                        )
                    }


                    Spacer(modifier = Modifier.padding(vertical = 8.dp))

                    //提交按钮
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            click(
                                phone.text,
                                pwd.text,
                                confirmPwd.text,
                                mAccount.text
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xff5c59fe)),
                        contentPadding = PaddingValues(8.dp, 12.dp)
                    ) {
                        Text(text = "提交", color = Color.White, fontSize = 18.sp)
                    }

                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .fillMaxWidth(0.8f)
                .background(Color.White),
            onDismissRequest = {  },
            title = {
                Text(
                    text = "您的账号为",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center)
                        .padding(end = 4.dp),
                )
            },
            text = {
                Text(
                    text = account.toString(),
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center)
                )
            },
            buttons = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        modifier = Modifier
                            .width(140.dp)
                            .height(35.dp)
                            .padding(start = 16.dp, end = 20.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0XFF46B8F1))
                            .wrapContentSize(Alignment.Center),
                        onClick = { dialogClick(false) }) {
                        Text(
                            text = "复制",
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                    TextButton(
                        modifier = Modifier
                            .width(140.dp)
                            .height(35.dp)
                            .padding( end = 16.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0XFF1AD186))
                            .wrapContentSize(Alignment.Center),
                        onClick = {
                            showDialog = false
                            dialogClick(true) }) {
                        Text(
                            text = "返回",
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(bottom = 8.dp))
            }
        )
    }

}