package com.bugu.things.storage.type;

import com.bugu.things.storage.App;
import com.bugu.things.storage.Constant;
import com.bugu.things.storage.bean.A;
import com.bugu.things.storage.bean.B;
import com.bugu.things.storage.bean.Delete;
import com.bugu.things.storage.bean.Put;
import com.bugu.things.storage.bean.Storage;
import com.bugu.things.storage.bean.Take;
import com.bugu.things.storage.ext.ExtsKt;

import java.util.List;

/**
 * Author by xpl, Date on 2021/2/8.
 */

@Storage
public interface MyStorage {

    @Put(name = "a2.txt")
    boolean put(A<List<B>> a);

    @Take(name = "a2.txt")
    A<List<B>> take();

    @Delete(name = "a2.txt")
    boolean delete();
}
