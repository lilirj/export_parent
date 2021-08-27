package com.itit.service.system.impl;

import com.alibaba.druid.util.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itit.dao.system.DeptDao;
import com.itit.dao.system.RoleDao;
import com.itit.domain.system.Dept;
import com.itit.domain.system.Module;
import com.itit.domain.system.Role;
import com.itit.service.system.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoleServicImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Override
    public PageInfo<Role> findAll(String companyId, Integer pageNum, Integer pageSize) {
       //分页查询 用户信息 企业id作为筛选条件
        //开启分页查询 默认会对其下第一条mybatis的查询语句进行分页
        PageHelper.startPage(pageNum,pageSize) ;
        //调用dao查询用户信息
        List<Role> list = roleDao.findAll(companyId);
        //pageInfo会封装所有的 分页参数
        PageInfo<Role> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }


    /**
     * 添加角色
     * @param role
     */
    @Override
    public void save(Role role) {
        //设置id
        role.setId(UUID.randomUUID().toString());
        //dao方法
        roleDao.save(role);

    }

    /**
     * 修改信息
     * @param role
     */
    @Override
    public void update(Role role) {
       // 调用dao
        roleDao.update(role);
    }

    /**
     *根据id查询角色数据
     * @param roleId
     * @return
     */
    @Override
    public Role findByRoleId(String roleId) {
        //调用dao
        Role role = roleDao.findById(roleId);
        return role;
    }

    /**
     * 删除用户信息
     * @param roleID
     */
    @Override
    public void delete(String roleID) {
        //因为 主键被外键引用可能导致删除失败的问题
        //先查询是否被引用
        roleDao.delete(roleID);
    }

    @Override
    public void updateRoleModule(String roleid, String moduleIds) {
        //角色分配权限
        //1.删除角色的权限
        roleDao.deleteModuleByRoleid(roleid);

        //2.给角色添加权限
        //如果传入的权限id为空串 会违反主外键约束报错 所以先提前判断
        if(!StringUtils.isEmpty(moduleIds)) { //如果不为空才 给角色添加权限
            //moduleIds是权限Id拼接成的字符串 以逗号隔开
            //切割字符串 拿到权限id
            String[] array = moduleIds.split(",");
            for (String moduleId : array) {
                //调用dao层 给角色赋予权限
                roleDao.saveRoeidModule(roleid, moduleId);
            }
        }
    }

    //.获得所有角色 企业id做为筛选条件
    @Override
    public List<Role> findAllRole(String loginCompanyId) {
        return roleDao.findAll(loginCompanyId);
    }

    //查询用户拥有的角色
    @Override
    public List<Role> findRoleByUserId(String userId) {
       List<Role> userRole= roleDao.findRoleByUserId(userId);
        return userRole;
    }

    //给用户分配角色
    @Override
    public void saveUserRole(String userid, String[] roleIds) {

        //先删除用户的角色（解除用户的角色信息）
        roleDao.deleteRoleByUser(userid);

        //给用户添加角色
        if(roleIds!=null && roleIds.length>0){
            //遍历角色id
            for (String roleId : roleIds) {
                //调用dao给用户添加角色
                roleDao.saveUserRole(userid,roleId);
            }
        }
    }


}
