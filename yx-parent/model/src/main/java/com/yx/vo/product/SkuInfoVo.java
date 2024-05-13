package com.yx.vo.product;

import com.yx.model.product.SkuAttrValue;
import com.yx.model.product.SkuImage;
import com.yx.model.product.SkuInfo;
import com.yx.model.product.SkuPoster;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SkuInfoVo extends SkuInfo {

    @ApiModelProperty(value = "海报列表")
    private List<SkuPoster> skuPosterList;

    @ApiModelProperty(value = "属性值")
    private List<SkuAttrValue> skuAttrValueList;

    @ApiModelProperty(value = "图片")
    private List<SkuImage> skuImagesList;

}

