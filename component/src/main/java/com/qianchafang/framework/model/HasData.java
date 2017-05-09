package com.qianchafang.framework.model;

import java.util.Map;

/**
 * Created by Poan on 2017/5/9.
 */
public interface HasData {
    String getData();

    Map<String, Object> resolvedData();

    default void updateData(String data) {
    }
}
