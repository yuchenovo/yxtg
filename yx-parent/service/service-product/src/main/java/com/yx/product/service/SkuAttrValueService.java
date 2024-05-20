package com.yx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yx.model.product.SkuAttrValue;

import java.util.List;

/**
 * <p>
 * spu属性值 服务类
 * </p>
 *
 * @author xyh
 * @since 2024-05-15
 */
public interface SkuAttrValueService extends IService<SkuAttrValue> {

    List<SkuAttrValue> getSkuAttrValueList(Long id);
}
