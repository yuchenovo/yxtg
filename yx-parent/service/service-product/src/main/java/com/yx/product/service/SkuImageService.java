package com.yx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yx.model.product.SkuImage;

import java.util.List;

/**
 * <p>
 * 商品图片 服务类
 * </p>
 *
 * @author xyh
 * @since 2024-05-15
 */
public interface SkuImageService extends IService<SkuImage> {

    List<SkuImage> getSkuImagesList(Long id);
}
