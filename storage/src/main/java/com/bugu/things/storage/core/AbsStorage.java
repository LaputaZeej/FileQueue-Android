package com.bugu.things.storage.core;

import android.content.Context;

import com.bugu.things.storage.Constant;

/**
 * Author by xpl, Date on 2021/2/5.
 */
public abstract class AbsStorage<T> implements Storage<T> {
    private static final String TAG = Constant.TAG + "AbsStorage";
    protected Context context;

    public AbsStorage(Context context){
        this.context = context.getApplicationContext();
    }
}
