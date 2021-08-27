package com.itit.service.system;

import com.github.pagehelper.PageInfo;
import com.itit.domain.system.Dept;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/spring/applicationContext-*.xml")
public class TestDeptServiceImpl {
    @Autowired
    private DeptService deptService;

    @Test
    //测试 查询部门信息分页方法findByCompany
    public void testfindByCompany(){
        //设置企业id
        String companId="1";
        Integer pageNum=1;
        Integer pageSize=5;

        //pageInfo封装了所有的分页信息
        PageInfo<Dept> pageInfo = deptService.findByCompany(companId,pageNum,pageSize);
        //pageInfo.getList 获得当前页信息
        List<Dept> list = pageInfo.getList();
    }
}
