package com.example.pagergallery.unit.listener;

public interface BaseListenerImp {

    void setOnItemClickListener(OnItemClickListener listener);
    void setOnItemScaleListener(OnItemScaleListener listener);
    void setOnItemLongClickListener(OnItemLongClickListener listener);
    void setOnItemDoubleClickListener(OnItemDoubleClickListener listener);
    void setOnItemChildClickListener(OnItemChildClickListener listener);

}
