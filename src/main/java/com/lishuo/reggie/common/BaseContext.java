package com.lishuo.reggie.common;


/**
 * 基于ThreadLocal封装的工具类  保存当前用户id
 *
 * 以线程为作用域，单独保存
 */
public class BaseContext {

    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    /**
     * 设置值
     * @param id
     */
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return
     */
    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
