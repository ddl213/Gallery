package com.example.pagergallery.unit.view

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.pagergallery.R
import com.example.pagergallery.unit.shortToast
import java.util.regex.Pattern

class EditTextWithClear @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private var iconDrawable: Drawable? = null

    val filter = InputFilter { source, _, _, _, _, _ ->
        val p = Pattern.compile("[0-9a-zA-Z]+")
        val m = p.matcher(source.toString())
        if (!m.matches()) "" else null
    }

    private var maxLength = 16

    init {
        addTextChangedListener(MyTextWatcher(toggleClearIcon(), this))

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.EditTextWithClear,
            0,
            0
        ).apply {
            try {
                val iconID = getResourceId(R.styleable.EditTextWithClear_clearIcon, -1)
                if (iconID != -1) iconDrawable = ContextCompat.getDrawable(context, iconID)
            } finally {
                recycle()
            }
        }
    }

    override fun addTextChangedListener(watcher: TextWatcher?) {
        super.addTextChangedListener(watcher)
    }


    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        toggleClearIcon()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let { e ->
            iconDrawable?.let {
                if (e.action == MotionEvent.ACTION_UP
                    && e.y > height / 2 - it.intrinsicHeight / 2 - 20
                    && e.y < height / 2 + it.intrinsicHeight / 2 + 20
                    && e.x > width - it.intrinsicWidth - 20
                    && e.x < width + it.intrinsicWidth + 20
                ) text?.clear()
            }
        }
        performClick()
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    private fun toggleClearIcon() {
        val icon = if (isFocusable && text?.isNotEmpty() == true) iconDrawable else null
        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, icon, null)
    }

    fun isMultiLine(multiLineEnable: Boolean) {
        if (!multiLineEnable) {
            isSingleLine = true
            filters= arrayOf(MyInputFilter(maxLength,this.context))
        } else {
            iconDrawable = null
            isVerticalScrollBarEnabled = true
            gravity = Gravity.TOP
            setPadding(paddingLeft + 10, paddingTop + 20, paddingRight, paddingBottom + 30)
            filters= arrayOf(MyInputFilter(maxLength,this.context))
        }
    }

    fun setMaxLength(length : Int){
        maxLength = length
    }

    class MyInputFilter(
        private val maxLength: Int,
        val context: Context
    ) : InputFilter {
        override fun filter(
            source: CharSequence?,
            start: Int,
            end: Int,
            dest: Spanned?,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            var keep = maxLength - (dest?.length?.minus((dend - dstart)) ?: 0)

            when {
                keep <= 0 -> {
                    context.shortToast("字数不能超过${maxLength}个").show()
                    return ""
                }
                keep > end - start -> {
                    return null
                }
                else -> {
                    keep += start
                    if (source?.get(keep - 1)?.let { Character.isHighSurrogate(it) } == true) {
                        --keep
                        if (keep == start) return ""
                    }
                    return source?.subSequence(start, keep)
                }
            }
        }

    }

    class MyTextWatcher(
        private val toggleClearIcon: Unit,
        private val editText: AppCompatEditText
    ) : TextWatcher {
        private var currentText = ""
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            toggleClearIcon
        }

        override fun afterTextChanged(p0: Editable?) {
            val lines = editText.lineCount
            if (lines > editText.maxLines) {
                editText.context.shortToast("签名最多不能超过7行").show()
                editText.setText(currentText)
                editText.setSelection(currentText.length)
            } else {
                currentText = if (p0.isNullOrBlank()) "" else p0.toString()
            }
        }

    }
}
