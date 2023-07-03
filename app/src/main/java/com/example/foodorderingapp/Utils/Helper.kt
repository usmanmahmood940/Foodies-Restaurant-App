package com.example.foodorderingapp.Utils

import android.content.ContentResolver
import android.content.res.Resources
import android.net.Uri

object Helper {

    fun getImageDrawableToUri(resources:Resources,imageDrawbale:Int) :Uri{
        val drawableResId =imageDrawbale
        val imageUri = Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                    resources.getResourcePackageName(drawableResId) + "/" +
                    resources.getResourceTypeName(drawableResId) + "/" +
                    resources.getResourceEntryName(drawableResId)
        )
       return imageUri
    }
}