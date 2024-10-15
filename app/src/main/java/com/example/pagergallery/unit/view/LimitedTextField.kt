package com.example.pagergallery.unit.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.TextFieldValue


@Composable
fun LimitedTextField(
    value : TextFieldValue,
    placeHolder : String,
    imageVector : ImageVector,
    maxLength : Int
){

    var inputValue by remember { mutableStateOf(value) }

    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = inputValue,
        onValueChange = { inputValue = filterMaxLength(it,inputValue,maxLength) },
        placeholder = { Text(text = placeHolder) },
        leadingIcon = {
            Icon(
                imageVector = imageVector,
                contentDescription = null
            )
        },
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
        singleLine = true
    )

}