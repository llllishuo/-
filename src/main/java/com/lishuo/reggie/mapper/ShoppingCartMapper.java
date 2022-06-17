package com.lishuo.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lishuo.reggie.entity.AddressBook;
import com.lishuo.reggie.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}
