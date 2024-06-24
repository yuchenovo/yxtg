package com.yx.search.service;

import com.yx.model.search.SkuEs;
import com.yx.vo.search.SkuEsQueryVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    /**
     * 获取爆款商品
     *
     * @return {@link List}<{@link SkuEs}>
     */
    List<SkuEs> findHotSkuList();

    /**
     * 查询分类商品
     *
     * @param pageable     可分页
     * @param skuEsQueryVo SKU es query vo
     * @return {@link Page}<{@link SkuEs}>
     */
    Page<SkuEs> search(Pageable pageable, SkuEsQueryVo skuEsQueryVo);


    /**
     * 更新商品热度
     *
     * @param skuId 编号SKU ID
     */
    void incrHotScore(Long skuId);
}
