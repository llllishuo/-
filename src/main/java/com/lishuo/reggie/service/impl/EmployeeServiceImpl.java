package com.lishuo.reggie.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lishuo.reggie.entity.Employee;
import com.lishuo.reggie.mapper.EmployeeMapper;
import com.lishuo.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {


}
