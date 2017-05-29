package io.github.iurimenin.easyprod.app.util

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.rengwuxian.materialedittext.MaterialEditText
import java.text.NumberFormat


/**
 * Created by Iuri Menin on 29/05/17.
 */
class MoneyMaskMaterialEditText : MaterialEditText {

    private var textWatcher: TextWatcher? = null

    constructor(context: Context) : super(context) {
        addTextWatcher()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        addTextWatcher()
    }

    constructor(context: Context, attrs: AttributeSet, style: Int) : super(context, attrs, style) {
        addTextWatcher()
    }

    fun setTextFromDouble(value: Double?) {

        this@MoneyMaskMaterialEditText.setText(NumberFormat.getCurrencyInstance()
                .format(value).replace(NumberFormat.getCurrencyInstance()
                .currency.symbol, ""))
    }

    val double: Double get() {

        if (this@MoneyMaskMaterialEditText.text.toString().isNullOrEmpty())
            return 0.0

        val cleanString = this@MoneyMaskMaterialEditText.text.toString()
                .replace("[,.*]".toRegex(), "")
                .replace(NumberFormat.getCurrencyInstance().currency.symbol, "")
        return java.lang.Double.parseDouble(cleanString) / 100
    }

    private fun addTextWatcher() {

        textWatcher = object : TextWatcher {

            private var current = ""

            override fun afterTextChanged(arg0: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (s.toString() != current) {
                    this@MoneyMaskMaterialEditText.removeTextChangedListener(this)

                    val cleanString = s.toString()
                            .replace("[,.*]".toRegex(), "")
                            .replace(NumberFormat.getCurrencyInstance().currency.symbol, "")

                    if (!cleanString.isNullOrEmpty()) {
                        val parsed = java.lang.Double.parseDouble(cleanString)
                        val formatted = NumberFormat.getCurrencyInstance().format(parsed / 100)
                                .replace(NumberFormat.getCurrencyInstance().currency.symbol, "")
                        current = formatted
                        this@MoneyMaskMaterialEditText.setText(formatted)
                        this@MoneyMaskMaterialEditText.setSelection(formatted.length)
                    } else {
                        this@MoneyMaskMaterialEditText.text = null
                    }

                    this@MoneyMaskMaterialEditText.addTextChangedListener(this)
                }
            }
        }

        this.addTextChangedListener(textWatcher)
    }
}