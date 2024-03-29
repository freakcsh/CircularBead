package com.freak.circularbead.base;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.freak.circularbead.app.App;


public class BaseActivity extends AppCompatActivity {
    private AppCompatActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //活动控制器
        mActivity=this;
        App.getInstance().addActivity(this,this.getClass());
//        EventBus.getDefault().register(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.getInstance().removeActivity(this);

//        EventBus.getDefault().unregister(this);
    }




}
