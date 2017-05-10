package com.qianchafang.user.passport.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Created by Poan on 2017/5/10.
 */
public interface PassportService {
    /**
     * 使用微信账户名为其创建账户，密码为其随机设置一个；
     * 后期再做调整
     */
    int createAccount(String wxName);

    String createTicket(int userId);

    /**
     * 调用本地接口时，需要对其票据进行验证;
     * 考虑到后期可能会对验证结果进行扩展，不使用boolean，使用integer
     */
    Integer verifyTicket(String ticket);
}
