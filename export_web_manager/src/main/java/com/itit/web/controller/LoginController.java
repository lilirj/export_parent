package com.itit.web.controller;

import com.alibaba.druid.util.StringUtils;
import com.itit.domain.system.Module;
import com.itit.domain.system.User;
import com.itit.service.system.ModuleService;
import com.itit.service.system.UserService;
import com.itit.web.controller.system.BaseController;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class LoginController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private HttpSession session;


    /**
     *  ！！！！传统登陆！！！！！
     * 执行流程：
     * 1. 访问http://localhost:8080/index.jsp
     * 2. index.jsp ----> window.location.href = "login.do";
     * 3. 所以：需要创建LoginController，处理/login请求.  (.do后缀不会去匹配)
     * 4. 登录成功，默认去到：/WEB-INF/pages/home/main.jsp
     *      视图解析器前缀：/WEB-INF/pages/
     *      视图解析器后缀：.jsp
     */
    /*
    @RequestMapping("/login")
    public String login(String email,String password){
        //处理登陆请求

        //1.判断是否有传入登录参数
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(password)){
            //为空 跳转登陆页面
            return "forward:/login.jsp";
        }

        //2.有数据 就在数据库中查询是是否是有这个用户
        //根据传入的email查询数据库看是否有该用户
        User user=userService.findUserByEmail(email);

        //3.如果查询的结果user为null表示没有这个用户 判断email是否正确
        if (user!=null){
            //4.校验密码
            if (user.getPassword().equals(password)){
                //登陆成功 保存登陆的信息
                sessione.setAttribute("loginUser",user);

                //获取登陆用户的权限（模块）
                List<Module> moduleList=moduleService.findModuleByUserId(user.getId());
                //保存到域对象中
                sessione.setAttribute("moduleList",moduleList);

                // 跳转到主界面
                return "home/main";
            }
        }

        //5.登陆失败 跳转登陆页面
        request.setAttribute("error","用户名获密码错误！");
        return "forward:/login.jsp";

    }
    */


    //shiro登陆认证实现
    @RequestMapping("/login")
    public String login(String email,String password){
        //处理登陆请求
        //1.判断是否有传入登录参数
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(password)){
            //为空 跳转登陆页面
            return "forward:/login.jsp";
        }

        try {
            /* 通过shiro实现登录认证*/
            //1. 创建subject对象，表示当前用户
            Subject subject = SecurityUtils.getSubject();
            //2.创建User...Token  封装登录的账号密码
            AuthenticationToken token = new UsernamePasswordToken(email,password);
            //3.登陆认证  (认证失败，会报错)
            subject.login(token);

            //4.登陆成功
            //4.1获得认证的用户
            // subject.getPrincipal() 获取的就是realm的认证方法返回的对象的构造函数的第一个参数
            User user = (User) subject.getPrincipal();
            //4.2保存用户 到域对象
            session.setAttribute("loginUser",user);
            //4.3获取用户的权限 保存到域对象中
            List<Module> moduleList = moduleService.findModuleByUserId(user.getId());
            session.setAttribute("moduleList",moduleList);
            //4.4跳转到主页面
            return "home/main";

        } catch (AuthenticationException e) {
            e.printStackTrace();
            //登陆失败 跳转登陆页面
            request.setAttribute("error","用户名获密码错误！");
            return "forward:/login.jsp";
        }
    }

    @RequestMapping("/home")
    public String home(){
        return "/home/home";
    }

    /**
     * 功能入口：header中点击注销
     * 退出用户登陆
     * @return
     */




   /*
   //退出用户登陆 使用shiro可以直接使用过滤器实现
   @RequestMapping("/logout")
    public String logout(){

        //清空shiro中的认证身份信息
        SecurityUtils.getSubject().logout();
        //删除域中存储的用户信息
        sessione.removeAttribute("loginUser");
        //销毁session
        sessione.invalidate();
        //返回登陆界面
        return "redirect:/login.jsp";

    }*/
}
