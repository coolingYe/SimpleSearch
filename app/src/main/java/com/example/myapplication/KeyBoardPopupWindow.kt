package com.example.myapplication

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.NoCopySpan
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum
import com.github.houbb.pinyin.util.PinyinHelper
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.function.Consumer
import java.util.regex.Pattern

class KeyBoardPopupWindow(private val context: Context, private val rootView:View?=null) : View.OnClickListener, TextWatcher {
    private lateinit var popupWindow: PopupWindow
    private lateinit var popupWindowView: View
    private var searchInfo = ""
    lateinit var tvSearch: TextView
    private val sHandler = Handler(Looper.getMainLooper())
    private var textChanges: TextChanges? = null

    private lateinit var productList: List<String>

    companion object {
        val productListTest = listOf("苹果", "西瓜", "橘子", "香蕉", "梨子", "荔枝", "龙眼", "猕猴桃", "圣女果", "哈密瓜", "西红柿", "芒果", "葡萄")
    }

    fun setProductList(productList: List<String>) {
        this.productList = productList
    }

    private val productPinyinList by lazy {
        val array = ArrayList<String>()
        productList.forEach { char ->
            array.add(PinyinHelper.toPinyin(char, PinyinStyleEnum.FIRST_LETTER).replace(" ", ""))
        }
        array
    }

    fun initPopUpWindowSearch() {
        popupWindow = PopupWindow()
        with(popupWindow) {
            width = ViewGroup.LayoutParams.WRAP_CONTENT
            height = ViewGroup.LayoutParams.MATCH_PARENT
            contentView = LayoutInflater.from(context)
                .inflate(R.layout.layout_popupwindows_search_view, null)
            setBackgroundDrawable(BitmapDrawable())
            animationStyle = R.style.left_anim
            isFocusable = true
        }
        popupWindowView = popupWindow.contentView

        val keyboard = popupWindowView.findViewById<CustomerFlowLayout>(R.id.flowlayout_search)
        tvSearch = popupWindowView.findViewById(R.id.tv_search) as TextView
        tvSearch.addTextChangedListener(this)
        val tvClean = popupWindowView.findViewById(R.id.tv_clean) as TextView
        val tvBackspace = popupWindowView.findViewById(R.id.tv_backspace) as TextView
        tvClean.setOnClickListener(this)
        tvBackspace.setOnClickListener(this)
        keyboard.removeAllViews()
        keyboard.setTags(getKeyboardInfo())
        keyboard.setOnTagItemClickListener {
            it as TextView
            searchInfo += it.text
            tvSearch.text = searchInfo
        }
    }

    fun show() {
        rootView?.let {
            (context as AppCompatActivity).lifecycleScope.launch {
                delay(500L)
                popupWindow.showAtLocation(it, Gravity.START, 0, 0)
            }
        }
    }

    fun hide() {
        if (popupWindow.isShowing) {
            popupWindow.dismiss()
        }
    }

    private fun getKeyboardInfo(): List<String> {
        return run {
            val str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
            str.map { it.toString() }
        }
    }

    override fun onClick(v: View?) {
        v?.let {
            when (v.id) {
                R.id.tv_clean -> {
                    searchInfo = ""
                    tvSearch.text = searchInfo
                }
                R.id.tv_backspace -> {
                    if (searchInfo.isNotEmpty()) {
                        searchInfo = searchInfo.substring(0, searchInfo.length - 1)
                        tvSearch.text = searchInfo
                    }
                }
                else -> {}
            }
        }
    }

    fun setOnTextChangesListener(textChanges: TextChanges) {
        this.textChanges = textChanges
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        textChanges?.let {
            sHandler.post(it.beforeTextChanged(s, start, count, after))
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        textChanges?.let {
            sHandler.post(it.onTextChanged(s, start, before, count))
        }
    }

    override fun afterTextChanged(s: Editable?) {
        textChanges?.let {
            sHandler.post(it.afterTextChanged(s))
        }
    }

    fun searchResult(keyboard: String): List<String> {
        val results: ArrayList<String> = ArrayList()
        if (productPinyinList.isNotEmpty()) {
            productPinyinList.forEachIndexed { index, char ->
                if ("iuv".contains(keyboard, true)) {
                    return results
                }
                val matcher = Pattern.compile(keyboard, Pattern.CASE_INSENSITIVE).matcher(char)
                if (matcher.find()) {
                    if (matcher.start() == 0) {
                        results.add(productList[index])
                    }
                }
            }
        }
        return results
    }

    interface TextChanges {
        fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) : Runnable

        fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int): Runnable

        fun afterTextChanged(s: Editable?): Runnable
    }
}