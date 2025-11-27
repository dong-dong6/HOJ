package top.hcode.hoj.utils;

import cn.dev33.satoken.stp.StpUtil;
import top.hcode.hoj.shiro.AccountProfile;

/**
 * @Author: Himit_ZH
 * @Date: 2020/7/20 14:13
 * @Description: 用户账号工具类，用于获取当前登录用户信息
 */
public class AccountUtils {

    private static final String SESSION_USER_KEY = "userInfo";

    private AccountUtils() {
    }

    /**
     * 获取当前登录用户信息
     * @return AccountProfile 当前登录用户信息，未登录返回 null
     */
    public static AccountProfile getProfile() {
        if (!StpUtil.isLogin()) {
            return null;
        }
        return (AccountProfile) StpUtil.getSession().get(SESSION_USER_KEY);
    }

    /**
     * 设置当前登录用户信息到 Session
     * @param profile 用户信息
     */
    public static void setProfile(AccountProfile profile) {
        StpUtil.getSession().set(SESSION_USER_KEY, profile);
    }

    /**
     * 获取当前登录用户的 UID
     * @return uid
     */
    public static String getUid() {
        return (String) StpUtil.getLoginId();
    }

    /**
     * 判断当前是否已登录
     * @return true-已登录, false-未登录
     */
    public static boolean isLogin() {
        return StpUtil.isLogin();
    }
}
