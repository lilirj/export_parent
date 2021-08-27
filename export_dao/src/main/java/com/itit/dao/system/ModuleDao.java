package com.itit.dao.system;

import com.itit.domain.system.Module;

import java.util.List;

public interface ModuleDao {

    //根据id查询
    Module findById(String moduleId);

    //根据id删除
    void delete(String moduleId);

    //添加
    void save(Module module);

    //更新
    void update(Module module);

    //查询全部
    List<Module> findAll();

    //根据角色id 查询所有角色的权限
    List<Module> findModuleByRoleId(String roleId);

    //根据级别 查询权限
    List<Module> findModuleByBelong(int belong);

    //根据用户id查询用户拥有的权限
    List<Module> findModuleByUserId(String userId);
}