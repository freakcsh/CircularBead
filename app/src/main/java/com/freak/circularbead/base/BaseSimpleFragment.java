package com.freak.circularbead.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.freak.circularbead.app.App;
import com.freak.httphelper.log.LogUtil;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.freak.circularbead.app.App.DESIGN_WIDTH;


/**
 * 无MVP的Fragment基类
 * @author Administrator
 * @date 2019/9/11.
 */

public abstract class BaseSimpleFragment extends Fragment {

    protected View mView;
    protected AppCompatActivity mActivity;
    protected Context mContext;
    private View netErrorView;

    protected abstract int getLayoutId();

    protected abstract void initEventAndData(View view);

    protected abstract void initLazyData();
    private Unbinder unbinder;
    //Fragment的View加载完毕的标记
    private boolean isViewCreated;
    //Fragment对用户可见的标记
    private boolean isUIVisible;

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
        mView = inflater.inflate(getLayoutId(), container, false);
        //返回一个Unbinder值（进行解绑），注意这里的this不能使用getActivity()
        unbinder = ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initEventAndData(view);
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
            LogUtil.e("懒加载 加载数据");
            initLazyData();
            //数据加载完毕,恢复标记,防止重复加载
            isViewCreated = false;
            isUIVisible = false;
            //Log.i("xx","走了");
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

}