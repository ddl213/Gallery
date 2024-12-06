package com.example.pagergallery.unit.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.pagergallery.unit.listener.OnItemChildClickListener
import com.example.pagergallery.unit.listener.OnItemClickListener
import com.example.pagergallery.unit.listener.OnItemLongClickListener

abstract class BaseAdapter<T, VB : ViewBinding>(
    private val list: List<T>,
    viewBindingClass: Class<VB>
) : RecyclerView.Adapter<BaseViewHolder<VB>>() {

    //item的点击事件
    private var onItemClickListener : OnItemClickListener? = null
    private var onItemLongClickListener : OnItemLongClickListener? = null
    private var onItemChildClickListener :  OnItemChildClickListener? = null

    private val inflateMethod = viewBindingClass.getInflateMethod()
    abstract val initViewHolder: (BaseViewHolder<VB>) -> Unit


    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<VB> =
        BaseViewHolder(
            inflateMethod.invoke(
                null,
                LayoutInflater.from(parent.context),
                parent,
                false
            ) as VB,
            initViewHolder
        )

    override fun onBindViewHolder(holder: BaseViewHolder<VB>, position: Int) {
        setData(holder, position, list[position])
    }

    override fun getItemCount(): Int = list.size

    abstract fun setData(holder: BaseViewHolder<VB>, position: Int, item: T)

    private fun <VB : ViewBinding> Class<VB>.getInflateMethod() =
        getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)


    private fun setItemClickListener(holder: BaseViewHolder<VB> ){

    }

    //itemView的点击事件
    protected fun setOnItemClickListener(listener: OnItemClickListener){
        onItemClickListener = listener
    }

    protected fun setOnItemLongClickListener(listener: OnItemLongClickListener){
        onItemLongClickListener = listener
    }

    protected fun setOnItemChildClickListener(listener: OnItemChildClickListener){
        onItemChildClickListener = listener
    }

//    protected fun getOnItemClickListener(listener: OnItemClickListener) : {
//        onItemClickListener = listener
//    }
//
//    protected fun getOnItemLongClickListener(listener: OnItemLongClickListener){
//        onItemLongClickListener = listener
//    }
//
//    protected fun getOnItemChildClickListener(listener: OnItemChildClickListener){
//        onItemChildClickListener = listener
//    }
}

class BaseViewHolder<VB : ViewBinding>(val binding: VB, init: (BaseViewHolder<VB>) -> Unit) :
    RecyclerView.ViewHolder(binding.root) {
    init {
        init.invoke(this)
    }
}

inline fun <T, reified VB : ViewBinding> adapterOf(
    list: List<T>,
    clazz: Class<VB> = VB::class.java,
    noinline initViewHolder: (BaseViewHolder<VB>) -> Unit,
    crossinline setData: BaseAdapter<T, VB>.(holder: BaseViewHolder<VB>, position: Int, item: T) -> Unit,
): BaseAdapter<T, VB> =
    object : BaseAdapter<T, VB>(list, clazz) {
        override val initViewHolder: (BaseViewHolder<VB>) -> Unit
            get() = initViewHolder

        override fun setData(holder: BaseViewHolder<VB>, position: Int, item: T) {
            setData(this, holder, position, item)
        }
    }