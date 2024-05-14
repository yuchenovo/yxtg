package com.yx.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yx.model.sys.Region;

import java.util.List;

/**
 * <p>
 * 地区表 服务类
 * </p>
 *
 * @author admin
 * @since 2024-05-13
 */
public interface RegionService extends IService<Region> {

    List<Region> getRegionByKeyword(String keyword);
}
