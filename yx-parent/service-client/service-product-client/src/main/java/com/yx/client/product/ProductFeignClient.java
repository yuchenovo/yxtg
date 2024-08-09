package com.yx.client.product;


import com.yx.model.product.Category;
import com.yx.model.product.SkuInfo;
import com.yx.vo.product.SkuInfoVo;
import com.yx.vo.product.SkuStockLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "service-product")
public interface ProductFeignClient {

    @GetMapping("/api/product/inner/getCategory/{categoryId}")
    public Category getCategory(@PathVariable("categoryId") Long categoryId);

    @GetMapping("/api/product/inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId);

    /**
     * 批量获取sku信息
     * @param skuIdList
     * @return
     */
    @PostMapping("/api/product/inner/findSkuInfoList")
    List<SkuInfo> findSkuInfoList(@RequestBody List<Long> skuIdList);

    /**
     * 根据关键字获取sku列表，活动使用
     * @param keyword
     * @return
     */
    @GetMapping("/api/product/inner/findSkuInfoByKeyword/{keyword}")
    List<SkuInfo> findSkuInfoByKeyword(@PathVariable("keyword") String keyword);

    @PostMapping("/api/product/inner/findCategoryList")
    public List<Category> findCategoryList(@RequestBody List<Long> categoryIdList);

    @GetMapping("/api/product/inner/findAllCategoryList")
    public List<Category> findAllCategoryList();

    @GetMapping("/api/product/inner/findNewPersonSkuInfoList")
    public List<SkuInfo> findNewPersonSkuInfoList();

    @GetMapping("/api/product/inner/getSkuInfoVo/{skuId}")
    public SkuInfoVo getSkuInfoVo(@PathVariable Long skuId);

    @PostMapping("/api/product/inner/checkAndLock/{orderNo}")
    Boolean checkAndLock(List<SkuStockLockVo> commonStockLockVoList, String orderNo);
}
