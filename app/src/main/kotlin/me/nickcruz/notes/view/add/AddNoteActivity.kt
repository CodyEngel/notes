package me.nickcruz.notes.view.add

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import butterknife.ButterKnife
import butterknife.OnClick
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_notes.*
import kotlinx.android.synthetic.main.content_add_note.*
import me.nickcruz.notes.R
import me.nickcruz.notes.viewmodel.notes.NotesViewModel

/**
 * Created by Nick Cruz on 6/13/17
 */
class AddNoteActivity : LifecycleActivity() {

    companion object {
        fun getStartIntent(context: Context): Intent = Intent(context, AddNoteActivity::class.java)
    }

    val compositeDisposable = CompositeDisposable()

    lateinit var notesViewModel: NotesViewModel

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)
        setActionBar(toolbar)
        ButterKnife.bind(this)

        notesViewModel = ViewModelProviders.of(this)
                .get(NotesViewModel::class.java)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    @OnClick(R.id.fab)
    internal fun addNoteClicked() {
        compositeDisposable.add(notesViewModel
                .addNote(titleEditText.text.toString(), contentEditText.text.toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ finish() }))
    }
}