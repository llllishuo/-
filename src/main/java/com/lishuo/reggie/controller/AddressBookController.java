package com.lishuo.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.lishuo.reggie.common.BaseContext;
import com.lishuo.reggie.common.R;
import com.lishuo.reggie.entity.AddressBook;
import com.lishuo.reggie.entity.Mail;
import com.lishuo.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {

    @Autowired
    public AddressBookService addressBookService;

    /**
     * 地址查询
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> getList(){
        LambdaQueryWrapper<AddressBook> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        log.info(""+BaseContext.getCurrentId());
        queryWrapper.orderByDesc(AddressBook::getIsDefault);

        List<AddressBook> list = addressBookService.list(queryWrapper);

        return R.success(list);

    }

    /**
     * 添加地址
     * @param addressBook
     * @param session
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook, HttpSession session){
//        log.info(""+addressBook);
        Object user = session.getAttribute("user");

//        log.info(""+user);
        LambdaQueryWrapper<AddressBook> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,user);
        queryWrapper.eq(AddressBook::getIsDefault,1);
        int count = addressBookService.count(queryWrapper);
        if(count==0){
            addressBook.setIsDefault(1);
        }
        addressBook.setUserId((Long) user);
        addressBookService.save(addressBook);

        return R.success("添加成功！");
    }


    /**
     * 修改默认地址
     * @param addressBook
     * @return
     */

    @PutMapping("/default")
    @Transactional
    public R<AddressBook> updateDefault(@RequestBody AddressBook addressBook){
        log.info(""+addressBook);

        //将所有用户default改为0
        LambdaUpdateWrapper<AddressBook> queryWrapper=new LambdaUpdateWrapper<>();//update条件构造器
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());//当前用户的地址
        queryWrapper.set(AddressBook::getIsDefault,0);//将default修改为0
        addressBookService.update(queryWrapper);
        //再修改需要设置的
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);



        return R.success(addressBook);
    }

    /**
     * 获取默认地址
     * @return
     */
    @GetMapping("/default")
    @Transactional
    public R<AddressBook> getDefault(){
        LambdaQueryWrapper<AddressBook> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault,1);
        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        return R.success(addressBook);
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return R.success(addressBook);
        } else {
            return R.error("没有找到该对象");
        }
    }
    /**
     * 修改地址
     * @param addressBook
     * @return
     */

    @PutMapping
    public R<AddressBook> update(@RequestBody AddressBook addressBook){
        log.info(""+addressBook);

        addressBookService.updateById(addressBook);


        return R.success(addressBook);
    }


    @DeleteMapping
    public R<String> delete(Long ids){
//        log.info(""+ids);

        addressBookService.removeById(ids);

        return R.success("删除成功!");

    }



}
