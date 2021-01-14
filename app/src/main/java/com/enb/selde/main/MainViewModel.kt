package com.enb.selde.main

import android.content.Intent
import android.view.View
import com.enb.selde.draw.DrawingActivity
import com.enb.selde.utils.BaseViewModel

class MainViewModel: BaseViewModel() {

    fun clickIntent(view: View){
        val intent = Intent(view.context, DrawingActivity::class.java)
        view.context.startActivity(intent)
    }
}