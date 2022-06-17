package com.lishuo.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lishuo.reggie.common.R;
import com.lishuo.reggie.entity.Employee;
import com.lishuo.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;


    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        //1.将提交的密码进行MD5加密；
        String password = employee.getPassword();
        password=DigestUtils.md5DigestAsHex(password.getBytes());
//        log.info("密码加密结果："+password);

        //2.根据提交的username与数据库进行匹配；
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();//封装查询对象
        queryWrapper.eq(Employee::getUsername,employee.getUsername());//数据库信息与输入的username进行等值匹配
        Employee emp = employeeService.getOne(queryWrapper);

        if(emp==null){
            return R.error("登录失败，用户名不匹配！");
        }
        //3.进行密码比对；
        if(!emp.getPassword().equals(password)){
            return R.error("登录失败，密码错误！");
        }

        //4.查看员工权限状态；

        if (emp.getStatus()==0){
            return R.error("由于权限不足，您无法登录！");
        }
        //5.登陆成功将员工id存入Session；
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);

    }

    /**
     * 退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理session
        request.getSession().removeAttribute("employee");
        return R.success("退出成功!");

    }


    /**
     * 新增员工
     * @param employee
     * @return
     */

    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){

//        log.info("新增员工信息:"+employee.toString());

        //设置初始密码,md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

//        //获取当前时间
//        employee.setCreateTime(LocalDateTime.now());
//        //获取更新时间
//        employee.setUpdateTime(LocalDateTime.now());

        //获取当前登录用户id
        long id = (long) request.getSession().getAttribute("employee");


//        employee.setCreateUser(id);//创建人
//        employee.setUpdateUser(id);//更新人

        employeeService.save(employee);
        return R.success("新增员工成功！") ;

    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
//        log.info("page:{},pageSize:{},name:{}",page,pageSize,name);

        //构造分页构造器
        Page pageInfo=new Page<>(page,pageSize);

        //条件构造器
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);

        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询

        employeeService.page(pageInfo,queryWrapper);


        return R.success(pageInfo);
    }

    /**
     * 根据Id修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> updata(HttpServletRequest request,@RequestBody Employee employee){

//        log.info(employee.toString());

//        long empid = (long) request.getSession().getAttribute("employee");
//        employee.setUpdateUser(empid);
//        employee.setUpdateTime(LocalDateTime.now());
        employeeService.updateById(employee);

//        long id = Thread.currentThread().getId();
//        log.info("update线程id:"+id);

        return R.success("修改成功！");
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable long id){
//        log.info("根据id查询:"+id);
        Employee byId = employeeService.getById(id);
        if(byId!=null){
            return R.success(byId);
        }
        return R.error("查无此人!");
    }


}
