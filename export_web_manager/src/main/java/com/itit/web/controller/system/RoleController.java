package com.itit.web.controller.system;

import com.alibaba.druid.util.StringUtils;
import com.github.pagehelper.PageInfo;
import com.itit.domain.system.Module;
import com.itit.domain.system.Role;
import com.itit.service.system.ModuleService;
import com.itit.service.system.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/system/role")
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private ModuleService moduleService;


    @RequestMapping("/list")
    /**
     *1.列表的分页查询
     */
    public String list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize, Model model) {
        //设置企业id
        String companyId = getLoginCompanyId();

        //调用service 进行分页
        PageInfo<Role> pageInfo = roleService.findAll(companyId, pageNum, pageSize);

        //存入域对象中
        model.addAttribute("pageInfo", pageInfo);

        //响应页面
        return "system/role/role-list";
    }

    /**
     * 跳转到添加页面
     *
     * @return
     */
    @RequestMapping("/toAdd")
    public String toAdd() {
        return "system/role/role-add";
    }

    /**
     * 添加角色信息
     *
     * @param role
     * @return
     */
    @RequestMapping("/edit")
    public String edit(Role role) {
        //设置企业id
        String companyId = getLoginCompanyId();
        //设置企业名称
        String companyName = getLoginCompanyNmae();
        //保存
        role.setCompanyId(companyId);
        role.setCompanyName(companyName);

        // 添加 修改的参数与 返回页面 一致一起写  根据id判断 添加没有id
        if (StringUtils.isEmpty(role.getId())) {
            //为空 调用service的添加角色
            roleService.save(role);
        } else {
            //修改角色
            roleService.update(role);
        }
        return "redirect:/system/role/list";
    }

    /**
     * 删除角色信息
     *
     * @param id
     * @return
     */
    @RequestMapping("/delete")
    public String delete(String id) {
        roleService.delete(id);
        return "redirect:/system/role/list";
    }

    /**
     * 跳转到修改页面 先回显数据
     *
     * @return
     */
    @RequestMapping("/toUpdate")
    public String toUpdate(String id, Model model) {
        //调用 回显角色数据
        Role role = roleService.findByRoleId(id);
        //保存
        model.addAttribute("role", role);
        return "system/role/role-update";
    }

    /**
     * 6. 角色分配权限 (1) 进入角色分配权限页面
     * 功能入口：role-list.jsp点击权限
     * 请求地址：/system/role/roleModule
     * 请求参数：roleId 角色id
     * 响应地址: /WEB-INF/pages/system/role/role-module.jsp
     */
    @RequestMapping("/roleModule")
    public String roleModule(String roleid, Model model) {
        //根据id查询角色的信息
        Role role = roleService.findByRoleId(roleid);
        //保存
        model.addAttribute("role", role);
        //跳转页面
        return "system/role/role-module";
    }


    /**
     * 7. 角色分配权限 (2) role-module.jsp页面异步请求，返回json格式数据
     * 功能入口：role-module.jsp发送异步请求
     * 请求地址：/system/role/getZtreeNodes
     * 请求参数：roleId
     * 响应数据：[
     * { id:2, pId:0, name:"随意勾选 2", checked:true, open:true}
     * ]
     */
    @RequestMapping("/getZtreeNodes")
    @ResponseBody
    public List<Map<String, Object>> getZtreeNodes(String roleId) {

        // 1. 返回的集合
        List<Map<String, Object>> result = new ArrayList<>();
        //2.查询所有的权限
        List<Module> moduleList = moduleService.findAll();
        //3.查询角色拥有的权限
        List<Module> roleModule = moduleService.findModuleByRoleId(roleId);

        //4.遍历所有的权限
        for (Module module : moduleList) {

            Map<String, Object> map = new HashMap<>();
            map.put("id", module.getId());
            map.put("pId", module.getParentId());
            map.put("name", module.getName());
            map.put("open", true);

            //判断当前角色已经拥有的权限，是否包含遍历的结果，如果包含 就选中
            if (roleModule.contains(module)) {
                map.put("checked",true);
            }

            //添加到list<Map<String,Obj>>集合中
            result.add(map);
        }
        return result;
    }

    /**
     * 8. 角色分配权限 (3) role-module.jsp页面点击保存
     * 请求地址：/system/role/updateRoleModule
     * 请求参数：
     *     roleId  角色id
     *     moduleIds 权限id，多个权限id用逗号隔开
     * 响应地址：
     *     重定向到角色列表
     */
    @RequestMapping("/updateRoleModule")
    public String updateRoleModule(String roleid,String moduleIds){
        //调用service添加角色权限
        roleService.updateRoleModule(roleid,moduleIds);
        return "redirect:/system/role/list";

    }
}
