package com.itit.service.cargo.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itit.dao.cargo.*;
import com.itit.domain.cargo.*;
import com.itit.service.cargo.ExportService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import vo.ExportProductResult;
import vo.ExportResult;

import java.util.*;

@Service
public class ExportServiceImpl implements ExportService{

    // 注入报运单dao
    @Autowired
    private ExportDao exportDao;

    // 注入商品dao
    @Autowired
    private ExportProductDao exportProductDao;

    // 注入商品附件dao
    @Autowired
    private ExtEproductDao extEproductDao;

    //注入货物dao
    @Autowired
    private ContractProductDao contractProductDao;

    //注入货物附件dao
    @Autowired
    private ExtCproductDao extCproductDao;

    //注入购销合同dao
    @Autowired
    private ContractDao contractDao;



    @Override
    public Export findById(String id) {
        return exportDao.selectByPrimaryKey(id);
    }


    /**
     * 添加报运单：
     * 1、添加报运单信息 co_export
     * 2、添加报运的商品 co_export_product
     * 3、添加商品附件   co_ext_eproduct
     */
    @Override
    public void save(Export export) {

        //1. 设置报运单信息：id、创建时间、状态、货物数量、附件数量
        export.setId(UUID.randomUUID().toString());
        export.setCreateTime(new Date());
        export.setState(0);

        //2.获得购销合同id  报运单的contractIds属性 存储了购销合同id 以逗号隔开
        String[] contractIds = export.getContractIds().split(",");
        //后面查询货物可以直接使用
        List<String> idlist = Arrays.asList(contractIds);

        //5.设置map 绑定商品id 与 货物id
        // 定义map，保存货物id和商品id。 因为在添加商品附件时候，需要用商品id
        // key : 货物id;  value: 商品id
        Map<String,String> map=new HashMap<>();

        //3. 添加报运的商品 co_export_product
        //3.1 根据多个购销合同id查询货物    货物就是报运单商品的数据来源   数据搬家
        // select * from co_contract_product where contract_id in ("","")
        ContractProductExample cPExample = new ContractProductExample();
        cPExample.createCriteria().andContractIdIn(idlist);
        List<ContractProduct> cPlist = contractProductDao.selectByExample(cPExample);
        if (cPlist!=null && cPlist.size()>0){
            //3.2遍历所有的货物
            for (ContractProduct contractProduct : cPlist) {
                //3.3 货物是 商品的数据来源  进行数据搬家
                ExportProduct exportProduct = new ExportProduct();
                BeanUtils.copyProperties(contractProduct,exportProduct);
                //3.4设置商品的id
                exportProduct.setId(UUID.randomUUID().toString());
                //3.5设置商品的报运单id
                exportProduct.setExportId(export.getId());
                //3.6将商品保存到商品表中
                exportProductDao.insertSelective(exportProduct);

                //5.1绑定货物id 与商品id
                map.put(contractProduct.getId(),exportProduct.getId());

            }
        }

        //4.添加报运商品附件   货物附件就是报运单 商品附件的数据来源  数据搬家
        //4.1 根据购销合同，查询购销合同下的附件
        // select * from co_ext_cproduct where contract_id in ("","")
        //构建查询对象
        ExtCproductExample extCproductExample = new ExtCproductExample();
        //设置查询条件   （根据多个购销合同id 查询附件 使用in）
        extCproductExample.createCriteria().andContractIdIn(idlist);
        //查询货物附件
        List<ExtCproduct> extCproductList = extCproductDao.selectByExample(extCproductExample);
        if (extCproductList!=null && extCproductList.size()>0){
            //遍历所有货物附件
            for (ExtCproduct extCproduct : extCproductList) {
                //创建商品附件
                ExtEproduct extEproduct = new ExtEproduct();
                // 对象拷贝
                BeanUtils.copyProperties(extCproduct,extEproduct);  //数据搬家 将货物附件的值 封装到商品附件对应的属性中
                //设置商品附件的id
                extEproduct.setId(UUID.randomUUID().toString());
                //设置商品附件的报运单id
                extEproduct.setExportId(export.getId());

                //设置商品id
                //获得货物id
                String contractProductId = extCproduct.getContractProductId();
                //获得商品id
                String s = map.get(contractProductId);

                //设置商品id.var
                extEproduct.setExportProductId(s);

                //将商品附件 添加到商品附件表中
                extEproductDao.insertSelective(extEproduct);
            }
        }


        //6.设置报运单 商品数量 附件数量
        export.setProNum(cPlist.size());
        export.setExtNum(extCproductList.size());

        //7.遍历购销合同id
        //修改购销合同的状态  报运后 购销合同的状态因改为2 已报运
        for (String id : idlist) {
            //根据购销合同id 查询
            Contract contract = contractDao.selectByPrimaryKey(id);
            contract.setState(2);
            //更新到数据库中
            contractDao.updateByPrimaryKeySelective(contract);
        }

        //8.添加报运单
        exportDao.insertSelective(export);
    }

    @Override
    public void update(Export export) {   //要修改报运单 也要修改报运单列表
        //修改报运单
        exportDao.updateByPrimaryKeySelective(export);

        //修改商品
        //获得商品列表
        List<ExportProduct> exportProducts = export.getExportProducts();
        if (exportProducts!=null && exportProducts.size()>0){
            //遍历商品
            for (ExportProduct exportProduct : exportProducts) {
                //修改商品（商品id，页面隐藏域存储了值），动态sql
                exportProductDao.updateByPrimaryKeySelective(exportProduct);
            }
        }
    }

    @Override
    public void delete(String id) {
        exportDao.deleteByPrimaryKey(id);
    }

    @Override
    public PageInfo<Export> findByPage(ExportExample example, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Export> list = exportDao.selectByExample(example);
        return  new PageInfo<>(list);
    }

    //根据报运平台返回的ExportResult结果  修改报运单
    @Override
    public void updateExport(ExportResult exportResult) {
        //1.修改报运单状态、备注
        //1.1获得报运单id
        String exportId = exportResult.getExportId();
        //1.2创建对象 设置修改的字段、修改条件
        Export export = new Export();
        export.setId(exportId);
        export.setState(exportResult.getState());
        export.setRemark(exportResult.getRemark());
        //修改到数据库
        exportDao.updateByPrimaryKeySelective(export);

        //修改报运商品：交税金额
        //获得商品集合
        Set<ExportProductResult> products = exportResult.getProducts();
        if (products!=null && products.size()>0){
            //遍历商品集合
            for (ExportProductResult product : products) {
                //设置商品 id 交税金额
                ExportProduct exportProduct = new ExportProduct();
                exportProduct.setId(product.getExportProductId());
                exportProduct.setTax(product.getTax());
                //修改商品的交税金额
                exportProductDao.updateByPrimaryKeySelective(exportProduct);
            }
        }


    }
}
