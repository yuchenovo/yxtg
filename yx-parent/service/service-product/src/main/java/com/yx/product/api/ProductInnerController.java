package com.yx.product.api;

import com.yx.common.result.Result;
import com.yx.model.product.Category;
import com.yx.model.product.SkuInfo;
import com.yx.product.service.CategoryService;
import com.yx.product.service.SkuInfoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 97557
 */
@RestController
@RequestMapping("/api/product")
public class ProductInnerController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 按 SKU ID 获取类别
     *
     * @param categoryId ID
     * @return {@link Result}
     */
    @ApiOperation(value = "根据分类id获取分类信息")
    @GetMapping("inner/getCategory/{categoryId}")
    public Category getCategory(@PathVariable String categoryId) {
        return categoryService.getById(categoryId);
    }

    /**
     * 按 SKU ID 获取类别
     *
     * @param skuId 编号SKU ID
     * @return {@link Result}
     */
    @ApiOperation(value = "根据skuId获取sku信息")
    @GetMapping("inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable Long skuId) {
        return skuInfoService.getById(skuId);
    }

    /**
     * 查找 SKU 信息列表
     *
     * @param skuIdList SKU ID 列表
     * @return {@link List}<{@link SkuInfo}>
     */
    @PostMapping("inner/findSkuInfoList")
    public List<SkuInfo> findSkuInfoList(@RequestBody List<Long> skuIdList) {
        return skuInfoService.findSkuInfoList(skuIdList);
    }

    @GetMapping("inner/findSkuInfoByKeyword/{keyword}")
    public List<SkuInfo> findSkuInfoByKeyword(@PathVariable("keyword") String keyword) {
        return skuInfoService.findSkuInfoByKeyword(keyword);
    }

    /**
     * 根据分类id获取分类列表
     *
     * @param categoryIdList 类别 ID 列表
     * @return {@link List}<{@link Category}>
     */
    @PostMapping("inner/findCategoryList")
    public List<Category> findCategoryList(@RequestBody List<Long> categoryIdList) {
        return categoryService.listByIds(categoryIdList);
    }


}
