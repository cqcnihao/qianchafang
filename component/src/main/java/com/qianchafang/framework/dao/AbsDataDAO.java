package com.qianchafang.framework.dao;

import com.qianchafang.framework.model.HasUuid;

/**
 * Created by Poan on 2017/5/7.
 * 实体基类的四个基本属性：
 * id
 * `data` 用于存放扩展属性，以json格式存储到data字段
 * status 实体的各种状态
 * createTime 创建时间
 * 由于实体经常需要与其ID一并获取，由对model和Id进行一系列的预处理，以便子类方便操作
 */
public class AbsDataDAO<K extends Number, V extends HasUuid> {





}
