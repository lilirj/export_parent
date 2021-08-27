package com.itit.web.controller.system;

import com.alibaba.druid.util.StringUtils;
import com.github.pagehelper.PageInfo;
import com.itit.domain.system.Dept;
import com.itit.service.system.DeptService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/system/dept")
public class DeptController extends BaseController{

    //注入service
    @Autowired
    private DeptService deptService;

    /**
     * * 1. 部门列表分页查询
      请求地址 /system/dept/list
      请求参数
     *     @param pageNum  单前页
     *     @param pageSize 页面大小
     *     @param model    存储返回的数据
     *响应地址
     * @return system/dept/dept-list
     */

    @RequestMapping("/list")
    @RequiresPermissions("部门管理")  /*注解的方式实现shiro授权*/
    public String list( @RequestParam(defaultValue = "1") Integer pageNum,
                        @RequestParam(defaultValue = "5") Integer pageSize,
                        Model model){

        //设置企业的id 暂时写死
        String companyid=getLoginCompanyId();

        // 调用service分页查询，参数1：企业id；参数2:当前页； 参数3：页大小
        PageInfo<Dept> pageInfo = deptService.findByCompany(companyid, pageNum, pageSize);

        // 保存查询结果 (页面如何获取查询的当前页数据？${pageInfo.list})
        model.addAttribute("pageInfo",pageInfo);
        return "system/dept/dept-list";
    }

    /**
     * 2.部门添加 1）进入添加页面
     * 请求地址 /system/dept/toAdd
     * 响应参数  所有的部门信息（显示到下拉框） 筛选条件企业id
     * 响应地址  /WEB-INF/pages/system/dept/dept-add.jsp   视图解析器配置了前缀  /WEB-INF/pages/   后缀.jsp
     * @return
     */
    @RequestMapping("/toAdd")
    public String toAdd(Model model){

        //设置企业id
        String companyId=getLoginCompanyId();

        //调用service 查询所有部门信息
        List<Dept> deptList=deptService.findAll(companyId);

        //保存数据
        model.addAttribute("deptList",deptList);
        return "system/dept/dept-add";

    }

    /**
     * 3.部门添加 2）点击保存添加部门信息
     * 请求入口：dept-add.jsp点击保存
     * 请求参数  请求体中的数据   前端传入的数据 与参数对象的属性一致就可以自动封装 请求参数
     * @return
     */
    @RequestMapping("/edit")
    public String edit(Dept dept){

        // 企业id、名称从登陆用户那里获取 先写死
        String companyId=getLoginCompanyId();
        String companyName =getLoginCompanyNmae();
        dept.setCompanyId(companyId);
        dept.setCompanyName(companyName);

        //调用servic的添加方法 （添加方法保存数据 修改跟添加差不多请求参数与返回页面都一致  所以写在一起）
        if(StringUtils.isEmpty(dept.getId())){
            //id为空说明是添加方法
            deptService.save(dept);
        }else {
            //id不为空说明是修改方法
            deptService.update(dept);
        }

        return "redirect:/system/dept/list";
    }


    /**
     * 4. 部门修改(1)进入修改页面
     * 功能入口：dept-list.jsp点击编辑
     * 请求地址：/system/dept/toUpdate
     * 请求参数: id
     * 响应地址：/WEB-INF/pages/system/dept/dept-update.jsp
     */
    @RequestMapping("/toUpdate")
    public String toUpdate(String id,Model model){
        //设置企业id
        String companyId=getLoginCompanyId();

        // 1. 根据部门id查询  回显示数据
        Dept dept =deptService.findBydeptId(id);
        //2. 查询所有部门，作为下拉列表数据源
        List<Dept> deptList = deptService.findAll(companyId);
        //3.保存数据
        model.addAttribute("dept",dept);
        model.addAttribute("deptList",deptList);  //做为上级部门下拉框 的数据来源
        return "system/dept/dept-update";
    }

    /**
     * 功能入口 ：list页面的删除按钮
     * 响应页面 ：重定向到/system/dept/list请求 刷新页面
     * @param id  根据部门id删除数据
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Map<String,Object> delete(String id){
        //返回的数据
        Map<String, Object> map = new HashMap<>();
        //删除表数据需要注意 主外键约束 要判断主键是否被引用 如果为引用不能删除(搜索外键是否有对应的主键引用)
        boolean flag=deptService.deleById(id);
        if(flag){
            // 删除成功
            map.put("message","1");
        }else {
            map.put("message","0");
        }
        return map;
    }

}
