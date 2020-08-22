package com.sanjay.application.mvp;

import androidx.annotation.UiThread;

import com.sanjay.application.mvp.lifecycle.IBaseLifecycleObserver;

/**
 * Created by sanjay.zsj09@gmail.com on 2020/8/19.
 */
public interface IMVPPresenter<V extends IMVPView>
        extends IBaseLifecycleObserver {

    @UiThread
    void takeView(V view);

    @UiThread
    void dropView();

}
