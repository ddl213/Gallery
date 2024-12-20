package com.example.pagergallery.unit.listener;

import android.view.View;

import androidx.annotation.NonNull;

import com.example.pagergallery.unit.base.BaseAdapter;

import org.jetbrains.annotations.NotNull;

public interface OnItemDoubleClickListener {

    void onItemDoubleClick(@NotNull BaseAdapter<?, ?> adapter, @NonNull View view, int position);

}