package com.itit.web.exception;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义全局异常
 * 1. 可以捕获所有的控制器方法的异常；
 * 2. 当控制方法出现异常，自动来到当前类的resolveException()方法
 * 3. 在resolveException()中可以对异常进行判断、处理。
 */
@Component
public class CustomException implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        // 打印异常信息（否则控制台没有错误，不方便排查）
        e.printStackTrace();

        ModelAndView modelAndView = new ModelAndView();
        // 设置友好页面 保存错误信息，这样页面就可以获取
        modelAndView.addObject("errorMsg","对不起服务忙，请联系管理员134679852"+e.getMessage());
        // 转发到的错误页面，视图解析器已经配置了转发页面路径的前缀、后缀
        modelAndView.setViewName("error");
        return modelAndView;
    }
}
