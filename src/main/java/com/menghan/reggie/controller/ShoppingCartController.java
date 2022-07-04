package com.menghan.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.menghan.reggie.common.BaseContext;
import com.menghan.reggie.common.R;
import com.menghan.reggie.entity.ShoppingCart;
import com.menghan.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {


    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCart
     * @param session
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart, HttpSession session){
//        log.info(""+shoppingCart);
        //获取用户id
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        //查询菜品或者套餐是否再购物车中
        if(shoppingCart.getDishId()!=null){
            //菜品
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
            queryWrapper.eq(shoppingCart.getDishFlavor()!=null,ShoppingCart::getDishFlavor,shoppingCart.getDishFlavor());

        }else {
            //套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //如果购物车中存在不同口味则根据上一次添加信息添加
        int count = shoppingCartService.count(queryWrapper);
        if(count>1){
            Object shoppingCartId = session.getAttribute("shoppingCartId");
            queryWrapper.eq(ShoppingCart::getId,shoppingCartId);
        }
        //查询菜品或者套餐是否再购物车中
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        if(one!=null){
            //已经存在
            Integer number = one.getNumber();
            one.setNumber(number+1);
            shoppingCartService.updateById(one);
        }else {
            //不存在则新增
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            one=shoppingCart;
        }
        if(one.getDishId()!=null){
            session.setAttribute("shoppingCartId",one.getId());
        }
        return R.success(one);
    }
    /**
     * 减少购物车
     * @param shoppingCart
     * @param session
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart, HttpSession session){
//        log.info(""+shoppingCart);
        //获取用户id
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        //查询菜品或者套餐是否再购物车中
        if(shoppingCart.getDishId()!=null){
            //菜品
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
//            queryWrapper.eq(shoppingCart.getDishFlavor()!=null,ShoppingCart::getDishFlavor,shoppingCart.getDishFlavor());

        }else {
            //套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
/*        //如果购物车中存在不同口味则根据上一次添加信息添加
        int count = shoppingCartService.count(queryWrapper);
        if(count>1){
            Object shoppingCartId = session.getAttribute("shoppingCartId");
            queryWrapper.eq(ShoppingCart::getId,shoppingCartId);
        }*/
        //查询菜品或者套餐是否再购物车中
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        Integer integer = one.getNumber();

        if(one!=null){
            //已经存在
            if(integer==1){
                //如果只有一个就删除
                shoppingCartService.remove(queryWrapper);
            }else {
                Integer number = one.getNumber();
                one.setNumber(number-1);
                shoppingCartService.updateById(one);
            }

        }else {
            //不存在则新增
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            one=shoppingCart;
        }
        /*if(one.getDishId()!=null){
            session.setAttribute("shoppingCartId",one.getId());
        }*/
        return R.success(one);
    }

    /**
     * 获取购物车内容
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){

        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){

        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);

        return R.success("删除成功！");
    }
}
