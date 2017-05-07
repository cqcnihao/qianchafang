package com.qianchafang.framework.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Poan on 2017/5/7.
 */
public class EntityStatus {

    enum EStatus {
        // value值与数据库中字段对应；暂时想到这几个值，后续再补充
        normal(1),
        hide(2),
        delete(3);

        private final int value;

        private EStatus(int value) {
            this.value = value;
        }

        // 以下是为了方便获取EStatus的数据库字段值
        private static Map<Integer, EStatus> cacheMap = new HashMap<>(EStatus.values().length);

        static {
            for (EStatus eStatus : EStatus.values()) {
                cacheMap.put(eStatus.getValue(), eStatus);
            }
        }


        public int getValue() {
            return value;
        }

        public static EStatus fromValue(int value) {
            return cacheMap.get(value);
        }


    }

}
