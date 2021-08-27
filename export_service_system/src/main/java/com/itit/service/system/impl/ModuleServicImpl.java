package com.itit.service.system.impl;

import com.alibaba.druid.util.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itit.dao.system.DeptDao;
import com.itit.dao.system.ModuleDao;
import com.itit.dao.system.ModuleDao;
import com.itit.dao.system.UserDao;
import com.itit.domain.system.Dept;
import com.itit.domain.system.Module;
import com.itit.domain.system.Module;
import com.itit.domain.system.User;
import com.itit.service.system.ModuleService;
import com.itit.service.system.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ModuleServicImpl implements ModuleService {

    @Autowired
    private ModuleDao moduleDao;
    @Autowired
    private UserDao userDao;

    @Override
    public PageInfo<Module> findById(Integer pageNum, Integer pageSize) {

        //开启分页查询 默认会对其下第一条mybatis的查询语句进行分页
        PageHelper.startPage(pageNum,pageSize) ;
        //调用dao查询权限信息
        List<Module> list = moduleDao.findAll();
        //pageInfo会封装所有的 分页参数
        PageInfo<Module> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }


    @Override
    public void save(Module module) {
        //添加权限
        //设置id
        module.setId(UUID.randomUUID().toString());
        //dao方法
        moduleDao.save(module);
    }

    /**
     * 修改权限信息
     * @param module
     */
    @Override
    public void update(Module module) {
       // 调用dao
        moduleDao.update(module);
    }

    /**
     *根据id查询权限数据  可以做权限数据回显
     * @param moduleId
     * @return
     */
    @Override
    public Module findByModuleId(String moduleId) {
        //调用dao
        Module module = moduleDao.findById(moduleId);
        return module;
    }

    /**
     * 删除权限信息
     * @param moduleID
     */
    @Override
    public void delete(String moduleID) {
        moduleDao.delete(moduleID);
    }

    /**
     * 查询所有
     * @return
     */
    @Override
    public List<Module> findAll() {
        return moduleDao.findAll();
    }


    @Override
    public List<Module> findModuleByRoleId(String roleId) {
        //调用dao根据id角色id查询权限
        List<Module> list=moduleDao.findModuleByRoleId(roleId);
        return list;
    }

    /**
     * 根据用户id 查询用户的权限
     * 注意用户有等级  对应等级有对应的权限
     *  用户的等级：
     *  0-saas管理员               只能查看saas模块
     *  1-企业管理员                可以查看除sass模块的其他所有模块
     *  2-管理所有下属部门和人员      2，3，4 查询查询登录用户已经拥有的权限
     *  3-管理本部门
     *  4-普通员工
     * @param userId
     * @return
     */
    @Override
    public List<Module> findModuleByUserId(String userId) {

        //1.根据userId查询用户  获得用户的级别
        User user = userDao.findById(userId);
        //2.获得用户的级别
        Integer degree = user.getDegree();
        //3.根据用户级别查询用户权限
        if(degree==0){
            //-- 3.1 如果登录用户的degree=0，说明是Saas管理员，只能查看saas模块
            //调用dao查询 ss_module表中 belong为0的权限
            return moduleDao.findModuleByBelong(0);
        }else if(degree ==1) {
            //3.2. 如果登录用户的degree=1，说明是企业管理员，可以查看除了saas模块以外的所有模块
            return moduleDao.findModuleByBelong(1);
        }else {
            //-- 3.3 其他用户登录，就根据用户的角色权限，查询登录用户已经拥有的权限
            return moduleDao.findModuleByUserId(userId);
        }
    }
}
