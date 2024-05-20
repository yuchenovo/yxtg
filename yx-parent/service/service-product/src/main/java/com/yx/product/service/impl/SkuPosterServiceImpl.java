package com.yx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yx.model.product.SkuPoster;
import com.yx.product.mapper.SkuPosterMapper;
import com.yx.product.service.SkuPosterService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 商品海报表 服务实现类
 * </p>
 *
 * @author xyh
 * @since 2024-05-15
 */
@Service
public class SkuPosterServiceImpl extends ServiceImpl<SkuPosterMapper, SkuPoster> implements SkuPosterService {

    @Override
    public List<SkuPoster> getSkuPosterList(Long id) {
        LambdaQueryWrapper<SkuPoster> posterWrapper = new LambdaQueryWrapper<>();
        posterWrapper.eq(SkuPoster::getSkuId,id);
        return list(posterWrapper);
    }
}
