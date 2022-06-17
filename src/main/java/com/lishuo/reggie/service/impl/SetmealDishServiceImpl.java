package com.lishuo.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lishuo.reggie.entity.Setmeal;
import com.lishuo.reggie.entity.SetmealDish;
import com.lishuo.reggie.mapper.SetmealDishMapper;
import com.lishuo.reggie.mapper.SetmealMapper;
import com.lishuo.reggie.service.SetmealDishService;
import com.lishuo.reggie.service.SetmealService;
import org.springframework.stereotype.Service;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
