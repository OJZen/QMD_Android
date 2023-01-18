package com.qmd.jzen.adapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

/**
 * Create by OJun on 2021/9/5.
 *
 */

@BindingAdapter("imageFromUrl")
fun bindImageFromUrl(view:ImageView, imageUrl:String?){
    imageUrl?.apply {
        Glide.with(view.context)
            .load(this)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }
}

