package com.itit.web.controller.cargo;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.itit.domain.cargo.Contract;
import com.itit.domain.cargo.ContractExample;
import com.itit.service.cargo.ContractService;
import com.itit.web.controller.system.BaseController;
import com.itit.web.util.FileUploadUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vo.ContractProductVo;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/cargo/contract")
public class ContractController extends BaseController {

    @Reference // 引入dubbo提供的注解 注入接口代理对象
    private ContractService contractService;


    /**
     * 1.  列表分页查询
     */
    @RequestMapping("/list")
    public String list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize) {
        //1.1构造条件查询对象
        ContractExample contractExample = new ContractExample();
        //1.3排序：根据创建时间降序排序
        contractExample.setOrderByClause("create_time desc");  //create_time表中的字段  desc降序排序
        ContractExample.Criteria criteria = contractExample.createCriteria();
        //1.2添加条件：企业id
        criteria.andCompanyIdEqualTo(getLoginCompanyId());

        //4.细粒度权限控制
        /**
         * 细粒度权限控制
         * 1.普通用户登陆，degree=4，只能查看自己创建的购销合同
         * 2. 部门经理登陆，degree=3,查看本部门创建的购销合同
         * 3. 如果是大部门经理登陆，degeee=2，可以查看本部门以及所有子部门下的员工创建的购销合同
         */
        if (getlonginUser().getDegree() == 4) {
            //* 1.普通用户登陆 设置查询的条件 根据登陆用户id查询购销合同
            //SELECT * FROM co_contract WHERE create_by='登陆用户的id'
            criteria.andCreateByEqualTo(getlonginUser().getId());
        } else if (getlonginUser().getDegree() == 3) {
            //* 2. 部门经理登陆 设置查询条件 根据部门id查询购销合同
            // SELECT * FROM co_contract WHERE create_dept='登陆用户的部门id'
            criteria.andCreateDeptEqualTo(getlonginUser().getDeptId());
        }

        //2.远程调用方法 实现分页查询
        PageInfo<Contract> pageInfo =
                contractService.findByPage(contractExample, pageNum, pageSize);
        //2.2 结果保存到域中
        request.setAttribute("pageInfo", pageInfo);

        //3 转发到页面
        return "cargo/contract/contract-list";
    }


    /**
     * 2.  添加（1）进入添加页面
     */
    @RequestMapping("/toAdd")
    public String toAdd() {
        return "cargo/contract/contract-add";
    }


    /**
     * 3.  操作（2）添加保存 / 修改保存
     */
    @RequestMapping("/edit")
    public String edit(Contract contract) {
        //设置企业id 企业名称
        contract.setCompanyId(getLoginCompanyId());
        contract.setCompanyName(getLoginCompanyNmae());

        if (StringUtils.isEmpty(contract.getId())) {
            /* 记录创建人 以及创建人部门 用于细粒度的权限控制*/
            contract.setCreateBy(getlonginUser().getId());
            contract.setCreateDept(getlonginUser().getDeptId());

            //调用添加方法
            contractService.save(contract);
        } else {
            //调用修改的方法
            contractService.update(contract);
        }

        return "redirect:/cargo/contract/list";
    }

    /**
     * 4.  修改(1)进入修改页面
     */
    @RequestMapping("/toUpdate")
    public String toUpdate(String id, Model model) {
        //根据id查询 购销合同
        Contract contract = contractService.findById(id);
        //保存到域中
        model.addAttribute("contract", contract);
        return "cargo/contract/contract-update";

    }

    /**
     * 5. 删除
     */
    @RequestMapping("/delete")
    public String delete(String id) {
        contractService.delete(id);
        return "redirect:/cargo/contract/list";
    }

    /**
     * 查看
     * http://localhost:8080/cargo/contract/toView.do?id=1
     */
    @RequestMapping("/toView")
    public String toView(String id) {
        Contract contract = contractService.findById(id);
        request.setAttribute("contract", contract);
        return "cargo/contract/contract-view";
    }

    /**
     * 提交: 把购销合同状态修改为1，已上报待报运状态
     * http://localhost:8080/cargo/contract/submit.do?id=1
     */
    @RequestMapping("/submit")
    public String submit(String id) {
        // 创建购销合同对象
        Contract contract = new Contract();
        // 设置id，作为修改条件
        contract.setId(id);
        // 设置状态，修改的值
        contract.setState(1);
        //调用contractService修改    动态修改 只修改传入对象属性不为空的对应字段
        contractService.update(contract);
        //重定向到list
        return "redirect:/cargo/contract/list";
    }

    /**
     * 取消：把购销合同的状态设置为0，草稿状态
     * http://localhost:8080/cargo/contract/cancel.do?id=f
     */
    @RequestMapping("/cancel")
    public String cancel(String id) {
        // 创建购销合同对象
        Contract contract = new Contract();
        // 设置id，作为修改条件
        contract.setId(id);
        // 设置状态，修改的值
        contract.setState(0);
        // 修改
        contractService.update(contract);
        // 重定向到列表
        return "redirect:/cargo/contract/list";
    }

    /**
     * 货物菜单 点击出货表
     * 访问地址：http://localhost:8080/cargo/contract/print.do
     * 跳转到出货表页面
     */
    @RequestMapping("/print")
    public String print() {
        return "/cargo/print/contract-print";
    }

    /**
     * 出货表 点击提交
     * 访问地址：/cargo/contract/printExcel.do
     * 完成出货表导出
     * 出货表导出（2）模板导出：先读取一个设置好样式的excel，再往excel中填充数据导出
     */
    @RequestMapping("/printExcel")
    public void printExcel(String inputDate) throws IOException {
        //模板导出 !!!先读取一个设置好样式的excel，再往excel中填充数据导出
        //读取excel文件流 获得文件时输入流
        InputStream inputStream = sessione.getServletContext().getResourceAsStream("/make/xlsprint/tOUTPRODUCT.xlsx");
        //1.获取工作簿 【根据excel文件流，创建工作簿】
        Workbook workbook = new XSSFWorkbook(inputStream);

        //2.获取表
        Sheet sheet = workbook.getSheetAt(0);

        //表中的数据处理
        //3.1导出标题  获得行 第一行
        Row row = sheet.getRow(0);
        //3.2获得单元格 第二个单元格
        Cell cell = row.getCell(1);
        //3.3添加单元格内容
        String bigTitle = inputDate.replace("-0", "-").replace("-", "年") + "月份出货表";
        cell.setCellValue(bigTitle);

        //4./* 获取第三行样式 (单元格的样式)*/
        //定义样式数组 保存单元格样式
        CellStyle[] cellStyles = new CellStyle[8];
        //获取第三行
        row = sheet.getRow(2);
        for (int i = 0; i < cellStyles.length; i++) {
            //获得第三行的每一列的样式 设置到数组中
            cellStyles[i] = row.getCell(i + 1).getCellStyle();
        }

        //5.导出数据行
        //5.1 调用service拿到导出的数据
        // 参数1：根据日期查询，dao映射语句：and c.ship_time LIKE '2019-07%'  模糊查询记得加上%
        List<ContractProductVo> list = contractService.findByShipTime(inputDate + "%", getLoginCompanyId());
        //5.2 循环给单元格添加内容
        int index = 2;
        if (list != null && list.size() > 0) {
            for (ContractProductVo contractProductVo : list) {
                //创建行 从第三行开始 行数索引为2
                row = sheet.createRow(index++);

                //定义一个Opj[] 存储每一行的数据
                Object[] objects = new Object[]{
                        contractProductVo.getCustomName(),
                        contractProductVo.getContractNo(),
                        contractProductVo.getProductNo(),
                        String.valueOf(contractProductVo.getCnumber()),  //因为数据库中有空值要处理  不能会在256行 出现空指针异常
                        contractProductVo.getFactoryName(),
                        contractProductVo.getDeliveryPeriod(),
                        contractProductVo.getShipTime(),
                        contractProductVo.getTradeTerms()
                };

                //遍历行中的单元格添加数据
                for (int i = 0; i < objects.length; i++) {
                    //创建单元格  从第二个单元格开始
                    cell = row.createCell(i + 1);
                    if (!(objects[i] instanceof Date)){
                        cell.setCellValue(objects[i].toString());
                    }else{
                        cell.setCellValue(parseDate((Date) objects[i]));
                    }
                    cell.setCellStyle(cellStyles[i]);
                }
            }
        }

        //6.导出下载
        //6.1设置响应头
        response.setHeader("Content-Disposition", "filename=export.xlsx");
        //6.2获取响应输出流
        ServletOutputStream outputStream = response.getOutputStream();
        //6.3将excel文件流写出到reponse流
        workbook.write(outputStream);
        //6.4关闭资源
        workbook.close();
        outputStream.close();

    }
   /*
    普通导出
    public void printExcel(String inputDate ) throws IOException {
        //1.创建工作簿
        Workbook workbook=new XSSFWorkbook();
        //2.创建工作表
        Sheet sheet = workbook.createSheet("出货表");
        //3设置单元格格式
        // 3.1设置单元格宽度  （设置表的列宽） (参数2的单位 1/256)
        sheet.setColumnWidth(0,256*5);
        sheet.setColumnWidth(1,256*26);
        sheet.setColumnWidth(2,256*11);
        sheet.setColumnWidth(3,256*29);
        sheet.setColumnWidth(4,256*12);
        sheet.setColumnWidth(5,256*15);
        sheet.setColumnWidth(6,256*10);
        sheet.setColumnWidth(7,256*10);
        sheet.setColumnWidth(8,256*10);
        //3.2合并单元格
        sheet.addMergedRegion(new CellRangeAddress(0,0,1,8));

        //4.创建第1行
        Row row = sheet.createRow(0);
        //4.1设置行高
        row.setHeightInPoints(36);
        //4.2创建第一行第二列
        Cell cell = row.createCell(1);
        //4.3给单元格赋值
        String bigTitle =inputDate.replace("-0","-").replace("-","年")+"月份出货表";
        cell.setCellValue(inputDate);
        //设置单元格样式
        cell.setCellStyle(this.bigTitle(workbook));


        //5.创建第二行
        row= sheet.createRow(1);
        //设置行高
        row.setHeightInPoints(26);
        String[] titles={"客户","订单号","货号","数量","工厂","工厂交期","船期","贸易条款"};
        //循环给第二行的每列添加数据
        for (int i = 0; i < titles.length; i++) {
            //获得每一列
            cell = row.createCell(i+1);
            //设置内容
            cell.setCellValue(titles[i]);
            //设置样式
            cell.setCellStyle(this.title(workbook));
        }


        // 第三步： 导出数据行（调用service查询）
        // 参数1：根据日期查询，dao映射语句：and c.ship_time LIKE '2019-07%'   模糊查询要加上%
        List<ContractProductVo> list = contractService.findByShipTime(inputDate+"%",getLoginCompanyId());
        if (list!=null && list.size()>0){
            int index=2;
            for (ContractProductVo contractProductVo : list) {
                //创建行
                row = sheet.createRow(index++);
                //设置单元格内容
                cell  = row.createCell(1);
                cell.setCellValue(contractProductVo.getCustomName()+"");
                cell.setCellStyle(this.title(workbook));

                cell  = row.createCell(2);
                cell.setCellValue(contractProductVo.getContractNo());
                cell.setCellStyle(this.title(workbook));

                cell  = row.createCell(3);
                cell.setCellValue(contractProductVo.getProductNo());
                cell.setCellStyle(this.title(workbook));

                cell  = row.createCell(4);
                cell.setCellValue(contractProductVo.getCnumber()+"");   //注意excel表中 的值不能为null 会报错空指针异常  可以是""
                cell.setCellStyle(this.title(workbook));

                cell  = row.createCell(5);
                cell.setCellValue(contractProductVo.getFactoryName());
                cell.setCellStyle(this.title(workbook));

                cell  = row.createCell(6);
                cell.setCellValue(contractProductVo.getDeliveryPeriod());
                cell.setCellStyle(this.title(workbook));

                cell  = row.createCell(7);
                cell.setCellValue(contractProductVo.getShipTime());
                cell.setCellStyle(this.title(workbook));

                cell  = row.createCell(8);
                cell.setCellValue(contractProductVo.getTradeTerms());
                cell.setCellStyle(this.title(workbook));

            }
        }


        // 第四步： 导出下载
        // 设置下载响应头 (http协议的固定下载格式)  设置下载响应头 浏览器才会将数据下载
        response.setHeader("Content-Disposition","attachment;filename=export.xlsx");
        // 获取resposne输出流  输出到浏览器上
        ServletOutputStream outputStream = response.getOutputStream();
        //将excel文件流写入到response输出流，实现下载
        workbook.write(outputStream);

        workbook.close();
        outputStream.close();


    }*/

    /*//大标题的样式
    public CellStyle bigTitle(Workbook wb){
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short)16);
        font.setBold(true);//字体加粗
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);				//横向居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);		//纵向居中
        return style;
    }

    //小标题的样式
    public CellStyle title(Workbook wb){
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short)12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);				//横向居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);		//纵向居中
        style.setBorderTop(BorderStyle.THIN);						//上细线
        style.setBorderBottom(BorderStyle.THIN);					//下细线
        style.setBorderLeft(BorderStyle.THIN);						//左细线
        style.setBorderRight(BorderStyle.THIN);						//右细线
        return style;
    }

    //文字样式
    public CellStyle text(Workbook wb){
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short)10);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);				//横向居左
        style.setVerticalAlignment(VerticalAlignment.CENTER);		//纵向居中
        style.setBorderTop(BorderStyle.THIN);						//上细线
        style.setBorderBottom(BorderStyle.THIN);					//下细线
        style.setBorderLeft(BorderStyle.THIN);						//左细线
        style.setBorderRight(BorderStyle.THIN);						//右细线
        return style;
    }*/

    //日期转换成字符串
    public String parseDate(Date date){
        if (date!=null){
            return new SimpleDateFormat("yyyy-MM").format(date);
        }
        return "";
    }

}
