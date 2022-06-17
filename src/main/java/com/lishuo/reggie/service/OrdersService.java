package com.lishuo.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lishuo.reggie.entity.Category;
import com.lishuo.reggie.entity.Orders;

public interface OrdersService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    public void submit(Orders orders);

}
