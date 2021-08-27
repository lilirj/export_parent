package com.itit.service.system;

import com.github.pagehelper.PageInfo;
import com.itit.domain.system.Dept;
import com.itit.domain.system.Module;

import java.util.List;

public interface ModuleService {

    //// 分页查询
    PageInfo<Module> findById(Integer pageNum, Integer pageSize);

    //添加
    void save(Module module);

    //修改
    void update(Module module);

    //根据id 查询数据  做数据回显
    Module findByModuleId(String moduleId);

    //删除
    void delete(String moduleID);

    List<Module> findAll();

    List<Module> findModuleByRoleId(String roleId);

    List<Module> findModuleByUserId(String userId);
}
