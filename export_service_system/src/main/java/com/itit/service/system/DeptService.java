package com.itit.service.system;

import com.github.pagehelper.PageInfo;
import com.itit.domain.system.Dept;

import java.util.List;

public interface DeptService {
    /**
     * 分页查询部门
     * @param companyId 根据所属企业id查询
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回封装分页参数的PageInfo对象（当前页、页大小、总记录数、总页数、当前页数据等）
     */
    PageInfo<Dept> findByCompany(String companyId,Integer pageNum,Integer pageSize);

    //查询所有部门信息
    List<Dept> findAll(String companyId);

    void save(Dept dept);

    void update(Dept dept);

    Dept findBydeptId(String id);

    boolean deleById(String id);
}
