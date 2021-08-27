package com.itit.web.controller.cargo;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.itit.dao.cargo.ExportDao;
import com.itit.domain.cargo.*;
import com.itit.domain.system.User;
import com.itit.service.cargo.ContractService;
import com.itit.service.cargo.ExportProductService;
import com.itit.service.cargo.ExportService;
import com.itit.web.controller.system.BaseController;
import com.itit.web.util.BeanMapUtils;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.beanutils.BeanMap;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vo.ExportProductVo;
import vo.ExportVo;

import javax.servlet.ServletOutputStream;
import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.util.*;

@Controller
@RequestMapping("/cargo/export")
public class ExportController extends BaseController {

    @Reference
    private ContractService contractService;
    @Reference
    private ExportService exportService;
    @Reference
    private ExportProductService exportProductService;



    /**
     * 跳转到合同管理页面 只显示状态为1 表示已上报的购销合同
     * 访问地址：http://localhost:8080/cargo/export/contractList.do
     */
    @RequestMapping("/contractList")
    public String contractList(@RequestParam(defaultValue = "1") Integer pageNum,
                               @RequestParam(defaultValue = "5") Integer pageSize){

        //1.构建查询条件对象
        ContractExample contractExample = new ContractExample();
        contractExample.setOrderByClause("create_time desc"); //根据创建时间降序排序

        ContractExample.Criteria criteria = contractExample.createCriteria();
        //添加条件跟根据企业id查询
        criteria.andCompanyIdEqualTo(getLoginCompanyId());
        //根据状态查询购销合同  状态为1 表示已上报 待报运
        criteria.andStateEqualTo(1);

        //2.调用service查询状态为1的购销合同 分页显示
        PageInfo<Contract> pageInfo = contractService.findByPage(contractExample, pageNum, pageSize);

        //3.保存结果
        request.setAttribute("pageInfo",pageInfo);

        //4.转发到页面
        return "cargo/export/export-contractList";
    }


    /**
     * 报运单列表 分页查询
     * 访问地址：/cargo/export/list.do
     */
    @RequestMapping("/list")
    public String list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize){
        //构建查询对象
        ExportExample exportExample = new ExportExample();
        //// 排序：根据创建时间降序排序
        exportExample.setOrderByClause("create_time desc");
        //添加查询条件 根据企业id查询
        exportExample.createCriteria().andCompanyIdEqualTo(getLoginCompanyId());

        //1.查询报运单列表
        PageInfo<Export> pageInfo = exportService.findByPage(exportExample, pageNum, pageSize);
        //2.保存数据
        request.setAttribute("pageInfo",pageInfo);
        //3.转发到报运列表
        return "/cargo/export/export-list";
    }

    /**
     * 合同管理 点击报运
     * 访问地址：/cargo/export/toExport.do
     * 参数 id 多个同名参数 使用字符串或数组接收
     */
    @RequestMapping("/toExport")
    public String toExport(String id){
        request.setAttribute("id",id);
        return "/cargo/export/export-toExport";
    }

    /**
     * 点击保存，生成报运单
     * 访问地址： /cargo/export/edit.do
     * 添加报运对象
     */
    @RequestMapping("/edit")
    public String edit (Export export){
        //设置企业id
        export.setCompanyId(getLoginCompanyId());
        export.setCompanyName(getLoginCompanyNmae());

        if (StringUtils.isEmpty(export.getId())){
            //添加报运单
            exportService.save(export);
        }else {
            //修改报运单
            exportService.update(export);
        }

        return "redirect:/cargo/export/list";
    }

    /**
     * 报运列表 点击编辑
     * 访问地址：${ctx }/cargo/export/toUpdate.do?id=${o.id}
     * 参数 ${o.id}  报运单id
     * 回显数据  报运单数据 报运单下的商品数据
     */
    @RequestMapping("/toUpdate")
    public String toUpdate(String id){

        //根据报运单id 查询报运单
        Export export = exportService.findById(id);

        //根据报运单id 查询报运单下的所有商品
        //构建查询对象
        ExportProductExample exportProductExample = new ExportProductExample();
        exportProductExample.createCriteria().andExportIdEqualTo(id);
        List<ExportProduct> eps= exportProductService.findByExportId(exportProductExample);

        //保存结果
        request.setAttribute("eps",eps);
        request.setAttribute("export",export);

        return "/cargo/export/export-update";
    }

    /**
     * 5. 报运单提交、取消  修改报运单状态
     * http://localhost:8080/cargo/export/submit.do?id=7
     * http://localhost:8080/cargo/export/cancel.do?id=7
     */
    @RequestMapping("/submit")
    public String submit(String id){
        //提交修改 报运单状态为1 表示提交
        //根据id查询报运单对象
        Export export = exportService.findById(id);
        //修改状态为1
        export.setState(1);
        //更新到数据库中
        exportService.update(export);
        return "redirect:/cargo/export/list";
    }

    @RequestMapping("/cancel")
    public String cancel(String id){
        //提交修改 报运单状态为0 表示取消提交  状态0显示草稿状态
        //创建报运单对象
        Export export = new Export();
        //修改状态为0
        export.setId(id);
        export.setState(0);
        //更新到数据库中
        //因为使用了逆向工程所以update会动态更新sql，对象属性有值才生成更新的字段
        exportService.update(export);
        return "redirect:/cargo/export/list";
    }

    /**
     * 报运列表点击  电子报运
     * 访问地址：/cargo/export/exportE.do?id=1
     */
    @RequestMapping("/exportE")
    public String exportE(String id){
        //1.封装报运单信息
        //1.1根据报运单id查询报运单对象
        Export export = exportService.findById(id);
        //1.2对象拷贝
        ExportVo exportVo = new ExportVo();
        BeanUtils.copyProperties(export,exportVo);
        //1.3 设置报运单id
        exportVo.setExportId(id);

        //2封装报运商品信息
        //2.1获得报运单下的商品集合
        //2.2构建查询对象 添加查询条件  根据报运单id 查询商品
        ExportProductExample exportProductExample = new ExportProductExample();
        exportProductExample.createCriteria().andExportIdEqualTo(id);
        //2.3 查询商品集合
        List<ExportProduct> exportProductList = exportProductService.findByExportId(exportProductExample);
        //3.定义要封装的商品集合
        List<ExportProductVo> exportProductVos = new ArrayList<>();
        //判断
        if (exportProductList!=null && exportProductList.size()>0){
            for (ExportProduct exportProduct : exportProductList) {
                //1.2.4 创建webservice请求需要的报运单的商品对象、并封装、添加到集合
                ExportProductVo exportProductVo = new ExportProductVo();
                BeanUtils.copyProperties(exportProduct,exportProductVo); //对象拷贝
                //设置报运单id
                exportProductVo.setExportId(id);
                //设置商品id
                exportProductVo.setExportProductId(exportProduct.getId());
                //添加到集合
                exportProductVos.add(exportProductVo);
            }
        }
        //
        exportVo.setProducts(exportProductVos);

        //3.电子报运

        //4.报运成功查询报运的结果


        //5.根据返回的结果修改数据库
        //5.1修改报运单的状态（模拟）
        //根据据返回的报运单id 修改状态
        Export export1 = new Export();
        export1.setId(id);
        export1.setState(2); //状态2 表示已报运
        //修改报运单
        exportService.update(export1);
        //5.2修改商品的交税金额


        return "redirect:/cargo/export/list";
    }

    /**
     * PDF形式下载报运单   jasperRep框架
     *访问地址：/cargo/export/exportPdf.do?id=1
     */
    @Autowired
    private DataSource dataSource;
    @RequestMapping("/exportPdf")
    public void exportPdf(String id) throws Exception {

        //1.读取模板
        InputStream inputStream =
                sessione.getServletContext().getResourceAsStream("/jsaper/export.jasper");

        //封装模板的数据
        //A.根据id查询报运单信息
        Export export = exportService.findById(id);
        /*定义集合，往模板中填充数据（1）map集合，封装报运单信息。模板中对应的是parameter的名称*/
        Map<String, Object> map = BeanMapUtils.beanToMap(export);

        //B.根据报运单id 查询报运单下所有的商品信息
        ExportProductExample exportProductExample = new ExportProductExample();
        exportProductExample.createCriteria().andExportIdEqualTo(id);
        List<ExportProduct> list = exportProductService.findByExportId(exportProductExample);

        //构建数据源对象  构造一个javbean数据源对象，
        JRBeanCollectionDataSource jrDataSource = new JRBeanCollectionDataSource(list);

        //2.创建jasperPrint对象，用于往模板中添加数据
        JasperPrint jasperPrint =
                JasperFillManager.fillReport(inputStream,map,jrDataSource);  //dataSource.getConnection()  数据库连接


        //3.导出PDF 并下载
        //设置编码
        response.setCharacterEncoding("UTF-8");
        //3.1获得response输出流
        ServletOutputStream outputStream = response.getOutputStream();
        //3.2设置下载响应头
        response.setHeader("content-disposition","attachment;fileName=export.pdf");
        //3.2导出pdf
        JasperExportManager.exportReportToPdfStream(jasperPrint,outputStream);
        inputStream.close();
        outputStream.close();
    }





}

/* exportPdf

        //创建map 封装数据 (使用map填充数据)   jasper中设置了属性  使用map封装 对应jasper中的kep
        HashMap<String, Object> map = new HashMap<>();
        map.put("userName","露丝");
        map.put("email","23567@qq.com");
        map.put("deptName","开发部");
        map.put("companyName","不会画出版社");
        */

        /*//创建list集合  （使用JavaBean 数据源填充数据）    jasper中的fields接收数据源  参数与list集合中的对象属性一致
        List<User> list = new ArrayList<>();
        for (int g=0;g<=2;g++) {
            for (int i = 0; i <= 6; i++) {
                User user = new User();
                user.setEmail("23567@qq.com");
                user.setUserName("路西");
                user.setCompanyName("好事花生"+g);
                user.setDeptName("设计部");
                list.add(user);
            }
        }*/
