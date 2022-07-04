package com.menghan.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.menghan.reggie.entity.Mail;
import com.menghan.reggie.mapper.MailMapper;
import com.menghan.reggie.service.MailService;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl extends ServiceImpl<MailMapper, Mail> implements MailService {

}
