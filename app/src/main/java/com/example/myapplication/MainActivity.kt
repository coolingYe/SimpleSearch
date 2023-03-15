package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), ViewTreeObserver.OnGlobalFocusChangeListener, View.OnKeyListener, View.OnClickListener {

    lateinit var mView: ActivityMainBinding
    lateinit var popupWindow: PopupWindow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mView = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mView.root)
        initPopUpWindowSetting()
        mView.clSimperHeader.setOnClickListener {
            showPopUpWindowSetting()
        }
        mView.clSimperHeader.setOnFocusChangeListener { _, hasFocus ->
            run {
                if (hasFocus) {
                    showPopUpWindowSetting()
                } else hidePopUpWindowSetting()
            }
        }
        window.decorView.viewTreeObserver.addOnGlobalFocusChangeListener(this)
    }

    @SuppressLint("InflateParams")
    fun initPopUpWindowSetting() {
        popupWindow = PopupWindow()
        with(popupWindow) {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            contentView = LayoutInflater.from(this@MainActivity).inflate(R.layout.layout_popupwindows_header_view, null)
            setBackgroundDrawable(BitmapDrawable())
            isFocusable = true
            animationStyle = R.style.top_anim
        }
        val popupWindow = popupWindow.contentView
        popupWindow.findViewById<Button>(R.id.btn_view4).setOnClickListener(this)
        popupWindow.findViewById<Button>(R.id.btn_view4).setOnKeyListener(this)
    }

    private fun showPopUpWindowSetting() {
        popupWindow.showAtLocation(mView.clMain, Gravity.TOP, 0, 0)
        mView.root.viewTreeObserver.addOnGlobalFocusChangeListener(this)
    }

    private fun hidePopUpWindowSetting() {
        if (popupWindow.isShowing) {
            popupWindow.dismiss()
            mView.btn1.requestFocus()
        }
    }

    override fun onGlobalFocusChanged(oldFocus: View?, newFocus: View?) {
        Log.d("test", "onGlobalFocusChanged newFocus: $newFocus")
        Log.d("test", "onGlobalFocusChanged oldFocus: $oldFocus")
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            hidePopUpWindowSetting()
        }
        return false
    }

    override fun onClick(v: View?) {
        v?.let {
            when(v.id) {
                R.id.btn_view4 -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                }
                R.id.btn_view5 -> {

                }
                R.id.btn_view6 -> {

                }
            }
        }
    }
}