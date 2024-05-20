package com.yx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yx.model.product.Attr;

import java.util.List;

/**
 * <p>
 * 商品属性 服务类
 * </p>
 *
 * @author xyh
 * @since 2024-05-15
 */
public interface AttrService extends IService<Attr> {

    List<Attr> getAttrListByGroupId(Long groupId);
}
