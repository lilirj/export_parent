package com.itit.service.company;

import com.github.pagehelper.PageInfo;
import com.itit.domain.company.Company;

import java.util.List;

public interface CompanyService {

    List<Company> findAll();

    void save(Company company);

    void update(Company company);

    Company findById(String id);

    void delete(String id);

    /**
     * 分页查询的service方法
     * @param pageNum   当前页
     * @param pageSize  页大小
     * @return 返回PageHelper提供的封装分页参数的PageInfo对象
     */
    PageInfo<Company> findByPage(int pageNum,int pageSize);

}
