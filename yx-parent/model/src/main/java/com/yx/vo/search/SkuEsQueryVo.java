package com.yx.vo.search;

import lombok.Data;

// 封装查询条件
@Data
public class SkuEsQueryVo {

    private Long categoryId;

    private String keyword;

    private Long wareId;

}
