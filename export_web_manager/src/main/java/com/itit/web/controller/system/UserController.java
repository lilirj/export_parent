package com.itit.web.controller.system;

import com.alibaba.druid.util.StringUtils;
import com.github.pagehelper.PageInfo;
import com.itit.domain.system.Dept;
import com.itit.domain.system.Role;
import com.itit.domain.system.User;
import com.itit.service.system.RoleService;
import com.itit.service.system.UserService;
import org.springframework.amqp.rabbit.connection.RabbitAccessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.lang.model.element.NestingKind;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/system/user")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;


    @RequestMapping("/list")
    /**
     *1.列表的分页查询
     */
    public String list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize, Model model){
        //设置企业id
        String companyId = getLoginCompanyId();

        //调用service 进行分页
        PageInfo<User> pageInfo = userService.findAll(companyId, pageNum, pageSize);

        //存入域对象中
        model.addAttribute("pageInfo",pageInfo);

        //响应页面
        return "system/user/user-list";
    }

    /**
     * 跳转到添加页面
     * @return
     */
    @RequestMapping("/toAdd")
    public String toAdd(Model model){
        /*设置企业id*/
        String companyId=getLoginCompanyId();
        //调用service层 查询所有部门
        List<Dept> deptList = userService.findAllDept(companyId);
        //保存数据到域中
        model.addAttribute("deptList",deptList);
        return "system/user/user-add";
    }

    /**
     * 添加用户信息
     * @param user
     * @return
     */
    //注入容器中 发送消息的模型对象
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @RequestMapping("/edit")
    public String edit(User user){
        //设置企业id
        String companyId=getLoginCompanyId();
        //设置企业名称
        String companyName=getLoginCompanyNmae();
        //保存
        user.setCompanyId(companyId);
        user.setCompanyName(companyName);

        // 添加 修改的参数与 返回页面 一致一起写  根据id判断 添加没有id
        if (StringUtils.isEmpty(user.getId())){
            //为空 调用service的添加用户
            userService.save(user);

            //添加成功 发送邮件
            //设置消息内容
            HashMap<String, Object> map = new HashMap<>();
            //map.put("email",user.getEmail()); 获得用户邮箱
            map.put("email","2307604247@qq.com");  //暂时用自己的邮箱模拟
            map.put("title","新员工入职欢迎邮件");
            map.put("content","你好，欢迎来到ITCASt，我们是一个有激情的团队!");
            //发送消息 到指定队列
            rabbitTemplate.convertAndSend("msg.email",map);

        }else {
            //修改用户
            userService.update(user);
        }
        return "redirect:/system/user/list";
    }

    /**
     * 删除用户信息
     * @param userId
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Map<String,Object> delete(String userId){
        //返回的数据
        HashMap<String, Object> map = new HashMap<>();
        //调用service的删除方法
        boolean  flag= userService.delete(userId);
        //判断结果
        if (flag){
            //删除成功
            map.put("message","1");
        }else {
            map.put("message","0");
        }
        //返回给异步请求
        return map;
    }

    /**
     * 跳转到修改页面 先回显数据
     * @return
     */
    @RequestMapping("/toUpdate")
    public String toUpdate(String id,Model model){
        //调用 回显用户数据
        User user = userService.findByUserId(id);
        //查询所有的部门 显示在下拉框
        List<Dept> deptList = userService.findAllDept(getLoginCompanyId());

        //保存
        model.addAttribute("user",user);
        model.addAttribute("deptList",deptList);

        return "system/user/user-update";
    }

    /**
     * 跳转给用户分配角色的页面
     * 功能入口：user-list.jsp点击角色
     * 请求地址：/system/user/roleList
     * 请求参数：id  用户id
     * 返回地址：/WEB-INF/pages/system/user/user-role.jsp
     */
    @RequestMapping("/roleList")
    public String roleList(String id) { //id UserId
        //调用service的方法
        //1.根据用户id获取用户对象
        User user = userService.findByUserId(id);
        //2.获得所有角色 企业id做为筛选条件
        List<Role> roleList=roleService.findAllRole(getLoginCompanyId());
        //3.查询用户拥有的角色
        List<Role> userRole= roleService.findRoleByUserId(id);
        //定义字符串存储 用户拥有的所有角色id 多个id用逗号隔开
        String userRoles="";
        //遍历用户拥有的角色
        if(userRole !=null && userRole.size()>0){
            for (Role role : userRole) {
                userRoles +=role.getId()+",";
            }
        }

        //保存结果到域中
        request.setAttribute("user",user);
        request.setAttribute("roleList",roleList);
        request.setAttribute("userRoles",userRoles);
        return "system/user/user-role";
    }

    /**
     * 7. 用户分配角色（2）user-role.jsp点击保存
     *      * 请求地址：/system/user/changeRole
     *      * 请求参数：
     *      *      userId  用户id
     *      *      roleIds 角色id，多个同名参数 (1.用字符串接收，自动以逗号隔开；2.数组接收)
     *      操作表：pe_role_user
     *      * 返回地址： 保存成功，重定向到用户列表
     */
    @RequestMapping("/changeRole")
    public String changeRole(String userid,String[] roleIds){  //多个同名参数使用数组接收会自动封装   也可以使用字符串接收会自动以逗号隔开
        //调用service，实现用户分配角色
        roleService.saveUserRole(userid,roleIds);
        return "redirect:/system/user/list";
    }
}
