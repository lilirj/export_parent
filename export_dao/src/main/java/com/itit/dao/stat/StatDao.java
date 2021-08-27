package com.itit.dao.stat;

import java.util.List;
import java.util.Map;

public interface StatDao {

    /**
     * 需求1：统计生产商家销售金额
     * @return 一行数据封装一个map，key就是查询列名称，value是列的值
     */
    List<Map<String,Object>> getFactoryData();

    /**
     * 需求2：产品销售排行前5
     */
    List<Map<String,Object>> getProductData(int top);

    /**
     * 需求3： 按小时统计系统的访问人数
     */
    List<Map<String,Object>> getOnline();
}
