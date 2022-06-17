package com.lishuo.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lishuo.reggie.dto.DishDto;
import com.lishuo.reggie.dto.SetmealDto;
import com.lishuo.reggie.entity.Setmeal;

public interface SetmealService extends IService<Setmeal> {

    /**
     * 查询套餐及相关菜品
     * @param id
     * @return
     */
    public SetmealDto getByIdWitDish(Long id);
    /**
     * 更新套餐
     * @param setmealDto
     */
    public void updateWithDish(SetmealDto setmealDto);
}
