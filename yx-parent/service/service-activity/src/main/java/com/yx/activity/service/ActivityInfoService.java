package com.yx.activity.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yx.model.activity.ActivityInfo;
import com.yx.model.activity.ActivityRule;
import com.yx.model.order.CartInfo;
import com.yx.model.product.SkuInfo;
import com.yx.vo.activity.ActivityRuleVo;
import com.yx.vo.order.CartInfoVo;
import com.yx.vo.order.OrderConfirmVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 活动表 服务类
 * </p>
 *
 * @author xyh
 * @since 2024-05-28
 */
public interface ActivityInfoService extends IService<ActivityInfo> {

    IPage<ActivityInfo> selectPage(Page<ActivityInfo> pageParam);

    Map<String, Object> findActivityRuleList(Long id);

    void saveActivityRule(ActivityRuleVo activityRuleVo);

    List<SkuInfo> findSkuInfoByKeyword(String keyword);

    Map<Long, List<String>> findActivity(List<Long> skuIdList);

    OrderConfirmVo findCartActivityAndCoupon(List<CartInfo> cartInfoList, Long userId);

    Map<String, Object> findActivityAndCoupon(Long skuId, Long userId);

    List<CartInfoVo> findCartActivityList(List<CartInfo> cartInfoList);
    List<ActivityRule> findActivityRuleBySkuId(Long skuId);
}
