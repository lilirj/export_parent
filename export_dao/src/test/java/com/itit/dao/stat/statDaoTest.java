package com.itit.dao.stat;

import com.itit.dao.system.DeptDao;
import com.itit.dao.system.ModuleDao;
import com.itit.dao.system.RoleDao;
import com.itit.domain.system.Dept;
import com.itit.domain.system.Role;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-dao.xml")
public class statDaoTest {

    @Autowired
    private StatDao statDao;

    @Test
    public void test(){
        List<Map<String, Object>> factoryData = statDao.getFactoryData();
        System.out.println(factoryData.size());
    }

}
