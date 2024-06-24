package com.yx.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.yx.common.auth.AuthContextHolder;
import com.yx.common.constant.RedisConst;
import com.yx.common.exception.YxException;
import com.yx.common.result.Result;
import com.yx.common.result.ResultCodeEnum;
import com.yx.common.utils.JwtHelper;
import com.yx.enums.UserType;
import com.yx.model.user.User;
import com.yx.user.service.UserService;
import com.yx.user.utils.ConstantPropertiesUtil;
import com.yx.user.utils.HttpClientUtils;
import com.yx.vo.user.LeaderAddressVo;
import com.yx.vo.user.UserLoginVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 97557
 */
@RestController
@RequestMapping("/api/user/weixin")
public class WeixinApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    //用户微信授权登录
    @ApiOperation(value = "微信登录获取openid(小程序)")
    @GetMapping("/wxLogin/{code}")
    public Result loginWx(@PathVariable String code) {
        String wxOpenAppId = ConstantPropertiesUtil.WX_OPEN_APP_ID;
        String wxOpenAppSecret = ConstantPropertiesUtil.WX_OPEN_APP_SECRET;
        StringBuffer url = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/jscode2session")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&js_code=%s")
                .append("&grant_type=authorization_code");
        String tokenUrl = String.format(url.toString(),
                                        wxOpenAppId,
                                        wxOpenAppSecret,
                                        code);
        String result = null;
        try {
            result = HttpClientUtils.get(tokenUrl);
        } catch (Exception e) {
            throw new YxException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }

        //请求微信接口服务，得到 session_key 和 openid
        JSONObject jsonObject = JSONObject.parseObject(result);
        String session_key = jsonObject.getString("session_key");
        String openid = jsonObject.getString("openid");

        // openId判断是否是第一次使用微信登录
        User user = userService.getUserByOpenId(openid);
        if(user == null) {
            user = new User();
            user.setOpenId(openid);
            user.setNickName(openid);
            user.setPhotoUrl("");
            user.setUserType(UserType.USER);
            user.setIsNew(0);
            userService.save(user);
        }

        //根据userId查询提货点和团长信息
        LeaderAddressVo leaderAddressVo =
                userService.getLeaderAddressByUserId(user.getId());

        String token = JwtHelper.createToken(user.getId(), user.getNickName());

        UserLoginVo userLoginVo = userService.getUserLoginVo(user.getId());
        redisTemplate.opsForValue()
                .set(RedisConst.USER_LOGIN_KEY_PREFIX+user.getId(),
                        userLoginVo,
                        RedisConst.USERKEY_TIMEOUT,
                        TimeUnit.DAYS);

        Map<String,Object> map = new HashMap<>();
        map.put("user",user);
        map.put("token",token);
        map.put("leaderAddressVo",leaderAddressVo);
        return Result.ok(map);
    }

    @PostMapping("/auth/updateUser")
    @ApiOperation(value = "更新用户昵称与头像")
    public Result updateUser(@RequestBody User user) {
        //获取当前登录用户id
        User user1 = userService.getById(AuthContextHolder.getUserId());
        //把昵称更新为微信用户
        user1.setNickName(user.getNickName().replaceAll("[ue000-uefff]", "*"));
        user1.setPhotoUrl(user.getPhotoUrl());
        userService.updateById(user1);
        return Result.ok(null);
    }
}
