package com.example.pagergallery.unit.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseAdapter<T, VB : ViewBinding>(
    private val list: List<T>,
    viewBindingClass: Class<VB>
) : RecyclerView.Adapter<BaseViewHolder<VB>>() {
    private val inflateMethod = viewBindingClass.getInflateMethod()

    abstract val initViewHolder : (BaseViewHolder<VB>) -> Unit

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<VB> = BaseViewHolder(
            inflateMethod.invoke(
                null,
                LayoutInflater.from(parent.context),
                parent,
                false
            ) as VB,
            initViewHolder
        )

    override fun onBindViewHolder(holder: BaseViewHolder<VB>, position: Int) {
        setData(holder,position,list[position])
    }

    abstract fun setData(holder: BaseViewHolder<VB>,position: Int,item : T)

    override fun getItemCount(): Int = list.size

    private fun <VB : ViewBinding> Class<VB>.getInflateMethod() =
        getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
}
class BaseViewHolder<VB : ViewBinding>(val binding: VB, init : (BaseViewHolder<VB>) -> Unit) : RecyclerView.ViewHolder(binding.root){
    init {
        init.invoke(this)
    }
}

inline fun <T,reified VB : ViewBinding> adapterOf(
    list: List<T>,
    clazz: Class<VB> = VB::class.java,
    noinline initViewHolder : (BaseViewHolder<VB>) -> Unit,
    crossinline setData : BaseAdapter<T,VB>.(holder: BaseViewHolder<VB>, position : Int, item : T) -> Unit,
) : BaseAdapter<T,VB> =
    object : BaseAdapter<T,VB>(list,clazz){
        override val initViewHolder: (BaseViewHolder<VB>) -> Unit
            get() = initViewHolder

        override fun setData(holder: BaseViewHolder<VB>, position: Int, item: T) {
            setData(this,holder, position, item)
        }
    }