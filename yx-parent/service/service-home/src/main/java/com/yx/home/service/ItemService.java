package com.yx.home.service;

import java.util.Map;

public interface ItemService {

    /**
     * 详情
     *
     * @param id     同上
     * @param userId 用户 ID
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    Map<String, Object> item(Long id, Long userId);
}
