package com.fromfinalform.blocks.core.locale

fun Int.getLocaleString(): String {
    return LocaleUtils.getLocaleString(this)
}

fun String.getLocaleString(): String {
    return LocaleUtils.getLocaleString(this)
}