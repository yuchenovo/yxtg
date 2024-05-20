package com.yx.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yx.model.product.SkuInfo;
import com.yx.vo.product.SkuInfoQueryVo;
import com.yx.vo.product.SkuInfoVo;

/**
 * <p>
 * sku信息 服务类
 * </p>
 *
 * @author xyh
 * @since 2024-05-15
 */
public interface SkuInfoService extends IService<SkuInfo> {

    IPage<SkuInfo> selectPageSkuInfo(Page<SkuInfo> pageParam, SkuInfoQueryVo skuInfoQueryVo);

    void publish(Long skuId, Integer status);

    /**
     * 是新人
     *
     * @param skuId  编号SKU ID
     * @param status 地位
     */
    void isNewPerson(Long skuId, Integer status);

    void check(Long skuId, Integer status);

    void saveSkuInfo(SkuInfoVo skuInfoVo);

    SkuInfoVo getSkuInfo(Long id);

    void updateSkuInfo(SkuInfoVo skuInfoVo);
}
