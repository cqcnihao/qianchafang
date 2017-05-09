package com.qianchafang.user.model;

import com.qianchafang.framework.model.HasCreateTime;
import com.qianchafang.framework.model.HasData;
import com.qianchafang.framework.model.HasId;
import com.qianchafang.framework.model.HasStatus;

/**
 * Created by Poan on 2017/5/9.
 */
public class User implements HasData, HasId<Long>, HasCreateTime, HasStatus {
    @Override
    public Long getId() {
        return null;
    }
}
