package com.enb.selde.utils

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition
import com.enb.selde.draw.widget.DrawView

@BindingAdapter("drawBackground")
fun setImageUrl(view: DrawView ,url: String?){
    if(!TextUtils.isEmpty(url)){
        Glide.with(view.context)
            .load(url)
            .into(object : ViewTarget<DrawView, Drawable>(view){
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    view.background = resource
                }
            })
    }
}

@BindingAdapter("url")
fun setImageUrl(view: ImageView, url: String?){
    var landscapeMode = false

    if (!TextUtils.isEmpty(url)){
        Glide.with(view.context)
            .asBitmap()
            .load(url)
            .into(object : SimpleTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    if (resource.width > resource.height){landscapeMode = true}
                    RxBus.publish(RxEvent.EventIsLandscape(landscapeMode))
                    RxBus.publish(RxEvent.EventBackgroundImg(resource))
                    view.setImageBitmap(resource)
                }
            })
    }else{
//        Log.d("empty", "true")
        val param: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT)
        view.layoutParams = param
    }
}