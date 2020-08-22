package com.sanjay.application.mvp.impl;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sanjay.application.mvp.IMVPView;
import com.sanjay.application.mvp.delegate.IMVPDelegateCallBack;
import com.sanjay.application.mvp.util.Preconditions;

/**
 * Created by sanjay.zsj09@gmail.com on 2020/8/19.
 */
public abstract class BaseMVPComponentFragment<V extends IMVPView<P>, P extends BaseMVPPresenter<V>>
        extends Fragment implements IMVPDelegateCallBack<V, P> {

    protected V mView;
    protected P mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView = initMVPView();
        mPresenter = initPresenter();
        // binding
        getMVPView().setPresenter(getPresenter());
        getPresenter().takeView(getMVPView());
        getLifecycle().addObserver(getPresenter());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getLifecycle().removeObserver(getPresenter());
        getPresenter().dropView();
        getMVPView().removePresenter();
    }

    @NonNull
    @Override
    public P getPresenter() {
        Preconditions.checkNotNull(mPresenter, "MVp presenter is not init.d!");
        return mPresenter;
    }

    @NonNull
    @Override
    public V getMVPView() {
        Preconditions.checkNotNull(mPresenter, "MVP view is not init.d!");
        return mView;
    }

}
