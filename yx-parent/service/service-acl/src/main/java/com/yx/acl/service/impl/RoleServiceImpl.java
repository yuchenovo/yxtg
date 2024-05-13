package com.yx.acl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yx.acl.mapper.RoleMapper;
import com.yx.acl.service.AdminRoleService;
import com.yx.acl.service.IRoleService;
import com.yx.model.acl.AdminRole;
import com.yx.model.acl.Role;
import com.yx.vo.acl.RoleQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 97557
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {
    @Autowired
    private AdminRoleService adminRoleService;

    @Override
    public IPage<Role> selectRolePage(Page<Role> pageParam, RoleQueryVo roleQueryVo) {
        String roleName = roleQueryVo.getRoleName();
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(roleName)) {
            wrapper.like(Role::getRoleName, roleName);
        }
        return baseMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public Map<String, Object> getRoleByAdminId(Long adminId) {
        List<Role> allRoleList = baseMapper.selectList(null);
        LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminRole::getAdminId,adminId);
        List<Long> roleList = adminRoleService.list(wrapper).stream().map(AdminRole::getRoleId).collect(Collectors.toList());
        LambdaQueryWrapper<Role> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.in(!CollectionUtils.isEmpty(roleList),Role::getId,roleList);
        List<Role> assignRoles = baseMapper.selectList(roleWrapper);
        Map<String, Object> map = new HashMap<>(16);
        map.put("allRolesList",allRoleList);
        map.put("assignRoles",assignRoles);
        return map;
    }

    @Override
    public void saveAdminRole(Long adminId, Long[] roleId) {
        LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminRole::getAdminId,adminId);
        adminRoleService.remove(wrapper);
        List<AdminRole> list = new ArrayList<>();
        for (Long rId : roleId) {
            AdminRole adminRole = new AdminRole();
            adminRole.setRoleId(rId);
            adminRole.setAdminId(adminId);
            list.add(adminRole);
        }
        adminRoleService.saveBatch(list);
    }
}
