package com.itit.service.company;

import com.github.pagehelper.PageInfo;
import com.itit.domain.company.Company;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * 问题：classpath*与classpath区别？
 * 1. classpath 加载当前项目类路径下的配置文件
 * 2. classpath* 加载所有项目类路径下的配置文件；
 *   包含当前项目、当前项目依赖的项目中的类路径下的配置文件
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationContext-*.xml")
public class testServiceCompany {
    @Autowired
    private CompanyService companyService;

    @Test
    public void findAll(){
        List<Company> all = companyService.findAll();
        System.out.println(all);
    }

    @Test
    public void pagehelper(){
        //测试分页查询企业信息

        //设置当前页
        int pageNum =2;
        //设置页大小
        int pageSize=2;
        PageInfo<Company> pageInfo = companyService.findByPage(pageNum, pageSize);
        //总记录数
        System.out.println(pageInfo.getTotal());
        //当前页数据
        System.out.println(pageInfo.getList());
    }

}
