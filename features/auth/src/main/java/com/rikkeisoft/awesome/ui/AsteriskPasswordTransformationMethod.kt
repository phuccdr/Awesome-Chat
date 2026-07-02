package com.rikkeisoft.awesome.ui

import android.text.method.PasswordTransformationMethod
import android.view.View

class AsteriskPasswordTransformationMethod : PasswordTransformationMethod() {
    override fun getTransformation(
        source: CharSequence, view: View
    ): CharSequence {
        return PasswordCharSequence(source)
    }

    private class PasswordCharSequence(
        private val source: CharSequence
    ) : CharSequence {
        override val length: Int
            get() = source.length

        override fun get(index: Int): Char {
            return '*'
        }

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            return "*".repeat(endIndex - startIndex)
        }
    }
}