package com.itit.service.system.impl;

import com.alibaba.druid.util.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itit.dao.system.DeptDao;
import com.itit.dao.system.UserDao;
import com.itit.domain.system.Dept;
import com.itit.domain.system.User;
import com.itit.service.system.UserService;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.security.provider.MD5;

import java.util.List;
import java.util.UUID;

@Service
public class UserServicImpl implements UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private DeptDao deptDao;

    @Override
    public PageInfo<User> findAll(String companyId, Integer pageNum, Integer pageSize) {
       //分页查询 用户信息 企业id作为筛选条件
        //开启分页查询 默认会对其下第一条mybatis的查询语句进行分页
        PageHelper.startPage(pageNum,pageSize) ;
        //调用dao查询用户信息
        List<User> list = userDao.findAll(companyId);
        //pageInfo会封装所有的 分页参数
        PageInfo<User> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    @Override
    public List<Dept> findAllDept(String companyId) {
        //  查询所有部门信息
        //调用dao方法 查询所有部门 显示在下拉框
        List<Dept> list = deptDao.findAll(companyId);
        return list;
    }


    @Override
    public void save(User user) {
        //添加用户

        //因为用户表中dept_id部门外键  如果用户不选择部门导致转入的数据DeptId为"" 会违反主外键约束异常
        if (StringUtils.isEmpty(user.getDeptId())){
            user.setDeptId(null);
        }/*else {
            //不为空 查询对应的部门名称存入user中
            //获得用户选择部门的对应名称 设置到user中进行保存
            Dept dept = deptDao.findById(user.getDeptId());
            user.setDeptName(dept.getDeptName());
        }*/
        //设置id
        user.setId(UUID.randomUUID().toString());
        //对密码进行加密
        String s = new Md5Hash(user.getPassword(),user.getEmail()).toString();
        user.setPassword(s);

        //dao方法
        userDao.save(user);

    }

    /**
     * 修改用户信息
     * @param user
     */
    @Override
    public void update(User user) {

        //因为用户表中dept_id部门外键  如果用户不选择部门导致转入的数据DeptId为"" 会违反主外键约束异常
        if (StringUtils.isEmpty(user.getDeptId())){
            user.setDeptId(null);
        }
       // 调用dao
        userDao.update(user);
    }

    /**
     *根据id查询用户数据  可以做用户数据回显
     * @param userId
     * @return
     */
    @Override
    public User findByUserId(String userId) {
        //调用dao
        User user = userDao.findById(userId);
        return user;
    }

    /**
     * 删除用户信息
     * @param userID
     */
    @Override
    public boolean delete(String userID) {
        //因为 主键被外键引用可能导致删除失败的问题
        //先查询是否被引用
        Long userRole = userDao.findUserRole(userID);
        //判断
        if (userRole==0){
            //没有数据 可以删除
            //再调用dao删除
            userDao.delete(userID);
            return true;
        }
        return false;
    }

    @Override
    public User findUserByEmail(String email) {
        User user=userDao.findUserByEmail(email);
        return user;
    }


}
