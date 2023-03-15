package com.example.myapplication

import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum
import com.github.houbb.pinyin.util.PinyinHelper
import org.junit.Test

import org.junit.Assert.*
import java.util.regex.Pattern

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
    private fun firstLetterTest() {
        val keyboard = "p"
        val product = listOf("苹果", "西瓜", "橘子", "香蕉", "梨子", "荔枝", "龙眼", "猕猴桃", "圣女果", "哈密瓜", "西红柿", "芒果", "葡萄")
        val firstLetters = ArrayList<String>()
        product.forEach { char ->
            firstLetters.add(PinyinHelper.toPinyin(char, PinyinStyleEnum.FIRST_LETTER).replace(" ", ""))
        }
        firstLetters.forEachIndexed { index, char ->
            if ("iuv".contains(keyboard)) {
                return
            }
            val matcher = Pattern.compile(keyboard, Pattern.CASE_INSENSITIVE).matcher(char)
            if (matcher.find()) {
                if (matcher.start() == 0) {
                    println(product[index])
                }
            }
        }
    }
}