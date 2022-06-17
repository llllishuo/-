package com.lishuo.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lishuo.reggie.entity.Category;
import com.lishuo.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface UserMapper extends BaseMapper<User> {
}
