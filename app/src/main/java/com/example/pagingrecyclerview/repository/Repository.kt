package com.example.pagingrecyclerview.repository

import com.example.pagingrecyclerview.api.PicsumService

/**
 * @Description: 仓库类隔离
 * @Author: zouji
 * @CreateDate: 2023/5/7 14:52
 */
class Repository {
    suspend fun getPictures() = PicsumService.create().getPicsumList()
}