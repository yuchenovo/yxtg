package com.yx.acl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yx.acl.mapper.PermissionMapper;
import com.yx.acl.service.PermissionService;
import com.yx.acl.service.RolePermissionService;
import com.yx.acl.utils.PermissionHelper;
import com.yx.model.acl.Permission;
import com.yx.model.acl.RolePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 97557
 * @description 针对表【permission(权限)】的数据库操作Service实现
 * @createDate 2023-07-13 15:58:31
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission>
        implements PermissionService {

    @Autowired
    private RolePermissionService rolePermissionService;
    @Override
    public List<Permission> queryAllMenu() {
        List<Permission> list = baseMapper.selectList(null);
        return PermissionHelper.buildPermission(list);
    }

    @Override
    public boolean removeChildById(Long id) {
        List<Long> idList = new ArrayList<>();
        getIdList(id,idList);
        idList.add(id);
        return baseMapper.deleteBatchIds(idList) > 0;
    }

    @Override
    public Map<String, Object> getPermissionByRoleId(Long roleId) {
        Map<String, Object> map = new HashMap<>(16);
        List<Permission> allPermissionList = baseMapper.selectList(null);
        List<Permission> buildPermission = PermissionHelper.buildPermission(allPermissionList);
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId,roleId);
        List<Long> permissionList = rolePermissionService.list(wrapper).stream().map(RolePermission::getPermissionId).collect(Collectors.toList());
        for (Permission permission : buildPermission) {
            if (permission.getPid() == 0){
                if (permissionList.contains(permission.getId())){
                    permission.setSelect(true);
                }
                setChildrenPermission(permission.getChildren(),permissionList);
            }
        }
        map.put("allPermissionList",buildPermission);
        return map;
    }

    private void setChildrenPermission(List<Permission> children, List<Long> permissionList) {
        for (Permission child : children) {
            if (permissionList.contains(child.getId())){
                child.setSelect(true);
            }
            setChildrenPermission(child.getChildren(),permissionList);
        }
    }

    @Override
    public void saveRolePermission(Long roleId, Long[] permissionId) {
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId,roleId);
        rolePermissionService.remove(wrapper);
        List<RolePermission> list = new ArrayList<>();
        for (Long pId : permissionId) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setPermissionId(pId);
            rolePermission.setRoleId(roleId);
            list.add(rolePermission);
        }
        rolePermissionService.saveBatch(list);
    }

    private void getIdList(Long id,List<Long> idList){
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getPid,id);
        baseMapper.selectList(wrapper).forEach(item ->{
            idList.add(item.getId());
            getIdList(item.getId(),idList);
        });

    }
}




