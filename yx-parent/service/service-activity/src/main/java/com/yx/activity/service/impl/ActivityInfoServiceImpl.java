package com.yx.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yx.activity.mapper.ActivityInfoMapper;
import com.yx.activity.service.ActivityInfoService;
import com.yx.activity.service.ActivityRuleService;
import com.yx.activity.service.ActivitySkuService;
import com.yx.activity.service.CouponInfoService;
import com.yx.client.product.ProductFeignClient;
import com.yx.enums.ActivityType;
import com.yx.model.activity.ActivityInfo;
import com.yx.model.activity.ActivityRule;
import com.yx.model.activity.ActivitySku;
import com.yx.model.activity.CouponInfo;
import com.yx.model.order.CartInfo;
import com.yx.model.product.SkuInfo;
import com.yx.vo.activity.ActivityRuleVo;
import com.yx.vo.order.CartInfoVo;
import com.yx.vo.order.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 活动表 服务实现类
 * </p>
 *
 * @author xyh
 * @since 2024-05-28
 */
@Service
public class ActivityInfoServiceImpl extends ServiceImpl<ActivityInfoMapper, ActivityInfo> implements ActivityInfoService {

    @Autowired
    private ActivityRuleService activityRuleService;
    @Autowired
    private ActivitySkuService activitySkuService;
    @Autowired
    private CouponInfoService couponInfoService;
    @Autowired
    private ProductFeignClient productFeignClient;

    @Override
    public IPage<ActivityInfo> selectPage(Page<ActivityInfo> pageParam) {
        IPage<ActivityInfo> activityInfoPage = baseMapper.selectPage(pageParam, null);
        activityInfoPage.getRecords().forEach(item-> item.setActivityTypeString(item.getActivityType().getComment()));
        return activityInfoPage;
    }

    @Override
    public Map<String, Object> findActivityRuleList(Long id) {
        Map<String, Object> result = new HashMap<>(16);
        //根据活动id查询，查询规则列表 activity_rule表
        LambdaQueryWrapper<ActivityRule> wrapperActivityRule = new LambdaQueryWrapper<>();
        wrapperActivityRule.eq(ActivityRule::getActivityId,id);
        List<ActivityRule> activityRuleList = activityRuleService.list(wrapperActivityRule);
        result.put("activityRuleList",activityRuleList);

        //根据活动id查询，查询使用规则商品skuid列表 activity_sku表
        List<ActivitySku> activitySkuList = activitySkuService.list(
                new LambdaQueryWrapper<ActivitySku>().eq(ActivitySku::getActivityId, id)
        );
        //获取所有skuId
        List<Long> skuIdList =
                activitySkuList.stream().map(ActivitySku::getSkuId).collect(Collectors.toList());
        //2.1 通过远程调用 service-product模块接口，根据 skuid列表 得到商品信息
        List<SkuInfo> skuInfoList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(skuIdList)) {
            skuInfoList = productFeignClient.findSkuInfoList(skuIdList);
        }
        result.put("skuInfoList",skuInfoList);

        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveActivityRule(ActivityRuleVo activityRuleVo) {
        //ActivityRule数据删除
        Long activityId = activityRuleVo.getActivityId();
        activityRuleService.remove(
                new LambdaQueryWrapper<ActivityRule>().eq(ActivityRule::getActivityId,activityId)
        );
        //ActivitySku数据删除
        activitySkuService.remove(
                new LambdaQueryWrapper<ActivitySku>().eq(ActivitySku::getActivityId,activityId)
        );

        //获取规则列表数据
        List<ActivityRule> activityRuleList = activityRuleVo.getActivityRuleList();
        ActivityInfo activityInfo = baseMapper.selectById(activityId);
        for (ActivityRule activityRule:activityRuleList) {
            activityRule.setActivityId(activityId);
            activityRule.setActivityType(activityInfo.getActivityType());
            activityRuleService.save(activityRule);
        }

        //获取规则范围数据
        List<ActivitySku> activitySkuList = activityRuleVo.getActivitySkuList();
        for (ActivitySku activitySku:activitySkuList) {
            activitySku.setActivityId(activityId);
            activitySkuService.save(activitySku);
        }
    }

    @Override
    public List<SkuInfo> findSkuInfoByKeyword(String keyword) {
        //根据关键字查询sku匹配内容列表
        List<SkuInfo> skuInfoList =
                productFeignClient.findSkuInfoByKeyword(keyword);
        if(skuInfoList.size()==0) {
            return skuInfoList;
        }
        List<Long> skuIdList =
                skuInfoList.stream().map(SkuInfo::getId).collect(Collectors.toList());

        //判断添加商品之前是否参加过活动，排除已经参加活动商品
        List<Long> existSkuIdList = baseMapper.selectSkuIdListExist(skuIdList);
        List<SkuInfo> findSkuList = new ArrayList<>();
        for (SkuInfo skuInfo:skuInfoList) {
            if(!existSkuIdList.contains(skuInfo.getId())) {
                findSkuList.add(skuInfo);
            }
        }
        return findSkuList;
    }

    @Override
    public Map<Long, List<String>> findActivity(List<Long> skuIdList) {
        Map<Long, List<String>> result = new HashMap<>(16);
        //skuIdList遍历，得到每个skuId
        skuIdList.forEach(skuId -> {
            //根据skuId进行查询，查询sku对应活动里面规则列表
            List<ActivityRule> activityRuleList =
                    baseMapper.findActivityRule(skuId);
            //数据封装，规则名称
            if(!CollectionUtils.isEmpty(activityRuleList)) {
                List<String> ruleList = new ArrayList<>();
                //把规则名称处理
                for (ActivityRule activityRule:activityRuleList) {
                    ruleList.add(this.getRuleDesc(activityRule));
                }
                result.put(skuId,ruleList);
            }
        });
        return result;
    }

    @Override
    public OrderConfirmVo findCartActivityAndCoupon(List<CartInfo> cartInfoList, Long userId) {
        //1 获取购物车，每个购物项参与活动，根据活动规则分组，
        //一个规则对应多个商品
        List<CartInfoVo> cartInfoVoList = this.findCartActivityList(cartInfoList);

        //2 计算参与活动之后金额
        BigDecimal activityReduceAmount = cartInfoVoList.stream()
                .filter(cartInfoVo -> cartInfoVo.getActivityRule() != null)
                .map(cartInfoVo -> cartInfoVo.getActivityRule().getReduceAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //3 获取购物车可以使用优惠卷列表
        List<CouponInfo> couponInfoList =
                couponInfoService.findCartCouponInfo(cartInfoList,userId);

        //4 计算商品使用优惠卷之后金额，一次只能使用一张优惠卷
        BigDecimal couponReduceAmount = new BigDecimal(0);
        if(!CollectionUtils.isEmpty(couponInfoList)) {
            couponReduceAmount = couponInfoList.stream()
                    .filter(couponInfo -> couponInfo.getIsOptimal() == 1)
                    .map(CouponInfo::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        //5 计算没有参与活动，没有使用优惠卷原始金额
        BigDecimal originalTotalAmount = cartInfoList.stream()
                .filter(cartInfo -> cartInfo.getIsChecked() == 1)
                .map(cartInfo -> cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //6 最终金额
        BigDecimal totalAmount =
                originalTotalAmount.subtract(activityReduceAmount).subtract(couponReduceAmount);

        //7 封装需要数据到OrderConfirmVo,返回
        OrderConfirmVo orderTradeVo = new OrderConfirmVo();
        orderTradeVo.setCarInfoVoList(cartInfoVoList);
        orderTradeVo.setActivityReduceAmount(activityReduceAmount);
        orderTradeVo.setCouponInfoList(couponInfoList);
        orderTradeVo.setCouponReduceAmount(couponReduceAmount);
        orderTradeVo.setOriginalTotalAmount(originalTotalAmount);
        orderTradeVo.setTotalAmount(totalAmount);
        return orderTradeVo;
    }

    @Override
    public Map<String, Object> findActivityAndCoupon(Long skuId, Long userId) {
        List<ActivityRule> activityRuleList = this.findActivityRuleBySkuId(skuId);

        List<CouponInfo> couponInfoList =  couponInfoService.findCouponInfoList(skuId,userId);

        //3 封装到map集合，返回
        Map<String, Object> map = new HashMap<>();
        map.put("couponInfoList",couponInfoList);
        map.put("activityRuleList", activityRuleList);
        return map;
    }
    @Override
    public List<ActivityRule> findActivityRuleBySkuId(Long skuId) {
        List<ActivityRule> activityRuleList = baseMapper.findActivityRule(skuId);
        for (ActivityRule activityRule:activityRuleList) {
            String ruleDesc = this.getRuleDesc(activityRule);
            activityRule.setRuleDesc(ruleDesc);
        }
        return activityRuleList;
    }

    @Override
    public List<CartInfoVo> findCartActivityList(List<CartInfo> cartInfoList) {
        //创建最终返回集合
        List<CartInfoVo> cartInfoVoList = new ArrayList<>();
        //获取所有skuId
        List<Long> skuIdList = cartInfoList.stream().map(CartInfo::getSkuId).collect(Collectors.toList());
        //根据所有skuId列表获取参与活动
        List<ActivitySku> activitySkuList = baseMapper.selectCartActivity(skuIdList);
        //根据活动进行分组，每个活动里面有哪些skuId信息
        //map里面key是分组字段 活动id
        // value是每组里面sku列表数据，set集合
        Map<Long, Set<Long>> activityIdToSkuIdListMap = activitySkuList.stream()
                .collect(
                        Collectors.groupingBy(
                                ActivitySku::getActivityId,
                                Collectors.mapping(ActivitySku::getSkuId, Collectors.toSet())
                        )
                );

        //获取活动里面规则数据
        //key是活动id  value是活动里面规则列表数据
        Map<Long,List<ActivityRule>> activityIdToActivityRuleListMap
                = new HashMap<>();
        //所有活动id
        Set<Long> activityIdSet = activitySkuList.stream().map(ActivitySku::getActivityId)
                .collect(Collectors.toSet());
        if(!CollectionUtils.isEmpty(activityIdSet)) {
            //activity_rule表
            LambdaQueryWrapper<ActivityRule> wrapper = new LambdaQueryWrapper<>();
            wrapper.orderByDesc(ActivityRule::getConditionAmount,ActivityRule::getConditionNum);
            wrapper.in(ActivityRule::getActivityId,activityIdSet);
            List<ActivityRule> activityRuleList = activityRuleService.list(wrapper);

            //封装到activityIdToActivityRuleListMap里面
            //根据活动id进行分组
            activityIdToActivityRuleListMap = activityRuleList.stream().collect(
                    Collectors.groupingBy(ActivityRule::getActivityId)
            );
        }

        //有活动的购物项skuId
        Set<Long> activitySkuIdSet = new HashSet<>();
        if(!CollectionUtils.isEmpty(activityIdToSkuIdListMap)) {
            //遍历activityIdToSkuIdListMap集合
            Iterator<Map.Entry<Long, Set<Long>>> iterator = activityIdToSkuIdListMap.entrySet().iterator();
            while(iterator.hasNext()) {
                Map.Entry<Long, Set<Long>> entry = iterator.next();
                //活动id
                Long activityId = entry.getKey();
                //每个活动对应skuId列表
                Set<Long> currentActivitySkuIdSet = entry.getValue();
                //获取当前活动对应的购物项列表
                List<CartInfo> currentActivityCartInfoList = cartInfoList.stream()
                        .filter(cartInfo ->
                                currentActivitySkuIdSet.contains(cartInfo.getSkuId())).collect(Collectors.toList());
                //计数购物项总金额和总数量
                BigDecimal activityTotalAmount =
                        this.computeTotalAmount(currentActivityCartInfoList);
                int activityTotalNum = this.computeCartNum(currentActivityCartInfoList);

                //计算活动对应规则
                //根据activityId获取活动对应规则
                List<ActivityRule> currentActivityRuleList =
                        activityIdToActivityRuleListMap.get(activityId);
                ActivityType activityType = currentActivityRuleList.get(0).getActivityType();
                //判断活动类型：满减和打折
                ActivityRule activityRule = null;
                if(activityType == ActivityType.FULL_REDUCTION) {//满减"
                    activityRule = this.computeFullReduction(activityTotalAmount, currentActivityRuleList);
                } else {//满量
                    activityRule = this.computeFullDiscount(activityTotalNum, activityTotalAmount, currentActivityRuleList);
                }

                //CartInfoVo封装
                CartInfoVo cartInfoVo = new CartInfoVo();
                cartInfoVo.setActivityRule(activityRule);
                cartInfoVo.setCartInfoList(currentActivityCartInfoList);
                cartInfoVoList.add(cartInfoVo);

                //记录哪些购物项参与活动
                activitySkuIdSet.addAll(currentActivitySkuIdSet);
            }
        }

        //没有活动购物项skuId
        //获取哪些skuId没有参加活动
        skuIdList.removeAll(activitySkuIdSet);
        if(!CollectionUtils.isEmpty(skuIdList)) {
            //skuId对应购物项
            Map<Long, CartInfo> skuIdCartInfoMap = cartInfoList.stream().collect(
                    Collectors.toMap(CartInfo::getSkuId, CartInfo -> CartInfo)
            );
            for(Long skuId  : skuIdList) {
                CartInfoVo cartInfoVo = new CartInfoVo();
                cartInfoVo.setActivityRule(null);//没有活动

                List<CartInfo> cartInfos = new ArrayList<>();
                cartInfos.add(skuIdCartInfoMap.get(skuId));
                cartInfoVo.setCartInfoList(cartInfos);

                cartInfoVoList.add(cartInfoVo);
            }
        }

        return cartInfoVoList;
    }
    private ActivityRule computeFullDiscount(Integer totalNum, BigDecimal totalAmount, List<ActivityRule> activityRuleList) {
        ActivityRule optimalActivityRule = null;
        //该活动规则skuActivityRuleList数据，已经按照优惠金额从大到小排序了
        for (ActivityRule activityRule : activityRuleList) {
            //如果订单项购买个数大于等于满减件数，则优化打折
            if (totalNum >= activityRule.getConditionNum()) {
                BigDecimal skuDiscountTotalAmount = totalAmount.multiply(activityRule.getBenefitDiscount().divide(new BigDecimal("10")));
                BigDecimal reduceAmount = totalAmount.subtract(skuDiscountTotalAmount);
                activityRule.setReduceAmount(reduceAmount);
                optimalActivityRule = activityRule;
                break;
            }
        }
        if(null == optimalActivityRule) {
            //如果没有满足条件的取最小满足条件的一项
            optimalActivityRule = activityRuleList.get(activityRuleList.size()-1);
            optimalActivityRule.setReduceAmount(new BigDecimal("0"));
            optimalActivityRule.setSelectType(1);

            String ruleDesc = "满" +
                    optimalActivityRule.getConditionNum() +
                    "元打" +
                    optimalActivityRule.getBenefitDiscount() +
                    "折，还差" +
                    (totalNum - optimalActivityRule.getConditionNum()) +
                    "件";
            optimalActivityRule.setRuleDesc(ruleDesc);
        } else {
            String ruleDesc = "满" +
                    optimalActivityRule.getConditionNum() +
                    "元打" +
                    optimalActivityRule.getBenefitDiscount() +
                    "折，已减" +
                    optimalActivityRule.getReduceAmount() +
                    "元";
            optimalActivityRule.setRuleDesc(ruleDesc);
            optimalActivityRule.setSelectType(2);
        }
        return optimalActivityRule;
    }

    private ActivityRule computeFullReduction(BigDecimal totalAmount, List<ActivityRule> activityRuleList) {
        ActivityRule optimalActivityRule = null;
        //该活动规则skuActivityRuleList数据，已经按照优惠金额从大到小排序了
        for (ActivityRule activityRule : activityRuleList) {
            //如果订单项金额大于等于满减金额，则优惠金额
            if (totalAmount.compareTo(activityRule.getConditionAmount()) > -1) {
                //优惠后减少金额
                activityRule.setReduceAmount(activityRule.getBenefitAmount());
                optimalActivityRule = activityRule;
                break;
            }
        }
        if(null == optimalActivityRule) {
            //如果没有满足条件的取最小满足条件的一项
            optimalActivityRule = activityRuleList.get(activityRuleList.size()-1);
            optimalActivityRule.setReduceAmount(new BigDecimal("0"));
            optimalActivityRule.setSelectType(1);

            String ruleDesc = "满" +
                    optimalActivityRule.getConditionAmount() +
                    "元减" +
                    optimalActivityRule.getBenefitAmount() +
                    "元，还差" +
                    totalAmount.subtract(optimalActivityRule.getConditionAmount()) +
                    "元";
            optimalActivityRule.setRuleDesc(ruleDesc);
        } else {
            String ruleDesc = "满" +
                    optimalActivityRule.getConditionAmount() +
                    "元减" +
                    optimalActivityRule.getBenefitAmount() +
                    "元，已减" +
                    optimalActivityRule.getReduceAmount() +
                    "元";
            optimalActivityRule.setRuleDesc(ruleDesc);
            optimalActivityRule.setSelectType(2);
        }
        return optimalActivityRule;
    }
    private int computeCartNum(List<CartInfo> cartInfoList) {
        int total = 0;
        for (CartInfo cartInfo : cartInfoList) {
            //是否选中
            if(cartInfo.getIsChecked() == 1) {
                total += cartInfo.getSkuNum();
            }
        }
        return total;
    }

    private BigDecimal computeTotalAmount(List<CartInfo> cartInfoList) {
        BigDecimal total = new BigDecimal("0");
        for (CartInfo cartInfo : cartInfoList) {
            //是否选中
            if(cartInfo.getIsChecked() == 1) {
                BigDecimal itemTotal = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                total = total.add(itemTotal);
            }
        }
        return total;
    }

    private String getRuleDesc(ActivityRule activityRule) {
        ActivityType activityType = activityRule.getActivityType();
        StringBuilder ruleDesc = new StringBuilder();
        if (activityType == ActivityType.FULL_REDUCTION) {
            ruleDesc
                    .append("满")
                    .append(activityRule.getConditionAmount())
                    .append("元减")
                    .append(activityRule.getBenefitAmount())
                    .append("元");
        } else {
            ruleDesc
                    .append("满")
                    .append(activityRule.getConditionNum())
                    .append("元打")
                    .append(activityRule.getBenefitDiscount())
                    .append("折");
        }
        return ruleDesc.toString();
    }
}
