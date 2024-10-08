package com.yx.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yx.model.activity.ActivityInfo;
import com.yx.model.activity.ActivityRule;
import com.yx.model.activity.ActivitySku;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 活动表 Mapper 接口
 * </p>
 *
 * @author xyh
 * @since 2024-05-28
 */
public interface ActivityInfoMapper extends BaseMapper<ActivityInfo> {

    List<Long> selectSkuIdListExist(@Param("skuIdList") List<Long> skuIdList);

    List<ActivityRule> findActivityRule(Long skuId);

    List<ActivitySku> selectCartActivity(List<Long> skuIdList);
}
