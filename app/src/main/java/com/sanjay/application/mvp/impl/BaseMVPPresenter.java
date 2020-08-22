package com.sanjay.application.mvp.impl;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.sanjay.application.mvp.IMVPPresenter;
import com.sanjay.application.mvp.IMVPView;
import com.sanjay.application.mvp.util.Preconditions;

/**
 * Created by sanjay.zsj09@gmail.com on 2020/8/19.
 */
public abstract class BaseMVPPresenter<V extends IMVPView> implements IMVPPresenter<V> {

    private LifecycleOwner mLifecycleOwner;

    private V mView;

    @Override
    public void onCreate(@NonNull LifecycleOwner lifecycleOwner) {
        mLifecycleOwner = lifecycleOwner;
    }

    @Override
    public void onStart(@NonNull LifecycleOwner lifecycleOwner) {

    }

    @Override
    public void onResume(@NonNull LifecycleOwner lifecycleOwner) {

    }

    @Override
    public void onPause(@NonNull LifecycleOwner lifecycleOwner) {

    }

    @Override
    public void onStop(@NonNull LifecycleOwner lifecycleOwner) {

    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner lifecycleOwner) {

    }

    @Override
    public void takeView(V view) {
        Preconditions.checkNotNull(view, "View must not be null!");
        this.mView = view;
    }

    @Override
    public void dropView() {
        this.mView = null;
    }

    @NonNull
    protected LifecycleOwner getLifecycleOwner() {
        Preconditions.checkNotNull(mLifecycleOwner, "LifecycleOwner == null");
        return mLifecycleOwner;
    }

    @NonNull
    public V getView() {
        Preconditions.checkNotNull(mView, "View has been detached!");
        return mView;
    }

}
