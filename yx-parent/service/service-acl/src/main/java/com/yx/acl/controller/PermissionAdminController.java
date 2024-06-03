package com.yx.acl.controller;

import com.yx.acl.service.PermissionService;
import com.yx.common.result.Result;
import com.yx.model.acl.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 权限管理员控制器
 *
 * @author admin
 * @date 2024/04/29
 */
@RestController
@RequestMapping("/admin/acl/permission")
@Api(tags = "菜单管理")
public class PermissionAdminController {
    @Autowired
    private PermissionService permissionService;

    @ApiOperation(value = "获取菜单")
    @GetMapping
    public Result index() {
        List<Permission> list = permissionService.queryAllMenu();
        return Result.ok(list);
    }

    @ApiOperation(value = "新增菜单")
    @PostMapping("save")
    public Result save(@RequestBody Permission permission) {
        permissionService.save(permission);
        return Result.ok(null);
    }

    @ApiOperation(value = "修改菜单")
    @PutMapping("update")
    public Result updateById(@RequestBody Permission permission) {
        permissionService.updateById(permission);
        return Result.ok(null);
    }

    @ApiOperation(value = "递归删除菜单")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id)
    {
        permissionService.removeChildById(id);
        return Result.ok(null);
    }

    /**
     * 给某个角色授权
     *
     * @param roleId
     * @param permissionId
     * @return {@link Result}
     */
    @ApiOperation("给某个角色授权")
    @PostMapping("doAssign")
    public Result doAssign(@RequestParam Long roleId,
                           @RequestParam Long[] permissionId) {
        permissionService.saveRolePermission(roleId,permissionId);
        return Result.ok(null);
    }


    /**
     * 查看某个角色的权限列表
     *
     * @param roleId
     * @return {@link Result}
     */
    @ApiOperation("查看某个角色的权限列表")
    @GetMapping("toAssign/{roleId}")
    public Result toAssign(@PathVariable Long roleId) {
        Map<String, Object> result = permissionService.getPermissionByRoleId(roleId);
        return Result.ok(result.get("allPermissionList"));
    }
}
