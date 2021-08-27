package com.itit.service.cargo;

import com.itit.dao.cargo.FactoryDao;
import com.itit.domain.cargo.Factory;
import com.itit.domain.cargo.FactoryExample;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-dao.xml")
public class FactoryDaoTest {
    @Autowired
    private FactoryDao factoryDao;

    //根据id查询
    @Test
    public void findId(){
        Factory factory = factoryDao.selectByPrimaryKey("1d3125df-b186-4a2d-8048-df817d30e52b");
        System.out.println("factory"+factory);
    }

    //查询全部
    @Test
    public void findAll(){
        List<Factory> list = factoryDao.selectByExample(null);
        System.out.println("List"+list);
    }

    //条件查询
    @Test
    public void findByCondition(){
        //A. 创建查询条件对象
        FactoryExample factoryExample = new FactoryExample();
        // 获取条件对象
        FactoryExample.Criteria criteria = factoryExample.createCriteria();
        // 添加条件:factory_name 厂家名称含有祁县的数据
        criteria.andFullNameLike("%祁县%");

        //B. 条件查询
        List<Factory> list = factoryDao.selectByExample(factoryExample);
        System.out.println(list);
    }


    /**
     * 4. 更新（1）普通更新，全字段更新
     * update co_factory set ctype = ?, full_name = ?, factory_name = ?,
     * contacts = ?, phone = ?, mobile = ?, fax = ?, address = ?, inspector = ?,
     * remark = ?, order_no = ?, state = ?, create_by = ?, create_dept = ?, create_time = ?,
     * update_by = ?, update_time = ? where id = ?
     */
    @Test
    public void update(){
        Factory factory = new Factory();
        factory.setFactoryName("草原工厂");
        factory.setCtype("货物");
        factory.setId("fa382919-5a95-4471-8f12-5e37bbf469ca");
        factory.setCreateTime(new Date());
        factory.setUpdateTime(new Date());

        factoryDao.updateByPrimaryKeySelective(factory);
    }


    /**
     * 4. 更新（2）动态更新，判断对象属性如果不为null，才生产更新的sql
     * update co_factory SET factory_name = ?, create_time = ?, update_time = ? where id = ?
     */
    @Test
    public void update2(){
        Factory factory = new Factory();
        factory.setFactoryName("测试工厂");
        factory.setUpdateTime(new Date());
        factory.setCreateTime(new Date());

        factoryDao.updateByPrimaryKeySelective(factory);
    }


}
