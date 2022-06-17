package com.lishuo.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lishuo.reggie.entity.Mail;
import com.lishuo.reggie.entity.User;
import com.lishuo.reggie.mapper.MailMapper;
import com.lishuo.reggie.mapper.UserMapper;
import com.lishuo.reggie.service.MailService;
import com.lishuo.reggie.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl extends ServiceImpl<MailMapper, Mail> implements MailService {

}
