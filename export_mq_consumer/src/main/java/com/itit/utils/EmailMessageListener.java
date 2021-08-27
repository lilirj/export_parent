package com.itit.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class EmailMessageListener implements MessageListener {
    // 工具类实现对象与消息的转换
    private static final ObjectMapper MAPPER= new ObjectMapper();  //对象映射器

    @Override
    public void onMessage(Message message) {
        try {

            // 根据消息内容，获取消息的节点对象
            JsonNode jsonNode = MAPPER.readTree(message.getBody());

            // 根据key获取消息内容
            String email = jsonNode.get("email").asText();
            String content = jsonNode.get("content").asText();
            String title = jsonNode.get("title").asText();

            // 打印测试
            System.out.println("email: " + email);
            System.out.println("content: " + content);
            System.out.println("title: " + title);

            // 发送邮件
            MailUtil.sendMsg(email,title,content);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
