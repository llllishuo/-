package com.lishuo.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lishuo.reggie.common.R;
import com.lishuo.reggie.dto.DishDto;
import com.lishuo.reggie.dto.SetmealDto;
import com.lishuo.reggie.entity.*;
import com.lishuo.reggie.service.CategoryService;
import com.lishuo.reggie.service.SetmealDishService;
import com.lishuo.reggie.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
@Api(tags = "套餐相关接口")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;
    /**
     * 新增套餐
     * @param
     * @return
     */
    @PostMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto){
//        log.info("setmealDto:"+setmealDto);

        setmealService.save(setmealDto);



        List<SetmealDish> setmealDishes=new ArrayList<>();
        setmealDishes=setmealDto.getSetmealDishes().stream().map((s)->{
            s.setSetmealId(setmealDto.getId());
            setmealDishService.save(s);
            return s;
        }).collect(Collectors.toList());


        return R.success("添加成功!");



    }
    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize,String name){
        //分页构造器
        Page<Setmeal> pageInfo=new Page<>(page,pageSize);

        Page<SetmealDto> setmealDtoPage=new Page<>();

        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();


        //添加过滤条件
        queryWrapper.like(name!=null,Setmeal::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);


        setmealService.page(pageInfo,queryWrapper);


        //对象拷贝
        BeanUtils.copyProperties(pageInfo,setmealDtoPage,"records");//数据内容不完整（不是dto需要dto类型）不进行拷贝方便后续填入完整数据

        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((s)->{
            SetmealDto setmealDto=new SetmealDto();

            //将基本信息存入（copy）
            BeanUtils.copyProperties(s,setmealDto);
            Long categoryId = s.getCategoryId();
            Category category = categoryService.getById(categoryId);//查询获得菜品名称
            if(category!=null){
                String categoryName = category.getName();

                setmealDto.setCategoryName(categoryName);//填入名称
            }
            return setmealDto;
        }).collect(Collectors.toList());


        setmealDtoPage.setRecords(list);//将获取好的完整数据传入record中

        return R.success(setmealDtoPage);
    }
    /**
     * 批量修改菜品销售状态
     * @param i
     * @param ids
     * @return
     */
    @PostMapping("/status/{i}")
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> status(@PathVariable int i,Long[] ids){
//        log.info("status:{},ids:{}",i,ids);

        for (Long id : ids) {
            Setmeal setmeal = setmealService.getById(id);//根据id获取菜品信息
            setmeal.setStatus(i);//修改status
            setmealService.updateById(setmeal);//修改
        }

        return R.success("修改成功!");

    }
    /**
     * 根据id删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation(value = "根据id删除接口")//介绍接口
    @ApiImplicitParams({//介绍参数组
            @ApiImplicitParam(name = "ids",value = "ids",readOnly = true)
    })
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> delete(Long[] ids){

//        log.info(""+ids);

        //循环删除，停售的套餐
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper =new LambdaQueryWrapper<>();
        //添加条件
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus,1);//判断是否起售
        setmealLambdaQueryWrapper.in(Setmeal::getId,ids);
        int count = setmealService.count(setmealLambdaQueryWrapper);
        if(count!=0){
            return R.error("部分套餐正在售卖，无法删除！");
        }




        LambdaQueryWrapper<SetmealDish> queryWrapper =new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.in(SetmealDish::getSetmealId,ids);//匹配套餐菜品表中的套餐id
        setmealDishService.remove(queryWrapper);//删除套餐对应的菜品

        setmealService.removeByIds(Arrays.asList(ids));//删除菜品

        return R.success("删除成功!");
    }
    /**
     * 根据id查询菜品信息与口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id){

        SetmealDto setmealDto = setmealService.getByIdWitDish(id);


        return R.success(setmealDto);
    }
    /**
     * 修改套餐
     * @param setmealDto
     * @return
     */
    @PutMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> update(@RequestBody SetmealDto setmealDto){
//        log.info("dishDto:"+dishDto);


        setmealService.updateWithDish(setmealDto);


        return R.success("添加成功！") ;



    }

    /**
     * 查询套餐列表
     * @param categoryId
     * @param status
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "setmealCache",key = "#categoryId+'_'+#status")
    public R<List<Setmeal>> list(Long categoryId,int status){
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();

        queryWrapper.eq(Setmeal::getStatus,status);
        queryWrapper.eq(Setmeal::getCategoryId,categoryId);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return  R.success(list);
    }
}
