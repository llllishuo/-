package com.menghan.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.menghan.reggie.entity.OrderDetail;
import com.menghan.reggie.mapper.OrdersDetailMapper;
import com.menghan.reggie.service.OrdersDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrdersDetailServiceImpl extends ServiceImpl<OrdersDetailMapper, OrderDetail> implements OrdersDetailService {

}
