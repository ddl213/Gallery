package com.example.pagergallery.unit.listener;

import android.view.View;

import androidx.annotation.NonNull;

import com.example.pagergallery.unit.base.BaseAdapter;

public interface OnItemLongClickListener {

    void onItemLongClick(@NonNull BaseAdapter<?, ?> adapter, @NonNull View view, int position);

}