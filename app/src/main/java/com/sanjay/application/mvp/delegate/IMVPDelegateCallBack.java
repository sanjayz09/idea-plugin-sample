package com.sanjay.application.mvp.delegate;

import androidx.annotation.NonNull;

import com.sanjay.application.mvp.IMVPPresenter;
import com.sanjay.application.mvp.IMVPView;

/**
 * Created by sanjay.zsj09@gmail.com on 2020/8/20.
 */
public interface IMVPDelegateCallBack<V extends IMVPView<P>, P extends IMVPPresenter<V>> {

    @NonNull
    P initPresenter();

    @NonNull
    P getPresenter();

    @NonNull
    V initMVPView();

    @NonNull
    V getMVPView();

}
