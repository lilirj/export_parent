package com.itit.dao.system;

import com.itit.domain.system.Dept;

import java.util.List;

public interface DeptDao {

    //查询所有部门信息 根据企业id进行筛选
    List<Dept> findAll(String companyId );

    //根据部门id查询部门信息
    Dept findById(String deptId);

    void save(Dept dept);

    void update(Dept dept);

    List<Dept> findByParentId(String id);

    void delete(String id);
}
