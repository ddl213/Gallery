package com.example.pagergallery.unit.listener;

import android.view.View;

import androidx.annotation.NonNull;

import com.example.pagergallery.unit.base.adapter.BaseAdapter;

import org.jetbrains.annotations.NotNull;

public interface OnItemScaleListener {

    void onItemScale(@NotNull BaseAdapter<?, ?> adapter, @NonNull View view,int position, Float scaleFactor);

}
