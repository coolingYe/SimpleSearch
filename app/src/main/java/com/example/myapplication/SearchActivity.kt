package com.example.myapplication

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.ActivitySearchBinding
import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum
import com.github.houbb.pinyin.util.PinyinHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class SearchActivity : AppCompatActivity(), View.OnClickListener, TextWatcher {

    private lateinit var mView: ActivitySearchBinding
    var searchInfo = ""
    lateinit var tvSearch: TextView

    private val productList = listOf("苹果","水蜜桃","黄桃","油桃","杨桃","樱桃","车厘子","猕猴桃","奇异果",
        "菠萝","凤梨","西瓜","山楂","橙子","榴莲","菠萝蜜","柚子","蓝莓","红梅","草莓","黑莓","柠檬","红枣","香蕉",
        "甜瓜","沙果","海棠","野樱梅","枇杷","欧楂","香梨","雪梨","温柏","蔷薇果","花楸","火龙果","蟠桃","李子","梅子",
        "青梅","西梅","白玉樱桃","䨱盆子","云梅","罗甘梅","白里叶梅","砂糖桔","青柠","金桔","葡萄柚","香木缘","佛手","指橙",
        "黄皮果","哈密瓜","香瓜","白兰瓜","刺角瓜","金铃子","大蕉","南洋红香蕉","提子","醋栗","黑醋栗","红醋栗","脐橙","木瓜",
        "龙眼","桂圆","荔枝","芒果","黄金瓜","杨梅","莲雾","雪莲果","黑布林","圣女果","石榴","桑葚","人参果","冬枣","椰子","仙人掌果")

    private lateinit var productPinyinList: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mView = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(mView.root)
        lifecycleScope.launch(Dispatchers.IO) {
            val array = ArrayList<String>()
            productList.forEach { char ->
                array.add(
                    PinyinHelper.toPinyin(char, PinyinStyleEnum.FIRST_LETTER).replace(" ", "")
                )
            }
            productPinyinList = array
        }
        initPopUpWindowSearch()
    }


    private fun initPopUpWindowSearch() {
        val popupWindowView = mView.layoutSearch.layoutSearchView
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
        val animation: Animation =
            AnimationUtils.loadAnimation(this@SearchActivity, R.anim.enter_left_long)
        mView.layoutSearch.layoutSearchView.startAnimation(animation)
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
}