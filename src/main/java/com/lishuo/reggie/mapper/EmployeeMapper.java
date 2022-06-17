package com.lishuo.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lishuo.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
//                                                 对应的实体类
public interface EmployeeMapper extends BaseMapper<Employee> {


}
