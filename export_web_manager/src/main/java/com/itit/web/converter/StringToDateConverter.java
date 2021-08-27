package com.itit.web.converter;

import com.alibaba.druid.util.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 类型转换器，在封装请求参数时候，实现String转Date
 */

//自定义类型转换器  实现converter接口
@Component
public class StringToDateConverter implements Converter<String, Date> {
    @Override
    public Date convert(String s) {
        try {
            // 校验: org.springframework.util.StringUtils
            if (!StringUtils.isEmpty(s)){
                // 转换
                return new SimpleDateFormat("yyyy-MM-dd").parse(s);
            }
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

}
