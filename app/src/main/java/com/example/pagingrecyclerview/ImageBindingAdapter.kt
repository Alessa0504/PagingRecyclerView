package com.example.pagingrecyclerview

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

/**
 * @Description:
 * @Author: zouji
 * @CreateDate: 2023/5/7 12:29
 */
object ImageBindingAdapter {
    @BindingAdapter("load")   //重命名
    @JvmStatic
    fun loadUrl(imageView: ImageView, url: String?) {  //参数一表示作用于ImageView
        if (!url.isNullOrEmpty()) {
            Glide.with(imageView.context).load(url).into(imageView)
        } else {
            imageView.setImageResource(R.drawable.ic_launcher_foreground)
        }
    }
}