package com.enb.selde.main

import android.os.Bundle
import androidx.activity.viewModels
import com.enb.selde.R.layout.activity_main
import com.enb.selde.databinding.ActivityMainBinding
import com.enb.selde.utils.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override val layoutResourceId = activity_main
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewDataBinding.viewModel = viewModel
        viewDataBinding.lifecycleOwner = this
    }
}