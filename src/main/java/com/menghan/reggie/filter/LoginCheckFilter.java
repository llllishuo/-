package com.menghan.reggie.filter;


import com.alibaba.fastjson.JSON;
import com.menghan.reggie.common.BaseContext;
import com.menghan.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否完成登录
 */
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request= (HttpServletRequest) servletRequest;
        HttpServletResponse response= (HttpServletResponse) servletResponse;


        //1.获取本次请求的uri；
        String requestURI = request.getRequestURI();
        //不需要拦截路径
        String[] urls=new String[]{
                "/employee/login"
                ,"employee/logout"
                ,"/backend/**"
                ,"/front/**"
                ,"/user/sendMsg"
                ,"/user/login"
                ,"/user/mail"
                ,"/doc.html"
                ,"/webjars/**"
                ,"/swagger-resources"
                ,"/v2/api-docs"
        };
        //2.判断是否处理，不处理则放行；

        boolean check = check(urls, requestURI);
        if(check){
            filterChain.doFilter(request,response);
            return;
        }

        //3.判断登录状态，已登录则放行；
        if(request.getSession().getAttribute("employee")!=null){


//            long id = Thread.currentThread().getId();
//            log.info("过滤器线程id:"+id);
            Long empId = (Long) request.getSession().getAttribute("employee");

            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }
        //移动端
        if(request.getSession().getAttribute("user")!=null){


//            long id = Thread.currentThread().getId();
//            log.info("过滤器线程id:"+id);
            Long Id = (Long) request.getSession().getAttribute("user");

            BaseContext.setCurrentId(Id);

            filterChain.doFilter(request,response);
            return;
        }


        //4.未登录返回结果,通过输出流响应数据；
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }

    /**
     * 检测本次请求是否放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
