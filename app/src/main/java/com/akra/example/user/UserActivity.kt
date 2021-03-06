package com.akra.example.user

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import com.akra.example.App
import com.akra.example.R
import com.akra.example.model.User
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.act_main.*
import javax.inject.Inject

/**
 * Created by Евгений on 8/21/2017.
 */

class UserActivity : AppCompatActivity() {

    @Inject lateinit var modulesPresentationModel: UserPresentationModel

    private var composite = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.component.inject(this)
        setContentView(R.layout.act_main)
        btnView.clicks()
                .flatMap { modulesPresentationModel.saveUser(formView.nameValue(), formView.surnameValue()) }
                .subscribe().addTo(composite)
        Observable.combineLatest(formView.nameChanges(), formView.surnameChanges(), BiFunction<String, String, User>(::User))
                .map { (name, surname) -> !TextUtils.isEmpty(name) && !TextUtils.isEmpty(surname) }
                .subscribe(btnView.getEnableConsumer()).addTo(composite)
        modulesPresentationModel.getUserState()
                .subscribe(formView.userConsumer()).addTo(composite)
    }

    override fun onDestroy() {
        super.onDestroy()
        composite.dispose()
        modulesPresentationModel.dispose()
    }
}
