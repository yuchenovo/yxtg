package com.yx.activity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yx.model.activity.CouponInfo;
import com.yx.model.order.CartInfo;
import com.yx.vo.activity.CouponRuleVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 优惠券信息 服务类
 * </p>
 *
 * @author xyh
 * @since 2024-05-28
 */
public interface CouponInfoService extends IService<CouponInfo> {

    IPage<CouponInfo> selectPageCouponInfo(Long page, Long limit);

    CouponInfo getCouponInfo(Long id);

    Map<String, Object> findCouponRuleList(Long id);

    void saveCouponRule(CouponRuleVo couponRuleVo);

    CouponInfo findRangeSkuIdList(List<CartInfo> cartInfoList, Long couponId);

    void updateCouponInfoUseStatus(Long couponId, Long userId, Long orderId);

    List<CouponInfo> findCouponInfoList(Long skuId, Long userId);

    List<CouponInfo> findCartCouponInfo(List<CartInfo> cartInfoList, Long userId);
}
