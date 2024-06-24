package com.yx.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yx.activity.mapper.CouponInfoMapper;
import com.yx.activity.service.CouponInfoService;
import com.yx.activity.service.CouponRangeService;
import com.yx.client.product.ProductFeignClient;
import com.yx.enums.CouponRangeType;
import com.yx.model.activity.CouponInfo;
import com.yx.model.activity.CouponRange;
import com.yx.model.order.CartInfo;
import com.yx.model.product.Category;
import com.yx.model.product.SkuInfo;
import com.yx.vo.activity.CouponRuleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 优惠券信息 服务实现类
 * </p>
 *
 * @author xyh
 * @since 2024-05-28
 */
@Service
public class CouponInfoServiceImpl extends ServiceImpl<CouponInfoMapper, CouponInfo> implements CouponInfoService {

    @Autowired
    private CouponRangeService couponRangeService;
    @Autowired
    private ProductFeignClient productFeignClient;

    @Override
    public IPage<CouponInfo> selectPageCouponInfo(Long page, Long limit) {
        Page<CouponInfo> pageParam = new Page<>(page,limit);
        IPage<CouponInfo> couponInfoPage = baseMapper.selectPage(pageParam, null);
        List<CouponInfo> couponInfoList = couponInfoPage.getRecords();
        couponInfoList.forEach(item -> {
            item.setCouponTypeString(item.getCouponType().getComment());
            CouponRangeType rangeType = item.getRangeType();
            if(rangeType != null) {
                item.setRangeTypeString(rangeType.getComment());
            }
        });
        return couponInfoPage;
    }

    @Override
    public CouponInfo getCouponInfo(Long id) {
        CouponInfo couponInfo = baseMapper.selectById(id);
        couponInfo.setCouponTypeString(couponInfo.getCouponType().getComment());
        if(couponInfo.getRangeType() != null) {
            couponInfo.setRangeTypeString(couponInfo.getRangeType().getComment());
        }
        return couponInfo;
    }

    @Override
    public Map<String, Object> findCouponRuleList(Long id) {
        CouponInfo couponInfo = baseMapper.selectById(id);
        //根据优惠卷id查询coupon_range 查询里面对应range_id
        List<CouponRange> couponRangeList = couponRangeService.list(
                new LambdaQueryWrapper<CouponRange>().eq(CouponRange::getCouponId, id)
        );
        // 如果规则类型 SKU      range_id就是skuId值
        // 如果规则类型 CATEGORY range_id就是分类Id值
        List<Long> randIdList =
                couponRangeList.stream().map(CouponRange::getRangeId).collect(Collectors.toList());

        Map<String,Object> result = new HashMap<>(16);
        if(!CollectionUtils.isEmpty(randIdList)) {
            if(couponInfo.getRangeType() == CouponRangeType.SKU) {
                // 远程调用根据多个skuId值获取对应sku信息
                List<SkuInfo> skuInfoList =
                        productFeignClient.findSkuInfoList(randIdList);
                result.put("skuInfoList",skuInfoList);

            } else if(couponInfo.getRangeType() == CouponRangeType.CATEGORY) {
                //远程调用根据多个分类Id值获取对应分类信息
                List<Category> categoryList =
                        productFeignClient.findCategoryList(randIdList);
                result.put("categoryList",categoryList);
            }
        }
        return result;
    }

    @Override
    public void saveCouponRule(CouponRuleVo couponRuleVo) {
        //根据优惠卷id删除规则数据
        couponRangeService.remove(
                new LambdaQueryWrapper<CouponRange>().eq(CouponRange::getCouponId,couponRuleVo.getCouponId())
        );

        //更新优惠卷基本信息
        CouponInfo couponInfo = baseMapper.selectById(couponRuleVo.getCouponId());
        couponInfo.setRangeType(couponRuleVo.getRangeType());
        couponInfo.setConditionAmount(couponRuleVo.getConditionAmount());
        couponInfo.setAmount(couponRuleVo.getAmount());
        couponInfo.setConditionAmount(couponRuleVo.getConditionAmount());
        couponInfo.setRangeDesc(couponRuleVo.getRangeDesc());
        baseMapper.updateById(couponInfo);

        //添加优惠卷新规则数据
        List<CouponRange> couponRangeList = couponRuleVo.getCouponRangeList();
        for (CouponRange couponRange:couponRangeList) {
            couponRange.setCouponId(couponRuleVo.getCouponId());
            couponRangeService.save(couponRange);
        }
    }

    @Override
    public CouponInfo findRangeSkuIdList(List<CartInfo> cartInfoList, Long couponId) {
        return null;
    }

    @Override
    public void updateCouponInfoUseStatus(Long couponId, Long userId, Long orderId) {

    }

    @Override
    public List<CouponInfo> findCouponInfoList(Long skuId, Long userId) {
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);

        return baseMapper.selectCouponInfoList(skuInfo.getId(),
                skuInfo.getCategoryId(),userId);
    }
}
