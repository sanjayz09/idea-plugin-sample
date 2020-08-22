package com.sanjay.application.mvp.util;

import androidx.annotation.NonNull;

/**
 * Created by sanjay.zsj09@gmail.com on 2020/8/20.
 */
public class Preconditions {

    private Preconditions() {
        throw new IllegalStateException("You can't instantiate me!");
    }

    @NonNull
    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        } else {
            return reference;
        }
    }

    @NonNull
    public static <T> T checkNotNull(T reference, Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        } else {
            return reference;
        }
    }

}
