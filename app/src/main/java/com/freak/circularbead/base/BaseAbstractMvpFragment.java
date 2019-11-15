package com.freak.circularbead.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import com.freak.circularbead.app.App;
import com.freak.httphelper.BasePresenter;
import com.freak.httphelper.log.LogUtil;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.freak.circularbead.app.App.DESIGN_WIDTH;


/**
 * MVP Fragment基类
 *
 * @author freak
 * @date 2019/9/11.
 */

public abstract class BaseAbstractMvpFragment<T extends BasePresenter> extends Fragment implements BaseView {
    protected T mPresenter;
    protected View mView;
    protected AppCompatActivity mActivity;
    protected Context mContext;
    private RelativeLayout netErrorView;
    protected View loadingView;
    private boolean hidden = false;
    private Unbinder unbinder;
    //Fragment的View加载完毕的标记
    private boolean isViewCreated;
    //Fragment对用户可见的标记
    private boolean isUIVisible;

    protected abstract T createPresenter();

    protected abstract int getLayoutId();

    protected abstract void initEventAndData(View view);

    protected abstract void initLazyData();

    /**
     * 显示加载中view，由子类实现
     */
    protected abstract void showLoading();

    @Override
    public void onAttach(Context context) {
        mActivity = (AppCompatActivity) context;
        mContext = context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        App.resetDensity(mActivity, DESIGN_WIDTH);
        mView = inflater.inflate(getLayoutId(), null);

        //返回一个Unbinder值（进行解绑），注意这里的this不能使用getActivity()
        unbinder = ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mPresenter == null) {
            mPresenter = createPresenter();
        }

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
        initEventAndData(view);
        isViewCreated = true;
        lazyLoad();
        showLoading();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //isVisibleToUser这个boolean值表示:该Fragment的UI 用户是否可见
        if (isVisibleToUser) {
            isUIVisible = true;
            lazyLoad();
        } else {
            isUIVisible = false;
        }
    }

    private void lazyLoad() {
        //这里进行双重标记判断,是因为setUserVisibleHint会多次回调,并且会在onCreateView执行前回调,必须确保onCreateView加载完毕且页面可见,才加载数据
        if (isViewCreated && isUIVisible) {
            initLazyData();
            //数据加载完毕,恢复标记,防止重复加载
            isViewCreated = false;
            isUIVisible = false;
            //Log.i("xx","走了");
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
//        this.hidden=hidden;
//        /**
//         * 判断该页面是否需要开启网络变化监听，如需要则启动广播
//         */
//        if (!hidden){
//            if (needRegisterNetworkChangeObserver()) {
//                NetStateChangeReceiver.registerObserver(this);
//            }
//        }

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            LogUtil.e("解绑view");
            mPresenter.detachView();
        }

        isViewCreated = false;
        isUIVisible = false;
    }

    /**
     * 打开一个Activity 默认 不关闭当前activity
     *
     * @param className
     */
    public void gotoActivity(Class<?> className) {
        gotoActivity(className, false);
    }

    /**
     * 打开一个Activity  关闭当前activity
     *
     * @param className
     */
    public void gotoActivityWithFinish(Class<?> className) {
        gotoActivity(className, true);
    }

    /**
     * 打开一个Activity，并控制是否finish
     *
     * @param className              className
     * @param isCloseCurrentActivity 是否关闭
     */
    public void gotoActivity(Class<?> className, boolean isCloseCurrentActivity) {
        gotoActivity(className, isCloseCurrentActivity, null);
    }

    /**
     * 打开一个activity，并传递数据
     *
     * @param className              className
     * @param isCloseCurrentActivity 是否关闭
     * @param bundle                 bundle数据
     */
    public void gotoActivity(Class<?> className, boolean isCloseCurrentActivity, Bundle bundle) {
        Intent intent = new Intent(mActivity, className);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        if (isCloseCurrentActivity) {
            mActivity.finish();
        }
    }

    /**
     * 打开一个带结果返回的activity，并传递数据
     *
     * @param className   className
     * @param bundle      bundle数据
     * @param requestCode 请求码
     */
    public void gotoActivityWithResult(Class<?> className, Bundle bundle, int requestCode) {
        Intent intent = new Intent(mActivity, className);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }
}
