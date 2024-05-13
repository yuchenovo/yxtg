package com.yx.acl.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.yx.model.acl.Permission;

import java.util.List;
import java.util.Map;

/**
 * @author 97557
 * @description 针对表【permission(权限)】的数据库操作Service
 * @createDate 2023-07-13 15:58:31
 */
public interface PermissionService extends IService<Permission> {

    /**
     * “查询全部”菜单
     *
     * @return {@link List}<{@link Permission}>
     */
    List<Permission> queryAllMenu();

    /**
     * 按 ID 删除孩子
     *
     * @param id 同上
     * @return
     */
    boolean removeChildById(Long id);

    /**
     * 按角色 ID 获取权限
     *
     * @param roleId 角色 ID
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    Map<String, Object> getPermissionByRoleId(Long roleId);

    /**
     * 保存角色权限
     *
     * @param roleId       角色 ID
     * @param permissionId 权限 ID
     */
    void saveRolePermission(Long roleId, Long[] permissionId);
}
