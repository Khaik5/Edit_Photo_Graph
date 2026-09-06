package com.example.drawcanvas_v2.data.repository

import android.content.ContentUris
import android.content.ContentResolver
import android.os.Build
import android.provider.MediaStore
import com.example.drawcanvas_v2.data.model.ImageItem

class ImageRepository {
    fun getImages(
        contentResolver: ContentResolver
    ): List<ImageItem> {
        val images = mutableListOf<ImageItem>()
        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection =
            if (
                Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.R
            ) {
                arrayOf(
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.Media.IS_FAVORITE
                )
            } else {
                arrayOf(
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME
                )
            }
        val sort = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        contentResolver.query(
            collection,
            projection,
            null,
            null,
            sort
        )?.use { cursor ->
            val idColumn =
                cursor.getColumnIndexOrThrow(
                    MediaStore.Images.Media._ID
                )
            val folderColumn =
                cursor.getColumnIndex(
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME
                )
            val favouriteColumn =
                if (
                    Build.VERSION.SDK_INT >=
                    Build.VERSION_CODES.R
                ) {
                    cursor.getColumnIndex(
                        MediaStore.Images.Media.IS_FAVORITE
                    )
                } else {
                    -1
                }
            while (
                cursor.moveToNext()
            ) {
                val id =
                    cursor.getLong(
                        idColumn
                    )
                val folder =
                    if (
                        folderColumn >= 0
                    ) {
                        cursor.getString(
                            folderColumn
                        ) ?: ""
                    } else {
                        ""
                    }
                val favourite = favouriteColumn >= 0 && cursor.getInt(favouriteColumn) == 1
                val selfie = folder.contains("selfie", ignoreCase = true)
                val uri = ContentUris.withAppendedId(collection, id)
                images.add(
                    ImageItem(
                        id = id,
                        image = uri,
                        favourite = favourite,
                        selfie = selfie
                    )
                )
            }
        }
        return images
    }
}