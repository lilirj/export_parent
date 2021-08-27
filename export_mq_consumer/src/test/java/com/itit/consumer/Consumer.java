package com.itit.consumer;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class Consumer {
    public static void main(String[] args) throws IOException {
        //加载容器
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("applicationContext-rabbitmq-consumer.xml");
        context.start();
        System.in.read();
    }

}
