package com.itit.web.controller.cargo;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.itit.domain.cargo.ContractProduct;
import com.itit.domain.cargo.ContractProductExample;
import com.itit.domain.cargo.Factory;
import com.itit.domain.cargo.FactoryExample;
import com.itit.service.cargo.ContractProductService;
import com.itit.service.cargo.FactoryService;
import com.itit.web.controller.system.BaseController;
import com.itit.web.util.FileUploadUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 货物模块controller
 */
@Controller
@RequestMapping("/cargo/contractProduct")
public class ContractProductController extends BaseController {

    //使用dubbo注解 注入代理对象
    @Reference
    private ContractProductService contractProductService;
    @Reference
    private FactoryService factoryService;
    @Autowired
    private FileUploadUtil fileUploadUtil;

    /**
     * 1.  货物列表分页查询
     * 功能入口：购销合同列表点击“货物”
     * 请求地址：http://localhost:8080/cargo/contractProduct/list.do?contractId=1
     * 请求参数：contractId  购销合同id
     * 返回地址：/WEB/-INF/cargo/product/product-list.jsp
     *
     * 注意：需要根据购销合同id，查询货物列表
     * SELECT * FROM co_contract_product WHERE contract_id=''
     */
    @RequestMapping("/list")
    public String list(@RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(defaultValue = "5") Integer pageSize,
                       String contractId){

        //1.根据id 查询购销合同下的货物列表 分页展示
        //1.1构造查询对象
        ContractProductExample contractProductExample = new ContractProductExample();
        //1.2 添加查询条件
        ContractProductExample.Criteria criteria = contractProductExample.createCriteria();
        criteria.andContractIdEqualTo(contractId);
        //1.3分页查询
        PageInfo<ContractProduct> pageInfo =
                contractProductService.findByPage(contractProductExample, pageNum, pageSize);

        //2.查询货物厂家
        //2.1 构造厂家的查询条件对象
        FactoryExample factoryExample = new FactoryExample();
        //2.2添加查询条件
        factoryExample.createCriteria().andCtypeEqualTo("货物");
        List<Factory> factoryList = factoryService.findAll(factoryExample);


        //3.保存结果
        //3.1 保存货物列表
        request.setAttribute("pageInfo",pageInfo);
        //3.2 保存厂家
        request.setAttribute("factoryList",factoryList);
        //3.3 保存购销合同id（为什么？因为货物表co_contract_product要存储购销合同id）
        request.setAttribute("contractId",contractId);
        //4. 转发到货物的添加以及列表页面
        return "cargo/product/product-list";

    }


    /**
     * 3.  添加保存 / 修改保存
     */
    @RequestMapping("/edit")
    public String edit(MultipartFile productPhoto, ContractProduct contractProduct){
        //MultipartFile直接获取文件的信息 方法参数名称要于前端的文件域的name（请求参数的文件名称）一致

        //设置企业id 企业名称
        contractProduct.setCompanyId(getLoginCompanyId());
        contractProduct.setCompanyName(getLoginCompanyNmae());

        //判断是添加还是修改
        if(StringUtils.isEmpty(contractProduct.getId())){

            /* 文件上传到七牛云*/
            try {
                //fileUploadUtil.upload(productPhoto) 返回的是上传的域名+文件名（http://qxzp6h8f8.hn-bkt.clouddn.com/8CBBC746BCB547DC8BD091D95A81949C_Snipaste_2021-08-14_21-59-35.png）
                String fullFileUrl = "http://"+fileUploadUtil.upload(productPhoto);
                //将文件的路径保存到数据库中
                contractProduct.setProductImage(fullFileUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //添加
            contractProductService.save(contractProduct);
        }else {
            //有id 是修改
            contractProductService.update(contractProduct);
        }

        // 保存货物成功，需要重定向到货物列表, 传入contractId购销合同id
        return "redirect:/cargo/contractProduct/list?contractId="
                +contractProduct.getContractId();
    }

    /**
     * 3. 进入货物的修改页面
     * 请求地址：http://localhost:8080/cargo/contractProduct/toUpdate.do?id=d
     * 请求参数：id 货物id
     */
    @RequestMapping("/toUpdate")
    public String toUpdate(String id){
        //1.根据id查货物信息
        ContractProduct contractProduct = contractProductService.findById(id);

        //2.查询货物厂家
        //2.1 构造厂家的查询条件对象
        FactoryExample factoryExample = new FactoryExample();
        //2.2添加查询条件
        factoryExample.createCriteria().andCtypeEqualTo("货物");
        List<Factory> factoryList = factoryService.findAll(factoryExample);

        //3.结果保存的域中
        request.setAttribute("contractProduct",contractProduct);
        request.setAttribute("factoryList",factoryList);

        return "/cargo/product/product-update";
    }

    /**
     * 4. 删除
     * 请求地址：http://localhost:8080/cargo/contractProduct/delete.do?id=1&contractId=2
     * 请求参数：
     *      id          货物id
     *      contractId  购销合同，用于跳转
     */
    @RequestMapping("/delete")
    public String delete(String id,String contractId){
        contractProductService.delete(id);
        return "redirect:/cargo/contractProduct/list?contractId="+contractId;

    }

    /**
     * 货物列表页面  上传货物
     * 访问地址：http://localhost:8080/cargo/contractProduct/toImport.do?contractId=9
     * 参数：contractId
     * 跳转的上传货物页面
     */
    @RequestMapping("/toImport")
    public String toImport(String contractId){
        //注意购销合同id
        request.setAttribute("contractId",contractId);
        return "/cargo/product/product-import";
    }

    /**
     * 上传文件页面 点击上传
     * 访问地址：/cargo/contractProduct/import.do
     * 参数：
     * 重定向到跳转到购销合同列表
     */
    @RequestMapping("/import")
    public String importExcel(MultipartFile file,String contractId) throws IOException {

        //1.构建工作簿
        Workbook workbook=new XSSFWorkbook(file.getInputStream());
        //2.获得工作表
        Sheet sheet = workbook.getSheetAt(0);
        //3.获得总行数
        int rows = sheet.getPhysicalNumberOfRows();

        //4.循环读取表中数据    拿到总行数 遍历每一行  读取列中的数据封装到对象中 将货物对象添加到数据库中
        for (int i = 1; i < rows; i++) {

            //4.1 获取每一行  从第二行开始  （数据从第二行开始，第一行是表头）
            Row row = sheet.getRow(i);

            //4.2 创建货物对象封装数据
            ContractProduct cp = new ContractProduct();
            //注意是添加货物 需要购销合同id
            cp.setContractId(contractId);

            //4.3 获得行中的列值  设置对象中的值
            cp.setFactoryName(row.getCell(1).getStringCellValue());
            cp.setProductNo(row.getCell(2).getStringCellValue());
            cp.setCnumber((int) row.getCell(3).getNumericCellValue());
            cp.setPackingUnit(row.getCell(4).getStringCellValue());
            cp.setLoadingRate(row.getCell(5).getNumericCellValue()+"");
            cp.setBoxNum((int)row.getCell(6).getNumericCellValue());
            cp.setPrice((double)row.getCell(7).getNumericCellValue());
            cp.setProductDesc(row.getCell(8).getStringCellValue());
            cp.setProductRequest(row.getCell(9).getStringCellValue());

            //4.4 设置厂家id
            //根据厂家名称获得厂家id
            Factory factory = factoryService.findByFactoryName(cp.getFactoryName());
            if (factory!=null){
                cp.setFactoryId(factory.getId());
            }

            //4.5添加货物
            contractProductService.save(cp);
        }

        return "redirect:/cargo/contract/list";
    }

}
