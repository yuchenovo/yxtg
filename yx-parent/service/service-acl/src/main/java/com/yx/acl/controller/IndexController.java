package com.yx.acl.controller;

import com.yx.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;


/**
 * 登录控制器
 *
 * @author 97557
 * @date 2023/07/13
 */
@Api(tags = "登录")
@RestController
@RequestMapping("/admin/acl/index")
public class IndexController {

    /**
     * 登录
     *
     * @return {@link Result}<{@link Void}>
     */
    @PostMapping("/login")
    @ApiOperation("登录")
    public Result login() {
        HashMap<String, String> map = new HashMap<>(16);
        map.put("token", "admin-token");
        return Result.ok(map);
    }

    /**
     * 得到信息
     *
     * @return {@link Result}<{@link Void}>
     */
    @GetMapping("/info")
    @ApiOperation("获取信息")
    public Result getInfo() {
        HashMap<String, String> map = new HashMap<>(16);
        map.put("name", "admin");
        map.put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        return Result.ok(map);
    }

    /**
     * 注销
     *
     * @return {@link Result}<{@link Void}>
     */
    @PostMapping("/logout")
    @ApiOperation("登出")
    public Result<Void> logout() {

        return Result.ok(null);
    }
}
