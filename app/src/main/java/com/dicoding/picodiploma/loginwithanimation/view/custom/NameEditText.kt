package com.dicoding.picodiploma.loginwithanimation.view.custom

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.dicoding.picodiploma.loginwithanimation.R
import com.google.android.material.textfield.TextInputEditText

class NameEditText : TextInputEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isEmpty()) {
                    setError(context.getString(R.string.error_name), null)
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }
}