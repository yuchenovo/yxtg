package com.yx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yx.model.product.SkuImage;
import com.yx.product.mapper.SkuImageMapper;
import com.yx.product.service.SkuImageService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 商品图片 服务实现类
 * </p>
 *
 * @author xyh
 * @since 2024-05-15
 */
@Service
public class SkuImageServiceImpl extends ServiceImpl<SkuImageMapper, SkuImage> implements SkuImageService {

    @Override
    public List<SkuImage> getSkuImagesList(Long id) {
        LambdaQueryWrapper<SkuImage> imageWrapper = new LambdaQueryWrapper<>();
        imageWrapper.eq(SkuImage::getSkuId,id);
        return list(imageWrapper);
    }
}
