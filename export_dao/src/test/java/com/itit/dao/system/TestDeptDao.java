package com.itit.dao.system;

import com.itit.domain.system.Dept;
import com.itit.domain.system.Module;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-dao.xml")
public class TestDeptDao {

    @Autowired
    private DeptDao deptDao;

    @Autowired
    private ModuleDao moduleDao;

    @Test
    public void findAll(){
        //设置企业id
        List<Dept> list = deptDao.findAll("1");
        System.out.println(list.size());
    }

    @Test
    public void RoleModule(){
        List<Module> list = moduleDao.findModuleByRoleId("4028a1cd4ee2d9d6014ee2df4c6a0004");
    }
}
