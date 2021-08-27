package com.itit.web.controller.system;

import com.itit.domain.system.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class BaseController {

    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpServletResponse response;
    @Autowired
    protected HttpSession sessione;

    /**
     * 从登陆用户中获得所属的企业id
     */
    public String getLoginCompanyId(){
        return getlonginUser().getCompanyId();
    }

    /**
     * 从登陆用户中获取企业名称
     */
    public String getLoginCompanyNmae(){
        return getlonginUser().getCompanyName();
    }

    /**
     * 获得登陆的用户对象
     */
    public User getlonginUser(){
        User user= (User)sessione.getAttribute("loginUser");
        return user;
    }

}
