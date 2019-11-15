package com.freak.circularbead;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.freak.circularbeadimageview.CircularBeadImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.image_view_collect_commodity_item_photo)
    CircularBeadImageView imageViewCollectCommodityItemPhoto;
    @BindView(R.id.image_view_add_to_shopping_cart_bg)
    CircularBeadImageView imageViewAddToShoppingCartBg;
    @BindView(R.id.image1)
    CircularBeadImageView image1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Glide.with(this).load(getDrawableUrl(R.drawable.ic_splash)).thumbnail(0.1f).into(imageViewCollectCommodityItemPhoto);
        Glide.with(this).load(getDrawableUrl(R.drawable.imageqq)).thumbnail(0.1f).into(image1);
    }

    private String getDrawableUrl(@DrawableRes int drawable) {
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + getResources().getResourcePackageName(drawable) + "/"
                + getResources().getResourceTypeName(drawable) + "/"
                + getResources().getResourceEntryName(drawable));
        return imageUri.toString();
    }

}
