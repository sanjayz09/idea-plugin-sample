package com.sanjay.application.mvp;

import androidx.annotation.UiThread;

/**
 * Created by sanjay.zsj09@gmail.com on 2020/8/19.
 */
public interface IMVPView<P> {

    @UiThread
    void setPresenter(P presenter);

    @UiThread
    void removePresenter();

}
