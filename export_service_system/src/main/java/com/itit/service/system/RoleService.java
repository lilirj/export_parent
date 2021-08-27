package com.itit.service.system;

import com.github.pagehelper.PageInfo;
import com.itit.domain.system.Dept;
import com.itit.domain.system.Module;
import com.itit.domain.system.Role;

import java.util.List;

public interface RoleService {

    //// 分页查询
    PageInfo<Role> findAll(String companyId, Integer pageNum, Integer pageSize);

    //添加用户信息
    void save(Role role);

    //修改用户信息
    void update(Role role);

    //根据id 查询数据  做数据回显
    Role findByRoleId(String roleId);

    //删除用户信息
    void delete(String roleID);

    void updateRoleModule(String roleid ,String moduleIds );

    List<Role> findAllRole(String loginCompanyId);

    List<Role> findRoleByUserId(String userId);

    void  saveUserRole(String userid,String[] roleIds);
}
