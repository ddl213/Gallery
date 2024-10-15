package com.example.pagergallery.unit.view

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue


fun filterMaxLength(
    inputTextField: TextFieldValue,
    lastTextField: TextFieldValue,
    maxLength: Int
): TextFieldValue {
    if (maxLength < 0) return inputTextField // 错误的长度，不处理直接返回
    if (inputTextField.text.length <= maxLength) return inputTextField // 总计输入内容没有超出长度限制

    // 输入内容超出了长度限制
    // 这里要分两种情况：
    // 1. 直接输入的，则返回原数据即可
    // 2. 粘贴后会导致长度超出，此时可能还可以输入部分字符，所以需要判断后截断输入

    val inputCharCount = inputTextField.text.length - lastTextField.text.length
    if (inputCharCount > 1) { // 同时粘贴了多个字符内容
        val allowCount = maxLength - lastTextField.text.length
        // 允许再输入字符已经为空，则直接返回原数据
        if (allowCount <= 0) return lastTextField

        // 还有允许输入的字符，则将其截断后插入
        val newString = StringBuffer()
        newString.append(lastTextField.text)
        val newChar = inputTextField.text.substring(lastTextField.selection.start..allowCount)
        newString.insert(lastTextField.selection.start, newChar)
        return lastTextField.copy(text = newString.toString(), selection = TextRange(lastTextField.selection.start + newChar.length))
    }
    else { // 正常输入
        return if (inputTextField.selection.collapsed) { // 如果当前不是选中状态，则使用上次输入的光标位置，如果使用本次的位置，光标位置会 +1
            lastTextField
        } else { // 如果当前是选中状态，则使用当前的光标位置
            lastTextField.copy(selection = inputTextField.selection)
        }
    }
}