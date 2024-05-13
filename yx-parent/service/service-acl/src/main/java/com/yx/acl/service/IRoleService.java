package com.yx.acl.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yx.model.acl.Role;
import com.yx.vo.acl.RoleQueryVo;

import java.util.Map;


public interface IRoleService extends IService<Role> {

    /**
     * 角色分页
     *
     * @param pageParam   页面参数
     * @param roleQueryVo Role Query vo
     * @return {@link IPage}<{@link Role}>
     */
    IPage<Role> selectRolePage(Page<Role> pageParam, RoleQueryVo roleQueryVo);

    /**
     * 通过管理员 ID 获取角色
     *
     * @param adminId 管理员 ID
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    Map<String, Object> getRoleByAdminId(Long adminId);

    /**
     * 保存管理员角色
     *
     * @param adminId 管理员 ID
     * @param roleId  角色 ID
     */
    void saveAdminRole(Long adminId, Long[] roleId);
}
