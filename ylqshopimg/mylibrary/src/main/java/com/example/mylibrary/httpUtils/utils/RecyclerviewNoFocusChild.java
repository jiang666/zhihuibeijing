package com.example.mylibrary.httpUtils.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class RecyclerviewNoFocusChild extends RecyclerView {

    public RecyclerviewNoFocusChild(@NonNull Context context) {
        super(context);
    }

    public RecyclerviewNoFocusChild(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerviewNoFocusChild(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void requestChildFocus(View child, View focused) {
    }
}
