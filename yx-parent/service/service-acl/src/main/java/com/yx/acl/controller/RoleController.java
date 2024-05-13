package com.yx.acl.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yx.acl.service.IRoleService;
import com.yx.common.result.Result;
import com.yx.model.acl.Role;
import com.yx.vo.acl.RoleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色控制器
 *
 * @author 97557
 * @date 2023/07/13
 */
@RestController
@RequestMapping("/admin/acl/role")
@Api(tags = "角色")
@CrossOrigin
public class RoleController {
    @Autowired
    private IRoleService roleService;

    /**
     * 页面列表
     *
     * @param current     当前
     * @param limit       限制
     * @param roleQueryVo Role Query vo
     * @return {@link Result}
     */
    @ApiOperation("角色条件分页查询")
    @GetMapping("{current}/{limit}")
    public Result pageList(@PathVariable Long current,
                           @PathVariable Long limit,
                           RoleQueryVo roleQueryVo) {
        Page<Role> pageParam = new Page<>(current, limit);
        IPage<Role> pageModel = roleService.selectRolePage(pageParam, roleQueryVo);
        return Result.ok(pageModel);
    }

    /**
     * 获取
     *
     * @param id 同上
     * @return {@link Result}
     */
    @ApiOperation("根据id查询角色")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        Role role = roleService.getById(id);
        return Result.ok(role);
    }

    /**
     *
     *
     * @param role 角色
     * @return {@link Result}
     */
    @ApiOperation("添加角色")
    @PostMapping("save")
    public Result save(@RequestBody Role role) {
        boolean is_success = roleService.save(role);
        if (is_success) {
            return Result.ok(null);
        } else {
            return Result.fail(null);
        }
    }

    /**
     * 更新
     *
     * @param role 角色
     * @return {@link Result}
     */
    @ApiOperation("修改角色")
    @PutMapping("update")
    public Result update(@RequestBody Role role) {
        roleService.updateById(role);
        return Result.ok(null);
    }

    /**
     * 删除
     *
     * @param id 同上
     * @return {@link Result}
     */
    @ApiOperation("根据id删除角色")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        roleService.removeById(id);
        return Result.ok(null);
    }

    /**
     * 批量删除
     *
     * @param idList ID 列表
     * @return {@link Result}
     */
    @ApiOperation("批量删除角色")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        roleService.removeByIds(idList);
        return Result.ok(null);
    }

}
