package com.itit.web.util;

import com.itit.domain.system.SysLog;
import com.itit.domain.system.User;
import com.itit.service.system.SysLogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * 自动记录日志的切面类
 */
@Component
@Aspect
public class LogAspect {

    // 注入日志service
    @Autowired
    private SysLogService sysLogService;
    @Autowired
    private HttpServletRequest request;

    /**
     * 环绕通知
     * 第一个切入点表达式：
     *    语法：execution(* cn.itcast.web.controller.*.*.*(..))
     *    语义：拦截cn.itcast.web.controller这个包下，有一个子包，子包下的所有类的所有方法。
     *    技巧：从后往前看，最后一个*是方法，倒数第二个*是类，其他都是包。
     * 第二个切入点表达式：
     *    语法：!bean(sysLogController)
     *    语义：表示不拦截sysLogController这个控制器类的所有方法
     */
    @Around("execution(* com.itit.web.controller.*.*.*(..)) && !bean(sysLogController)")
    public Object insertLog(ProceedingJoinPoint pjp){

        try {
            // 放行，执行控制器方法
            Object retv = pjp.proceed();

            //创建日志对象
            SysLog sysLog = new SysLog();
            // 设置当前执行的方法名称
            sysLog.setMethod(pjp.getSignature().getName());
            // 设置当前执行的方法所在的类
            sysLog.setAction(pjp.getTarget().getClass().getName());
            // 设置访问者的ip地址
            sysLog.setId(request.getRemoteAddr());
            //设置访问时间
            sysLog.setTime(new Date());

            // 通过request获取session对象，再获取session中存储的登录用户
            User loginUser = (User) request.getSession().getAttribute("loginUser");
            if (loginUser!=null){
                sysLog.setUserName(loginUser.getUserName());
                sysLog.setCompanyId(loginUser.getCompanyId());
                sysLog.setCompanyName(loginUser.getCompanyName());
            }

            //保存日志 记录日志信息
            sysLogService.save(sysLog);

            //返回
            return retv;

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }




}
