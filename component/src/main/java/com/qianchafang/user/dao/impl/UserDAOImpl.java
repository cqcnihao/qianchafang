package com.qianchafang.user.dao.impl;

import com.qianchafang.framework.dao.AbsDataDAO;
import com.qianchafang.user.dao.UserDAO;
import com.qianchafang.user.model.User;
import org.springframework.stereotype.Repository;

import java.util.Collections;

/**
 * Created by Poan on 2017/5/9.
 */
@Repository
public class UserDAOImpl extends AbsDataDAO<Long, User> implements UserDAO {

    private static final String bizName = "user";

    private static final String tableName = "user";


    public UserDAOImpl() {
        super(bizName, tableName, User.class);
    }

    public int getMaxId() {
        return getNamedParameterJdbcTemplate()
                .queryForObject("select id from " + tableName + " order by id desc limit 1",
                        Collections.<String, Object>emptyMap(), Integer.class)
                .intValue();
    }
}
