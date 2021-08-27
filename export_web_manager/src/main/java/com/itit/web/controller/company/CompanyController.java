package com.itit.web.controller.company;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.itit.domain.company.Company;
import com.itit.service.company.CompanyService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;

@RequestMapping("/company")
@Controller
public class CompanyController {


    @Reference  //注入接口代理对象
    private CompanyService companyService;

    /**
     *1. 查询全部
     * 如何保存数据到request域?
     * 方式1：Model作为方法参数          【推荐】
     * 方式2：ModelMap 作为方法参数
     * 方式3：HttpServletRequest作为方法参数
     * 方式4：Map 作为方法参数
     * 方式5：ModelAndView作为方法返回值   【推荐】
     */
    @RequestMapping("/list")
    public String list(Model model){

        //硬编码的方式 指定访问资源需要的权限 授权
        Subject subject = SecurityUtils.getSubject();
        // 访问后面的代码必须要有指定的权限；否则报错
        subject.checkPermission("企业管理");


        //查询
        List<Company> list = companyService.findAll();
        //保存
        model.addAttribute("list",list);
        //返还给页面
        return "company/company-list";
    }

    //测试 自定义类型转换 及异常处理
    @RequestMapping("/testConverter")
    public String converter(Date date){
        int i=1/0;
        System.out.println("date="+date);
        return "redirect:/company/list" ;
    }


    /**
     * 2. 添加（1）进入添加页面
     * 功能入口：company-list.jsp页面，点击“新建”
     * 提交地址：/company/toAdd
     * 响应地址：/WEB-INF/pages/company/company-add.jsp
     * 视图解析器前缀：/WEB-INF/pages/
     * 视图解析器后缀：.jsp
     */
    //跳转到添加页面
    @RequestMapping("/toAdd")
    public String toAdd(){
        return "company/company-add";
    }


    /**
     * 3.添加（2）保存
     * 功能入口：company-add.jsp点击保存
     * 请求地址：/company/edit.do
     * 请求参数：封装为Company对象 (页面参数名称要与对象属性名称一致)
     * 响应地址：保存成功，重定向到列表，显示保存的结果
     */
    @RequestMapping("/edit")
    public String edit(Company company){
        //因为添加和修改的参数于返回的页面请求一致所有放在同一个请求方法中

        // 判断：根据id判断是添加还是修改
        if (StringUtils.isEmpty(company.getId())){
            //如果主键id为空 说明是添加操作
             companyService.save(company);
        }else {
            // 主键id不为NULL，说明当前是修改操作
             companyService.update(company);
        }
        return "redirect:/company/list";
    }

    /**
     * 4.修改（1）跳转到修改页面 且显示回显数据
     * 功能入口：company-list.jsp点击编辑
     * 请求地址：/company/toUpdate
     * 请求参数：id
     * 响应地址：/WEB-INF/pages/company/company-update.jsp
     */

    @RequestMapping("/toUpdate")
    public String toUpdate(String id,Model model){
        //调用service，根据id查询企业
        Company company = companyService.findById(id);
        // 保存company对象，页面回显数据
        model.addAttribute("company",company);
        return "company/company-update";
    }


    //根据id删除企业信息
    @RequestMapping("/delete")
    public String delete(String id){
        companyService.delete(id);
        return "redirect:/company/list";
    }

}
