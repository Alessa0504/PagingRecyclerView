package com.example.pagingrecyclerview.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pagingrecyclerview.api.PicsumItem
import com.example.pagingrecyclerview.repository.Repository
import kotlinx.coroutines.launch

/**
 * @Description:
 * @Author: zouji
 * @CreateDate: 2023/5/7 13:54
 */
class PicsumViewModel(private val repository: Repository) : ViewModel() {
    var picsumLivedata = MutableLiveData<ArrayList<PicsumItem>>()

    /**
     * 获取图片的请求
     */
    fun getPics() {
        viewModelScope.launch {
            //todo fuc loading
//            funcBeforeRequest.invoke()
            val response = repository.getPictures()  //retrofit suspend自动切换子、主线程
            if (response.isSuccessful) {
                response.body()?.let {
                    picsumLivedata.value = it
                }
            } else {
                Log.e("PicsumViewModel", "request error ${response.message()}")
            }
        }
    }
}