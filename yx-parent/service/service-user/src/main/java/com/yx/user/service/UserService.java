package com.yx.user.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.yx.model.user.User;
import com.yx.vo.user.LeaderAddressVo;
import com.yx.vo.user.UserLoginVo;

public interface UserService extends IService<User> {
    /**
     * openId判断是否是第一次使用微信登录
     *
     * @param openid OpenID
     * @return {@link User}
     */
    User getUserByOpenId(String openid);

    /**
     * 根据userId查询提货点和团长信息
     *
     * @param userId 用户 ID
     * @return {@link LeaderAddressVo}
     */
    LeaderAddressVo getLeaderAddressByUserId(Long userId);

    /**
     * 获取用户登录名 VO
     *
     * @param id 同上
     * @return {@link UserLoginVo}
     */
    UserLoginVo getUserLoginVo(Long id);
}
