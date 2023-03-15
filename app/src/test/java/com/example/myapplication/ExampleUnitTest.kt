package com.example.myapplication

import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum
import com.github.houbb.pinyin.util.PinyinHelper
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun firstWordTest() {
        val product = listOf("苹果", "西瓜", "橘子", "香蕉", "梨子", "荔枝", "龙眼")
        product.forEach { char ->
            println(PinyinHelper.toPinyin(char, PinyinStyleEnum.FIRST_LETTER).replace(" ",""))
        }
    }
}