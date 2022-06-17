package com.lishuo.reggie.controller;


import com.lishuo.reggie.common.R;
import com.lishuo.reggie.entity.Orders;
import com.lishuo.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {

    @Autowired
    private OrdersService ordersService;


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
}