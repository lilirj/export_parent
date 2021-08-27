package com.itit.web.controller.system;

import com.alibaba.druid.util.StringUtils;
import com.github.pagehelper.PageInfo;
import com.itit.domain.system.Dept;
import com.itit.domain.system.Module;
import com.itit.service.system.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/system/module")
public class ModuleController extends BaseController {
    @Autowired
    private ModuleService moduleService;


    @RequestMapping("/list")
    /**
     *1.列表的分页查询
     */
    public String list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize, Model model){
        
        //调用service 进行分页
        PageInfo<Module> pageInfo = moduleService.findById(pageNum,pageSize);
        //存入域对象中
        model.addAttribute("pageInfo",pageInfo);
        //响应页面
        return "system/module/module-list";
    }

    /**
     * 跳转到添加页面
     * @return
     */
    @RequestMapping("/toAdd")
    public String toAdd(Model model){
        //调用service层 查询所有权限显示在下拉框
        List<Module> moduleList = moduleService.findAll();
        //保存数据到域中
        model.addAttribute("moduleList",moduleList);
        return "system/module/module-add";
    }

    /**
     * 添加权限信息
     * @param module
     * @return
     */
    @RequestMapping("/edit")
    public String edit(Module module){

        // 添加 修改的参数与 返回页面 一致一起写  根据id判断 添加没有id
        if (StringUtils.isEmpty(module.getId())){
            //为空 调用service的添加权限
            moduleService.save(module);
        }else {
            //修改权限
            moduleService.update(module);
        }

        return "redirect:/system/module/list";
    }

    /**
     * 删除权限信息
     * @param moduleId
     * @return
     */
    @RequestMapping("/delete")
    public String delete(String moduleId){
        //调用service的删除方法
        moduleService.delete(moduleId);
        return "redirect:/system/module/list";
    }

    /**
     * 跳转到修改页面 先回显数据
     * @return
     */
    @RequestMapping("/toUpdate")
    public String toUpdate(String id,Model model){

        //调用 回显权限数据
        Module module = moduleService.findByModuleId(id);
        //查询所有的权限 显示在下拉框
        List<Module> moduleList = moduleService.findAll();

        //保存
        model.addAttribute("module",module);
        model.addAttribute("moduleList",moduleList);

        return "system/module/module-update";
    }


}
