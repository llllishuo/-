package com.lishuo.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lishuo.reggie.common.CustomException;
import com.lishuo.reggie.entity.Category;
import com.lishuo.reggie.entity.Dish;
import com.lishuo.reggie.entity.Setmeal;
import com.lishuo.reggie.mapper.CategoryMapper;
import com.lishuo.reggie.service.CategoryService;
import com.lishuo.reggie.service.DishService;
import com.lishuo.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除
     * 先判断是否关联数据
     * @param id
     */
    @Override
    public void remove(Long id){

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper =new LambdaQueryWrapper<>();

        //添加查询条件

        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);

        //查询当前分类是否关联菜品
        int count = dishService.count(dishLambdaQueryWrapper);

        log.info(String.valueOf(count));
        if(count>0){
            //抛出异常
            throw new CustomException("当前信息存在关联菜品，无法删除!");

        }



        LambdaQueryWrapper<Setmeal> setmealServiceLambdaQueryWrapper =new LambdaQueryWrapper<>();

        //添加查询条件

        setmealServiceLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);

        //查询当前分类是否关联套餐
        int count1 = setmealService.count(setmealServiceLambdaQueryWrapper);

        log.info(String.valueOf(count1));
        if(count1>0){
            //抛出异常
            throw new CustomException("当前信息存在关联套餐，无法删除!");
        }



        //正常删除
        super.removeById(id);

    }





}
