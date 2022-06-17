package com.lishuo.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lishuo.reggie.dto.DishDto;
import com.lishuo.reggie.entity.Dish;
import com.lishuo.reggie.entity.DishFlavor;
import com.lishuo.reggie.mapper.DishMapper;
import com.lishuo.reggie.service.DishFlavorService;
import com.lishuo.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {


    @Autowired
    private DishFlavorService dishFlavorService;


    /**
     * 新增菜品同时保存口味数据
     * @param dishDto
     */
    @Transactional//使用业务
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品基本信息到菜品表
        this.save(dishDto);


        //取出菜品id分给每个口味
        Long dishId = dishDto.getId();

        //遍历每种口味，分别把dishId赋进菜品id中
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors=flavors.stream().map((f)->{
            f.setDishId(dishId);
            return f;
        }).collect(Collectors.toList());


        //保存菜品口味到口味表
        dishFlavorService.saveBatch(flavors);
    }



    /**
     * 查询菜品及口味
     * @param id
     * @return
     */

    @Override
    public DishDto getByIdWithFlavor(Long id) {

        //查询菜品
        Dish dish = this.getById(id);


        //查询口味
        LambdaQueryWrapper<DishFlavor> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper);

        DishDto dishDto=new DishDto();

        //dish拷贝进dishDto
        BeanUtils.copyProperties(dish,dishDto);
        dishDto.setFlavors(dishFlavors);


        return dishDto;
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新菜品表

        this.updateById(dishDto);
        //更新口味表
        //先清理
        LambdaQueryWrapper<DishFlavor> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //再添加
        //遍历每种口味，分别把dishId赋进菜品id中
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors=flavors.stream().map((f)->{
            f.setDishId(dishDto.getId());
            return f;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }
}
