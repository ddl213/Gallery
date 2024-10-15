package com.example.pagergallery.unit.view

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import com.example.pagergallery.R
import com.example.pagergallery.unit.logD

class ExpandableFlowLayout : ViewGroup {

    private val defaultHorizontalSpace = paddingStart + paddingEnd
    private val defaultVerticalSpace = paddingTop + paddingBottom
    private lateinit var expandView: View

    private var elementDividerHorizontal : Int = 0
    private var elementDividerVertical : Int = 0
    private var isExpanding = false

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        context?.obtainStyledAttributes(attrs,R.styleable.ExpandableFlowLayout)?.run {
            elementDividerHorizontal = getDimensionPixelSize(R.styleable.ExpandableFlowLayout_element_divider_horizontal,50)
            elementDividerVertical = getDimensionPixelSize(R.styleable.ExpandableFlowLayout_element_divider_vertical,30)

            recycle()
        }
        expandView = context?.let {
            AppCompatImageView(context).apply {
                layoutParams = LayoutParams( LayoutParams.WRAP_CONTENT,36.dp2px)
                scaleType  = ImageView.ScaleType.CENTER
                setPadding(5.dp2px,4.dp2px,5.dp2px,0)
                background = AppCompatResources.getDrawable(context,R.drawable.radius_border_50_gray)
                setImageResource(R.drawable.baseline_keyboard_arrow_down_24)
                setOnClickListener {
                    isExpanding = !isExpanding
                    rotation = if (isExpanding) 180f else 0f
                    requestLayout()
                }
            }
        }!!
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    private var defaultShowRow = 2 //默认可显示2行
    private var maxExpandLineCount = 5 //展开最多可显示的行数
    private var expandable = false //是否能展开
    private var isDeleting = false

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val rootWidth = MeasureSpec.getSize(widthMeasureSpec)
        var useWidth = defaultHorizontalSpace
        var useHeight = defaultVerticalSpace

        if(childCount == 0) {
            setMeasuredDimension(rootWidth,useHeight)
            return
        }

        var rowCount = 1

        measureChild(expandView,widthMeasureSpec,heightMeasureSpec)

        for (index in 0 until childCount - 1 ){
            val child = getChildAt(index)
            if (child != null){

                measureChild(child,widthMeasureSpec,heightMeasureSpec)
                val childUsedWidth = child.measuredWidth + elementDividerHorizontal
                val childUsedHeight = child.measuredHeight + elementDividerVertical

                //如果当前是第一个，添加一个子view的高度
                if (useHeight == defaultVerticalSpace){
                    useHeight += childUsedHeight
                }

                if (useWidth + childUsedWidth >= rootWidth){
                    rowCount++

                    if (!isExpanding && rowCount > defaultShowRow){
                        break
                    }

                    useWidth = defaultHorizontalSpace
                    useHeight += childUsedHeight
                }
                useWidth += childUsedWidth
//                logD("child${index}: ${child.text}")
//                logD("${rowCount}--${index}--${childCount - 2}--${useWidth+expandView.measuredWidth}--${rootWidth}")
                if (index == childCount - 2 && isExpanding && rowCount > defaultShowRow){
                    logD("展开")
                    if (useWidth + expandView.measuredWidth > rootWidth){
                        logD("展开")
                        useHeight += childUsedHeight
                    }
                }
            }
        }
        logD("${rowCount}--${childCount - 2}--${useWidth+expandView.measuredWidth}--${rootWidth}")

        expandable = rowCount > defaultShowRow
        setMeasuredDimension(rootWidth,useHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if(childCount == 0) return

        val availableWidth = right - left

        var usedWidth = defaultHorizontalSpace
        var posX = paddingStart
        var posY = paddingTop
        var rowCount = 1

        for (index in 0 until childCount - 1){

            val child = getChildAt(index)
            if (child != null){

                val childUsedWidth = child.measuredWidth + elementDividerHorizontal
                val childUsedHeight = child.measuredHeight + elementDividerVertical

                val nextRow = if (!isExpanding && rowCount == defaultShowRow && expandable){
                    usedWidth + childUsedWidth + expandView.measuredWidth > availableWidth
                }else{
                    usedWidth + childUsedWidth > availableWidth
                }

                if (nextRow){
                    rowCount++

                    if (!isExpanding && rowCount > defaultShowRow){
                        child.layout(0,0,0,0)
                        break
                    }

                    usedWidth = defaultHorizontalSpace
                    posX = paddingStart
                    posY += childUsedHeight
                }

                child.layout(posX,posY,posX + child.measuredWidth,posY + child.measuredHeight)
                posX += childUsedWidth
                usedWidth += childUsedWidth

                if (index == childCount - 2 && isExpanding && rowCount > defaultShowRow){
                    if (usedWidth + expandView.measuredWidth > availableWidth){
                        posX = paddingStart
                        posY += childUsedHeight
                    }
                }
            }
        }
        if (expandable){
            logD("expandable：${posX}--${posY}--${expandView.measuredWidth}--${expandView.measuredHeight}")
            expandView.layout(posX,posY,posX + expandView.measuredWidth,posY + expandView.measuredHeight)
        }else{
            expandView.layout(0,0,0,0)
        }
    }

    private fun setDrawableRight(view: TextView){
        view.apply {
            if (isDeleting) setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.baseline_close_24,0)
            else setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0)
        }
    }

    fun setData(data : List<String>){
        removeAllViews()
        if(data.isEmpty()) return

        for (content in data){
            addView(initTextView(content))
        }
        addView(expandView)
    }

    //初始化textview样式
    private fun initTextView(content: String) = TextView(context).apply {
        text = content
        height = 34.dp2px
        gravity = Gravity.CENTER
        setTextSize(TypedValue.COMPLEX_UNIT_SP,18f)
        setBackgroundResource(R.drawable.radius_border_50_gray)
        setPadding(10.dp2px,4.dp2px,10.dp2px,0)
        setOnClickListener {
            mOnClickListener.click(indexOfChild(this),content)
            if (isDeleting) {
                removeView(this)
            }
        }
    }

    //更改删除状态
    fun delete(){
        if(!isExpanding && expandable){
            val view = getChildAt(childCount-1) as AppCompatImageView
            view.rotation = if (isExpanding) 180f else 0f

            isExpanding = !isExpanding
        }
        isDeleting = !isDeleting
        for (index in 0 until  childCount -1){
            val child = getChildAt(index) as TextView
            setDrawableRight(child)
        }
        requestLayout()
    }

    //监听textview是否点击
    interface OnClickListener {
        fun click(index : Int , content: String)
    }

    private lateinit var mOnClickListener : OnClickListener

    fun setOnClickListener(mOnClickListener: OnClickListener){
        this.mOnClickListener = mOnClickListener
    }

    private inline val Int.dp2px : Int
        get() = (Resources.getSystem().displayMetrics.density * this + 0.5f).toInt()

}