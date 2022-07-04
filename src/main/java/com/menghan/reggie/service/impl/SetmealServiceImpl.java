package com.menghan.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.menghan.reggie.dto.SetmealDto;
import com.menghan.reggie.entity.Setmeal;
import com.menghan.reggie.entity.SetmealDish;
import com.menghan.reggie.mapper.SetmealMapper;
import com.menghan.reggie.service.SetmealDishService;
import com.menghan.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    public SetmealDishService setmealDishService;
    /**
     * 查询套餐及相关菜品
     * @param id
     * @return
     */

    @Override
    public SetmealDto getByIdWitDish(Long id) {

        //查询菜品
        Setmeal setmeal = this.getById(id);


        //查询套餐菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper =new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(SetmealDish::getSetmealId,id);//匹配套餐菜品表中的每个菜品对应的套餐id
        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);

        SetmealDto setmealDto = new SetmealDto();

        //dish拷贝进dishDto
        BeanUtils.copyProperties(setmeal,setmealDto);
        setmealDto.setSetmealDishes(setmealDishes);


        return setmealDto;
    }

    /**
     * 更新套餐
     * @param setmealDto
     */
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        //更新套餐表

        this.updateById(setmealDto);
        //更新套餐菜品表
        //先清理
        LambdaQueryWrapper<SetmealDish> queryWrapper =new LambdaQueryWrapper<>();

        //添加条件
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());//匹配套餐菜品表中的每个菜品对应的套餐id

        setmealDishService.remove(queryWrapper);//删除套餐对应的菜品
        //再添加
        //遍历每种菜品，分别把setmeal的Id赋进菜品的setmealid中
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes=setmealDishes.stream().map((f)->{
            Long id = setmealDto.getId();
            f.setSetmealId(id);
            return f;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }
}
