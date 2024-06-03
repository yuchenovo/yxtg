package com.yx.search.controller;


import com.yx.common.result.Result;
import com.yx.model.search.SkuEs;
import com.yx.search.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search/sku")
public class SkuApiController {

    @Autowired
    private SkuService skuService;

    //查询分类商品
//    @GetMapping("{page}/{limit}")
//    public Result listSku(@PathVariable Integer page,
//                          @PathVariable Integer limit,
//                          SkuEsQueryVo skuEsQueryVo) {
//        //创建pageable对象，0代表第一页
//        Pageable pageable = PageRequest.of(page-1,limit);
//        Page<SkuEs> pageModel = skuService.search(pageable,skuEsQueryVo);
//        return Result.ok(pageModel);
//    }

    /**
     * 上架
     *
     * @param skuId 编号SKU ID
     * @return {@link Result}
     */
    @GetMapping("inner/upperSku/{skuId}")
    public Result upperSku(@PathVariable Long skuId) {
        skuService.upperSku(skuId);
        return Result.ok(null);
    }

    /**
     * 下架
     *
     * @param skuId 编号SKU ID
     * @return {@link Result}
     */
    @GetMapping("inner/lowerSku/{skuId}")
    public Result lowerSku(@PathVariable Long skuId) {
        skuService.lowerSku(skuId);
        return Result.ok(null);
    }

    //获取爆款商品
    @GetMapping("inner/findHotSkuList")
    public List<SkuEs> findHotSkuList() {
        return skuService.findHotSkuList();
    }

    //更新商品热度
    @GetMapping("inner/incrHotScore/{skuId}")
    public Boolean incrHotScore(@PathVariable("skuId") Long skuId) {
        skuService.incrHotScore(skuId);
        return true;
    }
}
