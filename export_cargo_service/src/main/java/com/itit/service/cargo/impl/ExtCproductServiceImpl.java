package com.itit.service.cargo.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itit.dao.cargo.ContractDao;
import com.itit.dao.cargo.ExtCproductDao;
import com.itit.domain.cargo.Contract;
import com.itit.domain.cargo.ExtCproduct;
import com.itit.domain.cargo.ExtCproductExample;
import com.itit.service.cargo.ExtCproductService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@Service  //dubbo注解 发布服务
public class ExtCproductServiceImpl implements ExtCproductService {

    @Autowired
    private ExtCproductDao extCproductDao;
    @Autowired
    private ContractDao contractDao;

    @Override
    public PageInfo<ExtCproduct> findByPage(ExtCproductExample extCproductExample, int pageNum, int pageSize) {
        //开启分页
        PageHelper.startPage(pageNum,pageSize);
        List<ExtCproduct> list = extCproductDao.selectByExample(extCproductExample);
        return new PageInfo<>(list);
    }

    @Override
    public List<ExtCproduct> findAll(ExtCproductExample extCproductExample) {
        return extCproductDao.selectByExample(extCproductExample);
    }

    @Override
    public ExtCproduct findById(String id) {
        return extCproductDao.selectByPrimaryKey(id);
    }

    @Override
    public void save(ExtCproduct extCproduct) {

        //1.查询附件总金额
        Double amount = 0d;
        if (extCproduct.getCnumber() != null && extCproduct.getPrice() != null) {
            amount = extCproduct.getCnumber() * extCproduct.getPrice();
        }
        //1.2设置到附件中
        extCproduct.setAmount(amount);

        //2.修改购销合同总金额 及附件数量
        //2.1 根据合同id查询合同对象
        Contract contract = contractDao.selectByPrimaryKey(extCproduct.getContractId());
        //2.2设置合同总金额=原合作金额+附件金额
        contract.setTotalAmount(contract.getTotalAmount()+amount);
        //2.3设置合同的附件数量
        contract.setExtNum(contract.getExtNum()+1);
        //2.4修改购销合同
        contractDao.updateByPrimaryKeySelective(contract);

        //3.添加附件
        //3.1设置id
        extCproduct.setId(UUID.randomUUID().toString());
        extCproductDao.insertSelective(extCproduct);
    }

    @Override
    public void update(ExtCproduct extCproduct) {
        //1获得附件的总金额
        Double amount =0d;
        if(extCproduct.getCnumber()!= null && extCproduct.getPrice()!=null){
            amount=extCproduct.getCnumber()*extCproduct.getPrice();

        }
        //1.2将总金额设置到对象中
        extCproduct.setAmount(amount);

        //2获得原来的附件金额 数据库中的值
        //2.1根据附件id查询附件对象
        ExtCproduct dbextCproduct = extCproductDao.selectByPrimaryKey(extCproduct.getId());
        //2.2获得修改前的附件总金额
        Double dbamount = dbextCproduct.getAmount();

        //3.联动修改购销合同的总金额 = 原合同金额-修改前附件金额+新附件金额
        //3.1根据合同id 查询合同对象
        Contract contract = contractDao.selectByPrimaryKey(extCproduct.getContractId());
        //3.1设置合同的金额
        contract.setTotalAmount(contract.getTotalAmount()-dbamount+amount);
        //3.2 修改合同 将数据修改到数据库中
        contractDao.updateByPrimaryKeySelective(contract);

        //修改附件
        extCproductDao.updateByPrimaryKeySelective(extCproduct);
    }

    @Override
    public void delete(String id) {
        //1.查询附件的金额
        ExtCproduct extCproduct = extCproductDao.selectByPrimaryKey(id);
        Double amount = extCproduct.getAmount();

        //2.联动修改合同的总金额  及附件的数量
        //2.1根据id查询合同对象
        Contract contract = contractDao.selectByPrimaryKey(extCproduct.getContractId());
        //2.2设置合同总金额= 原合同金额-附件金额
        contract.setTotalAmount(contract.getTotalAmount()-amount);
        //2.3设置合同中的附件数量
        contract.setExtNum(contract.getExtNum()-1);
        //2.4修改购销合同
        contractDao.updateByPrimaryKeySelective(contract);

        //3.删除附件
        extCproductDao.deleteByPrimaryKey(id);
    }
}
