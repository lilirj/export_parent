package com.itit.web.controller.cargo;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.itit.domain.cargo.*;
import com.itit.service.cargo.ContractService;
import com.itit.service.cargo.ExtCproductService;
import com.itit.service.cargo.FactoryService;
import com.itit.web.controller.system.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/cargo/extCproduct")
public class ExtCproductController extends BaseController {

    @Reference
    private FactoryService factoryService;
    @Reference
    private ExtCproductService extCproductService;
    @Reference
    private ContractService contractService;

    /**
     * 点击货物列表的中附件
     * 访问地址：http://localhost:8080/cargo/extCproduct/list.do?contractId=f&contractProductId=6
     * 请求参数：contractId=f&contractProductId=6
     * 去到附件列表页面
     * */
    @RequestMapping("/list")
    public String list(String contractId, String contractProductId,
                       @RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(defaultValue = "5") Integer pageSize){

        //1.查询所有附件的产家
        //1.1构建工厂的查询对象
        FactoryExample factoryExample = new FactoryExample();
        //1.2添加条件  根据类型为附件查询
        factoryExample.createCriteria().andCtypeEqualTo("附件");
        //1.3查询附件厂家
        List<Factory> factoryList = factoryService.findAll(factoryExample);

        //2.根据货物id查询 附件列表
        //2.1构建附件查询对象
        ExtCproductExample extCproductExample = new ExtCproductExample();
        //2.2添加条件 根据货物ID查询 货物下的附件
        extCproductExample.createCriteria().andContractProductIdEqualTo(contractProductId);
        //2.3分页查询附件列表
        PageInfo<ExtCproduct> pageInfo = extCproductService.findByPage(extCproductExample, pageNum, pageSize);

        //保存到域中
        request.setAttribute("factoryList",factoryList);
        request.setAttribute("pageInfo",pageInfo);
        request.setAttribute("contractId",contractId);
        request.setAttribute("contractProductId",contractProductId);

        return "/cargo/extc/extc-list";
    }

    /**
     * 保存附件    保存 修改 传入参数与返回的页面一致 controller写在一起 通过id区分
     * 访问地址：${ctx}/cargo/extCproduct/edit.do
     * 请求参数 ExtCproduct
     * 跳转到附件列表页面
     */
    @RequestMapping("/edit")
    public String edit(ExtCproduct extCproduct){
        //1.设置企业id 企业名称
        extCproduct.setCompanyId(getLoginCompanyId());
        extCproduct.setCompanyName(getLoginCompanyNmae());

        //2.判断是修改还是删除
        if (StringUtils.isEmpty(extCproduct.getId())){
            //添加附件
            extCproductService.save(extCproduct);
        }else {
            //修改附件
            extCproductService.update(extCproduct);
        }

        //3.返回 附件列表
        return "redirect:/cargo/extCproduct/list?contractId="+extCproduct.getContractId()
                +"&contractProductId="+extCproduct.getContractProductId();

    }

    /**
     * 附件列表页面 点击修改
     * 访问地址：http://localhost:8080/cargo/extCproduct/toUpdate.do?id=c&contractId=9&contractProductId=e
     * 跳转到修改页面  需要回显数据  下拉框需要所有附件厂家
     */
    @RequestMapping("/toUpdate")
    public String toUpdate (String id,String contractId,String contractProductId){
        //查询附件厂家
        //1.1构建工厂的查询对象
        FactoryExample factoryExample = new FactoryExample();
        //1.2添加条件  根据类型为附件查询
        factoryExample.createCriteria().andCtypeEqualTo("附件");
        //1.3查询附件厂家
        List<Factory> factoryList = factoryService.findAll(factoryExample);

        //2.根据id查询附件
        ExtCproduct extCproduct = extCproductService.findById(id);


        //3.保存查询结果
        request.setAttribute("extCproduct",extCproduct);
        request.setAttribute("factoryList",factoryList);
        //保存合同id 货物id
        request.setAttribute("contractId",contractId);
        request.setAttribute("contractProductId",contractProductId);
        //4.转发到修改页面
        return "/cargo/extc/extc-update";
    }

    /**
     * 附件列表页面 点击删除
     * 访问地址：http://localhost:8080/cargo/extCproduct/delete.do?id=a&contractId=2&contractProductId=b
     * 重定向到附件列表页面
     */
    @RequestMapping("/delete")
    public String delete(String id,String contractId,String contractProductId){
        extCproductService.delete(id);
        return "redirect:/cargo/extCproduct/list?contractId="+contractId+"&contractProductId="+contractProductId;

    }

}
