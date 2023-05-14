package com.example.pagingrecyclerview.untils

import android.view.View
import androidx.lifecycle.ViewModel

/**
 * @Description:
 * @Author: zouji
 * @CreateDate: 2023/5/5 19:33
 */
fun View.show(isShow: Boolean) = this.apply {
    if (isShow) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}