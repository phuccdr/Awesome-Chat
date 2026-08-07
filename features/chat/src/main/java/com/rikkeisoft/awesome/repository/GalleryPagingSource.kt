package com.rikkeisoft.awesome.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rikkeisoft.awesome.model.GalleryImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class GalleryPagingSource(
    private val context: Context,
) : PagingSource<Int, GalleryImage>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GalleryImage> {
        return try {
            val offset = params.key ?: 0
            val limit = params.loadSize
            val images = loadImages(limit = limit, offset = offset)

            LoadResult.Page(
                data = images,
                prevKey = if (offset == 0) null else offset - limit,
                nextKey = if (images.isEmpty()) null else offset + images.size
            )
        } catch (e: Exception) {
            Timber.e(e, "GalleryPagingSource - load error")
            LoadResult.Error(e)
        }
    }

    suspend fun loadImages(limit: Int = 40, offset: Int = 0): List<GalleryImage> =
        withContext(Dispatchers.IO) {
            val images = mutableListOf<GalleryImage>()
            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
            val projection = arrayOf(
                MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_ADDED
            )

            try {
                val queryArgs = Bundle().apply {
                    putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
                    putInt(ContentResolver.QUERY_ARG_OFFSET, offset)
                    putStringArray(
                        ContentResolver.QUERY_ARG_SORT_COLUMNS,
                        arrayOf(MediaStore.Images.Media.DATE_ADDED)
                    )
                    putInt(
                        ContentResolver.QUERY_ARG_SORT_DIRECTION,
                        ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
                    )
                }

                context.contentResolver.query(
                    collection, projection, queryArgs, null
                )?.use { cursor ->
                    val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idCol)
                        val uri = ContentUris.withAppendedId(collection, id)
                        images += GalleryImage(id, uri, cursor.getLong(dateCol))
                    }
                }
            } catch (e: Exception) {
                Timber.tag("Gallery").d(e, "GalleryRepository - loadImages error")
            }
            Timber.tag("Gallery")
                .d("GalleryRepository - loadImages offset: $offset - limit: $limit")
            images
        }

    override fun getRefreshKey(state: PagingState<Int, GalleryImage>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(state.config.pageSize)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(state.config.pageSize)
        }
    }
}
