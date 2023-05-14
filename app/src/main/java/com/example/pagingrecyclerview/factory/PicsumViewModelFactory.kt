package com.example.pagingrecyclerview.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pagingrecyclerview.repository.Repository

/**
 * @Description: 自定义ViewModelFactory，用于传入构造函数参数
 * @Author: zouji
 * @CreateDate: 2023/5/8 10:44
 */
class PicsumViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    // 原版：
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(PicsumViewModel::class.java)) {   // isAssignableFrom方法用于检查modelClass是否是PicsumViewModel的实例或者子类
//            return PicsumViewModel(repository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }

    // 简化板：用反射简化，这样就不用每个ViewModel都必须写一个Factory
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Repository::class.java).newInstance(repository)
    }
}