package com.yx.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yx.activity.mapper.ActivityInfoMapper;
import com.yx.activity.service.ActivityInfoService;
import com.yx.activity.service.ActivityRuleService;
import com.yx.activity.service.ActivitySkuService;
import com.yx.client.product.ProductFeignClient;
import com.yx.model.activity.ActivityInfo;
import com.yx.model.activity.ActivityRule;
import com.yx.model.activity.ActivitySku;
import com.yx.model.product.SkuInfo;
import com.yx.vo.activity.ActivityRuleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
}
