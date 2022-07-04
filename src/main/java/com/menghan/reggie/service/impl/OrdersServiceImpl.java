package com.menghan.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.menghan.reggie.common.BaseContext;
import com.menghan.reggie.common.CustomException;
import com.menghan.reggie.entity.*;
import com.menghan.reggie.entity.*;
import com.menghan.reggie.mapper.OrdersMapper;
import com.menghan.reggie.service.*;
import com.menghan.reggie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private AddressBookService addressBookService;


    @Autowired
    private MailService mailService;

    @Autowired
    private OrdersDetailService ordersDetailService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @Transactional
    @Override
    public void submit(Orders orders) {

        //获取用户id

        Long userId = BaseContext.getCurrentId();


        //查询当前用户购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);


        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);
        if(shoppingCartList==null||shoppingCartList.size()==0){
            throw new CustomException("购物车为空，无法下单");
        }

        //查询用户信息
        Mail byId = mailService.getById(userId);


        //查询地址信息
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if(addressBook==null){
            throw new CustomException("地址有误，无法下单");
        }
        //向订单表插入一条数据
        //先补全数据
        //生成订单号
        long orderId = IdWorker.getId();//订单号

        AtomicInteger amount = new AtomicInteger(0);//原子整形，多线程内容，保证在同一线程计算，安全

        List<OrderDetail> orderDetails = shoppingCartList.stream().map((o) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(o.getNumber());//份数
            orderDetail.setDishFlavor(o.getDishFlavor());//口味
            orderDetail.setDishId(o.getDishId());//菜品id
            orderDetail.setSetmealId(o.getSetmealId());//套餐id
            orderDetail.setName(o.getName());//菜品或者套餐名称
            orderDetail.setImage(o.getImage());//图片文件名
            orderDetail.setAmount(o.getAmount());//单份金额
            //     累加      （      单份金额     乘                      份数       ）.转为intValue
            amount.addAndGet(o.getAmount().multiply(new BigDecimal(o.getNumber())).intValue());//累加  相当于  ？+=？
            return orderDetail;
        }).collect(Collectors.toList());


        orders.setId(orderId);//订单号
        orders.setOrderTime(LocalDateTime.now());//订单生成时间
        orders.setCheckoutTime(LocalDateTime.now());//结账时间
        orders.setStatus(2);//订单状态 2-带派送
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);//用户id
        orders.setNumber(String.valueOf(orderId));//订单号
        orders.setUserName(byId.getName());//用户姓名
        orders.setConsignee(addressBook.getConsignee());//收货人
        orders.setPhone(addressBook.getPhone());//电话
        orders.setAddress(
                (addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail())
        );//地址,把数据库里的拼起来





        this.save(orders);



        //向订单明细表插入数据（多条）

        ordersDetailService.saveBatch(orderDetails);

        //清空购物车
        shoppingCartService.remove(queryWrapper);
    }
}
