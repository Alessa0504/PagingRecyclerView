package com.example.pagingrecyclerview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.pagingrecyclerview.api.PicsumItem
import com.example.pagingrecyclerview.databinding.ActivityMainBinding
import com.example.pagingrecyclerview.databinding.ItemProduct0Binding
import com.example.pagingrecyclerview.databinding.LayoutEmptyBinding
import com.example.pagingrecyclerview.factory.PicsumViewModelFactory
import com.example.pagingrecyclerview.repository.Repository
import com.example.pagingrecyclerview.viewmodel.PicsumViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var picsumViewModel: PicsumViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        val repository = Repository()
        val factory = PicsumViewModelFactory(repository)   // 当viewModel的构造函数需要传入参数，就需要自定义factory
//        picsumViewModel = PicsumViewModel()  // 不直接new，用ViewModelProvider创建viewModel
        picsumViewModel =
            ViewModelProvider(this, factory)[PicsumViewModel::class.java]   //创建viewModel
        picsumViewModel.getPics()  //网络请求图片
        // recyclerView展示响应数据变化
        picsumViewModel.picsumLivedata.observe(this) {
            activityMainBinding.rvRefresh.apply {
                setData(it)
                this.funcBindItem = { dataBean, binding ->
                    if (dataBean is PicsumItem) {
                        (binding as ItemProduct0Binding).picsumItem = dataBean
                    }
                }
            }
        }

    }
}