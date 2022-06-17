package com.lishuo.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lishuo.reggie.dto.SetmealDto;
import com.lishuo.reggie.entity.Setmeal;
import com.lishuo.reggie.entity.SetmealDish;
import com.lishuo.reggie.entity.User;
import com.lishuo.reggie.mapper.SetmealMapper;
import com.lishuo.reggie.mapper.UserMapper;
import com.lishuo.reggie.service.SetmealDishService;
import com.lishuo.reggie.service.SetmealService;
import com.lishuo.reggie.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
