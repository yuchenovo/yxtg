package com.yx.search.service;

import com.yx.model.search.SkuEs;

import java.util.List;

public interface SkuService {

    /**
     * 上架
     *
     * @param skuId 编号SKU ID
     */
    void upperSku(Long skuId);

    /**
     * 下架
     *
     * @param skuId 编号SKU ID
     */
    void lowerSku(Long skuId);

    //获取爆款商品
    List<SkuEs> findHotSkuList();

    //查询分类商品
//    Page<SkuEs> search(Pageable pageable, SkuEsQueryVo skuEsQueryVo);

    //更新商品热度
    void incrHotScore(Long skuId);
}
