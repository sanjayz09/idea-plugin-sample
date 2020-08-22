package com.sanjay.application.mvp.impl;

import android.content.Context;
import android.util.AttributeSet;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.sanjay.application.mvp.IMVPView;

/**
 * Created by sanjay.zsj09@gmail.com on 2020/8/19.
 */
public abstract class BaseMVPViewConstraintLayout<P extends BaseMVPPresenter>
        extends ConstraintLayout implements IMVPView<P> {

    P mPresenter;

    public BaseMVPViewConstraintLayout(Context context) {
        super(context);
    }

    public BaseMVPViewConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseMVPViewConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setPresenter(P presenter) {
        if (presenter == null) {
            throw new NullPointerException("Presenter must not be null!");
        }

        this.mPresenter = presenter;
    }

    @Override
    public void removePresenter() {
        this.mPresenter = null;
    }

}
