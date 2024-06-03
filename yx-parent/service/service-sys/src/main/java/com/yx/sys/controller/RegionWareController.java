package com.yx.sys.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yx.common.result.Result;
import com.yx.model.sys.RegionWare;
import com.yx.sys.service.RegionWareService;
import com.yx.vo.sys.RegionWareQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 城市仓库关联表 前端控制器
 * </p>
 *
 * @author admin
 * @since 2024-05-13
 */
@Api(tags = "开通区域接口")
@RestController
@RequestMapping("/admin/sys/regionWare")
public class RegionWareController {
    @Autowired
    private RegionWareService regionWareService;

    /**
     * 开通区域列表
     *
     * @param page              页
     * @param limit             限制
     * @param regionWareQueryVo region ware query vo
     * @return {@link Result}
     */
    @ApiOperation("开通区域列表")
    @GetMapping("{page}/{limit}")
    public Result list(@PathVariable Long page,
                       @PathVariable Long limit,
                       RegionWareQueryVo regionWareQueryVo) {
        Page<RegionWare> pageParam = new Page<>(page,limit);
        IPage<RegionWare> pageModel = regionWareService.selectPageRegionWare(pageParam,regionWareQueryVo);
        return Result.ok(pageModel);
    }

    /**
     * 添加开通区域
     *
     * @param regionWare 区域器皿
     * @return {@link Result}
     */
    @ApiOperation("添加开通区域")
    @PostMapping("save")
    public Result addRegionWare(@RequestBody RegionWare regionWare) {
        regionWareService.save(regionWare);
        return Result.ok(null);
    }

    /**
     * 删除开通区域
     *
     * @param id 同上
     * @return {@link Result}
     */
    @ApiOperation("删除开通区域")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        regionWareService.removeById(id);
        return Result.ok(null);
    }

    /**
     * 取消开通区域
     *
     * @param id     同上
     * @param status 地位
     * @return {@link Result}
     */
    @ApiOperation("取消开通区域")
    @PostMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable Long id,
                               @PathVariable Integer status) {
        regionWareService.updateStatus(id,status);
        return Result.ok(null);
    }
}

