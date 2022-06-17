package com.lishuo.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lishuo.reggie.entity.OrderDetail;
import com.lishuo.reggie.entity.Orders;
import com.lishuo.reggie.mapper.OrdersDetailMapper;
import com.lishuo.reggie.mapper.OrdersMapper;
import com.lishuo.reggie.service.OrdersDetailService;
import com.lishuo.reggie.service.OrdersService;
import org.springframework.stereotype.Service;

@Service
public class OrdersDetailServiceImpl extends ServiceImpl<OrdersDetailMapper, OrderDetail> implements OrdersDetailService {

}
