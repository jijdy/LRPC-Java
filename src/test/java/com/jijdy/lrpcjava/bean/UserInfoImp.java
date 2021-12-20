package com.jijdy.lrpcjava.bean;

public class UserInfoImp implements UserInfo{

    public UserInfoImp(){}

    @Override
    public String getInfo() {
        return "Username: jijdy  " +
                "UserId: 1234";
    }

    @Override
    public void noOP() {
        /* no operate */
    }
}
