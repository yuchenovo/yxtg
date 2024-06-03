package com.yx.activity.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yx.activity.service.CouponInfoService;
import com.yx.common.result.Result;
import com.yx.model.activity.CouponInfo;
import com.yx.vo.activity.CouponRuleVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 优惠券信息 前端控制器
 * </p>
 *
 * @author xyh
 * @since 2024-05-28
 */
@RestController
@RequestMapping("/admin/activity/couponInfo")
public class CouponInfoController {

    @Autowired
    private CouponInfoService couponInfoService;

    @GetMapping("{page}/{limit}")
    public Result list(@PathVariable Long page,
                       @PathVariable Long limit) {
        IPage<CouponInfo> pageModel =
                couponInfoService.selectPageCouponInfo(page,limit);
        return Result.ok(pageModel);
    }


    @PostMapping("save")
    public Result save(@RequestBody CouponInfo couponInfo) {
        couponInfoService.save(couponInfo);
        return Result.ok(null);
    }

    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        CouponInfo couponInfo = couponInfoService.getCouponInfo(id);
        return Result.ok(couponInfo);
    }

    @PutMapping("update")
    public Result update(@RequestBody CouponInfo couponInfo) {
        couponInfoService.updateById(couponInfo);
        return Result.ok(null);
    }

    /**
     * 删除
     *
     * @param id 同上
     * @return {@link Result}
     */
    @ApiOperation("根据id删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        couponInfoService.removeById(id);
        return Result.ok(null);
    }

    /**
     * 批量删除
     *
     * @param idList ID 列表
     * @return {@link Result}
     */
    @ApiOperation("批量删除")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        couponInfoService.removeByIds(idList);
        return Result.ok(null);
    }

    /**
     * 查找优惠券规则列表
     *
     * @param id 同上
     * @return {@link Result}
     */
    @GetMapping("findCouponRuleList/{id}")
    public Result findCouponRuleList(@PathVariable Long id) {
        Map<String,Object> map =
                couponInfoService.findCouponRuleList(id);
        return Result.ok(map);
    }

    /**
     * 保存优惠券规则
     *
     * @param couponRuleVo 优惠券规则 VO
     * @return {@link Result}
     */
    @PostMapping("saveCouponRule")
    public Result saveCouponRule(@RequestBody CouponRuleVo couponRuleVo) {
        couponInfoService.saveCouponRule(couponRuleVo);
        return Result.ok(null);
    }
}

