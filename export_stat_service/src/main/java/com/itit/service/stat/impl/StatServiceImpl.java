package com.itit.service.stat.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itit.dao.stat.StatDao;
import com.itit.service.stat.Stat;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class StatServiceImpl implements Stat {

    @Autowired
    private StatDao statDao;

    @Override
    public List<Map<String, Object>> getFactoryData() {
        return statDao.getFactoryData();
    }

    @Override
    public List<Map<String, Object>> getProductData(int top) {
        return statDao.getProductData(top);
    }

    @Override
    public List<Map<String, Object>> getOnline() {
        return statDao.getOnline();
    }
}
