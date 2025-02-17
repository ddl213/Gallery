package com.example.pagergallery.fragment.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.pagergallery.databinding.ImageCellBinding
import com.example.pagergallery.repository.api.Item
import com.example.pagergallery.unit.loadImage

class GalleryAdapter(private val onClick : (Int?) -> Unit) :
    PagingDataAdapter<Item, GalleryAdapter.MyViewHolder>(DiffCALLBACK) {
    //回调接口 判断数据是否发生改变
    object DiffCALLBACK : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }

    }

    //创建视图
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val holder = MyViewHolder(
            ImageCellBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        holder.itemView.setOnClickListener { onClick(holder.absoluteAdapterPosition) }
        return holder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }


    class MyViewHolder(private val binding : ImageCellBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            binding.imgWebUrl.layoutParams.height = item.webImageHeight//给每个图片设置一个初始高度，防止加载图片过程中出现多次排列
            itemView.context.loadImage(item.webFormatURL, binding.imgWebUrl,false)//加载图片
        }
    }

}


