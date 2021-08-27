package com.itit.service.cargo.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itit.dao.cargo.FactoryDao;
import com.itit.domain.cargo.Factory;
import com.itit.domain.cargo.FactoryExample;
import com.itit.service.cargo.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@Service
public class FactoryServiceImpl implements FactoryService {

    @Autowired
    FactoryDao factoryDao;

    @Override
    public PageInfo<Factory> findByPage(FactoryExample factoryExample, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Factory> list = factoryDao.selectByExample(factoryExample);
        return new PageInfo<>(list);
    }

    @Override
    public List<Factory> findAll(FactoryExample factoryExample) {
        List<Factory> list = factoryDao.selectByExample(factoryExample);
        return list;
    }

    @Override
    public Factory findById(String id) {
        return factoryDao.selectByPrimaryKey(id);
    }

    @Override
    public void save(Factory factory) {
        factory.setId(UUID.randomUUID().toString());
        factoryDao.insertSelective(factory);

    }

    @Override
    public void update(Factory factory) {
        factoryDao.updateByPrimaryKeySelective(factory);
    }

    @Override
    public void delete(String id) {
        factoryDao.deleteByPrimaryKey(id);
    }

    @Override
    public Factory findByFactoryName(String factoryName) {
        //根据厂家名称查询 厂家id
        //构建厂家查询条件对象
        FactoryExample factoryExample = new FactoryExample();
        //添加查询条件
        factoryExample.createCriteria().andFactoryNameEqualTo(factoryName);
        List<Factory> factoryList = factoryDao.selectByExample(factoryExample);

        //判断
        return (factoryList!=null && factoryList.size()>0) ? factoryList.get(0) : null;
    }
}
