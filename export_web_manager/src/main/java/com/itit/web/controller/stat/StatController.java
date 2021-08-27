package com.itit.web.controller.stat;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itit.service.stat.Stat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/stat")
public class StatController {

    @Reference
    private Stat stat;

    /**
     * 统计分析 显示图标数据
     */

    /**
     * 点击统计分析模块，进入对应的页面
     * 生产厂家销售统计：http://localhost:8080/stat/toCharts.do?chartsType=factory
     * 产品销售排行：   http://localhost:8080/stat/toCharts.do?chartsType=sell
     * 系统访问压力图： http://localhost:8080/stat/toCharts.do?chartsType=online
     */
    @RequestMapping("/toCharts")
    public String toCharts(String chartsType){
        return "/stat/stat-"+chartsType;
    }

    /**
     *ECharts的数据要求是json格式 且页面是异步提交 要求返回json格式
     * 生产厂家销售统计，返回页面需要的json数据
     */
    @RequestMapping("/getFactoryData")
    @ResponseBody
    public List<Map<String,Object>> getFactoryData(){
        List<Map<String, Object>> list = stat.getFactoryData();
        return list;
    }

    /**
     * 产品销售排行榜 前5
     * 访问地址：/stat/getProductData
     */
    @RequestMapping("/getProductData")
    @ResponseBody    //将返回的类型转换为json格式
    public List<Map<String,Object>> getProductData(){

        List<Map<String, Object>> list = stat.getProductData(5);
        return list;
    }

    /**
     * 按小时统计访问人数
     * 访问地址：/stat/getOnline
     */
    @RequestMapping("/getOnline")
    @ResponseBody
    public List<Map<String,Object>> getOnline(){
        return stat.getOnline();
    }






}
