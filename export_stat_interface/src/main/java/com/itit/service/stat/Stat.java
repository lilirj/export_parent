package com.itit.service.stat;

import java.util.List;
import java.util.Map;

public interface Stat {

    /**
     * 需求1：统计生产商家销售金额
     */
    List<Map<String,Object>> getFactoryData();

    /**
     * 需求2：产品销售排行前5
     */
    List<Map<String,Object>> getProductData(int top);

    /**
     * 需求3：按小时统计访问人数，通过折线图显示
     */
    List<Map<String,Object>> getOnline();
}
