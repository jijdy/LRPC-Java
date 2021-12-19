package com.jijdy.lrpcjava.bean;

public class UserInfoImp implements UserInfo{

    @Override
    public String getInfo() {
        return "Username: jijdy \n" +
                "UserId: 1234";
    }

    @Override
    public void noOP() {
        /* no operate */
    }
}
