package com.yydcdut.note.mvp.v.login;

import com.yydcdut.note.mvp.IView;

/**
 * Created by yuyidong on 15/11/16.
 */
public interface IUserDetailFragView extends IView {
    void initUserDetail(String location, String useAge, String phone, String android, String storage);

    void updateLocation(String location);

    void initUserImage();

    void initUserInfo(boolean isQQLogin, String QQName, boolean isEvernoteLogin,
                      String evernoteName, String useStorage, String noteNumber,
                      String sandboxNumber, String wordNumber, String cloud);

    void logoutQQ();

    void logoutEvernote();

    void showProgressBar();

    void hideProgressBar();

    void showQQ(String name, String path);

    void showSnakebar(String message);

}
