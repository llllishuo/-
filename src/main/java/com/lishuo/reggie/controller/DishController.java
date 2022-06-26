package com.lishuo.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lishuo.reggie.common.R;
import com.lishuo.reggie.dto.DishDto;
import com.lishuo.reggie.entity.*;
import com.lishuo.reggie.service.CategoryService;
import com.lishuo.reggie.service.DishFlavorService;
import com.lishuo.reggie.service.DishService;
import com.lishuo.reggie.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RequestMapping("/dish")
@RestController
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;



    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info("dishDto:"+dishDto);


        dishService.saveWithFlavor(dishDto);


        return R.success("添加成功！") ;



    }

    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){

        //分页构造器
        Page<Dish> pageInfo=new Page<>(page,pageSize);

        Page<DishDto> dishDtoPage=new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();


        //添加过滤条件
        queryWrapper.like(name!=null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);


        dishService.page(pageInfo,queryWrapper);


        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");//数据内容不完整（不是dto需要dto类型）不进行拷贝方便后续填入完整数据

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((r)->{
            DishDto dishDto=new DishDto();

            //将基本信息存入（copy）
            BeanUtils.copyProperties(r,dishDto);
            Long categoryId = r.getCategoryId();//获取菜品id
            Category category = categoryService.getById(categoryId);//查询获得菜品名称
            if(category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);//填入名称
            }
            return dishDto;
        }).collect(Collectors.toList());


        dishDtoPage.setRecords(list);//将获取好的完整数据传入record中

        return R.success(dishDtoPage);
    }
    /**
     * 根据id删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long[] ids){
//        log.info(""+ids);
        //判断该菜品是否存在于套餐中
        LambdaQueryWrapper<SetmealDish> queryWrapper =new LambdaQueryWrapper<>();

        //添加条件
        queryWrapper.in(SetmealDish::getDishId,ids);

        int count = setmealDishService.count(queryWrapper);

        if(count!=0){
            return R.error("部分套餐中含已选菜品，无法删除！");
        }

        //如果套餐中没有该菜品再删除
        LambdaQueryWrapper<DishFlavor> dishLambdaQueryWrapper =new LambdaQueryWrapper<>();

        //添加条件
        dishLambdaQueryWrapper.in(DishFlavor::getDishId,ids);//匹配口味表中的菜品id

        dishFlavorService.remove(dishLambdaQueryWrapper);//删除菜品对应的口味

        dishService.removeByIds(Arrays.asList(ids));//删除菜品


        return R.success("删除成功!");
    }

    /**
     * 批量修改菜品销售状态
     * @param i
     * @param ids
     * @return
     */
    @PostMapping("/status/{i}")
    public R<String> status(@PathVariable int i,Long[] ids){
//        log.info("status:{},ids:{}",i,ids);

        for (Long id : ids) {
            Dish dish = dishService.getById(id);//根据id获取菜品信息
            dish.setStatus(i);//修改status
            dishService.updateById(dish);//修改
        }

        return R.success("修改成功!");

    }

    /**
     * 根据id查询菜品信息与口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){

        DishDto dishDto = dishService.getByIdWithFlavor(id);


        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
//        log.info("dishDto:"+dishDto);


        dishService.updateWithFlavor(dishDto);

        String key="dish_"+dishDto.getCategoryId()+"_"+dishDto.getStatus();
        redisTemplate.delete(key);


        return R.success("添加成功！") ;



    }
    /**
     * 根据分类id查询菜品
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> getDish(Dish dish){

        Long categoryId = dish.getCategoryId();


        String key="dish_"+dish.getCategoryId()+"_"+dish.getStatus();
        //先从redis获取数据
        List<DishDto> dishDtoList=null;
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        if(dishDtoList!=null){
            return R.success(dishDtoList);
        }

        //不存在再查询数据库

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();


        //添加过滤条件
        queryWrapper.eq(categoryId!=null,Dish::getCategoryId,categoryId);
        //查询起售状态的菜品
        queryWrapper.eq(Dish::getStatus,1);

        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        List<Dish> dishList = dishService.list(queryWrapper);



        dishDtoList=dishList.stream().map((r)->{
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(r,dishDto);
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper=new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,r.getId());
            List<DishFlavor> flavorList = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            dishDto.setFlavors(flavorList);
            return dishDto;
        }).collect(Collectors.toList());

        redisTemplate.opsForValue().set(key,dishDtoList);

        return R.success(dishDtoList);
    }
}
