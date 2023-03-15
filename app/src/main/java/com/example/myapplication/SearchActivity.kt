package com.example.myapplication

import android.annotation.SuppressLint
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.ActivitySearchBinding
import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum
import com.github.houbb.pinyin.util.PinyinHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class SearchActivity : AppCompatActivity(), View.OnClickListener, TextWatcher {

    private lateinit var mView: ActivitySearchBinding
    private lateinit var popupWindow: PopupWindow
    private lateinit var popupWindowView: View
    var searchInfo = ""
    lateinit var tvSearch: TextView

    private val productList =
        listOf("苹果", "西瓜", "橘子", "香蕉", "梨子", "荔枝", "龙眼", "猕猴桃", "圣女果", "哈密瓜", "西红柿", "芒果", "葡萄")

    private val productPinyinList by lazy {
        val array = ArrayList<String>()
        productList.forEach { char ->
            array.add(PinyinHelper.toPinyin(char, PinyinStyleEnum.FIRST_LETTER).replace(" ", ""))
        }
        array
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mView = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(mView.root)
        initPopUpWindowSearch()

    }

    @SuppressLint("InflateParams")
    fun initPopUpWindowSearch() {
        popupWindow = PopupWindow()
        with(popupWindow) {
            width = ViewGroup.LayoutParams.WRAP_CONTENT
            height = ViewGroup.LayoutParams.MATCH_PARENT
            contentView = LayoutInflater.from(this@SearchActivity)
                .inflate(R.layout.layout_popupwindows_search_view, null)
            setBackgroundDrawable(BitmapDrawable())
            animationStyle = R.style.left_anim
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

    private fun getKeyboardInfo(): List<String> {
        return run {
            val str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
            str.map { it.toString() }
        }
    }

    override fun onResume() {
        super.onResume()
        showPopUpWindowSetting()
    }

    private fun showPopUpWindowSetting() {
        lifecycleScope.launch {
            delay(500L)
            popupWindow.showAtLocation(mView.clSearch, Gravity.START, 0, 0)
        }
    }

    private fun hidePopUpWindowSetting() {
        if (popupWindow.isShowing) {
            popupWindow.dismiss()
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

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        lifecycleScope.launch {
            if (count > 0) {
                searchResult(s.toString()).let {
                    if (it.isNotEmpty()) {
                        mView.flowlayoutSearchResult.removeAllViews()
                        mView.flowlayoutSearchResult.setTags(it)
                        return@launch
                    }
                }
            }
            mView.flowlayoutSearchResult.removeAllViews()
        }
    }

    override fun afterTextChanged(s: Editable?) {

    }

    private fun searchResult(keyboard: String): List<String> {
        val results: ArrayList<String> = ArrayList()
        if (productPinyinList.isNotEmpty()) {
            productPinyinList.forEachIndexed { index, char ->
                if ("iuv".contains(keyboard)) {
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
}