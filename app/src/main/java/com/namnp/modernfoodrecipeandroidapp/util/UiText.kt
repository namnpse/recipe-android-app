package com.namnp.modernfoodrecipeandroidapp.util

import android.content.Context
import androidx.annotation.StringRes

sealed class UiText {
    data class PureString(val value: String): UiText()
    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any,
    ): UiText()

    fun asString(context: Context): String {
        return when(this) {
            is PureString -> value
            is StringResource -> context.getString(resId, *args)
        }
    }
}