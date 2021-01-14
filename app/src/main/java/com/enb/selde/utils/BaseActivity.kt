package com.enb.selde.utils

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseActivity<T: ViewDataBinding> : AppCompatActivity() {

    lateinit var viewDataBinding: T

    var backPress = true

    abstract val layoutResourceId: Int

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)

        viewDataBinding = DataBindingUtil.setContentView(this, layoutResourceId)
    }

    fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun showLongToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    fun addDisposable(disposable: Disposable){
        compositeDisposable.add(disposable)
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        if (backPress){ super.onBackPressed() }
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        Log.d("clear", "onCleared")

        super.onDestroy()
    }
}