package com.itit.service.system.impl;

import com.alibaba.druid.util.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itit.dao.system.DeptDao;
import com.itit.domain.system.Dept;
import com.itit.service.system.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DeptServiceImpl implements DeptService {
    @Autowired
    private DeptDao deptDao;

    @Override
    public PageInfo<Dept> findByCompany(String companyId, Integer pageNum, Integer pageSize) {

        //开启分页支持 （会自动对其后的第一条查询语句分页）
        PageHelper.startPage(pageNum,pageSize);
        //调用dao查询所有部门信息（根据企业id进行筛选）的方法
        List<Dept> list = deptDao.findAll(companyId);
        //创建pageInfo对象 会自动封装所有的分页数据
        PageInfo<Dept> deptPageInfo = new PageInfo<>(list);
        //返回数据
        return deptPageInfo;
    }

    @Override
    public List<Dept> findAll(String companyId) {
        List<Dept> list = deptDao.findAll(companyId);
        return list;
    }

    @Override
    public void save(Dept dept) {
        //添加数据 id不能为空 违反主键约定
        String uuid = UUID.randomUUID().toString();
        dept.setId(uuid);
        // 解决问题：保存部门，不选择父部门，父部门id是"", 导致报错。
        // 如何解决：如果父部门id是空字符串，改为NULL    违反了主外键约束
        if (StringUtils.isEmpty(dept.getParent().getId()) ){  //如果父部门id为空串"" 或null
                dept.getParent().setId(null);
        }
        // 父部门id设置为NULL;  (设置之前是空字符串)
        deptDao.save(dept);
    }

    @Override
    public void update(Dept dept) {
        //判断父部门id是否为空串或 null
        /*if (StringUtils.isEmpty(dept.getParent().getId())){
            dept.getParent().setId(null);
        }*/
        deptDao.update(dept);
    }

    @Override
    public Dept findBydeptId(String id) {
        return deptDao.findById(id);
    }

    /*根据id删除部门*/
    @Override
    public boolean deleById(String id) {
        //部门删除： 需求就是删除id是100的部门
        //1）根据要删除的部门查询是否有子部门  （当前主键是否为外键引用 如果引用不能删除  就是拿着主键id匹配外键的对应字段 没匹配到说明没有引用）
        //SELECT * FROM pe_dept WHERE parent_id='100'
        List<Dept> list =deptDao.findByParentId(id);

        // 2）如果当前部门没有子部门(上面的查询没有结果)，可以删除
        //DELETE FROM pe_dept WHERE dept_id='100'
        if (list==null || list.size()==0){
            // 说明当前要删除的部门，没有子部门，可以删除
            deptDao.delete(id);
            return true;
        }
        return false;
    }
}
