package com.yx.sys.controller;


import com.yx.common.result.Result;
import com.yx.model.sys.Region;
import com.yx.sys.service.RegionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 地区表 前端控制器
 * </p>
 *
 * @author admin
 * @since 2024-05-13
 */
@RestController
@Api
@RequestMapping("/admin/sys/region")
public class RegionController {
    @Autowired
    private RegionService regionService;

    /**
     * 根据区域关键字查询区域列表信息
     *
     * @param keyword 关键词
     * @return {@link Result}
     */
    @ApiOperation("根据区域关键字查询区域列表信息")
    @GetMapping("findRegionByKeyword/{keyword}")
    public Result findRegionByKeyword(@PathVariable("keyword") String keyword) {
        List<Region> list = regionService.getRegionByKeyword(keyword);
        return Result.ok(list);
    }
}

