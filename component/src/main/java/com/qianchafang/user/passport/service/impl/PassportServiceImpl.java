package com.qianchafang.user.passport.service.impl;

import com.qianchafang.user.passport.service.PassportService;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Poan on 2017/5/10.
 * 可以通过微信api获取用户名，默认为其创建账户， 后期要做购物的话，
 * 可以先参考微信提供的接口，再确定是否有必要增加创建账户及密码的环节；
 */
public class PassportServiceImpl implements PassportService {

    @Override
    public int createAccount(String wxName) {
        return 0;
    }


    @Override
    public String createTicket(int userId) {
        // todo：票的生成待参考其他应用，，，
        return null;
    }

    @Override
    public Integer verifyTicket(String ticket) {
        return null;
    }

}
