package com.itit.service.cargo.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itit.dao.cargo.ContractDao;
import com.itit.domain.cargo.Contract;
import com.itit.domain.cargo.ContractExample;
import com.itit.service.cargo.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import vo.ContractProductVo;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service //引入dubbo提供的发布服务的注解
public class ContractServiceImpl implements ContractService {

    @Autowired
    private ContractDao contractDao;


    @Override
    public PageInfo<Contract> findByPage(ContractExample contractExample, int pageNum, int pageSize) {
        //分页查询
        PageHelper.startPage(pageNum,pageSize);
        List<Contract> list = contractDao.selectByExample(contractExample);
        return new PageInfo<>(list);
    }

    @Override
    public List<Contract> findAll(ContractExample contractExample) {
        return contractDao.selectByExample(contractExample);
    }

    @Override
    public Contract findById(String id) {
       return contractDao.selectByPrimaryKey(id);
    }

    @Override
    public void save(Contract contract) {
        //1. 设置id
        contract.setId(UUID.randomUUID().toString());
        //2. 设置一些默认值 因为如果用户不赋值 属性为null 后面计算会报错
        contract.setState(0);
        contract.setProNum(0);
        contract.setExtNum(0);
        contract.setTotalAmount(0d);
        //3. 设置创建时间
        contract.setCreateTime(new Date());
        contractDao.insertSelective(contract);
    }

    @Override
    public void update(Contract contract) {
        contractDao.updateByPrimaryKeySelective(contract);
    }

    @Override
    public void delete(String id) {
        contractDao.deleteByPrimaryKey(id);

    }

    @Override
    public List<ContractProductVo> findByShipTime(String inputDate, String companyId) {
        List<ContractProductVo> contractProductVoList = contractDao.findByShipTime(inputDate, companyId);
        return contractProductVoList;
    }
}
