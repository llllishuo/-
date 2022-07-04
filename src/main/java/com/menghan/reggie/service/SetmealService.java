package com.menghan.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.menghan.reggie.dto.SetmealDto;
import com.menghan.reggie.entity.Setmeal;

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
