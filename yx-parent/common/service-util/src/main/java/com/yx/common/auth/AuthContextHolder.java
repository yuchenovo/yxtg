package com.yx.common.auth;


import com.yx.vo.user.UserLoginVo;

/**
 * @author 97557
 */
public class AuthContextHolder {

    private static ThreadLocal<Long> userIdThread = new ThreadLocal<>();

    private static ThreadLocal<Long> wareIdThread = new ThreadLocal<>();

    private static ThreadLocal<UserLoginVo> userLoginVo = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        userIdThread.set(userId);
    }

    public static Long getUserId() {
        return userIdThread.get();
    }

    public static Long getWareId(){
        return wareIdThread.get();
    }

    public static void setWareId(Long wareId){
        wareIdThread.set(wareId);
    }

    public static UserLoginVo getUserLoginVo() {
        return userLoginVo.get();
    }

    public static void setUserLoginVo(UserLoginVo _userLoginVo) {
        userLoginVo.set(_userLoginVo);
    }


}
