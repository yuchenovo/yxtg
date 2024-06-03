package com.yx.acl.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yx.acl.service.AdminService;
import com.yx.acl.service.IRoleService;
import com.yx.common.result.Result;
import com.yx.common.utils.MD5;
import com.yx.model.acl.Admin;
import com.yx.vo.acl.AdminQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * admin controller
 *
 * @author admin
 * @date 2024/04/26
 */
@Api(tags = "用户接口")
@RestController
@RequestMapping("/admin/acl/user")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private IRoleService roleService;


    /**
     * 做分配
     *
     * @param adminId 管理员 ID
     * @param roleId  角色 ID
     * @return {@link Result}
     */
    @ApiOperation("为用户进行角色分配")
    @PostMapping("doAssign")
    public Result doAssign(@RequestParam Long adminId,
                           @RequestParam Long[] roleId) {
        roleService.saveAdminRole(adminId,roleId);
        return Result.ok(null);
    }


    /**
     * 分配
     *
     * @param adminId 管理员 ID
     * @return {@link Result}
     */
    @ApiOperation("获取用户角色")
    @GetMapping("toAssign/{adminId}")
    public Result toAssign(@PathVariable Long adminId) {
        //返回map集合包含两部分数据：所有角色 和 为用户分配角色列表
       Map<String,Object> map  = roleService.getRoleByAdminId(adminId);
       return Result.ok(map);
    }


    /**
     * 列表
     *
     * @param current      当前
     * @param limit        限制
     * @param adminQueryVo admin query vo
     * @return {@link Result}
     */
    @ApiOperation("用户列表")
    @GetMapping("{current}/{limit}")
    public Result list(@PathVariable Long current,
                       @PathVariable Long limit,
                       AdminQueryVo adminQueryVo) {
        Page<Admin> pageParam = new Page<>(current,limit);
        IPage<Admin> pageModel = adminService.selectPageUser(pageParam,adminQueryVo);
        return Result.ok(pageModel);
    }


    /**
     * 获取
     *
     * @param id 同上
     * @return {@link Result}
     */
    @ApiOperation("根据id查询")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        Admin admin = adminService.getById(id);
        return Result.ok(admin);
    }


    /**
     * 救
     *
     * @param admin 管理
     * @return {@link Result}
     */
    @ApiOperation("添加用户")
    @PostMapping("save")
    public Result save(@RequestBody Admin admin) {
        //获取输入的密码
        String password = admin.getPassword();

        //对输入密码进行加密 MD5
        String passwordMD5 = MD5.encrypt(password);

        //设置到admin对象里面
        admin.setPassword(passwordMD5);

        //调用方法添加
        adminService.save(admin);
        return Result.ok(null);
    }


    /**
     * 更新
     *
     * @param admin 管理
     * @return {@link Result}
     */
    @ApiOperation("修改用户")
    @PutMapping("update")
    public Result update(@RequestBody Admin admin) {
        adminService.updateById(admin);
        return Result.ok(null);
    }


    /**
     * 删除
     *
     * @param id 同上
     * @return {@link Result}
     */
    @ApiOperation("根据id删除用户")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        adminService.removeById(id);
        return Result.ok(null);
    }

    /**
     * 批量删除
     *
     * @param idList ID 列表
     * @return {@link Result}
     */
    @ApiOperation("批量删除")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        adminService.removeByIds(idList);
        return Result.ok(null);
    }

}
