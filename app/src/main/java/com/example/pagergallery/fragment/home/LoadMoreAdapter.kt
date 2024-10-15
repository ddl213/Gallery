package com.example.pagergallery.fragment.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.pagergallery.R

class LoadMoreAdapter(private val retry : () -> Unit) : LoadStateAdapter<LoadMoreAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): MyViewHolder {
        val holder = MyViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_load_more,
                parent,
                false
            ).also {
                (it.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan=true
                it.setOnClickListener { retry() }
            }
        )

        return holder
    }

    override fun onBindViewHolder(holder: MyViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        return loadState is LoadState.Loading
                || loadState is LoadState.Error
                || (loadState is LoadState.NotLoading && loadState.endOfPaginationReached)
    }

    class MyViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView){
        private val tvMessage = itemView.findViewById<TextView>(R.id.tv_message)
        private val progressBar = itemView.findViewById<ProgressBar>(R.id.progressBar)

        fun bind(loadState: LoadState){
            when(loadState){
                is LoadState.Loading ->{
                    progressBar.visibility = View.VISIBLE
                    tvMessage.visibility = View.GONE
                }
                is LoadState.NotLoading -> {
                    progressBar.visibility = View.GONE
                    tvMessage.visibility = View.VISIBLE
                    tvMessage.text = "全部加载完成"
                }
                is LoadState.Error -> {
                    progressBar.visibility = View.GONE
                    tvMessage.visibility = View.VISIBLE
                    tvMessage.text = "出错了${loadState.error.message},请点击重试"
                }
            }
        }
    }
}