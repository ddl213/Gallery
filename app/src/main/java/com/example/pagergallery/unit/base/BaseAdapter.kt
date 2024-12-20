package com.example.pagergallery.unit.base

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.pagergallery.unit.listener.BaseListenerImp
import com.example.pagergallery.unit.listener.OnItemChildClickListener
import com.example.pagergallery.unit.listener.OnItemClickListener
import com.example.pagergallery.unit.listener.OnItemDoubleClickListener
import com.example.pagergallery.unit.listener.OnItemLongClickListener
import com.example.pagergallery.unit.listener.OnItemScaleListener
import uk.co.senab.photoview.PhotoView
import uk.co.senab.photoview.PhotoViewAttacher

abstract class BaseAdapter<T, VB : ViewBinding>(
    viewBindingClass: Class<VB>,
    list: MutableList<T>? = null
) : RecyclerView.Adapter<BaseViewHolder<VB>>(),
    BaseListenerImp {

    private var list: MutableList<T> = list ?: mutableListOf()

    //item的点击事件
    private var mOnItemClickListener: OnItemClickListener? = null
    private var mOnItemScaleListener: OnItemScaleListener? = null
    private var mOnItemLongClickListener: OnItemLongClickListener? = null
    private var mOnItemDoubleClickListener: OnItemDoubleClickListener? = null
    private var mOnItemChildClickListener: OnItemChildClickListener? = null

    private val inflateMethod = viewBindingClass.getInflateMethod()
    //abstract val initViewHolder: (BaseViewHolder<VB>) -> Unit

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<VB> {
        val holder = BaseViewHolder(
            inflateMethod.invoke(
                null,
                LayoutInflater.from(parent.context),
                parent,
                false
            ) as VB,
            //initViewHolder
        )

        bindViewClickListener(holder)
        return holder
    }

    override fun onBindViewHolder(holder: BaseViewHolder<VB>, position: Int) {
        setData(holder, position, list[position])
    }

    override fun getItemCount(): Int = list.size

    abstract fun setData(holder: BaseViewHolder<VB>, position: Int, item: T?)

    private fun <VB : ViewBinding> Class<VB>.getInflateMethod() =
        getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)

    //绑定控件点击事件
    private fun bindViewClickListener(holder: BaseViewHolder<VB>) {
        //item点击事件
        mOnItemClickListener?.let {
            holder.itemView.setOnClickListener { v ->
                val position = holder.absoluteAdapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener

                setOnItemClick(v, position)
            }
        }

        //item的长按事件
        mOnItemLongClickListener?.let {
            holder.itemView.setOnLongClickListener { v ->
                val position = holder.absoluteAdapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnLongClickListener false

                setOnItemLongClick(v, position)
            }
        }

        //item子控件的点击事件
        mOnItemChildClickListener?.let {
            for (id in getChildClickViewIds()) {
                holder.itemView.findViewById<View>(id)?.let {
                    if (!it.isClickable) {
                        it.isClickable = true
                    }
                    it.setOnClickListener { v ->
                        val position = holder.absoluteAdapterPosition
                        if (position == RecyclerView.NO_POSITION) return@setOnClickListener

                        setOnItemLongClick(v, position)
                    }
                }
            }
        }

        //photo点击和缩放事件
        if (null != photoViewId) {
            holder.itemView.findViewById<PhotoView>(photoViewId!!)?.let { photoView ->
                if (!photoView.isClickable) {
                    photoView.isClickable = true
                }

                //点击事件
                mOnItemClickListener?.let {
                    photoView.onViewTapListener =
                        PhotoViewAttacher.OnViewTapListener { view, _, _ ->
                            val position = holder.absoluteAdapterPosition
                            if (position == RecyclerView.NO_POSITION) return@OnViewTapListener

                            setOnItemClick(view, position)
                        }
                }
                //缩放事件
                mOnItemScaleListener?.let {
                    photoView.setOnScaleChangeListener { scaleFactor, _, _ ->
                        val position = holder.absoluteAdapterPosition
                        if (position == RecyclerView.NO_POSITION) return@setOnScaleChangeListener

                        setOnItemScale(photoView, position, scaleFactor)
                    }
                }
            }
        }
    }

    //调用itemView点击事件
    private fun setOnItemClick(v: View, position: Int) {
        mOnItemClickListener?.onItemClick(this, v, position)
    }

    private fun setOnItemLongClick(v: View, position: Int): Boolean {
        return mOnItemLongClickListener?.onItemLongClick(this, v, position) ?: false
    }

    private fun setOnItemScale(v: View, position: Int, scaleFactor: Float) {
        mOnItemScaleListener?.onItemScale(this, v, position, scaleFactor)
    }

    private fun setOnItemDoubleClick(v: View, position: Int) {
        mOnItemDoubleClickListener?.onItemDoubleClick(this, v, position)
    }

    private fun setOnItemChildClick(v: View, position: Int) {
        mOnItemChildClickListener?.onItemChildClick(this, v, position)
    }

    //外界设置点击监听事件
    override fun setOnItemClickListener(listener: OnItemClickListener) {
        mOnItemClickListener = listener
    }

    override fun setOnItemScaleListener(listener: OnItemScaleListener) {
        mOnItemScaleListener = listener
    }

    override fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        mOnItemLongClickListener = listener
    }

    override fun setOnItemDoubleClickListener(listener: OnItemDoubleClickListener) {
        mOnItemDoubleClickListener = listener
    }

    //获取监听器
    override fun setOnItemChildClickListener(listener: OnItemChildClickListener) {
        mOnItemChildClickListener = listener
    }

    private fun getOnItemClick(): OnItemClickListener? {
        return mOnItemClickListener
    }

    private fun getOnItemLongClick(): OnItemLongClickListener? {
        return mOnItemLongClickListener
    }

    private fun getOnItemScale(): OnItemScaleListener? {
        return mOnItemScaleListener
    }

    private fun getOnItemDoubleClick(): OnItemDoubleClickListener? {
        return mOnItemDoubleClickListener
    }

    private fun getOnItemChildClick(): OnItemChildClickListener? {
        return mOnItemChildClickListener
    }


    //需要点击事件的View
    private val childClickViewIds = LinkedHashSet<Int>()

    private fun getChildClickViewIds(): LinkedHashSet<Int> {
        return childClickViewIds
    }

    fun addChildClickViewIds(@IdRes vararg ids: Int) {
        childClickViewIds.addAll(ids.toList())
    }

    //photoView添加双击和缩放监听
    private var photoViewId: Int? = null

    fun addDoubleAndScaleListener(id: Int) {
        photoViewId = id
    }

    //设置adapter数据源
    @SuppressLint("NotifyDataSetChanged")
    fun setNewInstance(list: MutableList<T>?) {
        if (list === this.list) {
            return
        }
        this.list = list ?: arrayListOf()
        notifyDataSetChanged()
    }

    fun setList(list: List<T>) {
        if (list !== this.list) {
            this.list.clear()
            if (list.isNotEmpty()) {
                this.list.addAll(list)
            }
        } else {
            if (list.isNotEmpty()) {
                val newInstance = ArrayList(list)
                this.list.clear()
                this.list.addAll(newInstance)
            } else {
                this.list.clear()
            }
        }
    }

    fun removeItemByPos(position: Int) {
        list.removeAt(position)
    }

    fun removeItemByValue(item: T?) {
        list.remove(item)
    }

    fun clear() {
        list.clear()
    }

}

class BaseViewHolder<VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root) {}

inline fun <T, reified VB : ViewBinding> adapterOf(
    clazz: Class<VB> = VB::class.java,
    list: MutableList<T>? = null,
    crossinline setData: BaseAdapter<T, VB>.(holder: BaseViewHolder<VB>, position: Int, item: T?) -> Unit,
): BaseAdapter<T, VB> =

    object : BaseAdapter<T, VB>(clazz, list ?: mutableListOf<T>()) {
        override fun setData(holder: BaseViewHolder<VB>, position: Int, item: T?) {
            setData(this, holder, position, item)
        }
    }