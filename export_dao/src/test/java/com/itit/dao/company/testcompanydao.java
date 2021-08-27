package com.itit.dao.company;

import com.itit.dao.system.DeptDao;
import com.itit.domain.company.Company;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-dao.xml")
public class testcompanydao {

    @Autowired
    private CompanyDao companyDao;

    @Autowired
    private DeptDao deptDao;

    @Test
    public void findAll(){
        List<Company> all = companyDao.findAll();
        System.out.println(all);
    }



}
