package com.yx.acl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yx.acl.mapper.AdminMapper;
import com.yx.acl.service.AdminService;
import com.yx.model.acl.Admin;
import com.yx.vo.acl.AdminQueryVo;
import jodd.util.StringUtil;
import org.springframework.stereotype.Service;

/**
 * @author 97557
 * @description 针对表【admin(用户表)】的数据库操作Service实现
 * @createDate 2023-07-14 09:45:54
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin>
        implements AdminService {

    @Override
    public IPage<Admin> selectPageUser(Page<Admin> pageParam, AdminQueryVo adminQueryVo) {
        String name = adminQueryVo.getName();
        String username = adminQueryVo.getUsername();
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtil.isNotBlank(name),Admin::getName,name)
                .like(StringUtil.isNotBlank(username),Admin::getUsername,username);
        return baseMapper.selectPage(pageParam,wrapper);
    }
}




