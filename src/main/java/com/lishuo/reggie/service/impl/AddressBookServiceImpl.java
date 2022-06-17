package com.lishuo.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lishuo.reggie.entity.AddressBook;
import com.lishuo.reggie.entity.User;
import com.lishuo.reggie.mapper.AddressBookMapper;
import com.lishuo.reggie.mapper.UserMapper;
import com.lishuo.reggie.service.AddressBookService;
import com.lishuo.reggie.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}
