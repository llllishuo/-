package com.lishuo.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lishuo.reggie.common.R;
import com.lishuo.reggie.entity.Category;
import com.lishuo.reggie.entity.Dish;
import com.lishuo.reggie.entity.Setmeal;
import com.lishuo.reggie.service.CategoryService;
import com.lishuo.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishService dishService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
//        log.info("category:"+category);
        categoryService.save(category);
        return R.success("新增分类成功！");

    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        //分页构造器
        Page<Category> categoryPage = new Page<>(page,pageSize);

        //条件构造器对象
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort);

        //进行分页查询
        categoryService.page(categoryPage,queryWrapper);

        return R.success(categoryPage);
    }


    /**
     * 根据id删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids){


//        log.info(""+ids);

        //判断该种类中是否含有菜品
        LambdaQueryWrapper<Dish> queryWrapper =new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(Dish::getCategoryId,ids);

        int count = dishService.count(queryWrapper);
        if(count!=0){
            return R.error("该种类仍有菜品，无法删除！");
        }

        //如果没有再删除
        categoryService.remove(ids);

        return R.success("删除成功!");
    }

    /**
     * 根据id修改
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info(""+category);

        categoryService.updateById(category);
        return R.success("修改成功!");
    }

    /**
     * 获取菜品列表
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){

        //条件构造器
        LambdaQueryWrapper<Category>  queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());

        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);


        return R.success(list);


    }



}
