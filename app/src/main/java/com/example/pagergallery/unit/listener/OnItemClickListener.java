package com.example.pagergallery.unit.listener;

import android.view.View;

import androidx.annotation.NonNull;

import com.example.pagergallery.unit.base.BaseAdapter;

import org.jetbrains.annotations.NotNull;

public interface OnItemClickListener {

    void onItemClick(@NonNull BaseAdapter<?,?> adapter, @NonNull View view, int position);

}