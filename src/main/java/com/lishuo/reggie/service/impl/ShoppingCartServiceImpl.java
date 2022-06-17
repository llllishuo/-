package com.lishuo.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lishuo.reggie.entity.AddressBook;
import com.lishuo.reggie.entity.ShoppingCart;
import com.lishuo.reggie.mapper.AddressBookMapper;
import com.lishuo.reggie.mapper.ShoppingCartMapper;
import com.lishuo.reggie.service.AddressBookService;
import com.lishuo.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

}
