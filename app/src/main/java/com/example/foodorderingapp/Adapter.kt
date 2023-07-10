package com.example.foodorderingapp

import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide


@BindingAdapter("imageFromUrl")
fun ImageView.imageFromUrl(url: String){
    Glide.with(this.context).load(url).into(this)
}

@BindingAdapter("imageByQuantity")
fun ImageView.imageByQuantity(quantity: Int){
    if(quantity > 1) {
        this.setImageResource(R.drawable.ic_minus)
    }
    else{
        this.setImageResource(R.drawable.ic_minus_disabled)
    }

}

