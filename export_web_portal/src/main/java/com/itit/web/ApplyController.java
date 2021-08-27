package com.itit.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itit.domain.company.Company;
import com.itit.service.company.CompanyService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController //相当于@Controller  + @ResponseBody
public class ApplyController {

    /**
     * @Reference
     * 1. 通过dubbo提供的注解，注入接口的代理对象
     * 2. 注意：引入dubbo的包
     * 3. retries 当远程调用服务，达到超时时间，默认重试2次（建议保持默认）
     *            设置为0表示不重试，连接超时直接断开。
     */
    @Reference(retries = 2) //注入接口代理对象
    private CompanyService companyService;

    @RequestMapping("/apply")
    public String apply(Company company){
        try {
            companyService.save(company);
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }
}
