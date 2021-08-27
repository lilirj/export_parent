package com.itit.service.system;

import com.github.pagehelper.PageInfo;
import com.itit.domain.system.Dept;
import com.itit.domain.system.User;

import java.util.List;

public interface UserService {

    //// 分页查询
    PageInfo<User> findAll(String companyId, Integer pageNum, Integer pageSize);

    //添加用户信息
    void save(User user);

    //查询所有部门信息
    List<Dept> findAllDept(String companyId);

    //修改用户信息
    void update(User user);

    //根据id 查询数据  做数据回显
    User findByUserId(String userId);

    //删除用户信息
    boolean delete(String userID);

    User findUserByEmail(String email);
}
