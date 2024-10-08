package com.yx.search.service.impl;


import com.yx.client.activity.ActivityFeignClient;
import com.yx.client.product.ProductFeignClient;
import com.yx.common.auth.AuthContextHolder;
import com.yx.enums.SkuType;
import com.yx.model.product.Category;
import com.yx.model.product.SkuInfo;
import com.yx.model.search.SkuEs;
import com.yx.search.repository.SkuRepository;
import com.yx.search.service.SkuService;
import com.yx.vo.search.SkuEsQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SkuRepository skuRepository;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private ActivityFeignClient activityFeignClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @Override
    public void incrHotScore(Long skuId) {
        String key = "hotScore";
        //redis保存数据，每次+1
        Double hotScore = redisTemplate.opsForZSet().incrementScore(key, "skuId:" + skuId, 1);
        //规则
        if(hotScore%10==0) {
            //更新es
            Optional<SkuEs> optional = skuRepository.findById(skuId);
            SkuEs skuEs = optional.get();
            skuEs.setHotScore(Math.round(hotScore));
            skuRepository.save(skuEs);
        }
    }

    /**
     * 获取爆款商品
     *
     * @return {@link List}<{@link SkuEs}>
     */
    @Override
    public List<SkuEs> findHotSkuList() {
        Pageable pageable = PageRequest.of(0,10);
        Page<SkuEs> pageModel = skuRepository.findByOrderByHotScoreDesc(pageable);
        List<SkuEs> skuEsList = pageModel.getContent();
        return skuEsList;
    }

    //查询分类商品
    @Override
    public Page<SkuEs> search(Pageable pageable, SkuEsQueryVo skuEsQueryVo) {
        //1 向SkuEsQueryVo设置wareId，当前登录用户的仓库id
        skuEsQueryVo.setWareId(AuthContextHolder.getWareId());

        Page<SkuEs> pageModel = null;
        //2 调用SkuRepository方法，根据springData命名规则定义方法，进行条件查询
        //// 判断keyword是否为空，如果为空 ，根据仓库id + 分类id查询
        String keyword = skuEsQueryVo.getKeyword();
        if(StringUtils.isEmpty(keyword)) {
            pageModel =
                    skuRepository
                            .findByCategoryIdAndWareId(
                                    skuEsQueryVo.getCategoryId(),
                                    skuEsQueryVo.getWareId(),
                                    pageable);
        } else {
            ///如果keyword不为空根据仓库id + keyword进行查询
            pageModel = skuRepository
                    .findByKeywordAndWareId(
                            skuEsQueryVo.getKeyword(),
                            skuEsQueryVo.getWareId(),
                            pageable);
        }

        //3 查询商品参加优惠活动
        List<SkuEs> skuEsList = pageModel.getContent();

        if(!CollectionUtils.isEmpty(skuEsList)) {
            //遍历skuEsList，得到所有skuId
            List<Long> skuIdList =
                    skuEsList.stream()
                            .map(item -> item.getId())
                            .collect(Collectors.toList());
            //根据skuId列表远程调用，调用service-activity里面的接口得到数据
            Map<Long,List<String>> skuIdToRuleListMap =
                    activityFeignClient.findActivity(skuIdList);//远程调用
            //封装获取数据到skuEs里面 ruleList属性里面
            if(skuIdToRuleListMap != null) {
                skuEsList.forEach(skuEs -> {
                    skuEs.setRuleList(skuIdToRuleListMap.get(skuEs.getId()));
                });
            }
        }
        return pageModel;
    }


    @Override
    public void upperSku(Long skuId) {
        //1 通过远程调用 ，根据skuid获取相关信息
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        if(skuInfo == null) {
            return;
        }
        Category category = productFeignClient.getCategory(skuInfo.getCategoryId());
        //2 获取数据封装SkuEs对象
        SkuEs skuEs = new SkuEs();
        //封装分类
        if(category != null) {
            skuEs.setCategoryId(category.getId());
            skuEs.setCategoryName(category.getName());
        }
        //封装sku信息部分
        skuEs.setId(skuInfo.getId());
        skuEs.setKeyword(skuInfo.getSkuName()+","+skuEs.getCategoryName());
        skuEs.setWareId(skuInfo.getWareId());
        skuEs.setIsNewPerson(skuInfo.getIsNewPerson());
        skuEs.setImgUrl(skuInfo.getImgUrl());
        skuEs.setTitle(skuInfo.getSkuName());
        if(skuInfo.getSkuType().equals(SkuType.COMMON.getCode())) {
            skuEs.setSkuType(0);
            skuEs.setPrice(skuInfo.getPrice().doubleValue());
            skuEs.setStock(skuInfo.getStock());
            skuEs.setSale(skuInfo.getSale());
            skuEs.setPerLimit(skuInfo.getPerLimit());
        }
        //3 调用方法添加ES
        skuRepository.save(skuEs);
    }

    @Override
    public void lowerSku(Long skuId) {
        skuRepository.deleteById(skuId);
    }
}
