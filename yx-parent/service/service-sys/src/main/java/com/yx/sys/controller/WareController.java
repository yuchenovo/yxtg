package com.yx.sys.controller;


import com.yx.common.result.Result;
import com.yx.model.sys.Ware;
import com.yx.sys.service.WareService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 仓库表 前端控制器
 * </p>
 *
 * @author admin
 * @since 2024-05-13
 */
@RestController
@RequestMapping("/admin/sys/ware")
public class WareController {
    @Autowired
    private WareService wareService;

    /**
     * 查询所有仓库列表
     *
     * @return {@link Result}
     */
    @ApiOperation("查询所有仓库列表")
    @GetMapping("findAllList")
    public Result findAllList() {
        List<Ware> list = wareService.list();
        return Result.ok(list);
    }
}

