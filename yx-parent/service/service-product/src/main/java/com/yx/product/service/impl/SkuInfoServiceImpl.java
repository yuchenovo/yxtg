package com.yx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yx.model.product.SkuAttrValue;
import com.yx.model.product.SkuImage;
import com.yx.model.product.SkuInfo;
import com.yx.model.product.SkuPoster;
import com.yx.mq.constant.MqConst;
import com.yx.mq.service.RabbitService;
import com.yx.product.mapper.SkuInfoMapper;
import com.yx.product.service.SkuAttrValueService;
import com.yx.product.service.SkuImageService;
import com.yx.product.service.SkuInfoService;
import com.yx.product.service.SkuPosterService;
import com.yx.vo.product.SkuInfoQueryVo;
import com.yx.vo.product.SkuInfoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * sku信息 服务实现类
 * </p>
 *
 * @author xyh
 * @since 2024-05-15
 */
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo> implements SkuInfoService {

    @Autowired
    private SkuAttrValueService skuAttrValueService;
    @Autowired
    private SkuImageService skuImageService;
    @Autowired
    private SkuPosterService skuPosterService;
    @Autowired
    private RabbitService rabbitService;

    @Override
    public IPage<SkuInfo> selectPageSkuInfo(Page<SkuInfo> pageParam, SkuInfoQueryVo skuInfoQueryVo) {
        String skuType = skuInfoQueryVo.getSkuType();
        Long categoryId = skuInfoQueryVo.getCategoryId();
        String keyword = skuInfoQueryVo.getKeyword();
        LambdaQueryWrapper<SkuInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(categoryId != null,SkuInfo::getCategoryId,categoryId)
                .eq(!StringUtils.isEmpty(skuType),SkuInfo::getSkuType,skuType)
                .like(!StringUtils.isEmpty(keyword),SkuInfo::getSkuName,keyword);
        return baseMapper.selectPage(pageParam,wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSkuInfo(SkuInfoVo skuInfoVo) {
        SkuInfo skuInfo = new SkuInfo();
        BeanUtils.copyProperties(skuInfoVo,skuInfo);
        baseMapper.insert(skuInfo);
        saveSkuList(skuInfoVo, skuInfo);
    }

    @Override
    public SkuInfoVo getSkuInfo(Long id) {
        SkuInfoVo skuInfoVo = new SkuInfoVo();
        SkuInfo skuInfo = getById(id);
        BeanUtils.copyProperties(skuInfo,skuInfoVo);
        skuInfoVo.setSkuImagesList(skuImageService.getSkuImagesList(id));
        skuInfoVo.setSkuAttrValueList(skuAttrValueService.getSkuAttrValueList(id));
        skuInfoVo.setSkuPosterList(skuPosterService.getSkuPosterList(id));
        return skuInfoVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSkuInfo(SkuInfoVo skuInfoVo) {
        SkuInfo skuInfo = new SkuInfo();
        BeanUtils.copyProperties(skuInfoVo,skuInfo);
        updateById(skuInfo);
        Long id = skuInfoVo.getId();
        skuImageService.remove(new QueryWrapper<SkuImage>().eq("sku_id",id));
        skuPosterService.remove(new QueryWrapper<SkuPoster>().eq("sku_id",id));
        skuAttrValueService.remove(new QueryWrapper<SkuAttrValue>().eq("sku_id",id));
        saveSkuList(skuInfoVo, skuInfo);
    }

    @Override
    public void removeSku(Long id) {
        removeById(id);
        rabbitService.sendMessage(MqConst.EXCHANGE_GOODS_DIRECT,MqConst.ROUTING_GOODS_LOWER,id);
    }

    @Override
    public List<SkuInfo> findSkuInfoList(List<Long> skuIdList) {
        return baseMapper.selectBatchIds(skuIdList);
    }

    @Override
    public List<SkuInfo> findSkuInfoByKeyword(String keyword) {
        LambdaQueryWrapper<SkuInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(!StringUtils.isEmpty(keyword),SkuInfo::getSkuName,keyword);
        return baseMapper.selectList(wrapper);
    }

    private void saveSkuList(SkuInfoVo skuInfoVo, SkuInfo skuInfo) {
        List<SkuImage> skuImagesList = skuInfoVo.getSkuImagesList();
        if (!CollectionUtils.isEmpty(skuImagesList)){
            for (SkuImage skuImage : skuImagesList) {
                skuImage.setSkuId(skuInfo.getId());
            }
            skuImageService.saveBatch(skuImagesList);
        }
        List<SkuPoster> skuPosterList = skuInfoVo.getSkuPosterList();
        if (!CollectionUtils.isEmpty(skuPosterList)){
            for (SkuPoster skuPoster : skuPosterList) {
                skuPoster.setSkuId(skuInfo.getId());
            }
            skuPosterService.saveBatch(skuPosterList);
        }
        List<SkuAttrValue> skuAttrValueList = skuInfoVo.getSkuAttrValueList();
        if (!CollectionUtils.isEmpty(skuAttrValueList)){
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuInfo.getId());
            }
            skuAttrValueService.saveBatch(skuAttrValueList);
        }
    }

    @Override
    public void publish(Long skuId, Integer status) {
        SkuInfo skuInfo = getById(skuId);
        skuInfo.setPublishStatus(status);
        updateById(skuInfo);
        //mq发送上下架消息
        if (status == 1){
            rabbitService.sendMessage(MqConst.EXCHANGE_GOODS_DIRECT,MqConst.ROUTING_GOODS_UPPER,skuId);
        } else {
            rabbitService.sendMessage(MqConst.EXCHANGE_GOODS_DIRECT,MqConst.ROUTING_GOODS_LOWER,skuId);
        }

    }

    @Override
    public void isNewPerson(Long skuId, Integer status) {
        SkuInfo skuInfo = getById(skuId);
        skuInfo.setIsNewPerson(status);
        updateById(skuInfo);
    }

    @Override
    public void check(Long skuId, Integer status) {
        SkuInfo skuInfo = getById(skuId);
        skuInfo.setCheckStatus(status);
        updateById(skuInfo);
    }

}
