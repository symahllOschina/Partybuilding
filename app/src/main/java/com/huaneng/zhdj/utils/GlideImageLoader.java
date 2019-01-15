package com.huaneng.zhdj.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.huaneng.zhdj.R;
import com.youth.banner.loader.ImageLoaderInterface;

/**
 * Created by TH on 2017/9/5.
 */

public class GlideImageLoader implements ImageLoaderInterface<ImageView> {

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        Glide.with(context).load(path).into(imageView);
    }

    @Override
    public ImageView createImageView(Context context) {
        ImageView imageView = new ImageView(context);
        imageView.setBackgroundResource(R.drawable.default_img);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }
}
