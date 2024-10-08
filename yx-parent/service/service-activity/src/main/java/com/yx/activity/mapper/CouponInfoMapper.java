package com.yx.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yx.model.activity.CouponInfo;

import java.util.List;

/**
 * <p>
 * 优惠券信息 Mapper 接口
 * </p>
 *
 * @author xyh
 * @since 2024-05-28
 */
public interface CouponInfoMapper extends BaseMapper<CouponInfo> {

    List<CouponInfo> selectCouponInfoList(Long skuId, Long categoryId, Long userId);
}
