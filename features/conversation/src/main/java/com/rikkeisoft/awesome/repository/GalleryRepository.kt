package com.rikkeisoft.awesome.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rikkeisoft.awesome.model.GalleryImage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GalleryRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val PAGE_SIZE = 30
    fun getGalleryImages(pageSize: Int = PAGE_SIZE): Flow<PagingData<GalleryImage>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize, enablePlaceholders = false, initialLoadSize = pageSize
            ), pagingSourceFactory = {
                GalleryPagingSource(context)
            }).flow
    }


}
