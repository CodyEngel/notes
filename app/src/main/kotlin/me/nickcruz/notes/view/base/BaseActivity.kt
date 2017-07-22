package me.nickcruz.notes.view.base

import android.arch.lifecycle.LifecycleActivity
import io.reactivex.disposables.Disposable

/**
 * Created by Nick Cruz on 7/22/17
 */
abstract class BaseActivity : LifecycleActivity() {

    private val disposer: Disposer = Disposer(lifecycle)

    fun Disposable.attachToLifecycle() {
        lifecycle.addObserver(disposer)
        disposer.add(this)
    }
}