package com.yx.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yx.model.product.SkuAttrValue;
import com.yx.product.mapper.SkuAttrValueMapper;
import com.yx.product.service.SkuAttrValueService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * spu属性值 服务实现类
 * </p>
 *
 * @author xyh
 * @since 2024-05-15
 */
@Service
public class SkuAttrValueServiceImpl extends ServiceImpl<SkuAttrValueMapper, SkuAttrValue> implements SkuAttrValueService {

    @Override
    public List<SkuAttrValue> getSkuAttrValueList(Long id) {
        LambdaQueryWrapper<SkuAttrValue> valueWrapper = new LambdaQueryWrapper<>();
        valueWrapper.eq(SkuAttrValue::getSkuId,id);
        return list(valueWrapper);
    }
}
