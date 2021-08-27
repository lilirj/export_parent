package com.itit.service.company.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itit.dao.company.CompanyDao;
import com.itit.domain.company.Company;
import com.itit.service.company.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

/**
 * 要使用dubbo提供的@Service注解
 * 导入的包：com.alibaba.dubbo.config.annotation.Service;
 */
@Service(timeout = 100000) //发布服务   timeout 设置超时时间
public class CompanyServiceImpl implements CompanyService {
    @Autowired
    private CompanyDao companyDao;

    /**
     * 查询全部
     */
    @Override
    public List<Company> findAll() {
        return companyDao.findAll();
    }

    /**
     * 添加
     * @param company
     */
    @Override
    public void save(Company company) {
        // 注意：uuid作为主键 (uuid就是javautil包提供的生成全球唯一的32位的序列号)
        String s = UUID.randomUUID().toString();
        company.setId(s);
        companyDao.save(company);
    }

    /**
     * 修改
     * @param company
     */
    @Override
    public void update(Company company) {
         companyDao.update(company);
    }

    /*
     * 根据id查询企业信息
     * */
    @Override
    public Company findById(String id) {
        return companyDao.findById(id);
    }

    @Override
    public void delete(String id) {
        companyDao.delete(id);
    }

    //分页查询所有企业信息
    @Override
    public PageInfo<Company> findByPage(int pageNum, int pageSize) {
        // 开启分页支持，会自动对其后的第一条查询语句进行分页
        PageHelper.startPage(pageNum,pageSize);
        //调用dao的查询所有企业信息的方法
        List<Company> list = companyDao.findAll();
        //自动封装分页参数  pageInfo中自动封装了需要的参数信息
        PageInfo<Company> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }


}
