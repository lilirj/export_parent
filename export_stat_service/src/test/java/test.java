import com.itit.dao.stat.StatDao;
import com.itit.service.stat.Stat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationContext-*.xml")

public class test {
    @Autowired
    private StatDao statDao;
    @Autowired
    private Stat stat;

    @Test
    public void test(){
        List<Map<String, Object>> factoryData = statDao.getFactoryData();
        System.out.println(factoryData);
    }

    @Test
    public void test11(){
        List<Map<String, Object>> factoryData = stat.getFactoryData();
        System.out.println(factoryData);
    }

}
