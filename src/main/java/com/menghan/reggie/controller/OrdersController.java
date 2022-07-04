package com.menghan.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.menghan.reggie.common.BaseContext;
import com.menghan.reggie.common.R;
import com.menghan.reggie.entity.*;
import com.menghan.reggie.entity.Mail;
import com.menghan.reggie.entity.Orders;
import com.menghan.reggie.service.MailService;
import com.menghan.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MailService mailService;


    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> orderSubmit(@RequestBody Orders orders){
//        log.info(""+orders);
        ordersService.submit(orders);
        return R.success("成功下单！");
    }


    /**
     * 客户端订单分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> pageUser(int page, int pageSize){
//        log.info("page:{},pageSize:{},name:{}",page,pageSize,name);


        //构造分页构造器
        Page pageInfo=new Page<>(page,pageSize);

        //条件构造器
        LambdaQueryWrapper<Orders> queryWrapper=new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());

        //添加排序条件
        queryWrapper.orderByDesc(Orders::getStatus);

        //执行查询

        ordersService.page(pageInfo,queryWrapper);


        return R.success(pageInfo);
    }
    /**
     * 管理端订单分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
//        log.info("page:{},pageSize:{},name:{}",page,pageSize,name);


        //构造分页构造器
        Page<Orders> pageInfo=new Page<>(page,pageSize);

        //条件构造器
        LambdaQueryWrapper<Orders> queryWrapper=new LambdaQueryWrapper();
        //添加过滤条件
//        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());

        //添加排序条件
        queryWrapper.orderByDesc(Orders::getCheckoutTime);



        //执行查询

        ordersService.page(pageInfo, queryWrapper);
//对象拷贝
        Page<Orders> ordersPage=new Page<>();
        BeanUtils.copyProperties(pageInfo,ordersPage,"records");//数据内容不完整（不是dto需要dto类型）不进行拷贝方便后续填入完整数据

        List<Orders> records = pageInfo.getRecords();

        List<Orders> list = records.stream().map((s)->{
            Orders orders=new Orders();

            //将基本信息存入（copy）
            BeanUtils.copyProperties(s,orders);
            Long userId = s.getUserId();
            LambdaQueryWrapper<Mail> mailQueryWrapper=new LambdaQueryWrapper();
            //添加过滤条件
            mailQueryWrapper.eq(Mail::getId, userId);
            Mail one = mailService.getOne(mailQueryWrapper);
            if(one!=null){
                String mail = one.getMail();

                orders.setUserName(mail);//填入名称
            }
            return orders;
        }).collect(Collectors.toList());


        ordersPage.setRecords(list);//将获取好的完整数据传入record中


        return R.success(ordersPage);
    }

    /**
     * 修改订单状态
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> updateToStatus(@RequestBody Orders orders){

//        log.info(orders+"");
//订单状态 1待付款，2待派送，3已派送，4已完成，5已取消
        if(orders.getStatus()==1||orders.getStatus()==5){
            return R.error("该订单无法执行派送！");
        }
        if(orders.getStatus()==3||orders.getStatus()==4){
            return R.error("该订单正在进行派送或已派送完！");
        }
        LambdaQueryWrapper<Orders> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getId,orders.getId());
        Orders one = ordersService.getOne(queryWrapper);
        one.setStatus(2);
        ordersService.updateById(one);
        return  R.success("开始进行派送！");
    }

}
