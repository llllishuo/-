package com.lishuo.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lishuo.reggie.common.R;
import com.lishuo.reggie.entity.Mail;
import com.lishuo.reggie.service.MailService;
import com.lishuo.reggie.service.UserService;
import com.lishuo.reggie.utils.MailUtils;
import com.lishuo.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class MailController {


    @Autowired
    private MailService mailService;


    @Autowired
    private MailUtils mailUtils;


    /**
     * 发送邮箱验证码
     * @param mail
     * @return
     */
    @PostMapping("/mail")
    public R<String> mailCode(@RequestBody Mail mail, HttpSession session){
        //获取邮箱

        String userMail = mail.getMail();




        if(StringUtils.isNotEmpty(userMail)){
            //生成验证码

            String code = ValidateCodeUtils.generateValidateCode(4).toString();//4位
            //发送验证码
            log.info(code);

            mailUtils.sendMail(userMail,code);

            //存储验证码
            session.setAttribute(userMail,code);
            return R.success("验证码发送成功！");
        }




        return R.error("发送失败！");
    }

    /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<Mail> login(@RequestBody Map map, HttpSession session){
//       log.info(String.valueOf(map));
        //获取邮箱
        String userMail = map.get("mail").toString();

        //获取验证码
        String code = map.get("code").toString();

        //从session中比对验证码
        Object codeInSession = session.getAttribute(userMail);
        if(codeInSession!=null&&codeInSession.equals(code)){
            //登录成功

            LambdaQueryWrapper<Mail> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(Mail::getMail,userMail);
            Mail one = mailService.getOne(queryWrapper);
            if(one==null){
                //判断邮箱是否为新用户，是则自动注册
                one=new Mail();
                one.setMail(userMail);
                one.setStatus(1);
                mailService.save(one);
            }

            session.setAttribute("user",one.getId());
            return R.success(one);

        }



        return R.error("登录失败！");

    }

    /**
     * 移动端登出
     * @param request
     * @return
     */
    @PostMapping("/loginout")
    public R<String> loginout(HttpServletRequest request){
        //清理session
        request.getSession().removeAttribute("user");
        return R.success("退出成功!");

    }





}
