package com.fromfinalform.blocks.presentation.view.common

import android.os.HandlerThread
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.databinding.BindingAdapter
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@BindingAdapter(
    "android:layout_marginLeft",
    "android:layout_marginTop",
    "android:layout_marginRight",
    "android:layout_marginBottom", requireAll = false)
fun View.bindMargin(marginLeft: Int, marginTop: Int, marginRight: Int, marginBottom: Int) {
    val lp = this.layoutParams as MarginLayoutParams
    lp.setMargins(marginLeft, marginTop, marginRight, marginBottom)
    this.layoutParams = lp
}

@BindingAdapter("android:text")
fun TextView.setText(value: Observable<String>) {
    value
        .observeOn(Schedulers.computation())
        .subscribe {
        this.text = it
    }
}