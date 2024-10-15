package com.example.pagergallery.fragment.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.pagergallery.R

class EmptyAdapter(private val retry : () -> Unit) : RecyclerView.Adapter<EmptyAdapter.MyViewHolder>() {

    private var str : String? = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder = MyViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.layout_empty,
            parent,
            false
        ).also {
            (it.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
        }
    )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvEmpty.text = str!!
        holder.itemView.setOnClickListener{ retry() }
    }

    override fun getItemCount(): Int = 1

    fun setText(isEmpty : Boolean){
        str = if (isEmpty) "被找到了盲区，换个关键词试试~"
        else "发生了一些意料之外的错误..."
        notifyItemChanged(0)
    }

    class MyViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView){
        val tvEmpty: TextView = itemView.findViewById(R.id.tv_empty)

    }
}