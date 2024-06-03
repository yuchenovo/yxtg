package com.yx.product.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yx.common.result.Result;
import com.yx.model.product.Category;
import com.yx.product.service.CategoryService;
import com.yx.vo.product.CategoryQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 商品三级分类 前端控制器
 * </p>
 *
 * @author xyh
 * @since 2024-05-15
 */
@RestController
@RequestMapping("/admin/product/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 商品分类列表
     *
     * @param page            页
     * @param limit           限制
     * @param categoryQueryVo 类别查询 VO
     * @return {@link Result}
     */
    @ApiOperation("商品分类列表")
    @GetMapping("{page}/{limit}")
    public Result list(@PathVariable Long page,
                       @PathVariable Long limit,
                       CategoryQueryVo categoryQueryVo) {
        Page<Category> pageParam = new Page<>(page,limit);
        IPage<Category> pageModel = categoryService.selectPageCategory(pageParam,categoryQueryVo);
        return Result.ok(pageModel);
    }

    @ApiOperation(value = "获取商品分类信息")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        Category category = categoryService.getById(id);
        return Result.ok(category);
    }

    @ApiOperation(value = "新增商品分类")
    @PostMapping("save")
    public Result save(@RequestBody Category category) {
        categoryService.save(category);
        return Result.ok(null);
    }

    @ApiOperation(value = "修改商品分类")
    @PutMapping("update")
    public Result updateById(@RequestBody Category category) {
        categoryService.updateById(category);
        return Result.ok(null);
    }

    @ApiOperation(value = "删除商品分类")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        categoryService.removeById(id);
        return Result.ok(null);
    }

    @ApiOperation(value = "根据id列表删除商品分类")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        categoryService.removeByIds(idList);
        return Result.ok(null);
    }

    /**
     * 查询所有商品分类
     *
     * @return {@link Result}
     */
    @ApiOperation("查询所有商品分类")
    @GetMapping("findAllList")
    public Result findAllList() {
        List<Category> list = categoryService.list();
        return Result.ok(list);
    }
}

