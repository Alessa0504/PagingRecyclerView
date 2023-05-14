package com.example.pagingrecyclerview

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.pagingrecyclerview.untils.show
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader


/**
 * @Description:
 * @Author: zouji
 * @CreateDate: 2023/5/5 17:30
 */
class RefreshRecyclerView<T> : SmartRefreshLayout {

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    private var emptyLayoutId: Int = 0
    private var topLayoutId: Int = 0
    private var itemLayoutId: Int = 0
    private var enableTop = false
    private var params: ViewGroup.LayoutParams? = null
    private var rootView: FrameLayout? = null
    private var emptyLayout: View? = null
    private var topLayout: View? = null
    private var data = ArrayList<T>()
    private var recyclerView: RecyclerView? = null

    //把data[position]和binding提供给外部，让外部设置数据，内部刷新
    var funcBindItem: ((dataBean: T, binding: ViewDataBinding) -> Unit)? = null

    constructor(context: Context) : super(context) {
    }

    /**
     * 自定义控件会被调用该构造方法
     */
    constructor(
        context: Context,
        attributeSet: AttributeSet,
    ) : super(context, attributeSet) {
        val typedArray =
            context.obtainStyledAttributes(attributeSet, R.styleable.MyRefreshRecyclerView)
        emptyLayoutId =
            typedArray.getResourceId(R.styleable.MyRefreshRecyclerView_my_empty_layout, 0)
        itemLayoutId = typedArray.getResourceId(R.styleable.MyRefreshRecyclerView_my_item_layout, 0)
        topLayoutId = typedArray.getResourceId(R.styleable.MyRefreshRecyclerView_my_top_layout, 0)
        enableTop = typedArray.getBoolean(R.styleable.MyRefreshRecyclerView_my_top_enable, false)
        params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        // 设置SmartRefreshLayout的header和footer
        setRefreshHeader(ClassicsHeader(context))
        setRefreshFooter(ClassicsFooter(context))
        // 没有xml布局，动态添加布局
        initRoot(context)
//        initEmpty(context)
        initRecyclerView(context)
        initTop(context)
    }

    /**
     * 初始化根布局
     *
     * @param context
     */
    private fun initRoot(context: Context) {
        rootView = FrameLayout(context)
        addView(rootView, params)
    }

    /**
     * topLayout不属于RecyclerView内部，单独初始化
     *
     * @param context
     */
    private fun initTop(context: Context) {
        topLayoutId.takeIf { it != 0 }.let {
            val params =
                FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            params.gravity = Gravity.BOTTOM + Gravity.RIGHT  //通过params的gravity设置位置
            params.rightMargin = 100
            params.bottomMargin = 100
            topLayout = View.inflate(context, topLayoutId, null)  //添加至rootView
            rootView?.addView(topLayout, params)
            topLayout?.show(false)   //先不展示，滑动后再展示
            topLayout?.setOnClickListener {
                //smoothScrollToPosition先缓慢平滑向上滑动至第一个，50ms后若没有定位到第一再直接定位到第一(scrollToPosition直接定位，不平滑上移)
                recyclerView?.smoothScrollToPosition(0)
                Handler(Looper.getMainLooper()).postDelayed(Runnable { recyclerView?.scrollToPosition(0) }, 50)
                it.show(false)
            }
        }
    }

    /**
     * 添加空布局
     *
     * @param context
     */
    private fun initEmpty(context: Context) {
        emptyLayoutId.takeIf { it != 0 }.let {
            emptyLayout = View.inflate(context, emptyLayoutId, rootView)  //添加至rootView
            emptyLayout?.layoutParams = params
            emptyLayout?.show(data.size == 0)//当没有数据时就展示空布局
        }
    }

    private fun initRecyclerView(context: Context) {
        recyclerView = RecyclerView(context)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.layoutParams = params
        // 设置adapter
        val adapter = object : RecyclerView.Adapter<MyViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
                val inflater: LayoutInflater = LayoutInflater.from(context)
                val binding: ViewDataBinding?
                return when (viewType) {
                    VIEW_TYPE_EMPTY -> {
                        // 1. 如果使用ViewDataBinding，不能用这种方式inflate，要使用DataBindingUtil.inflate设置binding.root
                        // 2. 在onCreateViewHolder方法中通过DataBindingUtil.inflate去set binding；然后在onBindViewHolder方法中通过DataBindingUtil.getBinding去get binding
//                        MyViewHolder(
//                            LayoutInflater.from(parent.context)
//                                .inflate(emptyLayoutId, parent, false)
//                        )
                        // set binding
                        binding = DataBindingUtil.inflate(inflater, emptyLayoutId, parent, false)
                        MyViewHolder(binding.root)
                    }

                    VIEW_TYPE_ITEM -> {
//                        MyViewHolder(
//                            LayoutInflater.from(parent.context).inflate(itemLayoutId, parent, false)
//                        )
                        binding = DataBindingUtil.inflate(inflater, itemLayoutId, parent, false)
                        MyViewHolder(binding.root)
                    }

                    else -> {
                        throw IllegalArgumentException("Invalid view type")
                    }
                }
            }

            override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
                val itemView = holder.itemView   //就是方法onCreateViewHolder中的itemView代表每个item布局
                // get binding
                val binding =
                    DataBindingUtil.getBinding<ViewDataBinding>(itemView)  //获取itemView的binding，无需findViewById
                when (getItemViewType(position)) {
                    VIEW_TYPE_EMPTY -> {

                    }

                    VIEW_TYPE_ITEM -> {
                        funcBindItem!!(data[position], binding!!)
                    }
                }
                binding?.let {
                    it.executePendingBindings()
                }
            }

            override fun getItemCount(): Int {
                return data.size
            }

            override fun getItemViewType(position: Int): Int {
                return if (data.isEmpty()) {
                    VIEW_TYPE_EMPTY
                } else {
                    VIEW_TYPE_ITEM
                }
            }
        }
        recyclerView?.adapter = adapter  //设置adapter
        rootView?.addView(recyclerView, params)
        // 设置recyclerView的滑动监听
        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 50) {
                    topLayout?.show(true)
                }
            }
        })
    }

    /**
     * 从外部设置data
     *
     * @param data
     */
    fun setData(data: ArrayList<T>) {
        this.data.clear()
        this.data.addAll(data)
        recyclerView?.adapter?.notifyDataSetChanged()  //会触发onCreateViewHolder & onBindViewHolder
    }
}

class MyViewHolder(itemView: View) : ViewHolder(itemView)