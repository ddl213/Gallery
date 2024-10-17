package com.example.pagergallery.repository.api

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.pagergallery.repository.Repository
import com.example.pagergallery.repository.local.tables.cache.Cache
import com.example.pagergallery.unit.logD
import retrofit2.HttpException
import java.io.IOException

class PagingDataSource(
    private val repository: Repository,
    private val queryString: String?) : PagingSource<Int,Item>() {
    override fun getRefreshKey(state: PagingState<Int, Item>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Item> {
        return try {
            val page = params.key ?: 1 //获取当前的页数  如果为空则为第一页
            val data = repository.getPic(queryString,page)
            logD("page:$page,data:${data.isNotEmpty()}")
            LoadResult.Page(
                data,
                if (page > 1) page - 1 else null,
                if (data.isNotEmpty()) page + 1 else null
            )
        }catch (e : IOException){
            LoadResult.Error(e)
        }catch (e:HttpException){
            LoadResult.Error(e)
        }
    }
}