package com.freak.circularbead;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.freak.circularbeadimageview.CircularBeadClipPathImageView;
import com.freak.circularbead.view.clippath.CircularBeadImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.image_view_collect_commodity_item_photo)
    CircularBeadImageView imageViewCollectCommodityItemPhoto;
    @BindView(R.id.image_view_add_to_shopping_cart_bg)
    CircularBeadImageView imageViewAddToShoppingCartBg;
    @BindView(R.id.image1)
    CircularBeadImageView image1;
    @BindView(R.id.clip_path_image)
    CircularBeadClipPathImageView clipPathImage;
    @BindView(R.id.image2)
    CircularBeadImageView image2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Glide.with(this).load(getDrawableUrl(R.drawable.ic_splash)).thumbnail(0.1f).into(imageViewCollectCommodityItemPhoto);
        Glide.with(this).load(getDrawableUrl(R.drawable.ic_splash)).thumbnail(0.1f).into(clipPathImage);
        Glide.with(this).load(getDrawableUrl(R.drawable.ic_splash)).thumbnail(0.1f).into(image2);
        Glide.with(this).load(getDrawableUrl(R.drawable.imageqq)).thumbnail(0.1f).into(clipPathImage);
        Glide.with(this).load(getDrawableUrl(R.drawable.imageqq)).thumbnail(0.1f).into(image1);
    }

    private String getDrawableUrl(@DrawableRes int drawable) {
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + getResources().getResourcePackageName(drawable) + "/"
                + getResources().getResourceTypeName(drawable) + "/"
                + getResources().getResourceEntryName(drawable));
        return imageUri.toString();
    }

    @OnClick({R.id.image_view_collect_commodity_item_photo, R.id.image_view_add_to_shopping_cart_bg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_view_collect_commodity_item_photo:
                break;
            case R.id.image_view_add_to_shopping_cart_bg:
                break;
        }
    }
}
