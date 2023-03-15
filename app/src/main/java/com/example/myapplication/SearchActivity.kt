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
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.ActivitySearchBinding
import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum
import com.github.houbb.pinyin.util.PinyinHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.w3c.dom.Text

class SearchActivity : AppCompatActivity(), View.OnClickListener, TextWatcher {

    lateinit var mView: ActivitySearchBinding
    lateinit var popupWindow: PopupWindow
    lateinit var popupWindowView: View
    var searchInfo = ""
    lateinit var tvSearch: TextView

    private val product = listOf("苹果", "西瓜", "橘子", "香蕉", "梨子", "荔枝", "龙眼")

    val productFirstWords by lazy {
        val list = ArrayList<String>()
        product.forEach { char ->
            list.add(PinyinHelper.toPinyin(char, PinyinStyleEnum.FIRST_LETTER).replace(" ",""))
        }
        list
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
            isFocusable = true
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

    fun getKeyboardInfo(): List<String> {
        val list = ArrayList<String>()
        val str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        str.forEach {
            list.add(it.toString())
        }
        return list
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
        if (count > 0) {

            Toast.makeText(this, s.toString(), Toast.LENGTH_SHORT).show()
            return
        }
    }

    override fun afterTextChanged(s: Editable?) {

    }
}