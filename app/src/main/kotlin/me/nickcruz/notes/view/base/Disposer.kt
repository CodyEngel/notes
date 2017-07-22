package me.nickcruz.notes.view.base

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by Nick Cruz on 7/22/17
 */
internal class Disposer(lifecycle: Lifecycle) : LifecycleObserver {

    val compositeDisposable = CompositeDisposable()

    init {
        lifecycle.addObserver(this)
    }

    fun add(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cleanUp() {
        compositeDisposable.clear()
    }
}
