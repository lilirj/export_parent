package com.itit.service.cargo.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itit.dao.cargo.ContractDao;
import com.itit.dao.cargo.ContractProductDao;
import com.itit.dao.cargo.ExtCproductDao;
import com.itit.domain.cargo.*;
import com.itit.service.cargo.ContractProductService;
import com.itit.service.cargo.ContractService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@Service(timeout = 1000000)
public class ContractProductServiceImpl implements ContractProductService {

    @Autowired
    private ContractProductDao contractProductDao;
    @Autowired
    private ContractDao contractDao;
    @Autowired
    private ExtCproductDao extCproductDao;

    @Override
    public PageInfo<ContractProduct> findByPage(ContractProductExample contractProductExample, int pageNum, int pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<ContractProduct> list = contractProductDao.selectByExample(contractProductExample);
        PageInfo<ContractProduct> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    @Override
    public List<ContractProduct> findAll(ContractProductExample contractProductExample) {
        return contractProductDao.selectByExample(contractProductExample);
    }

    @Override
    public ContractProduct findById(String id) {
        return contractProductDao.selectByPrimaryKey(id);
    }

    @Override
    public void save(ContractProduct contractProduct) {
        //1.计算货物的价格
        //1.1 初始化 货物总价为0  防止后续计算出错
        Double amount=0d;
        if (contractProduct.getCnumber()!= null && contractProduct.getPrice()!=null){
            amount=contractProduct.getCnumber()*contractProduct.getPrice();
            contractProduct.setAmount(amount);
        }

        //2.联动修改购销合同总金额  货物数量
        //2.1 根据购销合同查询购销合同对象
        Contract contract = contractDao.selectByPrimaryKey(contractProduct.getContractId());
        //2.2 设置参数 修改总金额 = 原来的总金额 + 货物金额
        contract.setTotalAmount(contract.getTotalAmount()+amount);
        //2.3 修改货物数量 = 原来的货物数量+1
        contract.setProNum(contract.getProNum()+1);
        //2.4 将修改保存到数据中  修改购销合同
        contractDao.updateByPrimaryKeySelective(contract);

        //3.添加货物
        contractProduct.setId(UUID.randomUUID().toString());
        contractProductDao.insertSelective(contractProduct);
    }

    @Override
    public void update(ContractProduct contractProduct) {
        //1.计算修改后的总价
        Double amount=0d;
        if (contractProduct.getCnumber()!=null && contractProduct.getPrice()!=null){
            amount=contractProduct.getCnumber()*contractProduct.getPrice();
            contractProduct.setAmount(amount);
        }

        //2.查询修改前的货物总价 （数据库中的货物总价）
        //2.1 根据货物id查询货物对象
        ContractProduct dbContractProduct = contractProductDao.selectByPrimaryKey(contractProduct.getId());
        //2.2获取货物金额
        Double dbAmount = dbContractProduct.getAmount();


        //3.联动修改  购销合同的总价=原来购销合同的总价-修改前货物的价格+修改后货物的价格
        Contract contract = contractDao.selectByPrimaryKey(contractProduct.getContractId());
        //3.1 设置购销的总价
        contract.setTotalAmount(contract.getTotalAmount()-dbAmount+amount);
        //3.2设置到数据库中 修改购销合同
        contractDao.updateByPrimaryKeySelective(contract);

        //4.修改货物
        contractProductDao.updateByPrimaryKeySelective(contractProduct);
    }

    /**
     * 需求：删除货物
     *
     * |-- 购销合同
     *    |-- 货物       删除货物，同时把货物的附件也删除；还要累加附件的金额。
     *       |-- 附件1
     *       |-- 附件2
     */
    @Override
    public void delete(String id) {
        //1.查询货物的总金额
        ContractProduct contractProduct = contractProductDao.selectByPrimaryKey(id);
        Double amount = contractProduct.getAmount();

        //2.查询货物对应所有附件累加的金额
        //2.1根据货物id 查询货物下的所有附件
        //构造 查询附件对象
        ExtCproductExample extCproductExample = new ExtCproductExample();
        //添加查询条件
        extCproductExample.createCriteria().andContractProductIdEqualTo(id);
        //查询货物下的所有附件
        List<ExtCproduct> extCproductlist = extCproductDao.selectByExample(extCproductExample);
        //2.2遍历附件 累加附件的金额
        Double extCproductAmounts=0d;
        if (extCproductlist !=null && extCproductlist.size()>0){
            for (ExtCproduct extCproduct : extCproductlist) {
                //累加附件的金额
                extCproductAmounts+=extCproduct.getAmount();
                //4.根据货物id删除 附件
                extCproductDao.deleteByPrimaryKey(extCproduct.getId());
            }
        }

        //3.联动修改 购销合同的  总金额 = 原购销合同金额-货物金额-货物下所有附件的金额   货物数量  附加数量
        Contract contract = contractDao.selectByPrimaryKey(contractProduct.getContractId());
        //3.1设置购销合同金额
        contract.setTotalAmount(contract.getTotalAmount()-amount-extCproductAmounts);
        //3.2设置购销合同的货物数量
        contract.setProNum(contract.getProNum()-1);
        //3.3设置附件数量  原来的附件数-货物下的附件数
        contract.setExtNum(contract.getExtNum()-extCproductlist.size());
        //3.4 修改到数据库  修改购销合同
        contractDao.updateByPrimaryKeySelective(contract);

        //5.删除货物
        contractProductDao.deleteByPrimaryKey(id);
    }
}
