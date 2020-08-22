package com.sanjay.application.mvp.lifecycle;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

/**
 * Created by sanjay.zsj09@gmail.com on 2020/8/20.
 */
public interface IBaseLifecycleObserver extends LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onCreate(@NonNull LifecycleOwner lifecycleOwner);

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onStart(@NonNull LifecycleOwner lifecycleOwner);

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResume(@NonNull LifecycleOwner lifecycleOwner);

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void onPause(@NonNull LifecycleOwner lifecycleOwner);

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onStop(@NonNull LifecycleOwner lifecycleOwner);

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy(@NonNull LifecycleOwner lifecycleOwner);

}
