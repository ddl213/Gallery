package com.example.pagergallery.unit.listener;

import android.view.View;

import androidx.annotation.NonNull;

import com.example.pagergallery.unit.base.adapter.BaseAdapter;

public interface OnItemLongClickListener {

    Boolean onItemLongClick(@NonNull BaseAdapter<?, ?> adapter, @NonNull View view, int position);

}
