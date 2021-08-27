import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class StatProvider {

    /**
     * 基于main方法启动提供者
     */
    public static void main(String[] args) throws IOException {

        //1.加载配置文件
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("classpath*:spring/applicationContext-*.xml");
        //2.启动
        context.start();
        //3.输入后停止
        System.in.read();
    }




}
