package com.yx.acl.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yx.model.acl.Admin;
import com.yx.vo.acl.AdminQueryVo;

/**
 * @author 97557
 * @description 针对表【admin(用户表)】的数据库操作Service
 * @createDate 2023-07-14 09:45:54
 */
public interface AdminService extends IService<Admin> {

    IPage<Admin> selectPageUser(Page<Admin> pageParam, AdminQueryVo adminQueryVo);
}
