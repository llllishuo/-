package com.menghan.reggie.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class MailUtils {

    @Autowired
    private JavaMailSender javaMailSender;
/*

    //发送人
    private String from="1756524586@qq.com";
    //接收人
    private String to="lishuo123@smail.sut.edu.cn";
    //标题
    private String subject="瑞吉外卖项目验证码";
    //正文
    private String context="验证码：";
*/
    @Value("${spring.mail.username}")
    private String from;

    public void sendMail(String to,String code) {
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("瑞吉外卖项目测试-验证码");
        message.setText("验证码为："+code+",有效期：五分钟,"+"如有问题联系1756524586");
        javaMailSender.send(message);
    }
}
