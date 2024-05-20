package com.yx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yx.model.product.SkuPoster;

import java.util.List;

/**
 * <p>
 * 商品海报表 服务类
 * </p>
 *
 * @author xyh
 * @since 2024-05-15
 */
public interface SkuPosterService extends IService<SkuPoster> {

    List<SkuPoster> getSkuPosterList(Long id);
}
