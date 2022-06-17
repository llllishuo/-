package com.lishuo.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lishuo.reggie.entity.OrderDetail;
import com.lishuo.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface OrdersDetailMapper extends BaseMapper<OrderDetail> {
}
