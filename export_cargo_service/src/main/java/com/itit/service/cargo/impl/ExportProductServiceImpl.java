package com.itit.service.cargo.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itit.dao.cargo.ExportProductDao;
import com.itit.domain.cargo.ExportProduct;
import com.itit.domain.cargo.ExportProductExample;
import com.itit.service.cargo.ExportProductService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class ExportProductServiceImpl implements ExportProductService{

    @Autowired
    private ExportProductDao exportProductDao;

    @Override
    public ExportProduct findById(String id) {
        return exportProductDao.selectByPrimaryKey(id);
    }

    @Override
    public void save(ExportProduct exportProduct) {
        exportProductDao.insertSelective(exportProduct);
    }

    @Override
    public void update(ExportProduct exportProduct) {
        exportProductDao.updateByPrimaryKeySelective(exportProduct);
    }

    @Override
    public void delete(String id) {
        exportProductDao.deleteByPrimaryKey(id);
    }

    @Override
    public PageInfo<ExportProduct> findByPage(ExportProductExample example, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<ExportProduct> list = exportProductDao.selectByExample(example);
        return  new PageInfo<>(list);
    }

    @Override
    public List<ExportProduct> findByExportId(ExportProductExample exportProductExample) {
        return exportProductDao.selectByExample(exportProductExample);
    }
}
