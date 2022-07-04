package com.menghan.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.menghan.reggie.dto.DishDto;
import com.menghan.reggie.entity.Dish;

public interface DishService extends IService<Dish> {


    /**
     * 新增菜品，同时添加口味数据
     * @param dishDto
     */
    public void saveWithFlavor(DishDto dishDto);

    /**
     * 查询菜品及口味
     * @param id
     * @return
     */
    public DishDto getByIdWithFlavor(Long id);

    /**
     * 更新菜品及口味
     * @param dishDto
     */
    public void updateWithFlavor(DishDto dishDto);
}
