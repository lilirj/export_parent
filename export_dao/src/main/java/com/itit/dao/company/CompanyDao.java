package com.itit.dao.company;

import com.itit.domain.company.Company;

import java.util.List;

//查询所有企业
public interface CompanyDao {
    /**
     * 查询全部
     * MyBatise开发：
     * 1. sql写到注解
     * 2. 简单sql写到注解，复杂sql（动态sql）写到xml
     * 3. sql全部写到xml
     *    好处：架构统一，dao代码可读写好
     */
    List<Company> findAll();

    /**
     * 添加
     * @param company
     */
    void save(Company company);

    /**
     * 修改
     * @param company
     */
    void update(Company company);

    /**
     * 根据id查询企业信息
     * @param id
     * @return
     */
    Company findById(String id);

    //根据id删除企业信息
    void delete(String id);


}
